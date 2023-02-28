package com.hyphenate.easemob.im.officeautomation.domain

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 23/10/2018
 */
//String chatName, String members, int maxusers, String description, String isPublic, String newmemberCanreadHistory, String allowinvites, String avatar, String membersOnly,String owner,int userId

data class CreateGroupPostEntity(var chatName: String?,
                                 var members: String?,
                                 var maxusers: Int = 0,
                                 var description: String?,
                                 var isPublic: String?,
                                 var newmemberCanreadHistory: String?,
                                 var allowinvites: String?,
                                 var avatar: String?,
                                 var membersOnly: String?,
                                 var owner: String?,
                                 var userId: Int = 0) {


}