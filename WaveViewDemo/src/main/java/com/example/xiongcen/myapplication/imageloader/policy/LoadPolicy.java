package com.example.xiongcen.myapplication.imageloader.policy;

import com.example.xiongcen.myapplication.imageloader.request.BitmapRequest;

/**
 * Created by xiongcen on 16/11/6.
 */

public interface LoadPolicy {
    int compare(BitmapRequest request1, BitmapRequest request2);
}
