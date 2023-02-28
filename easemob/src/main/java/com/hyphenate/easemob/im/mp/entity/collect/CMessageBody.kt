package com.hyphenate.mp.entity.collect

import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 26/12/2018
 */
open class CMessageBody : MultiItemEntity, Serializable {

    override fun getItemType(): Int {
        return when (type) {
            "txt" -> CMessage.ITEM_TYPE_TXT
            "card" -> CMessage.ITEM_TYPE_TXT_CARD
            "img" -> CMessage.ITEM_TYPE_IMAGE
            "file" -> CMessage.ITEM_TYPE_FILE
            "loc" -> CMessage.ITEM_TYPE_LOCATION
            "audio" -> CMessage.ITEM_TYPE_AUDIO
            "video" -> CMessage.ITEM_TYPE_VIDEO
            "history" -> CMessage.ITEM_TYPE_HISTORY
            "link" -> CMessage.ITEM_TYPE_LINK
            else -> CMessage.ITEM_TYPE_DEFAULT
        }
    }

    var msg_id: String? = null
    var from_id: String? = null
    var msg: String? = null
    var to_id: String? = null
    var chat_type: String? = null
    var type: String? = null
    var timestamp: Long = 0L
}

class CTextMessageBody : CMessageBody(), Serializable {
    var cardUserId: Int = 0
    var realName: String? = null
    var avatar: String? = null
    var linkTitle: String? = null
    var linkImg: String? = null
}

class CVideoMessageBody : CMessageBody(), Serializable {
    var remoteUrl: String? = null
    var thumbUrl: String? = null
    var fileLength: Long = 0
    var duration: Int = 0
}

class CLocationMessageBody : CMessageBody(), Serializable {
    var address: String? = null
    var longitude: Double = 0.0
    var latitude: Double = 0.0

}

class CImageMessageBody : CMessageBody(), Serializable {
    var remoteUrl: String? = null
    var thumbnailUrl: String? = null
    var fileLength: Long = 0
    var width: Int? = 0
    var height: Int? = 0
}

class CFileMessageBody : CMessageBody(), Serializable {
    var remoteUrl: String? = null
    var fileLength: Long = 0
    var fileName: String? = null

}

class CAudioMessageBody : CMessageBody(), Serializable {
    var remoteUrl: String? = null
    var duration: Int = 0
}