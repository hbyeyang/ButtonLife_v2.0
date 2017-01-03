package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author YY
 * @ClassName: VerityCodeReq
 * @Description: 发送验证码
 * @date 2015年6月3日 下午4:11:57
 */
public class VerityCodeReq extends HttpReq {
    private String tel;

    public VerityCodeReq(int reqid, String user_tel) {
        super();
        this.id = reqid;
        this.tel = user_tel;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Override
    public String getParams() {
        return "tel=" + tel;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}
