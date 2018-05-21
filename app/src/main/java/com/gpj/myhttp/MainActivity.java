package com.gpj.myhttp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gpj.httplib.activity.BaseActivity;
import com.gpj.httplib.net.HttpRequest;
import com.gpj.httplib.net.RequestCallback;

public class MainActivity extends BaseActivity {

    private TextView textView;
    private TextView textView2;
    private Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);
        textView2 = findViewById(R.id.text2);
        refresh = findViewById(R.id.refresh);

        getMyHttpClient().invoke("getWeatherInfo", null, new RequestCallback() {
            @Override
            public void onSuccess(String content) {
                textView.setText(content);
            }

            @Override
            public void onFail(String content) {
                textView.setText(content);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyHttpClient().invoke("getWeatherInfo", null, new RequestCallback() {
                    @Override
                    public void onSuccess(String content) {
                        textView2.setText(content);
                    }

                    @Override
                    public void onFail(String errorMessage) {
                        textView2.setText(errorMessage);
                    }
                });
            }
        });
    }
}
