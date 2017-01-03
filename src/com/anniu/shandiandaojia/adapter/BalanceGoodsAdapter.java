package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.db.jsondb.CartGoods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/***
 * @author zxl
 * @ClassName: BalanceGoodsAdapter
 * @Description: 结算页面商品适配器
 * @date 2015年7月16日 下午4:05:59
 */
public class BalanceGoodsAdapter extends BaseAdapter {
    private List<CartGoods> goodsList = new ArrayList<CartGoods>();
    private Context mContext;
    private LayoutInflater mInflater;

    public BalanceGoodsAdapter(Context context, List<CartGoods> cargoodsList) {
        super();
        this.goodsList = cargoodsList;
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return goodsList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return goodsList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        CartGoods cartGoods = (CartGoods) goodsList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_goods_name_num_money, null);
            holder.tv_goodsName = (TextView) convertView.findViewById(R.id.tv_order_goods_name);
            holder.tv_goodsNum = (TextView) convertView.findViewById(R.id.tv_order_goods_num);
            holder.tv_goodsMoney = (TextView) convertView.findViewById(R.id.tv_order_goods_money);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        double money = cartGoods.getUnitPrice() * cartGoods.getGoodsCount();
        BigDecimal bdmoney = new BigDecimal(money);
        bdmoney = bdmoney.setScale(2, BigDecimal.ROUND_DOWN);
        holder.tv_goodsName.setText(cartGoods.getGoodsName());
        holder.tv_goodsNum.setText(cartGoods.getGoodsCount() + "");
        holder.tv_goodsMoney.setText("￥" + bdmoney);
        return convertView;
    }

    static class ViewHolder {
        TextView tv_goodsName;
        TextView tv_goodsNum;
        TextView tv_goodsMoney;
    }
}
