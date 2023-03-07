package com.hyphenate.easemob.im.officeautomation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easemob.R
import com.hyphenate.easemob.easeui.utils.EaseSmileUtils
import com.hyphenate.easemob.im.mp.utils.UserProvider
import com.hyphenate.easemob.im.officeautomation.domain.VoteInfoEntity

class VoteResultAdapter(private var context:Context) : RecyclerView.Adapter<VoteResultAdapter.ResultHolder>() {

    private var infoEntity: VoteInfoEntity? = null
    var clickListener : OnItemClickListener? = null

    inner class ResultHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(bean: VoteInfoEntity.OptionBean) {
            val countView = itemView.findViewById<LinearLayout>(R.id.vote_count_view)
            val content = itemView.findViewById<TextView>(R.id.option_content)
            content.text = bean.voteOption
            val ratio = ((bean.count.toDouble() / infoEntity?.entity?.totalCount!!) * 100).toInt()
            val progress = itemView.findViewById<ProgressBar>(R.id.vote_progress)
            var voted = false
            infoEntity?.entity?.voted?.forEach {
                if (it.optionId == bean.id && it.userId == UserProvider.getInstance().loginUser.user_id) {
                    voted = true
                    content.setText(
                        EaseSmileUtils.getIconText(context, bean.voteOption + "[selected]"),
                        TextView.BufferType.SPANNABLE)
                }
            }

            if (infoEntity?.entity?.isPublic == 1 || (infoEntity?.entity?.isPublic == 2 && infoEntity?.entity?.rule.equals("owner"))) {
                countView.visibility = VISIBLE
                val count = itemView.findViewById<TextView>(R.id.tv_vote_count)
                val percentage = itemView.findViewById<TextView>(R.id.tv_vote_percentage)
                val arrow = itemView.findViewById<ImageView>(R.id.iv_right_arrow)
                count.text = "${bean.count}票"
                percentage.text = "$ratio%"
                if (bean.count <= 0) {
                    arrow.visibility = GONE
                } else {
                    arrow.visibility = VISIBLE
                    itemView.setOnClickListener {
                        clickListener?.onItemClick(bean, ratio)
                    }
                }
                progress.progress = ratio
            } else {
                countView.visibility = GONE
                if (voted) {
                    progress.progress = 100
                } else {
                    progress.progress = 0
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultHolder {
        return ResultHolder(
            LayoutInflater.from(context).inflate(R.layout.vote_result_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ResultHolder, position: Int) {
        infoEntity?.entity?.detail?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return infoEntity?.entity?.detail?.size ?: 0
    }

    fun setEntity(infoEntity: VoteInfoEntity) {
        this.infoEntity = infoEntity
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(option : VoteInfoEntity.OptionBean, ratio : Int)
    }
}