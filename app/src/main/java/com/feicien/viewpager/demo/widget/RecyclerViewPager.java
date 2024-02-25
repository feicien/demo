package com.feicien.viewpager.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.feicien.viewpager.demo.adapter.BasePagerAdapter;
import com.feicien.viewpager.demo.utils.LogUtils;


public class RecyclerViewPager extends ViewPager {
    private static final String TAG = "RecyclerViewPager";
    private GestureDetectorCompat mGestureDetector;
    private float mX;
    private float mY;



    public RecyclerViewPager(Context context) {
        this(context, null);
    }

    public RecyclerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() { 
            @Override 
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }

            @Override 
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                return true;
            }

            @Override 
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (motionEvent == null || motionEvent2 == null) {
                    return false;
                }
                float x = motionEvent.getX() - motionEvent2.getX();
                float y = motionEvent.getY() - motionEvent2.getY();
                boolean m22964B = RecyclerViewPager.this.exceedsAngleThreshold(motionEvent, motionEvent2);
                if (Math.abs(x) > Math.abs(y) || m22964B) {
                    return true;
                }
                return super.onScroll(motionEvent, motionEvent2, f, f2);
            }
        });
    }

    public boolean exceedsAngleThreshold(MotionEvent motionEvent, MotionEvent motionEvent2) {
        return Math.round((float) Math.asin((Math.abs(((double) motionEvent2.getX()) - (((double) motionEvent.getX()) / Math.sqrt((double) ((motionEvent.getX() * motionEvent2.getX()) + (motionEvent.getY() * motionEvent2.getY()))))) / 3.141592653589793d) * 180.0d)) > 30;
    }

    public void release() {
        LogUtils.i(TAG, "release");
        PagerAdapter adapter = getAdapter();
        if (adapter instanceof BasePagerAdapter) {
            ((BasePagerAdapter) adapter).release();
        }
        removeAllViews();
    }

    public void restore() {
        LogUtils.i(TAG, "restore");
        PagerAdapter adapter = getAdapter();
        if (adapter instanceof BasePagerAdapter) {
            ((BasePagerAdapter) adapter).setContext(getContext());
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
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        GestureDetectorCompat gestureDetectorCompat = this.mGestureDetector;
        if (gestureDetectorCompat != null) {
            gestureDetectorCompat.onTouchEvent(event);
        }
        return super.dispatchTouchEvent(event);
    }

    @Override 
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        restore();
    }

    @Override 
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (getAdapter() instanceof BasePagerAdapter) {
            ((BasePagerAdapter) getAdapter()).release();
            LogUtils.i(TAG, "onDetachedFromWindow");
        }
        release();
        this.mGestureDetector = null;
    }

    @Override 
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent == null) {
            return false;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mX = motionEvent.getX();
            this.mY = motionEvent.getY();
            return super.onInterceptTouchEvent(motionEvent);
        }
        if (action != 1) {
            if (action != 2) {
                return super.onInterceptTouchEvent(motionEvent);
            }
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            float abs = Math.abs(x - this.mX);
            float abs2 = Math.abs(y - this.mY);
            double sqrt = Math.sqrt((abs * abs) + (abs2 * abs2));
            if (Math.round((float) ((Math.asin(abs2 / sqrt) / 3.141592653589793d) * 180.0d)) < 30 && Math.round((float) ((Math.asin(abs / sqrt) / 3.141592653589793d) * 180.0d)) < 30 && Math.abs(this.mX - motionEvent.getX()) <= Math.abs(this.mY - motionEvent.getY())) {
                return false;
            }
            return true;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }
}
