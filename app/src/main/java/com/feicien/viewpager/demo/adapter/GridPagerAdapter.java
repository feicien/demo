package com.feicien.viewpager.demo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.feicien.viewpager.demo.R;
import com.feicien.viewpager.demo.bean.AppIconInfo;
import com.feicien.viewpager.demo.drag.DragInfo;
import com.feicien.viewpager.demo.drag.DragListenerDispatcher;
import com.feicien.viewpager.demo.drag.DragManager;
import com.feicien.viewpager.demo.drag.RecyclerDragListenerImp;
import com.feicien.viewpager.demo.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;


public class GridPagerAdapter extends BasePagerAdapter {
    private static final String TAG = "GridPagerAdapter";
    private final int mColumn;
    private final List<List<AppIconInfo>> mPageData;
    private final int mRow;


    protected DragManager<RecyclerView> mDragManager;


    public GridPagerAdapter(Context context, List<AppIconInfo> list, DragListenerDispatcher<ViewPager> dragListenerDispatcher, int row, int column) {
        super(context, list);
        this.mPageData = new ArrayList();
        this.mRow = row;
        this.mColumn = column;
        updateAllPageData(list);
        DragManager<RecyclerView> dragManager = new DragManager<>();
        this.mDragManager = dragManager;
        dragListenerDispatcher.attachDragManager(dragManager);
    }



    public class MyGridRecyclerAdapter extends DragAdapter<ViewHolder> {

        private MyGridRecyclerAdapter(Context context, List<AppIconInfo> list, int i) {
            super(list, i);
        }

        @Override
        public long getItemId(int i) {
            return getData().get(i).hashCode();
        }

        @Override
        public int getItemViewType(int i) {
            return (GridPagerAdapter.this.getAllData().size() > (this.mPageIndex + 1) * GridPagerAdapter.this.getPageContentSize() || i != (GridPagerAdapter.this.getAllData().size() - (this.mPageIndex * GridPagerAdapter.this.getPageContentSize())) - 1) ? 1 : 0;
        }

