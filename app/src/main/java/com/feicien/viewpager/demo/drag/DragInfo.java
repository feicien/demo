package com.feicien.viewpager.demo.drag;

import android.graphics.Point;
import android.view.View;


public class DragInfo {
    private View view;
    private float x = -1.0f;
    private float y = -1.0f;
    private long itemId = -1;
    private int pageIndex = -1;
    private Point outShadowSize = new Point();
    private Point outShadowTouchPoint = new Point();

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public View getView() {
        return this.view;
    }

    public long getItemId() {
        return this.itemId;
    }

    public int getPageIndex() {
        return this.pageIndex;
    }

    public Point getOutShadowSize() {
        return this.outShadowSize;
    }

    public Point getOutShadowTouchPoint() {
        return this.outShadowTouchPoint;
    }

    public void reset() {
        this.x = -1.0f;
        this.y = -1.0f;
        this.itemId = -1L;
        this.pageIndex = -1;
        this.view = null;
    }

    public void setX(float f) {
        this.x = f;
    }

    public void setY(float f) {
        this.y = f;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setOutShadowSize(Point outShadowSize) {
        this.outShadowSize = outShadowSize;
    }

    public void setOutShadowTouchPoint(Point outShadowTouchPoint) {
        this.outShadowTouchPoint = outShadowTouchPoint;
    }
}
