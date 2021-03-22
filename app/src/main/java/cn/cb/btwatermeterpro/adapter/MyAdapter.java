package cn.cb.btwatermeterpro.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.cb.btwatermeterpro.R;
import cn.cb.btwatermeterpro.activity.LoginActivity;
import cn.cb.btwatermeterpro.bean.CommonBean;
import cn.cb.btwatermeterpro.fragment.MyFragment;
import es.dmoral.toasty.MyToast;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private MyFragment mContext;
    private List<CommonBean> mList;

    public MyAdapter(MyFragment context, List<CommonBean> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext.getContext()).inflate(R.layout.item_nav_my, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(listener);
        holder.img.setImageResource(mList.get(position).getResId());
        holder.title.setText(mList.get(position).getTitle());
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            CommonBean commonBean = mList.get(index);
            if (commonBean.getId() == MyFragment.ITEM_ID_EXIT) {
                showExit();
            } else if (commonBean.getCls() == null) {
                MyToast.show("功能开发中。。。");
            } else {
                Intent intent = new Intent(mContext.getContext(), commonBean.getCls());
                intent.putExtra(Intent.EXTRA_TITLE, commonBean.getTitle());
                mContext.startActivity(intent);
            }
        }
    };

    private void showExit() {
        AlertDialog builder = new AlertDialog.Builder(mContext.getContext())
                .setTitle("提示")
                .setMessage("确认退出登录？")
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(mContext.getContext(), LoginActivity.class);
                        intent.putExtra(Intent.ACTION_ATTACH_DATA, false);
                        mContext.startActivity(intent);
                        mContext.getActivity().finish();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
        builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        builder.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title;
        View itemView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            img = itemView.findViewById(R.id.item_nav_img);
            title = itemView.findViewById(R.id.item_nav_title);
        }
    }
}
