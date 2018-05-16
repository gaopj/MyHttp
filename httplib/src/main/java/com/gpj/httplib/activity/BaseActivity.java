package com.gpj.httplib.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gpj.httplib.net.RequestManager;

/**
 * Created by v-pigao on 5/16/2018.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected RequestManager requestManager;


    public RequestManager getRequestManager(){
        return requestManager;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestManager = RequestManager.getInstance(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(requestManager!=null){
            requestManager.cancelAllRequest();
        }
    }
}
