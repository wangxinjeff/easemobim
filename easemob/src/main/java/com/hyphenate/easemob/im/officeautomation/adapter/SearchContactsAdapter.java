package com.hyphenate.easemob.im.officeautomation.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.domain.SearchContactsBean;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;

import java.util.List;

/**
 * Created by qby on 2018/06/14.
 * recyclerView上拉加载适配器,搜索联系人
 */

public class SearchContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "SearchContactsAdapter";
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
    private List<SearchContactsBean.EntitiesBean> mList;
    private static final int TYPE_ITEM = 0;  //部门Item View
    private static final int TYPE_FOOTER = 2;  //顶部FootView
    private SearchContactsItemCallback callback;
    private String searchText;

    public SearchContactsAdapter(Context context, List<SearchContactsBean.EntitiesBean> mList, SearchContactsItemCallback callback) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = mList;
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
        if (viewType == TYPE_ITEM) {
            View view = mInflater.inflate(R.layout.item_search_list_contacts, parent, false);
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

    public void setSearchText(String txt){
        this.searchText = txt;
    }

    /**
     * 数据的绑定显示
     *
     * @param holder   holder
     * @param position 索引
     */
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder contentHolder = (ItemViewHolder) holder;
            SearchContactsBean.EntitiesBean dataBean = mList.get(position);
            String name = TextUtils.isEmpty(dataBean.getRealName()) ? dataBean.getUsername() : dataBean.getRealName();
            EaseCommonUtils.initHighLight(context, contentHolder.tv_name, searchText, name);
//            contentHolder.tv_name.setText(name);
            AvatarUtils.setAvatarContent(context, name, dataBean.getImage(), contentHolder.iv_avatar);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onItemClick(position);
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
        if (mList.size() >= Constant.PAGE_SIZE && position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if (mList.size() < Constant.PAGE_SIZE) {
            return mList.size();
        } else {
            return mList.size() + 1;
        }
    }

    //自定义的ViewHolder，持有每个contact的的所有界面元素
    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private AvatarImageView iv_avatar;
        private TextView tv_name;

        private ItemViewHolder(View view) {
            super(view);
            iv_avatar = view.findViewById(R.id.iv_avatar);
            tv_name = view.findViewById(R.id.tv_name);
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

    public interface SearchContactsItemCallback {

        void onItemClick(int position);
    }
}