package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.utils.Utils;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

/**
* @ClassName: LocationActivity 
* @Description: 定位页面 
* @author zxl 
* @date 2015年5月29日 下午7:23:39
 */
public class LocationActivity extends BaseActivity {

	public static LocationClient mLocationClient;
	private TextView titleBarTv;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor="gcj02";//gcj02、bd09ll、bd09

	@Override
	public void onClick(View v) {
		if (Utils.isFastClick()) {
			return;
		}
		switch (v.getId()) {
		case R.id.btn_current:
			InitLocation();
			mLocationClient.start();
			
			Intent intent = new Intent(this, FamliyMartActivity.class);
			intent.putExtra(FamliyMartActivity.EXTRA_FROM, FamliyMartActivity.EXTRA_L);
			startActivity(intent);
			finish();
			break;
		case R.id.btn_modify_location:
			startActivity(new Intent(LocationActivity.this, ModifyLocationActivity.class));
			finish();
			break;
		}
	}
	
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);//设置定位模式
		option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);//是否反地理编码
		mLocationClient.setLocOption(option);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}


	@Override
	protected void init(Bundle saveInstanceState) {
		setContentView(R.layout.activity_location);
		titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
		titleBarTv.setText(R.string.title_text_location1);
		
		findViewById(R.id.btn_current).setOnClickListener(this);
		findViewById(R.id.btn_modify_location).setOnClickListener(this);
		
		mLocationClient = ((App)getApplication()).mLocationClient;
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
}
