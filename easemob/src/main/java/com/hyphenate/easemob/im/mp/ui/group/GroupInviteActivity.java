package com.hyphenate.easemob.im.mp.ui.group;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hyphenate.easemob.easeui.widget.SimpleDividerItemDecoration;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;
import com.hyphenate.util.DensityUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class GroupInviteActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
//    private List<RecyclerViewData> mDatas = new ArrayList<>();

    private List<InviteEntity> inviteEntities = new ArrayList<>();
    private int page = 0;
    private int size = 20;

    private int lastVisibleItem;
    private boolean isLastPage;

    private GroupInviteAdapterNew adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_invite);

        initViews();
        initDatas();

        getGroupAllNotify();
    }



    private void initDatas() {
        adapter = new GroupInviteAdapterNew(inviteEntities);
        adapter.bindToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener()  {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                InviteEntity entty = (InviteEntity) adapter.getItem(position);
                if (entty == null) {
                    return;
                }
                JSONObject jsonBody = new JSONObject();
                if (view.getId() == R.id.btn_accept) {
                    try {
                        jsonBody.put("userId", entty.getUserId());
                        jsonBody.put("approve", 1); // 1 同意 2 拒绝
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    EMAPIManager.getInstance().approveMemberApply(entty.getChatGroupId(), false, jsonBody.toString(), new EMDataCallBack<String>() {
                        @Override
                        public void onSuccess(String value) {
                            try {
                                JSONObject result = new JSONObject(value);
                                if ("OK".equals(result.getString("status"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyItemRemoved(position);
                                            getGroupAllNotify();
                                            ToastUtils.showShort(getResources().getString(R.string.Has_agreed_to));
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(int error, String errorMsg) {
                            MPLog.e("GroupInvite", "approveMemberApply:" + errorMsg);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showShort(getResources().getString(R.string.request_failed_check_network));
                                }
                            });

                        }
                    });
                } else if (view.getId() == R.id.btn_refuse) {
                    try {
                        jsonBody.put("userId", entty.getUserId());
                        jsonBody.put("approve", 2); // 1 同意 2 拒绝
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    EMAPIManager.getInstance().approveMemberApply(entty.getChatGroupId(), false, jsonBody.toString(), new EMDataCallBack<String>() {
                        @Override
                        public void onSuccess(String value) {
                            try {
                                JSONObject result = new JSONObject(value);
                                if ("OK".equals(result.getString("status"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyItemRemoved(position);
                                            getGroupAllNotify();
                                            ToastUtils.showShort(getResources().getString(R.string.Has_refused_to));
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(int error, String errorMsg) {
                            MPLog.e("GroupInvite", "approveMemberApply:" + errorMsg);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showShort(getResources().getString(R.string.request_failed_check_network));
                                }
                            });

                        }
                    });
                }

            }
        });
    }

    private void initViews() {
        $(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = $(R.id.recycler_view);
        swipeRefreshLayout = $(R.id.swipe_layout);
        TextView tvTitle = $(R.id.tv_title);
        tvTitle.setText("群组申请");


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        SimpleDividerItemDecoration itemDecoration = new SimpleDividerItemDecoration(this, ContextCompat.getDrawable(this, R.color.color_grey_light), DensityUtil.dip2px(this, 0.5f));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //不是正在刷新、列表滑动为空闲状态、最下面显示的为最后一条、当前页不是最后一页
                if (!swipeRefreshLayout.isRefreshing() && newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == adapter.getItemCount() && !isLastPage) {
//                    adapter.changeMoreStatus(NewFriendsAdapter.LOADING_MORE);
                    page++;
                    getGroupAllNotify();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //获取列表最下面一条的索引
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                inviteEntities.clear();
                getGroupAllNotify();
            }
        });
    }

    private void getGroupAllNotify() {
        String params = "";
        EMAPIManager.getInstance().getGroupAllNotify(params, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        try{
                            JSONObject jsonObj = new JSONObject(value);
                            inviteEntities.clear();
                            inviteEntities.addAll(getDatasFromJson(jsonObj));
                            adapter.notifyDataSetChanged();
                        }catch (Exception e) {
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
                        ToastUtils.showShort(R.string.request_failed_check_network);
                    }
                });
            }
        });


    }

//    private List<RecyclerViewData> getDatasFromJson(JSONObject jsonObj) {
//        List<RecyclerViewData> list = new ArrayList<>();
//        JSONArray jsonEntities = jsonObj.optJSONArray("entities");
//        if (jsonEntities != null && jsonEntities.length() > 0) {
//            for (int i = 0; i < jsonEntities.length(); i++) {
//                JSONObject jsonEntity = jsonEntities.optJSONObject(i);
//                if (jsonEntity != null) {
//                    String groupName = jsonEntity.optString("name");
//                    int groupId = jsonEntity.optInt("id");
//                    JSONArray jsonUserList = jsonEntity.optJSONArray("userList");
//                    if (jsonUserList != null && jsonUserList.length() > 0) {
//                        List<InviteEntity> inviteEntities = new ArrayList<>();
//                        for (int j = 0; j < jsonUserList.length(); j++) {
//                            JSONObject jsonUser = jsonUserList.optJSONObject(j);
//                            InviteEntity entty = InviteEntity.fromJson(jsonUser);
//                            entty.setChatGroupId(groupId);
//                            if (entty != null) {
//                                inviteEntities.add(entty);
//                            }
//                        }
//                        list.add(new RecyclerViewData(groupName, inviteEntities, true));
//                    }
//
//                }
//
//            }
//        }
//
//
//        return list;
//    }

    private List<InviteEntity> getDatasFromJson(JSONObject jsonObj) {
        List<InviteEntity> inviteEntities = new ArrayList<>();
        JSONArray jsonEntities = jsonObj.optJSONArray("entities");
        if (jsonEntities != null && jsonEntities.length() > 0) {
            for (int i = 0; i < jsonEntities.length(); i++) {
                JSONObject jsonEntity = jsonEntities.optJSONObject(i);
                if (jsonEntity != null) {
                    String groupName = jsonEntity.optString("name");
                    int groupId = jsonEntity.optInt("id");
                    JSONArray jsonUserList = jsonEntity.optJSONArray("userList");
                    if (jsonUserList != null && jsonUserList.length() > 0) {
                        for (int j = 0; j < jsonUserList.length(); j++) {
                            JSONObject jsonUser = jsonUserList.optJSONObject(j);
                            InviteEntity entty = InviteEntity.fromJson(jsonUser);
                            if (entty != null) {
                                entty.setChatGroupId(groupId);
                                entty.setChatGroupName(groupName);
                                inviteEntities.add(entty);
                            }
                        }
                    }
                }
            }
        }
        return inviteEntities;
    }
















}
