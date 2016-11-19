package com.example.xiongcen.myapplication.network.httpstacks;

import android.net.http.AndroidHttpClient;

import com.example.xiongcen.myapplication.network.config.HttpClientConfig;
import com.example.xiongcen.myapplication.network.requests.Request;
import com.example.xiongcen.myapplication.network.response.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by xiongcen on 16/11/18.
 */

public class HttpClientStack implements HttpStack {

    /**
     * 使用HttpClient执行网络请求时的Https配置
     */
    HttpClientConfig mConfig = HttpClientConfig.getConfig();

    HttpClient mHttpClient = AndroidHttpClient.newInstance(mConfig.userAgent);

    @Override
    public Response performRequest(Request<?> request) {

        try {
            HttpUriRequest httpUriRequest = createHttpUriRequest(request);
            // 添加连接参数
            setConnectionParams(httpUriRequest);
            // 添加header
            addHeaders(httpUriRequest, request.getHeaders());
            // https配置
            configHttps(request);
            // 执行请求
            HttpResponse httpResponse = mHttpClient.execute(httpUriRequest);
            // 构建response
            Response response = new Response(httpResponse.getStatusLine());
            // 设置Entity
            response.setEntity(httpResponse.getEntity());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private HttpUriRequest createHttpUriRequest(Request<?> request) {
        HttpUriRequest httpUriRequest = null;
        switch (request.getHttpMethod()) {
            case GET:
                httpUriRequest = new HttpGet(request.getUrl());
                break;
            case DELETE:
                httpUriRequest = new HttpDelete(request.getUrl());
                break;
            case POST:
                httpUriRequest = new HttpPost(request.getUrl());
                httpUriRequest.addHeader(Request.CONTENT_TYPE, request.getContentType());
                setEntityIfNonEmptyBody((HttpPost) httpUriRequest, request);
                break;
            case PUT:
                httpUriRequest = new HttpPut(request.getUrl());
                httpUriRequest.addHeader(Request.CONTENT_TYPE, request.getContentType());
                setEntityIfNonEmptyBody((HttpPost) httpUriRequest, request);
                break;
            default:
                throw new IllegalStateException("Unknown request method.");
        }
        return httpUriRequest;
    }

    private void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpUriRequest, Request<?> request) {
        byte[] body = request.getBody();
        if (body != null) {
            HttpEntity entity = new ByteArrayEntity(body);
            httpUriRequest.setEntity(entity);
        }
    }

    /**
     * 设置连接参数
     * @param httpUriRequest
     */
    // TODO: 16/11/19 一些优化设置没有写
    private void setConnectionParams(HttpUriRequest httpUriRequest) {
        HttpParams params = httpUriRequest.getParams();
        HttpConnectionParams.setConnectionTimeout(params, mConfig.connTimeOut);
        HttpConnectionParams.setSoTimeout(params, mConfig.soTimeOut);
    }

    private static void addHeaders(HttpUriRequest httpRequest, Map<String, String> headers) {
        for(String key : headers.keySet()) {
            httpRequest.setHeader(key, headers.get(key));
        }
    }

    private void configHttps(Request<?> request) {
        SSLSocketFactory sslSocketFactory = mConfig.getSocketFactory();
        if (request.isHttps() && sslSocketFactory != null) {
            Scheme sch = new Scheme("https", (SocketFactory) sslSocketFactory, 443);
            mHttpClient.getConnectionManager().getSchemeRegistry().register(sch);
        }
    }
}
