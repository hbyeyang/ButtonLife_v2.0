package com.anniu.shandiandaojia.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.anniu.shandiandaojia.db.jsondb.PrepayIdInfo;
import com.anniu.shandiandaojia.utils.Utils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class AppRegister extends BroadcastReceiver {

	private PrepayIdInfo info;

	@Override
	public void onReceive(Context context, Intent intent) {
		final IWXAPI api = WXAPIFactory.createWXAPI(context, null);
		info = Utils.getPrepayIdInfo();
		api.registerApp(info.getAppid());
	}
}
