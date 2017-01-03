package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.adapter.CartListAdapter;
import com.anniu.shandiandaojia.adapter.GoodsLoveAdapter;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.BottomStatusBar;
import com.anniu.shandiandaojia.db.jsondb.CartGoods;
import com.anniu.shandiandaojia.db.jsondb.Goods;
import com.anniu.shandiandaojia.db.jsondb.GoodsViewModel;
import com.anniu.shandiandaojia.db.jsondb.ShopInfo;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.GoodsLogic;
import com.anniu.shandiandaojia.logic.ShoppingCartLogic;
import com.anniu.shandiandaojia.utils.CommonUtil;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.Utils;
import com.anniu.shandiandaojia.view.AddAndSubView;
import com.anniu.shandiandaojia.view.KeyboardLayout;
import com.anniu.shandiandaojia.view.KeyboardLayout.onKybdsChangeListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author zxl
 * @ClassName: GoodsDetailActivity
 * @Description: 商品详情页面
 * @date 2015年6月3日 下午5:35:05
 */
public class GoodsDetailActivity extends BaseActivity {
    private TextView titleBarTv, goodsName, promotePrice, goodsPrice, cartNum, cartPrice, cartNotice;
    private ImageView leftTitle, goodsImg;
    private AddAndSubView addSub;
    private Goods goods;
    private GridView gridview;
    private ArrayList<Goods> goodsLove = new ArrayList<Goods>();
    private GoodsLoveAdapter goodsLoveAdapter;

