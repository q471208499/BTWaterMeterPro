package cn.cb.btwatermeterpro.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.clj.fastble.project.blepro.BleProDevice;
import com.clj.fastble.project.blepro.BleProSend;
import com.clj.fastble.utils.HexUtil;

import cn.cb.baselibrary.utils.ABTextUtils;
import cn.cb.btwatermeterpro.BTApplication;
import cn.cb.btwatermeterpro.R;
import cn.cb.btwatermeterpro.activity.base.BleConnectBaseActivity;
import es.dmoral.toasty.MyToast;

public class SettingActivity extends BleConnectBaseActivity {

    private EditText meterAddressSrc, meterAddressTarget, initNumber, send, time, indexCode, softwareDate, hardwareDate;
    private TextView log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initBarView();
        bindView();
        initEdit();
    }

    @Override
    protected void startTask() {

    }

    @Override
    protected void allReady() {
        byte[] b = BleProDevice.getInfoData();
        write(b);
        setLog("发：", b);
        showLoading();
    }

    private void initEdit() {
        meterAddressSrc.setText(BTApplication.getConnectAddress() == null ? "" : BTApplication.getConnectAddress().replaceAll(":", ""));
        meterAddressTarget.setText("");
        initNumber.setText("");
        send.setText("");
        time.setText("");
        indexCode.setText("");
        softwareDate.setText("");
        hardwareDate.setText("");
    }

    private void bindView() {
        meterAddressSrc = findViewById(R.id.setting_meter_address_src_value);
        log = findViewById(R.id.setting_log);
        meterAddressTarget = findViewById(R.id.setting_meter_address_target_value);
        initNumber = findViewById(R.id.setting_init_number_value);
        send = findViewById(R.id.setting_send_value);
        time = findViewById(R.id.setting_time_value);
        indexCode = findViewById(R.id.setting_index_code_value);
        softwareDate = findViewById(R.id.setting_software_value);
        hardwareDate = findViewById(R.id.setting_hardware_value);

        findViewById(R.id.setting_btn_read).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_set).setOnClickListener(clickListener);

        //===============================debug step log【Start】====================================
        findViewById(R.id.setting_btn_connect).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_mut).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_service).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_notify).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_write).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_set_test).setOnClickListener(clickListener);
        //===============================debug step log【End】====================================
    }

    private View.OnClickListener clickListener = v -> {
        if (v.getId() == R.id.setting_btn_read) {
            if (checkConnected(BTApplication.getConnectAddress())) {
                startTest();
            } else {
                startScan();
            }
        } else if (v.getId() == R.id.setting_btn_set) {
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
            showLoading();
        }
        //===============================debug step log【Start】====================================
        else if (v.getId() == R.id.setting_btn_connect) {
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
        //===============================debug step log【End】====================================
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
        startTest();
    }

    @Override
    protected void updateReadValue(byte[] data) {
        dismissLoading();
        setLog("收：", data);
        BleProDevice.Receive receive = new BleProDevice.Receive(HexUtil.formatHexString(data));
        if (receive.isValidForCommon()) {
            indexCode.setText(receive.getDeviceId());
            softwareDate.setText(receive.getSoftwareDate());
            hardwareDate.setText(receive.getHardwareDate());
            initNumber.setText(String.valueOf(receive.getMeterNumber()));
            time.setText(String.valueOf(receive.getTime()));
            send.setText(String.valueOf(receive.getSignaling()));
        }
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