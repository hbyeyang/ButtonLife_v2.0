package com.anniu.shandiandaojia.logic;

import android.content.Intent;
import android.os.Bundle;

import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.CartGoods;
import com.anniu.shandiandaojia.db.jsondb.MyAddress;
import com.anniu.shandiandaojia.db.jsondb.OrderInfo;
import com.anniu.shandiandaojia.db.jsondb.Payment;
import com.anniu.shandiandaojia.db.jsondb.PaymentWay;
import com.anniu.shandiandaojia.db.jsondb.PrepayIdInfo;
import com.anniu.shandiandaojia.net.GlobalValue;
import com.anniu.shandiandaojia.net.NetMgr.OnHttpRsp;
import com.anniu.shandiandaojia.net.ReqInfo;
import com.anniu.shandiandaojia.net.bean.HttpRsp;
import com.anniu.shandiandaojia.net.bean.entity.AckorderReq;
import com.anniu.shandiandaojia.net.bean.entity.CancelOrderReq;
import com.anniu.shandiandaojia.net.bean.entity.ConfirmOrderReq;
import com.anniu.shandiandaojia.net.bean.entity.FindallOrdersReq;
import com.anniu.shandiandaojia.net.bean.entity.OrderDetails;
import com.anniu.shandiandaojia.net.bean.entity.OrderInsertorderReq;
import com.anniu.shandiandaojia.net.bean.entity.PrepayIdReq;
import com.anniu.shandiandaojia.net.bean.entity.WXCheckPayReq;
import com.anniu.shandiandaojia.net.bean.entity.WaterSubmitReq;
import com.anniu.shandiandaojia.service.HttpService;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/***
 * @author YY
 * @ClassName: OrderLogic
 * @Description: 用户账单
 * @date 2015年6月5日 下午12:57:10
 */
public class OrderLogic extends BaseLogic {

    // 结算请求
    public static String ACTION_GET_ACKORDER_STATUS = "OrderLogic.ACTION_GET_ACKORDER_STATUS";
    //普通商品订单结算
    public static String ACTION_POST_ORDER_INSERTORDER = "OrderLogic.ACTION_POST_ORDER_INSERTORDER";
    //订单详情
    public static String ACTION_GET_ORDER_DETAIL = "OrderLogic.ACTION_GET_ORDER_DETAIL";
    //获取用户订单
    public static String ACTION_GET_USER_ORDER = "OrderLogic.ACTION_GET_USER_ORDER";
    //取消订单
    public static String ACTION_POST_CANCEL_ORDER = "OrderLogic.ACTION_POST_CANCEL_ORDER";
    //取消订单(订单详情页面)
    public static String ACTION_POST_CANCEL_ORDER_DETAIL_FROM_BALANCE = "OrderLogic.ACTION_POST_CANCEL_ORDER_DETAIL_FROM_BALANCE";
    //取消订单详情页面
    public static String ACTION_POST_CANCEL_ORDER_DETAIL = "OrderLogic.ACTION_POST_CANCEL_ORDER_DETAIL";
    // WX支付获取prepayid
    public static String ACTION_GET_PREPAYID = "OrderLogic.ACTION_GET_PREPAYID";
    //WX支付获取prepayid订单列表页
    public static String ACTION_GET_PREPAYID_ADAPTER = "OrderLogic.ACTION_GET_PREPAYID_ADAPTER";
    //WX支付获取prepayid订单列表页
    public static String ACTION_GET_PREPAYID_ORDERDETAIL = "OrderLogic.ACTION_GET_PREPAYID_ORDERDETAIL";
    //WX支付获取prepayid水票
    public static String ACTION_GET_PREPAYID_WATER = "OrderLogic.ACTION_GET_PREPAYID_WATER";
    //购买水票
    public static String ACTION_POST_ORDER_WATER_TICKET = "OrderLogic.ACTION_POST_ORDER_WATER_TICKET";
    //确认订单
    public static String ACTION_GET_CONFIRM_ORDER = "OrderLogic.ACTION_GET_CONFIRM_ORDER";
    //WX支付检查状态
    public static String ACTION_GET_CHECKPAY = "OrderLogic.ACTION_GET_CHECKPAY";

