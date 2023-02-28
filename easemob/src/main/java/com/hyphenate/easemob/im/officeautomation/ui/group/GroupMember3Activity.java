package com.hyphenate.easemob.im.officeautomation.ui.group;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.EMCallBack;
import com.hyphenate.easemob.imlibs.cache.OnlineCache;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.eventbus.Subscribe;
import com.hyphenate.eventbus.ThreadMode;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupMemberChanged;
import com.hyphenate.easemob.imlibs.mp.events.EventOnLineOffLineQuery;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.im.mp.widget.ClearEditText;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;
import com.hyphenate.easemob.im.officeautomation.ui.ContactDetailsActivity;
import com.hyphenate.easemob.im.officeautomation.ui.group.GroupAddMemberActivity;

import java.util.ArrayList;
import java.util.List;

import me.zhouzhuo.zzletterssidebar.utils.CharacterParser;

public class GroupMember3Activity extends BaseActivity {

    private static String TAG = "GroupMember3Activity";

    private RecyclerView recyclerView;
    private ImageView ivBack;
    private TextView tvRight;
    private ClearEditText mClearEditText;
    private ArrayList<Integer> pickedList = new ArrayList<>();
    private MPGroupEntity groupEntity;
    private MembersAdapter adapter;
    private final List<MPUserEntity> data = new ArrayList<>();
    private final List<MPUserEntity> assistantData = new ArrayList<>();
    private final List<MPUserEntity> delUsers = new ArrayList<>();
    private boolean isDel;
    private List<MPUserEntity> userEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_memeber_33);

        userEntities = getIntent().getParcelableArrayListExtra("userEntities");
        groupEntity = getIntent().getParcelableExtra("groupEntity");
        isDel = getIntent().getBooleanExtra("isDel", false);
        data.addAll(userEntities);
        assistantData.addAll(data);
        for (MPUserEntity entity : userEntities) {
            pickedList.add(entity.getId());
        }

        initViews();
        initListeners();

        if (!groupEntity.isCluster()) {
            fetchUserListStatus(userEntities);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventContactQuery(EventOnLineOffLineQuery query) {
        if (!groupEntity.isCluster()) {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }


    private void fetchUserListStatus(List<MPUserEntity> userEntities) {
        ArrayList<String> userIds = new ArrayList<>();
        for (MPUserEntity item : userEntities) {
            Boolean status = OnlineCache.getInstance().get(item.getImUserId());
            if (status == null) {
                userIds.add(item.getImUserId());
            }
        }

        if(userIds.isEmpty()) {
            return;
        }

        EMClient.getInstance().chatManager().getUserStatusWithUserIds(userIds, new EMCallBack() {
            @Override
            public void onSuccess() {
                MPLog.i(TAG, "getUserStatusWithUserIds-onSuccess,userIds:" + userIds.toString());
            }

            @Override
            public void onError(int i, String s) {
                MPLog.e(TAG, "getUserStatusWithUserIds-onError:" + s + ",userIds:" + userIds.toString());
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }


    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvRight = findViewById(R.id.tv_right);
        if (isDel) {
            tvRight.setText(getResources().getString(R.string.delete));
        }

        if (groupEntity.isAllowInvites() || groupEntity.getCreateUserId() == UserProvider.getInstance().getLoginUser().getId()) {
            tvRight.setVisibility(View.VISIBLE);
        } else {
            tvRight.setVisibility(View.GONE);
        }

        recyclerView = findViewById(R.id.recylerview);
        mClearEditText = findViewById(R.id.filter_edit);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MembersAdapter();
        recyclerView.setAdapter(adapter);


        mClearEditText.setOnFocusChangeListener((v, hasFocus) -> mClearEditText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL));
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

    @Override
    public void finishActivity() {
        super.finishActivity();
        if (isDel) {
            if (data.size() < userEntities.size()) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
            }
        }
    }


    private void initListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDel) {
                    if (data.size() < userEntities.size()) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                    }
                }
                finish();
            }
        });

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDel) {
                    // 跳转新增界面
                    startActivityForResult(new Intent(activity, GroupAddMemberActivity.class).putExtra("pickedList", pickedList).putExtra("groupEntity", groupEntity), 1000);
                } else {

                    if (delUsers.size() == 0) {
                        Toast.makeText(GroupMember3Activity.this, "请选择要删除的群成员", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    StringBuilder builder = new StringBuilder();
                    for (MPUserEntity member : delUsers) {
                        builder.append(member.getId()).append(",");
                    }
                    removeMembersFromGroup(groupEntity.getId(), builder.substring(0, builder.lastIndexOf(",")));
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }

    }


    public class MembersAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_department_list_user_right_check, parent, false);
            return new MemberViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            MemberViewHolder memberViewHolder = (MemberViewHolder) holder;
            MPUserEntity member = data.get(position);
            memberViewHolder.tv_name.setText(member.getRealName());
            AvatarUtils.setAvatarContent(GroupMember3Activity.this, member.getRealName(), member.getAvatar(), memberViewHolder.iv_avatar);

            if (isOwner(member)) {
                memberViewHolder.tv_owner.setVisibility(View.VISIBLE);
                memberViewHolder.tv_owner.setText(getResources().getString(R.string.label_group_owner));
            } else {
                memberViewHolder.tv_owner.setVisibility(View.GONE);
            }

            if (isDel) {
                memberViewHolder.pick.setVisibility(View.VISIBLE);
                if (isOwner(member)) {
                    memberViewHolder.pick.setChecked(true);
                    memberViewHolder.pick.setEnabled(false);
                } else {
                    memberViewHolder.pick.setChecked(false);
                    memberViewHolder.pick.setEnabled(true);
                }
            }

            memberViewHolder.pick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        delUsers.add(member);
                    } else {
                        delUsers.remove(member);
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity, ContactDetailsActivity.class).putExtra("userId", member.getId()));
                }
            });

            if(!groupEntity.isCluster()) {
                Boolean status = OnlineCache.getInstance().get(member.getImUserId());
                if (status != null && status) {
                    memberViewHolder.tv_online.setText("[在线]");
                    memberViewHolder.tv_online.setTextColor(Color.GREEN);
                } else {
                    memberViewHolder.tv_online.setText("[离线]");
                    memberViewHolder.tv_online.setTextColor(Color.GRAY);
                }
            } else {
                memberViewHolder.tv_online.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MemberViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_name;
            private TextView tv_owner;
            private AvatarImageView iv_avatar;
            private CheckBox pick;
            private TextView tv_online;

            private MemberViewHolder(View itemView) {
                super(itemView);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_owner = itemView.findViewById(R.id.tv_owner);
                iv_avatar = itemView.findViewById(R.id.iv_avatar);
                pick = itemView.findViewById(R.id.cb_pick);
                tv_online = itemView.findViewById(R.id.tv_online);
            }
        }
    }


    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<MPUserEntity> filterMemberItems = new ArrayList<>();
        if (TextUtils.isEmpty(filterStr)) {
            filterMemberItems.addAll(assistantData);
        } else {
            filterMemberItems.clear();
            synchronized (data) {
                for (MPUserEntity item : data) {
                    String name = item.getRealName();
                    if (name.contains(filterStr) || CharacterParser.getInstance().getSelling(name).startsWith(filterStr)) {
                        filterMemberItems.add(item);
                    }
                }
            }
        }
        data.clear();
        data.addAll(filterMemberItems);
        adapter.notifyDataSetChanged();
    }

    private boolean isOwner(MPUserEntity userEntity) {
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser == null) {
            return false;
        }
        return userEntity.getId() == groupEntity.getOwnerId();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void removeMembersFromGroup(int groupId, String removedUserIds) {
        EMAPIManager.getInstance().deleteMembersFromGroup(groupId, removedUserIds, groupEntity.isCluster(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        data.removeAll(delUsers);
                        assistantData.removeAll(delUsers);
                        delUsers.clear();
                        adapter.notifyDataSetChanged();
                        MPEventBus.getDefault().post(new EventGroupMemberChanged());
                        Toast.makeText(activity, "删除成员成功！", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "删除失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
