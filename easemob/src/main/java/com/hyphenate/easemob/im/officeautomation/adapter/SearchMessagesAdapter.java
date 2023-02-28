package com.hyphenate.easemob.im.officeautomation.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.domain.SearchMessagesBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qby on 2018/06/14.
 * 搜索聊天记录适配器
 */
public class SearchMessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater mInflater;
    private List<SearchMessagesBean> mList;
    private SearchMessagesItemCallback callback;
    private String searchText;

    public SearchMessagesAdapter(Context context, ArrayList<SearchMessagesBean> mList, SearchMessagesItemCallback callback) {
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
        View view = mInflater.inflate(R.layout.item_search_list_messages, parent, false);
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
            SearchMessagesBean searchMessagesBean = mList.get(position);

            String username = TextUtils.isEmpty(searchMessagesBean.getRealName()) ? searchMessagesBean.getUserName() : searchMessagesBean.getRealName();
            String name = TextUtils.isEmpty(username) ? searchMessagesBean.getEasemobName() : username;
            if (searchMessagesBean.getType() == EMMessage.ChatType.Chat) {
                AvatarUtils.setAvatarContent(context, name, searchMessagesBean.getAvatar(), contentHolder.iv_avatar);
            }else{
                GlideUtils.load(context, searchMessagesBean.getAvatar(), R.drawable.ease_group_icon, contentHolder.iv_avatar);
            }

            EaseCommonUtils.initHighLight(context, contentHolder.tv_name, searchText, name);
//            contentHolder.tv_name.setText(name);
            contentHolder.tv_content.setText(searchMessagesBean.getContent());
            contentHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onMessageClick(position);
                }
            });
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //自定义的ViewHolder，持有每个message的的所有界面元素
    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_content;
        private AvatarImageView iv_avatar;

        private ItemViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_content = itemView.findViewById(R.id.tv_content);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
        }
    }


    public interface SearchMessagesItemCallback {

        void onMessageClick(int position);
    }
}
