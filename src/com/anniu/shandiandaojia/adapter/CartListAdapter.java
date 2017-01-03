package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.db.jsondb.CartGoods;
import com.anniu.shandiandaojia.view.AddAndSubView;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
* @ClassName: CartListAdapter 
* @Description: 商品列表适配器 
* @author zxl 
* @date 2015年6月8日 下午8:54:26
 */
public class CartListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context mContext;
	private ArrayList<CartGoods> mList;
	public int changecode;
	public CartListAdapter(Context context, ArrayList<CartGoods> goods) {
		super();
		this.mContext = context;
		this.mList = goods;
		mInflater = LayoutInflater.from(mContext);
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
		ViewHolder holder ;
		CartGoods goods = mList.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_cart_goods, null);
			holder.goodsName = (TextView) convertView.findViewById(R.id.tv_goods_name);
			holder.goodsPrice = (TextView) convertView.findViewById(R.id.tv_goods_price);
			holder.addSub = (AddAndSubView) convertView.findViewById(R.id.addSub);
			holder.addSub.setViewsLayoutParm(120, 35);
			holder.addSub.setButtonLayoutParm(35, 35);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		double price = goods.getUnitPrice();
		BigDecimal bdPrice = new BigDecimal(price);
		bdPrice = bdPrice.setScale(2, BigDecimal.ROUND_DOWN);
		
		holder.goodsName.setText(goods.getGoodsName());
		holder.goodsPrice.setText("￥" + bdPrice);
		holder.addSub.setNum(goods.getGoodsCount());
		holder.addSub.setLimited(goods.getLimitCount());
		holder.addSub.setGoodsId(goods.getShopGoodsId());
		return convertView;
	}

	class ViewHolder {
		TextView goodsName;
		TextView goodsPrice;
		AddAndSubView addSub;
	}

	public void addData(ArrayList<CartGoods> list) {
		mList = list;
		notifyDataSetChanged();
	}
}