    public static String EXTRA_ORDER_NUM = "order_num";
    public static String EXTRA_RECEIVING = "order_receiving";
    public static String EXTRA_CANCELCANSE = "cancelCanse";
    public static String EXTRA_REMARK = "remark";
    public static String EXTRA_STARTTIME = "starttime";
    public static String EXTRA_ENDTIME = "endtime";
    public static String EXTRA_PAY_WAY = "payway";// 支付方式 WX_APP，WX_JS，ALI，BAI_DU，DAO_FU
    public static String EXTRA_COUPON_CODE = "coupon_code";
    public static String EXTRA_GOODS_CODE = "goods_code";
    public static String EXTRA_PREPAY_ID = "prepay_id";
    public static String EXTRA_ADDRESS = "address";
    //--------------------------------------------------
    public static String EXTRA_GOODS = "goods";
    public static String EXTRA_GOODSAMOUNT = "goodsAmount";
    public static String EXTRA_COUPONID = "couponId";
    public static String EXTRA_COUPONAMOUNT = "couponAmount";
    public static String EXTRA_POSTFEE = "postFee";
    public static String EXTRA_PAYMENTAMOUNT = "paymentAmount";
    public static String EXTRA_ORDERNUM = "orderNum";
    public static String EXTRA_ORDERINFO = "orderInfo";
    public static String EXTRA_ORDERLIST = "orderlist";

    public OrderLogic(HttpService service) {
        super(service);
        ArrayList<String> actions = new ArrayList<String>();
        actions.add(ACTION_GET_USER_ORDER);
        actions.add(ACTION_POST_CANCEL_ORDER);
        actions.add(ACTION_GET_CONFIRM_ORDER);
        actions.add(ACTION_GET_ORDER_DETAIL);
        actions.add(ACTION_GET_ACKORDER_STATUS);
        actions.add(ACTION_POST_ORDER_INSERTORDER);
        actions.add(ACTION_GET_PREPAYID);
        actions.add(ACTION_GET_PREPAYID_ORDERDETAIL);
        actions.add(ACTION_GET_PREPAYID_WATER);
        actions.add(ACTION_GET_CHECKPAY);
        actions.add(ACTION_POST_ORDER_WATER_TICKET);
        actions.add(ACTION_POST_CANCEL_ORDER_DETAIL);
        actions.add(ACTION_GET_PREPAYID_ADAPTER);
        actions.add(ACTION_POST_CANCEL_ORDER_DETAIL_FROM_BALANCE);
        mService.registerAction(this, actions);
    }

