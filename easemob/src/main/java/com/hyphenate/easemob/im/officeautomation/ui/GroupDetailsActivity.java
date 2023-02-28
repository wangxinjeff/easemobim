/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easemob.im.officeautomation.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.entity.Media;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.easeui.domain.MPGroupMemberEntity;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.easeui.ui.EaseDingMsgSendActivity;
import com.hyphenate.easemob.easeui.ui.EaseGroupListener;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.easeui.widget.EaseAlertDialog;
import com.hyphenate.easemob.easeui.widget.EaseAlertDialog.AlertDialogUser;
import com.hyphenate.easemob.easeui.widget.EaseExpandGridView;
import com.hyphenate.easemob.easeui.widget.EaseSwitchButton;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.eventbus.Subscribe;
import com.hyphenate.eventbus.ThreadMode;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.imlibs.message.MessageUtils;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupDeleted;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupsChanged;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.ui.NewChatSelectActivity;
import com.hyphenate.easemob.im.officeautomation.ui.PersonalCardActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.utils.ImageTools;
import com.hyphenate.easemob.im.officeautomation.utils.MyToast;
import com.hyphenate.util.EMLog;
import com.hyphenate.easemob.imlibs.mp.utils.MPPathUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupDetailsActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "GroupDetailsActivity";
    private static final int REQUEST_CODE_ADD_USER = 0;
    private static final int REQUEST_CODE_EXIT = 1;
    private static final int REQUEST_CODE_EXIT_DELETE = 2;
    private static final int REQUEST_CODE_EDIT_GROUPNAME = 5;
    private static final int REQUEST_CODE_EDIT_GROUP_DESCRIPTION = 6;
//    private static final int REQUEST_CODE_EDIT_GROUP_EXTENSION = 7;

    private static final int REQUEST_CODE_ = 8;
    private static final int REQUEST_CODE_CHOOSE_PICTURE = 9;
    private static final int REQUEST_CODE_PICTURE_CROP = 10;
    private static final int REQUEST_CODE_GROUP_MEMBERS  = 11;

    private static final int REQUEST_CODE_DING_MSG = 100;
    public static final int RESULT_CODE_DING_MSG = 101;

//    private String imGroupId;
    private ProgressBar loadingPB;
    private View card_view;
    private ImageView group_avatar;
    private Button exitBtn;
    private Button deleteBtn;
//    private EMGroup group;
    private MPGroupEntity groupEntity;
    private OwnerAdminAdapter ownerAdminAdapter;
    private ProgressDialog progressDialog;
    private TextView announcementText;
    private TextView tvDesc;
    private File groupAvatar;    private ImageView ivAdd;

    String st = "";

    private EaseSwitchButton switchButton;
    private EaseSwitchButton muteNotificationSwitch;
    private SwipeRefreshLayout srl;
    private EaseSwitchButton stickySwitch;
    private EMPushConfigs pushConfigs;

    private String operationUserId = "";

    final private List<String> adminList = Collections.synchronizedList(new ArrayList<String>());

    GroupChangeListener groupChangeListener;

    private RelativeLayout changeGroupNameLayout;
    private RelativeLayout changeGroupDescriptionLayout;
    private RelativeLayout rl_switch_sticky;
    private RelativeLayout rlGroupMembers;

    private RelativeLayout rl_switch_block_groupmsg;
    private RelativeLayout searchLayout;
    private RelativeLayout muteNotificationLayout;
    private RelativeLayout announcementLayout;
    private RelativeLayout groupNotiLayout;
    private RelativeLayout sharedFilesLayout;
    private RelativeLayout clearAllHistory;
    private TextView tvGroupName;

    private LoginUser loginUser;


    private void sendGroupMessage(String nick, String avatar){
        if (groupEntity == null){
            return;
        }
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.addBody(new EMCmdMessageBody(Constant.CMD_ACTION_GROUPS_CHANGED));
        message.setTo(groupEntity.getImChatGroupId());
        message.setChatType(EMMessage.ChatType.GroupChat);
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(new JSONObject().put("id", groupEntity.getImChatGroupId()).put("name", nick).put("avatar", avatar));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        message.setAttribute(Constant.CMD_EXT_GROUPS, jsonArray);
        MessageUtils.sendMessage(message);
        EMMessage selfMessage = EMMessage.createSendMessage(EMMessage.Type.TXT);
        selfMessage.addBody(new EMTextMessageBody("'我'" + getString(R.string.updated_group_info)));
        selfMessage.setTo(groupEntity.getImChatGroupId());
        selfMessage.setChatType(EMMessage.ChatType.GroupChat);
        selfMessage.setAttribute(Constant.MESSAGE_TYPE_RECALL, true);
        EMClient.getInstance().chatManager().saveMessage(selfMessage);
    }

    private void updateViews(){
        if (groupEntity == null){
            return;
        }
//        tvGroupName.setText(getGroupName() + "(" + group.getMemberCount() + st);
        tvGroupName.setText(getGroupName());
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser == null) {
            return;
        }
        if (groupEntity.getOwnerId() == 0 || groupEntity.getOwnerId() != loginUser.getId()) {
            // 显示退出按钮
            exitBtn.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.GONE);
            changeGroupNameLayout.setVisibility(View.GONE);
            changeGroupDescriptionLayout.setVisibility(View.GONE);
//            if (group.getAdminList() != null && group.getAdminList().contains(EMClient.getInstance().getCurrentUser())){
//                changeGroupNameLayout.setVisibility(View.VISIBLE);
//            }
        } else {
            // 显示解散按钮
            exitBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.VISIBLE);
        }

        if ((groupEntity.getOwnerId() > 0 && groupEntity.getOwnerId() == loginUser.getId()) || groupEntity.isAllowInvites()) {
            ivAdd.setVisibility(View.VISIBLE);
        } else {
            ivAdd.setVisibility(View.INVISIBLE);
        }

        // update block
