package com.hyphenate.easemob.im.officeautomation.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.CloseUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.entity.Media;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.easeui.widget.EaseExpandGridView;
import com.hyphenate.easemob.im.gray.GrayScaleConfig;
import com.hyphenate.easemob.imlibs.message.MessageUtils;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.im.mp.ui.group.GroupEditActivity;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.mp.utils.NetworkUtil;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.ui.group.GroupAddMemberActivity;
import com.hyphenate.easemob.im.officeautomation.ui.group.GroupMembersManagerActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.utils.ImageTools;
import com.hyphenate.easemob.im.officeautomation.utils.MyToast;
import com.hyphenate.easemob.imlibs.mp.utils.MPPathUtil;
import com.kyleduo.switchbutton.SwitchButton;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by qby on 2018/06/21.
 * 改
 * 创建群组页面
 */
public class CreateGroupActivity extends BaseActivity {
    private static final String TAG = "CreateGroupActivity";
    public static final int IMAGE_REQUEST_CODE = 4;
    private static final int REQUEST_CODE_CHOOSE_PICTURE = 5;
    private static final int REQUEST_CODE_PICTURE_CROP = 6;
    private static final int REQUEST_CODE_UPDATE_GROUP_NAME = 7;
    private static final int REQUEST_CODE_UPDATE_GROUP_DESC = 8;
    private ImageView back;
    private TextView done;
    private View llDone;
    private ImageView iv_head;
    private RelativeLayout groupAvatarLayout;
    private RelativeLayout rlGroupMembers;
    private GridAdapter memberAdapter;
    private EaseExpandGridView memberGridView;
    private TextView tvGroupName;
    private TextView tvGroupDesc;
    private TextView tv_current_members;
    private SwitchButton sb_member_invite;
    private SwitchButton sb_members_only;
    private SwitchButton sb_cluster;
    private ArrayList<Integer> pickedUidList;
    private Map<Integer, Integer> pickedOrgIdMap;
    private List<EaseUser> pickedUsers;
    private StringBuilder defaultGroupName;
    //设置默认值
    //是否公开群
    private boolean isPublic = false;
    //是否需要管理员审批
    private boolean membersOnly = false;
    //是否开放支持新成员查看聊天记录
    private boolean chatRecord = false;
    private File groupAvatar;
    private String groupName;
    private String groupDescription;
    private ArrayList<EaseUser> userList;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private LoginUser loginUser;

