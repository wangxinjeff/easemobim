package com.hyphenate.easemob.im.officeautomation.domain

import android.os.Parcel
import android.os.Parcelable
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity

class SearchEntity {
    var userList: MutableList<MPUserEntity>? = null
    var chatGroupList: MutableList<SearchGroupEntity>? = null
}


class SearchGroupEntity() :Parcelable {
    var id: Int = 0
    var createTime: Long = 0
    var lastUpdateTime: Long = 0
    var tenantId: Int = 0
    var name: String? = null
    var description: String? = null
    var avatar: String? = null
    var maxusers: Int? = 0
    var newMemberCanreadHistory: Boolean = false
    var allowInvites: Boolean = false
    var membersOnly: Boolean = false
    var isPublic: Boolean = false
    var ownerId: Int = 0
    var type: String? = null
    var createUserId: Int = 0
    var lastUpdateUserId: Int = 0
    var groupNotice: String? = null
    var imChatGroupId: String? = null
    var imChatGroup: ImChatGroupEntity? = null

    var disturb: Boolean = false
    var contract: Boolean = false
    var memberCount: Int = 0

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        createTime = parcel.readLong()
        lastUpdateTime = parcel.readLong()
        tenantId = parcel.readInt()
        name = parcel.readString()
        description = parcel.readString()
        avatar = parcel.readString()
        maxusers = parcel.readValue(Int::class.java.classLoader) as? Int
        newMemberCanreadHistory = parcel.readByte() != 0.toByte()
        allowInvites = parcel.readByte() != 0.toByte()
        membersOnly = parcel.readByte() != 0.toByte()
        isPublic = parcel.readByte() != 0.toByte()
        ownerId = parcel.readInt()
        type = parcel.readString()
        createUserId = parcel.readInt()
        lastUpdateUserId = parcel.readInt()
        groupNotice = parcel.readString()
        imChatGroupId = parcel.readString()
        imChatGroup = parcel.readParcelable(ImChatGroupEntity::class.java.classLoader)
        disturb = parcel.readByte() != 0.toByte()
        contract = parcel.readByte() != 0.toByte()
        memberCount = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeLong(createTime)
        parcel.writeLong(lastUpdateTime)
        parcel.writeInt(tenantId)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(avatar)
        parcel.writeValue(maxusers)
        parcel.writeByte(if (newMemberCanreadHistory) 1 else 0)
        parcel.writeByte(if (allowInvites) 1 else 0)
        parcel.writeByte(if (membersOnly) 1 else 0)
        parcel.writeByte(if (isPublic) 1 else 0)
        parcel.writeInt(ownerId)
        parcel.writeString(type)
        parcel.writeInt(createUserId)
        parcel.writeInt(lastUpdateUserId)
        parcel.writeString(groupNotice)
        parcel.writeString(imChatGroupId)
        parcel.writeParcelable(imChatGroup, flags)
        parcel.writeByte(if (disturb) 1 else 0)
        parcel.writeByte(if (contract) 1 else 0)
        parcel.writeInt(memberCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchGroupEntity> {
        override fun createFromParcel(parcel: Parcel): SearchGroupEntity {
            return SearchGroupEntity(parcel)
        }

        override fun newArray(size: Int): Array<SearchGroupEntity?> {
            return arrayOfNulls(size)
        }
    }
}

class ImChatGroupEntity() :Parcelable{
    var id: Int = 0
    var createTime: Long = 0
    var lastUpdateTime: Long = 0
    var tenantId: Int = 0
    var chatGroupId: Int = 0
    var appkey: String? = null
    var imChatGroupId: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        createTime = parcel.readLong()
        lastUpdateTime = parcel.readLong()
        tenantId = parcel.readInt()
        chatGroupId = parcel.readInt()
        appkey = parcel.readString()
        imChatGroupId = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeLong(createTime)
        parcel.writeLong(lastUpdateTime)
        parcel.writeInt(tenantId)
        parcel.writeInt(chatGroupId)
        parcel.writeString(appkey)
        parcel.writeString(imChatGroupId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImChatGroupEntity> {
        override fun createFromParcel(parcel: Parcel): ImChatGroupEntity {
            return ImChatGroupEntity(parcel)
        }

        override fun newArray(size: Int): Array<ImChatGroupEntity?> {
            return arrayOfNulls(size)
        }
    }
}
