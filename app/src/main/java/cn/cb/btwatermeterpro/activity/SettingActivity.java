package cn.cb.btwatermeterpro.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.clj.fastble.project.blepro.BleProDevice;
import com.clj.fastble.project.blepro.BleProSend;
import com.clj.fastble.utils.HexUtil;

import cn.cb.baselibrary.utils.ABTextUtils;
import cn.cb.btwatermeterpro.R;
import cn.cb.btwatermeterpro.activity.base.BleConnectBaseActivity;
import cn.wch.blelib.WCHBluetoothManager;
import cn.wch.blelib.exception.BLELibException;
import es.dmoral.toasty.MyToast;

public class SettingActivity extends BleConnectBaseActivity {

    private EditText meterAddressSrc, meterAddressTarget, initNumber, send, time, indexCode, version;
    private TextView log;

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
        log = findViewById(R.id.setting_log);
        meterAddressTarget = findViewById(R.id.setting_meter_address_target_value);
        initNumber = findViewById(R.id.setting_init_number_value);
        send = findViewById(R.id.setting_send_value);
        time = findViewById(R.id.setting_time_value);
        indexCode = findViewById(R.id.setting_index_code_value);
        version = findViewById(R.id.setting_version_value);

        findViewById(R.id.setting_btn_read).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_set).setOnClickListener(clickListener);

        findViewById(R.id.setting_btn_connect).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_mut).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_service).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_notify).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_write).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_set_test).setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = v -> {
        if (v.getId() == R.id.setting_btn_read) {
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    runOnUiThread(() -> {
                        setMtu(100);
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(2000);
                    runOnUiThread(this::startTest);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(2000);
                    runOnUiThread(this::enableNotify);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(2000);
                    runOnUiThread(() -> {
                        byte[] b = BleProDevice.getInfoData();
                        write(b);
                        setLog("发：", b);
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } else if (v.getId() == R.id.setting_btn_set) {
            startTest();
        } else if (v.getId() == R.id.setting_btn_connect) {
            startScan();
        } else if (v.getId() == R.id.setting_btn_mut) {
            setMtu(100);
        } else if (v.getId() == R.id.setting_btn_service) {
            startTest();
        } else if (v.getId() == R.id.setting_btn_notify) {
            enableNotify();
        } else if (v.getId() == R.id.setting_btn_write) {
            byte[] b = BleProDevice.getInfoData();
            write(b);
            setLog("发：", b);
        } else if (v.getId() == R.id.setting_btn_set_test) {
            if (ABTextUtils.isEmpty(initNumber, send, time)) {
                MyToast.show(R.string.toast_complete);
                return;
            }
            String target = meterAddressTarget.getText().toString();
            int initNum = Integer.parseInt(initNumber.getText().toString());
            int sendInt = Integer.parseInt(send.getText().toString());
            int timeInt = Integer.parseInt(time.getText().toString());
            byte[] bytes = BleProSend.getSettingData(target, initNum, sendInt, timeInt);
            write(bytes);
            setLog("发：", bytes);
        }
    };

    @Override
    protected void onDiscoverService() {
        /*setMtu(100);
        getDeviceInfo();*/
    }

    private void getDeviceInfo() {
        read();
        byte[] b = BleProDevice.getInfoData();
        System.out.println("### write: " + HexUtil.formatHexString(b, true));
        write(b);
        setLog("发：", b);
    }

    @Override
    protected void onConnectSuccess() {
        meterAddressSrc.setText(address.replace(":", ""));
        //连接成功，随后进行枚举服务
    }

    @Override
    protected void updateReadValue(byte[] data) {
        setLog("收：", data);
    }

    private void setLog(String flag, byte[] data) {
        String logStr = HexUtil.formatHexString(data, true);
        String textStr = log.getText().toString();
        if (logStr == null)
            logStr = "";
        StringBuilder sb = new StringBuilder();
        sb.append(textStr).append(flag + logStr).append("\n");
        System.out.println("### updateReadValue: " + sb.toString());
        runOnUiThread(() -> {
            log.setText(sb.toString());
        });
    }
}