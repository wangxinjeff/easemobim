package com.hyphenate.easemob.im.officeautomation.domain

import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity

class VoteInfoEntity {
    var status : String = ""
    var errorCode : Int = 0
    var entity : EntityBean? = null
    var responseDate : Long = 0

    inner class EntityBean{
        var voted : MutableList<VotedBean>? = null
        var isPublic : Int = 0
        var rule : String = ""
        var voteStatus : Int = 0
        var createUser : MPUserEntity? = null
        var detail : MutableList<OptionBean>? = null
        var endTime : Long = 0
        var multipleChoice : Int = 0
        var totalCount : Int = 0
        var voteSubject : String = ""
    }

    inner class VotedBean{
        var id : Int = 0
        var userId : Int = 0
        var voteTime : Long = 0
        var optionId : Int = 0
    }

    inner class OptionBean{
        var id : Int = 0
        var voteOption : String = ""
        var voteId : Int = 0
        var count : Int = 0
    }

}