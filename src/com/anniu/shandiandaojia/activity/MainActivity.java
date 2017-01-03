package com.anniu.shandiandaojia.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.adapter.BargainGoodsAdapter;
import com.anniu.shandiandaojia.adapter.CartListAdapter;
import com.anniu.shandiandaojia.adapter.HomeCategoryGoodsAdapter;
import com.anniu.shandiandaojia.adapter.MyAdapter;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.Banner;
import com.anniu.shandiandaojia.db.jsondb.BottomStatusBar;
import com.anniu.shandiandaojia.db.jsondb.CartGoods;
import com.anniu.shandiandaojia.db.jsondb.Category;
import com.anniu.shandiandaojia.db.jsondb.Goods;
import com.anniu.shandiandaojia.db.jsondb.HomeViewModel;
import com.anniu.shandiandaojia.db.jsondb.PushMessage;
import com.anniu.shandiandaojia.db.jsondb.ShopInfo;
import com.anniu.shandiandaojia.db.jsondb.ShopStatus;
import com.anniu.shandiandaojia.db.jsondb.WaterInfo;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.GoodsLogic;
import com.anniu.shandiandaojia.logic.LocationLogic;
import com.anniu.shandiandaojia.logic.ShoppingCartLogic;
import com.anniu.shandiandaojia.logic.TicketLogic;
import com.anniu.shandiandaojia.logic.UpdateLogic;
import com.anniu.shandiandaojia.receiver.MyReceiver;
import com.anniu.shandiandaojia.utils.CommonUtil;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.anniu.shandiandaojia.utils.Utils;
import com.anniu.shandiandaojia.view.MyGridView;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    private TextView tvShopTitle, cartNum, cartPrice, cartNotice, bargainTitle;
    private View line1, line2, line3;
    private ShopInfo shop;

    private ImageView[] mImageViews;//装ImageView数组
    private ViewPager viewPager;
    private ImageView[] tips;//装点点的ImageView数组
    private ViewGroup group;
    private MyGridView gridView;
    private Runnable viewpagerRunnable;
    private static Handler handler;
    private static final int TIME = 5000;
    private boolean isFrist = false;

    private BargainGoodsAdapter bargainAdapter;
    private ArrayList<Goods> bragainList = new ArrayList<Goods>();
    private LinearLayout category;
    private RelativeLayout rlShopTitle, loadingView, errorView;

    private PopupWindow popupWindow, carPopupWindow;
    private CartListAdapter cartListAdapter;
    private ArrayList<CartGoods> cartList = new ArrayList<CartGoods>();

    public static LocationClient mLocationClient;
    private LocationMode tempMode = LocationMode.Hight_Accuracy;
    private String tempcoor = "gcj02";//gcj02、bd09ll、bd09

    private boolean firstFlag = false;
    private String shopName = null;
    private ImageView shopClose, ivArrow;
    private BottomStatusBar bsb = null;

    public static final String MESSAGE_RECEIVED_ACTION = "com.anniu.shandiandaojia.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    private String hrefType;
    private String hrefTarget;
    public static boolean isForeground = false;
    private ListView popupListView;
    private WaterInfo ticket;
    SwipeRefreshLayout refresh;
    ScrollView scrollview;

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_main);
        if (CommonUtil.isNetworkConnected(context)) {
            initView();
        } else {
            showErrorLocal(tryAgainListener);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initView() {
        sendUpdateAction();
        //获取商铺信息
        sendGetShopInfoAction();
        //实例化imageloader对象和参数
        initImageLoader();
        handler = new Handler();

        scrollview = (ScrollView) findViewById(R.id.scroll_view);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setOnRefreshListener(this);
        refresh.setColorScheme(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        loadingView = (RelativeLayout) findViewById(R.id.rl_loading);
        loadingView.setVisibility(View.VISIBLE);
        errorView = (RelativeLayout) findViewById(R.id.rl_error);

        findViewById(R.id.tv_search).setOnClickListener(this);
        findViewById(R.id.tv_personal).setOnClickListener(this);
        findViewById(R.id.rl_title_text).setOnClickListener(this);
        rlShopTitle = (RelativeLayout) findViewById(R.id.rl_title_text);
        tvShopTitle = (TextView) findViewById(R.id.tv_shop_title);
        shopClose = (ImageView) findViewById(R.id.iv_shop_close);
        ivArrow = (ImageView) findViewById(R.id.iv_arrow);

        findViewById(R.id.tv_category).setOnClickListener(this);
        findViewById(R.id.tv_carrying_water).setOnClickListener(this);
        findViewById(R.id.tv_full).setOnClickListener(this);

        gridView = (MyGridView) findViewById(R.id.grid_view);
        category = (LinearLayout) findViewById(R.id.column_category);

        findViewById(R.id.iv_cart).setOnClickListener(this);
        cartNum = (TextView) findViewById(R.id.tv_cart_num);
        cartPrice = (TextView) findViewById(R.id.cart_price);
        cartNotice = (TextView) findViewById(R.id.tv_notice);
        findViewById(R.id.rl_notice).setOnClickListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        refresh.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        refresh.setEnabled(true);
                        break;
                }
                return false;
            }
        });
        group = (ViewGroup) findViewById(R.id.viewGroup);

        bargainTitle = (TextView) findViewById(R.id.tv_bargain_title);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);

        mLocationClient = ((App) getApplication()).mLocationClient;

        Intent i = getIntent();
        if (i != null) {
            hrefType = i.getStringExtra(MyReceiver.MSG_HREF_TYPE);
            hrefTarget = i.getStringExtra(MyReceiver.MSG_HREF_TARGET);
            if (!TextUtils.isEmpty(hrefType)) {
                startActivity(getHrefIntent());
            }
        }
    }

    private Intent getHrefIntent() {
        Intent intent = new Intent();
        if (!TextUtils.isEmpty(hrefType)) {
            int type = Integer.parseInt(hrefType);
            if (type == PushMessage.HREF_TYPE_RECEIVE) {
                intent.setClass(this, MainActivity.class);
            }
        }
        return intent;
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean needLoad = SPUtils.getBoolean(this, GlobalInfo.KEY_NEEDLOAD, false);
        if (needLoad) {//特定情况才刷新，不是每次回到首页就刷新
            sendGetShopInfoAction();
            SPUtils.saveBoolean(this, GlobalInfo.KEY_NEEDLOAD, true);
        }
        ActivityMgr.finishAllActivity(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SPUtils.saveBoolean(this, GlobalInfo.KEY_NEEDLOAD, false);
    }

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_personal://个人中心
                if (Utils.loginState()) {//判断是否登录过
                    startActivity(new Intent(this, UserCenterActivity.class));
                } else {
                    startActivity(new Intent(this, RegistActivity.class));
                }
                break;
            case R.id.tv_search://搜索
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.rl_title_text://点击title
                sendSelectBrowerShopAction();
                break;
            case R.id.tv_category:// 分类
                intent = new Intent();
                intent.setClass(this, GoodsCategoryActivity.class);
                intent.putExtra(GoodsCategoryActivity.EXTRA_TYPE_CODE, 0);
                intent.putExtra(GoodsCategoryActivity.EXTRA_SHOP_NAME, shopName);
                startActivity(intent);
                break;
            case R.id.tv_carrying_water://桶装水
                if (firstFlag) {
                    intent = new Intent();
                    intent.putExtra(TicketLogic.EXTRA_TICKETCODE, ticket.getId());
                    intent.setClass(this, AKeyWaterActivity.class);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(this, BuyWaterTicket.class));
                }
                break;
            case R.id.tv_full://整箱购
                intent = new Intent();
                intent.setClass(this, GoodsCategoryActivity.class);
                intent.putExtra(GoodsCategoryActivity.EXTRA_TYPE_CODE, GlobalInfo.FCLCODE);
                intent.putExtra(GoodsCategoryActivity.EXTRA_SHOP_NAME, shopName);
                startActivity(intent);
                break;
            case R.id.iv_cart://购物车图标
                if (Utils.loginState()) {//判断是否登录过
                    sendGetCartGoodsListAction();
                } else {
                    startActivity(new Intent(this, RegistActivity.class));
                }
                break;
            case R.id.rl_notice://去购物车
                if (Utils.loginState()) {// 判断是否登录过
                    startActivity(new Intent(this, ShoppingCartActivity.class));
                } else {
                    startActivity(new Intent(this, RegistActivity.class));
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        refresh.setRefreshing(false);
        loadingView.setVisibility(View.GONE);
        switch (eventId) {
            case Event.GET_SHOP_INFO_SUCCESS://获取商铺首页信息成功事件
                hideErrorLocal();
                HomeViewModel homeView = (HomeViewModel) bundle.getSerializable(LocationLogic.EXTRA_SHOP);

                ticket = homeView.getUserTicket();
                if (ticket == null) {
                    firstFlag = false;
                } else {
                    firstFlag = true;
                }
                initShopInfo(homeView);
                setVidwPager(homeView);
                bargainGoods(homeView);
                categoryGoods(homeView);
                cartNoticeHomeView(homeView);

                scrollview.post(new Runnable() {
                    public void run() {
                        scrollview.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                break;
            case Event.GET_SHOP_INFO_FAILED://获取商铺首页信息失败事件
                MyToast.show(this, notice);
                break;
            case Event.GET_SELECT_BROWER_SHOP_SUCESS://首页重新选择便利店成功事件
                showPopWindow(bundle);
                break;
            case Event.INSERT_OR_UPDATE_CART_GOOD_NUM_SUCESS://添加或删除/修改商品数量成功事件
                cartNotice(bundle);
                notifyPop(bundle);
                break;
            case Event.GET_CART_LIST_SUCESS://获取购物车商品列表成功事件
                showCartPopWindow(bundle);
                break;
            case Event.GET_DELETE_CART_GOOD_SUCESS://删除购物车一条商品成功事件
                cartNotice(bundle);
                notifyPopDete(bundle);
                break;
            case Event.GET_SELECT_BROWER_SHOP_FAILED://首页重新选择便利店失败事件
            case Event.INSERT_OR_UPDATE_CART_GOOD_NUM_FAILED://添加或删除/修改商品数量失败事件
            case Event.GET_CART_LIST_FAILED://获取购物车商品列表失败事件
            case Event.GET_DELETE_CART_GOOD_FAILED://删除购物车一条商品失败事件
                MyToast.show(this, notice);
                break;
            default:
                break;
        }
    }

    private void cartNoticeHomeView(HomeViewModel homeViewModel) {
        bsb = homeViewModel.getPost();
        if (bsb == null) {
            cartNum.setVisibility(View.GONE);
            cartPrice.setText("￥ 0.00");
            cartNotice.setText("差" + shop.getFreePostPrice() + "元免费配送");
            return;
        }

        if (bsb.getGoodsCount() > 0) {
            cartNum.setVisibility(View.VISIBLE);
            String number;
            if (bsb.getGoodsCount() > 99) {
                number = "99+";
            } else {
                number = bsb.getGoodsCount() + "";
            }
            cartNum.setText(number);
        } else {
            cartNum.setVisibility(View.GONE);
        }

        double amount = bsb.getAmount();
        BigDecimal bdmoney = new BigDecimal(amount);
        bdmoney = bdmoney.setScale(2, BigDecimal.ROUND_DOWN);
        cartPrice.setText(amount > 0 ? "￥" + bdmoney : "￥0.00");
        String text;
        if (amount == 0) {
            double free_send_price = bsb.getFreePostPrice();
            BigDecimal bdsend = new BigDecimal(free_send_price);
            bdsend = bdsend.setScale(2, BigDecimal.ROUND_DOWN);
            text = "差" + bdsend + "元免费配送";
        } else if (0 < amount && amount < bsb.getFreePostPrice()) {
            double sub = Utils.sub(bsb.getFreePostPrice(), amount);
            text = "差" + sub + "元免费配送";
        } else {
            text = "选好了";
        }
        cartNotice.setText(text);
    }

    /**
     * 点击加减判断是否减到0
     */
    private void notifyPop(Bundle bundle) {
        String cmd = bundle.getString(ShoppingCartLogic.EXTRA_CMD);
        int goodsId = bundle.getInt(ShoppingCartLogic.EXTRA_ID);
        int count = bundle.getInt(ShoppingCartLogic.EXTRA_COUNT);

        if (cmd.equals("POP")) {
            CartGoods goods = null;
            for (int i = 0; i < cartList.size(); i++) {
                goods = cartList.get(i);
                if (goods.getShopGoodsId() == goodsId) {
                    break;
                }
            }
            if (goods == null)
                return;
            int num = goods.getGoodsCount();
            if (--num <= 0) {
                cartList.remove(goods);
                if (cartList.size() <= 0) {
                    carPopupWindow.dismiss();
                }
                cartListAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(popupListView);
            } else {
                goods.setGoodsCount(num - 1);
            }
        } else if (cmd.equals("SET")) {
            if (count == 0) {
                CartGoods goods = null;
                for (int i = 0; i < cartList.size(); i++) {
                    goods = cartList.get(i);
                    if (goods.getShopGoodsId() == goodsId) {
                        break;
                    }
                }
                cartList.remove(goods);
                cartListAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(popupListView);
            }
        }
    }

    /**
     * 刪除
     */
    private void notifyPopDete(Bundle bundle) {
        int goodsId = bundle.getInt(ShoppingCartLogic.EXTRA_ID);
        CartGoods goods = null;
        for (int i = 0; i < cartList.size(); i++) {
            goods = cartList.get(i);
            if (goods.getGoodsId() == goodsId) {
                break;
            }
        }
        cartList.remove(goods);
        cartListAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(popupListView);
    }

    /**
     * 显示购物车商品view
     */
    private void showCartPopWindow(Bundle bundle) {
        ArrayList<CartGoods> list = (ArrayList<CartGoods>) bundle.getSerializable(ShoppingCartLogic.EXTRA_CARTGOODSLIST);
        if (list == null) {
            MyToast.show(this, "购物车没有商品！");
            return;
        }
        cartList.clear();
        cartList.addAll(list);

        View view = getLayoutInflater().inflate(R.layout.popupwindow_goods_list, null, false);
        popupListView = (ListView) view.findViewById(R.id.listview);
        cartListAdapter = new CartListAdapter(this, cartList);
        popupListView.setAdapter(cartListAdapter);
        setListViewHeightBasedOnChildren(popupListView);

        carPopupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.7f;
        getWindow().setAttributes(params);

        carPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        carPopupWindow.setAnimationStyle(R.style.style_ppwcart);
        carPopupWindow.setOutsideTouchable(true);
        carPopupWindow.setFocusable(true);
        carPopupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha = 1f;
                getWindow().setAttributes(params);
            }
        });

        carPopupWindow.update();
        carPopupWindow.showAtLocation(findViewById(R.id.layout_cart), Gravity.BOTTOM, 0, Utils.dip2px(this, 50));
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            totalHeight += listItem.getMeasuredHeight();
        }

        int h = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        if (h > App.windowHeight - Utils.dip2px(context, 180)) {
            h = App.windowHeight - Utils.dip2px(context, 180);
        } else {
            h = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = h;
        ((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        listView.setLayoutParams(params);
    }

    /**
     * 重新选择便利店
     */
    private void showPopWindow(Bundle bundle) {
        ArrayList<ShopInfo> shopList = (ArrayList<ShopInfo>) bundle.getSerializable(LocationLogic.EXTRA_SHOPLIST);

        View view = getLayoutInflater().inflate(R.layout.popupwindow_shop_list, null, false);
        LinearLayout group = (LinearLayout) view.findViewById(R.id.ll_pop_group);

        if (shopList != null && shopList.size() > 0) {
            RelativeLayout viewText;
            ShopInfo shop;
            for (int i = 0; i < shopList.size(); i++) {
                shop = shopList.get(i);
                viewText = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.item_shop, null);
                TextView textView = (TextView) viewText.findViewById(R.id.shop);
                textView.setText(shop.getName() + "(" + shop.getDistance() + "m)");
                textView.setTag(shop);
                textView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        ShopInfo shop = (ShopInfo) v.getTag();
                        int old = SPUtils.getInt(MainActivity.this, GlobalInfo.KEY_SHOPCODE, 0);
                        if (old != shop.getId()) {
                            SPUtils.saveBoolean(MainActivity.this, GlobalInfo.KEY_NEEDLOAD, true);
                            SPUtils.saveInt(MainActivity.this, GlobalInfo.KEY_SHOPCODE, shop.getId());
                            sendGetShopInfoAction();
                        }
                    }
                });
                group.addView(viewText, i);
            }
        }

        TextView text = new TextView(this);
        text.setText("+ 添加便利店");
        text.setTextSize(16);
        text.setPadding(0, 10, 0, 0);
        text.setGravity(Gravity.CENTER);
        text.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                InitLocation();
                mLocationClient.start();

                Intent intent = new Intent(MainActivity.this, FamliyMartActivity.class);
                intent.putExtra(FamliyMartActivity.EXTRA_FROM, FamliyMartActivity.EXTRA_MAIN);
                startActivity(intent);
            }
        });
        if (shopList != null) {
            group.addView(text, shopList.size());
        }
        view.setFocusableInTouchMode(true);
        popupWindow = new PopupWindow(view, (int) (App.windowWidth * 0.6f), LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.style_ppw);
        popupWindow.setOutsideTouchable(true);

        ivArrow.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_title_arrow_up));

        popupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                ivArrow.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_title_arrow_down));
            }
        });

        int xoffInPixels = App.windowWidth / 2 - rlShopTitle.getWidth() / 2;
        int xoffInDip = Utils.px2dip(this, xoffInPixels);
        popupWindow.showAsDropDown(rlShopTitle, -xoffInDip, 0);
        popupWindow.update();
    }

    /**
     * 购物车提示
     */
    private void cartNotice(Bundle bundle) {
        BottomStatusBar bsb = (BottomStatusBar) bundle.getSerializable(ShoppingCartLogic.EXTRA_POST);
        if (bsb == null) {
            cartNum.setVisibility(View.GONE);
            cartPrice.setText("￥ 0.00");
            cartNotice.setText("差" + shop.getFreePostPrice() + "元免费配送");
            return;
        }

        if (bsb.getGoodsCount() > 0) {
            cartNum.setVisibility(View.VISIBLE);
        } else {
            cartNum.setVisibility(View.GONE);
        }

        String number;
        if (bsb.getGoodsCount() > 99) {
            number = "99+";
        } else {
            number = bsb.getGoodsCount() + "";
        }
        cartNum.setText(number);
        double amount = bsb.getAmount();
        BigDecimal bdmoney = new BigDecimal(amount);
        bdmoney = bdmoney.setScale(2, BigDecimal.ROUND_DOWN);
        cartPrice.setText(amount > 0 ? "￥" + bdmoney : "￥0.00");
        String text;
        if (amount == 0) {
            double free_send_price = bsb.getFreePostPrice();
            BigDecimal bdsend = new BigDecimal(free_send_price);
            bdsend = bdsend.setScale(2, BigDecimal.ROUND_DOWN);
            text = "差" + bdsend + "元免费配送";
        } else if (0 < amount && amount < bsb.getFreePostPrice()) {
            double sub = Utils.sub(bsb.getFreePostPrice(), amount);
            text = "差" + sub + "元免费配送";
        } else {
            text = "选好了";
        }
        cartNotice.setText(text);
    }

    /**
     * 分类商品
     */
    private void categoryGoods(HomeViewModel homeViewModel) {
        ArrayList<Category> typegoods = (ArrayList<Category>) homeViewModel.getGoodsTypeList();
        if (typegoods == null) {
            return;
        }
        if (category.getChildCount() > 0) {
            category.removeAllViews();
        }
        Category categoryGoods;
        for (int i = 0; i < typegoods.size(); i++) {
            categoryGoods = typegoods.get(i);
            final ArrayList<Goods> goodsList = categoryGoods.getGoodsList();
            View goodsView = LayoutInflater.from(this).inflate(R.layout.home_category_goods, null);
            category.addView(goodsView, category.getChildCount());

            MyGridView itemGoodsView = (MyGridView) goodsView.findViewById(R.id.gridview_goods);

            TextView titleView = (TextView) goodsView.findViewById(R.id.tv_category_name);
            TextView moreView = (TextView) goodsView.findViewById(R.id.tv_more);

            titleView.setText(categoryGoods.getName());
            moreView.setTag(categoryGoods.getId());

            moreView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (Utils.isFastClick()) {
                        return;
                    }
                    int goodsTypeCode = (Integer) v.getTag();
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, GoodsCategoryActivity.class);
                    intent.putExtra(GoodsCategoryActivity.EXTRA_TYPE_CODE, goodsTypeCode);
                    intent.putExtra(GoodsCategoryActivity.EXTRA_SHOP_NAME, shopName);
                    startActivity(intent);
                }
            });

            itemGoodsView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (Utils.isFastClick()) {
                        return;
                    }
                    Goods goods = goodsList.get(position);
                    Intent intent = new Intent(ActivityMgr.mContext, GoodsDetailActivity.class);
                    intent.putExtra(GoodsLogic.EXTRA_ID, goods.getId());
                    startActivity(intent);
                }
            });

            HomeCategoryGoodsAdapter adapter = new HomeCategoryGoodsAdapter(this, goodsList);
            itemGoodsView.setAdapter(adapter);
            itemGoodsView.setOnScrollListener(new PauseOnScrollListener(adapter.mImageLoader, false, true));
        }
    }

    /**
     * 特价商品
     */
    private void bargainGoods(HomeViewModel homeViewModel) {
        ArrayList<Goods> discountGoods = (ArrayList<Goods>) homeViewModel.getDiscountGoodsList();
        if (discountGoods != null && discountGoods.size() > 0) {
            bargainTitle.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.VISIBLE);
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
            line3.setVisibility(View.VISIBLE);

            bragainList.clear();
            bragainList = discountGoods;
            if (bargainAdapter == null) {
                bargainAdapter = new BargainGoodsAdapter(this, bragainList);
                gridView.setAdapter(bargainAdapter);
            } else {
                bargainAdapter.addData(bragainList);
            }

            gridView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (Utils.isFastClick()) return;
                    Goods goods = bragainList.get(position);
                    Intent intent = new Intent(ActivityMgr.mContext, GoodsDetailActivity.class);
                    intent.putExtra(GoodsLogic.EXTRA_ID, goods.getId());
                    intent.putExtra(GoodsLogic.EXTRA_NUM, 0);
                    intent.putExtra(GoodsLogic.EXTRA_LIMITED, goods.getLimitCount());
                    startActivity(intent);
                }
            });
            gridView.setOnScrollListener(new PauseOnScrollListener(bargainAdapter.mImageLoader, false, true));
        } else {
            bargainTitle.setVisibility(View.GONE);
            gridView.setVisibility(View.GONE);
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
            line3.setVisibility(View.GONE);
        }
    }

    /**
     * viewpager
     */
    private void setVidwPager(HomeViewModel homeViewModel) {
        ArrayList<Banner> banner = (ArrayList<Banner>) homeViewModel.getBannerList();
        if (banner == null) {
            return;
        }

        if (tips != null) tips = null;
        if (mImageViews != null) mImageViews = null;
        if (group != null) group.removeAllViews();

        // 将点点加入到ViewGroup中
        tips = new ImageView[banner.size()];
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(this);
            android.widget.LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(10, 10);
            layoutParams.setMargins(0, 0, 10, 0);
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
            group.addView(imageView, layoutParams);
        }

        // 将图片装载到数组中
        mImageViews = new ImageView[banner.size()];
        Banner b;
        ImageView imageView;
        for (int i = 0; i < mImageViews.length; i++) {
            b = banner.get(i);
            imageView = new ImageView(this);
            mImageViews[i] = imageView;
            imageView.setScaleType(ScaleType.FIT_XY);
            mImageLoader.displayImage(b.getPictureUrl(), imageView, options);
            imageView.setTag(b);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Banner banner = (Banner) v.getTag();
                    String href = banner.getHref();
                    String title = banner.getTitle();
                    Intent intent = new Intent();
                    if (href.contains("@GT:")) {// 链接至商品分类页，商品分类ID为2
                        String[] strings = href.split(":");
                        int num = Integer.parseInt(strings[1]);
                        intent.setClass(MainActivity.this, GoodsCategoryActivity.class);
                        intent.putExtra(GoodsCategoryActivity.EXTRA_TYPE_CODE, num);
                    } else if (href.contains("@G:")) {// 链接至商品详情页，商品ID为1668
                        String[] strings = href.split(":");
                        int num = Integer.parseInt(strings[1]);
                        intent.setClass(MainActivity.this, GoodsDetailActivity.class);
                        intent.putExtra(GoodsLogic.EXTRA_ID, num);
                    } else { // 跳转至普通页面
                        intent.setClass(MainActivity.this, EventsActivity.class);
                        intent.putExtra(EventsActivity.EXTRA_TAYENAME, title);
                        intent.putExtra(EventsActivity.EXTRA_TAYEURL, href);
                    }
                    startActivity(intent);
                }
            });
        }

        //设置Adapter
        viewPager.setAdapter(new MyAdapter(mImageViews));

        if (!isFrist) {
            isFrist = true;
            initRunnable();
            //设置监听，主要是设置点点的背景
            initListener();
        }
    }

    /**
     * 初始化商铺信息
     */
    private void initShopInfo(HomeViewModel homeViewModel) {
        shop = homeViewModel.getShop();
        ShopStatus status = shop.getStatus();
        if (status.equals(ShopStatus.OPENED)) {
            shopClose.setVisibility(View.GONE);
        } else {
            shopClose.setVisibility(View.VISIBLE);
        }
        tvShopTitle.setText(shop.getName());
        shopName = shop.getName();
        SPUtils.saveString(this, GlobalInfo.KEY_SHOPNAME, shopName);
    }

    /**
     * 获取购物车商品列表
     */
    private void sendGetCartGoodsListAction() {
        sendAction(new Intent(ShoppingCartLogic.ACTION_GETCARTLIST));
    }

    /**
     * 首页重新选择便利店
     */
    private void sendSelectBrowerShopAction() {
        Intent intent = new Intent(LocationLogic.ACTION_GET_SELECT_BROWER_SHOP);
        intent.putExtra(LocationLogic.EXTRA_LNG, SPUtils.getString(this, GlobalInfo.LNG, ""));
        intent.putExtra(LocationLogic.EXTRA_LAT, SPUtils.getString(this, GlobalInfo.LAT, ""));
        sendAction(intent);
    }

    /**
     * 获取商铺信息
     */
    private void sendGetShopInfoAction() {
        Intent intent = new Intent(LocationLogic.ACTION_GET_LOCATION_LIST2SHOP);
        intent.putExtra(LocationLogic.EXTRA_SHOPCODE, SPUtils.getInt(MainActivity.this, GlobalInfo.KEY_SHOPCODE, 0));
        sendAction(intent);
    }

    /**
     * 自动升级
     */
    private void sendUpdateAction() {
        Intent intent = new Intent(UpdateLogic.ACTION_GET_UPDATEINFO);
        intent.putExtra(UpdateLogic.EXTRA_UPDATE_MODE, UpdateLogic.UPDATE_STRATEGY_AUTO);
        sendAction(intent);
    }

    private void InitLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);//设置定位模式
        option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//是否反地理编码
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this, Event.GET_SHOP_INFO_SUCCESS, Event.GET_SHOP_INFO_FAILED,
                Event.GET_SELECT_BROWER_SHOP_SUCESS, Event.GET_SELECT_BROWER_SHOP_FAILED,
                Event.INSERT_OR_UPDATE_CART_GOOD_NUM_SUCESS, Event.INSERT_OR_UPDATE_CART_GOOD_NUM_FAILED,
                Event.GET_CART_LIST_SUCESS, Event.GET_CART_LIST_FAILED,
                Event.GET_DELETE_CART_GOOD_SUCESS, Event.GET_DELETE_CART_GOOD_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            moveTaskToBack(true);
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    //viewPager数据显示开始----------------------------------------------------------------------------------------------
    private void initImageLoader() {
        mImageLoader = App.initImageLoader();
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
                .showStubImage(R.drawable.no_banner)// 加载过程中显示的图像，可以不设置
                .showImageForEmptyUri(R.drawable.no_banner) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.no_banner) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    protected void initRunnable() {
        viewpagerRunnable = new Runnable() {

            @Override
            public void run() {
                int nowIndex = viewPager.getCurrentItem();
                int count = viewPager.getAdapter().getCount();
                if (nowIndex + 1 >= count) {
                    viewPager.setCurrentItem(0);
                } else {
                    viewPager.setCurrentItem(nowIndex + 1);
                }
                handler.postDelayed(viewpagerRunnable, TIME);
            }
        };
        handler.postDelayed(viewpagerRunnable, TIME);
    }

    private void initListener() {
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            boolean isScrolled = false;

            @Override
            public void onPageScrollStateChanged(int status) {
                switch (status) {
                    case 1:
                        isScrolled = false;
                        break;
                    case 2:
                        isScrolled = true;
                        break;
                    case 0:
                        if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isScrolled) {
                            viewPager.setCurrentItem(0);
                        } else if (viewPager.getCurrentItem() == 0 && !isScrolled) {
                            viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int index) {
                setImageBackground(index % mImageViews.length);
            }
        });
    }

    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }
    //viewPager数据显示结束----------------------------------------------------------------------------------------------

    private void showErrorLocal(OnClickListener tryAgainListener) {
        errorView = (RelativeLayout) findViewById(R.id.rl_error);
        errorView.setOnClickListener(tryAgainListener);
        errorView.setVisibility(View.VISIBLE);
    }

    private void hideErrorLocal() {
        errorView.setVisibility(View.GONE);
    }

    private OnClickListener tryAgainListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (CommonUtil.isNetworkConnected(context)) {
                initView();
            }
        }
    };

    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
    }

    private static final int REFRESH_COMPLETE = 0X110;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    sendGetShopInfoAction();
                    break;
                default:
                    break;
            }
        }
    };
}
