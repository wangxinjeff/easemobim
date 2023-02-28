package com.hyphenate.easemob.im.officeautomation.domain

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 29/10/2018
 */
class PersonalInfoBean{
    /**   {"status":"OK",
     * "entity":{"id":28,"createTime":1528966130000,"lastUpdateTime":1528966130000,"username":"liyuzhao",
     * "realName":"liyuzhao","status":"enable","type":"admin","organizationId":21,"tenantId":9,"
     * appkey":"1101180614253625/oaapp9","easemobName":"oa_im_615898","userPinyin":"liyuzhao"}}
     *
     */

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
         * tenantId : 1a8b4672-9940-4655-99e4-27dc388ae773
         */

        var id: Int = 0
        var createTime: Long = 0
        var lastUpdateTime: Long = 0
        var username: String? = null
        var type: String? = null
        var realName: String? = null
        var status: String? = null
        var organizationId: Int = 0
        var tenantId: Int = 0
        var image: String? = null
        var mobilephone: String? = null
        var email: String? = null
        var easemobName: String? = null
        var userPinyin: String? = null
    }
}