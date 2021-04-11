package cn.cb.btwatermeterpro;

import com.alibaba.fastjson.JSONObject;

import cn.cb.baselibrary.BaseApplication;
import cn.cb.btwatermeterpro.db.DbManager;
import cn.wch.blelib.WCHBluetoothManager;
import cn.wch.blelib.exception.BLELibException;
import cn.wch.blelib.utils.LogUtil;

public class BTApplication extends BaseApplication {

    private static JSONObject user;

    @Override
    public void onCreate() {
        super.onCreate();
        DEBUG = BuildConfig.DEBUG;
        DB_VERSION = 1;
        DbManager.createInstance(this, DB_NAME, DB_VERSION);

        try {
            WCHBluetoothManager.getInstance().init(this);
        } catch (BLELibException e) {
            e.printStackTrace();
            LogUtil.d(e.getMessage());
        }
    }

    public static void setUser(JSONObject object) {
        user = object;
    }

    public static JSONObject getUser() {
        return user;
    }
}
