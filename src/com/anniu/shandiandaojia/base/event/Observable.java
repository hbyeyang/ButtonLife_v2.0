package com.anniu.shandiandaojia.base.event;

import com.anniu.shandiandaojia.utils.MyLog;

import java.util.ArrayList;


// 从java.util.Observable拷贝过来的, 修改了一下
/**
 * 被观察者
 * 
 */
public class Observable {
	private final MultiHashMap<Integer, Observer> observers = new MultiHashMap<Integer, Observer>(12);

	public Observable() {
	}

	/**
	 * 或取对eventId这个消息监听的数目
	 * 
	 * @param eventId
	 * @return
	 */
	public int getListenerCount(int eventId) {
		ArrayList<Observer> ls = observers.get(eventId);
		if (ls != null) {
			return ls.size();
		}
		return 0;
	}

	/**
	 * 是否有对eventId这个消息的监听
	 * 
	 * @param eventId
	 * @return
	 */
	public boolean hasListener(int eventId) {
		return getListenerCount(eventId) > 0;
	}

	/**
	 * 添加一个对eventId消息的监听.
	 * 
	 * @param eventId
	 * @param listener
	 */
	synchronized public void addListener(int eventId, Observer listener) {
		observers.put(eventId, listener);
	}

	/**
	 * 清除eventId之前的所有监听, 添加新监听listener
	 * 
	 * @param eventId
	 * @param listener
	 */
	synchronized public void setListener(int eventId, Observer listener) {
		observers.remove(eventId);
		observers.put(eventId, listener);
	}

	public int size() {
		return observers.size();
	}

	public synchronized void remove(int eventId, Observer observer) {
		observers.remove(eventId, observer);
	}

	public synchronized void remove(Observer observer) {
		observers.removeValue(observer);
	}

	public synchronized void clear() {
		observers.clear();
	}

	/**
	 * 激发一个广播,通知所有监听者,
	 * 
	 * @param sender
	 * 谁激发的, 可以是null, 由监听者和被监听者协商
	 * @param eventId
	 * 事件类型, 标识事件
	 * @param args
	 * 自定义的参数
	 */
	public void notify(Object sender, int eventId, Object... args) {
		synchronized (this) {
			ArrayList<Observer> ls = observers.get(eventId);
			if (ls != null) {
				Observer[] arrays = new Observer[ls.size()];
				ls.toArray(arrays);
				for (Observer observer : arrays) {
					if (observer != null) {
						try {
                            observer.onNotify(eventId, args);
						} catch (Exception e) {
                            MyLog.e(this.getClass().getSimpleName(),
                                    e.toString());
						}
					}
				}
			}
		}
	}

	public void notify(int eventId, Object... args) {
		notify(null, eventId, args);
	}
}
