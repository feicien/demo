package com.feicien.viewpager.demo.drag;

import android.os.Handler;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.feicien.viewpager.demo.adapter.GridPagerAdapter;
import com.feicien.viewpager.demo.utils.LogUtils;

import java.lang.ref.WeakReference;

public class ViewPagerDragListenerImp  implements View.OnDragListener {
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
    protected final WeakReference<ViewPager> mViewRef;

    
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
                LogUtils.d(TAG, "scrollRunnable can not execute, viewpager is null");
                return;
            }
            int currentItem = mViewPager.getCurrentItem();
            if (this.mEdge == EDGE_LEFT) {
                this.mViewPager.setCurrentItem(currentItem - 1);
            } else {
                this.mViewPager.setCurrentItem(currentItem + 1);
            }
            isScrollScheduled = false;
            mAccumulatedDistance = 0;
            if (isDragStart) {
                triggerPageScroll(this.mViewPager, mTouchCoordinates[0], mTouchSize);
            }
        }
    }

    public ViewPagerDragListenerImp(ViewPager viewPager) {
        this.mViewRef = new WeakReference<>(viewPager);
        this.mAccumulatedDistance = 0;
        this.mTouchCoordinates = new int[2];
        this.mHandler = new Handler();
        this.mRunnable = new MyRunnable(viewPager);
    }

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



    private RecyclerDragListenerImp getDragDispatcherByPageIndex(int pageIndex, ViewPager viewPager) {
        if (viewPager != null) {
            RecyclerDragListenerImp m22979c = DragManager.getInstance().getDragListener(pageIndex);
            if (viewPager.getCurrentItem() == pageIndex && m22979c != null) {
                return m22979c;
            }
            return null;
        }
        return null;
    }

    private RecyclerView getRecyclerViewByPageIndex(int pageIndex, ViewPager viewPager) {
        if (viewPager.getAdapter() != null && (viewPager.getAdapter() instanceof GridPagerAdapter)) {
            return ((GridPagerAdapter) viewPager.getAdapter()).getPage(pageIndex);
        }
        return null;
    }

    private boolean m3475t(DragInfo dragInfo, int pageIndex, View view, View view2) {
        RecyclerDragListenerImp dragListenerDispatcher = this.mDragListenerDispatcher;
        if (dragListenerDispatcher != null) {
            dragListenerDispatcher.onDragExit(dragInfo);
            if (view instanceof RecyclerView) {
                RecyclerView.ItemAnimator itemAnimator = ((RecyclerView) view).getItemAnimator();
                if (itemAnimator == null) {
                    LogUtils.d(TAG, "has error on move, itemAnimator is null");
                    return true;
                }
                itemAnimator.endAnimations();
            }
            DragInfo dragInfo2 = new DragInfo();
            if (view2 instanceof RecyclerView) {
                this.mRecyclerView = (RecyclerView) view2;
            }
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView == null) {
                LogUtils.d(TAG, "has error on move, nextView == null");
                return true;
            }
            if (recyclerView.getAdapter() != null) {
                if (this.mRecyclerView.getAdapter().getItemCount() == 1) {
                    LogUtils.i(TAG, "Only one data, drag and drop is not possible");
                    return true;
                }
                int itemCount = pageIndex >= dragInfo.getPageIndex() ? 0 : this.mRecyclerView.getAdapter().getItemCount() - 1;
                dragInfo2.setItemId(this.mRecyclerView.getAdapter().getItemId(itemCount));
                RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.mRecyclerView.findViewHolderForAdapterPosition(itemCount);
                if (findViewHolderForAdapterPosition != null) {
                    dragInfo2.setView(findViewHolderForAdapterPosition.itemView);
                }
            }
            dragInfo2.setPageIndex(pageIndex);
            this.mDragListenerDispatcher.onPageTransfer(dragInfo, dragInfo2);
            dragInfo.setPageIndex(dragInfo2.getPageIndex());
            dragInfo.setView(dragInfo2.getView());
        }
        return false;
    }




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




    public void onDragOver(DragInfo dragInfo, ViewPager viewPager) {
        int currentItem = viewPager.getCurrentItem();
        RecyclerDragListenerImp dispatcher = getDragDispatcherByPageIndex(currentItem, viewPager);
        RecyclerView recyclerView = getRecyclerViewByPageIndex(dragInfo.getPageIndex(), viewPager);
        if (recyclerView == null) {
            LogUtils.d(TAG, "has error on move, pageView == null, dragging pageIndex = " + dragInfo.getPageIndex() + ", pageAdapter = " + viewPager.getAdapter());
            return;
        }
        if (dispatcher != null) {
            if (this.mDragListenerDispatcher != dispatcher) {
                RecyclerView recyclerView1 = getRecyclerViewByPageIndex(currentItem, viewPager);
                if (recyclerView1 == null) {
                    LogUtils.d(TAG, "has error on move, nextView == null, current pageIndex = " + currentItem + ", pageAdapter = " + viewPager.getAdapter());
                    return;
                } else if (m3475t(dragInfo, currentItem, recyclerView, recyclerView1)) {
                    LogUtils.i(TAG, "replaceData ");
                    return;
                } else {
                    dispatcher.onDragEnter(dragInfo);
                    recyclerView = recyclerView1;
                }
            }
            dispatcher.onDragOver(dragInfo, recyclerView);
        } else {
            RecyclerDragListenerImp dragListenerDispatcher = this.mDragListenerDispatcher;
            if (dragListenerDispatcher != null) {
                dragListenerDispatcher.onDragExit(dragInfo);
            }
        }
        this.mDragListenerDispatcher = dispatcher;

        this.mTouchSize = dragInfo.getOutShadowSize().x;
        float dragX = dragInfo.getX();
        float xCoordinate = (dragX - dragInfo.getOutShadowTouchPoint().x) + (dragInfo.getOutShadowSize().x / 2.0f);
        float dragY = (dragInfo.getY() - dragInfo.getOutShadowTouchPoint().y) + (dragInfo.getOutShadowSize().y / 2.0f);
        this.mAccumulatedDistance = (int) (Math.hypot(mTouchCoordinates[0] - xCoordinate, mTouchCoordinates[1] - dragY) + mAccumulatedDistance);
        mTouchCoordinates[0] = (int) xCoordinate;
        mTouchCoordinates[1] = (int) dragY;
        triggerPageScroll(viewPager, xCoordinate, this.mTouchSize);
    }


    public void onDragStart(DragInfo dragInfo, ViewPager viewPager) {
        if (dragInfo == null) {
            LogUtils.d(TAG, "onDragStart dragInfo is null.");
            return;
        }
        if (viewPager == null) {
            LogUtils.d(TAG, "onDragStart viewPager is null.");
            return;
        }
        this.isDragStart = true;
        this.mDragListenerDispatcher = null;
        this.mTouchSize = dragInfo.getOutShadowSize().x;
        float m3276a = (dragInfo.getX() - dragInfo.getOutShadowTouchPoint().x) + (dragInfo.getOutShadowSize().x / 2.0f);

        float edgeThreshold = mTouchSize / 4.0f;
        if (m3276a - edgeThreshold >= 0 && edgeThreshold + m3276a <= viewPager.getWidth()) {
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



    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        if (view != null && dragEvent != null) {
            ViewPager viewPager = mViewRef.get();
            if (view == viewPager && (dragEvent.getLocalState() instanceof DragInfo)) {
                DragInfo dragInfo = (DragInfo) dragEvent.getLocalState();
                dragInfo.setX(dragEvent.getX());
                dragInfo.setY(dragEvent.getY());
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        onDragStart(dragInfo, viewPager);
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        onDragOver(dragInfo, viewPager);
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        onDragEnd(dragInfo, viewPager);
                        return true;
                    default:
                        return true;
                }
            }
            LogUtils.d(TAG, "onDrag: view and getLocalState is null");
            return false;
        }
        LogUtils.d(TAG, "onDrag: view and event is null");
        return false;
    }
}
