package com.zorro.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.zorro.zorroclient.R;

/*
 *
 * Title: .
 * Description: .
 *
 * Created by Zorro(zeroapp@126.com) on 2018/8/22.
 */
public class CustomInputDialog extends Dialog implements View.OnClickListener {

    private TextView mContentView;
    private TextView mTitleView;
    private TextView mLeftView;
    private TextView mRightView;
    private View mLineView;
    private EditText mEditText;

    private Context mContext;

    private String mContent;
    private String mLeftName;
    private String mRightName;
    private String mTitle;

    private int mInputType = -1;
    private String mHintText;
    private String mInitText;
    private int mMaxLength;


    private CustomInputDialogDelegate mDelegate;



    public interface CustomInputDialogDelegate{
        void customAlertDialogOnClick(CustomInputDialog dialog, boolean isLeft, String inputContent);
    }
    public CustomInputDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
        mContext = context;
    }

    public CustomInputDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        mContext = context;

    }

    protected CustomInputDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    public CustomInputDialog setTitle(String title) {
        mTitle = title;
        return this;
    }

    public CustomInputDialog setContent(String content) {
        mContent = content;
        return this;
    }

    public CustomInputDialog setLeftName(String leftName) {
        mLeftName = leftName;
        return this;
    }

    public CustomInputDialog setRightName(String rightName) {
        mRightName = rightName;
        return this;
    }

    public CustomInputDialog setDelegate(CustomInputDialogDelegate delegate) {
        mDelegate = delegate;
        return this;
    }

    public CustomInputDialog setInputType(int inputType) {
        mInputType = inputType;
        return this;
    }

    public CustomInputDialog setHintText(String hintText) {
        mHintText = hintText;
        return this;
    }

    public CustomInputDialog setInitText(String initText) {
        mInitText = initText;
        return this;
    }

    public CustomInputDialog setDefaultButton() {
        mLeftName = mContext.getString(R.string.cancel);
        mRightName = mContext.getString(R.string.OK);
        return this;
    }

    public CustomInputDialog setMaxLength(int maxLength) {
        mMaxLength = maxLength;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_input_dialog);
        initView();

        setCanceledOnTouchOutside(true);

        mEditText.requestFocus();

        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm= (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });


    }

    private void initView() {
        mTitleView = (TextView) findViewById(R.id.title);
        mContentView = (TextView) findViewById(R.id.content);
        mLeftView = (TextView) findViewById(R.id.left_view);
        mRightView = (TextView) findViewById(R.id.right_view);
        mEditText = (EditText) findViewById(R.id.input_dialog_text);
        mLineView = findViewById(R.id.button_line);

        mLeftView.setOnClickListener(this);
        mRightView.setOnClickListener(this);

        if (TextUtils.isEmpty(mTitle)) {
            mTitleView.setVisibility(View.GONE);
        } else {
            mTitleView.setText(mTitle);
            mTitleView.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(mContent)) {
            mContentView.setVisibility(View.GONE);
        } else {
            mContent = mContent.replaceAll("\r|\n", "");
            mContentView.setText(mContent);
            mContentView.setVisibility(View.VISIBLE);
        }
        if (mInputType > -1) {
            mEditText.setInputType(mInputType);
        }

        if (!TextUtils.isEmpty(mHintText)) {
            mEditText.setHint(mHintText);
        }

        if (!TextUtils.isEmpty(mInitText)) {
            mEditText.setText(mInitText);
        }

        if (mMaxLength > 0) {
            InputFilter[] fArray = new InputFilter[1];
            fArray[0] = new InputFilter.LengthFilter(mMaxLength);
            mEditText.setFilters(fArray);
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
                    mDelegate.customAlertDialogOnClick(this, true, mEditText.getText().toString().trim());
                }
                dismiss();
                break;
            case R.id.right_view:
                if (mDelegate != null) {
                    mDelegate.customAlertDialogOnClick(this, false, mEditText.getText().toString().trim());
                }
                dismiss();
                break;
        }
    }
}
