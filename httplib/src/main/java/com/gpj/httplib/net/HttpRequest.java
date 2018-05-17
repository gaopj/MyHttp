package com.gpj.httplib.net;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by v-pigao on 5/15/2018.
 */

public class HttpRequest implements Runnable {
    public static final String TAG = "httplib";
    private URLData mUrlData;
    private HashMap<String, String> mParams;
    private RequestCallback mCallback;
    private WeakReference<Handler> mHandler;
    private CacheManager mCacheManager;
    private Response response;

    private volatile boolean isCancel = false;

    public HttpRequest(URLData urlData, HashMap<String, String> params, RequestCallback callback, Handler handler) {
        mUrlData = urlData;
        mParams = params;
        mCallback = callback;
        mHandler = new WeakReference<>(handler);
        mCacheManager = MyHttpClient.getInstance().mCacheManager;
    }

    @Override
    public void run() {
        response = null;
        if (mUrlData.netType.equals("GET")) {
           CacheManager.CacheBean bean= mCacheManager.getStringFromMemory(mUrlData.url);
           if(bean!=null&& mUrlData.expires>0  && System.currentTimeMillis()<=bean.time ){
               response = new Response();
               response.setError(false);
               response.setResult(bean.context);
               Log.d(TAG,"调取缓存中数据："+mUrlData.url);
           }
        }
        if (response == null) {
            Log.d(TAG,"从网络拉取数据："+mUrlData.url);
            response = netRequest();
        }


        if (mHandler != null && !isCancel && mCallback != null) {
            mHandler.get().post(new Runnable() {
                @Override
                public void run() {
                    if (response.isError()) {
                        mCallback.onFail(response.getErrorMessage());
                    } else {
                        mCallback.onSuccess(response.getResult());
                    }
                }
            });
        }

    }

    public void abort() {
        isCancel = true;
    }

    private Response netRequest() {
        Response response = new Response();
        HttpURLConnection connection = null;
        String strResponse = null;
        try {
            // 调用URL对象的openConnection方法获取HttpURLConnection的实例
            URL url = new URL(mUrlData.url);
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接超时、读取超时的时间，单位为毫秒（ms）
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            // 设置请求方式，GET或POST
            connection.setRequestMethod(mUrlData.netType);

            if (mParams != null && mUrlData.netType.equals("POST")) {
                DataOutputStream data = new DataOutputStream(connection.getOutputStream());
                StringBuilder sb = new StringBuilder();
                Iterator<Map.Entry<String, String>> iterable = mParams.entrySet().iterator();
                if (iterable.hasNext()) {
                    Map.Entry e = iterable.next();
                    sb.append(e.getKey()).append("=").append(e.getValue());
                }
                while (iterable.hasNext()) {
                    Map.Entry e = iterable.next();
                    sb.append("&").append(e.getKey()).append("=").append(e.getValue());
                }
                data.writeBytes(sb.toString());
            }

            //连接
            connection.connect();

            //得到响应码
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // getInputStream方法获取服务器返回的输入流
                InputStream in = connection.getInputStream();
                // 使用BufferedReader对象读取返回的数据流
                // 按行读取，存储在StringBuider对象response中
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                strResponse = responseBuilder.toString();
            }

        } catch (Exception e) {
            response.setErrorType(-1);
            response.setError(true);
            response.setErrorMessage(e.getMessage());
        } finally {
            if (connection != null) {
                // 结束后，关闭连接
                connection.disconnect();
            }
        }
        if (strResponse == null) {
            response.setErrorType(-1);
            response.setError(true);
            response.setErrorMessage("网络异常，返回空值");
        } else {
            response.setError(false);
            response.setResult(strResponse);
            if(mUrlData.expires>0) {
                CacheManager.CacheBean bean = new CacheManager.CacheBean();
                bean.time = System.currentTimeMillis()+mUrlData.expires*1000;
                bean.context = strResponse;
                mCacheManager.setStringToMemory(mUrlData.url,bean);
            }
        }
        return response;
    }
}
