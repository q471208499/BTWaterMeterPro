package cn.cb.btwatermeterpro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.data.BleDevice;
import com.clj.fastble.project.blepro.BleProScan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cb.baselibrary.utils.ABDateUtils;
import cn.cb.baselibrary.utils.ABTimeUtils;
import cn.cb.btwatermeterpro.R;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private Context mContext;
    private List<Map<String, Object>> list = new ArrayList<>();
    private boolean details;
    private final String KEY_TIMES = "scanTimes";
    private final String KEY_TIME = "scanTime";
    private final String KEY_RSSI = "Rssi";
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
        Map<String, Object> data = list.get(position);
        String mac = (String) data.get(KEY_MAC);
        String time = (String) data.get(KEY_TIME);
        int times = (int) data.get(KEY_TIMES);
        int Rssi = (int) data.get(KEY_RSSI);
        Object obj = data.get(KEY_DATA);

        holder.detailsView.setVisibility(details ? View.VISIBLE : View.GONE);
        //holder.noDetailsView.setVisibility(!details ? View.VISIBLE : View.GONE);
        //holder.scanObj.setVisibility(obj != null ? View.VISIBLE : View.GONE);

        holder.meterCode.setText(mac.replace(":", ""));
        holder.signal.setText(String.valueOf(Rssi));
        holder.receiveTime.setText(time);
        holder.receiveTimes.setText(times + " 次");
        if (obj != null) {
            BleProScan proScan = (BleProScan) obj;
            holder.totalFlow.setText(proScan.getFlow() + " m³");
            holder.voltage.setText(proScan.getVoltage() + " V");
            holder.contraryFlow.setText(proScan.getContrary() + " m³");
            holder.nonMagnetic.setText(String.valueOf(proScan.getSignal()));
            holder.instantaneousFlow.setText(proScan.getInstantaneous() + " 升/时");
            holder.usageTime.setText(proScan.getUsageTime() + "分");
            holder.meterClock.setText(getClock(proScan.getTime()));
            //holder.scanObj.setText(new JSONObject(proScan.getDataMap()).toJSONString());
            holder.voltageStatus.setVisibility(proScan.getVoltage() <= 3.3 ? View.VISIBLE : View.GONE);
            holder.nonMagneticStatus.setVisibility(proScan.getSignal() <= 10 ? View.VISIBLE : View.GONE);
        }
    }

    private String getClock(String time) {
        return ABDateUtils.getCurDateStr(ABDateUtils.FORMAT_YMD) + String.format(" %s:%s", time.substring(0, 2), time.substring(2, 4));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }

    public void addItem(BleDevice bleDevice) {
        BleProScan proScan = new BleProScan(bleDevice.getScanRecord());
        String scanMac = bleDevice.getMac();
        /*if (BuildConfig.DEBUG) {
            proScan = new BTProScan("14 68 01 30 17 B5 F3 C0 08 3E 1C 48 AC 00 00 00 00 78 00 E6 16");
        }*/
        if (proScan.verify()) {
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> item = list.get(i);
                String itemMac = (String) item.get(KEY_MAC);
                if (itemMac.equals(scanMac)) {
                    item.put(KEY_TIMES, (int) item.get(KEY_TIMES) + 1);
                    item.put(KEY_DATA, proScan);
                    item.put(KEY_RSSI, bleDevice.getRssi());
                    item.put(KEY_TIME, ABTimeUtils.getCurrentTimeInString());
                    notifyItemChanged(i);
                    return;
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put(KEY_TIMES, 1);
            map.put(KEY_MAC, scanMac);
            map.put(KEY_DATA, proScan);
            map.put(KEY_RSSI, bleDevice.getRssi());
            map.put(KEY_TIME, ABTimeUtils.getCurrentTimeInString());
            list.add(map);
            notifyItemChanged(list.size());
        }
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {

        private TextView meterCode, totalFlow, voltage, signal, contraryFlow, nonMagnetic,
                instantaneousFlow, usageTime, meterClock, nonMagneticStatus, voltageStatus,
                receiveTime, receiveTimes, scanObj;
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
            voltageStatus = itemView.findViewById(R.id.item_voltage_status_value);
            noDetailsView = itemView.findViewById(R.id.item_no_details);
            receiveTime = itemView.findViewById(R.id.item_receive_time2_value);
            receiveTimes = itemView.findViewById(R.id.item_receive_times2_value);
            scanObj = itemView.findViewById(R.id.item_scan_obj);
        }
    }
}
