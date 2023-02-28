package com.hyphenate.easemob.im.officeautomation.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.adapter.CustomListAdapter;
import com.hyphenate.easemob.im.officeautomation.adapter.DepartmentRefreshFooterAdapter;
import com.hyphenate.easemob.im.officeautomation.domain.DepartmentBean;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.widget.CustomHorizontalScrollview;
import com.hyphenate.easemob.im.officeautomation.widget.SimpleDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.andy.qpopuwindow.QPopuWindow;

/**
 * Created by qby on 2018/06/08 09:30.
 * 通讯录组织架构
 * the same class{@link CompanyStructureActivity}
 */
public class OrgStructureActivity extends BaseActivity {
    private static final String TAG = "OrgStructureActivity";
    private static final int REQUEST_CODE_UPDATE = 1;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CustomHorizontalScrollview mCustomHsv;
    private RecyclerView mRecyclerView;
    private int orgId = -1;
    private int companyId = -1;
    //标记请求是否执行完毕
//    private boolean isDepartment, isDepartmentUser;
    private List<DepartmentBean> departmentIndexList = new ArrayList<>();
    private List<MPOrgEntity> departmentList = new ArrayList<>();
    private List<MPUserEntity> departmentUserList = new ArrayList<>();
    //分页展示当前页码
    private int page = 0;
    private int size = 1000;
    //是否是最后一页
    private boolean isLastPage;
    private int rawX, rawY;
    //最后显示的条目
    private int lastVisibleItem;
    //列表适配器
    private DepartmentRefreshFooterAdapter refreshAdapter;
    //索引适配器
    private CustomListAdapter customListAdapter;

