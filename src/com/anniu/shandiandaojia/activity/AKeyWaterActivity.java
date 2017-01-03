package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.MyAddress;
import com.anniu.shandiandaojia.db.jsondb.WaterInfo;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.OrderLogic;
import com.anniu.shandiandaojia.logic.TicketLogic;
import com.anniu.shandiandaojia.utils.CommonUtil;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.anniu.shandiandaojia.utils.Utils;
import com.anniu.shandiandaojia.view.lee.wheel.WheelViewTimeActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author YY
 * @ClassName: AKeyWater
 * @Description: 一键送水界面
 * @date 2015年6月16日 下午12:07:08
 */
public class AKeyWaterActivity extends BaseActivity {
    private TextView titleBarTv, name, phone, address, buyNum, surplusNum,
            modify, remark, receiver, sendTime;
    private ImageView waterBg, ivSub, ivAdd;
    private int number = 1;
    private int ticketnum = 0;
    private String contextText = "";
    private WaterInfo waterInfo;
    private MyAddress myAddress;
    private String time;
    int cTime = Integer.parseInt(Utils.getCurrentTimeHHmmStr(Utils.getCurrentSystemTime()));
    private String start_time = cTime + 1 + ":00";
    private String end_time = cTime + 2 + ":00";
    private RelativeLayout rlSendTime, rlRemark, rlContacts, loadingView, errorView;
    private Button commit;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    private int waterCode = 0;

    @Override
    public void onClick(View v) {
        Intent intent;
        if (Utils.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.title_bar_left:// 点击返回
                finish();
                break;
            case R.id.iv_sub:// 数量-1
                number--;
                if (number < 1) {
                    number = 1;
                    MyToast.show(this, "最少要使用" + number + "张水券");
                }
                buyNum.setText(number + "");
                surplusNum.setText((waterInfo.getWaterCount() - number) + "");
                break;
            case R.id.iv_add:// 数量+1
                int numbers = ticketnum;
                number++;
                if (number > numbers) {
                    number = numbers;
                    MyToast.show(this, "您的水券只有" + number + "张");
                }
                buyNum.setText(number + "");
                surplusNum.setText((waterInfo.getWaterCount() - number) + "");
                break;
            case R.id.bt_commit://提交一键送水的订单
                String nameText = name.getText().toString();
                if (TextUtils.isEmpty(nameText)) {
                    MyToast.show(this, "请填写收货人信息！");
                    return;
                }
                getWaterWaterorder();
                break;
            case R.id.tv_modifi://点击修改水票
                intent = new Intent();
                intent.setClass(this, WaterTicketActivity.class);
                intent.putExtra(WaterTicketActivity.EXTRA_FROM, 2);
                startActivity(intent);
                break;
            case R.id.rl_contacts://点击修改地址
                intent = new Intent();
                intent.setClass(this, AddressListActivity.class);
                intent.putExtra(AddressListActivity.EXTRA_FROM, 1);
                startActivityForResult(intent, AddressListActivity.REQUESTCODE);
                break;
            case R.id.rl_send_time://修改送水时间
                intent = new Intent();
                String currTime = sendTime.getText().toString().trim();
                intent.putExtra(WheelViewTimeActivity.EXTRA_CURRENT_TIME, currTime);
                intent.setClass(this, WheelViewTimeActivity.class);
                startActivityForResult(intent, WheelViewTimeActivity.REQUESTCODE);
                break;
            case R.id.rl_remark://点击填写备注
                intent = new Intent(this, RemarkActivity.class);
                intent.putExtra(RemarkActivity.CONTENT, contextText);
                startActivityForResult(intent, RemarkActivity.REQUESTCODE);
                break;
            default:
                break;
        }
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
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_akey_water);
        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText(R.string.a_key_send_water);
        findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        findViewById(R.id.title_bar_left).setOnClickListener(this);

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

    private void initView() {
        waterCode = getIntent().getIntExtra(TicketLogic.EXTRA_TICKETCODE, 0);
        previewAKeyWater(waterCode);

        loadingView = (RelativeLayout) findViewById(R.id.rl_loading);
        loadingView.setVisibility(View.VISIBLE);
        errorView = (RelativeLayout) findViewById(R.id.rl_error);

        initDate();
        setOnClickListener();
        initImageloader();
    }

    /**
     * 初始化控件
     */
    private void initDate() {
        name = (TextView) findViewById(R.id.tv_name);
        phone = (TextView) findViewById(R.id.tv_phone);
        address = (TextView) findViewById(R.id.tv_address);
        rlSendTime = (RelativeLayout) findViewById(R.id.rl_send_time);
        sendTime = (TextView) findViewById(R.id.tv_send_time);

        int time = Integer.parseInt(Utils.getCurrentTimeHHmmStr(Utils.getCurrentSystemTime()));
        sendTime.setText((time + 1) + ":00" + " - " + (time + 2) + ":00");

        waterBg = (ImageView) findViewById(R.id.iv_water);
        ivSub = (ImageView) findViewById(R.id.iv_sub);
        ivAdd = (ImageView) findViewById(R.id.iv_add);
        buyNum = (TextView) findViewById(R.id.num);
        surplusNum = (TextView) findViewById(R.id.tv_num);
        remark = (TextView) findViewById(R.id.tv_remark);
        commit = (Button) findViewById(R.id.bt_commit);
        modify = (TextView) findViewById(R.id.tv_modifi);
        rlContacts = (RelativeLayout) findViewById(R.id.rl_contacts);
        receiver = (TextView) findViewById(R.id.tv_watername);
        rlRemark = (RelativeLayout) findViewById(R.id.rl_remark);
    }

