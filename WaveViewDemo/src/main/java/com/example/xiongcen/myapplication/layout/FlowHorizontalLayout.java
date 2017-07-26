package com.example.xiongcen.myapplication.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


import com.example.xiongcen.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 固定横向排列的FlowLayout
 *
 * 默认添加的View只能水平排列
 * 设置mPortraitGravity表示设置纵向摆放位置，若设置居中，则以该行行高居中计算；
 * 设置mLandscapeGravity表示设置横向摆放位置，若设置居中，则以该行行宽居中计算。
 * Created by xiongcen on 2017/7/11.
 */

public class FlowHorizontalLayout extends ViewGroup {

    private static final String TAG = FlowHorizontalLayout.class.getSimpleName();

    public static final int GRAVITY_START = 0;
    public static final int GRAVITY_CENTER = 1;
    public static final int GRAVITY_END = 2;

    /**
     * 两个子控件之间的纵向间隙
     */
    private int mPortraitSpacing = 0;
    /**
     * 两个子控件之间的横向间隙
     */
    private int mLandscapeSpacing = 0;
    /**
     * 纵向摆放位置(上中下)
     */
    private int mPortraitGravity = GRAVITY_START;
    /**
     * 横向摆放位置(左中右)
     */
    private int mLandscapeGravity = GRAVITY_START;

    public FlowHorizontalLayout(Context context) {
        this(context, null);
    }

    public FlowHorizontalLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FlowHorizontalLayout(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.FlowHorizontalLayout);
        try {
            mLandscapeSpacing = a.getDimensionPixelSize(R.styleable.FlowHorizontalLayout_landscapeSpacing, 0);
            mPortraitSpacing = a.getDimensionPixelSize(R.styleable.FlowHorizontalLayout_portraitSpacing, 0);
            mLandscapeGravity = a.getInt(R.styleable.FlowHorizontalLayout_landscapeGravity, GRAVITY_START);
            mPortraitGravity = a.getInt(R.styleable.FlowHorizontalLayout_portraitGravity, GRAVITY_START);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthActualSize = widthSpecSize - this.getPaddingRight() - this.getPaddingLeft();

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightActualSize = heightSpecSize - this.getPaddingTop() - this.getPaddingBottom();

        // 容器期望宽高
        int parentWidth = 0;
        int parentHeight = 0;

        // 当前行宽
        int lineWidth = 0;
        // 当前行高
        int lineHeight = 0;

        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < getChildCount(); i++) {

                View child = getChildAt(i);

                // 测量子元素并考量其外边距
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);

                // 获取子元素布局参数
                MarginLayoutParams mlp = (MarginLayoutParams) child.getLayoutParams();

                // 计算子元素实际宽高
                int childActualWidth = child.getMeasuredWidth() + mlp.leftMargin + mlp.rightMargin;
                int childActualHeight = child.getMeasuredHeight() + mlp.topMargin + mlp.bottomMargin;

                Log.i(TAG, "onMeasure ->第" + i + "个 childActualWidth:" + childActualWidth + ";childActualHeight:" + childActualHeight);

                // 第一个元素的宽度就超过一行宽那么整个View都不显示
                if (i == 0 && childActualWidth > widthActualSize) {
                    this.setVisibility(View.GONE);
                    break;
                }

                if (child.getVisibility() != View.GONE) {
                    // 换行的条件应该加上mLandscapeSpacing，如果加上mLandscapeSpacing后发现需要换行，
                    // 则rowWidth不应该包含即将要换行的View的mLandscapeSpacing，
                    // 所以每次换行或者第一个View的rowWidth应该都只是View实际的宽度，
                    // 确保横向有下一个元素才加上mLandscapeSpacing。
                    int totalWidth = 0;
                    if (i == 0) {
                        totalWidth = lineWidth + childActualWidth;
                    } else {
                        totalWidth = lineWidth + childActualWidth + mLandscapeSpacing;
                    }

                    if (totalWidth > widthActualSize) {
                        // 换行
                        parentWidth = Math.max(parentWidth, lineWidth);
                        parentHeight += lineHeight + mPortraitSpacing;

                        lineWidth = childActualWidth;
                        lineHeight = childActualHeight;
                    } else {
                        // 不换行
                        lineWidth = totalWidth;
                        lineHeight = Math.max(lineHeight, childActualHeight);
                    }
                }

