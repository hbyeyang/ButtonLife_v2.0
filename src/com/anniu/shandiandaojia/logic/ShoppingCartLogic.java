package com.anniu.shandiandaojia.logic;

import android.content.Intent;
import android.os.Bundle;

import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.BottomStatusBar;
import com.anniu.shandiandaojia.db.jsondb.CartGoods;
import com.anniu.shandiandaojia.net.GlobalValue;
import com.anniu.shandiandaojia.net.NetMgr.OnHttpRsp;
import com.anniu.shandiandaojia.net.ReqInfo;
import com.anniu.shandiandaojia.net.bean.HttpRsp;
import com.anniu.shandiandaojia.net.bean.entity.CartGoodsListReq;
import com.anniu.shandiandaojia.net.bean.entity.GoodDeleteReq;
import com.anniu.shandiandaojia.net.bean.entity.InOrOutCartReq;
import com.anniu.shandiandaojia.net.bean.entity.ShoppingCartInfoReq;
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
 * @ClassName: ShoppingCartLogic
 * @Description: 购物车逻辑类
 * @date 2015年6月8日 上午11:38:47
 */
public class ShoppingCartLogic extends BaseLogic {

    //获取首页购物车商品列表
    public static String ACTION_GETCARTLIST = "ShoppingCartLogic.ACTION_GETCARTLIST";
    //添加或删除/修改商品数量
    public static String ACTION_INSERTORUPDATECARTGOODNUM = "ShoppingCartLogic.ACTION_INSERTORUPDATECARTGOODNUM";
    //获取商品详情页购物车商品列表
    public static String ACTION_GETCARTLISTGOODSDETAILS = "ShoppingCartLogic.ACTION_GETCARTLISTGOODSDETAILS";
    //分类页购物车商品列表
    public static String ACTION_GETCATEGORYCARTLIST = "ShoppingCartLogic.ACTION_GETCATEGORYCARTLIST";
    // 获取购物车页信息
    public static String ACTION_GET_SHOPPING_CART_INFO = "ShoppingCartLogic.ACTION_GET_SHOPPING_CART_INFO";
    //删除一条购物车中的商品
    public static String ACTION_GET_DELETE_CART_GOOD = "ShoppingCartLogic.ACTION_GET_DELETE_CART_GOOD";

    public static String EXTRA_NUM = "num";
    public static String EXTRA_POST = "post";
    public static String EXTRA_CARTGOODSLIST = "cartGoodsList";
    public static String EXTRA_ID = "id";
    public static String EXTRA_COUNT = "count";
    public static String EXTRA_CMD = "cmd";//1=添加一个，2=减一个 3=根据可选参数num修改数量


    public ShoppingCartLogic(HttpService service) {
        super(service);
        List<String> actions = new ArrayList<String>();
        actions.add(ACTION_INSERTORUPDATECARTGOODNUM);
        actions.add(ACTION_GETCARTLIST);
        actions.add(ACTION_GETCARTLISTGOODSDETAILS);
        actions.add(ACTION_GET_SHOPPING_CART_INFO);
        actions.add(ACTION_GET_DELETE_CART_GOOD);
        actions.add(ACTION_GETCATEGORYCARTLIST);
        mService.registerAction(this, actions);
    }

    @Override
    public void onHandleAction(Intent intent) {
        String action = intent.getAction();
        if (ACTION_INSERTORUPDATECARTGOODNUM.equals(action)) {
            int id = intent.getExtras().getInt(EXTRA_ID);
            int count = intent.getExtras().getInt(EXTRA_COUNT);
            String cmd = intent.getExtras().getString(EXTRA_CMD);
            updateCartGoods(id, count, cmd);
        } else if (ACTION_GETCARTLIST.equals(action)) {
            getCartList();
        } else if (ACTION_GETCARTLISTGOODSDETAILS.equals(action)) {
            getCartGoodsList();
        } else if (ACTION_GET_SHOPPING_CART_INFO.equals(action)) {
            getShoppingCartInfo();
        } else if (ACTION_GET_DELETE_CART_GOOD.equals(action)) {
            int id = intent.getExtras().getInt(EXTRA_ID);
            getShoppingCartDelete(id);
        } else if (ACTION_GETCATEGORYCARTLIST.equals(action)) {
            getCategoryCartList();
        }
    }

