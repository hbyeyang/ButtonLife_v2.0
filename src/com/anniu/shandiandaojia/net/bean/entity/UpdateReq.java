package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author zxl
 * @ClassName: UpdateReq
 * @Description: 请求更新
 * @date 2015年5月19日 下午3:22:41
 */
public class UpdateReq extends HttpReq {

    public UpdateReq(int reqId) {
        this.id = reqId;
    }

    @Override
    public String getParams() {
        return "?type=" + "APK";
    }

    @Override
    public String parseData(String str) {
        return null;
    }
}
