package com.hyphenate.easemob.im.officeautomation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easemob.R
import com.hyphenate.easemob.im.officeautomation.domain.VoteInfoEntity

class VoteTakeAdapter(private var context:Context) : RecyclerView.Adapter<VoteTakeAdapter.ViewHolder>() {

    private var infoEntity: VoteInfoEntity? = null
    var optionList : MutableList<VoteInfoEntity.OptionBean> = mutableListOf()
    var multipleChoice = false

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(bean: VoteInfoEntity.OptionBean) {
            val checkBox = itemView.findViewById<CheckBox>(R.id.checkbox)
            val content = itemView.findViewById<TextView>(R.id.tv_content)
            content.text = bean.voteOption
            checkBox.setOnClickListener(null)
            optionList.forEach {
                checkBox.isChecked = it.id == bean.id
            }

            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    if(!multipleChoice){
                        optionList.clear()
                        optionList.add(bean)
                        notifyDataSetChanged()
                    } else {
                        optionList.add(bean)
                    }
                } else {
                    if(optionList.size > 1){
                        optionList.remove(bean)
                    }
                }
            }

            itemView.setOnClickListener {
                if(checkBox.isChecked){
                    if(optionList.size > 1){
                        checkBox.isChecked = false
                    }
                } else {
                    checkBox.isChecked = true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.vote_take_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        infoEntity?.entity?.detail?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return infoEntity?.entity?.detail?.size ?: 0
    }

    fun setEntity(infoEntity: VoteInfoEntity) {
        this.infoEntity = infoEntity
        notifyDataSetChanged()
    }
}