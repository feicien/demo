package com.feicien.viewpager.demo.drag;

import android.content.ClipData;
import android.graphics.Point;
import android.util.SparseArray;
import android.view.View;

import com.feicien.viewpager.demo.utils.LogUtils;


public class DragManager<V extends View> {
    private static final String TAG = "DragManager";
    private final SparseArray<DragListenerDispatcher<V>> mListeners = new SparseArray<>();

    public void addDragListener(int pageIndex, DragListenerDispatcher<V> dragListenerDispatcher) {
        this.mListeners.put(pageIndex, dragListenerDispatcher);
    }

    public void removeAllListener() {
        this.mListeners.clear();
    }

    public DragListenerDispatcher<V> getDragListener(int pageIndex) {
        return this.mListeners.get(pageIndex);
    }

    public long getDraggingId(int pageIndex) {
        DragListenerDispatcher<V> dragListener = getDragListener(pageIndex);
        if (dragListener == null) {
            return -1L;
        }
        return dragListener.getDraggingId();
    }

    public void removeDragListener(int i) {
        this.mListeners.remove(i);
    }

    public void startDragAndDrop(View view, DragInfo dragInfo) {
        if (view == null || dragInfo == null) {
            return;
        }
        startDragAndDrop(view, dragInfo, new View.DragShadowBuilder(view));
    }

    private void startDragAndDrop(View view, DragInfo dragInfo, View.DragShadowBuilder dragShadowBuilder) {
        try {
            Point outShadowSize = new Point();
            Point outShadowTouchPoint = new Point();
            dragShadowBuilder.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint);
            dragInfo.setOutShadowSize(outShadowSize);
            dragInfo.setOutShadowTouchPoint(outShadowTouchPoint);
            dragInfo.setView(view);
            view.startDragAndDrop(ClipData.newPlainText("", ""), dragShadowBuilder, dragInfo, 256);
        } catch (IllegalStateException e) {
            LogUtils.e(TAG, "startDrag startDragAndDrop Exception");
        }
    }
}
