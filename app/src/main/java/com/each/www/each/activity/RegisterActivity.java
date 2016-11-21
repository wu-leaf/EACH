package com.each.www.each.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.each.www.each.R;
import com.each.www.each.entity.Test;
import com.each.www.each.services.RegisterCodeTimerService;
import com.each.www.each.utils.Constants;
import com.each.www.each.utils.MyApplication;
import com.each.www.each.utils.SPUtil;
import com.each.www.each.utils.ToastUtil;

import org.json.JSONArray;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;

public class RegisterActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor> {
    private Context mContext;
    private EditText nickName;
    private EditText phoneNumber;

    private EditText Et_authCode;  //手动输入的
    private EditText passWord;
    private Button btnGetAuthCode;
    private Intent mIntent;
    private Button btnActionRegister;
    private View mProgressView;

    private MyApplication mApplication;
    private Test mTest = new Test();
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
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
    private RegisterTask mAuthTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = getApplicationContext();
        BmobSMS.initialize(mContext, "5af10a3c714ada947f17eb3022f444b2");
        initViews();

    }
    private void initViews() {
          mProgressView = findViewById(R.id.register_progress);
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
                attemptRegister();
            }
        });
    }

    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }
      //验证输入为空，非法字符
        nickName.setError(null);
        phoneNumber.setError(null);
        Et_authCode.setError(null);
        passWord.setError(null);
        /**
         * 昵称，手机号，验证码，密码
         */
        String nickname = nickName.getText().toString();
        String phone = phoneNumber.getText().toString();
        String authcode = Et_authCode.getText().toString();
        String password = passWord.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //验证昵称
        // 检查是否有效昵称.
        if (!TextUtils.isEmpty(nickname) && !isNicknameValid(nickname)) {
            nickName.setError("昵称不合法");
            focusView = nickName;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(phone)) {
            phoneNumber.setError("手机号码不能为空");
            focusView = phoneNumber;
            cancel = true;
        } else if (!isPhoneValid(phone)) {
            phoneNumber.setError("手机号码不正确");
            focusView = phoneNumber;
            cancel = true;
        }
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passWord.setError("当前密码无效");
            focusView = passWord;
            cancel = true;
        }
        if (TextUtils.isEmpty(authcode)){
            Et_authCode.setError("验证码不能为空");
            focusView = Et_authCode;
            cancel = true;
        }
        if (mTest.getaTest()!= null){
            if (mTest.getaTest()){
                Et_authCode.setError("验证码错误");
                focusView = Et_authCode;
                cancel = true;
                // new MyApplication().setTest(true);
            }
        }



        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //通过verifySmsCode方式可验证该短信验证码
            BmobSMS.verifySmsCode(mContext, phoneNumber.getText().toString(), authcode,
                    new VerifySMSCodeListener() {
                        @Override
                        public void done(BmobException ex) {
                            if (ex == null) {//短信验证码已验证成功
                                Log.e("bmob", "验证通过");
                                //显示一个进度转,启动一个后台任务
                                //执行用户的登录尝试。
                                showProgress(true);
                               /* mAuthTask = new RegisterTask();
                                mAuthTask.execute((Void) null);*/
                            } else {
                                Log.e("bmob", "验证失败：code =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());
                                //mApplication = (MyApplication)getApplication();
                                //mApplication.setTest(false);
                                mTest.setaTest(true);
                            }
                        }
                    });
            return;
        }
    }




    private boolean isPasswordValid(String password) {
        String reg = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private boolean isPhoneValid(String phone) {
        Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
           // mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private boolean isNicknameValid(String nickname) {
        //TODO: Replace this with your own logic
        return true;
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

    private void velleyPost(final String nickname,final String phone, final String password){
        String url = Constants.REGISTER_URL;
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray array) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                //将请求参数填入map
                map.put("nickname",nickname);
                map.put("phone",phone);
                map.put("password",password);
                return map;
            }
        };
        //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
        request.setTag("testPost");
        //将请求加入全局队列中
        MyApplication.getHttpQueues().add(request);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    public class RegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mNickname;
        private final String mPassword;
        private final String mPhone;


        RegisterTask(String nickname, String phone,String password) {
            mNickname = nickname;
            mPhone = phone;
            mPassword = password;

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

        /*    try {


            } catch (InterruptedException e) {
                return false;
            }*/

         /*   for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }*/

            // TODO: register the new account here.
            velleyPost(mNickname,mPhone,mPassword);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
               // mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
