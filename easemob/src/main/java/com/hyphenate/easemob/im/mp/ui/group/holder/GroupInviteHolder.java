package com.hyphenate.easemob.im.mp.ui.group.holder;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easemob.R;

import drawthink.expandablerecyclerview.holder.BaseViewHolder;

public class GroupInviteHolder extends BaseViewHolder {

    public ImageView ivAvatar;
    public TextView tvName;
    public TextView tvOrgName;
    public TextView tvInvitorName;
    public Button btnAgree;
    public Button btnRefuse;

    public TextView tvTitle;

    public GroupInviteHolder(Context ctx, View itemView, int viewType) {
        super(ctx, itemView, viewType);
        ivAvatar = itemView.findViewById(R.id.iv_avatar);
        tvName = itemView.findViewById(R.id.tv_name);
        tvOrgName = itemView.findViewById(R.id.tv_org_name);
        tvInvitorName = itemView.findViewById(R.id.tv_inviter_name);
        tvTitle = itemView.findViewById(R.id.tv_title);

        btnAgree = itemView.findViewById(R.id.btn_accept);
        btnRefuse = itemView.findViewById(R.id.btn_refuse);
    }

    @Override
    public int getChildViewResId() {
        return R.id.child;
    }

    @Override
    public int getGroupViewResId() {
        return R.id.group;
    }
}
