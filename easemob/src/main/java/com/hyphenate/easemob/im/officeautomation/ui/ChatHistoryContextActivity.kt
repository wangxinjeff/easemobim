package com.hyphenate.easemob.im.officeautomation.ui

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.gson.Gson
import com.hyphenate.easemob.easeui.glide.GlideUtils
import com.hyphenate.easemob.easeui.player.MediaManager
import com.hyphenate.easemob.easeui.ui.EaseShowBigImageActivity
import com.hyphenate.easemob.easeui.ui.EaseShowNormalFileActivity
import com.hyphenate.easemob.easeui.ui.EaseShowVideoActivity
import com.hyphenate.easemob.easeui.utils.AvatarUtils
import com.hyphenate.easemob.easeui.utils.EaseSmileUtils
import com.hyphenate.easemob.easeui.widget.EaseTitleBar
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack
import com.hyphenate.easemob.imlibs.mp.utils.MPLog
import com.hyphenate.easemob.imlibs.mp.utils.MPPathUtil
import com.hyphenate.easemob.R
import com.hyphenate.easemob.im.officeautomation.domain.MsgEntity
import com.hyphenate.easemob.im.officeautomation.domain.MsgUserInfoEntity
import com.hyphenate.easemob.im.officeautomation.domain.SearchConversation
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager
import com.hyphenate.easemob.im.officeautomation.utils.CommonUtils
import com.hyphenate.mp.entity.collect.CMessage
import com.hyphenate.util.DateUtils
import com.hyphenate.util.PathUtil
import com.hyphenate.util.TextFormater
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.File
import java.lang.ref.SoftReference
import java.util.*

class ChatHistoryContextActivity : BaseActivity() {

