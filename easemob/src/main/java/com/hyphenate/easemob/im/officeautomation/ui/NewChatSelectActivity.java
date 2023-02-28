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

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.adapter.CustomListAdapter;
import com.hyphenate.easemob.im.officeautomation.adapter.NewChatAdapter;
import com.hyphenate.easemob.im.officeautomation.adapter.NewChatDepartmentAdapter;
import com.hyphenate.easemob.im.officeautomation.adapter.NewChatGroupAdapter;
import com.hyphenate.easemob.im.officeautomation.domain.DepartmentBean;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.widget.CustomHorizontalScrollview;
import com.hyphenate.easemob.im.officeautomation.widget.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by qby on 2018/06/21.
 * 发起群聊选择页面
 */
public class NewChatSelectActivity extends BaseActivity {

    private static final String TAG = "NewChatSelectActivity";
    public static final int RESULT_CODE_CHAT_DETAILS = 1;
    //适配器
    private NewChatAdapter selectAdapter;
    //索引展示数据
    private ArrayList<DepartmentBean> indexList;
    //供选择的部门
    private ArrayList<MPOrgEntity> selectOrgsList;
    //供选择的联系人列表
    private ArrayList<EaseUser> selectContactList;
    //已选中的
    private ArrayList<EaseUser> selectedList;
    //已有成员
    private ArrayList<String> members;
    //群组列表
    private ArrayList<GroupBean> groupList;
    //组织架构选人列表，部门
    private List<MPOrgEntity> selectDepartList;
    //组织架构选人列表，员工
    private List<EaseUser> selectUserList;
    //不展示当前聊天的会话
    private String current_id;
    private LinearLayout ll;
    private LinearLayout ll_change;
    private TextView tv_sure;
    //如果是添加群成员
    private String imGroupId;
    private HorizontalScrollView hsv;
    private TextView tv_title;
    private RecyclerView rv;
    private RecyclerView rv_group;
    private RecyclerView rv_depart;
    private CustomHorizontalScrollview custom_hsv;
    private ImageView iv_back;
    private NewChatGroupAdapter groupAdapter;
    private NewChatDepartmentAdapter departmentAdapter;
    private CustomListAdapter indexListAdapter;
    private boolean isGroups;
    private boolean isDepartments;
    private int currentOrgId;
    private boolean isAddMember;
    private boolean isBack;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(getLayout());
        initViews();
        initListeners();
        initData();
    }

    protected int getLayout() {
        return R.layout.activity_new_chat_select;
    }

    public void initViews() {
        tv_title = $(R.id.tv_title);
        rv = $(R.id.rv);
        rv_group = $(R.id.rv_group);
        rv_depart = $(R.id.rv_depart);
        iv_back = $(R.id.iv_back);
        ll = $(R.id.ll);
        ll_change = $(R.id.ll_change);
        custom_hsv = $(R.id.custom_hsv);
        tv_sure = $(R.id.tv_submit);
        hsv = $(R.id.hsv);
    }

    protected void initListeners() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (this) {
                    //点击确定
                    if (selectedList.size() > 0) {
                        if (TextUtils.isEmpty(imGroupId)) {
                            if (selectedList.size() > 1) {
                                if (selectedList.size() >= EaseConstant.MAX_GROUP_COUNT) {
                                    Toast.makeText(NewChatSelectActivity.this, getString(R.string.exceceding_maximum_group, EaseConstant.MAX_GROUP_COUNT), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                ArrayList<String> imageUrls = new ArrayList<>();
                                startActivity(new Intent(NewChatSelectActivity.this, CreateGroupActivity.class).putExtra("invite_list", selectedList).putExtra("imageUrls", imageUrls));
                                finish();
                            }
                        } else {
                            ArrayList<Integer> var = getToBeAddMembers();
                            Intent intent = new Intent();
                            intent.putIntegerArrayListExtra("newmembers", var);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }
            }
        });
    }

    protected void initData() {
        current_id = getIntent().getStringExtra("current_id");
        imGroupId = getIntent().getStringExtra("imGroupId");
        isAddMember = getIntent().getBooleanExtra("add_member", false);
        if (isAddMember) {
            if (TextUtils.isEmpty(current_id) && TextUtils.isEmpty(imGroupId)) {
                tv_title.setText(getString(R.string.create_group));
            } else {
                tv_title.setText(getString(R.string.add_member));
            }
        }

        indexList = new ArrayList<>();
        selectOrgsList = new ArrayList<>();
        selectContactList = new ArrayList<>();
        selectedList = new ArrayList<>();
        members = new ArrayList<>();
        groupList = new ArrayList<>();
        selectDepartList = new ArrayList<>();
        selectUserList = new ArrayList<>();

        indexListAdapter = new CustomListAdapter(this, R.layout.item_contacts_list_title_custom, indexList, new CustomListAdapter.DepartmentIndexItemCallback() {
            @Override
            public void onClick(Integer position) {
                if (position != indexListAdapter.getCurrentPosition()) {
                    refreshIndexList(position);
                }
            }
        });
        custom_hsv.setAdapter(indexListAdapter);

        requestData();

        rv.setLayoutManager(new LinearLayoutManager(activity));
        rv_group.setLayoutManager(new LinearLayoutManager(activity));
        rv_depart.setLayoutManager(new LinearLayoutManager(activity));
        rv.addItemDecoration(new SimpleDividerItemDecoration(activity));
        rv_group.addItemDecoration(new SimpleDividerItemDecoration(activity));
        rv_depart.addItemDecoration(new SimpleDividerItemDecoration(activity));

        groupAdapter = new NewChatGroupAdapter(this, groupList, new NewChatGroupAdapter.GroupCallback() {
            @Override
            public void onItemClick(int position) {
                // enter group chat
                Intent intent = new Intent(NewChatSelectActivity.this, ChatActivity.class);
                // it is group chat
                intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                intent.putExtra("userId", groupList.get(position).getImGroupId());
                startActivity(intent);
            }
        });
        rv_group.setAdapter(groupAdapter);

        departmentAdapter = new NewChatDepartmentAdapter(this, selectDepartList, selectUserList, selectedList, members, new NewChatDepartmentAdapter.DepartmentItemCallback() {
            @Override
            public void onDepartmentClick(int position) {
                MPOrgEntity entitiesBean = selectDepartList.get(position);
                DepartmentBean departmentBean = new DepartmentBean(entitiesBean.getName(), entitiesBean.getId());
                //刷新索引
                indexList.add(departmentBean);
                indexListAdapter.notifyDataSetChanged();
                custom_hsv.fillViewWithAdapter(indexListAdapter);
                getDepartment(departmentBean);
            }

            @Override
            public void onAllClick() {
                for (int i = 0; i < selectUserList.size(); i++) {
                    EaseUser easeUser = selectUserList.get(i);
                    if (!EMClient.getInstance().getCurrentUser().equals(easeUser.getUsername()))
                        doIfChecked(easeUser);
                }
            }

            @Override
            public void onUserClick(int position) {
                EaseUser user = selectUserList.get(position);
                doIfChecked(user);
            }
        });
        rv_depart.setAdapter(departmentAdapter);

        // set adapter
        selectAdapter = new NewChatAdapter(this, selectOrgsList, selectContactList, selectedList, members, !isAddMember, new NewChatAdapter.NewChatItemCallback() {
            @Override
            public void onGroupClick() {
                showBack();
                isGroups = true;
                getGroups();
            }

            @Override
            public void onDepartmentClick(int position) {
                showBack();
                isDepartments = true;
                MPOrgEntity entitiesBean = selectOrgsList.get(position);
                DepartmentBean departmentBean = new DepartmentBean(entitiesBean.getName(), entitiesBean.getId());
                //刷新索引
                indexList.add(departmentBean);
                indexListAdapter.notifyDataSetChanged();
                custom_hsv.fillViewWithAdapter(indexListAdapter);
                getDepartment(departmentBean);
            }

            @Override
            public void onUserClick(int position) {
                if (position <= 0){
                    return;
                }
                //选中
                EaseUser user = selectContactList.get(position - 1);
                doIfChecked(user);
            }
        });
        rv.setAdapter(selectAdapter);
    }

    //请求进入页面时展示的数据
    private void requestData() {
        getMembers();
        getOrgsList();
        getSelectContactList();
    }

    //选中或移除
    protected void doIfChecked(EaseUser user) {
        synchronized (user) {
            if (selectedList.contains(user)) {
                removeFromLL(user);
            } else {
                addToLL(user);
            }
        }
    }

    //获取已选中成员列表
    private void getMembers() {
        if (!TextUtils.isEmpty(imGroupId)) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(imGroupId);
            members.add(group.getOwner());
            members.addAll(group.getAdminList());
            members.addAll(group.getMuteList());
            members.addAll(group.getBlackList());
            members.addAll(group.getMembers());
        } else if (!TextUtils.isEmpty(current_id)) {
            members.add(current_id);
            //添加当前聊天对象到选中列表
            EaseUser toUser = EaseUserUtils.getUserInfo(current_id);
            //添加当前聊天对象头像到hsv
            addToLL(toUser);
        }
        MPLog.e(TAG, "成员数：" + members.size() + ":" + (members.size() > 0 ? members.get(0) : ""));
    }

    //获取所有部门
    private void getOrgsList() {
        List<MPOrgEntity> orgsListByParent = AppHelper.getInstance().getModel().getOrgsListByParent(Constant.COMPANY_BASE_ID);
        if (orgsListByParent != null) {
            selectOrgsList.addAll(orgsListByParent);
        }
    }

    //获取所有用户
    protected void getSelectContactList() {
        selectContactList.clear();
        ArrayList<EaseUser> easeUsers = new ArrayList<>(AppHelper.getInstance().getModel().getExtUserList());
        for (EaseUser easeUser : easeUsers) {
            //如果是当前用户，跳过
//            if (PreferenceManager.getInstance().getLoginEaseName().equalsIgnoreCase(easeUser.getUsername()))
//                continue;
            if (TextUtils.isEmpty(easeUser.getUsername()) || easeUser.getUsername().equalsIgnoreCase(EMClient.getInstance().getCurrentUser())){
                continue;
            }

            selectContactList.add(easeUser);
        }

        Collections.sort(selectContactList, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if (lhs.getInitialLetter().equals(rhs.getInitialLetter())) {
                    return lhs.getNick().compareTo(rhs.getNick());
                } else {
                    if ("#".equals(lhs.getInitialLetter())) {
                        return 1;
                    } else if ("#".equals(rhs.getInitialLetter())) {
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });
    }

    //获取群组列表数据
    private void getGroups() {
        rv_group.setVisibility(View.VISIBLE);
        rv_depart.setVisibility(View.GONE);
        //刷新索引
        DepartmentBean departmentBean = new DepartmentBean(getString(R.string.groups_list), 0);
        indexList.add(departmentBean);
        indexListAdapter.notifyDataSetChanged();
        custom_hsv.fillViewWithAdapter(indexListAdapter);
        //刷新数据
        groupList.clear();
        ArrayList<GroupBean> extGroupList = AppHelper.getInstance().getModel().getExtGroupList();
        groupList.addAll(extGroupList);
        groupAdapter.notifyDataSetChanged();
    }


    //展示部门信息列表
    private void getDepartment(DepartmentBean departmentBean) {
        rv_group.setVisibility(View.GONE);
        rv_depart.setVisibility(View.VISIBLE);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                custom_hsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 100);
        //刷新部门数据
        selectDepartList.clear();
        selectUserList.clear();
        List<MPOrgEntity> orgs = AppHelper.getInstance().getModel().getOrgsListByParent(departmentBean.getId());
        List<EaseUser> users = AppHelper.getInstance().getModel().getExtUsersByOrgId(departmentBean.getId());
        selectDepartList.addAll(orgs);
        for (int i = 0; i < users.size(); i++) {
            EaseUser easeUser = users.get(i);
            if (TextUtils.isEmpty(easeUser.getUsername()))
                continue;
            selectUserList.add(easeUser);
        }
//        selectUserList.addAll(users);
        departmentAdapter.notifyDataSetChanged();
    }

    /**
     * 获取群成员头像
     *
     * @param imageUrls
     * @param avatar
     */
