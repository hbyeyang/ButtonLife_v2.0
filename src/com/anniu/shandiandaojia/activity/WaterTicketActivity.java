package com.anniu.shandiandaojia.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.adapter.WaterTicketAdapter;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.WaterInfo;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.TicketLogic;
import com.anniu.shandiandaojia.utils.DialogUtils;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.Utils;

import java.util.ArrayList;

/**
 * @author YY
 * @ClassName: WaterTicketActivity
 * @Description: 我的水票
 * @date 2015年5月30日 下午5:20:31
 */
public class WaterTicketActivity extends BaseActivity {
    private TextView titleBarTv;
    private ListView listview;
    private WaterTicketAdapter waterTicketAdapter;
    private RelativeLayout rl_goumai;
    private ArrayList<WaterInfo> waterList = new ArrayList<>();
    public static String EXTRA_FROM = "from";
    public static String EXTRA_ID = "orderId";
    private int from = 0;//0个人中心，1水票详情支付页面,2一键送水
    private int orderId;//订单id

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        if (v.getId() == R.id.title_bar_left) {//返回
            finish();
        } else if (v.getId() == R.id.rl_goumai) {//继续购买水票
            startActivity(new Intent(this, BuyWaterTicket.class));
        }
    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_water_ticket);
        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText("我的水票");

        findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        findViewById(R.id.title_bar_left).setOnClickListener(this);
        listview = (ListView) findViewById(R.id.lv_waterticketinfo);
        rl_goumai = (RelativeLayout) findViewById(R.id.rl_goumai);
        rl_goumai.setOnClickListener(this);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WaterInfo info = waterList.get(position);
                getWaterUsetickets(info.getId());

                if (from != 2) {
                    Intent intent = new Intent(WaterTicketActivity.this, AKeyWaterActivity.class);
                    intent.putExtra(TicketLogic.EXTRA_TICKETCODE, info.getId());
                    WaterTicketActivity.this.startActivity(intent);
                }
                finish();
            }
        });

        Intent intent = getIntent();
        from = intent.getExtras().getInt(EXTRA_FROM, 0);
        orderId = intent.getExtras().getInt(EXTRA_ID, 0);

        findWaterTicket();
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        switch (eventId) {
            case Event.GET_PERSON_FINDTICKETS_SUCESS://获取个人中心我的水票成功事件
                ArrayList<WaterInfo> waterinfoList = (ArrayList<WaterInfo>) bundle.getSerializable(TicketLogic.EXTRA_VOUCHER_TICKET_LIST);
                if (waterinfoList == null) {
                    MyToast.show(this, "您还没有购买水票！");
                    return;
                }
                setData(waterinfoList);
                if (from == 1) {
                    sendGetCouponAmountAction(orderId);
                }
                break;
            case Event.GET_COUPON_AMOUNT_SUCESS://购买水票返还的优惠券金额成功事件
                double amount = bundle.getDouble(TicketLogic.EXTRA_AMOUNT);
                DialogUtils.showCustomeDialog(this, amount + "元优惠劵已放入账户！赶快任性消费吧！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
            case Event.GET_PERSON_FINDTICKETS_FAILED://获取个人中心我的水票失败事件
            case Event.GET_COUPON_AMOUNT_FAILED://购买水票返还的优惠券金额失败事件
                MyToast.show(this, notice);
                break;
            default:
                break;
        }
    }


    private void setData(ArrayList waterinfoList) {
        waterList.clear();
        waterList.addAll(waterinfoList);
        if (waterTicketAdapter == null) {
            waterTicketAdapter = new WaterTicketAdapter(this, waterList);
            listview.setAdapter(waterTicketAdapter);
        } else {
            waterTicketAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this,
                Event.GET_PERSON_FINDTICKETS_SUCESS, Event.GET_PERSON_FINDTICKETS_FAILED,
                Event.GET_COUPON_AMOUNT_SUCESS, Event.GET_COUPON_AMOUNT_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

    //获取个人中心水票列表
    private void findWaterTicket() {
        sendAction(new Intent(TicketLogic.ACTION_GET_FINDWATERTICKET));
    }

    // 获取一键送水界面提交订单页面数据
    protected void getWaterUsetickets(int tic_code) {
        Intent intent = new Intent(TicketLogic.ACTION_GET_WATER_USETICKETS);
        intent.putExtra(TicketLogic.EXTRA_TICKETCODE, tic_code);
        sendAction(intent);
    }

    // 获取获取购买水票的优惠券金额
    private void sendGetCouponAmountAction(int orderId) {
        Intent intent = new Intent(TicketLogic.ACTION_GET_COUPONAMOUNT);
        intent.putExtra(TicketLogic.EXTRA_NUMBER, orderId);
        sendAction(intent);
    }
}
