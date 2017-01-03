package com.anniu.shandiandaojia.net;

import com.anniu.shandiandaojia.net.bean.HttpReq;
import com.anniu.shandiandaojia.net.bean.HttpRsp;
import com.anniu.shandiandaojia.net.bean.SocketReq;
import com.anniu.shandiandaojia.net.bean.SocketRsp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * http和socket管理模块，只负责维护网络连接，收发数据
 */
public class NetMgr {
	private ExecutorService httpThreadPool;
	private ExecutorService socketSendPool;
	private ExecutorService socketRecvPool;
	private HttpClient httpClient;
	private SocketClient socketClient;

	public NetMgr() {
		httpThreadPool = Executors.newFixedThreadPool(5);
		socketSendPool = Executors.newFixedThreadPool(1);
		socketRecvPool = Executors.newFixedThreadPool(1);
		httpClient = new HttpClient();
		socketClient = new SocketClient();
	}

	public void sendHttpReq(final HttpReq req) {

		if (null != req) {
			Runnable sendThread = new Runnable() {

				@Override
				public void run() {
					if (req.isHttpPost()) {
						httpClient.sendPost(req);
					} else {
						httpClient.sendReq(req);
					}
				}
			};

			httpThreadPool.execute(sendThread);
		}
	}

	public void sendSocketReq(SocketReq req) {
		// socketClient.sendReq(req);
	}

	public interface OnHttpRsp {
		void onHttpRsp(HttpRsp httpRsp);
	}

	public interface OnSocketRsp {
		 void onSocketRsp(SocketRsp rsp);
	}

}
