package com.anniu.shandiandaojia.logic;

public class Event {

    //全局的UI事件: 显示全局的toast或者dialog时需要设置具体的 global event
    public static final int GLOBAL_UI = 1;

    //登录成功事件
    public static final int LOGIN_SUCCESS = GLOBAL_UI + 1;
    //登录失败事件
    public static final int LOGIN_FAILED = LOGIN_SUCCESS + 1;

    //更新成功事件
    public static final int UPDATE_SUCCESS = LOGIN_FAILED + 1;
    //更新失败事件
    public static final int UPDATE_FAILED = UPDATE_SUCCESS + 1;

    //定位成功事件
    public static final int GET_LOCATION_SUCCESS = UPDATE_FAILED + 1;
    //定位失败事件
    public static final int GET_LOCATION_FAILED = GET_LOCATION_SUCCESS + 1;

    //通过地址定位成功事件
    public static final int GET_LOCATION_ADDR_SUCCESS = GET_LOCATION_FAILED + 1;
    //通过地址定位失败事件
    public static final int GET_LOCATION_ADDR_FAILED = GET_LOCATION_ADDR_SUCCESS + 1;

    //获取商铺首页信息成功事件
    public static final int GET_SHOP_INFO_SUCCESS = GET_LOCATION_ADDR_FAILED + 1;
    //获取商铺首页信息失败事件
    public static final int GET_SHOP_INFO_FAILED = GET_SHOP_INFO_SUCCESS + 1;

    //发送验证码成功事件
    public static final int POST_SEND_VERFICATION_SUCCESS = GET_SHOP_INFO_FAILED + 1;
    //发送验证码失败事件
    public static final int POST_SEND_VERFICATION_FAILED = POST_SEND_VERFICATION_SUCCESS + 1;

    //校验验证码并成功登录事件
    public static final int POST_CHECK_OUT_VERFICATION_SUCCESS = POST_SEND_VERFICATION_FAILED + 1;
    //校验验证码并成功失败事件
    public static final int POST_CHECK_OUT_VERFICATION_FAILED = POST_CHECK_OUT_VERFICATION_SUCCESS + 1;

    //获取商品详情成功事件
    public static final int GET_GOODS_DETAILS_SUCCESS = POST_CHECK_OUT_VERFICATION_FAILED + 1;
    //获取商品详情失败事件
    public static final int GET_GOODS_DETAILS_FAILED = GET_GOODS_DETAILS_SUCCESS + 1;

    //绑定水票成功事件
    public static final int GET_BINDING_WATER_SUCESS = GET_GOODS_DETAILS_FAILED + 1;
    //绑定水票失败事件
    public static final int GET_BINDING_WATER_FAILED = GET_BINDING_WATER_SUCESS + 1;

    //获取水票成功事件
    public static final int GET_WATER_TICKET_SUCESS = GET_BINDING_WATER_FAILED + 1;
    //获取水票失败事件
    public static final int GET_WATER_TICKET_FAILED = GET_WATER_TICKET_SUCESS + 1;

    //绑定代金券成功事件
    public static final int GET_BINDING_VOUCHER_SUCESS = GET_WATER_TICKET_FAILED + 1;
    //绑定代金券失败事件
    public static final int GET_BINDING_VOUCHER_FAILED = GET_BINDING_VOUCHER_SUCESS + 1;

    //获取代金券成功事件
    public static final int GET_VOUCHER_TICKET_SUCESS = GET_BINDING_VOUCHER_FAILED + 1;
    //获取代金券失败事件
    public static final int GET_VOUCHER_TICKET_FAILED = GET_VOUCHER_TICKET_SUCESS + 1;

    //获取个人中心设置成功事件
    public static final int GET_PERSON_SETTING_SUCESS = GET_VOUCHER_TICKET_FAILED + 1;
    //获取个人中心设置失败事件
    public static final int GET_PERSON_SETTING_FAILED = GET_PERSON_SETTING_SUCESS + 1;

    //保存个人中心姓名性别成功事件
    public static final int POST_PERSON_NAME_SEX_SUCESS = GET_PERSON_SETTING_FAILED + 1;
    //保存个人中心姓名性别失败事件
    public static final int POST_PERSON_NAME_SEX_FAILED = POST_PERSON_NAME_SEX_SUCESS + 1;

