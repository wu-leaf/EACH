package com.each.www.each.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.each.www.each.R;
import com.each.www.each.services.RegisterCodeTimerService;
import com.each.www.each.utils.ToastUtil;
import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;

public class RegisterActivity extends AppCompatActivity {
    private Context mContext;
    private EditText nickName;
    private EditText phoneNumber;
    private String authCode;
    private EditText Et_authCode;  //手动输入的
    private EditText passWord;
    private Button btnGetAuthCode;
    private Intent mIntent;
    private Button btnActionRegister;
    // 广播接收者
    private final BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case RegisterCodeTimerService.IN_RUNNING:
                    if (btnGetAuthCode.isEnabled())
                        btnGetAuthCode.setEnabled(false);
                    // 正在倒计时
                    btnGetAuthCode.setText("获取验证码(" + intent.getStringExtra("time") + ")");
                    Log.e("TAG", "倒计时中(" + intent.getStringExtra("time") + ")");
                    break;
                case RegisterCodeTimerService.END_RUNNING:
                    // 完成倒计时
                    btnGetAuthCode.setEnabled(true);
                    btnGetAuthCode.setText("获取验证码");

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = getApplicationContext();
        BmobSMS.initialize(mContext, "5af10a3c714ada947f17eb3022f444b2");
        initViews();
    }
    private void initViews() {
          mIntent = new Intent(mContext, RegisterCodeTimerService.class);
          nickName = (EditText)findViewById(R.id.register_nickname);
          phoneNumber = (EditText)findViewById(R.id.register_phone);
          Et_authCode = (EditText)findViewById(R.id.register_authcode);
          passWord = (EditText)findViewById(R.id.register_password);
          btnActionRegister = (Button)findViewById(R.id.register_in_button);
          btnGetAuthCode = (Button)findViewById(R.id.getAuthCode);
          btnGetAuthCode.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  // 将按钮设置为不可用状态
                  btnGetAuthCode.setEnabled(false);
                  // 启动倒计时的服务
                  startService(mIntent);
                  // 通过requestSMSCode方式给绑定手机号的该用户发送指定短信模板的短信验证码
                  BmobSMS.requestSMSCode(mContext, phoneNumber.getText().toString(), "天才", new RequestSMSCodeListener() {
                      @Override
                      public void done(Integer smsId, BmobException ex) {
                          if (ex == null) {//验证码发送成功
                              Log.e("bmob", "短信id：" + smsId);//用于查询本次短信发送详情
                          }
                      }
                  });
              }
          });
        btnActionRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = Et_authCode.getText().toString();
                if (!TextUtils.isEmpty(number)) {
                    //通过verifySmsCode方式可验证该短信验证码
                    BmobSMS.verifySmsCode(mContext, phoneNumber.getText().toString(), number, new VerifySMSCodeListener() {
                        @Override
                        public void done(BmobException ex) {
                            if (ex == null) {//短信验证码已验证成功
                                Log.e("bmob", "验证通过");
                            } else {
                                Log.e("bmob", "验证失败：code =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 注册广播
        registerReceiver(mUpdateReceiver, updateIntentFilter());
    }
    @Override
    protected void onPause() {
        super.onPause();
        // 移除注册
        unregisterReceiver(mUpdateReceiver);
    }
    // 注册广播
    private static IntentFilter updateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RegisterCodeTimerService.IN_RUNNING);
        intentFilter.addAction(RegisterCodeTimerService.END_RUNNING);
        return intentFilter;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
