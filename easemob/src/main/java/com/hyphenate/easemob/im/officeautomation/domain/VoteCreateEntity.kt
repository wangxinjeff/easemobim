package com.hyphenate.easemob.im.officeautomation.domain

class VoteCreateEntity {
    var status : String = ""
    var errorCode : Int = 0
    var entity : VoteEntity? = null
    var responseDate : Long = 0

    inner class VoteEntity{
        var groupVote : GroupVote? = null
        var groupVoteOptionList : MutableList<VoteOptionBean>? = null
    }

    inner class GroupVote{
        var id : String = ""
        var voteSubject : String = ""
        var endTime : String = ""
        var multipleChoice : Int = 0
        var groupId : String = ""
        var createUserId : String = ""
        var isPublic : Int = 0
        var voteStatus : Int = 0
    }

    inner class VoteOptionBean{
        var id : String = ""
        var voteOption : String = ""
        var voteId : String = ""
    }
}