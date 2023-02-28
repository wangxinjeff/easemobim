package com.hyphenate.easemob.im.mp.entity.history

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 19/11/2018
 */
open class HMessageBody


class HVideoMessageBody : HMessageBody() {
    var thumb_url: String? = null
    var remote_url: String? = null
}


class HTextMessageBody : HMessageBody() {
    var content: String? = null

    //名片
    var userId: Int = 0
    var imUserId: String? = null
    var userAvatar: String? = null
    var realName: String? = null

}


class HVoiceMessageBody : HMessageBody() {
    var remoteUrl: String? = null
    var duration: Int? = 0
}


class HLocationMessageBody : HMessageBody() {
    var address: String? = null
    var lat: Double = 0.0
    var lng: Double = 0.0
}


class HImageMessageBody : HMessageBody() {
    var remoteUrl: String? = null
    var thumbnailUrl: String? = null
    var displayName: String? = null
    var width: Int? = 0
    var height: Int? = 0
}

class HFileMessageBody : HMessageBody() {
    var remoteUrl: String? = null
    var displayName: String? = null
    var fileLength: Long? = 0
}