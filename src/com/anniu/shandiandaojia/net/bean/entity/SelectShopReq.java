package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author zxl
 * @ClassName: SelectShopReq
 * @Description: 重新选择店铺
 * @date 2015年6月5日 下午2:11:16
 */
public class SelectShopReq extends HttpReq {

    //精度
    private String lng;
    // 纬度
    private String lat;

    public SelectShopReq(int reqId, String lng, String lat) {
        super();
        this.id = reqId;
        this.lng = lng;
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    @Override
    public String getParams() {
        return "?lng=" + lng + "&lat=" + lat;
    }

    @Override
    public String parseData(String str) {
        return null;
    }

}
