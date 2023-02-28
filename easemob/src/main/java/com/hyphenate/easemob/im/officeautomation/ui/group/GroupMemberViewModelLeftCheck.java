package com.hyphenate.easemob.im.officeautomation.ui.group;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.easemob.easeui.domain.MPGroupMemberEntity;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.easeui.widget.listview.check.CheckViewModel;
import com.hyphenate.easemob.R;

public class GroupMemberViewModelLeftCheck extends GroupMemberViewModel {

    public GroupMemberViewModelLeftCheck(Context context) {
        super(context);
    }

    @Override
    protected int getLayout() {
        return R.layout.group_member_left_check;
    }
}
