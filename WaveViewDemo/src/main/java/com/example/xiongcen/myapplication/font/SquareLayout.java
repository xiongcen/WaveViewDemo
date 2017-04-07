package com.example.xiongcen.myapplication.font;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xiongcen on 17/4/6.
 */

public class SquareLayout extends ViewGroup {

    private static final int ORIENTATION_HORIZONTAL = 0, ORIENTATION_VERTICAL = 1;// 排列方向的常量标识值
    private static final int DEFAULT_MAX_ROW = Integer.MAX_VALUE, DEFAULT_MAX_COLUMN = Integer.MAX_VALUE;// 最大行列默认值

    private int mMaxRow = DEFAULT_MAX_ROW;// 最大行数
    private int mMaxColumn = DEFAULT_MAX_COLUMN;// 最大列数

    private int mOrientation = ORIENTATION_HORIZONTAL;// 排列方向默认横向

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 初始化最大行列数
        mMaxRow = mMaxColumn = 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*
         * 声明临时变量存储父容器的期望值
         * 该值应该等于父容器的内边距加上所有子元素的测量宽高和外边距
         */
        int parentDesireWidth = 0;
        int parentDesireHeight = 0;

        // 声明临时变量存储子元素的测量状态
        int childMeasureState = 0;

        if (getChildCount() > 0) {

            // 声明两个一维数组存储子元素宽高数据
            int[] childWidths = new int[getChildCount()];
            int[] childHeights = new int[getChildCount()];

            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);

                if (childAt.getVisibility() != View.GONE) {
                    // 测量子元素并考量其外边距
                    measureChildWithMargins(childAt, widthMeasureSpec, 0, heightMeasureSpec, 0);

                    // 比较子元素测量宽高并比较取其较大值
                    int childMeasureSize = Math.max(childAt.getMeasuredWidth(), childAt.getMeasuredHeight());

                    // 重新封装子元素测量规格
                    int childMeasureSpec = MeasureSpec.makeMeasureSpec(childMeasureSize, MeasureSpec.EXACTLY);

                    // 重新测量子元素
                    childAt.measure(childMeasureSpec, childMeasureSpec);

                    // 获取子元素布局参数
                    MarginLayoutParams mlp = (MarginLayoutParams) childAt.getLayoutParams();

                    /**
                     * 考量外边距计算子元素实际宽高
                     */
                    int childActualWidth = childAt.getMeasuredWidth() + mlp.leftMargin + mlp.rightMargin;
                    int childActualHeight = childAt.getMeasuredHeight() + mlp.topMargin + mlp.bottomMargin;

                    // 如果为横向排列
                    if (mOrientation == ORIENTATION_HORIZONTAL) {
                        // 累加子元素的实际宽度
                        parentDesireWidth += childActualWidth;

                        // 获取子元素中高度最大值
                        parentDesireHeight = Math.max(parentDesireHeight, childActualHeight);
                    }
                    // 如果为纵向排列
                    else if (mOrientation == ORIENTATION_VERTICAL) {
                        // 累加子元素的实际高度
                        parentDesireHeight += childActualHeight;

                        // 获取子元素宽度最大值
                        parentDesireWidth = Math.max(parentDesireWidth, childActualWidth);
                    }

                    // 合并子元素的测量状态
                    childMeasureState = combineMeasuredStates(childMeasureState, childAt.getMeasuredState());
                }

            }
            /**
             * 考量父容器内边距将其累加到期望值
             */
            parentDesireWidth += getPaddingLeft() + getPaddingRight();
            parentDesireHeight += getPaddingTop() + getPaddingBottom();

            System.out.println("xc " + "SquareLayout.onMeasure parentDesireWidth:" + parentDesireWidth + ",parentDesireHeight:" + parentDesireHeight);

            /**
             * 尝试比较父容器期望值与Android建议的最小值大小并取较大值
             */
            parentDesireWidth = Math.max(parentDesireWidth, getSuggestedMinimumWidth());
            parentDesireHeight = Math.max(parentDesireHeight, getSuggestedMinimumHeight());

        }
        // 确定父容器的测量宽高
        int width = resolveSizeAndState(parentDesireWidth, widthMeasureSpec, childMeasureState);
        int height = resolveSizeAndState(parentDesireHeight, heightMeasureSpec, childMeasureState << MEASURED_HEIGHT_STATE_SHIFT);
        System.out.println("xc " + "SquareLayout.onMeasure width:" + width + ",height:" + height);
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() > 0) {
            // 声明临时变量存储宽高倍增值
            int multi = 0;

            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                if (childAt.getVisibility() != View.GONE) {
                    MarginLayoutParams mlp = (MarginLayoutParams) childAt.getLayoutParams();

                    int childActualSize = childAt.getMeasuredWidth();

                    System.out.println("xc " + "SquareLayout.onLayout i:" + i + ",childActualSize:" + childActualSize + ",height:" + childAt.getMeasuredHeight());

                    if (mOrientation == ORIENTATION_HORIZONTAL) {
                        // 确定子元素左上、右下坐标
                        childAt.layout(getPaddingLeft() + mlp.leftMargin + multi, getPaddingTop() + mlp.topMargin, childActualSize + getPaddingLeft()
                                + mlp.leftMargin + multi, childActualSize + getPaddingTop() + mlp.topMargin);

                        // 累加倍增值
                        multi += childActualSize + mlp.leftMargin + mlp.rightMargin;

                    } else if (mOrientation == ORIENTATION_VERTICAL) {
                        // 确定子元素左上、右下坐标
                        childAt.layout(getPaddingLeft() + mlp.leftMargin, getPaddingTop() + mlp.topMargin + multi, childActualSize + getPaddingLeft()
                                + mlp.leftMargin, childActualSize + getPaddingTop() + mlp.topMargin + multi);

                        // 累加倍增值
                        multi += childActualSize + mlp.topMargin + mlp.bottomMargin;
                    }
                }
            }
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }
}
