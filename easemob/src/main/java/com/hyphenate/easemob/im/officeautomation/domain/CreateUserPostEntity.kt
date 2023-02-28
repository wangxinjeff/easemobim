package com.hyphenate.easemob.im.officeautomation.domain

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 23/10/2018
 */

data class CreateUserPostEntity(var username: String?,
                           var password: String?,
                           var realName: String?,
                           var mobilephone: String?,
                           var tenantId: Int = 0,
                           var organizationId: Int = 0) {

}