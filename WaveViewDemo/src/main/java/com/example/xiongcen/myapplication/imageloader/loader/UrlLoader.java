package com.example.xiongcen.myapplication.imageloader.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.xiongcen.myapplication.imageloader.disklrucache.IOUtil;
import com.example.xiongcen.myapplication.imageloader.request.BitmapRequest;
import com.example.xiongcen.myapplication.imageloader.utils.BitmapDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xiongcen on 16/11/6.
 */

public class UrlLoader extends AbsLoader {

    private InputStream is = null;

    @Override
    protected Bitmap onLoadImage(BitmapRequest request) {
        final String imageUrl = request.getImageUri();
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(15 * 1000);
            connection.setReadTimeout(20 * 1000);
            connection.setUseCaches(true);
            int responseCode = connection.getResponseCode();
            if (responseCode >= 300) {
                connection.disconnect();
                throw new IOException(responseCode + " " + connection.getResponseMessage() +
                        " " + responseCode);
            }
            is = connection.getInputStream();

            // 图片解析器
            BitmapDecoder decoder = new BitmapDecoder() {

                @Override
                public Bitmap decodeBitmapWithOption(BitmapFactory.Options options) {
                    Bitmap bitmap = BitmapFactory.decodeStream(is, null,
                            options);
                    return bitmap;
                }
            };
            bitmap = decoder.decodeBitmap(request.getImageViewWidth(),
                    request.getImageViewHeight());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(is);
            if (connection != null) {
                // 关闭流
                connection.disconnect();
            }
            return bitmap;
        }
    }
}
