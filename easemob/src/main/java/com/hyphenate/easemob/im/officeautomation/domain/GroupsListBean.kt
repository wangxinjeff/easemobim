package com.hyphenate.easemob.im.officeautomation.domain

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 29/10/2018
 */
class GroupsListBean{
    /**
     * status : OK
     * entities : [{"id":15,"createTime":1529650559000,"lastUpdateTime":1529650559000,"chatName":"戚保勇，杜坤，杨森林","avatar":"/v1/tenants/9/mediafiles/7b221c0a-52b4-4dc1-8e4a-1cd9fc452764SU1HXzE4NDc0MTEzNy5qcGc=","owner":"oa_9_947228_qibaoyong","newmemberCanreadHistory":"false","allowinvites":"true","membersOnly":"false","tenantId":9,"status":"enable","members":"oa_9_947228_qibaoyong，oa_9_657993_dukun，oa_9_657993_yangsenlin","maxusers":500,"description":"阿狸测试群1","isPublic":"false","easemobGroupId":"52693206892545","userId":37}]
     */

    var status: String? = null
    var message: String? = null
    var entities: List<EntitiesBean>? = null

    class EntitiesBean {
        /**
         * id : 15
         * createTime : 1529650559000
         * lastUpdateTime : 1529650559000
         * chatName : 戚保勇，杜坤，杨森林
         * avatar : /v1/tenants/9/mediafiles/7b221c0a-52b4-4dc1-8e4a-1cd9fc452764SU1HXzE4NDc0MTEzNy5qcGc=
         * owner : oa_9_947228_qibaoyong
         * newmemberCanreadHistory : false
         * allowinvites : true
         * membersOnly : false
         * tenantId : 9
         * status : enable
         * members : oa_9_947228_qibaoyong，oa_9_657993_dukun，oa_9_657993_yangsenlin
         * maxusers : 500
         * description : 阿狸测试群1
         * isPublic : false
         * easemobGroupId : 52693206892545
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
        var type: String? = null
    }
}