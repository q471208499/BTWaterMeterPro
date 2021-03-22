package cn.cb.btwatermeterpro.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.cb.baselibrary.fragment.BaseFragment;
import cn.cb.baselibrary.widget.MyDividerItemDecoration;
import cn.cb.btwatermeterpro.R;
import cn.cb.btwatermeterpro.activity.DownloadActivity;
import cn.cb.btwatermeterpro.activity.HistoryActivity;
import cn.cb.btwatermeterpro.activity.MainActivity;
import cn.cb.btwatermeterpro.activity.PatchActivity;
import cn.cb.btwatermeterpro.activity.SettingActivity;
import cn.cb.btwatermeterpro.adapter.HomeAdapter;
import cn.cb.btwatermeterpro.bean.CommonBean;

public class HomeFragment extends BaseFragment {

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
    }

    private List<CommonBean> getList() {
        List<CommonBean> list = new ArrayList<>();
        list.add(new CommonBean(0, "表册下载", R.mipmap.ic_home_down, DownloadActivity.class));
        list.add(new CommonBean(1, "及时抄表", R.mipmap.ic_home_now, MainActivity.class));
        list.add(new CommonBean(2, "历史查询", R.mipmap.ic_home_his, HistoryActivity.class));
        list.add(new CommonBean(3, "数据补抄", R.mipmap.ic_home_patch, PatchActivity.class));
        list.add(new CommonBean(4, "水表设置", R.mipmap.ic_home_setting, SettingActivity.class));
        return list;
    }


}