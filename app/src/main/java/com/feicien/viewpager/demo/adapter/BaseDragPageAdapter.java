package com.feicien.viewpager.demo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.feicien.viewpager.demo.bean.AppIconInfo;
import com.feicien.viewpager.demo.drag.DragInfo;
import com.feicien.viewpager.demo.drag.DragListenerDispatcher;
import com.feicien.viewpager.demo.drag.DragManager;
import com.feicien.viewpager.demo.drag.RecyclerDragListenerImp;
import com.feicien.viewpager.demo.utils.LogUtils;

import java.util.List;


public abstract class BaseDragPageAdapter extends BaseGridPagerAdapter {
    private static final String TAG = "BaseDragPageAdapter";
    protected DragManager<RecyclerView> mDragManager;

    
    public abstract class DragAdapter<VH extends BaseViewHolder> extends GridRecycleAdapter<VH> implements DragNotifier {
        public DragAdapter(List<AppIconInfo> list, int i) {
            super(list, i);
            setHasStableIds(true);
        }

        private int getPageChildIndexById(int i, long j) {
            if (!BaseDragPageAdapter.this.getPage(i).isPresent()) {
                LogUtils.d(BaseDragPageAdapter.TAG, "getPageChildIndexById recyclerView is null.");
                return -1;
            }
            RecyclerView recyclerView = BaseDragPageAdapter.this.getPage(i).get();
            if (recyclerView.getAdapter() == null || !(recyclerView.getAdapter() instanceof DragAdapter)) {
                return -1;
            }
            return BaseDragPageAdapter.this.transToDataListIndex(i, ((DragAdapter) recyclerView.getAdapter()).getPositionForId(j));
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
                LogUtils.d(BaseDragPageAdapter.TAG, " Drag and drop the exit button. toPosition :" + toPosition);
            } else {
                if (toPosition == (BaseDragPageAdapter.this.getAllData().size() - (this.mPageIndex * BaseDragPageAdapter.this.getPageContentSize())) - 1) {
                    LogUtils.d(BaseDragPageAdapter.TAG, "Drag and drop the setting button. toPosition " + toPosition);
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
            BaseDragPageAdapter.this.switchPageItem(getPageChildIndexById(dragInfo.getPageIndex(), dragInfo.getItemId()), getPageChildIndexById(dragInfo2.getPageIndex(), dragInfo2.getItemId()));
            BaseDragPageAdapter.this.notifyPageChanged(dragInfo.getPageIndex());
            BaseDragPageAdapter.this.notifyPageChanged(dragInfo2.getPageIndex());
        }

        @Override 
        public void onBindViewHolder(VH vh, int position) {
            super.onBindViewHolder(vh, position);
            long draggingId = BaseDragPageAdapter.this.getDraggingId(this.mPageIndex);
            vh.itemView.setVisibility(draggingId == getItemId(position) ? 4 : 0);
            vh.itemView.setAlpha(draggingId == getItemId(position) ? 0.0f : 1.0f);
            vh.itemView.postInvalidate();
        }

        @Override 
        public VH onCreateViewHolder(ViewGroup viewGroup, int i) {
            return (VH) super.onCreateViewHolder(viewGroup, i);
        }
    }

    public BaseDragPageAdapter(Context context, List<AppIconInfo> list, DragListenerDispatcher<ViewPager> dragListenerDispatcher, int row, int column) {
        super(context, list, row, column);
        DragManager<RecyclerView> dragManager = new DragManager<>();
        this.mDragManager = dragManager;
        dragListenerDispatcher.attachDragManager(dragManager);
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
        super.onBindPage(context, recyclerView, i);
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
        super.release();
        DragManager<RecyclerView> dragManager = this.mDragManager;
        if (dragManager == null) {
            LogUtils.d(TAG, "release mDragManager is null.");
        } else {
            dragManager.removeAllListener();
        }
    }

    public void setLongClickDrag(View view) {
        if (view == null) {
            LogUtils.d(TAG, "onItemLongDragClick view is null.");
            return;
        }
        RecyclerView recyclerView = view.getParent() instanceof RecyclerView ? (RecyclerView) view.getParent() : null;
        if (recyclerView == null || this.mDragManager == null) {
            LogUtils.d(TAG, "onItemLongDragClick recyclerView or mDragManager is null.");
            return;
        }
        BaseViewHolder c5272b = recyclerView.getChildViewHolder(view) instanceof BaseViewHolder ? (BaseViewHolder) recyclerView.getChildViewHolder(view) : null;
        if (c5272b == null) {
            LogUtils.d(TAG, "onItemLongDragClick childViewHolder is null.");
            return;
        }
        DragInfo dragInfo = new DragInfo();
        dragInfo.setPageIndex(c5272b.getPageIndex());
        dragInfo.setItemId(c5272b.getItemId());
        this.mDragManager.startDragAndDrop(view, dragInfo);
        LogUtils.i(TAG, "onItemLongDragClick dragInfo" + dragInfo);
        if (recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyItemChanged(recyclerView.getChildAdapterPosition(view));
        }
    }
}
