package com.zorro.base;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.view.MenuItem;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class BaseActivity extends Activity {

    private static final String TAG = "Zorro";
    /**
     * 根据{@link #onResume()} {@link #onPause()} 方法调度情况获得Activity的显示状态
     */
    private AtomicBoolean visibility = new AtomicBoolean(false);

    private Service mService = null;
    private Messenger mMessenger = null;
    private ServiceConnection servConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = null;//TODO init service

            mMessenger = getMessenger();
            if (mMessenger != null) {
                try {
//                    mService.registerCallback(mMessenger);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            BaseActivity.this.onServiceConnected(mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            BaseActivity.this.onServiceDisconnected();
        }
    };

    public Service getService() {
        return mService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBindService();
        Log.d(TAG, "onCreate: !");
        Logger.getLogger("BaseActivity").info(this.getClass().getName()+".onCreate");
    }

    @Override
    protected void onDestroy() {
        doUnbindService();
        super.onDestroy();
    }

    @Override
    public void finish() {
        doUnbindService();
        super.finish();
    }

    @Override
    protected void onPause() {
        visibility.compareAndSet(true, false);
        super.onPause();
    }

    @Override
    protected void onResume() {
        visibility.compareAndSet(false, true);
        super.onResume();
    }

    /**
     * 获得Activity的显示状态(根据{@link #onResume()} {@link #onPause()} 方法调度情况获得)
     * @return
     */
    public final boolean isVisibility(){
        return visibility.get();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return false;
    }

    private void doBindService() {
        Intent intent = new Intent("");
        bindService(intent, servConn, Context.BIND_AUTO_CREATE);
    }

    private void doUnbindService() {
        if (mService != null && mMessenger != null) {
            try {
//                mService.unregisterCallback(mMessenger);
            } catch (Exception e) {
            }
            mMessenger = null;
        }
        if (servConn != null) {
            try {
                unbindService(servConn);
                servConn = null;
            } catch (Exception e) {
            }
        }
    }

    protected void onServiceConnected(Service service) {

    }

    protected void onServiceDisconnected() {
    }

    protected Messenger getMessenger() {
        return null;
    }

    protected void enableHomeButton(boolean enable) {
        ActionBar actionbar = getActionBar();
        if (actionbar != null) {
            actionbar.setHomeButtonEnabled(enable);
            actionbar.setDisplayHomeAsUpEnabled(enable);
        }

    }

}
