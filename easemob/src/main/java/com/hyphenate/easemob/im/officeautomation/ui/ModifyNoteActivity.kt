package com.hyphenate.easemob.im.officeautomation.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack
import com.hyphenate.easemob.R
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager
import org.json.JSONException
import org.json.JSONObject

class ModifyNoteActivity : BaseActivity() {

    lateinit var back: ImageView
    lateinit var save: TextView
    lateinit var etNote: EditText
    private var note: String? = null
    private var mUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_note)

        note = intent.getStringExtra("note")
        mUserId = intent.getIntExtra("userId", -1)

        back = findViewById(R.id.iv_back)
        save = findViewById(R.id.tv_save)
        etNote = findViewById(R.id.et_note)
        etNote.text = SpannableStringBuilder(note)
        etNote.setSelection(etNote.text.length)

        back.setOnClickListener {
            finish()
        }

        save.setOnClickListener {
//            if (etNote.text.toString().isEmpty()) {
//                Toast.makeText(this@ModifyNoteActivity, "内容不能为空", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            if (note == etNote.text.toString()) {
                return@setOnClickListener
            }

            val jsonObject = JSONObject()
            try {
                jsonObject.put("alias", etNote.text.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            EMAPIManager.getInstance().postAlias(mUserId, jsonObject.toString(), object : EMDataCallBack<String>() {
                override fun onSuccess(value: String) {
                    try {
                        val result = JSONObject(value)
                        val status = result.optString("status")
                        if ("OK".equals(status, ignoreCase = true)) {
                            val entity = result.optJSONObject("entity")
                            val alias = entity.optString("alias")
                            runOnUiThread {
                                val intent = Intent()
                                intent.putExtra("note", alias)
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }

                override fun onError(error: Int, errorMsg: String) {
                    runOnUiThread {
                        Toast.makeText(this@ModifyNoteActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }
}