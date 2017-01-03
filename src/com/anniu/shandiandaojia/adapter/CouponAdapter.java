package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.db.jsondb.Coupon;
import com.anniu.shandiandaojia.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;

public class CouponAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Coupon> coupons;

    public CouponAdapter(Context context, ArrayList<Coupon> voucherList) {
        super();
        this.mContext = context;
        this.coupons = voucherList;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        if (coupons != null) {
            return coupons.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (coupons.get(position) != null) {
            return coupons.get(position);
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
        Coupon coupon = coupons.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_money_ticket, null);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_disc = (TextView) convertView.findViewById(R.id.tv_disc);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.valid_date = (TextView) convertView.findViewById(R.id.valid_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_price.setText(coupon.getAmount() + "元");
        String name;
        String text;
        if (TextUtils.isEmpty(coupon.getShopName())) {
            name = "";
            text = "全场";
        } else {
            text = coupon.getShopName();
            name = coupon.getShopName();
        }

        holder.tv_name.setText(name + "优惠券");
        holder.tv_disc.setText("满" + coupon.getMinUseAmount() + "元商品可用，此优惠券适用于" + text);

        String enddate = coupon.getExpireTime();
        String currentTime = Utils.getCurrentTime();
        String end = Utils.getTimeFormatStr2(Long.parseLong(enddate));
        String current = Utils.getTimeFormatStr2(Long.parseLong(currentTime));
        try {
            int daysBetween = Utils.daysBetween(current, end);
            String timeFormatStr2 = Utils.getTimeFormatStr2(Long.parseLong(enddate));
            String[] splitTime = timeFormatStr2.split("-");
            daysBetween += 1;
            holder.tv_time.setText("剩余：" + daysBetween + "天");
            holder.valid_date.setText("有效期至" + splitTime[0] + "年" + splitTime[1] + "月" + splitTime[2] + "日");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    static class ViewHolder {
        TextView tv_price;
        TextView tv_name;
        TextView tv_disc;
        TextView tv_time;
        TextView valid_date;
    }
}
