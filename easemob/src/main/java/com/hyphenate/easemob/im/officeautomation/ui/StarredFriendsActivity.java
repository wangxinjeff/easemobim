package com.hyphenate.easemob.im.officeautomation.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.eventbus.Subscribe;
import com.hyphenate.eventbus.ThreadMode;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.events.EventOnLineOffLineNotify;
import com.hyphenate.easemob.imlibs.mp.events.EventOnLineOffLineQuery;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.adapter.StarredRefreshFooterAdapter;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.widget.SimpleDividerItemDecoration;
import com.hyphenate.util.EMLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qby on 2018/06/11 21:30.
 * 通讯录星标朋友
 */
public class StarredFriendsActivity extends BaseActivity {
    private static final String TAG = "StarredFriendsActivity";
    private static final int REQUEST_CODE_PERSONAL_INFO = 1;
    private ImageView ivBack;
    private SwipeRefreshLayout srl;
    private RecyclerView rv;
    List<MPUserEntity> mpUserEntityList = new ArrayList<>();
    //分页展示当前页码
    private int page = 0;
    //是否是最后一页
    private boolean isLastPage;
    //最后显示的条目
    private int lastVisibleItem;
    //列表适配器
    private StarredRefreshFooterAdapter refreshAdapter;
    //获取部门员工请求

