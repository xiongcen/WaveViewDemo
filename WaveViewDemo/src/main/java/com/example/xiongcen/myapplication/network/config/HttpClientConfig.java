package com.example.xiongcen.myapplication.network.config;

import javax.net.ssl.SSLSocketFactory;

/**
 * 针对使用HttpClientStack执行请求时为https请求配置的SSLSocketFactory配置类
 * Created by xiongcen on 16/11/19.
 */

public class HttpClientConfig extends HttpConfig {

    private static HttpClientConfig sConfig = new HttpClientConfig();
    private SSLSocketFactory mSslSocketFactory;

    private HttpClientConfig() {

    }

    public static HttpClientConfig getConfig() {
        return sConfig;
    }

    /**
     * 配置https请求的SSLSocketFactory
     *
     * @param sslSocketFactory
     * @param
     */
    public void setHttpsConfig(SSLSocketFactory sslSocketFactory) {
        mSslSocketFactory = sslSocketFactory;
    }

    public SSLSocketFactory getSocketFactory() {
        return mSslSocketFactory;
    }
}
