package com.hyphenate.easemob.im.mp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.hyphenate.easemob.R
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity
import com.hyphenate.easemob.im.officeautomation.utils.AppUtil
import com.hyphenate.easemob.im.officeautomation.utils.MyToast

class ModifyActivity : BaseActivity() {

    companion object {
        private val TAG = "ModifyActivity"
        val REQUEST_CODE_UPDATE_NAME = 100
        val REQUEST_CODE_UPDATE_PHONE = 101
        val REQUEST_CODE_UPDATE_EMAIL = 102
        val REQUEST_CODE_UPDATE_TEL = 103
    }

    private var etContent: EditText? = null
    private var etAnnouncement: EditText? = null
    private var mTvSave: TextView? = null
    private var mIvBack: ImageView? = null
    private var tvTitle: TextView? = null

    private var code: Int = 0
    private var isOnlyRead: Boolean = false

    private var title: String? = null
    private var content: String? = null


    override fun onCreate(arg0: Bundle?) {
        super.onCreate(arg0)
        setContentView(R.layout.activity_modify)

        content = intent.getStringExtra("content")
        title = intent.getStringExtra("title")
        code = intent.getIntExtra("code", 0)
        isOnlyRead = intent.getBooleanExtra("onlyRead", false)

        initViews()
        initData()
    }

    private fun initViews() {
        etContent = `$`(R.id.et_name)
        etAnnouncement = `$`(R.id.et_announcement)
        mTvSave = `$`(R.id.tv_save)
        mIvBack = `$`(R.id.iv_back)
        tvTitle = `$`(R.id.tv_title)

        when (code) {
            REQUEST_CODE_UPDATE_NAME -> {
                etContent!!.inputType = InputType.TYPE_CLASS_TEXT
            }
            REQUEST_CODE_UPDATE_PHONE -> {
                etContent!!.inputType = InputType.TYPE_CLASS_PHONE
            }
            REQUEST_CODE_UPDATE_TEL -> {
                etContent!!.inputType = InputType.TYPE_CLASS_PHONE
            }
            REQUEST_CODE_UPDATE_EMAIL -> {
                etContent!!.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
        }

        mIvBack!!.setOnClickListener { finish() }
        mTvSave!!.setOnClickListener(View.OnClickListener {
            val content: String = if ("群公告" == title) {
                etAnnouncement!!.text.toString()
            } else {
                etContent!!.text.toString()
            }
            if (code == REQUEST_CODE_UPDATE_NAME) {
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(applicationContext, R.string.error_empty_username, Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }
            } else if (code == REQUEST_CODE_UPDATE_TEL) {
                if (TextUtils.isEmpty(content)) {
                    MyToast.showToast(getString(R.string.error_empty_tel))
                    return@OnClickListener
                }
            } else if (code == REQUEST_CODE_UPDATE_PHONE) {
                if (!AppUtil.isPhone(content)) {
                    MyToast.showToast(getString(R.string.error_invalid_phone))
                    return@OnClickListener
                }
            } else if (code == REQUEST_CODE_UPDATE_EMAIL) {
                if (!AppUtil.isEmail(content)) {
                    MyToast.showToast(getString(R.string.error_invalid_email))
                    return@OnClickListener
                }
            }
            setResult(Activity.RESULT_OK, Intent().putExtra("content", content))
            finish()
        })
    }


    private fun initData() {
        if (isOnlyRead) {
            mTvSave!!.visibility = View.GONE
            etContent!!.isEnabled = false
            etAnnouncement!!.isEnabled = false
        }

        if ("群公告" == title) {
            etAnnouncement!!.visibility = View.VISIBLE
            etContent!!.visibility = View.GONE
            content?.let {
                etAnnouncement!!.setText(it)
                etAnnouncement!!.setSelection(it.length)
            }
        } else {
            content?.let {
                etContent!!.setText(it)
                etContent!!.setSelection(it.length)
            }
            etAnnouncement!!.visibility = View.GONE
            etContent!!.visibility = View.VISIBLE
        }
        if (!TextUtils.isEmpty(title)) {
            tvTitle!!.text = title
        }
    }
}
