package com.hyphenate.easemob.im.officeautomation.domain

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 23/10/2018
 */
class CreateUserResultEntity{
    var status: String? = null
    var message: String? = null
    var entity: EntityBean? = null

    class EntityBean {
        /**
         * id : 15
         * createTime : 1528370000884
         * lastUpdateTime : 1528370000884
         * username : 15810588081
         * password : 8027f6126b4c31f3aeb47d1ec17149cb
         * realName : 李萌
         * status : enable
         * organizationId : 0
         * tenantId : 3
         */

        var id: Int = 0
        var createTime: Long = 0
        var lastUpdateTime: Long = 0
        var username: String? = null
        var password: String? = null
        var realName: String? = null
        var status: String? = null
        var organizationId: Int = 0
        var tenantId: Int = 0
    }
}