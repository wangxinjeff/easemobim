package com.hyphenate.easemob.im.officeautomation.domain

import com.hyphenate.chat.EMConversation
import com.hyphenate.easemob.easeui.domain.EaseUser

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 29/10/2018
 */
class SelectUser: EaseUser() {
    var type: EMConversation.EMConversationType = EMConversation.EMConversationType.Chat
}