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

import java.util.Map;

import cn.cb.btwatermeterpro.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HisViewHolder> {

    private Context mContext;
    private JSONArray mArray = new JSONArray();

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

    public void addDataMap(Map<String, Object> dataMap) {
        int[] increment = (int[]) dataMap.get("increment");
        int[] readNumber = (int[]) dataMap.get("readNumber");

        JSONObject o = new JSONObject();
        o.put("time", "00:00");
        o.put("readNumber", String.valueOf((int) dataMap.get("start")));
        o.put("flow", "0");
        mArray.add(o);
        notifyItemInserted(mArray.size());

        for (int i = 0; i < increment.length; i++) {
            JSONObject object = new JSONObject();
            //object.put("time", (i + 1) + ":00");
            object.put("time", String.format("%02d:00", i + 1));
            object.put("readNumber", String.valueOf(readNumber[i]));
            object.put("flow", String.valueOf(increment[i]));
            mArray.add(object);
            notifyItemInserted(mArray.size());
        }
    }

    public boolean cleanDataMapList() {
        mArray.clear();
        notifyDataSetChanged();
        return true;
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
