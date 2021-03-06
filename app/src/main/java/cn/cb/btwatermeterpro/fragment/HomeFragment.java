package cn.cb.btwatermeterpro.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import cn.cb.baselibrary.fragment.BaseFragment;
import cn.cb.baselibrary.widget.MyDividerItemDecoration;
import cn.cb.btwatermeterpro.BTApplication;
import cn.cb.btwatermeterpro.R;
import cn.cb.btwatermeterpro.activity.DownloadActivity;
import cn.cb.btwatermeterpro.activity.HistoryActivity;
import cn.cb.btwatermeterpro.activity.MainActivity;
import cn.cb.btwatermeterpro.activity.NavActivity;
import cn.cb.btwatermeterpro.activity.PatchActivity;
import cn.cb.btwatermeterpro.activity.SettingActivity;
import cn.cb.btwatermeterpro.adapter.HomeAdapter;
import cn.cb.btwatermeterpro.bean.CommonBean;

public class HomeFragment extends BaseFragment {
    private SwipeRefreshLayout refreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        bindView(root);
        return root;
    }

    private void bindView(View root) {
        root.findViewById(R.id.home_banner);
        RecyclerView recyclerView = root.findViewById(R.id.home_recycler);
        recyclerView.setAdapter(new HomeAdapter(getContext(), getList()));
        recyclerView.addItemDecoration(new MyDividerItemDecoration());
        recyclerView.suppressLayout(true);
        setOutline(recyclerView);

        refreshLayout = root.findViewById(R.id.home_refresh);
        refreshLayout.setOnRefreshListener(refreshListener);
    }

    private SwipeRefreshLayout.OnRefreshListener refreshListener = () -> {
        if (getActivity() == null) return;
        NavActivity navActivity = (NavActivity) getActivity();
        if (!navActivity.checkConnected(BTApplication.getConnectAddress())) navActivity.startScan();
        refreshLayout.setRefreshing(false);
    };

    private List<CommonBean> getList() {
        List<CommonBean> list = new ArrayList<>();
        list.add(new CommonBean(0, "????????????", R.mipmap.ic_home_down, DownloadActivity.class));
        list.add(new CommonBean(1, "????????????", R.mipmap.ic_home_now, MainActivity.class));
        list.add(new CommonBean(2, "????????????", R.mipmap.ic_home_his, HistoryActivity.class));
        list.add(new CommonBean(3, "????????????", R.mipmap.ic_home_patch, PatchActivity.class));
        list.add(new CommonBean(4, "????????????", R.mipmap.ic_home_setting, SettingActivity.class));
        return list;
    }


}