package com.example.xiongcen.myapplication.circleimg;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.xiongcen.myapplication.R;
import com.example.xiongcen.myapplication.porter.view.ClipRevealFrame;
import com.example.xiongcen.myapplication.util.RevealAnimationUtils;

public class Activity2 extends AppCompatActivity {

    private ClipRevealFrame mRootLayout;
    private Animator mEnterRevealAni;
    private Animator mExitRevealAni;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        mRootLayout = (ClipRevealFrame) findViewById(R.id.root_layout);
        //mRootLayout.setBackgroundColor(Color.GRAY);

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                mEnterRevealAni = RevealAnimationUtils.createCircularReveal(mRootLayout, mRootLayout.getWidth() / 2, mRootLayout.getHeight() / 2, 0, (float) Math.hypot((double) mRootLayout.getWidth(), (double) mRootLayout.getHeight()));
                mEnterRevealAni.setInterpolator(new AccelerateDecelerateInterpolator());
                mEnterRevealAni.setDuration(800);
                mEnterRevealAni.start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(isAniRunning(mEnterRevealAni) || isAniRunning(mExitRevealAni)) {
            return;
        }
        mExitRevealAni = RevealAnimationUtils.createCircularReveal(mRootLayout, mRootLayout.getWidth() / 2, mRootLayout.getHeight() / 2,  (float) Math.hypot((double) mRootLayout.getWidth(), (double) mRootLayout.getHeight()), 0);
        mExitRevealAni.setInterpolator(new AccelerateDecelerateInterpolator());
        mExitRevealAni.setDuration(800);
        mExitRevealAni.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRootLayout.setVisibility(View.GONE);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mExitRevealAni.start();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }


    private boolean isAniRunning(Animator animator) {
        if(animator != null && animator.isRunning()) {
            return true;
        }
        return false;
    }
}
