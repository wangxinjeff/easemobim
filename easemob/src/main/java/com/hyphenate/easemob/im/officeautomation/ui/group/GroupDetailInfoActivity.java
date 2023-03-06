package com.hyphenate.easemob.im.officeautomation.ui.group;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.easeui.domain.MPGroupMemberEntity;
import com.hyphenate.easemob.easeui.ui.EaseDingMsgSendActivity;
import com.hyphenate.easemob.easeui.ui.EaseShowBigImageActivity;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.easeui.widget.EaseExpandGridView;
import com.hyphenate.easemob.im.officeautomation.fragment.EditDialogFragment;
import com.hyphenate.easemob.im.officeautomation.fragment.NormalDialogFragment;
import com.hyphenate.easemob.imlibs.mp.events.EventUserInfoRefresh;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.eventbus.Subscribe;
import com.hyphenate.eventbus.ThreadMode;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupDeleted;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupMemberChanged;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupsChanged;
import com.hyphenate.easemob.im.mp.manager.NoDisturbManager;
import com.hyphenate.easemob.im.mp.ui.ModifyActivity;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.im.officeautomation.domain.NoDisturbEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;
import com.hyphenate.easemob.im.officeautomation.ui.ContactDetailsActivity;
import com.hyphenate.easemob.im.officeautomation.ui.GroupSearchMessageActivity;
import com.hyphenate.easemob.im.officeautomation.ui.group.ChangeGroupOwnerActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.utils.ErrorCode;
import com.hyphenate.easemob.im.officeautomation.utils.MyToast;
import com.kyleduo.switchbutton.SwitchButton;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.hyphenate.easemob.pictureselector.PictureBean;
import com.hyphenate.easemob.pictureselector.PictureSelector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GroupDetailInfoActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "GroupDetail";

    private static final int REQUEST_CODE_GROUP_MEMBERS = 300;
    private static final int REQUEST_CODE_GROUP_ANNOUNCEMENT = 301;
    private static final int REQUEST_CODE_CHOOSE_PICTURE = 302;
    private static final int REQUEST_CAMERA_CODE = 303;

    public static final int RESULT_CODE_SEND_GROUP_NOTIFICATION = 8;

    private MPGroupEntity groupEntity;
    private int chatGroupId;


    private ImageView ivBack;
    private EaseExpandGridView memberGridView;

    private RelativeLayout rlGroupMembers;
    private RelativeLayout rlGroupName;
    private RelativeLayout rlGroupOwner;
    private ImageView chevronGroupOwner;
    private ImageView chevronGroupName;
    private ImageView chevronGroupAvatar;
    private RelativeLayout rlGroupNotify;
    private View rlGroupNotifyDevider;
    private RelativeLayout rlGroupAnnouncement;
    private RelativeLayout rlGroupSearch;
    private RelativeLayout rlSwitchSticky;
    private RelativeLayout rlGroupSave;
    private RelativeLayout rlGroupValidation;
    private RelativeLayout rlGroupAvatar;
    private RelativeLayout rlGroupDisturb;
    private RelativeLayout rlGroupClear;
    private RelativeLayout rlGroupExit;
    private RelativeLayout rlGroupDelete;
    private RelativeLayout rlGroupMute;

    private TextView tvGroupName;
    private TextView tvGroupOwner;
    private TextView tvGroupAnnoucement;

    private AvatarImageView ivAvatar;
    private TextView memberSize;

    private LoginUser loginUser;
    private GridAdapter memberAdapter;

    private SwitchButton btnSwitchValidation;
    private SwitchButton btnSwitchSave;
    private SwitchButton btnSwitchSticky;
    private SwitchButton btnSwitchNoDisturb;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ArrayList<Integer> pickedList = new ArrayList<>();
    private boolean selfIsOwner;
    private ArrayList<MPUserEntity> userEntities;
    private boolean isRegion;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail_info);
        initViews();
        initDatas();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        memberGridView = findViewById(R.id.gridview);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);

        memberSize = findViewById(R.id.tv_members_size);
        rlGroupMembers = findViewById(R.id.rl_group_members);
        rlGroupName = findViewById(R.id.rl_group_name);
        rlGroupOwner = findViewById(R.id.rl_group_owner);
        chevronGroupOwner = findViewById(R.id.iv_owner_chevron);
        chevronGroupAvatar = findViewById(R.id.iv_right);
        chevronGroupName = findViewById(R.id.iv_chevron_name);

        rlGroupNotify = findViewById(R.id.rl_group_notification);
        rlGroupNotifyDevider = findViewById(R.id.rl_group_notification_devider);
        rlGroupAnnouncement = findViewById(R.id.rl_group_announcement);
        rlGroupSearch = findViewById(R.id.rl_group_search);
        rlGroupValidation = findViewById(R.id.rl_group_validation);
        btnSwitchValidation = findViewById(R.id.switch_btn_validation);
        rlSwitchSticky = findViewById(R.id.rl_group_sticky);
        rlGroupSave = findViewById(R.id.rl_group_save);
        rlGroupAvatar = findViewById(R.id.rl_group_avatar);
        rlGroupDisturb = findViewById(R.id.rl_group_disturb);
        rlGroupDelete = findViewById(R.id.rl_group_delete);
        rlGroupExit = findViewById(R.id.rl_group_exit);
        rlGroupClear = findViewById(R.id.rl_group_clear);
        rlGroupMute = findViewById(R.id.rl_group_mute);

        ivAvatar = findViewById(R.id.iv_avatar);
        tvGroupName = findViewById(R.id.tv_name);
        tvGroupOwner = findViewById(R.id.tv_group_owner);
        tvGroupAnnoucement = findViewById(R.id.tv_group_announcement);


        btnSwitchSave = findViewById(R.id.switch_btn_save);
        btnSwitchSticky = findViewById(R.id.switch_btn_sticky);
        btnSwitchNoDisturb = findViewById(R.id.switch_btn_disturb);
        rlGroupValidation.setVisibility(View.GONE);


        ivBack.setOnClickListener(this);
        rlGroupMembers.setOnClickListener(this);
        rlGroupName.setOnClickListener(this);
        rlGroupOwner.setOnClickListener(this);
        rlGroupNotify.setOnClickListener(this);
        rlGroupAnnouncement.setOnClickListener(this);
        rlGroupSearch.setOnClickListener(this);
        rlSwitchSticky.setOnClickListener(this);
        rlGroupSave.setOnClickListener(this);
        rlGroupAvatar.setOnClickListener(this);
        ivAvatar.setOnClickListener(this);
        rlGroupDisturb.setOnClickListener(this);
        rlGroupDelete.setOnClickListener(this);
        rlGroupExit.setOnClickListener(this);
        rlGroupMute.setOnClickListener(this);
        rlGroupClear.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateGroupInfo();
            }
        });

    }

    private void initDatas() {
        loginUser = UserProvider.getInstance().getLoginUser();
        Intent gIntent = getIntent();
        chatGroupId = gIntent.getIntExtra("groupId", -1);
        isRegion = gIntent.getBooleanExtra("isRegion", false);
        if (loginUser == null) {
            return;
        }
        memberAdapter = new GridAdapter(this, R.layout.em_grid_owner, new ArrayList<>());
        memberGridView.setAdapter(memberAdapter);
        memberGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroupMemberEntity entty = (GroupMemberEntity) parent.getItemAtPosition(position);
                if (entty.memberType == GroupMemberType.NORMAL) {
                    startActivity(new Intent(activity, ContactDetailsActivity.class).putExtra("userId", entty.realEntity.getUserId()));
                } else if (entty.memberType == GroupMemberType.REMOVE) {
                    startActivityForResult(new Intent(activity, GroupMember3Activity.class).putExtra("groupEntity", groupEntity).putExtra("isDel", true).putExtra("userEntities", userEntities), 1001);
                } else if (entty.memberType == GroupMemberType.ADD) {
                    startActivityForResult(new Intent(activity, GroupAddMemberActivity.class).putExtra("pickedList", pickedList).putExtra("groupEntity", groupEntity), 1000);
                } else if (entty.memberType == GroupMemberType.MORE) {
                    startActivity(new Intent(activity, GroupMember3Activity.class).putExtra("userEntities", userEntities).putExtra("groupEntity", groupEntity));
                }
            }
        });

        updateGroupInfo();
    }

    private void updateViews() {
        if (groupEntity == null) {
            return;
        }
        if (loginUser == null) {
            finish();
            return;
        }

        selfIsOwner = checkOwner(groupEntity);
        if (selfIsOwner) {
            rlGroupNotify.setVisibility(View.GONE);
            rlGroupNotifyDevider.setVisibility(View.GONE);
            chevronGroupOwner.setVisibility(View.VISIBLE);
            chevronGroupName.setVisibility(View.VISIBLE);
            chevronGroupAvatar.setVisibility(View.VISIBLE);
        } else {
            rlGroupNotify.setVisibility(View.GONE);
            rlGroupNotifyDevider.setVisibility(View.GONE);
            chevronGroupOwner.setVisibility(View.INVISIBLE);
            chevronGroupName.setVisibility(View.INVISIBLE);
            chevronGroupAvatar.setVisibility(View.INVISIBLE);
        }
        tvGroupName.setText(groupEntity.getName());
        tvGroupAnnoucement.setText(groupEntity.getGroupNotice());
        //是否需要群主审批
        if (groupEntity.isMembersOnly()) {
            btnSwitchValidation.setChecked(true);
        } else {
            btnSwitchValidation.setChecked(false);
        }
        EaseUser ownerUser = UserProvider.getInstance().getEaseUserById(groupEntity.getOwnerId());
        if (ownerUser != null) {
            tvGroupOwner.setText(TextUtils.isEmpty(ownerUser.getAlias()) ? ownerUser.getNickname() : ownerUser.getAlias());
        }

        if (groupEntity.isContract()) {
            btnSwitchSave.setChecked(true);
        } else {
            btnSwitchSave.setChecked(false);
        }

        if (groupEntity.isDisturb()) {
            btnSwitchNoDisturb.setChecked(true);
        } else {
            btnSwitchNoDisturb.setChecked(false);
        }

        if (selfIsOwner) {
            rlGroupExit.setVisibility(View.GONE);
            rlGroupDelete.setVisibility(View.VISIBLE);
            rlGroupMute.setVisibility(View.VISIBLE);
        } else {
            rlGroupMute.setVisibility(View.GONE);
            rlGroupDelete.setVisibility(View.GONE);
            rlGroupExit.setVisibility(View.VISIBLE);
        }


        String avatar = groupEntity.getAvatar();
        if (!TextUtils.isEmpty(avatar)) {
            if (!avatar.startsWith("http")) {
                avatar = MPClient.get().getAppServer() + avatar;
            }
            Glide.with(this).load(avatar).apply(RequestOptions.errorOf(R.drawable.ease_group_icon)).into(ivAvatar);
        }else {
            AvatarUtils.setGroupAvatarContent(groupEntity.getName(), ivAvatar);
        }


        try {
            if (TextUtils.isEmpty(AppHelper.getInstance().getStickyTime(groupEntity.getImChatGroupId(), EMConversation.EMConversationType.GroupChat))) {
                btnSwitchSticky.setChecked(false);
            } else {
                btnSwitchSticky.setChecked(true);
            }
        } catch (Exception ignored) {
        }

        memberAdapter.clear();
        memberAdapter.addAll(create());
        memberAdapter.notifyDataSetChanged();

    }

    private List<MPGroupMemberEntity> dealWithGroupMembers(List<MPGroupMemberEntity> memberEntities, MPGroupEntity groupEntity) {
        List<MPGroupMemberEntity> list = new ArrayList<>();
        for (MPGroupMemberEntity item : memberEntities) {
            MPUserEntity userEntity = groupEntity.getUserEntity(item.getUserId());
            if (userEntity != null) {
                item.setNick(userEntity.getRealName());
            }
            list.add(item);
        }
        return list;
    }


    private BasePopupView basePopupView;

    private void dismissDialog() {
        if (basePopupView != null && basePopupView.isShow()) {
            basePopupView.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupMemberChange(EventGroupMemberChanged event) {
        updateGroupInfo();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
        dismissDialogFragment();
        dismissProgressDialog();
    }

    private void dismissProgressDialog() {
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void updateGroupInfo() {
        dialog = ProgressDialog.show(GroupDetailInfoActivity.this, "获取群成员", "加载中...", true, true);
        EMAPIManager.getInstance().getGroupDetailWithMemberList(chatGroupId, isRegion, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                if (isFinishing()) {
                    return;
                }
                try {
                    JSONObject jsonObj = new JSONObject(value);
                    if (jsonObj.optString("status").equals("ERROR")) {
                        MPLog.e(TAG, "getGroupDetailWithMemberList:" + value);
                        finish();
                        return;
                    }
                    JSONObject jsonEntity = jsonObj.optJSONObject("entity");
                    JSONArray jsonUserList = jsonEntity.optJSONArray("userList");
                    JSONObject jsonGroup = jsonEntity.optJSONObject("chatgroup");
                    JSONArray jsonUserGroupRSList = jsonEntity.optJSONArray("userChatGroupRelationshipList");
                    groupEntity = MPGroupEntity.create(jsonGroup);
                    groupEntity.setCluster(isRegion);
                    if (groupEntity != null) {
                        List<MPGroupMemberEntity> memberEntities = MPGroupMemberEntity.create(jsonUserGroupRSList);
                        userEntities = (ArrayList<MPUserEntity>) MPUserEntity.create(jsonUserList);
                        pickedList.clear();
                        for (MPUserEntity entity : userEntities) {
                            pickedList.add(entity.getId());
                        }
                        groupEntity.setUserMaps(userEntities);
                        groupEntity.setMemberEntities(dealWithGroupMembers(memberEntities, groupEntity));
                    }
                    MPEventBus.getDefault().post(new EventGroupsChanged());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressDialog();
                            memberSize.setText(userEntities.size() + "人");
                            swipeRefreshLayout.setRefreshing(false);
                            updateViews();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    MPLog.e(TAG, "getGroupDetailWithMemberList json parse error");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressDialog();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }

            }

            @Override
            public void onError(int error, String errorMsg) {
                MPLog.e(TAG, "getGroupDetailWithMemberList error->" + errorMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });


    }

    private DialogFragment dialogFragment;

    private void dismissDialogFragment() {
        if (dialogFragment != null) {
            dialogFragment.dismissAllowingStateLoss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserInfoRefresh(EventUserInfoRefresh event){
        memberAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.rl_group_members) {
            if (groupEntity == null) {
                return;
            }
            startActivityForResult(new Intent(activity, GroupMember3Activity.class).putExtra("userEntities", userEntities).putExtra("groupEntity", groupEntity), 1000);
        } else if (id == R.id.rl_group_name) {
            if (groupEntity == null) {
                return;
            }
            if (!selfIsOwner) {
                return;
            }
//            new XPopup.Builder(activity).asInputConfirm("群名称", null, groupEntity.getName(),
//                    "请输入群名称", new OnInputConfirmListener() {
//                        @Override
//                        public void onConfirm(String text) {
//                            changeGroupName(text);
//                        }
//                    }, () -> {
//
//                    }, R.layout.dialog_group_detail_edit_name).show();

            new EditDialogFragment.Builder(activity).setTitle("群名称").setHint("请输入群名称")
                    .setContent(groupEntity.getName())
                    .setOnConfirmClickListener(new EditDialogFragment.OnConfirmClickListener() {
                        @Override
                        public void onConfirmClick(String content) {
                            changeGroupName(content);
                        }
                    }).show();

        } else if (id == R.id.rl_group_owner) {
            if (groupEntity == null) {
                return;
            }
            if (!selfIsOwner) {
                return;
            }
            startActivityForResult(new Intent(activity, ChangeGroupOwnerActivity.class).putExtra("groupEntity", groupEntity), REQUEST_CODE_GROUP_MEMBERS);
        } else if (id == R.id.rl_group_notification) {
            Intent intent1 = new Intent(GroupDetailInfoActivity.this, EaseDingMsgSendActivity.class);
            intent1.putExtra(EaseConstant.EXTRA_USER_ID, chatGroupId);
            startActivityForResult(intent1, RESULT_CODE_SEND_GROUP_NOTIFICATION);
        } else if (id == R.id.rl_group_announcement) {
            Intent intent = new Intent(activity, ModifyActivity.class);
            intent.putExtra("content", groupEntity.getGroupNotice());
            intent.putExtra("title", "群公告");
            if (!selfIsOwner) {
                intent.putExtra("onlyRead", true);
            }
            startActivityForResult(intent, REQUEST_CODE_GROUP_ANNOUNCEMENT);
        } else if (id == R.id.rl_group_search) {
            if (groupEntity == null) {
                return;
            }
            startActivity(new Intent(this, GroupSearchMessageActivity.class)
                    .putExtra("groupId", groupEntity.getImChatGroupId()));
        } else if (id == R.id.rl_group_validation) {
        } else if (id == R.id.rl_group_sticky) {
            if (groupEntity == null) {
                return;
            }
            String stickyTime = "";
            if (btnSwitchSticky.isChecked()) {
                btnSwitchSticky.setChecked(false);
            } else {
                stickyTime = String.valueOf(System.currentTimeMillis());
                btnSwitchSticky.setChecked(true);
            }
            AppHelper.getInstance().saveStickyTime(groupEntity.getImChatGroupId(), stickyTime, EMConversation.EMConversationType.GroupChat);
        } else if (id == R.id.rl_group_save) {
            if (btnSwitchSave.isChecked()) {
                btnSwitchSave.setChecked(false);
                deleteGroupFromContract();
            } else {
                btnSwitchSave.setChecked(true);
                saveGroupToContract();
            }
        } else if (id == R.id.rl_group_disturb) {
            if (btnSwitchNoDisturb.isChecked()) {
                btnSwitchNoDisturb.setChecked(false);
                deleteGroupDisturb();
            } else {
                btnSwitchNoDisturb.setChecked(true);
                addGroupDisturb();
            }
        } else if (id == R.id.rl_group_avatar) {
            if (!selfIsOwner) {
                return;
            }
            PictureSelector
                    .create(activity, PictureSelector.SELECT_REQUEST_CODE)
                    .selectPicture(true, 200, 200, 1, 1);
        } else if (id == R.id.iv_avatar) {
            Intent avatarIntent = new Intent(this, EaseShowBigImageActivity.class);
            String avatar = groupEntity.getAvatar();
            if (avatar == null) {
                return;
            }
            if (!avatar.startsWith("http") && avatar.length() > 0) {
                if (avatar.substring(0, 1).equals("/")) {
                    avatar = MPClient.get().getAppServer() + avatar;
                } else {
                    avatar = MPClient.get().getAppServer() + "/" + avatar;
                }
            }
            avatarIntent.putExtra("remote_url", avatar);
            startActivity(avatarIntent);
        } else if (id == R.id.rl_group_exit) {
            if (!selfIsOwner) {
//                basePopupView = new XPopup.Builder(this).asConfirm(null, "确认要退出此群吗？", new OnConfirmListener() {
//                    @Override
//                    public void onConfirm() {
//                        exitGroup();
//                    }
//                }).show();

                new NormalDialogFragment.Builder(activity).setTitle("确认要退出此群吗？")
                        .setOnConfirmClickListener(this::exitGroup).show();
            }
        } else if (id == R.id.rl_group_delete) {
            if (selfIsOwner) {
//                basePopupView = new XPopup.Builder(this).asConfirm(null, "确认要解散此群吗？", new OnConfirmListener() {
//                    @Override
//                    public void onConfirm() {
//                        deleteGroup();
//                    }
//                }).show();

                new NormalDialogFragment.Builder(activity).setTitle("确认要解散此群吗？")
                        .setOnConfirmClickListener(this::deleteGroup).show();
            }
        } else if (id == R.id.rl_group_mute) {
            if (selfIsOwner) {
                startActivity(new Intent(activity, GroupMutesActivity.class).putExtra("groupEntity", groupEntity));
            }
        } else if (id == R.id.rl_group_clear) {
//            basePopupView = new XPopup.Builder(this).asConfirm(null, "确认要清空聊天记录吗？", new OnConfirmListener() {
//                @Override
//                public void onConfirm() {
//                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(groupEntity.getImChatGroupId(), EMConversation.EMConversationType.GroupChat);
//                    if (conversation != null) {
//                        conversation.clearAllMessages();
//                    }
//                    Toast.makeText(GroupDetailInfoActivity.this, "已清空聊天记录", Toast.LENGTH_SHORT).show();
//                }
//            }).show();

            new NormalDialogFragment.Builder(activity).setTitle("确认要清空聊天记录吗？")
                    .setOnConfirmClickListener(() -> {
                        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(groupEntity.getImChatGroupId(), EMConversation.EMConversationType.GroupChat);
                        if (conversation != null) {
                            conversation.clearAllMessages();
                        }
                        Toast.makeText(GroupDetailInfoActivity.this, "已清空聊天记录", Toast.LENGTH_SHORT).show();
                    }).show();
        }
    }

    private void exitGroup() {
        EMAPIManager.getInstance().exitGroup(chatGroupId, isRegion, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            AppHelper.getInstance().getModel().deleteGroupInfo(groupEntity.getImChatGroupId());
                            MPEventBus.getDefault().post(new EventGroupDeleted(groupEntity.getId(), groupEntity.getImChatGroupId()));
                        } catch (Exception e) {
                        }
                        finish();
                    }
                });

            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "退出群失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private void deleteGroup() {
        EMAPIManager.getInstance().deleteGroup(chatGroupId, isRegion, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            AppHelper.getInstance().getModel().deleteGroupInfo(groupEntity.getImChatGroupId());
                            MPEventBus.getDefault().post(new EventGroupDeleted(groupEntity.getId(), groupEntity.getImChatGroupId()));
                        } catch (Exception e) {
                        }
                        finish();
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "解散群失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }


    private void deleteGroupDisturb() {
        EMAPIManager.getInstance().removeGroupDisturb(chatGroupId, groupEntity.isCluster(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        groupEntity.setDisturb(false);
                        NoDisturbManager.getInstance().removeNoDisturb(groupEntity.getImChatGroupId());
                        btnSwitchNoDisturb.setChecked(false);
                    }
                });

            }

            @Override
            public void onError(int error, String errorMsg) {
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (btnSwitchNoDisturb.isChecked()) {
                            btnSwitchNoDisturb.setChecked(false);
                        } else {
                            btnSwitchNoDisturb.setChecked(true);
                        }

                    }
                });

            }
        });
    }

    private void addGroupDisturb() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("isRegion", groupEntity.isCluster());
        } catch (JSONException ignored) {
        }
        EMAPIManager.getInstance().addGroupDisturb(chatGroupId, jsonBody.toString(), groupEntity.isCluster(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        groupEntity.setDisturb(true);
                        NoDisturbEntity entity = new NoDisturbEntity();
                        entity.setGroup(true);
                        entity.setId(groupEntity.getImChatGroupId());
                        entity.setName(groupEntity.getName());
                        entity.setLastUpdateTime(System.currentTimeMillis());
                        NoDisturbManager.getInstance().saveNoDisturb(entity);
                        btnSwitchNoDisturb.setChecked(true);
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (btnSwitchNoDisturb.isChecked()) {
                            btnSwitchNoDisturb.setChecked(false);
                        } else {
                            btnSwitchNoDisturb.setChecked(true);
                        }

                    }
                });
            }
        });
    }

    private void deleteGroupFromContract() {
        EMAPIManager.getInstance().deleteGroupFromContract(chatGroupId, groupEntity.isCluster(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSwitchSave.setChecked(false);
                        groupEntity.setContract(false);
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (btnSwitchSave.isChecked()) {
                            btnSwitchSave.setChecked(false);
                        } else {
                            btnSwitchSave.setChecked(true);
                        }
                    }
                });
            }
        });
    }


    private void saveGroupToContract() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("isRegion", groupEntity.isCluster());
        } catch (JSONException ignored) {
        }
        EMAPIManager.getInstance().saveGroupToContract(chatGroupId, jsonBody.toString(), groupEntity.isCluster(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSwitchSave.setChecked(true);
                        groupEntity.setContract(true);
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (btnSwitchSave.isChecked()) {
                            btnSwitchSave.setChecked(false);
                        } else {
                            btnSwitchSave.setChecked(true);
                        }
                    }
                });
            }
        });
    }

    private void changeGroupInfo(String jsonBody) {
        if (groupEntity == null) {
            return;
        }
        EMAPIManager.getInstance().changeGroupInfo(groupEntity.getId(), groupEntity.isCluster(), jsonBody, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "修改群信息成功！", Toast.LENGTH_SHORT).show();
                        updateGroupInfo();
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                MPLog.e(TAG, "changeGroupInfo error:" + errorMsg);
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "修改群信息失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void changeGroupName(String text) {
        if (groupEntity == null) {
            return;
        }
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("name", text);
            jsonObj.put("isRegion", groupEntity.isCluster() ? 1 : 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        changeGroupInfo(jsonObj.toString());
    }


    /**
     * 群组成员的gridAdapter
     */
    private class GridAdapter extends ArrayAdapter<GroupMemberEntity> {
        private int res;

        public GridAdapter(Context context, int textViewResourceId, List<GroupMemberEntity> objects) {
            super(context, textViewResourceId, objects);
            res = textViewResourceId;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(res, null);
                holder.imageView = convertView.findViewById(R.id.iv_avatar);
                holder.textView = convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final LinearLayout button = convertView.findViewById(R.id.button_avatar);
            GroupMemberEntity entity = getItem(position);
            if (entity == null) {
                return convertView;
            }
            if (entity.memberType == GroupMemberType.NORMAL) {
                holder.imageView.setImageResource(R.drawable.ease_default_avatar);
                MPGroupMemberEntity realEntity = entity.realEntity;
                if (realEntity != null) {
                    MPUserEntity mpUserEntity = groupEntity.getUserEntity(realEntity.getUserId());
                    if (mpUserEntity != null) {
                        holder.textView.setText(TextUtils.isEmpty(mpUserEntity.getAlias()) ? mpUserEntity.getRealName() : mpUserEntity.getAlias());
                        String avatar = mpUserEntity.getAvatar();
                        AvatarUtils.setAvatarContent(activity, mpUserEntity.getRealName(), avatar, holder.imageView);
                    } else {
                        holder.textView.setText("");
                    }
                }
            } else if (entity.memberType == GroupMemberType.ADD) {
                holder.textView.setText("");
                Glide.with(getContext()).load(R.drawable.mp_ic_group_add).into(holder.imageView);
            } else if (entity.memberType == GroupMemberType.REMOVE) {
                holder.textView.setText("");
                holder.imageView.setImageResource(R.drawable.mp_ic_group_sub);

            } else if (entity.memberType == GroupMemberType.MORE) {
                holder.textView.setText("");
                holder.imageView.setImageResource(R.drawable.mp_temp_icon_more);

            }

            return convertView;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    }

    private static class ViewHolder {
        AvatarImageView imageView;
        TextView textView;
    }

    private boolean checkOwner(MPGroupEntity groupEntity) {
        if (groupEntity == null) {
            return false;
        }

        if (loginUser == null) {
            return false;
        }
        return groupEntity.getOwnerId() == loginUser.getId();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_GROUP_ANNOUNCEMENT) {
                String content = data.getStringExtra("content");
                if (content != null) {
                    JSONObject jsonObj = new JSONObject();
                    try {
                        jsonObj.put("groupNotice", content);
                        jsonObj.put("isRegion", groupEntity.isCluster() ? 1 : 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    changeGroupInfo(jsonObj.toString());
                }
            } else if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
                if (data != null) {
                    PictureBean pictureBean = data.getParcelableExtra(PictureSelector.PICTURE_RESULT);
                    if (pictureBean.isCut()) {
                        getImageToView(new File(pictureBean.getPath()));
                    } else {
                        getImageToView(new File(pictureBean.getUri().getPath()));
                    }
//                    String picturePath = data.getStringExtra(PictureSelector.PICTURE_PATH);
//                    if (!TextUtils.isEmpty(picturePath)) {
//                        getImageToView(new File(picturePath));
//                    }
                }

            } else if (requestCode == 1000) {
                List<Integer> pickList = data.getIntegerArrayListExtra("pickList");
                for (Integer id : pickList) {
                    if (!pickedList.contains(id)) {
                        updateGroupInfo();
                        break;
                    }
                }
            } else if (requestCode == 1001 || requestCode == REQUEST_CODE_GROUP_MEMBERS) {
                updateGroupInfo();
            } else if (requestCode == RESULT_CODE_SEND_GROUP_NOTIFICATION) {
                setResult(RESULT_OK, data);
                finish();
            }
        } else if (resultCode == 1000) {
            List<Integer> pickList = data.getIntegerArrayListExtra("pickList");
            if (pickList == null) return;
            for (Integer id : pickList) {
                if (!pickedList.contains(id)) {
                    updateGroupInfo();
                    break;
                }
            }
        }
    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void getImageToView(File avatarFile) {
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser == null) {
            return;
        }
        showProgressDialog();
        EMAPIManager.getInstance().postFile(avatarFile, Constant.FILE_UPLOAD_TYPE_AVATAR, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(final String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        try {
                            JSONObject jsonObj = new JSONObject(value);
                            JSONObject jsonEntty = jsonObj.optJSONObject("entity");
                            String avatarUrl = jsonEntty.optString("url");
                            String md5Val = jsonEntty.optString("md5");
                            if (!TextUtils.isEmpty(avatarUrl)) {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("avatar", avatarUrl);
                                    jsonObj.put("isRegion", groupEntity.isCluster() ? 1 : 0);
                                } catch (JSONException ignored) {
                                }
                                updateAvatarToServer(jsonObject.toString());
                            } else {
                                toastInvalidResponse(TAG, "postFile value failed");
                            }
                        } catch (Exception e) {
                            toastInvalidResponse(TAG, "postFile value jsonException");
                        }
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        try {
                            JSONObject jsonError = new JSONObject(errorMsg);
                            String errorCode = jsonError.optString("errorCode", null);
                            String errorRet = ErrorCode.getInstance().getErrorInfo(activity, errorCode);
                            if (errorRet == null) {
                                errorRet = jsonError.optString("errorDescription");
                            }
                            MyToast.showToast(errorRet);
                        } catch (Exception e) {
                            Toast.makeText(activity, "更新头像失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }


    private void updateAvatarToServer(String jsonContent) {
        changeGroupInfo(jsonContent);
    }

    private List<GroupMemberEntity> create() {
        List<GroupMemberEntity> entities = new ArrayList<>();
        if (groupEntity == null || groupEntity.getMemberEntities() == null || groupEntity.getMemberEntities().isEmpty()) {
            return entities;
        }
        List<MPGroupMemberEntity> memberEntities = groupEntity.getMemberEntities();

        int memberSize = memberEntities.size();

        if (selfIsOwner) {
            if (memberSize <= 10) {
                for (int i = 0; i < memberSize; i++) {
                    GroupMemberEntity entty = new GroupMemberEntity();
                    entty.memberType = GroupMemberType.NORMAL;
                    entty.realEntity = memberEntities.get(i);
                    entities.add(entty);
                }
                GroupMemberEntity addEntity = new GroupMemberEntity();
                addEntity.memberType = GroupMemberType.ADD;
                entities.add(addEntity);

                GroupMemberEntity removeEntity = new GroupMemberEntity();
                removeEntity.memberType = GroupMemberType.REMOVE;
                entities.add(removeEntity);
            } else {
                for (int i = 0; i < 9; i++) {
                    GroupMemberEntity entty = new GroupMemberEntity();
                    entty.memberType = GroupMemberType.NORMAL;
                    entty.realEntity = memberEntities.get(i);
                    entities.add(entty);
                }
                GroupMemberEntity moreEntity = new GroupMemberEntity();
                moreEntity.memberType = GroupMemberType.MORE;
                entities.add(moreEntity);

                GroupMemberEntity addEntity = new GroupMemberEntity();
                addEntity.memberType = GroupMemberType.ADD;
                entities.add(addEntity);

                GroupMemberEntity removeEntity = new GroupMemberEntity();
                removeEntity.memberType = GroupMemberType.REMOVE;
                entities.add(removeEntity);
            }
        } else {
            if (memberSize <= 11) {
                for (int i = 0; i < memberSize; i++) {
                    GroupMemberEntity entty = new GroupMemberEntity();
                    entty.memberType = GroupMemberType.NORMAL;
                    entty.realEntity = memberEntities.get(i);
                    entities.add(entty);
                }
                GroupMemberEntity addEntity = new GroupMemberEntity();
                addEntity.memberType = GroupMemberType.ADD;
                entities.add(addEntity);
            } else {
                for (int i = 0; i < 10; i++) {
                    GroupMemberEntity entty = new GroupMemberEntity();
                    entty.memberType = GroupMemberType.NORMAL;
                    entty.realEntity = memberEntities.get(i);
                    entities.add(entty);
                }
                GroupMemberEntity moreEntity = new GroupMemberEntity();
                moreEntity.memberType = GroupMemberType.MORE;
                entities.add(moreEntity);

                GroupMemberEntity addEntity = new GroupMemberEntity();
                addEntity.memberType = GroupMemberType.ADD;
                entities.add(addEntity);
            }

        }

        return entities;
    }


    class GroupMemberEntity {
        GroupMemberType memberType;
        MPGroupMemberEntity realEntity;
    }


    enum GroupMemberType {
        MORE,
        NORMAL,
        ADD,
        REMOVE
    }


}
