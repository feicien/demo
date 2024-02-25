package com.feicien.viewpager.demo.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.feicien.viewpager.demo.drag.DragInfo;


public interface DragNotifier {
    int getPositionForId(long j);

    void onDragEnd(int i, View view);

    void onDragEnter(int i, View view);

    void onDragExit(int i, View view);

    void onDragStart(int i, View view);

    void onDrop(long j, View view);

    void onItemClick(RecyclerView.ViewHolder viewHolder);

    void onItemLongClick(RecyclerView.ViewHolder viewHolder);

    void onMove(int i, int i2);

    void onPageTransfer(DragInfo dragInfo, DragInfo dragInfo2);
}
