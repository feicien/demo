package com.feicien.viewpager.demo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.feicien.viewpager.demo.R;
import com.feicien.viewpager.demo.bean.AppIconInfo;
import com.feicien.viewpager.demo.drag.DragListenerDispatcher;
import com.feicien.viewpager.demo.utils.LogUtils;

import java.util.List;


public class GridPagerAdapter extends BaseDragPageAdapter {
    private static final String TAG = "GridPagerAdapter";

    public GridPagerAdapter(Context context, List<AppIconInfo> list, DragListenerDispatcher<ViewPager> dragListenerDispatcher, int row, int column) {
        super(context, list, dragListenerDispatcher, row, column);
    }

    @Override 
    public GridRecycleAdapter generateItemAdapter(Context context, List list, int i) {
        return new MyGridRecyclerAdapter(context, list, i);
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
            GridPagerAdapter.this.setLongClickDrag(viewHolder.itemView);
            AppIconInfo appIconInfo = this.data.get(absoluteAdapterPosition);
            if (TextUtils.isEmpty(appIconInfo.packageName)) {
                LogUtils.d(GridPagerAdapter.TAG, "onItemLongClick getName is null. ");
            }
        }
    }

    
    public static class ViewHolder extends BaseViewHolder {
        private TextView mAppNameView;
        private ImageView mImageView;

        ViewHolder(View view, int pageIndex, int viewType) {
            super(view, pageIndex);
            if (view == null) {
                LogUtils.d(GridPagerAdapter.TAG, "ItemViewHolder itemView is null. ");
                return;
            }
            this.mAppNameView = (TextView) view.findViewById(R.id.item_app_name);
            this.mImageView = (ImageView) view.findViewById(R.id.mobile_add_icon);
        }
    }
}