    //获取用户订单成功事件
    public static final int GET_USER_ALLORDERS_SUCESS = POST_PERSON_NAME_SEX_FAILED + 1;
    //获取用户订单失败事件
    public static final int GET_USER_ALLORDERS_FAILED = GET_USER_ALLORDERS_SUCESS + 1;

    //首页重新选择便利店成功事件
    public static final int GET_SELECT_BROWER_SHOP_SUCESS = GET_USER_ALLORDERS_FAILED + 1;
    //首页重新选择便利店失败事件
    public static final int GET_SELECT_BROWER_SHOP_FAILED = GET_SELECT_BROWER_SHOP_SUCESS + 1;

    //获取个人中心我的地址成功事件
    public static final int GET_PERSON_FINDUSERLOC_SUCESS = GET_SELECT_BROWER_SHOP_FAILED + 1;
    //获取个人中心我的地址失败事件
    public static final int GET_PERSON_FINDUSERLOC_FAILED = GET_PERSON_FINDUSERLOC_SUCESS + 1;

    //添加或删除/修改商品数量成功事件
    public static final int INSERT_OR_UPDATE_CART_GOOD_NUM_SUCESS = GET_PERSON_FINDUSERLOC_FAILED + 1;
    //添加或删除/修改商品数量失败事件
    public static final int INSERT_OR_UPDATE_CART_GOOD_NUM_FAILED = INSERT_OR_UPDATE_CART_GOOD_NUM_SUCESS + 1;

    //修改用户收货信息--修改成功事件
    public static final int POST_PERSON_EDITADDR_SUCESS = INSERT_OR_UPDATE_CART_GOOD_NUM_FAILED + 1;
    //修改用户收货信息--修改失败事件
    public static final int POST_PERSON_EDITADDR_FAILED = POST_PERSON_EDITADDR_SUCESS + 1;

    //修改用户收货信息--新增成功事件
    public static final int POST_PERSON_INSERTADDR_SUCESS = POST_PERSON_EDITADDR_FAILED + 1;
    //修改用户收货信息--新增失败事件
    public static final int POST_PERSON_INSERTADDR_FAILED = POST_PERSON_INSERTADDR_SUCESS + 1;

    //修改用户收货信息--修改默认收货地址成功事件
    public static final int GET_PERSON_EDITDEFAULT_ADDR_SUCESS = POST_PERSON_INSERTADDR_FAILED + 1;
    //修改用户收货信息--修改默认收货地址失败事件
    public static final int GET_PERSON_EDITDEFAULT_ADDR_FAILED = GET_PERSON_EDITDEFAULT_ADDR_SUCESS + 1;

    //获取购物车商品列表成功事件
    public static final int GET_CART_LIST_SUCESS = GET_PERSON_EDITDEFAULT_ADDR_FAILED + 1;
    //获取购物车商品列表失败事件
    public static final int GET_CART_LIST_FAILED = GET_CART_LIST_SUCESS + 1;

    //删除用户收货信息成功事件
    public static final int POST_PERSON_DELETEADDR_SUCESS = GET_CART_LIST_FAILED + 1;
    //删除用户收货信息失败事件
    public static final int POST_PERSON_DELETEADDR_FAILED = POST_PERSON_DELETEADDR_SUCESS + 1;

    //取消订单成功事件
    public static final int POST_CANCEL_ORDER_SUCESS = POST_PERSON_DELETEADDR_FAILED + 1;
    //取消订单失败事件
    public static final int POST_CANCEL_ORDER_FAILED = POST_CANCEL_ORDER_SUCESS + 1;

    //确认订单成功事件
    public static final int GET_CONFIRM_ORDER_SUCESS = POST_CANCEL_ORDER_FAILED + 1;
    //确认订单失败事件
    public static final int GET_CONFIRM_ORDER_FAILED = GET_CONFIRM_ORDER_SUCESS + 1;

    //获取订单详情成功事件
    public static final int GET_ORDER_STATUS_SUCESS = GET_CONFIRM_ORDER_FAILED + 1;
    //获取订单详情失败事件
    public static final int GET_ORDER_STATUS_FAILED = GET_ORDER_STATUS_SUCESS + 1;

