package com.zorro.appupgrade;

import android.content.Context;
import android.content.SharedPreferences;

public class AppVersionPreference {
    private final String PREF_NAME = "zorro.preference.app.version";
    private String NEW_VERSION_URL = "new_version_url";
    private String HAS_NOTIFY = "notify";
    private String HAS_NEW_VERSION = "has_new_version";
    private String LAST_ALERT_TIME = "last_alert_time";
    private SharedPreferences preferences;

    private final String APP_VERSION_CODE = "version_code";
    private final String APP_REVISION = "revision";

    public AppVersionPreference(Context ctx) {
        preferences = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean hasNotify() {
        return preferences.getBoolean(HAS_NOTIFY, false);
    }

    public void setHasNotify(boolean hasNotify) {
        preferences.edit().putBoolean(HAS_NOTIFY, hasNotify).apply();
    }

    public String getAppUrl() {
        return preferences.getString(NEW_VERSION_URL, null);
    }

    public void setAppUrl(String appUrl) {
        preferences.edit().putString(NEW_VERSION_URL, appUrl).apply();
    }

    public void setHasNewVersion(boolean has) {
        preferences.edit().putBoolean(HAS_NEW_VERSION, has).apply();
    }

    public boolean hasNewVersion() {
        return preferences.getBoolean(HAS_NEW_VERSION, false);
    }

    public long getLastAlertTime() {
        return preferences.getLong(LAST_ALERT_TIME, 0);
    }

    public void setLastAlertTime(long lastAlertTime) {
        preferences.edit().putLong(LAST_ALERT_TIME, lastAlertTime).apply();
    }

    public int getVersionCode() {
        return preferences.getInt(APP_VERSION_CODE, -1);
    }

    public void setVersionCode(int versionCode) {
        preferences.edit().putInt(APP_VERSION_CODE, versionCode).apply();
    }

    public int getRevision() {
        return preferences.getInt(APP_REVISION, 0);
    }

    public void setRevision(int revision) {
        preferences.edit().putInt(APP_REVISION, revision).apply();
    }


}