    private PopupWindow carPopupWindow;
    private CartListAdapter cartListAdapter;
    private ArrayList<CartGoods> cartList = new ArrayList<CartGoods>();

    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    private RelativeLayout loadingView, errorView;
    private int goodsId;
    private int goods_num;
    private ShopInfo shop;
    private ListView goodsList;

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_goods_details);
        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText(R.string.goods_details);
        leftTitle = (ImageView) findViewById(R.id.iv_logo);
        leftTitle.setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        findViewById(R.id.title_bar_left).setOnClickListener(this);

        if (CommonUtil.isNetworkConnected(context)) {
            initView();
        } else {
            showErrorLocal(tryAgainListener);
        }
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

    private void initView() {
        goodsId = getIntent().getExtras().getInt(GoodsLogic.EXTRA_ID);
        sendGoodsDetailsAction();

        KeyboardLayout viewKL = (KeyboardLayout) findViewById(R.id.kl);
        viewKL.setOnkbdStateListener(new onKybdsChangeListener() {

            @Override
            public void onKeyBoardStateChange(int state) {
                switch (state) {
                    case KeyboardLayout.KEYBOARD_STATE_HIDE:
                        goods_num = addSub.getNum();
                        sendAddGood2CartAction3();
                        break;
                    default:
                        break;
                }
            }
        });

        addSub = (AddAndSubView) findViewById(R.id.addSub);
        addSub.setGoodsId(goodsId);
        addSub.setButtonLayoutParm(35, 35);
        addSub.setViewsLayoutParm(120, 35);
        addSub.setGravity(Gravity.RIGHT);

        goodsImg = (ImageView) findViewById(R.id.iv_goods_img);
        goodsName = (TextView) findViewById(R.id.tv_goods_name);
        promotePrice = (TextView) findViewById(R.id.tv_promote_price);
        goodsPrice = (TextView) findViewById(R.id.tv_price);
        gridview = (GridView) findViewById(R.id.gridview);

        findViewById(R.id.iv_cart).setOnClickListener(this);
        cartNum = (TextView) findViewById(R.id.tv_cart_num);
        cartPrice = (TextView) findViewById(R.id.cart_price);
        cartNotice = (TextView) findViewById(R.id.tv_notice);
        findViewById(R.id.rl_notice).setOnClickListener(this);

        loadingView = (RelativeLayout) findViewById(R.id.rl_loading);
        loadingView.setVisibility(View.VISIBLE);
        errorView = (RelativeLayout) findViewById(R.id.rl_error);

        initImageloader();
    }

    private void initImageloader() {
        mImageLoader = App.initImageLoader();
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
                .showStubImage(R.drawable.no_data)//加载过程中显示的图像，可以不设置
                .showImageForEmptyUri(R.drawable.no_data) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.no_data) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        if (Utils.isIMMShow(v)) {
            Utils.hidiInputMethodManager(v);
        }
        switch (v.getId()) {
            case R.id.title_bar_left://关闭
                finish();
                break;
            case R.id.iv_cart://购物车图标
                if (Utils.loginState()) {//判断是否登录过
                    sendGetCartGoodsListAction();
                } else {
                    startActivity(new Intent(this, RegistActivity.class));
                    finish();
                }
                break;
            case R.id.rl_notice://去购物车
                if (Utils.loginState()) {
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
            case Event.GET_GOODS_DETAILS_SUCCESS://获取商品详情成功事件
                hideErrorLocal();
                loadingView.setVisibility(View.GONE);
                GoodsViewModel model = (GoodsViewModel) bundle.getSerializable(GoodsLogic.EXTRA_GOODINFO);

                shop = model.getShop();
                setGoodsInfo(model);
                cartNotice(bundle);
                setGoodsLove(model);
                break;
            case Event.INSERT_OR_UPDATE_CART_GOOD_NUM_SUCESS://添加或删除/修改商品数量成功事件
                cartNoticeUpdate(bundle);
                notifyPop(bundle);
                break;
            case Event.GET_CART_LIST_GOODS_DETAILS_SUCESS://获取商品详情页购物车商品列表成功事件
                showCartPopWindow(bundle);
                break;
            case Event.GET_DELETE_CART_GOOD_SUCESS://删除购物车一条商品成功事件
                deleteCarNotice(bundle);
                notifyPopDete(bundle);
                break;
            case Event.GET_GOODS_DETAILS_FAILED://获取商品详情失败事件
            case Event.INSERT_OR_UPDATE_CART_GOOD_NUM_FAILED://添加或删除/修改商品数量失败事件
            case Event.GET_CART_LIST_GOODS_DETAILS_FAILED://获取商品详情页购物车商品列表失败事件
            case Event.GET_DELETE_CART_GOOD_FAILED://删除购物车一条商品失败事件
                MyToast.show(this, notice);
                break;

            default:
                break;
        }
    }

    private void deleteCarNotice(Bundle bundle) {
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
                setListViewHeightBasedOnChildren(goodsList);
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
                setListViewHeightBasedOnChildren(goodsList);
            }
        }
    }

    /**
     * 刪除
     */
    private void notifyPopDete(Bundle bundle) {
        int goodId = bundle.getInt(ShoppingCartLogic.EXTRA_ID);
        CartGoods goods = null;
        for (int i = 0; i < cartList.size(); i++) {
            goods = cartList.get(i);
            if (goods.getGoodsId() == goodId) {
                break;
            }
        }
        cartList.remove(goods);
        cartListAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(goodsList);
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
        goodsList = (ListView) view.findViewById(R.id.listview);
        cartListAdapter = new CartListAdapter(this, cartList);
        goodsList.setAdapter(cartListAdapter);
        setListViewHeightBasedOnChildren(goodsList);

        carPopupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);//App.windowHeight * 2 / 5
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


    /**
     * 关联购买
     */
    private void setGoodsLove(GoodsViewModel model) {
        ArrayList<Goods> goodsList = (ArrayList<Goods>) model.getRelativeGoodsList();
        if (goodsList != null && goodsList.size() > 0) {
            goodsLove.clear();
            goodsLove.addAll(goodsList);
            if (goodsLoveAdapter == null) {
                goodsLoveAdapter = new GoodsLoveAdapter(this, goodsLove);
                gridview.setAdapter(goodsLoveAdapter);
            } else {
                goodsLoveAdapter.addData(goodsLove);
            }
        } else {
            MyToast.show(context, "无关联购买商品！");
        }
    }

    /**
     * 购物车提示
     */
    private void cartNotice(Bundle bundle) {
        GoodsViewModel model = (GoodsViewModel) bundle.getSerializable(GoodsLogic.EXTRA_GOODINFO);
        BottomStatusBar bsb = model.getPost();

        if (bsb != null) {
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
        } else {
            cartNum.setVisibility(View.GONE);
            cartPrice.setText("￥ 0.00");
            cartNotice.setText("差" + shop.getFreePostPrice() + "元免费配送");
        }
    }

    /**
     * 初始化商品信息
     */
    private void setGoodsInfo(GoodsViewModel model) {
        goods = model.getGoods();
        mImageLoader.displayImage(goods.getPictureUrl(), goodsImg, options);
        goodsName.setText(goods.getName());
        addSub.setLimited(goods.getLimitCount());

        boolean status = goods.getOnSale();
        if (status) {//特价
            goodsPrice.setVisibility(View.VISIBLE);
            goodsPrice.setText("￥" + goods.getOriginalPrice() + "");
            goodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            promotePrice.setText("￥" + goods.getPrice() + "");
        } else {
            goodsPrice.setVisibility(View.GONE);
            promotePrice.setText("￥" + goods.getPrice() + "");
        }
        addSub.setNum(goods.getCartGoodsCount());
    }

    /**
     * 商品详情页面
     */
    private void sendGoodsDetailsAction() {
        Intent intent = new Intent(GoodsLogic.ACTION_GET_GOODS_DETAILS);
        intent.putExtra(GoodsLogic.EXTRA_ID, goodsId);
        sendAction(intent);
    }

    /**
     * 手动输入商品数量
     */
    public void sendAddGood2CartAction3() {
        Intent intent = new Intent(ShoppingCartLogic.ACTION_INSERTORUPDATECARTGOODNUM);
        intent.putExtra(ShoppingCartLogic.EXTRA_ID, goodsId);
        intent.putExtra(ShoppingCartLogic.EXTRA_NUM, goods_num);
        intent.putExtra(ShoppingCartLogic.EXTRA_CMD, 3);
        sendAction(intent);
    }

    /**
     * 获取购物车商品列表
     */
    private void sendGetCartGoodsListAction() {
        sendAction(new Intent(ShoppingCartLogic.ACTION_GETCARTLISTGOODSDETAILS));
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this, Event.GET_GOODS_DETAILS_SUCCESS, Event.GET_GOODS_DETAILS_FAILED,
                Event.INSERT_OR_UPDATE_CART_GOOD_NUM_SUCESS, Event.INSERT_OR_UPDATE_CART_GOOD_NUM_FAILED,
                Event.GET_CART_LIST_GOODS_DETAILS_SUCESS, Event.GET_CART_LIST_GOODS_DETAILS_FAILED,
                Event.GET_DELETE_CART_GOOD_SUCESS, Event.GET_DELETE_CART_GOOD_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
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
