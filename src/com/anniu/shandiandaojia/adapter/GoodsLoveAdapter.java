package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

import java.util.ArrayList;

/**
 * @author zxl
 * @ClassName: GoodsLoveAdapter
 * @Description: 关联购买
 * @date 2015年6月4日 上午11:11:38
 */
public class GoodsLoveAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<Goods> mList;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    public int changecode;

    public GoodsLoveAdapter(Context context, ArrayList<Goods> goods) {
        super();
        this.mContext = context;
        this.mList = goods;
        mInflater = LayoutInflater.from(mContext);
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
            holder.goodsName = (TextView) convertView.findViewById(R.id.tv_goods_name);
            holder.goodsPrice = (TextView) convertView.findViewById(R.id.tv_goods_price);
            holder.promotePrice = (TextView) convertView.findViewById(R.id.tv_promote_price);
            holder.add = (ImageView) convertView.findViewById(R.id.iv_add);
            holder.add.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (Utils.loginState()) {
                        Goods goods = (Goods) v.getTag();
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

        boolean status = goods.getOnSale();
        if (status) {//特价
            holder.rl_price.setVisibility(View.VISIBLE);
            holder.goodsPrice.setText("￥" + goods.getOriginalPrice() + "");
            holder.promotePrice.setVisibility(View.VISIBLE);
            holder.promotePrice.setText("￥" + goods.getPrice() + "");
            holder.goodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.rl_price.setVisibility(View.GONE);
            holder.promotePrice.setText("￥" + goods.getPrice() + "");
        }
        holder.add.setTag(goods);
        return convertView;
    }

    class ViewHolder {
        RelativeLayout rl_price;
        ImageView shopImg;
        TextView goodsName;
        TextView goodsPrice;
        TextView promotePrice;
        ImageView add;
    }

    public void addData(ArrayList<Goods> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void sendAddGood2CartAction(int id) {
        Intent intent = new Intent(ShoppingCartLogic.ACTION_INSERTORUPDATECARTGOODNUM);
        intent.putExtra(ShoppingCartLogic.EXTRA_ID, id);
        intent.putExtra(ShoppingCartLogic.EXTRA_NUM, 0);
        intent.putExtra(ShoppingCartLogic.EXTRA_CMD, "PUSH");
        App.getInstance().sendAction(intent);
    }
}
