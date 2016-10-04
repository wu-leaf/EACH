package com.each.www.each.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import com.each.www.each.R;
import com.each.www.each.utils.ActivityCollector;

public class WelcomeActivity extends AppCompatActivity {
    private static final int GO_GUIDE = 1001;
    private static final int GO_HOME = 1000;
    private static final int TIME = 2000;
    private boolean isFirstIn;
    private Handler mHandler;

    public WelcomeActivity(){
        Log.e("TAG","WelcomeActivity");
        this.isFirstIn = false;
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WelcomeActivity.GO_HOME /*1000*/:
                        WelcomeActivity.this.goHome();
                        break;
                    case WelcomeActivity.GO_GUIDE /*1001*/:
                        WelcomeActivity.this.goGuide();
                        break;
                    default:
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        ActivityCollector.addActivity(this);
        init();
    }

    private void init() {
        Log.e("TAG","init");
        SharedPreferences perPreferences = getSharedPreferences("jike",WelcomeActivity.MODE_PRIVATE);
        this.isFirstIn = perPreferences.getBoolean("isFirstIn",true);
        Log.e("TAG",isFirstIn+"");
        if (this.isFirstIn) {
            this.mHandler.sendEmptyMessageDelayed(GO_GUIDE, 2000);
            SharedPreferences.Editor editor = perPreferences.edit();
            editor.putBoolean("isFirstIn", false);
            editor.commit();
            return;
        }
        this.mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
    }

    private void goHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void goGuide() {
        startActivity(new Intent(this, GuideActivity.class));
        finish();
    }

    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
