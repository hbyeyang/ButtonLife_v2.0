package com.anniu.shandiandaojia.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.adapter.ShoppingCartGoodsAdapter;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.BottomStatusBar;
import com.anniu.shandiandaojia.db.jsondb.CartGoods;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.ShoppingCartLogic;
import com.anniu.shandiandaojia.utils.CommonUtil;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.Utils;
import com.anniu.shandiandaojia.view.MyDialog;
import com.ant.liao.GifView;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author zxl
 * @ClassName: ShoppingCartActivity
 * @Description: 购物车页面
 * @date 2015年6月9日 下午2:10:30
 */
public class ShoppingCartActivity extends BaseActivity {
    private TextView titleBarTv, cartPrice, cartNotice;
    private ArrayList<CartGoods> goodsList = new ArrayList<CartGoods>();
    private ShoppingCartGoodsAdapter adapter;
    private RelativeLayout loadingView, errorView, empty;
    private GifView gif;
    private LinearLayout initData;
    private ImageView leftTitle;
    private ListView listview;
    private BottomStatusBar bsb;

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_shopping_cart);
        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText(R.string.shopping_cart);
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
        listview = (ListView) findViewById(R.id.listview);
        findViewById(R.id.iv_cart).setVisibility(View.INVISIBLE);
        cartNotice = (TextView) findViewById(R.id.tv_notice);
        cartPrice = (TextView) findViewById(R.id.cart_price);
        findViewById(R.id.rl_notice).setOnClickListener(this);

        loadingView = (RelativeLayout) findViewById(R.id.rl_loading);
        loadingView.setVisibility(View.VISIBLE);
        gif = (GifView) findViewById(R.id.gif);
        gif.setGifImage(R.drawable.ic_loading);
        errorView = (RelativeLayout) findViewById(R.id.rl_error);
        initData = (LinearLayout) findViewById(R.id.ll_init_data);
        empty = (RelativeLayout) findViewById(R.id.rl_empty);

        sendGoodsListAction();
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
            case R.id.title_bar_left:// 返回
                finish();
                break;
            case R.id.rl_notice:// 去结算
                String cartText = cartNotice.getText().toString();
                if ("去结算".equals(cartText)) {
                    if (goodsList.size() <= 0) {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                        return;
                    }
                    Intent intent = new Intent(this, BalanceActivity.class);
                    intent.putExtra(BalanceActivity.EXTRA_FROM, 0);
                    startActivity(intent);
                    finish();
                } else if ("去逛逛".equals(cartText)) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    showAlertDialog();
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        switch (eventId) {
            case Event.GET_CART_INFO_SUCESS://获取购物车页面信息成功事件
                loadingView.setVisibility(View.GONE);
                hideErrorLocal();
                cartNotice(bundle);
                goodsListInit(bundle);
                break;
            case Event.GET_DELETE_CART_GOOD_SUCESS://删除购物车一条商品成功事件
                loadingView.setVisibility(View.GONE);
                cartNotice(bundle);
                notifyPopDete(bundle);
                break;
            case Event.INSERT_OR_UPDATE_CART_GOOD_NUM_SUCESS://添加或删除/修改商品数量成功事件
                cartNotice(bundle);
                notifyPop(bundle);
                break;
            case Event.GET_DELETE_CART_GOOD_FAILED://删除购物车一条商品失败事件
            case Event.GET_CART_INFO_FAILED://获取购物车页面信息失败事件
            case Event.INSERT_OR_UPDATE_CART_GOOD_NUM_FAILED://添加或删除/修改商品数量失败事件
                loadingView.setVisibility(View.GONE);
                MyToast.show(this, notice);
                break;
            default:
                break;
        }
    }

    /**
     * 点击加减判断是否减到0
     */
    private void notifyPop(Bundle bundle) {
        int operation = bundle.getInt(ShoppingCartLogic.EXTRA_CMD);
        int goodId = bundle.getInt(ShoppingCartLogic.EXTRA_ID);
        int number = bundle.getInt(ShoppingCartLogic.EXTRA_NUM);

        if (operation == 2) {
            CartGoods goods = null;
            for (int i = 0; i < goodsList.size(); i++) {
                goods = goodsList.get(i);
                if (goods.getId() == goodId) {
                    break;
                }
            }
            int num = goods.getNum();
            if (--num <= 0) {
                goodsList.remove(goods);
                adapter.notifyDataSetChanged();
            } else {
                goods.setNum(num--);
            }
        } else if (operation == 3) {
            if (number == 0) {
                CartGoods goods = null;
                for (int i = 0; i < goodsList.size(); i++) {
                    goods = goodsList.get(i);
                    if (goods.getId() == goodId) {
                        break;
                    }
                }
                goodsList.remove(goods);
                adapter.notifyDataSetChanged();
            }
        }

        if (goodsList.size() <= 0) {
            initData.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 刪除
     */
    private void notifyPopDete(Bundle bundle) {
        int goodis = bundle.getInt(ShoppingCartLogic.EXTRA_ID);
        CartGoods goods = null;
        for (int i = 0; i < goodsList.size(); i++) {
            goods = goodsList.get(i);
            if (goods.getShopGoodsId() == goodis) {
                break;
            }
        }
        goodsList.remove(goods);
        adapter.notifyDataSetChanged();
        if (goodsList.size() <= 0) {
            initData.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 商品列表数据展示
     */
    private void goodsListInit(Bundle bundle) {
        ArrayList<CartGoods> list = (ArrayList<CartGoods>) bundle.getSerializable(ShoppingCartLogic.EXTRA_CARTGOODSLIST);
        if (list != null && list.size() > 0) {
            goodsList.clear();
            goodsList = list;
            if (adapter == null) {
                adapter = new ShoppingCartGoodsAdapter(this, goodsList);
                listview.setAdapter(adapter);
                listview.setOnScrollListener(new PauseOnScrollListener(adapter.mImageLoader, false, true));
            } else {
                adapter.addData(goodsList);
            }
        } else {
            initData.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 购物车提示
     */
    private void cartNotice(Bundle bundle) {
        bsb = (BottomStatusBar) bundle.getSerializable(ShoppingCartLogic.EXTRA_POST);
        double amount = bsb.getAmount();
        BigDecimal bdmoney = new BigDecimal(amount);
        bdmoney = bdmoney.setScale(2, BigDecimal.ROUND_DOWN);
        cartPrice.setText(amount > 0 ? "￥" + bdmoney : "￥0.00");
        String text;
        if (0 < amount && amount < bsb.getFreePostPrice()) {
            double sub = Utils.sub(bsb.getFreePostPrice(), amount);
            text = "差" + sub + "元免费配送";
        } else if (amount == 0) {
            text = "去逛逛";
        } else {
            text = "去结算";
        }
        cartNotice.setText(text);
    }

    /***
     * 提示框
     */
    public void showAlertDialog() {
        if (goodsList != null && goodsList.size() > 0) {
            MyDialog.Builder builder = new MyDialog.Builder(this);
            CartGoods goods = goodsList.get(0);
            builder.setTitle(goods.getShopName());
            builder.setMessage("商铺最低起送价为" + bsb.getFreePostPrice() + "元！");
            builder.setConfirmButton("继续购物", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //跳回到首页
                    startActivity(new Intent(ShoppingCartActivity.this, MainActivity.class));
                    dialog.dismiss();
                    finish();
                }
            });
            builder.create().show();
        } else {
            startActivity(new Intent(ShoppingCartActivity.this, MainActivity.class));
            finish();
        }
    }


    /**
     * 获取购物车页面信息
     */
    private void sendGoodsListAction() {
        sendAction(new Intent(ShoppingCartLogic.ACTION_GET_SHOPPING_CART_INFO));
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this, Event.GET_CART_INFO_SUCESS, Event.GET_CART_INFO_FAILED,
                Event.GET_DELETE_CART_GOOD_SUCESS, Event.GET_DELETE_CART_GOOD_FAILED,
                Event.INSERT_OR_UPDATE_CART_GOOD_NUM_SUCESS, Event.INSERT_OR_UPDATE_CART_GOOD_NUM_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }

}