    private boolean isPick;
    //曾经已经选中的
    private List<Integer> pickedList;
    //当前要选中的
    private ArrayList<Integer> pickList = new ArrayList<>();
    private TextView submit;
    private MPGroupEntity groupEntity;
    private boolean isCard;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_org_structure);

        companyId = getIntent().getIntExtra("companyId", -1);

        isPick = getIntent().getBooleanExtra("isPick", false);
        isCard = getIntent().getBooleanExtra("isCard", false);

        if (isCard) isPick = false;
        pickedList = getIntent().getIntegerArrayListExtra("pickedList");
        groupEntity = getIntent().getParcelableExtra("groupEntity");
        List<Integer> list = getIntent().getIntegerArrayListExtra("pickList");
        if (list != null && list.size() > 0) {
            pickList.addAll(list);
        }

        initViews();
        initListeners();
        initData();
    }

    private void initViews() {
        mSwipeRefreshLayout = findViewById(R.id.srl);
        mCustomHsv = findViewById(R.id.custom_hsv);
        mRecyclerView = findViewById(R.id.rv);
        submit = findViewById(R.id.tv_submit);
        if (isPick) submit.setVisibility(View.VISIBLE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupEntity == null) {
                    Intent intent = new Intent();
                    intent.putExtra("pickList", pickList);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    addMembersToGroup(pickList, groupEntity);
                }
            }
        });
        submit.setText(String.format(getResources().getString(R.string.select_someone), pickList.size()));
    }

    private void initListeners() {
        $(R.id.iv_back).setOnClickListener(v -> onBackPressed());
        //下拉刷新
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(this::doRefreshListData);
        //通讯录部门、员工列表
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(activity));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //不是正在刷新、列表滑动为空闲状态、最下面显示的为最后一条、当前页不是最后一页
                if (!mSwipeRefreshLayout.isRefreshing() && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == refreshAdapter.getItemCount() && !isLastPage) {
                    refreshAdapter.changeMoreStatus(DepartmentRefreshFooterAdapter.LOADING_MORE);
//                    page++;
//                    isDepartmentUser = false;
                }
                refreshAdapter.changeMoreStatus(DepartmentRefreshFooterAdapter.LOADING_END);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //获取列表最下面一条的索引
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    //临时解决办法
    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("info", "meet a IOOBE in RecyclerView");
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (customListAdapter.getCount() > 1) {
            refreshCustomListAdapter(customListAdapter.getCurrentPosition() - 1);
        } else {
            Intent intent = new Intent();
            intent.putIntegerArrayListExtra("pickList", pickList);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private void initData() {
        Intent gIntent = getIntent();
        if (gIntent == null) return;
        String orgName = gIntent.getStringExtra("orgName");
        if (!TextUtils.isEmpty(orgName)) {
            ((TextView) findViewById(R.id.tv_title)).setText(orgName);
        }
        boolean isMyOrg = gIntent.getBooleanExtra("myOrg", false);
        DepartmentBean departmentBean;
        if (isMyOrg) {
            orgId = gIntent.getIntExtra("orgId", -1);
            departmentBean = new DepartmentBean(orgName != null ? orgName : "", orgId);
        } else {
            departmentBean = new DepartmentBean(getString(R.string.company), orgId);
        }
        departmentIndexList.add(departmentBean);

        //通讯录导航索引
        customListAdapter = new CustomListAdapter(this, R.layout.item_contacts_list_title_custom, departmentIndexList, position -> {
            //如果点击的不是当前部门
            if (position != customListAdapter.getCurrentPosition()) {
                //刷新列表，展示选中索引数据
                refreshCustomListAdapter(position);
            }
        });
        mCustomHsv.setAdapter(customListAdapter);

        //下拉刷新、上拉加载
        refreshAdapter = new DepartmentRefreshFooterAdapter(isPick, isCard, activity, departmentList, departmentUserList, new DepartmentRefreshFooterAdapter.DepartmentItemCallback() {
            @Override
            public void onDepartmentLongClick(View view, int position) {
                doDepartmentLongClick(view, position);
            }

            @Override
            public void onDepartmentClick(int position) {
                doDepartmentClick(position);
            }

            @Override
            public void onUserClick(String easemobName) {
                doUserClick(easemobName);
            }

            @Override
            public void onUserPick(CheckBox checkBox, MPUserEntity entity) {
                if (isCard) {
                    Intent intent = new Intent();
                    intent.putExtra("card", entity);
                    setResult(3000, intent);
                    finish();
                } else {
                    if (checkBox.isEnabled()) {
                        if (checkBox.isChecked()) {
                            checkBox.setChecked(false);
                            if (pickList.contains(entity.getId()))
                                pickList.remove((Integer) entity.getId());
                        } else {
                            checkBox.setChecked(true);
                            if (!pickList.contains(entity.getId())) {
                                pickList.add(entity.getId());
                            }
                        }
                        submit.setText(String.format(getResources().getString(R.string.select_someone), pickList.size()));
                    }
                }

            }
        });
        mRecyclerView.setAdapter(refreshAdapter);

        getDepartmentsAndContacts();
    }

    private void doRefreshListData() {
//        page = 0;
        departmentList.clear();
        departmentUserList.clear();
        refreshAdapter.notifyDataSetChanged();
        getDepartmentsAndContacts();
    }

    private void doUserClick(String imUsername) {
        if (TextUtils.isEmpty(imUsername)) return;
//        startActivity(new Intent(this, PersonalCardActivity.class).putExtra("username", imUsername));
        startActivityForResult(new Intent(this, ContactDetailsActivity.class).putExtra("imUserId", imUsername), 200);
    }


    private void doDepartmentClick(int position) {
        //获取点击的部门信息
        MPOrgEntity entitiesBean = departmentList.get(position);
        orgId = entitiesBean.getId();
        //构建部门索引model,刷新部门索引
        DepartmentBean departmentBean1 = new DepartmentBean(entitiesBean.getName(), orgId);
        departmentIndexList.add(departmentBean1);
        customListAdapter.notifyDataSetChanged();
        mCustomHsv.fillViewWithAdapter(customListAdapter);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               mCustomHsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                           }
                       }, 100
        );
        //请求部门数据，刷新列表
