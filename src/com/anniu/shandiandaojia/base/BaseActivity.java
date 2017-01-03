package com.anniu.shandiandaojia.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.utils.MyLog;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends FragmentActivity implements OnClickListener {
	/*** 屏幕的高 */
	protected int windowHeight;
	/*** 屏幕的宽 */
	protected int windowWidth;
	protected App mApp;
	protected Handler mUIHandler = new Handler();
	protected BaseActivity context;
	protected Intent baseIntent;
	
	protected View rootView = null;
	protected RelativeLayout contentView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityMgr.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		context = this;
		baseIntent = getIntent();
		mApp = (App) getApplication();
		init(savedInstanceState);
		addEventListener();
	}
	
	private void initMainView() {
		super.setContentView(R.layout.base_activity_layout);
		rootView = findViewById(R.id.root_layout);
		contentView = (RelativeLayout) findViewById(R.id.content_layout);
		
		LayoutParams params = contentView.getLayoutParams();
		params.width = LayoutParams.FILL_PARENT;
		contentView.setLayoutParams(params);
	}
	
	@Override
	public void setContentView(int layoutResID) {
		initMainView();
		FrameLayout outView = new FrameLayout(context);//定义框架布局器  
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		outView.setLayoutParams(params);
		View child = LayoutInflater.from(context).inflate(layoutResID, outView);

		contentView.removeAllViews();
		contentView.addView(child);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(context);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(context);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		removeListener();
		ActivityMgr.destroyActivity(this);
	}

	public void sendAction(Intent intent) {
		mApp.sendAction(intent);
	}

	/**
	 * @Title: onNotify
	 * @Description: 通知UI
	 * @param eventId
	 *            事件id
	 * @param bundle
	 *            传递的数据
	 * @return void 返回类型
	 * @throws
	 */
	public void onNotify(final int eventId, final Bundle bundle) {
		mUIHandler.post(new Runnable() {

			public void run() {
				try {
					onUIEvent(eventId, bundle);
				} catch (Exception e) {
					MyLog.e("mUIHandler", e.toString());
				}
			}
		});
	}
	
	protected abstract void init(Bundle bundle);

	protected abstract void onUIEvent(int eventId, Bundle bundle);

	protected abstract void addEventListener();

	protected abstract void removeListener();
	
	
}
