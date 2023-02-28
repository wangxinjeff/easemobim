package com.hyphenate.easemob.im.officeautomation.ui.group

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easemob.easeui.utils.AvatarUtils
import com.hyphenate.easemob.easeui.widget.AvatarImageView
import com.hyphenate.easemob.im.mp.AppHelper
import com.hyphenate.easemob.R
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity
import com.hyphenate.easemob.im.officeautomation.listener.MyTextWatcher
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity
import java.util.ArrayList
import java.util.concurrent.Executors

class GroupSearchMemberActivity : BaseActivity() {

    private lateinit var btnOk: View
    private lateinit var mSearch: EditText
    private lateinit var mSearchClear: ImageButton
    private var searchText: String? = null
    private lateinit var recyclerView: RecyclerView
    private var searchList = mutableListOf<MPUserEntity>()
    private var pickList = mutableListOf<Int>()
    private val cacheThreadPool = Executors.newCachedThreadPool()
    private val pickedList = mutableListOf<Int>()
    private var isCard: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_member)


        intent.getIntegerArrayListExtra("pickList")?.let {
            pickList.addAll(it)
        }
        intent.getIntegerArrayListExtra("pickedList")?.let {
            pickedList.addAll(it)
        }

        isCard = intent.getBooleanExtra("isCard", false)

        btnOk = `$`(R.id.tv_ok)
        mSearch = `$`(R.id.search)
        mSearchClear = `$`(R.id.search_clear)
        recyclerView = `$`(R.id.recylerview)

        initView()
    }


    fun initView() {
        btnOk.setOnClickListener { onBackPressed() }

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        if (recyclerView.adapter == null)
            recyclerView.adapter = SearchAdapter()


        mSearch.addTextChangedListener(object : MyTextWatcher() {
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                super.onTextChanged(charSequence, i, i1, i2)
                if (charSequence.isNotEmpty()) {
                    mSearchClear.visibility = View.VISIBLE
                    val searchText = charSequence.toString()

                    searchUserList(searchText)
                } else {
                    mSearchClear.visibility = View.INVISIBLE
                    searchList.clear()
                    recyclerView.adapter?.notifyDataSetChanged()
                }
            }
        })
        mSearchClear.setOnClickListener {
            mSearch.text.clear()
            searchText = ""
            hideSoftKeyboard()
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putIntegerArrayListExtra("pickList", pickList as ArrayList<Int>?)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    fun searchUserList(content: String) {
        searchList.clear()
        cacheThreadPool.execute {
            val usersList = AppHelper.getInstance().model.searchUsersByKeyword(content)
            if (usersList != null && usersList.isNotEmpty()) {
                for (user in usersList) {
                    when {
                        pickedList.contains(user.id) -> user.pickStatus = 2
                        pickList.contains(user.id) -> user.pickStatus = 1
                        else -> user.pickStatus = 0
                    }
                    searchList.add(user)
                }
                runOnUiThread {
                    recyclerView.adapter?.notifyDataSetChanged()
                }

            }
        }

    }


    inner class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {

            val view = layoutInflater.inflate(R.layout.item_department_list_user, parent, false)
            return SearchViewHolder(view)
        }

        override fun getItemCount(): Int {
            return searchList.size
        }

        override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {

            holder.bind(searchList[position])
        }

        inner class SearchViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            private val name = view.findViewById<TextView>(R.id.tv_name)
            private val avatar = view.findViewById<AvatarImageView>(R.id.iv_avatar)
            private val pick = view.findViewById<CheckBox>(R.id.cb_pick)
            fun bind(mpUserEntity: MPUserEntity) {

                name.text = mpUserEntity.realName
                AvatarUtils.setAvatarContent(this@GroupSearchMemberActivity, if (TextUtils.isEmpty(mpUserEntity.realName)) mpUserEntity.username else mpUserEntity.realName, mpUserEntity.avatar, avatar)

                if (!isCard) {
                    pick.visibility = View.VISIBLE
                    pick.isClickable = false
                    when {
                        mpUserEntity.pickStatus == 0 -> {
                            pick.isChecked = false
                            view.isEnabled = true
                        }
                        mpUserEntity.pickStatus == 1 -> {
                            pick.isChecked = true
                            view.isEnabled = true
                        }
                        mpUserEntity.pickStatus == 2 -> {
                            pick.isChecked = true
                            view.isEnabled = false
                            pick.isEnabled = false
                        }
                    }

                    view.setOnClickListener {
                        if (pick.isChecked) {
                            pickList.remove(mpUserEntity.id)
                            pick.isChecked = false
                        } else {
                            if (!pickList.contains(mpUserEntity.id)) {
                                pickList.add(mpUserEntity.id)
                            }
                            pick.isChecked = true
                        }
                    }
                } else {
                    view.setOnClickListener {
                        val intent = Intent()
                        intent.putExtra("card", mpUserEntity)
                        setResult(3000, intent)
                        finish()
                    }
                }
            }
        }
    }
}
