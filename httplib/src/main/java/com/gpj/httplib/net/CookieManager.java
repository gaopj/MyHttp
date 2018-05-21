package com.gpj.httplib.net;

import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by v-pigao on 5/21/2018.
 */

public class CookieManager {
    public static final String TAG = "httplib";

    private Map<String, Map<String, Map<String, String>>> store = new ConcurrentHashMap<>();;

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE_VALUE_DELIMITER = ";";
    private static final String PATH = "path";
    private static final String EXPIRES = "expires";
    private static final String DATE_FORMAT = "EEE, dd-MMM-yyyy hh:mm:ss z";
    private static final String SET_COOKIE_SEPARATOR = "; ";
    private static final String COOKIE = "Cookie";

    private static final char NAME_VALUE_SEPARATOR = '=';
    private static final char DOT = '.';

    private DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);;

    public CookieManager() {
    }

    /**
     * 检索并存储由打开的URLConnection另一端的主机返回的cookie。
     * 必须使用connect（）方法打开连接后调用，不然会抛IOException异常。
     * @param conn
     * @throws java.io.IOException
     *             如果conn打开失败会抛该异常.
     */
    public void storeCookies(URLConnection conn) throws IOException {

        // 确认这些cookie是从哪边发送来的
        String domain = getDomainFromHost(conn.getURL().getHost());

        // 为domain存储cookies
        Map<String, Map<String, String>> domainStore;

        synchronized (store) {
            if (store.containsKey(domain)) {
                domainStore = store.get(domain);
            } else {
                domainStore = new HashMap<>();
                store.put(domain, domainStore);
            }
        }

        // 从URLConnection中获取cookies
        String headerName = null;
        for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++) {
            if (headerName.equalsIgnoreCase(SET_COOKIE)) {
                Map<String, String> cookie = new HashMap<>();
                StringTokenizer st = new StringTokenizer(conn.getHeaderField(i), COOKIE_VALUE_DELIMITER);

                // http协议规范规定字符串中的第一个键值对是cookie键值，因此将它们作为特殊情况处理：
                if (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    String name = token.substring(0, token.indexOf(NAME_VALUE_SEPARATOR));
                    String value = token.substring(token.indexOf(NAME_VALUE_SEPARATOR) + 1, token.length());
                    domainStore.put(name, cookie);
                    cookie.put(name, value);
                }

                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    cookie.put(token.substring(0,token.indexOf(NAME_VALUE_SEPARATOR)).toLowerCase(),
                            token.substring(token.indexOf(NAME_VALUE_SEPARATOR) + 1, token.length()));
                }
            }
        }
        Log.d(TAG,"storeCookies : "+toString());
    }

    /**
     * 在打开URLConnection之前，调用此方法将设置全部未过期的cookie匹配路径或子路径以获取底层URL
     * 必须使用connect（）方法打开连接前调用，不然会抛IOException异常。
     *
     * @param conn
     * @throws java.io.IOException
     */
    public void setCookies(URLConnection conn) throws IOException {

        // 确定检索适当cookie的域和路径
        URL url = conn.getURL();
        String domain = getDomainFromHost(url.getHost());
        String path = url.getPath();

        Map<String, Map<String, String>> domainStore = store.get(domain);
        if (domainStore == null)
            return;
        StringBuffer cookieStringBuffer = new StringBuffer();

        Iterator<String> cookieNames = domainStore.keySet().iterator();
        while (cookieNames.hasNext()) {
            String cookieName = cookieNames.next();
            Map<String, String> cookie = domainStore.get(cookieName);

            // 检查cookie以确保路径匹配，并且cookie没有过期，将cookie添加到标题字符串
            if (comparePaths( cookie.get(PATH), path) && isNotExpired((String) cookie.get(EXPIRES))) {
                cookieStringBuffer.append(cookieName);
                cookieStringBuffer.append("=");
                cookieStringBuffer.append( cookie.get(cookieName));
                if (cookieNames.hasNext())
                    cookieStringBuffer.append(SET_COOKIE_SEPARATOR);
            }
        }
        try {
            Log.d(TAG,"setCookies : "+cookieStringBuffer.toString());
            conn.setRequestProperty(COOKIE, cookieStringBuffer.toString());
        } catch (java.lang.IllegalStateException ise) {
            IOException ioe = new IOException(
                    "无效的状态! 无法在已连接的URLConnection上设置Cookie。"
                            + " 只调用setCookies（java.net.URLConnection）后调用java.net.URLConnection.connect（）。");
            throw ioe;
        }
    }

    private String getDomainFromHost(String host) {
        if (host.indexOf(DOT) != host.lastIndexOf(DOT)) {
            return host.substring(host.indexOf(DOT) + 1);
        } else {
            return host;
        }
    }

    private boolean isNotExpired(String cookieExpires) {
        if (cookieExpires == null)
            return true;
        Date now = new Date();
        try {
            return (now.compareTo(dateFormat.parse(cookieExpires))) <= 0;
        } catch (java.text.ParseException pe) {
            pe.printStackTrace();
            return false;
        }
    }

    private boolean comparePaths(String cookiePath, String targetPath) {
        if (cookiePath == null) {
            return true;
        } else if (cookiePath.equals("/")) {
            return true;
        } else if (targetPath.regionMatches(0, cookiePath, 0, cookiePath
                .length())) {
            return true;
        } else {
            return false;
        }

    }


    public String toString() {
        return store.toString();
    }

}