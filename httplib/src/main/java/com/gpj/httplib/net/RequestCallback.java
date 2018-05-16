package com.gpj.httplib.net;

/**
 * Created by v-pigao on 5/15/2018.
 */

public interface RequestCallback {
    void onSuccess(String content);
    void onFail(String content);
}
