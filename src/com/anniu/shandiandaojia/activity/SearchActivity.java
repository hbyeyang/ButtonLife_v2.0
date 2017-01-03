package com.anniu.shandiandaojia.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.adapter.SearchAdapter;
import com.anniu.shandiandaojia.adapter.SearchHotKeywordsAdapter;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.base.BaseLogic;
import com.anniu.shandiandaojia.db.jsondb.Goods;
import com.anniu.shandiandaojia.db.jsondb.HotWords;
import com.anniu.shandiandaojia.logic.Event;
import com.anniu.shandiandaojia.logic.GoodsLogic;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.SPUtils;
import com.anniu.shandiandaojia.utils.Utils;
import com.anniu.shandiandaojia.view.ClearEditText;
import com.anniu.shandiandaojia.view.MyGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zxl
 * @ClassName: SearchActivity
 * @Description: 搜索页面
 * @date 2015年6月2日 下午4:37:59
 */
public class SearchActivity extends BaseActivity {
    private ImageView back;
    private ClearEditText searchText;
    private ImageView search;
    private MyGridView gridview;
    private SearchHotKeywordsAdapter adapter;
    private List<HotWords> listItem = new ArrayList<HotWords>();
    private RelativeLayout loadingView;

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        if (v.getId() == R.id.img_back) {//返回
            finish();
        } else if (v.getId() == R.id.iv_search) {//搜索
            loadingView.setVisibility(View.VISIBLE);
            getSearchGoods();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
        }
    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_search);
        getHotKeyWordsAction();

        back = (ImageView) findViewById(R.id.img_back);
        searchText = (ClearEditText) findViewById(R.id.search_text);
        search = (ImageView) findViewById(R.id.iv_search);
        gridview = (MyGridView) findViewById(R.id.gridview);
        back.setOnClickListener(this);
        search.setOnClickListener(this);
        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HotWords searchhots = listItem.get(position);
                String name = searchhots.getName();
                searchText.setText(name);
            }
        });
        loadingView = (RelativeLayout) findViewById(R.id.rl_loading);
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {
        String notice = bundle.getString(BaseLogic.EXTRA_ERROR);
        switch (eventId) {
            case Event.GET_INDEX_SEARCHHOTS_SUCESS://进入搜索界面热词成功事件
                loadingView.setVisibility(View.GONE);
                ArrayList<HotWords> searchhotsList = (ArrayList<HotWords>) bundle.getSerializable(GoodsLogic.EXTRA_SEARCHHOTS_LIST);
                if (searchhotsList != null && searchhotsList.size() > 0) {
                    listItem.clear();
                    listItem.addAll(searchhotsList);
                    if (adapter == null) {
                        adapter = new SearchHotKeywordsAdapter(this, listItem);
                        gridview.setAdapter(adapter);
                    } else {
                        adapter.setData(listItem);
                    }
                }
                break;
            case Event.GET_INDEX_SEARCHHOTS_FAILED://进入搜索界面热词失败事件
                loadingView.setVisibility(View.GONE);
                MyToast.show(this, notice);
                break;
            case Event.GET_SEARCH_GOODS_SUCESS://搜索商品成功事件
                loadingView.setVisibility(View.GONE);
                ArrayList<Goods> goodsList = (ArrayList<Goods>) bundle.getSerializable(GoodsLogic.EXTRA_GOODS_LIST);
                if (goodsList != null && goodsList.size() > 0) {
                    showPopupwindow(goodsList);
                } else {
                    MyToast.show(this, "您搜索的商品不存在！");
                }
                break;
            case Event.GET_SEARCH_GOODS_FAILED://搜索商品失败事件
                loadingView.setVisibility(View.GONE);
                MyToast.show(this, notice);
                break;
            default:
                break;
        }
    }

    /**
     * 点击搜索商品成功后展示搜索的商品
     */
    private void showPopupwindow(final ArrayList<Goods> goodsList) {
        ListView contentView = new ListView(getApplicationContext());
        contentView.setAdapter(new SearchAdapter(goodsList, this));
        contentView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Goods good = goodsList.get(position);
                Intent intent = new Intent();
                intent.putExtra(GoodsLogic.EXTRA_ID, good.getId());
                intent.setClass(SearchActivity.this, GoodsDetailActivity.class);
                startActivity(intent);
            }
        });

        PopupWindow popupWindow = new PopupWindow(contentView, searchText.getWidth(), searchText.getHeight() * 8, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.update();
        popupWindow.showAsDropDown(searchText);
        popupWindow.setOutsideTouchable(true);
    }

    /**
     * 获取搜索界面热词
     */
    private void getHotKeyWordsAction() {
        Intent intent = new Intent(GoodsLogic.ACTION_GET_HOTSWORDS);
        sendAction(intent);
    }

    /**
     * 搜索商品
     */
    private void getSearchGoods() {
        String goodsName = searchText.getText().toString().trim();
        if (TextUtils.isEmpty(goodsName)) {
            MyToast.show(this, "请您输入需要搜索的内容！");
            return;
        }
        Intent intent = new Intent(GoodsLogic.ACTION_GET_SEARCH_GOODS);
        intent.putExtra(GoodsLogic.EXTRA_SHOPCODE, SPUtils.getInt(this, GlobalInfo.KEY_SHOPCODE, 0));
        intent.putExtra(GoodsLogic.EXTRA_KEYWORDS, goodsName);
        sendAction(intent);
    }

    @Override
    protected void addEventListener() {
        App.getInstance().addListener(this,
                Event.GET_INDEX_SEARCHHOTS_SUCESS, Event.GET_INDEX_SEARCHHOTS_FAILED,
                Event.GET_SEARCH_GOODS_SUCESS, Event.GET_SEARCH_GOODS_FAILED);
    }

    @Override
    protected void removeListener() {
        App.getInstance().removeListener(this);
    }
}
