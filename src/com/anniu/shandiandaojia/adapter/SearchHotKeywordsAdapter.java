package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.db.jsondb.HotWords;

import java.util.List;

public class SearchHotKeywordsAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<HotWords> listItem;
	private Context mContext;
	
	public SearchHotKeywordsAdapter(Context context,
			List<HotWords> hotKeyList) {
		super();
		this.listItem = hotKeyList;
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}
	
	@Override
	public int getCount() {
		return listItem.size();
	}

	@Override
	public Object getItem(int position) {
		return listItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_hot_keyword, null);
			holder.keyword = (TextView) convertView.findViewById(R.id.keyword_text);
			convertView.setTag(holder); 
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		if (listItem != null && listItem.size() > 0) {
			holder.keyword.setText(listItem.get(position).getName());
		}
		return convertView;
	}

	static class ViewHolder {
		public TextView keyword;
	}
	
	public void setData(List<HotWords> list){
		listItem = list;
		notifyDataSetChanged();
	}
}
