package com.hyphenate.easemob.im.officeautomation.domain

import com.hyphenate.chat.EMMessage

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 29/10/2018
 */
class SearchMessagesBean{
    var type: EMMessage.ChatType = EMMessage.ChatType.Chat
    var avatar: String? = null
    var userName: String? = null
    var realName: String? = null
    var content: String? = null
    var easemobName: String? = null
    var messageId: String? = null
}