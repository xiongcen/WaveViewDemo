package com.example.xiongcen.myapplication.porter.view;

/**
 * Created by zhaonan on 16/6/13.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class ClipRevealFrame extends FrameLayout {

    boolean clipOutlines;
    float centerX;
    float centerY;
    float radius;
    private Path revealPath;

    public ClipRevealFrame(Context context) {
        super(context);
        init();
    }

    public ClipRevealFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClipRevealFrame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        revealPath = new Path();
        clipOutlines = false;
        setWillNotDraw(false);
        if(isHardwareAccelerated()) { // 5.0 以下默认是开启硬件加速的 开启后addCircle 画出来的路径不是圆形是方的
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void setClipOutLines(boolean shouldClip) {
        clipOutlines = shouldClip;
    }

    public void setClipCenter(final int x, final int y) {
        centerX = x;
        centerY = y;
    }

    public void setClipRadius(final float radius) {
        this.radius = radius;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        if (!clipOutlines) {
            super.draw(canvas);
            return;
        }
        final int state = canvas.save();
        revealPath.reset();
        revealPath.addCircle(centerX, centerY, radius, Path.Direction.CW);
        canvas.clipPath(revealPath);
        //canvas.clipPath(revealPath, Region.Op.REPLACE);
        super.draw(canvas);
        canvas.restoreToCount(state);
    }

}

