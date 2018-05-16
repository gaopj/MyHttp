package com.gpj.httplib.net;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.NavUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

/**
 * Created by v-pigao on 5/15/2018.
 */

public class RequestManager {
    private Handler mHandler;
    private Context appContext;

    private RequestManager(){};

    private static class RequestManagerHolder{
        private static final RequestManager INSTANCE = new RequestManager();
    }
    public static final RequestManager getInstance(){
        return RequestManager.RequestManagerHolder.INSTANCE;
    }

    public static final RequestManager getInstance(Context context){
        RequestManager requestManager = RequestManager.RequestManagerHolder.INSTANCE;
        requestManager.init(context);
        return requestManager;
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
        requestList.add(request);
        executorService().execute(request);
    }

    public void init(Context context){
        if(appContext==null||mHandler== null) {
            appContext = context.getApplicationContext();
            mHandler = new Handler(context.getMainLooper());
        }
    }
}
