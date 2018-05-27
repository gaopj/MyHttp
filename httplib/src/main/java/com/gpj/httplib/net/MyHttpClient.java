package com.gpj.httplib.net;

import android.content.Context;
import android.os.Handler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by v-pigao on 5/15/2018.
 */

public class MyHttpClient {
    private Handler mHandler; // 持有主线程Lopper，用于回调
    private static Context appContext; // application的 Context
    private List<HttpRequest> requestList = new LinkedList<>(); // 持有的所有请求队列
    private ExecutorService executorService; // 用于网络请求的线程池
    CacheManager mCacheManager; // 缓存管理
    CookieManager mCookieManager; // Cookie管理


    private MyHttpClient(){};

    private static class RequestManagerHolder{
        private static final MyHttpClient INSTANCE = new MyHttpClient();
    }

    static final MyHttpClient getInstance(){
        MyHttpClient myHttpClient = MyHttpClient.RequestManagerHolder.INSTANCE;
        return myHttpClient;
    }

    public static final MyHttpClient getInstance(Context context){
        MyHttpClient myHttpClient = MyHttpClient.RequestManagerHolder.INSTANCE;
        myHttpClient.init(context);
        return myHttpClient;
    }



    private synchronized ExecutorService executorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>());
        }
        return executorService;
    }


    /**
     * 取消所有请求
     */
    public void cancelAllRequest(){
        if(requestList!=null&& requestList.size()>0){
            for(HttpRequest request:requestList){
                request.abort();
            }
            requestList.clear();
        }
    }

    /**
     * get 请求
     * @param urlKey url.xml配置中的Key
     * @param callback 请求回调函数，在主线程中执行
     */
    public void invokeGet(String urlKey, RequestCallback callback){
        URLData data=UrlConfigManager.findURL(urlKey,appContext);
        data.netType = "GET";
        invoke(data, null,  callback);
    }


    /**
     * post 请求
     * @param urlKey url.xml配置中的Key
     * @param params post 请求需要传入的参数
     * @param callback 请求回调函数，在主线程中执行
     */
    public void invokePost(String urlKey,HashMap<String,String> params, RequestCallback callback){
        URLData data=UrlConfigManager.findURL(urlKey,appContext);
        data.netType = "POST";
        invoke(data, params,  callback);
    }

    public void invoke(String urlKey,HashMap<String,String> params, RequestCallback callback){
        URLData data=UrlConfigManager.findURL(urlKey,appContext);
        invoke(data, params,  callback);
    }

    public void invoke(URLData urlData, HashMap<String,String> params, RequestCallback callback){
        HttpRequest request = new HttpRequest(urlData,params,callback,mHandler);
        invoke(request);
    }

    public void invoke(HttpRequest request){
        if(request==null)
            return;
        requestList.add(request);
        executorService().execute(request);
    }

    public void init(Context context){
        if(appContext==null) {
            appContext = context.getApplicationContext();
        }
        if(mHandler ==null){
            mHandler = new Handler(context.getMainLooper());
        }
        if(mCacheManager==null){
            mCacheManager = new CacheManager();
        }
        if(mCookieManager ==null){
            mCookieManager = new CookieManager();
        }
        UrlConfigManager.init(context);
    }
}

