package com.hyphenate.easemob.im.officeautomation.domain

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 23/10/2018
 */

//String content, String startTime, String endTime, String deadTime, int repeats, int isAllday, int remindType, String note
data class CreateEventPostEntity(var content: String?,
                          var startTime: String?,
                          var endTime: String?,
                          var deadTime: String?,
                          var repeats: Int,
                          var isAllday: Int,
                          var remindType: Int,
                          var note: String?){

}