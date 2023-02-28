package com.hyphenate.easemob.im.mp.rest

import com.hyphenate.EMError
import com.hyphenate.eventbus.MPEventBus
import com.hyphenate.easemob.im.mp.AppHelper
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack
import com.hyphenate.easemob.imlibs.mp.events.EventUsersReady
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager
import com.hyphenate.easemob.im.officeautomation.http.BaseRequest
import com.hyphenate.easemob.im.officeautomation.utils.PreferenceManager
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author liyuzhao
 * 获取本部门下的所有人
 */
class EMMyOrgUsersRequest {


    companion object {
//        private var mCall: Call? = null

        fun cancel() {
//            if (mCall != null && !mCall!!.isCanceled) {
//                mCall!!.cancel()
//            }
        }
    }

    private val pageSize = 1000
    private val allUsers = Collections.synchronizedList(ArrayList<MPUserEntity>());
    private var page : Int = 0;
    private var mDataCallBack: EMDataCallBack<List<MPUserEntity>>? = null

    fun setListener(callBack: EMDataCallBack<List<MPUserEntity>>): EMMyOrgUsersRequest {
        this.mDataCallBack = callBack;
        return this;
    }

    fun request() {
        cancel()
        if (PreferenceManager.getInstance().lastCacheOrgUsersTime > 0) {
            page = 0
        } else {
            allUsers.clear()
            getAllUsers()
        }
    }

    private fun getAllUsers() {
        EMAPIManager.getInstance().getAllUsers(BaseRequest.getCompanyId(), page, pageSize, object : EMDataCallBack<String>() {
            override fun onError(error: Int, errorMsg: String?) {
                if (mDataCallBack != null) {
                    mDataCallBack!!.onError(error, errorMsg)
                }
            }

            override fun onSuccess(value: String?) {
                try {
                    val jsonObj = JSONObject(value)
                    val totalPages = jsonObj.optInt("totalPages")
                    val isLast = jsonObj.optBoolean("last")
                    val userList = MPUserEntity.create(jsonObj.optJSONArray("entities"))
                    allUsers.addAll(userList)
                    if (page < totalPages - 1) {
                        page++
                        getAllUsers()
                    }
                    if (isLast) {
                        PreferenceManager.getInstance().lastCacheOrgUsersTime = System.currentTimeMillis()
                        AppHelper.getInstance().model.saveUsersList(allUsers)
                        MPEventBus.getDefault().post(EventUsersReady())
                        if (mDataCallBack != null) {
                            mDataCallBack!!.onSuccess(allUsers)
                        }
                    }
                } catch (e : Exception) {
                    if (mDataCallBack != null) {
                        mDataCallBack!!.onError(EMError.GENERAL_ERROR, "jsonException")
                    }
                }
            }

        })

    }

}