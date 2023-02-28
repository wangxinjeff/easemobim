package com.hyphenate.easemob.im.officeautomation.domain

import android.os.Parcel
import android.os.Parcelable
import com.chad.library.adapter.base.entity.MultiItemEntity

class SearchMsgEntity {

    var userInfo: MutableList<MsgUserInfoEntity>? = null
    var user: MutableList<MsgEntity>? = null
    var groupInfo: MutableList<MsgUserInfoEntity>? = null
    var group: MutableList<MsgEntity>? = null

    fun getSearchMsgEntity(): MutableList<SearchConversation>? {
        val list = mutableListOf<SearchConversation>()
        val msgList = mutableListOf<MsgEntity>()

        if (user != null && user!!.size > 0) {
            msgList.addAll(user!!)
        }
        if (group != null && group!!.size > 0) {
            msgList.addAll(group!!)
        }

        for (msg in msgList) {
            val searchConversation = SearchConversation()
            if ("groupchat" == msg.chat_type) {
                for (info in groupInfo!!) {
                    if (msg.to_id == info.key) {
                        searchConversation.fromId = msg.from_id
                        searchConversation.toId = msg.to_id
                        searchConversation.groupName = info?.name
                        searchConversation.groupAvatar = info?.avatar
                        searchConversation.count = msg.count
                        if(msg.msg?.count()!! > 4000) {
                            searchConversation.msg = ""
                        } else {
                            searchConversation.msg = msg.msg
                        }
                        searchConversation.chatType = msg.chat_type
                        searchConversation.type = msg.type
                        break
                    }
                }
            } else {
                for (info in userInfo!!) {
                    if (msg.from_id == info.key) {
                        searchConversation.fromName = if (info.alias.isNullOrEmpty()) info.real_name else info.alias
                        searchConversation.fromId = msg.from_id
                        searchConversation.fromAvatar = info?.avatar

                        searchConversation.count = msg.count
                        if(msg.msg?.count()!! > 4000) {
                            searchConversation.msg = ""
                        } else {
                            searchConversation.msg = msg.msg
                        }
                        searchConversation.type = msg.type
                        searchConversation.chatType = msg.chat_type
                        break
                    }
                }
                for (info in userInfo!!) {
                    if (msg.to_id == info.key) {

                        searchConversation.toName = if (info.alias.isNullOrEmpty()) info.real_name else info.alias
                        searchConversation.toId = msg.to_id
                        searchConversation.toAvatar = info?.avatar

                        searchConversation.count = msg.count
                        if(msg.msg?.count()!! > 4000) {
                            searchConversation.msg = ""
                        } else {
                            searchConversation.msg = msg.msg
                        }
                        searchConversation.type = msg.type
                        searchConversation.chatType = msg.chat_type
                        break
                    }
                }
            }
            list.add(searchConversation)
        }


        return list
    }

}

class SearchConversation() : Parcelable {

    var toName: String? = null
    var fromName: String? = null
    var fromAvatar: String? = null
    var toId: String? = null
    var fromId: String? = null
    var toAvatar: String? = null
    var count: Int = 0
    var msg: String? = null
    var chatType: String? = null
    var type: String? = null
    var groupName: String? = null
    var groupAvatar: String? = null

    constructor(parcel: Parcel) : this() {
        toName = parcel.readString()
        fromName = parcel.readString()
        fromAvatar = parcel.readString()
        toId = parcel.readString()
        fromId = parcel.readString()
        toAvatar = parcel.readString()
        count = parcel.readInt()
        msg = parcel.readString()
        chatType = parcel.readString()
        type = parcel.readString()
        groupName = parcel.readString()
        groupAvatar = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(toName)
        parcel.writeString(fromName)
        parcel.writeString(fromAvatar)
        parcel.writeString(toId)
        parcel.writeString(fromId)
        parcel.writeString(toAvatar)
        parcel.writeInt(count)
        parcel.writeString(msg)
        parcel.writeString(chatType)
        parcel.writeString(type)
        parcel.writeString(groupName)
        parcel.writeString(groupAvatar)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchConversation> {
        override fun createFromParcel(parcel: Parcel): SearchConversation {
            return SearchConversation(parcel)
        }

        override fun newArray(size: Int): Array<SearchConversation?> {
            return arrayOfNulls(size)
        }
    }

}


class MsgUserInfoEntity {

    var alias: String? = null
    var real_name: String? = null
    var id: Int = 0
    var avatar: String? = null
    var key: String? = null

    var name: String? = null
}

class MsgEntity : MultiItemEntity {

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
        return when (type) {
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


    var msg: String? = null
    var chat_type: String? = null
    var from_id: String? = null
    var count: Int = 0
    var to_id: String? = null
    var type: String? = null
    var key: String? = null
    var timestamp: Long = 0

    var userInfo: MutableList<MsgUserInfoEntity>? = null

    var filename: String? = null
    var url: String? = null
    var file_length: Long = 0
    var length: Int = 0
}