package com.anniu.shandiandaojia.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.utils.Utils;
/**
 * 
 * @ClassName:  MessageCenterActivity
 * @Description: 消息中心
 * @author YY
 * @date 2015年5月30日 下午8:18:31
 */
public class MessageCenterActivity extends BaseActivity {

	private TextView titleBarTv;
	/**点击活动消息*/
	private TextView tv_activity_news;
	/**点击系统消息*/
	private TextView tv_system_news;
	private View activity_line;
	private View system_line;
	
	@Override
	public void onClick(View v) {
		if (Utils.isFastClick()) {
			return;
		}
		if (v.getId()==R.id.title_bar_left) {
			finish();
		}else if (v.getId()==R.id.tv_activity_news) {
			tv_activity_news.setTextColor(this.getResources().getColor(R.color.title_color));
			tv_system_news.setTextColor(Color.BLACK);
			activity_line.setBackgroundColor(getResources().getColor(R.color.title_color));
			system_line.setBackgroundColor(getResources().getColor(R.color.white));
		}else if (v.getId()==R.id.tv_system_news) {
			tv_system_news.setTextColor(this.getResources().getColor(R.color.title_color));
			tv_activity_news.setTextColor(Color.BLACK);
			activity_line.setBackgroundColor(getResources().getColor(R.color.white));
			system_line.setBackgroundColor(getResources().getColor(R.color.title_color));
		}
	}

	@Override
	protected void init(Bundle saveInstanceState) {
		setContentView(R.layout.activity_user_news);
		
		titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
		titleBarTv.setText("消息中心");
		
		findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
		findViewById(R.id.title_bar_left).setOnClickListener(this);
		
		tv_activity_news = (TextView) findViewById(R.id.tv_activity_news);
		tv_activity_news.setOnClickListener(this);
		tv_activity_news.setTextColor(this.getResources().getColor(R.color.title_color));
		tv_system_news = (TextView) findViewById(R.id.tv_system_news);
		tv_system_news.setOnClickListener(this);
		activity_line = findViewById(R.id.activity_line);
		system_line = findViewById(R.id.system_line);
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
