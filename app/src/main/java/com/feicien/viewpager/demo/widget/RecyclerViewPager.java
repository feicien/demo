package com.feicien.viewpager.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.feicien.viewpager.demo.adapter.GridPagerAdapter;
import com.feicien.viewpager.demo.utils.LogUtils;


public class RecyclerViewPager extends ViewPager {
    private static final String TAG = "RecyclerViewPager";
    private GestureDetectorCompat mGestureDetector;
    private float mInitialX;
    private float mInitialY;

    public RecyclerViewPager(Context context) {
        this(context, null);
    }

    public RecyclerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGestureDetector(context);
    }

    private void initGestureDetector(Context context) {
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(@NonNull MotionEvent e) {
                // 总是处理按下事件
                return true;
            }

            @Override
            public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                // Fling 事件可根据需要实现
                return true;
            }

            @Override
            public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
                if (e1 == null) {
                    return false;
                }
                // 判断是否超过滑动角度阈值
                boolean exceedsThreshold = exceedsAngleThreshold(e1, e2);
                if (Math.abs(distanceX) > Math.abs(distanceY) || exceedsThreshold) {
                    // 水平滚动或超过角度阈值时处理
                    return true;
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
    }

    /**
     * 计算两个触摸事件之间的水平和垂直差值（deltaX和deltaY），然后利用这两个差值计算出两点之间的直线距离。
     * 接着，使用Math.asin函数计算出由deltaX和直线距离构成的直角三角形的锐角大小（以弧度为单位），
     * 最后将这个角度转换为度数，并判断它是否大于30度
     */
    public boolean exceedsAngleThreshold(@NonNull MotionEvent e1, @NonNull MotionEvent e2) {
        // 计算两点之间的差值
        float deltaX = e2.getX() - e1.getX();
        float deltaY = e2.getY() - e1.getY();

        // 计算距离来避免除以零
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (distance == 0) {
            // 避免除以零的情况
            return false;
        }

        // 计算角度（使用反正切函数计算两点形成的直角三角形的角度）
        double angleRadians = Math.asin(Math.abs(deltaY) / distance);

        // 将弧度转换为度
        double angleDegrees = angleRadians * (180.0 / Math.PI);

        // 判断角度是否大于30度
        return angleDegrees > 30;
    }

    // 释放资源的方法
    public void release() {
        LogUtils.i(TAG, "Releasing resources");
        PagerAdapter adapter = getAdapter();
        if (adapter instanceof GridPagerAdapter) {
            ((GridPagerAdapter) adapter).release();
        }
        removeAllViews();
    }

    // 恢复状态的方法
    public void restore() {
        LogUtils.i(TAG, "Restoring state");
        PagerAdapter adapter = getAdapter();
        if (adapter instanceof GridPagerAdapter) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v == this || (!(v instanceof ViewPager) && !(v instanceof RecyclerView))) {
            return super.canScroll(v, checkV, dx, x, y);
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev == null) {
            return false;
        }
        if (mGestureDetector != null) {
            mGestureDetector.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        restore();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (getAdapter() instanceof GridPagerAdapter) {
            ((GridPagerAdapter) getAdapter()).release();
            LogUtils.i(TAG, "Detached from window");
        }
        release();
        mGestureDetector = null;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev == null) {
            return false;
        }
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mInitialX = ev.getX();
            mInitialY = ev.getY();
            return super.onInterceptTouchEvent(ev);
        } else if (action == MotionEvent.ACTION_MOVE) {
            float deltaX = ev.getX() - mInitialX;
            float deltaY = ev.getY() - mInitialY;
            // 根据滑动距离判断是否拦截事件
            if (shouldInterceptTouchEvent(deltaX, deltaY)) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    // 根据滑动的X和Y距离判断是否应该拦截触摸事件
    private boolean shouldInterceptTouchEvent(float deltaX, float deltaY) {
        double angle = Math.atan2(Math.abs(deltaY), Math.abs(deltaX)) * (180 / Math.PI);
        return angle < 30 || Math.abs(deltaX) > Math.abs(deltaY);
    }
}

