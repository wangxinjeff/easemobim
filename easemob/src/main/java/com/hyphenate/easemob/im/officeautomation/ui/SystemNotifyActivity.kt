package com.hyphenate.easemob.im.officeautomation.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody
import com.hyphenate.easemob.easeui.EaseConstant
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils
import com.hyphenate.easemob.easeui.widget.EaseTitleBar
import com.hyphenate.easemob.easeui.widget.MPClickableSpan
import com.hyphenate.eventbus.Subscribe
import com.hyphenate.eventbus.ThreadMode
import com.hyphenate.easemob.imlibs.mp.events.EventEMMessageReceived
import com.hyphenate.easemob.R
import com.hyphenate.util.DateUtils
import java.util.*
import java.util.concurrent.Executors

class SystemNotifyActivity : BaseActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var titleBar: EaseTitleBar

    private lateinit var list: MutableList<EMMessage>
    private lateinit var conversation: EMConversation
    private val executorService = Executors.newCachedThreadPool()
    private var mId: String? = null
    private var haveMore: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_history)

        refreshWaterMark(this)

        titleBar = findViewById(R.id.title_bar)
        titleBar.setLeftLayoutClickListener {
            finish()
        }
        titleBar.title = "系统消息"
        rv = findViewById(R.id.rv)
        rv.visibility = View.GONE
        recyclerView = findViewById(R.id.recylerview)
        swipeRefreshLayout = findViewById(R.id.swipe_layout)
        swipeRefreshLayout.visibility = View.VISIBLE

        conversation = EMClient.getInstance().chatManager().getConversation("admin", EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true)
        conversation.markAllMessagesAsRead()

        list = mutableListOf()

        recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(this@SystemNotifyActivity)
            layoutManager = linearLayoutManager
//            addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                    super.onScrollStateChanged(recyclerView, newState)
//                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == recyclerView.adapter!!.itemCount && mId != null) {
//                        loadMsg()
//                    }
//                }
//
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
//                    lastVisibleItem = linearLayoutManager!!.findLastVisibleItemPosition()
//                }
//            })
        }

        swipeRefreshLayout.setOnRefreshListener {
            if (haveMore)
                loadMsg()
            else
                Toast.makeText(this@SystemNotifyActivity, "  没有更多数据", Toast.LENGTH_SHORT).show()
            swipeRefreshLayout.isRefreshing = false
        }

        recyclerView.adapter = SystemNotifyAdapter()


        loadMsg()

    }

    private fun loadMsg() {
        val progressDialog = ProgressDialog.show(this@SystemNotifyActivity, "加载中...", "", false)
        executorService.execute {
//            var msgList = mutableListOf<EMMessage>();
            val isFirstLoad = list.isEmpty()
            if (conversation.allMessages.size > 0) {
                mId = conversation.allMessages[0].msgId
                list.clear()
                list.addAll(conversation.allMessages)
            }
            val msgList = conversation.loadMoreMsgFromDB(mId, 10)
            if (msgList.size > 0) {
                mId = msgList[0].msgId
                list.clear()
                list.addAll(conversation.allMessages)
                haveMore = true
            } else {
                mId = null
                haveMore = false
            }

            list.sortWith(Comparator { o1, o2 ->
                if (o1!!.msgTime > o2!!.msgTime) {
                    1
                } else {
                    -1
                }
            })

            runOnUiThread {
                if (swipeRefreshLayout.isRefreshing) {
                    swipeRefreshLayout.isRefreshing = false
                }
                progressDialog.dismiss()
                recyclerView.adapter?.notifyDataSetChanged()
                if(isFirstLoad) {
                    recyclerView.scrollToPosition(list.size - 1)
                } else {
                    if (msgList.size > 0) {
                        recyclerView.scrollToPosition(msgList.size - 1)
                    }
                }
            }
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNotifyChanged(messageReceived: EventEMMessageReceived) {
        list.add(messageReceived.message)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    inner class SystemNotifyAdapter : RecyclerView.Adapter<SystemNotifyAdapter.SystemNotifyViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SystemNotifyViewHolder {
            val view = layoutInflater.inflate(R.layout.item_chats_list_history, parent, false)
            return SystemNotifyViewHolder(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: SystemNotifyViewHolder, position: Int) {
            holder.bind(list[position])
        }

        inner class SystemNotifyViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

            val msg: TextView = view.findViewById(R.id.message)
            val time: TextView = view.findViewById(R.id.time)
            val avatar: ImageView = view.findViewById(R.id.iv_avatar)
            fun bind(message: EMMessage) {
                var content = (message.body as EMTextMessageBody).message
                if (message.getBooleanAttribute(EaseConstant.EXT_WITH_BUTTON, false)) {
                    val event = message.getStringAttribute(EaseConstant.EXT_WITH_BUTTON_EVENT, "")
                    val extra = message.getLongAttribute(EaseConstant.EXT_WITH_BUTTON_EVENT_EXTRA, 0)
                    val forwardString = "去查看"
                    val clickSpan = MPClickableSpan("oa_forward_meeting"
                    ) {

                        finish()
                    }
                    val spanString = SpannableStringBuilder(content)
                    spanString.setSpan(
                        clickSpan,
                        content.length - forwardString.length,
                        content.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    msg.setText(spanString, TextView.BufferType.SPANNABLE);
                    msg.movementMethod = LinkMovementMethod.getInstance()
                } else {
                    msg.text = content;
                }
                time.text = DateUtils.getTimestampString(Date(message.msgTime))
                avatar.setImageResource(R.drawable.system_notify_icon)
            }
        }

    }
}