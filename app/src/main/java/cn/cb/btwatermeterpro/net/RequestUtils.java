package cn.cb.btwatermeterpro.net;

import cn.cb.baselibrary.net.Result;
import cn.cb.baselibrary.net.okhttp3.RequestMode;
import cn.cb.baselibrary.net.okhttp3.RequestParams;
import cn.cb.baselibrary.net.okhttp3.ResponseCallback;
import cn.cb.btwatermeterpro.BuildConfig;

public class RequestUtils {

    public static void doLogin(String username, String password, ResponseCallback callback) {
        String url = BuildConfig.HTTP_HOST + "bt/login";
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        RequestMode.postRequest(url, params, callback, Result.class);
    }

    public static void getRecord(int pageNum, int pageSize, ResponseCallback callback) {
        String url = BuildConfig.HTTP_HOST + "bt/getRecord";
        RequestParams params = new RequestParams();
        //params.put("id", BTApplication.getAppUser().getUserId());
        params.put("pageNum", String.valueOf(pageNum));
        params.put("pageSize", String.valueOf(pageSize));
        RequestMode.getRequest(url, params, callback, Result.class);
    }

    public static void getAreaList(int pageNum, int pageSize, ResponseCallback callback) {
        String url = BuildConfig.HTTP_HOST + "bt/getAreaList";
        RequestParams params = new RequestParams();
        //params.put("reachNo", BTApplication.getAppUser().getMeterInfo().getReachNo());
        params.put("pageNum", String.valueOf(pageNum));
        params.put("pageSize", String.valueOf(pageSize));
        RequestMode.getRequest(url, params, callback, Result.class);
    }

    public static void uploadReading(String meterAddress, String meterCumuUsage, String voltage,
                                     String status0L, String createTime, String userName,
                                     String companyCode, ResponseCallback callback) {
        String url = BuildConfig.HTTP_HOST + "bt/uploadReading";
        RequestParams params = new RequestParams();
        params.put("meterAddress", meterAddress);
        params.put("meterCumuUsage", meterCumuUsage);
        params.put("voltage", voltage);
        params.put("status0L", status0L);
        params.put("createTime", createTime);
        params.put("userName", userName);
        params.put("companyCode", companyCode);
        RequestMode.getRequest(url, params, callback, Result.class);
    }
}
