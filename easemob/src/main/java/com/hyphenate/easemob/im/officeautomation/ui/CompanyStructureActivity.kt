package com.hyphenate.easemob.im.officeautomation.ui

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hyphenate.chat.EMClient
import com.hyphenate.easemob.im.officeautomation.ui.ContactDetailsActivity
import com.hyphenate.easemob.easeui.EaseUI
import com.hyphenate.easemob.easeui.utils.AvatarUtils
import com.hyphenate.easemob.easeui.widget.AvatarImageView
import com.hyphenate.easemob.im.gray.GrayScaleConfig
import com.hyphenate.easemob.im.mp.AppHelper
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack
import com.hyphenate.easemob.R
import com.hyphenate.easemob.im.officeautomation.adapter.CustomListAdapter
import com.hyphenate.easemob.im.officeautomation.domain.DepartmentBean
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager
import com.hyphenate.easemob.im.officeautomation.widget.CustomHorizontalScrollview
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.util.*

class CompanyStructureActivity : BaseActivity() {

    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var mCustomHsv: CustomHorizontalScrollview? = null
    private var recyclerView: RecyclerView? = null
    private var submit: TextView? = null

    private val page = 0
    private val size = 1000
    private var orgId: Int = -1
    private var companyId: Int = -1
    private var orgName: String? = null
    private var departmentBeanList = mutableListOf<DepartmentBean>()
    private var customListAdapter: CustomListAdapter? = null

    private val departmentList = mutableListOf<MPOrgEntity>()
    private val departmentUserList = mutableListOf<MPUserEntity>()

    private var loadingPopupView: LoadingPopupView? = null
    private var isLastPage = false
    private var isSchedule = false

    private var pickedUidList = mutableListOf<Int>()
    //部门id，公司id
    private var pickedOrgIdMap = mutableMapOf<Int, Int>()
    //<部门id,部门下用户id集合>
    private var allSubsUserMap = mutableMapOf<Int, MutableList<Int>>()

    private var isRefresh = true

