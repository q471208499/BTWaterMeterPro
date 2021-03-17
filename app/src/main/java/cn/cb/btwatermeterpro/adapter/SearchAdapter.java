package cn.cb.btwatermeterpro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.data.BleDevice;
import com.clj.fastble.utils.BTProScan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cb.baselibrary.utils.ABTimeUtils;
import cn.cb.btwatermeterpro.BuildConfig;
import cn.cb.btwatermeterpro.R;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private Context mContext;
    private boolean details;
    private List<Map<String, Object>> list = new ArrayList<>();
    private final String KEY_TIMES = "scanTimes";
    private final String KEY_TIME = "scanTime";
    private final String KEY_MAC = "mac";
    private final String KEY_DATA = "data";

    public SearchAdapter(Context context) {
        mContext = context;
    }

    public void switchDetails() {
        this.details = !details;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        holder.detailsView.setVisibility(details ? View.VISIBLE : View.GONE);
        holder.noDetailsView.setVisibility(!details ? View.VISIBLE : View.GONE);

        Map<String, Object> data = list.get(position);
        String mac = (String) data.get(KEY_MAC);
        String time = (String) data.get(KEY_TIME);
        int times = (int) data.get(KEY_TIMES);
        Object obj = data.get("KEY_DATA");

        holder.meterCode.setText(mac.replace(":", ""));
        if (obj != null) {
            BTProScan proScan = (BTProScan) obj;
            holder.totalFlow.setText(String.valueOf(proScan.getFlow()));
            holder.voltage.setText(String.valueOf(proScan.getVoltage()));
            holder.signal.setText(String.valueOf(proScan.getSignal()));
            holder.contraryFlow.setText(String.valueOf(proScan.getContrary()));
            //holder.nonMagnetic.setText(String.valueOf(proScan.getMagStatus()));
            holder.instantaneousFlow.setText(String.valueOf(proScan.getInstantaneous()));
            holder.usageTime.setText(String.valueOf(proScan.getUsageTime()));
            holder.meterClock.setText(String.valueOf(proScan.getTime()));
            holder.nonMagneticStatus.setText(String.valueOf(proScan.getMagStatus()));
        }
        holder.receiveTime.setText(time);
        holder.receiveTimes.setText(String.valueOf(times));
        holder.receiveTime2.setText(time);
        holder.receiveTimes2.setText(times + "次");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }

    public void addItem(BleDevice bleDevice) {
        BTProScan proScan = new BTProScan(bleDevice.getScanRecord());
        String scanMac = bleDevice.getMac();
        if (proScan.verify()) {
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> item = list.get(i);
                String itemMac = (String) item.get(KEY_MAC);
                if (itemMac.equals(scanMac)) {
                    item.put(KEY_TIMES, (int) item.get(KEY_TIMES) + 1);
                    item.put(KEY_DATA, proScan);
                    item.put(KEY_TIME, ABTimeUtils.getCurrentTimeInString());
                    notifyItemChanged(i);
                    return;
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put(KEY_TIMES, 1);
            map.put(KEY_MAC, scanMac);
            map.put(KEY_DATA, proScan);
            map.put(KEY_TIME, ABTimeUtils.getCurrentTimeInString());
            list.add(map);
            notifyItemChanged(list.size());
        } else if (BuildConfig.DEBUG) {
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> item = list.get(i);
                String itemMac = (String) item.get(KEY_MAC);
                if (itemMac.equals(scanMac)) {
                    item.put(KEY_TIMES, (int) item.get(KEY_TIMES) + 1);
                    //item.put(KEY_DATA, proScan);
                    item.put(KEY_TIME, ABTimeUtils.getCurrentTimeInString());
                    notifyItemChanged(i);
                    return;
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put(KEY_TIMES, 1);
            map.put(KEY_MAC, scanMac);
            map.put(KEY_TIME, ABTimeUtils.getCurrentTimeInString());
            list.add(map);
            notifyItemChanged(list.size());
        }
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {

        private TextView meterCode, totalFlow, voltage, signal, contraryFlow, nonMagnetic, instantaneousFlow,
                usageTime, meterClock, nonMagneticStatus, receiveTime, receiveTimes, receiveTime2, receiveTimes2;
        private View detailsView, noDetailsView;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            meterCode = itemView.findViewById(R.id.item_meter_code_value);
            totalFlow = itemView.findViewById(R.id.item_total_flow_value);
            voltage = itemView.findViewById(R.id.item_voltage_value);
            signal = itemView.findViewById(R.id.item_signal_value);
            detailsView = itemView.findViewById(R.id.item_details);
            contraryFlow = itemView.findViewById(R.id.item_contrary_flow_value);
            nonMagnetic = itemView.findViewById(R.id.item_non_magnetic_value);
            instantaneousFlow = itemView.findViewById(R.id.item_instantaneous_flow_value);
            usageTime = itemView.findViewById(R.id.item_usage_time_value);
            meterClock = itemView.findViewById(R.id.item_meter_clock_value);
            nonMagneticStatus = itemView.findViewById(R.id.item_non_magnetic_status_value);
            receiveTime = itemView.findViewById(R.id.item_receive_time_value);
            receiveTimes = itemView.findViewById(R.id.item_receive_times_value);
            noDetailsView = itemView.findViewById(R.id.item_no_details);
            receiveTime2 = itemView.findViewById(R.id.item_receive_time2_value);
            receiveTimes2 = itemView.findViewById(R.id.item_receive_times2_value);
        }
    }
}