    private void getCategoryCartList() {
        CartGoodsListReq req = new CartGoodsListReq(ReqInfo.REQ_GET_CARTLIST);
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
                                    if (objData.has(EXTRA_CARTGOODSLIST)) {
                                        JSONArray carGoodsArray = objData.getJSONArray(EXTRA_CARTGOODSLIST);
                                        if (carGoodsArray != null && carGoodsArray.length() > 0) {
                                            ArrayList<CartGoods> cartGoodsList = new ArrayList<CartGoods>();
                                            JSONObject goodObj;
                                            for (int i = 0; i < carGoodsArray.length(); i++) {
                                                goodObj = (JSONObject) carGoodsArray.get(i);
                                                cartGoodsList.add(gson.fromJson(goodObj.toString(), CartGoods.class));
                                            }
                                            bundle.putSerializable(EXTRA_CARTGOODSLIST, cartGoodsList);
                                        }
                                    }
                                }
                                notice(Event.GET_CATEGORY_CART_LIST_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_CATEGORY_CART_LIST_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_CATEGORY_CART_LIST_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 刪除购物车中一条商品
     **/
    private void getShoppingCartDelete(final int id) {
        GoodDeleteReq req = new GoodDeleteReq(ReqInfo.REQ_GET_DELETECARTGOOD, id);
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
                            bundle.putInt(EXTRA_ID, id);

                            if (success) {
                                if (obj.has(GlobalValue.KEY_DATA)) {
                                    JSONObject objData = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    BottomStatusBar bar = gson.fromJson(objData.toString(), BottomStatusBar.class);
                                    bundle.putSerializable(EXTRA_POST, bar);
                                }
                                notice(Event.GET_DELETE_CART_GOOD_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_DELETE_CART_GOOD_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_DELETE_CART_GOOD_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 获取购物车页面信息
     **/
    private void getShoppingCartInfo() {
        ShoppingCartInfoReq req = new ShoppingCartInfoReq(ReqInfo.REQ_GET_CARTINFO);
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
                                if (obj.has(GlobalValue.KEY_DATA)) {
                                    JSONObject objData = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    if (objData.has(EXTRA_CARTGOODSLIST)) {
                                        JSONArray carGoodsArray = objData.getJSONArray(EXTRA_CARTGOODSLIST);
                                        if (carGoodsArray != null && carGoodsArray.length() > 0) {
                                            ArrayList<CartGoods> cartGoodsList = new ArrayList<CartGoods>();
                                            JSONObject goodObj;
                                            for (int i = 0; i < carGoodsArray.length(); i++) {
                                                goodObj = (JSONObject) carGoodsArray.get(i);
                                                cartGoodsList.add(gson.fromJson(goodObj.toString(), CartGoods.class));
                                            }
                                            bundle.putSerializable(EXTRA_CARTGOODSLIST, cartGoodsList);
                                        }
                                    }

                                    if (objData.has(EXTRA_POST)) {
                                        JSONObject postObj = objData.getJSONObject(EXTRA_POST);
                                        BottomStatusBar bar = gson.fromJson(postObj.toString(), BottomStatusBar.class);
                                        bundle.putSerializable(EXTRA_POST, bar);
                                    }
                                }
                                notice(Event.GET_CART_INFO_SUCESS, bundle);
                            } else {
                                if (errorCode == 101) {
                                    SPUtils.saveBoolean(mContext, GlobalInfo.KEY_ISLOGIN, false);
                                    SPUtils.saveString(mContext, GlobalValue.KEY_COOKIE, "");
                                }
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_CART_INFO_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_CART_INFO_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 获取商品详情页购物车商品列表
     **/
    private void getCartGoodsList() {
        CartGoodsListReq req = new CartGoodsListReq(ReqInfo.REQ_GET_CARTLIST);
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
                                    if (objData.has(EXTRA_CARTGOODSLIST)) {
                                        JSONArray carGoodsArray = objData.getJSONArray(EXTRA_CARTGOODSLIST);
                                        if (carGoodsArray != null && carGoodsArray.length() > 0) {
                                            ArrayList<CartGoods> cartGoodsList = new ArrayList<CartGoods>();
                                            JSONObject goodObj;
                                            for (int i = 0; i < carGoodsArray.length(); i++) {
                                                goodObj = (JSONObject) carGoodsArray.get(i);
                                                cartGoodsList.add(gson.fromJson(goodObj.toString(), CartGoods.class));
                                            }
                                            bundle.putSerializable(EXTRA_CARTGOODSLIST, cartGoodsList);
                                        }
                                    }
                                }
                                notice(Event.GET_CART_LIST_GOODS_DETAILS_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_CART_LIST_GOODS_DETAILS_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_CART_LIST_GOODS_DETAILS_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 获取购物车商品列表
     */
    private void getCartList() {
        CartGoodsListReq req = new CartGoodsListReq(ReqInfo.REQ_GET_CARTLIST);
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
                                if (obj.has(GlobalValue.KEY_DATA)) {
                                    JSONObject objData = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    if (objData.has(EXTRA_CARTGOODSLIST)) {
                                        JSONArray carGoodsArray = objData.getJSONArray(EXTRA_CARTGOODSLIST);
                                        if (carGoodsArray != null && carGoodsArray.length() > 0) {
                                            ArrayList<CartGoods> cartGoodsList = new ArrayList<>();
                                            JSONObject goodObj;
                                            for (int i = 0; i < carGoodsArray.length(); i++) {
                                                goodObj = (JSONObject) carGoodsArray.get(i);
                                                cartGoodsList.add(gson.fromJson(goodObj.toString(), CartGoods.class));
                                            }
                                            bundle.putSerializable(EXTRA_CARTGOODSLIST, cartGoodsList);
                                        }
                                    }

                                    if (objData.has(EXTRA_POST)) {
                                        JSONObject postObj = objData.getJSONObject(EXTRA_POST);
                                        bundle.putSerializable(EXTRA_POST, gson.fromJson(postObj.toString(), BottomStatusBar.class));
                                    }
                                }
                                notice(Event.GET_CART_LIST_SUCESS, bundle);
                            } else {
                                if (errorCode == 101) {
                                    SPUtils.saveBoolean(mContext, GlobalInfo.KEY_ISLOGIN, false);
                                    SPUtils.saveString(mContext, GlobalValue.KEY_COOKIE, "");
                                }
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_CART_LIST_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_CART_LIST_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    private void updateCartGoods(final int id, final int count, final String cmd) {
        InOrOutCartReq req = new InOrOutCartReq(ReqInfo.REQ_POST_UPDATE_CART_GOOD_NUM, id, count, cmd);
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
                        int errorCode = 0;
                        if (obj.has(GlobalValue.KEY_SUCCESS)) {
                            success = obj.getBoolean(GlobalValue.KEY_SUCCESS);
                            bundle.putBoolean(GlobalValue.KEY_SUCCESS, success);
                            bundle.putInt(EXTRA_COUNT, count);
                            bundle.putString(EXTRA_CMD, cmd);
                            bundle.putInt(EXTRA_ID, id);
                            if (!obj.isNull(GlobalValue.KEY_ERRORCODE)) {
                                errorCode = obj.getInt(GlobalValue.KEY_ERRORCODE);
                            }

                            if (success) {
                                if (obj.has(GlobalValue.KEY_DATA)) {
                                    JSONObject objData = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    BottomStatusBar bsb = gson.fromJson(objData.toString(), BottomStatusBar.class);
                                    bundle.putSerializable(EXTRA_POST, bsb);
                                }
                                notice(Event.INSERT_OR_UPDATE_CART_GOOD_NUM_SUCESS, bundle);
                            } else {
                                if (errorCode == 101) {
                                    SPUtils.saveBoolean(mContext, GlobalInfo.KEY_ISLOGIN, false);
                                    SPUtils.saveString(mContext, GlobalValue.KEY_COOKIE, "");
                                }
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.INSERT_OR_UPDATE_CART_GOOD_NUM_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.INSERT_OR_UPDATE_CART_GOOD_NUM_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }
}
