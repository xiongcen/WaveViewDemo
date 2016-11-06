package com.example.xiongcen.myapplication.imageloader.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.example.xiongcen.myapplication.imageloader.request.BitmapRequest;

/**
 * 图片的内存缓存,key为图片的uri,值为图片本身
 * <p>
 * Created by xiongcen on 16/11/6.
 */

public class MemoryCache implements BitmapCache {

    private LruCache<String, Bitmap> mMemeryCache;

    public MemoryCache() {
        // 计算可使用的最大内存
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 取4分之一的可用内存作为缓存
        final int cacheSize = maxMemory / 4;
        mMemeryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };


    }

    @Override
    public Bitmap get(BitmapRequest key) {
        return mMemeryCache.get(key.getImageUri());
    }

    @Override
    public void put(BitmapRequest key, Bitmap value) {
        mMemeryCache.put(key.getImageUri(), value);
    }

    @Override
    public void remove(BitmapRequest key) {
        mMemeryCache.remove(key.getImageUri());
    }
}
