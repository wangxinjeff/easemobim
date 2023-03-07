package com.hyphenate.easemob.im.officeautomation.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easemob.R
import com.hyphenate.easemob.easeui.glide.GlideUtils
import com.hyphenate.easemob.easeui.utils.EaseUserUtils
import com.hyphenate.easemob.easeui.widget.AvatarImageView
import com.hyphenate.easemob.im.officeautomation.domain.VoteOptionEntity
import com.hyphenate.easemob.imlibs.mp.MPClient

class VoteOptionAdapter(private var context:Context) : RecyclerView.Adapter<VoteOptionAdapter.ViewHolder>() {

    private var optionEntity: VoteOptionEntity? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(bean: VoteOptionEntity.EntitiesBean) {
            val ivAvatar = itemView.findViewById<AvatarImageView>(R.id.iv_avatar)
            val tvName = itemView.findViewById<TextView>(R.id.tv_name)
            val user = EaseUserUtils.getUserInfo(bean.userId)
            var avatar = bean.avatar
            if (!TextUtils.isEmpty(avatar) && !avatar.startsWith("http")) {
                avatar = if (avatar.startsWith("/")) {
                    MPClient.get().appServer + avatar
                } else {
                    MPClient.get().appServer + "/" + avatar
                }
            }
            GlideUtils.load(context, avatar, R.drawable.ease_default_avatar, ivAvatar)
            EaseUserUtils.setUserNick(bean.userId, tvName)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.vote_option_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        optionEntity?.entities?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return optionEntity?.entities?.size ?: 0
    }

    fun setEntity(optionEntity: VoteOptionEntity) {
        this.optionEntity = optionEntity
        notifyDataSetChanged()
    }
}