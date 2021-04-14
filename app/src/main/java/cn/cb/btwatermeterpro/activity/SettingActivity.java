package cn.cb.btwatermeterpro.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.UUID;

import cn.cb.btwatermeterpro.R;
import cn.cb.btwatermeterpro.activity.base.SettingBaseActivity;
import cn.cb.btwatermeterpro.ui.DeviceListDialog;
import cn.cb.btwatermeterpro.ui.DialogUtil;
import cn.wch.blelib.WCHBluetoothManager;
import cn.wch.blelib.exception.BLELibException;
import cn.wch.blelib.host.core.callback.NotifyDataCallback;
import cn.wch.blelib.host.scan.ScanObserver;
import cn.wch.blelib.utils.Location;
import cn.wch.blelib.utils.LogUtil;
import es.dmoral.toasty.MyToast;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SettingActivity extends SettingBaseActivity {

    private EditText meterAddressSrc, meterAddressTarget, initNumber, send, time, indexCode, version;

    private boolean isConnected = false;

    private List<BluetoothGattService> serviceList;

    private BluetoothGattCharacteristic currentCharacteristic;

    private DeviceListDialog deviceListDialog;

    private int count_R = 0;
    private int count_W = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBarView();
        bindView();
        initEdit();
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void onActivitySomeResult(int requestCode, int resultCode, @Nullable Intent data) {

    }

    @Override
    protected void onConnecting() {
        isConnected = false;
        runOnUiThread(() -> DialogUtil.getInstance().showLoadingDialog(SettingActivity.this, "正在连接..."));
    }

    @Override
    protected void onDiscoverService(List<BluetoothGattService> services) {
        LogUtil.d("onDiscoverService: " + services.size());
        serviceList = services;
        isConnected = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isConnected = true;
                DialogUtil.getInstance().hideLoadingDialog();
                invalidateOptionsMenu();
                //initServiceSpinner(serviceList);
                //enableWidget(true);
            }
        });
    }

    @Override
    protected void onConnectSuccess() {
        meterAddressSrc.setText(address.replace(":", ""));
        //LogUtil.d("onConnectSuccess: " + serviceList.size());
        //连接成功，随后进行枚举服务
    }

    @Override
    protected void onConnectError(String message) {
        isConnected = false;
        runOnUiThread(() -> DialogUtil.getInstance().hideLoadingDialog());
    }

    @Override
    protected void onDisconnect() {
        isConnected = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                invalidateOptionsMenu();
                //enableWidget(false);
                //enableButtons(false);
                DialogUtil.getInstance().hideLoadingDialog();
                //resetWidget();
                stopCurrentChar();
            }
        });
    }

    private void stopCurrentChar() {
        LogUtil.d("stop current char");
        if (currentCharacteristic == null) {
            return;
        }
        //sbNotify.setOnCheckedChangeListener(null);
        //sbNotify.setChecked(false);
        if ((currentCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
            try {
                if (WCHBluetoothManager.getInstance().getNotifyState(currentCharacteristic)) {
                    WCHBluetoothManager.getInstance().openNotify(currentCharacteristic, null);
                }
            } catch (BLELibException e) {
                e.printStackTrace();
                LogUtil.d(e.getMessage());
            }
        }
        currentCharacteristic = null;
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

    private void startScan() {
        //init transitionRunnable

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            //大于安卓10，需要检查定位服务
            LogUtil.d("位置服务打开：" + Location.isLocationEnable(this));
            if (!Location.isLocationEnable(this)) {
                DialogUtil.getInstance().showSimpleDialog(this, "蓝牙扫描需要开启位置信息服务", new DialogUtil.IResult() {
                    @Override
                    public void onContinue() {
                        Location.requestLocationService(SettingActivity.this);
                    }

                    @Override
                    public void onCancel() {

                    }
                });

                return;
            }
        }

        if (!checkBtEnabled()) {
            MyToast.show("请先打开蓝牙");
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            MyToast.show("定位权限未开启");
            return;
        }
        try {
            WCHBluetoothManager.getInstance().startScan(null, scanObserver);
            //WCHBluetoothManager.getInstance().startScanCH914X(scanObserver);
        } catch (BLELibException e) {
            e.printStackTrace();
        }
        showDeviceList();
    }

    private void showDeviceList() {
        //展示设备列表并且更新信息
        deviceListDialog = DeviceListDialog.newInstance();
        deviceListDialog.setCancelable(false);
        deviceListDialog.show(getSupportFragmentManager(), "DeviceList");
        deviceListDialog.setOnClickListener(new DeviceListDialog.OnClick() {
            @Override
            public void onClick(String mac) {
                connectBLE(mac);
            }

            @Override
            public void onCancel() {
                stopScan();
            }
        });
    }

    private void stopScan() {
        LogUtil.d("停止扫描");
        WCHBluetoothManager.getInstance().stopScan();
    }

    private ScanObserver scanObserver = new ScanObserver() {
        @Override
        public void OnScanDevice(BluetoothDevice device, int rssi, byte[] broadcastRecord) {
            String mac = device.getAddress();

            //update device list
            if (deviceListDialog != null && deviceListDialog.isVisible()) {
                deviceListDialog.update(device, rssi);
                return;
            }
            //unknown status
            //LogUtil.d("unknown status");
        }
    };

    private boolean checkBtEnabled() {
        boolean enabled = BluetoothAdapter.getDefaultAdapter().isEnabled();
        if (!enabled) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_BLUETOOTH_CODE);
        }
        return enabled;
    }

    private void stopTest() {
        stopCurrentChar();
    }

    private void startTest() {
        //TODO uuid
        currentCharacteristic = getCurrentCharacteristic("", "");
        if (currentCharacteristic == null) {
            MyToast.show("无法识别当前Characteristic");
            return;
        }
        clearData();
        if ((currentCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) {

        } else {
            try {
                boolean notifyState = WCHBluetoothManager.getInstance().getNotifyState(currentCharacteristic);
                LogUtil.d("当前通知： " + notifyState);
                if (notifyState) {
                    WCHBluetoothManager.getInstance().openNotify(currentCharacteristic, notifyDataCallback);
                }
                /*sbNotify.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                        enableNotify(currentCharacteristic, isChecked);

                    }
                });*/
                enableNotify(currentCharacteristic, true);//TODO enable
            } catch (BLELibException e) {
                e.printStackTrace();
            }
        }
    }

    private void enableNotify(BluetoothGattCharacteristic characteristic, boolean enable) {
        LogUtil.d("改变通知: " + enable);
        Observable.create((ObservableOnSubscribe<String>) emitter -> {

            try {
                if (enable) {
                    boolean b = WCHBluetoothManager.getInstance().openNotify(characteristic, notifyDataCallback);
                    if (!b) {
                        emitter.onError(new Throwable("打开通知失败"));
                        return;
                    }
                } else {
                    boolean b = WCHBluetoothManager.getInstance().closeNotify(characteristic);
                    if (!b) {
                        emitter.onError(new Throwable("关闭通知失败"));
                        return;
                    }
                }
            } catch (BLELibException e) {
                emitter.onError(new Throwable(e));
                return;
            }

            emitter.onComplete();

        }).subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        DialogUtil.getInstance().showLoadingDialog(SettingActivity.this, "正在改变通知");
                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull String s) {

                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        DialogUtil.getInstance().hideLoadingDialog();
                        MyToast.show(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().hideLoadingDialog();
                    }
                });
    }

    NotifyDataCallback notifyDataCallback = new NotifyDataCallback() {
        @Override
        public void OnError(String mac, Throwable t) {
            LogUtil.d(t.getMessage());
        }

        @Override
        public void OnData(String mac, byte[] data) {
            updateValueTextView(data);
        }
    };

    private void updateValueTextView(final byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        count_R += data.length;
        LogUtil.d("共接收到：" + count_R);
    }

    void clearData() {
        count_R = 0;
        count_W = 0;
    }

    private BluetoothGattCharacteristic getCurrentCharacteristic(String serviceUUID, String charUUID) {
        UUID uuid1 = UUID.fromString(serviceUUID);
        UUID uuid2 = UUID.fromString(charUUID);

        for (BluetoothGattService service : serviceList) {
            if (service.getUuid().toString().equalsIgnoreCase(uuid1.toString())) {
                return service.getCharacteristic(uuid2);
            }
        }
        return null;
    }

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
}