package com.example.xiongcen.myapplication.imageloader.loader;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.example.xiongcen.myapplication.imageloader.cache.BitmapCache;
import com.example.xiongcen.myapplication.imageloader.config.DisplayConfig;
import com.example.xiongcen.myapplication.imageloader.core.SimpleImageLoader;
import com.example.xiongcen.myapplication.imageloader.request.BitmapRequest;

/**
 * Created by xiongcen on 16/11/6.
 */

public abstract class AbsLoader implements Loader {

    /**
     * 图片缓存
     * 是static的原因是不管Loader有多少个，缓存对象都应该是共享的，也就是缓存只有一份
     */
    private static BitmapCache mCache = SimpleImageLoader.getInstance().getConfig().getBitmapCache();

    @Override
    public void loadImage(BitmapRequest request) {
        // 1.从缓存中获取
        Bitmap resultBitmap = mCache.get(request);

        Log.e("", "### 是否有缓存 : " + resultBitmap + ", uri = " + request.getImageUri());

        if (resultBitmap == null) {
            showLoading(request);

            // 2.没有缓存，调用onLoaderImage加载图片
            resultBitmap = onLoadImage(request);

            // 3.缓存图片
            cacheBitmap(request, resultBitmap);
        } else {
            request.setJustCacheInMemory(true);
        }
        deliveryToUIThread(request, resultBitmap);
    }

    /**
     * 加载图片的hook方法，留给子类处理
     */
    protected abstract Bitmap onLoadImage(BitmapRequest request);

    /**
     * @param request
     * @param bitmap
     */
    private void cacheBitmap(BitmapRequest request, Bitmap bitmap) {
        // 缓存新的图片
        if (bitmap != null && mCache != null) {
            synchronized (mCache) {
                mCache.put(request, bitmap);
            }
        }
    }

    /**
     * 显示加载中的视图,注意这里也要判断imageview的tag与image uri的相等性,否则逆序加载时出现问题
     *
     * @param request
     */
    protected void showLoading(final BitmapRequest request) {
        final ImageView imageView = request.getImageView();
        if (request.isImageViewTagValid()
                && hasLoadingPlaceholder(request.getDisplayConfig())) {
            imageView.post(new Runnable() {

                @Override
                public void run() {
                    imageView.setImageResource(request.getDisplayConfig().getLoadingResId());
                }
            });
        }
    }

    /**
     * 将结果投递到UI,更新ImageView
     *
     * @param request
     * @param bitmap
     */
    protected void deliveryToUIThread(final BitmapRequest request,
                                      final Bitmap bitmap) {
        final ImageView imageView = request.getImageView();
        if (imageView == null) {
            return;
        }
        imageView.post(new Runnable() {

            @Override
            public void run() {
                updateImageView(request, bitmap);
            }
        });
    }

    /**
     * 更新ImageView
     *
     * @param request
     * @param result
     */
    private void updateImageView(BitmapRequest request, Bitmap result) {
        final ImageView imageView = request.getImageView();
        final String uri = request.getImageUri();
        if (result != null && imageView.getTag().equals(uri)) {
            imageView.setImageBitmap(result);
        }

        // 加载失败
        if (result == null && hasFaildPlaceholder(request.getDisplayConfig())) {
            imageView.setImageResource(request.getDisplayConfig().getFailedResId());
        }

        // 回调接口
        if (request.getListener() != null) {
            request.getListener().onComplete(imageView, result, uri);
        }
    }

    private boolean hasLoadingPlaceholder(DisplayConfig displayConfig) {
        return displayConfig != null && displayConfig.getLoadingResId() > 0;
    }

    private boolean hasFaildPlaceholder(DisplayConfig displayConfig) {
        return displayConfig != null && displayConfig.getFailedResId() > 0;
    }


}
