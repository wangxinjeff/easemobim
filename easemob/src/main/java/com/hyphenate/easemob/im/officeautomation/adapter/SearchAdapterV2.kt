package com.hyphenate.easemob.im.officeautomation.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.chat.EMClient
import com.hyphenate.easemob.easeui.domain.MPGroupEntity
import com.hyphenate.easemob.easeui.glide.GlideUtils
import com.hyphenate.easemob.easeui.utils.AvatarUtils
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils
import com.hyphenate.easemob.easeui.widget.AvatarImageView
import com.hyphenate.easemob.R
import com.hyphenate.easemob.im.officeautomation.domain.SearchConversation
import com.hyphenate.easemob.im.officeautomation.domain.SearchGroupEntity
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity

class SearchAdapterV2(private val context: Context, private val mList1: List<MPUserEntity>, private val mList2: List<MPGroupEntity>, private val mList3: List<SearchConversation>, private val callback: SearchAllItemCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var searchText: String? = null


    companion object {
        private const val TYPE_ITEM_CONTACTS = 0  //联系人Item View
        private const val TYPE_ITEM_GROUPS = 1  //群组Item View
        private const val TYPE_ITEM_MESSAGES = 2  //消息记录Item View
    }

    /**
     * item显示类型
     *
     * @param parent   父控件
     * @param viewType view类型
     * @return holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //进行判断显示类型，来创建返回不同的View
        when (viewType) {
            TYPE_ITEM_CONTACTS -> {
                val view = mInflater.inflate(R.layout.item_search_list_contacts, parent, false)
                //这边可以做一些属性设置，甚至事件监听绑定
                //view.setBackgroundColor(Color.RED);
                return ContactsViewHolder(view)
            }
            TYPE_ITEM_GROUPS -> {
                val view = mInflater.inflate(R.layout.item_search_list_groups, parent, false)
                //这边可以做一些属性设置，甚至事件监听绑定
                //view.setBackgroundColor(Color.RED);
                return GroupsViewHolder(view)
            }
            else -> {
                val footView = mInflater.inflate(R.layout.item_search_list_messages, parent, false)
                //这边可以做一些属性设置，甚至事件监听绑定
                //view.setBackgroundColor(Color.RED);
                return MessagesViewHolder(footView)
            }
        }
    }

    /**
     * 数据的绑定显示
     *
     * @param holder   holder
     * @param position 索引
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ContactsViewHolder -> {
                holder.bind(position)
            }
            is GroupsViewHolder -> {
                holder.bind(position)
            }
            is MessagesViewHolder -> {
                holder.bind(position)
            }
        }
        holder.itemView.tag = position
    }

    /**
     * 进行判断是普通Item视图还是FootView视图
     *
     * @param position 索引
     * @return 条目ID
     */
    override fun getItemViewType(position: Int): Int {
        // 最后一个item设置为footerView
        return when {
            position < mList1.size -> TYPE_ITEM_CONTACTS
            position < mList1.size + mList2.size -> TYPE_ITEM_GROUPS
            else -> TYPE_ITEM_MESSAGES
        }
    }

    override fun getItemCount(): Int {
        return mList1.size + mList2.size + mList3.size
    }

    fun setSearchText(text: String) {
        this.searchText = text
    }

    inner class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAvatar: AvatarImageView = itemView.findViewById(R.id.iv_avatar)
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val tvMore: TextView = itemView.findViewById(R.id.tv_more)
        private val tvTitle: TextView = itemView.findViewById(R.id.title)
        private val tvPhone: TextView = itemView.findViewById(R.id.tv_phone)
        private val layoutContent: RelativeLayout = itemView.findViewById(R.id.layout_content)

        fun bind(position: Int) {
            tvTitle.visibility = if (position == 0) View.VISIBLE else View.GONE
//            tvMore.visibility = if (position == 2) View.VISIBLE else View.GONE
            val dataBean = mList1[position]
            val name = if (TextUtils.isEmpty(dataBean.alias)) dataBean.realName!! + "" else dataBean.alias
            val mobilePhone = dataBean.phone
            if (TextUtils.isEmpty(mobilePhone)) {
                tvPhone.visibility = View.GONE
            } else {
                //                contentHolder.tv_phone.setText(phone);
                EaseCommonUtils.initHighLight(context, tvPhone, searchText, mobilePhone)
                tvPhone.visibility = View.VISIBLE
            }
            EaseCommonUtils.initHighLight(context, tvName, searchText, name)
            //            contentHolder.tv_name.setText(name);
            AvatarUtils.setAvatarContent(context, name, dataBean.avatar, ivAvatar)

            layoutContent.setOnClickListener { callback.onItemClick(position, TYPE_ITEM_CONTACTS) }
            tvMore.setOnClickListener { callback.onMoreClick(TYPE_ITEM_CONTACTS) }
        }

    }

    //自定义的ViewHolder，持有每个user的的所有界面元素
    inner class GroupsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val ivAvatar: ImageView = itemView.findViewById(R.id.iv_avatar)
        private val tvMore: TextView = itemView.findViewById(R.id.tv_more)
        private val tvTitle: TextView = itemView.findViewById(R.id.title)
        private val layoutContent: RelativeLayout = itemView.findViewById(R.id.layout_content)

        fun bind(position: Int) {
            if (position < mList1.size) {
                return
            }
            val realPos = position - mList1.size
            tvTitle.visibility = if (realPos == 0) View.VISIBLE else View.GONE
//            tvMore.visibility = if (realPos == 2) View.VISIBLE else View.GONE
            if (realPos >= mList2.size) {
                return
            }
            val searchGroupBean = mList2[realPos]
            EaseCommonUtils.initHighLight(context, tvName, searchText, searchGroupBean.name)
            //            contentHolder.tv_name.setText(searchGroupBean.getNick());
            val remoteUrl = searchGroupBean.avatar
            GlideUtils.load(context, remoteUrl, R.drawable.ease_group_icon, ivAvatar)
            layoutContent.setOnClickListener { callback.onItemClick(realPos, TYPE_ITEM_GROUPS) }
            tvMore.setOnClickListener { callback.onMoreClick(TYPE_ITEM_GROUPS) }
        }

    }

    //自定义的ViewHolder，持有每个message的的所有界面元素
    inner class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val tvContent: TextView = itemView.findViewById(R.id.tv_content)
        private val ivAvatar: AvatarImageView = itemView.findViewById(R.id.iv_avatar)
        private val tvMore: TextView = itemView.findViewById(R.id.tv_more)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val layoutContent: LinearLayout = itemView.findViewById(R.id.layout_content)

        fun bind(position: Int) {
            val realPos = position - mList1.size - mList2.size
            title.visibility = if (realPos == 0) View.VISIBLE else View.GONE
//            tvMore.visibility = if (realPos == 2) View.VISIBLE else View.GONE
            val searchMessagesBean = mList3[realPos]
            val username: String?
            if (searchMessagesBean.chatType == "chat") {
                username = if (searchMessagesBean.fromId == EMClient.getInstance().currentUser) {
                    searchMessagesBean.toName
                } else {
                    searchMessagesBean.fromName
                }
                AvatarUtils.setAvatarContent(context, username, if (searchMessagesBean.fromId == EMClient.getInstance().currentUser) searchMessagesBean.toAvatar else searchMessagesBean.fromAvatar, ivAvatar)
            } else {
                username = searchMessagesBean.groupName
                val remoteUrl = searchMessagesBean.groupAvatar
                GlideUtils.load(context, remoteUrl, R.drawable.ease_group_icon, ivAvatar)
            }

            EaseCommonUtils.initHighLight(context, tvName, searchText, username)
            //            contentHolder.tv_name.setText(name);
            //            contentHolder.tv_content.setText(searchMessagesBean.content);
            EaseCommonUtils.initHighLight(context, tvContent, searchText, searchMessagesBean.msg)
            layoutContent.setOnClickListener { callback.onItemClick(realPos, TYPE_ITEM_MESSAGES) }
            tvMore.setOnClickListener { callback.onMoreClick(TYPE_ITEM_MESSAGES) }
        }

    }

    //type 0,1,2 联系人，群组，聊天记录
    interface SearchAllItemCallback {

        fun onItemClick(position: Int, type: Int)

        fun onMoreClick(type: Int)
    }
}