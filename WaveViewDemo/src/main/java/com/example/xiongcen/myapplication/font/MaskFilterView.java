package com.example.xiongcen.myapplication.font;

import android.app.Activity;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 模糊遮罩滤镜
 * Created by xiongcen on 16/8/29.
 */
public class MaskFilterView extends View {
    private static final int RECT_SIZE = 500;
    private Paint mPaint;// 画笔
    private Context mContext;// 上下文环境引用

    private int left, top, right, bottom;//

    public MaskFilterView(Context context) {
        this(context, null);
    }

    public MaskFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        // 关闭硬件加速很重要，只有这一句代码才能让滤镜生效
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        // 初始化画笔
        initPaint();

        // 初始化资源
        initRes(context);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        // 实例化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0xFF603811);

        // 设置画笔遮罩滤镜
        // radius值越大阴影越扩散
        // SOLID的效果就是在图像的Alpha边界外产生一层与Paint颜色一致的阴影效果而不影响图像本身
        mPaint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.SOLID));
    }

    /**
     * 初始化资源
     */
    private void initRes(Context context) {
        /*
         * 计算位图绘制时左上角的坐标使其位于屏幕中心
         */
        left = MeasureUtil.getScreenSize((Activity) mContext)[0] / 2 - RECT_SIZE / 2;
        top = MeasureUtil.getScreenSize((Activity) mContext)[1] / 2 - RECT_SIZE / 2;
        right = MeasureUtil.getScreenSize((Activity) mContext)[0] / 2 + RECT_SIZE / 2;
        bottom = MeasureUtil.getScreenSize((Activity) mContext)[1] / 2 + RECT_SIZE / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.GRAY);

        // 画一个矩形
        canvas.drawRect(left, top, right, bottom, mPaint);
    }
}
