package cn.cb.btwatermeterpro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.cb.btwatermeterpro.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HisViewHolder> {

    private Context mContext;
    private JSONArray mArray = new JSONArray();
    private int index = 0;

    public HistoryAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public HisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history, parent, false);
        return new HisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HisViewHolder holder, int position) {
        JSONObject object = mArray.getJSONObject(position);
        holder.time.setText(object.getString("time"));
        holder.readNumber.setText(object.getString("readNumber"));
        holder.flow.setText(object.getString("flow"));
    }

    @Override
    public int getItemCount() {
        return mArray == null ? 0 : mArray.size();
    }

    public int getIndex() {
        return index;
    }

    public void setDataMap(List<Map<String, Object>> mapList, int index) {
        Map<String, Object> dataMap = mapList.get(index);
        this.index = index;
        mArray.clear();
        int[] increment = (int[]) dataMap.get("increment");
        int[] readNumber = (int[]) dataMap.get("readNumber");

        for (int i = 0; i < Objects.requireNonNull(increment).length; i++) {
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMinimumFractionDigits(3);
            JSONObject object = new JSONObject();
            assert readNumber != null;
            String nStr = nf.format(readNumber[i] / 1000F).replaceAll(",", "");
            String fStr = nf.format(increment[i] / 1000F).replaceAll(",", "");
            object.put("time", String.format("%02d:00", i));
            object.put("readNumber", nStr);
            object.put("flow", fStr);
            mArray.add(object);
            notifyDataSetChanged();
        }
    }

    class HisViewHolder extends RecyclerView.ViewHolder {
        TextView time, readNumber, flow;

        public HisViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.his_time);
            readNumber = itemView.findViewById(R.id.his_read_number);
            flow = itemView.findViewById(R.id.his_flow);

        }
    }
}
