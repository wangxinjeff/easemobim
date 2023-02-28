package com.hyphenate.easemob.im.officeautomation.ui.group;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.hyphenate.easemob.easeui.domain.MPGroupMemberEntity;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.easeui.widget.listview.check.CheckViewModel;
import com.hyphenate.easemob.R;

public class GroupMemberViewModel extends CheckViewModel<MPGroupMemberEntity> {

    private TextView tvTitle;
    private TextView tvRole;
    private AvatarImageView ivAvatar;
    private Context context;


    public GroupMemberViewModel(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected int getLayout() {
        return R.layout.vm_test;
    }

    @Override
    protected void initView() {
        tvTitle = contentView.findViewById(R.id.tv_title);
        tvRole = contentView.findViewById(R.id.tv_role);
        ivAvatar = contentView.findViewById(R.id.iv_avatar);
    }

    @Override
    protected void setDataToView() {
        String nick = data.getNick();
        if (nick == null) {
            nick = String.valueOf(data.getUserId());
        }
        tvTitle.setText(nick);
        if ("owner".equalsIgnoreCase(data.getType())) {
            tvRole.setVisibility(View.VISIBLE);
        } else {
            tvRole.setVisibility(View.GONE);
        }
        AvatarUtils.setAvatarContent(context, nick, data.getAvatar(), ivAvatar);
    }
}
