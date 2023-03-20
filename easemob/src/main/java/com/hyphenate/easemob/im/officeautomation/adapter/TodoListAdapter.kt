package com.hyphenate.easemob.im.officeautomation.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.gson.Gson
import com.hyphenate.easemob.R
import com.hyphenate.easemob.easeui.utils.AvatarUtils
import com.hyphenate.easemob.easeui.widget.AvatarImageView
import com.hyphenate.easemob.im.officeautomation.domain.ExtUserType
import com.hyphenate.easemob.im.officeautomation.domain.TodoListEntity
import com.hyphenate.easemob.imlibs.mp.utils.DateTimeUtil
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class TodoListAdapter : RecyclerView.Adapter<TodoListAdapter.TodoListHolder>(){

    private var mData = mutableListOf<TodoListEntity.Entity>()
    var listener: OnItemSelectListener? = null

    inner class TodoListHolder(itemView: View) : ViewHolder(itemView){
        private lateinit var tvContent: TextView
        private lateinit var tvTime: TextView
        private lateinit var tvUser: TextView
        private lateinit var tvFrom: TextView
        private lateinit var ivAvatar: AvatarImageView

        fun bind(position: Int){
            tvContent = itemView.findViewById(R.id.tv_content)
            tvTime = itemView.findViewById(R.id.tv_time)
            tvUser = itemView.findViewById(R.id.tv_user)
            ivAvatar = itemView.findViewById(R.id.iv_avatar)
            tvFrom = itemView.findViewById(R.id.tv_come_from)

            val entity = mData[position]
            val format = SimpleDateFormat("MM/dd", Locale.ENGLISH)
            var time = format.format(Date(entity.updateTime))
            if(DateTimeUtil.isSameDay(entity.updateTime)){
                val format = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                time = format.format(Date(entity.updateTime))
            }
            tvTime.text = time
            tvUser.text = entity.msgEntity.fromId
            val extJson = JSONObject(entity.msgEntity.ext)
            val userType = Gson().fromJson<ExtUserType>(extJson.optString("userType"), ExtUserType::class.java)
            tvUser.text = userType.nick
            AvatarUtils.setAvatarContent(itemView.context, userType.nick, userType.avatar, ivAvatar)

            if(TextUtils.equals(entity.msgEntity.chatType, "groupchat")){
                if(extJson.optString("groupType").isNotEmpty()){
                    val groupJson = JSONObject(extJson.optString("groupType"))
                    tvFrom.text = "来自" + groupJson.optString("nick")
                    tvFrom.visibility = View.VISIBLE
                }
            } else {
                tvFrom.visibility = View.GONE
            }

            var content = ""
            val extMsg = extJson.optString("extMsg")
            if(extMsg.isNotEmpty()){
                val extMsgJson = JSONObject(extMsg)
                when(extMsgJson.optString("type")) {
                    "burn_after_reading" -> {
                        content = "[阅后即焚]"
                    }

                    "people_card" -> {
                        val contentJson = JSONObject(extMsgJson.optString("content"))
                        content = "[名片]" + contentJson.optString("realName")
                    }

                    "vote" -> {
                        content = "[投票]"
                    }
                }
            }

            if(content.isEmpty()){
                when(extJson.optString("msgType")){
                    "txt" -> {
                        content = entity.msgEntity.msg
                    }

                    "image" -> {
                        content = "[图片]"
                    }

                    "video" -> {
                        content = "[视频]"
                    }

                    "voice" -> {
                        content = "[语音]"
                    }

                    "voice" -> {
                        content = "[语音]"
                    }

                    "file" -> {
                        content = "[文件]"
                    }

                    "loc" -> {
                        content = "[位置]"
                    }
                }
            }

            if(content.isEmpty()){
                when(entity.msgEntity.type){
                    "txt" -> {
                        content = entity.msgEntity.msg
                    }

                    "image" -> {
                        content = "[图片]"
                    }

                    "video" -> {
                        content = "[视频]"
                    }

                    "voice" -> {
                        content = "[语音]"
                    }

                    "voice" -> {
                        content = "[语音]"
                    }

                    "file" -> {
                        content = "[文件]"
                    }

                    "loc" -> {
                        content = "[位置]"
                    }
                }
            }

            tvContent.text = content
            if (entity.status == 1){
                tvContent.setTextColor(ContextCompat.getColor(itemView.context, R.color.label_text_color))
            } else {
                tvContent.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
            }

            itemView.setOnClickListener { listener?.onItemClick(position, entity.status == 1) }
            itemView.setOnLongClickListener {it -> listener?.onItemLongClick(it, position, entity.status == 1)!! }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListHolder {
        return TodoListHolder(LayoutInflater.from(parent.context).inflate(R.layout.to_do_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: TodoListHolder, position: Int) {
        holder.bind(position)
    }

    fun setData(mData: MutableList<TodoListEntity.Entity>){
        this.mData = mData
        notifyDataSetChanged()
    }

    interface OnItemSelectListener{
        fun onItemClick(position: Int, isDeal: Boolean)
        fun onItemLongClick(view: View, position: Int, isDeal: Boolean): Boolean
    }
}