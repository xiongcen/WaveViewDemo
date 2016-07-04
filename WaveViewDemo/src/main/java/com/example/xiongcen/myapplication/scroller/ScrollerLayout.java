package com.example.xiongcen.myapplication.scroller;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * Created by xiongcen on 16/7/3.
 */
public class ScrollerLayout extends LinearLayout {
    private int mTouchSlop;
    // 是否开始拖拽
    private boolean mIsBeingDragged;

    private float mLastMotionY;
    private float mInitialMotionY;
    private float resistance = 0.6f;
    private Scroller mScroller;
    private ListView mListView;
    private boolean isMove = false;
    private int duration = 300;
    private ScrollRershListener l;
    private boolean isRersh = false;

    public ScrollerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(final Context context) {
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        DecelerateInterpolator interpolator = new DecelerateInterpolator();
        mScroller = new Scroller(context, interpolator);
        post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mListView = (ListView) ScrollerLayout.this.getChildAt(0);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        // 手取消触碰或者放开手 不拦截，交由子view处理
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsBeingDragged = false;
            return false;
        }

        // 手不按下 并且 开始拖拽，则拦截，由父layout处理
        if (action != MotionEvent.ACTION_DOWN && mIsBeingDragged) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = mInitialMotionY = ev.getY();
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                final float y = ev.getY(), x = ev.getX();
                final float diff, absDiff;
                diff = y - mLastMotionY;
                // 绝对值
                absDiff = Math.abs(diff);

                if (absDiff > mTouchSlop) {
                    // 移动的距离大于一般手指触摸移动的距离，说明手指在移动
                    if (mListView.getFirstVisiblePosition() == 0) {
                        View view = mListView.getChildAt(0);
                        Rect rect = new Rect();
                        view.getLocalVisibleRect(rect);
                        int top = rect.top;
                        // 手指开始移动，并且第一个view距离屏幕top=0说明还未开始移动，
                        // 则应该开始移动，拦截，由父layout处理
                        if (top == 0) {
                            mLastMotionY = y;
                            mIsBeingDragged = true;
                        }
                    }
                }

                break;
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 手按下 并且触碰任意边缘 不处理touch事件，交由子view处理
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }

        // 如果在移动则不处理touch事件，交由子view处理
        if (isMove) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // 手按下 由父layout处理touch事件
                mLastMotionY = mInitialMotionY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mIsBeingDragged) {
                    if (l != null && !isRersh) {
                        l.startRersh();
                        isRersh = true;
                    }
                    mLastMotionY = event.getY();
                    float moveY = mLastMotionY - mInitialMotionY;
                    pullEvent(moveY);
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (mIsBeingDragged) {
                    mIsBeingDragged = false;
                    startMoveAnim(getScrollY(), Math.abs(getScrollY()), duration);
                    if(l!= null && isRersh && (event.getY() - mInitialMotionY) > 0){
                        l.endRersh(event.getY() - mInitialMotionY);
                        isRersh = false;
                    }
                    return true;
                }
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    // 开始移动
    private void pullEvent(float moveY) {
        if (l != null) {
            l.Rersh(moveY);
        }
        if (moveY > 0) {
            int value = (int) Math.abs(moveY);
            scrollTo(0, -(int) (value * resistance));
        }
    }

    // 松开手，listview回到原位置
    public void startMoveAnim(int startY, int dy, int duration) {
        isMove = true;
        mScroller.startScroll(0, startY, 0, dy, duration);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
            isMove = true;
        } else {
            isMove = false;
        }
        super.computeScroll();
    }

    public interface ScrollRershListener {
        void Rersh(float value);

        void startRersh();

        void endRersh(float value);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setOnScrollRershListener(ScrollRershListener l) {
        this.l = l;
    }
}
