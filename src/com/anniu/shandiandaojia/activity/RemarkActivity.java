package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.Utils;

/***
 * 
 * @ClassName: RemarkActivity
 * @Description: 订单备注
 * @author YY
 * @date 2015年6月30日 上午11:09:44
 */
public class RemarkActivity extends BaseActivity {

	private TextView titleBarTv;// 界面标题
	private Button bt_commit_content;
	private EditText et_content;
	private String mContent;
	public static int REQUESTCODE = 3;
	public static String CONTENT = "content";

	@Override
	public void onClick(View v) {
		if (Utils.isFastClick()) {
			return;
		}
		if (v.getId() == R.id.title_bar_left) {// 返回
			setContent();
		} else if (v.getId() == R.id.bt_commit_content) {// 提交
			mContent = et_content.getText().toString().trim();
			if (TextUtils.isEmpty(mContent)) {
				MyToast.show(this, "请您填写内容！");
				return;
			}
			Intent intent = new Intent();
			intent.putExtra(GlobalInfo.KEY_CONTENT, mContent);
			setResult(REQUESTCODE, intent);
			finish();
		}
	}

	private void setContent() {
		Intent intent = new Intent();
		intent.putExtra(GlobalInfo.KEY_CONTENT, mContent);
		setResult(REQUESTCODE, intent);
		finish();
	}

	@Override
	protected void init(Bundle saveInstanceState) {
		setContentView(R.layout.activity_remark);
		titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
		titleBarTv.setText("备注");
		findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
		findViewById(R.id.title_bar_left).setOnClickListener(this);
		bt_commit_content = (Button) findViewById(R.id.bt_commit_content);
		bt_commit_content.setOnClickListener(this);
		et_content = (EditText) findViewById(R.id.et_content);
		mContent = getIntent().getExtras().getString(CONTENT);
		if (!TextUtils.isEmpty(mContent)) {
			et_content.setText(mContent);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setContent();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
