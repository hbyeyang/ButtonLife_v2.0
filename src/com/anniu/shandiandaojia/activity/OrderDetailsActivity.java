package com.anniu.shandiandaojia.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.adapter.GoodsAdapter;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.OrderGoods;
import com.anniu.shandiandaojia.db.jsondb.OrderInfo;
import com.anniu.shandiandaojia.db.jsondb.OrderStatus;
import com.anniu.shandiandaojia.db.jsondb.PaymentWay;
import com.anniu.shandiandaojia.db.jsondb.PrepayIdInfo;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.OrderLogic;
import com.anniu.shandiandaojia.utils.CommonUtil;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.anniu.shandiandaojia.utils.Utils;
import com.anniu.shandiandaojia.view.MyDialog;
import com.anniu.shandiandaojia.wxapi.PayActivity;
import com.tencent.mm.sdk.constants.Build;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author YY
 * @ClassName: OrderDetailsActivity
 * @Description: 订单详情
 * @date 2015年6月9日 下午1:25:21
 */
public class OrderDetailsActivity extends BaseActivity {
    private String shopTel, textShopName, transaddr, userTel, district;
    private ImageView ivOrderSend, ivComp, ivSend, ivOrderComp,
            ivOrderStatus;
    private TextView tvOrderNum, tvOrderTime, tvOrderPayWay,
            tvUserPhone, tvUserAddr, shop_name, btEvaluation,
            btOrderAgain, tvPayMoney, tvOrderComp, tvOrderCompContent,
            titleBarTv, tvSend, tvComp, couponAmount, postAmount, receiveCode;
    private PaymentWay paymentWay;
    private double payAmount;
    private ArrayList<OrderGoods> goodslist = new ArrayList<OrderGoods>();
    private OrderInfo orderInfo = null;
    private ListView lvOrderlist;// 展示订单里面的商品
    private Boolean isCalling = true;
    private LinearLayout llEvaluation, llStatus, llUserPhone, llUserAddress;
    private RelativeLayout loadingView, errorView, llNopay;

    public static String EXTRA_FROM = "from";
    private int from = 0;//0订单列表，1订单确认页面,2订单预览界面,3微信回调,4水票详情页
    private int orderNum;
    public static String EXTRA_ERRCODE = "errorcode";

    private PrepayIdInfo prepayInfo;
    private long orderTime;
    private int resultCode;//微信支付返回码，0,成功,-1,错误,-2,用户取消

