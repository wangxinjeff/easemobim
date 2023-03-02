package com.hyphenate.easemob.im.officeautomation.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.widget.WaterMarkBg;
import com.hyphenate.easemob.im.officeautomation.runtimepermissions.PermissionsManager;
import com.hyphenate.easemob.im.officeautomation.runtimepermissions.PermissionsResultAction;
import com.hyphenate.eventbus.Subscribe;
import com.hyphenate.eventbus.ThreadMode;
import com.hyphenate.easemob.imlibs.message.MessageUtils;
import com.hyphenate.easemob.im.mp.cache.TenantOptionCache;
import com.hyphenate.easemob.imlibs.mp.events.EventTenantOptionChanged;
import com.hyphenate.easemob.easeui.ui.EaseShowBigImageActivity;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.http.BaseRequest;
import com.hyphenate.easemob.im.officeautomation.utils.AppUtil;
import com.hyphenate.easemob.im.officeautomation.utils.MyToast;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qby on 2018/06/11 10:38.
 * 个人名片
 */
public class PersonalCardActivity extends BaseActivity {

    private static final String TAG = "PersonalCardActivity";
    public static final int RESULT_CODE_STARRED_FRIENDS = 1;
    private ImageView ivBack;
    private TextView mTvTitle;
    private TextView mTvName;
    private TextView mTvPhone;
    private TextView mTvEmail;

    private TextView tvPosition;
    private TextView tvDepartment;
    private TextView tvSuperior;