    private lateinit var titleBar: EaseTitleBar
    private lateinit var rv: RecyclerView
    private lateinit var progressDialog: ProgressDialog
    private var timeStamp: Long = 0
    private lateinit var searchConversation: SearchConversation
    private lateinit var msgList: MutableList<MsgEntity>
    private var userInfoList: MutableList<MsgUserInfoEntity>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_history)

        titleBar = findViewById(R.id.title_bar)
        rv = findViewById(R.id.rv)

        timeStamp = intent.getLongExtra("timeStamp", 0)
        searchConversation = intent.getParcelableExtra("bean")!!
        msgList = mutableListOf()

        titleBar.setLeftLayoutClickListener { finish() }

        val layoutManager = LinearLayoutManager(this)
        rv.layoutManager = layoutManager
        if (rv.adapter == null)
            rv.adapter = ChatHistoryContextAdapter(msgList)
        (rv.adapter as ChatHistoryContextAdapter).setEnableLoadMore(false)

        progressDialog = ProgressDialog(this)
        request()

        titleBar.title = "聊天详情"
    }

    fun request() {
        progressDialog.show()
        val queryType = searchConversation.chatType
        val msgType = "all"
        EMAPIManager.getInstance().getSearchMsg(timeStamp, timeStamp, searchConversation.fromId, searchConversation.toId, queryType, 1, 200, msgType, "", false, true, object : EMDataCallBack<String>() {
            override fun onSuccess(value: String?) {
                val result = JSONObject(value)
                val entity = result.getJSONObject("entity")
                val msgs = entity.getJSONArray("entities")
                for (i in 0 until msgs.length()) {
                    val msg = Gson().fromJson<MsgEntity>(msgs[i].toString(), MsgEntity::class.java)
                    if (msg.userInfo == null)
                        msgList.add(msg)
                    else
                        userInfoList = msg.userInfo
                }

                runOnUiThread {
                    progressDialog.dismiss()
                    rv.adapter = ChatHistoryContextAdapter(msgList)
                }

            }

            override fun onError(error: Int, errorMsg: String?) {
                runOnUiThread {
                    progressDialog.dismiss()
                }
            }

        })
    }


    inner class ChatHistoryContextAdapter(msgsEntity: MutableList<MsgEntity>?) : BaseMultiItemQuickAdapter<MsgEntity, BaseViewHolder>(msgsEntity) {

        init {
            addItemType(CMessage.ITEM_TYPE_TXT, R.layout.item_chats_list_history)
            addItemType(CMessage.ITEM_TYPE_TXT_CARD, R.layout.item_chats_list_txt_card)
            addItemType(CMessage.ITEM_TYPE_LINK, R.layout.item_chats_list_txt_link)
            addItemType(CMessage.ITEM_TYPE_IMAGE, R.layout.item_chats_h_image)
            addItemType(CMessage.ITEM_TYPE_FILE, R.layout.item_chats_h_file)
            addItemType(CMessage.ITEM_TYPE_AUDIO, R.layout.item_chats_h_voice)
            addItemType(CMessage.ITEM_TYPE_VIDEO, R.layout.item_chats_h_video)
            addItemType(CMessage.ITEM_TYPE_LOCATION, R.layout.item_chats_h_location)
            addItemType(CMessage.ITEM_TYPE_DEFAULT, R.layout.item_chats_list_history)
        }

        /**
         * iv_video = itemView.findViewById(R.id.iv_video);
         * rl_video = itemView.findViewById(R.id.rl_video);
         * @param helper
         * @param item
         */

        override fun convert(helper: BaseViewHolder, item: MsgEntity) {

            if ("groupchat" == searchConversation.chatType) {
                userInfoList?.let {
                    for (index in it.indices) {
                        if (item.from_id == it[index].key) {
                            helper.setText(R.id.name, it[index].real_name)
                            AvatarUtils.setAvatarContent(this@ChatHistoryContextActivity, it[index].real_name, it[index].avatar, helper.getView(R.id.iv_avatar))
                            break
                        }
                    }
                }
            } else {
                if (item.from_id == searchConversation.fromId) {
                    helper.setText(R.id.name, searchConversation.fromName)
                    AvatarUtils.setAvatarContent(this@ChatHistoryContextActivity, searchConversation.fromName, searchConversation.fromAvatar, helper.getView(R.id.iv_avatar))
                } else {
                    helper.setText(R.id.name, searchConversation.toName)
                    AvatarUtils.setAvatarContent(this@ChatHistoryContextActivity, searchConversation.toName, searchConversation.toAvatar, helper.getView(R.id.iv_avatar))
                }
            }
            //日期、时间
            val timeStr = DateUtils.getTimestampString(Date(item.timestamp))
            helper.setText(R.id.time, timeStr)
            when (item.type) {
                "txt" -> {
                    helper.setText(R.id.message, EaseSmileUtils.getSmiledText(this@ChatHistoryContextActivity, item.msg))
                }
                "img" -> {
                    val remoteUrl = item.url
                    GlideUtils.loadFromRemote(this@ChatHistoryContextActivity, remoteUrl, R.drawable.ease_default_image, helper.getView(R.id.iv_image))
                    helper.itemView.setOnClickListener {
                        val intent = Intent(this@ChatHistoryContextActivity, EaseShowBigImageActivity::class.java)
                        intent.putExtra("remote_url", remoteUrl)
                        startActivity(intent)
                    }
                }
                "file" -> {
                    val remoteUrl = item.url
                    val displayName = item.filename
                    val fileSize = item.file_length
                    helper.setText(R.id.tv_file_name, displayName)
                    helper.setText(R.id.tv_file_size, TextFormater.getDataSize(fileSize))


                    helper.itemView.setOnClickListener {
                        val intent = Intent(this@ChatHistoryContextActivity, EaseShowNormalFileActivity::class.java)
                        intent.putExtra("remote_url", remoteUrl)
                        intent.putExtra("display_name", displayName)
                        startActivity(intent)
                    }
                }
                "loc" -> {
//                    val address = item.address
//                    val lat = item.latitude
//                    val lng = item.longitude
//                    helper.setText(R.id.tv_location, address)
//
//                    helper.itemView.setOnClickListener {
//                        val intent = Intent(this@ChatHistoryContextActivity, EMBaiduMapActivity::class.java)
//                        intent.putExtra("latitude", lat)
//                        intent.putExtra("longitude", lng)
//                        intent.putExtra("address", address)
//                        startActivity(intent)
//                    }


                }
                "audio" -> {
                    val duration = item.length
                    val remoteUrl = item.url

                    helper.itemView.setOnClickListener {
                        downloadFile(remoteUrl!!, helper.getView(R.id.iv_audio_play))
                    }
                    helper.setText(R.id.tv_audio_duration, "$duration\"")
                }

                "video" -> {

                    GlideUtils.load(this@ChatHistoryContextActivity, item.url, R.drawable.default_image, helper.getView(R.id.iv_video_image))


                    helper.itemView.setOnClickListener {
                        val intent = Intent(this@ChatHistoryContextActivity, EaseShowVideoActivity::class.java)
                        intent.putExtra("remote_url", item.url)
                        startActivity(intent)
                    }
                }
                else -> try {
                    helper.setText(R.id.message, "[特殊消息]")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    private fun downloadFile(remoteUrl: String, animView: ImageView) {
        val voiceDirFile = MPPathUtil.getInstance().voicePath

        if (voiceDirFile == null) {
            MPLog.e("info", "voiceDirFile is null")
            return
        }
        val fileName = CommonUtils.getMd5Hash(remoteUrl)
        if (fileName == null) {
            MPLog.e("info", "file Name is null")
            return
        }
        val voiceFile = File(voiceDirFile, fileName)
        if (voiceFile.exists() && voiceFile.isFile) {
            playLocalVoice(voiceFile.path, animView)
            return
        }
        EMAPIManager.getInstance().downloadFile(remoteUrl, voiceFile.path, object : EMDataCallBack<String>() {
            override fun onSuccess(value: String) {
                if (isFinishing) {
                    return
                }
                runOnUiThread { playLocalVoice(voiceFile.path, animView) }
            }

            override fun onError(error: Int, errorMsg: String) {
                if (isFinishing) {
                    return
                }
                runOnUiThread { Toast.makeText(applicationContext, getString(R.string.tip_audio_download_failed), Toast.LENGTH_SHORT).show() }
            }
        })
    }

    private fun playLocalVoice(localVoicePath: String, animView: ImageView) {

        if (MediaManager.getManager().isPlaying) {
            animView.setImageResource(R.drawable.ease_chatfrom_voice_playing)
            MediaManager.getManager().release()
            return
        }

        animView.setImageResource(R.drawable.voice_from_icon)

        val animationDrawable = animView.drawable as AnimationDrawable
        animationDrawable.start()

        MediaManager.getManager().playSound(localVoicePath, 0, MediaPlayer.OnCompletionListener {
            if (isFinishing) {
                return@OnCompletionListener
            }
            animationDrawable.stop()
            animView.setImageResource(R.drawable.ease_chatfrom_voice_playing)
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        MediaManager.getManager().release()
    }

    private fun getWebTitle(url: String?): List<String>? {
        val webContent = ArrayList<String>()
        return try {
            //还是一样先从一个URL加载一个Document对象。
            val doc = Jsoup.connect(url).get()
            val links = doc.select("head")
            val image = doc.select("img[src$=.jpg]")
            val titlelinks = links[0].select("title")
            val imagelinks = image[0].attributes().asList()[1]
            webContent.add(titlelinks[0].text())
            webContent.add(imagelinks.value)
            webContent
        } catch (e: Exception) {
            null
        }

    }
}