package com.example.xiongcen.myapplication.network.httpstacks;

import com.example.xiongcen.myapplication.network.config.HttpUrlConnConfig;
import com.example.xiongcen.myapplication.network.requests.Request;
import com.example.xiongcen.myapplication.network.response.Response;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by xiongcen on 16/11/18.
 */

public class HttpUrlConnStack implements HttpStack {

    HttpUrlConnConfig mConfig = HttpUrlConnConfig.getConfig();

    @Override
    public Response performRequest(Request<?> request) {
        HttpURLConnection urlConnection = null;
        try {
            // 构建HttpURLConnection
            urlConnection = createUrlConnection(request.getUrl());
            // 设置headers
            setRequestHeaders(urlConnection, request);
            // 设置body参数
            setRequestParams(urlConnection, request);
            // https设置
            configHttps(request);
            return fetchResponse(urlConnection);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    private HttpURLConnection createUrlConnection(String url) throws IOException {
        URL newURL = new URL(url);
        URLConnection urlConnection = newURL.openConnection();
        urlConnection.setConnectTimeout(mConfig.connTimeOut);
        urlConnection.setReadTimeout(mConfig.soTimeOut);
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(true);
        return (HttpURLConnection) urlConnection;
    }

    private void setRequestHeaders(HttpURLConnection connection, Request<?> request) {
        Set<String> headersKeys = request.getHeaders().keySet();
        for (String headName : headersKeys) {
            connection.addRequestProperty(headName, request.getHeaders().get(headName));
        }

    }

    private void setRequestParams(HttpURLConnection connection, Request<?> request) throws IOException {
        Request.HttpMethod method = request.getHttpMethod();
        connection.setRequestMethod(method.toString());
        byte[] body = request.getBody();
        if (body != null) {
            connection.setDoOutput(true);
            connection.addRequestProperty(Request.CONTENT_TYPE, request.getContentType());

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(body);
            dataOutputStream.close();

        }
    }

    private void configHttps(Request<?> request) {
        if (request.isHttps()) {
            SSLSocketFactory sslSocketFactory = mConfig.getSslSocketFactory();
            if (sslSocketFactory != null) {
                HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
            }
            HostnameVerifier hostnameVerifier = mConfig.getHostnameVerifier();
            if (hostnameVerifier != null) {
                HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
            }
        }
    }

    private Response fetchResponse(HttpURLConnection connection) throws IOException {
        // Initialize HttpResponse with data from the HttpURLConnection.
        ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
        int responseCode = connection.getResponseCode();
        if (responseCode == -1) {
            throw new IOException("Could not retrieve response code from HttpUrlConnection.");
        }
        // 状态行数据
        StatusLine responseStatus = new BasicStatusLine(protocolVersion,
                connection.getResponseCode(), connection.getResponseMessage());
        // 构建response
        Response response = new Response(responseStatus);
        // 设置response数据
        response.setEntity(entityFromURLConnection(connection));
        addHeadersToResponse(response, connection);
        return response;
    }

    private HttpEntity entityFromURLConnection(HttpURLConnection connection) {
        BasicHttpEntity entity = new BasicHttpEntity();
        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            inputStream = connection.getErrorStream();
        }

        // TODO : GZIP
        entity.setContent(inputStream);
        entity.setContentLength(connection.getContentLength());
        entity.setContentEncoding(connection.getContentEncoding());
        entity.setContentType(connection.getContentType());

        return entity;
    }

    private void addHeadersToResponse(Response response, HttpURLConnection connection) {
        for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
            if (header.getKey() != null) {
                Header h = new BasicHeader(header.getKey(), header.getValue().get(0));
                response.addHeader(h);
            }
        }
    }
}
