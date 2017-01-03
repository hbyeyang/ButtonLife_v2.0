package com.anniu.shandiandaojia.db.jsondb;

import java.io.Serializable;

/***
 * @author zxl
 * @ClassName: ShopInfo
 * @Description: 商铺信息
 * @date 2015年6月1日 下午4:00:58
 */
public class ShopInfo implements Serializable {

    private static final long serialVersionUID = -596521256770847099L;
    private int id;
    private String no;
    private String name;
    private String fullName;
    private int businessId;
    private String businessName;
    private String tel;
    private String address;
    private int type;
    private Double lng;
    private Double lat;
    private String openTime;
    private String closeTime;
    // 最低起送价
    private Double lowestPostPrice;
    // 配送费
    private Double postFee;
    //免费配送价
    private Double freePostPrice;
    //平均送货时间
    private int postTime;
    private int cityId;
    private String cityName;
    private ShopStatus status;
    private String note;
    private String pictureAddress;
    private int isHold;
    private int distance;

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public Double getLowestPostPrice() {
        return lowestPostPrice;
    }

    public void setLowestPostPrice(Double lowestPostPrice) {
        this.lowestPostPrice = lowestPostPrice;
    }

    public Double getPostFee() {
        return postFee;
    }

    public void setPostFee(Double postFee) {
        this.postFee = postFee;
    }

    public Double getFreePostPrice() {
        return freePostPrice;
    }

    public void setFreePostPrice(Double freePostPrice) {
        this.freePostPrice = freePostPrice;
    }

    public int getPostTime() {
        return postTime;
    }

    public void setPostTime(int postTime) {
        this.postTime = postTime;
    }

    public String getPictureAddress() {
        return pictureAddress;
    }

    public void setPictureAddress(String pictureAddress) {
        this.pictureAddress = pictureAddress;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public ShopStatus getStatus() {
        return status;
    }

    public void setStatus(ShopStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getIsHold() {
        return isHold;
    }

    public void setIsHold(int isHold) {
        this.isHold = isHold;
    }
}
