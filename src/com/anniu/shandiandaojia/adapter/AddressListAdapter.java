package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.activity.ModifyAddress;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.db.jsondb.MyAddress;
import com.anniu.shandiandaojia.logic.UserLogic;
import com.anniu.shandiandaojia.utils.Utils;

import java.util.ArrayList;

/**
 * @author YY
 * @ClassName: AddressListAdapter
 * @Description: 我的地址适配器
 * @date 2015年6月8日 下午12:48:49
 */
public class AddressListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MyAddress> mlist;
    public int user_loc_code;
    private LayoutInflater inflater;

    public AddressListAdapter(Context context, ArrayList<MyAddress> list) {
        super();
        this.mContext = context;
        this.mlist = list;
        inflater = LayoutInflater.from(mContext);
    }

    public int getUser_loc_code() {
        return user_loc_code;
    }

    public void setUser_loc_code(int user_loc_code) {
        this.user_loc_code = user_loc_code;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        MyAddress address = mlist.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_my_address, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.tv_checked = (TextView) convertView.findViewById(R.id.ischeck);
            holder.tv_modify = (TextView) convertView.findViewById(R.id.tv_modify);
            holder.rl_icon = (RelativeLayout) convertView.findViewById(R.id.rl_icon);
            holder.rl_icon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utils.isFastClick()) {
                        return;
                    }
                    int code = (Integer) v.getTag();
                    Intent intent = new Intent(UserLogic.ACTION_GET_PERSON_INSERTADDR);
                    intent.putExtra(UserLogic.EXTRA_ADDRESS_ID, code);
                    App.getInstance().sendAction(intent);
                }
            });

            holder.tv_checked.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utils.isFastClick()) {
                        return;
                    }
                    int code = (Integer) v.getTag();
                    Intent intent = new Intent(UserLogic.ACTION_GET_PERSON_INSERTADDR);
                    intent.putExtra(UserLogic.EXTRA_ADDRESS_ID, code);
                    App.getInstance().sendAction(intent);
                }
            });

            holder.tv_modify.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (Utils.isFastClick()) {
                        return;
                    }
                    MyAddress address = (MyAddress) v.getTag();
                    Intent intent = new Intent(mContext, ModifyAddress.class);
                    intent.putExtra(UserLogic.EXTRA_ADDRESS_ID, address);
                    mContext.startActivity(intent);
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        boolean defaultCode = address.isDefault();
        if (defaultCode) {
            holder.tv_checked.setBackgroundResource(R.drawable.sex_ischecked);
        } else {
            holder.tv_checked.setBackgroundResource(R.drawable.sex_default);
        }

        holder.tv_name.setText(address.getContactName());
        holder.tv_phone.setText(address.getContactTel());
        String district = address.getConsignDistrict();
        if (TextUtils.isEmpty(district)) {
            district = "";
        }
        holder.tv_address.setText(district + address.getConsignAddress());

        holder.tv_modify.setTag(address);
        holder.tv_checked.setTag(address.getId());
        holder.rl_icon.setTag(address.getId());
        return convertView;
    }

    static class ViewHolder {
        TextView tv_checked;
        TextView tv_name;
        TextView tv_phone;
        TextView tv_address;
        TextView tv_modify;
        RelativeLayout rl_icon;
    }

    public void addData(ArrayList<MyAddress> list) {
        mlist = list;
        notifyDataSetChanged();
    }

}
