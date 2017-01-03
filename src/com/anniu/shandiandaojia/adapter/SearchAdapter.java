package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.db.jsondb.Goods;

import java.util.ArrayList;
/**
 * 
 * @ClassName:  SearchAdapter
 * @Description: 展示搜索商品
 * @author YY
 * @date 2015年7月3日 上午10:25:19
 */
public class SearchAdapter extends BaseAdapter {
	
	private ArrayList<Goods> lists;
	private Context mContext;

	public SearchAdapter(ArrayList<Goods> goodsList,Context context) {
		super();
		this.lists = goodsList;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public View getView(final int position, View convertView,ViewGroup parent) {
		ViewHolder holder;
		String goods_name = lists.get(position).getName();
		if (convertView == null) {
			convertView = View.inflate(mContext,R.layout.item_search, null);
			holder = new ViewHolder();
			holder.tv_hots = (TextView) convertView.findViewById(R.id.tv_des);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_hots.setText(goods_name);
		return convertView;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	static class ViewHolder{
		TextView tv_hots;
	}

}
