package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.app.App;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * @ClassName: ExpandAdapter
 * @Description: expandableListView
 * @author zxl
 * @date 2015年7月20日 下午2:23:31
 */
public class ExpandAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<String> group;
	private List<List<String>> child;
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;

	public ExpandAdapter(Context context, List<String> group,
			List<List<String>> child) {
		this.context = context;
		this.group = group;
		this.child = child;
		mImageLoader = App.initImageLoader();
		options = new DisplayImageOptions.Builder()
				.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
				 .showStubImage(R.drawable.no_data)//加载过程中显示的图像，可以不设置
				.showImageForEmptyUri(R.drawable.no_data) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.no_data) // 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				.build();
	}

	@Override
	public int getGroupCount() {
		return group.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return child.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return group.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return child.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	/*** group */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		ViewParentHolder parentHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_parent, null);
			parentHolder = new ViewParentHolder();
			parentHolder.parentTextView = (TextView) convertView.findViewById(R.id.parentText);
			parentHolder.parentImageViw = (ImageView) convertView.findViewById(R.id.arrow);
			convertView.setTag(parentHolder);
		} else {
			parentHolder = (ViewParentHolder) convertView.getTag();
		}
		
		parentHolder.parentTextView.setText(group.get(groupPosition));
		if (isExpanded) {
			parentHolder.parentImageViw.setBackgroundResource(R.drawable.water_arrow_up);
		} else {
			parentHolder.parentImageViw.setBackgroundResource(R.drawable.water_arrow_down);
		}
		return convertView;
	}

	/*** child */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item, null);
			holder = new ViewHolder();
			holder.childTextView = (TextView) convertView.findViewById(R.id.childText);
			holder.childImageViw = (ImageView) convertView.findViewById(R.id.childImage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (groupPosition == 2) {
			holder.childTextView.setVisibility(View.GONE);
			holder.childImageViw.setVisibility(View.VISIBLE);
			mImageLoader.displayImage(child.get(groupPosition).get(childPosition),holder.childImageViw, options);
		} else {
			holder.childTextView.setVisibility(View.VISIBLE);
			holder.childImageViw.setVisibility(View.GONE);
			holder.childTextView.setText(child.get(groupPosition).get(childPosition));
		}
		
		return convertView;
	}

	class ViewHolder {
		TextView childTextView;
		ImageView childImageViw;
	}

	class ViewParentHolder {
		TextView parentTextView;
		ImageView parentImageViw;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
	
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
