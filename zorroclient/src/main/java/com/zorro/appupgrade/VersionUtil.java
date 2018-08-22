package com.zorro.appupgrade;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import java.util.logging.Logger;

public class VersionUtil {
    public static final String DEBUG = "debug";
    private static final Logger LOGGER = Logger.getLogger(VersionUtil.class.getName());
    private static String versionName = DEBUG;

    public static String getVersionName(Context context) {
        if(versionName == null || versionName.equals(DEBUG)){
            try {
                versionName =  context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            } catch (Exception e) {
                versionName = DEBUG;
            }
        }
        return versionName;
    }

    public static int getVersionCode(Context ctx) {
        int currentVersionCode = 0;
        try {
            return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return currentVersionCode;
        }
    }

    public static String getOSVersion() {
//        int versioncode = SystemProperties.getInt("ro.build.version.code", 0);
//        return getVersionName(versioncode) + "-" + SystemProperties.getInt("ro.build.version.dev", 0);

        return "";
    }

    private static String getVersionName(int versioncode) {
        int major = versioncode / 10000;
        int minor = (versioncode - (major * 10000)) / 100;
        return major + "." + minor + "." + ((versioncode - (major * 10000)) - (minor * 100));
    }

    public static int getHardwareVersionCode() {
        String version = "";//SystemProperties.getString("ro.hardwareno", "0");
        if (version.equalsIgnoreCase("0")) {
            return 0;
        }
        String[] iversion;
        if (version.startsWith("VENUS")) {
            iversion = version.split(":");
            if (iversion == null || iversion.length != 2) {
                return 0;
            }
            return Integer.parseInt(iversion[1]);
        }
        iversion = version.split(" ");
        if (iversion == null || iversion.length < 2) {
            return 0;
        }
        String[] subv = iversion[1].split("\\.");
        if (subv == null || subv.length != 3) {
            return 0;
        }
        return ((Integer.parseInt(subv[0]) * 10000) + (Integer.parseInt(subv[1]) * 100)) + Integer.parseInt(subv[2]);
    }

    public static String getHardwareVersion() {
        String version = "";//SystemProperties.getString("ro.hardwareno", "0");
        if (version == null) {
            return version;
        }
        String[] iversion = version.split(" ");
        if (iversion == null || iversion.length < 2) {
            return version;
        }
        return version.substring(iversion[0].length() + 1);
    }

    public static boolean isDevPrivateBuild(String versionName) {
        return DEBUG.equalsIgnoreCase(versionName);
    }
}
