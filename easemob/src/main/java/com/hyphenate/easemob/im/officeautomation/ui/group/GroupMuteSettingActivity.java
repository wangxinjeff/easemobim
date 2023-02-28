package com.hyphenate.easemob.im.officeautomation.ui.group;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.MPGroupMemberEntity;
import com.hyphenate.easemob.easeui.widget.listview.check.KylinCheckListView;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupMuteChanged;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.im.mp.widget.ClearEditText;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.widget.SimpleDividerItemDecoration;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.zhouzhuo.zzletterssidebar.utils.CharacterParser;

public class GroupMuteSettingActivity extends BaseActivity {

    private static final String TAG = "GroupMuteSettingActivity";

    private ClearEditText mClearEditText;
    private KylinCheckListView checkListView;
    private ImageView ivBack;
    private TextView tvRight;
    private TextView tvTitle;
    private CheckBox cbAll;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog loadingPopup;
    private List<MPGroupMemberEntity> memberEntities = Collections.synchronizedList(new ArrayList<>());
    private List<MPGroupMemberEntity> copyMemberEntities = Collections.synchronizedList(new ArrayList<>());

    private int groupId;
    private boolean isRegion;

    private DialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_mute_setting);
        initViews();
        initListeners();
        initDatas();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvRight = findViewById(R.id.tv_right);
        tvTitle = findViewById(R.id.tv_title);
        checkListView = findViewById(R.id.cv_content);
        mClearEditText = findViewById(R.id.filter_edit);
        cbAll = findViewById(R.id.cb_all);

        swipeRefreshLayout = findViewById(R.id.swipe_layout);

        // 设置Item布局
        checkListView.setItemClass(GroupMemberViewModel.class);
        // 设置多选
        checkListView.setShowType(KylinCheckListView.MULTISELECT);
        checkListView.setShowLocation(KylinCheckListView.CHECKRIGHT);
        // 创建CheckList
        checkListView.create();

        checkListView.getRecyclerView().addItemDecoration(new SimpleDividerItemDecoration(activity));

        loadingPopup = new ProgressDialog(this);
        loadingPopup.setTitle("正在加载中...");

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
                List<Integer> integerList = checkListView.multResults();
                if (integerList == null || integerList.isEmpty()) {
                    Toast.makeText(GroupMuteSettingActivity.this, "请选择", Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONArray jsonArr = new JSONArray();
                for (Integer item : integerList) {
                    MPGroupMemberEntity entity = (MPGroupMemberEntity) checkListView.getData(item);
                    if (entity != null && entity.enabled) {
                        jsonArr.put(entity.getUserId());
                    }
                }

                muteGroupMembers(jsonArr, -1);

//                ArrayList<String> dialogItems = new ArrayList<>();
//                dialogItems.add("10分钟");
//                dialogItems.add("1小时");
//                dialogItems.add("1天");
//                dialogItems.add("7天");
//                dialogItems.add("30天");
//                dialogFragment = new CircleDialog.Builder()
//                        .setItems(dialogItems, (parent, view, position, id) -> {
//                            if (dialogFragment != null) {
//                                dialogFragment.dismissAllowingStateLoss();
//                                dialogFragment = null;
//                            }
//                            long time = 0;
//                            if (position == 0) {
//                                time = Constant.MUTE_DURATION_TEN_MINIMUTE;
//                            } else if (position == 1) {
//                                time = Constant.MUTE_DURATION_ONE_HOUR;
//                            } else if (position == 2) {
//                                time = Constant.MUTE_DURATION_ONE_DAY;
//                            } else if (position == 3) {
//                                time = Constant.MUTE_DURATION_SEVEN_DAY;
//                            } else if (position == 4) {
//                                time = Constant.MUTE_DURATION_ONE_MOUNTH;
//                            }
//                            muteGroupMembers(jsonArr, time);
//                            return false;
//                        }).setNegative(getString(R.string.cancel), null).show(getSupportFragmentManager());

            }
        });

        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkListView.multAllCheck(true);
                    cbAll.setText("取消");
                } else {
                    checkListView.multAllCheck(false);
                    cbAll.setText("全选");
                }
            }
        });
    }

    private void initDatas() {
        Intent gIntent = getIntent();
        if (gIntent == null) {
            return;
        }
        groupId = gIntent.getIntExtra("groupId", -1);
        isRegion = gIntent.getBooleanExtra("isRegion", false);

        checkListView.setDataToView(memberEntities);

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoading();
    }

    private void dismissLoading() {
        if (loadingPopup != null && loadingPopup.isShowing()) {
            loadingPopup.dismiss();
        }

        if (dialogFragment != null) {
            dialogFragment.dismissAllowingStateLoss();
            dialogFragment = null;
        }
    }


    private void updateGroup() {
        if (groupId <= 0) {
            return;
        }
        loadingPopup.show();
        StringBuilder sb = new StringBuilder();
        //page=%2$s&size=%3$s&isRegion=%4$s
        sb.append("page=0&size=").append(200);
        if (isRegion) {
            sb.append("&isRegion=1");
        }
        EMAPIManager.getInstance().getMemberList(groupId, sb.toString(),
                new EMDataCallBack<String>() {
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
            if ("owner".equalsIgnoreCase(item.getType())) {
                continue;
            }
            EaseUser userEntity = UserProvider.getInstance().getEaseUserById(item.getUserId());
            if (userEntity != null) {
                item.setNick(userEntity.getNickname());
                item.setAvatar(userEntity.getAvatar());
            } else {
                item.setNick(String.valueOf(item.getUserId()));
            }
            if (item.isMute()) {
                item.enabled = false;
                item.isCheck = true;
            }
            list.add(item);
        }
        return list;
    }

    private void muteGroupMembers(JSONArray jsonArr, long muteTime) {
        if (groupId <= 0) {
            return;
        }
        loadingPopup.show();

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("userIdList", jsonArr);
//            jsonObj.put("muteDuration", muteTime);
            if (isRegion) {
                jsonObj.put("isRegion", 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        EMAPIManager.getInstance().muteGroupMembers(groupId, jsonObj.toString(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    if (isFinishing()) {
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoading();
                            MPEventBus.getDefault().post(new EventGroupMuteChanged());
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                } catch (Exception e) {
                    MPLog.e(TAG, "getMemberList json parse error");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoading();
                            swipeRefreshLayout.setRefreshing(false);
                            ToastUtils.showShort("禁言失败！");
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

}
