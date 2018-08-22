package com.zorro.tools;

import android.text.TextUtils;

/*
 *
 * Title: .
 * Description: .
 *
 * Created by Zorro(zeroapp@126.com) on 2018/8/22.
 */
public class CheckUtil {

    /**
     * 4-16 chars
     */
    public static boolean checkPasswordLength(String password) {
        if (isEmpty(password)) {
            return false;
        }
        return password.length() >= 6 && password.length() <= 16;
    }

    /**
     * 4-16 chars
     */
    public static boolean checkPasswordLength(CharSequence password) {
        if (isEmpty(password)) {
            return false;
        }
        return password.length() >= 6 && password.length() <= 16;
    }

    public static boolean isEmpty(String text) {
        return TextUtils.isEmpty(text);
    }

    public static boolean isEmpty(CharSequence text) {
        return TextUtils.isEmpty(text);
    }
}
