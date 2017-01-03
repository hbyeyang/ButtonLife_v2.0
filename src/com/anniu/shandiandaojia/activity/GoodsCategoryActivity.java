package com.anniu.shandiandaojia.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.adapter.CartListAdapter;
import com.anniu.shandiandaojia.adapter.TabAdapter;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.BottomStatusBar;
import com.anniu.shandiandaojia.db.jsondb.CartGoods;
import com.anniu.shandiandaojia.db.jsondb.Category;
import com.anniu.shandiandaojia.db.jsondb.GoodsListViewModel;
import com.anniu.shandiandaojia.db.jsondb.ShopInfo;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.GoodsLogic;
import com.anniu.shandiandaojia.logic.LocationLogic;
import com.anniu.shandiandaojia.logic.ShoppingCartLogic;
import com.anniu.shandiandaojia.utils.CommonUtil;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.anniu.shandiandaojia.utils.Utils;
import com.anniu.shandiandaojia.view.MyDialog;
import com.anniu.shandiandaojia.view.PagerSlidingTabStrip;
import com.anniu.shandiandaojia.view.pulltorefreshlayout.PullToRefreshLayout;
import com.anniu.shandiandaojia.view.pulltorefreshlayout.PullToRefreshLayout.OnRefreshListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zxl
 * @ClassName: GoodsCategoryActivity
 * @Description: 商品分类页面
 * @date 2015年6月3日 下午3:58:22
 */
public class GoodsCategoryActivity extends BaseActivity implements OnRefreshListener {
    protected String TAG = "GoodsCategoryActivity";
    public static String EXTRA_TYPE_CODE = "type_code";
    public static String EXTRA_SHOP_NAME = "shop_name";
    public static final int LOAD_TYPE_REFRESH = 1;
    public static final int LOAD_TYPE_LOADMORE = 2;
    private PagerSlidingTabStrip tabs;
    private ViewPager mViewPager;
    private TabAdapter mAdapter;
    private RelativeLayout rlShopTitle, errorView;
    private TextView cartNum, cartPrice, cartNotice, tvShopTitle;
    private ImageView ivArrow;
    private CartListAdapter cartListAdapter;
    private ArrayList<CartGoods> cartList = new ArrayList<CartGoods>();
    public List<Category> categoryList = new ArrayList<Category>();
    private PopupWindow carPopupWindow, popupWindow;

    private int pagenum = 1;
    private int pagesize = 21;
    private int typeCode = -1;// 商品分类id,默认-1
    private int temptypecode = 0;// 首页过来的typecode
    private String shopName;// 店铺名称

    public static LocationClient mLocationClient;
    private LocationMode tempMode = LocationMode.Hight_Accuracy;
    private String tempcoor = "gcj02";// gcj02、bd09ll、bd09

