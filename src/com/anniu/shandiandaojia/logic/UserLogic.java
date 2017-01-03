package com.anniu.shandiandaojia.logic;

import android.content.Intent;
import android.os.Bundle;

import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.MyAddress;
import com.anniu.shandiandaojia.db.jsondb.UserInfo;
import com.anniu.shandiandaojia.net.GlobalValue;
import com.anniu.shandiandaojia.net.NetMgr.OnHttpRsp;
import com.anniu.shandiandaojia.net.ReqInfo;
import com.anniu.shandiandaojia.net.bean.HttpRsp;
import com.anniu.shandiandaojia.net.bean.entity.CheckOutVetifyCodeReq;
import com.anniu.shandiandaojia.net.bean.entity.ComplainorderReq;
import com.anniu.shandiandaojia.net.bean.entity.DeleteaddrReq;
import com.anniu.shandiandaojia.net.bean.entity.EditAddrReq;
import com.anniu.shandiandaojia.net.bean.entity.FindpersonbyidReq;
import com.anniu.shandiandaojia.net.bean.entity.FinduserlocReq;
import com.anniu.shandiandaojia.net.bean.entity.InsertAddrGetReq;
import com.anniu.shandiandaojia.net.bean.entity.InsertAddrReq;
import com.anniu.shandiandaojia.net.bean.entity.LoginOutReq;
import com.anniu.shandiandaojia.net.bean.entity.SubmitadviceReq;
import com.anniu.shandiandaojia.net.bean.entity.UpdatePersoninfoReq;
import com.anniu.shandiandaojia.net.bean.entity.VerityCodeReq;
import com.anniu.shandiandaojia.service.HttpService;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * @author YY
 * @ClassName: UserLogic
 * @Description: 用户Logic类
 * @date 2015年6月3日 下午3:37:15
 */
public class UserLogic extends BaseLogic {

    //获取个人中心详情
    public static String ACTION_GET_PERSON_SETTING = "UserLogic.ACTION_GET_PERSON_SETTING";
    //保存个人中心姓名性别
    public static String ACTION_POST_PERSON_NAME_SEX = "UserLogic.ACTION_POST_PERSON_NAME_SEX";
    //获取个人中心我的地址
    public static String ACTION_GET_PERSON_FINDUSERLOC = "UserLogic.ACTION_GET_PERSON_FINDUSERLOC";
    //获取验证码
    public static String ACTION_POST_VERFICATION_CODE = "UserLogic.ACTION_POST_VERFICATION_CODE";
    //登出
    public static String ACTION_LOGINOUT = "UserLogic.ACTION_LOGINOUT";
    //校验验证码 并登陆
    public static String ACTION_POST_CHECK_OUT_VERFICATION_CODE = "UserLogic.ACTION_POST_CHECK_OUT_VERFICATION_CODE";
    //修改收货信息--新增
    public static String ACTION_POST_PERSON_INSERTADDR = "UserLogic.ACTION_POST_PERSON_INSERTADDR";
    //修改收货信息--修改默认收货地址
    public static String ACTION_GET_PERSON_INSERTADDR = "UserLogic.ACTION_GET_PERSON_INSERTADDR";
    //修改收货信息--修改
    public static String ACTION_POST_PERSON_EDITADDR = "UserLogic.ACTION_POST_PERSON_EDITADDR";
    //修改收货信息--删除
    public static String ACTION_POST_PERSON_DELETEADDR = "UserLogic.ACTION_POST_PERSON_DELETEADDR";
    //意见反馈
    public static String ACTION_POST_SUBMITADVICE = "UserLogic.ACTION_POST_SUBMITADVICE";
    //投诉商铺
    public static String ACTION_POST_COMPLAINORDER = "UserLogic.ACTION_POST_COMPLAINORDER";

    public static String EXTRA_USER_CODE = "user_code";
    public static String EXTRA_USER_SEX = "user_sex";
    public static String EXTRA_PHONE = "user_tel";
    public static String EXTRA_VERTIFYCODE = "vertify_code";
    public static String EXTRA_USER_NAME = "user_name";
    public static String EXTRA_NICK_NAME = "nick_name";
    public static String EXTRA_ADDRESS_ID = "address_id";
    public static String EXTRA_USER_ADDR = "user_addr";
    public static String EXTRA_USER_DISTRICT = "user_district";
    public static String EXTRA_SUBMITADVICE = "submitadvice";
    public static String EXTRA_SHOP_CODE = "shop_code";
    public static String EXTRA_ORDER_NUM = "order_num";
    public static String EXTRA_USER = "user";
    public static String EXTRA_ISDEFAULT = "isDefault";
    public static String EXTRA_TYPE = "type";

