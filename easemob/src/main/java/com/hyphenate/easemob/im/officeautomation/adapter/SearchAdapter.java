package com.hyphenate.easemob.im.officeautomation.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.domain.SearchContactsBean;
import com.hyphenate.easemob.im.officeautomation.domain.SearchMessagesBean;

import java.util.List;

/**
 * Created by qby on 2018/05/14.
 * recyclerView上拉加载适配器,搜索全部
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "SearchAdapter";
    private Context context;
    private LayoutInflater mInflater;
    private List<SearchContactsBean.EntitiesBean> mList1;
    private List<GroupBean> mList2;
    private List<SearchMessagesBean> mList3;
    private static final int TYPE_ITEM_CONTACTS = 0;  //联系人Item View
    private static final int TYPE_ITEM_GROUPS = 1;  //群组Item View
    private static final int TYPE_ITEM_MESSAGES = 2;  //消息记录Item View
    private SearchAllItemCallback callback;
    private String searchText;

    public SearchAdapter(Context context, List<SearchContactsBean.EntitiesBean> mList1, List<GroupBean> mList2, List<SearchMessagesBean> mList3, SearchAllItemCallback callback) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList1 = mList1;
        this.mList2 = mList2;
        this.mList3 = mList3;
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
        if (viewType == TYPE_ITEM_CONTACTS) {
            View view = mInflater.inflate(R.layout.item_search_list_contacts, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            return new ContactsViewHolder(view);
        } else if (viewType == TYPE_ITEM_GROUPS) {
            View view = mInflater.inflate(R.layout.item_search_list_groups, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            return new GroupsViewHolder(view);
        } else if (viewType == TYPE_ITEM_MESSAGES) {
            View foot_view = mInflater.inflate(R.layout.item_search_list_messages, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            return new MessagesViewHolder(foot_view);
        }
        return null;
    }

    /**
     * 数据的绑定显示
     *
     * @param holder   holder
     * @param position 索引
     */
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        if (holder instanceof ContactsViewHolder) {
            ContactsViewHolder contentHolder = (ContactsViewHolder) holder;
            contentHolder.title.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            contentHolder.tv_more.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
            SearchContactsBean.EntitiesBean dataBean = mList1.get(position);
            String name = TextUtils.isEmpty(dataBean.getAlias()) ? dataBean.getRealName() + "" : dataBean.getAlias();
            String phone = dataBean.getMobilephone();
            if (TextUtils.isEmpty(phone)){
                contentHolder.tv_phone.setVisibility(View.GONE);
            }else{
//                contentHolder.tv_phone.setText(phone);
                EaseCommonUtils.initHighLight(context, contentHolder.tv_phone, searchText, phone);
                contentHolder.tv_phone.setVisibility(View.VISIBLE);
            }
            EaseCommonUtils.initHighLight(context, contentHolder.tv_name, searchText, name);
//            contentHolder.tv_name.setText(name);
            AvatarUtils.setAvatarContent(context, name, dataBean.getImage(), contentHolder.iv_avatar);

            contentHolder.layout_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onItemClick(position, TYPE_ITEM_CONTACTS);
                }
            });
            contentHolder.tv_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onMoreClick(TYPE_ITEM_CONTACTS);
                }
            });
            holder.itemView.setTag(position);
        } else if (holder instanceof GroupsViewHolder) {
            GroupsViewHolder contentHolder = (GroupsViewHolder) holder;
            if(position < mList1.size()) {
                return;
            }
            final int realPos = position - mList1.size();
            contentHolder.title.setVisibility(realPos == 0 ? View.VISIBLE : View.GONE);
            contentHolder.tv_more.setVisibility(realPos == 2 ? View.VISIBLE : View.GONE);
            if (realPos >= mList2.size()){
                return;
            }
            GroupBean searchGroupBean = mList2.get(realPos);
            EaseCommonUtils.initHighLight(context, contentHolder.tv_name, searchText, searchGroupBean.getNick());
//            contentHolder.tv_name.setText(searchGroupBean.getNick());
            String remoteUrl = searchGroupBean.getAvatar();
            GlideUtils.load(context, remoteUrl, R.drawable.ease_group_icon, contentHolder.iv_avatar);
            contentHolder.layout_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onItemClick(realPos, TYPE_ITEM_GROUPS);
                }
            });
            contentHolder.tv_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onMoreClick(TYPE_ITEM_GROUPS);
                }
            });
            holder.itemView.setTag(position);
        } else if (holder instanceof MessagesViewHolder) {
            MessagesViewHolder contentHolder = (MessagesViewHolder) holder;
            final int realPos = position - mList1.size() - mList2.size();
            contentHolder.title.setVisibility(realPos == 0 ? View.VISIBLE : View.GONE);
            contentHolder.tv_more.setVisibility(realPos == 2 ? View.VISIBLE : View.GONE);
            SearchMessagesBean searchMessagesBean = mList3.get(realPos);
            String username = TextUtils.isEmpty(searchMessagesBean.getRealName()) ? searchMessagesBean.getUserName() : searchMessagesBean.getRealName();
            String name = TextUtils.isEmpty(username) ? searchMessagesBean.getEasemobName() : username;
            if (searchMessagesBean.getType() == EMMessage.ChatType.Chat){
                AvatarUtils.setAvatarContent(context, username, searchMessagesBean.getAvatar(), contentHolder.iv_avatar);
            }else{
                String remoteUrl = searchMessagesBean.getAvatar();
                GlideUtils.load(context, remoteUrl, R.drawable.ease_group_icon, contentHolder.iv_avatar);
            }

            EaseCommonUtils.initHighLight(context, contentHolder.tv_name, searchText, name);
//            contentHolder.tv_name.setText(name);
//            contentHolder.tv_content.setText(searchMessagesBean.content);
            EaseCommonUtils.initHighLight(context, contentHolder.tv_content, searchText, searchMessagesBean.getContent());
            contentHolder.layout_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onItemClick(realPos, TYPE_ITEM_MESSAGES);
                }
            });
            contentHolder.tv_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onMoreClick(TYPE_ITEM_MESSAGES);
                }
            });
            holder.itemView.setTag(position);
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
        if (position < mList1.size()) {
            return TYPE_ITEM_CONTACTS;
        } else if (position < mList1.size() + mList2.size()) {
            return TYPE_ITEM_GROUPS;
        } else {
            return TYPE_ITEM_MESSAGES;
        }
    }

    public void setSearchText(String text){
        this.searchText = text;
    }

    @Override
    public int getItemCount() {
        return mList1.size() + mList2.size() + mList3.size();
    }

    private static class ContactsViewHolder extends RecyclerView.ViewHolder {
        private AvatarImageView iv_avatar;
        private TextView tv_name;
        private TextView tv_more;
        private TextView title;
        private TextView tv_phone;
        private RelativeLayout layout_content;

        private ContactsViewHolder(View itemView) {
            super(itemView);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_more = itemView.findViewById(R.id.tv_more);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            title = itemView.findViewById(R.id.title);
            layout_content = itemView.findViewById(R.id.layout_content);
        }
    }

    //自定义的ViewHolder，持有每个user的的所有界面元素
    private static class GroupsViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private ImageView iv_avatar;
        private TextView tv_more;
        private TextView title;
        private RelativeLayout layout_content;

        private GroupsViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            tv_more = itemView.findViewById(R.id.tv_more);
            title = itemView.findViewById(R.id.title);
            layout_content = itemView.findViewById(R.id.layout_content);
        }
    }

    //自定义的ViewHolder，持有每个message的的所有界面元素
    private static class MessagesViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_content;
        private AvatarImageView iv_avatar;
        private TextView tv_more;
        private TextView title;
        private LinearLayout layout_content;

        private MessagesViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_content = itemView.findViewById(R.id.tv_content);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            tv_more = itemView.findViewById(R.id.tv_more);
            title = itemView.findViewById(R.id.title);
            layout_content = itemView.findViewById(R.id.layout_content);
        }
    }

    //type 0,1,2 联系人，群组，聊天记录
    public interface SearchAllItemCallback {

        void onItemClick(int position, int type);

        void onMoreClick(int type);
    }
}