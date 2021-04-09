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

import cn.cb.btwatermeterpro.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HisViewHolder> {

    private Context mContext;
    private JSONArray mArray;

    public HistoryAdapter(Context context) {
        mContext = context;
    }

    public void notifyDataSet(JSONArray array) {
        if (mArray != null)
            mArray.clear();
        mArray = array;
        notifyDataSetChanged();
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
