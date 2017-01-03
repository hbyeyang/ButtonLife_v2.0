package com.anniu.shandiandaojia.base;

import android.content.Context;
import android.os.Bundle;

import com.anniu.shandiandaojia.net.bean.HttpReq;
import com.anniu.shandiandaojia.net.bean.SocketReq;
import com.anniu.shandiandaojia.service.HttpService;
import com.anniu.shandiandaojia.service.HttpService.ActionListener;
import com.google.gson.Gson;

public abstract class BaseLogic implements ActionListener {
    public static String EXTRA_ERROR = "extra_error";
    public static String EXTRA_ERRORMESSAGE = "errorMessage";
    protected HttpService mService;
    protected Context mContext;
    protected Gson gson = new Gson();

    protected BaseLogic(HttpService service) {
        mService = service;
        mContext = service.getApplicationContext();
    }

    protected void sendHttpReq(HttpReq req) {
        mService.sendHttpReq(req);
    }

    protected void sendSocketReq(SocketReq req) {
        mService.sendSocketReq(req);
    }

    protected void notice(int eventId, Bundle bundle) {
        mService.sendBroadCast(eventId, bundle);
    }
}
