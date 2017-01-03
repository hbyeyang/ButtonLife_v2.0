package com.anniu.shandiandaojia.db.jsondb;

import java.io.Serializable;
import java.util.List;

/**
 * @author YY
 * @ClassName: OrderInfo
 * @Description: 订单详情
 * @date 2015年6月9日 下午3:24:30
 */
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = -1266443445040629024L;
    private int id;
    private String remark;
    private String no;
    private int userId;
    private String userName;
    private int shopId;
    private String type;//便利店商品
    private String postStartTime;
    private String postEndTime;
    private double goodsAmount;
    private double couponAmount;
    private double postFee;
    private double paymentAmount;
    private int couponId;
    private String receivedCode;
    private int addressId;
    private String note;
    private boolean cancelled;
    private String cancelNote;
    // 订单状态 1、已提交 2、发货中  3、已完成  4、商家取消  5、用户取消  6、待支付
    private OrderStatus status;
    private long createTime;

    private List<OrderGoods> goods;
    private MyAddress address;
    private Payment payment;
    private ShopInfo shop;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public ShopInfo getShop() {
        return shop;
    }

    public void setShop(ShopInfo shop) {
        this.shop = shop;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostStartTime() {
        return postStartTime;
    }

    public void setPostStartTime(String postStartTime) {
        this.postStartTime = postStartTime;
    }

    public String getPostEndTime() {
        return postEndTime;
    }

    public void setPostEndTime(String postEndTime) {
        this.postEndTime = postEndTime;
    }

    public double getGoodsAmount() {
        return goodsAmount;
    }

    public void setGoodsAmount(double goodsAmount) {
        this.goodsAmount = goodsAmount;
    }

    public double getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(double couponAmount) {
        this.couponAmount = couponAmount;
    }

    public double getPostFee() {
        return postFee;
    }

    public void setPostFee(double postFee) {
        this.postFee = postFee;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public String getReceivedCode() {
        return receivedCode;
    }

    public void setReceivedCode(String receivedCode) {
        this.receivedCode = receivedCode;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    public String getCancelNote() {
        return cancelNote;
    }

    public void setCancelNote(String cancelNote) {
        this.cancelNote = cancelNote;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderGoods> getGoods() {
        return goods;
    }

    public void setGoods(List<OrderGoods> goods) {
        this.goods = goods;
    }

    public MyAddress getAddress() {
        return address;
    }

    public void setAddress(MyAddress address) {
        this.address = address;
    }
}
