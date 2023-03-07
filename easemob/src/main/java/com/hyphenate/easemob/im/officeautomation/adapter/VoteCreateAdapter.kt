package com.hyphenate.easemob.im.officeautomation.adapter

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easemob.R
import com.hyphenate.easemob.im.officeautomation.utils.MyToast

class VoteCreateAdapter(private var context : Context) : RecyclerView.Adapter<VoteCreateAdapter.VoteCreateViewHolder>() {

    var changeListener: OnOptionsChangeListener? = null
    var dataList = mutableListOf("", "")

    fun addOptions(){
        if (dataList.size < 20){
            dataList.add("")
            notifyItemRangeInserted(dataList.size - 1, 1)
            notifyItemRangeChanged(0, itemCount)
        } else {
            MyToast.showToast("最多20个选项")
        }
    }

    fun removeOption(position : Int){
        dataList.removeAt(position)
        notifyDataSetChanged()
        changeListener?.onRemove(position)
    }

    inner class VoteCreateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var etContent: EditText? = null
        var ivClose : ImageView? = null

        fun bind(position: Int, content: String){
            etContent = itemView.findViewById(R.id.et_content)
            ivClose = itemView.findViewById(R.id.iv_close)
            if(TextUtils.isEmpty(content)){
                etContent?.hint = "选项 ${position + 1}"
                etContent?.setText(content)
            } else {
                etContent?.setText(content)
            }
            if(dataList.size > 2){
                ivClose?.visibility = View.VISIBLE
            } else {
                ivClose?.visibility = View.INVISIBLE
            }

            val textWatcher = object : TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if(s.toString().length > 20){
                        MyToast.showToast("已超出最大字数限制")
                        etContent?.setText(s?.substring(0, 20))
                        etContent?.setSelection(20)
                    } else {
                        dataList[position] = s.toString()
                    }
                }
            }
            etContent?.addTextChangedListener(textWatcher)
            etContent?.tag = textWatcher

            ivClose?.setOnClickListener {
                removeOption(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoteCreateViewHolder {
        return VoteCreateViewHolder(LayoutInflater.from(context).inflate(R.layout.vote_create_item, parent, false))
    }

    override fun onBindViewHolder(holder: VoteCreateViewHolder, position: Int) {
        holder.bind(position, dataList[position])

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onViewDetachedFromWindow(holder: VoteCreateViewHolder) {
        super.onViewDetachedFromWindow(holder)

        val etContent = holder.etContent
        if(etContent != null && etContent.tag is TextWatcher){
            etContent.removeTextChangedListener(etContent.tag as TextWatcher)
        }
    }

    interface OnOptionsChangeListener{
        fun onRemove(position: Int)
    }
}