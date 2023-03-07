package com.hyphenate.easemob.im.officeautomation.ui

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.easemob.R
import com.hyphenate.easemob.easeui.EaseConstant
import com.hyphenate.easemob.im.mp.AppHelper
import com.hyphenate.easemob.im.mp.utils.UserProvider
import com.hyphenate.easemob.im.officeautomation.adapter.VoteResultAdapter
import com.hyphenate.easemob.im.officeautomation.adapter.VoteTakeAdapter
import com.hyphenate.easemob.im.officeautomation.domain.VoteInfoEntity
import com.hyphenate.easemob.im.officeautomation.domain.VoteMsgEntity
import com.hyphenate.easemob.im.officeautomation.fragment.NormalDialogFragment
import com.hyphenate.easemob.im.officeautomation.utils.MyToast
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.enums.PopupPosition
import com.lxj.xpopup.impl.LoadingPopupView
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class VoteDetailActivity : BaseActivity(), View.OnClickListener, VoteResultAdapter.OnItemClickListener{

    private lateinit var ivBack: ImageView
    private lateinit var ivRight: ImageView
    private lateinit var voteTitle : TextView
    private lateinit var voteInfo : TextView
    private lateinit var statusView : LinearLayout
    private lateinit var voteStatus : TextView
    private lateinit var closeResult : TextView
    private lateinit var voteResultView : LinearLayout
    private lateinit var voteTakeView : LinearLayout
    private lateinit var bottomView : LinearLayout

    private lateinit var resultRecycler : RecyclerView
    private lateinit var takeRecycler : RecyclerView
    private lateinit var btnWantVote : Button
    private lateinit var btnTakeVote : Button
    private lateinit var voteResendView : LinearLayout
    private lateinit var voteCloseView : LinearLayout

    private var isPublic = true
    private var isOwner = false
    private var ownerName = ""
    private var endTime = ""
    private var votedCount = 0
    private var ownVoted = false
    private var isEnded = false
    private var multipleChoice = false
    private var subject = ""
    private var voteId = ""

    private var progressLoading: LoadingPopupView? = null

    private lateinit var basePopupView: BasePopupView
    lateinit var result : VoteInfoEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote_detail)
        voteId = intent.getStringExtra("voteId").toString()
        initView()
        initListener()
        initData()
    }

    private fun initView() {
        ivBack = findViewById(R.id.iv_back)
        ivRight = findViewById(R.id.iv_right)
        voteTitle = findViewById(R.id.tv_vote_title)
        voteInfo = findViewById(R.id.tv_vote_info)
        statusView = findViewById(R.id.status_view)
        voteStatus = findViewById(R.id.tv_vote_status)
        closeResult = findViewById(R.id.tv_close_result)
        voteResultView = findViewById(R.id.vote_result_view)
        voteTakeView = findViewById(R.id.vote_take_view)
        bottomView = findViewById(R.id.bottom_view)

        resultRecycler = findViewById(R.id.result_recycler)
        takeRecycler = findViewById(R.id.vote_take_recycler)
        btnWantVote = findViewById(R.id.btn_want_vote)
        btnTakeVote = findViewById(R.id.btn_take_vote)
        voteResendView = findViewById(R.id.vote_resend_view)
        voteCloseView = findViewById(R.id.vote_close_view)

        ivRight.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mp_ic_more))
        ivRight.visibility = View.GONE
        resultRecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = VoteResultAdapter(context)
            (adapter as VoteResultAdapter).clickListener = this@VoteDetailActivity
        }

        takeRecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = VoteTakeAdapter(context)
        }

        progressLoading = XPopup.Builder(this).asLoading("加载中...")
    }

    private fun initListener(){
        ivBack.setOnClickListener(this)
        ivRight.setOnClickListener(this)
        btnWantVote.setOnClickListener(this)
        btnTakeVote .setOnClickListener(this)
        voteResendView.setOnClickListener(this)
        voteCloseView.setOnClickListener(this)
    }

    private fun initData() {
        getVoteInfo()
    }

    private fun getVoteInfo(){
        showProgressDialog("加载中...")
        EMAPIManager.getInstance().getVoteInfo(voteId, object : EMDataCallBack<String>() {
            override fun onSuccess(value: String?) {
                result = Gson().fromJson<VoteInfoEntity>(value, VoteInfoEntity::class.java)
                isPublic = result.entity?.isPublic == 1
                isOwner = result.entity?.rule.equals("owner")
                multipleChoice = result.entity?.multipleChoice == 1
                val format = SimpleDateFormat("yy-MM-dd", Locale.ENGLISH)
                endTime = format.format(result.entity?.endTime)
                ownerName = result.entity?.createUser?.realName.toString()
                subject = result.entity?.voteSubject.toString()

                votedCount = result.entity?.totalCount!!
                result.entity?.voted?.forEach {
                    if(it.userId == UserProvider.getInstance().loginUser.user_id){
                        ownVoted = true
                    }
                }
                isEnded = result.entity?.voteStatus == 2 || result.entity?.voteStatus == 3

                AppHelper.getInstance().model.getMsgIdWithVoteId(voteId,
                    object : EMValueCallBack<MutableList<String>> {
                        override fun onSuccess(value: MutableList<String>?) {
                            runOnUiThread {
                                value?.forEach {
                                    val message = EMClient.getInstance().chatManager().getMessage(it)
                                    val json = message?.getJSONObjectAttribute(EaseConstant.MSG_EXT_VOTE)
                                    val entity = Gson().fromJson<VoteMsgEntity>(json.toString(), VoteMsgEntity::class.java)
                                    entity.status = result.entity?.voteStatus!!
                                    message.setAttribute(EaseConstant.MSG_EXT_VOTE, Gson().toJson(entity))
                                    EMClient.getInstance().chatManager().updateMessage(message)
                                }
                                if(result.entity?.voteStatus == 4){
                                    MyToast.showInfoToast(getString(R.string.vote_has_been_deleted))
                                    finish()
                                }
                            }
                        }

                        override fun onError(errorCode: Int, errorMsg: String?) {

                        }

                    })

                runOnUiThread {
                    hideProgressDialog()
                    (resultRecycler.adapter as VoteResultAdapter).setEntity(result)
                    (takeRecycler.adapter as VoteTakeAdapter).multipleChoice = multipleChoice
                    (takeRecycler.adapter as VoteTakeAdapter).setEntity(result)
                    refreshView()
                }
            }

            override fun onError(error: Int, errorMsg: String?) {
                runOnUiThread {
                    Toast.makeText(this@VoteDetailActivity, "$error + $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun refreshView(){
        subject = if(multipleChoice){
            "$subject[多选]"
        } else {
            "$subject[单选]"
        }
        val spanBuilder = SpannableStringBuilder(subject)
        val span = ForegroundColorSpan(getColor(R.color.theme_color))
        spanBuilder.setSpan(span, subject.length - 4, subject.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        voteTitle.text = spanBuilder

        voteInfo.text = String.format(getString(R.string.vote_info), ownerName, endTime, votedCount)

        if(isOwner){
            ivRight.visibility = View.VISIBLE
            bottomView.visibility = View.VISIBLE
            voteTakeView.visibility = View.GONE
            voteResultView.visibility = View.VISIBLE
            if(ownVoted){
                btnWantVote.visibility = View.GONE
            } else {
                btnWantVote.visibility = View.VISIBLE
            }
            if(isEnded || !isPublic){
                statusView.visibility = View.VISIBLE
                if(isEnded){
                    voteStatus.visibility = View.VISIBLE
                    btnWantVote.visibility= View.GONE
                    bottomView.visibility = View.GONE
                } else {
                    voteStatus.visibility = View.GONE
                    bottomView.visibility = View.VISIBLE
                }
                if (isPublic){
                    closeResult.visibility = View.GONE
                } else {
                    closeResult.visibility = View.VISIBLE
                }
            }
        } else {
            ivRight.visibility = View.GONE
            if(ownVoted){
                voteTakeView.visibility = View.GONE
                voteResultView.visibility = View.VISIBLE
                btnWantVote.visibility= View.GONE
            } else {
                voteTakeView.visibility = View.VISIBLE
            }

            if(isEnded || !isPublic){
                statusView.visibility = View.VISIBLE
                if(isEnded){
                    voteStatus.visibility = View.VISIBLE
                    voteTakeView.visibility = View.GONE
                    voteResultView.visibility = View.VISIBLE
                    btnWantVote.visibility= View.GONE
                } else {
                    voteStatus.visibility = View.GONE
                }
                if (isPublic){
                    closeResult.visibility = View.GONE
                } else {
                    closeResult.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.iv_back -> finish()
            R.id.iv_right -> {
                val attachPopupView = XPopup.Builder(this)
                    .hasShadowBg(false)
                    .popupPosition(PopupPosition.Bottom)
                    .isCenterHorizontal(false)
                    .isDestroyOnDismiss(true)
                    .isLightStatusBar(true)
                    .atView(ivRight)
                    .asAttachList(arrayOf(getString(R.string.remove_vote)), null,
                        { position, text ->

                            NormalDialogFragment.Builder(activity).setTitle(getString(R.string.remove_vote)).setOnConfirmClickListener(getString(R.string.delete)
                            ) {
                                showProgressDialog("删除中...")
                                EMAPIManager.getInstance().removeVote(voteId, object :
                                    EMDataCallBack<String>() {
                                    override fun onSuccess(value: String?) {
                                        AppHelper.getInstance().model.getMsgIdWithVoteId(voteId,
                                            object : EMValueCallBack<MutableList<String>> {
                                                override fun onSuccess(value: MutableList<String>?) {
                                                    runOnUiThread {
                                                        hideProgressDialog()
                                                        value?.forEach {
                                                            val message = EMClient.getInstance().chatManager().getMessage(it)
                                                            val json = message?.getJSONObjectAttribute(EaseConstant.MSG_EXT_VOTE)
                                                            val entity = Gson().fromJson<VoteMsgEntity>(json.toString(), VoteMsgEntity::class.java)
                                                            entity.status = 4
                                                            message.setAttribute(EaseConstant.MSG_EXT_VOTE, Gson().toJson(entity))
                                                            EMClient.getInstance().chatManager().updateMessage(message)
                                                        }
                                                        finish()
                                                    }
                                                }

                                                override fun onError(errorCode: Int, errorMsg: String?) {

                                                }

                                            })
                                    }

                                    override fun onError(error: Int, errorMsg: String?) {
                                        runOnUiThread {
                                            Toast.makeText(this@VoteDetailActivity, "$error + $errorMsg", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                })
                            }.show()
                        },0 ,0)
                attachPopupView.show()
            }
            R.id.btn_want_vote -> {
                voteTakeView.visibility = View.VISIBLE
                voteResultView.visibility = View.GONE
            }
            R.id.btn_take_vote -> {
                if((takeRecycler.adapter as VoteTakeAdapter).optionList.size > 0){
                    takeVote((takeRecycler.adapter as VoteTakeAdapter).optionList)
                } else {
                    Toast.makeText(this@VoteDetailActivity, "请选择选项", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.vote_resend_view -> {
                NormalDialogFragment.Builder(activity).setTitle(getString(R.string.resend_vote_to_conversation)).setOnConfirmClickListener("发送"
                ) {
                    val voteJson = JSONObject()
                    voteJson.put(EaseConstant.VOTE_ID, voteId)
                    voteJson.put(EaseConstant.VOTE_SUBJECT, result.entity?.voteSubject)
                    voteJson.put(EaseConstant.VOTE_MULTIPLE_CHOICE, multipleChoice)
                    voteJson.put(EaseConstant.VOTE_STATUS, result.entity?.voteStatus)
                    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                    voteJson.put(EaseConstant.VOTE_END_TIME, format.format(result.entity?.endTime))
                    val options = JSONArray()

                    val optionList = result.entity?.detail
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
                    setResult(RESULT_OK, Intent().putExtra(EaseConstant.MSG_EXT_VOTE, voteJson.toString()))
                    finish()
                }.show()
            }
            R.id.vote_close_view -> {
                NormalDialogFragment.Builder(activity).setTitle(getString(R.string.whether_close_vote)).setOnConfirmClickListener(getString(R.string.close_vote)
                ) {
                    EMAPIManager.getInstance().closeVote(voteId, object : EMDataCallBack<String>(){
                        override fun onSuccess(value: String?) {
                            AppHelper.getInstance().model.getMsgIdWithVoteId(voteId,
                                object : EMValueCallBack<MutableList<String>> {
                                    override fun onSuccess(value: MutableList<String>?) {
                                        runOnUiThread {
                                            getVoteInfo()
                                            value?.forEach {
                                                val message = EMClient.getInstance().chatManager().getMessage(it)
                                                val json = message?.getJSONObjectAttribute(EaseConstant.MSG_EXT_VOTE)
                                                val entity = Gson().fromJson<VoteMsgEntity>(json.toString(), VoteMsgEntity::class.java)
                                                entity.status = 2
                                                message.setAttribute(EaseConstant.MSG_EXT_VOTE, Gson().toJson(entity))
                                                EMClient.getInstance().chatManager().updateMessage(message)
                                            }
                                        }
                                    }

                                    override fun onError(errorCode: Int, errorMsg: String?) {

                                    }

                                })
                        }

                        override fun onError(error: Int, errorMsg: String?) {
                            runOnUiThread {
                                Toast.makeText(this@VoteDetailActivity, "$error + $errorMsg", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }.show()
            }
        }
    }

    override fun onItemClick(option: VoteInfoEntity.OptionBean, ratio : Int) {
        startActivity(Intent(this, VoteOptionDetailActivity::class.java).putExtra("title",option.voteOption).putExtra("id", option.id).putExtra("ratio", ratio))
    }

    private fun takeVote(options : MutableList<VoteInfoEntity.OptionBean>){
        val optionIds = JSONArray()
        options.forEach {
            optionIds.put(it.id)
        }

        val json = JSONObject()
        json.put("voteId", options[0].voteId)
        json.put("voteOptionIdList", optionIds)
        EMAPIManager.getInstance().takeVote(json.toString(), object : EMDataCallBack<String>(){
            override fun onSuccess(value: String?) {
                runOnUiThread { getVoteInfo() }
            }

            override fun onError(error: Int, errorMsg: String?) {
                runOnUiThread {
                    if(error == 400){
                        val jsonObject = errorMsg?.let { JSONObject(it) }
                        when(jsonObject?.optInt("errorCode")){
                            200001 -> {
                                MyToast.showInfoToast(getString(R.string.vote_has_been_closed))
                                getVoteInfo()
                            }
                            200003 -> {
                                MyToast.showInfoToast(getString(R.string.vote_has_been_deleted))
                                finish()
                            }
                            else -> {
                                Toast.makeText(this@VoteDetailActivity, "$error + $errorMsg", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@VoteDetailActivity, "$error + $errorMsg", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

}