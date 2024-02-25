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

    public abstract void onItemClick(RecyclerView.ViewHolder viewHolder);

    public abstract void onItemLongClick(RecyclerView.ViewHolder viewHolder);

    public OnRecyclerItemClickListener(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        this.mGestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new GestureDetector.SimpleOnGestureListener() { 
            @Override 
            public void onLongPress(@NonNull MotionEvent motionEvent) {
                View findChildViewUnder = OnRecyclerItemClickListener.this.mRecyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (findChildViewUnder != null) {
                    RecyclerView.ViewHolder childViewHolder = OnRecyclerItemClickListener.this.mRecyclerView.getChildViewHolder(findChildViewUnder);
                    if (childViewHolder == null) {
                        LogUtils.d(TAG, "ItemTouchHelperGestureListener onLongPress vh is null");
                        return;
                    } else {
                        OnRecyclerItemClickListener.this.onItemLongClick(childViewHolder);
                        return;
                    }
                }
                LogUtils.d(TAG, "ItemTouchHelperGestureListener onLongPress child is null");
            }

            @Override 
            public boolean onSingleTapUp(@NonNull MotionEvent motionEvent) {
                View findChildViewUnder = OnRecyclerItemClickListener.this.mRecyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (findChildViewUnder != null) {
                    RecyclerView.ViewHolder childViewHolder = OnRecyclerItemClickListener.this.mRecyclerView.getChildViewHolder(findChildViewUnder);
                    if (childViewHolder == null) {
                        LogUtils.d(TAG, "ItemTouchHelperGestureListener onSingleTapUp vh is null");
                        return false;
                    }
                    OnRecyclerItemClickListener.this.onItemClick(childViewHolder);
                    return true;
                }
                LogUtils.d(TAG, "ItemTouchHelperGestureListener onSingleTapUp child is null");
                return false;
            }
        });
    }

    @Override 
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
        this.mGestureDetector.onTouchEvent(motionEvent);
        return false;
    }

    @Override 
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
        this.mGestureDetector.onTouchEvent(motionEvent);
    }

    @Override 
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
