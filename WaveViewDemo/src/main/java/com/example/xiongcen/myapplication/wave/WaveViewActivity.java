package com.example.xiongcen.myapplication.wave;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.xiongcen.myapplication.R;

/**
 * Created by xiongcen on 16-7-4.
 */
public class WaveViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wave_demo_layout);

        Button button = (Button) findViewById(R.id.button);
        final WaveView view = (WaveView) findViewById(R.id.wave);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.startAnim();
            }
        });


    }
}
