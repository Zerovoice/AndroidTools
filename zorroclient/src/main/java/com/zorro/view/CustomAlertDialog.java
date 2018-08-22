package com.zorro.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zorro.zorroclient.R;


/*
 *
 * Title: .
 * Description: .
 *
 * Created by Zorro(zeroapp@126.com) on 2018/8/22.
 */
public class CustomAlertDialog extends Dialog implements View.OnClickListener {

    private TextView mContentView;
    private TextView mTitleView;
    private TextView mLeftView;
    private TextView mRightView;
    private View mLineView;

    private Context mContext;

    private String mContent;
    private String mLeftName;
    private String mRightName;
    private String mTitle;

    private CustomAlertDialogDelegate mDelegate;



    public interface CustomAlertDialogDelegate{
        void customAlertDialogOnClick(CustomAlertDialog dialog, boolean isLeft);
    }
    public CustomAlertDialog(@NonNull Context context) {
        super(context, R.style.AppTheme);
        mContext = context;
    }

    public CustomAlertDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        mContext = context;

    }

    protected CustomAlertDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }


    /**
     * 自定义弹框。类似ios的alertView
     * @param context
     * @param title 标题，可为空
     * @param content 提示内容
     * @param delegate 回调
     */
    public CustomAlertDialog(@NonNull Context context, String title, @Nullable String content, CustomAlertDialogDelegate delegate) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mTitle = title;
        mContent = content;
        mDelegate = delegate;
    }

    /**
     *
     * 自定义弹框。类似ios的alertView
     * @param context
     * @param title 标题，可为空
     * @param content 提示内容
     * @param leftName 左边按钮文案, 为空不显示
     * @param rightName 右边按钮文案, 为空不显示
     * @param delegate 回调
     */
    public CustomAlertDialog(@NonNull Context context, String title, @Nullable String content, String leftName, String rightName, CustomAlertDialogDelegate delegate) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mTitle = title;
        mContent = content;
        mDelegate = delegate;
        mLeftName = leftName;
        mRightName = rightName;
    }

    public CustomAlertDialog setTitle(String title) {
        mTitle = title;
        return this;
    }

    public CustomAlertDialog setContent(String content) {
        mContent = content;
        return this;
    }

    public CustomAlertDialog setLeftName(String leftName) {
        mLeftName = leftName;
        return this;
    }

    public CustomAlertDialog setRightName(String rightName) {
        mRightName = rightName;
        return this;
    }

    public CustomAlertDialog setDelegate(CustomAlertDialogDelegate delegate) {
        mDelegate = delegate;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_alert_dialog);
        setCanceledOnTouchOutside(false);

        initView();
    }

    private void initView() {
        mTitleView = (TextView) findViewById(R.id.title);
        mContentView = (TextView) findViewById(R.id.content);
        mLeftView = (TextView) findViewById(R.id.left_view);
        mRightView = (TextView) findViewById(R.id.right_view);
        mLineView = findViewById(R.id.button_line);

        mLeftView.setOnClickListener(this);
        mRightView.setOnClickListener(this);

        if (TextUtils.isEmpty(mTitle)) {
            mTitleView.setVisibility(View.GONE);
        } else {
            mTitleView.setText(mTitle);
            mTitleView.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(mContent)) {
            mContent = mContent.replaceAll("\r|\n", "");
            mContentView.setText(mContent);
        }
        if (!TextUtils.isEmpty(mLeftName)) {
            mLeftView.setText(mLeftName);
        } else {
            mLeftView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mRightName)) {
            mRightView.setText(mRightName);
        } else {
            mRightView.setVisibility(View.GONE);
        }

        if (mLeftView.getVisibility() == View.GONE || mRightView.getVisibility() == View.GONE) {
            mLineView.setVisibility(View.GONE);

            if (mLeftView.getVisibility() == View.VISIBLE) {
                mLeftView.getPaint().setFakeBoldText(true);
            } else if (mRightView.getVisibility() == View.VISIBLE) {
                mRightView.getPaint().setFakeBoldText(true);
            }
            if (mLeftView.getVisibility() == View.GONE && mRightView.getVisibility() == View.GONE) {
                mLeftView.setVisibility(View.VISIBLE);
                mLeftView.setText(R.string.OK);
                mLeftView.getPaint().setFakeBoldText(true);
            }
        } else {
            mRightView.getPaint().setFakeBoldText(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_view:
                if (mDelegate != null) {
                    mDelegate.customAlertDialogOnClick(this, true);
                }
                dismiss();
                break;
            case R.id.right_view:
                if (mDelegate != null) {
                    mDelegate.customAlertDialogOnClick(this, false);
                }
                dismiss();
                break;
        }
    }
}