    public UserLogic(HttpService service) {
        super(service);
        ArrayList<String> actions = new ArrayList<String>();
        actions.add(ACTION_POST_VERFICATION_CODE);
        actions.add(ACTION_POST_CHECK_OUT_VERFICATION_CODE);
        actions.add(ACTION_GET_PERSON_SETTING);
        actions.add(ACTION_POST_PERSON_NAME_SEX);
        actions.add(ACTION_GET_PERSON_FINDUSERLOC);
        actions.add(ACTION_POST_PERSON_EDITADDR);
        actions.add(ACTION_POST_PERSON_INSERTADDR);
        actions.add(ACTION_GET_PERSON_INSERTADDR);
        actions.add(ACTION_POST_PERSON_DELETEADDR);
        actions.add(ACTION_POST_SUBMITADVICE);
        actions.add(ACTION_POST_COMPLAINORDER);
        actions.add(ACTION_LOGINOUT);
        mService.registerAction(this, actions);
    }

    @Override
    public void onHandleAction(Intent intent) {
        String action = intent.getAction();
        if (ACTION_POST_VERFICATION_CODE.equals(action)) {
            String phone = intent.getExtras().getString(EXTRA_PHONE);
            postVerficationCode(phone);
        } else if (ACTION_POST_CHECK_OUT_VERFICATION_CODE.equals(action)) {
            String phone = intent.getExtras().getString(EXTRA_PHONE);
            String verficationCode = intent.getStringExtra(EXTRA_VERTIFYCODE);
            postCheckOutVerfyCode(phone, verficationCode);
        } else if (ACTION_GET_PERSON_SETTING.equals(action)) {
            getUserInfo();
        } else if (ACTION_POST_PERSON_NAME_SEX.equals(action)) {
            int userCode = intent.getIntExtra(EXTRA_USER_CODE, 0);
            String userName = intent.getStringExtra(EXTRA_NICK_NAME);
            boolean userSex = intent.getBooleanExtra(EXTRA_USER_SEX, false);
            postupdatepersoninfo(userName, userCode, userSex);
        } else if (ACTION_GET_PERSON_FINDUSERLOC.equals(action)) {
            getFindUserLoc();
        } else if (ACTION_POST_PERSON_EDITADDR.equals(action)) {
            int userLocCode = intent.getIntExtra(EXTRA_ADDRESS_ID, 0);
            String userName = intent.getStringExtra(EXTRA_USER_NAME);
            String userPhone = intent.getStringExtra(EXTRA_PHONE);
            String userAddr = intent.getStringExtra(EXTRA_USER_ADDR);
            String userDistrict = intent.getStringExtra(EXTRA_USER_DISTRICT);
            boolean isDefault = intent.getBooleanExtra(EXTRA_ISDEFAULT, false);
            posteditAddr(userLocCode, userName, userPhone, userAddr, userDistrict, isDefault);
        } else if (ACTION_POST_PERSON_INSERTADDR.equals(action)) {
            String contactName = intent.getStringExtra(EXTRA_USER_NAME);
            String contactTel = intent.getStringExtra(EXTRA_PHONE);
            String consignDistrict = intent.getStringExtra(EXTRA_USER_DISTRICT);
            String consignAddress = intent.getStringExtra(EXTRA_USER_ADDR);
            boolean isDefault = intent.getBooleanExtra(EXTRA_ISDEFAULT, false);
            postinsertAddr(contactName, contactTel, consignDistrict, consignAddress, isDefault);
        } else if (ACTION_GET_PERSON_INSERTADDR.equals(action)) {
            int addressId = intent.getIntExtra(UserLogic.EXTRA_ADDRESS_ID, 0);
            getinsertAddr(addressId);
        } else if (ACTION_POST_PERSON_DELETEADDR.equals(action)) {
            int addressId = intent.getIntExtra(UserLogic.EXTRA_ADDRESS_ID, 0);
            postdeleteaddr(addressId);
        } else if (ACTION_POST_SUBMITADVICE.equals(action)) {
            String content = intent.getStringExtra(UserLogic.EXTRA_SUBMITADVICE);
            String type = intent.getStringExtra(UserLogic.EXTRA_TYPE);
            postsubmitadvice(content, type);
        } else if (ACTION_POST_COMPLAINORDER.equals(action)) {
            String content = intent.getStringExtra(UserLogic.EXTRA_SUBMITADVICE);
            String type = intent.getStringExtra(UserLogic.EXTRA_TYPE);
            postComplainorder(content, type);
        } else if (ACTION_LOGINOUT.equals(action)) {
            loginOut();
        }
    }

