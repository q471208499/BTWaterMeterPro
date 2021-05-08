package cn.cb.btwatermeterpro.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.clj.fastble.project.blepro.BleProDevice;
import com.clj.fastble.project.blepro.BleProSend;
import com.clj.fastble.utils.HexUtil;

import java.text.DecimalFormat;

import cn.cb.baselibrary.utils.ABTextUtils;
import cn.cb.btwatermeterpro.BTApplication;
import cn.cb.btwatermeterpro.R;
import cn.cb.btwatermeterpro.activity.base.BleConnectBaseActivity;
import cn.cb.btwatermeterpro.provider.BTConstant;
import es.dmoral.toasty.MyToast;

public class SettingActivity extends BleConnectBaseActivity {

    private EditText meterAddressSrc, meterAddressTarget, initNumber, send, time, indexCode, softwareDate, hardwareDate;
    private TextView log;
    private final StringBuilder logStrBuilder = new StringBuilder();

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
        runOnUiThread(() -> {
            byte[] b = BleProDevice.getInfoData();
            write(b);
            setLog("发：", b);
        });
    }

    private void initEdit() {
        meterAddressSrc.setText(BTApplication.getConnectAddress() == null ? "" : BTApplication.getConnectAddress().replaceAll(":", ""));
        meterAddressTarget.setText(BTApplication.getConnectAddress() == null ? "" : BTApplication.getConnectAddress().replaceAll(":", ""));
        initNumber.setText("");
        send.setText("");
        time.setText("");
        indexCode.setText("");
        softwareDate.setText("");
        hardwareDate.setText("");
    }

    private void bindView() {
        meterAddressSrc = findViewById(R.id.setting_meter_address_src_value);
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
        log = findViewById(R.id.setting_log);
        findViewById(R.id.setting_btn_connect).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_mut).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_service).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_notify).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_write).setOnClickListener(clickListener);
        findViewById(R.id.setting_btn_set_test).setOnClickListener(clickListener);
        int visible = BTConstant.DEVICE_LOG ? View.VISIBLE : View.GONE;
        findViewById(R.id.setting_log).setVisibility(visible);
        findViewById(R.id.setting_btn_connect).setVisibility(visible);
        findViewById(R.id.setting_btn_mut).setVisibility(visible);
        findViewById(R.id.setting_btn_service).setVisibility(visible);
        findViewById(R.id.setting_btn_notify).setVisibility(visible);
        findViewById(R.id.setting_btn_write).setVisibility(visible);
        findViewById(R.id.setting_btn_set_test).setVisibility(visible);
        //===============================debug step log【End】====================================
    }

    private final View.OnClickListener clickListener = v -> {
        if (v.getId() == R.id.setting_btn_read) {
            if (checkConnected(BTApplication.getConnectAddress())) {
                startTest();
            } else {
                startScan();
            }
        } else if (v.getId() == R.id.setting_btn_set) {
            if (ABTextUtils.isEmpty(meterAddressTarget, initNumber, send, time)) {
                MyToast.show(R.string.toast_complete);
                return;
            }
            String target = meterAddressTarget.getText().toString();
            long initNum = getInitNumber();
            int sendInt = Integer.parseInt(send.getText().toString());
            int timeInt = Integer.parseInt(time.getText().toString());
            byte[] bytes = BleProSend.getSettingData(target, initNum, sendInt, timeInt);
            write(bytes);
            setLog("发：", bytes);
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
            if (ABTextUtils.isEmpty(meterAddressTarget, initNumber, send, time)) {
                MyToast.show(R.string.toast_complete);
                return;
            }
            String target = meterAddressTarget.getText().toString();
            long initNum = getInitNumber();
            int sendInt = Integer.parseInt(send.getText().toString());
            int timeInt = Integer.parseInt(time.getText().toString());
            byte[] bytes = BleProSend.getSettingData(target, initNum, sendInt, timeInt);
            write(bytes);
            setLog("发：", bytes);
        }
        //===============================debug step log【End】====================================
    };

    private long getInitNumber() {
        String initStr = initNumber.getText().toString();
        double d = Double.parseDouble(initStr);
        DecimalFormat df = new DecimalFormat("#.000");
        return Long.parseLong(df.format(d).replace(".", ""));
    }

    @Override
    protected void onDiscoverService() {
        /*setMtu(100);
        getDeviceInfo();*/
    }

    @Override
    protected void onConnectSuccess() {
        meterAddressSrc.setText(address.replace(":", ""));
        //连接成功，随后进行枚举服务
        MyToast.showL("连接成功，请再次获取参数！");
    }

    @Override
    protected void updateReadValue(byte[] data) {
        setLog("收：", data);
        runOnUiThread(() -> {
            BleProDevice.Receive receive = new BleProDevice.Receive(HexUtil.formatHexString(data));
            if (receive.isValidForCommon()) {
                indexCode.setText(receive.getDeviceId());
                initNumber.setText(String.valueOf(receive.getMeterNumber()));
                time.setText(String.valueOf(receive.getTime()));
                send.setText(String.valueOf(receive.getSignaling()));
                String softwareStr = String.format("20%2s.%2s",
                        receive.getSoftwareDate().substring(2, 4),
                        receive.getSoftwareDate().substring(0, 2));
                String hardwareStr = String.format("20%2s.%2s",
                        receive.getHardwareDate().substring(2, 4),
                        receive.getHardwareDate().substring(0, 2));
                softwareDate.setText(softwareStr);
                hardwareDate.setText(hardwareStr);
            }
        });
    }

    private void setLog(String flag, byte[] data) {
        String logStr = HexUtil.formatHexString(data, true);
        logStrBuilder.append(flag).append(logStr).append("\n");
        System.out.println("### updateReadValue: " + logStr);
        runOnUiThread(() -> log.setText(logStrBuilder.toString()));
    }
}