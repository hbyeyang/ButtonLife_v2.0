package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.adapter.ShopListAdapter;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.ShopInfo;
import com.anniu.shandiandaojia.db.jsondb.ShopStatus;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.LocationLogic;
import com.anniu.shandiandaojia.utils.CommonUtil;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.anniu.shandiandaojia.utils.Utils;

import java.util.ArrayList;

/**
 * @author zxl
 * @ClassName: FamliyMartActivity
 * @Description: 附近便利店
 * @date 2015年5月29日 下午7:33:49
 */
public class FamliyMartActivity extends BaseActivity {
    public static String EXTRA_FROM = "from";
    public static String EXTRA_ADDRESS = "address";
    public static String EXTRA_M = "ModifyLocationActivity";
    public static String EXTRA_L = "LocationActivity";
    public static String EXTRA_MAIN = "MainActivity";
    public static String EXTRA_C = "GoodsCategoryActivity";

    private ImageView leftTitle;
    private TextView titleBarTv, shopAddr;
    private ListView listview;

    private ArrayList<ShopInfo> shopList = new ArrayList<ShopInfo>();
    private ShopListAdapter adapter;
    private String fromActivity = "";
    private String address = "";
    private RelativeLayout loadingView, errorView;

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.title_bar_left:
                stopLocation();
                finish();
                break;
            case R.id.tv_modify://修改地址
                startActivity(new Intent(this, ModifyLocationActivity.class));
                finish();
                break;
        }
    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_famliymart);
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
        fromActivity = intent.getStringExtra(EXTRA_FROM);
        address = intent.getStringExtra(EXTRA_ADDRESS);

        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText(R.string.title_text_famliymart);
        leftTitle = (ImageView) findViewById(R.id.iv_logo);
        leftTitle.setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        shopAddr = (TextView) findViewById(R.id.tv_shop_addr);
        listview = (ListView) findViewById(R.id.listview);

        loadingView = (RelativeLayout) findViewById(R.id.rl_loading);
        loadingView.setVisibility(View.VISIBLE);
        errorView = (RelativeLayout) findViewById(R.id.rl_error);

        findViewById(R.id.title_bar_left).setOnClickListener(this);
        findViewById(R.id.tv_modify).setOnClickListener(this);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShopInfo shopInfo = shopList.get(position);
                ShopStatus status = shopInfo.getStatus();
                if (status == null) {
                    return;
                }
                int shopcode = shopInfo.getId();
                SPUtils.saveInt(FamliyMartActivity.this, GlobalInfo.KEY_SHOPCODE, shopcode);
                SPUtils.saveBoolean(FamliyMartActivity.this, GlobalInfo.KEY_NEEDLOAD, true);
                SPUtils.saveBoolean(FamliyMartActivity.this, GlobalInfo.HAS_LOCATION, true);
                if (status.equals(ShopStatus.OPENED)) {// 1：正常营业 0：店铺休息中
                    if (fromActivity.equals(EXTRA_C)) {
                        Intent intent = new Intent();
                        intent.setClass(FamliyMartActivity.this, GoodsCategoryActivity.class);
                        intent.putExtra(GoodsCategoryActivity.EXTRA_SHOP_NAME, shopInfo.getName());
                        intent.putExtra(GoodsCategoryActivity.EXTRA_TYPE_CODE, 0);
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(FamliyMartActivity.this, MainActivity.class));
                    }
                } else {
                    MyToast.show(FamliyMartActivity.this, "超市休息中！");
                    startActivity(new Intent(FamliyMartActivity.this, MainActivity.class));
                }
                finish();
            }
        });
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        loadingView.setVisibility(View.GONE);
        switch (eventId) {
            case Event.GET_LOCATION_SUCCESS:
                hideErrorLocal();
                setData(bundle);
                break;
            case Event.GET_LOCATION_ADDR_SUCCESS:
                hideErrorLocal();
                setData(bundle);
                break;
            case Event.GET_LOCATION_FAILED:
                MyToast.show(this, notice);
                break;
            case Event.GET_LOCATION_ADDR_FAILED:
                stopLocation();
                MyToast.show(this, notice);
                break;
            default:
                break;
        }
    }

    private void setData(Bundle bundle) {
        String address = SPUtils.getString(this, GlobalInfo.ADDRESS, "");
        shopAddr.setText(address);
        ArrayList<ShopInfo> list = (ArrayList<ShopInfo>) bundle.getSerializable(LocationLogic.EXTRA_SHOPLIST);

        stopLocation();
        shopList.clear();
        shopList.addAll(list);
        if (adapter == null) {
            adapter = new ShopListAdapter(this, shopList);
            listview.setAdapter(adapter);
        } else {
            adapter.addData(shopList);
        }
    }

    private void stopLocation() {
        if (!TextUtils.isEmpty(fromActivity)) {
            if (fromActivity.equals(EXTRA_M)) {
                ModifyLocationActivity.mLocationClient.stop();
            } else if (fromActivity.equals(EXTRA_L)) {
                LocationActivity.mLocationClient.stop();
            } else if (fromActivity.equals(EXTRA_C)) {
                GoodsCategoryActivity.mLocationClient.stop();
            } else {
                MainActivity.mLocationClient.stop();
            }
        } else {
            ModifyLocationActivity.mLocationClient.stop();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            stopLocation();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this,
                Event.GET_LOCATION_SUCCESS, Event.GET_LOCATION_FAILED,
                Event.GET_LOCATION_ADDR_FAILED, Event.GET_LOCATION_ADDR_SUCCESS);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }
}
