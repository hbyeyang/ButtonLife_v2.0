package com.anniu.shandiandaojia.base.event;

public interface Observer {
    void onNotify(int eventId, Object... args);
}
