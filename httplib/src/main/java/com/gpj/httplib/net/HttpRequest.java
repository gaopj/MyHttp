package com.gpj.httplib.net;

import android.os.Handler;

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

    private URLData mUrlData ;
    private HashMap<String,String> mParams;
    private RequestCallback mCallback;
    private WeakReference<Handler> mHandler;

    private volatile boolean isCancel =false;

    public HttpRequest(URLData urlData, HashMap<String,String> params, RequestCallback callback, Handler handler){
        mUrlData = urlData;
        mParams = params;
        mCallback = callback;
        mHandler = new WeakReference<Handler>(handler);
    }
    @Override
    public void run() {
        final Response response = new Response();
        HttpURLConnection connection = null;
        String strResponse = null;
        try {
            // 调用URL对象的openConnection方法获取HttpURLConnection的实例
            URL url = new URL(mUrlData.url);
            connection = (HttpURLConnection) url.openConnection();
            // 设置请求方式，GET或POST
            connection.setRequestMethod(mUrlData.netType);
            // 设置连接超时、读取超时的时间，单位为毫秒（ms）
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            if(mParams!=null&&mUrlData.netType.equals("POST")){
                DataOutputStream data = new DataOutputStream(connection.getOutputStream());
                StringBuilder sb = new StringBuilder();
                Iterator<Map.Entry<String, String>> iterable = mParams.entrySet().iterator();
                if(iterable.hasNext()){
                    Map.Entry e = iterable.next();
                    sb.append(e.getKey()).append("=").append(e.getValue());
                }
                while (iterable.hasNext()){
                    Map.Entry e = iterable.next();
                    sb.append("&").append(e.getKey()).append("=").append(e.getValue());
                }
            }

            //连接
            connection.connect();

            //得到响应码
            int responseCode = connection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK) {
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

        } catch (Exception e){
            response.setErrorType(-1);
            response.setError(true);
            response.setErrorMessage(e.getMessage());
        } finally {
            if (connection != null){
                // 结束后，关闭连接
                connection.disconnect();
            }
        }
        if(strResponse==null){
            response.setErrorType(-1);
            response.setError(true);
            response.setErrorMessage("网络异常，返回空值");
        }else {
            response.setError(false);
            response.setResult(strResponse);
        }
        if(mHandler!=null && !isCancel) {
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
}
