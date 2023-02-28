package com.hyphenate.easemob.im.officeautomation.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.im.gray.GrayScaleConfig;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.utils.PreferenceManager;

import java.util.List;

/**
 * Created by qby on 2018/05/14.
 * recyclerView上拉加载适配器,组织架构
 */

public class DepartmentRefreshFooterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "DepartmentRefreshFooterAdapter";
    //上拉加载
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //没有加载的时候
    public static final int LOADING_END = 2;
    private Context context;
    //上拉加载更多状态-默认为0
    private int load_more_status = 0;
    private LayoutInflater mInflater;
    private List<MPOrgEntity> mList1;
    private List<MPUserEntity> mList2;
    private static final int TYPE_ITEM_DEPART = 0;  //部门Item View
    private static final int TYPE_ITEM_USER = 1;  //用户Item View
    private static final int TYPE_FOOTER = 2;  //顶部FootView
    private DepartmentItemCallback callback;

    private boolean isPick;
    private boolean isCard;
    private MPGroupEntity groupEntity;

    public DepartmentRefreshFooterAdapter(boolean isPick, boolean isCard, Context context, List<MPOrgEntity> mList1, List<MPUserEntity> mList2, DepartmentItemCallback callback) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList1 = mList1;
        this.mList2 = mList2;
        this.callback = callback;
        this.isPick = isPick;
        this.isCard = isCard;
        groupEntity = ((Activity)context).getIntent().getParcelableExtra("groupEntity");
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
//            View view = mInflater.inflate(R.layout.item_department_list, parent, false);
            View view = mInflater.inflate(R.layout.item_department_list_catalog, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            return new DepartmentViewHolder(view);
        } else if (viewType == TYPE_ITEM_USER) {
            View view;
            if(isPick) {
                view = mInflater.inflate(R.layout.item_department_list_user_left_check, parent, false);
            }else{
                view = mInflater.inflate(R.layout.item_department_list_user_item, parent, false);
            }
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            return new DepartmentUserViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View foot_view = mInflater.inflate(R.layout.recycler_load_more_layout, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            return new FootViewHolder(foot_view);
        }
        return null;
    }

    /**
     * 数据的绑定显示
     *
     * @param holder   holder
     * @param position 索引
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof DepartmentViewHolder) {
            DepartmentViewHolder contentHolder = (DepartmentViewHolder) holder;
            MPOrgEntity dataBean = mList1.get(position);
            if(GrayScaleConfig.showOrgMemberCount) {
                if (contentHolder.tvDepartNum != null)
                    contentHolder.tvDepartNum.setText(dataBean.getMemberCount() + "");
//                contentHolder.tv_department.setText(String.format(context.getResources().getString(R.string.org_name_number), dataBean.getName(), dataBean.getMemberCount()));
            }
            contentHolder.tv_department.setText(dataBean.getName());

            if (position == mList1.size() - 1 && mList2.size() > 0) {
                contentHolder.depart_divider.setVisibility(View.VISIBLE);
            } else {
                contentHolder.depart_divider.setVisibility(View.GONE);
            }
            contentHolder.rl_department.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onDepartmentClick(position);
                }
            });
            contentHolder.rl_department.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (PreferenceManager.getInstance().isAdmin())
                        callback.onDepartmentLongClick(view, position);
                    return true;
                }
            });

            holder.itemView.setTag(position);
        } else if (holder instanceof DepartmentUserViewHolder) {
            DepartmentUserViewHolder contentHolder = (DepartmentUserViewHolder) holder;
            final MPUserEntity entitiesBean = mList2.get(position - mList1.size());

            if (isOwner(entitiesBean)) {
                contentHolder.tv_owner.setVisibility(View.VISIBLE);
                contentHolder.tv_owner.setText(context.getResources().getString(R.string.label_group_owner));
            } else {
                contentHolder.tv_owner.setVisibility(View.GONE);
            }

            if (isPick) {
                if (entitiesBean.getImUserId().equals(EMClient.getInstance().getCurrentUser())) {
                    entitiesBean.setPickStatus(2);
                }
                contentHolder.pick.setVisibility(View.VISIBLE);
                contentHolder.pick.setEnabled(false);
                contentHolder.pick.setClickable(false);
                if (entitiesBean.getPickStatus() == 0) {
                    contentHolder.pick.setChecked(false);
                    contentHolder.pick.setEnabled(true);
                    contentHolder.itemView.setEnabled(true);
                } else if (entitiesBean.getPickStatus() == 1) {
                    contentHolder.pick.setChecked(true);
                    contentHolder.pick.setEnabled(true);
                    contentHolder.itemView.setEnabled(true);
                } else if (entitiesBean.getPickStatus() == 2) {
                    contentHolder.pick.setChecked(true);
                    contentHolder.pick.setEnabled(false);
                    contentHolder.itemView.setEnabled(false);
                }
            }
            AvatarUtils.setAvatarContent(context, TextUtils.isEmpty(entitiesBean.getRealName()) ? entitiesBean.getUsername() : entitiesBean.getRealName(), entitiesBean.getAvatar(), contentHolder.iv_avatar);

            contentHolder.tv_name.setText(TextUtils.isEmpty(entitiesBean.getAlias()) ? entitiesBean.getRealName() : entitiesBean.getAlias());
            contentHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPick) {
                        if (contentHolder.pick.isChecked()) {
                            entitiesBean.setPickStatus(0);
                        } else {
                            entitiesBean.setPickStatus(1);
                        }
                    }
                    if (isPick || isCard) {
                        callback.onUserPick(contentHolder.pick, entitiesBean);
                    } else {
                        if (TextUtils.isEmpty(entitiesBean.getImUserId()))
                            return;
                        callback.onUserClick(entitiesBean.getImUserId());
                    }
                }
            });

            holder.itemView.setTag(position);
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            switch (load_more_status) {
                case PULLUP_LOAD_MORE:
                    footViewHolder.pb_more.setVisibility(View.GONE);
                    footViewHolder.foot_view_item_tv.setText(context.getResources().getString(R.string.pull_to_load));
                    break;
                case LOADING_MORE:
                    footViewHolder.pb_more.setVisibility(View.VISIBLE);
                    footViewHolder.foot_view_item_tv.setText(context.getResources().getString(R.string.load_more));
                    break;
                case LOADING_END:
                    footViewHolder.pb_more.setVisibility(View.GONE);
                    footViewHolder.foot_view_item_tv.setText(context.getResources().getString(R.string.load_end));
                    break;
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
        Log.i("info", "position:" + position + "---mList1:" + mList1.size() + "---mList2:" + mList2.size());
        if (mList2.size() >= Constant.PAGE_SIZE && position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else if (position + 1 <= mList1.size()) {
            return TYPE_ITEM_DEPART;
        } else {
            return TYPE_ITEM_USER;
        }
    }

    @Override
    public int getItemCount() {
        if (mList2.size() < Constant.PAGE_SIZE) {
            return mList1.size() + mList2.size();
        } else {
            return mList1.size() + mList2.size() + 1;
        }
    }

    //自定义的DepartmentViewHolder，持有每个Department的的所有界面元素
    private static class DepartmentViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rl_department;
        private TextView tv_department;
        private View depart_divider;
        private CheckBox checkOrg;
        private View rightIcon;
        private TextView tvDepartNum;

        private DepartmentViewHolder(View view) {
            super(view);
            rl_department = view.findViewById(R.id.rl_department);
            tv_department = view.findViewById(R.id.tv_department);
            tvDepartNum = view.findViewById(R.id.tv_department_num);
            depart_divider = view.findViewById(R.id.depart_divider);
            checkOrg = view.findViewById(R.id.cb_all);
            rightIcon = view.findViewById(R.id.iv_right);
        }
    }

    //自定义的DepartmentUserViewHolder，持有每个DepartmentUser的的所有界面元素
    private static class DepartmentUserViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_owner;
        private AvatarImageView iv_avatar;
        private CheckBox pick;
        private LinearLayout layoutPickAll;
        private CheckBox pickAll;
        private RelativeLayout itemLayout;

        private DepartmentUserViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_owner = itemView.findViewById(R.id.tv_owner);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            pick = itemView.findViewById(R.id.cb_pick);
            layoutPickAll = itemView.findViewById(R.id.ll_check_all);
            pickAll = itemView.findViewById(R.id.cb_all);
            itemLayout = itemView.findViewById(R.id.rl_department_user);
        }
    }

    /**
     * 底部FootView布局
     */
    private static class FootViewHolder extends RecyclerView.ViewHolder {
        private TextView foot_view_item_tv;
        private ProgressBar pb_more;

        private FootViewHolder(View view) {
            super(view);
            foot_view_item_tv = view.findViewById(R.id.foot_view_item_tv);
            pb_more = view.findViewById(R.id.pb_more);
        }
    }

    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     *
     * @param status 加载状态
     */
    public void changeMoreStatus(int status) {
        load_more_status = status;
        notifyDataSetChanged();
    }

    /**
     * 获取加载更多状态
     *
     * @return 加载更多状态
     */
    public int getLoadMoreStatus() {
        return load_more_status;
    }

    private boolean isOwner(MPUserEntity userEntity) {
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser == null) {
            return false;
        }
        if(null != groupEntity) {
            return userEntity.getId() == groupEntity.getOwnerId();
        }else{
            return false;
        }
    }

    public interface DepartmentItemCallback {
        void onDepartmentLongClick(View view, int position);

        void onDepartmentClick(int position);

        void onUserClick(String easemobName);

        void onUserPick(CheckBox checkBox, MPUserEntity entity);
    }
}