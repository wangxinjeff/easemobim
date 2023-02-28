package com.hyphenate.easemob.im.officeautomation.domain

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 23/10/2018
 */
class CreateGroupResultEntity{
    var status: String? = null
    var entity: EntitiesBean? = null

    class EntitiesBean {
        var id: Int = 0
        var createTime: Long = 0
        var lastUpdateTime: Long = 0
        var easemobGroupId: String? = null
        var status: String? = null
        var tenantId: String? = null
        var owner: String? = null
        var chatName: String? = null
        var members: String? = null
        var maxusers: Int = 0
        var description: String? = null
        var isPublic: Boolean = false
        var newmemberCanreadHistory: Boolean = false
        var allowinvites: Boolean = false
        var avatar: String? = null
        var membersOnly: Boolean = false
        var type: String? = null
    }
}