        @Override
        public int getPositionForId(long itemId) {
            for (int i = 0; i < getData().size(); i++) {
                if (getData().get(i).hashCode() == itemId) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position, int pageIndex) {
            if (viewHolder == null) {
                return;
            }



            ViewGroup.LayoutParams layoutParams = viewHolder.mAppNameView.getLayoutParams();

            viewHolder.mAppNameView.setLayoutParams(layoutParams);
            List<AppIconInfo> list = this.data;
            if (list == null) {
                LogUtils.d(GridPagerAdapter.TAG, "setIconContent data is null.");
                return;
            }
            String str = list.get(position).packageName;
            String name = list.get(position).name;
            viewHolder.mAppNameView.setText(name);
            if (position % 2 == 0) {
                viewHolder.mImageView.setImageResource(R.mipmap.ic_baidu_map);
            } else {
                viewHolder.mImageView.setImageResource(R.mipmap.icon_gaode_map);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType, int pageIndex) {
            return new ViewHolder(View.inflate(viewGroup.getContext(), R.layout.app_icon_item_layout, null), pageIndex, viewType);
        }

        @Override
        public void onDragEnd(int position, View view) {
            super.onDragEnd(position, view);
        }

        @Override
        public void onDragStart(int position, View view) {
            super.onDragStart(position, view);
        }

        @Override
        public void onItemClick(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder == null) {
                return;
            }
            int absoluteAdapterPosition = viewHolder.getAdapterPosition();
            List<AppIconInfo> list = this.data;
            if (list == null || list.get(absoluteAdapterPosition) == null) {
                return;
            }
            String m22909d = this.data.get(absoluteAdapterPosition).name;
            if (TextUtils.isEmpty(m22909d)) {
                LogUtils.d(GridPagerAdapter.TAG, "onItemClick getName is null. ");
                return;
            }
            int i = this.mPageIndex;
            if (i != 0 || absoluteAdapterPosition != 0) {
                if (i == 0 && absoluteAdapterPosition == 1) {
                    GridPagerAdapter.this.mContextRef.get();
                    return;
                }
                String pkg = this.data.get(absoluteAdapterPosition).packageName;
                if (TextUtils.isEmpty(pkg)) {
                    LogUtils.d(GridPagerAdapter.TAG, "onItemClick packageName is null. ");
                } else {
                    LogUtils.i(GridPagerAdapter.TAG, "onItemClick reportAppActionClick is packageName. " + pkg);
                }
            }
        }

        @Override
        public void onItemLongClick(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder == null) {
                return;
            }
            int absoluteAdapterPosition = viewHolder.getAdapterPosition();
            if (this.mPageIndex == 0 && (absoluteAdapterPosition == 0 || absoluteAdapterPosition == 1)) {
                LogUtils.d(GridPagerAdapter.TAG, "onItemLongClick Drag and drop the exit button.");
                return;
            }
            if (absoluteAdapterPosition == (GridPagerAdapter.this.getAllData().size() - (this.mPageIndex * GridPagerAdapter.this.getPageContentSize())) - 1) {
                LogUtils.d(GridPagerAdapter.TAG, "onItemLongClick Drag and drop to add button.");
                return;
            }
            View view = viewHolder.itemView;
            RecyclerView recyclerView = view.getParent() instanceof RecyclerView ? (RecyclerView) view.getParent() : null;
            if (recyclerView == null || mDragManager == null) {
                LogUtils.d(TAG, "onItemLongDragClick recyclerView or mDragManager is null.");
                return;
            }
            ViewHolder c5272b = recyclerView.getChildViewHolder(view) instanceof ViewHolder ? (ViewHolder) recyclerView.getChildViewHolder(view) : null;
            if (c5272b == null) {
                LogUtils.d(TAG, "onItemLongDragClick childViewHolder is null.");
                return;
            }
            DragInfo dragInfo = new DragInfo();
            dragInfo.setPageIndex(c5272b.getPageIndex());
            dragInfo.setItemId(c5272b.getItemId());
            mDragManager.startDragAndDrop(view, dragInfo);
            LogUtils.i(TAG, "onItemLongDragClick dragInfo" + dragInfo);
            if (recyclerView.getAdapter() != null) {
                recyclerView.getAdapter().notifyItemChanged(recyclerView.getChildAdapterPosition(view));
            }
            AppIconInfo appIconInfo = this.data.get(absoluteAdapterPosition);
            if (TextUtils.isEmpty(appIconInfo.packageName)) {
                LogUtils.d(GridPagerAdapter.TAG, "onItemLongClick getName is null. ");
            }
        }
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final int mPageIndex;
        private TextView mAppNameView;
        private ImageView mImageView;

        ViewHolder(View view, int pageIndex, int viewType) {
            super(view);
            this.mPageIndex = pageIndex;
            this.mAppNameView = (TextView) view.findViewById(R.id.item_app_name);
            this.mImageView = (ImageView) view.findViewById(R.id.mobile_add_icon);
        }

        public int getPageIndex() {
            return this.mPageIndex;
        }
    }



    public abstract class DragAdapter<VH extends ViewHolder> extends GridRecycleAdapter<VH> implements DragNotifier {
        public DragAdapter(List<AppIconInfo> list, int i) {
            super(list, i);
            setHasStableIds(true);
        }

        private int getPageChildIndexById(int i, long j) {
            if (!GridPagerAdapter.this.getPage(i).isPresent()) {
                LogUtils.d(GridPagerAdapter.TAG, "getPageChildIndexById recyclerView is null.");
                return -1;
            }
            RecyclerView recyclerView = GridPagerAdapter.this.getPage(i).get();
            if (recyclerView.getAdapter() == null || !(recyclerView.getAdapter() instanceof DragAdapter)) {
                return -1;
            }
            return GridPagerAdapter.this.transToDataListIndex(i, ((DragAdapter) recyclerView.getAdapter()).getPositionForId(j));
        }

        @Override
        public void onDragEnd(int position, View view) {
            notifyItemChanged(position);
        }

        @Override
        public void onDragEnter(int position, View view) {
            notifyItemChanged(position);
        }

        @Override
        public void onDragExit(int position, View view) {
            notifyItemChanged(position);
        }

        @Override
        public void onDragStart(int position, View view) {
            notifyItemChanged(position);
        }

        @Override
        public void onDrop(long itemId, View view) {
        }

