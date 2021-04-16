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

import cn.cb.baselibrary.utils.ABDateUtils;
import cn.cb.baselibrary.widget.MyDividerItemDecoration;
import cn.cb.btwatermeterpro.R;
import cn.cb.btwatermeterpro.activity.base.BleConnectBaseActivity;
import cn.cb.btwatermeterpro.adapter.HistoryAdapter;

public class HistoryActivity extends BleConnectBaseActivity {

    private TextView picker1, pickerA, pickerB;
    private View datePicker, datePickers;
    private HistoryAdapter historyAdapter;
    private EditText meterAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initBarView();
        bindView();
        getData();
    }

    @Override
    protected void onConnectSuccess() {
        meterAddress.setText(address.replace(":", ""));
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

        picker1.setText(ABDateUtils.getCurDateStr(ABDateUtils.FORMAT_YMD));
        pickerA.setText(ABDateUtils.getCurDateStr(ABDateUtils.FORMAT_YMD));
        pickerB.setText(ABDateUtils.getCurDateStr(ABDateUtils.FORMAT_YMD));
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
        }
    };

    private void showPickDateDialog(View v) {
        DatePickerDialog dialog = new DatePickerDialog(this);
        dialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            TextView textView = (TextView) v;
            textView.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
        });
        dialog.show();
    }
}