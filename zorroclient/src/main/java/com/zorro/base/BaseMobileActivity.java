package com.zorro.base;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zorro.appupgrade.UpgradeUtil;
import com.zorro.zorroclient.R;

public class BaseMobileActivity extends BaseActivity {

    private ProgressDialog dialog;
    private volatile boolean viewReady;
    private volatile boolean serviceReady;
    private Object lock = new Object();

    private View customTitleView;
    private TextView titleView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableHomeButton(true);

        synchronized (lock) {
            viewReady = true;
            checkReady();
        }
        Drawable drawable = getDrawable(R.color.black_30alpha);
        this.getWindow().setBackgroundDrawable(drawable);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setWindowContentOverlayCompat();
    }

    /**
     * Set the window content overlay on device's that don't respect the theme
     * attribute. This fix is only for Android 4.3. See the link below
     * https://code.google.com/p/android/issues/detail?id=58280
     */
    private void setWindowContentOverlayCompat() {
        if (Build.VERSION.SDK_INT == 18) {
            View contentView = findViewById(android.R.id.content);
            if (contentView instanceof FrameLayout) {
                TypedValue tv = new TypedValue();
                if (getTheme().resolveAttribute(
                        android.R.attr.windowContentOverlay, tv, true)) {
                    if (tv.resourceId != 0) {
                        ((FrameLayout) contentView)
                                .setForeground(getResources().getDrawable(
                                        tv.resourceId));
                    }
                }
            }
        }
    }

    @Override
    protected final void onServiceConnected(Service service) {
        super.onServiceConnected(service);
        synchronized (lock) {
            serviceReady = true;
            checkReady();
        }
    }

    protected void onClickBackButton() {
        finish();
    }

    private void checkReady() {
        synchronized (lock) {
            if (viewReady && serviceReady) {
                onViewAndServiceReady(getService());
            }
        }
    }

   protected void setActivityTitle(String title){
       if (null!=titleView) {
           titleView.setText(title);
       }
   }

    protected void onViewAndServiceReady(Service service) {

    }

    @Override
    protected Messenger getMessenger() {
        return new Messenger(new MessageHandler(this));
    }

    protected void onMessage(Message msg) {
    }


    protected void alertKickedOut(String content) {
        //clear kicked out msg from DB
        try {
//            getAIDLService().updateUserKickedOutPrompt(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(isVisibility()){
            Intent intent = new Intent("");//IntentActions.Activity.LOGIN_REGISTER_ACTIVITY
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        finish();
    }

    public void alertKickedOut(int contentRes) {
        alertKickedOut(getResources().getString(contentRes));
    }

    public void alartMustUpdate() {
        UpgradeUtil upgradeUtil = new UpgradeUtil(this);
        upgradeUtil.checkVersion();
    }

    public void popupDialog(CharSequence message) {
        this.popupDialog("", message);
    }

    public void popupDialog(int msgRes) {
        this.popupDialog("", getResources().getString(msgRes));
    }

    public void popupDialog(CharSequence title, CharSequence message) {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setOnCancelListener(null);
        dialog.setTitle(title);
        dialog.setMessage(message);

        dialog.show();
    }

    public void hideDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog=null;
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        super.onDestroy();
    }

    protected final void goMainActivity() {
        Intent intent = new Intent("");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    protected boolean autoShowNoNetworkToast(){
        return true;
    }

    public static final class MessageHandler extends SafeHandler<BaseMobileActivity> {
        public MessageHandler(BaseMobileActivity ref) {
            super(ref);
        }

        @Override
        public void handleMessage(BaseMobileActivity ref, Message msg) {
            ref.onMessage(Message.obtain(msg));
        }
    }
}