    private LinearLayout mLlSend;
    private RelativeLayout mRlSendMsg;
    private RelativeLayout mRlSendPhone;
    private RelativeLayout mRlAddFriend;
    private AvatarImageView mIvAvatar;
    private ImageView mIvStar;
    private ImageView mIvMore;
    private ImageView mIvAddOrDel;
    //App Server user_id
    private int userId;
    private int friendStatus;
    private String imUsername;
    private boolean isFriend;
    //粘贴板
    private ClipboardManager clipboard;
    //是否是星标好友
    private boolean isStarred;
    private boolean isFromStarred;
    private String groupId;
    private String role;
    private MPUserEntity userEntity;
//    private EMUserEntity infoBean = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_personal_card);
        initViews();
        initListeners();
        initData();
    }

    private void initViews() {
        ivBack = $(R.id.iv_back);
        mTvTitle = $(R.id.tv_title);
        mIvMore = findViewById(R.id.iv_more);
        mTvName = findViewById(R.id.tv_name);
        mTvPhone = findViewById(R.id.tv_phone);
        mTvEmail = findViewById(R.id.tv_email);
        tvDepartment = findViewById(R.id.tv_department);
        tvPosition = findViewById(R.id.tv_position);
        tvSuperior = findViewById(R.id.tv_superior);

        mLlSend = findViewById(R.id.ll_send);
        mRlSendMsg = findViewById(R.id.rl_send_msg);
        mRlAddFriend = findViewById(R.id.rl_add_friend);
        mRlSendPhone = findViewById(R.id.rl_send_phone);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mIvStar = findViewById(R.id.iv_star);
        mIvAddOrDel = findViewById(R.id.iv_add_or_del);

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void initListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mIvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userEntity == null || TextUtils.isEmpty(userEntity.getAvatar())) {
                    return;
                }
                String remoteUrl = userEntity.getAvatar();
                if (!remoteUrl.startsWith("http")) {
                    remoteUrl = EaseUI.getInstance().getAppServer() + remoteUrl;
                }
                startActivity(new Intent(activity, EaseShowBigImageActivity.class).putExtra("remote_url", remoteUrl));

            }
        });
        mIvStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStarred) {
                    //取消星标
                    cancelStarredFriend();
                } else {
                    //添加星标
                    addStarredFriend(userId);
                }
            }
        });
        mTvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //复制消息
                String phone = mTvPhone.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    showCopyOrCallPhone(phone);
                }
            }
        });
        mTvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //复制消息
                final String email = mTvEmail.getText().toString();
                if (!TextUtils.isEmpty(email)) {

                    ArrayList<String> emailList = new ArrayList<>();
                    emailList.add(getString(R.string.copy));
                    mDialogFragment = new CircleDialog.Builder()
                            .setItems(emailList, new OnLvItemClickListener() {
                                @Override
                                public boolean onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                    if (position == 0) {
                                        clipboard.setPrimaryClip(ClipData.newPlainText(null, email));
                                        MyToast.showToast(getString(R.string.email_has_copied_to_clipboard));
                                    }
                                    return false;
                                }
                            }).setNegative(getString(R.string.cancel), null).show(getSupportFragmentManager());
                }
            }
        });
        mIvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> phoneList = new ArrayList<>();
                EMGroup emGroup = EMClient.getInstance().groupManager().getGroup(groupId);
                if (emGroup == null) {
                    return;
                }
                List<String> adminList = emGroup.getAdminList();
                boolean isAdmin = false;
                if (adminList != null && !adminList.isEmpty() && adminList.contains(imUsername)) {
                    isAdmin = true;
                }

                final boolean isMuted = AppHelper.getInstance().getModel().isMute(groupId, imUsername);
                if (!isMuted) {
                    phoneList.add(getString(R.string.set_mute));
                } else {
                    phoneList.add(getString(R.string.cancel_mute));
                }
                phoneList.add(getString(R.string.remove_member));
                if (emGroup.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
                    if (isAdmin) {
                        phoneList.add(getString(R.string.remove_admin_privilege));
                    } else {
                        phoneList.add(getString(R.string.add_to_administrator));
                    }
                    phoneList.add(getString(R.string.transfer_owner_ship));
                }
                final boolean userIsAdmin = isAdmin;
                mDialogFragment = new CircleDialog.Builder()
                        .setItems(phoneList, new OnLvItemClickListener() {
                            @Override
                            public boolean onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                                showProgressDialog();
                                EaseUI.getInstance().execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            if (position == 0) {
                                                if (!isMuted) {
                                                    List<String> muteMembers = new ArrayList<String>();
                                                    muteMembers.add(imUsername);
                                                    EMClient.getInstance().groupManager().muteGroupMembers(groupId, muteMembers, 20 * 60 * 1000);
                                                    AppHelper.getInstance().getModel().muteGroupUsername(groupId, imUsername, 20 * 60 * 1000);
                                                } else {
                                                    List<String> list = new ArrayList<String>();
                                                    list.add(imUsername);
                                                    EMClient.getInstance().groupManager().unMuteGroupMembers(groupId, list);
                                                    AppHelper.getInstance().getModel().unMuteGroupUsername(groupId, imUsername);
                                                }
                                            } else if (position == 1) {
                                                EMClient.getInstance().groupManager().removeUserFromGroup(groupId, imUsername);
                                            } else if (position == 2) {
                                                if (userIsAdmin) {
                                                    EMClient.getInstance().groupManager().removeGroupAdmin(groupId, imUsername);
                                                } else {
                                                    EMClient.getInstance().groupManager().addGroupAdmin(groupId, imUsername);
                                                }

                                            } else if (position == 3) {
                                                EMClient.getInstance().groupManager().changeOwner(groupId, imUsername);
                                            }
                                        } catch (HyphenateException e) {
                                            e.printStackTrace();
                                        }
                                        if (isFinishing()) return;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                hideProgressDialog();
                                                setResult(RESULT_OK);
                                                finish();
                                            }
                                        });
                                    }
                                });
                                return false;
                            }
                        }).setNegative(getString(R.string.cancel), null).show(getSupportFragmentManager());
            }
        });


        mRlAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFriend) {
                    EMAPIManager.getInstance().deleteFriend(userId, new EMDataCallBack<String>() {
                        @Override
                        public void onSuccess(String value) {

                            try {
                                JSONObject result = new JSONObject(value);
                                if ("OK".equals(result.getString("status"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(PersonalCardActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(int error, String errorMsg) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PersonalCardActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } else {
                    EMAPIManager.getInstance().invitedOrAcceptFriend(userId, new EMDataCallBack<String>() {
                        @Override
                        public void onSuccess(String value) {

                            try {
                                JSONObject result = new JSONObject(value);
                                if ("OK".equals(result.getString("status"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(PersonalCardActivity.this, getResources().getString(R.string.send_successful), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(int error, String errorMsg) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PersonalCardActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });
    }


    private void friendRelationStatus(int friendId) {
        EMAPIManager.getInstance().friendRelationStatus(friendId, new EMDataCallBack<String>() {

            @Override
            public void onSuccess(String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(value);
                            if ("OK".equals(jsonObject.getString("status"))) {
                                JSONObject entity = jsonObject.getJSONObject("entity");
                                if ("friend".equals(entity.getString("result"))) {
                                    isFriend = true;
                                    mIvAddOrDel.setImageResource(R.drawable.delete_user_icon);
                                } else {
                                    isFriend = false;
                                    mIvAddOrDel.setImageResource(R.drawable.add_friend_icon);
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
            }
        });
    }

    private DialogFragment mDialogFragment;


    private void initData() {
        Intent gIntent = getIntent();
        if (gIntent == null) {
            finish();
            return;
        }
        imUsername = gIntent.getStringExtra("username");
        userId = gIntent.getIntExtra("userId", -1);
        friendStatus = gIntent.getIntExtra("friendStatus", -1);
        isFromStarred = gIntent.getBooleanExtra("isFromStarred", false);
        groupId = gIntent.getStringExtra("groupId");
        role = gIntent.getStringExtra("role");
        String currentUser = EMClient.getInstance().getCurrentUser();
        if (!TextUtils.isEmpty(groupId) && imUsername != null && !imUsername.equals(currentUser)) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            if ("member".equals(role) || ("admin".equals(role) && group != null && currentUser.equals(group.getOwner()))) {
                mIvMore.setVisibility(View.VISIBLE);
            } else {
                mIvMore.setVisibility(View.GONE);
            }
        } else {
            mIvMore.setVisibility(View.GONE);
        }

        MPUserEntity mpUserEntity = AppHelper.getInstance().getModel().getUserInfo(imUsername);
        if (mpUserEntity == null) {
            finish();
            return;
        }

        if (!TextUtils.isEmpty(mpUserEntity.getRealName())) {
            mTvTitle.setText(mpUserEntity.getRealName());
        }

        if (userId < 1) {
            userId = mpUserEntity.getId();
        }

        if (imUsername.equals(EMClient.getInstance().getCurrentUser())) {
            mIvStar.setVisibility(View.GONE);
            mRlAddFriend.setVisibility(View.GONE);
            mRlSendMsg.setVisibility(View.GONE);
            mRlSendPhone.setVisibility(View.GONE);
        } else {
            getIsStarred(userId);
        }
        getUserPersonalInfo();
        friendRelationStatus(userId);
//        refreshWaterMark(PersonalCardActivity.this);
    }

//    private void refreshWaterMark() {
//        if (TenantOptionCache.getInstance().isShowWaterMark()) {
//            findViewById(R.id.ll_layout).setBackground(WaterMarkBg.create(this));
//        } else {
//            findViewById(R.id.ll_layout).setBackgroundColor(getResources().getColor(R.color.bg));
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTenantOptionsChanged(EventTenantOptionChanged event) {
        if (TenantOptionCache.OPTION_NAME_WATERMARK.equals(event.getOptionName())) {
//            refreshWaterMark(PersonalCardActivity.this);
        }
    }

    /**
     * 设置内容
     */
    private void setData(final MPUserEntity entityBean, List<MPOrgEntity> orgEntities) {
        AvatarUtils.setAvatarContent(this, entityBean.getRealName(), entityBean.getAvatar(), mIvAvatar);
        mTvName.setText(entityBean.getRealName());

        if (!TextUtils.isEmpty(entityBean.getPhone()))
            mTvPhone.setText(entityBean.getPhone());
        if (!TextUtils.isEmpty(entityBean.getEmail()))
            mTvEmail.setText(entityBean.getEmail());
        if (orgEntities != null && orgEntities.size() > 0) {
            MPOrgEntity orgEntity = orgEntities.get(0);
            if (orgEntity != null) {
                if (orgEntity.getPosition() != null) {
                    tvPosition.setText(orgEntity.getPosition());
                } else {
                    tvPosition.setText("");
                }

                if (orgEntity.getFullName() != null) {
                    tvDepartment.setText(orgEntity.getFullName());
                } else {
                    tvDepartment.setText("");
                }

            }
        } else {
            tvDepartment.setText("");
            tvPosition.setText("");
        }

        if (userId == BaseRequest.getUserId() || TextUtils.isEmpty(entityBean.getImUserId())) {
            mLlSend.setVisibility(View.GONE);
        } else {
            mLlSend.setVisibility(View.VISIBLE);
            mRlSendMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //避免本地未存储app server用户名与realName时显示为easemobName
                    startActivity(new Intent(PersonalCardActivity.this, ChatActivity.class).putExtra("userId", entityBean.getImUserId()));
                    finish();
                }
            });
            mRlSendPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //弹出选择网络电话、本地电话
                    showPhoneFlipWindow();
                }
            });
        }
    }

    //改变星标
    private void notifyStarredChange() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIvStar.setImageResource(isStarred ? R.drawable.oa_icon_stared : R.drawable.oa_icon_star_normal);
            }
        });
    }

    //获取是否是收藏的好友
    private void getIsStarred(int userId) {
        EMAPIManager.getInstance().getIsStaredFriend(userId, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    String status = jsonObject.getString("status");
                    if ("OK".equalsIgnoreCase(status)) {
                        JSONObject entity = jsonObject.getJSONObject("entity");
                        if (entity != null) {
                            mIvStar.setVisibility(View.VISIBLE);
                            isStarred = entity.getBoolean("starred");
                            notifyStarredChange();
                        } else {
                            toastInvalidResponse(TAG, "entity = null");
                        }
                    } else {
                        toastInvalidResponse(TAG, "status = " + status);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    toastInvalidResponse(TAG, null);
                }

            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }


    //获取员工信息
    private void getUserPersonalInfo() {
        EMAPIManager.getInstance().getUserDetails(userId, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(final String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonResult = new JSONObject(value);
                            JSONObject jsonObj = jsonResult.optJSONObject("entity");
                            JSONArray jsonCompanyList = jsonObj.optJSONArray("companyList");
                            JSONArray jsonOrgList = jsonObj.optJSONArray("organizationList");
                            JSONArray jsonUserCompany = jsonObj.optJSONArray("userCompanyRelationshipList");

                            userEntity = MPUserEntity.create(jsonObj.optJSONObject("user"));
                            List<MPOrgEntity> orgEntities = MPOrgEntity.create(jsonOrgList, jsonCompanyList, jsonUserCompany);
                            if (userEntity != null) {
                                userEntity.setOrgId(jsonOrgList.getJSONObject(0).getInt("id"));
                                setData(userEntity, orgEntities);
                                AppHelper.getInstance().getModel().saveUserInfo(userEntity);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void addStarredFriend(int userId) {
        JSONObject jsonBody = new JSONObject();
        EMAPIManager.getInstance().addStartedFriends(userId, jsonBody.toString(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(final String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MPLog.d(TAG, value);
                        try {
                            JSONObject jsonObject = new JSONObject(value);
                            String status = jsonObject.getString("status");
                            if ("OK".equalsIgnoreCase(status)) {
                                isStarred = true;
                                notifyStarredChange();
                                if (isFromStarred)
                                    setResult(RESULT_CODE_STARRED_FRIENDS);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    private void cancelStarredFriend() {

        EMAPIManager.getInstance().deleteStaredFriends(userId, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                MPLog.d(TAG, value);
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    String status = jsonObject.getString("status");
                    if ("OK".equalsIgnoreCase(status)) {
                        isStarred = false;
                        notifyStarredChange();
                        if (isFromStarred)
                            setResult(RESULT_CODE_STARRED_FRIENDS);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    //选择复制还是拨打电话
    private void showCopyOrCallPhone(final String phone) {
        ArrayList<String> phoneList = new ArrayList<>();
        phoneList.add(getString(R.string.phone_call));
        phoneList.add(getString(R.string.copy));
        phoneList.add(getString(R.string.save_to_contacts));
        if (mDialogFragment != null) {
            mDialogFragment.dismissAllowingStateLoss();
        }
        mDialogFragment = new CircleDialog.Builder()
                .setItems(phoneList, new OnLvItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        if (position == 0) {
                            callPhone();
                        } else if (position == 1) {
                            clipboard.setPrimaryClip(ClipData.newPlainText(null, phone));
                            MyToast.showToast(getString(R.string.phone_has_copied_to_clipboard));
                        } else if (position == 2) {
                            String name = mTvName.getText().toString().trim();
                            saveToContacts(name, phone);
                        }
                        return false;
                    }
                }).setNegative(getString(R.string.cancel), null).show(getSupportFragmentManager());
    }

    //弹窗提示拨打电话
    private void showPhoneFlipWindow() {
        ArrayList<String> phoneList = new ArrayList<>();
        phoneList.add(getString(R.string.phone_call));
        if (mDialogFragment != null) {
            mDialogFragment.dismissAllowingStateLoss();
        }
        mDialogFragment = new CircleDialog.Builder()
                .setItems(phoneList, new OnLvItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        if (position == 0) {
                            callPhone();
                        }
                        return false;
                    }
                }).setNegative(getString(R.string.cancel), null).show(getSupportFragmentManager());

    }

    // 保存至现有联系人
    public void saveToContacts(String name, String phone) {
//        Uri insertUri = android.provider.ContactsContract.Contacts.CONTENT_URI;
//        Intent intent = new Intent(Intent.ACTION_INSERT, insertUri);
        Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        intent.setType("vnd.android.cursor.item/person");
        intent.setType("vnd.android.cursor.item/contact");
        intent.setType("vnd.android.cursor.item/raw_contact");
        intent.putExtra(android.provider.ContactsContract.Intents.Insert.NAME, name);
        intent.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE, phone);
        intent.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE_TYPE, 3);
        startActivity(intent);
    }

    //拨打电话
    private void callPhone() {
        String phoneNum = mTvPhone.getText().toString();
        if (!TextUtils.isEmpty(phoneNum) && AppUtil.isPhone(phoneNum)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                MyToast.showToast(getString(R.string.permission_deny));
                requestPermissions(Manifest.permission.CALL_PHONE);
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + phoneNum.trim());
                intent.setData(data);
                startActivity(intent);
            }
        } else {
            MyToast.showToast(getString(R.string.phone_invalid));
        }
    }

    @TargetApi(23)
    private void requestPermissions(final String permission) {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(activity, new String[]{permission}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                if (Manifest.permission.CALL_PHONE.equals(permission))
                    callPhone();
            }

            @Override
            public void onDenied(String permission) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialogFragment != null) {
            mDialogFragment.dismissAllowingStateLoss();
        }
    }
}
