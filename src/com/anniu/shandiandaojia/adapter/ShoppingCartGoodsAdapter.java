package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.db.jsondb.CartGoods;
import com.anniu.shandiandaojia.logic.ShoppingCartLogic;
import com.anniu.shandiandaojia.view.AddAndSubView;
import com.anniu.shandiandaojia.view.MyDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author zxl
 * @ClassName: ShoppingCartGoodsAdapter
 * @Description: 购物车页面商品列表适配器
 * @date 2015年6月9日 下午3:46:39
 */
public class ShoppingCartGoodsAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<CartGoods> mList;
    public ImageLoader mImageLoader;
    private DisplayImageOptions options;

    public ShoppingCartGoodsAdapter(Context context, ArrayList<CartGoods> goods) {
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
        CartGoods goods = mList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_shopping_cart_goods, null);
            holder.img = (ImageView) convertView.findViewById(R.id.iv_img);
            holder.goodsName = (TextView) convertView.findViewById(R.id.tv_goods_name);
            holder.goodsPrice = (TextView) convertView.findViewById(R.id.tv_goods_price);
            holder.line = convertView.findViewById(R.id.line);
            holder.addSub = (AddAndSubView) convertView.findViewById(R.id.addSub);
            holder.goodsDelete = (TextView) convertView.findViewById(R.id.tv_goods_delete);
            holder.addSub.setButtonLayoutParm(27, 27);

            holder.goodsDelete.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showAlertDialog((Integer) v.getTag());
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        double goodPrice = goods.getUnitPrice();
        BigDecimal bdgoodsPrice = new BigDecimal(goodPrice);
        bdgoodsPrice = bdgoodsPrice.setScale(2, BigDecimal.ROUND_DOWN);
        holder.goodsPrice.setText("￥" + bdgoodsPrice);
        mImageLoader.displayImage(goods.getPictureUrl(), holder.img, options);
        holder.goodsName.setText(goods.getGoodsName());
        holder.addSub.setNum(goods.getGoodsCount());
        holder.addSub.setLimited(goods.getLimitCount());
        holder.addSub.setGoodsId(goods.getShopGoodsId());
        holder.goodsDelete.setTag(goods.getShopGoodsId());

        if (position == mList.size() - 1) {
            holder.line.setVisibility(View.GONE);
        } else {
            holder.line.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView img;
        TextView goodsName;
        TextView goodsPrice;
        AddAndSubView addSub;
        TextView goodsDelete;
        View line;
    }

    public void addData(ArrayList<CartGoods> goodsList) {
        mList = goodsList;
        notifyDataSetChanged();
    }

    /**
     * 刪除购物车中某一条纪录
     */
    private void sendDeleteAction(int goodId) {
        Intent intent = new Intent(ShoppingCartLogic.ACTION_GET_DELETE_CART_GOOD);
        intent.putExtra(ShoppingCartLogic.EXTRA_ID, goodId);
        App.getInstance().sendAction(intent);
    }

    /**
     * 提示框
     */
    public void showAlertDialog(final int goodId) {
        MyDialog.Builder builder = new MyDialog.Builder(mContext);
        builder.setTitle("提示");
        builder.setMessage("您真的要刪除该商品！");
        builder.setConfirmButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                sendDeleteAction(goodId);
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
}
