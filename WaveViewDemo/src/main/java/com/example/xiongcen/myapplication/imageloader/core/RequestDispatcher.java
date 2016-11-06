package com.example.xiongcen.myapplication.imageloader.core;

import android.util.Log;

import com.example.xiongcen.myapplication.imageloader.loader.Loader;
import com.example.xiongcen.myapplication.imageloader.loader.LoaderManager;
import com.example.xiongcen.myapplication.imageloader.request.BitmapRequest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by xiongcen on 16/11/6.
 */

public class RequestDispatcher extends Thread {

    private BlockingQueue<BitmapRequest> mRequestQueue = new PriorityBlockingQueue<>();

    public RequestDispatcher(BlockingQueue<BitmapRequest> queue) {
        this.mRequestQueue = queue;
    }

    @Override
    public void run() {
        try {
            while (!this.isInterrupted()) {
                final BitmapRequest request = mRequestQueue.take();
                if (request.isIsCancel()) {
                    continue;
                }
                // 解析图片schema
                final String schema = parseSchema(request.getImageUri());
                // 根据schema获取对应的Loader
                Loader imageLoader = LoaderManager.getInstance().getLoader(schema);
                // 加载图片
                imageLoader.loadImage(request);

            }
        } catch (InterruptedException e) {
            Log.i("", "### 请求分发器退出");
        }
    }

    /**
     * 这里是解析图片uri的格式,uri格式为: schema:// + 图片路径。
     */
    private String parseSchema(String uri) {
        if (uri.contains("://")) {
            return uri.split("://")[0];
        } else {
            Log.e(getName(), "### wrong scheme, image uri is : " + uri);
        }
        return "";
    }
}
