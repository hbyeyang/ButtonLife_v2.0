package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.adapter.CouponAdapter;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.Coupon;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.TicketLogic;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.ToastBasic;
import com.anniu.shandiandaojia.utils.Utils;

import java.util.ArrayList;

/**
 * @author YY
 * @ClassName: CouponsActivity
 * @Description: 优惠券
 * @date 2015年5月30日 下午7:32:55
 */
public class CouponsActivity extends BaseActivity {
    private TextView titleBarTv;//界面标题
    ArrayList<Coupon> couponList = new ArrayList<>();
    private ListView listview;
    private CouponAdapter couponAdapter = null;
    public static int REQUESTCODE = 4;
    public static String ISHASVOUCHER = "hasvoucher";
    private RelativeLayout loadingView;

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        if (v.getId() == R.id.title_bar_left) {
            setDefaut();
        }
    }

    private void setDefaut() {
        Intent intent = new Intent();
        intent.putExtra(ISHASVOUCHER, false);
        setResult(REQUESTCODE, intent);
        finish();
    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_myvoucher);
        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText("我的优惠券");
        findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        findViewById(R.id.title_bar_left).setOnClickListener(this);
        listview = (ListView) findViewById(R.id.lv_voucherticket);

        loadingView = (RelativeLayout) findViewById(R.id.rl_loading);
        loadingView.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        int code = intent.getIntExtra(BalanceActivity.CODE, 0);
        double totalMoney = intent.getDoubleExtra(TicketLogic.EXTRA_TOTALMONEY, 0);

        if (code == 6) {
            getCanBeUseCoupons(totalMoney);
        } else {
            getAllCoupons();
        }
    }


    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        switch (eventId) {
            case Event.GET_FINDUSERCOUPON_SUCESS://获取优惠券成功事件
                loadingView.setVisibility(View.GONE);
                ArrayList<Coupon> coupons = (ArrayList<Coupon>) bundle.getSerializable(TicketLogic.EXTRA_VOUCHER_TICKET_LIST);
                if (coupons != null && coupons.size() > 0) {
                    couponList.clear();
                    couponList.addAll(coupons);
                    if (couponAdapter == null) {
                        couponAdapter = new CouponAdapter(this, couponList);
                        listview.setAdapter(couponAdapter);
                    } else {
                        couponAdapter.notifyDataSetChanged();
                    }
                } else {
                    ToastBasic.showToast(this, "您还没有代金券！");
                }
                break;
            case Event.GET_FINDUSERCOUPON_FAILED://获取优惠券失败事件
                loadingView.setVisibility(View.GONE);
                MyToast.show(this, notice);
                break;
            case Event.GET_ORDER_FINDCOUPON_SUCESS://触发优惠券成功事件
                loadingView.setVisibility(View.GONE);
                ArrayList<Coupon> coupons1 = (ArrayList<Coupon>) bundle.getSerializable(TicketLogic.EXTRA_VOUCHER_TICKET_LIST);
                if (coupons1 != null) {
                    this.couponList.clear();
                    this.couponList.addAll(coupons1);
                    if (couponAdapter == null) {
                        couponAdapter = new CouponAdapter(CouponsActivity.this, this.couponList);
                        listview.setAdapter(couponAdapter);
                    } else {
                        couponAdapter.notifyDataSetChanged();
                    }

                    listview.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Coupon coupon = couponList.get(position);
                            Intent intent = new Intent();
                            intent.putExtra(TicketLogic.EXTRA_COUPON, coupon);
                            intent.putExtra(ISHASVOUCHER, true);
                            setResult(REQUESTCODE, intent);
                            finish();
                        }
                    });
                } else {
                    ToastBasic.showToast(this, "没有代金券！");
                }
                break;
            case Event.GET_ORDER_FINDCOUPON_FAILED://触发优惠券失败事件
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
                Event.GET_FINDUSERCOUPON_SUCESS, Event.GET_FINDUSERCOUPON_FAILED,
                Event.GET_ORDER_FINDCOUPON_SUCESS, Event.GET_ORDER_FINDCOUPON_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setDefaut();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //根据金额获取可用的优惠券
    private void getCanBeUseCoupons(double totalMoney) {
        Intent intent = new Intent(TicketLogic.ACTION_GET_ORDER_FINDCOUPON);
        intent.putExtra(TicketLogic.EXTRA_AMOUNT, totalMoney);
        intent.putExtra(TicketLogic.EXTRA_STATUS, "未使用");
        sendAction(intent);
    }

    //获取所有的优惠券
    private void getAllCoupons() {
        sendAction(new Intent(TicketLogic.ACTION_GET_FINDUSERCOUPON));
    }

}
