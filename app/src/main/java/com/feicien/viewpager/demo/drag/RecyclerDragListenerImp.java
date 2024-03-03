package com.feicien.viewpager.demo.drag;

import android.graphics.PointF;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.feicien.viewpager.demo.adapter.MyGridRecyclerAdapter;
import com.feicien.viewpager.demo.adapter.OnRecyclerItemClickListener;
import com.feicien.viewpager.demo.utils.LogUtils;

import java.util.Objects;


public class RecyclerDragListenerImp extends DragListenerDispatcher<RecyclerView> {
    private static final String TAG = "RecyclerDragListenerImp";
    private final MyGridRecyclerAdapter mGridRecyclerAdapter;
    private long mDraggingId;
    private int mScrollState;
    private final PointF point;

    public RecyclerDragListenerImp(RecyclerView recyclerView, MyGridRecyclerAdapter adapter) {
        super(recyclerView);
        this.point = new PointF(Float.MIN_VALUE, Float.MIN_VALUE);
        this.mDraggingId = -1L;
        this.mScrollState = 0;
        this.mGridRecyclerAdapter = adapter;
        recyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerView) {
            @Override 
            public void onItemClick(RecyclerView.ViewHolder viewHolder) {
                if (mGridRecyclerAdapter != null) {
                    mGridRecyclerAdapter.onItemClick(viewHolder);
                }
            }

            @Override 
            public void onItemLongClick(RecyclerView.ViewHolder viewHolder) {
                LogUtils.i(TAG, "onItemLongClick position ");
                if (mGridRecyclerAdapter != null) {
                    mGridRecyclerAdapter.onItemLongClick(viewHolder);
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                mScrollState = newState;
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    LogUtils.i(TAG, "onScrollStateChanged default ");
                } else {
                    clearMove();
                }
            }


            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                recyclerView.post(() -> {
                    if (mScrollState != RecyclerView.SCROLL_STATE_IDLE) {
                        return;
                    }
                    clearMove();
                });
            }
        });
    }

    

    @Override 
    public void onDrop(DragInfo dragInfo, RecyclerView recyclerView) {
        if (dragInfo == null) {
            LogUtils.d(TAG, "onDrop dragInfo is null ");
        }
    }

    @Override 
    public void clearMove() {
        this.point.set(Float.MIN_VALUE, Float.MIN_VALUE);
    }

    @Override 
    public long getDraggingId() {
        return this.mDraggingId;
    }

    @Override 
    public boolean acceptDrop(DragInfo dragInfo, RecyclerView recyclerView) {
        return true;
    }

    @Override 
    public void onPageTransfer(DragInfo dragInfo, DragInfo dragInfo2) {
        if (mGridRecyclerAdapter != null) {
            mGridRecyclerAdapter.onPageTransfer(dragInfo, dragInfo2);
        }
    }

    @Override 
    public void onDragEnd(final DragInfo dragInfo, final RecyclerView recyclerView) {
        if (dragInfo != null && recyclerView != null) {
            this.mDraggingId = -1L;
            final long itemId = dragInfo.getItemId();
            RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
            if (itemAnimator != null) {
                itemAnimator.isRunning(() -> {
                    int position = mGridRecyclerAdapter.getPositionForId(itemId);
                    mGridRecyclerAdapter.notifyItemChanged(position);
                    dragInfo.reset();
                });
                return;
            }
            return;
        }
        LogUtils.d(TAG, "onDragEnd dragInfo or recyclerView is null ");
    }

    
    

    @Override 
    public void onDragEnter(DragInfo dragInfo, RecyclerView recyclerView) {
        if (dragInfo == null) {
            LogUtils.d(TAG, "onDragEnter dragInfo is null ");
            return;
        }
        this.mDraggingId = dragInfo.getItemId();
        if (mGridRecyclerAdapter != null) {
            int position = mGridRecyclerAdapter.getPositionForId(dragInfo.getItemId());
            mGridRecyclerAdapter.notifyItemChanged(position);
        }
    }

    @Override 
    public void onDragExit(DragInfo dragInfo, RecyclerView recyclerView) {
        if (dragInfo == null) {
            LogUtils.d(TAG, "onDragExit dragInfo is null ");
            return;
        }
        this.mDraggingId = -1L;
        if (mGridRecyclerAdapter != null) {
            int position = mGridRecyclerAdapter.getPositionForId(dragInfo.getItemId());
            mGridRecyclerAdapter.notifyItemChanged(position);
        }
    }

    @Override 
    public void onDragOver(DragInfo dragInfo, final RecyclerView recyclerView) {
        if (dragInfo != null && recyclerView != null) {
            final long itemId = dragInfo.getItemId();
            float x = dragInfo.getX();
            float y = dragInfo.getY();
            int positionForId = this.mGridRecyclerAdapter.getPositionForId(itemId);
            View findChildViewUnder = recyclerView.findChildViewUnder(x, y);
            int adapterPosition = findChildViewUnder != null ? recyclerView.getChildViewHolder(findChildViewUnder).getAbsoluteAdapterPosition() : -1;
            if (adapterPosition < 0 || positionForId == adapterPosition) {
                return;
            }
            RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
            boolean equals = this.point.equals(Float.MIN_VALUE, Float.MIN_VALUE);
            this.point.set(x, y);
            if (!equals || itemAnimator == null) {
                return;
            }
            itemAnimator.isRunning(() -> {
                if (point.equals(Float.MIN_VALUE, Float.MIN_VALUE)) {
                    LogUtils.i(TAG, "onAnimationsFinished nextMoveTouchPoint is null.");
                    return;
                }
                final int positionForId1 = mGridRecyclerAdapter.getPositionForId(itemId);
                View findChildViewUnder1 = recyclerView.findChildViewUnder(point.x, point.y);
                if (findChildViewUnder1 == null) {
                    LogUtils.d(TAG, "onAnimationsFinished child is null.");
                    return;
                }
                final int adapterPosition1 = recyclerView.getChildViewHolder(findChildViewUnder1).getAbsoluteAdapterPosition();
                if (adapterPosition1 >= 0 && positionForId1 != adapterPosition1) {
                    recyclerView.post(() -> mGridRecyclerAdapter.onMove(positionForId1, adapterPosition1));
                }
                clearMove();
            });
            return;
        }
        LogUtils.d(TAG, "onDragOver dragInfo or recyclerView is null ");
    }

    @Override 
    public boolean onDragPrepare(DragInfo dragInfo, RecyclerView recyclerView) {
        return Objects.equals(recyclerView.getTag(), Integer.valueOf(dragInfo.getPageIndex()));
    }

    @Override 
    public void onDragStart(DragInfo dragInfo, RecyclerView recyclerView) {
        if (dragInfo != null && recyclerView != null) {
            long itemId = dragInfo.getItemId();
            this.mDraggingId = itemId;
            int position = recyclerView.findViewHolderForItemId(itemId).getAbsoluteAdapterPosition();
            this.mGridRecyclerAdapter.notifyItemChanged(position);
            return;
        }
        LogUtils.d(TAG, "onDragStart dragInfo or recyclerView is null ");
    }
}
