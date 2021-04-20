package cn.cb.btwatermeterpro.activity.base;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.cb.baselibrary.activity.BaseActivity;
import cn.cb.btwatermeterpro.ui.DeviceListDialog;
import cn.cb.btwatermeterpro.ui.DialogUtil;
import cn.wch.blelib.WCHBluetoothManager;
import cn.wch.blelib.exception.BLELibException;
import cn.wch.blelib.host.core.ConnRuler;
import cn.wch.blelib.host.core.Connection;
import cn.wch.blelib.host.core.callback.ConnectCallback;
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


/**
 * 此抽象类适用与CH9141相关的通信
 */
public abstract class BleConnectBaseActivity extends BaseActivity {
    //UUID: 00001800-0000-1000-8000-00805f9b34fb
    //UUID: 6e400001-b5a3-f393-e0a9-e50e24dcca9f
    //readCharacteristic() - uuid: 00002a00-0000-1000-8000-00805f9b34fb
    private final String SERVER_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9f";
    private final String SEND_UUID = "6e400002-b5a3-f393-e0a9-e50e24dcca9f";
    private final String RECEIVE_UUID = "6e400003-b5a3-f393-e0a9-e50e24dcca9f";

    private RxPermissions rxPermissions;
    protected int REQUEST_BLUETOOTH_CODE = 111;

    protected String address;

    protected ScheduledExecutorService scheduledExecutorService;

    protected Runnable speedRunnable;
    protected boolean isHardwareOld = false;

