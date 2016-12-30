package com.example.xiongcen.myapplication.util;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by xiongcen on 16/12/30.
 */

public class ViewUtils {

    /**
     * 找到view在屏幕上的bottom
     *
     * @param view
     * @return
     */
    public static int findViewBottomOnScreen(View view) {
        int[] locOnSrc = new int[2];
        view.getLocationOnScreen(locOnSrc);
        int viewBtmLoc = locOnSrc[1] + view.getHeight();
        return viewBtmLoc;
    }

    /**
     * 找到view在屏幕上的Top
     *
     * @param view
     * @return
     */
    public static int findViewTopOnScreen(View view) {
        int[] locOnSrc = new int[2];
        view.getLocationOnScreen(locOnSrc);
        return locOnSrc[1];
    }

    /**
     * touch事件是否落在view中
     *
     * @param event
     * @param view
     * @return
     */
    public static boolean isTouchEventInView(MotionEvent event, View view) {
        Rect viewRect = new Rect();
        int[] locOnSrc = new int[2];
        view.getLocationOnScreen(locOnSrc);
        viewRect.set(locOnSrc[0], locOnSrc[1], locOnSrc[0] + view.getWidth(), locOnSrc[1] + view.getHeight());
        viewRect.contains((int) event.getX(), (int) event.getY());
        return false;
    }
}