//        page = 0;
        departmentList.clear();
        departmentUserList.clear();
        refreshAdapter.notifyDataSetChanged();
        EaseUI.getInstance().execute(this::getDepartmentsAndContacts);
    }

    private void doDepartmentLongClick(View view, int position) {
        QPopuWindow.getInstance(activity).builder
                .bindView(view, position)
                .setPopupItemList(new String[]{getString(R.string.create_department), getString(R.string.modify_department), getString(R.string.create_user)})
                .setPointers(rawX, rawY)
                .setDividerVisibility(true)
                .setOnPopuListItemClickListener(new QPopuWindow.OnPopuListItemClickListener() {
                    @Override
                    public void onPopuListItemClick(View view, int viewPosition, int position) {
                        MPLog.i(TAG, viewPosition + "位置：" + position);
                        MPOrgEntity entitiesBean = departmentList.get(viewPosition);
                        int id = entitiesBean.getId();
                        if (position == 0) {
                            //创建
                            startActivity(new Intent(OrgStructureActivity.this, UpdateDepartNameActivity.class).putExtra("orgId", id).putExtra("title", getString(R.string.create_department)));
                        } else if (position == 1) {
                            //修改
                            startActivityForResult(new Intent(OrgStructureActivity.this, UpdateDepartNameActivity.class).putExtra("orgId", id), REQUEST_CODE_UPDATE);
                        } else if (position == 2) {
                            //添加成员
                            startActivity(new Intent(OrgStructureActivity.this, CreateUserActivity.class).putExtra("orgId", id));
                        }
                    }
                })
                .show();
    }

    /**
     * 获取部门下信息
     */
    private void getDepartmentsAndContacts() {
        EaseUI.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                getDepartments();
                getUsers();
            }
        });
    }

    //获取用户列表
    private void getUsers() {
        getDepartmentUserFromServer(orgId);
    }


    private void getDepartmentUsersFromDB() {
        List<MPUserEntity> users = AppHelper.getInstance().getModel().getUsersByOrgId(orgId);
        departmentUserList.clear();
        if (users != null && !users.isEmpty()) {
            for (MPUserEntity entity : users) {
                entity.setOrgId(orgId);
                if (isPick && pickList.size() > 0 && pickList.contains(entity.getId())) {
                    entity.setPickStatus(1);
                }
                if (isPick && pickedList.size() > 0 && pickedList.contains(entity.getId())) {
                    entity.setPickStatus(2);
                }
                departmentUserList.add(entity);
            }
        }
        runOnUiThread(() -> {
            isLastPage = true;
            refreshAdapter.changeMoreStatus(DepartmentRefreshFooterAdapter.LOADING_END);
            refreshAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        });
    }

    private void getDepartmentUserFromServer(int orgId) {
        EMAPIManager.getInstance().getUsersByOrgId(companyId, orgId, page, size, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(final String value) {

                try {
                    JSONObject jsonObj = new JSONObject(value);
                    String status = jsonObj.optString("status");
                    int elements = jsonObj.optInt("numberOfElements");
                    isLastPage = jsonObj.optBoolean("last");
                    List<MPUserEntity> userList = MPUserEntity.create(jsonObj.optJSONArray("entities"));
                    boolean ret = AppHelper.getInstance().getModel().saveMPUserList(userList);
                    if (ret) {
                        departmentUserList.clear();
                        departmentUserList.addAll(userList);
                    }
                    for (MPUserEntity entity : userList) {
                        entity.setOrgId(orgId);
                        if (isPick && pickList.size() > 0 && pickList.contains(entity.getId())) {
                            entity.setPickStatus(1);
                        }
                        if (isPick && pickedList.size() > 0 && pickedList.contains(entity.getId())) {
                            entity.setPickStatus(2);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    MPLog.e(TAG, "" + MPLog.getStackTraceString(e));
                }


                runOnUiThread(() -> {
                    isLastPage = true;
                    refreshAdapter.changeMoreStatus(DepartmentRefreshFooterAdapter.LOADING_END);
                    refreshAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                getDepartmentUsersFromDB();
                MPLog.e(TAG, "error: " + error + ", errorMsg:" + errorMsg);
            }
        });
    }

    private void getDepartmentsFromDB() {
        List<MPOrgEntity> orgsList = AppHelper.getInstance().getModel().getOrgsListByParent(orgId);
        departmentList.clear();
        if (orgsList != null && !orgsList.isEmpty()) {
            departmentList.addAll(orgsList);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getDepartmentsFromServer() {
        EMAPIManager.getInstance().getOrgInfoForSub(companyId, orgId, page, size, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(final String value) {
                try {
                    JSONObject jsonObj = new JSONObject(value);
                    JSONArray jsonEntities = jsonObj.optJSONArray("entities");
                    if (jsonEntities != null) {
                        List<MPOrgEntity> orgEntities = MPOrgEntity.create(jsonEntities);
                        boolean ret = AppHelper.getInstance().getModel().saveOrgsListByParentOrgId(orgEntities);

                        if (ret) {
                            departmentList.clear();
                            if (orgEntities != null && !orgEntities.isEmpty()) {
                                departmentList.addAll(orgEntities);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MPLog.e(TAG, "e:" + MPLog.getStackTraceString(e));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                getDepartmentsFromDB();
                MPLog.e(TAG, "error:" + error + ", errorMsg:" + errorMsg);
            }
        });
    }


    //获取部门列表
    private void getDepartments() {
        getDepartmentsFromServer();
    }
//
//
//    //子部门信息
//    private void getDepartmentListRequest() {
//        EMAPIManager.getInstance().getOrgInfo(BaseRequest.getTenantId(), orgId, new EMDataCallBack<String>() {
//            @Override
//            public void onSuccess(final String value) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        isDepartment = true;
//                        if (isDepartmentUser) {
//                            mSwipeRefreshLayout.setRefreshing(false);
//                        }
//                        OrganizationBean organizationBean = null;
//                        try {
//                            organizationBean = new Gson().fromJson(value, OrganizationBean.class);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        if (organizationBean != null) {
//                            String status = organizationBean.getStatus();
//                            if ("OK".equalsIgnoreCase(status)) {
//                                int numberOfElements = organizationBean.getNumberOfElements();
//                                departmentList.clear();
//                                if (numberOfElements > 0) {
//                                    departmentList.addAll(organizationBean.getEntities());
//                                }
//                                refreshAdapter.notifyDataSetChanged();
//                            } else {
//                                toastInvalidResponse(TAG, "status = " + status);
//                            }
//                        } else {
//                            toastInvalidResponse(TAG, "organizationBean = null");
//                        }
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
//                        isDepartment = true;
//                        if (isDepartmentUser) {
//                            mSwipeRefreshLayout.setRefreshing(false);
//                        }
//                    }
//                });
//
//            }
//        });
//    }
//
//    //部门下员工信息
//    private void getDepartmentUserRequest() {
//        EMAPIManager.getInstance().getUsersByOrgId(BaseRequest.getTenantId(), orgId, page, Constant.PAGE_SIZE, new EMDataCallBack<String>() {
//            @Override
//            public void onSuccess(final String value) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        isDepartmentUser = true;
//                        if (isDepartment) {
//                            mSwipeRefreshLayout.setRefreshing(false);
//                        }
//                        DepartmentUserBean departmentUserBean = null;
//                        try {
//                            departmentUserBean = new Gson().fromJson(value, DepartmentUserBean.class);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        if (departmentUserBean != null) {
//                            String status = departmentUserBean.getStatus();
//                            if ("OK".equalsIgnoreCase(status)) {
//                                int elements = departmentUserBean.getNumberOfElements();
//                                isLastPage = departmentUserBean.getLast();
//                                if (page == 0) {
//                                    departmentUserList.clear();
//                                }
//                                if (elements > 0) {
//                                    List<MPUserEntity> entities = departmentUserBean.getEntities();
//                                    if (entities != null) {
//                                        departmentUserList.addAll(entities);
//                                        for (int i = 0; i < entities.size(); i++) {
//                                            MPUserEntity entitiesBean = entities.get(i);
//                                            AppHelper.getInstance().getModel().saveUserExtInfo(entitiesBean.getEasemobName(), entitiesBean.getId(), entitiesBean.getRealName(), entitiesBean.getImage());
//                                        }
//                                    } else {
//                                        toastInvalidResponse(TAG, "entities = null");
//                                    }
//                                }
//
//                                if (isLastPage)
//                                    refreshAdapter.changeMoreStatus(DepartmentRefreshFooterAdapter.LOADING_END);
//                                else
//                                    refreshAdapter.changeMoreStatus(DepartmentRefreshFooterAdapter.LOADING_MORE);
//
//                            } else {
//                                toastInvalidResponse(TAG, "status =" + status);
//                            }
//                        } else {
//                            toastInvalidResponse(TAG, "departmentUserBean = null");
//                        }
//
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
//                        isDepartmentUser = true;
//                        if (isDepartment) {
//                            mSwipeRefreshLayout.setRefreshing(false);
//                        }
//                    }
//                });
//            }
//        });
//    }


    //点击索引 或 点击返回 切换数据展示
    private void refreshCustomListAdapter(Integer position) {
        //获取点击的部门ID
        DepartmentBean departmentBean1 = departmentIndexList.get(position);
        orgId = departmentBean1.getId();
        //截取索引列表
        List<DepartmentBean> departmentBeans = departmentIndexList.subList(0, position + 1);
        ArrayList<DepartmentBean> copyDepartmentIndexList = new ArrayList<>(departmentBeans);
        departmentIndexList.clear();
        departmentIndexList.addAll(copyDepartmentIndexList);
        //刷新索引列表
        customListAdapter.notifyDataSetChanged();
        mCustomHsv.fillViewWithAdapter(customListAdapter);

        //点击部门索引，请求数据，刷新列表
//        page = 0;
        departmentList.clear();
        departmentUserList.clear();
        refreshAdapter.notifyDataSetChanged();
        EaseUI.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                getDepartmentsAndContacts();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && customListAdapter.getCurrentPosition() > 0) {
            refreshCustomListAdapter(customListAdapter.getCurrentPosition() - 1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE) {
            if (resultCode == UpdateDepartNameActivity.RESULT_CODE_UPDATE) {
                getDepartments();
            }
        } else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {

            MPUserEntity user = data.getParcelableExtra("user");
            if (user != null) {

                for (MPUserEntity mpUserEntity : departmentUserList) {
                    if (mpUserEntity.getImUserId().equals(user.getImUserId())) {
                        mpUserEntity.setAlias(user.getAlias());
                        break;
                    }
                }
                refreshAdapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        rawX = (int) ev.getRawX();
        rawY = (int) ev.getRawY();
        return super.dispatchTouchEvent(ev);
    }

}
