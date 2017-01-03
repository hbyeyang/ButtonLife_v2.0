package com.anniu.shandiandaojia.net.bean.entity;

import com.anniu.shandiandaojia.net.bean.HttpReq;

/**
 * @author zxl
 * @ClassName: LocationReq
 * @Description: 请求地理位置
 * @date 2015年5月30日 下午5:26:40
 */
public class LocationReq extends HttpReq {

    /**
     * 地图api的精度
     */
    private String lng;
    /**
     * 地图api的纬度
     */
    private String lat;
    private int type;

    public LocationReq(int reqId, String lng, String lat, int type) {
        super();
        this.id = reqId;
        this.lng = lng;
        this.lat = lat;
        this.type = type;
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
        return "?lng=" + lng + "&lat=" + lat + "&type=" + type;
    }

    @Override
    public String parseData(String str) {
        return null;
    }
}
