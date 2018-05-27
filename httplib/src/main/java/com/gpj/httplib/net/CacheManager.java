package com.gpj.httplib.net;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by v-pigao on 5/17/2018.
 */

 class CacheManager  {
    public static final String TAG = "httplib";
    private LruCache<String,CacheBean> mLruCache ;

    public CacheManager(){
        // maxMemory 是允许的最大值 ，超过这个最大值，则会回收
        long maxMemory = Runtime.getRuntime().maxMemory()/8; // 获取最大的可用内存 一般使用可用内存的1 / 8
        mLruCache = new LruCache<>((int)maxMemory);

    }

    /**
     * 通过url从内存中获取数据
     * @param url
     */
    public CacheBean getStringFromMemory(String url){
        CacheBean bean = mLruCache.get(url);
        return bean;
    }

    /**
     * 设置数据到内存
     * @param url
     * @param bean
     */
    public void setStringToMemory(String url,CacheBean bean){
        mLruCache.put(url,bean);
    }

    public static class CacheBean{
        public long time;
        public String context;

        @Override
        public String toString() {
            return "CacheBean{" +
                    "time=" + time +
                    ", context='" + context + '\'' +
                    '}';
        }
    }
}