    //获取商品详情页购物车商品列表成功事件
    public static final int GET_CART_LIST_GOODS_DETAILS_SUCESS = GET_ORDER_STATUS_FAILED + 1;
    //获取商品详情页购物车商品列表失败事件
    public static final int GET_CART_LIST_GOODS_DETAILS_FAILED = GET_CART_LIST_GOODS_DETAILS_SUCESS + 1;

    //获取购物车页面信息成功事件
    public static final int GET_CART_INFO_SUCESS = GET_CART_LIST_GOODS_DETAILS_FAILED + 1;
    //获取购物车页面信息失败事件
    public static final int GET_CART_INFO_FAILED = GET_CART_INFO_SUCESS + 1;

    //删除购物车一条商品成功事件
    public static final int GET_DELETE_CART_GOOD_SUCESS = GET_CART_INFO_FAILED + 1;
    //删除购物车一条商品失败事件
    public static final int GET_DELETE_CART_GOOD_FAILED = GET_DELETE_CART_GOOD_SUCESS + 1;

    //意见反馈成功事件
    public static final int POST_SUBMITADVICE_SUCESS = GET_DELETE_CART_GOOD_FAILED + 1;
    //意见反馈失败事件
    public static final int POST_SUBMITADVICE_FAILED = POST_SUBMITADVICE_SUCESS + 1;

    //通过分类id获取商品信息成功事件
    public static final int GET_GOODS_BY_TYPE_SUCESS = POST_SUBMITADVICE_FAILED + 1;
    //通过分类id获取商品信息失败事件
    public static final int GET_GOODS_BY_TYPE_FAILED = GET_GOODS_BY_TYPE_SUCESS + 1;

    //投诉商铺成功事件
    public static final int POST_COMPLAINORDER_SUCESS = GET_GOODS_BY_TYPE_FAILED + 1;
    //投诉商铺失败事件
    public static final int POST_COMPLAINORDER_FAILED = POST_COMPLAINORDER_SUCESS + 1;

    //通过分类首次进入显示 全部商品成功事件
    public static final int GET_GOODS_SUCESS = POST_COMPLAINORDER_FAILED + 1;
    //通过分类首次进入显示 全部商品失败事件
    public static final int GET_GOODS_FAILED = GET_GOODS_SUCESS + 1;

    //结算页数据初始化成功事件
    public static final int GET_ACKORDER_SUCESS = GET_GOODS_FAILED + 1;
    //结算页数据初始化失败事件
    public static final int GET_ACKORDER_FAILED = GET_ACKORDER_SUCESS + 1;

    //获取分类页购物车商品列表成功事件
    public static final int GET_CATEGORY_CART_LIST_SUCESS = GET_ACKORDER_FAILED + 1;
    //获取分类页购物车列表失败事件
    public static final int GET_CATEGORY_CART_LIST_FAILED = GET_CATEGORY_CART_LIST_SUCESS + 1;

    //版本检查更新成功事件
    public static final int GET_VERSION_SUCESS = GET_CATEGORY_CART_LIST_FAILED + 1;
    //版本检查更新失败事件
    public static final int GET_VERSION_FAILED = GET_VERSION_SUCESS + 1;

    //获取优惠券成功事件
    public static final int GET_FINDUSERCOUPON_SUCESS = GET_VERSION_FAILED + 1;
    //获取优惠券失败事件
    public static final int GET_FINDUSERCOUPON_FAILED = GET_FINDUSERCOUPON_SUCESS + 1;

    //获取优惠券成功事件
    public static final int GET_FINDWATERTICKET_SUCESS = GET_FINDUSERCOUPON_FAILED + 1;
    //获取优惠券失败事件
    public static final int GET_FINDWATERTICKET_FAILED = GET_FINDWATERTICKET_SUCESS + 1;

    //获取购买水漂成功事件
    public static final int GET_WATER_FIND_SUCESS = GET_FINDWATERTICKET_FAILED + 1;
    //获取购买水漂失败事件
    public static final int GET_WATER_FIND_FAILED = GET_WATER_FIND_SUCESS + 1;

