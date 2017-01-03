package com.anniu.shandiandaojia.logic;

import android.content.Intent;
import android.os.Bundle;

import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.Goods;
import com.anniu.shandiandaojia.db.jsondb.GoodsListViewModel;
import com.anniu.shandiandaojia.db.jsondb.GoodsViewModel;
import com.anniu.shandiandaojia.db.jsondb.HotWords;
import com.anniu.shandiandaojia.net.GlobalValue;
import com.anniu.shandiandaojia.net.NetMgr.OnHttpRsp;
import com.anniu.shandiandaojia.net.ReqInfo;
import com.anniu.shandiandaojia.net.bean.HttpRsp;
import com.anniu.shandiandaojia.net.bean.entity.CategoryGoodsReq;
import com.anniu.shandiandaojia.net.bean.entity.CategoryViewReq;
import com.anniu.shandiandaojia.net.bean.entity.GoodsDetailsReq;
import com.anniu.shandiandaojia.net.bean.entity.SearchGoodsReq;
import com.anniu.shandiandaojia.net.bean.entity.SearchHotsWordsReq;
import com.anniu.shandiandaojia.service.HttpService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zxl
 * @ClassName: GoodsLogic
 * @Description: 商品逻辑类
 * @date 2015年6月3日 下午7:22:50
 */
public class GoodsLogic extends BaseLogic {
    // 分类页面首次进入
    public static String ACTION_GET_CATEGORY_ACTIVITY_INFO = "GoodsLogic.ACTION_GET_CATEGORY_ACTIVITY_INFO";
    // 通过分类id获取商品列表
    public static String ACTION_GET_GOODS_BY_TYPE = "GoodsLogic.ACTION_GET_GOODS_BY_TYPE";
    //获取商品详情
    public static String ACTION_GET_GOODS_DETAILS = "GoodsLogic.ACTION_GET_GOODS_DETAILS";
    //搜索商品
    public static String ACTION_GET_SEARCH_GOODS = "GoodsLogic.ACTION_GET_SEARCH_GOODS";
    //搜索热词
    public static String ACTION_GET_HOTSWORDS = "UserLogic.ACTION_GET_HOTSWORDS";
    public static String EXTRA_NUM = "goods_num";
    public static String EXTRA_SHOPCODE = "shopId";
    public static String EXTRA_PAGENUM = "pagenum";
    public static String EXTRA_PAGESIZE = "pagesize";
    public static String EXTRA_GOODSTYPELIST = "goodstypeList";
    public static String EXTRA_LIMITED = "limited";
    public static String EXTRA_ID = "id";
    public static String EXTRA_LOADTYPE = "loadtype";
    public static String EXTRA_TYPECODE = "typeId";
    public static String EXTRA_GOODINFO = "goodInfo";
    public static String EXTRA_KEYWORDS = "keywords";
    public static String EXTRA_GOODS_LIST = "goods_list";
    public static String EXTRA_SEARCHHOTS_LIST = "searchhots_list";

    public GoodsLogic(HttpService service) {
        super(service);
        List<String> actions = new ArrayList<String>();
        actions.add(ACTION_GET_CATEGORY_ACTIVITY_INFO);
        actions.add(ACTION_GET_GOODS_DETAILS);
        actions.add(ACTION_GET_GOODS_BY_TYPE);
        actions.add(ACTION_GET_SEARCH_GOODS);
        actions.add(ACTION_GET_HOTSWORDS);
        mService.registerAction(this, actions);
    }

    @Override
    public void onHandleAction(Intent intent) {
        String action = intent.getAction();
        if (ACTION_GET_GOODS_DETAILS.equals(action)) {
            int changcode = intent.getExtras().getInt(EXTRA_ID);
            getGoodsDetails(changcode);
        } else if (ACTION_GET_GOODS_BY_TYPE.equals(action)) {
            int typeId = intent.getExtras().getInt(EXTRA_TYPECODE);
            int page = intent.getExtras().getInt(EXTRA_PAGENUM);
            int limit = intent.getExtras().getInt(EXTRA_PAGESIZE);
            int loadtype = intent.getExtras().getInt(EXTRA_LOADTYPE);
            getGoodsByType(typeId, page, limit, loadtype);
        } else if (ACTION_GET_SEARCH_GOODS.equals(action)) {
            int shopId = intent.getIntExtra(EXTRA_SHOPCODE, 0);
            String keywords = intent.getStringExtra(EXTRA_KEYWORDS);
            getSearchGoods(shopId, keywords);
        } else if (ACTION_GET_HOTSWORDS.equals(action)) {
            getIndexSearchhots();
        } else if (ACTION_GET_CATEGORY_ACTIVITY_INFO.equals(action)) {
            int typeId = intent.getExtras().getInt(EXTRA_TYPECODE);
            int page = intent.getExtras().getInt(EXTRA_PAGENUM);
            int limit = intent.getExtras().getInt(EXTRA_PAGESIZE);
            getCategoryInfo(typeId, page, limit);
        }
    }

