package com.gpj.httplib.net;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.gpj.httplib.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by v-pigao on 5/15/2018.
 */

public class UrlConfigManager {
    public static final String TAG = "httplib";

    private static Map<String ,URLData > mUrlList;

    public static URLData findURL(String key, Context context){
        URLData data = null;
        if(mUrlList ==null || mUrlList.isEmpty()) {
            mUrlList = new HashMap<>();
            fetchUrlDataFromXml(context);
        }

        data = mUrlList.get(key);
        return  data;
    }

    private static void fetchUrlDataFromXml(Context context){
        XmlResourceParser xrp = context.getResources().getXml(R.xml.url);

        try {
            int event = xrp.getEventType();   //先获取当前解析器光标在哪
            while (event != XmlPullParser.END_DOCUMENT){    //如果还没到文档的结束标志，那么就继续往下处理
                switch (event){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        //一般都是获取标签的属性值，所以在这里数据你需要的数据
                        if (xrp.getName().equals(URLData.NODE)){
                            URLData data = new URLData();
                                data.key = xrp.getAttributeValue(null,URLData.KEY);
                                int expires = Integer.parseInt(xrp.getAttributeValue(null,URLData.EXPIRES));
                                data.expires = expires;
                                data.netType = xrp.getAttributeValue(null,URLData.NET_TYPE);
                                data.url = xrp.getAttributeValue(null,URLData.URL);
                            mUrlList.put(data.key,data);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                event = xrp.next();   //将当前解析器光标往下一步移
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
