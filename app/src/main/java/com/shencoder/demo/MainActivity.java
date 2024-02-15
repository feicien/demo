package com.shencoder.demo;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.shencoder.demo.grid.PagerGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PagerGridLayoutManager.setDebug(BuildConfig.DEBUG);



        RecyclerView rv = findViewById(R.id.rv);
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.set(20, 10, 20, 10);
            }
        });
        TextView tvPagerIndex = findViewById(R.id.tvPagerIndex);
        TextView tvPagerCount = findViewById(R.id.tvPagerCount);
        final PagerGridLayoutManager layoutManager = new PagerGridLayoutManager(4, 3);


        layoutManager.setPagerChangedListener(new PagerGridLayoutManager.PagerChangedListener() {
            @Override
            public void onPagerCountChanged(int pagerCount) {
                Log.w(TAG, "onPagerCountChanged-pagerCount:" + pagerCount);
                tvPagerCount.setText(String.valueOf(pagerCount));
            }

            @Override
            public void onPagerIndexSelected(int prePagerIndex, int currentPagerIndex) {
                tvPagerIndex.setText(currentPagerIndex == PagerGridLayoutManager.NO_ITEM ? "-" : String.valueOf(currentPagerIndex + 1));
                Log.w(TAG, "onPagerIndexSelected-prePagerIndex " + prePagerIndex + ",currentPagerIndex:" + currentPagerIndex);
            }
        });

        //设置滑动每像素需要花费的时间
        layoutManager.setMillisecondPreInch(100);
        //设置最大滚动时间
        layoutManager.setMaxScrollOnFlingDuration(500);

        rv.setLayoutManager(layoutManager);

        List<TestBean> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            list.add(new TestBean(i, String.valueOf(i)));
        }

        TestAdapter adapter = new TestAdapter(list);
        rv.setAdapter(adapter);

//        adapter.setOnItemClickListener((adapter1, view1, position) -> {
//            Toast.makeText(this, "点击了位置：" + position, Toast.LENGTH_SHORT).show();
//        });
//        //长按删除数据
//        adapter.setOnItemLongClickListener((adapter12, view12, position) -> {
//            Toast.makeText(this, "删除了位置：" + position, Toast.LENGTH_SHORT).show();
//            adapter12.removeAt(position);
//            return true;
//        });


    }
}