    private var isCreate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_org_structure)

        orgName = intent.getStringExtra("orgName")
        if (intent.getBooleanExtra("myOrg", false))
            orgId = intent.getIntExtra("orgId", -1)
        companyId = intent.getIntExtra("companyId", -1)
        isSchedule = intent.getBooleanExtra("isSchedule", false)
        isCreate = intent.getBooleanExtra("isCreate", false)

        intent.getIntegerArrayListExtra("pickedUidList")?.let {
            pickedUidList.addAll(it)
        }

        intent.getSerializableExtra("pickedOrgIdMap")?.let {
            pickedOrgIdMap.putAll(it as MutableMap<Int, Int>)
        }


        mSwipeRefreshLayout = findViewById(R.id.srl)
        mCustomHsv = findViewById(R.id.custom_hsv)
        recyclerView = findViewById(R.id.rv)
        submit = findViewById(R.id.tv_submit)

        submit?.text = String.format(resources.getString(R.string.select_someone), pickedUidList.size)
        submit!!.visibility = View.VISIBLE
        submit!!.setOnClickListener {
            val intent = Intent()
            intent.putIntegerArrayListExtra("pickedUidList", pickedUidList as ArrayList<Int>)
            intent.putExtra("pickedOrgIdMap", pickedOrgIdMap as Serializable)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        `$`<View>(R.id.iv_back).setOnClickListener { onBackPressed() }

        recyclerView!!.apply {
            val linearLayout = LinearLayoutManager(this@CompanyStructureActivity)
            layoutManager = linearLayout
            adapter = CompanyStructureAdapter()
        }

        mSwipeRefreshLayout!!.setOnRefreshListener {
            isRefresh = true
            getDepartmentFromServer()
        }

        loadingPopupView = XPopup.Builder(this@CompanyStructureActivity).asLoading("加载中...")
        initView()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        if (customListAdapter?.count!! > 1) {
            refreshCustomListAdapter(customListAdapter!!.currentPosition - 1)
        } else {
            finish()
        }
    }

    private fun initView() {

        departmentBeanList = mutableListOf()
        val departmentBean = DepartmentBean(orgName, orgId)
        departmentBeanList.add(departmentBean)

        (`$`<View>(R.id.tv_title) as TextView).text = orgName

        //通讯录导航索引
        customListAdapter = CustomListAdapter(this, R.layout.item_contacts_list_title_custom, departmentBeanList) { position ->
            //如果点击的不是当前部门
            if (position != customListAdapter?.currentPosition) {
                //刷新列表，展示选中索引数据
                refreshCustomListAdapter(position)
            }
        }
        mCustomHsv!!.setAdapter(customListAdapter)

        getDataList()

        if (!pickedOrgIdMap.isNullOrEmpty()) {
            for ((key, value) in pickedOrgIdMap.entries) {
                if (!allSubsUserMap.keys.contains(key)) {
                    getMembersByOrgId(value, key)
                }
            }
        }
    }


    //点击索引 或 点击返回 切换数据展示
    private fun refreshCustomListAdapter(position: Int) {
        //获取点击的部门ID
        val departmentBean = departmentBeanList[position]
        orgId = departmentBean.id
        //截取索引列表
        val departmentBeans = departmentBeanList.subList(0, position + 1)
        val copyDepartmentIndexList = ArrayList(departmentBeans)
        departmentBeanList.clear()
        departmentBeanList.addAll(copyDepartmentIndexList)
        //刷新索引列表
        customListAdapter?.notifyDataSetChanged()
        mCustomHsv?.fillViewWithAdapter(customListAdapter)

        //点击部门索引，请求数据，刷新列表
        //        page = 0;
        departmentList.clear()
        departmentUserList.clear()
        recyclerView?.adapter?.notifyDataSetChanged()
        EaseUI.getInstance().execute { getDataList() }
    }


    private fun getDataList() {

        val orgsList = AppHelper.getInstance().model.getOrgsListByParent(orgId)
        if (orgsList != null && orgsList.isNotEmpty()) {
            departmentList.clear()
            departmentList.addAll(orgsList)

            for (mp in departmentList) {
                if (pickedOrgIdMap.keys.contains(mp.id)) {
                    mp.pickStatus = 1
                }
            }

            runOnUiThread {
                recyclerView?.adapter?.notifyDataSetChanged()
                mSwipeRefreshLayout?.isRefreshing = false
                if (!loadingPopupView!!.isShow) {
                    loadingPopupView!!.show()
                }
            }
            getDepartmentUser(orgId)
        } else {
            runOnUiThread {
                loadingPopupView?.show()
                getDepartmentFromServer()
            }
        }
    }


    private fun getDepartmentFromServer() {
        departmentList.clear()
        EMAPIManager.getInstance().getOrgInfoForSub(companyId, orgId, page, size, object : EMDataCallBack<String>() {
            override fun onSuccess(value: String) {
                if (isRefresh) {
                    getDepartmentUserFromServer()
                    isRefresh = false
                } else {
                    getDepartmentUser(orgId)
                }

                try {
                    val jsonObj = JSONObject(value)
                    val jsonEntities = jsonObj.optJSONArray("entities")
                    if (jsonEntities != null && jsonEntities.length() > 0) {
                        val orgEntities = MPOrgEntity.create(jsonEntities)
                        AppHelper.getInstance().model.saveOrgsListByParentOrgId(orgEntities)

                        if (orgEntities != null && orgEntities.isNotEmpty()) {
                            departmentList.addAll(orgEntities)
                        }

                        for (mp in departmentList) {
                            if (pickedOrgIdMap.keys.contains(mp.id)) {
                                mp.pickStatus = 1
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                runOnUiThread {
                    recyclerView?.adapter?.notifyDataSetChanged()
                    mSwipeRefreshLayout?.isRefreshing = false
                }
            }

            override fun onError(error: Int, errorMsg: String) {
                runOnUiThread {
                    loadingPopupView?.dismiss()
                }
            }
        })
    }

    private fun getDepartmentUser(orgId: Int) {

        val users = AppHelper.getInstance().model.getUsersByOrgId(orgId)

        if (users != null && users.isNotEmpty()) {
            departmentUserList.clear()
            departmentUserList.addAll(users)
            for (mp in departmentUserList) {
                if (pickedOrgIdMap.keys.contains(mp.id) || pickedUidList.contains(mp.id)) {
                    mp.pickStatus = 1
                }
            }
            runOnUiThread {
                recyclerView?.adapter?.notifyDataSetChanged()
                mSwipeRefreshLayout?.isRefreshing = false
                loadingPopupView?.dismiss()
            }
        } else {
            getDepartmentUserFromServer()
        }

    }


    private fun getDepartmentUserFromServer() {
        departmentUserList.clear()
        EMAPIManager.getInstance().getUsersByOrgId(companyId, orgId, page, size, object : EMDataCallBack<String>() {
            override fun onSuccess(value: String) {
                try {
                    val jsonObj = JSONObject(value)
                    val status = jsonObj.optString("status")
                    val elements = jsonObj.optInt("numberOfElements")
                    isLastPage = jsonObj.optBoolean("last")
                    val userList = MPUserEntity.create(jsonObj.optJSONArray("entities"))
                    AppHelper.getInstance().model.saveMPUserList(userList)
                    departmentUserList.addAll(userList)

                    for (mp in departmentUserList) {
                        if (pickedUidList.contains(mp.id)) {
                            mp.pickStatus = 1
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                runOnUiThread {
                    isLastPage = true
                    recyclerView?.adapter?.notifyDataSetChanged()
                    mSwipeRefreshLayout?.isRefreshing = false
                    loadingPopupView?.dismiss()
                }
            }

            override fun onError(error: Int, errorMsg: String) {
                runOnUiThread {
                    loadingPopupView?.dismiss()
                }
            }
        })
    }


    inner class CompanyStructureAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val TYPE_DEPART = 0
        private val TYPE_USER = 1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == TYPE_DEPART) {
                val view = layoutInflater.inflate(R.layout.item_department_list_catalog, parent, false)
                DepartmentViewHolder(view)
            } else {
                val view = layoutInflater.inflate(R.layout.item_department_list_user_right_check, parent, false)
                DepartmentUserViewHolder(view)
            }
        }

        override fun getItemCount(): Int {
            return departmentList.size + departmentUserList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is DepartmentViewHolder) {
                holder.bind(departmentList[position])
            } else if (holder is DepartmentUserViewHolder) {
                holder.bind(departmentUserList[position - departmentList.size])
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (departmentList.size >= position + 1) {
                TYPE_DEPART
            } else {
                TYPE_USER
            }
        }

        inner class DepartmentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            private val rl_department = view.findViewById<RelativeLayout>(R.id.rl_department)
            private val tv_department = view.findViewById<TextView>(R.id.tv_department)
            private val depart_divider = view.findViewById<View>(R.id.depart_divider)
            private val checkOrg = view.findViewById<CheckBox>(R.id.cb_all)
            private val departNum = view.findViewById<TextView>(R.id.tv_department_num)

            private val rightIcon = view.findViewById<ImageView>(R.id.iv_right)
            fun bind(entity: MPOrgEntity) {
                if (GrayScaleConfig.showOrgMemberCount) {
                    departNum?.text = "${entity.memberCount}"
//                    tv_department.text = String.format(resources.getString(R.string.org_name_number), entity.name, entity.memberCount)
                }
                tv_department.text = "  " + entity.name


                if (adapterPosition == departmentList.size - 1 && departmentUserList.size > 0) {
                    depart_divider.visibility = View.VISIBLE
                } else {
                    depart_divider.visibility = View.GONE
                }
                if (GrayScaleConfig.useOrgChecked) {
                    if (isSchedule) {
                        checkOrg.visibility = View.VISIBLE
                        rightIcon.setImageDrawable(resources.getDrawable(R.drawable.go_child_org_icon))
                        tv_department.setPadding(0, 10, 0, 10)
                        if (entity.pickStatus == 0) {
                            checkOrg.isChecked = false
                            rightIcon.isEnabled = true
                            rightIcon.isClickable = true
                            val wrappedDrawable = DrawableCompat.wrap(resources.getDrawable(R.drawable.go_child_org_icon))
                            DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(resources.getColor(R.color.theme_color)));
                            rightIcon.setImageDrawable(wrappedDrawable)
                        } else if (entity.pickStatus == 1) {
                            checkOrg.isChecked = true
                            rightIcon.isEnabled = false
                            rightIcon.isClickable = false
                            val wrappedDrawable = DrawableCompat.wrap(resources.getDrawable(R.drawable.go_child_org_icon))
                            DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(resources.getColor(R.color.gray)))
                            rightIcon.setImageDrawable(wrappedDrawable)
                        }

                        checkOrg.isClickable = false
                        tv_department.setOnClickListener {
                            if (checkOrg.isChecked) {
                                checkOrg.isChecked = false
                                entity.pickStatus = 0
                                if (pickedOrgIdMap.keys.contains(entity.id)) {
                                    pickedOrgIdMap.remove(entity.id)
                                    if (allSubsUserMap.containsKey(entity.id)) {
                                        for (id in allSubsUserMap[entity.id]!!) {
                                            if (pickedUidList.contains(id)) {
                                                pickedUidList.remove(id)
                                            }
                                        }
                                    }
                                }
                                submit?.text = String.format(resources.getString(R.string.select_someone), pickedUidList.size)
                            } else {
                                entity.pickStatus = 1
                                checkOrg.isChecked = true
                                if (!pickedOrgIdMap.keys.contains(entity.id)) {
                                    pickedOrgIdMap[entity.id] = entity.companyId
                                }
                                if (allSubsUserMap.containsKey(entity.id)) {
                                    allSubsUserMap[entity.id]!!.forEach {
                                        if (!pickedUidList.contains(it)) {
                                            pickedUidList.add(it)
                                        }
                                    }
                                    submit?.text = String.format(resources.getString(R.string.select_someone), pickedUidList.size)
                                } else {
                                    getMembersByOrgId(entity.companyId, entity.id)
                                }
                            }
                            notifyDataSetChanged()

                        }

                        rightIcon.setOnClickListener {
                            orgId = entity.id
                            //构建部门索引model,刷新部门索引
                            val departmentBean1 = DepartmentBean(entity.name, entity.id)
                            departmentBeanList.add(departmentBean1)
                            customListAdapter?.notifyDataSetChanged()
                            mCustomHsv?.fillViewWithAdapter(customListAdapter)
                            val timer = Timer()
                            timer.schedule(object : TimerTask() {
                                override fun run() {
                                    mCustomHsv?.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
                                }
                            }, 100
                            )
                            //请求部门数据，刷新列表
                            departmentList.clear()
                            departmentUserList.clear()
                            notifyDataSetChanged()
                            EaseUI.getInstance().execute { getDataList() }
                        }

                    } else {
                        rl_department.setOnClickListener {
                            //构建部门索引model,刷新部门索引
                            orgId = entity.id
                            val departmentBean = DepartmentBean(entity.name, entity.id)
                            departmentBeanList.add(departmentBean)
                            customListAdapter?.notifyDataSetChanged()
                            mCustomHsv?.fillViewWithAdapter(customListAdapter)
                            val timer = Timer()
                            timer.schedule(object : TimerTask() {
                                override fun run() {
                                    mCustomHsv?.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
                                }
                            }, 100
                            )
                            //请求部门数据，刷新列表
                            departmentList.clear()
                            departmentUserList.clear()
                            notifyDataSetChanged()
                            EaseUI.getInstance().execute { getDataList() }
                        }
                    }
                } else {
                    checkOrg.visibility = View.GONE
                    tv_department.setOnClickListener {
                        orgId = entity.id
                        //构建部门索引model,刷新部门索引
                        val departmentBean1 = DepartmentBean(entity.name, entity.id)
                        departmentBeanList.add(departmentBean1)
                        customListAdapter?.notifyDataSetChanged()
                        mCustomHsv?.fillViewWithAdapter(customListAdapter)
                        val timer = Timer()
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                mCustomHsv?.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
                            }
                        }, 100
                        )
                        //请求部门数据，刷新列表
                        departmentList.clear()
                        departmentUserList.clear()
                        notifyDataSetChanged()
                        EaseUI.getInstance().execute { getDataList() }
                    }
                    rightIcon.setOnClickListener {
                        orgId = entity.id
                        //构建部门索引model,刷新部门索引
                        val departmentBean1 = DepartmentBean(entity.name, entity.id)
                        departmentBeanList.add(departmentBean1)
                        customListAdapter?.notifyDataSetChanged()
                        mCustomHsv?.fillViewWithAdapter(customListAdapter)
                        val timer = Timer()
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                mCustomHsv?.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
                            }
                        }, 100
                        )
                        //请求部门数据，刷新列表
                        departmentList.clear()
                        departmentUserList.clear()
                        notifyDataSetChanged()
                        EaseUI.getInstance().execute { getDataList() }
                    }
                }

            }
        }

        inner class DepartmentUserViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            private val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
            private val iv_avatar = itemView.findViewById<AvatarImageView>(R.id.iv_avatar)
            private val pick = itemView.findViewById<CheckBox>(R.id.cb_pick)
            private val layoutPickAll = itemView.findViewById<LinearLayout>(R.id.ll_check_all)
            private val userDivider = itemView.findViewById<View>(R.id.user_divider)
            private val pickAll = itemView.findViewById<CheckBox>(R.id.cb_all)
            private val itemLayout = itemView.findViewById<RelativeLayout>(R.id.rl_department_user)
            fun bind(entity: MPUserEntity) {
                layoutPickAll.visibility = View.GONE
                if (isSchedule) {
                    if (isCreate && entity.imUserId == EMClient.getInstance().currentUser) {
                        entity.pickStatus = 2
                    }

//                    pick.setButtonDrawable(R.drawable.circle_checkbox_bg)
//                    pick.scaleX = 1.5f
//                    pick.scaleY = 1.5f

                    pick.visibility = View.VISIBLE
                    pick.isEnabled = false
                    pick.isClickable = false
                    when {
                        entity.pickStatus == 0 -> {
                            pick.isChecked = false
                            pick.isEnabled = true
                            itemView.isEnabled = true
                        }
                        entity.pickStatus == 1 -> {
                            pick.isChecked = true
                            pick.isEnabled = true
                            itemView.isEnabled = true
                        }
                        entity.pickStatus == 2 -> {
                            pick.isChecked = true
                            pick.isEnabled = false
                            itemView.isEnabled = false
                        }
                    }
                    pickAll.isChecked = departmentUserList[0].isPickAll

                    AvatarUtils.setAvatarContent(this@CompanyStructureActivity, if (TextUtils.isEmpty(entity.realName)) entity.username else entity.realName, entity.avatar, iv_avatar)

                    tv_name.text = if (TextUtils.isEmpty(entity.alias)) entity.realName else entity.alias

                    if (adapterPosition == 0) {
                        layoutPickAll.visibility = View.VISIBLE
                        userDivider.visibility = View.VISIBLE
                        pickAll.isClickable = false

                        layoutPickAll.setOnClickListener {
                            if (departmentUserList[0].isPickAll) {
                                pickAll.isChecked = false
                                for (user in departmentUserList) {
                                    if (isCreate) {
                                        if (user.imUserId != EMClient.getInstance().currentUser) {
                                            user.pickStatus = 0
                                            if (pickedUidList.contains(user.id)) {
                                                pickedUidList.remove(user.id)
                                            }
                                        }
                                    } else if (isSchedule) {
                                        user.pickStatus = 0
                                        if (pickedUidList.contains(user.id)) {
                                            pickedUidList.remove(user.id)
                                        }
                                    }
                                }
                                departmentUserList[0].isPickAll = false
                                if (pickedOrgIdMap.keys.contains(orgId)) {
                                    pickedOrgIdMap.remove(orgId)
                                }
                                notifyDataSetChanged()
                            } else {
                                pickAll.isChecked = true
                                for (user in departmentUserList) {
                                    if (isCreate) {
                                        if (user.imUserId != EMClient.getInstance().currentUser) {
                                            user.pickStatus = 1
                                            if (!pickedUidList.contains(user.id)) {
                                                pickedUidList.add(user.id)
                                            }
                                        }
                                    } else if (isSchedule) {
                                        user.pickStatus = 1
                                        if (!pickedUidList.contains(user.id)) {
                                            pickedUidList.add(user.id)
                                        }
                                    }
                                }
                                departmentUserList[0].isPickAll = true
                                notifyDataSetChanged()
                            }
                            submit?.text = String.format(resources.getString(R.string.select_someone), pickedUidList.size)
                        }
                    }
                    itemLayout.setOnClickListener(View.OnClickListener {
                        if (isSchedule) {
                            if (isCreate && entity.pickStatus == 2) {
                                return@OnClickListener
                            }
                            if (pick.isChecked) {
                                entity.pickStatus = 0
                                pick.isChecked = false
                                if (pickedUidList.contains(entity.id)) {
                                    pickedUidList.remove(entity.id)
                                }
                            } else {
                                entity.pickStatus = 1
                                pick.isChecked = true
                                if (!pickedUidList.contains(entity.id)) {
                                    pickedUidList.add(entity.id)
                                }
                            }
                            departmentUserList[0].isPickAll = pickedUidList.size == departmentUserList.size
                            notifyDataSetChanged()
                            submit?.text = String.format(resources.getString(R.string.select_someone), pickedUidList.size)
                        } else {
                            if (TextUtils.isEmpty(entity.imUserId))
                                return@OnClickListener
                            startActivityForResult(Intent(this@CompanyStructureActivity, ContactDetailsActivity::class.java).putExtra("imUserId", entity.imUserId), 200)
                        }
                    })

                }
            }

        }
    }

    private fun getMembersByOrgId(companyId: Int, orgId: Int) {

        if (!loadingPopupView!!.isShow) {
            loadingPopupView!!.show()
        }
        EMAPIManager.getInstance().getSubOrgsOfUsers(companyId, orgId, 0, 1000, object : EMDataCallBack<String>() {
            override fun onSuccess(value: String) {
                try {
                    val response = JSONObject(value)
                    if ("OK" == response.getString("status")) {
                        val jsonObj = JSONObject(value)
                        val elements = jsonObj.optInt("numberOfElements")
                        val list = MPUserEntity.create(jsonObj.optJSONArray("entities"))
                        val tempList = mutableListOf<Int>()

                        list.forEach {
                            if (!pickedUidList.contains(it.id)) {
                                pickedUidList.add(it.id)
                            }
                            tempList.add(it.id)
                        }

                        allSubsUserMap[orgId] = tempList
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                runOnUiThread {
                    loadingPopupView!!.dismiss()
                    submit?.text = String.format(resources.getString(R.string.select_someone), pickedUidList.size)
                }

            }

            override fun onError(error: Int, errorMsg: String) {

                runOnUiThread {
                    loadingPopupView!!.dismiss()
                }
                Log.i("info", "get member:$errorMsg")
            }
        })
    }
}