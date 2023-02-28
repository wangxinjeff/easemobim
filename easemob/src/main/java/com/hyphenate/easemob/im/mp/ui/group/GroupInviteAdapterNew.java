package com.hyphenate.easemob.im.mp.ui.group;


import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hyphenate.easemob.R;

import java.util.List;

public class GroupInviteAdapterNew extends BaseMultiItemQuickAdapter<InviteEntity, BaseViewHolder> {

    public GroupInviteAdapterNew(List<InviteEntity> data) {
        super(data);
        addItemType(0, R.layout.item_layout_group_invite);
    }

    @Override
    protected void convert(BaseViewHolder helper, InviteEntity item) {
        helper.setText(R.id.tv_group_name, "申请加入:" + item.getChatGroupName());
        helper.setText(R.id.tv_inviter_name, "邀请人:" + item.getInvitorRealName());
        helper.setText(R.id.tv_name, item.getUserRealName() + " 入群申请");
        helper.addOnClickListener(R.id.btn_accept);
        helper.addOnClickListener(R.id.btn_refuse);
    }
}
