package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.db.jsondb.ShopInfo;
import com.anniu.shandiandaojia.db.jsondb.ShopStatus;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * @author zxl
 * @ClassName: ShopListAdapter
 * @Description: 商铺列表
 * @date 2015年6月1日 下午3:08:11
 */
public class ShopListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<ShopInfo> mList;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;

    public ShopListAdapter(Context context, ArrayList<ShopInfo> shopsList) {
        super();
        this.mContext = context;
        this.mList = shopsList;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = App.initImageLoader();
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
                .showStubImage(R.drawable.no_title_bg)//加载过程中显示的图像，可以不设置
                .showImageForEmptyUri(R.drawable.no_title_bg) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.no_title_bg) // 设置图片加载/解码过程中错误时候显示的图片
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
        ShopInfo shopInfo = mList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_shop_list, null);
            holder.shopImg = (ImageView) convertView.findViewById(R.id.iv_shop_img);
            holder.shopName = (TextView) convertView.findViewById(R.id.tv_shop_name);
            holder.businessHours = (TextView) convertView.findViewById(R.id.tv_business_hours);
            holder.shopAddress = (TextView) convertView.findViewById(R.id.tv_shop_address);
            holder.shopDes = (TextView) convertView.findViewById(R.id.tv_shop_des);
            holder.shopClose = (ImageView) convertView.findViewById(R.id.iv_shop_close);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        mImageLoader.displayImage(shopInfo.getPictureAddress(), holder.shopImg, options);
        holder.shopName.setText(shopInfo.getName());
        holder.businessHours.setText("(" + shopInfo.getOpenTime() + "—" + shopInfo.getCloseTime() + ")");
        holder.shopAddress.setText(shopInfo.getAddress() + "(" + shopInfo.getDistance() + "米)");
        holder.shopDes.setText(shopInfo.getFreePostPrice() + "元起送/平均" + shopInfo.getPostTime() + "分钟" + "/" + shopInfo.getDistance() + "米");
        ShopStatus status = shopInfo.getStatus();
        if (status != null) {
            // 1：正常营业 0：闭店
            if (status.equals(ShopStatus.OPENED)) {
                holder.shopClose.setVisibility(View.GONE);
            } else {
                holder.shopClose.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    class ViewHolder {
        ImageView shopImg;
        TextView shopName;
        TextView businessHours;
        TextView shopAddress;
        TextView shopDes;
        ImageView shopClose;
    }

    public void addData(ArrayList<ShopInfo> list) {
        mList = list;
        notifyDataSetChanged();
    }
}
