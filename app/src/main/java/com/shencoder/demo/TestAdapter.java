package com.shencoder.demo;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {
    public static final String TAG = "TestAdapter";
    private final List<TestBean> mList;

    public TestAdapter(List<TestBean> list) {
        this.mList = list;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestBean testBean = mList.get(position);
        holder.myTextView.setText(testBean.name);
    }

    public List<TestBean> getList() {
        return mList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView myTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvItem);
        }
    }


}
