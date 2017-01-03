package com.anniu.shandiandaojia.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.adapter.ExpandAdapter;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.Goods;
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
import com.anniu.shandiandaojia.view.MyExpandableListView;
import com.anniu.shandiandaojia.wxapi.PayActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.constants.Build;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YY
 * @ClassName: WaterTicketDetailActivity
 * @Description: 水票的详细信息
 * @date 2015年6月17日 下午2:46:24
 */
public class WaterTicketDetailActivity extends BaseActivity {
    public static String EXTRA_GOODSCODE = "code";
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;

    private TextView titleBarTv, name, price;
    private ImageView leftTitle, imgbg;
    private Goods good;
    private Button pay;
    private int code, orderId;
    private MyExpandableListView elv;
    private PrepayIdInfo prepayInfo = null;

    private List<String> group;
    private List<List<String>> child;
    private ExpandAdapter adapter;
    private boolean isCommit = true;
    private RelativeLayout loadingView, errorView;

    int resultCode;
    int from;

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
        setContentView(R.layout.activity_water_detail);

        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText("水票详情");
        leftTitle = (ImageView) findViewById(R.id.iv_logo);
        leftTitle.setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        findViewById(R.id.title_bar_left).setOnClickListener(this);

        if (CommonUtil.isNetworkConnected(context)) {
            initView();
        } else {
            showErrorLocal(tryAgainListener);
        }
    }

    private void initView() {
        Intent intent = getIntent();
        code = intent.getExtras().getInt(EXTRA_GOODSCODE);
        from = intent.getExtras().getInt(OrderDetailsActivity.EXTRA_FROM);
        if (from == 3) {
            resultCode = intent.getExtras().getInt(OrderDetailsActivity.EXTRA_ERRCODE);
        }
        getWaterDetail(code);

        pay = (Button) findViewById(R.id.bt_pay);
        imgbg = (ImageView) findViewById(R.id.iv_img);
        name = (TextView) findViewById(R.id.tv_name);
        price = (TextView) findViewById(R.id.tv_price);
        pay.setOnClickListener(this);
        elv = (MyExpandableListView) findViewById(R.id.elv);
        elv.setGroupIndicator(null);

        loadingView = (RelativeLayout) findViewById(R.id.rl_loading);
        loadingView.setVisibility(View.VISIBLE);
        errorView = (RelativeLayout) findViewById(R.id.rl_error);
        initImageLoader();
    }

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.title_bar_left:// 关闭
                finish();
                break;
            case R.id.bt_pay://支付
                if (!Utils.loginState()) {// 判断是否登录过
                    startActivity(new Intent(this, RegistActivity.class));
                    finish();
                    return;
                }

                if (!isCommit) {
                    return;
                }
                isCommit = !isCommit;
                loadingView.setVisibility(View.VISIBLE);
                if (orderId != 0) {
                    isCommit = !isCommit;
                    getWXPrepayidAction(orderId);
                    return;
                }
                boolean isPaySupported = App.msgApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
                if (isPaySupported) {
                    postOrderInfo();
                } else {
                    MyToast.show(this, "您手机未安装微信或者当前版本不支持微信支付！");
                }
                isCommit = !isCommit;

                break;
            default:
                break;
        }
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        loadingView.setVisibility(View.GONE);
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        switch (eventId) {
            case Event.GET_WATER_DETAIL_SUCESS://获取水票详情成功事件
                hideErrorLocal();
                good = (Goods) bundle.getSerializable(TicketLogic.EXTRA_WATER_DETAIL);

                int w = App.windowWidth;
                LayoutParams params = imgbg.getLayoutParams();
                params.height = (int) (w * 0.84);
                imgbg.setLayoutParams(params);

                mImageLoader.displayImage(good.getPictureUrl(), imgbg, options);
                name.setText(good.getName() + "桶裝水(" + good.getGroupCount() + "桶)");
                price.setText(good.getPrice() + "元");
                initData();
                adapter = new ExpandAdapter(this, group, child);
                elv.setAdapter(adapter);
                elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        setListViewHeight(parent, groupPosition);
                        return false;
                    }
                });
                break;
            case Event.GET_WATER_SUBMIT_SUCESS://购买水票成功事件
                orderId = bundle.getInt(OrderLogic.EXTRA_ORDER_NUM, 0);
                getWXPrepayidAction(orderId);
                break;
            case Event.GET_PREPAY_ID_WATER_SUCESS://WX支付获取prepayid水票页面成功事件
                SPUtils.saveInt(this, GlobalInfo.KEY_FROM, 3);
                SPUtils.saveBoolean(context, GlobalInfo.KEY_ISWATER, true);
                SPUtils.saveInt(this, GlobalInfo.KEY_ORDER_NUM, orderId);
                prepayInfo = (PrepayIdInfo) bundle.getSerializable(OrderLogic.EXTRA_PREPAY_ID);

                Intent intent = new Intent();
                intent.setClass(this, PayActivity.class);
                intent.putExtra(PayActivity.EXTRA_PAYINFO, prepayInfo);
                startActivity(intent);
                break;
            case Event.GET_PREPAY_ID_WATER_FAILED://WX支付获取prepayid水票页面失败事件
            case Event.GET_WATER_DETAIL_FAILED://获取水票详情失败事件
            case Event.GET_WATER_SUBMIT_FAILED://购买水票失败事件
                MyToast.show(this, notice);
                break;
            default:
                break;
        }
    }

    private void setListViewHeight(ExpandableListView parent, int groupPosition) {
        ExpandableListAdapter listAdapter = elv.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(elv.getWidth(), View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, elv);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupItem.getMeasuredHeight();
            if (((elv.isGroupExpanded(i)) && (i != groupPosition)) || ((!elv.isGroupExpanded(i)) && (i == groupPosition))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null, elv);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight();
                }
            }
        }

        ViewGroup.LayoutParams params = elv.getLayoutParams();
        int height = totalHeight + (elv.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        elv.setLayoutParams(params);
        elv.requestLayout();
    }

    private void initData() {
        group = new ArrayList<String>();
        child = new ArrayList<List<String>>();
        addInfo("水票简介", new String[]{
                "水票",
                "1." + good.getPrice() + "元水票含" + good.getName() + good.getGroupCount() + "桶水",
                "2.水票仅限当前购买所在便利店使用",
                "3.水票无使用期限",
                " ",
                "代金券",
                "1." + good.getPrice() + "元水票返" + good.getCouponAmount() + "元代金券.",
                "2.代金券仅限当前购买所在便利店使用",
                "3.代金券请在有效期之内用完."});
        addInfo("使用方法", new String[]{
                "首次购买水票",
                "1.首页点击“一键送水”",
                "2.购买心仪的水票",
                "3.在个人中心-我的水票页点击使用",
                "4.输入收货人信息，提交订单即可",
                " ",
                "已有水票，快递下单",
                "1.首页点击“一键送水”",
                "2.提交订单"});
        addInfo(good.getName() + "桶装水介绍（含真伪鉴别方法）", new String[]{good.getDescription()});
    }

    /**
     * 添加数据信息
     */
    private void addInfo(String g, String[] c) {
        group.add(g);
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < c.length; i++) {
            list.add(c[i]);
        }
        child.add(list);
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
                int orderId = SPUtils.getInt(ActivityMgr.getContext(), GlobalInfo.KEY_ORDER_NUM, 0);
                Intent intent = new Intent(WaterTicketDetailActivity.this, OrderDetailsActivity.class);
                intent.putExtra(OrderDetailsActivity.EXTRA_FROM, 4);
                intent.putExtra(OrderLogic.EXTRA_ORDERNUM, orderId);
                startActivity(intent);
                dialog.dismiss();
                finish();
            }
        });

        builder.create().show();
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this,
                Event.GET_WATER_DETAIL_SUCESS, Event.GET_WATER_DETAIL_FAILED,
                Event.GET_WATER_SUBMIT_SUCESS, Event.GET_WATER_SUBMIT_FAILED,
                Event.GET_PREPAY_ID_WATER_SUCESS, Event.GET_PREPAY_ID_WATER_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

    /**
     * 获取水票详情
     */
    private void getWaterDetail(int goodsCode) {
        Intent intent = new Intent(TicketLogic.ACTION_GET_WATER_DETAIL);
        intent.putExtra(TicketLogic.EXTRA_GOODSCODE, goodsCode);
        sendAction(intent);
    }

    /**
     * 请求公司服务器购买水票
     */
    private void postOrderInfo() {
        Intent intent = new Intent(OrderLogic.ACTION_POST_ORDER_WATER_TICKET);
        intent.putExtra(OrderLogic.EXTRA_GOODS_CODE, good.getId());
        intent.putExtra(OrderLogic.EXTRA_PAY_WAY, PaymentWay.WX_APP);
        sendAction(intent);
    }

    /**
     * WX支付获取prepayid水票
     */
    private void getWXPrepayidAction(int ordernum) {
        Intent intent = new Intent(OrderLogic.ACTION_GET_PREPAYID_WATER);
        intent.putExtra(OrderLogic.EXTRA_ORDERNUM, ordernum);
        sendAction(intent);
    }

    private void initImageLoader() {
        mImageLoader = App.initImageLoader();
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
                .showStubImage(R.drawable.no_data)//加载过程中显示的图像，可以不设置
                .showImageForEmptyUri(R.drawable.no_data) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.no_data) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }
}
