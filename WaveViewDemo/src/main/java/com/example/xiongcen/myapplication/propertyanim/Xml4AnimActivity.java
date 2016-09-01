package com.example.xiongcen.myapplication.propertyanim;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.xiongcen.myapplication.R;

/**
 * Created by xiongcen on 16/9/1.
 */
public class Xml4AnimActivity extends Activity {

    private ImageView mMv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xml_animator);
        mMv = (ImageView) findViewById(R.id.id_mv);
    }

    public void scaleX(View view) {
        Animator anim = AnimatorInflater.loadAnimator(this, R.animator.scalex);
        // 不设置target会异常
        anim.setTarget(mMv);
        anim.start();
    }

    public void scaleXandScaleY(View view) {
        Animator anim = AnimatorInflater.loadAnimator(this, R.animator.scale);
        // 不设置缩放的中心点，默认是中心缩放；如果设置(0，0)，则以View的左上角为中心点
        mMv.setPivotX(0);
        mMv.setPivotY(0);
        //显示的调用invalidate
//        mMv.invalidate();
        anim.setTarget(mMv);
        anim.start();
    }
}
