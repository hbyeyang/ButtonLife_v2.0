package com.anniu.shandiandaojia.logic;

import android.content.Intent;
import android.os.Bundle;

import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.Coupon;
import com.anniu.shandiandaojia.db.jsondb.Goods;
import com.anniu.shandiandaojia.db.jsondb.MyAddress;
import com.anniu.shandiandaojia.db.jsondb.WaterInfo;
import com.anniu.shandiandaojia.net.GlobalValue;
import com.anniu.shandiandaojia.net.NetMgr.OnHttpRsp;
import com.anniu.shandiandaojia.net.ReqInfo;
import com.anniu.shandiandaojia.net.bean.HttpRsp;
import com.anniu.shandiandaojia.net.bean.entity.CouponAmountReq;
import com.anniu.shandiandaojia.net.bean.entity.CouponReq;
import com.anniu.shandiandaojia.net.bean.entity.FindWaterTicketReq;
import com.anniu.shandiandaojia.net.bean.entity.OrderCouponReq;
import com.anniu.shandiandaojia.net.bean.entity.TicketBuyWaterReq;
import com.anniu.shandiandaojia.net.bean.entity.WaterDetailReq;
import com.anniu.shandiandaojia.net.bean.entity.WaterFindReq;
import com.anniu.shandiandaojia.net.bean.entity.WaterUseticketsReq;
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
 * @author YY
 * @ClassName: TicketLogic
 * @Description: 票的Logic类
 * @date 2015年6月15日 下午2:56:19
 */
public class TicketLogic extends BaseLogic {
    //获取所有优惠券
    public static String ACTION_GET_FINDUSERCOUPON = "TicketLogic.ACTION_GET_FINDUSERCOUPON";
    //可以使用的优惠券
    public static String ACTION_GET_ORDER_FINDCOUPON = "TicketLogic.ACTION_GET_ORDER_FINDCOUPON";
    //获取个人中心水票列表
    public static String ACTION_GET_FINDWATERTICKET = "TicketLogic.ACTION_GET_FINDWATERTICKET";
    //获取水票详情
    public static String ACTION_GET_WATER_DETAIL = "TicketLogic.ACTION_GET_WATER_DETAIL";
    //水票列表页面
    public static String ACTION_GET_WATER_FIND = "TicketLogic.ACTION_GET_WATER_FIND";
    //获取一键送水界面提交订单页面数据
    public static String ACTION_GET_WATER_USETICKETS = "TicketLogic.ACTION_GET_WATER_USETICKETS";
    //用水票购买水
    public static String ACTION_POST_WATER_WATERORDER = "TicketLogic.ACTION_POST_WATER_WATERORDER";
    //购买水票的优惠券金额
    public static String ACTION_GET_COUPONAMOUNT = "TicketLogic.ACTION_GET_COUPONAMOUNT";

    public static String EXTRA_VOUCHER_TICKET_LIST = "voucher_ticket_list";
    public static String EXTRA_WATER_LIST = "water_list";
    public static String EXTRA_GOODSCODE = "goods_code";
    public static String EXTRA_WATER_DETAIL = "water_detail";
    public static String EXTRA_COUPON = "coupon";
    public static String EXTRA_TOTALMONEY = "totalmoney";
    //-------------------------------------------------------
    public static String EXTRA_STATUS = "status";
    public static String EXTRA_AMOUNT = "amount";
    public static String EXTRA_TICKETCODE = "tic_code";
    public static String EXTRA_USERADDRESS = "userAddress";
    public static String EXTRA_USERTICKET = "userTicket";
    public static String EXTRA_NOTE = "note";
    public static String EXTRA_SHOP_CODE = "shop_code";
    public static String EXTRA_STARTTIME = "starttime";
    public static String EXTRA_ENDTIME = "endtime";
    public static String EXTRA_NUMBER = "number";

    public TicketLogic(HttpService service) {
        super(service);
        List<String> actions = new ArrayList<String>();
        actions.add(ACTION_GET_FINDWATERTICKET);
        actions.add(ACTION_GET_FINDUSERCOUPON);
        actions.add(ACTION_GET_WATER_FIND);
        actions.add(ACTION_GET_WATER_DETAIL);
        actions.add(ACTION_GET_WATER_USETICKETS);
        actions.add(ACTION_POST_WATER_WATERORDER);
        actions.add(ACTION_GET_ORDER_FINDCOUPON);
        actions.add(ACTION_GET_COUPONAMOUNT);
        mService.registerAction(this, actions);
    }

