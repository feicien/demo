package com.feicien.viewpager.demo.drag;

import android.content.ClipData;
import android.graphics.Point;
import android.util.SparseArray;
import android.view.View;

import com.feicien.viewpager.demo.utils.LogUtils;


public class DragManager {
    private static final String TAG = "DragManager";

    //静态内部类
    private static class SingletonHolder{
        private static final DragManager singleton = new DragManager();
    }
    private DragManager(){}
    public static DragManager getInstance(){
        return SingletonHolder.singleton;
    }


    private final SparseArray<RecyclerDragListenerImp> mListeners = new SparseArray<>();

    public void addDragListener(int pageIndex, RecyclerDragListenerImp dragListenerDispatcher) {
        this.mListeners.put(pageIndex, dragListenerDispatcher);
    }

    public void removeAllListener() {
        this.mListeners.clear();
    }

    public RecyclerDragListenerImp getDragListener(int pageIndex) {
        return this.mListeners.get(pageIndex);
    }

    public long getDraggingId(int pageIndex) {
        RecyclerDragListenerImp dragListener = getDragListener(pageIndex);
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
