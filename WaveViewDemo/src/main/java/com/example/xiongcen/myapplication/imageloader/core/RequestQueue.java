package com.example.xiongcen.myapplication.imageloader.core;

import android.util.Log;

import com.example.xiongcen.myapplication.imageloader.request.BitmapRequest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求队列，使用优先队列，使得请求可以按照优先级进行处理
 * <p>
 * Created by xiongcen on 16/11/6.
 */

public class RequestQueue {

    /**
     * 请求队列
     */
    private BlockingQueue<BitmapRequest> mRequestQueue = new PriorityBlockingQueue<>();

    /**
     * 请求的序列化生成器
     */
    private AtomicInteger mSerialNumGenerator = new AtomicInteger(0);

    /**
     * 默认的核心数
     */
    public static int DEFAULT_CORE_NUMS = Runtime.getRuntime().availableProcessors() + 1;

    /**
     * CPU核心数 + 1个分发线程数
     */
    private int mDispatcherNums = DEFAULT_CORE_NUMS;

    /**
     * NetworkExecutor,执行网络请求的线程
     */
    private RequestDispatcher[] mDispatchers = null;

    public RequestQueue() {
        this(DEFAULT_CORE_NUMS);
    }

    public RequestQueue(int coreNums) {
        mDispatcherNums = coreNums;
    }

    private final void startDispatchers() {
        mDispatchers = new RequestDispatcher[mDispatcherNums];
        for (int i = 0; i < mDispatcherNums; i++) {
            mDispatchers[i] = new RequestDispatcher(mRequestQueue);
            mDispatchers[i].start();
        }
    }

    public void start() {
        stop();
        startDispatchers();
    }

    /**
     * 停止RequestDispatcher
     */
    public void stop() {
        if (mDispatchers != null && mDispatchers.length > 0) {
            for (int i = 0; i < mDispatchers.length; i++) {
                mDispatchers[i].interrupt();
            }
        }
    }


    public void addRequest(BitmapRequest request) {
        if (!mRequestQueue.contains(request)) {
            request.setSerialNum(generateSerialNumber());
            mRequestQueue.add(request);
        } else {
            Log.d("", "### 请求队列中已经含有");
        }
    }

    private int generateSerialNumber() {
        return mSerialNumGenerator.incrementAndGet();
    }

}
