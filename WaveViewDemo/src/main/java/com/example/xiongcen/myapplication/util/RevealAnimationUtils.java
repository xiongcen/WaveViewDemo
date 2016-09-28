package com.example.xiongcen.myapplication.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;

import com.example.xiongcen.myapplication.porter.view.ClipRevealFrame;

/**
 * Created by zhaonan on 16/8/23.
 */

public class RevealAnimationUtils {
    public static Animator createCircularReveal(final ClipRevealFrame view, int x, int y, float startRadius,
                                                float endRadius) {
        Animator reveal;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            reveal = ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, endRadius);
//
//        }
//        else
        {
            view.setClipOutLines(true);
            view.setClipCenter(x, y);
            reveal = ObjectAnimator.ofFloat(view, "ClipRadius", startRadius, endRadius);
            reveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setClipOutLines(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        return reveal;
    }
}
