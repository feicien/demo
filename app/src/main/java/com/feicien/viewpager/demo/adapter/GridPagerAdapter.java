package com.feicien.viewpager.demo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.feicien.viewpager.demo.bean.AppIconInfo;
import com.feicien.viewpager.demo.drag.DragManager;
import com.feicien.viewpager.demo.drag.RecyclerDragListenerImp;
import com.feicien.viewpager.demo.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class GridPagerAdapter extends PagerAdapter {
    private final List<AppIconInfo> mAppIconData;
    protected WeakReference<Context> mContextRef;
    private final List<ViewGroup> mPagesList = new ArrayList<>();
    private static final String TAG = "GridPagerAdapter";
    private final int mColumn;
    private final List<List<AppIconInfo>> mPageData;
    private final int mRow;


    public GridPagerAdapter(Context context, List<AppIconInfo> list, int row, int column) {
        ArrayList<AppIconInfo> arrayList = new ArrayList<>();
        this.mAppIconData = arrayList;
        this.mContextRef = new WeakReference<>(context);
        arrayList.clear();
        arrayList.addAll(list);
        this.mPageData = new ArrayList<>();
        this.mRow = row;
        this.mColumn = column;
        updateAllPageData(list);
    }


    protected FrameLayout createPage(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.addView(onCreatePage(frameLayout), new FrameLayout.LayoutParams(-1, -1));
        return frameLayout;
    }

    public RecyclerView onCreatePage(ViewGroup viewGroup) {
        if (viewGroup != null && viewGroup.getContext() != null) {
            RecyclerView recyclerView = new RecyclerView(viewGroup.getContext());

            recyclerView.setOverScrollMode(2);
            recyclerView.setVerticalScrollBarEnabled(false);
            return recyclerView;
        }
        return new RecyclerView(this.mContextRef.get());
    }

    @Override
    public void destroyItem(@NonNull ViewGroup viewGroup, int position, @NonNull Object obj) {
        if (obj instanceof View) {
            viewGroup.removeView((View) obj);
        }
        if (obj instanceof ViewGroup) {
            ViewGroup viewGroup2 = (ViewGroup) obj;
            if (viewGroup2.getChildCount() > 0) {
                View childAt = viewGroup2.getChildAt(0);
                if (childAt instanceof RecyclerView) {
                    onUnbindPage((RecyclerView) childAt, position);
                }
            }
        }
    }


    public List<AppIconInfo> getAllData() {
        ArrayList<AppIconInfo> arrayList = new ArrayList<>();
        int size = this.mPageData.size();
        for (int i = 0; i < size; i++) {
            arrayList.addAll(this.mPageData.get(i));
        }
        return arrayList;
    }

    @Override
    public int getCount() {
        if (mPagesList.isEmpty() && (this.mAppIconData) != null && !mAppIconData.isEmpty()) {


            Context context = this.mContextRef.get();
            if (this.mContextRef != null && context != null) {

                initPages(context);
            }

        }
        return this.mPagesList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object obj) {
        return -2;
    }

    public RecyclerView getPage(int pageIndex) {
        if (!mPagesList.isEmpty()) {
            View childAt = mPagesList.get(pageIndex).getChildAt(0);
            if (childAt instanceof RecyclerView) {
                return (RecyclerView) childAt;
            }
        }
        return null;
    }

    public int getPageNum() {
        return this.mPageData.size();
    }

    public final int getPageSize() {
        return this.mPagesList.size();
    }

    protected void initPages(Context context) {
        if (context == null) {
            return;
        }
        int pageNum = getPageNum();
        for (int i = 0; i < pageNum; i++) {
            this.mPagesList.add(createPage(context));
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup viewGroup, int i) {
        if (this.mPagesList.isEmpty() || i < 0 || i >= this.mPagesList.size()) {
            return new Object();
        }
        ViewGroup viewGroup2 = this.mPagesList.get(i);
        if (viewGroup2.getParent() == null) {
            viewGroup.addView(viewGroup2);
        }
        View childAt = viewGroup2.getChildAt(0);
        if (childAt instanceof ViewGroup) {
            ViewGroup viewGroup3 = (ViewGroup) childAt;
            if ((childAt instanceof RecyclerView) && viewGroup3.getChildCount() == 0) {
                onBindPage(viewGroup.getContext(), (RecyclerView) childAt, i);
            }
        }
        return viewGroup2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }


    public void release() {
        if (getPageSize() <= 0) {
            return;
        }
        int pageNum = getPageNum();
        for (int i = 0; i < pageNum; i++) {
            RecyclerView recyclerView = getPage(i);
            if (recyclerView != null) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    ((LinearLayoutManager) layoutManager).setRecycleChildrenOnDetach(false);
                }
            }
        }
        DragManager.getInstance().removeAllListener();
        WeakReference<Context> weakReference = this.mContextRef;
        if (weakReference != null) {
            weakReference.clear();
        }
        this.mPagesList.clear();
        notifyDataSetChanged();
    }


    public long getDraggingId(int pageIndex) {
        return DragManager.getInstance().getDraggingId(pageIndex);
    }


    public void onBindPage(Context context, RecyclerView recyclerView, int pageIndex) {
        if (recyclerView == null) {
            LogUtils.d(TAG, "onBindPage recyclerView is null.");
            return;
        }
        if (context == null) {
            return;
        }
        recyclerView.setLayoutManager(new PagerGridLayoutManager(context, this.mColumn, 1, false));
        List<AppIconInfo> pageInfo = getPageInfo(pageIndex);
        if (pageInfo != null) {
            recyclerView.setAdapter(new MyGridRecyclerAdapter(pageInfo, pageIndex, this));
        }
        recyclerView.setTag(Integer.valueOf(pageIndex));
        DragManager.getInstance().addDragListener(pageIndex, new RecyclerDragListenerImp(recyclerView, (MyGridRecyclerAdapter) recyclerView.getAdapter()));
    }

    public void onUnbindPage(RecyclerView recyclerView, int position) {
        if (recyclerView == null) {
            LogUtils.d(TAG, "onUnbindPage view is null.");
            return;
        }
        DragManager.getInstance().removeDragListener(position);
    }


    public static class PagerGridLayoutManager extends GridLayoutManager {
        private PagerGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
            setRecycleChildrenOnDetach(true);
        }

        @Override
        public boolean canScrollVertically() {
            return false;
        }
    }


    private List<AppIconInfo> getPageInfo(int i) {
        return this.mPageData.get(i);
    }


    public int getPageContentSize() {
        return this.mColumn * this.mRow;
    }


    public void notifyPageChanged(int pageIndex) {
        if (pageIndex < 0 || pageIndex >= getCount()) {
            LogUtils.d(TAG, "notifyPageChanged pageIndex " + pageIndex);
            return;
        }
        RecyclerView recyclerView = getPage(pageIndex);
        if (recyclerView == null) {
            LogUtils.d(TAG, "notifyPageChanged page View is null.");
            return;
        }
        if (recyclerView.getAdapter() == null || !(recyclerView.getAdapter() instanceof MyGridRecyclerAdapter)) {
            return;
        }
        MyGridRecyclerAdapter gridRecycleAdapter = (MyGridRecyclerAdapter) recyclerView.getAdapter();
        gridRecycleAdapter.updateData(getPageInfo(pageIndex));
        gridRecycleAdapter.notifyDataSetChanged();
    }


    public void switchPageItem(int i, int i2) {
        List<AppIconInfo> allData = getAllData();
        allData.add(i2, allData.remove(i));
        updateAllPageData(allData);
    }

    public int transToDataListIndex(int i, int i2) {
        if (i < 0 || i2 < 0) {
            return -1;
        }
        return (getPageContentSize() * i) + i2;
    }

    protected void updateAllPageData(List<AppIconInfo> list) {
        if (list == null || list.isEmpty()) {
            LogUtils.d(TAG, "updateAllPageData list is null or empty.");
            return;
        }
        this.mPageData.clear();
        int pageNum = getPageNum(list);
        for (int i = 0; i < pageNum; i++) {
            this.mPageData.add(getPageInfo(i, list));
        }
    }

    protected void updatePageData(int i, List<AppIconInfo> list) {
        if (list == null || list.isEmpty()) {
            LogUtils.d(TAG, "update PageData list is null.");
        } else {
            this.mPageData.remove(i);
            this.mPageData.add(i, list);
        }
    }

    private List<AppIconInfo> getPageInfo(int i, List<AppIconInfo> list) {
        return new ArrayList<>(list.subList(getPageContentSize() * i, i == getPageNum(list) + (-1) ? list.size() : (i + 1) * getPageContentSize()));
    }

    private int getPageNum(List<AppIconInfo> list) {
        return (list.size() / getPageContentSize()) + (list.size() % getPageContentSize() == 0 ? 0 : 1);
    }

}