    @Override
    public void onHandleAction(Intent intent) {
        String action = intent.getAction();
        if (ACTION_GET_USER_ORDER.equals(action)) {
            getFindAllOrders();
        } else if (ACTION_POST_CANCEL_ORDER.equals(action)) {
            int orderNum = intent.getIntExtra(EXTRA_ORDER_NUM, 0);
            String cancelCanse = intent.getStringExtra(EXTRA_CANCELCANSE);
            getCancelOrder(orderNum, cancelCanse);
        } else if (ACTION_GET_CONFIRM_ORDER.equals(action)) {
            int orderNum = intent.getIntExtra(EXTRA_ORDER_NUM, 0);
            String receiving = intent.getStringExtra(EXTRA_RECEIVING);
            String receivingTime = receiving.substring(0, 2);
            getConfirmOrder(orderNum, receivingTime);
        } else if (ACTION_GET_ORDER_DETAIL.equals(action)) {
            int orderNum = intent.getIntExtra(EXTRA_ORDER_NUM, 0);
            getOrderDetails(orderNum);
        } else if (ACTION_GET_ACKORDER_STATUS.equals(action)) {
            getAckOrder();
        } else if (ACTION_POST_ORDER_INSERTORDER.equals(action)) {
            int couponId = intent.getIntExtra(EXTRA_COUPON_CODE, 0);
            String remark = intent.getStringExtra(EXTRA_REMARK);
            String postStartTime = intent.getStringExtra(EXTRA_STARTTIME);
            String postEndTime = intent.getStringExtra(EXTRA_ENDTIME);
            PaymentWay paymentWay = (PaymentWay) intent.getExtras().get(EXTRA_PAY_WAY);
            postOrderInsertOrder(remark, postStartTime, postEndTime, paymentWay, couponId);
        } else if (ACTION_GET_PREPAYID.equals(action)) {
            int ordernum = intent.getIntExtra(EXTRA_ORDERNUM, 0);
            getPrepayId(ordernum);
        } else if (ACTION_GET_CHECKPAY.equals(action)) {
            int ordernum = intent.getIntExtra(EXTRA_ORDERNUM, 0);
            checkPayState(ordernum);
        } else if (ACTION_POST_ORDER_WATER_TICKET.equals(action)) {
            int goodsCode = intent.getIntExtra(EXTRA_GOODS_CODE, 0);
            PaymentWay paymentWay = (PaymentWay) intent.getExtras().get(EXTRA_PAY_WAY);
            postBayWaterTicket(goodsCode, paymentWay);
        } else if (ACTION_GET_PREPAYID_WATER.equals(action)) {
            int ordernum = intent.getIntExtra(EXTRA_ORDERNUM, 0);
            getPrepayidWater(ordernum);
        } else if (ACTION_GET_PREPAYID_ORDERDETAIL.equals(action)) {
            int ordernum = intent.getIntExtra(EXTRA_ORDERNUM, 0);
            getPrepayidDetail(ordernum);
        } else if (ACTION_POST_CANCEL_ORDER_DETAIL.equals(action)) {
            int orderNum = intent.getIntExtra(EXTRA_ORDER_NUM, 0);
            String cancelCanse = intent.getStringExtra(EXTRA_CANCELCANSE);
            getCancelOrderDetail(orderNum, cancelCanse);
        } else if (ACTION_GET_PREPAYID_ADAPTER.equals(action)) {
            int ordernum = intent.getIntExtra(EXTRA_ORDERNUM, 0);
            getPrepayidAdapter(ordernum);
        } else if (ACTION_POST_CANCEL_ORDER_DETAIL_FROM_BALANCE.equals(action)) {
            int orderNum = intent.getIntExtra(EXTRA_ORDER_NUM, 0);
            String cancelCanse = intent.getStringExtra(EXTRA_CANCELCANSE);
            getCancelOrderDetailFromBalance(orderNum, cancelCanse);
        }
    }

