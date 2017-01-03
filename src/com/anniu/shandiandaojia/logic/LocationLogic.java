package com.anniu.shandiandaojia.logic;

import android.content.Intent;
import android.os.Bundle;

import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.HomeViewModel;
import com.anniu.shandiandaojia.db.jsondb.ShopInfo;
import com.anniu.shandiandaojia.net.GlobalValue;
import com.anniu.shandiandaojia.net.NetMgr.OnHttpRsp;
import com.anniu.shandiandaojia.net.ReqInfo;
import com.anniu.shandiandaojia.net.bean.HttpRsp;
import com.anniu.shandiandaojia.net.bean.entity.LocationAddrReq;
import com.anniu.shandiandaojia.net.bean.entity.LocationReq;
import com.anniu.shandiandaojia.net.bean.entity.SelectShopReq;
import com.anniu.shandiandaojia.net.bean.entity.ShopInfoReq;
import com.anniu.shandiandaojia.service.HttpService;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zxl
 * @ClassName: LocationLogic
 * @Description: 定位logic
 * @date 2015年5月30日 下午5:13:22
 */
public class LocationLogic extends BaseLogic {

    //定位
    public static String ACTION_GET_LOCATION = "LocationLogic.ACTION_GET_LOCATION";
    // 定位通过地址
    public static String ACTION_GET_LOCATION_ADDR = "LocationLogic.ACTION_GET_LOCATION_ADDR";
    //选择店铺进入店铺首页
    public static String ACTION_GET_LOCATION_LIST2SHOP = "LocationLogic.ACTION_GET_LOCATION_LIST2SHOP";
    //首页重新选择便利店
    public static String ACTION_GET_SELECT_BROWER_SHOP = "LocationLogic.ACTION_GET_SELECT_BROWER_SHOP";
    //分类页重新选择便利店
    public static String ACTION_GET_SELECT_BROWER_SHOP_CATEGORY = "LocationLogic.ACTION_GET_SELECT_BROWER_SHOP_CATEGORY";

    public static String EXTRA_LNG = "lng";//地图api的精度
    public static String EXTRA_LAT = "lat";// 地图api的纬度
    public static String EXTRA_ID = "id";
    public static String EXTRA_SHOP = "shop";
    public static String EXTRA_PAGENUM = "pagenum";
    public static String EXTRA_PAGESIZE = "pagesize";
    public static String EXTRA_LOADTYPE = "loadType";
    public static String EXTRA_SHOPCODE = "shopcode";
    public static String EXTRA_SHOPLIST = "shopList";
    public static String EXTRA_ADDRESS = "address";

    public LocationLogic(HttpService service) {
        super(service);
        List<String> actions = new ArrayList<String>();
        actions.add(ACTION_GET_LOCATION);
        actions.add(ACTION_GET_LOCATION_ADDR);
        actions.add(ACTION_GET_LOCATION_LIST2SHOP);
        actions.add(ACTION_GET_SELECT_BROWER_SHOP);
        actions.add(ACTION_GET_SELECT_BROWER_SHOP_CATEGORY);
        mService.registerAction(this, actions);
    }

    @Override
    public void onHandleAction(Intent intent) {
        String action = intent.getAction();
        if (ACTION_GET_LOCATION.equals(action)) {
            String lng = intent.getStringExtra(EXTRA_LNG);
            String lat = intent.getStringExtra(EXTRA_LAT);
            int loadType = intent.getIntExtra(EXTRA_LOADTYPE, 1);
            getLocation(lng, lat, loadType);
        } else if (ACTION_GET_LOCATION_ADDR.equals(action)) {
            String address = intent.getStringExtra(EXTRA_ADDRESS);
            int loadType = intent.getIntExtra(EXTRA_LOADTYPE, 1);
            getLocationByAddr(address, loadType);
        } else if (ACTION_GET_LOCATION_LIST2SHOP.equals(action)) {
            int shopcode = intent.getExtras().getInt(EXTRA_SHOPCODE);
            getShopInfo(shopcode);
        } else if (ACTION_GET_SELECT_BROWER_SHOP.equals(action)) {
            String lng = intent.getStringExtra(EXTRA_LNG);
            String lat = intent.getStringExtra(EXTRA_LAT);
            getBrowerShop(lng, lat);
        } else if (ACTION_GET_SELECT_BROWER_SHOP_CATEGORY.equals(action)) {
            String lng = intent.getStringExtra(EXTRA_LNG);
            String lat = intent.getStringExtra(EXTRA_LAT);
            getBrowerShopCategory(lng, lat);
        }
    }

