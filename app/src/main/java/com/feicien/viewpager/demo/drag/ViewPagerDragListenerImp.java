package com.feicien.viewpager.demo.drag;

import android.os.Handler;
import android.os.Looper;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.feicien.viewpager.demo.adapter.GridPagerAdapter;
import com.feicien.viewpager.demo.utils.LogUtils;

import java.lang.ref.WeakReference;

public class ViewPagerDragListenerImp implements View.OnDragListener {
    private static final String TAG = "ViewPagerDragListener";
    private static final int EDGE_LEFT = 0;
    private static final int EDGE_RIGHT = 1;
    //累计距离
    private int mAccumulatedDistance;
    private final int[] mTouchCoordinates;
    private final Handler mHandler;
    private boolean isDragStart;
    private RecyclerDragListenerImp mDragListenerDispatcher;
    private boolean isScrollScheduled;
    private int mTouchSize;
    private final MyRunnable mRunnable;
    private RecyclerView mRecyclerView;
    private final WeakReference<ViewPager> mViewRef;

    // Runnable implementation for handling the automatic scrolling when dragging near the edges
    public class MyRunnable implements Runnable {
        private int mEdge;
        private final ViewPager mViewPager;

        MyRunnable(ViewPager viewPager) {
            this.mViewPager = viewPager;
        }

        void setEdge(int edge) {
            this.mEdge = edge;
        }

        @Override
        public void run() {
            if (mViewPager == null) {
                LogUtils.d(TAG, "scrollRunnable cannot execute, viewPager is null");
                return;
            }
            int currentItem = mViewPager.getCurrentItem();
            mViewPager.setCurrentItem(mEdge == EDGE_LEFT ? currentItem - 1 : currentItem + 1);
            isScrollScheduled = false;
            mAccumulatedDistance = 0;
            if (isDragStart) {
                triggerPageScroll(mViewPager, mTouchCoordinates[0], mTouchSize);
            }
        }
    }

    public ViewPagerDragListenerImp(ViewPager viewPager) {
        this.mViewRef = new WeakReference<>(viewPager);
        this.mAccumulatedDistance = 0;
        this.mTouchCoordinates = new int[2];
        this.mHandler = new Handler(Looper.getMainLooper()); // Use main looper to ensure runs on UI thread
        this.mRunnable = new MyRunnable(viewPager);
    }

    // Method to determine if auto-scroll should be triggered based on the drag position
    public void triggerPageScroll(ViewPager viewPager, float xCoordinate, float touchSize) {
        int delayMillis = this.mAccumulatedDistance < ViewConfiguration.get(viewPager.getContext()).getScaledWindowTouchSlop() ? 1000 : 600;
        float edgeThreshold = touchSize / 4.0f;
        if (xCoordinate - edgeThreshold < 0) {
            if (!this.isScrollScheduled) {
                this.isScrollScheduled = true;
                this.mRunnable.setEdge(EDGE_LEFT);
                this.mHandler.postDelayed(this.mRunnable, delayMillis);
            }
        } else if (xCoordinate + edgeThreshold > viewPager.getWidth()) {
            if (!this.isScrollScheduled) {
                this.isScrollScheduled = true;
                this.mRunnable.setEdge(EDGE_RIGHT);
                this.mHandler.postDelayed(this.mRunnable, delayMillis);
            }
        } else {
            this.mHandler.removeCallbacks(this.mRunnable);
            this.isScrollScheduled = false;
        }
    }


    // Retrieve the appropriate drag listener for the current page
    private RecyclerDragListenerImp getDragDispatcherByPageIndex(int pageIndex, ViewPager viewPager) {
        if (viewPager != null) {
            RecyclerDragListenerImp listener = DragManager.getInstance().getDragListener(pageIndex);
            if (viewPager.getCurrentItem() == pageIndex && listener != null) {
                return listener;
            }
        }
        return null;
    }

    // Retrieve the RecyclerView for the current page in the ViewPager
    private RecyclerView getRecyclerViewByPageIndex(int pageIndex, ViewPager viewPager) {
        if (viewPager.getAdapter() != null && viewPager.getAdapter() instanceof GridPagerAdapter) {
            return ((GridPagerAdapter) viewPager.getAdapter()).getPage(pageIndex);
        }
        return null;
    }

    // Helper method to handle the transition of drag between different pages
    private boolean transitionDrag(DragInfo dragInfo, int pageIndex, View currentView, View nextView) {
        if (mDragListenerDispatcher != null) {
            mDragListenerDispatcher.onDragExit(dragInfo);
            if (currentView instanceof RecyclerView) {
                RecyclerView.ItemAnimator itemAnimator = ((RecyclerView) currentView).getItemAnimator();
                if (itemAnimator == null) {
                    LogUtils.d(TAG, "ItemAnimator is null");
                    return true;
                }
                itemAnimator.endAnimations();
            }
            DragInfo newDragInfo = new DragInfo();
            if (nextView instanceof RecyclerView) {
                this.mRecyclerView = (RecyclerView) nextView;
            }
            if (mRecyclerView == null) {
                LogUtils.d(TAG, "Next RecyclerView is null");
                return true;
            }
            if (mRecyclerView.getAdapter() != null && mRecyclerView.getAdapter().getItemCount() != 1) {
                int newItemIndex = pageIndex >= dragInfo.getPageIndex() ? 0 : mRecyclerView.getAdapter().getItemCount() - 1;
                newDragInfo.setItemId(mRecyclerView.getAdapter().getItemId(newItemIndex));
                RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(newItemIndex);
                if (viewHolder != null) {
                    newDragInfo.setView(viewHolder.itemView);
                }
                newDragInfo.setPageIndex(pageIndex);
                mDragListenerDispatcher.onPageTransfer(dragInfo, newDragInfo);
                dragInfo.setPageIndex(newDragInfo.getPageIndex());
                dragInfo.setView(newDragInfo.getView());
            } else {
                LogUtils.i(TAG, "Insufficient items for drag and drop");
                return true;
            }
        }
        return false;
    }

