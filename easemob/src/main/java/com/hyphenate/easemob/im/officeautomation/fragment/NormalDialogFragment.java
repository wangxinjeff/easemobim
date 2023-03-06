package com.hyphenate.easemob.im.officeautomation.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentTransaction;

import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.ui.BaseDialogFragment;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;

import java.lang.reflect.Field;

public class NormalDialogFragment extends BaseDialogFragment implements View.OnClickListener{

    private TextView tvTitle;
    private TextView tvConfirm;
    private TextView tvCancel;
    private OnConfirmClickListener mOnConfirmClickListener;
    private onCancelClickListener mOnCancelClickListener;
    private String confirmText;
    private String cancelText;
    private String titleText;

    @Override
    public void onStart() {
        super.onStart();
        //宽度填满，高度自适应
        try {
            Window dialogWindow = getDialog().getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialogWindow.setAttributes(lp);

            View view = getView();
            if(view != null) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                if(params instanceof FrameLayout.LayoutParams) {
                    int margin = (int) EaseCommonUtils.dip2px(mContext, 30);
                    ((FrameLayout.LayoutParams) params).setMargins(margin, 0, margin, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int showAllowingStateLoss(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        try {
            Field dismissed = NormalDialogFragment.class.getDeclaredField("mDismissed");
            dismissed.setAccessible(true);
            dismissed.set(this, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            Field shown = NormalDialogFragment.class.getDeclaredField("mShownByMe");
            shown.setAccessible(true);
            shown.set(this, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        transaction.add(this, tag);
        try {
            Field viewDestroyed = NormalDialogFragment.class.getDeclaredField("mViewDestroyed");
            viewDestroyed.setAccessible(true);
            viewDestroyed.set(this, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        int mBackStackId = transaction.commitAllowingStateLoss();
        try {
            Field backStackId = NormalDialogFragment.class.getDeclaredField("mBackStackId");
            backStackId.setAccessible(true);
            backStackId.set(this, mBackStackId);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return mBackStackId;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_normal;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        tvTitle = findViewById(R.id.tv_title);
        tvConfirm = findViewById(R.id.tv_confirm);
        tvCancel = findViewById(R.id.tv_cancel);
    }

    @Override
    public void initListener() {
        tvConfirm.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    @Override
    public void initData() {
        if(!TextUtils.isEmpty(titleText)){
            tvTitle.setText(titleText);
        }

        if(!TextUtils.isEmpty(confirmText)){
            tvConfirm.setText(confirmText);
        }

        if(!TextUtils.isEmpty(cancelText)){
            tvCancel.setText(cancelText);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.tv_confirm){
            onConfirmClick();
        } else if (id == R.id.tv_cancel){
            onCancelClick();
        }
    }

    /**
     * 点击了取消按钮
     */
    public void onCancelClick() {
        dismiss();
        if(mOnCancelClickListener != null) {
            mOnCancelClickListener.onCancelClick();
        }
    }

    /**
     * 点击了确认按钮
     */
    public void onConfirmClick() {
        dismiss();
        if(mOnConfirmClickListener != null) {
            mOnConfirmClickListener.onConfirmClick();
        }
    }

    /**
     * 设置确定按钮的点击事件
     * @param listener
     */
    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.mOnConfirmClickListener = listener;
    }

    /**
     * 设置取消事件
     * @param cancelClickListener
     */
    public void setOnCancelClickListener(onCancelClickListener cancelClickListener) {
        this.mOnCancelClickListener = cancelClickListener;
    }

    /**
     * 确定事件的点击事件
     */
    public interface OnConfirmClickListener {
        void onConfirmClick();
    }

    /**
     * 点击取消
     */
    public interface onCancelClickListener {
        void onCancelClick();
    }

    private void setConfirmText(String confirmText) {
        this.confirmText = confirmText;
    }

    private void setCancelText(String cancelText) {
        this.cancelText = cancelText;
    }

    private void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public static class Builder {
        public BaseActivity context;
        private String title;
        private String confirmText;
        private OnConfirmClickListener listener;
        private onCancelClickListener cancelClickListener;
        private Bundle bundle;
        private String cancel;

        public Builder(BaseActivity context) {
            this.context = context;
        }

        public Builder setTitle(@StringRes int title) {
            this.title = context.getString(title);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setOnConfirmClickListener(@StringRes int confirm, OnConfirmClickListener listener) {
            this.confirmText = context.getString(confirm);
            this.listener = listener;
            return this;
        }

        public Builder setOnConfirmClickListener(String confirm, OnConfirmClickListener listener) {
            this.confirmText = confirm;
            this.listener = listener;
            return this;
        }

        public Builder setOnConfirmClickListener(OnConfirmClickListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setOnCancelClickListener(@StringRes int cancel, onCancelClickListener listener) {
            this.cancel = context.getString(cancel);
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setOnCancelClickListener(String cancel, onCancelClickListener listener) {
            this.cancel = cancel;
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setOnCancelClickListener(onCancelClickListener listener) {
            this.cancelClickListener = listener;
            return this;
        }


        public Builder setArgument(Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public NormalDialogFragment build() {
            NormalDialogFragment fragment = getFragment();
            fragment.setTitleText(title);
            fragment.setConfirmText(confirmText);
            fragment.setOnConfirmClickListener(this.listener);
            fragment.setCancelText(cancel);
            fragment.setOnCancelClickListener(cancelClickListener);
            fragment.setArguments(bundle);
            return fragment;
        }

        protected NormalDialogFragment getFragment() {
            return new NormalDialogFragment();
        }

        public NormalDialogFragment show() {
            NormalDialogFragment fragment = build();
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragment.showAllowingStateLoss(transaction, null);
            return fragment;
        }
    }
}
