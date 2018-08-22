package com.zorro.tools;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.zorro.zorroclient.R;

import java.lang.ref.WeakReference;
/*
 *
 * Title: .
 * Description: .
 *
 * Created by Zorro(zeroapp@126.com) on 2018/8/22.
 */
public class AlertToast {

    private static final int CALL_TOAST_DISPLAY_TIME = 5000;

    private static ExpandToast toast;
    private static IconToast iconToast;
    private static AlertToast instance = new AlertToast();
    private static WeakReference<Context> mContext = null;
    private static float mDensity = 1.0f;

    public static void init(Context context) {
        if (mContext == null || mContext.get() == null) {
            mContext = new WeakReference<Context>(context);
        }
    }

    public static void setDensity(float density) {
        mDensity = density;
    }

    public synchronized static void toastText(int textId) {
        if (toast == null && mContext.get() != null) {
            toast = instance.new ExpandToast(mContext.get());
        }
        toast.show(textId);
    }

    public synchronized static void toastText(String textString) {
        if (toast == null && mContext.get() != null) {
            toast = instance.new ExpandToast(mContext.get());
        }
        toast.show(textString);
    }

    public synchronized static void toastText(int textId, int milliseconds) {
        if (toast == null && mContext.get() != null) {
            toast = instance.new ExpandToast(mContext.get());
        }
        toast.show(textId, milliseconds);
    }

    public synchronized static void toastText(String textString, int milliseconds) {
        if (toast == null && mContext.get() != null) {
            toast = instance.new ExpandToast(mContext.get());
        }
        toast.show(textString, milliseconds);
    }

    //=========================icon toast===================

    public synchronized static void toastIconText(int textId, int resId) {
        if (iconToast == null && mContext.get() != null) {
            iconToast = instance.new IconToast(mContext.get(), resId);
        }
        iconToast.show(textId);
    }

    public synchronized static void toastIconText(String textString, int resId) {
        if (iconToast == null && mContext.get() != null) {
            iconToast = instance.new IconToast(mContext.get(), resId);
        }
        iconToast.show(textString);
    }

    public synchronized static void toastIconText(int textId, int resId, int milliseconds) {
        if (iconToast == null && mContext.get() != null) {
            iconToast = instance.new IconToast(mContext.get(), resId);
        }
        iconToast.show(textId, milliseconds);
    }

    public synchronized static void toastIconText(String textString, int resId, int milliseconds) {
        if (iconToast == null && mContext.get() != null) {
            iconToast = instance.new IconToast(mContext.get(), resId);
        }
        iconToast.show(textString, milliseconds);
    }


    //=========================icon toast end ==============

    public interface KickedOutCallback {
        void onKickedOut();
    }

    private class ExpandToast {

        // 自定义显示时间
        private static final int FLOAT_VIEW_DISPLAY_TIME = 2000;

        private Toast mToast;
        private TextView mTextView;

        public ExpandToast(Context mContext) {

            mToast = new Toast(mContext);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            mTextView = (TextView) inflater.inflate(R.layout.toast_view, null);
            mToast.setView(mTextView);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        }

        public void show(int textId) {
            // 每次刷新都只是刷新text内容
            show(textId, FLOAT_VIEW_DISPLAY_TIME);
        }

        public void show(int textId, int milliseconds) {
            if (milliseconds <= 0) {
                return;
            }
            Context context = mContext.get();
            if (context != null) {
                String text = context.getString(textId);
                show(text, milliseconds);
            } else {
                mTextView.setText(textId);
                mToast.setDuration(milliseconds);
                mToast.show();
            }
        }

        public void show(String text) {
            // 每次刷新都只是刷新text内容
            show(text, FLOAT_VIEW_DISPLAY_TIME);
        }

        public void show(String text, int milliseconds) {
            if (milliseconds <= 0) {
                return;
            }

            int pad18 = (int) (18 * mDensity);
            int pad12 = (int) (12 * mDensity);
            text = text.replaceAll("\r|\n", "");
            if (text.length() <= 8) {
                mTextView.setPadding(pad18, pad12, pad18, pad12);
            } else {
                mTextView.setPadding(pad18, pad18, pad18, pad18);
            }

            if (text.length() > 24) {
                mTextView.setMaxWidth((int) (206 * mDensity));
            }


            mTextView.setText(TransformationUtils.getString(text));
            mToast.setDuration(milliseconds);
            mToast.show();
        }

    }

    private class IconToast {

        // 自定义显示时间
        private static final int FLOAT_VIEW_DISPLAY_TIME = 2000;

        private Toast mToast;
        private TextView mTextView;
        private ImageView mIconView;

        public IconToast(Context mContext, int resId) {

            mToast = new Toast(mContext);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.toast_icon_view, null);
            mTextView = (TextView) view.findViewById(R.id.textview);
            mIconView = (ImageView) view.findViewById(R.id.icon);
            mToast.setView(view);
            mIconView.setImageResource(resId);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        }

        public void show(int textId) {
            // 每次刷新都只是刷新text内容
            show(textId, FLOAT_VIEW_DISPLAY_TIME);
        }

        public void show(int textId, int milliseconds) {
            if (milliseconds <= 0) {
                return;
            }
            Context context = mContext.get();
            if (context != null) {
                String text = context.getString(textId);
                show(text, milliseconds);
            } else {
                mTextView.setText(textId);
                mToast.setDuration(milliseconds);
                mToast.show();
            }
        }

        public void show(String text) {
            // 每次刷新都只是刷新text内容
            show(text, FLOAT_VIEW_DISPLAY_TIME);
        }

        public void show(String text, int milliseconds) {
            if (milliseconds <= 0) {
                return;
            }

            int pad18 = (int) (18 * mDensity);
            int pad12 = (int) (12 * mDensity);
            text = text.replaceAll("\r|\n", "");

            if (text.length() > 24) {
                mTextView.setMaxWidth((int) (170 * mDensity));
            }


            mTextView.setText(TransformationUtils.getString(text));
            mToast.setDuration(milliseconds);
            mToast.show();
        }

    }
}