    //生成购买水票订单成功事件
    public static final int POST_WATER_CONFIRED_SUCESS = GET_WATER_FIND_FAILED + 1;
    //生成购买水票订单失败事件
    public static final int POST_WATER_CONFIRED_FAILED = POST_WATER_CONFIRED_SUCESS + 1;

    //获取水票详情成功事件
    public static final int GET_WATER_DETAIL_SUCESS = POST_WATER_CONFIRED_FAILED + 1;
    //获取水票详情失败事件
    public static final int GET_WATER_DETAIL_FAILED = GET_WATER_DETAIL_SUCESS + 1;

    //购买水票成功事件
    public static final int GET_WATER_SUBMIT_SUCESS = GET_WATER_DETAIL_FAILED + 1;
    //购买水票失败事件
    public static final int GET_WATER_SUBMIT_FAILED = GET_WATER_SUBMIT_SUCESS + 1;

    //进入一键送水成功事件
    public static final int GET_WATER_USETICKETS_SUCESS = GET_WATER_SUBMIT_FAILED + 1;
    //进入一键送水失败事件
    public static final int GET_WATER_USETICKETS_FAILED = GET_WATER_USETICKETS_SUCESS + 1;

    //一键送水提交订单成功事件
    public static final int GET_WATER_WATERORDER_SUCESS = GET_WATER_USETICKETS_FAILED + 1;
    //一键送水提交订单失败事件
    public static final int GET_WATER_WATERORDER_FAILED = GET_WATER_WATERORDER_SUCESS + 1;

    //进入搜索界面热词成功事件
    public static final int GET_INDEX_SEARCHHOTS_SUCESS = GET_WATER_WATERORDER_FAILED + 1;
    //进入搜索界面热词失败事件
    public static final int GET_INDEX_SEARCHHOTS_FAILED = GET_INDEX_SEARCHHOTS_SUCESS + 1;

    //搜索商品成功事件
    public static final int GET_SEARCH_GOODS_SUCESS = GET_INDEX_SEARCHHOTS_FAILED + 1;
    //搜索商品失败事件
    public static final int GET_SEARCH_GOODS_FAILED = GET_SEARCH_GOODS_SUCESS + 1;

    //获取个人中心我的水票成功事件
    public static final int GET_PERSON_FINDTICKETS_SUCESS = GET_SEARCH_GOODS_FAILED + 1;
    //获取个人中心我的水票失败事件
    public static final int GET_PERSON_FINDTICKETS_FAILED = GET_PERSON_FINDTICKETS_SUCESS + 1;

    // 用水票购买水成功事件
    public static final int POST_WATER_ORDER_SUCESS = GET_PERSON_FINDTICKETS_FAILED + 1;
    //用水票购买水失败事件
    public static final int POST_WATER_ORDER_FAILED = POST_WATER_ORDER_SUCESS + 1;

    //分类页重新选择便利店成功事件
    public static final int GET_SELECT_BROWER_SHOP_CATEGORY_SUCESS = GET_PERSON_FINDTICKETS_FAILED + 1;
    //分类页新选择便利店失败事件
    public static final int GET_SELECT_BROWER_SHOP_CATEGORY_FAILED = GET_SELECT_BROWER_SHOP_CATEGORY_SUCESS + 1;

    //普通订单提交成功事件
    public static final int POST_ORDER_INSERTORDER_SUCESS = GET_SELECT_BROWER_SHOP_CATEGORY_FAILED + 1;
    //普通订单提交失败事件
    public static final int POST_ORDER_INSERTORDER_FAILED = POST_ORDER_INSERTORDER_SUCESS + 1;

    //触发优惠券成功事件
    public static final int GET_ORDER_FINDCOUPON_SUCESS = POST_ORDER_INSERTORDER_FAILED + 1;
    //触发优惠券失败事件
    public static final int GET_ORDER_FINDCOUPON_FAILED = GET_ORDER_FINDCOUPON_SUCESS + 1;

    //WX支付获取prepayid成功事件
    public static final int GET_PREPAY_ID_SUCESS = GET_ORDER_FINDCOUPON_FAILED + 1;
    //WX支付获取prepayid失败事件
    public static final int GET_PREPAY_ID_FAILED = GET_PREPAY_ID_SUCESS + 1;

