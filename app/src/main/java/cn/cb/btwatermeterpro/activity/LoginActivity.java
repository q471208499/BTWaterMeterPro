package cn.cb.btwatermeterpro.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import java.util.Map;

import cn.cb.baselibrary.activity.BaseActivity;
import cn.cb.baselibrary.net.Result;
import cn.cb.baselibrary.net.okhttp3.NetCallback;
import cn.cb.baselibrary.utils.ABTextUtils;
import cn.cb.baselibrary.utils.MD5;
import cn.cb.baselibrary.utils.SPUtils;
import cn.cb.btwatermeterpro.R;
import cn.cb.btwatermeterpro.net.RequestUtils;
import cn.cb.btwatermeterpro.provider.BTConstant;
import es.dmoral.toasty.MyToast;

public class LoginActivity extends BaseActivity {
    private final String TAG = getClass().getSimpleName();

    private EditText loginName, loginPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindView();
        //new AppUpdateHelper(BuildConfig.UPDATE_URL + "?" + ABTimeUtils.getCurrentTimeInString()).getUpdateInfo();
        setData();
    }

    private void setData() {
        String username = SPUtils.getInstance(BTConstant.SP_NAME).getString(BTConstant.SP_KEY_USERNAME);
        String password = SPUtils.getInstance(BTConstant.SP_NAME).getString(BTConstant.SP_KEY_PASSWORD);
        if (!TextUtils.isEmpty(username)) loginName.setText(username);
        if (!TextUtils.isEmpty(password)) loginPwd.setText(password);
        //if (ABTextUtils.isNotEmpty(loginName, loginPwd)) doLogin();
    }

    private void bindView() {
        loginName = findViewById(R.id.login_name);
        loginPwd = findViewById(R.id.login_pwd);
        findViewById(R.id.login_btn).setOnClickListener(listener);
    }

    private NetCallback callback = new NetCallback() {

        @Override
        public void onSuccess(Object responseObj) {
            dismissLoading();
            if (responseObj instanceof Result) {
                Result result = (Result) responseObj;
                MyToast.show(result.getMessage());
                if (result.isSuccess()) {
                    Map<String, Object> m = (Map<String, Object>) result.getData();
                    /*AppUser appUser = JSONObject.toJavaObject(new JSONObject(m), AppUser.class);
                    BTApplication.setAppUser(appUser);*/

                    String pwd = loginPwd.getText().toString();
                    String username = loginName.getText().toString();
                    SPUtils.getInstance(BTConstant.SP_NAME).put(BTConstant.SP_KEY_USERNAME, username);
                    SPUtils.getInstance(BTConstant.SP_NAME).put(BTConstant.SP_KEY_PASSWORD, pwd);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                MyToast.show(R.string.server_error);
            }
        }
    };

    private View.OnClickListener listener = v -> {
        if (v.getId() == R.id.login_btn) {
            doLogin();
        }
    };

    private void doLogin() {
        if (ABTextUtils.isEmpty(loginName, loginPwd)) {
            MyToast.showL(R.string.enter_complete);
            return;
        }
        showLoading();
        String pwdMd5 = new MD5().getMD5ofStr(loginPwd.getText().toString());
        RequestUtils.doLogin(loginName.getText().toString(), pwdMd5, callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showFinishDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
