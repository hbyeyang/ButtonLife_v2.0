package com.anniu.shandiandaojia.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.adapter.OrderListAdapter;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.OrderInfo;
import com.anniu.shandiandaojia.db.jsondb.PrepayIdInfo;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.OrderLogic;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.anniu.shandiandaojia.utils.Utils;
import com.anniu.shandiandaojia.view.MyDialog;
import com.anniu.shandiandaojia.wxapi.PayActivity;
import com.tencent.mm.sdk.constants.Build;

import java.util.ArrayList;

/**
 * @author YY
 * @ClassName: MyOrderActivity
 * @Description: 我的订单展示界面
 * @date 2015年6月3日 下午3:12:40
 */
public class MyOrderActivity extends BaseActivity implements OnItemClickListener {
    private TextView titleBarTv, gohome;// 界面标题
    private ListView listview;// listview展示数据的
    private OrderListAdapter orderListAdapter;
    private ArrayList<OrderInfo> orders = new ArrayList<OrderInfo>();
    private FrameLayout emptyLayout;

    private PrepayIdInfo prepayInfo;
    public RelativeLayout loadingView;
    int resultCode;
    int from;

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.go_home://先去逛逛，跳转到主页
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_myorder);
        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText("我的订单");

        findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        findViewById(R.id.title_bar_left).setOnClickListener(this);
        emptyLayout = (FrameLayout) findViewById(R.id.fr_layout);
        gohome = (TextView) findViewById(R.id.go_home);
        gohome.setOnClickListener(this);

        listview = (ListView) findViewById(R.id.listview_order);
        listview.setOnItemClickListener(this);

        loadingView = (RelativeLayout) findViewById(R.id.rl_loading);
        loadingView.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        from = intent.getExtras().getInt(OrderDetailsActivity.EXTRA_FROM);
        if (from == 3) {
            resultCode = intent.getExtras().getInt(OrderDetailsActivity.EXTRA_ERRCODE);
        }

        findAllOrders();
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        loadingView.setVisibility(View.GONE);
        switch (eventId) {
            case Event.GET_USER_ALLORDERS_SUCESS://获取用户订单成功事件
                ArrayList<OrderInfo> orderList = (ArrayList<OrderInfo>) bundle.getSerializable(OrderLogic.EXTRA_ORDERLIST);
                if (orderList == null) {// 没有订单
                    emptyLayout.setVisibility(View.VISIBLE);
                } else {
                    emptyLayout.setVisibility(View.GONE);
                    orders.clear();
                    orders.addAll(orderList);
                    if (null == orderListAdapter) {
                        orderListAdapter = new OrderListAdapter(this, orders);
                        listview.setAdapter(orderListAdapter);
                    } else {
                        orderListAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case Event.GET_USER_ALLORDERS_FAILED://获取用户订单失败事件
                MyToast.show(this, notice);
                break;
            case Event.POST_CANCEL_ORDER_SUCESS://取消订单成功事件
                findAllOrders();
                break;
            case Event.POST_CANCEL_ORDER_FAILED://取消订单失败事件
                MyToast.show(this, notice);
                break;
            case Event.GET_CONFIRM_ORDER_SUCESS://确认订单成功事件
                findAllOrders();
                break;
            case Event.GET_CONFIRM_ORDER_FAILED://确认订单失败事件
                MyToast.show(this, notice);
                break;
            case Event.GET_PREPAY_ID_DETAIL_ADAPTER_SUCESS://WX支付详情页面获取prepayid成功事件
                prepayInfo = (PrepayIdInfo) bundle.getSerializable(OrderLogic.EXTRA_PREPAY_ID);
                boolean isPaySupported = App.msgApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
                if (isPaySupported) {
                    SPUtils.saveInt(this, GlobalInfo.KEY_FROM, 2);

                    Intent intent = new Intent();
                    intent.setClass(this, PayActivity.class);
                    intent.putExtra(PayActivity.EXTRA_PAYINFO, prepayInfo);
                    startActivity(intent);
                } else {
                    MyToast.show(this, "您手机未安装微信或者当前版本不支持微信支付！");
                }
                break;
            case Event.GET_PREPAY_ID_DETAIL_ADAPTER_FAILED://WX支付详情页面获取prepayid失败事件
                MyToast.show(this, notice);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (from == 3) {
            if (resultCode == -1) {
                showNoticeDialog(this, "支付失败", "提示", true);
            } else {
                showNoticeDialog(this, "用户取消", "提示", true);
            }
            from = 0;
        }
    }

    /**
     * 提示对话框
     */
    public void showNoticeDialog(final Context context, String message, String title,
                                 final boolean isForce) {
        final MyDialog.Builder builder = new MyDialog.Builder(context);
        builder.setCancelable(!isForce);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setConfirmButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this, Event.GET_USER_ALLORDERS_SUCESS,
                Event.GET_USER_ALLORDERS_FAILED, Event.POST_CANCEL_ORDER_SUCESS,
                Event.POST_CANCEL_ORDER_FAILED, Event.GET_CONFIRM_ORDER_SUCESS,
                Event.GET_CONFIRM_ORDER_FAILED, Event.GET_PREPAY_ID_DETAIL_ADAPTER_SUCESS,
                Event.GET_PREPAY_ID_DETAIL_ADAPTER_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

    /**
     * 根据用户ID查询所有订单
     */
    private void findAllOrders() {
        sendAction(new Intent(OrderLogic.ACTION_GET_USER_ORDER));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /**根据订单编号获取订单详细信息*/
        Intent intent = new Intent(this, OrderDetailsActivity.class);
        intent.putExtra(OrderLogic.EXTRA_ORDERNUM, orders.get(position).getId());
        intent.putExtra(OrderDetailsActivity.EXTRA_FROM, 0);
        startActivity(intent);
    }
}