    private void getCancelOrderDetailFromBalance(int orderNum, String cancelCanse) {
        CancelOrderReq req = new CancelOrderReq(ReqInfo.REQ_GET_CANCELORDER, orderNum, cancelCanse);
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
                                notice(Event.POST_CANCEL_ORDER_DETAIL_FROM_BALANCE_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.POST_CANCEL_ORDER_DETAIL_FROM_BALANCE_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.POST_CANCEL_ORDER_DETAIL_FROM_BALANCE_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    private void getPrepayidAdapter(final int orderNum) {
        PrepayIdReq req = new PrepayIdReq(ReqInfo.REQ_GET_GETPREPAYID, orderNum);
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
                                if (obj.has(GlobalValue.KEY_DATA)) {
                                    JSONObject object = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    PrepayIdInfo info = gson.fromJson(object.toString(), PrepayIdInfo.class);
                                    SPUtils.saveInt(mContext, GlobalInfo.KEY_ORDER_NUM, orderNum);
                                    bundle.putSerializable(EXTRA_PREPAY_ID, info);
                                    SPUtils.saveString(mContext, GlobalInfo.APP_ID, object.toString());
                                }
                                notice(Event.GET_PREPAY_ID_DETAIL_ADAPTER_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_PREPAY_ID_DETAIL_ADAPTER_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_PREPAY_ID_DETAIL_ADAPTER_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    private void getCancelOrderDetail(int orderNum, String cancelCanse) {
        CancelOrderReq req = new CancelOrderReq(ReqInfo.REQ_GET_CANCELORDER, orderNum, cancelCanse);
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
                                notice(Event.POST_CANCEL_ORDER_DETAIL_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.POST_CANCEL_ORDER_DETAIL_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.POST_CANCEL_ORDER_DETAIL_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    private void getPrepayidDetail(final int orderNum) {
        PrepayIdReq req = new PrepayIdReq(ReqInfo.REQ_GET_GETPREPAYID, orderNum);
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
                                if (obj.has(GlobalValue.KEY_DATA)) {
                                    JSONObject object = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    PrepayIdInfo info = gson.fromJson(object.toString(), PrepayIdInfo.class);
                                    SPUtils.saveInt(mContext, GlobalInfo.KEY_ORDER_NUM, orderNum);
                                    SPUtils.saveString(mContext, GlobalInfo.APP_ID, object.toString());
                                    bundle.putSerializable(EXTRA_PREPAY_ID, info);
                                }
                                notice(Event.GET_PREPAY_ID_DETAIL_TRUE_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_PREPAY_ID_DETAIL_TRUE_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_PREPAY_ID_DETAIL_TRUE_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    private void getPrepayidWater(final int orderNum) {
        PrepayIdReq req = new PrepayIdReq(ReqInfo.REQ_GET_GETPREPAYID, orderNum);
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
                                    JSONObject preobj = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    PrepayIdInfo info = gson.fromJson(preobj.toString(), PrepayIdInfo.class);
                                    bundle.putSerializable(EXTRA_PREPAY_ID, info);
                                    SPUtils.saveString(mContext, GlobalInfo.APP_ID, preobj.toString());
                                }
                                notice(Event.GET_PREPAY_ID_WATER_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_PREPAY_ID_WATER_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_PREPAY_ID_WATER_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    private void postBayWaterTicket(int goodsCode, PaymentWay payWay) {
        WaterSubmitReq req = new WaterSubmitReq(ReqInfo.REQ_GET_WATER_SUBMIT, payWay, goodsCode);
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
                                    bundle.putInt(EXTRA_ORDER_NUM, orderId);
                                }
                                notice(Event.GET_WATER_SUBMIT_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_WATER_SUBMIT_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_WATER_SUBMIT_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    private void checkPayState(int orderNum) {
        WXCheckPayReq req = new WXCheckPayReq(ReqInfo.REQ_GET_WXCALLBACK, orderNum);
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
                                if (obj.has(GlobalValue.KEY_DATA)) {
                                    JSONObject object = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    Payment payment = gson.fromJson(object.toString(), Payment.class);
                                    bundle.putSerializable(EXTRA_ORDERINFO, payment);
                                }
                                notice(Event.GET_PREPAY_CHECKPAY_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_PREPAY_CHECKPAY_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_PREPAY_CHECKPAY_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    private void getPrepayId(int orderNum) {
        PrepayIdReq req = new PrepayIdReq(ReqInfo.REQ_GET_GETPREPAYID, orderNum);
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
                                if (obj.has(GlobalValue.KEY_DATA)) {
                                    JSONObject object = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    PrepayIdInfo info = gson.fromJson(object.toString(), PrepayIdInfo.class);
                                    SPUtils.saveString(mContext, GlobalInfo.APP_ID, object.toString());
                                    bundle.putSerializable(EXTRA_PREPAY_ID, info);
                                }
                                notice(Event.GET_PREPAY_ID_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_PREPAY_ID_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_PREPAY_ID_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 普通商品订单支付
     */
    private void postOrderInsertOrder(String remark, String postStartTime
            , String postEndTime, PaymentWay paymentWay, int couponId) {
        OrderInsertorderReq req = new OrderInsertorderReq(ReqInfo.REQ_POST_ORDER_INSERTORDER,
                couponId, paymentWay, postStartTime, postEndTime, remark);
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
                                if (obj.has(GlobalValue.KEY_DATA)) {
                                    int orderNum = obj.getInt(GlobalValue.KEY_DATA);
                                    bundle.putInt(EXTRA_ORDERNUM, orderNum);
                                }
                                notice(Event.POST_ORDER_INSERTORDER_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.POST_ORDER_INSERTORDER_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.POST_ORDER_INSERTORDER_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 结算页面信息获取
     */
    private void getAckOrder() {
        AckorderReq req = new AckorderReq(ReqInfo.REQ_GET_ACKORDER);
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
                                if (obj.has(GlobalValue.KEY_DATA)) {
                                    JSONObject objData = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    if (objData.has(EXTRA_GOODSAMOUNT)) {// 商品总额
                                        double goodsAmount = objData.getDouble(EXTRA_GOODSAMOUNT);
                                        bundle.putDouble(EXTRA_GOODSAMOUNT, goodsAmount);
                                    }
                                    if (!objData.isNull(EXTRA_COUPONID)) {// 优惠券 ID
                                        int couponId = objData.getInt(EXTRA_COUPONID);
                                        bundle.putInt(EXTRA_COUPONID, couponId);
                                    }
                                    if (objData.has(EXTRA_COUPONAMOUNT)) {// 优惠券总额
                                        double couponAmount = objData.getDouble(EXTRA_COUPONAMOUNT);
                                        bundle.putSerializable(EXTRA_COUPONAMOUNT, couponAmount);
                                    }
                                    if (objData.has(EXTRA_POSTFEE)) {// 配送费
                                        double postFree = objData.getDouble(EXTRA_POSTFEE);
                                        bundle.putSerializable(EXTRA_POSTFEE, postFree);
                                    }
                                    if (objData.has(EXTRA_PAYMENTAMOUNT)) {// 支付金额
                                        double paymentAmount = objData.getDouble(EXTRA_PAYMENTAMOUNT);
                                        bundle.putSerializable(EXTRA_PAYMENTAMOUNT, paymentAmount);
                                    }

                                    if (!objData.isNull(EXTRA_ADDRESS)) {
                                        JSONObject addressObj = objData.getJSONObject(EXTRA_ADDRESS);
                                        MyAddress address = gson.fromJson(addressObj.toString(), MyAddress.class);
                                        bundle.putSerializable(EXTRA_ADDRESS, address);
                                    }

                                    if (objData.has(EXTRA_GOODS)) {
                                        JSONArray goodsArray = objData.getJSONArray(EXTRA_GOODS);
                                        if (goodsArray != null && goodsArray.length() > 0) {
                                            ArrayList<CartGoods> list = new ArrayList<CartGoods>();
                                            CartGoods cartGoods;
                                            JSONObject goodObj;
                                            for (int i = 0; i < goodsArray.length(); i++) {
                                                goodObj = (JSONObject) goodsArray.get(i);
                                                cartGoods = gson.fromJson(goodObj.toString(), CartGoods.class);
                                                list.add(cartGoods);
                                            }
                                            bundle.putSerializable(EXTRA_GOODS, list);
                                        }
                                    }
                                }
                                notice(Event.GET_ACKORDER_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_ACKORDER_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_ACKORDER_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 获取订单详情
     */
    private void getOrderDetails(int orderNum) {
        OrderDetails req = new OrderDetails(ReqInfo.REQ_GET_ORDERSTATUS, orderNum);
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
                                if (obj.has(GlobalValue.KEY_DATA)) {
                                    JSONObject object = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    OrderInfo orderInfo = gson.fromJson(object.toString(), OrderInfo.class);
                                    bundle.putSerializable(EXTRA_ORDERINFO, orderInfo);
                                }
                                notice(Event.GET_ORDER_STATUS_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_ORDER_STATUS_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_ORDER_STATUS_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 确定订单
     */
    private void getConfirmOrder(int orderNum, String receivingTime) {
        ConfirmOrderReq req = new ConfirmOrderReq(ReqInfo.REQ_GET_CONFIRMORDER, orderNum, receivingTime);
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
                                notice(Event.GET_CONFIRM_ORDER_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_CONFIRM_ORDER_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_CONFIRM_ORDER_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 取消订单
     */
    private void getCancelOrder(int orderNum, String cancelCause) {
        CancelOrderReq req = new CancelOrderReq(ReqInfo.REQ_GET_CANCELORDER, orderNum, cancelCause);
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
                                notice(Event.POST_CANCEL_ORDER_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.POST_CANCEL_ORDER_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.POST_CANCEL_ORDER_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 获取用户订单
     */
    private void getFindAllOrders() {
        FindallOrdersReq req = new FindallOrdersReq(ReqInfo.REQ_GET_FIND_ALLORDERS);
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
                                    JSONArray array = obj.getJSONArray(GlobalValue.KEY_DATA);
                                    ArrayList<OrderInfo> list = new ArrayList<OrderInfo>();
                                    JSONObject object;
                                    OrderInfo orderInfo;
                                    for (int i = 0; i < array.length(); i++) {
                                        object = (JSONObject) array.get(i);
                                        orderInfo = gson.fromJson(object.toString(), OrderInfo.class);
                                        list.add(orderInfo);
                                    }
                                    bundle.putSerializable(EXTRA_ORDERLIST, list);
                                }
                                notice(Event.GET_USER_ALLORDERS_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_USER_ALLORDERS_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_USER_ALLORDERS_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }
}
