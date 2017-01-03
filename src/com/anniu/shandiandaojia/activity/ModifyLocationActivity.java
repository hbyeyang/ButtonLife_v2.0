package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.logic.LocationLogic;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.Utils;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

/**
 * @author zxl
 * @ClassName: ModifyLocationActivity
 * @Description: 修改位置
 * @date 2015年5月29日 下午7:38:27
 */
public class ModifyLocationActivity extends BaseActivity {
    private ImageView leftTitle;
    private TextView titleBarTv;
    private TextView etAddr;

    public static LocationClient mLocationClient;
    private LocationMode tempMode = LocationMode.Hight_Accuracy;
    private String tempcoor = "gcj02";//gcj02、bd09ll、bd09
    private String address = "";

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.tv_location_current://定位当前
                InitLocation();
                mLocationClient.start();
                intent.setClass(this, FamliyMartActivity.class);
                intent.putExtra(FamliyMartActivity.EXTRA_FROM, FamliyMartActivity.EXTRA_M);
                startActivity(intent);
                finish();
                break;
            case R.id.iv_search://搜索
                String addr = etAddr.getText().toString();
                if (TextUtils.isEmpty(addr)) {
                    MyToast.show(this, "请输入当前地址信息！");
                    return;
                }
                address = addr;
                sendGetShopListByAddrAction();

                intent.setClass(this, FamliyMartActivity.class);
                intent.putExtra(FamliyMartActivity.EXTRA_FROM, FamliyMartActivity.EXTRA_M);
                intent.putExtra(FamliyMartActivity.EXTRA_ADDRESS, address);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }


    private void InitLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);//设置定位模式
        option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//是否反地理编码
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_modify_location);
        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText(R.string.title_text_modifylocation);
        leftTitle = (ImageView) findViewById(R.id.iv_logo);
        leftTitle.setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        etAddr = (TextView) findViewById(R.id.et_addr);

        findViewById(R.id.iv_search).setOnClickListener(this);
        findViewById(R.id.title_bar_left).setOnClickListener(this);
        findViewById(R.id.tv_location_current).setOnClickListener(this);
        mLocationClient = ((App) getApplication()).mLocationClient;
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {

    }

    @Override
    protected void addEventListener() {

    }

    @Override
    protected void removeListener() {

    }

    public void sendGetShopListByAddrAction() {
        Intent intent = new Intent(LocationLogic.ACTION_GET_LOCATION_ADDR);
        intent.putExtra(LocationLogic.EXTRA_ADDRESS, address);
        sendAction(intent);
    }
}
