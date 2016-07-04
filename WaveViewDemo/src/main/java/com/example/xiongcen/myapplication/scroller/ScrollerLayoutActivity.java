package com.example.xiongcen.myapplication.scroller;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.xiongcen.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ScrollerLayoutActivity extends Activity implements ScrollerLayout.ScrollRershListener {

    private ListView mListView;
    private ImageView mImageView;
    private ScrollerLayout layout;
    private Bitmap bitmap;
    private Matrix matrix = new Matrix();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroller_layout_activity);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scroller_layout_icon);
        mListView = (ListView) findViewById(R.id.listview);
        mImageView = (ImageView) findViewById(R.id.imageView);
        layout = (ScrollerLayout) findViewById(R.id.wxlayout);
        layout.setOnScrollRershListener(this);
        List<String> datas = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            datas.add("Nipuream " + i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datas);
        mListView.setAdapter(adapter);
    }

    @Override
    public void Rersh(float value) {
        if (value < 0) {
            mImageView.setVisibility(View.GONE);
            return;
        }
        mImageView.setVisibility(View.VISIBLE);
        rotateBitmap(value);
        transY(value);
    }

    private void rotateBitmap(float value) {
        float degree = value % 360 * 3;
        matrix.reset();
        matrix.setRotate(degree);
        Bitmap tmpBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        mImageView.setImageBitmap(tmpBitmap);
    }

    private void transY(float value) {
        value = Math.abs(value);
        if (value < 150) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
            params.topMargin = (int) value;
            mImageView.setLayoutParams(params);
        }
    }

    @Override
    public void startRersh() {
        mImageView.clearAnimation();
        mImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void endRersh(final float value) {
        if (Math.abs(value) < 150) {
            transAnim(value, 500);
            return;
        }

        final ObjectAnimator rotate = ObjectAnimator.ofFloat(mImageView, "rotation", 0f, 360f);
        rotate.setDuration(1000);
        rotate.setInterpolator(null);
        rotate.setRepeatCount(Integer.MAX_VALUE);
        rotate.start();

        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                // TODO Auto-generated method stub
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 1;
            }

            @Override
            protected void onPostExecute(Integer result) {
                rotate.cancel();
                transAnim(value, 2000);
            }

        }.execute();
    }

    private void transAnim(final float value, int duration) {
        final TranslateAnimation transY = new TranslateAnimation(0, 0, 0, -(value + mImageView.getHeight()));
        transY.setDuration(duration);
        transY.setFillAfter(false);
        transY.setInterpolator(new DecelerateInterpolator());
        transY.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                mImageView.setVisibility(View.GONE);
            }

        });
        mImageView.startAnimation(transY);
    }
}
