package cn.cb.btwatermeterpro.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.clj.fastble.project.blepro.BleProDevice;

import cn.cb.btwatermeterpro.R;
import cn.cb.btwatermeterpro.activity.base.BleConnectBaseActivity;
import cn.wch.blelib.WCHBluetoothManager;
import cn.wch.blelib.exception.BLELibException;

public class SettingActivity extends BleConnectBaseActivity {

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
            startScan();
        } else if (v.getId() == R.id.setting_btn_set) {

        }
    };

    @Override
    protected void onDestroy() {
        stopScan();
        try {
            WCHBluetoothManager.getInstance().disconnect(true);
        } catch (BLELibException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    protected void onConnectSuccess() {
        meterAddressSrc.setText(address.replace(":", ""));
        BleProDevice.getInfoData();
        //LogUtil.d("onConnectSuccess: " + serviceList.size());
        //连接成功，随后进行枚举服务
    }
}