    @Override
    public void onHandleAction(Intent intent) {
        String action = intent.getAction();
        if (ACTION_GET_FINDWATERTICKET.equals(action)) {
            getFindWaterTicket();
        } else if (ACTION_GET_FINDUSERCOUPON.equals(action)) {
            getAllCoupons();
        } else if (ACTION_GET_WATER_FIND.equals(action)) {
            getWaterFind();
        } else if (ACTION_GET_WATER_DETAIL.equals(action)) {
            int goodId = intent.getIntExtra(EXTRA_GOODSCODE, 0);
            getWaterDetail(goodId);
        } else if (ACTION_GET_WATER_USETICKETS.equals(action)) {
            int ticketId = intent.getIntExtra(EXTRA_TICKETCODE, 0);
            getWaterUsetickets(ticketId);
        } else if (ACTION_POST_WATER_WATERORDER.equals(action)) {
            int shopId = intent.getIntExtra(EXTRA_SHOP_CODE, 0);
            int ticketId = intent.getIntExtra(EXTRA_TICKETCODE, 0);
            int count = intent.getIntExtra(EXTRA_NUMBER, 0);
            String note = intent.getStringExtra(EXTRA_NOTE);
            String startTime = intent.getStringExtra(EXTRA_STARTTIME);
            String endTime = intent.getStringExtra(EXTRA_ENDTIME);
            postWaterOrder(shopId, ticketId, count, note, startTime, endTime);
        } else if (ACTION_GET_ORDER_FINDCOUPON.equals(action)) {
            double amount = intent.getDoubleExtra(EXTRA_AMOUNT, 0);
            String status = intent.getStringExtra(EXTRA_STATUS);
            getOrderFindCoupon(amount, status);
        } else if (ACTION_GET_COUPONAMOUNT.equals(action)) {
            int orderId = intent.getIntExtra(EXTRA_NUMBER, 0);
            getCouponAmount(orderId);
        }
    }

