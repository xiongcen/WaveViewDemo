package com.example.xiongcen.myapplication.network.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.example.xiongcen.myapplication.R;
import com.example.xiongcen.myapplication.network.core.RequestQueue;
import com.example.xiongcen.myapplication.network.core.SimpleNet;
import com.example.xiongcen.myapplication.network.requests.Request;
import com.example.xiongcen.myapplication.network.requests.StringRequest;

import java.io.ByteArrayOutputStream;

/**
 * Created by xiongcen on 16/11/19.
 */

public class MainActivity extends Activity {

    // 1、构建请求队列
    RequestQueue mQueue;
    TextView mResultTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net);
        mQueue = SimpleNet.newRequestQueue(getApplicationContext());
        mResultTv = (TextView) findViewById(R.id.result_tv);
        sendStringRequest();
    }

    /**
     * 发送GET请求,返回的是String类型的数据, 同理还有{@see JsonRequest}、{@see MultipartRequest}
     */
    private void sendStringRequest() {
        StringRequest request = new StringRequest(Request.HttpMethod.GET, "https://xiongcen.github.io/",
                new Request.RequestListener<String>() {

                    @Override
                    public void onComplete(int stCode, String response, String errMsg) {
                        mResultTv.setText(Html.fromHtml(response));
                    }
                });

        mQueue.addRequest(request);
    }

    /**
     * 发送MultipartRequest,可以传字符串参数、文件、Bitmap等参数,这种请求为POST类型
     */
//    protected void sendMultiRequest() {
//        // 2、创建请求
//        MultipartRequest multipartRequest = new MultipartRequest("你的url",
//                new Request.RequestListener<String>() {
//                    @Override
//                    public void onComplete(int stCode, String response, String errMsg) {
//                        // 该方法执行在UI线程
//                    }
//                });
//
//        // 3、添加各种参数
//        // 添加header
//        multipartRequest.addHeader("header-name", "value");
//
//        // 通过MultipartEntity来设置参数
//        MultipartEntity multi = multipartRequest.getMultiPartEntity();
//        // 文本参数
//        multi.addStringPart("location", "模拟的地理位置");
//        multi.addStringPart("type", "0");
//
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
//        // 直接从上传Bitmap
//        multi.addBinaryPart("images", bitmapToBytes(bitmap));
//        // 上传文件
//        multi.addFilePart("imgfile", new File("storage/emulated/0/test.jpg"));
//
//        // 4、将请求添加到队列中
//        mQueue.addRequest(multipartRequest);
//    }
    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    protected void onDestroy() {
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
        mQueue.stop();
        super.onDestroy();
    }
}
