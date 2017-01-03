package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;

import java.util.List;

/**
 * @author zxl
 * @ClassName: ExpandAdapter
 * @Description: expandableListView
 * @date 2015年7月20日 下午2:23:31
 */
public class FAQExpandAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> group;
    private List<List<String>> child;
    private LayoutInflater inflater;

    public FAQExpandAdapter(Context context, List<String> group,
                            List<List<String>> child) {
        this.context = context;
        this.group = group;
        this.child = child;
        inflater = LayoutInflater.from(context);
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

    /***
     * group
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewParentHolder parentHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_parent, null);
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

    /***
     * child
     */
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

        holder.childTextView.setVisibility(View.VISIBLE);
        holder.childImageViw.setVisibility(View.GONE);
        holder.childTextView.setText(child.get(groupPosition).get(childPosition));
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