//        EMLog.d(TAG, "group msg is blocked:" + groupEntity.isMsgBlocked());
//        if (group.isMsgBlocked()) {
//            switchButton.openSwitch();
//        } else {
//            switchButton.closeSwitch();
//        }
//        List<String> disabledIds = EMClient.getInstance().pushManager().getNoPushGroups();
//        if (disabledIds != null && disabledIds.contains(imGroupId)) {
//            muteNotificationSwitch.openSwitch();
//        } else {
//            muteNotificationSwitch.closeSwitch();
//        }

        announcementText.setText(groupEntity.getGroupNotice());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_group_details);
        initViews();
        initListeners();
        chatGroupId = getIntent().getIntExtra("groupId", -1);
        loginUser = UserProvider.getInstance().getLoginUser();
        card_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCurrentOwner(groupEntity)) {
//                    Intent intent = ImageHandleUtils.pickSingleImage(activity, true);
//                    startActivityForResult(intent, REQUEST_CODE_CHOOSE_PICTURE);
                    Intent intent = new Intent(activity, PickerActivity.class);
                    intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE);
                    long maxSize = 10485760L;
                    intent.putExtra(PickerConfig.MAX_SELECT_SIZE,maxSize); //default 10MB (Optional)
                    intent.putExtra(PickerConfig.MAX_SELECT_COUNT,1);  //default 40 (Optional)
                    startActivityForResult(intent, REQUEST_CODE_CHOOSE_PICTURE);
                }
            }
        });

        ownerAdminAdapter = new OwnerAdminAdapter(this, R.layout.em_grid_owner, new ArrayList<String>());
        EaseExpandGridView ownerAdminGridview = (EaseExpandGridView) findViewById(R.id.owner_and_administrators_grid_view);
        ownerAdminGridview.setAdapter(ownerAdminAdapter);

        // 保证每次进详情看到的都是最新的group
        updateGroup();
    }


    private void initViews(){
        st = ")";
        clearAllHistory = (RelativeLayout) findViewById(R.id.clear_all_history);
        loadingPB = (ProgressBar) findViewById(R.id.progressBar);
        exitBtn = (Button) findViewById(R.id.btn_exit_grp);
        deleteBtn = (Button) findViewById(R.id.btn_exitdel_grp);
        tvDesc = findViewById(R.id.tv_desc);
        changeGroupNameLayout = (RelativeLayout) findViewById(R.id.rl_change_group_name);
        changeGroupDescriptionLayout = (RelativeLayout) findViewById(R.id.rl_change_group_description);
        rlGroupMembers = (RelativeLayout) findViewById(R.id.rl_group_members);
        tvGroupName = ((TextView) findViewById(R.id.group_name));
        ivAdd = findViewById(R.id.iv_add);

        //设置群头像
        card_view = findViewById(R.id.card_view);
        group_avatar = findViewById(R.id.group_avatar);
        rl_switch_sticky = (RelativeLayout) findViewById(R.id.rl_switch_sticky);
        stickySwitch = (EaseSwitchButton) findViewById(R.id.switch_btn_sticky);
//        if (TextUtils.isEmpty(AppHelper.getInstance().getStickyTime(imGroupId, EMConversationType.GroupChat))) {
//            stickySwitch.closeSwitch();
//        } else {
//            stickySwitch.openSwitch();
//        }

        rl_switch_block_groupmsg = (RelativeLayout) findViewById(R.id.rl_switch_block_groupmsg);
        switchButton = (EaseSwitchButton) findViewById(R.id.switch_btn);
        searchLayout = (RelativeLayout) findViewById(R.id.rl_search);

        muteNotificationLayout = (RelativeLayout) findViewById(R.id.rl_switch_mute_notification);
        muteNotificationSwitch = (EaseSwitchButton) findViewById(R.id.switch_block_offline_message);

        announcementLayout = (RelativeLayout) findViewById(R.id.layout_group_announcement);
        announcementText = (TextView) findViewById(R.id.tv_group_announcement_value);

        // Group notification
        groupNotiLayout = (RelativeLayout) findViewById(R.id.layout_group_notification);

        sharedFilesLayout = (RelativeLayout) findViewById(R.id.layout_share_files);
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateGroup();
            }
        });

//        memberAdapter = new GridAdapter(this, R.layout.em_grid_owner, new ArrayList<>());
//        EaseExpandGridView userGridView = findViewById(R.id.gridview);
//        userGridView.setAdapter(memberAdapter);


    }

    private void initListeners(){
        clearAllHistory.setOnClickListener(this);
        changeGroupNameLayout.setOnClickListener(this);
        changeGroupDescriptionLayout.setOnClickListener(this);
        rlGroupMembers.setOnClickListener(this);
        rl_switch_sticky.setOnClickListener(this);
        rl_switch_block_groupmsg.setOnClickListener(this);
        searchLayout.setOnClickListener(this);
        muteNotificationLayout.setOnClickListener(this);
        announcementLayout.setOnClickListener(this);
        groupNotiLayout.setOnClickListener(this);
        sharedFilesLayout.setOnClickListener(this);
        pushConfigs = EMClient.getInstance().pushManager().getPushConfigs();

        groupChangeListener = new GroupChangeListener();
        EMClient.getInstance().groupManager().addGroupChangeListener(groupChangeListener);
        ivAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String st11 = getResources().getString(R.string.Add_a_button_was_clicked);
                EMLog.d(TAG, st11);
                if (groupEntity == null) {
                    return;
                }
                // 进入选人页面
                startActivityForResult(
                        (new Intent(GroupDetailsActivity.this, NewChatSelectActivity.class).putExtra("imGroupId", groupEntity.getImChatGroupId()).putExtra("add_member", true)),
                        REQUEST_CODE_ADD_USER);
            }
        });

    }



    //显示群头像
    private void showGroupAvatar() {
        String avatar = getGroupAvatar();
        if (TextUtils.isEmpty(avatar)) {
            group_avatar.setImageResource(R.drawable.ease_group_icon);
        } else {
            GlideUtils.load(this, avatar, R.drawable.ease_group_icon, group_avatar);
        }
    }


    boolean isCurrentOwner(MPGroupEntity groupEntity) {
        if (groupEntity == null){
            return false;
        }
        int ownerId = groupEntity.getOwnerId();
        if (ownerId <= 0) {
            return false;
        }
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser == null) {
            return false;
        }
        return ownerId == loginUser.getId();
    }

    boolean isOwner(MPGroupEntity groupEntity){
        if (groupEntity == null){
            return false;
        }
        if (loginUser == null) {
            return false;
        }
        return groupEntity.getOwnerId() == loginUser.getId();
    }

