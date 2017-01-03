package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.net.bean.HttpReq;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.SPUtils;

/**
 * @author YY
 * @ClassName: CheckOutVetifyCodeReq
 * @Description: 校验验证码
 * @date 2015年6月3日 下午6:00:39
 */
public class CheckOutVetifyCodeReq extends HttpReq {
    private String tel;
    private String code;
    private int shopId;

    public CheckOutVetifyCodeReq(int reqId, String tel, String code) {
        this.id = reqId;
        this.tel = tel;
        this.code = code;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    @Override
    public String getParams() {
        return "tel=" + tel + "&code=" + code
                + "&shopId=" + SPUtils.getInt(ActivityMgr.getContext(), GlobalInfo.KEY_SHOPCODE, 0);
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}
