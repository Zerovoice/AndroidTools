package com.zorro.tools;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class BitmapUtils {

    private final static File mImageFolder = Environment.getExternalStoragePublicDirectory("zorro");
    private final static String mFilePrefix = "zorro";
    private final static String mFileSuffix = ".jpg";

    public static String getSerialNumber(Context context) {
        Object obj = context.getSystemService(Context.TELEPHONY_SERVICE);
        if (obj instanceof TelephonyManager) {
            int permission = context.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE");
            if (permission == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telManager = (TelephonyManager) obj;
                String deviceId = telManager.getDeviceId();
                if (!TextUtils.isEmpty(deviceId)) {
                    return deviceId;
                }
            }
        }
        return android.os.Build.SERIAL;
    }

    public static Bitmap scaleBitmap(Bitmap source, int newWidth, int newHeight) {
        int width = source.getWidth();
        int height = source.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(source, 0, 0, width, height, matrix, true);
    }

//    public static void cropJpeg(Activity context, String imagePath, String imageType) {
//        Intent intent = new Intent(context, FaceDetectionActivity.class);
//        intent.setType(imageType);
//        intent.putExtra(FaceDetectionActivity.KEY_ACTION, FaceDetectionActivity.ACTION_UPLOAD_AVATAR);
//        intent.putExtra(FaceDetectionActivity.KEY_IMAGE_PATH, imagePath);
//        context.startActivityForResult(intent, PHOTO_REQUEST_CUT);
//    }
//
//    public static void cropJpegByUri(Activity context, Uri uri, String imageType) {
//        Intent intent = new Intent(context, FaceDetectionActivity.class);
//        intent.setDataAndType(uri, imageType);
//        intent.putExtra(FaceDetectionActivity.KEY_ACTION, FaceDetectionActivity.ACTION_UPLOAD_AVATAR);
//        context.startActivityForResult(intent, PHOTO_REQUEST_CUT);
//    }
//
//    public static void cropJpegImage(Activity context, Uri uri, String imageType) {
//        Intent intent = new Intent(context, NewCropActivity.class);
//        intent.setDataAndType(uri, imageType);
//        context.startActivityForResult(intent, PHOTO_REQUEST_CUT);
//    }

    public final static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public final static Bitmap bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public static String getImageHttpUri(String imageUrl) {
//		String uriStr = Uris.SCHEMA_HTTP + "://" + Uris.getServerAddress() + ":"+ Uris.HTTP_PORT +imageUrl;
//		return uriStr;
        return imageUrl;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public static boolean writeBitmapToGallery(Context context, Bitmap bitmap) {
        boolean checkSdcard = true;
        if (mImageFolder.exists()) {
            if (!mImageFolder.isDirectory()) {
                mImageFolder.delete();
                checkSdcard = mImageFolder.mkdir();
            }
        } else {
            checkSdcard = mImageFolder.mkdir();
        }
        if (!checkSdcard) {
            return false;
        }
        File destFile = new File(mImageFolder, mFilePrefix + System.currentTimeMillis() + mFileSuffix);

        if (bitmap == null) {
            return false;
        }
        boolean result = saveBitmapToFile(bitmap, destFile);
        if (result) {
            /*
             * This is not worked for android 4.4, reason below:
			 * A lot of apps used to use this intent to rescan the entire filesystem after changing only one file,
			 * which drained battery life considerably. Starting from Android 4.4,
			 *  only System apps can now use it. You'll likely have to find a workaround that doesn't use this intent.
			 */
            //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ mImageFolder.getAbsolutePath())));
            MediaScannerConnection.scanFile(context, new String[]{destFile.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
            return true;
        } else {
            return false;
        }
    }

    public static boolean saveBitmapToFile(Bitmap bmp, File destFile) {
        if (bmp == null || destFile == null) {
            return false;
        }
        CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            if (!destFile.exists()) {
                destFile.createNewFile();
            }
            stream = new FileOutputStream(destFile);
            return bmp.compress(format, quality, stream);
        } catch (Exception e) {
        }
        return false;
    }

    public static Bitmap toConformBitmap(Bitmap background, Bitmap foreground, boolean isCenter) {
        if (background == null || foreground == null) {
            return null;
        }

        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int fgWidth = foreground.getWidth();
        int fgHeight = foreground.getHeight();
        //create the new blank bitmap
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        cv.drawBitmap(background, 0, 0, null);
        if (isCenter) {
            cv.drawBitmap(foreground, bgWidth / 2 - fgWidth / 2, bgHeight / 2 - fgHeight / 2, null);
        } else {
            cv.drawBitmap(foreground, 0, bgHeight - fgHeight, null);
        }

        //save all comp.clip
        cv.save(Canvas.ALL_SAVE_FLAG);
        //store   
        cv.restore();
        return newbmp;
    }

    /**
     * 按正方形裁切图片
     */
    public static Bitmap cropImageToSquare(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        int wh = w > h ? h : w;// 裁切后所取的正方形区域边长

        int retX = w > h ? (w - h) / 2 : 0;//基于原图，取正方形左上角x坐标
        int retY = w > h ? 0 : (h - w) / 2;

        return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
    }

    /**
     * 此判断方法不够准确
     *
     * 用GlobalUtils.isForeground()方法代替
     *
     * @param context
     * @return
     */
    @Deprecated
    public static boolean isRunningForeground(Context context) {
        String Packagename = context.getPackageName();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);

        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        if (componentInfo.getPackageName().equals(Packagename)) return true;
        return false;
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean isNotificationEnabled(Context context) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        Class appOpsClass = null;
     /* Context.APP_OPS_MANAGER */
        try {
            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;

            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