    private void loginOut() {
        LoginOutReq req = new LoginOutReq(ReqInfo.REQ_GET_LOGOUT);
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
                                notice(Event.GET_LOGINOUT_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_LOGINOUT_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_LOGINOUT_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 投诉商铺
     */
    private void postComplainorder(String content, String type) {
        ComplainorderReq req = new ComplainorderReq(ReqInfo.REQ_POST_COMPLAINORDER, content, type);
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
                                notice(Event.POST_COMPLAINORDER_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.POST_COMPLAINORDER_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.POST_COMPLAINORDER_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 用户意见反馈
     */
    private void postsubmitadvice(String content, String type) {
        SubmitadviceReq req = new SubmitadviceReq(ReqInfo.REQ_POST_SUBMITADVICE, content, type);
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
                                notice(Event.POST_SUBMITADVICE_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.POST_SUBMITADVICE_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.POST_SUBMITADVICE_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 删除用户收货信息
     */
    private void postdeleteaddr(int addressId) {
        DeleteaddrReq req = new DeleteaddrReq(ReqInfo.REQ_POST_DELETEADDR, addressId);
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
                                notice(Event.POST_PERSON_DELETEADDR_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.POST_PERSON_DELETEADDR_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.POST_PERSON_DELETEADDR_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 修改默认的收货地址
     */
    private void getinsertAddr(final int addressId) {
        InsertAddrGetReq req = new InsertAddrGetReq(ReqInfo.REQ_GET_PERSON_EDITDEFAULT_ADDR, addressId);
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
                            bundle.putInt(EXTRA_ADDRESS_ID, addressId);
                            if (success) {
                                notice(Event.GET_PERSON_EDITDEFAULT_ADDR_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_PERSON_EDITDEFAULT_ADDR_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_PERSON_EDITDEFAULT_ADDR_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 新增用户收货地址
     */
    private void postinsertAddr(String contactName, String contactTel, String consignDistrict, String consignAddress, boolean isDefault) {
        InsertAddrReq req = new InsertAddrReq(ReqInfo.REQ_POST_PERSON_INSERTADDR, contactName, contactTel, consignDistrict, consignAddress, isDefault);
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
                                notice(Event.POST_PERSON_INSERTADDR_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.POST_PERSON_INSERTADDR_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.POST_PERSON_INSERTADDR_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 修改用户收货地址--修改
     */
    private void posteditAddr(int addressId, String userName, String userPhone, String userAddr, String userDistrict, boolean isDefault) {
        EditAddrReq req = new EditAddrReq(ReqInfo.REQ_POST_PERSON_EDITADDR, addressId, userName, userPhone, userAddr, userDistrict, isDefault);
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
                                notice(Event.POST_PERSON_EDITADDR_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.POST_PERSON_EDITADDR_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.POST_PERSON_EDITADDR_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 获取个人中心我的地址
     */
    private void getFindUserLoc() {
        FinduserlocReq req = new FinduserlocReq(ReqInfo.REQ_GET_PERSON_FINDUSERLOC);
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
                                    ArrayList<MyAddress> list = new ArrayList<MyAddress>();
                                    if (array.length() > 0) {
                                        MyAddress address;
                                        JSONObject object;
                                        for (int i = 0; i < array.length(); i++) {
                                            object = (JSONObject) array.get(i);
                                            address = gson.fromJson(object.toString(), MyAddress.class);
                                            list.add(address);
                                        }
                                        bundle.putSerializable(EXTRA_USER_ADDR, list);
                                    } else {
                                        bundle.putSerializable(EXTRA_USER_ADDR, null);
                                    }
                                }
                                notice(Event.GET_PERSON_FINDUSERLOC_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_PERSON_FINDUSERLOC_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_PERSON_FINDUSERLOC_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 保存个人中心姓名性别
     */
    private void postupdatepersoninfo(String userName, int userCode, boolean userSex) {
        UpdatePersoninfoReq req = new UpdatePersoninfoReq(ReqInfo.REQ_POST_PERSON_NAME_SEX, userName, userCode, userSex);
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
                                notice(Event.POST_PERSON_NAME_SEX_SUCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.POST_PERSON_NAME_SEX_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.POST_PERSON_NAME_SEX_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 获取个人中心详情
     */
    private void getUserInfo() {
        FindpersonbyidReq req = new FindpersonbyidReq(ReqInfo.REQ_GET_PERSON_SETTING);
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
                                    JSONObject object = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    UserInfo info = gson.fromJson(object.toString(), UserInfo.class);
                                    bundle.putSerializable(EXTRA_USER, info);
                                }
                                notice(Event.GET_PERSON_SETTING_SUCESS, bundle);
                            } else {
                                if (errorCode == 101) {
                                    SPUtils.saveBoolean(mContext, GlobalInfo.KEY_ISLOGIN, false);
                                    SPUtils.saveString(mContext, GlobalValue.KEY_COOKIE, "");
                                }
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.GET_PERSON_SETTING_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.GET_PERSON_SETTING_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 校验验证码并登录
     */
    private void postCheckOutVerfyCode(String phone, String verficationCode) {
        CheckOutVetifyCodeReq req = new CheckOutVetifyCodeReq(ReqInfo.REQ_POST_CHECKOUT_VERFI_CODE, phone, verficationCode);
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
                                notice(Event.POST_CHECK_OUT_VERFICATION_SUCCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.POST_CHECK_OUT_VERFICATION_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.POST_CHECK_OUT_VERFICATION_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }

    /**
     * 获取验证码
     */
    private void postVerficationCode(String phone) {
        VerityCodeReq req = new VerityCodeReq(ReqInfo.REQ_POST_VERFICATION_CODE, phone);
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
                                notice(Event.POST_SEND_VERFICATION_SUCCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.POST_SEND_VERFICATION_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.POST_SEND_VERFICATION_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }
}
