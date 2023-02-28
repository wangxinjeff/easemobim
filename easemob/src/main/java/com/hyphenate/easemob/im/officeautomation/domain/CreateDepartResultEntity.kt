package com.hyphenate.easemob.im.officeautomation.domain

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 22/10/2018
 */
open class CreateDepartResultEntity{

    var status: String? = null
    var entity: EntityBean? = null


    class EntityBean {
        var id: Int = 0
        var createTime: Long = 0
        var lastUpdateTime: Long = 0
        var orgName: String? = null
        var status: String? = null
        var rank: String ? = null
        var parentId: Int = 0
        var tenantId: Int = 0
    }
}

