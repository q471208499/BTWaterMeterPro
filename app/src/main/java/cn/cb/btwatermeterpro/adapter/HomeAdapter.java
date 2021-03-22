package cn.cb.btwatermeterpro.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.cb.btwatermeterpro.R;
import cn.cb.btwatermeterpro.bean.CommonBean;
import es.dmoral.toasty.MyToast;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    private Context mContext;
    private List<CommonBean> mList;

    public HomeAdapter(Context context, List<CommonBean> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_home, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(listener);
        holder.title.setText(mList.get(position).getTitle());
        holder.img.setImageResource(mList.get(position).getResId());
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            CommonBean commonBean = mList.get(index);
            if (commonBean.getCls() == null) {
                MyToast.show("功能开发中。。。");
            } else {
                Intent intent = new Intent(mContext, commonBean.getCls());
                intent.putExtra(Intent.EXTRA_TITLE, commonBean.getTitle());
                mContext.startActivity(intent);
            }
        }
    };

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title;
        View itemView;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            img = itemView.findViewById(R.id.home_item_img);
            title = itemView.findViewById(R.id.home_item_title);
        }
    }
}
