package com.feicien.viewpager.demo.drag;

import android.os.Handler;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.feicien.viewpager.demo.adapter.GridPagerAdapter;
import com.feicien.viewpager.demo.utils.LogUtils;

import java.util.Optional;


public class ViewPagerDragListenerImp extends DragListenerDispatcher<ViewPager> {
    private static final String TAG = "ViewPagerDragListener";
    private int f34907a;
    private final int[] f34908b;
    private final Handler f34909c;
    private int f34910d;
    private int f34911e;
    private boolean f34912f;
    private DragListenerDispatcher f34913g;
    private int f34914h;
    private int f34915i;
    private final RunnableC9853a f34916j;
    private RecyclerView mRecyclerView;

    
    public class RunnableC9853a implements Runnable {
        private int f34918a;
        private final ViewPager f34919b;

        RunnableC9853a(ViewPager hwViewPager) {
            this.f34919b = hwViewPager;
        }

        void m3472a(int i) {
            this.f34918a = i;
        }

        @Override 
        public void run() {
            ViewPager viewPager = this.f34919b;
            if (viewPager == null) {
                LogUtils.d(ViewPagerDragListenerImp.TAG, "scrollRunnable can not execute, viewpager is null");
                return;
            }
            int currentItem = viewPager.getCurrentItem();
            if (this.f34918a == 0) {
                this.f34919b.setCurrentItem(currentItem - 1);
            } else {
                this.f34919b.setCurrentItem(currentItem + 1);
            }
            ViewPagerDragListenerImp.this.f34914h = 0;
            ViewPagerDragListenerImp.this.f34907a = 0;
            if (ViewPagerDragListenerImp.this.m3483l()) {
                ViewPagerDragListenerImp viewPagerDragListenerImp = ViewPagerDragListenerImp.this;
                viewPagerDragListenerImp.m3488g(this.f34919b, viewPagerDragListenerImp.f34908b[0], ViewPagerDragListenerImp.this.f34915i);
            }
        }
    }

    public ViewPagerDragListenerImp(ViewPager hwViewPager) {
        super(hwViewPager);
        this.f34907a = 0;
        this.f34908b = new int[2];
        this.f34910d = 0;
        this.f34911e = 0;
        this.f34914h = 0;
        this.f34915i = 0;
        this.f34909c = new Handler();
        this.f34916j = new RunnableC9853a(hwViewPager);
    }

    public void m3488g(ViewPager hwViewPager, float f, float f2) {
        int i = this.f34907a < ViewConfiguration.get(hwViewPager.getContext()).getScaledWindowTouchSlop() ? 1000 : 600;
        float f3 = f2 / 4.0f;
        if (f - f3 < this.f34910d) {
            if (this.f34914h == 0) {
                this.f34914h = 1;
                this.f34916j.m3472a(0);
                this.f34909c.postDelayed(this.f34916j, i);
                return;
            }
            return;
        }
        if (f + f3 > hwViewPager.getWidth() - this.f34911e) {
            if (this.f34914h == 0) {
                this.f34914h = 1;
                this.f34916j.m3472a(1);
                this.f34909c.postDelayed(this.f34916j, i);
                return;
            }
            return;
        }
        m3486i();
    }

    private void m3487h(DragInfo dragInfo, ViewPager hwViewPager) {
        int currentItem = hwViewPager.getCurrentItem();
        Optional<DragListenerDispatcher> m3485j = m3485j(currentItem, hwViewPager);
        Optional<RecyclerView> m3484k = m3484k(dragInfo.getPageIndex(), hwViewPager);
        if (!m3484k.isPresent()) {
            LogUtils.d(TAG, "has error on move, pageView == null, dragging pageIndex = " + dragInfo.getPageIndex() + ", pageAdapter = " + hwViewPager.getAdapter());
            return;
        }
        if (m3485j != null && m3485j.isPresent()) {
            if (this.f34913g != m3485j.get()) {
                Optional<RecyclerView> m3484k2 = m3484k(currentItem, hwViewPager);
                if (!m3484k2.isPresent()) {
                    LogUtils.d(TAG, "has error on move, nextView == null, current pageIndex = " + currentItem + ", pageAdapter = " + hwViewPager.getAdapter());
                    return;
                } else if (m3475t(dragInfo, currentItem, m3484k.get(), m3484k2.get())) {
                    LogUtils.i(TAG, "replaceData ");
                    return;
                } else {
                    m3485j.get().onDragEnter(dragInfo, m3484k2.get());
                    m3484k = m3484k2;
                }
            }
            m3485j.get().onDragOver(dragInfo, m3484k.get());
        } else {
            DragListenerDispatcher dragListenerDispatcher = this.f34913g;
            if (dragListenerDispatcher != null) {
                dragListenerDispatcher.onDragExit(dragInfo, m3484k.get());
            }
        }
        this.f34913g = m3485j.get();
    }

    private void m3486i() {
        this.f34909c.removeCallbacks(this.f34916j);
        if (this.f34914h == 1) {
            this.f34914h = 0;
            this.f34916j.m3472a(1);
        }
    }