    /**
     * 获取优惠券金额
     */
    private void getCouponAmount(int orderId) {
        CouponAmountReq req = new CouponAmountReq(ReqInfo.REQ_GET_COUPON_AMOUNT, orderId);
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
                                    double amount = obj.getDouble(GlobalValue.KEY_DATA);
                                    bundle.putDouble(EXTRA_AMOUNT, amount);
                                }
                                notice(Event.GET_COUPON_AMOUNT_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_COUPON_AMOUNT_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_COUPON_AMOUNT_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 获取可用的优惠券
     */
    private void getOrderFindCoupon(double totalMoney, String status) {
        OrderCouponReq req = new OrderCouponReq(ReqInfo.REQ_GET_ORDER_FINDCOUPON, totalMoney, status);
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
                                JSONArray jsonArray = obj.getJSONArray(GlobalValue.KEY_DATA);
                                ArrayList<Coupon> couponList = new ArrayList<Coupon>();
                                Coupon coupon;
                                JSONObject jsonObject;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jsonObject = (JSONObject) jsonArray.get(i);
                                    coupon = gson.fromJson(jsonObject.toString(), Coupon.class);
                                    couponList.add(coupon);
                                }
                                bundle.putSerializable(EXTRA_VOUCHER_TICKET_LIST, couponList);
                                notice(Event.GET_ORDER_FINDCOUPON_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_ORDER_FINDCOUPON_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_ORDER_FINDCOUPON_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 用水票购买水
     */
    private void postWaterOrder(int shopId, int ticketId, int count, String note, String startTime, String endTime) {
        TicketBuyWaterReq req = new TicketBuyWaterReq(ReqInfo.REQ_POST_WATER_ORDER,
                shopId, ticketId, count, note, startTime, endTime);
        req.isHttpPost = true;
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
                                    int orderId = obj.getInt(GlobalValue.KEY_DATA);
                                    bundle.putInt(EXTRA_TICKETCODE, orderId);
                                }
                                notice(Event.POST_WATER_ORDER_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.POST_WATER_ORDER_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.POST_WATER_ORDER_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 获取一键送水界面提交订单页面数据
     */
    private void getWaterUsetickets(int ticketId) {
        WaterUseticketsReq req = new WaterUseticketsReq(ReqInfo.REQ_GET_WATER_USETICKETS, ticketId);
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
                        int errorCode = 0;
                        if (obj.has(GlobalValue.KEY_SUCCESS)) {
                            success = obj.getBoolean(GlobalValue.KEY_SUCCESS);
                            bundle.putBoolean(GlobalValue.KEY_SUCCESS, success);
                            if (!obj.isNull(GlobalValue.KEY_ERRORCODE)) {
                                errorCode = obj.getInt(GlobalValue.KEY_ERRORCODE);
                            }
                            if (success) {
                                if (!obj.isNull(GlobalValue.KEY_DATA)) {
                                    JSONObject object = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    if (!object.isNull(EXTRA_USERADDRESS)) {
                                        JSONObject addObj = object.getJSONObject(EXTRA_USERADDRESS);
                                        MyAddress address = gson.fromJson(addObj.toString(), MyAddress.class);
                                        bundle.putSerializable(EXTRA_USERADDRESS, address);
                                    }
                                    if (!object.isNull(EXTRA_USERTICKET)) {
                                        JSONObject tickObj = object.getJSONObject(EXTRA_USERTICKET);
                                        WaterInfo water = gson.fromJson(tickObj.toString(), WaterInfo.class);
                                        bundle.putSerializable(EXTRA_USERTICKET, water);
                                    }
                                }
                                notice(Event.GET_WATER_USETICKETS_SUCESS, bundle);
                            } else {
                                if (errorCode == 101) {
                                    SPUtils.saveBoolean(mContext, GlobalInfo.KEY_ISLOGIN, false);
                                    SPUtils.saveString(mContext, GlobalValue.KEY_COOKIE, "");
                                }
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_WATER_USETICKETS_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_WATER_USETICKETS_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 获取水票详情页面
     */
    private void getWaterDetail(int goodId) {
        WaterDetailReq req = new WaterDetailReq(ReqInfo.REQ_GET_WATER_DETAIL, goodId);
        req.setOnHttpRsp(new OnHttpRsp() {

            @Override
            public void onHttpRsp(HttpRsp httpRsp) {
                Bundle bundle = new Bundle();
                if (httpRsp.code == httpRsp.CODE_SUCCESS) {
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
                                    JSONObject object = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    Goods good = gson.fromJson(object.toString(), Goods.class);
                                    bundle.putSerializable(EXTRA_WATER_DETAIL, good);
                                }
                                notice(Event.GET_WATER_DETAIL_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_WATER_DETAIL_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_WATER_DETAIL_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 水票列表页面
     */
    private void getWaterFind() {
        WaterFindReq req = new WaterFindReq(ReqInfo.REQ_GET_WATER_FIND);
        req.setOnHttpRsp(new OnHttpRsp() {

            @Override
            public void onHttpRsp(HttpRsp httpRsp) {
                Bundle bundle = new Bundle();
                if (httpRsp.code == httpRsp.CODE_SUCCESS) {
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
                                    JSONArray array = obj.getJSONArray(GlobalValue.KEY_DATA);
                                    ArrayList<Goods> list = new ArrayList<Goods>();
                                    JSONObject object;
                                    Goods goods;
                                    for (int i = 0; i < array.length(); i++) {
                                        object = (JSONObject) array.get(i);
                                        goods = gson.fromJson(object.toString(), Goods.class);
                                        list.add(goods);
                                    }
                                    bundle.putSerializable(EXTRA_WATER_LIST, list);
                                }
                                notice(Event.GET_WATER_FIND_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_WATER_FIND_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_WATER_FIND_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 获取所有优惠券的请求
     */
    private void getAllCoupons() {
        CouponReq req = new CouponReq(ReqInfo.REQ_GET_FINDUSERCOUPON);
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
                                JSONArray jsonArray = obj.getJSONArray(GlobalValue.KEY_DATA);
                                if (jsonArray != null && jsonArray.length() > 0) {
                                    ArrayList<Coupon> list = new ArrayList<Coupon>();
                                    JSONObject jsonObject;
                                    Coupon coupon;
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        jsonObject = (JSONObject) jsonArray.get(i);
                                        coupon = gson.fromJson(jsonObject.toString(), Coupon.class);
                                        list.add(coupon);
                                    }
                                    bundle.putSerializable(EXTRA_VOUCHER_TICKET_LIST, list);
                                }
                                notice(Event.GET_FINDUSERCOUPON_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_FINDUSERCOUPON_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_FINDUSERCOUPON_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 获取所有水票的请求
     */
    private void getFindWaterTicket() {
        FindWaterTicketReq req = new FindWaterTicketReq(ReqInfo.REQ_GET_PERSON_FINDTICKETS);
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
                                    JSONArray jsonArray = obj.getJSONArray(GlobalValue.KEY_DATA);
                                    ArrayList<WaterInfo> list = new ArrayList<WaterInfo>();
                                    WaterInfo waterInfo;
                                    JSONObject object;
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        object = (JSONObject) jsonArray.get(i);
                                        waterInfo = gson.fromJson(object.toString(), WaterInfo.class);
                                        list.add(waterInfo);
                                    }
                                    bundle.putSerializable(EXTRA_VOUCHER_TICKET_LIST, list);
                                }
                                notice(Event.GET_PERSON_FINDTICKETS_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_PERSON_FINDTICKETS_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_PERSON_FINDTICKETS_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

}
