package com.anniu.shandiandaojia.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.activity.GoodsCategoryActivity;
import com.anniu.shandiandaojia.adapter.CategoryGoodsAdapter1;
import com.anniu.shandiandaojia.db.jsondb.Goods;
import com.anniu.shandiandaojia.view.pulltorefreshlayout.PullToRefreshLayout;
import com.anniu.shandiandaojia.view.pulltorefreshlayout.pullableview.PullableGridView;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

public class MyFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private static final String ARG_TYPECODE = "typecode";
    private int typeCode;
    private int position;
    public PullableGridView gridview;
    private PullToRefreshLayout pullToRefreshLayout;
    private CategoryGoodsAdapter1 adapter1;
    private List<Goods> goodsList = new ArrayList<Goods>();
    private GoodsCategoryActivity listener;
    public int tempLoadType = 1;

    public static MyFragment newInstance(int position, int code) {
        MyFragment f = new MyFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        b.putInt(ARG_TYPECODE, code);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        typeCode = getArguments().getInt(ARG_TYPECODE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goods_list, null);
        pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
        gridview = (PullableGridView) view.findViewById(R.id.gridview);
        if (listener != null) {
            pullToRefreshLayout.setOnRefreshListener(listener);
            pullToRefreshLayout.setTypecode(typeCode);
        }
        adapter1 = new CategoryGoodsAdapter1(getActivity());
        gridview.setAdapter(adapter1);
        gridview.setOnScrollListener(new PauseOnScrollListener(adapter1.mImageLoader, false, true));
        setDatas(goodsList, GoodsCategoryActivity.LOAD_TYPE_REFRESH);
        return view;
    }

    public void setDatas(List<Goods> dataList, int loadtype) {
        tempLoadType = loadtype;
        if (adapter1 != null && dataList != null) {
            adapter1.setItem(dataList);
        }
    }

    public void setListener(GoodsCategoryActivity listener, int typecode) {
        this.listener = listener;
        listener.onRefresh(pullToRefreshLayout, typecode);
    }

    public void stopRefresh() {
        if (pullToRefreshLayout != null) {
            pullToRefreshLayout.completePull(PullToRefreshLayout.PULL_FINISH_FINISH);
        }
    }

    public void stopLoadMore(int type) {
        if (pullToRefreshLayout != null) {
            pullToRefreshLayout.completePull(type);
        }
    }
}
