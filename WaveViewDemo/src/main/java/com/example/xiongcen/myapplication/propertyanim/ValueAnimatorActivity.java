package com.example.xiongcen.myapplication.propertyanim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.example.xiongcen.myapplication.R;

/**
 * Created by xiongcen on 16/8/31.
 */
public class ValueAnimatorActivity extends Activity {

    private ImageView mBlueBall;
    private float mScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.value_animator);

        mBlueBall = (ImageView) findViewById(R.id.id_ball);

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;
    }

    /**
     * 自由落体 垂直
     *
     * @param view
     */
    public void verticalRun(View view) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, mScreenHeight
                - mBlueBall.getHeight());
        animator.setTarget(mBlueBall);
        animator.setDuration(1000).start();
        // animator.setInterpolator(value)
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBlueBall.setTranslationY((Float) animation.getAnimatedValue());
            }
        });
    }

    /**
     * 抛物线
     *
     * @param view
     */
    public void paowuxian(View view) {
//        // 1.ValueAnimator的实现
//        ValueAnimator valueAnimator = new ValueAnimator();
//        valueAnimator.setDuration(3000);
//        valueAnimator.setObjectValues(new PointF(0, 0));
//        valueAnimator.setInterpolator(new LinearInterpolator());
//        valueAnimator.setEvaluator(new TypeEvaluator<PointF>() {
//            @Override
//            public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
//                System.out.println("xc " + "ValueAnimatorActivity.evaluate fraction * 3 =" + (fraction * 3));
//                PointF point = new PointF();
//                point.x = 200 * fraction * 3;
//                point.y = 100 * (fraction * 3);
//                return point;
//            }
//        });
//        valueAnimator.start();
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                PointF point = (PointF) animation.getAnimatedValue();
//                mBlueBall.setX(point.x);
//                mBlueBall.setY(point.y);
//            }
//        });

        // 2.ViewPropertyAnimator的实现，不需要调用start，会隐式启动动画
        mBlueBall.animate().x(100).y(200);
    }

    /**
     * 淡出且删除
     *
     * @param view
     */
    public void fadeOut(View view) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mBlueBall, "alpha", 0.5f);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                System.out.println("xc " + "ValueAnimatorActivity.onAnimationStart ");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                System.out.println("xc " + "ValueAnimatorActivity.onAnimationEnd ");
                ViewGroup parent = (ViewGroup) mBlueBall.getParent();
                if (parent != null) {
                    parent.removeView(mBlueBall);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                System.out.println("xc " + "ValueAnimatorActivity.onAnimationCancel ");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                System.out.println("xc " + "ValueAnimatorActivity.onAnimationRepeat ");
            }
        });
        anim.start();
    }
}
