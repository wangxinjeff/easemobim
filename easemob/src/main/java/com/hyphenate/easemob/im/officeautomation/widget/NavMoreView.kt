package com.hyphenate.easemob.im.officeautomation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easemob.R
import com.hyphenate.easemob.im.officeautomation.adapter.NavMoreAdapter

class NavMoreView(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : LinearLayout(context, attributeSet, defStyleAttr){
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    private lateinit var moreTitle : TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var iconArray : IntArray
    private lateinit var nameArray : Array<String>
    private lateinit var title : String
    private var adapter: NavMoreAdapter? = null
    private var itemClickListener: NavMoreAdapter.OnItemClickListener? = null
    private var height : Float = 0.0f

    init {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.nav_more_view, this)
        moreTitle = findViewById(R.id.more_title)
        recyclerView = findViewById(R.id.recycler_view)
    }

    fun setTitle(title : String){
        this.title = title
    }

    fun setItemIcons(icons : IntArray){
        iconArray = icons
    }

    fun setItemTitles(names : Array<String>){
        nameArray = names
    }

    fun setItemClickListener(listener: NavMoreAdapter.OnItemClickListener){
        itemClickListener = listener
        adapter?.itemClickListener = itemClickListener
    }

    fun build(){
        moreTitle.text = title
        val size = iconArray.size.coerceAtMost(nameArray.size)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        adapter = NavMoreAdapter(iconArray.take(size).toIntArray(), nameArray.take(size).toTypedArray(), context)
        recyclerView.adapter = adapter
        itemClickListener.let { adapter?.itemClickListener = it }
        val widthMeasureSpec = MeasureSpec.makeMeasureSpec((1 shl 30) - 1, MeasureSpec.AT_MOST)
        val heightMeasureSpec = MeasureSpec.makeMeasureSpec((1 shl 30) - 1, MeasureSpec.AT_MOST)
        measure(widthMeasureSpec, heightMeasureSpec)
        height = measuredHeight.toFloat()
    }

    fun show(animationListener: Animation.AnimationListener){
        val animation = TranslateAnimation(0f, 0f, height, 0f)
        animation.duration = 300
        animation.setAnimationListener(animationListener)
        startAnimation(animation)
    }

    fun hide(animationListener: Animation.AnimationListener){
        val animation = TranslateAnimation(0f, 0f, 0f, height)
        animation.duration = 300
        animation.setAnimationListener(animationListener)
        startAnimation(animation)
    }

    fun showBadge(position : Int){
        adapter?.showUnread = position
        adapter?.notifyDataSetChanged()
    }

    fun hideBadge(position : Int){
        adapter?.showUnread = -1
        adapter?.notifyDataSetChanged()
    }
}