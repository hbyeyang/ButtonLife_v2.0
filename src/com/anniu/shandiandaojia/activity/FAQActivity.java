package com.anniu.shandiandaojia.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.adapter.FAQExpandAdapter;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.utils.Utils;
import com.anniu.shandiandaojia.view.MyExpandableListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YY
 * @ClassName: FAQActivity
 * @Description: 常见问题
 * @date 2015年5月30日 下午7:18:48
 */
public class FAQActivity extends BaseActivity {
    private TextView titleBarTv;// 界面标题
    private MyExpandableListView listview;
    private FAQExpandAdapter adapter;
    private List<String> group;
    private List<List<String>> child;

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            return;
        }
        if (v.getId() == R.id.title_bar_left) {
            finish();
        }
    }

    @Override
    protected void init(Bundle saveInstanceState) {
        setContentView(R.layout.activity_question);
        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText("常见问题");
        findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        findViewById(R.id.title_bar_left).setOnClickListener(this);

        listview = (MyExpandableListView) findViewById(R.id.elv);
        listview.setGroupIndicator(null);
        initData();
        adapter = new FAQExpandAdapter(this, group, child);
        listview.setAdapter(adapter);
        listview.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < adapter.getGroupCount(); i++) {
                    if (groupPosition != i) {
                        listview.collapseGroup(i);
                    }
                }
            }
        });
    }

    private void initData() {
        group = new ArrayList<String>();
        child = new ArrayList<List<String>>();
        addInfo("1.闪店到家的客服帮助", new String[]{
                "(1)客服服务", "客服电话：4008885708", "电话服务时间：周一至周五09:00--17:30",
                "(2)商务合作", "邮箱：cuitao@buttonad.com", "电话：15321151001",});
        addInfo("2.如何在闪店到家购买商品？", new String[]{
                "(1)打开闪店到家客户端，选择商品，并通过“+”“-”添加商品数量，加入购物车；",
                "(2)进入购物车页面添加收货地址，收货时间，再次确认所选商品信息，点击【选好了】；",
                "(3)进入订单结算页，选择优惠券，支付方式，确认付款金额，点击【确认付款】完成下单。"
        });
        addInfo("3.闪店到家的支付方式有哪些？", new String[]{
                "“闪店到家”支持两种支付方式：货到付款和在线支付。实际支持的支付方式请以订单结算页面提示的支付方式为准。",
                "(1)货到付款",
                "“闪店到家”在可配送范围内均可支持货到付款，货到付款订单目前只支持现金支付方式，给您带来的不变，敬请谅解。",
                "(2)在线支付",
                "您可以使用“微信支付”“支付宝支付”进行在线支付。",
                "温馨提示：在支付过程中，务必要核对金额，选择对应的支付方式进行在线支付。"
        });
        addInfo("4.闪店到家配送须知", new String[]{
                "(1)配送费",
                "用户购买“闪店到家”商品，按“订单结算页--配送费”一栏提示的运费标准收取，不同的店铺或商品收取的标准存在一定差异，一般存在情形如下：",
                "①一般情况下，“闪店到家”商品订单金额30元起送，免运费，小于30元则无法支付。",
                "②促销活动商品或其他特殊商品，特殊情况的请以页面公告活订单结算页为准。",

                "温馨提示：配送费的收取标准随着“闪店到家”业务的发展可能进行相应调整，请大家及时关注闪店到家页面公告信息。",

                "(2)配送时间",
                "闪店到家通过闲置人员的循环利用，以及精确的定位，半小时为您送货到家。",
                "(3)配送范围",
                "“闪店到家”业务主要在指定门店3公里范围内，大家注意查看位置信息哦！"
        });
        addInfo("5.如何查询历史订单？", new String[]{
                "点击首页右上角的个人中心图标，之后点击“订单”，即可查询全部历史订单。"
        });
        addInfo("6.退货服务", new String[]{
                "依据《中华人民共和国产品质量法》，《中华人民共和国消费者权益保护法》等相关法规，“闪店到家”承诺对销售存在质量问题的商品办理退换货服务。 水果，蔬菜，水产，肉类等生鲜易腐食品，请您签收前进行商品检查，此类商品一经签收将不予办理退换货。 请保管好需办理售后服务的业务商品及其包装，票据和赠品作为办理相关业务的依据，退换货时，需要先于“闪店到家”客服中心取得确认。"
        });
    }

    /**
     * 添加数据信息
     */
    private void addInfo(String g, String[] c) {
        group.add(g);
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < c.length; i++) {
            list.add(c[i]);
        }
        child.add(list);
    }

    @Override
    protected void onUIEvent(int eventId, Bundle bundle) {

    }

    @Override
    protected void addEventListener() {

    }

    @Override
    protected void removeListener() {

    }

}
