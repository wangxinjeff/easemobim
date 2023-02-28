package com.hyphenate.easemob.im.officeautomation.ui.group;

import android.content.Intent;
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

import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.easeui.domain.MPGroupMemberEntity;
import com.hyphenate.easemob.easeui.widget.listview.check.KylinCheckListView;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupMemberChanged;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupsChanged;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.im.mp.widget.ClearEditText;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;
import com.hyphenate.easemob.im.officeautomation.widget.SimpleDividerItemDecoration;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.zhouzhuo.zzletterssidebar.utils.CharacterParser;

public class DeleteGroupMemberActivity extends BaseActivity {

    private static final String TAG = "DeleteGroupMemberActivity";
    private KylinCheckListView checkListView;
    private ImageView ivBack;
    private TextView tvRight;
    private TextView tvTitle;
    private MPGroupEntity mpGroupEntity;
    private List<MPGroupMemberEntity> memberEntities = Collections.synchronizedList(new ArrayList<>());
    private List<MPGroupMemberEntity> copyMemberEntities = Collections.synchronizedList(new ArrayList<>());
    private SwipeRefreshLayout swipeRefreshLayout;
    private BasePopupView loadingPopup;
    private boolean isOwner;
    private ClearEditText mClearEditText;
    private ArrayList<Integer> pickList = new ArrayList<>();
    private int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members_3);

        size = getIntent().getIntExtra("size", 0);

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
        mClearEditText = findViewById(R.id.filter_edit);

        tvRight.setText(R.string.btn_ensure);
        tvTitle.setText("删除成员");

        // 设置Item布局
        checkListView.setItemClass(GroupMemberViewModel.class);
        // 设置多选
        checkListView.setShowType(KylinCheckListView.MULTISELECT);
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

        loadingPopup = new XPopup.Builder(activity).asLoading("正在加载中...");

        mClearEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mClearEditText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            }
        });
        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }
        });

    }

    private void initListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (size > pickList.size()) {
                    Intent intent = new Intent();
                    intent.putExtra("pickList", pickList);
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        });

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 确认多选删除
                List<Integer> integers = checkListView.multResults();
                StringBuilder sb = new StringBuilder();
                boolean isFirst = true;
                for (Integer item : integers) {
                    MPGroupMemberEntity entty = (MPGroupMemberEntity) checkListView.getData(item);
                    if (isFirst) {
                        isFirst = false;
                        sb.append(entty.getUserId());
                    } else {
                        sb.append(",").append(entty.getUserId());
                    }
                }
                removeMembersFromGroup(mpGroupEntity.getId(), sb.toString());
            }
        });

//        checkListView.setOnItemLongClickListener(new KylinCheckListView.OnLongItemClickListener() {
//            @Override
//            public boolean onLongClick(int position) {
//                if (!isOwner) {
//                    return false;
//                }
//                if (position > memberEntities.size()) {
//                    return false;
//                }
//                MPGroupMemberEntity memberEntity = memberEntities.get(position);
//                copyMemberEntities.remove(memberEntity);
//                new XPopup.Builder(activity).asCenterList(null, new String[]{"删除", "取消"}, new OnSelectListener() {
//                    @Override
//                    public void onSelect(int selPos, String text) {
//                        if (selPos == 0) {
//                            memberEntities.remove(position);
//                            checkListView.updataAdapter();
//                            removeMemberFromGroup(mpGroupEntity.getId(), memberEntity.getUserId());
//
//                        }
//                    }
//                }).show();;
//                return true;
//            }
//        });
    }

    private void initDatas() {

        mpGroupEntity = getIntent().getParcelableExtra("groupEntity");
        isOwner = isOwner(mpGroupEntity);
        checkListView.setDataToView(memberEntities);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                List<MPGroupMemberEntity> groupMemberEntityList = dealWithGroupMembers();
//                if (isFinishing()) {
//                    return;
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        memberEntities.addAll(groupMemberEntityList);
//                        checkListView.updataAdapter();
//                    }
//                });
//
//            }
//        }).start();
        updateGroup();

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<MPGroupMemberEntity> filterMemberItems = new ArrayList<>();
        if (TextUtils.isEmpty(filterStr)) {
            filterMemberItems = copyMemberEntities;
        } else {
            filterMemberItems.clear();
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
        if (loadingPopup != null && loadingPopup.isShow()) {
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
                    pickList.clear();
                    for (MPGroupMemberEntity entity : tempList) {
                        pickList.add(entity.getId());

                    }
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

//    private void removeMemberFromGroup(int groupId, int removedUserId) {
//        loadingPopup.show();
//        EMAPIManager.getInstance().deleteMemberFromGroup(groupId, removedUserId, new EMDataCallBack<String>() {
//            @Override
//            public void onSuccess(String value) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dismissLoading();
//                        swipeRefreshLayout.setRefreshing(false);
//                        Toast.makeText(activity, "删除成员成功！", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//            }
//
//            @Override
//            public void onError(int error, String errorMsg) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dismissLoading();
//                        swipeRefreshLayout.setRefreshing(false);
//                        Toast.makeText(getApplicationContext(), "加载失败,请下拉刷新！", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }

    private void removeMembersFromGroup(int groupId, String removedUserIds) {
        loadingPopup.show();
        EMAPIManager.getInstance().deleteMembersFromGroup(groupId, removedUserIds, mpGroupEntity.isCluster(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(activity, "删除成员成功！", Toast.LENGTH_SHORT).show();
                        updateGroup();
                        MPEventBus.getDefault().post(new EventGroupMemberChanged());
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
                        Toast.makeText(getApplicationContext(), "删除失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}
