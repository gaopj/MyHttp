
这是一个http的网络框架，使用方法如下：

① 导入demo中的httplib这个moudle。

② 配置url，在httplib这个moudle下面的res/xml/url.xml中配置需要调用的url及相关参数，如下所示：

![配置url](https://github.com/gaopj/MyHttp/raw/master/pic/1.png)

其中Key是查找这个url的关键字；Expires 是get请求回调内容缓存的时间，单位为秒；NetType是请求方法，目前支持GET和POST;Url是请求的url。

③ 需要调用该框架的Activity需要继承BaseActivity这个类。如果不想继承的话，请在onStop()中实现BaseActivity中所实现的内容即在Activity销毁时取消所有请求。

④ 实现RequestCallback.class回调类，其中onSuccess(String content)方法是请求成功的回调，参数是请求成功的数据。onFail(String content)方法是请求失败时回调，参数是失败原因。这两个方法都在主线程中执行，可进行UI操作，但不要做耗时操作。

⑤ 调用getMyHttpClient()方法获得MyHttpClient.class类。通过该类的public void invokeGet(String urlKey, RequestCallback callback) 方法调用http请求，其中urlKey需要传入的是第②步中配置里的Key，callback是第④步中的RequestCallback对象。


```

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

        getMyHttpClient().invokeGet("getWeatherInfo",  new RequestCallback() {

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

```