    private Optional<DragListenerDispatcher> m3485j(int i, ViewPager hwViewPager) {
        DragManager dragManager = this.mDragManager;
        if (dragManager != null && hwViewPager != null) {
            DragListenerDispatcher m22979c = dragManager.getDragListener(i);
            if (hwViewPager.getCurrentItem() == i && m22979c != null) {
                return Optional.of(m22979c);
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

    private Optional<RecyclerView> m3484k(int i, ViewPager hwViewPager) {
        if (hwViewPager.getAdapter() != null && (hwViewPager.getAdapter() instanceof GridPagerAdapter)) {
            return ((GridPagerAdapter) hwViewPager.getAdapter()).getPage(i);
        }
        return Optional.empty();
    }

    private boolean m3475t(DragInfo dragInfo, int pageIndex, View view, View view2) {
        DragListenerDispatcher dragListenerDispatcher = this.f34913g;
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
            this.f34913g.onPageTransfer(dragInfo, dragInfo2);
            dragInfo.setPageIndex(dragInfo2.getPageIndex());
            dragInfo.setView(dragInfo2.getView());
        }
        return false;
    }

    @Override 
    public void clearMove() {
    }

    @Override 
    public boolean acceptDrop(DragInfo dragInfo, ViewPager hwViewPager) {
        Optional<DragListenerDispatcher> m3485j = m3485j(dragInfo.getPageIndex(), hwViewPager);
        if (m3485j == null || !m3485j.isPresent()) {
            return false;
        }
        return m3485j.get().acceptDrop(dragInfo, m3484k(dragInfo.getPageIndex(), hwViewPager).get());
    }

    @Override 
    public long getDraggingId() {
        return 0L;
    }

    public boolean m3483l() {
        return this.f34912f;
    }

    @Override 
    public void onDragEnd(DragInfo dragInfo, ViewPager hwViewPager) {
        this.f34907a = 0;
        if (this.f34912f) {
            this.f34912f = false;
            DragListenerDispatcher dragListenerDispatcher = this.f34913g;
            if (dragListenerDispatcher != null) {
                dragListenerDispatcher.onDragExit(dragInfo, m3484k(dragInfo.getPageIndex(), hwViewPager).get());
            }
            m3486i();
        }
        Optional<DragListenerDispatcher> m3485j = m3485j(dragInfo.getPageIndex(), hwViewPager);
        if (m3485j == null || !m3485j.isPresent()) {
            return;
        }
        m3485j.get().onDragEnd(dragInfo, m3484k(dragInfo.getPageIndex(), hwViewPager).get());
    }

    @Override 
    public void onDragEnter(DragInfo dragInfo, ViewPager hwViewPager) {
    }

    @Override 
    public void onDragExit(DragInfo dragInfo, ViewPager hwViewPager) {
    }

    @Override 
    public void onPageTransfer(DragInfo dragInfo, DragInfo dragInfo2) {
    }

    @Override 
    public void onDragOver(DragInfo dragInfo, ViewPager hwViewPager) {
        m3487h(dragInfo, hwViewPager);
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
        m3488g(hwViewPager, f, this.f34915i);
    }

    @Override 
    public boolean onDragPrepare(DragInfo dragInfo, ViewPager hwViewPager) {
        return true;
    }

    @Override 
    public void onDragStart(DragInfo dragInfo, ViewPager hwViewPager) {
        if (dragInfo == null) {
            LogUtils.d(TAG, "onDragStart dragInfo is null.");
            return;
        }
        if (hwViewPager == null) {
            LogUtils.d(TAG, "onDragStart viewPager is null.");
            return;
        }
        this.f34912f = true;
        this.f34913g = null;
        this.f34915i = dragInfo.getOutShadowSize().x;
        float m3276a = (dragInfo.getX() - dragInfo.getOutShadowTouchPoint().x) + (dragInfo.getOutShadowSize().x / 2.0f);
        int i = this.f34915i;
        if (m3276a - (i / 4.0f) >= this.f34910d && (i / 4.0f) + m3276a <= hwViewPager.getWidth() - this.f34911e) {
            this.f34914h = 0;
        } else {
            this.f34914h = 1;
            this.f34909c.postDelayed(this.f34916j, 600L);
        }
        Optional<DragListenerDispatcher> m3485j = m3485j(dragInfo.getPageIndex(), hwViewPager);
        if (m3485j == null || !m3485j.isPresent()) {
            return;
        }
        m3485j.get().onDragStart(dragInfo, m3484k(dragInfo.getPageIndex(), hwViewPager).get());
    }

    @Override 
    public void onDrop(DragInfo dragInfo, ViewPager hwViewPager) {
        Optional<DragListenerDispatcher> m3485j = m3485j(dragInfo.getPageIndex(), hwViewPager);
        if (m3485j == null || !m3485j.isPresent()) {
            return;
        }
        m3485j.get().onDrop(dragInfo, m3484k(dragInfo.getPageIndex(), hwViewPager).get());
    }

    public void m3474u(int i) {
        this.f34910d = i;
    }

    public void m3473v(int i) {
        this.f34911e = i;
    }
}
