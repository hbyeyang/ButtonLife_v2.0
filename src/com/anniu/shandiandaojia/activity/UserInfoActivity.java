package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.UserInfo;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.UserLogic;
import com.anniu.shandiandaojia.utils.CustomLengthFilter;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.anniu.shandiandaojia.utils.Utils;
import com.anniu.shandiandaojia.view.ClearEditText;
import com.anniu.shandiandaojia.view.MySwitch;
import com.anniu.shandiandaojia.view.MySwitch.OnChangedListener;

/**
 * @author YY
 * @ClassName: UserInfoActivity
 * @Description: 个人信息页面
 * @date 2015年5月30日 下午8:29:31
 */
public class UserInfoActivity extends BaseActivity implements OnChangedListener {
    private TextView titleBarTv, tvNickname, tvPhone, tvSex, noticetext, cancel, confirm;
    private String newName;
    private PopupWindow popupWindow;
    private ClearEditText clearName;
    private String sex[] = {"先生", "女士"};
    public static String EXTRA_USER = "user";
    private UserInfo user;

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        if (v.getId() == R.id.title_bar_left) {
            finish();
        } else if (v.getId() == R.id.rl_name) {
            showNameActivity();
        } else if (v.getId() == R.id.rl_sex) {
            showSexActivity();
        } else if (v.getId() == R.id.cancel) {
            MyToast.show(UserInfoActivity.this, "您取消了修改姓名！");
            popupWindow.dismiss();
        } else if (v.getId() == R.id.confirm) {
            rename();
        }
    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_information);
        user = (UserInfo) getIntent().getExtras().getSerializable(EXTRA_USER);

        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText("修改个人信息");
        findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        findViewById(R.id.title_bar_left).setOnClickListener(this);
        findViewById(R.id.rl_phone).setOnClickListener(this);
        findViewById(R.id.rl_name).setOnClickListener(this);
        findViewById(R.id.rl_sex).setOnClickListener(this);

        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvNickname = (TextView) findViewById(R.id.tv_name);
        tvSex = (TextView) findViewById(R.id.tv_sex);

        if (user != null) {
            tvPhone.setText(user.getTel());
            tvNickname.setText(user.getNickName());
            boolean sex = user.isSex();
            if (sex) {
                tvSex.setText("先生");
            } else {
                tvSex.setText("女士");
            }
        }
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        switch (eventId) {
            case Event.POST_PERSON_NAME_SEX_SUCESS://保存用户姓名性别成功事件
                MyToast.show(this, "恭喜您信息修改成功！");
                SPUtils.saveBoolean(this, GlobalInfo.KEY_NEEDLOAD_USERINFO, true);
                break;
            case Event.POST_PERSON_NAME_SEX_FAILED://保存用户姓名性别失败事件
                MyToast.show(this, notice);
                break;

            default:
                break;
        }
    }

    /**
     * 修改用户姓名或者昵称--用Activity弹窗
     */
    private void showNameActivity() {
        View view = getLayoutInflater().inflate(R.layout.motify_name_activity, null, true);

        clearName = (ClearEditText) view.findViewById(R.id.clear_name);
        noticetext = (TextView) view.findViewById(R.id.notice);
        cancel = (TextView) view.findViewById(R.id.cancel);
        confirm = (TextView) view.findViewById(R.id.confirm);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        clearName.setText(SPUtils.getString(this, GlobalInfo.KEY_NICK_NAME, ""));
        noticetext.setText("12个字符，可由中英文、数字等符号组成。");

        view.setFocusableInTouchMode(true);
        popupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.showAtLocation(view, Gravity.CENTER | Gravity.CENTER, 0, 0);
    }

    /**
     * 修改用户称谓--用Activity弹窗
     */
    private void showSexActivity() {
        View view = getLayoutInflater().inflate(R.layout.sex_dialog_activity, null, true);
        ListView lsitview = (ListView) view.findViewById(R.id.lv_list);

        lsitview.setAdapter(new ArrayAdapter<String>(context, R.layout.item_sex, R.id.tv_text, sex));
        lsitview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                newName = user.getNickName();
                boolean sexNew = user.isSex();
                if (sex[arg2].equals("先生")) {
                    tvSex.setText("先生");
                    sexNew = true;
                } else if (sex[arg2].equals("女士")) {
                    tvSex.setText("女士");
                    sexNew = false;
                }
                postUpdatePersoninfo(newName, sexNew);
                popupWindow.dismiss();
            }
        });
        view.setFocusableInTouchMode(true);
        popupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.showAtLocation(view, Gravity.CENTER | Gravity.CENTER, 0, 0);
    }

    /**
     * 判断输入的是否合法及发送修改请求
     */
    private void rename() {
        String newName = clearName.getText().toString().trim();
        int length = CustomLengthFilter.getLength(newName);
        if (length > 12) {
            noticetext.setText("超出字数限制");
            noticetext.setTextColor(getResources().getColor(R.color.red));
        } else if (TextUtils.isEmpty(newName)) {
            noticetext.setText("姓名或者昵称不能为空");
            noticetext.setTextColor(getResources().getColor(R.color.red));
        } else {
            tvNickname.setText(newName);//姓名修改成功后，更新显示并保存到本地，上传到服务器
            postUpdatePersoninfo(newName, user.isSex());
            popupWindow.dismiss();
        }
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this, Event.POST_PERSON_NAME_SEX_SUCESS, Event.POST_PERSON_NAME_SEX_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

    /**
     * 个人中心姓名性别保存至服务器
     */
    protected void postUpdatePersoninfo(String name, boolean sex) {
        Intent intent = new Intent(UserLogic.ACTION_POST_PERSON_NAME_SEX);
        intent.putExtra(UserLogic.EXTRA_USER_CODE, SPUtils.getInt(this, GlobalInfo.KEY_USERCODE, 0));
        intent.putExtra(UserLogic.EXTRA_NICK_NAME, name);
        intent.putExtra(UserLogic.EXTRA_USER_SEX, sex);
        sendAction(intent);
    }

    @Override
    public void OnChanged(MySwitch wiperSwitch, boolean checkState) {

    }

}
