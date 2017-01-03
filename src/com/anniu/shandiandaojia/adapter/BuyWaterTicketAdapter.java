package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.db.jsondb.Goods;
import com.anniu.shandiandaojia.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/***
 * @author YY
 * @ClassName: BuyWaterTicketAdapter
 * @Description: 购买水票的适配器
 * @date 2015年7月3日 上午9:46:45
 */
public class BuyWaterTicketAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Goods> list;
    private Context mContext;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;

    public BuyWaterTicketAdapter(Context context, ArrayList<Goods> goodses) {
        super();
        this.list = goodses;
        this.mContext = context;
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
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (list != null) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Goods good = list.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_water_ticket1, null);
            holder.ivBg = (ImageView) convertView.findViewById(R.id.ivBg);

            ViewGroup.LayoutParams params = holder.ivBg.getLayoutParams();
            int width = App.windowWidth - Utils.dip2px(mContext, 24);
            params.height = width * 2 / 5;
            params.width = width;
            holder.ivBg.setLayoutParams(params);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        mImageLoader.displayImage(good.getThumbUrl(), holder.ivBg, options);
        return convertView;
    }

    static class ViewHolder {
        ImageView ivBg;
    }
}

