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
    private Handler mHandler;
    private Context appContext;

     CacheManager mCacheManager;
     CookieManager mCookieManager;

    private MyHttpClient(){};

    private static class RequestManagerHolder{
        private static final MyHttpClient INSTANCE = new MyHttpClient();
    }
    public static final MyHttpClient getInstance(){
        return MyHttpClient.RequestManagerHolder.INSTANCE;
    }

    public static final MyHttpClient getInstance(Context context){
        MyHttpClient myHttpClient = MyHttpClient.RequestManagerHolder.INSTANCE;
        myHttpClient.init(context);
        return myHttpClient;
    }

    private ExecutorService executorService;

    private synchronized ExecutorService executorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>());
        }
        return executorService;
    }

    private List<HttpRequest> requestList = new LinkedList<>();

    public void cancelAllRequest(){
        if(requestList!=null&& requestList.size()>0){
            for(HttpRequest request:requestList){
                request.abort();
            }
            requestList.clear();
        }
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
    }
}
