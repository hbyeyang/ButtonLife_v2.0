package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.db.jsondb.HotWords;

import java.util.ArrayList;
/**
 * 
 * @ClassName:  HotsAdapter
 * @Description: 展示热词
 * @author YY
 * @date 2015年7月3日 上午10:18:05
 */
public class HotsAdapter extends BaseAdapter {
	
	private ArrayList<HotWords> mList;
	private Context mContext;
	private LayoutInflater mInflater;
	
	public HotsAdapter(ArrayList<HotWords> list,Context context){
		super();
		this.mList = list;
		this.mContext = context;
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
		ViewHolder holder = null;
		HotWords searchhots = mList.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.searchhots, null);
			holder.tv_hots = (TextView) convertView.findViewById(R.id.tv_hots);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_hots.setText(searchhots.getName());
		
		return convertView;
	}

	static class ViewHolder{
		TextView tv_hots;
	}
}
