package com.hyphenate.easemob.im.officeautomation.fragment

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity
import com.hyphenate.util.DensityUtil


abstract class BaseDialogFragment : DialogFragment() {
    lateinit var context : BaseActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as BaseActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let { initArgument(it) }
        val view = inflater.inflate(setLayout(), container, false)
        setDialogAttrs()
        return view
    }

    abstract fun setLayout(): Int

    abstract fun initArgument(bundle: Bundle)

    private fun setDialogAttrs(){
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        initData()
        initListener()
    }

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun initListener()

    abstract fun initData()

    //宽度填满，高度自适应
    fun setDialogParams(){
        try {
            val dialogWindow = dialog!!.window
            val lp = dialogWindow!!.attributes
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialogWindow.attributes = lp
            val view = view
            if (view != null) {
                val params = view.layoutParams
                if (params is FrameLayout.LayoutParams) {
                    val margin = DensityUtil.dip2px(context, 30f) as Int
                    params.setMargins(margin, 0, margin, 0)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * dialog全屏
     */
    fun setDialogFullParams(){
        val dialogHeight = getContextRect(context)
        val height = if (dialogHeight == 0) ViewGroup.LayoutParams.MATCH_PARENT else dialogHeight
        setDialogParams(ViewGroup.LayoutParams.MATCH_PARENT, height, 0.0f)
    }

    private fun setDialogParams(width: Int, height: Int, dimAmount: Float) {
        try {
            val dialogWindow = dialog!!.window
            val lp = dialogWindow!!.attributes
            lp.dimAmount = dimAmount
            lp.width = width
            lp.height = height
            dialogWindow.attributes = lp
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    //获取内容区域
    private fun getContextRect(activity: Activity): Int {
        //应用区域
//        val outRect1 = Rect()
//        activity.window.decorView.getWindowVisibleDisplayFrame(outRect1)
//        return outRect1.height()
        return Resources.getSystem().displayMetrics.heightPixels
    }

}