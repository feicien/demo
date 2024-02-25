package com.feicien.viewpager.demo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.feicien.viewpager.demo.R;
import com.feicien.viewpager.demo.bean.AppIconInfo;
import com.feicien.viewpager.demo.drag.DragInfo;
import com.feicien.viewpager.demo.drag.DragListenerDispatcher;
import com.feicien.viewpager.demo.drag.DragManager;
import com.feicien.viewpager.demo.drag.RecyclerDragListenerImp;
import com.feicien.viewpager.demo.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class GridPagerAdapter extends PagerAdapter {
    private final List<AppIconInfo> mAppIconData;
    protected WeakReference<Context> mContextRef;
    private final List<ViewGroup> mPagesList = new ArrayList<>();
    private static final String TAG = "GridPagerAdapter";
    private final int mColumn;
    private final List<List<AppIconInfo>> mPageData;
    private final int mRow;


    protected DragManager<RecyclerView> mDragManager;


    public GridPagerAdapter(Context context, List<AppIconInfo> list, DragListenerDispatcher<ViewPager> dragListenerDispatcher, int row, int column) {
        ArrayList arrayList = new ArrayList();
        this.mAppIconData = arrayList;
        this.mContextRef = new WeakReference<>(context);
        arrayList.clear();
        arrayList.addAll(list);
        this.mPageData = new ArrayList();
        this.mRow = row;
        this.mColumn = column;
        updateAllPageData(list);
        DragManager<RecyclerView> dragManager = new DragManager<>();
        this.mDragManager = dragManager;
        dragListenerDispatcher.attachDragManager(dragManager);
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

    public Optional<RecyclerView> getPage(int i) {
        List<ViewGroup> list = this.mPagesList;
        if (list != null && list.size() > 0) {
            View childAt = this.mPagesList.get(i).getChildAt(0);
            if (childAt instanceof RecyclerView) {
                return Optional.of((RecyclerView) childAt);
            }
        }
        return Optional.empty();
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
        WeakReference<Context> weakReference = this.mContextRef;
        if (weakReference != null) {
            weakReference.clear();
        }
        this.mPagesList.clear();
        notifyDataSetChanged();
    }


    public class MyGridRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

        protected List<AppIconInfo> data = new ArrayList<>();
        protected int mPageIndex;


        private MyGridRecyclerAdapter(List<AppIconInfo> list, int i) {
            this.mPageIndex = i;
            updateData(list);
            setHasStableIds(true);
        }

        public List<AppIconInfo> getData() {
            return new ArrayList(this.data);
        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }


        public void updateData(List<AppIconInfo> list) {
            if (list == null || list.isEmpty()) {
                LogUtils.d(TAG, "updateData AppIconInfo list is null. ");
                return;
            }
            this.data.clear();
            this.data.addAll(list);
            GridPagerAdapter.this.updatePageData(this.mPageIndex, list);
            notifyDataSetChanged();
        }



        private int getPageChildIndexById(int i, long j) {
            if (!GridPagerAdapter.this.getPage(i).isPresent()) {
                LogUtils.d(TAG, "getPageChildIndexById recyclerView is null.");
                return -1;
            }
            RecyclerView recyclerView = GridPagerAdapter.this.getPage(i).get();
            if (recyclerView.getAdapter() == null || !(recyclerView.getAdapter() instanceof MyGridRecyclerAdapter)) {
                return -1;
            }
            return GridPagerAdapter.this.transToDataListIndex(i, ((MyGridRecyclerAdapter) recyclerView.getAdapter()).getPositionForId(j));
        }



        public void onMove(int fromPosition, int toPosition) {
            if (fromPosition == -1 || toPosition == -1 || fromPosition == toPosition) {
                return;
            }
            if (this.mPageIndex == 0 && (toPosition == 1 || toPosition == 0)) {
                LogUtils.d(TAG, " Drag and drop the exit button. toPosition :" + toPosition);
            } else {
                if (toPosition == (GridPagerAdapter.this.getAllData().size() - (this.mPageIndex * GridPagerAdapter.this.getPageContentSize())) - 1) {
                    LogUtils.d(TAG, "Drag and drop the setting button. toPosition " + toPosition);
                    return;
                }
                List<AppIconInfo> data = getData();
                data.add(toPosition, data.remove(fromPosition));
                updateData(data);
            }
        }

        public void onPageTransfer(DragInfo dragInfo, DragInfo dragInfo2) {
            if (dragInfo == null || dragInfo2 == null || dragInfo.getItemId() == -1 || dragInfo2.getItemId() == -1) {
                return;
            }
            GridPagerAdapter.this.switchPageItem(getPageChildIndexById(dragInfo.getPageIndex(), dragInfo.getItemId()), getPageChildIndexById(dragInfo2.getPageIndex(), dragInfo2.getItemId()));
            GridPagerAdapter.this.notifyPageChanged(dragInfo.getPageIndex());
            GridPagerAdapter.this.notifyPageChanged(dragInfo2.getPageIndex());
        }




        @Override
        public long getItemId(int i) {
            return getData().get(i).hashCode();
        }

        @Override
        public int getItemViewType(int i) {
            return (GridPagerAdapter.this.getAllData().size() > (this.mPageIndex + 1) * GridPagerAdapter.this.getPageContentSize() || i != (GridPagerAdapter.this.getAllData().size() - (this.mPageIndex * GridPagerAdapter.this.getPageContentSize())) - 1) ? 1 : 0;
        }

        public int getPositionForId(long itemId) {
            for (int i = 0; i < getData().size(); i++) {
                if (getData().get(i).hashCode() == itemId) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            long draggingId = GridPagerAdapter.this.getDraggingId(this.mPageIndex);
            viewHolder.itemView.setVisibility(draggingId == getItemId(position) ? View.INVISIBLE : View.VISIBLE);
            viewHolder.itemView.setAlpha(draggingId == getItemId(position) ? 0.0f : 1.0f);
            viewHolder.itemView.postInvalidate();

            ViewGroup.LayoutParams layoutParams = viewHolder.mAppNameView.getLayoutParams();

            viewHolder.mAppNameView.setLayoutParams(layoutParams);
            List<AppIconInfo> list = this.data;
            if (list == null) {
                LogUtils.d(TAG, "setIconContent data is null.");
                return;
            }
            String name = list.get(position).name;
            viewHolder.mAppNameView.setText(name);
            if (position % 2 == 0) {
                viewHolder.mImageView.setImageResource(R.mipmap.ic_baidu_map);
            } else {
                viewHolder.mImageView.setImageResource(R.mipmap.icon_gaode_map);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            return new ViewHolder(View.inflate(viewGroup.getContext(), R.layout.app_icon_item_layout, null), mPageIndex);
        }


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
                LogUtils.d(TAG, "onItemClick getName is null. ");
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
                    LogUtils.d(TAG, "onItemClick packageName is null. ");
                } else {
                    LogUtils.i(TAG, "onItemClick reportAppActionClick is packageName. " + pkg);
                }
            }
        }

        public void onItemLongClick(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder == null) {
                return;
            }
            int absoluteAdapterPosition = viewHolder.getAdapterPosition();
            if (this.mPageIndex == 0 && (absoluteAdapterPosition == 0 || absoluteAdapterPosition == 1)) {
                LogUtils.d(TAG, "onItemLongClick Drag and drop the exit button.");
                return;
            }
            if (absoluteAdapterPosition == (GridPagerAdapter.this.getAllData().size() - (this.mPageIndex * GridPagerAdapter.this.getPageContentSize())) - 1) {
                LogUtils.d(TAG, "onItemLongClick Drag and drop to add button.");
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
                LogUtils.d(TAG, "onItemLongClick getName is null. ");
            }
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final int mPageIndex;
        private TextView mAppNameView;
        private ImageView mImageView;

        ViewHolder(View view, int pageIndex) {
            super(view);
            this.mPageIndex = pageIndex;
            this.mAppNameView = view.findViewById(R.id.item_app_name);
            this.mImageView = view.findViewById(R.id.mobile_add_icon);
        }

        public int getPageIndex() {
            return this.mPageIndex;
        }
    }




    public long getDraggingId(int pageIndex) {
        return this.mDragManager.getDraggingId(pageIndex);
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
            recyclerView.setAdapter(new MyGridRecyclerAdapter(pageInfo, pageIndex));
        }
        recyclerView.setTag(Integer.valueOf(pageIndex));
        if (this.mDragManager == null) {
            LogUtils.d(TAG, "onBindPage mDragManager is null.");
        } else if (recyclerView.getAdapter() instanceof MyGridRecyclerAdapter) {
            this.mDragManager.addDragListener(pageIndex, new RecyclerDragListenerImp(recyclerView, (MyGridRecyclerAdapter) recyclerView.getAdapter()));
        }
    }

    public void onUnbindPage(RecyclerView recyclerView, int position) {
        if (recyclerView == null) {
            LogUtils.d(TAG, "onUnbindPage view is null.");
            return;
        }
        DragManager<RecyclerView> dragManager = this.mDragManager;
        if (dragManager == null) {
            LogUtils.d(TAG, "onUnbindPage mDragManager is null.");
        } else {
            dragManager.removeDragListener(position);
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



    private List<AppIconInfo> getPageInfo(int i) {
        return this.mPageData.get(i);
    }


    public int getPageContentSize() {
        return this.mColumn * this.mRow;
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
        if (recyclerView.getAdapter() == null || !(recyclerView.getAdapter() instanceof MyGridRecyclerAdapter)) {
            return;
        }
        MyGridRecyclerAdapter gridRecycleAdapter = (MyGridRecyclerAdapter) recyclerView.getAdapter();
        gridRecycleAdapter.updateData(getPageInfo(i));
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
        return new ArrayList<>(list.subList(getPageContentSize() * i, i == getPageNum(list) + (-1) ? list.size() : (i + 1) * getPageContentSize()));
    }

    private int getPageNum(List<AppIconInfo> list) {
        return (list.size() / getPageContentSize()) + (list.size() % getPageContentSize() == 0 ? 0 : 1);
    }

}
