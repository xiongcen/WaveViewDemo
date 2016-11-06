package com.example.xiongcen.myapplication.imageloader.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.example.xiongcen.myapplication.imageloader.request.BitmapRequest;
import com.example.xiongcen.myapplication.imageloader.utils.BitmapDecoder;

import java.io.File;

/**
 * Created by xiongcen on 16/11/6.
 */

public class LocalLoader extends AbsLoader {

    @Override
    protected Bitmap onLoadImage(BitmapRequest request) {
        final String imagePath = Uri.parse(request.getImageUri()).getPath();
        final File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            return null;
        }
        request.setJustCacheInMemory(true);

        // 加载图片
        BitmapDecoder decoder = new BitmapDecoder() {

            @Override
            public Bitmap decodeBitmapWithOption(BitmapFactory.Options options) {
                return BitmapFactory.decodeFile(imagePath, options);
            }
        };

        return decoder.decodeBitmap(request.getImageViewWidth(),
                request.getImageViewHeight());
    }
}
