package com.example.xiongcen.myapplication.porter.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;


public class FreeShapeImageView extends ImageView {

    private Bitmap backgroundBitmap;

    private Bitmap mBitmap;

    private int viewWidth;

    private int viewHeight;
    private Paint paint = new Paint();

    {
        paint.setStyle(Paint.Style.STROKE);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除，大家一看效果就明白了
    }
    private PaintFlagsDrawFilter pdf = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    public FreeShapeImageView(Context context) {
        this(context,null,0);
    }
    public FreeShapeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public FreeShapeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mBitmap!=null && backgroundBitmap!=null){

            int min = Math.min(viewWidth, viewHeight);
            backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, min, min, true);
            mBitmap = Bitmap.createScaledBitmap(mBitmap, min, min, true);
            canvas.setDrawFilter(pdf);
            canvas.drawBitmap(createImage(), 0, 0, paint);
        }
    }
    private Bitmap createImage()
    {
        Bitmap finalBmp = Bitmap.createBitmap(viewWidth,viewHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(finalBmp);//finalBmp
        canvas.setDrawFilter(pdf);

//        Bitmap bm = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(bm);
//        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
//        c.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, getMeasuredHeight() / 2, p);
//        canvas.drawBitmap(bm, 0, 0, paint);

        canvas.drawBitmap(backgroundBitmap, 0, 0, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(mBitmap, 0, 0, paint);
        paint.setXfermode(null);
        return finalBmp;
    }
    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setBitmaps();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setBitmaps();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setBitmaps();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setBitmaps();
    }
    private void setBitmaps(){
        if(null==getBackground()){
            throw new IllegalArgumentException(String.format("background is null."));
        }else{
            backgroundBitmap = getBitmapFromDrawable(getBackground());
            invalidate();
        }
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        super.setScaleType(ScaleType.CENTER_CROP);
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap bitmap;
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }

//        Bitmap bm = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(bm);
//        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
//        c.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, getMeasuredHeight() / 2, p);
//        return bm;

    }



    public static Bitmap ZoomBitmap(Bitmap bitmap, Context ctx, int targetW, int targetH) {
        if(bitmap == null) return null;

        int width = bitmap.getWidth();// drawable.getIntrinsicWidth();
        int height = bitmap.getHeight();// drawable.getIntrinsicHeight();

        // 创建操作图片用的Matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放比例
        float scaleWidth = ((float) targetW / width);
        float scaleHeight = ((float) targetH / height);
        // 设置缩放比例
        matrix.postScale(scaleWidth, scaleHeight);
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

}
