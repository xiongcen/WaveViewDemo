package com.example.xiongcen.myapplication.imageloader.core;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.xiongcen.myapplication.imageloader.cache.BitmapCache;
import com.example.xiongcen.myapplication.imageloader.cache.MemoryCache;
import com.example.xiongcen.myapplication.imageloader.cache.NoCache;
import com.example.xiongcen.myapplication.imageloader.config.DisplayConfig;
import com.example.xiongcen.myapplication.imageloader.config.ImageLoaderConfig;
import com.example.xiongcen.myapplication.imageloader.policy.SerialPolicy;
import com.example.xiongcen.myapplication.imageloader.request.BitmapRequest;

/**
 * 图片加载类，支持url和本地图片的uri形式加载。根据图片路径格式来判断是网络图片还是本地图片，
 * 如果是网络图片则交由SimpleNet框架来加载，
 * 如果是本地图片则交给mExecutorService从sd卡中加载，
 * 加载之后直接更新UI，无需用户干预。
 * 如果用户设置了缓存策略，那么会将加载到的图片缓存起来。
 * 用户也可以设置加载策略，例如顺序加载{@see SerialPolicy}和逆向加载{@see ReversePolicy}
 * <p>
 * Created by xiongcen on 16/11/6.
 */

public class SimpleImageLoader {

    private static SimpleImageLoader sInstance;

    /**
     * 网络请求队列
     */
    private RequestQueue mImageQueue;

    /**
     * 缓存
     */
    private volatile BitmapCache mCache = new MemoryCache();

    /**
     * 图片加载配置对象
     */
    private ImageLoaderConfig mConfig;

    public static SimpleImageLoader getInstance() {
        if (sInstance == null) {
            synchronized (SimpleImageLoader.class) {
                if (sInstance == null) {
                    sInstance = new SimpleImageLoader();
                }
            }
        }
        return sInstance;
    }

    public void init(ImageLoaderConfig config) {
        mConfig = config;
        mCache = config.getBitmapCache();
        checkConfig();
        mImageQueue = new RequestQueue(mConfig.getThreadCount());
        mImageQueue.start();
    }

    private void checkConfig() {
        if (mConfig == null) {
            throw new RuntimeException("The config of SimpleImageLoader is Null, please call the init(ImageLoaderConfig config) method to initialize");
        }
        if (mConfig.getLoadPolicy() == null) {
            mConfig.setLoadPolicy(new SerialPolicy());
        }

        if (mCache == null) {
            mCache = new NoCache();
        }
    }

    public void displayImage(ImageView imageView, String uri) {
        displayImage(imageView, uri, null, null);
    }

    public void displayImage(final ImageView imageView, final String uri,
                             final DisplayConfig config, final ImageListener listener) {
        BitmapRequest request = new BitmapRequest(imageView, uri, config, listener);

        // 加载的配置对象，如果没有设置则使用ImageLoader的配置
        request.setDisplayConfig(request.getDisplayConfig() != null ? request.getDisplayConfig() : mConfig.getDisplayConfig());

        // 添加队列中
        mImageQueue.addRequest(request);
    }

    /**
     * 图片加载Listener
     */
    public static interface ImageListener {
        public void onComplete(ImageView imageView, Bitmap bitmap, String uri);
    }

    public ImageLoaderConfig getConfig() {
        return mConfig;
    }

    public void stop() {
        mImageQueue.stop();
    }
}