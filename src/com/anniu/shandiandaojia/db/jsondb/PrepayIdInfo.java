package com.anniu.shandiandaojia.db.jsondb;

import java.io.Serializable;

/**
 * @author zxl
 * @ClassName: PrepayIdInfo
 * @Description: 获取微信支付的信息
 * @date 2015年7月6日 下午2:48:34
 */
public class PrepayIdInfo implements Serializable {

    private static final long serialVersionUID = -39241639963472404L;
    private String appid;
    private String noncestr;
    private String sign;
    private String partnerid;
    private String prepayid;
    private String timestamp;

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

}
