package com.feicien.viewpager.demo.drag;

import android.view.DragEvent;
import android.view.View;

import com.feicien.viewpager.demo.utils.LogUtils;

import java.lang.ref.WeakReference;


public abstract class DragListenerDispatcher<V extends View> implements View.OnDragListener {
    private static final String TAG = "DragListenerDispatcher";
    protected final WeakReference<V> mViewRef;

    public abstract boolean acceptDrop(DragInfo dragInfo, V v);

    public abstract void clearMove();

    public abstract long getDraggingId();

    public abstract void onDragEnd(DragInfo dragInfo, V v);

    public abstract void onDragEnter(DragInfo dragInfo, V v);

    public abstract void onDragExit(DragInfo dragInfo, V v);

    public abstract void onDragOver(DragInfo dragInfo, V v);

    public abstract boolean onDragPrepare(DragInfo dragInfo, V v);

    public abstract void onDragStart(DragInfo dragInfo, V v);

    public abstract void onDrop(DragInfo dragInfo, V v);

    public abstract void onPageTransfer(DragInfo dragInfo, DragInfo dragInfo2);

    public DragListenerDispatcher(V v) {
        this.mViewRef = new WeakReference<>(v);
    }


    
    @Override 
    public boolean onDrag(View view, DragEvent dragEvent) {
        if (view != null && dragEvent != null) {
            if (view == this.mViewRef.get() && (dragEvent.getLocalState() instanceof DragInfo)) {
                DragInfo dragInfo = (DragInfo) dragEvent.getLocalState();
                if (!onDragPrepare(dragInfo, (V) view)) {
                    LogUtils.d(TAG, " onDrag onDragPrepare");
                    return false;
                }
                dragInfo.setX(dragEvent.getX());
                dragInfo.setY(dragEvent.getY());
                switch (dragEvent.getAction()) {
                    case 1:
                        onDragStart(dragInfo, (V) view);
                        return true;
                    case 2:
                        onDragOver(dragInfo, (V) view);
                        return true;
                    case 3:
                        onDrop(dragInfo, (V) view);
                        return true;
                    case 4:
                        onDragEnd(dragInfo, (V) view);
                        return true;
                    case 5:
                        onDragEnter(dragInfo, (V) view);
                        return true;
                    case 6:
                        onDragExit(dragInfo, (V) view);
                        return true;
                    default:
                        return true;
                }
            }
            LogUtils.d(TAG, "onDrag view and getLocalState is null");
            return false;
        }
        LogUtils.d(TAG, "onDrag view and view or event is null");
        return false;
    }
}
