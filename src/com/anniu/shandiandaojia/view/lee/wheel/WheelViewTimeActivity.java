package com.anniu.shandiandaojia.view.lee.wheel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.view.lee.wheel.widget.TosAdapterView;
import com.anniu.shandiandaojia.view.lee.wheel.widget.TosGallery;
import com.anniu.shandiandaojia.view.lee.wheel.widget.WheelView;
/**
 * 
 * @ClassName:  WheelViewTimeActivity
 * @Description: 时间弹窗
 * @author YY
 * @date 2015年6月18日 下午6:38:04
 */
public class WheelViewTimeActivity extends Activity implements OnClickListener {
	/**定义一个时间段数组*/
	private String[] timeArray = { "00:00 - 01:00", "01:00 - 02:00",
			"02:00 - 03:00", "03:00 - 04:00", "04:00 - 05:00",
			"05:00 - 06:000", "06:00 - 07:00", "07:00 - 08:00",
			"08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00",
			"12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00",
			"16:00 - 17:00", "17:00 - 18:00", "18:00 - 19:00", "19:00 - 20:00",
			"20:00 - 21:00", "21:00 - 22:00", "22:00 - 23:00", "23:00 - 00:00" };

    private WheelView mTimes = null;
    public static String EXTRA_CURRENT_TIME = "current_time";
    private NumberAdapter hourAdapter;
    /**选择的时间*/
    private String time = "08:00 - 09:00";
    /**点击取消选择*/
    private TextView tv_cancel;
    /**点击确定选择时间*/
	private TextView tv_compelet;
	public static int REQUESTCODE = 2;
	private int num = 0;

	@Override
	public void onClick(View v) {
		if (com.anniu.shandiandaojia.utils.Utils.isFastClick()) {
			return;
		}
		switch (v.getId()) {
		case R.id.tv_cancel://取消选择时间
			setDefaultTime();
			break;
		case R.id.tv_compelet://确认选择时间
			setDefaultTime();
			break;
		default:
			break;
		}
	}

	private void setDefaultTime() {
		Intent intent1 = new Intent();
		intent1.putExtra(GlobalInfo.KEY_TIME, time);
		setResult(REQUESTCODE, intent1);
		finish();
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showTime();
    }

	@SuppressWarnings("deprecation")
	private void showTime() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wheel_time);
        WindowManager m = getWindowManager();    
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高    
        LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值    
        p.width = (int) (d.getWidth() * 1);    //宽度设置为屏幕的0.8   
        getWindow().setAttributes(p);
        
        Intent intent = getIntent();
        String currentTime = intent.getStringExtra(EXTRA_CURRENT_TIME);
		for (int i = 0; i < timeArray.length; i++) {
			if (timeArray[i].equals(currentTime)) {
				num = i;
				break;
			}
		}
        
        mTimes = (WheelView) findViewById(R.id.wheel1);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);
        tv_compelet = (TextView) findViewById(R.id.tv_compelet);
        tv_compelet.setOnClickListener(this);
        mTimes.setScrollCycle(true);
        
        hourAdapter = new NumberAdapter(timeArray);
        mTimes.setAdapter(hourAdapter);
        mTimes.setSelection(num, true);
        ((WheelTextView)mTimes.getSelectedView()).setTextSize(30);
        mTimes.setOnItemSelectedListener(mListener);
        mTimes.setUnselectedAlpha(0.5f);
        time = timeArray[mTimes.getSelectedItemPosition()];
	}
    
    protected void getTime() {
    	int selectedItemPosition = mTimes.getSelectedItemPosition();
    	time = timeArray[selectedItemPosition];
	}
    
	private TosAdapterView.OnItemSelectedListener mListener = new TosAdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(TosAdapterView<?> parent, View view,
				int position, long id) {
			((WheelTextView) view).setTextSize(30);

			int index = Integer.parseInt(view.getTag().toString());
			int count = parent.getChildCount();
			if (index < count - 1) {
				((WheelTextView) parent.getChildAt(index + 1)).setTextSize(25);
			}
			if (index > 0) {
				((WheelTextView) parent.getChildAt(index - 1)).setTextSize(25);
			}
			getTime();
		}

		@Override
		public void onNothingSelected(TosAdapterView<?> parent) {

		}
	};


	private class NumberAdapter extends BaseAdapter {
        int mHeight = 50;
        String[] mData = null;

        public NumberAdapter(String[] data) {
            mHeight = (int) Utils.dipToPx(WheelViewTimeActivity.this, mHeight);
            this.mData = data;
        }

        @Override
        public int getCount() {
            return (null != mData) ? mData.length : 0;
        }

        @Override
        public View getItem(int arg0) {
            return getView(arg0, null, null);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }
        
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WheelTextView textView = null;

            if (null == convertView) {
                convertView = new WheelTextView(WheelViewTimeActivity.this);
                convertView.setLayoutParams(new TosGallery.LayoutParams(-1, mHeight));
                textView = (WheelTextView) convertView;
                textView.setTextSize(25);
                textView.setGravity(Gravity.CENTER);
            }
            
            
            String text = mData[position];
            if (null == textView) {
                textView = (WheelTextView) convertView;
            }
            
            textView.setText(text);
            return convertView;
        }
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setDefaultTime();
			return true;
		}
		return false;
	}
}
