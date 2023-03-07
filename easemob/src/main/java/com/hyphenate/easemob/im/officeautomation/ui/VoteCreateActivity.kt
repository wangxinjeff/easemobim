package com.hyphenate.easemob.im.officeautomation.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hyphenate.easemob.R
import com.hyphenate.easemob.easeui.EaseConstant
import com.hyphenate.easemob.im.mp.AppHelper
import com.hyphenate.easemob.im.officeautomation.adapter.VoteCreateAdapter
import com.hyphenate.easemob.im.officeautomation.domain.VoteCreateEntity
import com.hyphenate.easemob.im.officeautomation.utils.MyToast
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager
import com.hyphenate.util.DensityUtil
import com.kyleduo.switchbutton.SwitchButton
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class VoteCreateActivity : BaseActivity(), View.OnClickListener, VoteCreateAdapter.OnOptionsChangeListener {

    private lateinit var ivBack: ImageView
    private lateinit var etTitle: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var llAddOptions: LinearLayout
    private lateinit var rlDate : RelativeLayout
    private lateinit var rlMultiSelect : RelativeLayout
    private lateinit var rlDisclosureResult : RelativeLayout
    private lateinit var rlAnonymous : RelativeLayout

    private lateinit var ivAdd: ImageView
    private lateinit var tvDate: TextView
    private lateinit var btnSwitchMulti : SwitchButton
    private lateinit var btnSwitchResult : SwitchButton
    private lateinit var btnSwitchAnonymous : SwitchButton

    private lateinit var btnCreateVote : Button
    private var defaultTime = System.currentTimeMillis() + 1000*60*60*24
    private var endTime = defaultTime
    private var mYear = 0
    private var month = 0
    private var mDay = 0
    private var mHour = 0
    private var minute = 0
    private var groupId:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote_create)
        groupId = intent.getStringExtra("groupId")
        initView()
        initData()
    }

    private fun initView() {
        ivBack = findViewById(R.id.iv_back)
        etTitle = findViewById(R.id.et_vote_title)
        recyclerView = findViewById(R.id.recycler_view)
        llAddOptions = findViewById(R.id.ll_add_options)
        rlDate = findViewById(R.id.rl_date)
        rlMultiSelect = findViewById(R.id.rl_multi_select)
        rlDisclosureResult = findViewById(R.id.rl_disclosure_result)
        rlAnonymous = findViewById(R.id.rl_anonymous)

        ivAdd = findViewById(R.id.iv_add)
        tvDate = findViewById(R.id.tv_date)
        btnSwitchMulti = findViewById(R.id.switch_btn_multi_select)
        btnSwitchResult = findViewById(R.id.switch_btn_disclosure_result)
        btnSwitchAnonymous = findViewById(R.id.switch_btn_anonymous)
        btnCreateVote =  findViewById(R.id.btn_create_vote)

        ivBack.setOnClickListener(this)
        llAddOptions.setOnClickListener(this)
        rlDate.setOnClickListener(this)
        rlMultiSelect.setOnClickListener(this)
        rlDisclosureResult.setOnClickListener(this)
        rlAnonymous.setOnClickListener(this)
        btnCreateVote.setOnClickListener(this)

        etTitle.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().length > 20){
                    MyToast.showToast("已超出最大字数限制")
                    etTitle.setText(s?.substring(0, 20))
                    etTitle.setSelection(20)
                }
            }
        })

    }

    private fun initData() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = VoteCreateAdapter(this)
        (recyclerView.adapter as VoteCreateAdapter).changeListener = this

        showEndTime()

        mYear = Calendar.getInstance().get(Calendar.YEAR)
        month = Calendar.getInstance().get(Calendar.MONTH)
        mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1
        mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        minute = Calendar.getInstance().get(Calendar.MINUTE)
    }

    private fun showEndTime(){
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
        val date = format.format(Date(endTime))
        tvDate.text = date
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.iv_back -> finish()
            R.id.ll_add_options -> {
                val itemCount = (recyclerView.adapter as VoteCreateAdapter).dataList.size + 1
                if(itemCount <= 20){
                    val itemHeight = DensityUtil.dip2px(this, 52f)
                    val params = recyclerView.layoutParams
                    params.height = itemHeight * itemCount
                    recyclerView.layoutParams = params
                    (recyclerView.adapter as VoteCreateAdapter).addOptions()
                } else {
                    MyToast.showToast("最多20个选项")
                }
            }
            R.id.rl_date -> {
                val datePicker = DatePickerDialog(this,
                    { view, year, month, dayOfMonth ->
                        TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,
                            { view, hourOfDay, minute ->
                                mYear = year
                                this.month = month
                                mDay = dayOfMonth
                                mHour = hourOfDay
                                this.minute = minute
                                val time = "$mYear-${month+1}-$mDay $mHour:$minute"
                                val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
                                val date = format.parse(time)
                                if (date != null) {
                                    if(date.time > defaultTime){
                                        endTime = date.time
                                        showEndTime()
                                    } else {
                                       MyToast.showToast("结束日期不能小于默认日期")
                                    }
                                }
                            },
                            mHour, minute, true).show()
                    },
                    mYear, month, mDay)
                datePicker.datePicker.minDate = defaultTime
                datePicker.datePicker.maxDate = System.currentTimeMillis() + 1000*60*60*24*7
                datePicker.show()
//                DatePickerDialogFragment.showDialog(this, mYear, month, mDay, mHour, minute, object : DatePickerDialogFragment.OnClickListener{
//                    override fun onConfirmDate(timestamp: Long) {
//                        if(timestamp > defaultTime){
//                            endTime = timestamp
//                            showEndTime()
//                        } else {
//                            MyToast.showToast("结束日期不能小于默认日期")
//                        }
//                    }
//                })
            }
            R.id.rl_multi_select -> {
                btnSwitchMulti.isChecked = !btnSwitchMulti.isChecked
            }
            R.id.rl_disclosure_result -> {
                btnSwitchResult.isChecked = !btnSwitchResult.isChecked
            }
            R.id.rl_anonymous -> {
                btnSwitchAnonymous.isChecked = !btnSwitchAnonymous.isChecked
            }
            R.id.btn_create_vote -> {
                val title = etTitle.text.toString()
                val list = (recyclerView.adapter as VoteCreateAdapter).dataList
                if(title.isEmpty()){
                    MyToast.showToast("投票主题不能为空")
                    return
                }
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                val time = format.format(Date(endTime))
                val multi = btnSwitchMulti.isChecked
                val openResult = btnSwitchResult.isChecked
                val anonymous = btnSwitchAnonymous.isChecked

                try{
                    val jsonBody = JSONObject()
                    val groupVote = JSONObject()
                    val groupVoteOptionList = JSONArray()
                    groupVote.put("voteSubject", title)
                    groupVote.put("endTime", time)
                    groupVote.put("multipleChoice", if(multi) 1 else 2)
                    groupVote.put("groupId", AppHelper.getInstance().model.getGroupInfo(groupId).groupId)
                    groupVote.put("isPublic", if(openResult) 1 else 2)
                    list.forEach {
                        if(it.isEmpty()){
                            MyToast.showToast("投票选项不能为空")
                            return
                        } else {
                            groupVoteOptionList.put(JSONObject().put("voteOption", it))
                        }
                    }

                    jsonBody.put("groupVote", groupVote)
                    jsonBody.put("groupVoteOptionList", groupVoteOptionList)

                    EMAPIManager.getInstance().createVote(jsonBody.toString(), object:
                        EMDataCallBack<String>() {
                        override fun onSuccess(value: String?) {
                            val result = Gson().fromJson<VoteCreateEntity>(value, VoteCreateEntity::class.java)

                            val voteJson = JSONObject()
                            voteJson.put(EaseConstant.VOTE_ID, result.entity?.groupVote?.id)
                            voteJson.put(EaseConstant.VOTE_SUBJECT, result.entity?.groupVote?.voteSubject)
                            voteJson.put(EaseConstant.VOTE_MULTIPLE_CHOICE, result.entity?.groupVote?.multipleChoice == 1)
                            voteJson.put(EaseConstant.VOTE_STATUS, result.entity?.groupVote?.voteStatus)
                            voteJson.put(EaseConstant.VOTE_END_TIME, result.entity?.groupVote?.endTime)
                            val options = JSONArray()

                            val optionList = result.entity?.groupVoteOptionList
                            var list = optionList
                            if (optionList != null) {
                                if(optionList.size > 2){
                                    list = optionList.subList(0, 3)
                                }
                            }

                            list?.forEach {
                                options.put(it.voteOption)
                            }
                            voteJson.put(EaseConstant.VOTE_OPTIONS, options)

                            runOnUiThread { setResult(RESULT_OK, Intent().putExtra(EaseConstant.MSG_EXT_VOTE, voteJson.toString()))
                                finish()
                            }
                        }

                        override fun onError(error: Int, errorMsg: String?) {
                            runOnUiThread {
                                Toast.makeText(this@VoteCreateActivity, "$error + $errorMsg", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                } catch (e: JSONException){

                }
            }
        }
    }

    override fun onRemove(position: Int) {
        val itemHeight = DensityUtil.dip2px(this, 52f)
        val itemCount = (recyclerView.adapter as VoteCreateAdapter).dataList.size
        val params = recyclerView.layoutParams
        params.height = itemHeight * itemCount
        recyclerView.layoutParams = params
    }
}