package com.gpj.myhttp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.gpj.httplib.activity.BaseActivity;
import com.gpj.httplib.net.RequestCallback;

public class MainActivity extends BaseActivity {

    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);

        getRequestManager().invoke("getWeatherInfo", null, new RequestCallback() {
            @Override
            public void onSuccess(String content) {
                textView.setText(content);
            }

            @Override
            public void onFail(String content) {
                textView.setText(content);
            }
        });
    }
}
