package com.example.xiongcen.myapplication.circleimg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.xiongcen.myapplication.R;

/**
 * Created by xiongcen on 16/9/28.
 */
public class CircleActivity extends AppCompatActivity {

    private int mPathType = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_activity);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a123);

        final CircleImageViewA imgViewA = (CircleImageViewA) findViewById(R.id.imgViewA);
        imgViewA.setImageBitmap(bitmap);
        imgViewA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPathType++;
                imgViewA.setPathType(mPathType % 5);
            }
        });
        imgViewA.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startActivity(new Intent(CircleActivity.this, Activity2.class));
                return true;
            }
        });


        CircleImageViewB imgViewB = (CircleImageViewB) findViewById(R.id.imgViewB);
        imgViewB.setImageBitmap(bitmap);
        imgViewB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CircleActivity.this, Activity3.class));
            }
        });
    }

}
