package com.example.xiongcen.myapplication.mesh;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by xiongcen on 16/10/21.
 */

public class MeshActivity extends Activity {

    private static final boolean DEBUG_MODE = true;
    private BitmapMesh.SampleView mSampleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout linearLayout = new LinearLayout(this);

        mSampleView = new BitmapMesh.SampleView(this);
        mSampleView.setIsDebug(DEBUG_MODE);
        mSampleView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        Button btn = new Button(this);
        btn.setText("Run");
        btn.setTextSize(20.0f);
        btn.setLayoutParams(new LinearLayout.LayoutParams(150, -2));
        btn.setOnClickListener(new View.OnClickListener() {
            boolean mReverse = false;

            @Override
            public void onClick(View v) {
                if (mSampleView.startAnimation(mReverse)) {
                    mReverse = !mReverse;
                }
            }
        });

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.addView(btn);
        linearLayout.addView(mSampleView);

        setContentView(linearLayout);
    }
}
