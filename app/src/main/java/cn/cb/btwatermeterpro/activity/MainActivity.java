package cn.cb.btwatermeterpro.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import cn.cb.btwatermeterpro.R;
import cn.cb.btwatermeterpro.adapter.SearchAdapter;
import es.dmoral.toasty.MyToast;

public class MainActivity extends BleBaseActivity {

    private final String TAG = getClass().getSimpleName();

    private SearchAdapter adapter;
    private View emptyView;
    private View tipBar;
    private FloatingActionButton startPause;
    private boolean startFlag = false;
    private final int REQUEST_ENABLE_BT = 2009;
    private long stopTime = 0;
    private Chronometer chronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBarView();
        bindView();
        chronometer = findViewById(R.id.main_chronometer);
    }

    private void bindView() {
        adapter = new SearchAdapter(this);
        RecyclerView recyclerView = findViewById(R.id.search_recycler);
        recyclerView.setAdapter(adapter);
        startPause = findViewById(R.id.main_start_pause_btn);
        startPause.setOnClickListener(clickListener);
        findViewById(R.id.main_stop_btn).setOnClickListener(clickListener);
        emptyView = findViewById(R.id.main_empty);
        tipBar = findViewById(R.id.tip_bar);
        emptyView.setVisibility(View.VISIBLE);
    }

    private void start() {
        adapter.notifyDataSetChanged();
        startScan();
        newClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(Menu.NONE, Menu.NONE, Menu.FIRST, "详情");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == Menu.NONE) {
            adapter.switchDetails();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //******************************************************************************************
    private BluetoothLeScanner leScanner;
    private BluetoothAdapter bluetoothAdapter;

    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
                Log.i(TAG, "###onLeScan: " + bleDevice.getMac());
                adapter.addItem(bleDevice);
                switchView();
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                Log.i(TAG, "###onScanning: " + bleDevice.getMac());
                adapter.addItem(bleDevice);
                switchView();
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
            }
        });
    }

    private void newClick() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (enableBluetooth(bluetoothAdapter)) {
            startScanBLE();
        }
    }

    protected void startScanBLE() {
        leScanner = bluetoothAdapter.getBluetoothLeScanner();
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(0)
                .build();
        leScanner.startScan(null, settings, callback);
    }

    /**
     * 打开蓝牙
     */
    public boolean enableBluetooth(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                startScanBLE();
            } else {
                MyToast.errorL("开启蓝牙失败！");
            }
        }
    }

    private int times = 0;
    private ScanCallback callback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            times++;
            BluetoothDevice device = result.getDevice();
            BleDevice bleDevice = new BleDevice(device, result.getRssi(), result.getScanRecord().getBytes(), System.currentTimeMillis());
            adapter.addItem(bleDevice);
            switchView();
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
    //******************************************************************************************

    private View.OnClickListener clickListener = v -> {
        switch (v.getId()) {
            case R.id.main_start_pause_btn:
                startFlag = !startFlag;
                startPause.setImageResource(startFlag ? R.mipmap.ic_pause : R.mipmap.ic_start);
                break;
            case R.id.main_stop_btn:
                startFlag = false;
                startPause.setImageResource(R.mipmap.ic_start);
                adapter.clear();
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.stop();
                stopTime = 0;
                times = 0;
                switchView();
                break;
        }
        if (startFlag) {
            chronometer.setBase(SystemClock.elapsedRealtime() + stopTime);
            chronometer.start();
            start();
        } else {
            stopTime = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.stop();
            BleManager.getInstance().cancelScan();
            leScanner.stopScan(callback);
        }
    };

    private void switchView() {
        tipBar.setVisibility(adapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
        chronometer.setVisibility(adapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
        emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        TextView text = tipBar.findViewById(R.id.tip_text);
        text.setText("发现周边广播：" + times + " 次");
    }
}