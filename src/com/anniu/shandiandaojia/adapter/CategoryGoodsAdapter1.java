package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.activity.GoodsDetailActivity;
import com.anniu.shandiandaojia.activity.RegistActivity;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.db.jsondb.Goods;
import com.anniu.shandiandaojia.logic.GoodsLogic;
import com.anniu.shandiandaojia.logic.ShoppingCartLogic;
import com.anniu.shandiandaojia.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

/**
 * @author zxl
 * @ClassName: BargainGoodsAdapter
 * @Description: 商品分类数据适配器
 * @date 2015年6月2日 下午7:40:26
 */
public class CategoryGoodsAdapter1 extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<Goods> mList;
    public ImageLoader mImageLoader;
    private DisplayImageOptions options;

    public CategoryGoodsAdapter1(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = App.initImageLoader();
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
                .showStubImage(R.drawable.no_data)// 加载过程中显示的图像，可以不设置
                .showImageForEmptyUri(R.drawable.no_data) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.no_data) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();
    }


    @Override
    public Object getItem(int position) {
        if (mList != null) {
            return mList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItem(List<Goods> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
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

            LayoutParams imgParams = holder.shopImg.getLayoutParams();
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
                    if (Utils.loginState()) {// 先判断有没有登陆
                        Goods goods = (Goods) v.getTag();
                        sendAddGood2CartAction(goods.getId());
                    } else {
                        mContext.startActivity(new Intent(mContext, RegistActivity.class));
                    }
                }
            });

            holder.shopImg.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (Utils.isFastClick()) {
                        return;
                    }
                    Goods good = (Goods) v.getTag();
                    Intent intent = new Intent(mContext, GoodsDetailActivity.class);
                    intent.putExtra(GoodsLogic.EXTRA_ID, good.getId());
                    intent.putExtra(GoodsLogic.EXTRA_NUM, good.getCartGoodsCount());
                    intent.putExtra(GoodsLogic.EXTRA_LIMITED, good.getLimitCount());
                    mContext.startActivity(intent);
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        mImageLoader.displayImage(goods.getPictureUrl(), holder.shopImg, options);
        holder.goodsName.setText(goods.getName());
        holder.goodsName.setTextColor(mContext.getResources().getColor(R.color.black));//设置分类页面字体颜色

        Boolean sale = goods.getOnSale();
        if (sale) {//特价
            holder.rl_price.setVisibility(View.VISIBLE);
            holder.goodsPrice.setText("￥" + goods.getOriginalPrice());
            holder.promotePrice.setVisibility(View.VISIBLE);
            holder.promotePrice.setText("￥" + goods.getPrice());
            holder.goodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.rl_price.setVisibility(View.GONE);
            holder.promotePrice.setText("￥" + goods.getPrice());
        }
        holder.add.setTag(goods);
        holder.shopImg.setTag(goods);
        return convertView;
    }

    static class ViewHolder {
        RelativeLayout rl_price;
        ImageView shopImg;
        TextView goodsName;
        TextView goodsPrice;
        TextView promotePrice;
        ImageView add;
    }

    /**
     * 添加商品数量
     */
    public void sendAddGood2CartAction(int id) {
        Intent intent = new Intent(ShoppingCartLogic.ACTION_INSERTORUPDATECARTGOODNUM);
        intent.putExtra(ShoppingCartLogic.EXTRA_ID, id);
        intent.putExtra(ShoppingCartLogic.EXTRA_COUNT, 0);
        intent.putExtra(ShoppingCartLogic.EXTRA_CMD, "PUSH");
        App.getInstance().sendAction(intent);
    }
}
