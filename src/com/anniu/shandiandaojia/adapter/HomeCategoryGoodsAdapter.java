package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.activity.RegistActivity;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.db.jsondb.Goods;
import com.anniu.shandiandaojia.logic.ShoppingCartLogic;
import com.anniu.shandiandaojia.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

public class HomeCategoryGoodsAdapter extends BaseAdapter {
    private List<Goods> mList = new ArrayList<Goods>();
    public ImageLoader mImageLoader;
    private LayoutInflater mInflater;
    private DisplayImageOptions options;
    private Context mContext;

    public HomeCategoryGoodsAdapter(Context context, List<Goods> list) {
        super();
        this.mList = list;
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = App.initImageLoader();
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
                .showStubImage(R.drawable.no_data)
                .showImageForEmptyUri(R.drawable.no_data) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.no_data) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Goods goods = mList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_bargain_goods, null);
            holder.rl_price = (RelativeLayout) convertView.findViewById(R.id.rl_price);
            holder.shopImg = (ImageView) convertView.findViewById(R.id.iv_shop_img);

            ViewGroup.LayoutParams imgParams = holder.shopImg.getLayoutParams();
            imgParams.height = App.windowWidth / 3;
            imgParams.width = App.windowWidth / 3;
            holder.shopImg.setLayoutParams(imgParams);

            holder.goodsName = (TextView) convertView.findViewById(R.id.tv_goods_name);
            holder.goodsPrice = (TextView) convertView.findViewById(R.id.tv_goods_price);
            holder.promotePrice = (TextView) convertView.findViewById(R.id.tv_promote_price);
            holder.add = (ImageView) convertView.findViewById(R.id.iv_add);
            holder.add.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (Utils.isFastClick()) {
                        return;
                    }
                    if (Utils.loginState()) {
                        Goods goods = (Goods) v.getTag(R.id.tag_first);
                        sendAddGood2CartAction(goods.getId());
                    } else {
                        mContext.startActivity(new Intent(mContext, RegistActivity.class));
                    }
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        mImageLoader.displayImage(goods.getPictureUrl(), holder.shopImg, options);
        holder.goodsName.setText(goods.getName());
        if (goods.getOnSale()) {// 特价
            holder.rl_price.setVisibility(View.VISIBLE);
            holder.goodsPrice.setText("￥" + goods.getOriginalPrice());
            holder.promotePrice.setText("￥" + goods.getPrice());
            holder.goodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.rl_price.setVisibility(View.GONE);
            holder.promotePrice.setText("￥" + goods.getPrice());
        }
        holder.add.setTag(R.id.tag_first, goods);
        holder.add.setTag(R.id.tag_second, holder.shopImg);
        return convertView;
    }

    static class ViewHolder {
        ImageView shopImg;
        TextView goodsName;
        TextView goodsPrice;
        TextView promotePrice;
        ImageView add;
        RelativeLayout rl_price;
    }

    public void addData(ArrayList<Goods> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void sendAddGood2CartAction(int id) {
        Intent intent = new Intent(ShoppingCartLogic.ACTION_INSERTORUPDATECARTGOODNUM);
        intent.putExtra(ShoppingCartLogic.EXTRA_ID, id);
        intent.putExtra(ShoppingCartLogic.EXTRA_COUNT, 0);
        intent.putExtra(ShoppingCartLogic.EXTRA_CMD, "PUSH");
        App.getInstance().sendAction(intent);
    }

    public void startAnimation(ImageView imageview) {
        int py = App.windowHeight;
        int px = App.windowWidth;

        float x = imageview.getX();
        float y = imageview.getY();
        Animation msa = new ScaleAnimation(
                1.0f, 0, 1.0f, 0,
                Animation.ABSOLUTE, 0.5f,
                Animation.ABSOLUTE, 0.5f);
        msa.setDuration(1000);
        msa.setFillAfter(true);

        Animation mta = new TranslateAnimation(
                Animation.ABSOLUTE, x / 2, Animation.ABSOLUTE, (px - x) / 2,
                Animation.ABSOLUTE, y / 2, Animation.ABSOLUTE, (py - y) / 2);
        mta.setDuration(1000);
        AnimationSet mas = new AnimationSet(false);
        mas.addAnimation(msa);
        mas.setFillAfter(true);
        mas.addAnimation(mta);
        imageview.startAnimation(mas);
    }
}
