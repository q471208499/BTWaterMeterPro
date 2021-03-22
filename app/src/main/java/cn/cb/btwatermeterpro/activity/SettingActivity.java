package cn.cb.btwatermeterpro.activity;

import android.os.Bundle;

import cn.cb.baselibrary.activity.BaseActivity;
import cn.cb.btwatermeterpro.R;

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initBarView();
    }
}