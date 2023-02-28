package com.hyphenate.easemob.im.officeautomation.ui.group;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.easemob.easeui.domain.MPGroupMemberEntity;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.easeui.widget.listview.check.CheckViewModel;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupMuteChanged;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;

public class GroupMuteViewModel extends CheckViewModel<MPGroupMemberEntity> {

    private TextView tvTitle;
    private TextView tvRight;
    private AvatarImageView ivAvatar;
    private Context context;


    public GroupMuteViewModel(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected int getLayout() {
        return R.layout.layout_group_mute_item;
    }

    @Override
    protected void initView() {
        tvTitle = contentView.findViewById(R.id.tv_title);
        tvRight = contentView.findViewById(R.id.tv_right);
        ivAvatar = contentView.findViewById(R.id.iv_avatar);
    }

    @Override
    protected void setDataToView() {
        String nick = data.getNick();
        if (nick == null) {
            nick = String.valueOf(data.getUserId());
        }
        tvTitle.setText(nick);
        AvatarUtils.setAvatarContent(context, nick, data.getAvatar(), ivAvatar);

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EMAPIManager.getInstance().unmuteGroupMember(data.getChatGroupId(), data.getUserId(), data.isCluster(), new EMDataCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        if (context instanceof Activity) {
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "解禁成功！", Toast.LENGTH_SHORT).show();
                                    MPEventBus.getDefault().post(new EventGroupMuteChanged());
                                }
                            });
                        }

                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        if (context instanceof Activity) {
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "请求失败！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

            }
        });
    }








}