    private PopupWindow popupWindow;// 弹窗
    private String cancelCanse;
    private String cause[] = {"您确定取消该订单", "个人信息填写错误", "选错商品", "长时间未配送", "其他", "返回"};

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.title_bar_left:
                if (from == 0) {
                    finish();
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    SPUtils.saveBoolean(this, GlobalInfo.KEY_NEEDLOAD, true);
                    finish();
                }
                break;
            case R.id.title_bar_right:
                if (isCalling) {
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + shopTel)));
                    isCalling = false;
                }
                break;
            case R.id.bt_order_again:
                SPUtils.saveBoolean(this, GlobalInfo.KEY_NEEDLOAD, true);
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.bt_evaluation:
                complaint(orderInfo);
                break;
            case R.id.tv_cancle_order:
                if (from == 2) {//2订单预览界面过来，只刷新该页面
                    showCansePopuWindows(orderNum);
                } else {
                    cancelSendAction();
                }
                break;
            case R.id.tv_pay_order:
                boolean isPaySupported = App.msgApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
                if (isPaySupported) {
                    getWXPrepayidAction(orderInfo.getId());
                } else {
                    MyToast.show(this, "您手机未安装微信或者当前版本不支持微信支付！");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_order_status_details);
        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText("订单详情");
        findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));//点击返回
        findViewById(R.id.title_bar_left).setOnClickListener(this);
        findViewById(R.id.iv_logo_right).setBackgroundDrawable(getResources().getDrawable(R.drawable.shop_call));//点击拨打店主电话
        findViewById(R.id.title_bar_right).setOnClickListener(this);

        if (CommonUtil.isNetworkConnected(context)) {
            initView();
        } else {
            showErrorLocal(tryAgainListener);
        }
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

    private void initView() {
        Intent intent = getIntent();
        orderNum = intent.getExtras().getInt(OrderLogic.EXTRA_ORDERNUM, 0);
        from = intent.getExtras().getInt(EXTRA_FROM, 0);
        if (from == 3) {
            resultCode = intent.getExtras().getInt(EXTRA_ERRCODE);
        }
        SPUtils.saveInt(this, GlobalInfo.KEY_ORDER_NUM, orderNum);

        btEvaluation = (TextView) findViewById(R.id.bt_evaluation);
        btOrderAgain = (TextView) findViewById(R.id.bt_order_again);
        btOrderAgain.setOnClickListener(this);
        ivOrderStatus = (ImageView) findViewById(R.id.iv_order_status);
        lvOrderlist = (ListView) findViewById(R.id.lv_orderlist);

        ivOrderSend = (ImageView) findViewById(R.id.iv_order_send);
        ivSend = (ImageView) findViewById(R.id.iv_send);
        ivOrderComp = (ImageView) findViewById(R.id.iv_order_comp);
        ivComp = (ImageView) findViewById(R.id.iv_comp);
        tvOrderComp = (TextView) findViewById(R.id.tv_order_comp);
        tvOrderCompContent = (TextView) findViewById(R.id.tv_order_comp_content);

        llStatus = (LinearLayout) findViewById(R.id.ll_status);
        llEvaluation = (LinearLayout) findViewById(R.id.ll_evaluation);
        tvOrderNum = (TextView) findViewById(R.id.tv_order_num);
        tvOrderTime = (TextView) findViewById(R.id.tv_order_time);
        tvOrderPayWay = (TextView) findViewById(R.id.tv_order_pay_way);
        tvUserPhone = (TextView) findViewById(R.id.tv_user_phone);
        tvUserAddr = (TextView) findViewById(R.id.tv_user_addr);
        shop_name = (TextView) findViewById(R.id.shop_name);
        tvPayMoney = (TextView) findViewById(R.id.tv_pay_money);
        tvSend = (TextView) findViewById(R.id.tv_send);
        tvComp = (TextView) findViewById(R.id.tv_comp);

        couponAmount = (TextView) findViewById(R.id.tv_voucher);
        postAmount = (TextView) findViewById(R.id.send_money);

        loadingView = (RelativeLayout) findViewById(R.id.rl_loading);
        loadingView.setVisibility(View.VISIBLE);
        errorView = (RelativeLayout) findViewById(R.id.rl_error);

        llNopay = (RelativeLayout) findViewById(R.id.ll_nopay);
        findViewById(R.id.tv_cancle_order).setOnClickListener(this);
        findViewById(R.id.tv_pay_order).setOnClickListener(this);

        llUserPhone = (LinearLayout) findViewById(R.id.ll_user_phone);
        llUserAddress = (LinearLayout) findViewById(R.id.ll_user_address);
        receiveCode = (TextView) findViewById(R.id.tv_receive_code);

        getOrderDetail();
    }

    private void setData(ImageView ivOrderSend, ImageView ivSend,
                         ImageView ivOrderComp, ImageView ivComp,
                         LinearLayout llEvaluation, TextView tvOrderNum,
                         TextView tvOrderTime, TextView tvOrderPayWay,
                         TextView tvUserPhone, TextView tvUserAddr, TextView shopName,
                         ListView lvOrderlist, TextView tvPayMoney, OrderInfo orderinfo, TextView receiveCode) {

        shopTel = orderinfo.getShop().getTel();//店主电话
        String orderNum = orderinfo.getNo();//获取订单编号
        orderTime = orderinfo.getCreateTime();//下单时间
        paymentWay = orderinfo.getPayment().getPaymentWay();//支付方式
        textShopName = orderinfo.getShop().getName();//商店名称
        payAmount = orderinfo.getPaymentAmount();//支付金额
        couponAmount.setText("￥" + orderinfo.getCouponAmount());
        postAmount.setText("￥" + orderinfo.getPostFee());
        receiveCode.setText(orderinfo.getReceivedCode());

        shopName.setText(textShopName);
        tvOrderNum.setText(orderNum);
        String string2Time = Utils.string2Time(orderTime + "");
        tvOrderTime.setText(string2Time);
        if (paymentWay.equals(PaymentWay.ALI)) {
            tvOrderPayWay.setText("支付宝");
        } else if (paymentWay.equals(PaymentWay.WX_APP)) {
            tvOrderPayWay.setText("微信支付");
        } else if (paymentWay.equals(PaymentWay.BAI_DU)) {
            tvOrderPayWay.setText("百度钱包");
        } else if (paymentWay.equals(PaymentWay.DAO_FU)) {
            tvOrderPayWay.setText("货到付款");
        } else if (paymentWay.equals(PaymentWay.WATER_TICKET)) {
            tvOrderPayWay.setText("水票支付");
        } else {
            tvOrderPayWay.setText("微信支付");
        }

        if (orderinfo.getAddress() != null) {
            userTel = orderinfo.getAddress().getContactTel();//用户电话
            district = orderinfo.getAddress().getConsignDistrict();//区
            transaddr = orderinfo.getAddress().getConsignAddress();//用户收货地址
            if (TextUtils.isEmpty(district)) {
                district = "";
            }
            if (TextUtils.isEmpty(transaddr)) {
                transaddr = "";
            }
        }

        String type = orderinfo.getType();
        if (type.equals("水票订单")) {
            llEvaluation.setVisibility(View.VISIBLE);
            llUserPhone.setVisibility(View.GONE);
            llUserAddress.setVisibility(View.GONE);
        } else if (type.equals("水订单")) {
            llUserPhone.setVisibility(View.VISIBLE);
            llUserAddress.setVisibility(View.VISIBLE);
            tvUserPhone.setText(userTel);
            tvUserAddr.setText(district + transaddr);
        } else {
            llUserPhone.setVisibility(View.VISIBLE);
            llUserAddress.setVisibility(View.VISIBLE);
            tvUserPhone.setText(userTel);
            tvUserAddr.setText(district + transaddr);
        }

        OrderStatus status = orderinfo.getStatus();//获取订单的状态
        if (status.equals(OrderStatus.已提交)) {//已提交
            llStatus.setVisibility(View.VISIBLE);
            llEvaluation.setVisibility(View.GONE);
        } else if (status.equals(OrderStatus.发货中)) {//发货中
            llStatus.setVisibility(View.VISIBLE);
            ivOrderSend.setBackgroundColor(getResources().getColor(R.color.text_color));
            ivSend.setBackgroundDrawable(getResources().getDrawable(R.drawable.order_status_send2));
            llEvaluation.setVisibility(View.GONE);
            tvSend.setTextColor(this.getResources().getColor(R.color.order_text_color));
        } else if (status.equals(OrderStatus.已完成)) {//已完成订单
            llStatus.setVisibility(View.VISIBLE);
            llEvaluation.setVisibility(View.GONE);
            ivOrderSend.setBackgroundColor(getResources().getColor(R.color.text_color));
            ivSend.setBackgroundDrawable(getResources().getDrawable(R.drawable.order_status_send2));
            tvSend.setTextColor(this.getResources().getColor(R.color.order_text_color));
            ivOrderComp.setBackgroundColor(getResources().getColor(R.color.text_color));
            ivComp.setBackgroundDrawable(getResources().getDrawable(R.drawable.order_status_receiving2));
            tvComp.setTextColor(this.getResources().getColor(R.color.order_text_color));
        } else if (status.equals(OrderStatus.老板已取消)) {//老板已取消
            llStatus.setVisibility(View.GONE);
            btEvaluation.setVisibility(View.VISIBLE);
            tvOrderComp.setText("订单取消");
            tvOrderCompContent.setText("如果该订单为线上支付方式，订单金额将于三个工作日之内返还您的账户。");
            btEvaluation.setOnClickListener(this);
        } else if (status.equals(OrderStatus.用户已取消)) {//用户已取消
            llStatus.setVisibility(View.GONE);
            tvOrderComp.setText("订单取消");
            tvOrderCompContent.setText("如果该订单为线上支付方式，订单金额将于三个工作日之内返还您的账户。");
            btEvaluation.setVisibility(View.GONE);
        } else if (status.equals(OrderStatus.已退款)) {//已退款
            llStatus.setVisibility(View.GONE);
            llEvaluation.setVisibility(View.VISIBLE);
            tvOrderComp.setText("订单已退款");
            tvOrderCompContent.setText("感谢使用闪电到家，期待您的下次光临");
            ivOrderStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.order_money));
        } else {
            llStatus.setVisibility(View.GONE);
            llEvaluation.setVisibility(View.GONE);
            llNopay.setVisibility(View.VISIBLE);
        }
        GoodsAdapter goodsAdapter = new GoodsAdapter(goodslist, this);
        lvOrderlist.setAdapter(goodsAdapter);
        BigDecimal bdpaymoney = new BigDecimal(payAmount);
        bdpaymoney = bdpaymoney.setScale(2, BigDecimal.ROUND_DOWN);
        tvPayMoney.setText(" ￥" + bdpaymoney);
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        loadingView.setVisibility(View.GONE);
        switch (eventId) {
            case Event.GET_ORDER_STATUS_SUCESS://获取订单详情成功事件
                hideErrorLocal();
                orderInfo = (OrderInfo) bundle.getSerializable(OrderLogic.EXTRA_ORDERINFO);

                ArrayList<OrderGoods> list = (ArrayList<OrderGoods>) orderInfo.getGoods();
                if (list != null && list.size() > 0) {
                    goodslist.clear();
                    goodslist.addAll(list);
                }
                setData(ivOrderSend, ivSend, ivOrderComp, ivComp, llEvaluation, tvOrderNum,
                        tvOrderTime, tvOrderPayWay, tvUserPhone, tvUserAddr, shop_name,
                        lvOrderlist, tvPayMoney, orderInfo, receiveCode);
                break;
            case Event.POST_COMPLAINORDER_SUCESS://投诉商铺成功事件
                MyToast.show(this, "您的投诉我们已收到，会在三天内给您答复！");
                break;
            case Event.GET_PREPAY_ID_DETAIL_TRUE_SUCESS://WX支付获取prepayid成功事件
                prepayInfo = (PrepayIdInfo) bundle.getSerializable(OrderLogic.EXTRA_PREPAY_ID);
                SPUtils.saveInt(this, GlobalInfo.KEY_FROM, 1);

                Intent intent = new Intent();
                intent.setClass(this, PayActivity.class);
                intent.putExtra(PayActivity.EXTRA_PAYINFO, prepayInfo);
                startActivity(intent);
                break;
            case Event.POST_CANCEL_ORDER_DETAIL_SUCESS://订单详情页面取消订单成功事件
                findAllOrders();
                finish();
                break;
            case Event.POST_CANCEL_ORDER_DETAIL_FROM_BALANCE_SUCESS://刷新该页面，不跳转
                getOrderDetail();
                break;
            case Event.GET_ORDER_STATUS_FAILED://获取订单详情失败事件
            case Event.POST_CANCEL_ORDER_DETAIL_FAILED://订单详情页面取消订单失败事件
            case Event.GET_PREPAY_ID_DETAIL_TRUE_FAILED://WX支付获取prepayid失败事件
            case Event.POST_COMPLAINORDER_FAILED://投诉商铺失败事件
            case Event.POST_CANCEL_ORDER_DETAIL_FROM_BALANCE_FAILED://刷新该页面，不跳转
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
        App.getInstance().addListener(this,
                Event.GET_ORDER_STATUS_SUCESS, Event.GET_ORDER_STATUS_FAILED,
                Event.POST_COMPLAINORDER_SUCESS, Event.POST_COMPLAINORDER_FAILED,
                Event.GET_PREPAY_ID_DETAIL_TRUE_SUCESS, Event.GET_PREPAY_ID_DETAIL_TRUE_FAILED,
                Event.POST_CANCEL_ORDER_DETAIL_SUCESS, Event.POST_CANCEL_ORDER_DETAIL_FAILED,
                Event.POST_CANCEL_ORDER_DETAIL_FROM_BALANCE_SUCESS, Event.POST_CANCEL_ORDER_DETAIL_FROM_BALANCE_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (from == 0) {
                finish();
            } else {
                startActivity(new Intent(this, MainActivity.class));
                SPUtils.saveBoolean(this, GlobalInfo.KEY_NEEDLOAD, true);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 跳转界面投诉商铺
     */
    private void complaint(OrderInfo orderInfo) {
        Intent intent = new Intent();
        int orderNum = orderInfo.getId();
        int shopId = orderInfo.getShopId();
        int userId = orderInfo.getUserId();
        intent.putExtra(GlobalInfo.KEY_ORDER_NUM, orderNum);
        intent.putExtra(GlobalInfo.KEY_SHOPCODE, shopId);
        intent.putExtra(GlobalInfo.KEY_USERCODE, userId);
        intent.setClass(this, ComplaintActivity.class);
        startActivity(intent);
    }

    /**
     * 根据订单编号获取订单详细信息
     */
    public void getOrderDetail() {
        Intent intent = new Intent(OrderLogic.ACTION_GET_ORDER_DETAIL);
        intent.putExtra(OrderLogic.EXTRA_ORDER_NUM, orderNum);
        sendAction(intent);
    }

    /**
     * 点击发送取消订单的请求
     */
    private void cancelSendAction() {
        String cst = Utils.getCurrentSystemTime() + "";
        Intent intent = new Intent(OrderLogic.ACTION_POST_CANCEL_ORDER_DETAIL);
        intent.putExtra(OrderLogic.EXTRA_ORDER_NUM, orderNum);
        intent.putExtra(OrderLogic.EXTRA_CANCELCANSE, cst);
        sendAction(intent);
    }

    /**
     * 获取微信的prepayid
     */
    private void getWXPrepayidAction(int ordernum) {
        Intent intent = new Intent(OrderLogic.ACTION_GET_PREPAYID_ORDERDETAIL);
        intent.putExtra(OrderLogic.EXTRA_ORDERNUM, ordernum);
        App.getInstance().sendAction(intent);
    }

    /**
     * 根据用户ID查询所有订单
     */
    private void findAllOrders() {
        sendAction(new Intent(OrderLogic.ACTION_GET_USER_ORDER));
    }

    /**
     * 点击发送取消订单的请求
     */
    private void cancelAction(int orderNum, String cancelCanse) {
        Intent intent = new Intent(OrderLogic.ACTION_POST_CANCEL_ORDER_DETAIL_FROM_BALANCE);
        intent.putExtra(OrderLogic.EXTRA_ORDER_NUM, orderNum);
        intent.putExtra(OrderLogic.EXTRA_CANCELCANSE, cancelCanse);
        sendAction(intent);
        loadingView.setVisibility(View.VISIBLE);
    }


    /**
     * 取消订单弹窗
     */
    protected void showCansePopuWindows(final int orderNum) {
        View view = this.getLayoutInflater().inflate(R.layout.group_list, null, true);
        ListView lsitview = (ListView) view.findViewById(R.id.lv_list);

        lsitview.setAdapter(new ArrayAdapter<String>(this, R.layout.item_group, R.id.tv_text, cause));
        lsitview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (cause[position].equals("您确定取消该订单")) {
                    MyToast.show(OrderDetailsActivity.this, "请选择取消订单原因！");
                    return;
                }
                if (cause[position].equals("返回")) {
                    popupWindow.dismiss();
                    popupWindow = null;
                } else {
                    cancelCanse = cause[position];
                    /**点击发送取消订单的请求*/
                    cancelAction(orderNum, cancelCanse);
                    popupWindow.dismiss();
                    popupWindow = null;
                }
            }
        });
        view.setFocusableInTouchMode(true);
        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.style_ppw);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
}
