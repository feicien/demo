package com.feicien.viewpager.demo.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.feicien.viewpager.demo.R;
import com.feicien.viewpager.demo.bean.AppIconInfo;
import com.feicien.viewpager.demo.drag.DragInfo;
import com.feicien.viewpager.demo.drag.DragManager;
import com.feicien.viewpager.demo.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class MyGridRecyclerAdapter extends RecyclerView.Adapter<MyGridRecyclerAdapter.ViewHolder> {

    private static final String TAG = "MyGridRecyclerAdapter";

    protected List<AppIconInfo> data = new ArrayList<>();
    protected int mPageIndex;
    private GridPagerAdapter mPagerAdapter;


    public MyGridRecyclerAdapter(List<AppIconInfo> list, int i, GridPagerAdapter gridPagerAdapter) {
        this.mPageIndex = i;
        mPagerAdapter = gridPagerAdapter;
        updateData(list);
        setHasStableIds(true);
    }

    public List<AppIconInfo> getData() {
        return new ArrayList<>(this.data);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<AppIconInfo> list) {
        if (list == null || list.isEmpty()) {
            LogUtils.d(TAG, "updateData AppIconInfo list is null. ");
            return;
        }
        this.data.clear();
        this.data.addAll(list);
        mPagerAdapter.updatePageData(this.mPageIndex, list);
        notifyDataSetChanged();
    }



    private int getPageChildIndexById(int i, long j) {
        RecyclerView recyclerView = mPagerAdapter.getPage(i);
        if (recyclerView == null) {
            LogUtils.d(TAG, "getPageChildIndexById recyclerView is null.");
            return -1;
        }
        if (recyclerView.getAdapter() == null || !(recyclerView.getAdapter() instanceof MyGridRecyclerAdapter)) {
            return -1;
        }
        return mPagerAdapter.transToDataListIndex(i, ((MyGridRecyclerAdapter) recyclerView.getAdapter()).getPositionForId(j));
    }



    public void onMove(int fromPosition, int toPosition) {
        if (fromPosition == -1 || toPosition == -1 || fromPosition == toPosition) {
            return;
        }
        if (this.mPageIndex == 0 && (toPosition == 1 || toPosition == 0)) {
            LogUtils.d(TAG, " Drag and drop the exit button. toPosition :" + toPosition);
        } else {
            if (toPosition == (mPagerAdapter.getAllData().size() - (this.mPageIndex * mPagerAdapter.getPageContentSize())) - 1) {
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
        mPagerAdapter.switchPageItem(getPageChildIndexById(dragInfo.getPageIndex(), dragInfo.getItemId()), getPageChildIndexById(dragInfo2.getPageIndex(), dragInfo2.getItemId()));
        mPagerAdapter.notifyPageChanged(dragInfo.getPageIndex());
        mPagerAdapter.notifyPageChanged(dragInfo2.getPageIndex());
    }




    @Override
    public long getItemId(int i) {
        return getData().get(i).hashCode();
    }

    @Override
    public int getItemViewType(int i) {
        return (mPagerAdapter.getAllData().size() > (this.mPageIndex + 1) * mPagerAdapter.getPageContentSize() || i != (mPagerAdapter.getAllData().size() - (this.mPageIndex * mPagerAdapter.getPageContentSize())) - 1) ? 1 : 0;
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
    public void onBindViewHolder(@NonNull MyGridRecyclerAdapter.ViewHolder viewHolder, int position) {
        long draggingId = mPagerAdapter.getDraggingId(this.mPageIndex);
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
//                mPagerAdapter.mContextRef.get();
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
        if (absoluteAdapterPosition == (mPagerAdapter.getAllData().size() - (this.mPageIndex * mPagerAdapter.getPageContentSize())) - 1) {
            LogUtils.d(TAG, "onItemLongClick Drag and drop to add button.");
            return;
        }
        View view = viewHolder.itemView;
        RecyclerView recyclerView = view.getParent() instanceof RecyclerView ? (RecyclerView) view.getParent() : null;
        if (recyclerView == null) {
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
        DragManager.getInstance().startDragAndDrop(view, dragInfo);
        LogUtils.i(TAG, "onItemLongDragClick dragInfo" + dragInfo);
        if (recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyItemChanged(recyclerView.getChildAdapterPosition(view));
        }
        AppIconInfo appIconInfo = this.data.get(absoluteAdapterPosition);
        if (TextUtils.isEmpty(appIconInfo.packageName)) {
            LogUtils.d(TAG, "onItemLongClick getName is null. ");
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final int mPageIndex;
        private final TextView mAppNameView;
        private final ImageView mImageView;

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
}
