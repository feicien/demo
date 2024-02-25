package com.feicien.viewpager.demo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.feicien.viewpager.demo.bean.AppIconInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public abstract class BasePagerAdapter extends PagerAdapter {
    private final List<AppIconInfo> mAppIconData;
    protected WeakReference<Context> mContextRef;
    private final List<ViewGroup> mPagesList = new ArrayList();



    public abstract void onBindPage(Context context, RecyclerView recyclerView, int i);

    public abstract RecyclerView onCreatePage(ViewGroup viewGroup);

    public BasePagerAdapter(Context context, List<AppIconInfo> list) {
        ArrayList arrayList = new ArrayList();
        this.mAppIconData = arrayList;
        this.mContextRef = new WeakReference<>(context);
        arrayList.clear();
        arrayList.addAll(list);
    }

    private FrameLayout.LayoutParams generatePageLayoutParams() {
        return new FrameLayout.LayoutParams(-1, -1);
    }

    protected FrameLayout createPage(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.addView(onCreatePage(frameLayout), generatePageLayoutParams());
        return frameLayout;
    }

    @Override 
    public void destroyItem(ViewGroup viewGroup, int position, Object obj) {
        if (obj instanceof View) {
            viewGroup.removeView((View) obj);
        }
        if (obj instanceof ViewGroup) {
            ViewGroup viewGroup2 = (ViewGroup) obj;
            if (viewGroup2.getChildCount() > 0) {
                View childAt = viewGroup2.getChildAt(0);
                if (childAt instanceof RecyclerView) {
                    onUnbindPage((RecyclerView) childAt, position);
                }
            }
        }
    }


    protected List<AppIconInfo> getAllData() {
        return this.mAppIconData;
    }

    @Override
    public int getCount() {
        if (mPagesList.isEmpty() && (this.mAppIconData) != null && !mAppIconData.isEmpty()) {


            Context context = this.mContextRef.get();
            if (this.mContextRef != null && context != null) {

                initPages(context);
            }

        }
        return this.mPagesList.size();
    }

    @Override 
    public int getItemPosition(Object obj) {
        return -2;
    }

    public Optional<RecyclerView> getPage(int i) {
        List<ViewGroup> list = this.mPagesList;
        if (list != null && list.size() > 0) {
            View childAt = this.mPagesList.get(i).getChildAt(0);
            if (childAt instanceof RecyclerView) {
                return Optional.of((RecyclerView) childAt);
            }
        }
        return Optional.empty();
    }

    public int getPageNum() {
        return this.mAppIconData.size();
    }

    public final int getPageSize() {
        return this.mPagesList.size();
    }

    protected void initPages(Context context) {
        if (context == null) {
            return;
        }
        int pageNum = getPageNum();
        for (int i = 0; i < pageNum; i++) {
            this.mPagesList.add(createPage(context));
        }
    }

    @Override 
    public Object instantiateItem(ViewGroup viewGroup, int i) {
        if (viewGroup == null) {
            return new Object();
        }
        List<ViewGroup> list = this.mPagesList;
        if (list == null || list.isEmpty() || i < 0 || i >= this.mPagesList.size()) {
            return new Object();
        }
        ViewGroup viewGroup2 = this.mPagesList.get(i);
        if (viewGroup2.getParent() == null) {
            viewGroup.addView(viewGroup2);
        }
        View childAt = viewGroup2.getChildAt(0);
        if (childAt instanceof ViewGroup) {
            ViewGroup viewGroup3 = (ViewGroup) childAt;
            if ((childAt instanceof RecyclerView) && viewGroup3.getChildCount() == 0) {
                onBindPage(viewGroup.getContext(), (RecyclerView) childAt, i);
            }
        }
        return viewGroup2;
    }

    @Override 
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public void onUnbindPage(RecyclerView recyclerView, int position) {
    }

    public void reCreateAllPages(List<AppIconInfo> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        this.mAppIconData.clear();
        this.mPagesList.clear();
        this.mAppIconData.addAll(list);
        notifyDataSetChanged();
    }

    public void release() {
        WeakReference<Context> weakReference = this.mContextRef;
        if (weakReference != null) {
            weakReference.clear();
        }
        this.mPagesList.clear();
        notifyDataSetChanged();
    }

    public void removePage(int i) {
        if (this.mPagesList.size() <= 0 || this.mPagesList.remove(i) == null) {
            return;
        }
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.mContextRef = new WeakReference<>(context);
    }

}
