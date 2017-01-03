package com.anniu.shandiandaojia.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import com.anniu.shandiandaojia.logic.GoodsLogic;
import com.anniu.shandiandaojia.logic.LocationLogic;
import com.anniu.shandiandaojia.logic.OrderLogic;
import com.anniu.shandiandaojia.logic.ShoppingCartLogic;
import com.anniu.shandiandaojia.logic.TicketLogic;
import com.anniu.shandiandaojia.logic.UpdateLogic;
import com.anniu.shandiandaojia.logic.UserLogic;
import com.anniu.shandiandaojia.net.NetMgr;
import com.anniu.shandiandaojia.net.bean.HttpReq;
import com.anniu.shandiandaojia.net.bean.SocketReq;
import com.anniu.shandiandaojia.utils.MultiValueMap;
import com.anniu.shandiandaojia.utils.MyLog;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpService extends Service {

    protected static final String TAG = "BaseService";
    public static String ACTION_UPDATE_UI = "com.yopark.service.BaseService.Update_UI";
    public static String EXTRA_EVENT_ID = "Service_eventId";

    private NetMgr netMgr;
    private MultiValueMap<String, ActionListener> mResolver;
    private ExecutorService actionThreadPool;
    private UpdateLogic updateLogic;
    private LocationLogic locationLogic;
    private UserLogic userLogic;
    private GoodsLogic goodsLogic;
    private OrderLogic orderLogic;
    private ShoppingCartLogic shoppingCartLogic;
    private TicketLogic ticketLogic;

    public void sendHttpReq(HttpReq req) {
        netMgr.sendHttpReq(req);
    }

    public void sendSocketReq(SocketReq req) {
        netMgr.sendSocketReq(req);
    }

    @Override
    public void onCreate() {
        actionThreadPool = Executors.newFixedThreadPool(1);
        mResolver = new MultiValueMap<String, ActionListener>(8);
        netMgr = new NetMgr();
        updateLogic = new UpdateLogic(this);
        locationLogic = new LocationLogic(this);
        userLogic = new UserLogic(this);
        goodsLogic = new GoodsLogic(this);
        orderLogic = new OrderLogic(this);
        shoppingCartLogic = new ShoppingCartLogic(this);
        ticketLogic = new TicketLogic(this);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        actionThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    onHandleAction(intent);
                } catch (Exception t) {
                    MyLog.e(TAG, t.getMessage());
                }
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    public void onHandleAction(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            synchronized (mResolver) {
                if (!TextUtils.isEmpty(action) && mResolver.containsKey(action)) {
                    List<ActionListener> values = mResolver.get(action);
                    if (values != null) {
                        for (int i = 0; i < values.size(); i++) {
                            values.get(i).onHandleAction(intent);
                        }
                    }
                } else {
                    MyLog.e(TAG, "resolveAction.action = " + action + " is unregister !");
                }
            }
        }
    }

    public void sendBroadCast(int eventId, Bundle bundle) {
        Intent intent = new Intent(ACTION_UPDATE_UI);
        intent.putExtra(EXTRA_EVENT_ID, eventId);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        sendBroadcast(intent);
    }

    public void registerAction(ActionListener listener, List<String> actions) {
        if (listener != null && actions != null) {
            for (String action : actions) {
                if (action != null) {
                    mResolver.put(action, listener);
                }
            }
        }
    }

    public void unregisterAction(ActionListener listener, List<String> actions) {
        if (listener != null && actions != null) {
            for (String action : actions) {
                if (action != null && mResolver != null) {
                    mResolver.remove(action);
                }
            }
        }
    }

    public interface ActionListener {
        void onHandleAction(Intent intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
