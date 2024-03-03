package com.feicien.viewpager.demo.adapter;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.feicien.viewpager.demo.utils.LogUtils;


public abstract class OnRecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private static final String TAG = "OnRecyclerItemClickListener";
    private final GestureDetectorCompat mGestureDetector;
    private final RecyclerView mRecyclerView;

    // 定义点击和长按的抽象方法
    public abstract void onItemClick(RecyclerView.ViewHolder viewHolder);

    public abstract void onItemLongClick(RecyclerView.ViewHolder viewHolder);

    public OnRecyclerItemClickListener(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        this.mGestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(@NonNull MotionEvent e) {
                View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null) {
                    RecyclerView.ViewHolder viewHolder = mRecyclerView.getChildViewHolder(childView);
                    if (viewHolder != null) {
                        onItemLongClick(viewHolder);
                    } else {
                        LogUtils.d(TAG, "Long press but no ViewHolder found");
                    }
                } else {
                    LogUtils.d(TAG, "Long press on a non-existent child");
                }
            }

            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null) {
                    RecyclerView.ViewHolder viewHolder = mRecyclerView.getChildViewHolder(childView);
                    if (viewHolder != null) {
                        onItemClick(viewHolder);
                        return true;
                    } else {
                        LogUtils.d(TAG, "Tap up but no ViewHolder found");
                        return false;
                    }
                } else {
                    LogUtils.d(TAG, "Tap up on a non-existent child");
                    return false;
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        // 总是返回false，不拦截事件，让RecyclerView正常处理滚动等操作
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // 不需要实现，但必须重写
    }
}