    private ListView goodsListView;
    private ShopInfo shop;
    private RelativeLayout loading;

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_goods_category);
        if (!CommonUtil.isNetworkConnected(context)) {
            showErrorLocal(tryAgainListener);
        } else {
            initView();
        }
    }

    private void initView() {
        Intent intent = getIntent();
        temptypecode = intent.getExtras().getInt(EXTRA_TYPE_CODE);
        shopName = intent.getExtras().getString(EXTRA_SHOP_NAME);

        tvShopTitle = (TextView) findViewById(R.id.title_bar_tv);
        rlShopTitle = (RelativeLayout) findViewById(R.id.title_bar_center);
        ivArrow = (ImageView) findViewById(R.id.iv_arrow);
        cartNum = (TextView) findViewById(R.id.tv_cart_num);
        cartPrice = (TextView) findViewById(R.id.cart_price);
        cartNotice = (TextView) findViewById(R.id.tv_notice);
        if (TextUtils.isEmpty(shopName)) {
            shopName = SPUtils.getString(this, GlobalInfo.KEY_SHOPNAME, "");
        }
        tvShopTitle.setText(shopName);

        findViewById(R.id.iv_cart).setOnClickListener(this);
        findViewById(R.id.rl_notice).setOnClickListener(this);
        findViewById(R.id.title_bar_left).setOnClickListener(this);
        findViewById(R.id.title_bar_right).setOnClickListener(this);
        rlShopTitle.setOnClickListener(this);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTextSize(16);
        tabs.setTextColor(getResources().getColor(R.color.category_tab_color_normal));
        tabs.setIndicatorColor(getResources().getColor(R.color.red));

        mViewPager = (ViewPager) findViewById(R.id.id_pager);
        mAdapter = new TabAdapter(getSupportFragmentManager(), this);
        /** 默认设置一条记录*/
        Category category = new Category();
        category.setName("全部");
        category.setId(-1);
        categoryList.add(category);
        mAdapter.setCategories(categoryList);
        mViewPager.setAdapter(mAdapter);
        tabs.setViewPager(mViewPager);

        mLocationClient = ((App) getApplication()).mLocationClient;
        errorView = (RelativeLayout) findViewById(R.id.rl_error);
        loading = (RelativeLayout) findViewById(R.id.rl_loading);
        loading.setVisibility(View.VISIBLE);

        sendFirstAction();
    }


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
                Intent i = baseIntent;
                if (baseIntent != null) {
                    startActivity(i);
                    finish();
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.title_bar_left://返回
                finish();
                break;
            case R.id.title_bar_right://打电话
                showAlertDialog();
                break;
            case R.id.title_bar_center://切换商铺
                sendSelectBrowerShopAction();
                break;
            case R.id.iv_cart://购物车图标
                if (Utils.loginState()) {//获取购物车中的商品列表
                    sendGetCartGoodsListAction();
                } else {
                    startActivity(new Intent(this, RegistActivity.class));
                    finish();
                }
                break;
            case R.id.rl_notice://去购物车
                if (Utils.loginState()) {//判断是否登录过
                    startActivity(new Intent(this, ShoppingCartActivity.class));
                } else {
                    startActivity(new Intent(this, RegistActivity.class));
                }
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        switch (eventId) {
            case Event.GET_CATEGORY_INFO_SUCESS://第一次进入分类页面
                loading.setVisibility(View.GONE);
                hideErrorLocal();
                GoodsListViewModel goodsModel = (GoodsListViewModel) bundle.getSerializable(GoodsLogic.EXTRA_GOODSTYPELIST);
                shop = goodsModel.getShop();
                tabCategory(goodsModel);
                cartNotice(goodsModel);
                mAdapter.onRefreshComplete(bundle);
                break;
            case Event.GET_CATEGORY_INFO_FAILED:
                loading.setVisibility(View.GONE);
                mAdapter.onRefreshComplete(bundle);
                MyToast.show(this, notice);
                break;
            case Event.GET_GOODS_BY_TYPE_SUCESS://获取商品信息成功事件
                mAdapter.onRefreshComplete(bundle);
                break;
            case Event.GET_GOODS_BY_TYPE_FAILED://获取商品信息失败事件
                mAdapter.onRefreshComplete(bundle);
                MyToast.show(this, notice);
                break;
            case Event.GET_CATEGORY_CART_LIST_SUCESS://获取分类页购物车商品列表成功事件
                showCartPopWindow(bundle);
                break;
            case Event.INSERT_OR_UPDATE_CART_GOOD_NUM_SUCESS://添加或删除/修改商品数量成功事件
                cartNoticeUpdate(bundle);
                notifyPop(bundle);
                break;
            case Event.GET_SELECT_BROWER_SHOP_CATEGORY_SUCESS://分类页重新选择便利店成功事件
                showPopWindow(bundle);
                break;
            case Event.GET_CATEGORY_CART_LIST_FAILED://获取分类页购物车列表失败事件
            case Event.INSERT_OR_UPDATE_CART_GOOD_NUM_FAILED://添加或删除/修改商品数量失败事件
            case Event.GET_SELECT_BROWER_SHOP_CATEGORY_FAILED://分类页新选择便利店失败事件
                MyToast.show(this, notice);
                break;
            default:
                break;
        }
    }

    private void cartNoticeUpdate(Bundle bundle) {
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
     * 重新选择便利店
     */
    private void showPopWindow(Bundle bundle) {
        ArrayList<ShopInfo> shopList = (ArrayList<ShopInfo>) bundle.getSerializable(LocationLogic.EXTRA_SHOPLIST);

        View view = LayoutInflater.from(this).inflate(R.layout.popupwindow_shop_list, null, false);
        LinearLayout group = (LinearLayout) view.findViewById(R.id.ll_pop_group);

        if (shopList != null && shopList.size() > 0) {
            RelativeLayout viewText;
            ShopInfo shop;
            for (int i = 0; i < shopList.size(); i++) {
                shop = shopList.get(i);
                viewText = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.item_shop, null);
                TextView textView = new TextView(this);
                textView.setText(shop.getName() + "(" + shop.getDistance() + "m)");
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(5, 5, 5, 5);
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTextSize(16);
                textView.setTag(shop);
                textView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        ShopInfo shopinfo = (ShopInfo) v.getTag();
                        int shopcode = shopinfo.getId();
                        int old = SPUtils.getInt(GoodsCategoryActivity.this, GlobalInfo.KEY_SHOPCODE, 0);
                        if (old != shopcode) {
                            tvShopTitle.setText(shopinfo.getName());
                            SPUtils.saveBoolean(GoodsCategoryActivity.this, GlobalInfo.KEY_NEEDLOAD, true);
                            SPUtils.saveInt(GoodsCategoryActivity.this, GlobalInfo.KEY_SHOPCODE, shopcode);
                            sendFirstAction();
                        }
                    }
                });
                group.addView(textView, i);
            }
        }

        TextView text = new TextView(this);
        text.setText("+ 添加便利店");
        text.setTextSize(16);
        text.setTextColor(getResources().getColor(R.color.black));
        text.setGravity(Gravity.CENTER);
        text.setPadding(5, 5, 5, 5);
        text.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                InitLocation();
                mLocationClient.start();
                Intent intent = new Intent(GoodsCategoryActivity.this, FamliyMartActivity.class);
                intent.putExtra(FamliyMartActivity.EXTRA_FROM, FamliyMartActivity.EXTRA_C);
                startActivity(intent);
                finish();
            }
        });
        group.addView(text, shopList.size());
        view.setFocusableInTouchMode(true);
        popupWindow = new PopupWindow(view, (int) (App.windowWidth * 0.6f), LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.style_ppw);
        popupWindow.setOutsideTouchable(true);

        ivArrow.setBackgroundDrawable(getResources().getDrawable(R.drawable.category_title_arrow_up));

        popupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                ivArrow.setBackgroundDrawable(getResources().getDrawable(R.drawable.category_title_arrow_down));
            }
        });

        // 计算x轴方向的偏移量，使得PopupWindow在Title的正下方显示，此处的单位是pixels
        float xoffInPixels = App.windowWidth * 0.5f - rlShopTitle.getWidth() * 0.5f;
        // 将pixels转为dip
        int xoffInDip = Utils.px2dip(this, xoffInPixels);
        popupWindow.showAsDropDown(rlShopTitle, -xoffInDip, 0);
        popupWindow.update();
    }

    private void InitLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);//设置定位模式
        option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//是否反地理编码
        mLocationClient.setLocOption(option);
    }


    /**
     * 点击加减判断是否减到0
     */
    private void notifyPop(Bundle bundle) {
        int operation = bundle.getInt(ShoppingCartLogic.EXTRA_CMD);
        int goodsId = bundle.getInt(ShoppingCartLogic.EXTRA_ID);
        int number = bundle.getInt(ShoppingCartLogic.EXTRA_NUM);

        if (operation == 2) {
            CartGoods goods = null;
            for (int i = 0; i < cartList.size(); i++) {
                goods = cartList.get(i);
                if (goods.getGoodsId() == goodsId) {
                    break;
                }
            }
            int num = goods.getGoodsCount();
            if (--num <= 0) {
                cartList.remove(goods);
                cartListAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(goodsListView);
                if (cartList.size() <= 0) {
                    carPopupWindow.dismiss();
                }
            } else {
                goods.setGoodsCount(num - 1);
            }
        } else if (operation == 3) {
            if (number == 0) {
                CartGoods goods = null;
                for (int i = 0; i < cartList.size(); i++) {
                    goods = cartList.get(i);
                    if (goods.getGoodsId() == goodsId) {
                        break;
                    }
                }
                cartList.remove(goods);
                cartListAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(goodsListView);
            }
        }
    }

    /**
     * 分类tab数据填充
     */
    private void tabCategory(GoodsListViewModel goodsModel) {
        List<Category> list = goodsModel.getGoodsTypeList();
        if (list != null && list.size() > 0) {
            categoryList.clear();
            categoryList.addAll(list);
            mAdapter.notifyDataSetChanged();
            tabs.notifyDataSetChanged();
            if (temptypecode != 0) {
                for (int i = 0; i < categoryList.size(); i++) {
                    int code = categoryList.get(i).getId();
                    if (code == temptypecode) {
                        mViewPager.setCurrentItem(i);
                        temptypecode = 0;
                        break;
                    }
                }
            }
        }
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
        goodsListView = (ListView) view.findViewById(R.id.listview);
        cartListAdapter = new CartListAdapter(this, cartList);
        goodsListView.setAdapter(cartListAdapter);
        setListViewHeightBasedOnChildren(goodsListView);

        carPopupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.7f;
        getWindow().setAttributes(params);

        carPopupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
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

    /**
     * 购物车提示
     */
    private void cartNotice(GoodsListViewModel goodsModel) {
        BottomStatusBar bsb = goodsModel.getPost();
        if (bsb != null) {
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
        } else {
            cartNum.setVisibility(View.GONE);
            cartPrice.setText("￥ 0.00");
            cartNotice.setText("差" + shop.getFreePostPrice() + "元免费配送");
        }

    }

    /**
     * 提示框
     */
    public void showAlertDialog() {
        MyDialog.Builder builder = new MyDialog.Builder(this);
        builder.setTitle(shopName);
        builder.setMessage("老板电话：" + shop.getTel() + "\n您可以直接拨打老板电话，电话订购所需商品！");
        builder.setConfirmButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + shop.getTel())));
            }
        });
        builder.setCancelButton("取消", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this, Event.GET_CATEGORY_INFO_SUCESS, Event.GET_CATEGORY_INFO_FAILED,
                Event.GET_GOODS_BY_TYPE_SUCESS, Event.GET_GOODS_BY_TYPE_FAILED,
                Event.GET_CATEGORY_CART_LIST_SUCESS, Event.GET_CATEGORY_CART_LIST_FAILED,
                Event.INSERT_OR_UPDATE_CART_GOOD_NUM_SUCESS, Event.INSERT_OR_UPDATE_CART_GOOD_NUM_FAILED,
                Event.GET_SELECT_BROWER_SHOP_CATEGORY_SUCESS, Event.GET_SELECT_BROWER_SHOP_CATEGORY_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

    /**
     * 获取购物车商品列表
     */
    private void sendGetCartGoodsListAction() {
        sendAction(new Intent(ShoppingCartLogic.ACTION_GETCATEGORYCARTLIST));
    }

    /**
     * 分类页重新选择便利店
     */
    private void sendSelectBrowerShopAction() {
        Intent intent = new Intent(LocationLogic.ACTION_GET_SELECT_BROWER_SHOP_CATEGORY);
        intent.putExtra(LocationLogic.EXTRA_LNG, SPUtils.getString(this, GlobalInfo.LNG, ""));
        intent.putExtra(LocationLogic.EXTRA_LAT, SPUtils.getString(this, GlobalInfo.LAT, ""));
        sendAction(intent);
    }

    //首次进入获取分类信息，商品信息，购物车信息
    private void sendFirstAction() {
        Intent intent = new Intent(GoodsLogic.ACTION_GET_CATEGORY_ACTIVITY_INFO);
        intent.putExtra(GoodsLogic.EXTRA_TYPECODE, typeCode);
        intent.putExtra(GoodsLogic.EXTRA_PAGESIZE, pagesize);
        intent.putExtra(GoodsLogic.EXTRA_PAGENUM, pagenum);
        sendAction(intent);
    }

    //获取分类页tab分类信息
    public void sendGetGoodsByType(int loadType) {
        if (loadType == LOAD_TYPE_REFRESH) {
            pagenum = 1;
        }

        Intent intent = new Intent(GoodsLogic.ACTION_GET_GOODS_BY_TYPE);
        intent.putExtra(GoodsLogic.EXTRA_TYPECODE, typeCode);
        intent.putExtra(GoodsLogic.EXTRA_PAGESIZE, pagesize);
        intent.putExtra(GoodsLogic.EXTRA_PAGENUM, pagenum);
        intent.putExtra(GoodsLogic.EXTRA_LOADTYPE, loadType);
        sendAction(intent);
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout, int typecode) {
        typeCode = typecode;
        sendGetGoodsByType(LOAD_TYPE_REFRESH);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout, int typecode) {
        typeCode = typecode;
        pagenum++;
        sendGetGoodsByType(LOAD_TYPE_LOADMORE);
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
}
