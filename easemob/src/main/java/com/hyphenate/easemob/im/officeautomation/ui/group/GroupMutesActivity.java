package com.hyphenate.easemob.im.officeautomation.ui.group;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.widget.ContentLoadingProgressBar;

import com.blankj.utilcode.util.ToastUtils;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.easeui.domain.MPGroupMemberEntity;
import com.hyphenate.easemob.easeui.widget.listview.check.KylinCheckListView;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.eventbus.Subscribe;
import com.hyphenate.eventbus.ThreadMode;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupMuteChanged;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;
import com.hyphenate.easemob.im.officeautomation.ui.group.GroupMuteSettingActivity;
import com.hyphenate.easemob.im.officeautomation.ui.group.GroupMuteViewModel;
import com.hyphenate.easemob.im.officeautomation.widget.SimpleDividerItemDecoration;
import com.lxj.xpopup.core.BasePopupView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupMutesActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "GroupMutesActivity";
    private static final int PAGE_SIZE = 2000;
    private static final int REQUEST_CODE_MUTE_SETTINGS = 1001;

    private TextView tvRight;
    private ImageView ivBack;
    private TextView tvTitle;


    private KylinCheckListView checkListView;
    private List<MPGroupMemberEntity> memberEntities = Collections.synchronizedList(new ArrayList<>());
    private MPGroupEntity mpGroupEntity;
    private ContentLoadingProgressBar progressBar;
    private boolean isOwner;

    private RelativeLayout rlMuteAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_mutes);
        MPEventBus.getDefault().register(this);
        initViews();
        initListeners();
        initDatas();
    }

    private void initViews() {
        tvRight = findViewById(R.id.tv_right);
        tvRight.setVisibility(View.GONE);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);

        checkListView = findViewById(R.id.cv_content);
        tvTitle.setText("群禁言设置");

        rlMuteAdd = findViewById(R.id.rl_mute_add);
        progressBar = findViewById(R.id.progress_bar);

    }

    private void initListeners() {
        tvRight.setOnClickListener(this);
        ivBack.setOnClickListener(this);

        rlMuteAdd.setOnClickListener(this);
    }

    private void initDatas() {
        mpGroupEntity = getIntent().getParcelableExtra("groupEntity");
        isOwner = isOwner(mpGroupEntity);
        // 设置Item布局
        checkListView.setItemClass(GroupMuteViewModel.class);
        // 设置多选
        checkListView.setShowType(KylinCheckListView.NONE);
        checkListView.setShowLocation(KylinCheckListView.CHECKNONE);
        // 创建CheckList
        checkListView.create();

        checkListView.getRecyclerView().addItemDecoration(new SimpleDividerItemDecoration(activity));
        checkListView.setDataToView(memberEntities);

        updateGroupMute();
    }

    private boolean isOwner(MPGroupEntity groupEntity) {
        if (groupEntity == null) {
            return false;
        }
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser == null) {
            return false;
        }
        return groupEntity.getOwnerId() == loginUser.getId();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MPEventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.rl_mute_add) {
            Intent intent = new Intent(activity, GroupMuteSettingActivity.class);
            intent.putExtra("groupId", mpGroupEntity.getId());
            intent.putExtra("isRegion", mpGroupEntity.isCluster());
            startActivityForResult(intent, REQUEST_CODE_MUTE_SETTINGS);
        }

    }


    private void updateGroupMute() {
        progressBar.setVisibility(View.VISIBLE);
        StringBuilder sb = new StringBuilder();
        //page=%2$s&size=%3$s&isRegion=%4$s
        sb.append("page=").append(0).append("&size=").append(PAGE_SIZE);
        if (mpGroupEntity.isCluster()) {
            sb.append("&isRegion=1");
        }

        EMAPIManager.getInstance().getMuteGroupMembers(mpGroupEntity.getId(), sb.toString(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject jsonObj = new JSONObject(value);
                    JSONArray jsonArrEntities = jsonObj.optJSONArray("entities");
                    List<MPGroupMemberEntity> tempMembers = MPGroupMemberEntity.create(jsonArrEntities);
                    memberEntities.clear();
                    memberEntities.addAll(dealWithGroupMembers(tempMembers));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            checkListView.updataAdapter();
                        }
                    });

                } catch (Exception e) {
                    MPLog.e(TAG, "getMuteGroupMembers error:" + MPLog.getStackTraceString(e));
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                MPLog.e(TAG, "getMuteGroupMembers - error:" + errorMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShort("请求数据失败！");
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }
        });

    }


    private List<MPGroupMemberEntity> dealWithGroupMembers(List<MPGroupMemberEntity> memberEntities) {
        List<MPGroupMemberEntity> list = new ArrayList<>();
        for (MPGroupMemberEntity item : memberEntities) {
//            if ("owner".equalsIgnoreCase(item.getType())) {
//                continue;
//            }
            EaseUser userEntity = UserProvider.getInstance().getEaseUserById(item.getUserId());
            if (userEntity != null) {
                item.setNick(userEntity.getNickname());
                item.setAvatar(userEntity.getAvatar());
            } else {
                item.setNick(String.valueOf(item.getUserId()));
            }
            item.setCluster(mpGroupEntity.isCluster());
            list.add(item);
        }
        return list;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMuteMembersChanged(EventGroupMuteChanged event) {
        updateGroupMute();
    }
}
