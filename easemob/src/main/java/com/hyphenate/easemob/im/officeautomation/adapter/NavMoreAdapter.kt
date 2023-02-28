package com.hyphenate.easemob.im.officeautomation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easemob.easeui.glide.GlideUtils
import com.hyphenate.easemob.R

class NavMoreAdapter(private var iconArray : IntArray, private var nameArray : Array<String>, private var context: Context) : RecyclerView.Adapter<NavMoreAdapter.NavMoreViewHolder>() {

    var itemClickListener : OnItemClickListener? = null
    var showUnread = -1

    inner class NavMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int){
            val ivName = itemView.findViewById<TextView>(R.id.tv_grid_item_name)
            val pic = itemView.findViewById<ImageView>(R.id.iv_grid_item_icon)
            val unread = itemView.findViewById<ImageView>(R.id.iv_unread)
            ivName.text = nameArray[position]
            GlideUtils.load(context, iconArray[position], pic)
            itemView.setOnClickListener{
                itemClickListener?.onItemClick(position)
            }

            if(position == showUnread){
                unread.visibility = View.VISIBLE
            } else {
                unread.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavMoreViewHolder {
        return NavMoreViewHolder(LayoutInflater.from(context).inflate(R.layout.app_grid_item, parent, false))
    }

    override fun onBindViewHolder(holder: NavMoreViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return iconArray.size
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}