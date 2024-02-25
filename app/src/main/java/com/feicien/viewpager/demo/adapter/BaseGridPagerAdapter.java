package com.feicien.viewpager.demo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feicien.viewpager.demo.bean.AppIconInfo;
import com.feicien.viewpager.demo.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseGridPagerAdapter extends BasePagerAdapter {
    private static final int SCROLL_NONE = -1;
    private static final String TAG = "BaseGridPagerAdapter";
    private final int mColumn;
    private final List<List<AppIconInfo>> mPageData;
    private final int mRow;

    protected abstract GridRecycleAdapter generateItemAdapter(Context context, List<AppIconInfo> list, int i);

    
    public abstract class GridRecycleAdapter<VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {
        protected List<AppIconInfo> data = new ArrayList();
        protected int mPageIndex;

        public abstract void onBindViewHolder(VH vh, int i, int i2);

        public abstract VH onCreateViewHolder(ViewGroup viewGroup, int i, int i2);

        public GridRecycleAdapter(List<AppIconInfo> list, int i) {
            this.mPageIndex = i;
            updateData(list);
        }

        public List<AppIconInfo> getData() {
            return new ArrayList(this.data);
        }

        @Override 
        public int getItemCount() {
            return this.data.size();
        }

        public AppIconInfo getValue(int i) {
            return this.data.get(i);
        }

        public void updateData(List<AppIconInfo> list) {
            if (list == null || list.isEmpty()) {
                LogUtils.d(BaseGridPagerAdapter.TAG, "updateData AppIconInfo list is null. ");
                return;
            }
            this.data.clear();
            this.data.addAll(list);
            BaseGridPagerAdapter.this.updatePageData(this.mPageIndex, list);
            notifyDataSetChanged();
        }

        @Override 
        public void onBindViewHolder(VH vh, int position) {
            onBindViewHolder( vh, position, this.mPageIndex);
        }

        @Override 
        public VH onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            return onCreateViewHolder(viewGroup, viewType, this.mPageIndex);
        }
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

    
    public static class BaseViewHolder extends RecyclerView.ViewHolder {
        private final int mPageIndex;

        public BaseViewHolder(View view, int pageIndex) {
            super(view);
            this.mPageIndex = pageIndex;
        }

        public int getPageIndex() {
            return this.mPageIndex;
        }
    }

    public BaseGridPagerAdapter(Context context, List<AppIconInfo> list, int row, int column) {
        super(context, list);
        this.mPageData = new ArrayList();
        this.mRow = row;
        this.mColumn = column;
        updateAllPageData(list);
    }

    private RecyclerView.LayoutManager getLayoutManager(Context context) {
        return new PagerGridLayoutManager(context, this.mColumn, 1, false);
    }

    private List<AppIconInfo> getPageInfo(int i) {
        return this.mPageData.get(i);
    }

    @Override 
    public List<AppIconInfo> getAllData() {
        ArrayList<AppIconInfo> arrayList = new ArrayList<>();
        int size = this.mPageData.size();
        for (int i = 0; i < size; i++) {
            arrayList.addAll(this.mPageData.get(i));
        }
        return arrayList;
    }

    public int getPageContentSize() {
        return this.mColumn * this.mRow;
    }

    @Override 
    public int getPageNum() {
        return this.mPageData.size();
    }

    public void notifyPageChanged(int i) {
        if (i < 0 || i >= getCount()) {
            LogUtils.d(TAG, "notifyPageChanged pageIndex " + i);
            return;
        }
        if (!getPage(i).isPresent()) {
            LogUtils.d(TAG, "notifyPageChanged page View is null.");
            return;
        }
        RecyclerView recyclerView = getPage(i).get();
        if (recyclerView.getAdapter() == null || !(recyclerView.getAdapter() instanceof GridRecycleAdapter)) {
            return;
        }
        GridRecycleAdapter gridRecycleAdapter = (GridRecycleAdapter) recyclerView.getAdapter();
        gridRecycleAdapter.updateData(getPageInfo(i));
        gridRecycleAdapter.notifyDataSetChanged();
    }

    @Override 
    public void onBindPage(Context context, RecyclerView recyclerView, int i) {
        if (context == null || recyclerView == null) {
            return;
        }
        recyclerView.setLayoutManager(getLayoutManager(context));
        List<AppIconInfo> pageInfo = getPageInfo(i);
        if (pageInfo != null) {
            recyclerView.setAdapter(generateItemAdapter(context, pageInfo, i));
        }
    }

    @Override 
    public RecyclerView onCreatePage(ViewGroup viewGroup) {
        if (viewGroup != null && viewGroup.getContext() != null) {
            RecyclerView recyclerView = new RecyclerView(viewGroup.getContext());

            recyclerView.setOverScrollMode(2);
            recyclerView.setVerticalScrollBarEnabled(false);
            return recyclerView;
        }
        return new RecyclerView(this.mContextRef.get());
    }

    public void reBindAllPage() {
        for (int i = 0; i < getCount(); i++) {
            notifyPageChanged(i);
        }
    }

    @Override 
    public void release() {
        if (getPageSize() <= 0) {
            return;
        }
        int pageNum = getPageNum();
        for (int i = 0; i < pageNum; i++) {
            if (getPage(i).isPresent()) {
                RecyclerView.LayoutManager layoutManager = getPage(i).get().getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    ((LinearLayoutManager) layoutManager).setRecycleChildrenOnDetach(false);
                }
            }
        }
        super.release();
    }

    public void removePageData(AppIconInfo appIconInfo) {
        if (appIconInfo == null) {
            LogUtils.d(TAG, "removePageData appIconInfo is null.");
            return;
        }
        int pageNum = getPageNum();
        List<AppIconInfo> allData = getAllData();
        if (allData.remove(appIconInfo)) {
            updateAllPageData(allData);
            if (pageNum != getPageNum()) {
                removePage(pageNum - 1);
            } else {
                reBindAllPage();
            }
        }
    }

    public void setData(List<AppIconInfo> list) {
        if (list != null) {
            updateAllPageData(list);
            reCreateAllPages(list);
        }
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
            LogUtils.d(TAG, "updateAllPageData list is null.");
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
        return new ArrayList(list.subList(getPageContentSize() * i, i == getPageNum(list) + (-1) ? list.size() : (i + 1) * getPageContentSize()));
    }

    private int getPageNum(List<AppIconInfo> list) {
        return (list.size() / getPageContentSize()) + (list.size() % getPageContentSize() == 0 ? 0 : 1);
    }
}