    private void setOnClickListener() {
        rlSendTime.setOnClickListener(this);
        ivSub.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
        commit.setOnClickListener(this);
        modify.setOnClickListener(this);
        rlContacts.setOnClickListener(this);
        rlRemark.setOnClickListener(this);
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        switch (eventId) {
            case Event.GET_WATER_USETICKETS_SUCESS:// 进入一键送水成功事件
                hideErrorLocal();
                loadingView.setVisibility(View.GONE);
                waterInfo = (WaterInfo) bundle.getSerializable(TicketLogic.EXTRA_USERTICKET);
                myAddress = (MyAddress) bundle.getSerializable(TicketLogic.EXTRA_USERADDRESS);
                waterCode = waterInfo.getId();

                if (myAddress != null) {
                    name.setText(myAddress.getContactName());
                    phone.setText(myAddress.getContactTel());
                    String district = myAddress.getConsignDistrict();
                    if (TextUtils.isEmpty(district)) {
                        district = "";
                    }
                    address.setText(district + myAddress.getConsignAddress());
                }

                if (waterInfo != null) {
                    mImageLoader.displayImage(waterInfo.getPictureUrl(), waterBg, options);
                    ticketnum = waterInfo.getWaterCount();
                    surplusNum.setText((waterInfo.getWaterCount() - number) + "");
                    receiver.setText(waterInfo.getGoodsName());
                }
                contextText = remark.getText().toString().trim();
                buyNum.setText(number + "");
                break;
            case Event.GET_WATER_USETICKETS_FAILED:// 进入一键送水失败事件
                loadingView.setVisibility(View.GONE);
                MyToast.show(this, notice);
                break;
            case Event.POST_WATER_ORDER_SUCESS:// 用水票购买水成功事件
                int orderId = bundle.getInt(TicketLogic.EXTRA_TICKETCODE);
                Intent intent = new Intent();
                intent.setClass(this, OrderDetailsActivity.class);
                intent.putExtra(OrderDetailsActivity.EXTRA_FROM, 1);
                intent.putExtra(OrderLogic.EXTRA_ORDERNUM, orderId);
                startActivity(intent);
                finish();
                break;
            case Event.POST_WATER_ORDER_FAILED:// 用水票购买水失败事件
                MyToast.show(this, notice);
                break;
            default:
                break;
        }
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this,
                Event.GET_WATER_USETICKETS_SUCESS,Event.GET_WATER_USETICKETS_FAILED,
                Event.POST_WATER_ORDER_SUCESS, Event.POST_WATER_ORDER_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
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
                    address.setText(district + addressNew.getConsignAddress());
                }
            }
        } else if (requestCode == WheelViewTimeActivity.REQUESTCODE) {
            time = data.getStringExtra(GlobalInfo.KEY_TIME);
            String[] splitTime = time.split("-");
            start_time = splitTime[0];
            end_time = splitTime[1];
            sendTime.setText(start_time + "-" + end_time);
        } else if (requestCode == RemarkActivity.REQUESTCODE) {
            String text = data.getStringExtra(GlobalInfo.KEY_CONTENT);
            contextText = text;
            remark.setText(text);
        }
    }

    /**
     * 获取一键送水界面提交订单页面数据
     */
    private void previewAKeyWater(int ticketId) {
        Intent intent = new Intent(TicketLogic.ACTION_GET_WATER_USETICKETS);
        intent.putExtra(TicketLogic.EXTRA_TICKETCODE, ticketId);
        sendAction(intent);
    }

    /**
     * 一键送水界面提交订单
     */
    private void getWaterWaterorder() {
        String memo = remark.getText().toString().trim();
        Intent intent = new Intent(TicketLogic.ACTION_POST_WATER_WATERORDER);
        intent.putExtra(TicketLogic.EXTRA_SHOP_CODE, SPUtils.getInt(this, GlobalInfo.KEY_SHOPCODE, 0));
        intent.putExtra(TicketLogic.EXTRA_TICKETCODE, waterCode);
        intent.putExtra(TicketLogic.EXTRA_NUMBER, number);
        intent.putExtra(TicketLogic.EXTRA_NOTE, memo);
        intent.putExtra(TicketLogic.EXTRA_STARTTIME, start_time);
        intent.putExtra(TicketLogic.EXTRA_ENDTIME, end_time);
        sendAction(intent);
    }

    private void initImageloader() {
        mImageLoader = App.initImageLoader();
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
                .showStubImage(R.drawable.no_data)// 加载过程中显示的图像，可以不设置
                .showImageForEmptyUri(R.drawable.no_data) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.no_data) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }
}
