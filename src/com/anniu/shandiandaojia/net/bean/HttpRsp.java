package com.anniu.shandiandaojia.net.bean;

public class HttpRsp {
    public static final int CODE_TIMEOUT = -99;
    public static final int CODE_UNKNOW_HOST = -100;
    public static final int CODE_NET_ERROR = -101;
    public static final int CODE_SUCCESS = 200;

    //http应答码
    public int code;
    public String msg;
    public String result;
    public byte[] data;

    public String getMsg(int errorCode) {
        String msg;
        switch (errorCode) {
            case CODE_TIMEOUT:
                msg = "请求超时，请稍后重试！";
                break;
            case CODE_UNKNOW_HOST:
                msg = "网络请求错误，请稍后重试！";
                break;
            case CODE_NET_ERROR:
                msg = "网络连接错误，请稍后重试！";
                break;
            default:
                msg = "网络请求错误，请稍后重试！";
                break;
        }
        return msg;
    }

}
