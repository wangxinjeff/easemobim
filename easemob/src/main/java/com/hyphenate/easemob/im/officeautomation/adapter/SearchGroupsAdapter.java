package com.hyphenate.easemob.im.officeautomation.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/06/14.
 * recyclerView上拉加载适配器,搜索
 */

public class SearchGroupsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "SearchGroupsAdapter";
    private Context context;
    private LayoutInflater mInflater;
    private List<GroupBean> mList;
    private SearchGroupsItemCallback callback;
    private String searchText;

    public SearchGroupsAdapter(Context context, ArrayList<GroupBean> mList, SearchGroupsItemCallback callback) {
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
        View view = mInflater.inflate(R.layout.item_search_list_groups, parent, false);
        //这边可以做一些属性设置，甚至事件监听绑定
        //view.setBackgroundColor(Color.RED);
        return new ItemViewHolder(view);
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
            GroupBean searchGroupBean = mList.get(position);
            EaseCommonUtils.initHighLight(context, contentHolder.tv_name,searchText, searchGroupBean.getNick());
//            contentHolder.tv_name.setText(searchGroupBean.getNick());
            String remoteUrl = searchGroupBean.getAvatar();
            if (TextUtils.isEmpty(remoteUrl)) {
                contentHolder.iv_avatar.setImageResource(R.drawable.ease_group_icon);
            } else {
                GlideUtils.load(context, remoteUrl, R.drawable.ease_group_icon, contentHolder.iv_avatar);
            }
            contentHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onGroupClick(position);
                }
            });
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //自定义的ItemViewHolder，持有每个user的的所有界面元素
    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private ImageView iv_avatar;

        private ItemViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
        }
    }


    public interface SearchGroupsItemCallback {

        void onGroupClick(int position);
    }
}