//    boolean isCurrentAdmin(EMGroup group) {
//        if (group == null){
//            return false;
//        }
//        synchronized (adminList) {
//            String currentUser = EMClient.getInstance().getCurrentUser();
//            for (String admin : adminList) {
//                if (currentUser.equals(admin)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    boolean isAdmin(String id) {
        synchronized (adminList) {
            for (String admin : adminList) {
                if (id.equals(admin)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String st1 = getResources().getString(R.string.being_added);
        String st2 = getResources().getString(R.string.is_quit_the_group_chat);
        String st3 = getResources().getString(R.string.chatting_is_dissolution);
        String st4 = getResources().getString(R.string.are_empty_group_of_news);
        final String st5 = getResources().getString(R.string.is_modify_the_group_name);
        final String st6 = getResources().getString(R.string.Modify_the_group_name_successful);
        final String st7 = getResources().getString(R.string.change_the_group_name_failed_please);

        final String st8 = getResources().getString(R.string.is_modify_the_group_description);
        final String st9 = getResources().getString(R.string.Modify_the_group_description_successful);
        final String st10 = getResources().getString(R.string.change_the_group_description_failed_please);
        final String st11 = getResources().getString(R.string.Modify_the_group_extension_successful);
        final String st12 = getResources().getString(R.string.change_the_group_extension_failed_please);

        if (resultCode != RESULT_CANCELED) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailsActivity.this);
                progressDialog.setMessage(st1);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            switch (requestCode) {
                case REQUEST_CODE_DING_MSG:
                    setResult(RESULT_CODE_DING_MSG, data);
                    finish();
                    break;
                case REQUEST_CODE_ADD_USER:// 添加群成员
                    ArrayList<Integer> selectedMemIds = data.getIntegerArrayListExtra("newmembers");
                    progressDialog.setMessage(st1);
                    addMembersToGroup(selectedMemIds);
                    break;
                case REQUEST_CODE_EXIT: // 退出群
                    progressDialog.setMessage(st2);
                    progressDialog.show();
                    exitGrop();
                    break;
                case REQUEST_CODE_EXIT_DELETE: // 解散群
                    progressDialog.setMessage(st3);
                    progressDialog.show();
                    deleteGrop();
                    break;

                case REQUEST_CODE_EDIT_GROUPNAME: //修改群名称
                    final String returnData = data.getStringExtra("data");
                    updateGroupInfo(returnData, null, null);
                    break;
                case REQUEST_CODE_EDIT_GROUP_DESCRIPTION:
                    final String returnData1 = data.getStringExtra("data");
                    if (!TextUtils.isEmpty(returnData1)) {
                        progressDialog.setMessage(st5);
//                        progressDialog.show();
                        updateGroupInfo(null, null, returnData1);
                    }
                    break;
//                case REQUEST_CODE_EDIT_GROUP_EXTENSION: {
//                    final String returnExtension = data.getStringExtra("data");
//                    if (!TextUtils.isEmpty(returnExtension)) {
//                        progressDialog.setMessage(st5);
//                        progressDialog.show();
//
//                        EaseUI.getInstance().execute(new Runnable() {
//                            public void run() {
//                                try {
//                                    EMClient.getInstance().groupManager().updateGroupExtension(imGroupId, returnExtension);
//                                    runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            dismissDialog();
//                                            Toast.makeText(getApplicationContext(), st11, Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                } catch (HyphenateException e) {
//                                    e.printStackTrace();
//                                    runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            dismissDialog();
//                                            Toast.makeText(getApplicationContext(), st12, Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            }
//                        });
//                    }
//                }
//                break;
                case UCrop.REQUEST_CROP:
                    Uri croppedFileUri = UCrop.getOutput(data);
                    if (croppedFileUri != null) {
                        //获取默认的下载目录
                        String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        File saveFile = new File(downloadsDirectoryPath, "avatar.jpg");
                        //保存下载的图片
                        FileInputStream inStream = null;
                        FileOutputStream outStream = null;
                        FileChannel inChannel = null;
                        FileChannel outChannel = null;
                        try {
                            inStream = new FileInputStream(new File(croppedFileUri.getPath()));
                            outStream = new FileOutputStream(saveFile);
                            inChannel = inStream.getChannel();
                            outChannel = outStream.getChannel();
                            inChannel.transferTo(0, inChannel.size(), outChannel);
//                            Toast.makeText(getActivity(), "裁切后的图片保存在：" + saveFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                outChannel.close();
                                outStream.close();
                                inChannel.close();
                                inStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        getImageToView(saveFile);
                    } else {
                        MyToast.showToast("Error crop");
                    }


                    break;
                case REQUEST_CODE_CHOOSE_PICTURE:
                    if (data != null) {
                        ArrayList<Media> retMedias = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
                        if (retMedias != null) {
                            for (Media media : retMedias) {
                                if (media.mediaType == 0) {
                                    Uri imageUri;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        imageUri = FileProvider.getUriForFile(activity, getPackageName() + ".easemob", new File(media.path));//通过FileProvider创建一个content类型的Uri
                                    } else {
                                        imageUri = Uri.fromFile(new File(media.path));
                                    }
                                    File saveFile = new File(MPPathUtil.getInstance().getImagePath(), System.currentTimeMillis() + ".png");
                                    groupAvatar = saveFile;
                                    UCrop.of(imageUri, Uri.fromFile(saveFile))
                                            .withAspectRatio(1, 1).withMaxResultSize(300, 300).start(activity);
                                }
                            }
                        }
                    }
                    break;
                case REQUEST_CODE_PICTURE_CROP:
                    if(data == null){
                        return;
                    }
                    Bundle extras = data.getExtras();
                    if (extras == null) {
                        return;
                    }
                    Bitmap bitmap = extras.getParcelable("data");
                    if (bitmap == null) {
                        return;
                    }
                    if (groupAvatar == null) {
                        groupAvatar = new File(MPPathUtil.getInstance().getImagePath(), System.currentTimeMillis() + ".png");
                    }
                    ImageTools.savePhotoToSDCard(bitmap, groupAvatar.getPath());
                    Glide.with(activity).load(groupAvatar).into(group_avatar);
                    getImageToView(groupAvatar);
                    break;
                case REQUEST_CODE_GROUP_MEMBERS:
                    updateGroup();
                    break;
                case 200:
                    updateGroup();;
                default:
                    break;
            }
        }

    }

    private String getGroupName() {
        if (groupEntity != null) {
            return groupEntity.getName();
        }
        if (chatGroupId == 0) {
            return null;
        }
        GroupBean groupInfo = EaseUserUtils.getGroupInfoById(chatGroupId);
        if (groupInfo == null){
            return null;
        }
        return groupInfo.getNick();
    }

    private String getGroupAvatar() {
        if (groupEntity != null) {
            return groupEntity.getAvatar();
        }
        if (chatGroupId == 0) {
            return null;
        }
        GroupBean groupInfo = EaseUserUtils.getGroupInfoById(chatGroupId);
        if (groupInfo == null){
            return null;
        }
        return groupInfo.getAvatar();
    }

