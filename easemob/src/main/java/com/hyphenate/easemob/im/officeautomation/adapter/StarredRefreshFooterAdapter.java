package com.hyphenate.easemob.im.officeautomation.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.easemob.imlibs.cache.OnlineCache;
import com.hyphenate.easemob.imlibs.cache.OnlineCacheFriends;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;

import java.util.List;

/**
 * Created by Administrator on 2017/10/14.
 * recyclerView上拉加载适配器,收益
 */

public class StarredRefreshFooterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "StarredRefreshFooterAdapter";
    //上拉加载
    private static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //没有加载的时候
    public static final int LOADING_END = 2;
    private Context context;
    //上拉加载更多状态-默认为0
    private int load_more_status = 0;
    private LayoutInflater mInflater;
    private List<MPUserEntity> mpUserEntityList;
    private static final int TYPE_ITEM = 0;  //部门Item View
    private static final int TYPE_FOOTER = 1;  //顶部FootView
    private StarredItemCallback callback;
    private boolean isPick;
    private boolean isCard;

    public StarredRefreshFooterAdapter(boolean isPick, boolean isCard, Context context, List<MPUserEntity> mList, StarredItemCallback callback) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mpUserEntityList = mList;
        this.callback = callback;
        this.isPick = isPick;
        this.isCard = isCard;
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
        if (viewType == TYPE_ITEM) {
            View view = mInflater.inflate(R.layout.item_department_list_user, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            return new ItemViewHolder(view);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder contentHolder = (ItemViewHolder) holder;
            final MPUserEntity entitiesBean = mpUserEntityList.get(position);
            AvatarUtils.setAvatarContent(context, TextUtils.isEmpty(entitiesBean.getRealName()) ? entitiesBean.getUsername() : entitiesBean.getRealName(), entitiesBean.getAvatar(), contentHolder.iv_avatar);
            Boolean status = OnlineCacheFriends.getInstance().get(entitiesBean.getImUserId());
            MPLog.e("onBindViewHolder", "imUser:" + entitiesBean.getImUserId() + ",status:" + status);
            if (status == null) {
                status = OnlineCache.getInstance().get(entitiesBean.getImUserId());
                MPLog.e("onBindViewHolder-OnlineCache", "imUser:" + entitiesBean.getImUserId() + ",status:" + status);
            }
            if (status != null && status) {
                contentHolder.tvOnline.setText("[在线]");
                contentHolder.tvOnline.setTextColor(Color.GREEN);
            } else {
                contentHolder.tvOnline.setText("[离线]");
                contentHolder.tvOnline.setTextColor(Color.GRAY);
            }
            contentHolder.tv_name.setText(TextUtils.isEmpty(entitiesBean.getAlias()) ? entitiesBean.getRealName() : entitiesBean.getAlias());
            contentHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPick || isCard) {
                        callback.onUserPick(contentHolder.pick, position);
                    } else {
                        callback.onUserClick(position);
                    }
                }
            });
            holder.itemView.setTag(position);

            if (isPick) {
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
        if (mpUserEntityList.size() >= Constant.PAGE_SIZE && position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if (mpUserEntityList.size() < Constant.PAGE_SIZE) {
            return mpUserEntityList.size();
        } else {
            return mpUserEntityList.size() + 1;
        }
    }

    //自定义的ItemViewHolder，持有每个user的的所有界面元素
    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private AvatarImageView iv_avatar;
        private CheckBox pick;
        private TextView tvOnline;

        private ItemViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            pick = itemView.findViewById(R.id.cb_pick);
            tvOnline = itemView.findViewById(R.id.tv_online);
            tvOnline.setVisibility(View.VISIBLE);
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

    public interface StarredItemCallback {

        void onUserClick(int position);

        void onUserPick(CheckBox checkBox, int position);
    }
}