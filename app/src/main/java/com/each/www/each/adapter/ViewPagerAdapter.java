package com.each.www.each.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private List<View> views;

    public ViewPagerAdapter(List<View> views, Context context) {
        this.views = views;
        this.context = context;
    }

    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) this.views.get(position));
    }

    public Object instantiateItem(View container, int position) {
        ((ViewPager) container).addView((View) this.views.get(position));
        return this.views.get(position);
    }

    public int getCount() {
        return this.views.size();
    }

    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
}
