package com.hyphenate.mp.entity.collect

import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 26/12/2018
 */
class CMessage : MultiItemEntity, Serializable {

    companion object {
        const val ITEM_TYPE_DEFAULT: Int = 0
        const val ITEM_TYPE_TXT: Int = 1
        const val ITEM_TYPE_TXT_CARD: Int = 11
        const val ITEM_TYPE_IMAGE = 2
        const val ITEM_TYPE_FILE = 3
        const val ITEM_TYPE_LOCATION = 4
        const val ITEM_TYPE_AUDIO = 5
        const val ITEM_TYPE_VIDEO = 6
        const val ITEM_TYPE_HISTORY = 7
        const val ITEM_TYPE_LINK = 8
    }

    override fun getItemType(): Int {
        return when (colType) {
            "txt" -> ITEM_TYPE_TXT
            "card" -> ITEM_TYPE_TXT_CARD
            "img" -> ITEM_TYPE_IMAGE
            "file" -> ITEM_TYPE_FILE
            "loc" -> ITEM_TYPE_LOCATION
            "audio" -> ITEM_TYPE_AUDIO
            "video" -> ITEM_TYPE_VIDEO
            "history" -> ITEM_TYPE_HISTORY
            "link" -> ITEM_TYPE_LINK
            else -> ITEM_TYPE_DEFAULT
        }
    }

    inner class GroupInfoBody : Serializable {

        var name: String? = null
        var id: Int = 0
        var avatar: String? = null
        var key: String? = null
    }

    var id: String? = null
    var msgIds: String? = null
    var userId: Int = 0
    var friendId: Int = 0
    var fromId: String? = null
    var toId: String? = null
    var msgs: MutableList<CMessageBody>? = null
    var createTime: Long = 0L
    var colType: String? = null
    var groupInfo: GroupInfoBody? = null
    var colExt: String? = null

}