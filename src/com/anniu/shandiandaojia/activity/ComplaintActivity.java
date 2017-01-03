package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.UserLogic;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.Utils;

/**
 * @author YY
 * @ClassName: ComplaintActivity
 * @Description: 投诉界面
 * @date 2015年6月11日 下午4:48:40
 */
public class ComplaintActivity extends BaseActivity {
    private TextView titleBarTv;// 界面标题
    private Button bt_commit;
    private EditText et_content;
    private int shopCode;
    private int userCode;
    private String orderNum;
    private RelativeLayout loadingView;

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        if (v.getId() == R.id.title_bar_left) {
            finish();
        } else if (v.getId() == R.id.bt_commit) {
            String content = et_content.getText().toString().trim();
            postComplainorder(orderNum, userCode, shopCode, content);
            finish();
        }
    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_complaint);
        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText("投诉商铺");
        findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        findViewById(R.id.title_bar_left).setOnClickListener(this);
        bt_commit = (Button) findViewById(R.id.bt_commit);
        bt_commit.setOnClickListener(this);
        et_content = (EditText) findViewById(R.id.et_content);

        loadingView = (RelativeLayout) findViewById(R.id.rl_loading);
        loadingView.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        orderNum = intent.getStringExtra(GlobalInfo.KEY_ORDER_NUM);
        shopCode = intent.getIntExtra(GlobalInfo.KEY_SHOPCODE, 0);
        userCode = intent.getIntExtra(GlobalInfo.KEY_USERCODE, 0);
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        switch (eventId) {
            case Event.POST_COMPLAINORDER_SUCESS:// 投诉商铺成功事件

                break;
            case Event.POST_COMPLAINORDER_FAILED:// 投诉商铺失败事件

                break;
            default:
                break;
        }
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this,
                Event.POST_COMPLAINORDER_SUCESS,
                Event.POST_COMPLAINORDER_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

    /**
     * 投诉店铺
     */
    private void postComplainorder(String orderNum2, int userCode2,
                                   int shopCode2, String content) {
        Intent intent = new Intent(UserLogic.ACTION_POST_COMPLAINORDER);
        intent.putExtra(UserLogic.EXTRA_ORDER_NUM, orderNum2);
        intent.putExtra(UserLogic.EXTRA_USER_CODE, userCode2);
        intent.putExtra(UserLogic.EXTRA_SHOP_CODE, shopCode2);
        intent.putExtra(UserLogic.EXTRA_SUBMITADVICE, content);
        sendAction(intent);
    }
}