    private String strGroupName;
    private RelativeLayout rlGroupName;
    private String strGroupDesc;
    private RelativeLayout rlGroupDesc;
    private RelativeLayout rlSupportCluster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        setSwipeEnabled(false);
        initViews();
        initListeners();
        initData();
    }

    private void initViews() {
        back = $(R.id.iv_back);
        done = $(R.id.tv_done);
        llDone = findViewById(R.id.ll_done);
        iv_head = $(R.id.iv_head);
        groupAvatarLayout = $(R.id.rl_group_avatar);
        tvGroupName = $(R.id.tv_group_name);
        tvGroupDesc = $(R.id.tv_description);
        tv_current_members = $(R.id.tv_current_members);
        sb_member_invite = $(R.id.sb_members_invite);
        sb_members_only = $(R.id.sb_members_only);
        sb_cluster = $(R.id.sb_cluster);

        memberGridView = findViewById(R.id.gridview);
        rlGroupMembers = findViewById(R.id.rl_group_members);
        rlGroupName = findViewById(R.id.rl_group_name);
        rlGroupDesc = findViewById(R.id.rl_group_description);

        rlSupportCluster = findViewById(R.id.rl_support_cluster);

        if(GrayScaleConfig.isSupportRegion) {
            rlSupportCluster.setVisibility(View.VISIBLE);
        } else {
            rlSupportCluster.setVisibility(View.GONE);
        }
    }

    private void initListeners() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        llDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isConnected()) {
                    Toast.makeText(getApplicationContext(), R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pickedUidList.size() < 3) {
                    Toast.makeText(CreateGroupActivity.this, "群成员不得少于3人", Toast.LENGTH_SHORT).show();
                    return;
                }

                //点击完成
                groupName = strGroupName;
                groupDescription = strGroupDesc;
                membersOnly = sb_members_only.isChecked();
                if (TextUtils.isEmpty(groupName)) {
                    String tempDefaultGroupName = defaultGroupName.toString();
                    if (tempDefaultGroupName.length() > 0) {
                        try {
                            if (tempDefaultGroupName.length() > 25) {
                                groupName = tempDefaultGroupName.trim().substring(0, 25);
                            } else {
                                groupName = tempDefaultGroupName;
                            }
                        } catch (Exception e) {
                            groupName = "未设置";
                        }
                    }
                }
                if (groupAvatar == null) {
                    createGroup(groupName, pickedUidList, EaseConstant.MAX_GROUP_COUNT, groupDescription, isPublic, chatRecord, true, null, membersOnly);
                } else {
                    getImageToView(groupAvatar);
                }
            }
        });
        groupAvatarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PickerActivity.class);
                intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE);
                long maxSize = 10485760L;
                intent.putExtra(PickerConfig.MAX_SELECT_SIZE, maxSize); //default 10MB (Optional)
                intent.putExtra(PickerConfig.MAX_SELECT_COUNT, 1);  //default 40 (Optional)
                startActivityForResult(intent, REQUEST_CODE_CHOOSE_PICTURE);
            }
        });

        rlGroupMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateGroupActivity.this, GroupMembersManagerActivity.class).putExtra("userList", userList).putExtra("isDel", false));
            }
        });

        rlGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(activity, GroupEditActivity.class)
                                .putExtra("index", GroupEditActivity.UPDATE_GROUP_NAME),
                        REQUEST_CODE_UPDATE_GROUP_NAME);
            }
        });

        rlGroupDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(activity, GroupEditActivity.class)
                                .putExtra("index", GroupEditActivity.UPDATE_GROUP_DESCRIPTION),
                        REQUEST_CODE_UPDATE_GROUP_DESC);
            }
        });
    }


    private void initData() {
        loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser == null) {
            return;
        }
        pickedUidList = getIntent().getIntegerArrayListExtra("pickedUidList");
        if (!pickedUidList.contains(loginUser.getId())) {
            pickedUidList.add(0, loginUser.getId());
        }
        pickedOrgIdMap = (Map<Integer, Integer>) getIntent().getSerializableExtra("pickedOrgIdMap");
        if (pickedOrgIdMap == null) pickedOrgIdMap = new HashMap<>();
        if (pickedOrgIdMap.size() > 0) {
            getMembersByOrgId(new ArrayList<>(pickedOrgIdMap.keySet()), new ArrayList<>(pickedOrgIdMap.values()).get(0), new ArrayList<>(pickedOrgIdMap.keySet()).get(0));
        }
        defaultGroupName = new StringBuilder();
        defaultGroupName.append(loginUser.getNick());

        tv_current_members.setText(pickedUidList.size() + "人");

        userList = new ArrayList<>();
        memberAdapter = new GridAdapter(CreateGroupActivity.this, R.layout.em_grid_owner, userList);
        memberGridView.setAdapter(memberAdapter);

        EaseUI.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                pickedUsers = AppHelper.getInstance().getModel().getUserExtInfosById(pickedUidList);
                final boolean isCluster = filterUser(pickedUsers);
                for (int i = 0; i < pickedUsers.size(); i++) {
                    EaseUser itemUser = pickedUsers.get(i);
                    userList.add(itemUser);
                }
                if (userList.size() > 0) {
                    for (EaseUser itemUser : userList) {
                        if (itemUser.getId() == MPClient.get().getCurrentUser().getId()) {
                            continue;
                        }
                        defaultGroupName.append(",").append(itemUser.getNickname());
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sb_cluster.setChecked(isCluster);
                        memberAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        iv_head.setImageResource(R.drawable.ease_group_icon);

        memberGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < memberAdapter.getCount() - 2) {//成员
//                        EaseUser entity = (EaseUser) parent.getItemAtPosition(position);

                } else if (position == memberAdapter.getCount() - 2) {//删除
//                        ArrayList<EaseUser> users = new ArrayList<>();
//                        for (EaseUser user : pickedUsers) {
//                            if (user.getId() != loginUser.getId()) {
//                                users.add(user);
//                            }
//                        }
                    startActivityForResult(new Intent(CreateGroupActivity.this, GroupMembersManagerActivity.class).putExtra("userList", (ArrayList) pickedUsers).putExtra("isDel", true), 2001);
                } else if (position == memberAdapter.getCount() - 1) {//添加
                    startActivityForResult(new Intent(CreateGroupActivity.this, GroupAddMemberActivity.class).putExtra("isCreate", true).putExtra("pickedUidList", pickedUidList)/*.putExtra("pickedOrgIdMap", (Serializable) pickedOrgIdMap)*/, 2000);
                }
            }
        });
    }

    /**
     * 检查imUsername的前缀是否相同
     * @param users
     * @return
     */
    private boolean filterUser(List<EaseUser> users) {
        Set<String> set = new HashSet<>();
        for (EaseUser user : users) {
            String prefix = MessageUtils.getPrefixByUsername(user.getUsername());
            if (prefix != null) {
                set.add(prefix);
            }
        }
        set.add(MessageUtils.getPrefixByUsername(MPClient.get().getCurrentUser().getImUserId()));
        return set.size() > 1;
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
                        try {
                            JSONObject jsonObj = new JSONObject(value);
                            JSONObject jsonEntty = jsonObj.optJSONObject("entity");
                            String remoteUrl = jsonEntty.optString("url");
                            String md5Val = jsonEntty.optString("md5");
                            if (!TextUtils.isEmpty(remoteUrl)) {
                                createGroup(groupName, pickedUidList, EaseConstant.MAX_GROUP_COUNT, groupDescription, isPublic, chatRecord, true, remoteUrl, membersOnly);
                            }
                        } catch (Exception e) {
                            MPLog.e(TAG, "post File failed:" + MPLog.getStackTraceString(e));
                            hideProgressDialog();
                            ToastUtils.showShort("上传头像失败");
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
                        ToastUtils.showShort("上传头像失败");
                    }
                });

            }
        });
    }

    //创建群聊
    private void createGroup(String chatName, List<Integer> members, int maxusers, String description, boolean isPublic, boolean newmemberCanreadHistory, boolean allowinvites, String avatar, boolean membersOnly) {
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser == null) {
            return;
        }
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("name", chatName);
            jsonObj.put("description", description);
            jsonObj.put("avatar", avatar);
            jsonObj.put("maxusers", maxusers);
            jsonObj.put("newMemberCanreadHistory", newmemberCanreadHistory);
            jsonObj.put("allowInvites", true);
            jsonObj.put("membersOnly", membersOnly);
            jsonObj.put("isPublic", isPublic);
            if (members.contains(Integer.valueOf(MPClient.get().getCurrentUser().getId()))) {
                members.remove(Integer.valueOf(MPClient.get().getCurrentUser().getId()));
            }
            if (sb_cluster.isChecked()) {
                jsonObj.put("memberList", new JSONArray(members));
            } else {
                jsonObj.put("memberIdList", new JSONArray(members));
            }

            JSONArray orgJsonArray = new JSONArray();
