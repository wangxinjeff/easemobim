package com.hyphenate.easemob.im.officeautomation.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qby on 2018/06/28.
 * recyclerView上拉加载适配器,发起群聊用户列表适配器
 */

public class NewChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "NewChatAdapter";
    private int initSize;
    private Context context;
    private LayoutInflater mInflater;
    private List<MPOrgEntity> mList1;
    private List<EaseUser> mList2;
    private ArrayList<EaseUser> selectedList;
    private List<String> members;
    private static final int TYPE_ITEM_GROUP = 0;  //群Item View
    private static final int TYPE_ITEM_DEPART = 1;  //部门Item View
    private static final int TYPE_ITEM = 2;  //用户
    private NewChatItemCallback callback;

    public NewChatAdapter(Context context, List<MPOrgEntity> mList1, List<EaseUser> mList2, ArrayList<EaseUser> selectedList, List<String> members, boolean isShowGroup, NewChatItemCallback callback) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList1 = mList1;
        this.mList2 = mList2;
        this.selectedList = selectedList;
        this.members = members;
        if (!isShowGroup)
            initSize = -1;
        this.callback = callback;
    }

    /**
     * item显示类型
     *
     * @param parent   父控件
     * @param viewType view类型
     * @return holder
     */
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //进行判断显示类型，来创建返回不同的View
        if (viewType == TYPE_ITEM_GROUP) {
            View view = mInflater.inflate(R.layout.item_new_chat_list_group, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            return new GroupViewHolder(view);
        } else if (viewType == TYPE_ITEM_DEPART) {
            View view = mInflater.inflate(R.layout.item_new_chat_list_org, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            return new DepartmentViewHolder(view);
        } else if (viewType == TYPE_ITEM) {
            View foot_view = mInflater.inflate(R.layout.item_new_chat_list_user, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            return new UserViewHolder(foot_view);
        }
        return null;
    }

    /**
     * 数据的绑定显示
     *
     * @param holder   holder
     * @param position 索引
     */
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroupViewHolder) {
            GroupViewHolder contentHolder = (GroupViewHolder) holder;
            contentHolder.ll_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onGroupClick();
                }
            });
            holder.itemView.setTag(position);
        } else if (holder instanceof DepartmentViewHolder) {
            DepartmentViewHolder contentHolder = (DepartmentViewHolder) holder;
            final int realPos = position - (initSize + 1);
            MPOrgEntity dataBean = mList1.get(realPos);
            contentHolder.tv_org.setText(dataBean.getName());
            contentHolder.rl_org.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onDepartmentClick(realPos);
                }
            });
            holder.itemView.setTag(position);
        } else if (holder instanceof UserViewHolder) {
            final UserViewHolder contentHolder = (UserViewHolder) holder;
            final int realPos = position - mList1.size() - (initSize + 1);
            if (realPos == 0) {
                contentHolder.ll_contacts.setVisibility(View.VISIBLE);
                contentHolder.ll_user.setVisibility(View.GONE);
                return;
            }
            contentHolder.ll_user.setVisibility(View.VISIBLE);
            contentHolder.ll_contacts.setVisibility(View.GONE);
            EaseUser easeUser = mList2.get(realPos - 1);
            contentHolder.tv_name.setText(easeUser.getNickname());
            String avatar = easeUser.getAvatar();
            String username = easeUser.getUsername();
            if (TextUtils.isEmpty(username)){
                contentHolder.tvState.setVisibility(View.VISIBLE);
                contentHolder.cb.setEnabled(false);
            }else{
                contentHolder.cb.setEnabled(true);
                contentHolder.tvState.setVisibility(View.GONE);
            }

            AvatarUtils.setAvatarContent(context, easeUser, contentHolder.iv_avatar);

//            GlideUtils.load(context, avatar, R.drawable.em_default_avatar, contentHolder.iv_avatar);

            contentHolder.cb.setClickable(false);
            //如果是已选中的成员
            if (selectedList.contains(easeUser)) {
                contentHolder.cb.setChecked(true);
            } else {
                contentHolder.cb.setChecked(false);
            }
            //如果已经是群成员
            if (members.contains(easeUser.getUsername())) {
                contentHolder.cb.setChecked(true);
                contentHolder.cb.setEnabled(false);
                contentHolder.ll_user.setOnClickListener(null);
            } else {
                ((UserViewHolder) holder).ll_user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contentHolder.cb.setChecked(!contentHolder.cb.isChecked());
                        callback.onUserClick(realPos);
                    }
                });
            }
        }
    }

    /**
     * 进行判断是普通Item视图还是FootView视图
     *
     * @param position 索引
     * @return 条目ID
     */
    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (position == initSize) {
            return TYPE_ITEM_GROUP;
        } else if (position < mList1.size() + initSize + 1) {
            return TYPE_ITEM_DEPART;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
//        if (mList2.size() < Constant.PAGE_SIZE) {
//            return mList1.size() + mList2.size() + 1;
//        } else {
//            return ;
//        }
        return mList1.size() + mList2.size() + 1;
    }

    //自定义的DepartmentViewHolder，持有每个Department的的所有界面元素
    private static class DepartmentViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rl_org;
        private TextView tv_org;

        private DepartmentViewHolder(View view) {
            super(view);
            rl_org = view.findViewById(R.id.rl_org);
            tv_org = view.findViewById(R.id.tv_org);
        }
    }

    //自定义的GroupViewHolder
    private static class GroupViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout ll_group;

        private GroupViewHolder(View itemView) {
            super(itemView);
            ll_group = itemView.findViewById(R.id.ll_group);
        }
    }

    /**
     * 用户ItemView布局
     */
    private static class UserViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_contacts;
        private LinearLayout ll_user;
        private TextView tv_name;
        private AvatarImageView iv_avatar;
        private CheckBox cb;
        private TextView tvState;

        private UserViewHolder(View view) {
            super(view);
            ll_contacts = view.findViewById(R.id.ll_contacts);
            ll_user = view.findViewById(R.id.ll_user);
            tv_name = view.findViewById(R.id.tv_name);
            iv_avatar = view.findViewById(R.id.iv_avatar);
            cb = view.findViewById(R.id.cb);
            tvState = view.findViewById(R.id.tv_state);
        }
    }

    public interface NewChatItemCallback {
        void onGroupClick();

        void onDepartmentClick(int position);

        void onUserClick(int position);
    }
}