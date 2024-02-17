package com.shencoder.demo;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.shencoder.demo.grid.PagerGridLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private TestAdapter mAdapter;
    private PagerGridLayoutManager mLayoutManager;

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
        mLayoutManager = new PagerGridLayoutManager(4, 3);

        mLayoutManager.setPagerChangedListener(new PagerGridLayoutManager.PagerChangedListener() {
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
        mLayoutManager.setMillisecondPreInch(100);
        //设置最大滚动时间
        mLayoutManager.setMaxScrollOnFlingDuration(500);

        rv.setLayoutManager(mLayoutManager);

        List<TestBean> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            list.add(new TestBean(i, String.valueOf(i)));
        }

        mAdapter = new TestAdapter(list);
        rv.setAdapter(mAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rv);

    }


    ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
        @Override
        public boolean isLongPressDragEnabled() {
            // 启用长按拖拽
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            // 如果你还想启用滑动删除，可以在这里返回true
            return false;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            // 设置拖拽方向为上下左右
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            return makeMovementFlags(dragFlags, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            // 通知Adapter有项目移动了
            final int fromPosition = viewHolder.getAdapterPosition();
            final int toPosition = target.getAdapterPosition();
            Collections.swap(mAdapter.getList(), fromPosition, toPosition);
            mAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // 这里处理滑动删除，我们不需要这个功能，所以留空
        }


        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && isCurrentlyActive) {

                // 计算item左边缘和右边缘的位置（相对于RecyclerView）
                int itemLeftEdge = viewHolder.itemView.getLeft() + (int) dX;
                int itemRightEdge = viewHolder.itemView.getRight() + (int) dX;

                // 获取RecyclerView的边界信息
                Rect bounds = new Rect();
                recyclerView.getDrawingRect(bounds);

                int threshold = 0;


                // 判断是否接近屏幕边缘
                if (itemLeftEdge < bounds.left - threshold) {
                    // 在左边缘附近
                    Log.d(TAG, "left111 ------ itemLeftEdge: " + itemLeftEdge + " bounds.left: " + bounds.left);
//                    mLayoutManager.scrollToPre();
//                    mLayoutManager.scrollToPrePager();
                    mLayoutManager.smoothScrollToPrePager();
                    Log.d(TAG, "left222 ------ itemLeftEdge: " + itemLeftEdge + " bounds.left: " + bounds.left);

                } else if (itemRightEdge > bounds.right + threshold) {
                    // 在右边缘附近

                    Log.d(TAG, "right111 ----- itemRightEdge: " + itemRightEdge + " bounds.right: " + bounds.right);
//                    mLayoutManager.scrollToNext();
//                    mLayoutManager.scrollToNextPager();
                    mLayoutManager.smoothScrollToNextPager();
                    Log.d(TAG, "right222 ----- itemRightEdge: " + itemRightEdge + " bounds.right: " + bounds.right);
                }
            }
        }
    };

}