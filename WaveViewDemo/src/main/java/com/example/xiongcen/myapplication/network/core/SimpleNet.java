package com.example.xiongcen.myapplication.network.core;

import android.content.Context;
import android.util.Log;

import com.example.xiongcen.myapplication.network.httpstacks.HttpStack;

import java.io.File;

/**
 * Created by xiongcen on 16/11/19.
 */

public class SimpleNet {

    /**
     * 创建一个请求队列,NetworkExecutor数量为默认的数量
     *
     * @return
     */
    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, RequestQueue.DEFAULT_CORE_NUMS);
    }

    /**
     * 创建一个请求队列,NetworkExecutor数量为coreNums
     *
     * @param coreNums
     * @return
     */
    public static RequestQueue newRequestQueue(Context context, int coreNums) {
        return newRequestQueue(context, coreNums, null);
    }

    /**
     * 创建一个请求队列,NetworkExecutor数量为coreNums
     *
     * @param coreNums 线程数量
     * @param httpStack 网络执行者
     * @return
     */
    public static RequestQueue newRequestQueue(Context context, int coreNums, HttpStack httpStack) {
        enableHttpResponseCache(context);
        RequestQueue queue = new RequestQueue(Math.max(0, coreNums), httpStack);
        queue.start();
        return queue;
    }

    private static void enableHttpResponseCache(Context context) {
        try {
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            File httpCacheDir = new File(context.getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
            Log.d("", "HTTP response cache is unavailable.");
        }
    }
}
