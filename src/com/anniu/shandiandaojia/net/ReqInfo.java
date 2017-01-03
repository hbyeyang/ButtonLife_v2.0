package com.anniu.shandiandaojia.net;

public class ReqInfo {

    //定位
    public static final int REQ_GET_LOCATION = 101;
    //定位--通过地址
    public static final int REQ_GET_LOCATION_ADDR = 102;
    //获取商铺首页信息
    public static final int REQ_GET_SHOP_INFO = 103;
    //获取验证码
    public static final int REQ_POST_VERFICATION_CODE = 104;
    //校验验证码并登录
    public static final int REQ_POST_CHECKOUT_VERFI_CODE = 105;
    //获取商品详情
    public static final int REQ_GET_GOODS_DETAILS = 106;
    //获取个人中心详情
    public static final int REQ_GET_PERSON_SETTING = 107;
    //保存个人中心姓名性别
    public static final int REQ_POST_PERSON_NAME_SEX = 108;
    //获取用户订单
    public static final int REQ_GET_FIND_ALLORDERS = 109;
    //首页重新选择便利店
    public static final int REQ_GET_SELECT_BROWERS_SHOP = 110;
    //获取个人中心我的地址
    public static final int REQ_GET_PERSON_FINDUSERLOC = 111;
    //添加或删除/修改商品数量
    public static final int REQ_POST_UPDATE_CART_GOOD_NUM = 112;
    //修改用户收货信息--修改
    public static final int REQ_POST_PERSON_EDITADDR = 113;
    //修改用户收货信息--新增
    public static final int REQ_POST_PERSON_INSERTADDR = 114;
    //修改用户收货信息--选择默认地址
    public static final int REQ_GET_PERSON_EDITDEFAULT_ADDR = 115;
    //购物车商品列表
    public static final int REQ_GET_CARTLIST = 116;
    //删除用户收货信息--单个
    public static final int REQ_POST_DELETEADDR = 117;
    //取消订单
    public static final int REQ_GET_CANCELORDER = 118;
    //确认订单
    public static final int REQ_GET_CONFIRMORDER = 119;
    //订单详情
    public static final int REQ_GET_ORDERSTATUS = 120;
    //购物车详情
    public static final int REQ_GET_CARTINFO = 121;
    //根据商品商店id 和用户id 删除 购物车 列表的一条
    public static final int REQ_GET_DELETECARTGOOD = 122;
    //意见反馈
    public static final int REQ_POST_SUBMITADVICE = 123;
    //按照商品分类获取 商品信息
    public static final int REQ_GET_GETGOODSBYTYPE = 124;
    //投诉商铺
    public static final int REQ_POST_COMPLAINORDER = 125;
    //通过分类首次进入显示 全部商品
    public static final int REQ_GET_GOODS = 126;
    //结算跳转订单确认页面
    public static final int REQ_GET_ACKORDER = 127;
    //版本更新检测
    public static final int REQ_GET_VERSION = 128;
    //优惠券
    public static final int REQ_GET_FINDUSERCOUPON = 129;
    //水券
    public static final int REQ_GET_FINDWATERTICKET = 130;
    //初始化一键送水界面
    public static final int REQ_GET_WATER_FIND = 131;
    //水票详情
    public static final int REQ_GET_WATER_DETAIL = 132;
    //水票购买
    public static final int REQ_GET_WATER_SUBMIT = 133;
    //进入一键送水界面
    public static final int REQ_GET_WATER_USETICKETS = 134;
    //点击进入搜索界面
    public static final int REQ_GET_INDEX_SEARCHHOTS = 135;
    //搜索商品
    public static final int REQ_GET_SEARCH_GOODS = 136;
    //获取个人中心我的水票
    public static final int REQ_GET_PERSON_FINDTICKETS = 137;
    //用水票购买水
    public static final int REQ_POST_WATER_ORDER = 138;
    //普通商品提交订单
    public static final int REQ_POST_ORDER_INSERTORDER = 139;
    //点击切换使用水票
    public static final int REQ_GET_WATER_SELECTEDTICKET = 140;
    //触发优惠券
    public static final int REQ_GET_ORDER_FINDCOUPON = 141;
    //微信支付获取prepayid
    public static final int REQ_GET_GETPREPAYID = 142;
    //微信支付获取订单状态
    public static final int REQ_GET_WXCALLBACK = 143;
    //提交日志
    public static final int REQ_POST_EXCEPTION = 144;
    //登出
    public static final int REQ_GET_LOGOUT = 145;
    //分类页第一次获取
    public static final int REQ_GET_CATEGORY_INFO = 146;
    //购买水票返还的优惠券金额
    public static final int REQ_GET_COUPON_AMOUNT = 147;
}
