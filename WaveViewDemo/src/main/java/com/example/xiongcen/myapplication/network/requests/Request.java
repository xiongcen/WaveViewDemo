package com.example.xiongcen.myapplication.network.requests;

import com.example.xiongcen.myapplication.network.response.Response;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求类
 * Created by xiongcen on 16/11/17.
 */

public abstract class Request<T> implements Comparable<Request<T>> {


    /**
     * http请求方法的枚举
     */
    public static enum HttpMethod {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");

        /**
         * http request type
         */
        private String mHttpMethod = "";

        private HttpMethod(String method) {
            mHttpMethod = method;
        }

        @Override
        public String toString() {
            return mHttpMethod;
        }
    }

    /**
     * 优先级枚举
     */
    public static enum Priority {
        LOW,
        NORMAL,
        HIGN,
        IMMEDIATE
    }

    /**
     * 网络请求Listener
     */
    public static interface RequestListener<T> {
        /**
         * 请求完成的回调
         *
         * @param response
         */
        public void onComplete(int stCode, T response, String errMsg);
    }

    /**
     * Default encoding for POST or PUT parameters. See
     */
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    /**
     * Default Content-type
     */
    public final static String CONTENT_TYPE = "Content-Type";

    /**
     * 请求序列号
     */
    protected int mSerialNum = 0;

    /**
     * 优先级默认设置为Normal
     */
    protected Priority mPriority = Priority.NORMAL;

    /**
     * 是否取消该请求
     */
    protected boolean isCancel = false;

    /**
     * 该请求是否应该缓存
     */
    private boolean mShouldCache = true;

    /**
     * 请求Listener
     */
    protected RequestListener<T> mRequestListener;

    /**
     * 请求的url
     */
    private String mUrl = "";

    /**
     * 请求的方法
     */
    HttpMethod mHttpMethod = HttpMethod.GET;

    /**
     * 请求的header
     */
    private Map<String, String> mHeaders = new HashMap<String, String>();

    /**
     * 请求参数
     */
    private Map<String, String> mParams = new HashMap<String, String>();

    public Request(HttpMethod method, String url, RequestListener<T> listener) {
        mHttpMethod = method;
        mUrl = url;
        mRequestListener = listener;
    }


    public String getUrl() {
        return mUrl;
    }

    public int getSerialNumber() {
        return mSerialNum;
    }

    public void setSerialNumber(int mSerialNum) {
        this.mSerialNum = mSerialNum;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority mPriority) {
        this.mPriority = mPriority;
    }

    public void setShouldCache(boolean shouldCache) {
        this.mShouldCache = shouldCache;
    }

    public boolean shouldCache() {
        return mShouldCache;
    }

    public void cancel() {
        isCancel = true;
    }

    public boolean isCanceled() {
        return isCancel;
    }

    public void setHeaders(Map<String, String> mHeaders) {
        this.mHeaders = mHeaders;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public void setParams(Map<String, String> mParams) {
        this.mParams = mParams;
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    public byte[] getBody() {
        Map<String, String> params = getParams();
        if (params != null && !params.isEmpty()) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    public String getContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    public void setHttpMethod(HttpMethod mHttpMethod) {
        this.mHttpMethod = mHttpMethod;
    }

    public HttpMethod getHttpMethod() {
        return mHttpMethod;
    }

    public boolean isHttps() {
        return mUrl.startsWith("https");
    }

    @Override
    public int compareTo(Request<T> another) {
        Priority myPriority = this.getPriority();
        Priority anotherPriority = another.getPriority();
        // 如果优先级相等,那么按照添加到队列的序列号顺序来执行
        return myPriority.equals(another) ? this.getSerialNumber() - another.getSerialNumber()
                : myPriority.ordinal() - anotherPriority.ordinal();
    }

    public void deliveryResponse(Response response) {
        T result = parseResponse(response);
        if (mRequestListener != null) {
            int stCode = response!= null ? response.getStatusCode() : -1;
            String msg = response != null ? response.getMessage() : "unkown error";
            mRequestListener.onComplete(stCode, result, msg);
        }
    }

    /**
     * 从原生的网络请求中解析结果
     *
     * @param response
     * @return
     */
    public abstract T parseResponse(Response response);


}
