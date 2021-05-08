package cn.cb.btwatermeterpro.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.project.blepro.BleProHistory;
import com.clj.fastble.utils.HexUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.cb.baselibrary.utils.ABDateUtils;
import cn.cb.baselibrary.utils.ABTextUtils;
import cn.cb.baselibrary.widget.MyDividerItemDecoration;
import cn.cb.btwatermeterpro.BTApplication;
import cn.cb.btwatermeterpro.R;
import cn.cb.btwatermeterpro.activity.base.BleConnectBaseActivity;
import cn.cb.btwatermeterpro.adapter.HistoryAdapter;
import cn.cb.btwatermeterpro.provider.BTConstant;
import es.dmoral.toasty.MyToast;

public class HistoryActivity extends BleConnectBaseActivity {

    private TextView log;
    private HistoryAdapter historyAdapter;
    private EditText meterAddress;
    private Calendar startCld, endCld;
    private TextView dateCur;
    private final StringBuilder logStrBuilder = new StringBuilder();
    private final List<Map<String, Object>> mapList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initBarView();
        bindView();
        touchAddress();
    }

    @Override
    protected void startTask() {

    }

    @Override
    protected void allReady() {

    }

    @Override
    protected void onDiscoverService() {

    }

    @Override
    protected void onConnectSuccess() {
        meterAddress.setText(address.replace(":", ""));
    }

    @Override
    protected void updateReadValue(byte[] data) {
        setLog("收：", data);
        BleProHistory.Receive receive = new BleProHistory.Receive(HexUtil.formatHexString(data));
        Map<String, Object> dataMap = receive.getDataMap();
        mapList.add(dataMap);
        if (mapList.size() == 1) setAdapterFirstData();
        dismissLoading();
    }

    private void setAdapterFirstData() {
        setAdapterData(0);
    }

    private void setAdapterData(int index) {
        historyAdapter.setDataMap(mapList, index);
        String date = (String) mapList.get(index).get("date");
        assert date != null;
        date = String.format("%s-%s-%s", date.substring(0, 4), date.substring(4, 6), date.substring(6));
        dateCur.setText(date);
    }

    private void adapterDataLast() {
        int index = historyAdapter.getIndex();
        if (index == 0) {
            MyToast.show("已经是开始日期了");
            return;
        }
        setAdapterData(index - 1);
    }

    private void adapterDataNext() {
        int index = historyAdapter.getIndex();
        if (index == mapList.size() - 1) {
            MyToast.show("已经是结束日期了");
            return;
        }
        setAdapterData(index + 1);
    }

    private void bindView() {
        RecyclerView recyclerView = findViewById(R.id.his_recycler);
        meterAddress = findViewById(R.id.his_meter_address);
        TextView pickerStart = findViewById(R.id.his_date_picker_start);
        TextView pickerEnd = findViewById(R.id.his_date_picker_end);
        dateCur = findViewById(R.id.his_date_cur_value);

        findViewById(R.id.his_btn_last).setOnClickListener(clickListener);
        findViewById(R.id.his_btn_next).setOnClickListener(clickListener);
        pickerStart.setOnClickListener(clickListener);
        pickerEnd.setOnClickListener(clickListener);
        meterAddress.setOnClickListener(clickListener);
        meterAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) touchAddress();
        });

        //===============================debug step log【Start】====================================
        log = findViewById(R.id.his_log);
        findViewById(R.id.his_btn_connect).setOnClickListener(clickListener);
        findViewById(R.id.his_btn_service).setOnClickListener(clickListener);
        findViewById(R.id.his_btn_notify).setOnClickListener(clickListener);
        findViewById(R.id.his_btn_mut).setOnClickListener(clickListener);
        findViewById(R.id.his_btn_write).setOnClickListener(clickListener);
        int visible = BTConstant.DEVICE_LOG ? View.VISIBLE : View.GONE;
        findViewById(R.id.his_log).setVisibility(visible);
        findViewById(R.id.his_btn_connect).setVisibility(visible);
        findViewById(R.id.his_btn_service).setVisibility(visible);
        findViewById(R.id.his_btn_notify).setVisibility(visible);
        findViewById(R.id.his_btn_mut).setVisibility(visible);
        findViewById(R.id.his_btn_write).setVisibility(visible);
        //===============================debug step log【End】====================================

        pickerStart.setText(ABDateUtils.getCurDateStr(ABDateUtils.FORMAT_YMD));
        pickerEnd.setText(ABDateUtils.getCurDateStr(ABDateUtils.FORMAT_YMD));
        //dateCur.setText("####-##-##");
        dateCur.setText("");
        startCld = Calendar.getInstance();
        endCld = Calendar.getInstance();
        endCld.add(Calendar.DAY_OF_MONTH, 1);

        historyAdapter = new HistoryAdapter(this);
        recyclerView.setAdapter(historyAdapter);
        recyclerView.addItemDecoration(new MyDividerItemDecoration());
    }

    private void touchAddress() {
        if (checkConnected(BTApplication.getConnectAddress())) {
            meterAddress.setText(BTApplication.getConnectAddress() == null ? "" : BTApplication.getConnectAddress().replaceAll(":", ""));
            startTest();
        } else {
            startScan();
        }
    }

    private final View.OnClickListener clickListener = v -> {
        switch (v.getId()) {
            case R.id.his_date_picker_start:
            case R.id.his_date_picker_end:
                showPickDateDialog(v);
                break;
            case R.id.his_meter_address:
                touchAddress();
                break;
            case R.id.his_btn_connect:
                startScan();
                break;
            case R.id.his_btn_service:
                startTest();
                break;
            case R.id.his_btn_notify:
                enableNotify();
                break;
            case R.id.his_btn_mut:
                setMtu(100);
                break;
            case R.id.his_btn_write:
                getHisData();
                break;
            case R.id.his_btn_next:
                if (ABTextUtils.isEmpty(meterAddress)) {
                    MyToast.show(R.string.enter_complete);
                    return;
                }
                adapterDataNext();
                break;
            case R.id.his_btn_last:
                if (ABTextUtils.isEmpty(meterAddress)) {
                    MyToast.show(R.string.enter_complete);
                    return;
                }
                adapterDataLast();
                break;
            /*case R.id.his_btn_search:
                if (ABTextUtils.isEmpty(meterAddress)) {
                    MyToast.show(R.string.enter_complete);
                    return;
                }
                getHisData();
                break;*/
        }
    };

    private void getHisData() {
        if (!endCld.after(startCld)) {
            MyToast.show("截至时间有误！");
            return;
        }
        showLoading(true);
        mapList.clear();
        byte[] b = BleProHistory.getBytesDate(
                startCld.get(Calendar.YEAR),
                startCld.get(Calendar.MONTH) + 1,
                startCld.get(Calendar.DAY_OF_MONTH),
                differentDaysByMillisecond(startCld.getTime(), endCld.getTime()));
        setLog("发：", b);
        write(b);
    }

    private void setLog(String flag, byte[] data) {
        String logStr = HexUtil.formatHexString(data, true);
        logStrBuilder.append(flag).append(logStr).append("\n");
        System.out.println("### updateReadValue: " + logStr);
        runOnUiThread(() -> {
            log.setText(logStrBuilder.toString());
        });
    }

    private void showPickDateDialog(View v) {
        DatePickerDialog dialog = new DatePickerDialog(this);
        dialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            TextView textView = (TextView) v;
            String dateStr = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            textView.setText(dateStr);
            if (v.getId() == R.id.his_date_picker_start) {
                startCld = Calendar.getInstance();
                startCld.set(year, month, dayOfMonth);
                MyToast.show("请填写结束日期！");
            } else {
                endCld = Calendar.getInstance();
                endCld.set(year, month, dayOfMonth);
                if (!endCld.after(startCld)) {
                    MyToast.show("结束时间有误！");
                    return;
                }
                if (ABTextUtils.isEmpty(meterAddress)) {
                    MyToast.show(R.string.enter_complete);
                    return;
                }
                getHisData();
            }
        });
        dialog.show();
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     *
     * @param date1
     * @param date2
     * @return
     */
    private int differentDaysByMillisecond(Date date1, Date date2) {
        return (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
    }
}