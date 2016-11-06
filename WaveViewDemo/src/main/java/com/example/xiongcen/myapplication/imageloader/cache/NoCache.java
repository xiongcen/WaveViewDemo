package com.example.xiongcen.myapplication.imageloader.cache;

import android.graphics.Bitmap;

import com.example.xiongcen.myapplication.imageloader.request.BitmapRequest;

/**
 * Created by xiongcen on 16/11/6.
 */

public class NoCache implements BitmapCache {
    @Override
    public Bitmap get(BitmapRequest key) {
        return null;
    }

    @Override
    public void put(BitmapRequest key, Bitmap value) {

    }

    @Override
    public void remove(BitmapRequest key) {

    }
}
