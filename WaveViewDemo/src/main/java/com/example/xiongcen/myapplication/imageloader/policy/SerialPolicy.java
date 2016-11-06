package com.example.xiongcen.myapplication.imageloader.policy;

import com.example.xiongcen.myapplication.imageloader.request.BitmapRequest;

/**
 * 顺序加载
 *
 * Created by xiongcen on 16/11/6.
 */

public class SerialPolicy implements LoadPolicy {
    @Override
    public int compare(BitmapRequest request1, BitmapRequest request2) {
        // 按照添加到队列的序列号顺序来执行
        return request1.getSerialNum() - request2.getSerialNum();
    }
}