//            for (Map.Entry<Integer, Integer> map : pickedOrgIdMap.entrySet()) {
//                JSONObject orgJson = new JSONObject();
//                orgJson.put("id", map.getKey());
//                orgJson.put("companyId", map.getValue());
//                orgJsonArray.put(orgJson);
//            }
            jsonObj.put("organizationList", orgJsonArray);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        showProgressDialog("创建中...");
        if (sb_cluster.isChecked()) {
            MPLog.d(TAG, "group is cluster");
            EMAPIManager.getInstance().postCreateGroupCluster(jsonObj.toString(), new EMDataCallBack<String>() {
                @Override
                public void onSuccess(final String value) {
                    MPLog.d(TAG, "postCreateGroup->" + value);
                    if (isFinishing()) {
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            try {
                                JSONObject jsonResult = new JSONObject(value);
                                String status = jsonResult.optString("status");
                                if ("OK".equalsIgnoreCase(status)) {
                                    JSONObject jsonEntity = jsonResult.optJSONObject("entity");
                                    MPGroupEntity groupEntity = MPGroupEntity.create(jsonEntity);
                                    if (groupEntity != null) {
                                        AppHelper.getInstance().getModel().saveGroupInfo(groupEntity);
                                        finish();
                                    } else {
                                        toastInvalidResponse(TAG, "entity = null");
                                    }
                                } else {
                                    toastInvalidResponse(TAG, "status = " + status);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                toastInvalidResponse(TAG, "postCreateGroup jsonParse error");
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
                        }
                    });
                }
            });
        } else {
            EMAPIManager.getInstance().postCreateGroup(jsonObj.toString(), new EMDataCallBack<String>() {
                @Override
                public void onSuccess(final String value) {
                    MPLog.d(TAG, "postCreateGroup->" + value);
                    if (isFinishing()) {
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            try {
                                JSONObject jsonResult = new JSONObject(value);
                                String status = jsonResult.optString("status");
                                if ("OK".equalsIgnoreCase(status)) {
                                    JSONObject jsonEntity = jsonResult.optJSONObject("entity");
                                    MPGroupEntity groupEntity = MPGroupEntity.create(jsonEntity);
                                    groupEntity.setCluster(false);
                                    if (groupEntity != null) {
//                                        String imChatGroup = groupEntity.getImChatGroupId();
//                                        GroupBean groupBean = new GroupBean(groupEntity.getId(), imChatGroup, groupEntity.getName(), groupEntity.getAvatar(),
//                                                groupEntity.getCreateTime(), groupEntity.getType());
                                        AppHelper.getInstance().getModel().saveGroupInfo(groupEntity);
                                        finish();
                                    } else {
                                        toastInvalidResponse(TAG, "entity = null");
                                    }
                                } else {
                                    toastInvalidResponse(TAG, "status = " + status);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                toastInvalidResponse(TAG, "postCreateGroup jsonParse error");
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
                        }
                    });
                }
            });
        }

    }

    int index = 0;

    private void getMembersByOrgId(List<Integer> orgIds, int companyId, int orgId) {

        EMAPIManager.getInstance().getSubOrgsOfUsers(companyId, orgId, 0, 1000, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject response = new JSONObject(value);
                    if ("OK".equals(response.getString("status"))) {
                        JSONObject jsonObj = new JSONObject(value);
                        int elements = jsonObj.optInt("numberOfElements");
                        List<MPUserEntity> list = MPUserEntity.create(jsonObj.optJSONArray("entities"));
                        for (MPUserEntity entity : list) {
                            if (!pickedUidList.contains(entity.getId())) {
                                pickedUidList.add(entity.getId());
                                EaseUser user = new EaseUser();
                                user.setAlias(entity.getAlias());
                                user.setAvatar(entity.getAvatar());
                                user.setId(entity.getId());
                                user.setNickname(entity.getRealName());
                                user.setEasemobName(entity.getImUserId());
                                pickedUsers.add(user);
                            }
                        }
                        index++;
                        if (orgIds.size() == index) {
                            index = 0;
                            pickedOrgIdMap.clear();
                            runOnUiThread(() -> refreshMemberAdapter());
                        } else {
                            getMembersByOrgId(orgIds, new ArrayList<>(pickedOrgIdMap.values()).get(index), new ArrayList<>(pickedOrgIdMap.keySet()).get(index));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

                Log.i("info", "get member:" + errorMsg);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            List<Integer> userList = intent.getIntegerArrayListExtra("pickedUidList");
            Map<Integer, Integer> orgMap = (Map<Integer, Integer>) intent.getSerializableExtra("pickedOrgIdMap");
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    for (Integer id : userList) {
                        if (!pickedUidList.contains(id)) {
                            pickedUidList.add(id);
                        }
                    }
                    ArrayList<Integer> tempOrgIdList = new ArrayList<>();
                    if(orgMap != null && !orgMap.isEmpty()) {
                        for (Map.Entry<Integer, Integer> map : orgMap.entrySet()) {
                            if (!pickedOrgIdMap.keySet().contains(map.getKey())) {
                                pickedOrgIdMap.put(map.getKey(), map.getValue());
                                tempOrgIdList.add(map.getKey());
                            }
                        }
                    }
                    pickedUsers = AppHelper.getInstance().getModel().getUserExtInfosById(pickedUidList);
                    if (tempOrgIdList.size() > 0)
                        getMembersByOrgId(tempOrgIdList, new ArrayList<>(pickedOrgIdMap.keySet()).get(0), new ArrayList<>(pickedOrgIdMap.keySet()).get(0));
                    final boolean isCluster = filterUser(pickedUsers);
                    runOnUiThread(() -> {
                        refreshMemberAdapter();
                        sb_cluster.setChecked(isCluster);
                    });
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_UPDATE_GROUP_NAME) {
                strGroupName = data.getStringExtra("content");
                tvGroupName.setText(strGroupName);
            } else if (requestCode == REQUEST_CODE_UPDATE_GROUP_DESC) {
                strGroupDesc = data.getStringExtra("content");
                tvGroupDesc.setText(strGroupDesc);
            }
        }
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    if (data != null) {
                        List<String> pathList = data.getStringArrayListExtra("result");
                        if (pathList != null && !pathList.isEmpty()) {
                            String filePath = pathList.get(0);
                            groupAvatar = new File(filePath);
                            GlideUtils.load(activity, filePath, R.drawable.ease_group_icon, iv_head);
                        }
                    }

                    break;
                case UCrop.REQUEST_CROP:
                    //裁切成功
                    Uri croppedFileUri = UCrop.getOutput(data);
                    boolean avatarHasSet;
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
                            CloseUtils.closeIO(outChannel);
                            CloseUtils.closeIO(outStream);
                            CloseUtils.closeIO(inChannel);
                            CloseUtils.closeIO(inStream);
                        }
                        Glide.with(activity).load(groupAvatar).into(iv_head);
                        avatarHasSet = true;
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
                    if (data == null) {
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
                    Glide.with(activity).load(groupAvatar).into(iv_head);
                    avatarHasSet = true;
                    break;

                case 2000://添加群成员返回的结果
                    if (data != null) {
                        final List<Integer> userList = data.getIntegerArrayListExtra("pickedUidList");
                        final Map<Integer, Integer> orgMap = (Map<Integer, Integer>) data.getSerializableExtra("pickedOrgIdMap");
                        executorService.execute(() -> {
                            for (Integer id : userList) {
                                if (!pickedUidList.contains(id)) {
                                    pickedUidList.add(id);
                                }
                            }
                            for (Map.Entry<Integer, Integer> map : orgMap.entrySet()) {
                                if (!pickedOrgIdMap.keySet().contains(map.getKey())) {
                                    pickedOrgIdMap.put(map.getKey(), map.getValue());
                                }
                            }
                            pickedUsers = AppHelper.getInstance().getModel().getUserExtInfosById(pickedUidList);
                            final boolean isCluster = filterUser(pickedUsers);
                            runOnUiThread(() -> {
                                refreshMemberAdapter();
                                sb_cluster.setChecked(isCluster);
                            });
                        });
                    }
                    break;
                case 2001://删除群成员返回的结果
                    if (data != null) {
                        List<EaseUser> list = data.getParcelableArrayListExtra("userList");
                        executorService.execute(() -> {
                            pickedUidList.clear();
                            for (EaseUser user : pickedUsers) {
                                if (EMClient.getInstance().getCurrentUser().equals(user.getUsername())) {
                                    pickedUidList.add(user.getId());
                                    break;
                                }
                            }
                            pickedUsers.clear();
                            for (EaseUser user : list) {
                                pickedUidList.add(user.getId());
                            }
                            pickedUsers = AppHelper.getInstance().getModel().getUserExtInfosById(pickedUidList);
                            final boolean isCluster =  filterUser(pickedUsers);
                            runOnUiThread(() -> {
                                refreshMemberAdapter();
                                sb_cluster.setChecked(isCluster);
                            });
                        });
                    }
                    break;
            }
        }
    }

    private void refreshMemberAdapter() {
        userList.clear();
        for (int i = 0; i < pickedUsers.size(); i++) {
//            if (i > 3) {
//                break;
//            }
            userList.add(pickedUsers.get(i));
        }
        tv_current_members.setText(pickedUidList.size() + "人");
        memberAdapter.notifyDataSetChanged();
    }

    /**
     * 群组成员的gridAdapter
     */
    private class GridAdapter extends ArrayAdapter<EaseUser> {
        private int res;

        public GridAdapter(Context context, int textViewResourceId, List<EaseUser> objects) {
            super(context, textViewResourceId, objects);
            res = textViewResourceId;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(res, null);
                holder.imageView = convertView.findViewById(R.id.iv_avatar);
                holder.textView = convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 一行能放六个（包含加号+减号），也就是实际能显示4个

            if (position < getCount() - 2) {
                EaseUser entity = getItem(position);
                holder.imageView.setImageResource(R.drawable.ease_default_avatar);
                if (entity != null) {
                    holder.textView.setText(entity.getNickname());
                    String avatar = entity.getAvatar();
                    AvatarUtils.setAvatarContent(activity, entity.getNickname(), avatar, holder.imageView);
                } else {
                    holder.textView.setText("");
                }
            } else if (position == getCount() - 2) {
                holder.textView.setText("");
                Glide.with(getContext()).load(R.drawable.mp_ic_group_sub).into(holder.imageView);
            } else if (position == getCount() - 1) {
                holder.textView.setText("");
                Glide.with(getContext()).load(R.drawable.mp_ic_group_add).into(holder.imageView);
            }

            return convertView;
        }

        @Override
        public int getCount() {
            if (userList.size() > 9) {
                return 12;
            } else {
                return userList.size() + 2;
            }
        }
    }

    private static class ViewHolder {
        AvatarImageView imageView;
        TextView textView;
    }

}