                // 最后一行是不会超出width范围的，所以要单独处理
                if (i == childCount - 1) {
                    parentWidth = Math.max(parentWidth, lineWidth);
                    parentHeight += lineHeight;
                }

                Log.i(TAG, "onMeasure ->第" + i + "行 lineWidth:" + lineWidth + ";lineHeight:" + lineHeight);

            }

            // 父容器内边距将其累加到期望值
            parentWidth += getPaddingLeft() + getPaddingRight();
            parentHeight += getPaddingTop() + getPaddingBottom();

            Log.i(TAG, "onMeasure -> parentWidth:" + parentWidth + ";parentHeight:" + parentHeight);

            setMeasuredDimension(widthSpecMode == MeasureSpec.EXACTLY ? widthSpecSize : parentWidth,
                    heightSpecMode == MeasureSpec.EXACTLY ? heightSpecSize : parentHeight);
        }

    }

    private Map<Integer, List<Integer>> position = new HashMap<>();

    private void calculatePosition(int start, int end, int widthActualSize, int lineWidth, int lineHeight) {
        for (int j = start; j < end; j++) {
            List<Integer> integers = position.get(j);
            if (integers == null || integers.size() < 4) {
                continue;
            }
            int oldLeft = integers.get(0);
            int oldTop = integers.get(1);
            int oldRight = integers.get(2);
            int oldBottom = integers.get(3);

            int newLeft = oldLeft;
            int newTop = oldTop;
            int newRight = oldRight;
            int newBottom = oldBottom;

            int height = oldBottom - oldTop;


            if (mPortraitGravity == GRAVITY_START) {
                // 纵向摆放居上
                // top、bottom什么都不做
                if (mLandscapeGravity == GRAVITY_START) {
                    // 横向摆放居左
                    // left、right什么都不做
                } else if (mLandscapeGravity == GRAVITY_CENTER) {
                    // 横向摆放居中
                    int surplusWidth = (widthActualSize - lineWidth) / 2;
                    newLeft = oldLeft + surplusWidth;
                    newRight = oldRight + surplusWidth;
                } else if (mLandscapeGravity == GRAVITY_END) {
                    // 横向摆放居右
                    int surplusWidth = widthActualSize - lineWidth;
                    newLeft = oldLeft + surplusWidth;
                    newRight = oldRight + surplusWidth;
                }
            } else if (mPortraitGravity == GRAVITY_CENTER) {
                // 纵向摆放居中
                int surplusHeight = (lineHeight - height) / 2;
                newTop = oldTop + surplusHeight;
                newBottom = oldBottom + surplusHeight;

                if (mLandscapeGravity == GRAVITY_START) {
                    // 横向摆放居左
                    // left、right什么都不做
                } else if (mLandscapeGravity == GRAVITY_CENTER) {
                    // 横向摆放居中
                    int surplusWidth = (widthActualSize - lineWidth) / 2;
                    newLeft = oldLeft + surplusWidth;
                    newRight = oldRight + surplusWidth;
                } else if (mLandscapeGravity == GRAVITY_END) {
                    // 横向摆放居右
                    int surplusWidth = widthActualSize - lineWidth;
                    newLeft = oldLeft + surplusWidth;
                    newRight = oldRight + surplusWidth;
                }
            } else if (mPortraitGravity == GRAVITY_END) {
                // 纵向摆放居下
                int surplusHeight = lineHeight - height;
                newTop = oldTop + surplusHeight;
                newBottom = oldBottom + surplusHeight;

                if (mLandscapeGravity == GRAVITY_START) {
                    // 横向摆放居左
                    // left、right什么都不做
                } else if (mLandscapeGravity == GRAVITY_CENTER) {
                    // 横向摆放居中
                    int surplusWidth = (widthActualSize - lineWidth) / 2;
                    newLeft = oldLeft + surplusWidth;
                    newRight = oldRight + surplusWidth;
                } else if (mLandscapeGravity == GRAVITY_END) {
                    // 横向摆放居右
                    int surplusWidth = widthActualSize - lineWidth;
                    newLeft = oldLeft + surplusWidth;
                    newRight = oldRight + surplusWidth;
                }
            }

            integers.set(0, newLeft);
            integers.set(1, newTop);
            integers.set(2, newRight);
            integers.set(3, newBottom);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        position.clear();

        int widthActualSize = r - l - this.getPaddingRight() - this.getPaddingLeft();
        int heightActualSize = b - t - this.getPaddingTop() - this.getPaddingBottom();
        Log.i(TAG, "onLayout -> widthActualSize:" + widthActualSize + ";heightActualSize:" + heightActualSize);

        // 累加当前行的行宽
        int lineWidth = 0;
        // 当前行的行高
        int lineHeight = 0;
        // 当前View的top坐标和left坐标
        int top = getPaddingTop(), left = getPaddingLeft();

        int childCount = getChildCount();
        if (childCount > 0) {
            int lastChangeLineIndex = 0;
            for (int i = 0; i < childCount; i++) {

                View child = getChildAt(i);

                // 获取子元素布局参数
                MarginLayoutParams mlp = (MarginLayoutParams) child.getLayoutParams();

                // 计算子元素实际宽高
                int childActualWidth = child.getMeasuredWidth() + mlp.leftMargin + mlp.rightMargin;
                int childActualHeight = child.getMeasuredHeight() + mlp.topMargin + mlp.bottomMargin;

                // 第一个元素的宽度就超过一行宽那么整个View都不显示
                if (i == 0 && childActualWidth > widthActualSize) {
                    this.setVisibility(View.GONE);
                    break;
                }

                if (child.getVisibility() != View.GONE) {

                    // 换行的条件应该加上mLandscapeSpacing，如果加上mLandscapeSpacing后发现需要换行，
                    // 则rowWidth不应该包含即将要换行的View的mLandscapeSpacing，
                    // 所以每次换行或者第一个View的rowWidth应该都只是View实际的宽度，
                    // 确保横向有下一个元素才加上mLandscapeSpacing。
                    int totalWidth = 0;
                    if (i == 0) {
                        totalWidth = lineWidth + childActualWidth;
                    } else {
                        totalWidth = lineWidth + childActualWidth + mLandscapeSpacing;
                    }

                    if (totalWidth > widthActualSize) {
                        // 换行

                        // 先计算上一行的所有View应该摆放的位置
                        calculatePosition(lastChangeLineIndex, i, widthActualSize, lineWidth, lineHeight);
                        lastChangeLineIndex = i;

                        // 当前控件将跑到下一行，从最左边开始，所以left就是0，
                        // 而top则需要加上上一行的行高+mPortraitSpacing，才是这个控件的top点。
                        top += lineHeight + mPortraitSpacing;
                        left = getPaddingLeft();

                        // 重新初始化lineHeight和lineWidth
                        lineHeight = childActualHeight;
                        lineWidth = childActualWidth;
                    } else {
                        // 不换行
                        lineWidth = totalWidth;
                        lineHeight = Math.max(lineHeight, childActualHeight);
                    }

                    // 计算childView的left,top,right,bottom
                    int lc = left + mlp.leftMargin;
                    int tc = top + mlp.topMargin;
                    int rc = lc + child.getMeasuredWidth();
                    int bc = tc + child.getMeasuredHeight();

                    List<Integer> list = new ArrayList<>();
                    list.add(lc);
                    list.add(tc);
                    list.add(rc);
                    list.add(bc);
                    position.put(i, list);

                    // 将left置为下一子控件的起始点
                    left += childActualWidth + mLandscapeSpacing;

                    // 最后一行默认不换行，所以要单独处理
                    if (i == childCount - 1) {
                        calculatePosition(lastChangeLineIndex, childCount, widthActualSize, lineWidth, lineHeight);
                    }
                }
            }

            for (int i = 0; i < childCount; i++) {

                View child = getChildAt(i);

                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                List<Integer> integers = position.get(i);
                if (integers == null || integers.size() < 4) {
                    continue;
                }
                child.layout(integers.get(0), integers.get(1), integers.get(2), integers.get(3));
            }
        }
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 设置纵向摆放位置(上中下)
     *
     * @param mPortraitGravity
     */
    public void setPortraitGravity(int mPortraitGravity) {
        this.mPortraitGravity = mPortraitGravity;
    }

    /**
     * 横向摆放位置(左中右)
     *
     * @param mLandscapeGravity
     */
    public void setLandscapeGravity(int mLandscapeGravity) {
        this.mLandscapeGravity = mLandscapeGravity;
    }
}
