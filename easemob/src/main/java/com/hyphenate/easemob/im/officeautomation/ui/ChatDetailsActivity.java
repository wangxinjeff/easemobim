package com.hyphenate.easemob.im.officeautomation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.easeui.widget.EaseAlertDialog;
import com.hyphenate.easemob.easeui.widget.EaseTitleBar;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.ui.group.GroupAddMemberActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;

/**
 * Created by qby on 2018/06/04.
 * 单聊详情界面
 */
public class ChatDetailsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ChatDetailsActivity";
    private EaseTitleBar titleBar;
    //姓名、添加成员、清空聊天记录
    private RelativeLayout rlAddMember;
    private TextView tvName, addMember, clearAllRecords;
    //查找历史内容
//    private RelativeLayout rlSearchHistory;
    //开关，聊天置顶、消息免打扰
    private SwitchButton mSbStickyChat, mSbMuteNotifications;
    //对方ID
    private String toChatUsername;
    private EMConversation conversation;
    private String avatar;
    private String realName;
    private AvatarImageView iv_avatar;
    private RelativeLayout rl_user;
    private ImageView ivArrow;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(getLayout());
        setSwipeEnabled(false);
        initViews();
        initListeners();
        initData();
    }

    private int getLayout() {
        return R.layout.acivity_chat_details;
    }

    private void initViews() {
        titleBar = findViewById(R.id.title_bar);
        tvName = findViewById(R.id.tv_name);
        rlAddMember = findViewById(R.id.rl_add_member);
        addMember = findViewById(R.id.add_member);
        clearAllRecords = findViewById(R.id.tv_clear_all_records);
//        rlSearchHistory = findViewById(R.id.rl_search_history);
        mSbStickyChat = findViewById(R.id.sb_sticky_chat);
        mSbMuteNotifications = findViewById(R.id.sb_mute_notifications);
        iv_avatar = findViewById(R.id.iv_avatar);
        rl_user = findViewById(R.id.rl_user);
        ivArrow = findViewById(R.id.iv_right_arrow);
    }

    private void initListeners() {
        rlAddMember.setOnClickListener(this);
//        addMember.setOnClickListener(this);
//        rlSearchHistory.setOnClickListener(this);
        clearAllRecords.setOnClickListener(this);
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        toChatUsername = getIntent().getStringExtra("conversationId");
        if (TextUtils.isEmpty(toChatUsername)) {
            finish();
            return;
        }
        realName = toChatUsername;
        if ("admin".equals(toChatUsername)) {
            tvName.setText(getString(R.string.system_msg));
            iv_avatar.setImageResource(R.drawable.ease_default_avatar);
//            addMember.setVisibility(View.GONE);
        } else if (MPClient.get().isFileHelper(toChatUsername)) {
            tvName.setText(getString(R.string.file_transfer));
            AvatarUtils.setAvatarContent(ChatDetailsActivity.this, iv_avatar);
//            rlAddMember.setVisibility(View.GONE);
            ivArrow.setVisibility(View.GONE);
        } else {
            rl_user.setOnClickListener(this);
//            rlAddMember.setVisibility(View.VISIBLE);
            EaseUser userInfo = EaseUserUtils.getUserInfo(toChatUsername);
            if (userInfo != null) {
                realName = userInfo.getNick();
                avatar = userInfo.getAvatar();
                if (!TextUtils.isEmpty(userInfo.getAlias())) {
                    tvName.setText(userInfo.getAlias());
                    AvatarUtils.setAvatarContent(this, userInfo.getAlias(), avatar, iv_avatar);
                } else {
                    tvName.setText(realName);
                    AvatarUtils.setAvatarContent(this, realName, avatar, iv_avatar);
                }
            }
        }

        //获取会话
        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(Constant.CHATTYPE_SINGLE), true);
        //设置置顶、免打扰  开关
        mSbStickyChat.setChecked(!TextUtils.isEmpty(AppHelper.getInstance().getStickyTime(toChatUsername, EMConversation.EMConversationType.Chat)));
        mSbMuteNotifications.setChecked(!PreferenceUtils.getInstance().isEnableMsgRing(EMClient.getInstance().getCurrentUser(), toChatUsername));

        //先初始化开关，再设置监听
        mSbStickyChat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String stickyTime = "";
                if (isChecked) {
                    stickyTime = String.valueOf(System.currentTimeMillis());
                } else {
                    stickyTime = "";
                }
                AppHelper.getInstance().saveStickyTime(toChatUsername, stickyTime, EMConversation.EMConversationType.Chat);
            }
        });
        mSbMuteNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtils.getInstance().setEnableMsgRing(EMClient.getInstance().getCurrentUser(), toChatUsername, !isChecked);
            }
        });
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
//        if (i == R.id.add_member) {
        if (i == R.id.rl_add_member) {
//            startActivity(new Intent(ChatDetailsActivity.this, NewChatSelectActivity.class)
//                    .putExtra("current_id", toChatUsername)
//                    .putExtra("add_member", true));
            EaseUser easeUser = UserProvider.getInstance().getEaseUser(toChatUsername);
            if (easeUser == null) {
                return;
            }
            ArrayList<Integer> defaultSelectedIds = new ArrayList<>();
            defaultSelectedIds.add(easeUser.getId());
            startActivity(new Intent(activity, GroupAddMemberActivity.class)
                    .putExtra("isCreate", true)
                    .putExtra("pickedUidList", defaultSelectedIds));

        } else if (i == R.id.tv_clear_all_records) {
            emptyHistory();

        } else if (i == R.id.rl_user) {
            if ("admin".equals(toChatUsername)) {
                return;
            }
            Intent intent = new Intent(activity, ContactDetailsActivity.class);
            intent.putExtra("imUserId", toChatUsername);
            startActivity(intent);

        } else {
        }

    }

    /**
     * clear the conversation history
     */
    protected void emptyHistory() {
        String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
        new EaseAlertDialog(this, null, msg, null, new EaseAlertDialog.AlertDialogUser() {

            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    if (conversation != null) {
                        conversation.clearAllMessages();
                    }
                }
            }
        }, true).show();
    }

}
