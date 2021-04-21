package cn.cb.btwatermeterpro.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import cn.cb.baselibrary.activity.BaseActivity;
import cn.cb.baselibrary.utils.ABTimeUtils;
import cn.cb.baselibrary.utils.AppUpdateHelper;
import cn.cb.btwatermeterpro.BTApplication;
import cn.cb.btwatermeterpro.BuildConfig;
import cn.cb.btwatermeterpro.R;
import cn.cb.btwatermeterpro.activity.base.BleConnectBaseActivity;
import cn.cb.btwatermeterpro.db.DbManager;
import cn.wch.blelib.WCHBluetoothManager;
import cn.wch.blelib.exception.BLELibException;

public class NavActivity extends BleConnectBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_my)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        setStatusBarColor(android.R.color.white);
        getSupportActionBar().hide();
        new AppUpdateHelper(BuildConfig.UPDATE_URL + "?" + ABTimeUtils.getCurrentTimeInString()).getUpdateInfo();
        if (BTApplication.FIRST) DbManager.getInstance().getSqlServer().testData();
    }

    @Override
    protected void startTask() {
        startScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBle();
    }

    @Override
    protected void allReady() {

    }

    @Override
    protected void onDiscoverService() {

    }

    @Override
    protected void onConnectSuccess() {

    }

    @Override
    protected void updateReadValue(byte[] data) {

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showFinishDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}