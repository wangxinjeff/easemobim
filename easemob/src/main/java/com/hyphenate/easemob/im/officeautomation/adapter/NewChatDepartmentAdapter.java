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

import com.hyphenate.chat.EMClient;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.easeui.widget.EaseImageView;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;

import java.util.List;

/**
 * Created by qby on 2018/06/28.
 * recyclerView上拉加载适配器,组织架构
 */

public class NewChatDepartmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "NewChatDepartmentAdapter";
    private Context context;
    private LayoutInflater mInflater;
    private List<MPOrgEntity> mList1;
    private List<EaseUser> mList2;
    private List<EaseUser> selectedList;
    private List<String> members;
    private static final int TYPE_ITEM_DEPART = 0;  //部门Item View
    private static final int TYPE_ITEM_USER = 1;  //用户Item View
    private DepartmentItemCallback callback;
    private int isAllSelected;

    public NewChatDepartmentAdapter(Context context, List<MPOrgEntity> mList1, List<EaseUser> mList2, List<EaseUser> selectedList, List<String> members, DepartmentItemCallback callback) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList1 = mList1;
        this.mList2 = mList2;
        this.selectedList = selectedList;
        this.members = members;
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
        if (viewType == TYPE_ITEM_DEPART) {
            View view = mInflater.inflate(R.layout.item_new_chat_list_depart, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            return new DepartmentViewHolder(view);
        } else if (viewType == TYPE_ITEM_USER) {
            View view = mInflater.inflate(R.layout.item_new_chat_list_org_user, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            return new UserViewHolder(view);
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
        if (holder instanceof DepartmentViewHolder) {
            DepartmentViewHolder contentHolder = (DepartmentViewHolder) holder;
            MPOrgEntity dataBean = mList1.get(position);
            contentHolder.tv_department.setText(dataBean.getName());
            contentHolder.rl_department.setOnClickListener(view -> callback.onDepartmentClick(position));
            if (position == 0) {
                contentHolder.depart_divider.setVisibility(View.GONE);
            } else {
                contentHolder.depart_divider.setVisibility(View.VISIBLE);
            }
            holder.itemView.setTag(position);
        } else if (holder instanceof UserViewHolder) {
            UserViewHolder contentHolder = (UserViewHolder) holder;
            int realPos = position - mList1.size();
            EaseUser easeUser = mList2.get(realPos);
            GlideUtils.load(context, easeUser.getAvatar(), R.drawable.em_default_avatar, contentHolder.iv_avatar);
            contentHolder.tv_name.setText(TextUtils.isEmpty(easeUser.getNickname()) ? easeUser.getUsername() : easeUser.getNickname());

            if (members.contains(easeUser.getUsername()) || EMClient.getInstance().getCurrentUser().equals(easeUser.getUsername())) {
                contentHolder.cb.setChecked(true);
                contentHolder.cb.setEnabled(false);
                contentHolder.ll_user.setOnClickListener(null);
            } else {
                contentHolder.cb.setEnabled(true);
                if (selectedList.contains(easeUser)) {
                    contentHolder.cb.setChecked(true);
                } else {
                    contentHolder.cb.setChecked(false);
                }
               /* if (isAllSelected == 1) {
                    contentHolder.cb.setChecked(true);
                    contentHolder.cb_all.setChecked(true);
                } else if (isAllSelected == 2) {
                    contentHolder.cb.setChecked(false);
                    contentHolder.cb_all.setChecked(false);
                } else {
                    contentHolder.cb.setChecked(contentHolder.cb.isChecked());
                    contentHolder.cb_all.setChecked(false);
                }*/
                contentHolder.ll_user.setOnClickListener(view -> {
                    contentHolder.cb.setChecked(!contentHolder.cb.isChecked());
                    callback.onUserClick(realPos);
                });
            }
            /*contentHolder.ll_all.setOnClickListener(view -> {
                isAllSelected = contentHolder.cb_all.isChecked() ? 2 : 1;
                callback.onAllClick();
                notifyDataSetChanged();
            });*/
            holder.itemView.setTag(position);
        }
    }

    /**
     * 进行判断是普通Item视图还是部门视图
     *
     * @param position 索引
     * @return 条目ID
     */
    @Override
    public int getItemViewType(int position) {
        if (position + 1 <= mList1.size()) {
            return TYPE_ITEM_DEPART;
        } else {
            return TYPE_ITEM_USER;
        }
    }

    @Override
    public int getItemCount() {
        return mList1.size() + mList2.size();
    }

    public void checkedAllSelected(int isChecked) {
        isAllSelected = isChecked;
    }

    //自定义的DepartmentViewHolder，持有每个Department的的所有界面元素
    private static class DepartmentViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rl_department;
        private TextView tv_department;
        private View depart_divider;

        private DepartmentViewHolder(View view) {
            super(view);
            rl_department = view.findViewById(R.id.rl_department);
            tv_department = view.findViewById(R.id.tv_department);
            depart_divider = view.findViewById(R.id.depart_divider);
        }
    }

    /**
     * 用户ItemView布局
     */
    private static class UserViewHolder extends RecyclerView.ViewHolder {
//        private LinearLayout ll_all;
        private LinearLayout ll_user;
        private TextView tv_name;
        private EaseImageView iv_avatar;
        private CheckBox cb;
//        private CheckBox cb_all;

        private UserViewHolder(View view) {
            super(view);
//            ll_all = view.findViewById(R.id.ll_all);
            ll_user = view.findViewById(R.id.ll_user);
            tv_name = view.findViewById(R.id.tv_name);
            iv_avatar = view.findViewById(R.id.iv_avatar);
            cb = view.findViewById(R.id.cb);
//            cb_all = view.findViewById(R.id.cb_all);
        }
    }


    public interface DepartmentItemCallback {

        void onDepartmentClick(int position);

        void onAllClick();

        void onUserClick(int position);
    }
}