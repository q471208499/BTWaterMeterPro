package cn.cb.btwatermeterpro.activity;

import android.os.Bundle;

import cn.cb.baselibrary.activity.BaseActivity;
import cn.cb.btwatermeterpro.R;

public class DownloadActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initBarView();
    }
}