package com.anniu.shandiandaojia.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.adapter.BalanceGoodsAdapter;
import com.anniu.shandiandaojia.alipay.PayResult;
import com.anniu.shandiandaojia.alipay.SignUtils;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.CartGoods;
import com.anniu.shandiandaojia.db.jsondb.Coupon;
import com.anniu.shandiandaojia.db.jsondb.MyAddress;
import com.anniu.shandiandaojia.db.jsondb.PaymentWay;
import com.anniu.shandiandaojia.db.jsondb.PrepayIdInfo;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.OrderLogic;
import com.anniu.shandiandaojia.logic.TicketLogic;
import com.anniu.shandiandaojia.utils.CommonUtil;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.anniu.shandiandaojia.utils.Utils;
import com.anniu.shandiandaojia.view.MyDialog;
import com.anniu.shandiandaojia.view.MyListView;
import com.anniu.shandiandaojia.view.lee.wheel.WheelViewTimeActivity;
import com.anniu.shandiandaojia.wxapi.PayActivity;
import com.ant.liao.GifView;
import com.tencent.mm.sdk.constants.Build;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * @author zxl
 * @ClassName: BalanceActivity
 * @Description: 结算页面
 * @date 2015年6月10日 下午12:08:57
 */
public class BalanceActivity extends BaseActivity {
    private TextView titleBarTv, name, phone, address, goodsPrice, sendMoney,
            couponPrice, payPrice, tvRemark, sendTime, selectCoupon;
    private RelativeLayout rlSendTime, rlRemark, rlCoupon, loadingView, errorView;
    private GifView gif;
    private RadioButton rb_hdfk, rb_wxzf;
    private String time, content, startTime, endTime;
    private ImageView leftTitle;
    private MyListView goodsList;
    private Button btCommit;

    private List<CartGoods> cartList = new ArrayList<CartGoods>();
    private BalanceGoodsAdapter goodsAdapter;
    private RadioGroup rgGroup;
    private Coupon coupon;
    public static String CODE = "code";

    private boolean isPaySupported;
    private PrepayIdInfo prepayInfo;
    private boolean isCommit = true;
    public static String EXTRA_FROM = "from";
    public static String EXTRA_ERRCODE = "errorcode";
    private int from = 0;//0，购物车,1,微信支付回调页面
    private int resultCode;//微信支付返回码，0,成功,-1,错误,-2,用户取消

    private double paymentAmount, couponAmount, goodsAmount, postFree;
    private PaymentWay payway = PaymentWay.DAO_FU;
    private int orderNum = 0;
    private Integer couponId = -1;//默认为-1，不可以使用优惠券
    private MyAddress myAddress;
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

    private void showErrorLocal(OnClickListener tryAgainListener) {
        errorView = (RelativeLayout) findViewById(R.id.rl_error);
        errorView.setOnClickListener(tryAgainListener);
        errorView.setVisibility(View.VISIBLE);
    }

    private void hideErrorLocal() {
        errorView.setVisibility(View.GONE);
    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_balance);
        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText(R.string.balance_title);
        leftTitle = (ImageView) findViewById(R.id.iv_logo);
        leftTitle.setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        findViewById(R.id.title_bar_left).setOnClickListener(this);

