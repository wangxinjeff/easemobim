package com.hyphenate.easemob.im.mp.entity.history

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 19/11/2018
 */
class HMessage : MultiItemEntity {

    companion object {
        const val ITEM_TYPE_DEFAULT: Int = 0
        const val ITEM_TYPE_TXT: Int = 1
        const val ITEM_TYPE_TXT_CARD: Int = 11
        const val ITEM_TYPE_IMAGE = 2
        const val ITEM_TYPE_FILE = 3
        const val ITEM_TYPE_LOCATION = 4
        const val ITEM_TYPE_AUDIO = 5
        const val ITEM_TYPE_VIDEO = 6
    }

    override fun getItemType(): Int {
        return when (type) {
            "txt" -> ITEM_TYPE_TXT
            "card" -> ITEM_TYPE_TXT_CARD
            "image" -> ITEM_TYPE_IMAGE
            "file" -> ITEM_TYPE_FILE
            "location" -> ITEM_TYPE_LOCATION
            "voice" -> ITEM_TYPE_AUDIO
            "video" -> ITEM_TYPE_VIDEO
            else -> ITEM_TYPE_DEFAULT
        }
    }

    var type: String? = null
    var from: String? = null
    var avatar: String? = null
    var messageBody: HMessageBody? = null
    var timestamp: Long = 0L
}