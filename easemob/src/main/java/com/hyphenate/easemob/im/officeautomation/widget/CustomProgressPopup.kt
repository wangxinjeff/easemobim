package com.hyphenate.easemob.im.officeautomation.widget

import android.content.Context
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.NonNull
import com.hyphenate.easemob.R
import com.hyphenate.easemob.im.officeautomation.ui.FileTransferActivity
import com.lxj.xpopup.core.CenterPopupView
import com.lxj.xpopup.animator.PopupAnimator
import java.text.DecimalFormat


internal class CustomProgressPopup(@NonNull context: Context) : CenterPopupView(context), FileTransferActivity.ProgressListener {

    private lateinit var percent: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var prompt: TextView
    private lateinit var cancel: TextView
    private lateinit var confirm: TextView
    private lateinit var decimalFormat: DecimalFormat

    override fun getImplLayoutId(): Int {
        return R.layout.progress_layout
    }

    override fun onCreate() {
        super.onCreate()
        (context as FileTransferActivity).setProgressListener(this)
        percent = findViewById(R.id.tv_percent)
        progressBar = findViewById(R.id.progress_bar)
        prompt = findViewById(R.id.tv_prompt)
        cancel = findViewById(R.id.tv_cancel)
        confirm = findViewById(R.id.tv_confirm)
        decimalFormat = DecimalFormat("0.00")

        progressBar.progress = 0
        percent.text = "0%"

        confirm.setOnClickListener {
            dismiss()
        }

        cancel.setOnClickListener {
            dismiss() // 关闭弹窗
        }

    }

    override fun progressChanged(total: Long, progress: Long, type: Int) {
        val num = (decimalFormat.format(progress.toDouble() / total.toDouble()).toDouble() * 100).toInt()
        if (num < 100) {
            if (type == 0)
                prompt.text = "正在传输文件..."
            else
                prompt.text = "正在接收文件..."
        } else {
            if (type == 0)
                prompt.text = "文件传输完成"
            else
                prompt.text = "文件接收完成"

        }
        progressBar.progress = num
        percent.text = "$num%"
    }


    // 设置最大宽度，看需要而定
    override fun getMaxWidth(): Int {
        return super.getMaxWidth()
    }

    // 设置最大高度，看需要而定
    override fun getMaxHeight(): Int {
        return super.getMaxHeight()
    }

    // 设置自定义动画器，看需要而定
    override fun getPopupAnimator(): PopupAnimator {
        return super.getPopupAnimator()
    }

    /**
     * 弹窗的宽度，用来动态设定当前弹窗的宽度，受getMaxWidth()限制
     *
     * @return
     */
    override fun getPopupWidth(): Int {
        return 0
    }

    /**
     * 弹窗的高度，用来动态设定当前弹窗的高度，受getMaxHeight()限制
     *
     * @return
     */
    override fun getPopupHeight(): Int {
        return 0
    }

}