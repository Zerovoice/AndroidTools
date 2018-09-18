package com.zorro.testcode;

import android.os.SystemClock;
import android.util.Log;

public class NetworkLog {
	private static String TAG = "Retail_NetworkService";

	public static void d(String subtag, String msg) {
		Log.d(TAG, getMessage(subtag + ": " + msg));
	}

	public static void v(String subtag, String msg) {
		Log.v(TAG, getMessage(subtag + ": " + msg));
	}

	public static void i(String subtag, String msg) {
		Log.i(TAG, getMessage(subtag + ": " + msg));
	}

	public static void w(String subtag, String msg) {
		Log.w(TAG, getMessage(subtag + ": " + msg));
	}

	public static void e(String subtag, String msg) {
		Log.e(TAG, getMessage(subtag + ": " + msg));
	}

	public static void d(String msg) {
		Log.d(TAG, getMessage(msg));
	}

	public static void v(String msg) {
		Log.v(TAG, getMessage(msg));
	}

	public static void i(String msg) {
		Log.i(TAG, getMessage(msg));
	}

	public static void w(String msg) {
		Log.w(TAG, getMessage(msg));
	}

	public static void e(String msg) {
		Log.e(TAG, getMessage(msg));
	}

	private static String getMessage(String msg) {
		StringBuffer sbuffer = new StringBuffer();
		sbuffer.append("(").append(SystemClock.elapsedRealtime())
				.append(") - ").append(msg);
		return sbuffer.toString();
	}
}
