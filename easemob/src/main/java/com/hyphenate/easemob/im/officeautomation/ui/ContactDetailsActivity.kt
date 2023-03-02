package com.hyphenate.easemob.im.officeautomation.ui

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.hyphenate.EMCallBack
import com.hyphenate.easemob.imlibs.cache.OnlineCache
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMCmdMessageBody
import com.hyphenate.chat.EMMessage
import com.hyphenate.easemob.easeui.EaseUI
import com.hyphenate.easemob.easeui.ui.EaseShowBigImageActivity
import com.hyphenate.easemob.easeui.utils.AvatarUtils
import com.hyphenate.easemob.easeui.widget.AvatarImageView
import com.hyphenate.eventbus.Subscribe
import com.hyphenate.eventbus.ThreadMode
import com.hyphenate.exceptions.HyphenateException
import com.hyphenate.easemob.im.gray.GrayScaleConfig
import com.hyphenate.easemob.imlibs.message.MessageUtils
import com.hyphenate.easemob.im.mp.AppHelper
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack
import com.hyphenate.easemob.im.mp.cache.TenantOptionCache
import com.hyphenate.easemob.imlibs.mp.events.EventFriendNotify
import com.hyphenate.easemob.imlibs.mp.events.EventOnLineOffLineQuery
import com.hyphenate.easemob.imlibs.mp.events.EventTenantOptionChanged
import com.hyphenate.easemob.imlibs.mp.utils.MPLog
import com.hyphenate.easemob.im.mp.utils.UserProvider
import com.hyphenate.easemob.R
import com.hyphenate.easemob.im.officeautomation.db.InviteMessageDao
import com.hyphenate.easemob.im.officeautomation.domain.InviteMessage
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager
import com.hyphenate.easemob.im.officeautomation.http.BaseRequest
import com.hyphenate.easemob.im.officeautomation.runtimepermissions.PermissionsManager
import com.hyphenate.easemob.im.officeautomation.runtimepermissions.PermissionsResultAction
import com.hyphenate.easemob.im.officeautomation.utils.AppUtil
import com.hyphenate.easemob.im.officeautomation.utils.Constant
import com.hyphenate.easemob.im.officeautomation.utils.MyToast
import com.hyphenate.util.EMLog
import com.mylhyl.circledialog.CircleDialog
import org.json.JSONException
import org.json.JSONObject

class ContactDetailsActivity : BaseActivity(), View.OnClickListener {

    private val RESULT_CODE_STARRED_FRIENDS = 1
    private var TAG = "ContactDetailsActivity"

    private var ivBack: ImageView? = null
    private var ivAvatar: AvatarImageView? = null
    private var more: ImageView? = null
    private var tvUserNick: TextView? = null
    private var ivStar: ImageView? = null


    private var tvNote: TextView? = null
    private var tvMobilePhone: TextView? = null
    private var llEmail: View? = null
    private var tvEmail: TextView? = null
    private var tvOnline: TextView? = null
    private var llOrg: View? = null
    private var tvOrgFullName: TextView? = null
    private var llPosition: View? = null
    private var tvPosition: TextView? = null

    private var llSend: View? = null
    private var llSendPhone: View? = null

    private var imUsername: String? = null
    private var isFriend: Int = -1
    private var mUserId: Int = 0
    private var mpUserEntity: MPUserEntity? = null
    private var mpOrgEntities: List<MPOrgEntity>? = null

    private lateinit var layoutPersonMe: RelativeLayout
    private lateinit var layoutMobile: LinearLayout
    private lateinit var layoutEmail: LinearLayout
    private var popupWindow: PopupWindow? = null
    private lateinit var popAddFriend: TextView
    private lateinit var popStarred: TextView
    private lateinit var popNote: TextView

