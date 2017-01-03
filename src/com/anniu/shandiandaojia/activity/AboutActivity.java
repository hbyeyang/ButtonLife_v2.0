package com.anniu.shandiandaojia.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.utils.Utils;
/**
 * 
 * @ClassName:  AboutActivity
 * @Description: 关于闪电到家页面
 * @author YY
 * @date 2015年6月10日 下午3:55:47
 */
public class AboutActivity extends BaseActivity {
	
	private TextView titleBarTv;//标题
	private TextView version;



	@Override
	public void onClick(View v) {
		if (Utils.isFastClick()) {
			return;
		}
		if (v.getId()==R.id.title_bar_left) {//返回
			finish();
		}
	}

	@Override
	protected void init(Bundle saveInstanceState) {
		setContentView(R.layout.activity_about_us);
		
		titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
		titleBarTv.setText(R.string.about_us);
		findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
		findViewById(R.id.title_bar_left).setOnClickListener(this);
		
		version = (TextView) findViewById(R.id.versaion);
		version.setText("版本号"+Utils.version_name);
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
