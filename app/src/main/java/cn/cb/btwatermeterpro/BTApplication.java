package cn.cb.btwatermeterpro;

import com.alibaba.fastjson.JSONObject;

import cn.cb.baselibrary.BaseApplication;

public class BTApplication extends BaseApplication{

    private static JSONObject user;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void setUser(JSONObject object) {
        user = object;
    }

    public static JSONObject getUser() {
        return user;
    }
}