    /**
     * 包括蓝牙相关通信权限和存储存储权限(Android 10及以上存储需特殊考虑)
     */
    String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    String[] permissions_ble = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        init();
        initBle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BLUETOOTH_CODE && resultCode == RESULT_OK) {
            checkAutoStart();
        } else if (requestCode == REQUEST_BLUETOOTH_CODE) {
            MyToast.show("请允许打开蓝牙");
        } else {
            //onActivitySomeResult(requestCode, resultCode, data);
        }
    }


    protected boolean checkConnected(String address) {

        return WCHBluetoothManager.getInstance().isConnected(address);
    }

    protected void showDisconnectDialog() {

        DialogUtil.getInstance().showDisconnectDialog(this, new DialogUtil.IDisconnectResult() {
            @Override
            public void onDisconnect() {
                try {
                    WCHBluetoothManager.getInstance().disconnect(false);
                } catch (BLELibException e) {
                    e.printStackTrace();
                    LogUtil.d(e.getMessage());
                }
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        stopScan();
        try {
            WCHBluetoothManager.getInstance().disconnect(false);
        } catch (BLELibException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    protected void init() {
        rxPermissions = new RxPermissions(this);
    }


    public void requestPermission() {
        rxPermissions.requestEachCombined(permissions).subscribe(permission -> {
            if (permission.granted) {//全部同意后调用
                openBlueAdapter();
            } else if (permission.shouldShowRequestPermissionRationale) {//只要有一个选择：禁止，但没有选择“以后不再询问”，以后申请权限，会继续弹出提示
                MyToast.show("请允许权限,否则APP不能正常运行");
            } else {//只要有一个选择：禁止，但选择“以后不再询问”，以后申请权限，不会继续弹出提示
                MyToast.show("请到设置中打开权限");
            }
        });
    }


    private void initBle() {
        if (!isSupportBle(this)) {
            MyToast.show("本设备不支持BLE");
            //System.exit(0);
            return;
        }
        requestPermission();
    }

    private void openBlueAdapter() {
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            //执行操作
            checkAutoStart();
        } else {
            //未打开蓝牙
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_BLUETOOTH_CODE);
        }
    }

    /**
     * 在一些应用中需要在开始时扫描，那么就需要检查是否满足必要的条件
     */
    private void checkAutoStart() {
        //Adapter
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            LogUtil.d("Adapter is closed, deny auto start");
            return;
        }
        //permission
        for (String permission : permissions_ble) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                LogUtil.d("permission is deny, deny auto start");
                return;
            }
        }

        startTask();
    }

    /**
     * 蓝牙服务绑定后需要执行的操作
     */
    protected void startTask() {

    }

    public static boolean isSupportBle(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return BluetoothAdapter.getDefaultAdapter() != null && packageManager != null && packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /////////////////////////////////////connect///////////////////////////////////////////
    protected void connectBLE(String mac) {
        ConnRuler connRuler = new ConnRuler.Builder(mac).connectTimeout(10000).build();
        try {
            WCHBluetoothManager.getInstance().connect(connRuler, connectCallback);
        } catch (BLELibException e) {
            e.printStackTrace();
            LogUtil.d(e.getMessage());
        }
    }

    private ConnectCallback connectCallback = new ConnectCallback() {
        @Override
        public void OnError(String mac, Throwable t) {
            LogUtil.d("连接回调：" + t.getMessage());
            onConnectError(t.getMessage());
            MyToast.show(t.getMessage());
        }

        @Override
        public void OnConnecting(String mac) {
            System.out.println("### OnConnecting");
            onConnecting();
        }

        @Override
        public void OnConnectSuccess(String mac, Connection connection) {
            System.out.println("### OnConnectSuccess");
            address = mac;
            MyToast.show("蓝牙连接成功");
            onConnectSuccess();
            setSpeedMonitor();
        }

        @Override
        public void OnDiscoverService(String mac, List<BluetoothGattService> list) {
            System.out.println("### OnDiscoverService");
            onDiscoverService(list);
            //startTest();
            onDiscoverService();
        }

        @Override
        public void OnConnectTimeout(String mac) {
            MyToast.show("连接超时");
            onConnectError("连接超时");
        }

        @Override
        public void OnDisconnect(String mac, BluetoothDevice bluetoothDevice, int status) {
            LogUtil.d("连接回调：断开连接");
            cancelSpeedMonitor();
            MyToast.show("蓝牙断开连接");
            onDisconnect();
        }
    };

    protected void setMtu(int mtu) {
        try {
            WCHBluetoothManager.getInstance().setMTU(mtu, (gatt, mtu1, status) -> {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    LogUtil.d("MTU大小设置为" + mtu1);
                    //MyToast.show("MTU大小设置为" + mtu1);
                } else {
                    LogUtil.d("设置MTU大小失败");
                    MyToast.show("设置MTU大小失败");
                }
            });
        } catch (BLELibException e) {
            e.printStackTrace();
            LogUtil.d(e.getMessage());
        }
    }


    /**
     * 监控和收发速度
     */
    private void setSpeedMonitor() {
        if (speedRunnable == null) {
            LogUtil.d("speed monitor is null");
            return;
        }
        cancelSpeedMonitor();
        scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleWithFixedDelay(speedRunnable, 100, 1000, TimeUnit.MILLISECONDS);
    }

    private void cancelSpeedMonitor() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
    }

    /**
     * 用来监测收发速度，定时器每秒触发，执行操作
     *
     * @param runnable 需要执行的操作
     */
    protected void setScheduleSpeedMonitor(@NonNull Runnable runnable) {
        this.speedRunnable = runnable;
    }

    protected boolean checkValid() {
        return WCHBluetoothManager.getInstance().isConnected(address);
    }


    //protected abstract void onActivitySomeResult(int requestCode, int resultCode, @Nullable Intent data);

    //protected abstract void onConnecting();

    protected abstract void onDiscoverService();

    protected abstract void onConnectSuccess();

    //protected abstract void onConnectError(String message);

    //protected abstract void onDisconnect();
    protected boolean isConnected = false;

    private List<BluetoothGattService> serviceList;

    private BluetoothGattCharacteristic receiveBGC;

    private DeviceListDialog deviceListDialog;

    private int count_R = 0;
    private int count_W = 0;

    protected void onActivitySomeResult(int requestCode, int resultCode, @Nullable Intent data) {

    }

    protected void onConnecting() {
        isConnected = false;
        runOnUiThread(() -> DialogUtil.getInstance().showLoadingDialog(this, "正在连接..."));
    }

    protected void onDiscoverService(List<BluetoothGattService> services) {
        LogUtil.d("onDiscoverService: " + services.size());
        serviceList = services;
        isConnected = true;
        runOnUiThread(() -> {
            isConnected = true;
            DialogUtil.getInstance().hideLoadingDialog();
            //invalidateOptionsMenu();
            //initServiceSpinner(serviceList);
            //enableWidget(true);
        });
    }

    protected void onConnectError(String message) {
        isConnected = false;
        runOnUiThread(() -> DialogUtil.getInstance().hideLoadingDialog());
    }

    protected void onDisconnect() {
        isConnected = false;
        runOnUiThread(() -> {
            stopTest();
            //invalidateOptionsMenu();
            //enableWidget(false);
            //enableButtons(false);
            DialogUtil.getInstance().hideLoadingDialog();
            //resetWidget();
            stopCurrentChar();
        });
    }

    private void stopCurrentChar() {
        LogUtil.d("stop current char");
        /*if (sendBGC == null) {
            return;
        }
        //sbNotify.setOnCheckedChangeListener(null);
        //sbNotify.setChecked(false);
        if ((sendBGC.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
            try {
                if (WCHBluetoothManager.getInstance().getNotifyState(sendBGC)) {
                    WCHBluetoothManager.getInstance().openNotify(sendBGC, null);
                }
            } catch (BLELibException e) {
                e.printStackTrace();
                LogUtil.d(e.getMessage());
            }
        }
        sendBGC = null;*/
    }

    protected void startScan() {
        //init transitionRunnable

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            //大于安卓10，需要检查定位服务
            LogUtil.d("位置服务打开：" + Location.isLocationEnable(this));
            if (!Location.isLocationEnable(this)) {
                DialogUtil.getInstance().showSimpleDialog(this, "蓝牙扫描需要开启位置信息服务", new DialogUtil.IResult() {
                    @Override
                    public void onContinue() {
                        Location.requestLocationService(BleConnectBaseActivity.this);
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

    protected void stopScan() {
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

    protected void startTest() {
        receiveBGC = getCurrentCharacteristic(SERVER_UUID, RECEIVE_UUID);
        if (receiveBGC == null) {
            MyToast.show("无法识别当前Characteristic");
            return;
        }
        if ((receiveBGC.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) {

        } else {
            try {
                boolean notifyState = WCHBluetoothManager.getInstance().getNotifyState(receiveBGC);
                LogUtil.d("当前通知： " + notifyState);
                if (notifyState) {
                    WCHBluetoothManager.getInstance().openNotify(receiveBGC, notifyDataCallback);
                }
                /*sbNotify.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                        enableNotify(currentCharacteristic, isChecked);

                    }
                });*/
                enableNotify(receiveBGC, true);
            } catch (BLELibException e) {
                e.printStackTrace();
            }
        }

    }

    protected void enableNotify() {
        enableNotify(receiveBGC, true);
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
                        DialogUtil.getInstance().showLoadingDialog(BleConnectBaseActivity.this, "正在改变通知");
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

    private void updateValueTextView(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        count_R += data.length;
        LogUtil.d("共接收到：" + count_R);
        updateReadValue(data);
    }

    protected abstract void updateReadValue(byte[] data);

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

    protected void write(byte[] data) {
        BluetoothGattCharacteristic sendBGC = getCurrentCharacteristic(SERVER_UUID, SEND_UUID);
        if (sendBGC == null) {
            MyToast.show("无法识别当前Characteristic");
            return;
        }
        clearData();
        if ((sendBGC.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) == 0
                || (sendBGC.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) == 0) {
        } else {
            //写方式
            if ((sendBGC.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) == 0) {
                sendBGC.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            }
            if ((sendBGC.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) == 0) {
                sendBGC.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            }
        }
        write(sendBGC, data);
    }

    private void write(BluetoothGattCharacteristic characteristic, byte[] data) {
        System.out.println("### write");
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            try {
                int write = WCHBluetoothManager.getInstance().write(characteristic, data, data.length);
                if (write < 0) {
                    emitter.onError(new Throwable("发送失败"));
                } else if (write == data.length) {
                    count_W += write;
                    emitter.onComplete();
                } else {
                    count_W += write;
                    emitter.onComplete();
                }
                System.out.println("### 发送成功");
            } catch (BLELibException e) {
                emitter.onError(new Throwable(e.getMessage()));
            }

        }).subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        //btnSend.setEnabled(false);
                        //btnSend.setText(StringConstant.BTN_STOP);

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull String s) {

                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        //btnSend.setEnabled(true);
                        //btnSend.setText(StringConstant.BTN_SEND);
                        //showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        //btnSend.setEnabled(true);
                        //btnSend.setText(StringConstant.BTN_SEND);
                        //tvSendCount.setText(String.format(Locale.getDefault(),"%d 字节",count_W));
                    }
                });
    }

    protected void read() {
        read(receiveBGC);
    }

    protected void read(BluetoothGattCharacteristic characteristic) {
        System.out.println("### read");
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            try {
                byte[] read = WCHBluetoothManager.getInstance().read(characteristic, true);

                updateValueTextView(read);
            } catch (Exception e) {
                emitter.onError(new Throwable(e.getMessage()));
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        //btnRead.setEnabled(false);
                        DialogUtil.getInstance().showLoadingDialog(BleConnectBaseActivity.this, "正在读取");
                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull String s) {

                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        //btnRead.setEnabled(true);
                        DialogUtil.getInstance().hideLoadingDialog();
                    }

                    @Override
                    public void onComplete() {
                        //btnRead.setEnabled(true);
                        DialogUtil.getInstance().hideLoadingDialog();
                    }
                });
    }
}
