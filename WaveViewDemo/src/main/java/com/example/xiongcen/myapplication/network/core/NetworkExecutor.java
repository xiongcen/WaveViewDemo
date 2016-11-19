package com.example.xiongcen.myapplication.network.core;

import android.util.Log;

import com.example.xiongcen.myapplication.network.cache.Cache;
import com.example.xiongcen.myapplication.network.cache.LruMemCache;
import com.example.xiongcen.myapplication.network.httpstacks.HttpStack;
import com.example.xiongcen.myapplication.network.requests.Request;
import com.example.xiongcen.myapplication.network.response.Response;
import com.example.xiongcen.myapplication.network.response.ResponseDelivery;

import java.util.concurrent.BlockingQueue;

/**
 * Created by xiongcen on 16/11/18.
 */

public class NetworkExecutor extends Thread {

    /**
     * 网络请求队列
     */
    private BlockingQueue<Request<?>> mRequestQueue;

    /**
     * 网络请求栈
     */
    private HttpStack mHttpStack;

    /**
     * 结果分发器,将结果投递到主线程
     */
    private static ResponseDelivery mResponseDelivery = new ResponseDelivery();
    /**
     * 请求缓存
     */
    private static Cache<String, Response> mReqCache = new LruMemCache();
    /**
     * 是否停止
     */
    private boolean isStop = false;

    public NetworkExecutor(BlockingQueue<Request<?>> requestQueue, HttpStack httpStack) {
        mRequestQueue = requestQueue;
        mHttpStack = httpStack;
    }

    public void quit() {
        isStop = true;
        interrupt();
    }

    @Override
    public void run() {
        try {
            while (!isStop) {
                Request<?> request = mRequestQueue.take();
                if (request.isCanceled()) {
                    Log.i("### ", "### 取消执行了");
                    continue;
                }

                Response response = null;
                if (isUserCache(request)) {
                    // 从缓存中取
                    response = mReqCache.get(request.getUrl());
                } else {
                    // 从网络上获取数据
                    response = mHttpStack.performRequest(request);
                    // 如果该请求需要缓存,那么请求成功则缓存到mResponseCache中
                    if (request.shouldCache() && isSuccess(response)) {
                        mReqCache.put(request.getUrl(), response);
                    }
                }
                // 分发请求结果
                mResponseDelivery.deliveryResponse(request, response);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("", "### 请求分发器退出");
        }

    }

    private boolean isUserCache(Request<?> request) {
        return request.shouldCache() && mReqCache.get(request.getUrl()) != null;
    }

    private boolean isSuccess(Response response) {
        return response != null && response.getStatusCode() == 200;
    }
}
