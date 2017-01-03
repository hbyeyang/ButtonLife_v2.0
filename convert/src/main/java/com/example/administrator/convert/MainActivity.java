package com.example.administrator.convert;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {
    EditText px;
    EditText dip;
    TextView result;
    Button button, button1;
    int width;  // 宽度（PX）
    int height;  // 高度（PX）
    float density;  // 密度（0.75 / 1.0 / 1.5）
    int densityDpi;  // 密度DPI（120 / 160 / 240）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  //设置全屏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); //设置没有标题

        setContentView(R.layout.activity_main);

        px = (EditText) findViewById(R.id.et1);
        dip = (EditText) findViewById(R.id.et2);
        result = (TextView) findViewById(R.id.tv3);
        button = (Button) findViewById(R.id.button);
        button1 = (Button) findViewById(R.id.button1);
        button.setOnClickListener(this);
        button1.setOnClickListener(this);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        width = metric.widthPixels;
        height = metric.heightPixels;
        density = metric.density;
        densityDpi = metric.densityDpi;

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            float tempPx = (float) Double.parseDouble(px.getText().toString());
            if (TextUtils.isEmpty(tempPx + "")) {
                return;
            }
            int tempdip = px2dip(this, tempPx);
            result.setText(
                    "width=" + width + "\n" +
                            "height=" + height + "\n" +
                            "density=" + density + "\n" +
                            "densityDpi=" + densityDpi + "\n" +
                            tempdip + "dip");
        } else {
            float tempDip = (float) Double.parseDouble(dip.getText().toString());
            if (TextUtils.isEmpty(tempDip + "")) {
                return;
            }
            int temppx = dip2px(this, tempDip);
            result.setText(
                    "width=" + width + "\n" +
                            "height=" + height + "\n" +
                            "density=" + density + "\n" +
                            "densityDpi=" + densityDpi + "\n"
                            + temppx + "px");
        }
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
