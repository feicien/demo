package com.feicien.viewpager.demo.drag;

import android.os.Handler;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.feicien.viewpager.demo.adapter.GridPagerAdapter;
import com.feicien.viewpager.demo.utils.LogUtils;


public class ViewPagerDragListenerImp extends DragListenerDispatcher<ViewPager> {
    private static final String TAG = "ViewPagerDragListener";
    private static final int EDGE_LEFT = 0;
    private static final int EDGE_RIGHT = 1;
    private int f34907a;
    private final int[] f34908b;
    private final Handler mHandler;
    private int f34910d;
    private int f34911e;
    private boolean isDragStart;
    private DragListenerDispatcher mDragListenerDispatcher;
    private int f34914h;
    private int f34915i;
    private final MyRunnable mRunnable;
    private RecyclerView mRecyclerView;

    
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
            f34914h = 0;
            f34907a = 0;
            if (isDragStart) {
                m3488g(this.mViewPager, f34908b[0], f34915i);
            }
        }
    }

    public ViewPagerDragListenerImp(ViewPager viewPager) {
        super(viewPager);
        this.f34907a = 0;
        this.f34908b = new int[2];
        this.f34910d = 0;
        this.f34911e = 0;
        this.f34914h = 0;
        this.f34915i = 0;
        this.mHandler = new Handler();
        this.mRunnable = new MyRunnable(viewPager);
    }

    public void m3488g(ViewPager viewPager, float f, float f2) {
        int delayMillis = this.f34907a < ViewConfiguration.get(viewPager.getContext()).getScaledWindowTouchSlop() ? 1000 : 600;
        float f3 = f2 / 4.0f;
        if (f - f3 < this.f34910d) {
            if (this.f34914h == 0) {
                this.f34914h = 1;
                this.mRunnable.setEdge(EDGE_LEFT);
                this.mHandler.postDelayed(this.mRunnable, delayMillis);
                return;
            }
            return;
        }
        if (f + f3 > viewPager.getWidth() - this.f34911e) {
            if (this.f34914h == 0) {
                this.f34914h = 1;
                this.mRunnable.setEdge(EDGE_RIGHT);
                this.mHandler.postDelayed(this.mRunnable, delayMillis);
                return;
            }
            return;
        }
        this.mHandler.removeCallbacks(this.mRunnable);
        if (this.f34914h == 1) {
            this.f34914h = 0;
            this.mRunnable.setEdge(EDGE_RIGHT);
        }
    }



    private DragListenerDispatcher getDragDispatcherByPageIndex(int pageIndex, ViewPager viewPager) {
        if (viewPager != null) {
            DragListenerDispatcher m22979c = DragManager.getInstance().getDragListener(pageIndex);
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
        DragListenerDispatcher dragListenerDispatcher = this.mDragListenerDispatcher;
        if (dragListenerDispatcher != null) {
            dragListenerDispatcher.onDragExit(dragInfo, view);
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

    @Override 
    public void clearMove() {
    }

    @Override 
    public boolean acceptDrop(DragInfo dragInfo, ViewPager viewPager) {
        DragListenerDispatcher dispatcher = getDragDispatcherByPageIndex(dragInfo.getPageIndex(), viewPager);
        if (dispatcher == null) {
            return false;
        }
        return dispatcher.acceptDrop(dragInfo, getRecyclerViewByPageIndex(dragInfo.getPageIndex(), viewPager));
    }

    @Override 
    public long getDraggingId() {
        return 0L;
    }


    @Override 
    public void onDragEnd(DragInfo dragInfo, ViewPager viewPager) {
        this.f34907a = 0;
        if (this.isDragStart) {
            this.isDragStart = false;
            DragListenerDispatcher dragListenerDispatcher = this.mDragListenerDispatcher;
            if (dragListenerDispatcher != null) {
                dragListenerDispatcher.onDragExit(dragInfo, getRecyclerViewByPageIndex(dragInfo.getPageIndex(), viewPager));
            }
            this.mHandler.removeCallbacks(this.mRunnable);
            if (this.f34914h == 1) {
                this.f34914h = 0;
                this.mRunnable.setEdge(EDGE_RIGHT);
            }
        }
        DragListenerDispatcher dispatcher = getDragDispatcherByPageIndex(dragInfo.getPageIndex(), viewPager);
        if (dispatcher == null) {
            return;
        }
        dispatcher.onDragEnd(dragInfo, getRecyclerViewByPageIndex(dragInfo.getPageIndex(), viewPager));
    }

    @Override 
    public void onDragEnter(DragInfo dragInfo, ViewPager viewPager) {
    }

    @Override 
    public void onDragExit(DragInfo dragInfo, ViewPager viewPager) {
    }

    @Override 
    public void onPageTransfer(DragInfo dragInfo, DragInfo dragInfo2) {
    }

    @Override 
    public void onDragOver(DragInfo dragInfo, ViewPager viewPager) {
        int currentItem = viewPager.getCurrentItem();
        DragListenerDispatcher dispatcher = getDragDispatcherByPageIndex(currentItem, viewPager);
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
                    dispatcher.onDragEnter(dragInfo, recyclerView1);
                    recyclerView = recyclerView1;
                }
            }
            dispatcher.onDragOver(dragInfo, recyclerView);
        } else {
            DragListenerDispatcher dragListenerDispatcher = this.mDragListenerDispatcher;
            if (dragListenerDispatcher != null) {
                dragListenerDispatcher.onDragExit(dragInfo, recyclerView);
            }
        }
        this.mDragListenerDispatcher = dispatcher;

        this.f34915i = dragInfo.getOutShadowSize().x;
        float m3276a = dragInfo.getX();
        float f = (m3276a - dragInfo.getOutShadowTouchPoint().x) + (dragInfo.getOutShadowSize().x / 2.0f);
        float m3275b = (dragInfo.getY() - dragInfo.getOutShadowTouchPoint().y) + (dragInfo.getOutShadowSize().y / 2.0f);
        double d = this.f34907a;
        int[] iArr = this.f34908b;
        this.f34907a = (int) (Math.hypot(iArr[0] - f, iArr[1] - m3275b) + d);
        int[] iArr2 = this.f34908b;
        iArr2[0] = (int) f;
        iArr2[1] = (int) m3275b;
        m3488g(viewPager, f, this.f34915i);
    }

    @Override 
    public boolean onDragPrepare(DragInfo dragInfo, ViewPager viewPager) {
        return true;
    }

    @Override 
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
        this.f34915i = dragInfo.getOutShadowSize().x;
        float m3276a = (dragInfo.getX() - dragInfo.getOutShadowTouchPoint().x) + (dragInfo.getOutShadowSize().x / 2.0f);
        int i = this.f34915i;
        if (m3276a - (i / 4.0f) >= this.f34910d && (i / 4.0f) + m3276a <= viewPager.getWidth() - this.f34911e) {
            this.f34914h = 0;
        } else {
            this.f34914h = 1;
            this.mHandler.postDelayed(this.mRunnable, 600L);
        }
        DragListenerDispatcher dispatcher = getDragDispatcherByPageIndex(dragInfo.getPageIndex(), viewPager);
        if (dispatcher == null) {
            return;
        }
        dispatcher.onDragStart(dragInfo, getRecyclerViewByPageIndex(dragInfo.getPageIndex(), viewPager));
    }

    @Override 
    public void onDrop(DragInfo dragInfo, ViewPager viewPager) {
        DragListenerDispatcher dispatcher = getDragDispatcherByPageIndex(dragInfo.getPageIndex(), viewPager);
        if (dispatcher == null) {
            return;
        }
        dispatcher.onDrop(dragInfo, getRecyclerViewByPageIndex(dragInfo.getPageIndex(), viewPager));
    }


}
