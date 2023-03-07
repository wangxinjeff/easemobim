package com.hyphenate.easemob.im.officeautomation.domain

class VoteOptionEntity {
    var status : String = ""
    var errorCode : Int = 0
    var entities : MutableList<EntitiesBean>? = mutableListOf()
    var first : Boolean = true
    var last : Boolean = true
    var size : Int = 0
    var number : Int = 0
    var numberOfElements : Int = 0
    var totalPages : Int = 0
    var totalElements : Int = 0
    var responseDate : Long = 0

    inner class EntitiesBean{
        var id : Int = 0
        var userId : Int = 0
        var voteTime : Long = 0
        var optionId : Int = 0
        var userName : String = ""
        var avatar : String = ""
    }


}