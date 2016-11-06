package com.example.xiongcen.myapplication.imageloader.config;

import com.example.xiongcen.myapplication.imageloader.cache.BitmapCache;
import com.example.xiongcen.myapplication.imageloader.cache.MemoryCache;
import com.example.xiongcen.myapplication.imageloader.policy.LoadPolicy;
import com.example.xiongcen.myapplication.imageloader.policy.SerialPolicy;

/**
 * ImageLoader 配置类
 * <p>
 * Created by xiongcen on 16/11/6.
 */

public class ImageLoaderConfig {

    /**
     * 图片缓存配置对象
     */
    private BitmapCache mBitmapCache = new MemoryCache();

    /**
     * 加载图片时的loading和加载失败的图片配置对象
     */
    private DisplayConfig mDisplayConfig = new DisplayConfig();

    /**
     * 加载策略
     */
    private LoadPolicy mLoadPolicy = new SerialPolicy();

    /**
     * 当前设备的CPU个数:Runtime.getRuntime().availableProcessors()
     */
    private int mThreadCount = Runtime.getRuntime().availableProcessors() + 1;

    public ImageLoaderConfig setThreadCount(int threadCount) {
        this.mThreadCount = Math.max(1, threadCount);
        return this;
    }

    public ImageLoaderConfig setBitmapCache(BitmapCache bitmapCache) {
        this.mBitmapCache = bitmapCache;
        return this;
    }

    public ImageLoaderConfig setLoadPolicy(LoadPolicy loadPolicy) {
        if (loadPolicy != null) {
            this.mLoadPolicy = loadPolicy;
        }
        return this;
    }

    public ImageLoaderConfig setLoadingPlaceHolder(int resId) {
        mDisplayConfig.setLoadingResId(resId);
        return this;
    }

    public ImageLoaderConfig setNotFoundPlaceHolder(int resId) {
        mDisplayConfig.setFailedResId(resId);
        return this;
    }

    public BitmapCache getBitmapCache() {
        return mBitmapCache;
    }

    public LoadPolicy getLoadPolicy() {
        return mLoadPolicy;
    }

    public DisplayConfig getDisplayConfig() {
        return mDisplayConfig;
    }

    public int getThreadCount() {
        return mThreadCount;
    }
}