//    private void refreshOwnerAdminAdapter() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                ownerAdminAdapter.clear();
//                if (group == null){
//                    return;
//                }
//                ownerAdminAdapter.add(group.getOwner());
//                synchronized (adminList) {
//                    ownerAdminAdapter.addAll(adminList);
//                }
//                ownerAdminAdapter.notifyDataSetChanged();
//            }
//        });
//    }

    private void debugList(String str, List<String> list) {
        EMLog.d(TAG, str);
        for (String member : list) {
            EMLog.d(TAG, "    " + member);
        }
    }


    DialogFragment mDialogFragment;

    /**
     * 点击退出群组按钮
     *
     * @param view
     */
    public void exitGroup(View view) {
//        startActivityForResult(new Intent(this, ExitGroupDialog.class), REQUEST_CODE_EXIT);
        ArrayList<String> phoneList = new ArrayList<>();
        phoneList.add(getString(R.string.ok));
        dismiss();
        mDialogFragment = new CircleDialog.Builder()
                .setItems(phoneList, new OnLvItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        if (position == 0) {
                            exitGrop();
                        }
                        return false;
                    }
                }).setNegative(getString(R.string.cancel), null).show(getSupportFragmentManager());

    }


    private void dismiss(){
        if (mDialogFragment != null){
            mDialogFragment.dismissAllowingStateLoss();
        }
    }

    /**
     * 点击解散群组按钮
     *
     * @param view
     */
    public void exitDeleteGroup(View view) {
//        startActivityForResult(new Intent(this, ExitGroupDialog.class).putExtra("deleteToast", getString(R.string.dissolution_group_hint)),
//                REQUEST_CODE_EXIT_DELETE);
        ArrayList<String> phoneList = new ArrayList<>();
        phoneList.add(getString(R.string.ok));
        phoneList.add(getString(R.string.cancel));
        dismiss();
        new XPopup.Builder(this).asBottomList(null, phoneList.toArray(new String[0]), new OnSelectListener() {
            @Override
            public void onSelect(int position, String text) {
                if (position == 0) {
                    deleteGrop();
                }
            }
        }).show();
//        mDialogFragment = new CircleDialog.Builder()
//                .setItems(phoneList, new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                        if (position == 0) {
//                            deleteGrop();
//                        }
//                    }
//                }).setNegative(getString(R.string.cancel), null).show(getSupportFragmentManager());

    }

    /**
     * 清空群聊天记录
     */
    private void clearGroupHistory() {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(groupEntity.getImChatGroupId(), EMConversationType.GroupChat);
        if (conversation != null) {
            conversation.clearAllMessages();
        }
        Toast.makeText(this, R.string.messages_are_empty, Toast.LENGTH_SHORT).show();
    }

    /**
     * 退出群组
     */
    private void exitGrop() {
        if (chatGroupId <= 0) {
            return;
        }

        String st1 = getResources().getString(R.string.Exit_the_group_chat_failure);
        EMAPIManager.getInstance().exitGroup(chatGroupId, false, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        dismissDialog();
                        AppHelper.getInstance().getModel().delGroupInfoById(chatGroupId);
                        MPEventBus.getDefault().post(new EventGroupDeleted());
                        setResult(RESULT_OK);
                        finish();
                        if (ChatActivity.activityInstance != null)
                            ChatActivity.activityInstance.finish();
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        dismissDialog();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Exit_the_group_chat_failure) + " " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    /**
     * 解散群组
     */
    private void deleteGrop() {
        deleteGroupByRest();
    }

    private void deleteGroupByRest(){
        if (chatGroupId <= 0) {
            return;
        }

        EMAPIManager.getInstance().deleteGroup(chatGroupId, false, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        dismissDialog();
                        AppHelper.getInstance().getModel().delGroupInfoById(chatGroupId);
                        setResult(RESULT_OK);
                        MPEventBus.getDefault().post(new EventGroupDeleted());
                        finish();
                        if (ChatActivity.activityInstance != null)
                            ChatActivity.activityInstance.finish();
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        dismissDialog();
                        Toast.makeText(getApplicationContext(), R.string.Dissolve_group_chat_tofail, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void updateGroupTitle() {
//        ((TextView) findViewById(R.id.group_name)).setText(getGroupName() + "(" + group.getMemberCount()
//                + st);
        ((TextView) findViewById(R.id.group_name)).setText(getGroupName());
    }


    /**
     * 增加群成员
     *
     * @param newmembers
     */
    private void addMembersToGroup(ArrayList<Integer> newmembers) {
        if (groupEntity == null) {
            MPLog.e(TAG, "groupEntity is null");
            return;
        }
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("userIdList", new JSONArray(newmembers));
            jsonObj.put("isRegion", groupEntity.isCluster() ? 1 : 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser == null) {
            MPLog.e(TAG, "loginUser is null");
            return;
        }

        if (groupEntity.getOwnerId() == loginUser.getId()) {
            progressDialog.show();
            EMAPIManager.getInstance().addMembersToGroup(groupEntity.getId(), jsonObj.toString(), new EMDataCallBack<String>() {
                @Override
                public void onSuccess(String value) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            updateGroupTitle();
                            dismissDialog();
                        }
                    });
                }

                @Override
                public void onError(int error, String errorMsg) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            dismissDialog();
                            final String st6 = getResources().getString(R.string.Add_group_members_fail);
//                            if (e.getErrorCode() == EMError.GROUP_MEMBERS_FULL){
//                                Toast.makeText(getApplicationContext(), st6 + getString(R.string.excced_the_maximux_group_size_limit), Toast.LENGTH_LONG).show();
//                            }else{
//                                Toast.makeText(getApplicationContext(), st6, Toast.LENGTH_LONG).show();
//                            }
                            Toast.makeText(getApplicationContext(), st6, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        } else {
            //TODO. 群成员邀请进群
            // 一般成员调用invite方法
//            EMClient.getInstance().groupManager().inviteUser(imGroupId, addMemList.toArray(new String[0]), null);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.rl_switch_block_groupmsg) {
            toggleBlockGroup();

        } else if (i == R.id.rl_switch_sticky) {
            String stickyTime = "";
            if (stickySwitch.isSwitchOpen()) {
                stickySwitch.closeSwitch();
            } else {
                stickyTime = String.valueOf(System.currentTimeMillis());
                stickySwitch.openSwitch();
            }
//            AppHelper.getInstance().saveStickyTime(imGroupId, stickyTime, EMConversationType.GroupChat);

        } else if (i == R.id.clear_all_history) {
            String st9 = getResources().getString(R.string.sure_to_empty_this);
            new EaseAlertDialog(GroupDetailsActivity.this, null, st9, null, new AlertDialogUser() {

                @Override
                public void onResult(boolean confirmed, Bundle bundle) {
                    if (confirmed) {
                        clearGroupHistory();
                    }
                }
            }, true).show();


        } else if (i == R.id.rl_change_group_name) {
            if (groupEntity == null) {
                return;
            }
            startActivityForResult(new Intent(this, EditActivity.class).putExtra("data", getGroupName()).putExtra("editable", isCurrentOwner(groupEntity)),
                    REQUEST_CODE_EDIT_GROUPNAME);

        } else if (i == R.id.rl_change_group_description) {
            if (groupEntity == null) {
                return;
            }
            startActivityForResult(new Intent(this, EditActivity.class).putExtra("data", groupEntity.getDescription()).
                            putExtra("title", getString(R.string.change_the_group_description)).putExtra("editable", isCurrentOwner(groupEntity)).putExtra("max_length", 100),
                    REQUEST_CODE_EDIT_GROUP_DESCRIPTION);

        } else if (i == R.id.rl_group_members){
//            startActivityForResult(new Intent(this, GroupMembersActivity.class).putExtra("groupId", imGroupId),REQUEST_CODE_GROUP_MEMBERS);

        }else if (i == R.id.rl_search) {
//            startActivity(new Intent(this, GroupSearchMessageActivity.class).putExtra("groupId", imGroupId));


        } else if (i == R.id.rl_switch_mute_notification) {
            toggleBlockOfflineMsg();


        } else if (i == R.id.layout_group_announcement) {
            showAnnouncementDialog();

            // To send group ack message.
        } else if (i == R.id.layout_group_notification) {// Start the ding-type msg send ui.
            EMLog.i(TAG, "Intent to the ding-msg send activity.");
//            Intent intent = new Intent(GroupDetailsActivity.this, EaseDingMsgSendActivity.class);
//            intent.putExtra(EaseConstant.EXTRA_USER_ID, imGroupId);
//            startActivityForResult(intent, REQUEST_CODE_DING_MSG);

        } else if (i == R.id.layout_share_files) {
//            startActivity(new Intent(this, SharedFilesActivity.class).putExtra("groupId", imGroupId));

        } else {
        }

    }

    private void showAnnouncementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.group_announcement);
        if (groupEntity == null) {
            return;
        }
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser == null) {
            return;
        }
        if (groupEntity.getOwnerId() == loginUser.getId()) {
            final EditText et = new EditText(GroupDetailsActivity.this);
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
            et.setText(groupEntity.getGroupNotice());
            builder.setView(et);
            builder.setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String text = et.getText().toString();
                            if (!text.equals(groupEntity.getGroupNotice())) {
                                dialog.dismiss();
                                updateAnnouncement(text);
                            }
                        }
                    });
        } else {
            builder.setMessage(groupEntity.getGroupNotice());
            builder.setPositiveButton(R.string.ok, null);
        }
        builder.show();
    }

    private void sendUpdateAnnouncementSuccess(String announcement){
        if (groupEntity == null) {
            return;
        }
        String title = "修改了群公告：" + announcement;
        EMMessage message = EMMessage.createTxtSendMessage(title, groupEntity.getImChatGroupId());
        message.setChatType(EMMessage.ChatType.GroupChat);
        JSONObject jsonExtMsg = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(EMClient.getInstance().getCurrentUser());
            jsonExtMsg.put("type", "notice");
            jsonExtMsg.put("title", "'%s'" + title);
            jsonExtMsg.put("args", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        message.setAttribute("extMsg", jsonExtMsg);
        MessageUtils.sendMessage(message);
    }

    /**
     * update with the passed announcement
     *
     * @param announcement
     */
    private void updateAnnouncement(final String announcement) {
        createProgressDialog();
        progressDialog.setMessage(getString(R.string.tip_updating));
        progressDialog.show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("groupNotice", announcement);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EMAPIManager.getInstance().putGroupInfo(groupEntity.getId(), groupEntity.isCluster(), jsonBody.toString(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissDialog();
                        announcementText.setText(announcement);
                        sendUpdateAnnouncementSuccess(announcement);
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissDialog();
                        Toast.makeText(GroupDetailsActivity.this, "update fail," + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void toggleBlockOfflineMsg() {
        if (EMClient.getInstance().pushManager().getPushConfigs() == null) {
            return;
        }
        if (groupEntity == null) {
            return;
        }
        progressDialog = createProgressDialog();
        progressDialog.setMessage(getString(R.string.Is_change_setting));
        progressDialog.show();
//		final ArrayList list = (ArrayList) Arrays.asList(imGroupId);
        final List<String> list = new ArrayList<String>();
        list.add(groupEntity.getImChatGroupId());
        EaseUI.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (muteNotificationSwitch.isSwitchOpen()) {
                        EMClient.getInstance().pushManager().updatePushServiceForGroup(list, false);
                    } else {
                        EMClient.getInstance().pushManager().updatePushServiceForGroup(list, true);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissDialog();
                            if (muteNotificationSwitch.isSwitchOpen()) {
                                muteNotificationSwitch.closeSwitch();
                            } else {
                                muteNotificationSwitch.openSwitch();
                            }
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissDialog();
                            Toast.makeText(GroupDetailsActivity.this, "progress failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private ProgressDialog createProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(GroupDetailsActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        return progressDialog;
    }

    private void dismissDialog(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }



    private void toggleBlockGroup() {
        if (groupEntity == null) {
            return;
        }
        if (switchButton.isSwitchOpen()) {
            EMLog.d(TAG, "change to unblock group msg");
            createProgressDialog();
            progressDialog.setMessage(getString(R.string.Is_unblock));
            progressDialog.show();
            EaseUI.getInstance().execute(new Runnable() {
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().unblockGroupMessage(groupEntity.getImChatGroupId());
                        runOnUiThread(new Runnable() {
                            public void run() {
                                switchButton.closeSwitch();
                                dismissDialog();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                dismissDialog();
                                Toast.makeText(getApplicationContext(), R.string.remove_group_of, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
            });

        } else {
            String st8 = getResources().getString(R.string.group_is_blocked);
            final String st9 = getResources().getString(R.string.group_of_shielding);
            EMLog.d(TAG, "change to block group msg");
            createProgressDialog();
            progressDialog.setMessage(st8);
            progressDialog.show();
            EaseUI.getInstance().execute(new Runnable() {
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().blockGroupMessage(groupEntity.getImChatGroupId());
                        runOnUiThread(new Runnable() {
                            public void run() {
                                switchButton.openSwitch();
                                dismissDialog();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                dismissDialog();
                                Toast.makeText(getApplicationContext(), st9, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
            });
        }
    }

    class MemberMenuDialog extends Dialog {

        private MemberMenuDialog(@NonNull Context context) {
            super(context);

            init();
        }

        void init() {
            final MemberMenuDialog dialog = this;
            dialog.setTitle("group");
            dialog.setContentView(R.layout.em_chatroom_member_menu);

            int ids[] = {
                    R.id.menu_item_add_admin,
                    R.id.menu_item_rm_admin,
                    R.id.menu_item_remove_member,
                    R.id.menu_item_add_to_blacklist,
                    R.id.menu_item_remove_from_blacklist,
                    R.id.menu_item_transfer_owner,
                    R.id.menu_item_mute,
                    R.id.menu_item_unmute};

            for (int id : ids) {
                LinearLayout linearLayout = (LinearLayout) dialog.findViewById(id);
                linearLayout.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View v) {
                        dialog.dismiss();
                        loadingPB.setVisibility(View.VISIBLE);
                        EaseUI.getInstance().execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    int i = v.getId();
                                    if (i == R.id.menu_item_add_admin) {
                                        EMClient.getInstance().groupManager().addGroupAdmin(groupEntity.getImChatGroupId(), operationUserId);

                                    } else if (i == R.id.menu_item_rm_admin) {
                                        EMClient.getInstance().groupManager().removeGroupAdmin(groupEntity.getImChatGroupId(), operationUserId);

                                    } else if (i == R.id.menu_item_remove_member) {
                                        EMClient.getInstance().groupManager().removeUserFromGroup(groupEntity.getImChatGroupId(), operationUserId);

                                    } else if (i == R.id.menu_item_add_to_blacklist) {
                                        EMClient.getInstance().groupManager().blockUser(groupEntity.getImChatGroupId(), operationUserId);

                                    } else if (i == R.id.menu_item_remove_from_blacklist) {
                                        EMClient.getInstance().groupManager().unblockUser(groupEntity.getImChatGroupId(), operationUserId);

                                    } else if (i == R.id.menu_item_mute) {
                                        List<String> muteMembers = new ArrayList<String>();
                                        muteMembers.add(operationUserId);
                                        EMClient.getInstance().groupManager().muteGroupMembers(groupEntity.getImChatGroupId(), muteMembers, 20 * 60 * 1000);

                                    } else if (i == R.id.menu_item_unmute) {
                                        List<String> list = new ArrayList<String>();
                                        list.add(operationUserId);
                                        EMClient.getInstance().groupManager().unMuteGroupMembers(groupEntity.getImChatGroupId(), list);

                                    } else if (i == R.id.menu_item_transfer_owner) {
                                        EMClient.getInstance().groupManager().changeOwner(groupEntity.getImChatGroupId(), operationUserId);

                                    } else {
                                    }
                                    updateGroup();
                                } catch (final HyphenateException e) {
                                    runOnUiThread(new Runnable() {
                                                      @Override
                                                      public void run() {
                                                          Toast.makeText(GroupDetailsActivity.this, e.getDescription(), Toast.LENGTH_SHORT).show();
                                                      }
                                                  }
                                    );
                                    e.printStackTrace();

                                } finally {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadingPB.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        }

        void setVisibility(boolean[] visibilities) throws Exception {
            if (ids.length != visibilities.length) {
                throw new Exception("");
            }

            for (int i = 0; i < ids.length; i++) {
                View view = this.findViewById(ids[i]);
                view.setVisibility(visibilities[i] ? View.VISIBLE : View.GONE);
            }
        }

        int[] ids = {
                R.id.menu_item_transfer_owner,
                R.id.menu_item_add_admin,
                R.id.menu_item_rm_admin,
                R.id.menu_item_remove_member,
                R.id.menu_item_add_to_blacklist,
                R.id.menu_item_remove_from_blacklist,
                R.id.menu_item_mute,
                R.id.menu_item_unmute
        };
    }

    /**
     * 群组Owner和管理员gridadapter
     *
     * @author admin_new
     */
    private class OwnerAdminAdapter extends ArrayAdapter<String> {

        private int res;

        public OwnerAdminAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            res = textViewResourceId;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(res, null);
                holder.imageView = convertView.findViewById(R.id.iv_avatar);
                holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
                holder.badgeDeleteView = (ImageView) convertView.findViewById(R.id.badge_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final LinearLayout button = (LinearLayout) convertView.findViewById(R.id.button_avatar);

            final String username = getItem(position);
            convertView.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            EaseUserUtils.setUserNick(username, holder.textView);
            AvatarUtils.setAvatarContent(activity, username, holder.imageView);

            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (groupEntity == null) {
                        return;
                    }
                    Intent intent = new Intent(activity, PersonalCardActivity.class);
                    intent.putExtra("username", username);
                    if (isOwner(groupEntity)){
                        intent.putExtra("groupId", groupEntity.getImChatGroupId());
                        intent.putExtra("role", "admin");
                    }
                    startActivityForResult(intent, 200);
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    }

    protected void updateGroup() {
        if (chatGroupId <= 0) {
            return;
        }
        EMAPIManager.getInstance().getGroupDetailWithMemberList(chatGroupId, false, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                if (isFinishing()) {
                    return;
                }
                try {
                    JSONObject jsonObj = new JSONObject(value);
                    JSONObject jsonEntity = jsonObj.optJSONObject("entity");
                    JSONArray jsonUserList = jsonObj.optJSONArray("userList");
                    JSONObject jsonGroup = jsonObj.optJSONObject("chatgroup");
                    JSONArray jsonUserGroupRSList = jsonObj.optJSONArray("userChatGroupRelationshipList");
                    groupEntity = MPGroupEntity.create(jsonGroup);
                    if (groupEntity != null) {
                        List<MPGroupMemberEntity> memberEntities = MPGroupMemberEntity.create(jsonUserGroupRSList);
                        groupEntity.setMemberEntities(memberEntities);
                        groupEntity.setUserMaps(MPUserEntity.create(jsonUserList));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingPB.setVisibility(View.INVISIBLE);
                            updateViews();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    MPLog.e(TAG, "getGroupDetailWithMemberList json parse error");
                }

            }

            @Override
            public void onError(int error, String errorMsg) {
                MPLog.e(TAG, "getGroupDetailWithMemberList error->" + errorMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingPB.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
//        EMAPIManager.getInstance().getGroupInfoById(chatGroupId, new EMDataCallBack<String>() {
//            @Override
//            public void onSuccess(String value) {
//                if (isFinishing()) {
//                    return;
//                }
//                JSONObject jsonEntity = null;
//                try {
//                    JSONObject jsonObj = new JSONObject(value);
//                    jsonEntity = jsonObj.optJSONObject("entity");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                groupEntity = MPGroupEntity.create(jsonEntity);
//                if (groupEntity != null) {
//                    chatGroupId = groupEntity.getId();
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        loadingPB.setVisibility(View.INVISIBLE);
//                        updateViews();
//                    }
//                });
//
//            }
//
//            @Override
//            public void onError(int error, String errorMsg) {
//                MPLog.e(TAG, "getGroupInfo error->" + errorMsg);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        loadingPB.setVisibility(View.INVISIBLE);
//                    }
//                });
//            }
//        });
//        EaseUI.getInstance().execute(new Runnable() {
//            public void run() {
//                try {
////                    if (pushConfigs == null) {
////                        EMClient.getInstance().pushManager().getPushConfigsFromServer();
////                    }
//
//                } catch (Exception e) {
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            loadingPB.setVisibility(View.INVISIBLE);
//                        }
//                    });
//                } finally {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            srl.setRefreshing(false);
//                        }
//                    });
//                }
//            }
//        });
    }

    public void back(View view) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void getImageToView(File avatarFile) {
        showProgressDialog();
        EMAPIManager.getInstance().postFile(avatarFile, Constant.FILE_UPLOAD_TYPE_AVATAR, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(final String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        MPLog.d(TAG, value);
                        try {
                            JSONObject jsonObj = new JSONObject(value);
                            JSONObject jsonEntty = jsonObj.optJSONObject("entity");
                            String remoteUrl = jsonEntty.optString("url");
                            String md5Val = jsonEntty.optString("md5");
                            updateGroupInfo(null, remoteUrl, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            toastInvalidResponse(TAG, "uploadResultBean = null");
                        }
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }

    private int chatGroupId;

    //更改群头像昵称
    private void updateGroupInfo(String nick, String avatar, String description) {
        if (chatGroupId == 0) {
            return;
        }
        showProgressDialog();
        JSONObject jsonBody = new JSONObject();
        try {
            if (nick != null) {
                jsonBody.put("name", nick);
            }
            if (avatar != null) {
                jsonBody.put("avatar", avatar);
            }

            if (description != null) {
                jsonBody.put("description", description);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        EMAPIManager.getInstance().putGroupInfo(chatGroupId, groupEntity.isCluster(), jsonBody.toString(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(final String value) {
                try {
                    JSONObject jsonObj = new JSONObject(value);
                    JSONObject jsonEntity = jsonObj.optJSONObject("entity");
                    final MPGroupEntity groupEntity = MPGroupEntity.create(jsonEntity);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            if (groupEntity != null) {
//                                String easemobGroupId = groupEntity.getImChatGroupId();
//                                String avatarUrl = groupEntity.getAvatar();
//                                GroupBean groupBean = new GroupBean(groupEntity.getId(), easemobGroupId, groupEntity.getName(), avatarUrl, groupEntity.getCreateTime());
                                AppHelper.getInstance().getModel().saveGroupInfo(groupEntity);
                                sendGroupMessage(groupEntity.getName(), groupEntity.getAvatar());
                                updateGroupTitle();
                                showGroupAvatar();
                                updateViews();
                                MPEventBus.getDefault().post(new EventGroupsChanged());
                            } else {
                                MPLog.e(TAG, "putGroupInfo groupEntity is null");
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    EMLog.e(TAG, "putGroupInfo json parse error");
                    hideProgressDialog();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                MPLog.e(TAG, "putGroupInfo error : " + errorMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        MyToast.showToast("修改失败! ");
                    }
                });

            }
        });

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupChanged(EventGroupsChanged groupsChanged){
        showGroupAvatar();
    }

    @Override
    protected void onDestroy() {
        EMClient.getInstance().groupManager().removeGroupChangeListener(groupChangeListener);
        super.onDestroy();
        dismiss();
        dismissDialog();
    }

    private static class ViewHolder {
        AvatarImageView imageView;
        TextView textView;
        ImageView badgeDeleteView;
    }

    private class GroupChangeListener extends EaseGroupListener {

        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
//                    memberList = group.getMembers();
//                    memberList.remove(group.getOwner());
//                    memberList.removeAll(adminList);
//                    memberList.removeAll(muteList);
//                    refreshMembersAdapter();
                }
            });
        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            finish();
        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {
            finish();
        }

        @Override
        public void onMuteListAdded(String groupId, final List<String> mutes, final long muteExpire) {
            updateGroup();
        }

        @Override
        public void onMuteListRemoved(String groupId, final List<String> mutes) {
            updateGroup();
        }

        @Override
        public void onWhiteListAdded(String s, List<String> list) {

        }

        @Override
        public void onWhiteListRemoved(String s, List<String> list) {

        }

        @Override
        public void onAllMemberMuteStateChanged(String s, boolean b) {

        }

        @Override
        public void onAdminAdded(String groupId, String administrator) {
            updateGroup();
        }

        @Override
        public void onAdminRemoved(String groupId, String administrator) {
            updateGroup();
        }

        @Override
        public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    MyToast.showToast(getString(R.string.owner_change));
                }
            });
            updateGroup();
        }

        @Override
        public void onMemberJoined(String groupId, String member) {
            EMLog.d(TAG, "onMemberJoined");
            updateGroup();
        }

        @Override
        public void onMemberExited(String groupId, String member) {
            EMLog.d(TAG, "onMemberExited");
            updateGroup();
        }

        @Override
        public void onAnnouncementChanged(String groupId, final String announcement) {
            if (groupId.equals(groupEntity.getImChatGroupId())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        announcementText.setText(announcement);
                    }
                });
            }
        }

        @Override
        public void onSharedFileAdded(String groupId, final EMMucSharedFile sharedFile) {
            if (groupId.equals(groupEntity.getImChatGroupId())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(GroupDetailsActivity.this, "Group added a share file", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        public void onSharedFileDeleted(String groupId, String fileId) {
            if (groupId.equals(groupEntity.getImChatGroupId())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(GroupDetailsActivity.this, "Group deleted a share file", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }






}
