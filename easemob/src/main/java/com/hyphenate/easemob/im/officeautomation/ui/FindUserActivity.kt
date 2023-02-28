package com.hyphenate.easemob.im.officeautomation.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.blankj.utilcode.util.ToastUtils
import com.hyphenate.easemob.easeui.utils.AvatarUtils
import com.hyphenate.easemob.easeui.widget.AvatarImageView
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack
import com.hyphenate.easemob.R
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager
import org.json.JSONObject

class FindUserActivity : BaseActivity() {

    private lateinit var findUser: EditText

    private lateinit var itemUser: RelativeLayout
    private lateinit var userView: View
    private lateinit var avatar: AvatarImageView
    private lateinit var name: TextView


    private lateinit var mpEntitiesBean: MPUserEntity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_user)

        val imageView = `$`<ImageView>(R.id.iv_back)
        imageView.setOnClickListener { finish() }

        findUser = `$`(R.id.et_find_user)

        itemUser = `$`(R.id.rl_department_user)
        userView = `$`(R.id.item_user_view)
        avatar = `$`(R.id.iv_avatar)
        name = `$`(R.id.tv_name)

        findUser.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> getUserInfo(v.text.toString())
            }
            false
        }

        userView.setOnClickListener {
            startActivity(Intent(this, ContactDetailsActivity::class.java).putExtra("entity", mpEntitiesBean))
        }
    }


    fun getUserInfo(content: String) {
        EMAPIManager.getInstance().getSingleUserBaseInfo(content, object : EMDataCallBack<String>() {
            override fun onSuccess(value: String?) {
                val result = JSONObject(value)
                val mpUserEntityList = MPUserEntity.create(result.getJSONArray("entities"))
                runOnUiThread {
                    if ("OK" == result.getString("status") && mpUserEntityList != null && mpUserEntityList.size > 0) {
                        mpEntitiesBean = mpUserEntityList[0]

                        userView.visibility = View.VISIBLE
                        AvatarUtils.setAvatarContent(this@FindUserActivity, mpEntitiesBean.realName, mpEntitiesBean.avatar, avatar)
                        name.text = mpEntitiesBean.realName
                    } else {

                        userView.visibility = View.GONE
//                        Toast.makeText(this@FindUserActivity, resources.getString(R.string.user_not_existed), Toast.LENGTH_SHORT).show()
                        ToastUtils.showLong(resources.getString(R.string.user_not_existed))
                    }
                }
            }

            override fun onError(error: Int, errorMsg: String?) {
                runOnUiThread {
                    userView.visibility = View.GONE
                    Toast.makeText(this@FindUserActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}

