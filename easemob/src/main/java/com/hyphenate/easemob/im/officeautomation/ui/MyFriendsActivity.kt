package com.hyphenate.easemob.im.officeautomation.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.easemob.im.officeautomation.ui.ContactDetailsActivity
import com.hyphenate.easemob.easeui.domain.MPGroupEntity
import com.hyphenate.eventbus.Subscribe
import com.hyphenate.eventbus.ThreadMode
import com.hyphenate.easemob.im.mp.AppHelper
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack
import com.hyphenate.easemob.imlibs.mp.events.EventOnLineOffLineNotify
import com.hyphenate.easemob.imlibs.mp.events.EventOnLineOffLineQuery
import com.hyphenate.easemob.imlibs.mp.utils.MPLog
import com.hyphenate.easemob.R
import com.hyphenate.easemob.im.officeautomation.adapter.StarredRefreshFooterAdapter
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager
import com.hyphenate.easemob.im.officeautomation.utils.Constant
import com.hyphenate.easemob.im.officeautomation.widget.SimpleDividerItemDecoration
import org.json.JSONObject
import java.util.*

class MyFriendsActivity : BaseActivity() {

    private var TAG = "MyFriendsActivity"

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var submit: TextView

    private var page = 0
    private var isLastPage: Boolean = false
    private var lastVisibleItem: Int = 0
    private lateinit var myFriendsList: MutableList<MPUserEntity>
    private lateinit var mpUserEntity: MPUserEntity
    private var isPick = false
    //群组新添加的成员
    private val pickList = mutableListOf<Int>()
    //已创建的群组包含的成员。不可选
    private var pickedList: MutableList<Int> = mutableListOf()
    private var groupEntity: MPGroupEntity? = null
    private var isCard = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_friends)

        isPick = intent.getBooleanExtra("isPick", false)
        isCard = intent.getBooleanExtra("isCard", false)
        if (isCard) isPick = false
        intent.getIntegerArrayListExtra("pickedList")?.let {
            pickedList.addAll(it)
        }
        intent.getIntegerArrayListExtra("pickList")?.let {
            pickList.addAll(it)
        }

        groupEntity = intent.getParcelableExtra("groupEntity")!!

        initView()
    }

    fun initView() {
        `$`<ImageView>(R.id.iv_back).setOnClickListener {
            onBackPressed()
        }

        swipeRefreshLayout = `$`(R.id.srl)
        recyclerView = `$`(R.id.rv)
        submit = `$`(R.id.tv_app_save)
        if (isPick) {
            submit.visibility = View.VISIBLE
        }
        submit.text = String.format(resources.getString(R.string.select_someone), pickList.size)

        recyclerView.apply {
            val linearLayout = LinearLayoutManager(this@MyFriendsActivity)
            layoutManager = linearLayout
            addItemDecoration(SimpleDividerItemDecoration(this@MyFriendsActivity))
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    //不是正在刷新、列表滑动为空闲状态、最下面显示的为最后一条、当前页不是最后一页
                    if (!swipeRefreshLayout.isRefreshing && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == recyclerView.adapter!!.itemCount && !isLastPage) {
                        (recyclerView.adapter as StarredRefreshFooterAdapter).changeMoreStatus(StarredRefreshFooterAdapter.LOADING_MORE)
                        page++
                        getMyfriends()
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

        myFriendsList = mutableListOf()
        if (recyclerView.adapter == null) {
            recyclerView.adapter = StarredRefreshFooterAdapter(isPick, isCard, this@MyFriendsActivity, myFriendsList, object : StarredRefreshFooterAdapter.StarredItemCallback {
                override fun onUserClick(position: Int) {
                    mpUserEntity = myFriendsList[position]
                    startActivityForResult(Intent(this@MyFriendsActivity, ContactDetailsActivity::class.java)
                            .putExtra("userId", mpUserEntity.id), 1000)
                }

                override fun onUserPick(checkBox: CheckBox, position: Int) {
                    mpUserEntity = myFriendsList[position]
                    if (isCard) {
                        val intent = Intent()
                        intent.putExtra("card", mpUserEntity)
                        setResult(3000, intent)
                        finish()
                    } else {
                        if (checkBox.isChecked) {
                            checkBox.isChecked = false
                            pickList.remove(mpUserEntity.id)
                        } else {
                            checkBox.isChecked = true
                            if (!pickList.contains(mpUserEntity.id)) {
                                pickList.add(mpUserEntity.id)
                            }
                        }
                        submit.text = String.format(resources.getString(R.string.select_someone), pickList.size)
                    }
                }

            })

            getMyfriends()
        }

        swipeRefreshLayout.isRefreshing = true
        swipeRefreshLayout.setOnRefreshListener {
            page = 0
            getMyfriends()
        }


        submit.setOnClickListener {
            if (groupEntity == null) {
                val intent = Intent()
                intent.putExtra("pickList", pickList as ArrayList<Int>?)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                addMembersToGroup(pickList as ArrayList<Int>?, groupEntity)
            }

        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {

            val isFriend = data?.extras?.getInt("isFriend")
            if (isFriend == 0) {
                myFriendsList.remove(mpUserEntity)
                recyclerView.adapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putIntegerArrayListExtra("pickList", pickList as ArrayList<Int>?)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun getMyfriends() {
        EMAPIManager.getInstance().getMyFriends(page, Constant.PAGE_SIZE, object : EMDataCallBack<String>() {
            override fun onSuccess(value: String) {
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    MPLog.d(TAG, value)
                    val result = JSONObject(value)
                    val mpUserEntityList = MPUserEntity.create(result.optJSONArray("entities"))

                    if (mpUserEntityList != null) {
                        val status = result.optString("status")
                        if ("OK".equals(status!!, ignoreCase = true)) {
                            val elements = result.optInt("numberOfElements")
                            isLastPage = result.optBoolean("last")
                            if (page == 0) {
                                myFriendsList.clear()
                            }
                            if (elements > 0) {
                                asyncFetchUserStatus(mpUserEntityList)
                                for (bean in mpUserEntityList) {
                                    AppHelper.getInstance().model.saveUserInfo(bean)
                                    if (isPick && pickList.size > 0 && pickList.contains(bean.id)) {
                                        bean.pickStatus = 1
                                    }
                                    if (pickedList.size > 0 && pickedList.contains(bean.id)) {
                                        bean.pickStatus = 2
                                    }
                                    myFriendsList.add(bean)
                                }
                            }

                            if (isLastPage)
                                (recyclerView.adapter as StarredRefreshFooterAdapter).changeMoreStatus(StarredRefreshFooterAdapter.LOADING_END)
                            else
                                (recyclerView.adapter as StarredRefreshFooterAdapter).changeMoreStatus(StarredRefreshFooterAdapter.LOADING_MORE)

                        } else {
                            toastInvalidResponse(TAG, "status = $status")
                        }
                    } else {
                        toastInvalidResponse(TAG, "mpUserEntity = null")
                    }
                }

            }

            override fun onError(error: Int, errorMsg: String) {
                MPLog.e(TAG, "error:$errorMsg")
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(applicationContext, R.string.request_failed_check_network, Toast.LENGTH_SHORT).show()
                }

            }
        })
    }

    fun asyncFetchUserStatus(mpUserEntityList : List<MPUserEntity>) {
        val userIds = ArrayList<String>()
        for (item in mpUserEntityList) {
            userIds.add(item.imUserId)
        }

//        MPLog.d(this@MyFriendsActivity.TAG, "userIds:" + userIds.toTypedArray().toString())
        EMClient.getInstance().chatManager().getUserStatusWithUserIds(userIds, object : EMCallBack {
            override fun onSuccess() {
                MPLog.i(this@MyFriendsActivity.TAG, "getUserStatusWithUserIds-success,$userIds")
            }

            override fun onError(i: Int, s: String) {
                MPLog.e(this@MyFriendsActivity.TAG, "getUserStatusWithUserIds-onError:$s,userIds:$userIds")
            }

            override fun onProgress(i: Int, s: String) {}
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventContactQuery(query: EventOnLineOffLineQuery) {
        recyclerView.adapter?.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventOnlineStatusNotity(notify: EventOnLineOffLineNotify?) {
        recyclerView.adapter?.notifyDataSetChanged()
    }
}
