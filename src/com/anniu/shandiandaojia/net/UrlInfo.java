package com.anniu.shandiandaojia.net;

public class UrlInfo {
    public final static String PROTOCOL = "http://";
    public final static String HOST = "api.shandiandaojia.com";
    //    public final static String HOST = "www.anniuhoutai.com";
    public final static String PORT = ":80";

//    public final static String HOST = "192.168.188.201";
//    public final static String PORT = ":8080";

    public final static String PREFIX = "/btfl";
    public final static String BASEURL = PROTOCOL + HOST + PORT + PREFIX;

    public static String getUrl(int id) {
        switch (id) {
            case ReqInfo.REQ_GET_LOCATION://通过经纬度定位
                return BASEURL + "/shopList";
            case ReqInfo.REQ_GET_LOCATION_ADDR://通过地址定位
                return BASEURL + "/shop/search";
            case ReqInfo.REQ_GET_SHOP_INFO://获取商铺首页数据
                return BASEURL + "/shop/show";
            case ReqInfo.REQ_POST_VERFICATION_CODE://获取验证码
                return BASEURL + "/user/sendCode";
            case ReqInfo.REQ_POST_CHECKOUT_VERFI_CODE://检验验证码并登录
                return BASEURL + "/user/login";
            case ReqInfo.REQ_GET_GOODS_DETAILS://商品详情
                return BASEURL + "/shopGoods/show";
            case ReqInfo.REQ_GET_PERSON_SETTING://获取个人中心详情
                return BASEURL + "/user/show";
            case ReqInfo.REQ_POST_PERSON_NAME_SEX://修改用户信息
                return BASEURL + "/user/save";
            case ReqInfo.REQ_GET_FIND_ALLORDERS://获取用户订单
                return BASEURL + "/order/list";
            case ReqInfo.REQ_GET_SELECT_BROWERS_SHOP://首页重新选择便利店
                return BASEURL + "/shop/viewLog";
            case ReqInfo.REQ_GET_PERSON_FINDUSERLOC://获取个人中心我的地址
                return BASEURL + "/userAddress/list";
            case ReqInfo.REQ_POST_UPDATE_CART_GOOD_NUM://添加或删除/修改商品数量
                return BASEURL + "/cartGoods/save";
            case ReqInfo.REQ_POST_PERSON_EDITADDR://修改用户收货信息--修改
                return BASEURL + "/userAddress/save";
            case ReqInfo.REQ_POST_PERSON_INSERTADDR://新增用户收货信息
                return BASEURL + "/userAddress/save";
            case ReqInfo.REQ_GET_PERSON_EDITDEFAULT_ADDR://选择用户默认收货信息
                return BASEURL + "/userAddress/setDefault";
            case ReqInfo.REQ_GET_CARTLIST://购物车商品列表
                return BASEURL + "/cartGoods/list";
            case ReqInfo.REQ_POST_DELETEADDR://删除用户信息--单个
                return BASEURL + "/userAddress/delete";
            case ReqInfo.REQ_GET_CANCELORDER://取消订单
                return BASEURL + "/order/cancel";
            case ReqInfo.REQ_GET_CONFIRMORDER://确认订单
                return BASEURL + "/order/confirm";
            case ReqInfo.REQ_GET_ORDERSTATUS://订单详情
                return BASEURL + "/order/show";
            case ReqInfo.REQ_GET_CARTINFO://购物车页面
                return BASEURL + "/cartGoods/list";
            case ReqInfo.REQ_GET_DELETECARTGOOD://根据商品商店id 和用户id 删除 购物车 列表的一条
                return BASEURL + "/cartGoods/delete";
            case ReqInfo.REQ_POST_SUBMITADVICE://意见反馈
                return BASEURL + "/feedback/submit";
            case ReqInfo.REQ_GET_GETGOODSBYTYPE://按照商品分类获取 商品信息
                return BASEURL + "/shopGoods/list2";
            case ReqInfo.REQ_POST_COMPLAINORDER://投诉商铺
                return BASEURL + "/order/complainorder";
            case ReqInfo.REQ_GET_GOODS://分类首次进入显示全部商品
                return BASEURL + "/shopGoods/typeList";
            case ReqInfo.REQ_GET_ACKORDER://结算跳转订单确认页面
                return BASEURL + "/order/preview";
            case ReqInfo.REQ_GET_VERSION://版本检查更新
                return BASEURL + "/version/latest";
            case ReqInfo.REQ_GET_FINDUSERCOUPON://优惠券
                return BASEURL + "/coupon/list";
            case ReqInfo.REQ_GET_FINDWATERTICKET://水券
                return BASEURL + "/person/findwaterticket";
            case ReqInfo.REQ_GET_WATER_FIND://初始化一键送水界面
                return BASEURL + "/ticket/list";
            case ReqInfo.REQ_GET_WATER_DETAIL://水票详情
                return BASEURL + "/ticket/show";
            case ReqInfo.REQ_GET_WATER_SUBMIT://水票支付
                return BASEURL + "/ticket/submit";
            case ReqInfo.REQ_GET_WATER_USETICKETS://进入一键送水界面
                return BASEURL + "/userTicket/preview";
            case ReqInfo.REQ_POST_WATER_ORDER://提交水订单
                return BASEURL + "/userTicket/submit";
            case ReqInfo.REQ_GET_INDEX_SEARCHHOTS://点击进入搜索界面
                return BASEURL + "/hotWord/list";
            case ReqInfo.REQ_GET_SEARCH_GOODS://搜索商品
                return BASEURL + "/shopGoods/search";
            case ReqInfo.REQ_GET_PERSON_FINDTICKETS://获取个人中心我的水票
                return BASEURL + "/userTicket/list";
            case ReqInfo.REQ_POST_ORDER_INSERTORDER://普通商品提交订单
                return BASEURL + "/order/submit";
            case ReqInfo.REQ_GET_WATER_SELECTEDTICKET://点击切换使用水票
                return BASEURL + "/water/selectedticket";
            case ReqInfo.REQ_GET_ORDER_FINDCOUPON://触发优惠券
                return BASEURL + "/coupon/list";
            case ReqInfo.REQ_GET_GETPREPAYID://微信支付获取prepayid
                return BASEURL + "/tenpay/getprepayid";
            case ReqInfo.REQ_GET_WXCALLBACK://微信支付获取订单状态
                return BASEURL + "/order/check";
            case ReqInfo.REQ_POST_EXCEPTION://提交日志
                return BASEURL + "/tenpay/checkpay";
            case ReqInfo.REQ_GET_LOGOUT://登出
                return BASEURL + "/user/logout";
            case ReqInfo.REQ_GET_CATEGORY_INFO://分类页
                return BASEURL + "/shopGoods/list";
            case ReqInfo.REQ_GET_COUPON_AMOUNT://水票返款金额
                return BASEURL + "/userTicket/couponAmount";

            default:
                break;
        }
        return null;
    }
}
