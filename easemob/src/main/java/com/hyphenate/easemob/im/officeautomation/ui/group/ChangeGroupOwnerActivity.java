package com.hyphenate.easemob.im.officeautomation.ui.group;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.easeui.domain.MPGroupMemberEntity;
import com.hyphenate.easemob.easeui.widget.listview.check.KylinCheckListView;
import com.hyphenate.easemob.easeui.widget.search.SearchEditText;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.im.mp.widget.ClearEditText;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;
import com.hyphenate.easemob.im.officeautomation.ui.group.GroupMemberViewModelLeftCheck;
import com.hyphenate.easemob.im.officeautomation.widget.SimpleDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.zhouzhuo.zzletterssidebar.utils.CharacterParser;

public class ChangeGroupOwnerActivity extends BaseActivity {

    private static final String TAG = "ChangeGroupOwnerActivity";
    private KylinCheckListView checkListView;
    private ImageView ivBack;
    private TextView tvRight;
    private TextView tvTitle;
    private MPGroupEntity mpGroupEntity;
    private List<MPGroupMemberEntity> memberEntities = Collections.synchronizedList(new ArrayList<>());
    private List<MPGroupMemberEntity> copyMemberEntities = Collections.synchronizedList(new ArrayList<>());
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog loadingPopup;
    private boolean isOwner;
//    private ClearEditText mClearEditText;
    private SearchEditText mClearEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members_3);
        initViews();
        initListeners();

        initDatas();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvRight = findViewById(R.id.tv_right);
        tvTitle = findViewById(R.id.tv_title);
        checkListView = findViewById(R.id.cv_content);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        mClearEditText = findViewById(R.id.filter_edit_owner);

        tvRight.setText(R.string.btn_ensure);
        tvTitle.setText("更换群主");

        // 设置Item布局
        checkListView.setItemClass(GroupMemberViewModelLeftCheck.class);
        // 设置多选
        checkListView.setShowType(KylinCheckListView.RADIO);
        checkListView.setShowLocation(KylinCheckListView.CHECKLEFT);
        // 创建CheckList
        checkListView.create();

        checkListView.getRecyclerView().addItemDecoration(new SimpleDividerItemDecoration(activity));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateGroup();
            }
        });

        swipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
                if (checkListView.getRecyclerView() == null) {
                    return false;
                }
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) checkListView.getRecyclerView().getLayoutManager();
                return linearLayoutManager.findFirstCompletelyVisibleItemPosition() != 0;
            }
        });

        loadingPopup = new ProgressDialog(ChangeGroupOwnerActivity.this);
        loadingPopup.setTitle("正在加载中...");

//        mClearEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                mClearEditText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
//            }
//        });
        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.setOnSearchClickListener(new SearchEditText.OnSearchClickListener() {
            @Override
            public void onSearchClick(View view) {
                String keyword = mClearEditText.getText().toString();
                if (!TextUtils.isEmpty(keyword)) {
                    // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                    filterData(keyword);
                }


            }
        });
    }

    private void initListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 确认多选删除
                int position = checkListView.radioResult();
                if (position == -1) {
                    ToastUtils.showShort("您还未选择");
                    return;
                }
                MPGroupMemberEntity entty = (MPGroupMemberEntity) checkListView.getData(position);
                if (entty == null) {
                    return;
                }
                changeGroupOwner(mpGroupEntity.getId(), entty.getUserId());
            }
        });

    }


    private void initDatas() {

        mpGroupEntity = getIntent().getParcelableExtra("groupEntity");
        isOwner = isOwner(mpGroupEntity);
        checkListView.setDataToView(memberEntities);
//        checkListView.setCbBackgroup(R.drawable.circle_checkbox_bg);
        updateGroup();
    }


    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<MPGroupMemberEntity> filterMemberItems = new ArrayList<>();
        filterMemberItems.clear();
        if (TextUtils.isEmpty(filterStr)) {
            synchronized (copyMemberEntities) {
                for (MPGroupMemberEntity item : copyMemberEntities) {
                    item.isCheck = false;
                    filterMemberItems.add(item);
                }
            }
        } else {
            synchronized (copyMemberEntities) {
                for (MPGroupMemberEntity item : copyMemberEntities) {
                    String name = item.getNick();
                    if (name.contains(filterStr) || CharacterParser.getInstance().getSelling(name).startsWith(filterStr)) {
                        filterMemberItems.add(item);
                    }
                }
            }
        }
        memberEntities.clear();
        memberEntities.addAll(filterMemberItems);
        checkListView.updataAdapter();
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
        dismissLoading();
    }

    private void dismissLoading() {
        if (loadingPopup != null && loadingPopup.isShowing()) {
            loadingPopup.dismiss();
        }
    }

    private void updateGroup() {
        loadingPopup.show();
        StringBuilder sb = new StringBuilder();
        //page=%2$s&size=%3$s&isRegion=%4$s
        sb.append("page=0&size=").append(200);
        if (mpGroupEntity.isCluster()) {
            sb.append("&isRegion=1");
        }
        EMAPIManager.getInstance().getMemberList(mpGroupEntity.getId(), sb.toString(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject jsonObj = new JSONObject(value);
                    JSONArray jsonArr = jsonObj.optJSONArray("entities");
                    List<MPGroupMemberEntity> tempList = MPGroupMemberEntity.create(jsonArr);
                    memberEntities.clear();
                    memberEntities.addAll(dealWithGroupMembers(tempList));
                    copyMemberEntities.clear();
                    copyMemberEntities.addAll(memberEntities);
                    if (isFinishing()) {
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoading();
                            swipeRefreshLayout.setRefreshing(false);
                            checkListView.updataAdapter();
                        }
                    });
                } catch (Exception e) {
                    MPLog.e(TAG, "getMemberList json parse error");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoading();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }

            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getApplicationContext(), "加载失败,请下拉刷新！", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private List<MPGroupMemberEntity> dealWithGroupMembers(List<MPGroupMemberEntity> memberEntities) {
        List<MPGroupMemberEntity> list = new ArrayList<>();
        for (MPGroupMemberEntity item : memberEntities) {
            if (isOwner) {
                if ("owner".equalsIgnoreCase(item.getType())) {
                    continue;
                }
            }
            EaseUser userEntity = UserProvider.getInstance().getEaseUserById(item.getUserId());
            if (userEntity != null) {
                item.setNick(userEntity.getNickname());
                item.setAvatar(userEntity.getAvatar());
            } else {
                item.setNick(String.valueOf(item.getUserId()));
            }
            if ("owner".equalsIgnoreCase(item.getType())) {
                list.add(0, item);
            } else {
                list.add(item);
            }

        }
        return list;
    }


    private void changeGroupOwner(int groupId, int changedOwnerUserId) {
        loadingPopup.show();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("userId", changedOwnerUserId);
            jsonObj.put("isRegion", mpGroupEntity.isCluster() ? 1 : 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EMAPIManager.getInstance().changeGroupOwner(groupId, mpGroupEntity.isCluster(), jsonObj.toString(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(activity, "群主转移成功！", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                });

            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getApplicationContext(), "加载失败,请下拉刷新！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}