    private boolean isPick;
    private List<Integer> pickedList;
    private ArrayList<Integer> pickList = new ArrayList<>();
    private TextView submit;
    private MPGroupEntity groupEntity;
    private boolean isCard;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_starred_friends);

        Intent gIntent = getIntent();
        if (gIntent == null) {
            return;
        }
        isPick = gIntent.getBooleanExtra("isPick", false);
        isCard = gIntent.getBooleanExtra("isCard", false);
        if (isCard) isPick = false;
        pickedList = gIntent.getIntegerArrayListExtra("pickedList");
        groupEntity = gIntent.getParcelableExtra("groupEntity");
        List<Integer> list = gIntent.getIntegerArrayListExtra("pickList");
        if (list != null && list.size() > 0) {
            pickList.addAll(list);
        }

        initViews();
        initListeners();
        initData();
    }

    private void initViews() {
        ivBack = $(R.id.iv_back);
        srl = findViewById(R.id.srl);
        rv = findViewById(R.id.rv);

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putIntegerArrayListExtra("pickList", pickList);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void initListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //下拉刷新
        srl.setRefreshing(true);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                mpUserEntityList.clear();
                refreshAdapter.notifyDataSetChanged();
                getStarredFriends();
            }
        });
        //通讯录部门、员工列表
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new SimpleDividerItemDecoration(activity));
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //不是正在刷新、列表滑动为空闲状态、最下面显示的为最后一条、当前页不是最后一页
                if (!srl.isRefreshing() && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == refreshAdapter.getItemCount() && !isLastPage) {
                    refreshAdapter.changeMoreStatus(StarredRefreshFooterAdapter.LOADING_MORE);
                    page++;
                    getStarredFriends();
                }
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


    private void initData() {
        //下拉刷新、上拉加载
        refreshAdapter = new StarredRefreshFooterAdapter(isPick, isCard, activity, mpUserEntityList, new StarredRefreshFooterAdapter.StarredItemCallback() {
            @Override
            public void onUserClick(int position) {
                //获取点击的部门信息
                MPUserEntity entitiesBean = mpUserEntityList.get(position);
                startActivityForResult(new Intent(StarredFriendsActivity.this, ContactDetailsActivity.class)
                        .putExtra("userId", entitiesBean.getId())
                        .putExtra("isFromStarred", true), REQUEST_CODE_PERSONAL_INFO);
            }

            @Override
            public void onUserPick(CheckBox checkBox, int position) {
                MPUserEntity entitiesBean = mpUserEntityList.get(position);
                if (isCard) {
                    Intent intent = new Intent();
                    intent.putExtra("card", entitiesBean);
                    setResult(3000, intent);
                    finish();
                } else {
                    if (checkBox.isChecked()) {
                        checkBox.setChecked(false);
                        pickList.remove(Integer.valueOf(entitiesBean.getId()));
                    } else {
                        checkBox.setChecked(true);
                        if (!pickList.contains(entitiesBean.getId())) {
                            pickList.add(Integer.valueOf(entitiesBean.getId()));
                        }
                    }
                    submit.setText(String.format(getResources().getString(R.string.select_someone), pickList.size()));
                }
            }
        });
        rv.setAdapter(refreshAdapter);

        getStarredFriends();
    }

    /**
     * 获取部门下信息
     */
    private void getStarredFriends() {
        EMAPIManager.getInstance().getStaredFriends(page, Constant.PAGE_SIZE, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(final String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        srl.setRefreshing(false);
//                        MPLog.d(TAG, value);
                        JSONObject result = null;
                        try {
                            result = new JSONObject(value);
                            String status = result.optString("status");
                            if ("OK".equalsIgnoreCase(status)) {
                                int elements = result.optInt("numberOfElements");
                                isLastPage = result.optBoolean("last");
                                if (page == 0) {
                                    mpUserEntityList.clear();
                                }
                                mpUserEntityList.addAll(MPUserEntity.create(result.getJSONArray("entities")));
                                asyncFetchUserStatus(mpUserEntityList);
                                if (elements > 0) {
                                    for (MPUserEntity mpUserEntity : mpUserEntityList) {
                                        AppHelper.getInstance().getModel().saveUserInfo(mpUserEntity);
                                        if (isPick && pickList.size() > 0 && pickList.contains(mpUserEntity.getId())) {
                                            mpUserEntity.setPickStatus(1);
                                        }
                                        if (isPick && pickedList.size() > 0 && pickedList.contains(mpUserEntity.getId())) {
                                            mpUserEntity.setPickStatus(2);
                                        }
                                    }
                                }

                                if (isLastPage) {
                                    refreshAdapter.changeMoreStatus(StarredRefreshFooterAdapter.LOADING_END);
                                } else {
                                    refreshAdapter.changeMoreStatus(StarredRefreshFooterAdapter.LOADING_MORE);
                                }

                            } else {
                                toastInvalidResponse(TAG, "status = " + status);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });

            }

            @Override
            public void onError(int error, String errorMsg) {
                EMLog.e(TAG, "error:" + errorMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        srl.setRefreshing(false);
                        Toast.makeText(getApplicationContext(), R.string.request_failed_check_network, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PERSONAL_INFO) {
            page = 0;
            mpUserEntityList.clear();
            refreshAdapter.notifyDataSetChanged();
            getStarredFriends();
        }
    }

    public void asyncFetchUserStatus(List<MPUserEntity> mpUserEntityList){
        ArrayList<String> imUserIds = new ArrayList<>();
        for (MPUserEntity item : mpUserEntityList) {
            imUserIds.add(item.getImUserId());
        }
        EMClient.getInstance().chatManager().getUserStatusWithUserIds(imUserIds, new EMCallBack() {
            @Override
            public void onSuccess() {
                EMLog.d(TAG, "getUserStatusWithUserIds-success");
            }

            @Override
            public void onError(int i, String s) {
                EMLog.e(TAG, "getUserStatusWithUserIds-onError:" + s + ",userIds:" + imUserIds.toString());
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventContactQuery(EventOnLineOffLineQuery query){
        MPLog.e(TAG, "onEventContactQuery");
        if (refreshAdapter != null) {
            refreshAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventOnlineStatusNotity(EventOnLineOffLineNotify notify) {
        MPLog.e(TAG, "onEventOnlineStatusNotity");
        if (refreshAdapter != null) {
            refreshAdapter.notifyDataSetChanged();
        }
    }

}
