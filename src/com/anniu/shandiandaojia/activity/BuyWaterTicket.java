package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.adapter.BuyWaterTicketAdapter;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.Goods;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.TicketLogic;
import com.anniu.shandiandaojia.utils.CommonUtil;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.Utils;

import java.util.ArrayList;

/**
 * @author YY
 * @ClassName: BuyWaterTicket
 * @Description: 水票列表
 * @date 2015年6月16日 下午12:06:04
 */
public class BuyWaterTicket extends BaseActivity implements OnItemClickListener {
    private TextView titleBarTv;//标题
    private ListView listView;
    private BuyWaterTicketAdapter adapter;
    private ArrayList<Goods> goodsList = new ArrayList<Goods>();
    private RelativeLayout loadingView, errorView;

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.title_bar_left://点击返回
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_buy_water_ticket);
        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText(R.string.buy_water_ticket);
        findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        findViewById(R.id.title_bar_left).setOnClickListener(this);

        if (CommonUtil.isNetworkConnected(context)) {
            initView();
        } else {
            showErrorLocal(tryAgainListener);
        }
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(this);

        loadingView = (RelativeLayout) findViewById(R.id.rl_loading);
        loadingView.setVisibility(View.VISIBLE);
        errorView = (RelativeLayout) findViewById(R.id.rl_error);
        getWaterList();
    }

    private void showErrorLocal(OnClickListener tryAgainListener) {
        errorView = (RelativeLayout) findViewById(R.id.rl_error);
        errorView.setOnClickListener(tryAgainListener);
        errorView.setVisibility(View.VISIBLE);
    }

    private void hideErrorLocal() {
        errorView.setVisibility(View.GONE);
    }

    private OnClickListener tryAgainListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (CommonUtil.isNetworkConnected(context)) {
                Intent i = baseIntent;
                if (baseIntent != null) {
                    startActivity(i);
                    finish();
                }
            }
        }
    };

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        switch (eventId) {
            case Event.GET_WATER_FIND_SUCESS://获取购买水漂成功事件
                loadingView.setVisibility(View.GONE);
                hideErrorLocal();
                ArrayList<Goods> goodses = (ArrayList<Goods>) bundle.getSerializable(TicketLogic.EXTRA_WATER_LIST);
                if (goodses != null && goodses.size() > 0) {
                    goodsList.clear();
                    goodsList.addAll(goodses);
                    adapter = new BuyWaterTicketAdapter(this, goodsList);
                    listView.setAdapter(adapter);
                } else {
                    MyToast.show(context, "该超市没有水票！");
                }
                break;
            case Event.GET_WATER_FIND_FAILED://获取购买水漂失败事件
                loadingView.setVisibility(View.GONE);
                MyToast.show(this, notice);
                break;
            default:
                break;
        }
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this,
                Event.GET_WATER_FIND_SUCESS, Event.GET_WATER_FIND_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

    /**
     * 点击单个条目可以查看水票详情
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Goods good = goodsList.get(position);
        Intent intent = new Intent(this, WaterTicketDetailActivity.class);
        intent.putExtra(WaterTicketDetailActivity.EXTRA_GOODSCODE, good.getId());
        intent.putExtra(OrderDetailsActivity.EXTRA_FROM, 0);
        startActivity(intent);
    }

    /**
     * 购买水票列表界面
     */
    private void getWaterList() {
        sendAction(new Intent(TicketLogic.ACTION_GET_WATER_FIND));
    }
}
