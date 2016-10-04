package com.each.www.each.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.each.www.each.R;
import com.each.www.each.adapter.ViewPagerAdapter;
import com.each.www.each.utils.ActivityCollector;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private ImageView[] dots;
    private int[] ids;
    private Button start_btn = null;
    private List<View> views;
    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    public GuideActivity() {
        this.ids = new int[]{R.id.iv_point1, R.id.iv_point2, R.id.iv_point3};
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView( R.layout.activity_guide);
        ActivityCollector.addActivity(this);
        initViews();
        initDots();
    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);
        this.views = new ArrayList();
        this.views.add(inflater.inflate(R.layout.welcome_one, null));
        this.views.add(inflater.inflate(R.layout.welcome_two, null));
        this.views.add(inflater.inflate(R.layout.welcome_three, null));
        this.vpAdapter = new ViewPagerAdapter(this.views, this);
        this.vp = (ViewPager) findViewById(R.id.viewpager);
        this.vp.setAdapter(this.vpAdapter);
        this.start_btn = (Button) ((View) this.views.get(2)).findViewById(R.id.start_btn);
        if (start_btn!=null){
            this.start_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    GuideActivity.this.startActivity(new Intent(GuideActivity.this,
                            MainActivity.class));
                    GuideActivity.this.finish();
                }
            });
        }
        this.vp.setOnPageChangeListener(this);
    }

    private void initDots() {
        this.dots = new ImageView[this.views.size()];
        for (int i = 0; i < this.views.size(); i++) {
            this.dots[i] = (ImageView) findViewById(this.ids[i]);
        }
    }

    public void onPageScrollStateChanged(int arg0) {
    }

    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    public void onPageSelected(int arg0) {
        for (int i = 0; i < this.ids.length; i++) {
            if (arg0 == i) {
                this.dots[i].setImageResource(R.drawable.login_point_selected);
            } else {
                this.dots[i].setImageResource(R.drawable.login_point);
            }
        }
    }
}

