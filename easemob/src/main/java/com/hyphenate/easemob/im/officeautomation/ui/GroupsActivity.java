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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.eventbus.Subscribe;
import com.hyphenate.eventbus.ThreadMode;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupDeleted;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupsChanged;
import com.hyphenate.easemob.im.mp.rest.EMJoinedGroupsRequest;
import com.hyphenate.easemob.im.mp.widget.ClearEditText;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.adapter.GroupsAdapter;
import com.hyphenate.easemob.im.officeautomation.ui.group.GroupAddMemberActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GroupsActivity extends BaseActivity {
    private static final String TAG = "GroupsActivity";
    private RecyclerView rvGroups;
    protected List<MPGroupEntity> grouplist;
    private GroupsAdapter groupsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View btnAdd;
    private ClearEditText mClearEditText;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_fragment_groups);
        MPEventBus.getDefault().register(this);
        initViews();
        initListeners();

        initData();
    }


    private void initViews() {
        ImageView back = findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mClearEditText = findViewById(R.id.filter_edit);
        rvGroups = findViewById(R.id.rv);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);
        btnAdd = findViewById(R.id.iv_add);

        //show group list
        grouplist = new ArrayList<>();
        rvGroups.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvGroups.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvGroups.setAdapter(groupsAdapter = new GroupsAdapter(this, R.layout.em_row_group, grouplist));
//        groupsAdapter.setEmptyView(R.layout.lfile_emptyview);
    }

    private void initListeners() {
        //pull down to refresh
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshFromServer();
            }
        });
        groupsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MPGroupEntity bean = (MPGroupEntity) adapter.getItem(position);
                if (bean == null) {
                    return;
                }
                // enter group chat
                Intent intent = new Intent(GroupsActivity.this, ChatActivity.class);
                // it is group chat
                intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                intent.putExtra("userId", bean.getImChatGroupId());
                startActivity(intent);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a new group
//                startActivity(new Intent(GroupsActivity.this, NewChatSelectActivity.class));
                startActivity(new Intent(activity, GroupAddMemberActivity.class).putExtra("isCreate", true));
            }
        });
        mClearEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mClearEditText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                } else {
                    mClearEditText.setGravity(Gravity.CENTER);
                }
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

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<MPGroupEntity> filterGroups = new ArrayList<>();
        if (TextUtils.isEmpty(filterStr)) {
            filterGroups = grouplist;
        } else {
            filterGroups.clear();
            synchronized (grouplist) {
                for (MPGroupEntity item : grouplist) {
                    String name = item.getName();
                    String groupId = item.getImChatGroupId();
                    if (name.contains(filterStr) || groupId.contains(filterStr)) {
                        filterGroups.add(item);
                    }
                }
            }
        }
        groupsAdapter.setNewData(filterGroups);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventGroupDeleted(EventGroupDeleted deleted) {
        grouplist.clear();
        groupsAdapter.notifyDataSetChanged();
        requestData();
    }


    private void initData() {

        refresh();
        requestData();
    }

    private void requestData() {
        new EMJoinedGroupsRequest().setListener(new EMDataCallBack<List<MPGroupEntity>>() {
            @Override
            public void onSuccess(List<MPGroupEntity> value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        grouplist.clear();
                        grouplist.addAll(value);
                        sortGroupList(grouplist);
                        groupsAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).request();
    }

    private void sortGroupList(List<MPGroupEntity> grouplist) {
        if (grouplist == null || grouplist.isEmpty()) {
            return;
        }
        Collections.sort(grouplist, new Comparator<MPGroupEntity>() {
            @Override
            public int compare(MPGroupEntity group1, MPGroupEntity group2) {
                return Long.compare(group1.getCreateTime(), group2.getCreateTime());
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupChanged(EventGroupsChanged changed) {
        refresh();
    }


    private void refresh() {
        if (grouplist != null)
            grouplist.clear();
        if (groupsAdapter != null)
            groupsAdapter.notifyDataSetChanged();
//        ArrayList<MPGroupEntity> extGroupList = AppHelper.getInstance().getModel().getExtGroupList();
        List<MPGroupEntity> extGroupList = AppHelper.getInstance().getModel().getAllGroups();
        if (extGroupList != null && extGroupList.size() > 0) {
            swipeRefreshLayout.setRefreshing(false);
            grouplist.addAll(extGroupList);
            groupsAdapter.notifyDataSetChanged();
        } else {
            refreshFromServer();
        }
    }

    private void refreshFromServer() {
        swipeRefreshLayout.setRefreshing(true);
        requestData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MPEventBus.getDefault().unregister(this);
    }
}
