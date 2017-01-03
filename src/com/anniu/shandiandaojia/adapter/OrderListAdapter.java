package com.anniu.shandiandaojia.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.activity.MyOrderActivity;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.db.jsondb.OrderInfo;
import com.anniu.shandiandaojia.db.jsondb.OrderStatus;
import com.anniu.shandiandaojia.db.jsondb.ShopInfo;
import com.anniu.shandiandaojia.logic.OrderLogic;
import com.anniu.shandiandaojia.utils.MyToast;
import com.anniu.shandiandaojia.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * @author YY
 * @ClassName: OrderListAdapter
 * @Description: 我的订单查询适配器
 * @date 2015年6月3日 下午3:10:05
 */
public class OrderListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<OrderInfo> mlist;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    private PopupWindow popupWindow;// 弹窗
    private String times[] = {"确认收货时间", "10分钟", "20分钟", "30分钟", "40分钟",
            "50分钟", "60分钟", "70分钟", "80分钟", "90分钟"};
    private String cause[] = {"您确定取消该订单", "个人信息填写错误", "选错商品", "长时间未配送", "其他", "返回"};
    private String receivingtime;
    private String cancelCanse;

    public OrderListAdapter(Context context, ArrayList<OrderInfo> list) {
        super();
        this.context = context;
        this.mlist = list;
        mImageLoader = App.initImageLoader();
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
                .showStubImage(R.drawable.no_title_bg)//加载过程中显示的图像，可以不设置
                .showImageForEmptyUri(R.drawable.no_title_bg) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.no_title_bg) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                .build();

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
        OrderInfo orderInfo = mlist.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order_status, null);
            holder.shopImg = (ImageView) convertView.findViewById(R.id.iv_shop_img);
            holder.shopName = (TextView) convertView.findViewById(R.id.tv_shop_name);
            holder.totalPrice = (TextView) convertView.findViewById(R.id.tv_total_price);
            holder.orderTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.orderStatus = (TextView) convertView.findViewById(R.id.tv_order_status);
            holder.confirmation = (TextView) convertView.findViewById(R.id.tv_confirmation);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ShopInfo shop = orderInfo.getShop();
        mImageLoader.displayImage(shop.getPictureAddress(), holder.shopImg, options);
        holder.shopName.setText(shop.getName());
        holder.totalPrice.setText(orderInfo.getPaymentAmount() + "元");
        String time = Utils.getTimeFormatStr3(orderInfo.getCreateTime());//将事件转换为年月日格式
        holder.orderTime.setText(time);

        OrderStatus status = orderInfo.getStatus();
        String textConfirm;
        String textState;
        if (status.equals(OrderStatus.已提交)) {
            holder.confirmation.setVisibility(View.VISIBLE);
            textConfirm = "取消订单";
            textState = "已提交";
            holder.confirmation.setTextColor(context.getResources().getColor(R.color.line_color));
            holder.confirmation.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.goods_receipt_cancel));
            holder.confirmation.setTag(orderInfo.getId());
            holder.confirmation.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int num = (int) v.getTag();
                    /**取消原因弹窗*/
                    showCansePopuWindows(num);
                }
            });
        } else if (status.equals(OrderStatus.发货中)) {
            holder.confirmation.setVisibility(View.VISIBLE);
            holder.confirmation.setTextColor(context.getResources().getColor(R.color.title_color));
            holder.confirmation.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.goods_receipt_default));
            textConfirm = "确认收货";
            textState = "发货中";
            holder.confirmation.setTag(orderInfo.getId());
            holder.confirmation.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int num = (int) v.getTag();
                    /**确定时间弹窗*/
                    showTimePopuWindows(num);
                }
            });
        } else if (status.equals(OrderStatus.已完成)) {
            holder.confirmation.setVisibility(View.GONE);
            textConfirm = "";
            textState = "已完成";
        } else if (status.equals(OrderStatus.老板已取消)) {
            holder.confirmation.setVisibility(View.GONE);
            textConfirm = "";
            textState = "已取消";
        } else if (status.equals(OrderStatus.用户已取消)) {
            holder.confirmation.setVisibility(View.GONE);
            textConfirm = "";
            textState = "已取消";
        } else {
            holder.confirmation.setVisibility(View.VISIBLE);
            holder.confirmation.setTextColor(context.getResources().getColor(R.color.title_color));
            holder.confirmation.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.goods_receipt_default));
            textConfirm = "去支付";
            textState = "待支付";
            holder.confirmation.setTag(orderInfo);
            holder.confirmation.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderInfo order = (OrderInfo) v.getTag();
                    getWXPrepayidAction(order.getId());
                }
            });
        }
        holder.confirmation.setText(textConfirm);
        holder.orderStatus.setText(textState);

        return convertView;
    }

    /**
     * 取消订单弹窗
     *
     * @param orderNum
     */
    protected void showCansePopuWindows(final int orderNum) {
        MyOrderActivity activity = (MyOrderActivity) context;
        View view = activity.getLayoutInflater().inflate(R.layout.group_list, null, true);
        ListView lsitview = (ListView) view.findViewById(R.id.lv_list);

        lsitview.setAdapter(new ArrayAdapter<String>(context, R.layout.item_group, R.id.tv_text, cause));
        lsitview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 关闭popupWindow
                if (cause[position].equals("您确定取消该订单")) {
                    MyToast.show(context, "请选择取消订单原因！");
                    return;
                }
                if (cause[position].equals("返回")) {
                    popupWindow.dismiss();
                    popupWindow = null;
                } else {
                    cancelCanse = cause[position];
                    /**点击发送取消订单的请求*/
                    cancelSendAction(orderNum);
                    popupWindow.dismiss();
                    popupWindow = null;
                }
            }
        });
        view.setFocusableInTouchMode(true);
        popupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.style_ppw);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    /**
     * 订单状态只有当显示未收货时，才显示确认收货按钮
     */
    static class ViewHolder {
        ImageView shopImg;
        TextView shopName;
        TextView totalPrice;
        TextView orderTime;
        TextView orderStatus;
        TextView confirmation;
    }

    public void addData(ArrayList<OrderInfo> list) {
        mlist = list;
        notifyDataSetChanged();
    }

    /**
     * 确认收货时间弹窗
     */
    private void showTimePopuWindows(final int orderNum) {
        MyOrderActivity activity = (MyOrderActivity) context;
        View view = activity.getLayoutInflater().inflate(R.layout.group_list, null);
        ListView lsitview = (ListView) view.findViewById(R.id.lv_list);
        lsitview.setAdapter(new ArrayAdapter<String>(context, R.layout.item_group, R.id.tv_text, times));
        lsitview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (times[arg2].equals("确认收货时间")) {
                    MyToast.show(context, "请选择收货时间！");
                    return;
                }
                receivingtime = times[arg2];
                popupWindow.dismiss();
                popupWindow = null;
                /**点击发送确认订单的请求*/
                completeSendAction(orderNum);
            }
        });
        view.setFocusableInTouchMode(true);
        popupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.style_ppw);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.showAtLocation(view, Gravity.CENTER | Gravity.CENTER, 0, 0);
    }

    /**
     * 点击发送确认订单的请求
     */
    private void completeSendAction(int orderNum) {
        Intent intent = new Intent(OrderLogic.ACTION_GET_CONFIRM_ORDER);
        intent.putExtra(OrderLogic.EXTRA_ORDER_NUM, orderNum);
        intent.putExtra(OrderLogic.EXTRA_RECEIVING, receivingtime);
        App.getInstance().sendAction(intent);
        ((MyOrderActivity) context).loadingView.setVisibility(View.VISIBLE);
    }

    /**
     * 点击发送取消订单的请求
     */
    private void cancelSendAction(int orderNum) {
        Intent intent = new Intent(OrderLogic.ACTION_POST_CANCEL_ORDER);
        intent.putExtra(OrderLogic.EXTRA_ORDER_NUM, orderNum);
        intent.putExtra(OrderLogic.EXTRA_CANCELCANSE, cancelCanse);
        App.getInstance().sendAction(intent);
        ((MyOrderActivity) context).loadingView.setVisibility(View.VISIBLE);
    }

    /**
     * 获取微信的prepayid
     */
    private void getWXPrepayidAction(int ordernum) {
        Intent intent = new Intent(OrderLogic.ACTION_GET_PREPAYID_ADAPTER);
        intent.putExtra(OrderLogic.EXTRA_ORDERNUM, ordernum);
        App.getInstance().sendAction(intent);
        ((MyOrderActivity) context).loadingView.setVisibility(View.VISIBLE);
    }
}
