package com.hyphenate.easemob.im.officeautomation.domain

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 29/10/2018
 */
class SearchContactsBean {
    /**
     * status : OK
     * entities : [{"id":34,"createTime":1528981575000,"lastUpdateTime":1528981575000,"username":"oa003","password":"8027f6126b4c31f3aeb47d1ec17149cb","realName":"Android测试1","status":"enable","organizationId":25,"tenantId":15,"appkey":"1109180614099887#oaapp15","userPinyin":"Androidceshi1"}]
     * first : true
     * last : true
     * size : 10
     * number : 0
     * numberOfElements : 1
     * totalPages : 1
     * totalElements : 1
     */

    var status: String? = null
    var first: Boolean = false
    var last: Boolean = false
    var size: Int = 0
    var number: Int = 0
    var numberOfElements: Int = 0
    var totalPages: Int = 0
    var totalElements: Int = 0
    var entities: List<EntitiesBean>? = null

    class EntitiesBean {
        /**
         * id : 34
         * createTime : 1528981575000
         * lastUpdateTime : 1528981575000
         * username : oa003
         * password : 8027f6126b4c31f3aeb47d1ec17149cb
         * realName : Android测试1
         * status : enable
         * organizationId : 25
         * tenantId : 15
         * appkey : 1109180614099887#oaapp15
         * userPinyin : Androidceshi1
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
        var appkey: String? = null
        var easemobName: String? = null
        var userPinyin: String? = null
        var image: String? = null
        var mobilephone: String? = null
        var alias: String? = null
    }
}