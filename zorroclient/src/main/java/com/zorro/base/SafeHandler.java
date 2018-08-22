package com.zorro.base;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public abstract class SafeHandler<T> extends Handler {
    private final WeakReference<T> ref;

    public abstract void handleMessage(T t, Message message);

    public SafeHandler(T ref) {
        this.ref = new WeakReference(ref);
    }

    public final void handleMessage(Message msg) {
        T t = this.ref.get();
        if (t != null) {
            handleMessage(t, msg);
        }
    }
}
