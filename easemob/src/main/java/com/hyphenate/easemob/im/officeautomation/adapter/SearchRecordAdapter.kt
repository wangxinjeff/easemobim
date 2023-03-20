package com.hyphenate.easemob.im.officeautomation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.hyphenate.easemob.R

class SearchRecordAdapter : RecyclerView.Adapter<SearchRecordAdapter.RecordHolder>() {

    var recordList = mutableListOf<String>()
    var listener : OnItemSelectListener? = null

    inner class RecordHolder(itemView: View) : ViewHolder(itemView) {
        var tvContent : TextView? = null

        fun bind(position: Int){
            tvContent = itemView.findViewById(R.id.tv_content)
            tvContent?.text = recordList[position]
            itemView.setOnClickListener {
                listener?.onSelect(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
        return RecordHolder(LayoutInflater.from(parent.context).inflate(R.layout.search_record_item_view, parent, false))
    }

    override fun getItemCount(): Int {
        return recordList.size
    }

    override fun onBindViewHolder(holder: RecordHolder, position: Int) {
        holder.bind(position)
    }

    fun setData(recordList : MutableList<String>){
        this.recordList = recordList
        notifyDataSetChanged()
    }

    interface OnItemSelectListener{
        fun onSelect(position: Int)
    }
}