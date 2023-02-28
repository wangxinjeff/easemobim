package com.hyphenate.easemob.im.officeautomation.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easemob.easeui.domain.MPGroupEntity
import com.hyphenate.easemob.easeui.ui.EaseBaseFragment
import com.hyphenate.easemob.imlibs.mp.MPClient
import com.hyphenate.easemob.R
import com.hyphenate.easemob.im.officeautomation.adapter.SearchAdapterV2
import com.hyphenate.easemob.im.officeautomation.domain.*
import com.hyphenate.easemob.im.officeautomation.ui.ChatActivity
import com.hyphenate.easemob.im.officeautomation.ui.ChatHistoryCurrActivity
import com.hyphenate.easemob.im.officeautomation.ui.ContactDetailsActivity
import com.hyphenate.easemob.im.officeautomation.ui.SearchActivity
import com.hyphenate.easemob.im.officeautomation.utils.Constant
import com.hyphenate.easemob.im.officeautomation.widget.SimpleDividerItemDecoration
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity
import com.hyphenate.util.HanziToPinyin
import java.util.regex.Pattern

class SearchAllFragmentV2 : EaseBaseFragment() {

    private lateinit var recyclerView: RecyclerView
    private var searchText: String? = null

    private var searchContactList: MutableList<MPUserEntity> = mutableListOf()
    private var searchGroupList: MutableList<MPGroupEntity> = mutableListOf()
    private var searchConversationList: MutableList<SearchConversation> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_all, container, false)
    }

    override fun initView() {
        recyclerView = view!!.findViewById(R.id.rv)
    }

    override fun setUpView() {
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(SimpleDividerItemDecoration(activity))


        recyclerView.adapter = SearchAdapterV2(activity!!, searchContactList, searchGroupList, searchConversationList, Callback())
        if (!TextUtils.isEmpty(searchText)) {
            (recyclerView.adapter as SearchAdapterV2).setSearchText(searchText!!)
        }
    }


    //刷新数据
    fun refresh(searchText: String?, groupList: MutableList<MPGroupEntity>?, contactList: MutableList<MPUserEntity>?, msgList: MutableList<SearchConversation>?) {
        this.searchText = searchText
        if (activity == null) {
            return
        }
        if (!TextUtils.isEmpty(searchText)) {
            (recyclerView.adapter as SearchAdapterV2).setSearchText(searchText!!)

            if (groupList != null) {
                searchGroupList.run {
                    clear()
                    addAll(groupList)
                }
            }

            if (contactList != null) {
                searchContactList.run {
                    clear()
                    addAll(contactList)
                }
            }

            if (msgList != null) {
                searchConversationList.run {
                    clear()
                    addAll(msgList)
                }
            }

        }

        if (recyclerView.adapter != null)
            recyclerView.adapter!!.notifyDataSetChanged()
    }


    inner class Callback : SearchAdapterV2.SearchAllItemCallback {
        override fun onItemClick(position: Int, type: Int) {
            when (type) {
                0 -> {
                    val entitiesBean = searchContactList[position]
                    if(MPClient.get().isFileHelper(entitiesBean.imUserId)){
                        startActivity(Intent(activity, ChatActivity::class.java).putExtra("userId", MPClient.get().pcTarget))
                    }else{
                        startActivity(Intent(activity, ContactDetailsActivity::class.java).putExtra("userId", entitiesBean.id))
                    }
                }
                1 -> {
                    //获取点击的群组信息
                    val groupBean = searchGroupList[position]
                    // enter group chat
                    val intent = Intent(activity, ChatActivity::class.java)
                    // it is group chat
                    intent.putExtra("chatType", Constant.CHATTYPE_GROUP)
                    intent.putExtra("userId", groupBean.imChatGroupId)
                    startActivity(intent)
                }
                2 -> {
                    val searchConversationBean = searchConversationList[position]
                    val intent = Intent(activity, ChatHistoryCurrActivity::class.java)
                    intent.putExtra("bean", searchConversationBean)
                    intent.putExtra("searchText", searchText)
                    startActivity(intent)

                }
            }
        }

        override fun onMoreClick(type: Int) {
            if (activity == null) {
                return
            }
            (activity as SearchActivity).onMoreClick(type)
        }
    }

    private fun containSearchText(): Boolean {
        val p = Pattern.compile("[\u4e00-\u9fa5]")
        val m = p.matcher(searchText!!)
        return when {
            m.find() && resources.getString(R.string.file_transfer).contains(searchText!!) -> true
            else -> {
                val letters = HanziToPinyin.getInstance().get(resources.getString(R.string.file_transfer))
                val buffer = StringBuilder()
                for (token in letters) {
                    buffer.append(token.target.toLowerCase())
                }
                buffer.toString().contains(searchText!!)
            }
        }
    }

}