        if (CommonUtil.isNetworkConnected(context)) {
            initView();
        } else {
            showErrorLocal(tryAgainListener);
        }
    }

    /**
     * 初始化界面显示
     */
    private void initView() {
        Intent intent = getIntent();
        from = intent.getExtras().getInt(EXTRA_FROM);
        if (from != 0) {
            resultCode = intent.getExtras().getInt(EXTRA_ERRCODE);
        }
        getOrderDetailsAction();

        name = (TextView) findViewById(R.id.tv_name);
        phone = (TextView) findViewById(R.id.tv_phone);
        address = (TextView) findViewById(R.id.tv_address);

        goodsPrice = (TextView) findViewById(R.id.tv_total_money);
        sendMoney = (TextView) findViewById(R.id.tv_send_money);
        couponPrice = (TextView) findViewById(R.id.tv_couponmoney);
        payPrice = (TextView) findViewById(R.id.pay_money);

        goodsList = (MyListView) findViewById(R.id.lv_goods_list);
        btCommit = (Button) findViewById(R.id.bt_commit);

        rb_hdfk = (RadioButton) findViewById(R.id.rb_xjzf);
        rb_wxzf = (RadioButton) findViewById(R.id.rb_wxzf);
        tvRemark = (TextView) findViewById(R.id.tv_remark);

        rlSendTime = (RelativeLayout) findViewById(R.id.rl_send_time);
        sendTime = (TextView) findViewById(R.id.tv_send_time);
        rlRemark = (RelativeLayout) findViewById(R.id.rl_remark);

        rlRemark.setOnClickListener(this);

        selectCoupon = (TextView) findViewById(R.id.tv_select_coupon);
        rlCoupon = (RelativeLayout) findViewById(R.id.rl_coupon);
        rlCoupon.setOnClickListener(this);

        //HH
        int time = Integer.parseInt(Utils.getCurrentTimeHHmmStr(Utils.getCurrentSystemTime()));
        sendTime.setText((time) + ":00" + " - " + (time + 1) + ":00");

        startTime = time + ":00";
        endTime = (time + 1) + ":00";
        //给控件设置监听事件
        findViewById(R.id.rll_contacts).setOnClickListener(this);
        btCommit.setOnClickListener(this);
        rlSendTime.setOnClickListener(this);

        rb_hdfk.setOnClickListener(this);
        rb_wxzf.setOnClickListener(this);

        rgGroup = (RadioGroup) findViewById(R.id.rg_group);
        rgGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_xjzf) {
                    payway = PaymentWay.DAO_FU;
                    MyToast.show(BalanceActivity.this, "您选择现金支付");
                } else if (checkedId == R.id.rb_wxzf) {
                    payway = PaymentWay.WX_APP;
                    isPaySupported = App.msgApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
                    if (isPaySupported) {
                        MyToast.show(BalanceActivity.this, "您选择微信支付");
                    } else {
                        MyToast.show(BalanceActivity.this, "您手机未安装微信或者当前版本不支持微信支付！");
                    }
                }
            }
        });

        loadingView = (RelativeLayout) findViewById(R.id.rl_loading);
        loadingView.setVisibility(View.VISIBLE);
        gif = (GifView) findViewById(R.id.gif);
        gif.setGifImage(R.drawable.ic_loading);
        errorView = (RelativeLayout) findViewById(R.id.rl_error);
    }

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        Intent intent;
        switch (v.getId()) {
            case R.id.title_bar_left://关闭
                startActivity(new Intent(this, ShoppingCartActivity.class));
                finish();
                break;
            case R.id.rll_contacts://点击跳转到修改多个地址界面
                intent = new Intent();
                intent.setClass(this, AddressListActivity.class);
                intent.putExtra(AddressListActivity.EXTRA_FROM, 2);
                startActivityForResult(intent, AddressListActivity.REQUESTCODE);
                break;
            case R.id.rl_send_time://点击修改送货时间
                intent = new Intent();
                String currTime = sendTime.getText().toString().trim();
                intent.putExtra(WheelViewTimeActivity.EXTRA_CURRENT_TIME, currTime);
                intent.setClass(this, WheelViewTimeActivity.class);
                startActivityForResult(intent, WheelViewTimeActivity.REQUESTCODE);
                break;
            case R.id.bt_commit://提交订单
                String nameText = name.getText().toString();
                if (TextUtils.isEmpty(nameText)) {
                    MyToast.show(this, "请填写收货人信息！");
                    return;
                }
                if (!isCommit) {
                    return;
                }
                isCommit = !isCommit;
                loadingView.setVisibility(View.VISIBLE);
                if (orderNum != 0) {
                    isCommit = !isCommit;
                    callPay();
                    break;
                }
                //HH
                int time = Integer.parseInt(Utils.getCurrentTimeHHmmStr(Utils.getCurrentSystemTime()));
                startTime = time + ":00";
                endTime = (time + 1) + ":00";
                String remark = tvRemark.getText().toString().trim();

                if (payway.equals(PaymentWay.WX_APP)) {
                    if (!isPaySupported) {
                        isCommit = !isCommit;
                        MyToast.show(BalanceActivity.this, "您手机未安装微信或者当前版本不支持微信支付！");
                        return;
                    }
                }
                postOrderInsertOrder(remark, startTime, endTime, payway, couponId);
                isCommit = !isCommit;
                break;
            case R.id.rl_remark://点击填写备注
                intent = new Intent(this, RemarkActivity.class);
                intent.putExtra(RemarkActivity.CONTENT, content);
                startActivityForResult(intent, RemarkActivity.REQUESTCODE);
                break;
            case R.id.rl_coupon://点击触发优惠券
                String text = selectCoupon.getText().toString();
                if (couponAmount > 0) {
                    intent = new Intent();
                    intent.setClass(this, CouponsActivity.class);
                    intent.putExtra(CODE, 6);
                    intent.putExtra(TicketLogic.EXTRA_TOTALMONEY, goodsAmount);
                    startActivityForResult(intent, CouponsActivity.REQUESTCODE);
                } else if (couponAmount == 0) {
                    MyToast.show(this, text);
                } else {
                    MyToast.show(this, text);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        loadingView.setVisibility(View.GONE);
        switch (eventId) {
            case Event.GET_ACKORDER_SUCESS://结算页数据初始化成功事件
                initOrderData(bundle);
                break;
            case Event.POST_ORDER_INSERTORDER_SUCESS://普通订单提交成功事件
                isCommit = !isCommit;
                orderNum = bundle.getInt(OrderLogic.EXTRA_ORDERNUM);
                callPay();
                break;
            case Event.GET_PREPAY_ID_SUCESS://WX支付获取prepayid成功事件
                prepayInfo = (PrepayIdInfo) bundle.getSerializable(OrderLogic.EXTRA_PREPAY_ID);
                SPUtils.saveInt(this, GlobalInfo.KEY_FROM, 0);
                SPUtils.saveInt(this, GlobalInfo.KEY_ORDER_NUM, orderNum);

                Intent intent = new Intent();
                intent.setClass(this, PayActivity.class);
                intent.putExtra(PayActivity.EXTRA_PAYINFO, prepayInfo);
                startActivity(intent);
                break;
            case Event.POST_ORDER_INSERTORDER_FAILED://普通订单提交失败事件
            case Event.GET_ACKORDER_FAILED:// 结算页数据初始化失败事件
            case Event.GET_PREPAY_ID_FAILED://WX支付获取prepayid失败事件
                MyToast.show(this, notice);
                break;
            default:
                break;
        }
    }

    private void initOrderData(Bundle bundle) {
        hideErrorLocal();
        loadingView.setVisibility(View.GONE);
        myAddress = (MyAddress) bundle.getSerializable(OrderLogic.EXTRA_ADDRESS);
        if (myAddress != null) {
            name.setText(myAddress.getContactName());
            phone.setText(myAddress.getContactTel());
            String district = myAddress.getConsignDistrict();
            if (TextUtils.isEmpty(district)) {
                district = "";
            }
            address.setText(district + myAddress.getConsignAddress());
        }

        goodsAmount = bundle.getDouble(OrderLogic.EXTRA_GOODSAMOUNT);
        postFree = bundle.getDouble(OrderLogic.EXTRA_POSTFEE);
        couponAmount = bundle.getDouble(OrderLogic.EXTRA_COUPONAMOUNT);
        paymentAmount = bundle.getDouble(OrderLogic.EXTRA_PAYMENTAMOUNT);
        couponId = bundle.getInt(OrderLogic.EXTRA_COUPONID);

        goodsPrice.setText("￥" + goodsAmount);
        sendMoney.setText("￥" + postFree);
        couponPrice.setText("-￥" + couponAmount);
        payPrice.setText("总计：￥" + paymentAmount);
        if (couponId == -1) {
            selectCoupon.setText("特价商品不可使用优惠券");
            selectCoupon.setTextColor(getResources().getColor(R.color.red));
        } else if (couponAmount > 0) {
            selectCoupon.setText("￥" + couponAmount);
            selectCoupon.setTextColor(getResources().getColor(R.color.black));
        } else {
            selectCoupon.setText("暂无可用优惠券");
            selectCoupon.setTextColor(getResources().getColor(R.color.red));
        }

        ArrayList<CartGoods> goodslist = (ArrayList<CartGoods>) bundle.getSerializable(OrderLogic.EXTRA_GOODS);
        if (goodslist != null) {
            cartList.clear();
            cartList.addAll(goodslist);
            if (goodsAdapter == null) {
                goodsAdapter = new BalanceGoodsAdapter(this, cartList);
                goodsList.setAdapter(goodsAdapter);
            } else {
                goodsAdapter.notifyDataSetChanged();
            }
        }
    }

    private void callPay() {
        if (payway.equals(PaymentWay.ALI)) {// 支付宝支付
            AliPay();
        } else if (payway.equals(PaymentWay.WX_APP)) {// 微信支付
            getWXPrepayidAction();
        } else if (payway.equals(PaymentWay.BAI_DU)) {// 百度钱包支付

        } else {// 货到付款 DAO_FU
            loadingView.setVisibility(View.GONE);
            Intent intent = new Intent();
            intent.setClass(this, OrderDetailsActivity.class);
            intent.putExtra(OrderLogic.EXTRA_ORDERNUM, orderNum);
            intent.putExtra(OrderDetailsActivity.EXTRA_FROM, 1);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (from == 1) {
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
                int orderId = SPUtils.getInt(ActivityMgr.getContext(), GlobalInfo.KEY_ORDER_NUM, 0);
                Intent intent = new Intent(BalanceActivity.this, OrderDetailsActivity.class);
                intent.putExtra(OrderDetailsActivity.EXTRA_FROM, 2);
                intent.putExtra(OrderLogic.EXTRA_ORDERNUM, orderId);
                startActivity(intent);
                dialog.dismiss();
                finish();
            }
        });

        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddressListActivity.REQUESTCODE) {
            boolean hasAddress = data.getExtras().getBoolean(AddressListActivity.ISHASADDRESS);
            if (hasAddress) {
                MyAddress addressNew = (MyAddress) data.getExtras().get(GlobalInfo.KEY_LOC_INFO);
                if (addressNew != null) {
                    name.setText(addressNew.getContactName());
                    phone.setText(addressNew.getContactTel());
                    String district = addressNew.getConsignDistrict();
                    if (TextUtils.isEmpty(district)) {
                        district = "";
                    }
                    address.setText(district + addressNew.getConsignAddress());
                }
            }
        } else if (requestCode == CouponsActivity.REQUESTCODE) {// 选中的优惠券
            boolean flag = data.getExtras().getBoolean(CouponsActivity.ISHASVOUCHER);
            if (!flag) {
                goodsPrice.setText("￥" + goodsAmount);
                sendMoney.setText("￥" + postFree);
                couponPrice.setText("-￥" + couponAmount);
                payPrice.setText("总计：￥" + paymentAmount);
            } else {
                coupon = (Coupon) data.getSerializableExtra(TicketLogic.EXTRA_COUPON);
                couponId = coupon.getId();
                selectCoupon.setText("￥" + coupon.getAmount());
                couponPrice.setText("-￥" + coupon.getAmount());
                Double paymoneyDouble = goodsAmount - coupon.getAmount();
                BigDecimal paymoney = new BigDecimal(paymoneyDouble);
                paymoney = paymoney.setScale(2, BigDecimal.ROUND_DOWN);
                payPrice.setText("总计：￥" + paymoney);
            }
        } else if (requestCode == WheelViewTimeActivity.REQUESTCODE) {
            time = data.getStringExtra(GlobalInfo.KEY_TIME);
            String[] splitTime = time.split("-");
            startTime = splitTime[0];
            endTime = splitTime[1];
            sendTime.setText(startTime + "-" + endTime);
        } else if (requestCode == RemarkActivity.REQUESTCODE) {
            String text = data.getStringExtra(GlobalInfo.KEY_CONTENT);
            content = text;
            tvRemark.setText(text);
        }
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this,
                Event.GET_ACKORDER_SUCESS, Event.GET_ACKORDER_FAILED,
                Event.POST_ORDER_INSERTORDER_SUCESS, Event.POST_ORDER_INSERTORDER_FAILED,
                Event.GET_PREPAY_ID_SUCESS, Event.GET_PREPAY_ID_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

    /**
     * 发送请求，获取订单页数据
     */
    private void getOrderDetailsAction() {
        sendAction(new Intent(OrderLogic.ACTION_GET_ACKORDER_STATUS));
    }

    /**
     * 获取微信的prepayid
     */
    private void getWXPrepayidAction() {
        Intent intent = new Intent(OrderLogic.ACTION_GET_PREPAYID);
        intent.putExtra(OrderLogic.EXTRA_ORDERNUM, orderNum);
        sendAction(intent);
    }

    /**
     * 普通订单支付
     */
    private void postOrderInsertOrder(String remark, String startTime,
                                      String endTime, PaymentWay payWay, int couponId) {
        Intent intent = new Intent(OrderLogic.ACTION_POST_ORDER_INSERTORDER);
        intent.putExtra(OrderLogic.EXTRA_REMARK, remark);
        intent.putExtra(OrderLogic.EXTRA_STARTTIME, startTime);
        intent.putExtra(OrderLogic.EXTRA_ENDTIME, endTime);
        intent.putExtra(OrderLogic.EXTRA_PAY_WAY, payWay);
        intent.putExtra(OrderLogic.EXTRA_COUPON_CODE, couponId);
        sendAction(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(this, ShoppingCartActivity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //支付宝支付---------------------------------------------------------
    //商户PID
    public static final String PARTNER = "2088011325918143";
    //商户收款账号
    public static final String SELLER = "btfl@buttonad.com";
    //商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANX23iQrRAeE/LYJU7JJiJRUzXQAYD2ktMw104viJX2Jt43aLYA40PRwwChbRQHDOhWDVzw7vLQbAIEqh75L1CkpQA0PUUXS/Ac0FOGVsxVf/iXw3u6VGVpd6bMb/cWHNI1gsfuIOew3DiBC7FQ3po17q/5WErr3RF746tdudVORAgMBAAECgYBjzR+2zTuRKlXZ7yXMDoKXdarThe1eM3plmmHPaK/GC7vDfdYlbdsBeaoX6OKaPN7V9Ap3vQF7HPEyTo08lRAjO//PFCwQRL45LsPiWQ7Wrdvi5Yy91qWGJYLIwv5mDCR3lPIIeFTCHZEvX6zo1bi+/dJyIaToM5PvHFuN+saiAQJBAPvUl1Kp9XJcbmtKc8NpHJAGpehSMeW8Y7beb1AsfFwjGSnhRlpA5dBRGAhxefaVaVmYpEForrbJjA89kC3CNmECQQDZgcb8iHVpPJX0L9OSctd3uFEMf0amz0s57MVgacsegItB3Uyr0qHwKVCcQhYAPDA/ChCgk59LrOOkgZ6HassxAkAAqTIseV23ix6PEYb0QFZe2mWdEnonDDOGy1anczZwV3c9cOXqXrk7rrrNat7TEtlP7uKfeVLcD/NaJuM1+4iBAkAfBkff1YcJz76D6h5/kSnyxbOp2K23NXJBfOhabDywyR0T9ADvsUev/fSllTeWKP+ovkLI5MWgAbUmzeZS4oTBAkEAvwJh8d9EcJs6a65eiYWlPMp/pNuT80UQhFWi0eZmADUpt0OXvl8jefF7QdUSApKAIynimU3GL8BqRG/+0wIVMw==";
    //支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
    private static final int SDK_PAY_FLAG = 1;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Intent intent = new Intent(BalanceActivity.this, OrderDetailsActivity.class);
                        intent.putExtra(OrderLogic.EXTRA_ORDER_NUM, SPUtils.getString(context, GlobalInfo.KEY_ORDER_NUM, ""));
                        startActivity(intent);
                        finish();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            MyToast.show(BalanceActivity.this, "支付结果确认中");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            MyToast.show(BalanceActivity.this, "支付失败");
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    /***
     * call alipay sdk pay. 调用SDK支付
     **/
    public void AliPay() {
        // 订单
        String orderInfo = getOrderInfo("测试的商品", "该测试商品的详细描述", "0.01");
        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
        // 必须异步调用
        Thread payThread = new Thread(new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(BalanceActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        });
        payThread.start();
    }

    /***
     * create the order info. 创建订单信息
     **/
    public String getOrderInfo(String subject, String body, String price) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";
        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";
        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";
        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";
        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";
        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";
        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";
        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";
        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";
        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";
        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";
        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";
        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";
        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";
        return orderInfo;
    }

    /***
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     **/
    public String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    public String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }


    /***
     * get the sign type we use. 获取签名方式
     **/
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }
    //支付宝支付---------------------------------------------------------
}
