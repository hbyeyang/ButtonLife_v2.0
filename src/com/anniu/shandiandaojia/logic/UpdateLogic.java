package com.anniu.shandiandaojia.logic;

import android.content.Intent;
import android.os.Bundle;

import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.net.GlobalValue;
import com.anniu.shandiandaojia.net.NetMgr.OnHttpRsp;
import com.anniu.shandiandaojia.net.ReqInfo;
import com.anniu.shandiandaojia.net.bean.HttpRsp;
import com.anniu.shandiandaojia.net.bean.entity.UpdateReq;
import com.anniu.shandiandaojia.service.HttpService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * UpdateLogic
 * 更新业务类
 */
public class UpdateLogic extends BaseLogic {
    public static String ACTION_GET_UPDATEINFO = "UpdateLogic.ACTION_GET_UPDATEINFO";
    public static String EXTRA_UPDATE_MODE = "update_mode";
    public static String EXTRA_VERSION_NAME = "name";
    public static String EXTRA_VERSION_URL = "url";
    public static String EXTRA_DISCRIPT = "description";
    public static String EXTRA_FORCED = "forced";
    public static int UPDATE_STRATEGY_AUTO = 1;
    public static int UPDATE_STRATEGY_USER = 2;
    public static String EXTRA_CONTOL_TYPE = "contol_type";

    public UpdateLogic(HttpService service) {
        super(service);
        List<String> actions = new ArrayList<String>();
        actions.add(ACTION_GET_UPDATEINFO);
        mService.registerAction(this, actions);
    }

    @Override
    public void onHandleAction(Intent intent) {
        String action = intent.getAction();
        if (ACTION_GET_UPDATEINFO.equals(action)) {
            int update_mode = intent.getExtras().getInt(EXTRA_UPDATE_MODE);
            getServerCode(update_mode);
        }
    }

    /**
     * 版本升级
     */
    private void getServerCode(final int update_mode) {
        UpdateReq req = new UpdateReq(ReqInfo.REQ_GET_VERSION);
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
                            bundle.putInt(EXTRA_CONTOL_TYPE, update_mode);
                            if (success) {
                                if (obj.has(GlobalValue.KEY_DATA)) {
                                    JSONObject dataObj = obj.getJSONObject(GlobalValue.KEY_DATA);
                                    if (dataObj.has(EXTRA_VERSION_NAME)) {
                                        String versionName = dataObj.getString(EXTRA_VERSION_NAME);
                                        bundle.putString(EXTRA_VERSION_NAME, versionName);
                                    }
                                    if (dataObj.has(EXTRA_VERSION_URL)) {
                                        String url = dataObj.getString(EXTRA_VERSION_URL);
                                        bundle.putString(EXTRA_VERSION_URL, url);
                                    }
                                    if (dataObj.has(EXTRA_DISCRIPT)) {
                                        String description = dataObj.getString(EXTRA_DISCRIPT);
                                        bundle.putString(EXTRA_DISCRIPT, description);
                                    }
                                    if (dataObj.has(EXTRA_FORCED)) {
                                        boolean forced = dataObj.getBoolean(EXTRA_FORCED);
                                        bundle.putBoolean(EXTRA_FORCED, forced);
                                    }
                                }
                                notice(Event.UPDATE_SUCCESS, bundle);
                            } else {
                                bundle.putString(BaseLogic.EXTRA_ERROR, obj.getString(EXTRA_ERRORMESSAGE));
                                notice(Event.UPDATE_FAILED, bundle);
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    bundle.putString(BaseLogic.EXTRA_ERROR, httpRsp.getMsg(httpRsp.code));
                    notice(Event.UPDATE_FAILED, bundle);
                }
            }
        });
        sendHttpReq(req);
    }
}
