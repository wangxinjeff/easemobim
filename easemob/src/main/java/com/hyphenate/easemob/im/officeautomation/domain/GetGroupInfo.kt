package com.hyphenate.easemob.im.officeautomation.domain

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 29/10/2018
 */
class GetGroupInfo {
    var status: String? = null
    var entity: EntityBean? = null

    class EntityBean {
        /**
         * id : 22
         * createTime : 1529667066000
         * lastUpdateTime : 1529667066000
         * chatName : 戚保勇，杨森林，徐珂
         * avatar : /v1/tenants/9/mediafiles/c1369197-21d8-42ce-b4b8-fb3a3b112075SU1HXzE3NDg4Mzk5MC5qcGc=
         * owner : oa_9_947228_qibaoyong
         * newmemberCanreadHistory : false
         * allowinvites : true
         * membersOnly : false
         * tenantId : 9
         * status : enable
         * members : oa_9_947228_qibaoyong，oa_9_657993_yangsenlin，oa_9_657993_xuke
         * maxusers : 500
         * description : 阿狸测试群7
         * isPublic : false
         * easemobGroupId : 52710516785153
         * userId : 37
         */

        var id: Int = 0
        var createTime: Long = 0
        var lastUpdateTime: Long = 0
        var chatName: String? = null
        var avatar: String? = null
        var owner: String? = null
        var newmemberCanreadHistory: String? = null
        var allowinvites: String? = null
        var membersOnly: String? = null
        var tenantId: Int = 0
        var status: String? = null
        var members: String? = null
        var maxusers: Int = 0
        var description: String? = null
        var isPublic: String? = null
        var easemobGroupId: String? = null
        var userId: Int = 0
    }


}