    private void getBrowerShopCategory(String lng, String lat) {
        SelectShopReq req = new SelectShopReq(ReqInfo.REQ_GET_SELECT_BROWERS_SHOP, lng, lat);
        req.setOnHttpRsp(new OnHttpRsp() {
            @Override
            public void onHttpRsp(HttpRsp httpRsp) {
                {
                    Bundle bundle = new Bundle();
                    if (httpRsp.code == HttpRsp.CODE_SUCCESS) {
                        byte[] data = httpRsp.data;
                        try {
                            String str = new String(data, GlobalValue.ENCODING);
                            JSONObject obj = new JSONObject(str);
                            boolean success;
                            if (obj.has(GlobalValue.KEY_SUCCESS)) {
                                success = obj.getBoolean(GlobalValue.KEY_SUCCESS);
                                bundle.putBoolean(GlobalValue.KEY_SUCCESS, success);

                                if (success) {
                                    if (!obj.isNull(GlobalValue.KEY_DATA)) {
                                        JSONArray arrData = obj.getJSONArray(GlobalValue.KEY_DATA);
                                        JSONObject shopObj;
                                        ShopInfo info;
                                        ArrayList<ShopInfo> list = new ArrayList<ShopInfo>();
                                        for (int i = 0; i < arrData.length(); i++) {
                                            shopObj = (JSONObject) arrData.get(i);
                                            info = gson.fromJson(shopObj.toString(), ShopInfo.class);
                                            list.add(info);
                                        }
                                        bundle.putSerializable(EXTRA_SHOPLIST, list);
                                    }
                                    notice(Event.GET_SELECT_BROWER_SHOP_CATEGORY_SUCESS, bundle);
                                } else {
                                    bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                    notice(Event.GET_SELECT_BROWER_SHOP_CATEGORY_FAILED, bundle);
                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                        notice(Event.GET_SELECT_BROWER_SHOP_CATEGORY_FAILED, bundle);
                    }
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 首页重新选择便利店
     */
    private void getBrowerShop(String lng, String lat) {
        SelectShopReq req = new SelectShopReq(ReqInfo.REQ_GET_SELECT_BROWERS_SHOP, lng, lat);
        req.setOnHttpRsp(new OnHttpRsp() {
            @Override
            public void onHttpRsp(HttpRsp httpRsp) {
                Bundle bundle = new Bundle();
                if (httpRsp.code == HttpRsp.CODE_SUCCESS) {
                    byte[] data = httpRsp.data;
                    try {
                        String str = new String(data, GlobalValue.ENCODING);
                        JSONObject obj = new JSONObject(str);
                        boolean success;
                        if (obj.has(GlobalValue.KEY_SUCCESS)) {
                            success = obj.getBoolean(GlobalValue.KEY_SUCCESS);
                            bundle.putBoolean(GlobalValue.KEY_SUCCESS, success);
                            if (success) {
                                if (!obj.isNull(GlobalValue.KEY_DATA)) {
                                    JSONArray arrData = obj.getJSONArray(GlobalValue.KEY_DATA);
                                    JSONObject shopObj;
                                    ShopInfo info;
                                    ArrayList<ShopInfo> list = new ArrayList<ShopInfo>();
                                    for (int i = 0; i < arrData.length(); i++) {
                                        shopObj = (JSONObject) arrData.get(i);
                                        info = gson.fromJson(shopObj.toString(), ShopInfo.class);
                                        list.add(info);
                                    }
                                    bundle.putSerializable(EXTRA_SHOPLIST, list);
                                }
                                notice(Event.GET_SELECT_BROWER_SHOP_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_SELECT_BROWER_SHOP_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_SELECT_BROWER_SHOP_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 获取商铺首页信息
     */
    private void getShopInfo(int shopcode) {
        ShopInfoReq req = new ShopInfoReq(ReqInfo.REQ_GET_SHOP_INFO, shopcode);
        req.setOnHttpRsp(new OnHttpRsp() {
            @Override
            public void onHttpRsp(HttpRsp httpRsp) {
                {
                    Bundle bundle = new Bundle();
                    if (httpRsp.code == HttpRsp.CODE_SUCCESS) {
                        byte[] data = httpRsp.data;
                        try {
                            String str = new String(data, GlobalValue.ENCODING);
                            JSONObject obj = new JSONObject(str);
                            boolean success;
                            if (obj.has(GlobalValue.KEY_SUCCESS)) {
                                success = obj.getBoolean(GlobalValue.KEY_SUCCESS);
                                bundle.putBoolean(GlobalValue.KEY_SUCCESS, success);

                                if (success) {
                                    if (obj.has(GlobalValue.KEY_DATA)) {
                                        JSONObject objData = obj.getJSONObject(GlobalValue.KEY_DATA);
                                        HomeViewModel homeViewModel = gson.fromJson(objData.toString(), HomeViewModel.class);
                                        bundle.putSerializable(EXTRA_SHOP, homeViewModel);
                                    }
                                    notice(Event.GET_SHOP_INFO_SUCCESS, bundle);
                                } else {
                                    bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                    notice(Event.GET_SHOP_INFO_FAILED, bundle);
                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                        notice(Event.GET_SHOP_INFO_FAILED, bundle);
                    }
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 通过地址获取商店列表
     */
    private void getLocationByAddr(final String address, final int loadType) {
        LocationAddrReq req = new LocationAddrReq(ReqInfo.REQ_GET_LOCATION_ADDR, address);
        req.setOnHttpRsp(new OnHttpRsp() {

            @SuppressWarnings("unchecked")
            @Override
            public void onHttpRsp(HttpRsp httpRsp) {
                {
                    Bundle bundle = new Bundle();
                    if (httpRsp.code == HttpRsp.CODE_SUCCESS) {
                        {
                            byte[] data = httpRsp.data;
                            try {
                                String str = new String(data, GlobalValue.ENCODING);
                                JSONObject obj = new JSONObject(str);
                                boolean success;
                                if (obj.has(GlobalValue.KEY_SUCCESS)) {
                                    success = obj.getBoolean(GlobalValue.KEY_SUCCESS);
                                    bundle.putBoolean(GlobalValue.KEY_SUCCESS, success);
                                    bundle.putInt(EXTRA_LOADTYPE, loadType);
                                    SPUtils.saveString(mContext, GlobalInfo.ADDRESS, address);

                                    if (success) {
                                        if (obj.has(GlobalValue.KEY_DATA)) {
                                            JSONObject jsonObject = obj.getJSONObject(GlobalValue.KEY_DATA);
                                            if (jsonObject.has(EXTRA_SHOPLIST)) {
                                                JSONArray arrayData = jsonObject.getJSONArray(EXTRA_SHOPLIST);
                                                if (arrayData != null && arrayData.length() > 0) {
                                                    ArrayList<ShopInfo> shopList = new ArrayList<ShopInfo>();
                                                    ShopInfo shop;
                                                    for (int q = 0; q < arrayData.length(); q++) {
                                                        JSONObject shopObj = (JSONObject) arrayData.get(q);
                                                        shop = gson.fromJson(shopObj.toString(), ShopInfo.class);
                                                        shopList.add(shop);
                                                    }
                                                    bundle.putSerializable(EXTRA_SHOPLIST, shopList);
                                                }
                                            }
                                        }
                                        notice(Event.GET_LOCATION_ADDR_SUCCESS, bundle);
                                    } else {
                                        bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                        notice(Event.GET_LOCATION_ADDR_FAILED, bundle);
                                    }
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                        notice(Event.GET_LOCATION_ADDR_FAILED, bundle);
                    }
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 通过经纬度获取商店列表
     */
    private void getLocation(String lng, String lat, final int loadType) {
        LocationReq req = new LocationReq(ReqInfo.REQ_GET_LOCATION, lng, lat, 1);
        req.setOnHttpRsp(new OnHttpRsp() {

            @Override
            public void onHttpRsp(HttpRsp httpRsp) {
                Bundle bundle = new Bundle();
                if (httpRsp.code == HttpRsp.CODE_SUCCESS) {
                    {
                        byte[] data = httpRsp.data;
                        try {
                            String str = new String(data, GlobalValue.ENCODING);
                            JSONObject obj = new JSONObject(str);
                            boolean success;
                            if (obj.has(GlobalValue.KEY_SUCCESS)) {
                                success = obj.getBoolean(GlobalValue.KEY_SUCCESS);
                                bundle.putBoolean(GlobalValue.KEY_SUCCESS, success);
                                bundle.putInt(EXTRA_LOADTYPE, loadType);

                                if (success) {
                                    if (obj.has(GlobalValue.KEY_DATA)) {
                                        JSONObject objData = obj.getJSONObject(GlobalValue.KEY_DATA);
                                        if (objData.has(EXTRA_SHOPLIST)) {
                                            JSONArray arrayData = objData.getJSONArray(EXTRA_SHOPLIST);
                                            if (arrayData != null && arrayData.length() > 0) {
                                                ArrayList<ShopInfo> shopList = new ArrayList<ShopInfo>();
                                                ShopInfo shop;
                                                for (int q = 0; q < arrayData.length(); q++) {
                                                    JSONObject shopObj = (JSONObject) arrayData.get(q);
                                                    shop = gson.fromJson(shopObj.toString(), ShopInfo.class);
                                                    shopList.add(shop);
                                                }
                                                bundle.putSerializable(EXTRA_SHOPLIST, shopList);
                                            }
                                        }
                                        if (objData.has(EXTRA_ADDRESS)) {
                                            String addr = objData.getString(EXTRA_ADDRESS);
                                            SPUtils.saveString(mContext, GlobalInfo.ADDRESS, addr);
                                            bundle.putString(EXTRA_ADDRESS, addr);
                                        }
                                    }
                                    notice(Event.GET_LOCATION_SUCCESS, bundle);
                                } else {
                                    bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                    notice(Event.GET_LOCATION_FAILED, bundle);
                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_LOCATION_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }
}