    // Handle the end of a drag event
    public void onDragEnd(DragInfo dragInfo, ViewPager viewPager) {
        this.mAccumulatedDistance = 0;
        if (this.isDragStart) {
            this.isDragStart = false;
            if (mDragListenerDispatcher != null) {
                mDragListenerDispatcher.onDragExit(dragInfo);
            }
            this.mHandler.removeCallbacks(this.mRunnable);
            this.isScrollScheduled = false;
        }
        RecyclerDragListenerImp dispatcher = getDragDispatcherByPageIndex(dragInfo.getPageIndex(), viewPager);
        if (dispatcher != null) {
            dispatcher.onDragEnd(dragInfo, getRecyclerViewByPageIndex(dragInfo.getPageIndex(), viewPager));
        }
    }

    // Handle the drag-over event to possibly trigger a page change
    public void onDragOver(DragInfo dragInfo, ViewPager viewPager) {
        int currentItem = viewPager.getCurrentItem();
        RecyclerDragListenerImp dispatcher = getDragDispatcherByPageIndex(currentItem, viewPager);
        RecyclerView currentRecyclerView = getRecyclerViewByPageIndex(dragInfo.getPageIndex(), viewPager);
        if (currentRecyclerView == null) {
            LogUtils.d(TAG, "Current RecyclerView is null during drag");
            return;
        }
        if (dispatcher != null) {
            if (this.mDragListenerDispatcher != dispatcher) {
                RecyclerView nextRecyclerView = getRecyclerViewByPageIndex(currentItem, viewPager);
                if (nextRecyclerView == null) {
                    LogUtils.d(TAG, "Next RecyclerView is null during drag");
                    return;
                }
                if (!transitionDrag(dragInfo, currentItem, currentRecyclerView, nextRecyclerView)) {
                    dispatcher.onDragEnter(dragInfo);
                    currentRecyclerView = nextRecyclerView;
                }
            }
            dispatcher.onDragOver(dragInfo, currentRecyclerView);
        } else if (mDragListenerDispatcher != null) {
            mDragListenerDispatcher.onDragExit(dragInfo);
        }
        this.mDragListenerDispatcher = dispatcher;

        this.mTouchSize = dragInfo.getOutShadowSize().x;
        float xCoordinate = (dragInfo.getX() - dragInfo.getOutShadowTouchPoint().x) + (this.mTouchSize / 2.0f);
        float yCoordinate = (dragInfo.getY() - dragInfo.getOutShadowTouchPoint().y) + (dragInfo.getOutShadowSize().y / 2.0f);
        this.mAccumulatedDistance += (int) Math.hypot(mTouchCoordinates[0] - xCoordinate, mTouchCoordinates[1] - yCoordinate);
        mTouchCoordinates[0] = (int) xCoordinate;
        mTouchCoordinates[1] = (int) yCoordinate;
        triggerPageScroll(viewPager, xCoordinate, this.mTouchSize);
    }

    // Handle the start of a drag event
    public void onDragStart(DragInfo dragInfo, ViewPager viewPager) {
        if (dragInfo == null || viewPager == null) {
            LogUtils.d(TAG, "DragInfo or ViewPager is null at the start of drag");
            return;
        }
        this.isDragStart = true;
        this.mDragListenerDispatcher = null;
        this.mTouchSize = dragInfo.getOutShadowSize().x;
        float xCoordinate = (dragInfo.getX() - dragInfo.getOutShadowTouchPoint().x) + (this.mTouchSize / 2.0f);

        if (xCoordinate - (this.mTouchSize / 4.0f) >= 0 && (this.mTouchSize / 4.0f) + xCoordinate <= viewPager.getWidth()) {
            this.isScrollScheduled = false;
        } else {
            this.isScrollScheduled = true;
            this.mHandler.postDelayed(this.mRunnable, 600L);
        }
        RecyclerDragListenerImp dispatcher = getDragDispatcherByPageIndex(dragInfo.getPageIndex(), viewPager);
        if (dispatcher != null) {
            dispatcher.onDragStart(dragInfo, getRecyclerViewByPageIndex(dragInfo.getPageIndex(), viewPager));
        }
    }

    // Main onDrag method that delegates to specific handlers based on the event type
    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        if (view == null || dragEvent == null) {
            LogUtils.d(TAG, "View or DragEvent is null in onDrag");
            return false;
        }
        ViewPager viewPager = mViewRef.get();
        if (view == viewPager && (dragEvent.getLocalState() instanceof DragInfo)) {
            DragInfo dragInfo = (DragInfo) dragEvent.getLocalState();
            dragInfo.setX(dragEvent.getX());
            dragInfo.setY(dragEvent.getY());
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    onDragStart(dragInfo, viewPager);
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    onDragOver(dragInfo, viewPager);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    onDragEnd(dragInfo, viewPager);
                    break;
                default:
                    // Handle other actions if necessary
                    break;
            }
            return true;
        }
        LogUtils.d(TAG, "Incompatible view or local state in onDrag");
        return false;
    }
}
