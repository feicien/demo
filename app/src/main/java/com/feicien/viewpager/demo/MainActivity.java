package com.feicien.viewpager.demo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.feicien.viewpager.demo.adapter.GridPagerAdapter;
import com.feicien.viewpager.demo.bean.AppIconInfo;
import com.feicien.viewpager.demo.drag.ViewPagerDragListenerImp;
import com.feicien.viewpager.demo.widget.RecyclerViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerViewPager recyclerViewPager = findViewById(R.id.viewpager);
        List<AppIconInfo> list = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            list.add(new AppIconInfo(String.valueOf(i)));
        }

        list.add(list.size(), new AppIconInfo("添加"));


        ViewPagerDragListenerImp viewPagerDragListenerImp = new ViewPagerDragListenerImp(recyclerViewPager);
        GridPagerAdapter mGridPagerAdapter = new GridPagerAdapter(this, list, viewPagerDragListenerImp,  4, 3);
        recyclerViewPager.setOffscreenPageLimit(0);
        recyclerViewPager.setAdapter(mGridPagerAdapter);
        recyclerViewPager.setOnDragListener(viewPagerDragListenerImp);
    }
}
