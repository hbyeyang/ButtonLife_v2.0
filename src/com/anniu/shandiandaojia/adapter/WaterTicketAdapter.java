package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.db.jsondb.WaterInfo;
import com.anniu.shandiandaojia.utils.Utils;

import java.util.ArrayList;

/***
 * @author YY
 * @ClassName: WaterTicketAdapter
 * @Description: 水票适配器
 * @date 2015年6月29日 下午2:22:36
 */
public class WaterTicketAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<WaterInfo> waterticketList;
    private LayoutInflater inflater;

    public WaterTicketAdapter(Context context, ArrayList<WaterInfo> mList) {
        super();
        this.mContext = context;
        this.waterticketList = mList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return waterticketList.size();
    }

    @Override
    public Object getItem(int position) {
        return waterticketList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        WaterInfo waterInfo = waterticketList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_water_ticket2, null);
            holder.water_name = (TextView) convertView.findViewById(R.id.tv_water_name);
            holder.coupon_money = (TextView) convertView.findViewById(R.id.tv_coupon_money);
            holder.water_num = (TextView) convertView.findViewById(R.id.tv_water_num);
            holder.water_money = (TextView) convertView.findViewById(R.id.tv_water_money);
            holder.layout = (RelativeLayout) convertView.findViewById(R.id.rl_layout_item);

            ViewGroup.LayoutParams params = holder.layout.getLayoutParams();
            int w = App.windowWidth - Utils.dip2px(mContext, 24);
            params.width = w;
            params.height = w / 3;
            holder.layout.setLayoutParams(params);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.water_name.setText(waterInfo.getGoodsName());
        holder.coupon_money.setText("返" + waterInfo.getCouponAmount() + "元代金券");
        holder.water_num.setText("剩余：" + waterInfo.getWaterCount() + "桶");
        holder.water_money.setText(waterInfo.getTicketPrice() + "元");
        holder.layout.setTag(waterInfo);
        return convertView;
    }

    static class ViewHolder {
        TextView water_name;
        TextView coupon_money;
        TextView water_money;
        TextView water_num;
        RelativeLayout layout;
    }
}
