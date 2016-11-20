package com.each.www.each.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.each.www.each.R;
import com.each.www.each.utils.ActivityCollector;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private long exitTime = 0;
    private Button login;
    private Button register;
    private Button full;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = (Button)findViewById(R.id.login_main);
        login.setOnClickListener(this);

        register = (Button)findViewById(R.id.register_main);
        register.setOnClickListener(this);

        full = (Button)findViewById(R.id.fullscreen_main);
        full.setOnClickListener(this);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //强制竖屏，不得横屏
        if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    //程序退出提示

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_main:
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                break;
            case R.id.register_main:
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                break;
            case R.id.fullscreen_main:
                startActivity(new Intent(MainActivity.this,FullscreenActivity.class));
        }
    }
}
