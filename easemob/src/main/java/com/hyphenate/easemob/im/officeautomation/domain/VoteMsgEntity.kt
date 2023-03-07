package com.hyphenate.easemob.im.officeautomation.domain

import android.os.Parcel
import android.os.Parcelable

class VoteMsgEntity() : Parcelable{
    var id : String = ""
    var subject : String = ""
    var multipleChoice : Boolean = false
    var options : MutableList<String> = mutableListOf()
    var status : Int = 0
    var endTime : String = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()
        subject = parcel.readString().toString()
        multipleChoice = parcel.readByte() != 0.toByte()
        options = parcel.readArrayList(String::class.java.classLoader) as MutableList<String>
        status = parcel.readInt()
        endTime = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(subject)
        parcel.writeByte(if (multipleChoice) 1 else 0)
        parcel.writeList(options)
        parcel.writeInt(status)
        parcel.writeString(endTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VoteMsgEntity> {
        override fun createFromParcel(parcel: Parcel): VoteMsgEntity {
            return VoteMsgEntity(parcel)
        }

        override fun newArray(size: Int): Array<VoteMsgEntity?> {
            return arrayOfNulls(size)
        }
    }
}