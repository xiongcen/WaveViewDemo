package com.example.xiongcen.myapplication.network.response;

import android.os.Handler;
import android.os.Looper;

import com.example.xiongcen.myapplication.network.requests.Request;

import java.util.concurrent.Executor;

/**
 * 请求结果投递类,将请求结果投递给UI线程
 * Created by xiongcen on 16/11/18.
 */

public class ResponseDelivery implements Executor{

    /**
     * 主线程的hander
     */
    Handler mResponseHandler = new Handler(Looper.getMainLooper());

    public ResponseDelivery() {
        System.out.println("ResponseDelivery.ResponseDelivery");
    }

    public void deliveryResponse(final Request<?> request, final Response response) {
        Runnable respRunnable = new Runnable() {

            @Override
            public void run() {
                request.deliveryResponse(response);
            }
        };
        execute(respRunnable);
    }

    @Override
    public void execute(Runnable command) {
        mResponseHandler.post(command);
    }
}
