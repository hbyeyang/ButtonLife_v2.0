package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.UserLogic;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.Utils;

/**
 * @author YY
 * @ClassName: FeedBackActivity
 * @Description: 意见反馈
 * @date 2015年5月30日 下午6:58:44
 */
public class FeedBackActivity extends BaseActivity {
    private TextView titleBarTv;// 界面标题
    private Button bt_commit;
    private EditText et_content;
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
            if (TextUtils.isEmpty(content)) {
                MyToast.show(this, "意见不能为空！");
                return;
            }
            loadingView.setVisibility(View.VISIBLE);
            postFeedBack(content);
        }
    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_user_feedback);
        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText("反馈意见");
        findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        findViewById(R.id.title_bar_left).setOnClickListener(this);
        bt_commit = (Button) findViewById(R.id.bt_commit);
        bt_commit.setOnClickListener(this);
        et_content = (EditText) findViewById(R.id.et_content);

        loadingView = (RelativeLayout) findViewById(R.id.rl_loading);
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        switch (eventId) {
            case Event.POST_SUBMITADVICE_SUCESS:// 意见反馈成功事件
                loadingView.setVisibility(View.GONE);
                MyToast.show(FeedBackActivity.this, "反馈成功！");
                finish();
                break;
            case Event.POST_SUBMITADVICE_FAILED:// 意见反馈失败事件
                loadingView.setVisibility(View.GONE);
                MyToast.show(this, notice);
                break;
            default:
                break;
        }
    }

    /**
     * 意见反馈
     */
    private void postFeedBack(String content) {
        Intent intent = new Intent(UserLogic.ACTION_POST_SUBMITADVICE);
        intent.putExtra(UserLogic.EXTRA_SUBMITADVICE, content);
        intent.putExtra(UserLogic.EXTRA_TYPE, "意见");
        sendAction(intent);
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this, Event.POST_SUBMITADVICE_SUCESS,
                Event.POST_SUBMITADVICE_FAILED,
                Event.POST_COMPLAINORDER_SUCESS,
                Event.POST_COMPLAINORDER_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

}
