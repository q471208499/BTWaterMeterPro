package cn.cb.btwatermeterpro.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import cn.cb.baselibrary.activity.BaseActivity;
import cn.cb.btwatermeterpro.R;

public class HistoryActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initBarView();
        bindView();
    }

    private void bindView() {
        Spinner spinner = findViewById(R.id.his_query_type_value);
        String[] arr = {"日期", "区间"};
        SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arr);
        spinner.setAdapter(adapter);
    }
}