package com.example.xiongcen.myapplication.imageloader.request;

import android.widget.ImageView;

import com.example.xiongcen.myapplication.imageloader.config.DisplayConfig;
import com.example.xiongcen.myapplication.imageloader.core.SimpleImageLoader;
import com.example.xiongcen.myapplication.imageloader.policy.LoadPolicy;
import com.example.xiongcen.myapplication.imageloader.utils.ImageViewHelper;
import com.example.xiongcen.myapplication.imageloader.utils.Md5Helper;

import java.lang.ref.WeakReference;

/**
 * Created by xiongcen on 16/11/6.
 */

public class BitmapRequest implements Comparable<BitmapRequest> {

    private DisplayConfig mDisplayConfig;

    private int mSerialNum;

    private boolean mIsCancel;

    private WeakReference<ImageView> mImageViewRef;

    private SimpleImageLoader.ImageListener mListener;

    private String mImageUri;

    private String mImageUriMd5;

    private boolean mJustCacheInMemory;

    private LoadPolicy mLoadPolicy = SimpleImageLoader.getInstance().getConfig().getLoadPolicy();

    public BitmapRequest(ImageView imageView, String uri,
                         DisplayConfig config, SimpleImageLoader.ImageListener listener) {
        mImageViewRef = new WeakReference<ImageView>(imageView);
        mDisplayConfig = config;
        mListener = listener;
        mImageUri = uri;
        imageView.setTag(uri);
        mImageUriMd5 = Md5Helper.toMD5(mImageUri);
    }

    public void setDisplayConfig(DisplayConfig mDisplayConfig) {
        this.mDisplayConfig = mDisplayConfig;
    }

    public DisplayConfig getDisplayConfig() {
        return mDisplayConfig;
    }

    public void setSerialNum(int mSerialNum) {
        this.mSerialNum = mSerialNum;
    }

    public int getSerialNum() {
        return mSerialNum;
    }

    public boolean isIsCancel() {
        return mIsCancel;
    }

    public String getImageUri() {
        return mImageUri;
    }

    public String getImageUriMd5() {
        return mImageUriMd5;
    }

    public void setJustCacheInMemory(boolean mJustCacheInMemory) {
        this.mJustCacheInMemory = mJustCacheInMemory;
    }

    public boolean isJustCacheInMemory() {
        return mJustCacheInMemory;
    }

    public int getImageViewWidth() {
        return ImageViewHelper.getImageViewWidth(mImageViewRef.get());
    }

    public int getImageViewHeight() {
        return ImageViewHelper.getImageViewHeight(mImageViewRef.get());
    }

    @Override
    public int compareTo(BitmapRequest another) {
        return mLoadPolicy.compare(this, another);
    }

    public ImageView getImageView() {
        return mImageViewRef.get();
    }

    public boolean isImageViewTagValid() {
        return mImageViewRef.get() != null && mImageViewRef.get().getTag().equals(mImageUri);
    }

    public SimpleImageLoader.ImageListener getListener() {
        return mListener;
    }
}
