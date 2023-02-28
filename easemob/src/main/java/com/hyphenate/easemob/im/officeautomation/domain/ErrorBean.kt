package com.hyphenate.easemob.im.officeautomation.domain

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 29/10/2018
 */
class ErrorBean{
    /**
     * status : ERROR
     * errorCode : password_wrong
     * errorDescription : 密码错误
     * errorTime : 1530782495635
     */

    var status: String? = null
    var errorCode: String? = null
    var errorDescription: String? = null
    var errorTime: Long = 0

    override fun toString(): String {
        return "ErrorBean{" +
                "status='" + status + '\''.toString() +
                ", errorCode='" + errorCode + '\''.toString() +
                ", errorDescription='" + errorDescription + '\''.toString() +
                ", errorTime=" + errorTime +
                '}'.toString()
    }
}