    //获取分类信息，首次进入分类页
    private void getCategoryInfo(int typeId, int page, int limit) {
        CategoryViewReq req = new CategoryViewReq(ReqInfo.REQ_GET_CATEGORY_INFO, typeId, page, limit);
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
                                    JSONObject dataObj = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    GoodsListViewModel model = gson.fromJson(dataObj.toString(), GoodsListViewModel.class);
                                    bundle.putSerializable(EXTRA_GOODSTYPELIST, model);
                                }
                                notice(Event.GET_CATEGORY_INFO_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_CATEGORY_INFO_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_CATEGORY_INFO_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 获取搜索界面热词
     */
    private void getIndexSearchhots() {
        SearchHotsWordsReq req = new SearchHotsWordsReq(ReqInfo.REQ_GET_INDEX_SEARCHHOTS);
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
                                ArrayList<HotWords> list = new ArrayList<HotWords>();
                                HotWords searchhots;
                                JSONObject searchhonsObj;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    searchhonsObj = (JSONObject) jsonArray.get(i);
                                    searchhots = gson.fromJson(searchhonsObj.toString(), HotWords.class);
                                    list.add(searchhots);
                                }
                                bundle.putSerializable(EXTRA_SEARCHHOTS_LIST, list);
                                notice(Event.GET_INDEX_SEARCHHOTS_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_INDEX_SEARCHHOTS_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_INDEX_SEARCHHOTS_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 搜索商品
     */
    private void getSearchGoods(int shopId, String keywords) {
        SearchGoodsReq req = new SearchGoodsReq(ReqInfo.REQ_GET_SEARCH_GOODS, shopId, keywords);
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
                                    JSONArray array = obj.getJSONArray(GlobalValue.KEY_DATA);
                                    ArrayList<Goods> list = new ArrayList<Goods>();
                                    Goods goods;
                                    JSONObject object;
                                    if (array != null && array.length() > 0) {
                                        for (int i = 0; i < array.length(); i++) {
                                            object = (JSONObject) array.get(i);
                                            goods = gson.fromJson(object.toString(), Goods.class);
                                            list.add(goods);
                                        }
                                        bundle.putSerializable(EXTRA_GOODS_LIST, list);
                                    }
                                }
                                notice(Event.GET_SEARCH_GOODS_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_SEARCH_GOODS_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_SEARCH_GOODS_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 通过分类id获取商品列表
     */
    private void getGoodsByType(final int typeId, int page, int limit, final int loadtype) {
        CategoryGoodsReq req = new CategoryGoodsReq(ReqInfo.REQ_GET_GETGOODSBYTYPE,  typeId, page, limit);
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
                            bundle.putInt(EXTRA_LOADTYPE, loadtype);
                            bundle.putInt(EXTRA_TYPECODE, typeId);

                            if (success) {
                                if (obj.has(GlobalValue.KEY_DATA)) {
                                    JSONObject dataObj = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    GoodsListViewModel model = gson.fromJson(dataObj.toString(), GoodsListViewModel.class);
                                    bundle.putSerializable(EXTRA_GOODSTYPELIST, model);
                                }
                                notice(Event.GET_GOODS_BY_TYPE_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_GOODS_BY_TYPE_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_GOODS_BY_TYPE_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 商品详情
     */
    private void getGoodsDetails(int changcode) {
        GoodsDetailsReq req = new GoodsDetailsReq(ReqInfo.REQ_GET_GOODS_DETAILS, changcode);
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
                                    JSONObject dataObj = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    GoodsViewModel model = gson.fromJson(dataObj.toString(), GoodsViewModel.class);
                                    bundle.putSerializable(EXTRA_GOODINFO, model);
                                }
                                notice(Event.GET_GOODS_DETAILS_SUCCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_GOODS_DETAILS_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_GOODS_DETAILS_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }
}
