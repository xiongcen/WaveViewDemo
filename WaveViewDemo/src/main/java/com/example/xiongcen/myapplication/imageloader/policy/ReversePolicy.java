package com.example.xiongcen.myapplication.imageloader.policy;

import com.example.xiongcen.myapplication.imageloader.request.BitmapRequest;

/**
 * 逆向加载
 * <p>
 * Created by xiongcen on 16/11/6.
 */

public class ReversePolicy implements LoadPolicy {
    @Override
    public int compare(BitmapRequest request1, BitmapRequest request2) {
        // 注意Bitmap请求要先执行最晚加入队列的请求,ImageLoader的策略
        return request2.getSerialNum() - request1.getSerialNum();
    }
}
