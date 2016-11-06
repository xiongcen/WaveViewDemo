package com.example.xiongcen.myapplication.imageloader.loader;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.xiongcen.myapplication.imageloader.request.BitmapRequest;

/**
 * Created by xiongcen on 16/11/6.
 */

public class NullLoader extends AbsLoader {

    @Override
    public Bitmap onLoadImage(BitmapRequest request) {
        Log.e(NullLoader.class.getSimpleName(), "### wrong schema, your image uri is : "
                + request.getImageUri());
        return null;
    }

}
