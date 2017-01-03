package com.anniu.shandiandaojia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.base.BaseActivity;
import com.anniu.shandiandaojia.utils.Utils;

/**
 * @author zxl
 * @ClassName: EventsActivity
 * @Description: 活动页面
 * @date 2015年7月14日 上午11:55:28
 */
public class EventsActivity extends BaseActivity {
    private TextView titleBarTv;// 界面标题
    private WebView web_view;
    public static String EXTRA_TAYENAME = "type_name";
    public static String EXTRA_TAYEURL = "type_url";

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
        setContentView(R.layout.activity_events);

        Intent intent = getIntent();
        String name = intent.getExtras().getString(EXTRA_TAYENAME);
        String url = intent.getExtras().getString(EXTRA_TAYEURL);

        titleBarTv = (TextView) findViewById(R.id.title_bar_tv);
        titleBarTv.setText(name);

        findViewById(R.id.iv_logo).setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_back));
        findViewById(R.id.title_bar_left).setOnClickListener(this);

        web_view = (WebView) findViewById(R.id.web_view);
        // 加载网络资源
        web_view.loadUrl(url);

        // 启用支持javascript
        WebSettings settings = web_view.getSettings();
        settings.setJavaScriptEnabled(true);
        //新添加代码
        web_view.removeJavascriptInterface("searchBoxJavaBridge_");
        web_view.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        web_view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
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
