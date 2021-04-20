package cn.cb.btwatermeterpro.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.project.blepro.BleProHistory;
import com.clj.fastble.utils.HexUtil;

import java.util.Calendar;
import java.util.Date;

import cn.cb.baselibrary.utils.ABDateUtils;
import cn.cb.baselibrary.widget.MyDividerItemDecoration;
import cn.cb.btwatermeterpro.R;
import cn.cb.btwatermeterpro.activity.base.BleConnectBaseActivity;
import cn.cb.btwatermeterpro.adapter.HistoryAdapter;
import es.dmoral.toasty.MyToast;

public class HistoryActivity extends BleConnectBaseActivity {

    private TextView picker1, pickerA, pickerB, log;
    private View datePicker, datePickers;
    private HistoryAdapter historyAdapter;
    private EditText meterAddress;
    private Calendar startCld, endCld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initBarView();
        bindView();
        getData();
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
    }

    private void getData() {
        //historyAdapter.notifyDataSet(DbManager.getInstance().getSqlServer().getReadList("1111", "2021-3-23", "2021-3-25"));
    }

    private void bindView() {
        Spinner spinner = findViewById(R.id.his_query_type_value);
        RecyclerView recyclerView = findViewById(R.id.his_recycler);
        meterAddress = findViewById(R.id.his_meter_address);
        datePicker = findViewById(R.id.his_date_picker);
        datePickers = findViewById(R.id.his_date_pickers);
        picker1 = findViewById(R.id.his_date_picker_1);
        pickerA = findViewById(R.id.his_date_picker_a);
        pickerB = findViewById(R.id.his_date_picker_b);

        picker1.setOnClickListener(clickListener);
        pickerA.setOnClickListener(clickListener);
        pickerB.setOnClickListener(clickListener);
        meterAddress.setOnClickListener(clickListener);
        meterAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) startScan();
        });

        log = findViewById(R.id.his_log);
        findViewById(R.id.his_btn_connect).setOnClickListener(clickListener);
        findViewById(R.id.his_btn_service).setOnClickListener(clickListener);
        findViewById(R.id.his_btn_notify).setOnClickListener(clickListener);
        findViewById(R.id.his_btn_mut).setOnClickListener(clickListener);
        findViewById(R.id.his_btn_write).setOnClickListener(clickListener);

        picker1.setText(ABDateUtils.getCurDateStr(ABDateUtils.FORMAT_YMD));
        pickerA.setText(ABDateUtils.getCurDateStr(ABDateUtils.FORMAT_YMD));
        pickerB.setText(ABDateUtils.getCurDateStr(ABDateUtils.FORMAT_YMD));
        startCld = Calendar.getInstance();
        endCld = Calendar.getInstance();
        endCld.add(Calendar.DAY_OF_MONTH, 1);

        String[] arr = {"日期", "区间"};
        SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arr);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(selectedListener);
        historyAdapter = new HistoryAdapter(this);
        recyclerView.setAdapter(historyAdapter);
        recyclerView.addItemDecoration(new MyDividerItemDecoration());
    }

    private AdapterView.OnItemSelectedListener selectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            datePicker.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            datePickers.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private View.OnClickListener clickListener = v -> {
        switch (v.getId()) {
            case R.id.his_date_picker_1:
            case R.id.his_date_picker_a:
            case R.id.his_date_picker_b:
                showPickDateDialog(v);
                break;
            case R.id.his_meter_address:
                startScan();
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
                if (!endCld.after(startCld)) {
                    MyToast.show("截至时间有误！");
                    return;
                }
                byte[] b = BleProHistory.getBytesDate(
                        startCld.get(Calendar.YEAR),
                        startCld.get(Calendar.MONTH) + 1,
                        startCld.get(Calendar.DAY_OF_MONTH),
                        differentDaysByMillisecond(startCld.getTime(), endCld.getTime()));
                setLog("发：", b);
                write(b);
                break;
        }
    };

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

    private void showPickDateDialog(View v) {
        DatePickerDialog dialog = new DatePickerDialog(this);
        dialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            TextView textView = (TextView) v;
            textView.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            if (v.getId() == R.id.his_date_picker_1) {
                startCld = Calendar.getInstance();
                startCld.set(year, month, dayOfMonth);
                endCld = Calendar.getInstance();
                endCld.set(year, month, dayOfMonth);
                endCld.add(Calendar.DAY_OF_MONTH, 1);
            } else if (v.getId() == R.id.his_date_picker_a) {
                startCld = Calendar.getInstance();
                startCld.set(year, month, dayOfMonth);
            } else {
                endCld = Calendar.getInstance();
                endCld.set(year, month, dayOfMonth);
                if (!endCld.after(startCld)) MyToast.show("截至时间有误！");
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
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        return days;
    }
}