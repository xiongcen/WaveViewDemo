package com.example.xiongcen.myapplication.imageloader.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.example.xiongcen.myapplication.imageloader.disklrucache.DiskLruCache;
import com.example.xiongcen.myapplication.imageloader.disklrucache.IOUtil;
import com.example.xiongcen.myapplication.imageloader.request.BitmapRequest;
import com.example.xiongcen.myapplication.imageloader.utils.BitmapDecoder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by xiongcen on 16/11/6.
 */

public class DiskCache implements BitmapCache {

    /**
     * 1MB
     */
    private static final int MB = 1024 * 1024;
    /**
     * cache dir
     */
    private static final String IMAGE_DISK_CACHE = "bitmap";
    /**
     * Disk LRU Cache
     */
    private DiskLruCache mDiskLruCache;
    /**
     * Disk Cache Instance
     */
    private static DiskCache mDiskCache;

    private DiskCache(Context context) {
        initDiskCache(context);
    }

    public static DiskCache getDiskCache(Context context) {
        if (mDiskCache == null) {
            synchronized (DiskCache.class) {
                if (mDiskCache == null) {
                    mDiskCache = new DiskCache(context);
                }
            }

        }
        return mDiskCache;
    }

    /**
     * 初始化sdcard缓存
     */
    private void initDiskCache(Context context) {
        try {
            File cacheDir = getDiskCacheDir(context, IMAGE_DISK_CACHE);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache
                    .open(cacheDir, getAppVersion(context), 1, 50 * MB);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取sd缓存的目录,如果挂载了sd卡则使用sd卡缓存，否则使用应用的缓存目录。
     *
     * @param context    Context
     * @param uniqueName 缓存目录名,比如bitmap
     * @return
     */
    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.d("", "### context : " + context + ", dir = " + context.getExternalCacheDir());
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public synchronized Bitmap get(final BitmapRequest bean) {
        // 图片解析器
        BitmapDecoder decoder = new BitmapDecoder() {

            @Override
            public Bitmap decodeBitmapWithOption(BitmapFactory.Options options) {
                final InputStream inputStream = getInputStream(bean.getImageUriMd5());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null,
                        options);
                IOUtil.closeQuietly(inputStream);
                return bitmap;
            }
        };

        return decoder.decodeBitmap(bean.getImageViewWidth(),
                bean.getImageViewHeight());
    }

    private InputStream getInputStream(String md5) {
        DiskLruCache.Snapshot snapshot;
        try {
            snapshot = mDiskLruCache.get(md5);
            if (snapshot != null) {
                return snapshot.getInputStream(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * sd卡缓存只缓存从网络下下载下来的图片,本地图片则不缓存
     */
    @Override
    public void put(BitmapRequest key, Bitmap value) {
        if (key.isJustCacheInMemory()) {
            Log.e(IMAGE_DISK_CACHE, "### 仅缓存在内存中");
            return;
        }
        DiskLruCache.Editor editor = null;
        try {
            // 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
            editor = mDiskLruCache.edit(key.getImageUriMd5());
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                if (writeBitmapToDisk(value, outputStream)) {
                    // 写入disk缓存
                    editor.commit();
                } else {
                    editor.abort();
                }
                IOUtil.closeQuietly(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean writeBitmapToDisk(Bitmap bitmap, OutputStream outputStream) {
        BufferedOutputStream bos = new BufferedOutputStream(outputStream, 8 * 1024);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        boolean result = true;
        try {
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        } finally {
            IOUtil.closeQuietly(bos);
        }

        return result;
    }

    @Override
    public void remove(BitmapRequest key) {
        try {
//            mDiskLruCache.remove(Md5Helper.toMD5(key.getImageUriMd5()));
            mDiskLruCache.remove(key.getImageUriMd5());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
