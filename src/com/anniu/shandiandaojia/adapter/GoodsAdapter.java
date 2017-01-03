package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.db.jsondb.OrderGoods;

import java.util.ArrayList;

/***
 * @author YY
 * @ClassName: GoodsAdapter
 * @Description: 订单详情中展示所购买商品的详情列表
 * @date 2015年7月3日 上午9:59:52
 */
public class GoodsAdapter extends BaseAdapter {
    private ArrayList<OrderGoods> goodsList;
    private Context mContext;

    public GoodsAdapter(ArrayList<OrderGoods> goodslist, Context context) {
        super();
        this.goodsList = goodslist;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return goodsList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        OrderGoods goods = goodsList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_goods_name_num_money, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.tvGoodsName = (TextView) convertView.findViewById(R.id.tv_order_goods_name);
            holder.tvGoodsNum = (TextView) convertView.findViewById(R.id.tv_order_goods_num);
            holder.tvGoodsMoney = (TextView) convertView.findViewById(R.id.tv_order_goods_money);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvGoodsName.setText(goods.getGoodsName());
        holder.tvGoodsNum.setText(goods.getGoodsCount() + "");
        holder.tvGoodsMoney.setText("￥" + goods.getGoodsPrice());
        return convertView;
    }

    static class ViewHolder {
        TextView tvGoodsName;
        TextView tvGoodsNum;
        TextView tvGoodsMoney;
    }
}
