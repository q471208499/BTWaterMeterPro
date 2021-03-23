package cn.cb.btwatermeterpro.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.cb.baselibrary.fragment.BaseFragment;
import cn.cb.baselibrary.widget.MyDividerItemDecoration;
import cn.cb.btwatermeterpro.BTApplication;
import cn.cb.btwatermeterpro.R;
import cn.cb.btwatermeterpro.activity.AboutActivity;
import cn.cb.btwatermeterpro.adapter.MyAdapter;
import cn.cb.btwatermeterpro.bean.CommonBean;

public class MyFragment extends BaseFragment {

    public static final int ITEM_ID_EXIT = 3;
    private TextView name, subName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my, container, false);
        bindView(root);
        initData();
        return root;
    }

    private void initData() {
        JSONObject user = BTApplication.getUser();
        String userName = user.getJSONObject("meterInfo").getString("userName");
        name.setText(userName);
    }

    private void bindView(View root) {
        name = root.findViewById(R.id.my_title);
        subName = root.findViewById(R.id.my_sub_title);
        RecyclerView recyclerView = root.findViewById(R.id.my_recycler);
        recyclerView.addItemDecoration(new MyDividerItemDecoration());
        recyclerView.setAdapter(new MyAdapter(this, getList()));
        recyclerView.suppressLayout(true);
    }

    private List<CommonBean> getList() {
        List<CommonBean> list = new ArrayList<>();
        list.add(new CommonBean(2, "关于", R.mipmap.ic_about, AboutActivity.class));
        list.add(new CommonBean(ITEM_ID_EXIT, "退出登录", R.mipmap.ic_exit, null));
        return list;
    }
}