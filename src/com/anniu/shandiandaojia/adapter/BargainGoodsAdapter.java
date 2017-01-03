package com.anniu.shandiandaojia.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.activity.RegistActivity;
import com.anniu.shandiandaojia.app.App;
import com.anniu.shandiandaojia.db.jsondb.Goods;
import com.anniu.shandiandaojia.logic.ShoppingCartLogic;
import com.anniu.shandiandaojia.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

/**
 * @author zxl
 * @ClassName: BargainGoodsAdapter
 * @Description: 特价商品
 * @date 2015年6月2日 下午7:40:26
 */
public class BargainGoodsAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<Goods> mList;
    public ImageLoader mImageLoader;
    private DisplayImageOptions options;

    public BargainGoodsAdapter(Context context, ArrayList<Goods> goods) {
        super();
        this.mContext = context;
        this.mList = goods;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = App.initImageLoader();
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
                .showStubImage(R.drawable.no_data)//加载过程中显示的图像，可以不设置
                .showImageForEmptyUri(R.drawable.no_data) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.no_data) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();
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
        final ViewHolder holder;
        Goods goods = mList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_bargain_goods, null);
            holder.rlPrice = (RelativeLayout) convertView.findViewById(R.id.rl_price);
            holder.shopImg = (ImageView) convertView.findViewById(R.id.iv_shop_img);

            LayoutParams imgParams = holder.shopImg.getLayoutParams();
            imgParams.height = App.windowWidth / 3;
            imgParams.width = App.windowWidth / 3;
            holder.shopImg.setLayoutParams(imgParams);

            holder.goodsName = (TextView) convertView.findViewById(R.id.tv_goods_name);
            holder.goodsPrice = (TextView) convertView.findViewById(R.id.tv_goods_price);
            holder.promotePrice = (TextView) convertView.findViewById(R.id.tv_promote_price);
            holder.add = (ImageView) convertView.findViewById(R.id.iv_add);
            holder.add.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (Utils.isFastClick()) {
                        return;
                    }
                    if (Utils.loginState()) {
                        Goods good = (Goods) v.getTag();
                        sendAddGood2CartAction(good.getId());
                    } else {
                        mContext.startActivity(new Intent(mContext, RegistActivity.class));
                    }
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        mImageLoader.displayImage(goods.getPictureUrl(), holder.shopImg, options);
        holder.goodsName.setText(goods.getName());

        if (goods.getOnSale()) {//特价
            holder.rlPrice.setVisibility(View.VISIBLE);
            holder.goodsPrice.setText("￥" + goods.getOriginalPrice());
            holder.promotePrice.setVisibility(View.VISIBLE);
            holder.promotePrice.setText("￥" + goods.getPrice());
            holder.goodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.rlPrice.setVisibility(View.GONE);
            holder.goodsPrice.setText("￥" + goods.getPrice());
            holder.promotePrice.setVisibility(View.INVISIBLE);
        }
        holder.add.setTag(goods);
        return convertView;
    }

    class ViewHolder {
        RelativeLayout rlPrice;
        ImageView shopImg;
        TextView goodsName;
        TextView goodsPrice;
        TextView promotePrice;
        ImageView add;
    }

    public void addData(ArrayList<Goods> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void sendAddGood2CartAction(int id) {
        Intent intent = new Intent(ShoppingCartLogic.ACTION_INSERTORUPDATECARTGOODNUM);
        intent.putExtra(ShoppingCartLogic.EXTRA_ID, id);
        intent.putExtra(ShoppingCartLogic.EXTRA_COUNT, 0);
        intent.putExtra(ShoppingCartLogic.EXTRA_CMD, "PUSH");
        App.getInstance().sendAction(intent);
    }


    /**
     * 动画播放时间
     */
    private int AnimationDuration = 3000;

    private ViewGroup anim_mask_layout;

    private void setAnim(View imgIcon) {
        anim_mask_layout = createAnimLayout();
        Animation mScaleAnimation = new ScaleAnimation(1.5f, 0.1f, 1.5f, 0.1f, Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_SELF, 0.1f);
        mScaleAnimation.setDuration(AnimationDuration);
        mScaleAnimation.setFillAfter(true);

        int[] start_location = new int[2];
        imgIcon.getLocationInWindow(start_location);
        ViewGroup vg = (ViewGroup) imgIcon.getParent();
        vg.removeView(imgIcon);
        // 将组件添加到我们的动画层上
        View view = addViewToAnimLayout(anim_mask_layout, imgIcon, start_location);
        int[] end_location = new int[2];

//        ((MainActivity) mContext).ivCart.getLocationInWindow(end_location);
        // 计算位移
        int endX = end_location[0];
        int endY = end_location[1] - start_location[1];

        Animation mTranslateAnimation = new TranslateAnimation(0, endX, 0, endY);// 移动
        mTranslateAnimation.setDuration(AnimationDuration);

        AnimationSet mAnimationSet = new AnimationSet(false);
        // 这块要注意，必须设为false,不然组件动画结束后，不会归位。
        mAnimationSet.setFillAfter(false);
        mAnimationSet.addAnimation(mScaleAnimation);
        mAnimationSet.addAnimation(mTranslateAnimation);
        view.startAnimation(mAnimationSet);

        mTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                tvNumber.setText(goodsNumber+"");
            }
        });
    }

    private View addViewToAnimLayout(final ViewGroup vg, final View view,
                                     int[] location) {
        int x = location[0];
        int y = location[1];
        vg.addView(view);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setLayoutParams(lp);
        return view;
    }

    private ViewGroup createAnimLayout() {
        ViewGroup rootView = (ViewGroup) ((Activity) mContext).getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);
        animLayout.setLayoutParams(lp);
//        animLayout.setId(R.id.age);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }
}
