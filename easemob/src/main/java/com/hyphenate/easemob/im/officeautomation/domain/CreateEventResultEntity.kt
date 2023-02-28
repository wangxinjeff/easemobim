package com.hyphenate.easemob.im.officeautomation.domain

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 23/10/2018
 */
class CreateEventResultEntity{

    var status: String? = null
    var errorDescription: String? = null
    var entity: EntityBean? = null

    class EntityBean {
        var id: Int = 0
        var createTime: Long = 0
        var lastUpdateTime: Long = 0
        var content: String? = null
        var startTime: Long = 0
        var endTime: Long = 0
        var deadTime: Long = 0
        var repeats: Int = 0
        var isAllday: Int = 0
        var remindType: Int = 0
        var note: String? = null
        var userId: Int = 0
        var easemobName: String? = null
        var tenantId: Int = 0
    }

}