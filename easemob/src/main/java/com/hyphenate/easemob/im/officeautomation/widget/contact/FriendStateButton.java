package com.hyphenate.easemob.im.officeautomation.widget.contact;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.utils.DensityUtil;

public class FriendStateButton extends TextView {

    private FriendState friendState = FriendState.NOT_IS_FRIEND;
    private OnBtnClickListener listener;

    public FriendStateButton(Context context) {
        this(context, null);
    }

    public FriendStateButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FriendStateButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setOnBtnClickListener(OnBtnClickListener listener) {
        this.listener = listener;
    }

    private void init() {
        setBackgroundResource(R.drawable.bg_add_friend);
        int topVal = DensityUtil.dp2px(getContext(), 5);
        setWidth(DensityUtil.dp2px(getContext(), 90));
        setGravity(Gravity.CENTER);
        setPadding(0, topVal, 0, topVal);
        updateState(friendState);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(friendState, v);
                }
            }
        });
    }

    public void setFriendState(FriendState state) {
        this.friendState = state;
        updateState(state);
        invalidate();
    }

    private void updateState(FriendState state) {
        GradientDrawable gradientDrawable = (GradientDrawable) getBackground();
        if (state == FriendState.IS_FRIEND) {
            setText("解除好友");
            setTextColor(Color.parseColor("#f6101f"));
            gradientDrawable.setStroke(DensityUtil.dp2px(getContext(), 1), Color.parseColor("#f6101f"));
        } else if (state == FriendState.NOT_IS_FRIEND) {
            setText("添加好友");
            setTextColor(Color.parseColor("#23cdfd"));
            gradientDrawable.setStroke(DensityUtil.dp2px(getContext(), 1), Color.parseColor("#23cdfd"));
        } else if (state == FriendState.ACCEPT) {
            setText("接受");
            setTextColor(Color.parseColor("#23cdfd"));
            gradientDrawable.setStroke(DensityUtil.dp2px(getContext(), 1), Color.parseColor("#23cdfd"));
        }
    }

    public static enum FriendState {
        IS_FRIEND,
        NOT_IS_FRIEND,
        ACCEPT,
    }

    public static interface OnBtnClickListener {
        void onClick(FriendState state, View view);
    }


}
