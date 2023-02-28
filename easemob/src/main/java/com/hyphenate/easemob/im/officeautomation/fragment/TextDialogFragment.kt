package com.hyphenate.easemob.im.officeautomation.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.hyphenate.easemob.R
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity

/**
 * 展示引用消息的文本内容
 */
class TextDialogFragment : BaseDialogFragment() {
    lateinit var tvContent : TextView
    lateinit var content : String

    override fun setLayout(): Int {
        return R.layout.fragment_text_dialog
    }

    override fun initArgument(bundle: Bundle) {
        content = bundle.getString("content").toString()
        content = content.replace("&nbsp;", " ")
        if (content.contains("&lt;") && content.contains("&gt;")) {
            content = Html.fromHtml(Html.fromHtml(content).toString()).toString()
        } else if (content.contains("<html>") ||
            content.contains("<header>") ||
            content.contains("<body>") ||
            content.contains("<div>") ||
            content.contains("<a>") ||
            content.contains("<h>") ||
            content.contains("<ul>") ||
            content.contains("<li>") ||
            content.contains("<span>") ||
            content.contains("<strong>") ||
            content.contains("<b>") ||
            content.contains("<em>") ||
            content.contains("<cite>") ||
            content.contains("<dfn>") ||
            content.contains("<i>") ||
            content.contains("<big>") ||
            content.contains("<small>") ||
            content.contains("<font>") ||
            content.contains("<blockquote>") ||
            content.contains("<tt>") ||
            content.contains("<u>") ||
            content.contains("<del>") ||
            content.contains("<s>") ||
            content.contains("<strike>") ||
            content.contains("<sub>") ||
            content.contains("<sup>") ||
            content.contains("<img>") ||
            content.contains("<h1>") ||
            content.contains("<h2>") ||
            content.contains("<h3>") ||
            content.contains("<h4>") ||
            content.contains("<h5>") ||
            content.contains("<h6>") ||
            content.contains("<p>")
        ) {
            content = Html.fromHtml(content).toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView(savedInstanceState)
        initListener()
    }

    override fun initView(savedInstanceState: Bundle?) {
        tvContent = view?.findViewById(R.id.tv_content)!!
        tvContent.movementMethod = ScrollingMovementMethod.getInstance()
        if(!TextUtils.isEmpty(content)){
//            tvContent.setText(EaseSmileUtils.getSmiledText(context, content),
//                TextView.BufferType.SPANNABLE)
            tvContent.text = content
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        view?.setOnClickListener { dismiss() }
//        tvContent.setOnClickListener { dismiss() }
    }

    override fun initData() {

    }

    companion object{
        fun showDialog(
            activity: BaseActivity,
            content: String?
        ) {
            val fragment = TextDialogFragment()
            val bundle = Bundle()
            bundle.putString("content", content)
            fragment.arguments = bundle
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            fragment.show(transaction, "text_dialog")
        }
    }

    override fun onStart() {
        super.onStart()
        setDialogFullParams()
    }
}