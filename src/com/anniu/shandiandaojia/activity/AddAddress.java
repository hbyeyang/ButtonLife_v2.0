package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.UserLogic;
import com.anniu.shandiandaojia.utils.CustomLengthFilter;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.Utils;
import com.anniu.shandiandaojia.view.kankan.wheel.widget.OnWheelChangedListener;
import com.anniu.shandiandaojia.view.kankan.wheel.widget.WheelView;
import com.anniu.shandiandaojia.view.kankan.wheel.widget.adapter.ArrayWheelAdapter;
import com.anniu.shandiandaojia.view.kankan.wheel.widget.model.CityModel;
import com.anniu.shandiandaojia.view.kankan.wheel.widget.model.DistrictModel;
import com.anniu.shandiandaojia.view.kankan.wheel.widget.model.ProvinceModel;
import com.anniu.shandiandaojia.view.kankan.wheel.widget.service.XmlParserHandler;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @ClassName: AddAddress
 * @Description: 新增地址页面
 * @date 2015年6月8日 下午5:02:04
 */
public class AddAddress extends BaseActivity implements OnWheelChangedListener {
    private TextView titleBarTv, confirm, cancel;
    private EditText et_name, et_address, et_phone, et_district;
    private PopupWindow popupWindow;
    private com.anniu.shandiandaojia.view.kankan.wheel.widget.WheelView mViewDistrict, mViewCity, mViewProvince;

    private String[] mProvinceDatas;
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    private Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
    private Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();
    private String mCurrentProviceName;
    private String mCurrentCityName;
    private String mCurrentDistrictName;
    private String mCurrentZipCode = "";

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.bt_save:
                String name = et_name.getText().toString().trim();
                String phone = et_phone.getText().toString().trim();
                String address = et_address.getText().toString().trim();
                String district = et_district.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    MyToast.show(this, "姓名不能为空！");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    MyToast.show(this, "电话号码不能为空！");
                    return;
                }

                if (CustomLengthFilter.getLength(phone) != 11) {
                    MyToast.show(this, "输入的手机号码长度不对！");
                    return;
                }

                if (TextUtils.isEmpty(address)) {
                    MyToast.show(this, "收货地址不能为空！");
                    return;
                }

                int length = CustomLengthFilter.getLength(name);
                if (length > 12) {
                    MyToast.show(this, "姓名不能超过6个字符！");
                    return;
                }

                if (TextUtils.isEmpty(district)) {
                    MyToast.show(this, "所在城区不能为空！");
                    return;
                }
                sendAddAddressAction(name, phone, address, mCurrentDistrictName);
                break;
        }
    }


    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_add_address);
        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText(R.string.add_address);
        findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        findViewById(R.id.title_bar_left).setOnClickListener(this);
        findViewById(R.id.bt_save).setOnClickListener(this);

        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_address = (EditText) findViewById(R.id.et_address);
        et_district = (EditText) findViewById(R.id.et_district);
        et_district.setInputType(InputType.TYPE_NULL);
        et_district.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow();
            }
        });
        et_district.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showPopWindow();
                } else {
                    popupWindow.dismiss();
                }
            }
        });
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        switch (eventId) {
            case Event.POST_PERSON_INSERTADDR_SUCESS:// 修改用户收货信息--新增成功事件
                getFindUserloc();
                finish();
                break;
            case Event.POST_PERSON_INSERTADDR_FAILED:// 修改用户收货信息--新增失败事件
                MyToast.show(this, notice);
                break;

            default:
                break;
        }
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this,
                Event.POST_PERSON_INSERTADDR_SUCESS,
                Event.POST_PERSON_INSERTADDR_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

    //获取个人中心我的地址
    private void getFindUserloc() {
        Intent intent = new Intent(UserLogic.ACTION_GET_PERSON_FINDUSERLOC);
        sendAction(intent);
    }

    private void sendAddAddressAction(String name, String phone, String address, String district) {
        Intent intent = new Intent(UserLogic.ACTION_POST_PERSON_INSERTADDR);
        intent.putExtra(UserLogic.EXTRA_USER_NAME, name);
        intent.putExtra(UserLogic.EXTRA_PHONE, phone);
        intent.putExtra(UserLogic.EXTRA_USER_ADDR, address);
        intent.putExtra(UserLogic.EXTRA_USER_DISTRICT, district);
        sendAction(intent);
    }

    //选择地址
    private void showPopWindow() {
        if (popupWindow == null) {
            View view = getLayoutInflater().inflate(R.layout.popupwindow_address, null, false);
            initPopViews(view);
            setUpData();
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setAnimationStyle(R.style.style_ppwcart);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.update();
            popupWindow.showAtLocation(findViewById(R.id.ll_layout), Gravity.BOTTOM, 0, 0);
        } else {
            popupWindow.showAtLocation(findViewById(R.id.ll_layout), Gravity.BOTTOM, 0, 0);
        }
    }

    private void setUpData() {
        initProvinceDatas();
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(this, mProvinceDatas));
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        mViewDistrict.setVisibleItems(7);
        updateCities();
        updateAreas();
    }

    private void initPopViews(View view) {
        mViewProvince = (com.anniu.shandiandaojia.view.kankan.wheel.widget.WheelView) view.findViewById(R.id.province);
        mViewCity = (com.anniu.shandiandaojia.view.kankan.wheel.widget.WheelView) view.findViewById(R.id.city);
        mViewDistrict = (com.anniu.shandiandaojia.view.kankan.wheel.widget.WheelView) view.findViewById(R.id.district);

        mViewProvince.addChangingListener(this);
        mViewCity.addChangingListener(this);
        mViewDistrict.addChangingListener(this);

        confirm = (TextView) view.findViewById(R.id.tv_confirm);
        cancel = (TextView) view.findViewById(R.id.tv_cancel);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                et_district.setText(mCurrentCityName + "," + mCurrentDistrictName);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }


    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }

    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[]{""};
        }
        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
        mViewDistrict.setCurrentItem(0);
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
        }
    }

    private void initProvinceDatas() {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            provinceList = handler.getDataList();
            if (provinceList != null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList != null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                    List<DistrictModel> districtList = cityList.get(0).getDistrictList();
                    mCurrentDistrictName = districtList.get(0).getName();
                    mCurrentZipCode = districtList.get(0).getZipcode();
                }
            }
            mProvinceDatas = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                mProvinceDatas[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j = 0; j < cityList.size(); j++) {
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctNameArray = new String[districtList.size()];
                    DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                    for (int k = 0; k < districtList.size(); k++) {
                        DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }
}
