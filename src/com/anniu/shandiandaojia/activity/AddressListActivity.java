package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.adapter.AddressListAdapter;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.MyAddress;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.UserLogic;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.anniu.shandiandaojia.utils.Utils;

import java.util.ArrayList;

/**
 * @author YY
 * @ClassName: AddressListActivity
 * @Description: 我的地址
 * @date 2015年5月30日 下午6:41:03
 */
public class AddressListActivity extends BaseActivity {
    private TextView titleBarTv;
    private ArrayList<MyAddress> addressList = new ArrayList<MyAddress>();
    private AddressListAdapter adapter;
    private ListView listView;
    public static int REQUESTCODE = 1;
    public static String ISHASADDRESS = "hasAddress";
    public static String EXTRA_FROM = "from";
    private int from = 0;//0,个人中心，1，一键送水,2.结算页面

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        if (v.getId() == R.id.title_bar_left) {
            if (from == 0) {
                finish();
            } else {
                setAddress();
            }
        } else if (v.getId() == R.id.title_bar_right) {//点击标题右边的图标，新增加地址
            startActivity(new Intent(this, AddAddress.class));
        }
    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_myaddress);
        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText(R.string.my_address);
        findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        findViewById(R.id.title_bar_left).setOnClickListener(this);
        findViewById(R.id.iv_logo_right).setBackgroundDrawable(getResources().getDrawable(R.drawable.title_add_address));
        findViewById(R.id.title_bar_right).setOnClickListener(this);

        Intent intent = getIntent();
        from = intent.getExtras().getInt(EXTRA_FROM);

        listView = (ListView) findViewById(R.id.listview_address);
        listView.setItemsCanFocus(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyAddress address = (MyAddress) listView.getItemAtPosition(position);
                Intent intent;
                if (from == 0) {//个人中心
                    intent = new Intent(UserLogic.ACTION_GET_PERSON_INSERTADDR);
                    intent.putExtra(UserLogic.EXTRA_ADDRESS_ID, address.getId());
                    intent.putExtra(UserLogic.EXTRA_USER_CODE, SPUtils.getInt(AddressListActivity.this, GlobalInfo.KEY_USERCODE, 0));
                    sendAction(intent);
                } else if (from == 1) {//一键送水
                    intent = new Intent();
                    intent.putExtra(ISHASADDRESS, true);
                    intent.putExtra(GlobalInfo.KEY_LOC_INFO, address);
                    setResult(REQUESTCODE, intent);
                    finish();
                } else {//结算页面
                    intent = new Intent();
                    intent.putExtra(ISHASADDRESS, true);
                    intent.putExtra(GlobalInfo.KEY_LOC_INFO, address);
                    setResult(REQUESTCODE, intent);
                    finish();
                }
            }
        });
        getFindUserloc();
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        switch (eventId) {
            case Event.GET_PERSON_FINDUSERLOC_SUCESS://获取个人中心我的地址成功事件
                ArrayList<MyAddress> list = (ArrayList<MyAddress>) bundle.getSerializable(UserLogic.EXTRA_USER_ADDR);
                if (list != null && list.size() > 0) {
                    addressList.clear();
                    addressList.addAll(list);
                    if (adapter == null) {
                        adapter = new AddressListAdapter(this, addressList);
                        listView.setAdapter(adapter);
                    } else {
                        adapter.addData(addressList);
                    }
                } else {
                    addressList.clear();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    MyToast.show(this, "您当前还没有地址！");
                }
                break;
            case Event.POST_PERSON_EDITADDR_SUCESS://修改用户收货信息--修改成功事件
                getFindUserloc();
                break;
            case Event.GET_PERSON_EDITDEFAULT_ADDR_SUCESS:// 修改用户收货信息--修改默认收货地址成功事件
                int addressId = bundle.getInt(UserLogic.EXTRA_ADDRESS_ID);
                MyAddress address = null;
                for (int i = 0; i < this.addressList.size(); i++) {
                    address = this.addressList.get(i);
                    if (addressId == address.getId()) {
                        break;
                    }
                }

                Intent intent = new Intent();
                intent.putExtra(ISHASADDRESS, true);
                intent.putExtra(GlobalInfo.KEY_LOC_INFO, address);
                setResult(REQUESTCODE, intent);
                finish();
                break;
            case Event.POST_PERSON_EDITADDR_FAILED://修改用户收货信息--修改失败事件
            case Event.GET_PERSON_FINDUSERLOC_FAILED://获取个人中心我的地址失败事件
            case Event.GET_PERSON_EDITDEFAULT_ADDR_FAILED://修改用户收货信息--修改默认收货地址失败事件
                MyToast.show(this, notice);
                break;
            default:
                break;
        }
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this,
                Event.GET_PERSON_FINDUSERLOC_SUCESS, Event.GET_PERSON_FINDUSERLOC_FAILED,
                Event.POST_PERSON_EDITADDR_SUCESS, Event.POST_PERSON_EDITADDR_FAILED,
                Event.GET_PERSON_EDITDEFAULT_ADDR_SUCESS, Event.GET_PERSON_EDITDEFAULT_ADDR_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (from == 0) {
                finish();
            } else {
                setAddress();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //获取个人中心我的地址
    private void getFindUserloc() {
        sendAction(new Intent(UserLogic.ACTION_GET_PERSON_FINDUSERLOC));
    }

    private void setAddress() {
        Intent intent = new Intent();
        intent.putExtra(ISHASADDRESS, false);
        setResult(REQUESTCODE, intent);
        finish();
    }
}