//    private void getImageUrl(ArrayList<String> imageUrls, String avatar) {
//        if (!TextUtils.isEmpty(avatar) && !avatar.startsWith("http")) {
//            avatar = EaseConstant.BASE_URL + avatar;
//        }
//        MyLog.i("合成群组头像" + imageUrls.size() + ":", avatar);
//        imageUrls.add(avatar);
//    }

    /**
     * 从群详情添加成员
     *
     * @return
     */
    private ArrayList<Integer> getToBeAddMembers() {
        ArrayList<Integer> newMembers = new ArrayList<>();
        for (int i = 0; i < selectedList.size(); i++) {
            EaseUser easeUser = selectedList.get(i);
            newMembers.add(easeUser.getId());
        }
        return newMembers;
    }

    private void addToLL(EaseUser user) {
        selectedList.add(user);
        tv_sure.setText(String.format(getString(R.string.select_someone), selectedList.size()));

        View view = View.inflate(this, R.layout.item_image_view, null);
        AvatarImageView imageView = view.findViewById(R.id.iv_avatar);
//        String remoteUrl = user.getAvatar();
        AvatarUtils.setAvatarContent(activity, user, imageView);
//        GlideUtils.load(this, remoteUrl, R.drawable.em_default_avatar, imageView);
        ll.addView(view);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                hsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 100L);
    }

    private void removeFromLL(EaseUser user) {
        int i = selectedList.indexOf(user);
        selectedList.remove(user);
        tv_sure.setText(String.format(getString(R.string.select_someone), selectedList.size()));
        try {
            ll.removeViewAt(i);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    hsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                }
            }, 100L);
        } catch (Exception e) {
            MPLog.e(TAG, "头像列表没有展示该用户");
        }
    }

    private void showFore() {
        isBack = false;
        isGroups = false;
        isDepartments = false;
        rv.setVisibility(View.VISIBLE);
        ll_change.setVisibility(View.GONE);
        selectAdapter.notifyDataSetChanged();
    }

    private void showBack() {
        isBack = true;
        rv.setVisibility(View.GONE);
        ll_change.setVisibility(View.VISIBLE);
        //初始化索引添加“联系人”
        indexList.clear();
        DepartmentBean departmentBean = new DepartmentBean(getString(R.string.contacts), -1);
        indexList.add(departmentBean);
        departmentAdapter.notifyDataSetChanged();
    }

    //刷新索引
    private void refreshIndexList(int position) {
        //截取索引列表
        List<DepartmentBean> departmentBeans = indexList.subList(0, position + 1);
        ArrayList<DepartmentBean> copyDepartmentIndexList = new ArrayList<>(departmentBeans);
        indexList.clear();
        indexList.addAll(copyDepartmentIndexList);
        //刷新索引列表
        indexListAdapter.notifyDataSetChanged();
        custom_hsv.fillViewWithAdapter(indexListAdapter);

        if (position == 0) {
            showFore();
        } else {
            if (isDepartments) {
                DepartmentBean department = indexList.get(position);
                getDepartment(department);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isBack && indexListAdapter.getCurrentPosition() > 0) {
            refreshIndexList(indexListAdapter.getCurrentPosition() - 1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
