package com.example.xiongcen.myapplication.propertyanim;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.xiongcen.myapplication.R;

/**
 * Created by xiongcen on 16/8/30.
 */
public class ObjectAnimActivity extends Activity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.object_animator);
        imageView = (ImageView) findViewById(R.id.id_ball);
    }

    public void rotateyAnimRun(final View view) {
//        // 1.最简单一种ObjectAnimator展示
//        ObjectAnimator
//                .ofFloat(view, "rotationX", 0.0F, 360.0F)
//                .setDuration(500)//
//                .start();

        // 2.使用部分API实现多效果动画
        ObjectAnimator anim = ObjectAnimator//
                .ofFloat(view, "zhy", 1.0F, 0.0F)//
                .setDuration(500);//
        anim.start();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                System.out.println("xc " + "PropertyAnimActivity.onAnimationUpdate cVal=" + cVal);
                // 自己给view手动设置属性
                view.setAlpha(cVal);
                view.setScaleX(cVal);
                view.setScaleY(cVal);
            }
        });
    }

    // 多效果实现
    public void propertyValuesHolder(View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f,
                0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f,
                0, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f,
                0, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(1000).start();
    }

}