    //WX支付查询订单状态成功事件
    public static final int GET_PREPAY_CHECKPAY_SUCESS = GET_PREPAY_ID_FAILED + 1;
    //WX支付查询订单状态失败事件
    public static final int GET_PREPAY_CHECKPAY_FAILED = GET_PREPAY_CHECKPAY_SUCESS + 1;

    //WX支付获取prepayid水票页面成功事件
    public static final int GET_PREPAY_ID_WATER_SUCESS = GET_PREPAY_CHECKPAY_FAILED + 1;
    //WX支付获取prepayid水票页面失败事件
    public static final int GET_PREPAY_ID_WATER_FAILED = GET_PREPAY_ID_WATER_SUCESS + 1;

    //提交系統日志成功事件
    public static final int POST_ERROR_LOG_SUCESS = GET_PREPAY_ID_WATER_FAILED + 1;
    //提交系統日志失败事件
    public static final int POST_ERROR_LOG_FAILED = POST_ERROR_LOG_SUCESS + 1;

    //WX支付详情页面获取prepayid成功事件
    public static final int GET_PREPAY_ID_DETAIL_SUCESS = POST_ERROR_LOG_FAILED + 1;
    //WX支付详情页面获取prepayid失败事件
    public static final int GET_PREPAY_ID_DETAIL_FAILED = GET_PREPAY_ID_DETAIL_SUCESS + 1;

    //订单详情页面取消订单成功事件
    public static final int POST_CANCEL_ORDER_DETAIL_SUCESS = GET_PREPAY_ID_DETAIL_FAILED + 1;
    //订单详情页面取消订单失败事件
    public static final int POST_CANCEL_ORDER_DETAIL_FAILED = POST_CANCEL_ORDER_DETAIL_SUCESS + 1;

    //WX支付获取prepayid成功事件
    public static final int GET_PREPAY_ID_DETAIL_TRUE_SUCESS = POST_CANCEL_ORDER_DETAIL_FAILED + 1;
    //WX支付获取prepayid失败事件
    public static final int GET_PREPAY_ID_DETAIL_TRUE_FAILED = GET_PREPAY_ID_DETAIL_TRUE_SUCESS + 1;

    //WX支付获取prepayid成功事件
    public static final int GET_PREPAY_ID_DETAIL_ADAPTER_SUCESS = GET_PREPAY_ID_DETAIL_TRUE_FAILED + 1;
    //WX支付获取prepayid失败事件
    public static final int GET_PREPAY_ID_DETAIL_ADAPTER_FAILED = GET_PREPAY_ID_DETAIL_ADAPTER_SUCESS + 1;

    //登出成功事件
    public static final int GET_LOGINOUT_SUCESS = GET_PREPAY_ID_DETAIL_ADAPTER_FAILED + 1;
    //登出失败事件
    public static final int GET_LOGINOUT_FAILED = GET_LOGINOUT_SUCESS + 1;

    //分类页数据获取成功事件
    public static final int GET_CATEGORY_INFO_SUCESS = GET_LOGINOUT_FAILED + 1;
    //分类页数据获取失败事件
    public static final int GET_CATEGORY_INFO_FAILED = GET_CATEGORY_INFO_SUCESS + 1;

    //购买水票返还的优惠券金额成功事件
    public static final int GET_COUPON_AMOUNT_SUCESS = GET_CATEGORY_INFO_FAILED + 1;
    //购买水票返还的优惠券金额失败事件
    public static final int GET_COUPON_AMOUNT_FAILED = GET_COUPON_AMOUNT_SUCESS + 1;

    //订单详情页面取消订单成功事件(只刷新页面,不跳转)
    public static final int POST_CANCEL_ORDER_DETAIL_FROM_BALANCE_SUCESS = GET_COUPON_AMOUNT_FAILED + 1;
    //订单详情页面取消订单失败事件（只刷新页面,不跳转）
    public static final int POST_CANCEL_ORDER_DETAIL_FROM_BALANCE_FAILED = POST_CANCEL_ORDER_DETAIL_FROM_BALANCE_SUCESS + 1;
}
