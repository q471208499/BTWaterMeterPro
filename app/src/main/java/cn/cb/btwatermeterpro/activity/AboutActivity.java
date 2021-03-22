package cn.cb.btwatermeterpro.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import cn.cb.baselibrary.activity.BaseActivity;
import cn.cb.btwatermeterpro.BuildConfig;
import cn.cb.btwatermeterpro.R;

public class AboutActivity extends BaseActivity {

    private TextView name, version, copyright;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initBarView();
        bindView();
        initData();
    }

    private void initData() {
        name.setText(R.string.app_name);
        version.setText("v" + BuildConfig.VERSION_NAME);
        copyright.setText("Copyright© 2021 chenbo. All Rights Reserved");
    }

    private void bindView() {
        logo = findViewById(R.id.about_logo);
        name = findViewById(R.id.about_name);
        version = findViewById(R.id.about_version);
        copyright = findViewById(R.id.about_copyright);
    }
}