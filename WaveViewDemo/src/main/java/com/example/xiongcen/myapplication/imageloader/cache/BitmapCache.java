package com.example.xiongcen.myapplication.imageloader.cache;

import android.graphics.Bitmap;

import com.example.xiongcen.myapplication.imageloader.request.BitmapRequest;

/**
 * Created by xiongcen on 16/11/6.
 */

public interface BitmapCache {

    Bitmap get(BitmapRequest key);

    void put(BitmapRequest key, Bitmap value);

    void remove(BitmapRequest key);
}
