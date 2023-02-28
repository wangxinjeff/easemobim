package com.hyphenate.easemob.im.officeautomation.ui.group;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.im.gray.GrayScaleConfig;
import com.hyphenate.easemob.imlibs.mp.prefs.PrefsUtil;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;
import com.hyphenate.easemob.im.officeautomation.ui.CompanyStructureActivity;
import com.hyphenate.easemob.im.officeautomation.ui.CreateGroupActivity;
import com.hyphenate.easemob.im.officeautomation.ui.MyFriendsActivity;
import com.hyphenate.easemob.im.officeautomation.ui.OrgStructureActivity;
import com.hyphenate.easemob.im.officeautomation.ui.StarredFriendsActivity;
import com.hyphenate.easemob.im.officeautomation.ui.group.GroupSearchMemberActivity;
import com.hyphenate.easemob.im.officeautomation.widget.CompayOrgItemView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupAddMemberActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvRight;
    private TextView tvTitle;

    private RelativeLayout mRlMyFriends;
    private RelativeLayout mRlStarFriends;
    private LinearLayout llCompanyListView;

    //已存在的群成员
    private ArrayList<Integer> pickedList;
    private ArrayList<Integer> pickList;

    protected RelativeLayout mRlSearch;

    private MPGroupEntity groupEntity;

    private boolean isCreate;
    private boolean isCard;
    private boolean isSchedule;

    private List<Integer> pickedUidList;
    private Map<Integer, Integer> pickedOrgIdMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_addmember);

        initViews();
        initListeners();

    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvRight = findViewById(R.id.tv_right);
        tvTitle = findViewById(R.id.tv_title);

        mRlMyFriends = findViewById(R.id.rl_my_friends);
        mRlMyFriends.setVisibility(GrayScaleConfig.useContact ? View.VISIBLE : View.GONE);
        mRlStarFriends = findViewById(R.id.rl_star_friends);
        llCompanyListView = findViewById(R.id.ll_company_list);

        mRlSearch = findViewById(R.id.rl_search);

        pickedList = new ArrayList<>();
        isCreate = getIntent().getBooleanExtra("isCreate", false);
        isCard = getIntent().getBooleanExtra("isCard", false);
        isSchedule = getIntent().getBooleanExtra("isSchedule", false);
        if (isCard) {
            tvRight.setVisibility(View.GONE);
            tvTitle.setText(getResources().getString(R.string.select_contacts));
        }
        Intent gIntent = getIntent();
        if (gIntent == null) {
            return;
        }
        List<Integer> selectedIds = gIntent.getIntegerArrayListExtra("pickedList");
        if (selectedIds != null && !selectedIds.isEmpty()) {
            pickedList.addAll(selectedIds);
        }
        if (!isCreate && !isCard && !isSchedule) {
            groupEntity = gIntent.getParcelableExtra("groupEntity");
        } else {
            pickList = getIntent().getIntegerArrayListExtra("pickList");
            pickedUidList = getIntent().getIntegerArrayListExtra("pickedUidList");
            pickedOrgIdMap = (Map<Integer, Integer>) getIntent().getSerializableExtra("pickedOrgIdMap");
            if (pickedUidList != null && pickedUidList.size() > 0) {
                pickList = (ArrayList<Integer>) pickedUidList;
                tvRight.setText(String.format(getResources().getString(R.string.select_someone), pickedUidList.size()));
            }
        }
        addCompanyListViewContent();
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
                if (isCreate) {
                    synchronized (this) {
                        if (pickedUidList == null) pickedUidList = new ArrayList<>();
                        if (pickedUidList.size() >= EaseConstant.MAX_GROUP_COUNT) {
                            Toast.makeText(GroupAddMemberActivity.this, getString(R.string.exceceding_maximum_group, EaseConstant.MAX_GROUP_COUNT), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ArrayList<String> imageUrls = new ArrayList<>();
                        Intent intent = new Intent(GroupAddMemberActivity.this, CreateGroupActivity.class);
                        intent.putIntegerArrayListExtra("pickedUidList", (ArrayList<Integer>) pickedUidList);
                        if (pickedOrgIdMap == null) pickedOrgIdMap = new HashMap<>();
                        intent.putExtra("pickedOrgIdMap", (Serializable) pickedOrgIdMap);
                        intent.putExtra("imageUrls", imageUrls);
                        startActivity(intent);
                        finish();
                    }
                } else if (isSchedule) {
                    Intent intent = new Intent();
                    intent.putExtra("pickedOrgIdMap", (Serializable) pickedOrgIdMap);
                    intent.putIntegerArrayListExtra("pickedUidList", (ArrayList<Integer>) pickedUidList);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    if (pickList != null && pickList.size() > 0) {
                        addMembersToGroup(pickList, groupEntity);
                    } else {
                        Toast.makeText(GroupAddMemberActivity.this, getResources().getString(R.string.no_members), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        mRlMyFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(activity, MyFriendsActivity.class)
                        .putExtra("isPick", true)
                        .putExtra("pickedList", pickedList)
                        .putExtra("pickList", pickList)
                        .putExtra("isCard", isCard)
                        .putExtra("groupEntity", groupEntity), 1000);
            }
        });
        mRlStarFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(activity, StarredFriendsActivity.class)
                        .putExtra("isStarred", true)
                        .putExtra("isPick", true)
                        .putExtra("pickedList", pickedList)
                        .putExtra("groupEntity", groupEntity)
                        .putExtra("isCard", isCard)
                        .putExtra("pickList", pickList), 1000);
            }
        });

        mRlSearch.setOnClickListener(view -> {
            startActivityForResult(new Intent(GroupAddMemberActivity.this, GroupSearchMemberActivity.class)
                    .putExtra("pickList", pickList)
                    .putExtra("isCard", isCard)
                    .putExtra("pickedList", pickedList), 1000);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && data != null) {
            pickList = data.getIntegerArrayListExtra("pickList");
            if (resultCode == 1000) {
                Intent intent = new Intent();
                intent.putExtra("pickList", pickList);
                setResult(RESULT_OK, intent);
                finish();
            } else if (resultCode == RESULT_OK) {
                tvRight.setText(String.format(getResources().getString(R.string.select_someone), pickList.size()));
                pickedUidList = new ArrayList<>();
                for (Integer id : pickList) {
                    if (!pickedUidList.contains(id)) {
                        pickedUidList.add(id);
                    }
                }
            } else if (resultCode == 3000) {
                setResult(RESULT_OK, data);
                finish();
            }
        }

        if (requestCode == 1001 && data != null) {
            pickedUidList = data.getIntegerArrayListExtra("pickedUidList");
            if (pickList == null) pickList = new ArrayList<>();
            for (Integer id : pickedUidList) {
                if (!pickList.contains(id)) {
                    pickList.add(id);
                }
            }
            pickedOrgIdMap = (Map<Integer, Integer>) data.getSerializableExtra("pickedOrgIdMap");
            tvRight.setText(String.format(getResources().getString(R.string.select_someone), pickedUidList.size()));
        }

    }


    private void addCompanyListViewContent() {
        EaseUI.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String jsonEnttyStr = PrefsUtil.getInstance().getLoginAllEntity();
                if (TextUtils.isEmpty(jsonEnttyStr)) {
                    return;
                }
                try {
                    JSONObject jsonEntity = new JSONObject(jsonEnttyStr);
                    JSONArray jsonArrCompany = jsonEntity.optJSONArray("companyList");
                    JSONArray jsonArrOrg = jsonEntity.optJSONArray("organizationList");
                    JSONArray jsonArrUserCompany = jsonEntity.optJSONArray("userCompanyRelationshipList");
                    List<MPOrgEntity> orgEntities = MPOrgEntity.create(jsonArrOrg, jsonArrCompany, jsonArrUserCompany);
                    if (isFinishing()) {
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            realAddCompanyListView(orgEntities);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void realAddCompanyListView(List<MPOrgEntity> orgEntities) {
        for (MPOrgEntity item : orgEntities) {
            realAddCompanyViewItem(item);
        }
    }

    private void realAddCompanyViewItem(MPOrgEntity orgEntity) {
        CompayOrgItemView itemView = new CompayOrgItemView(this);
        itemView.setCompanyAndOrgInfo(orgEntity);
        itemView.setItemClickListener(new CompayOrgItemView.ICompanyOrgClickListener() {
            @Override
            public void onStructureClicked(int companyId, int orgId) {
                Intent intent = new Intent();
                int rcode = 1000;
                if (isSchedule || isCreate) {
                    rcode = 1001;
                    intent.setClass(activity, CompanyStructureActivity.class);
                    intent.putExtra("isSchedule", true);
                    intent.putExtra("isCreate", isCreate);
                    intent.putExtra("pickedOrgIdMap", (Serializable) pickedOrgIdMap);
                    intent.putIntegerArrayListExtra("pickedUidList", (ArrayList<Integer>) pickedUidList);
                } else {
                    intent.setClass(activity, OrgStructureActivity.class);
                }
                startActivityForResult(intent
                        .putExtra("orgName", orgEntity.getCompanyName())
                        .putExtra("orgId", orgId)
                        .putExtra("companyId", companyId)
                        .putExtra("isPick", true)
                        .putExtra("pickList", pickList)
                        .putExtra("isCard", isCard)
                        .putExtra("groupEntity", groupEntity)
                        .putExtra("pickedList", pickedList), rcode
                );
            }

            @Override
            public void onOrgClicked(int companyId, int orgId) {
                Intent intent = new Intent();
                int rcode = 1000;
                if (isSchedule || isCreate) {
                    rcode = 1001;
                    intent.setClass(activity, CompanyStructureActivity.class);
                    intent.putExtra("isSchedule", true);
                    intent.putExtra("isCreate", isCreate);
                    intent.putExtra("pickedOrgIdMap", (Serializable) pickedOrgIdMap);
                    intent.putIntegerArrayListExtra("pickedUidList", (ArrayList<Integer>) pickedUidList);
                } else {
                    intent.setClass(activity, OrgStructureActivity.class);
                }
                if (orgId >= 0) {
                    startActivityForResult(intent
                            .putExtra("orgName", orgEntity.getName())
                            .putExtra("myOrg", true)
                            .putExtra("isPick", true)
                            .putExtra("pickedList", pickedList)
                            .putExtra("groupEntity", groupEntity)
                            .putExtra("pickList", pickList)
                            .putExtra("companyId", companyId)
                            .putExtra("isCard", isCard)
                            .putExtra("orgId", orgId), rcode);
                } else {
                    startActivityForResult(intent
                            .putExtra("orgName", orgEntity.getCompanyName())
                            .putExtra("myOrg", true)
                            .putExtra("isPick", true)
                            .putExtra("pickList", pickList)
                            .putExtra("groupEntity", groupEntity)
                            .putExtra("pickedList", pickedList)
                            .putExtra("isCard", isCard)
                            .putExtra("companyId", companyId)
                            .putExtra("orgId", orgId), rcode);
                }

            }
        });
        llCompanyListView.addView(itemView);
    }

}