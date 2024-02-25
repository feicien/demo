package com.feicien.viewpager.demo.drag;

import android.graphics.PointF;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.feicien.viewpager.demo.adapter.DragNotifier;
import com.feicien.viewpager.demo.adapter.OnRecyclerItemClickListener;
import com.feicien.viewpager.demo.utils.LogUtils;

import java.util.Objects;


public class RecyclerDragListenerImp extends DragListenerDispatcher<RecyclerView> {
    private static final String TAG = "RecyclerDragListenerImp";
    private final DragNotifier mDragNotifier;
    private long mDraggingId;
    private int mScrollState;
    private final PointF point;

    public RecyclerDragListenerImp(RecyclerView recyclerView, DragNotifier dragNotifier) {
        super(recyclerView);
        this.point = new PointF(Float.MIN_VALUE, Float.MIN_VALUE);
        this.mDraggingId = -1L;
        this.mScrollState = 0;
        this.mDragNotifier = dragNotifier;
        recyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerView) {
            @Override 
            public void onItemClick(RecyclerView.ViewHolder viewHolder) {
                if (RecyclerDragListenerImp.this.mDragNotifier != null) {
                    RecyclerDragListenerImp.this.mDragNotifier.onItemClick(viewHolder);
                }
            }

            @Override 
            public void onItemLongClick(RecyclerView.ViewHolder viewHolder) {
                LogUtils.i(RecyclerDragListenerImp.TAG, "onItemLongClick position ");
                if (RecyclerDragListenerImp.this.mDragNotifier != null) {
                    RecyclerDragListenerImp.this.mDragNotifier.onItemLongClick(viewHolder);
                }
            }
        });
        recyclerView.addOnScrollListener(new AnonymousClass2());
    }

    
    
    
    public class AnonymousClass2 extends RecyclerView.OnScrollListener {
        AnonymousClass2() {
        }

        @Override 
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            RecyclerDragListenerImp.this.mScrollState = newState;
            if (newState != 0) {
                LogUtils.i(RecyclerDragListenerImp.TAG, "onScrollStateChanged default ");
            } else {
                RecyclerDragListenerImp.this.clearRecyclerMove();
            }
        }

        
        
        public  void m45xb48ea634() {
            RecyclerDragListenerImp.this.clearRecyclerMove();
        }

        @Override 
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            recyclerView.post(new Runnable() { 
                @Override 
                public final void run() {
                    AnonymousClass2.this.m45xb48ea634();
                }
            });
        }
    }

    public void clearRecyclerMove() {
        if (this.mScrollState != 0) {
            return;
        }
        clearMove();
    }

    
    public void m44x78cdec27(long j, RecyclerView recyclerView) {
        if (this.point.equals(Float.MIN_VALUE, Float.MIN_VALUE)) {
            LogUtils.i(TAG, "onAnimationsFinished nextMoveTouchPoint is null.");
            return;
        }
        final int positionForId = this.mDragNotifier.getPositionForId(j);
        PointF pointF = this.point;
        View findChildViewUnder = recyclerView.findChildViewUnder(pointF.x, pointF.y);
        if (findChildViewUnder == null) {
            LogUtils.d(TAG, "onAnimationsFinished child is null.");
            return;
        }
        final int adapterPosition = recyclerView.getChildViewHolder(findChildViewUnder).getAbsoluteAdapterPosition();
        if (adapterPosition >= 0 && positionForId != adapterPosition) {
            recyclerView.post(new Runnable() { 
                @Override 
                public final void run() {
                    RecyclerDragListenerImp.this.m39xdaab959d(positionForId, adapterPosition);
                }
            });
        }
        clearMove();
    }

    
    
    public  void m39xdaab959d(int positionForId, int adapterPosition) {
        this.mDragNotifier.onMove(positionForId, adapterPosition);
    }

    @Override 
    public void onDrop(DragInfo dragInfo, RecyclerView recyclerView) {
        if (dragInfo == null) {
            LogUtils.d(TAG, "onDrop dragInfo is null ");
            return;
        }
        DragNotifier dragNotifier = this.mDragNotifier;
        if (dragNotifier != null) {
            dragNotifier.onDrop(dragInfo.getItemId(), dragInfo.getView());
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
        DragNotifier dragNotifier = this.mDragNotifier;
        if (dragNotifier != null) {
            dragNotifier.onPageTransfer(dragInfo, dragInfo2);
        }
    }

    @Override 
    public void onDragEnd(final DragInfo dragInfo, final RecyclerView recyclerView) {
        if (dragInfo != null && recyclerView != null) {
            this.mDraggingId = -1L;
            final long itemId = dragInfo.getItemId();
            if (recyclerView.getItemAnimator() != null) {
                recyclerView.getItemAnimator().isRunning(new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() { 
                    @Override 
                    public final void onAnimationsFinished() {
                        RecyclerDragListenerImp.this.m43x530891cd(itemId, recyclerView, dragInfo);
                    }
                });
                return;
            }
            return;
        }
        LogUtils.d(TAG, "onDragEnd dragInfo or recyclerView is null ");
    }

    
    
    public  void m43x530891cd(final long itemId, final RecyclerView recyclerView, final DragInfo dragInfo) {
        int positionForId = this.mDragNotifier.getPositionForId(itemId);
        RecyclerView.ViewHolder findViewHolderForItemId = recyclerView.findViewHolderForItemId(itemId);
        if (findViewHolderForItemId != null && findViewHolderForItemId.getAbsoluteAdapterPosition() != positionForId) {
            recyclerView.post(new Runnable() { 
                @Override 
                public final void run() {
                    RecyclerDragListenerImp.this.m41xb789a1cb(recyclerView, itemId, dragInfo);
                }
            });
        } else {
            recyclerView.post(new Runnable() { 
                @Override 
                public final void run() {
                    RecyclerDragListenerImp.this.m42x54919cc(itemId, dragInfo);
                }
            });
        }
    }

    
    
    public  void m41xb789a1cb(RecyclerView recyclerView, final long itemId, final DragInfo dragInfo) {
        RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if (itemAnimator != null) {
            itemAnimator.isRunning(new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() { 
                @Override 
                public final void onAnimationsFinished() {
                    RecyclerDragListenerImp.this.m40x69ca29ca(itemId, dragInfo);
                }
            });
        }
    }

    
    
    public  void m40x69ca29ca(long itemId, DragInfo dragInfo) {
        DragNotifier dragNotifier = this.mDragNotifier;
        dragNotifier.onDragEnd(dragNotifier.getPositionForId(itemId), dragInfo.getView());
        dragInfo.reset();
    }

    
    
    public  void m42x54919cc(long itemId, DragInfo dragInfo) {
        DragNotifier dragNotifier = this.mDragNotifier;
        dragNotifier.onDragEnd(dragNotifier.getPositionForId(itemId), dragInfo.getView());
        dragInfo.reset();
    }

    @Override 
    public void onDragEnter(DragInfo dragInfo, RecyclerView recyclerView) {
        if (dragInfo == null) {
            LogUtils.d(TAG, "onDragEnter dragInfo is null ");
            return;
        }
        this.mDraggingId = dragInfo.getItemId();
        DragNotifier dragNotifier = this.mDragNotifier;
        if (dragNotifier != null) {
            dragNotifier.onDragEnter(dragNotifier.getPositionForId(dragInfo.getItemId()), dragInfo.getView());
        }
    }

    @Override 
    public void onDragExit(DragInfo dragInfo, RecyclerView recyclerView) {
        if (dragInfo == null) {
            LogUtils.d(TAG, "onDragExit dragInfo is null ");
            return;
        }
        this.mDraggingId = -1L;
        DragNotifier dragNotifier = this.mDragNotifier;
        if (dragNotifier != null) {
            dragNotifier.onDragExit(dragNotifier.getPositionForId(dragInfo.getItemId()), dragInfo.getView());
        }
    }

    @Override 
    public void onDragOver(DragInfo dragInfo, final RecyclerView recyclerView) {
        if (dragInfo != null && recyclerView != null) {
            final long itemId = dragInfo.getItemId();
            float x = dragInfo.getX();
            float y = dragInfo.getY();
            int positionForId = this.mDragNotifier.getPositionForId(itemId);
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
            itemAnimator.isRunning(new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() { 
                @Override 
                public final void onAnimationsFinished() {
                    RecyclerDragListenerImp.this.m44x78cdec27(itemId, recyclerView);
                }
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
            this.mDragNotifier.onDragStart(recyclerView.findViewHolderForItemId(itemId).getAbsoluteAdapterPosition(), dragInfo.getView());
            return;
        }
        LogUtils.d(TAG, "onDragStart dragInfo or recyclerView is null ");
    }
}
