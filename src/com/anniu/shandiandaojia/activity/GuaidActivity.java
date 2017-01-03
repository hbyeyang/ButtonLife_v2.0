package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.adapter.ViewPagerAdapter;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.anniu.shandiandaojia.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
* @ClassName: GuaidActivity 
* @Description: 引导页面
* @author zxl 
* @date 2015年5月7日 下午2:01:45
 */
public class GuaidActivity extends BaseActivity {
	private ViewPager view_pager;
	private Button button;

	@Override
	public void onClick(View v) {
		if (Utils.isFastClick()) {
			return;
		}
		switch (v.getId()) {
		case R.id.button:
			boolean isLocation = SPUtils.getBoolean(GuaidActivity.this, GlobalInfo.HAS_LOCATION, false);
			if (isLocation) {
				startActivity(new Intent(GuaidActivity.this,MainActivity.class));
			}else{
				startActivity(new Intent(GuaidActivity.this,LocationActivity.class));
			}
			finish();
			break;
		}
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

	@Override
	protected void init(Bundle saveInstanceState) {
		setContentView(R.layout.activity_guaid);
		view_pager = (ViewPager) findViewById(R.id.view_pager);
		button = (Button) findViewById(R.id.button);
		button.setOnClickListener(this);
		view_pager.setOnClickListener(this);

		List<View> lists = new ArrayList<View>();
		ImageView imageView1 = new ImageView(this);
		imageView1.setBackgroundResource(R.drawable.guide_step1);

		ImageView imageView2 = new ImageView(this);
		imageView2.setBackgroundResource(R.drawable.guide_step2);

		ImageView imageView3 = new ImageView(this);
		imageView3.setBackgroundResource(R.drawable.guide_step3);
		
		ImageView imageView4 = new ImageView(this);
		imageView4.setBackgroundResource(R.drawable.guide_step4);

		lists.add(imageView1);
		lists.add(imageView2);
		lists.add(imageView3);
		lists.add(imageView4);

		ViewPagerAdapter adapter = new ViewPagerAdapter(lists);
		view_pager.setAdapter(adapter);
		view_pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == 3) {
					button.setVisibility(View.VISIBLE);
				}else{
					button.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}
}
