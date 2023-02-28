package com.hyphenate.easemob.im.officeautomation.ui.group;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.CloseUtils;
import com.bumptech.glide.Glide;
import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.entity.Media;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.easeui.widget.EaseTitleBar;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.im.mp.ui.group.GroupEditActivity;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.mp.utils.NetworkUtil;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;
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
import java.util.List;

/**
 * Created by qby on 2018/06/21.
 * 创建群组页面
 */
//public class GroupCreateActivity extends BaseActivity  {
//    private static final String TAG = "CreateGroupActivity";
//    public static final int IMAGE_REQUEST_CODE = 4;
//    private static final int REQUEST_CODE_CHOOSE_PICTURE = 5;
//    private static final int REQUEST_CODE_PICTURE_CROP = 6;
//    private static final int REQUEST_CODE_UPDATE_GROUP_NAME = 7;
//    private static final int REQUEST_CODE_UPDATE_GROUP_DESC = 8;
//
//    private EaseTitleBar title_bar;
//    private ImageView iv_head;
//    private TextView tvGroupName;
//    private TextView tvGroupDesc;
//    private TextView tv_current_members;
//    private TextView tvMemberCount;
//    private SwitchButton sb_member_invite;
//    private TextView tv_complete;
//    private ArrayList<EaseUser> invite_list;
//    private StringBuilder defaultGroupName;
//    //设置默认值
//    //是否公开群
//    private boolean isPublic = false;
//    //是否需要管理员审批
//    private boolean ownerOnly = false;
//    //是否开放支持新成员查看聊天记录
//    private boolean chatRecord = false;
//    private File groupAvatar;
//    private String groupName;
//    private String groupDescription;
//    private List<Integer> memList;
//
//    private String strGroupName;
//    private RelativeLayout rlGroupName;
//    private String strGroupDesc;
//    private RelativeLayout rlGroupDesc;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_create_group);
//        setSwipeEnabled(false);
//        initViews();
//        initListeners();
//        initData();
//    }
//
//    private void initViews() {
//        title_bar = $(R.id.title_bar);
//        iv_head = $(R.id.iv_head);
//        tvGroupName = $(R.id.tv_group_name);
//        tvGroupDesc = $(R.id.tv_description);
//        tv_current_members = $(R.id.tv_current_members);
//        sb_member_invite = $(R.id.sb_members_invite);
//        tv_complete = $(R.id.tv_done);
//        tvMemberCount = findViewById(R.id.tv_member_count);
//        rlGroupName = findViewById(R.id.rl_group_name);
//        rlGroupDesc = findViewById(R.id.rl_group_description);
//
//    }
//
//    private void initListeners() {
//        title_bar.setLeftLayoutClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        iv_head.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = ImageHandleUtils.pickSingleImage(activity, true);
////                startActivityForResult(intent, REQUEST_CODE_CHOOSE_PICTURE);
//                Intent intent = new Intent(activity, PickerActivity.class);
//                intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE);
//                long maxSize = 10485760L;
//                intent.putExtra(PickerConfig.MAX_SELECT_SIZE,maxSize); //default 10MB (Optional)
//                intent.putExtra(PickerConfig.MAX_SELECT_COUNT,1);  //default 40 (Optional)
//                startActivityForResult(intent, REQUEST_CODE_CHOOSE_PICTURE);
//            }
//        });
//        tv_complete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!NetworkUtil.isConnected()){
//                    Toast.makeText(getApplicationContext(), R.string.network_unavailable, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                //点击完成
//                groupName = strGroupName;
//                groupDescription = strGroupDesc;
//                if (TextUtils.isEmpty(groupName)) {
//                    String tempDefaultGroupName = defaultGroupName.toString();
//                    if (tempDefaultGroupName.length() > 0){
//                        try{
//                            if (tempDefaultGroupName.length() > 20){
//                                groupName = tempDefaultGroupName.trim().substring(0, 20);
//                            }else{
//                                groupName = tempDefaultGroupName;
//                            }
//                        }catch (Exception e){
//                            groupName = "未设置";
//                        }
//                    }
//                }
//                if (groupAvatar == null) {
//                    createGroup(groupName, memList, EaseConstant.MAX_GROUP_COUNT, groupDescription, isPublic, chatRecord, sb_member_invite.isChecked(), null, ownerOnly);
//                } else {
//                    getImageToView(groupAvatar);
//                }
//            }
//        });
//
//        rlGroupName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivityForResult(new Intent(activity, GroupEditActivity.class)
//                        .putExtra("index", GroupEditActivity.UPDATE_GROUP_NAME),
//                        REQUEST_CODE_UPDATE_GROUP_NAME);
//            }
//        });
//
//        rlGroupDesc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivityForResult(new Intent(activity, GroupEditActivity.class)
//                                .putExtra("index", GroupEditActivity.UPDATE_GROUP_DESCRIPTION),
//                        REQUEST_CODE_UPDATE_GROUP_DESC);
//            }
//        });
//    }
//
//
//    private void initData() {
//        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
//        if (loginUser == null) {
//            return;
//        }
//        invite_list = getIntent().getParcelableArrayListExtra("invite_list");
//        if (invite_list != null){
//            tv_current_members.setText(String.valueOf(invite_list.size() + 1));
//
//            defaultGroupName = new StringBuilder();
//            memList = new ArrayList<>();
//            defaultGroupName.append(loginUser.getNick());
//            for (int i = 0; i < invite_list.size(); i++) {
//                EaseUser user = (EaseUser) invite_list.get(i);
//                memList.add(user.getId());
//                defaultGroupName.append(",").append(user.getNickname());
//            }
//            iv_head.setImageResource(R.drawable.ease_group_icon);
//        } else {
//            finish();
//        }
//    }
//
//    /**
//     * 保存裁剪之后的图片数据
//     */
//    private void getImageToView(File avatarFile) {
//        showProgressDialog();
//        EMAPIManager.getInstance().postFile(avatarFile, Constant.FILE_UPLOAD_TYPE_AVATAR, new EMDataCallBack<String>() {
//            @Override
//            public void onSuccess(final String value) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            JSONObject jsonObj = new JSONObject(value);
//                            JSONObject jsonEntty = jsonObj.optJSONObject("entity");
//                            String remoteUrl = jsonEntty.optString("url");
//                            String md5Val = jsonEntty.optString("md5");
//                            if (!TextUtils.isEmpty(remoteUrl)) {
//                                createGroup(groupName, memList, EaseConstant.MAX_GROUP_COUNT, groupDescription, isPublic, chatRecord, sb_member_invite.isChecked(), remoteUrl, ownerOnly);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            toastInvalidResponse(TAG, "uploadResultBean = null");
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onError(int error, String errorMsg) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        hideProgressDialog();
//                    }
//                });
//
//            }
//        });
//    }
//
//    //创建群聊
//    private void createGroup(String chatName, List<Integer> members, int maxusers, String description, boolean isPublic, boolean newmemberCanreadHistory, boolean allowinvites, String avatar, boolean membersOnly) {
//        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
//        if (loginUser == null){
//            return;
//        }
//        JSONObject jsonObj = new JSONObject();
//        try {
//            jsonObj.put("name", chatName);
//            jsonObj.put("description", description);
//            jsonObj.put("avatar", avatar);
//            jsonObj.put("maxusers", maxusers);
//            jsonObj.put("newMemberCanreadHistory", newmemberCanreadHistory);
//            jsonObj.put("allowInvites", allowinvites);
//            jsonObj.put("membersOnly", membersOnly);
//            jsonObj.put("isPublic", isPublic);
//
//            jsonObj.put("memberIdList", new JSONArray(members));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        showProgressDialog();
//        EMAPIManager.getInstance().postCreateGroup(jsonObj.toString(), new EMDataCallBack<String>() {
//            @Override
//            public void onSuccess(final String value) {
//                MPLog.d(TAG, "postCreateGroup->" + value);
//                if (isFinishing()) {
//                    return;
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        hideProgressDialog();
//                        try {
//                            JSONObject jsonResult = new JSONObject(value);
//                            String status = jsonResult.optString("status");
//                            if ("OK".equalsIgnoreCase(status)) {
//                                JSONObject jsonEntity = jsonResult.optJSONObject("entity");
//                                MPGroupEntity groupEntity = MPGroupEntity.create(jsonEntity);
//                                if (groupEntity != null) {
//                                    String imChatGroup = groupEntity.getImChatGroupId();
//                                    GroupBean groupBean = new GroupBean(groupEntity.getId(), imChatGroup, groupEntity.getName(), groupEntity.getAvatar(),
//                                            groupEntity.getCreateTime(), groupEntity.getType());
//                                    AppHelper.getInstance().getModel().saveGroupInfo(groupBean);
//                                    finish();
//                                } else {
//                                    toastInvalidResponse(TAG, "entity = null");
//                                }
//                            } else {
//                                toastInvalidResponse(TAG, "status = " + status);
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            toastInvalidResponse(TAG, "postCreateGroup jsonParse error");
//                        }
//
//                    }
//                });
//            }
//
//            @Override
//            public void onError(int error, String errorMsg) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        hideProgressDialog();
//                    }
//                });
//            }
//        });
//
//    }
//
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_CODE_UPDATE_GROUP_NAME) {
//                strGroupName = data.getStringExtra("content");
//                tvGroupName.setText(strGroupName);
//            } else if (requestCode == REQUEST_CODE_UPDATE_GROUP_DESC) {
//                strGroupDesc = data.getStringExtra("content");
//                tvGroupDesc.setText(strGroupDesc);
//            }
//        }
//
//        if (resultCode != RESULT_CANCELED) {
//            switch (requestCode) {
//                case IMAGE_REQUEST_CODE:
//                    if (data != null){
//                        List<String> pathList = data.getStringArrayListExtra("result");
//                        if (pathList != null && !pathList.isEmpty()){
//                            String filePath = pathList.get(0);
//                            groupAvatar = new File(filePath);
//                            GlideUtils.load(activity, filePath,R.drawable.ease_group_icon, iv_head);
//                        }
//                    }
//
//                    break;
//                case UCrop.REQUEST_CROP:
//                    //裁切成功
//                    Uri croppedFileUri = UCrop.getOutput(data);
//                    boolean avatarHasSet;
//                    if (croppedFileUri != null) {
//                        //获取默认的下载目录
//                        String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
//                        File saveFile = new File(downloadsDirectoryPath, "avatar.jpg");
//                        //保存下载的图片
//                        FileInputStream inStream = null;
//                        FileOutputStream outStream = null;
//                        FileChannel inChannel = null;
//                        FileChannel outChannel = null;
//                        try {
//                            inStream = new FileInputStream(new File(croppedFileUri.getPath()));
//                            outStream = new FileOutputStream(saveFile);
//                            inChannel = inStream.getChannel();
//                            outChannel = outStream.getChannel();
//                            inChannel.transferTo(0, inChannel.size(), outChannel);
////                            Toast.makeText(getActivity(), "裁切后的图片保存在：" + saveFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        } finally {
//                            CloseUtils.closeIO(outChannel);
//                            CloseUtils.closeIO(outStream);
//                            CloseUtils.closeIO(inChannel);
//                            CloseUtils.closeIO(inStream);
//                        }
//                        Glide.with(activity).load(groupAvatar).into(iv_head);
//                        avatarHasSet = true;
//                    } else {
//                        MyToast.showToast("Error crop");
//                    }
//                    break;
//
//                case REQUEST_CODE_CHOOSE_PICTURE:
//
//                    if (data != null) {
//                        ArrayList<Media> retMedias = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
//                        if (retMedias != null) {
//                            for (Media media : retMedias) {
//                                if (media.mediaType == 0) {
//                                    Uri imageUri;
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                        imageUri = FileProvider.getUriForFile(activity, getPackageName() + ".easemob", new File(media.path));//通过FileProvider创建一个content类型的Uri
//                                    } else {
//                                        imageUri = Uri.fromFile(new File(media.path));
//                                    }
//                                    File saveFile = new File(MPPathUtil.getInstance().getImagePath(), System.currentTimeMillis() + ".png");
//                                    groupAvatar = saveFile;
//                                    UCrop.of(imageUri, Uri.fromFile(saveFile))
//                                            .withAspectRatio(1, 1).withMaxResultSize(300, 300).start(activity);
//                                }
//                            }
//                        }
//                    }
//                    break;
//                case REQUEST_CODE_PICTURE_CROP:
//                    if(data == null){
//                        return;
//                    }
//                    Bundle extras = data.getExtras();
//                    if (extras == null) {
//                        return;
//                    }
//                    Bitmap bitmap = extras.getParcelable("data");
//                    if (bitmap == null) {
//                        return;
//                    }
//                    if (groupAvatar == null) {
//                        groupAvatar = new File(MPPathUtil.getInstance().getImagePath(), System.currentTimeMillis() + ".png");
//                    }
//                    ImageTools.savePhotoToSDCard(bitmap, groupAvatar.getPath());
//                    Glide.with(activity).load(groupAvatar).into(iv_head);
//                    avatarHasSet = true;
//                    break;
//            }
//        }
//
//    }
//
//}
