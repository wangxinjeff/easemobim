package com.hyphenate.easemob.im.officeautomation.widget.chatrow

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import com.hyphenate.chat.EMMessage
import com.hyphenate.easemob.R
import com.hyphenate.easemob.easeui.EaseConstant
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow
import com.hyphenate.easemob.easeui.widget.textview.AlignTextView
import com.hyphenate.easemob.im.officeautomation.domain.VoteMsgEntity
import com.hyphenate.easemob.im.officeautomation.utils.DensityUtil
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ViewConstructor")
class EaseChatRowVote(context: Context?, message: EMMessage?, position: Int,
                      adapter: EaseMessageAdapter?
) : EaseChatRow(context, message,
    position, adapter
) {

    private lateinit var voteTitle : AlignTextView
    private lateinit var voteOptionsLayout : LinearLayout
    private lateinit var ivVoteStatus : ImageView
    private lateinit var tvVoteStatus : TextView
    private lateinit var tvEndTime : TextView

    override fun onInflateView() {
        inflater.inflate(
            if (message.direct() == EMMessage.Direct.RECEIVE) R.layout.em_row_recv_vote else R.layout.em_row_sent_vote,
            this
        )
    }

    override fun onFindViewById() {
        voteTitle = findViewById(R.id.vote_title)
        voteOptionsLayout = findViewById(R.id.vote_options_layout)
        ivVoteStatus = findViewById(R.id.iv_icon_vote_status)
        tvVoteStatus = findViewById(R.id.tv_vote_status)
        tvEndTime = findViewById(R.id.tv_vote_end_time)

    }

    override fun onViewUpdate(msg: EMMessage?) {
        when (msg!!.status()) {
            EMMessage.Status.CREATE -> onMessageCreate()
            EMMessage.Status.SUCCESS -> onMessageSuccess()
            EMMessage.Status.FAIL -> onMessageError()
            EMMessage.Status.INPROGRESS -> onMessageInProgress()
        }
    }

    override fun onSetUpView() {
        val voteJson = message.getJSONObjectAttribute(EaseConstant.MSG_EXT_VOTE)
        val entity = Gson().fromJson<VoteMsgEntity>(voteJson.toString(), VoteMsgEntity::class.java)
        var subject = entity.subject
        val multipleChoice = entity.multipleChoice
        subject = if(multipleChoice){
            "$subject[多选]"
        } else {
            "$subject[单选]"
        }
        val spanBuilder = SpannableStringBuilder(subject)
        val span = ForegroundColorSpan(context.getColor(R.color.theme_color))
        spanBuilder.setSpan(span, subject.length - 4, subject.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        voteTitle.text = spanBuilder

        val options = entity.options
        voteOptionsLayout.removeAllViews()
        val layoutParams = LayoutParams(DensityUtil.dp2px(context, 198f), LayoutParams.WRAP_CONTENT)
        val item1 = LayoutInflater.from(context).inflate(R.layout.vote_msg_item, null, false)
        val item2 = LayoutInflater.from(context).inflate(R.layout.vote_msg_item, null, false)
        val item3 = LayoutInflater.from(context).inflate(R.layout.vote_msg_item, null, false)
        item1.layoutParams = layoutParams
        item2.layoutParams = layoutParams
        item3.layoutParams = layoutParams

        item1.findViewById<TextView>(R.id.vote_option).text = options.get(0)
        item2.findViewById<TextView>(R.id.vote_option).text = options.get(1)

        voteOptionsLayout.addView(item1)
        if(options.size > 2){
            voteOptionsLayout.addView(item2)
            item3.findViewById<TextView>(R.id.vote_option).text = options[2]
            item3.findViewById<View>(R.id.divider).visibility = GONE
            voteOptionsLayout.addView(item3)
        } else {
            item2.findViewById<View>(R.id.divider).visibility = GONE
            voteOptionsLayout.addView(item2)
        }

        when(entity.status){
            1 -> {
                tvVoteStatus.text = context.getString(R.string.on_the_ballot)
                tvEndTime.visibility = VISIBLE
            }
            2,3 ->{
                tvVoteStatus.text = context.getString(R.string.have_ended)
                tvEndTime.visibility = GONE
            }
            4 -> {
                tvVoteStatus.text = context.getString(R.string.deleted)
                tvEndTime.visibility = GONE
            }
        }
        val endTime = entity.endTime
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val date = format.parse(endTime)
        val time = SimpleDateFormat("MM-dd HH:mm", Locale.ENGLISH).format(Date(date.time))
        tvEndTime.text = String.format(context.getString(R.string.cut_off_time), time)
    }

    private fun onMessageCreate() {
        progressBar.visibility = GONE
        statusView.visibility = VISIBLE
    }

    private fun onMessageSuccess() {
        progressBar.visibility = GONE
        statusView.visibility = GONE
    }

    private fun onMessageError() {
        progressBar.visibility = GONE
        statusView.visibility = VISIBLE
    }

    private fun onMessageInProgress() {
        progressBar.visibility = VISIBLE
        statusView.visibility = GONE
    }

}