    //是否是星标好友
    private var isStarred: Boolean = false
    private var isFromStarred: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_details)
        initViews()
        initDatas()
    }


    private fun initViews() {
        ivBack = findViewById(R.id.iv_back)
        ivAvatar = findViewById(R.id.iv_useravator)
        tvUserNick = findViewById(R.id.tv_nick)
        ivStar = findViewById(R.id.iv_star)
        more = findViewById(R.id.iv_more)
        ivAvatar = findViewById(R.id.iv_useravator)
        tvOnline = findViewById(R.id.tv_online)

        tvNote = findViewById(R.id.tv_note)

        layoutMobile = findViewById(R.id.ll_mobilephone)
        layoutPersonMe = findViewById(R.id.rl_personal_me)
        layoutEmail = findViewById(R.id.ll_email)
        tvMobilePhone = findViewById(R.id.tv_mobilephone)

        llEmail = findViewById(R.id.ll_email)
        tvEmail = findViewById(R.id.tv_email)

        llOrg = findViewById(R.id.ll_org)
        tvOrgFullName = findViewById(R.id.tv_org_name)

        llPosition = findViewById(R.id.ll_position)
        tvPosition = findViewById(R.id.tv_position)


        llSend = findViewById(R.id.ll_send)
        llSendPhone = findViewById(R.id.ll_sendphone)


        popupWindow = PopupWindow(this)
        popupWindow!!.contentView = layoutInflater.inflate(R.layout.contact_detail_popup, null, false)
        popupWindow!!.setBackgroundDrawable(BitmapDrawable())
        popupWindow!!.isFocusable = true
        popupWindow!!.isOutsideTouchable = true

        popAddFriend = popupWindow!!.contentView.findViewById(R.id.tv_add_friend)
        popStarred = popupWindow!!.contentView.findViewById(R.id.tv_starred)
        popNote = popupWindow!!.contentView.findViewById(R.id.tv_note)

        if(GrayScaleConfig.useContact) {
            popAddFriend.visibility = View.VISIBLE
        } else {
            popAddFriend.visibility = View.GONE
        }

        popAddFriend.setOnClickListener {
            popupWindow!!.dismiss()
            if (isFriend == 1) {
                EMAPIManager.getInstance().deleteFriend(mUserId, object : EMDataCallBack<String>() {
                    override fun onSuccess(value: String) {
                        try {
                            val result = JSONObject(value)
                            if ("OK" == result.getString("status")) {
                                isFriend = 0
                                runOnUiThread {
                                    Toast.makeText(this@ContactDetailsActivity, "删除成功", Toast.LENGTH_SHORT).show()
                                    val intent = Intent()
                                    intent.putExtra("isFriend", isFriend)
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }

                    override fun onError(error: Int, errorMsg: String) {
                        runOnUiThread { Toast.makeText(this@ContactDetailsActivity, "解除好友失败", Toast.LENGTH_SHORT).show() }
                    }
                })

            } else if (isFriend == 0) {
                EMAPIManager.getInstance().invitedOrAcceptFriend(mUserId, object : EMDataCallBack<String>() {
                    override fun onSuccess(value: String) {
                        friendRelationStatus(mUserId)
                        try {
                            val result = JSONObject(value)
                            if ("OK" == result.getString("status")) {
                                runOnUiThread {
                                    if (isFriend == 0) {
                                        Toast.makeText(this@ContactDetailsActivity, resources.getString(R.string.send_successful), Toast.LENGTH_SHORT).show()
                                    } else {
                                        isFriend = 1
                                        val dao = InviteMessageDao()
                                        val inviteMessage = dao.getInviteMessage(mUserId)
                                        val values = ContentValues()
                                        inviteMessage.status = InviteMessage.InviteMessageStatus.BEAGREED
                                        values.put(InviteMessageDao.COLUMN_NAME_STATUS, inviteMessage.status!!.ordinal)
                                        dao.updateMessage(mUserId, values)
                                        Toast.makeText(this@ContactDetailsActivity, resources.getString(R.string.Has_agreed_to), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }

                    override fun onError(error: Int, errorMsg: String) {

                        runOnUiThread { Toast.makeText(this@ContactDetailsActivity, errorMsg, Toast.LENGTH_SHORT).show() }
                    }
                })
            }
        }

        popStarred.setOnClickListener {
            popupWindow!!.dismiss()
            if (isStarred) {
                //取消星标
                cancelStarredFriend()
            } else {
                //添加星标
                addStarredFriend(mUserId)
            }
        }

        popNote.setOnClickListener {
            popupWindow!!.dismiss()
            startActivityForResult(Intent(this@ContactDetailsActivity, ModifyNoteActivity::class.java).putExtra("note", tvNote!!.text.toString()).putExtra("userId", mUserId), 1000)
        }


        ivBack!!.setOnClickListener(this)
        more!!.setOnClickListener(this)

        llSend!!.setOnClickListener(this)
        llSendPhone!!.setOnClickListener(this)

        ivAvatar!!.setOnClickListener(this)

//        refreshWaterMark(this)
    }

    private fun initDatas() {
        val gIntent = intent
        if (gIntent == null) {
            finish()
            return
        }

        mUserId = gIntent.getIntExtra("userId", 0)
        imUsername = gIntent.getStringExtra("imUserId")
        mpUserEntity = gIntent.getParcelableExtra("entity")
        isFromStarred = gIntent.getBooleanExtra("isFromStarred", false)


        when {
            mpUserEntity != null -> {
                mUserId = mpUserEntity!!.id
                imUsername = mpUserEntity!!.imUserId
            }
            mUserId > 0 -> mpUserEntity = AppHelper.getInstance().model.getUserInfoById(mUserId)
            imUsername != null -> mpUserEntity = AppHelper.getInstance().model.getUserInfo(imUsername)
        }

        if (mpUserEntity != null) {
            setData(mpUserEntity, null)
            mUserId = mpUserEntity!!.id
            imUsername = mpUserEntity!!.imUserId
        }

        if (mUserId == BaseRequest.getUserId() || EMClient.getInstance().currentUser == imUsername) {
            ivStar!!.visibility = View.GONE
            llSend!!.visibility = View.GONE
            llSendPhone!!.visibility = View.GONE
            more!!.visibility = View.GONE
        } else {
            getIsStarred(mUserId)
        }

        //        if (mpUserEntity == null && userId > 0) {
        //        }

        //        if (mpUserEntity == null && userId > 0) {
        getUserPersonalInfo()
        //        }
        friendRelationStatus(mUserId)

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshTenantOption(event: EventTenantOptionChanged) {
        if (TenantOptionCache.OPTION_NAME_WATERMARK == event.optionName) {
//            refreshWaterMark(this@ContactDetailsActivity)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> onBackPressed()
            R.id.iv_more -> {
                popupWindow!!.showAsDropDown(v)
            }
            R.id.ll_send -> {
                if (imUsername == null) {
                    return
                }
                //避免本地未存储app server用户名与realName时显示为easemobName
                startActivity(Intent(activity, ChatActivity::class.java).putExtra("userId", imUsername))
                finish()
            }

            R.id.ll_sendphone -> {
                val loginUser = UserProvider.getInstance().loginUser
                if (loginUser == null || loginUser.entityBean == null) {
                    return
                }
                if (loginUser.entityBean.isSuperLevel) {
                    //弹出选择网络电话、本地电话
                    showPhoneFlipWindow()
                } else {
                    if (mpUserEntity!!.isSuperLevel) {
                        showSuperLevelDialog()
                        return
                    }

                    showPhoneFlipWindow()

                }
            }

            R.id.iv_useravator -> {
                if (mpUserEntity == null) {
                    return
                }
                var remoteUrl = mpUserEntity!!.avatar
                if (TextUtils.isEmpty(remoteUrl)) {
                    return
                }
                if (!remoteUrl.startsWith("http")) {
                    remoteUrl = EaseUI.getInstance().appServer + remoteUrl
                }
                startActivity(Intent(activity, EaseShowBigImageActivity::class.java).putExtra("remote_url", remoteUrl))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1000 && data != null) {

                val alias = data.getStringExtra("note")
                mpUserEntity!!.alias = alias
                AppHelper.getInstance().model.saveUserInfo(mpUserEntity)
                val easeUser = UserProvider.getInstance().getEaseUserById(mUserId)
                easeUser!!.alias = alias
                tvNote!!.text = alias ?: ""
                tvUserNick!!.text = mpUserEntity!!.realName

            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("isFriend", isFriend)
        intent.putExtra("user", mpUserEntity)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun finishActivity() {
        super.finishActivity()
        val intent = Intent()
        intent.putExtra("isFriend", isFriend)
        intent.putExtra("user", mpUserEntity)
        setResult(Activity.RESULT_OK, intent)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun friendNotification(eventFriendNotify: EventFriendNotify) {
        val message = eventFriendNotify.message
        val cmdMsgBody = message.body as EMCmdMessageBody
        val action = cmdMsgBody.action()
        try {
            val extJson = message.getJSONObjectAttribute("content")
            val uid = extJson.getInt("userId")
            val fid = extJson.getInt("friendId")
            if (Constant.CMD_ACTION_INVITED_FRIEND == action) {
                friendRelationStatus(uid)
            } else if (Constant.CMD_ACTION_ACCEPT_FRIEND == action) {
                friendRelationStatus(fid)
            } else if (Constant.CMD_ACTION_DELETED_FRIEND == action) {
                friendRelationStatus(fid)
            }
        } catch (e: HyphenateException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun requestOnlineNotify(imUserId: String) {
        if (MessageUtils.isCommonRegionWithMe(imUserId)) {
            val message = EMMessage.createSendMessage(EMMessage.Type.CMD)
            val cmdMessageBody = EMCmdMessageBody(Constant.ACTION_ONLINE_REQUEST)
            cmdMessageBody.deliverOnlineOnly(false)
            message.to = imUserId
            message.addBody(cmdMessageBody)
            MessageUtils.sendMessage(message)
        }
    }

    private fun requestDeleteOnlineNotify(imUserId: String) {
        if (MessageUtils.isCommonRegionWithMe(imUserId)) {
            val message = EMMessage.createSendMessage(EMMessage.Type.CMD)
            val cmdMessageBody = EMCmdMessageBody(Constant.ACTION_ONLINE_CANCEL)
            cmdMessageBody.deliverOnlineOnly(false)
            message.to = imUserId
            message.addBody(cmdMessageBody)
            MessageUtils.sendMessage(message)
        }
    }

    private fun dismissDialog() {
        if (mDialogFragment != null) {
            mDialogFragment!!.dismissAllowingStateLoss()
            mDialogFragment = null
        }
    }

    private fun showSuperLevelDialog() {
        mDialogFragment = CircleDialog.Builder()
                .setText("对方为高管人员，您不能主动拨打电话！")
                .setTitle("错误").setPositive(getString(R.string.ok), null).show(supportFragmentManager)
    }


    private fun setData(entityBean: MPUserEntity?, orgEntities: List<MPOrgEntity>?) {
        if (entityBean == null) {
            return
        }

        if (!entityBean.isActive) {
            dismissDialog()
            mDialogFragment = CircleDialog.Builder().setText("\r\n对方账号已被禁用\r\n").setCancelable(false)
                    .setCanceledOnTouchOutside(false)
                    .setPositive(getString(R.string.ok)) { finish() }.show(supportFragmentManager)
            return
        }


        this.mpUserEntity = entityBean
        this.mpOrgEntities = orgEntities
        AvatarUtils.setAvatarContent(this, entityBean.realName, entityBean.avatar, ivAvatar)
        if (!TextUtils.isEmpty(entityBean.realName)) {
            tvUserNick!!.text = entityBean.realName
            if (!TextUtils.isEmpty(entityBean.alias)) {
                tvNote!!.text = entityBean.alias
            } else {
                tvNote!!.text = entityBean.realName
            }
        }
        if (!TextUtils.isEmpty(entityBean.phone))
            tvMobilePhone!!.text = entityBean.phone
        if (!TextUtils.isEmpty(entityBean.email))
            tvEmail!!.text = entityBean.email

        if (orgEntities != null) {
            if (entityBean.id == BaseRequest.getUserId() || TextUtils.isEmpty(entityBean.imUserId)) {
                llSend!!.visibility = View.GONE
                llSendPhone!!.visibility = View.GONE
            } else {
                if (EMClient.getInstance().currentUser == entityBean.imUserId) {
                    llSendPhone!!.visibility = View.GONE
                    llSend!!.visibility = View.GONE
                } else {
                    llSend!!.visibility = View.VISIBLE
                    llSendPhone!!.visibility = View.VISIBLE
                }
            }
        }

        if (orgEntities != null && orgEntities.isNotEmpty()) {
            val orgEntity = orgEntities[0]
            if (orgEntity.position != null) {
                tvPosition!!.text = orgEntity.position
            } else {
                tvPosition!!.text = ""
            }

            when {
                orgEntity.fullName != null -> tvOrgFullName!!.text = orgEntity.fullName
                orgEntity.companyName != null -> tvOrgFullName!!.text = orgEntity.companyName
                else -> tvOrgFullName!!.text = ""
            }
        } else {
            tvOrgFullName!!.text = ""
            tvPosition!!.text = ""
        }

        if(MessageUtils.isCommonRegionWithMe(entityBean.imUserId)) {
            fetchUserOnlineStatus(entityBean.imUserId)
        }
    }

    private fun fetchUserOnlineStatus(imUserId: String?) {
        if (imUserId == null) {
            return
        }
        val status = OnlineCache.getInstance().get(imUserId);

        if (status == null) {
            val imUserIds = ArrayList<String>(1)
            imUserIds.add(imUserId)
            EMClient.getInstance().chatManager().getUserStatusWithUserIds(imUserIds, object : EMCallBack {
                override fun onSuccess() {
                    EMLog.d(this@ContactDetailsActivity.TAG, "getUserStatusWithUserIds-success,imUserId:$imUserId");
                }

                override fun onProgress(p0: Int, p1: String?) {
                }

                override fun onError(p0: Int, p1: String?) {
                    EMLog.d(this@ContactDetailsActivity.TAG, "getUserStatusWithUserIds-onError:$p1,imUserId:$imUserId");
                }

            })
        } else {
            updateOnlineView(status)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onEventContactQuery(query: EventOnLineOffLineQuery) {
        MPLog.e("EventOnLineOffLineQuery", "userStatus:${query.userStatus}")
        if(MessageUtils.isCommonRegionWithMe(imUsername)) {
            val status = OnlineCache.getInstance().get(imUsername)
            updateOnlineView(status)
        }
    }

    private fun updateOnlineView(status: Boolean) {
        if(MessageUtils.isCommonRegionWithMe(imUsername)) {
            if (status) {
                tvOnline?.text = "[在线]"
                tvOnline?.setTextColor(Color.GREEN)
            } else {
                tvOnline?.text = "[离线]"
                tvOnline?.setTextColor(Color.GRAY)
            }
        } else {
            tvOnline?.visibility = View.GONE
        }
    }


    private fun cancelStarredFriend() {

        EMAPIManager.getInstance().deleteStaredFriends(mUserId, object : EMDataCallBack<String>() {
            override fun onSuccess(value: String) {
                requestDeleteOnlineNotify(imUsername!!);
                try {
                    val jsonObject = JSONObject(value)
                    val status = jsonObject.getString("status")
                    if ("OK".equals(status, ignoreCase = true)) {
                        isStarred = false
                        popStarred.text = "标星"
                        notifyStarredChange()
                        if (isFromStarred)
                            setResult(RESULT_CODE_STARRED_FRIENDS)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onError(error: Int, errorMsg: String) {

            }
        })
    }

    //获取员工信息
    private fun getUserPersonalInfo() {
        if (mUserId == 0) {
            return
        }
        val progressDialog = ProgressDialog.show(this, "请稍后...", "正在加载...", true, true)
        EMAPIManager.getInstance().getUserDetails(mUserId, object : EMDataCallBack<String>() {
            override fun onSuccess(value: String) {
                if (isFinishing) {
                    return
                }

                runOnUiThread {
                    progressDialog.dismiss()
                    try {
                        val jsonResult = JSONObject(value)
                        val jsonObj = jsonResult.optJSONObject("entity")
                        val jsonCompanyList = jsonObj.optJSONArray("companyList")
                        val jsonOrgList = jsonObj.optJSONArray("organizationList")
                        val jsonUserCompany = jsonObj.optJSONArray("userCompanyRelationshipList")
                        val userEntity = MPUserEntity.create(jsonObj.optJSONObject("user"))
                        val orgEntities = MPOrgEntity.create(jsonOrgList, jsonCompanyList, jsonUserCompany)

                        if (jsonOrgList.length() == 0) {
                            userEntity!!.orgId = -1
                        } else {
                            userEntity!!.orgId = jsonOrgList.getJSONObject(0).getInt("id")
                        }

                        AppHelper.getInstance().model.saveUserInfo(userEntity)
                        if (orgEntities == null) {
                            return@runOnUiThread
                        }
                        if (imUsername == null) {
                            imUsername = userEntity.imUserId;
                        }
                        setData(userEntity, orgEntities)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onError(error: Int, errorMsg: String) {
                if (isFinishing) {
                    return
                }
                runOnUiThread {
                    progressDialog.dismiss()
                    Toast.makeText(this@ContactDetailsActivity, "获取用户信息失败！", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun addStarredFriend(userId: Int) {
        val jsonBody = JSONObject()
        EMAPIManager.getInstance().addStartedFriends(userId, jsonBody.toString(), object : EMDataCallBack<String>() {
            override fun onSuccess(value: String) {
                requestOnlineNotify(imUsername!!);
                runOnUiThread {
                    try {
                        val jsonObject = JSONObject(value)
                        val status = jsonObject.optString("status")
                        if ("OK".equals(status, ignoreCase = true)) {
                            isStarred = true
                            popStarred.text = "取消标星"
                            notifyStarredChange()
                            if (isFromStarred)
                                setResult(RESULT_CODE_STARRED_FRIENDS)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

            }

            override fun onError(error: Int, errorMsg: String) {

            }
        })
    }

    //获取是否是收藏的好友
    private fun getIsStarred(userId: Int) {
        EMAPIManager.getInstance().getIsStaredFriend(userId, object : EMDataCallBack<String>() {
            override fun onSuccess(value: String) {
                try {
                    val jsonObject = JSONObject(value)
                    val status = jsonObject.getString("status")
                    if ("OK".equals(status, ignoreCase = true)) {
                        val entity = jsonObject.getJSONObject("entity")
                        if (entity != null) {
                            runOnUiThread {
                                ivStar!!.visibility = View.VISIBLE
                                isStarred = entity.getBoolean("starred")
                                if (isStarred) {
                                    popStarred.text = "取消标星"
                                } else {
                                    popStarred.text = "标星"
                                }
                                notifyStarredChange()
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onError(error: Int, errorMsg: String) {

            }
        })
    }

    private fun friendRelationStatus(friendId: Int) {
        if (friendId <= 0) {
            return
        }
        EMAPIManager.getInstance().friendRelationStatus(friendId, object : EMDataCallBack<String>() {

            override fun onSuccess(value: String) {
                if (isFinishing) {
                    return
                }
                runOnUiThread {
                    try {
                        val jsonObject = JSONObject(value)
                        if ("OK" == jsonObject.getString("status")) {
                            val entity = jsonObject.getJSONObject("entity")
                            when (entity.optString("result")) {
                                "friend" -> {
                                    isFriend = 1
                                    popAddFriend.text = "删除好友"
                                }
                                "accepting" -> {
                                    isFriend = 0
                                    popAddFriend.text = "添加好友"
                                }
                                else -> {
                                    isFriend = 0
                                    popAddFriend.text = "添加好友"

                                }
                            }
                        }
                    } catch (e: Exception) {

                    }
                }
            }

            override fun onError(error: Int, errorMsg: String) {
            }
        })
    }

    //改变星标
    private fun notifyStarredChange() {
        if (isFinishing) {
            return
        }
        runOnUiThread { ivStar!!.setImageResource(if (isStarred) R.drawable.mp_temp_icon_star_solid else R.drawable.mp_temp_icon_star_white) }
    }

    private var mPermissions = arrayOf(Manifest.permission.CALL_PHONE)

    private fun checkPermission(): Boolean {
        val hasPermission = ContextCompat.checkSelfPermission(this, mPermissions[0])
        return if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, mPermissions[0])) {
                ActivityCompat.requestPermissions(this, mPermissions, 10)
            } else {
                //拒绝并不在询问
                Toast.makeText(this, "需要在设置中开启权限", Toast.LENGTH_SHORT).show()
            }
            false
        } else {
            true
        }
    }

    private var mDialogFragment: DialogFragment? = null

    //拨打电话
    private fun callPhone() {
        val phoneNum = tvMobilePhone!!.text.toString()
        if (!TextUtils.isEmpty(phoneNum) && AppUtil.isPhone(phoneNum)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                MyToast.showToast(getString(R.string.permission_deny))
                requestPermissions(Manifest.permission.CALL_PHONE)
            } else {
                val intent = Intent(Intent.ACTION_CALL)
                val data = Uri.parse("tel:" + phoneNum.trim { it <= ' ' })
                intent.data = data
                startActivity(intent)
            }
        } else {
            MyToast.showToast(getString(R.string.phone_invalid))
        }
    }


    @TargetApi(23)
    private fun requestPermissions(permission: String) {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(activity, arrayOf(permission), object : PermissionsResultAction() {
            override fun onGranted() {
                if (Manifest.permission.CALL_PHONE == permission)
                    callPhone()
            }

            override fun onDenied(permission: String) {

            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults)
    }


    override fun onDestroy() {
        super.onDestroy()
        dismissDialog()
        if (popupWindow != null) {
            if (popupWindow!!.isShowing) popupWindow!!.dismiss()
            popupWindow = null
        }
    }

    //弹窗提示拨打电话
    private fun showPhoneFlipWindow() {
        dismissDialog()
        val phoneList = ArrayList<String>()
        phoneList.add(getString(R.string.phone_call))
        mDialogFragment = CircleDialog.Builder()
            .setItems(phoneList) { adapterView, view, position, l ->
                dismissDialog()
                if (position == 0) {
                    if (checkPermission())
                        callPhone()
                }
                false
            }.setNegative(getString(R.string.cancel), null).show(supportFragmentManager)

    }

}