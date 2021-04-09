package cn.cb.btwatermeterpro.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import cn.cb.baselibrary.activity.BaseActivity;
import cn.cb.btwatermeterpro.R;

public class SettingActivity extends BaseActivity {

    private EditText meterAddressSrc, meterAddressTarget, initNumber, send, time, indexCode, version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initBarView();
        bindView();
        initEdit();
    }

    private void initEdit() {
        meterAddressSrc.setText("");
        meterAddressTarget.setText("");
        initNumber.setText("");
        send.setText("");
        time.setText("");
        indexCode.setText("");
        version.setText("");
    }

    private void bindView() {
        meterAddressSrc = findViewById(R.id.setting_meter_address_src_value);
        meterAddressTarget = findViewById(R.id.setting_meter_address_target_value);
        initNumber = findViewById(R.id.setting_init_number_value);
        send = findViewById(R.id.setting_send_value);
        time = findViewById(R.id.setting_time_value);
        indexCode = findViewById(R.id.setting_index_code_value);
        version = findViewById(R.id.setting_version_value);

        findViewById(R.id.setting_btn_read).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_set).setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = v -> {
        if (v.getId() == R.id.setting_btn_read) {

        } else if (v.getId() == R.id.setting_btn_set) {

        }
    };
}