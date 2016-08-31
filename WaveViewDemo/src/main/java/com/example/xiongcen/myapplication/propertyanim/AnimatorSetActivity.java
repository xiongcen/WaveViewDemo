package com.example.xiongcen.myapplication.propertyanim;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.example.xiongcen.myapplication.R;

/**
 * Created by xiongcen on 16/8/31.
 */
public class AnimatorSetActivity extends Activity {

    private ImageView mBlueBall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animator_set);
        mBlueBall = (ImageView) findViewById(R.id.id_ball);
    }

    /**
     * 简单的多动画Together
     *
     * @param view
     */
    public void togetherRun(View view) {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(mBlueBall, "scaleX",
                1.0f, 2f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(mBlueBall, "scaleY",
                1.0f, 2f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(2000);
        animSet.setInterpolator(new LinearInterpolator());
        //两个动画同时执行
        animSet.playSequentially(anim1, anim2);
        animSet.start();
    }

    /**
     * 多动画按次序执行
     *
     * @param view
     */
    public void playWithAfter(View view) {
        float cx = mBlueBall.getX();

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(mBlueBall, "scaleX",
                1.0f, 2f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(mBlueBall, "scaleY",
                1.0f, 2f);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(mBlueBall,
                "x", cx, 0f);
        ObjectAnimator anim4 = ObjectAnimator.ofFloat(mBlueBall,
                "x", cx);

        /**
         * anim1，anim2,anim3同时执行
         * anim4接着执行
         *
         * animSet.play().with();也是支持链式编程的，但是不要想着狂点，
         * 比如animSet.play(anim1).with(anim2).before(anim3).before(anim5);
         * 这样是不行的，系统不会根据你写的这一长串来决定先后的顺序
         */
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim1).with(anim2);
        animSet.play(anim2).with(anim3);
        animSet.play(anim4).after(anim3);
        animSet.setDuration(1000);
        animSet.start();

    }
}
