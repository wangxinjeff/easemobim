package com.hyphenate.easemob.im.officeautomation.ui

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.hyphenate.chat.EMCmdMessageBody
import com.hyphenate.easemob.im.officeautomation.ui.ContactDetailsActivity
import com.hyphenate.easemob.easeui.EaseUI
import com.hyphenate.eventbus.Subscribe
import com.hyphenate.eventbus.ThreadMode
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack
import com.hyphenate.easemob.imlibs.mp.events.EventFriendNotify
import com.hyphenate.easemob.R
import com.hyphenate.easemob.im.officeautomation.adapter.NewFriendsAdapter
import com.hyphenate.easemob.im.officeautomation.db.InviteMessageDao
import com.hyphenate.easemob.im.officeautomation.domain.InviteMessage
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager
import com.hyphenate.easemob.im.officeautomation.utils.Constant
import org.json.JSONObject
import java.lang.Exception

class NewFriendsActivity : BaseActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var stringBuffer: StringBuffer
    private lateinit var inviteMsgList: MutableList<InviteMessage>
    private var list: MutableList<InviteMessage> = mutableListOf()

    private var page = 0
    private var size = 20

    private lateinit var dao: InviteMessageDao

    private lateinit var button: TextView
    private lateinit var inviteMessage: InviteMessage
    private var lastVisibleItem: Int = 0
    private var isLastPage: Boolean = false

    //邀请通知 群组/好友
    private lateinit var notifyType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_friends)

        notifyType = intent.getStringExtra("notifyType")!!
        initViews()
        initData()
        if ("friend" == notifyType) {
            getUserAllNotify()
        } else {
            getGroupAllNotify()
        }
    }

    private fun initViews() {
        `$`<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        recyclerView = `$`(R.id.recycler_view)
        swipeRefreshLayout = `$`(R.id.swipe_layout)
        val title = `$`<TextView>(R.id.tv_title)

        if ("friend" == notifyType) {
            title.text = "好友申请"
        } else {
            title.text = "群组申请"
        }


        recyclerView.apply {
            val linearLayout = LinearLayoutManager(this@NewFriendsActivity)
            layoutManager = linearLayout


            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    //不是正在刷新、列表滑动为空闲状态、最下面显示的为最后一条、当前页不是最后一页
                    if (!swipeRefreshLayout.isRefreshing && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == recyclerView.adapter!!.itemCount && !isLastPage) {
                        (recyclerView.adapter as NewFriendsAdapter).changeMoreStatus(NewFriendsAdapter.LOADING_MORE)
                        page++
                        if ("friend" == notifyType) {
                            getUserAllNotify()
                        } else {
                            getGroupAllNotify()
                        }
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    //获取列表最下面一条的索引
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    lastVisibleItem = linearLayoutManager!!.findLastVisibleItemPosition()
                }
            })
        }


        swipeRefreshLayout.setOnRefreshListener {
            page = 0
            list.clear()
            if ("friend" == notifyType) {
                getUserAllNotify()
            } else {
                getGroupAllNotify()
            }
        }
    }


    private fun initData() {
        dao = InviteMessageDao()
        recyclerView.adapter = NewFriendsAdapter(this@NewFriendsActivity, list)

        if ("group" == notifyType) return
        asyncLoadFromDB()
        (recyclerView.adapter as NewFriendsAdapter).setClickListener { agreeBtn, position ->
            this.button = agreeBtn
            inviteMessage = list[position]
            startActivityForResult(Intent(this@NewFriendsActivity, ContactDetailsActivity::class.java).putExtra("imUserId", list[position].from), 1000)
        }
    }

    private fun asyncLoadFromDB() {
        EaseUI.getInstance().execute {
            //获取本地存储的好友邀请列表
            inviteMsgList = dao.messagesList
            list.clear()
            list.addAll(inviteMsgList)
            runOnUiThread {
                (recyclerView.adapter as NewFriendsAdapter).notifyDataSetChanged()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {

            val isFriend = data?.extras?.getInt("isFriend")
            if (isFriend == 1) {
                button.text = resources.getString(R.string.Has_agreed_to)
                button.setTextColor(Color.GRAY)
                button.background = null
                button.isEnabled = false

                inviteMessage.status = InviteMessage.InviteMessageStatus.BEAGREED
                val values = ContentValues()
                values.put(InviteMessageDao.COLUMN_NAME_STATUS, inviteMessage.status!!.ordinal)
                dao.updateMessage(inviteMessage.userId, values)
            }


        }
    }

    private fun getUserAllNotify() {
        EMAPIManager.getInstance().getUserAllNotify(page, size, object : EMDataCallBack<String>() {
            override fun onSuccess(value: String?) {
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    try {
                        val jsonObj = JSONObject(value)
                        isLastPage = jsonObj.optBoolean("last")
                        if (isLastPage)
                            (recyclerView.adapter as NewFriendsAdapter).changeMoreStatus(NewFriendsAdapter.LOADING_END)
                        else
                            (recyclerView.adapter as NewFriendsAdapter).changeMoreStatus(NewFriendsAdapter.LOADING_MORE)
                        val jsonArr = jsonObj.optJSONArray("entities")

                        if (page == 0) list.clear()

                        val mpUserEntities = MPUserEntity.createWithFriend(jsonArr)

                        val inviteMessageDao = InviteMessageDao()
                        for (mpEntitiesBean in mpUserEntities) {
                            println("mp:${mpEntitiesBean.realName}")
                            val inviteMessage = InviteMessage()
                            inviteMessage.userId = mpEntitiesBean.userId
                            inviteMessage.friendId = mpEntitiesBean.friendId
                            inviteMessage.realName = mpEntitiesBean.realName
                            inviteMessage.from = mpEntitiesBean.imUserId
                            if (mpEntitiesBean.status == 1) {
                                inviteMessage.status = InviteMessage.InviteMessageStatus.BEINVITEED
                            } else {
                                inviteMessage.status = InviteMessage.InviteMessageStatus.BEAGREED
                            }
                            inviteMessageDao.saveMessage(inviteMessage)

                            list.add(inviteMessage)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    (recyclerView.adapter as NewFriendsAdapter).notifyDataSetChanged()
                }

            }

            override fun onError(error: Int, errorMsg: String?) {
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(this@NewFriendsActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getGroupAllNotify() {
        val params = "page=$page&size=$size";
        EMAPIManager.getInstance().getGroupAllNotify(params, object : EMDataCallBack<String>() {
            override fun onSuccess(value: String?) {
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    try {
                        val jsonObj = JSONObject(value)
                        isLastPage = jsonObj.optBoolean("last")
                        if (isLastPage)
                            (recyclerView.adapter as NewFriendsAdapter).changeMoreStatus(NewFriendsAdapter.LOADING_END)
                        else
                            (recyclerView.adapter as NewFriendsAdapter).changeMoreStatus(NewFriendsAdapter.LOADING_MORE)

                        val jsonArr = jsonObj.optJSONArray("entities")
                        if (page == 0) list.clear()

                        val mpUserEntities = Gson().fromJson<Array<GroupInviteNotifyEntity>>(jsonArr.toString(), Array<GroupInviteNotifyEntity>::class.java)
                        val inviteMessageDao = InviteMessageDao()
                        for (mpEntitiesBean in mpUserEntities) {
                            val inviteMessage = InviteMessage()
                            inviteMessage.userId = mpEntitiesBean.userId
                            inviteMessage.chatGroupId = mpEntitiesBean.chatGroupId
                            inviteMessage.cluster = mpEntitiesBean.isRegion;
                            when {
                                mpEntitiesBean.approve == 0 -> inviteMessage.status = InviteMessage.InviteMessageStatus.BEAPPLYED
                                mpEntitiesBean.approve == 1 -> inviteMessage.status = InviteMessage.InviteMessageStatus.AGREED
                                mpEntitiesBean.approve == 2 -> inviteMessage.status = InviteMessage.InviteMessageStatus.REFUSED
                            }
                            inviteMessageDao.saveMessage(inviteMessage)

                            list.add(inviteMessage)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    (recyclerView.adapter as NewFriendsAdapter).notifyDataSetChanged()
                }

            }

            override fun onError(error: Int, errorMsg: String?) {
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(this@NewFriendsActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun friendNotification(eventFriendNotify: EventFriendNotify) {
        val message = eventFriendNotify.message
        val cmdMsgBody = message.body as EMCmdMessageBody
        when (cmdMsgBody.action()) {
            Constant.CMD_ACTION_INVITED_FRIEND -> initData()
        }
    }
}

data class GroupInviteNotifyEntity(
        val approve: Int,
        val block: Boolean,
        val chatGroupId: Int,
        val contract: Boolean,
        val createTime: Long,
        val createUserId: Int,
        val disturb: Boolean,
        val id: Int,
        val lastUpdateTime: Long,
        val lastUpdateUserId: Int,
        val mute: Boolean,
        val tenantId: Int,
        val type: String,
        val userId: Int,
        val isRegion: Boolean
)


data class GroupInviteUserEntity(
        val approve: Int,
        val block: Boolean,
        val chatGroupId: Int,
        val contract: Boolean,
        val createTime: Long,
        val createUserId: Int,
        val disturb: Boolean,
        val id: Int,
        val lastUpdateTime: Long,
        val lastUpdateUserId: Int,
        val mute: Boolean,
        val tenantId: Int,
        val type: String,
        val userId: Int,
        val isRegion: Boolean
)