package com.hyphenate.easemob.im.officeautomation.domain

import com.hyphenate.chat.EMMessage

/**
 * 合并转发图片扩展字段
 */


data class SizeBean(var width: Int, var height: Int)

data class ExtMediaMessage(var type: String, var thumb_url: String, var remote_url: String, var size: SizeBean, var nick: String, var avatar: String, var timestamp: Long, var msgId: String, var chatType: EMMessage.ChatType, var fromImId: String, var toImId: String)


/**
 * 语音类型消息
 */
data class ExtVoiceMessage(var type: String, var nick: String, var avatar: String, var timestamp: Long, var remote_url: String, var duration: Int, var msgId: String, var chatType: EMMessage.ChatType, var fromImId: String, var toImId: String)


/**
 * 文本类型消息
 */
data class ExtTxtMessage(var type: String, var msg: String, var nick: String, var avatar: String, var timestamp: Long, var msgId: String, var chatType: EMMessage.ChatType, var fromImId: String, var toImId: String, var extMsg: String?)


/**
 * 位置类型消息
 */
data class ExtLocationMessage(var type: String, var nick: String, var avatar: String, var timestamp: Long, private val addr: String, private val lat: Double, private val lng: Double, var msgId: String, var chatType: EMMessage.ChatType, var fromImId: String, var toImId: String)


/**
 * 合并转发图片扩展字段
 */
data class ExtFileMessage(var type: String, var remote_url: String, var display_name: String, var file_size: Long, var nick: String, var avatar: String, var timestamp: Long, var msgId: String, var chatType: EMMessage.ChatType, var fromImId: String, var toImId: String)


/**
 * 合并转发的消息内容
 */
data class ExtMsg(var type: String, var title: String, var contents: List<Any>)
