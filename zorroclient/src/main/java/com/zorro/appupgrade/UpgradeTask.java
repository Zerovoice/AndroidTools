package com.zorro.appupgrade;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;


import com.zorro.zorroclient.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class UpgradeTask extends AsyncTask<String, Integer, String> {
    public static final String CANCELED = "canceled";
    private static final String FILE_NAME = "zeroapp.apk";//TODO
    public static final String FINISHED = "finished";
    private UpdradeTaskListener callBack;
    private boolean cancelEnable = true;
    private Context mContext;
    private ProgressDialog mProgressDialog;

    public UpgradeTask(Context mContext, UpdradeTaskListener callBack, boolean cancelEnable) {
        this.mContext = mContext;
        this.callBack = callBack;
        this.cancelEnable = cancelEnable;
    }

    protected String doInBackground(String... params) {
        File file;
        String fileUrl = params[0];
        if (fileUrl == null) {
            return null;
        }
        try {
            URL url = new URL(fileUrl);
            URLConnection conection = url.openConnection();
            conection.connect();
            int lenghtOfFile = conection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            File newApk = new File(Environment.getExternalStorageDirectory(), FILE_NAME);
            if (newApk != null) {
                try {
                    if (newApk.exists()) {
                        newApk.delete();
                    }
                    newApk.setReadable(true,false);
                    newApk.createNewFile();
                } catch (Exception e) {
                    file = newApk;
                }
            }
            OutputStream output = new FileOutputStream(newApk);
            byte[] data = new byte[1024];
            long total = 0;
            while (true) {
                int count = input.read(data);
                if (count == -1) {
                    break;
                } else if (isCancelled()) {
                    output.close();
                    input.close();
                    return null;
                } else {
                    total += (long) count;
                    publishProgress(new Integer[]{Integer.valueOf((int) ((100 * total) / ((long) lenghtOfFile)))});
                    output.write(data, 0, count);
                }
            }
            output.flush();
            output.close();
            input.close();
            launchUpgrade(newApk);
            file = newApk;
        } catch (Exception e2) {
            return e2.getMessage();
        }
        return null;
    }

    public void launchUpgrade(File newApk) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(newApk), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.mContext.startActivity(intent);
    }

    protected void onPreExecute() {
        super.onPreExecute();
        if (this.mContext != null) {
            this.mProgressDialog = new ProgressDialog(this.mContext);
            this.mProgressDialog.setMessage(this.mContext.getString(R.string.downloading));
            this.mProgressDialog.setIndeterminate(false);
            this.mProgressDialog.setMax(100);
            this.mProgressDialog.setProgressStyle(1);
            this.mProgressDialog.setCancelable(false);
            this.mProgressDialog.setCanceledOnTouchOutside(false);
            if (this.cancelEnable) {
                this.mProgressDialog.setButton(-2, this.mContext.getString(R.string.cancel), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        UpgradeTask.this.cancel(true);
                        UpgradeTask.this.onCancelled();
                        dialog.dismiss();
                    }
                });
            }
            this.mProgressDialog.show();
        }
    }

    protected void onProgressUpdate(Integer... values) {
        if (this.mContext != null) {
            this.mProgressDialog.setProgress(values[0].intValue());
        }
    }


    private void dismissDialog() {
        try {
            if(this.mProgressDialog != null && this.mProgressDialog.isShowing()) {
                this.mProgressDialog.dismiss();
            }
        } catch (IllegalArgumentException var6) {

        } catch (Exception var7) {

        } finally {
            this.mProgressDialog = null;
        }

    }

    protected void onCancelled() {
        super.onCancelled();
        if (this.mContext != null) {
            this.mContext = null;
        }
        dismissDialog();
        if (this.callBack != null) {
            this.callBack.onFinished(CANCELED);
            this.callBack = null;
        }
    }

    protected void onPostExecute(String result) {
        if (this.mContext != null) {
            this.mContext = null;
        }
        dismissDialog();
        if (this.callBack != null) {
            if (result != null && !TextUtils.isEmpty(result)) {
                this.callBack.onFinished(result);
            }else{
                this.callBack.onFinished(FINISHED);
            }
            this.callBack = null;
        }
    }
}
