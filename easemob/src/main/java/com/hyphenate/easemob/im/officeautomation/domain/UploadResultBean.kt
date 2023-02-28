package com.hyphenate.easemob.im.officeautomation.domain

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 29/10/2018
 * 上传头像结果
 */
class UploadResultBean{
    /**
     * uuid : 5391ad07-7e80-4e62-bc53-6502ec08fb62QGF2YXRhci5qcGc=
     * contentType : image/jpeg
     * contentLength : 4082
     * url : /v1/tenants/feaa7d48-d7f3-49bc-a238-247372204aaa/mediafiles/5391ad07-7e80-4e62-bc53-6502ec08fb62QGF2YXRhci5qcGc=
     * fileName : @avatar.jpg
     */

    var uuid: String? = null
    var contentType: String? = null
    var contentLength: Int = 0
    var url: String? = null
    var md5: String? = null
    var fileName: String? = null
}