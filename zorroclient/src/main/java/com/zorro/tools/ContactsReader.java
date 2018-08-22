package com.zorro.tools;

/*
 *
 * Title: .
 * Description: .
 *
 * Created by Zorro(zeroapp@126.com) on 2018/8/22.
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

public class ContactsReader {
    Context context = null ;

    /**
     * To judge if an app can read contacts from phone.
     * @return
     */
    private boolean canReadContacts() {
        boolean can = false;
        try {
            Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cursor.getCount() > 0) {
                can = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return can;
    }

    @Deprecated
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }else{
            return PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Deprecated
    private boolean hasContactsPermission() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }


}
