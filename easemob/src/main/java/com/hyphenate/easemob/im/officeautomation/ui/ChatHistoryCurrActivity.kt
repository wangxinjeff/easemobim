package com.hyphenate.easemob.im.officeautomation.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hyphenate.chat.EMClient
import com.hyphenate.easemob.easeui.utils.AvatarUtils
import com.hyphenate.easemob.easeui.widget.AvatarImageView
import com.hyphenate.easemob.easeui.widget.EaseTitleBar
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack
import com.hyphenate.easemob.R
import com.hyphenate.easemob.im.officeautomation.domain.MsgEntity
import com.hyphenate.easemob.im.officeautomation.domain.SearchConversation
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager
import com.hyphenate.easemob.im.officeautomation.utils.Constant
import org.json.JSONObject

class ChatHistoryCurrActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var searchConversation: SearchConversation
    private var toChatId: String? = null
    private lateinit var searchText: String
    private lateinit var msgList: MutableList<MsgEntity>
    private lateinit var titleBar: EaseTitleBar
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_history)


        recyclerView = findViewById(R.id.rv)
        titleBar = findViewById(R.id.title_bar);
        titleBar.setLeftLayoutClickListener {
            finish()
        }

        searchText = intent.getStringExtra("searchText")!!

        searchConversation = intent.getParcelableExtra("bean")!!
        if ("chat" == searchConversation.chatType) {
            titleBar.title = "单聊消息"
            toChatId = if (searchConversation.fromId == EMClient.getInstance().currentUser) {
                searchConversation.toId
            } else {
                searchConversation.fromId
            }
        } else {
            titleBar.title = "群聊消息"
        }
        msgList = mutableListOf()

        recyclerView.apply {
            val layoutManager = LinearLayoutManager(this@ChatHistoryCurrActivity)
            setLayoutManager(layoutManager)
        }

        request()
    }


    fun request() {
        progressDialog = ProgressDialog(this)
        val beginTime = System.currentTimeMillis() - 90 * 24 * 60 * 60 * 1000L
        val endTime = System.currentTimeMillis()
        val queryType = searchConversation.chatType
        val msgType = "all"
        val words = searchText
        EMAPIManager.getInstance().getSearchMsg(beginTime, endTime, searchConversation.fromId, searchConversation.toId, queryType, 1, 200, msgType, words, true, false, object : EMDataCallBack<String>() {
            override fun onSuccess(value: String?) {
                val result = JSONObject(value)
                val entity = result.getJSONObject("entity")
                val msgs = entity.getJSONArray("entities")
                for (i in 0 until msgs.length()) {
                    val msg = Gson().fromJson<MsgEntity>(msgs[i].toString(), MsgEntity::class.java)
                    msgList.add(msg)
                }

                runOnUiThread {
                    progressDialog.dismiss()
                    recyclerView.adapter = ChatHistoryContextAdapter()
                }

            }

            override fun onError(error: Int, errorMsg: String?) {
                runOnUiThread {
                    progressDialog.dismiss()
                }
            }

        })
    }

    inner class ChatHistoryContextAdapter : RecyclerView.Adapter<ChatHistoryContextAdapter.ChatHistoryCurrViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHistoryCurrViewHolder {
            val view = layoutInflater.inflate(R.layout.history_context_item, parent, false)
            return ChatHistoryCurrViewHolder(view)
        }

        override fun getItemCount(): Int {
            return msgList.size
        }

        override fun onBindViewHolder(holder: ChatHistoryCurrViewHolder, position: Int) {
            holder.bind(msgList[position])
        }


        inner class ChatHistoryCurrViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
            private val avatar = view.findViewById<AvatarImageView>(R.id.iv_history_context_avatar)
            private val name = view.findViewById<TextView>(R.id.tv_history_context_name)
            private val content = view.findViewById<TextView>(R.id.tv__history_context_content)
            private val enterChat = view.findViewById<TextView>(R.id.tv_history_context_chat)
            private val context = view.findViewById<TextView>(R.id.tv_history_context_enter)
            fun bind(msgEntity: MsgEntity) {


                if ("groupchat" == searchConversation.chatType) {
                    name.text = "群名称:" + searchConversation.groupName
                    AvatarUtils.setAvatarContent(this@ChatHistoryCurrActivity, searchConversation.groupName, searchConversation.groupAvatar, avatar)
                } else {
                    if (msgEntity.from_id == searchConversation.fromId) {
                        name.text = searchConversation.fromName
                        AvatarUtils.setAvatarContent(this@ChatHistoryCurrActivity, searchConversation.fromName, searchConversation.fromAvatar, avatar)
                    }
                    if (msgEntity.from_id == searchConversation.toId) {
                        name.text = searchConversation.toName
                        AvatarUtils.setAvatarContent(this@ChatHistoryCurrActivity, searchConversation.toName, searchConversation.toAvatar, avatar)
                    }
                }

                content.text = msgEntity.msg

                context.setOnClickListener {

                    val intent = Intent(this@ChatHistoryCurrActivity, ChatHistoryContextActivity::class.java)
                    intent.putExtra("timeStamp", msgEntity.timestamp)
                    intent.putExtra("bean", searchConversation)
                    startActivity(intent)
                }

                enterChat.setOnClickListener {
                    var intent = Intent(this@ChatHistoryCurrActivity, ChatActivity::class.java)
                    if ("groupchat" != searchConversation.chatType) {
                        intent.putExtra("userId", toChatId)
                    } else {
                        intent.putExtra("userId", searchConversation.toId)
                        intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP)
                    }
                    startActivity(intent)
                }
            }
        }
    }
}