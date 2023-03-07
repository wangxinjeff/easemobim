package com.hyphenate.easemob.im.officeautomation.widget

import android.content.Context
import com.hyphenate.chat.EMMessage
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow
import com.hyphenate.easemob.easeui.widget.presenter.EaseChatRowPresenter
import com.hyphenate.easemob.im.officeautomation.widget.chatrow.EaseChatRowVote

open class EaseChatVotePresenter : EaseChatRowPresenter() {
    override fun onChecked(message: EMMessage?, b: Boolean) {

    }

    override fun onCreateChatRow(
        cxt: Context?,
        message: EMMessage?,
        position: Int,
        adapter: EaseMessageAdapter?
    ): EaseChatRow {
        return EaseChatRowVote(cxt, message, position, adapter)
    }

    override fun onBubbleClick(message: EMMessage?) {
    }
}