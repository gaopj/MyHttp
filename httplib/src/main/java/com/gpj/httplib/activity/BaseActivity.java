package com.gpj.httplib.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gpj.httplib.net.MyHttpClient;

/**
 * Created by v-pigao on 5/16/2018.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected MyHttpClient myHttpClient;


    public MyHttpClient getMyHttpClient(){
        return myHttpClient;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myHttpClient = MyHttpClient.getInstance(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(myHttpClient !=null){
            myHttpClient.cancelAllRequest();
        }
    }
}
