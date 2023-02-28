package com.hyphenate.easemob.im.officeautomation.ui.group

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.EMCallBack
import com.hyphenate.easemob.imlibs.cache.OnlineCache
import com.hyphenate.chat.EMClient
import com.hyphenate.easemob.easeui.domain.EaseUser
import com.hyphenate.easemob.easeui.utils.AvatarUtils
import com.hyphenate.easemob.easeui.widget.AvatarImageView
import com.hyphenate.eventbus.Subscribe
import com.hyphenate.eventbus.ThreadMode
import com.hyphenate.easemob.imlibs.mp.events.EventOnLineOffLineQuery
import com.hyphenate.easemob.R
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import java.util.ArrayList

class GroupMembersManagerActivity : BaseActivity() {

    private var TAG = "GroupMembersManagerActivity"
    private lateinit var back: ImageView
    private lateinit var recyclerView: RecyclerView
    private var dataList: MutableList<EaseUser> = mutableListOf()
    private var isDel: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_group_member_manager)

        val userList: MutableList<EaseUser> = intent.getParcelableArrayListExtra("userList")!!
        isDel = intent.getBooleanExtra("isDel", false)

        if (isDel) {
            userList.forEach {
                if (EMClient.getInstance().currentUser != it.username) {
                    dataList.add(it)
                }
            }
        } else {
            dataList.addAll(userList)
        }


        back = `$`(R.id.iv_back)
        back.setOnClickListener { onBackPressed() }

        recyclerView = `$`(R.id.recylerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (recyclerView.adapter == null) {
            recyclerView.adapter = MemberListAdapter()
        }
        fetchUserListStatus(dataList);
    }

    fun fetchUserListStatus(dataList : MutableList<EaseUser>) {
        var userIds = ArrayList<String>()
        dataList.forEach {
            var status = OnlineCache.getInstance().get(it.username)
            if(status == null) {
                userIds.add(it.username)
            }
        }

        if(userIds.isEmpty()) {
            return
        }
        EMClient.getInstance().chatManager().getUserStatusWithUserIds(userIds, object : EMCallBack {
            override fun onSuccess() {
                MPLog.i(this@GroupMembersManagerActivity.TAG, "getUserStatusWithUserIds-success,userIds:$userIds")
            }

            override fun onProgress(p0: Int, p1: String?) {
            }

            override fun onError(p0: Int, p1: String?) {
                MPLog.e(this@GroupMembersManagerActivity.TAG, "getUserStatusWithUserIds-onError,userIds:$userIds")
            }

        })
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onEventContactQuery(query: EventOnLineOffLineQuery) {
        recyclerView.adapter?.notifyDataSetChanged()
    }



    override fun onBackPressed() {
        val intent = Intent()
        intent.putParcelableArrayListExtra("userList", dataList as ArrayList<out Parcelable>)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    inner class MemberListAdapter : RecyclerView.Adapter<MemberListAdapter.MemberViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
            val view = layoutInflater.inflate(R.layout.item_group_member, parent, false)
            return MemberViewHolder(view)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
            holder.bind(dataList[position])
        }


        inner class MemberViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            private var avatar = view.findViewById<AvatarImageView>(R.id.iv_avatar)
            private var name = view.findViewById<TextView>(R.id.tv_name)
            private var delete = view.findViewById<ImageView>(R.id.iv_delete)
            private var owner = view.findViewById<TextView>(R.id.tv_owner)
            private var tvOnline = view.findViewById<TextView>(R.id.tv_online)

            fun bind(user: EaseUser) {

                name.text = user.nickname
                AvatarUtils.setAvatarContent(this@GroupMembersManagerActivity, if (TextUtils.isEmpty(user.nickname)) user.username else user.nickname, user.avatar, avatar)
                var status = OnlineCache.getInstance().get(user.username);
                if (status != null && status) {
                    tvOnline?.text = "[在线]"
                    tvOnline?.setTextColor(Color.GREEN)
                } else {
                    tvOnline?.text = "[离线]"
                    tvOnline?.setTextColor(Color.GRAY)
                }

                if (isDel) {

                    delete.visibility = View.VISIBLE
                    delete.setOnClickListener {
                        dataList.remove(user)
                        notifyDataSetChanged()
                    }
                } else {
                    if (EMClient.getInstance().currentUser == user.username) {
                        owner.visibility = View.VISIBLE
                        owner.text = resources.getString(R.string.label_group_owner)
                    } else {
                        owner.visibility = View.GONE
                    }
                }
            }
        }

    }


}
