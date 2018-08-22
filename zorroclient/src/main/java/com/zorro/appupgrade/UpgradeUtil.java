package com.zorro.appupgrade;

import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Html;

import com.zorro.base.BaseActivity;
import com.zorro.view.CustomAlertDialog;
import com.zorro.http.HttpConnector;
import com.zorro.http.request.Get;
import com.zorro.http.response.HttpResponse;
import com.zorro.tools.JsonUtil;
import com.zorro.zorroclient.R;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class UpgradeUtil {

    public final static String RESULT_NEWEST = "newest";
    private final static String PATH_VERSION = "/api/rest/version";
    private final static String QUERY_VERSION = "platform=%s&type=" + "release" + "&ud=%d&version=%s";//TODO CommonDef.getUpgradeType()
    private final static int MSG_SHOW_ALERT = 0X10;
    private final static int HOUR_24 = 24 * 60 * 60 * 1000;
    private static boolean upgradeEnable = true;
    private int clientVersion;
    private Context mContext;
    private Handler handler;
    private VersionResponse versionResponse;
    private AppVersionPreference mVersionPrefs;
    private CheckVersionListener checkVersionListener;
    private long lastCheckTime;

    public UpgradeUtil(Context context) {
        this.mContext = context;
        mVersionPrefs = new AppVersionPreference(mContext);
        this.handler = new Handler(new Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == MSG_SHOW_ALERT) {

                    if (msg.arg2 == 1) { // force popup alert
                        popupAlert(String.valueOf(msg.obj), msg.arg1 == 1);
                    } else {
                        if (needAlertUpgrade()) {
                            if (!isMobileNetType()) {
                                popupAlert(String.valueOf(msg.obj), msg.arg1 == 1);
                            }
                        }
                    }
                }
                return true;
            }
        });
    }

    private static void startUpgrade(final Context context, String appUrl, boolean cancelEnable) {
        UpgradeTask ut = new UpgradeTask(context, new UpdradeTaskListener() {
            @Override
            public void onFinished(String result) {
                upgradeEnable = true;
                if (result.equalsIgnoreCase(UpgradeTask.FINISHED)) {
                    Service service = ((BaseActivity) context).getService();
                    if (service != null) {
                        try {
                            //TODO sth
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    AppVersionPreference mVersionPrefs = new AppVersionPreference(context);
                    mVersionPrefs.setHasNewVersion(false); // set to false first.
                }
            }
        }, cancelEnable);
        ut.execute(appUrl);
    }

    public boolean needCheckVersion() {
        long current = System.currentTimeMillis();
        return Math.abs(current - lastCheckTime) > HOUR_24;
    }

    private boolean needAlertUpgrade() {
        long lastAlertTime = mVersionPrefs.getLastAlertTime();
        long now = System.currentTimeMillis();
        return Math.abs(now - lastAlertTime) > HOUR_24;
    }

    public void checkVersion() {
        checkVersion(false);
    }

    public void checkVersion(final boolean forcePopupAlert) {
        if (!upgradeEnable) {
            return;
        }
        clientVersion = VersionUtil.getVersionCode(mContext);
        URI uri = null;
        try {
            String query = String.format(Locale.US, QUERY_VERSION,"android", getLoginUserID(),VersionUtil.getVersionName(mContext));
            uri = new URI("https", null, "", 80, PATH_VERSION, query, null);
        } catch (URISyntaxException e1) {
        }
        HttpConnector.postExecute(new Get(uri), new HttpConnector.Callback() {
            @Override
            public void onDone(HttpResponse resp) {
                boolean result = false;
                if (resp.getCode() == 200) {
                    versionResponse = JsonUtil.toObject(resp.getData(), VersionResponse.class);
                    if (versionResponse != null) {
                        result = true;
                    }
                }
                if (checkVersionListener != null) {
                    checkVersionListener.onCheckedResult(result);
                }
                if (result) {
                    onGetVersionResult(forcePopupAlert);
                }
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
                if (checkVersionListener != null) {
                    checkVersionListener.onCheckedResult(false);
                }
            }
        });
    }

    private void saveVersionInfoIntoPrefs() {
        //context_menu_save version info into version preferences
        mVersionPrefs.setAppUrl(versionResponse.getAppUrl());
        mVersionPrefs.setHasNotify(false);
        lastCheckTime = System.currentTimeMillis();
        mVersionPrefs.setHasNewVersion(true);
    }

    private void onGetVersionResult(final boolean forcePopupAlert) {
        boolean notify = false;
        int calcelElable = 1;
        if (versionResponse.getMinSupportVersion() > clientVersion) {
            //newer than last check, need alert
            calcelElable = 0;
            //if(!mVersionPrefs.hasNotify())
            {
                saveVersionInfoIntoPrefs();
                notify = true;
            }
        } else if (versionResponse.getVersionCode() > clientVersion) {
            //newer than last check, need alert
            //if(!mVersionPrefs.hasNotify())
            {
                saveVersionInfoIntoPrefs();
                notify = true;
            }
        } else if (versionResponse.getVersionCode() == clientVersion &&
                versionResponse.getDevRevision() > 99999) {//TODO BuildConfig.REVISION
            //newer than last check, need alert
            //if(!mVersionPrefs.hasNotify())
            {
                saveVersionInfoIntoPrefs();
                notify = true;
            }
        } else {
            mVersionPrefs.setHasNewVersion(false);
        }
        if (notify) {
            String alertContent = buildAlertContent();
            Message msg = Message.obtain();
            msg.what = MSG_SHOW_ALERT;
            msg.obj = alertContent;
            msg.arg1 = calcelElable;
            if (forcePopupAlert) {
                msg.arg2 = 1;
            }
            handler.sendMessage(msg);

            sendClientHasNewVersionMessage();
        }
        if (checkVersionListener != null) {
            checkVersionListener.onCheckedResult(notify);
        }
    }

    private String buildAlertContent() {
        String content = "";//String.format(mContext.getResources().getString(R.string.app_name), versionResponse.getReleaseNote());
        return content;
    }

    private void popupAlert(String htmlContent, final boolean cancelEnable) {

        // set last alert time
        mVersionPrefs.setLastAlertTime(System.currentTimeMillis());

        upgradeEnable = false;
        new CustomAlertDialog(mContext).setTitle(mContext.getResources().getString(R.string.app_name)).
                setContent(Html.fromHtml(htmlContent).toString()).
                setLeftName(cancelEnable ? mContext.getString(R.string.cancel) : null).
                setRightName(mContext.getString(R.string.app_name)).
                setDelegate(new CustomAlertDialog.CustomAlertDialogDelegate() {
                    @Override
                    public void customAlertDialogOnClick(CustomAlertDialog dialog, boolean isLeft) {
                        if (isLeft) {
                            reportUpgradeEvent("cancelled");
                            mVersionPrefs.setHasNotify(true);
                            upgradeEnable = true;
                        } else {
                            reportUpgradeEvent("consumed");
                            onUpgradeButtonClicked(cancelEnable);
                        }
                    }
                }).show();
    }

    private void reportUpgradeEvent(String type) {
        Service service = getService();
        if (null != service) {
            try {
//                service.reportUpgradeEvent(type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private long getLoginUserID() {
        Service service = getService();
        if (null != service) {
            try {
//                UserProfile loginUser = service.getLoginUser();
//                if (loginUser != null) {
//                    return loginUser.getId();
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    private void sendClientHasNewVersionMessage() {
        Service service = getService();
        if (null != service) {
            try {
//                service.clientHasNewVersion();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void onUpgradeButtonClicked(boolean cancelEnable) {
        startUpgrade(mContext, versionResponse.getAppUrl(), cancelEnable);
    }

    private Service getService() {
        Service service = null;
        if (mContext instanceof BaseActivity) {
            service = ((BaseActivity) mContext).getService();
        }
        return service;
    }

    public void setCheckVersionListener(CheckVersionListener checkVersionListener) {
        this.checkVersionListener = checkVersionListener;
    }

    private boolean isMobileNetType() {
        boolean isMobileType = false;
        Service service = getService();
        if (service != null) {
//            NetworkState lastState = null;
//            try {
//                lastState = service.getNetworkState();
//            } catch (RemoteException e) {
//            }
//            if (lastState != null && lastState.getType() == NetworkType.MOBILE) {
//                isMobileType = true;
//            }
        }
        return isMobileType;
    }

    public interface CheckVersionListener {
        void onCheckedResult(boolean newVersionFound);
    }

    class VersionResponse {
        private String appUrl;
        private String platform;
        private String versionName;
        private String releaseNote;
        private int versionCode;
        private int devRevision;
        private int minSupportVersion;
        private long releaseTime;

        public String getAppUrl() {
            return appUrl;
        }

        public void setAppUrl(String appUrl) {
            this.appUrl = appUrl;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getReleaseNote() {
            return releaseNote;
        }

        public void setReleaseNote(String releaseNote) {
            this.releaseNote = releaseNote;
        }

        public long getReleaseTime() {
            return releaseTime;
        }

        public void setReleaseTime(long releaseTime) {
            this.releaseTime = releaseTime;
        }

        public int getMinSupportVersion() {
            return minSupportVersion;
        }

        public void setMinSupportVersion(int minSupportVersion) {
            this.minSupportVersion = minSupportVersion;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public int getDevRevision() {
            return devRevision;
        }

        public void setDevRevision(int devRevision) {
            this.devRevision = devRevision;
        }
    }
}