        @Override
        public void onMove(int fromPosition, int toPosition) {
            if (fromPosition == -1 || toPosition == -1 || fromPosition == toPosition) {
                return;
            }
            if (this.mPageIndex == 0 && (toPosition == 1 || toPosition == 0)) {
                LogUtils.d(GridPagerAdapter.TAG, " Drag and drop the exit button. toPosition :" + toPosition);
            } else {
                if (toPosition == (GridPagerAdapter.this.getAllData().size() - (this.mPageIndex * GridPagerAdapter.this.getPageContentSize())) - 1) {
                    LogUtils.d(GridPagerAdapter.TAG, "Drag and drop the setting button. toPosition " + toPosition);
                    return;
                }
                List<AppIconInfo> data = getData();
                data.add(toPosition, data.remove(fromPosition));
                updateData(data);
            }
        }

        @Override
        public void onPageTransfer(DragInfo dragInfo, DragInfo dragInfo2) {
            if (dragInfo == null || dragInfo2 == null || dragInfo.getItemId() == -1 || dragInfo2.getItemId() == -1) {
                return;
            }
            GridPagerAdapter.this.switchPageItem(getPageChildIndexById(dragInfo.getPageIndex(), dragInfo.getItemId()), getPageChildIndexById(dragInfo2.getPageIndex(), dragInfo2.getItemId()));
            GridPagerAdapter.this.notifyPageChanged(dragInfo.getPageIndex());
            GridPagerAdapter.this.notifyPageChanged(dragInfo2.getPageIndex());
        }

        @Override
        public void onBindViewHolder(VH vh, int position) {
            super.onBindViewHolder(vh, position);
            long draggingId = GridPagerAdapter.this.getDraggingId(this.mPageIndex);
            vh.itemView.setVisibility(draggingId == getItemId(position) ? View.INVISIBLE : View.VISIBLE);
            vh.itemView.setAlpha(draggingId == getItemId(position) ? 0.0f : 1.0f);
            vh.itemView.postInvalidate();
        }

        @Override
        public VH onCreateViewHolder(ViewGroup viewGroup, int i) {
            return (VH) super.onCreateViewHolder(viewGroup, i);
        }
    }



    public long getDraggingId(int pageIndex) {
        return this.mDragManager.getDraggingId(pageIndex);
    }


    @Override
    public void onBindPage(Context context, RecyclerView recyclerView, int i) {
        if (recyclerView == null) {
            LogUtils.d(TAG, "onBindPage recyclerView is null.");
            return;
        }
        if (context == null || recyclerView == null) {
            return;
        }
        recyclerView.setLayoutManager(getLayoutManager(context));
        List<AppIconInfo> pageInfo = getPageInfo(i);
        if (pageInfo != null) {
            recyclerView.setAdapter(new MyGridRecyclerAdapter(context, pageInfo, i));
        }
        recyclerView.setTag(Integer.valueOf(i));
        if (this.mDragManager == null) {
            LogUtils.d(TAG, "onBindPage mDragManager is null.");
        } else if (recyclerView.getAdapter() instanceof DragNotifier) {
            this.mDragManager.addDragListener(i, new RecyclerDragListenerImp(recyclerView, (DragNotifier) recyclerView.getAdapter()));
        }
    }

    @Override
    public void onUnbindPage(RecyclerView recyclerView, int position) {
        if (recyclerView == null) {
            LogUtils.d(TAG, "onUnbindPage view is null.");
            return;
        }
        super.onUnbindPage(recyclerView, position);
        DragManager<RecyclerView> dragManager = this.mDragManager;
        if (dragManager == null) {
            LogUtils.d(TAG, "onUnbindPage mDragManager is null.");
        } else {
            dragManager.removeDragListener(position);
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
        DragManager<RecyclerView> dragManager = this.mDragManager;
        if (dragManager == null) {
            LogUtils.d(TAG, "release mDragManager is null.");
        } else {
            dragManager.removeAllListener();
        }
        super.release();
    }


    
    public abstract class GridRecycleAdapter<VH extends ViewHolder> extends RecyclerView.Adapter<VH> {
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
                LogUtils.d(GridPagerAdapter.TAG, "updateData AppIconInfo list is null. ");
                return;
            }
            this.data.clear();
            this.data.addAll(list);
            GridPagerAdapter.this.updatePageData(this.mPageIndex, list);
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
