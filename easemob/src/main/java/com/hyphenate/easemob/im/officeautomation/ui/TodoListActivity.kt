package com.hyphenate.easemob.im.officeautomation.ui

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.View.*
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.easemob.R
import com.hyphenate.easemob.im.officeautomation.adapter.TodoListAdapter
import com.hyphenate.easemob.im.officeautomation.domain.TodoListEntity
import com.hyphenate.easemob.im.officeautomation.utils.Constant
import com.hyphenate.easemob.im.officeautomation.utils.MyToast
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack
import com.hyphenate.easemob.imlibs.mp.utils.MPLog
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager
import org.json.JSONException
import org.json.JSONObject

class TodoListActivity : BaseActivity(), OnClickListener, TodoListAdapter.OnItemSelectListener{

    private val TAG = "TodoListActivity"
    private lateinit var ivBack: ImageView
    lateinit var unDealView : ConstraintLayout
    lateinit var dealView : ConstraintLayout
    lateinit var tvUnDeal : TextView
    lateinit var tvDeal : TextView
    lateinit var unDealTitle : ConstraintLayout
    private lateinit var dealTitle : ConstraintLayout
    lateinit var unDealArrow : ImageView
    lateinit var dealArrow : ImageView
    lateinit var unDealRecycler : RecyclerView
    lateinit var dealRecycler : RecyclerView
    lateinit var unDealAdapter: TodoListAdapter
    lateinit var dealAdapter: TodoListAdapter
    lateinit var todoListEntity: TodoListEntity

    private lateinit var selectPopupWindow: PopupWindow
    var unDealHidden = false
    var dealHidden = false
    var unDealListHeight = 0
    var dealListHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_list)
        initView()
        initData()
        initListener()
    }

    private fun initView() {
        ivBack = findViewById(R.id.iv_back)
        unDealView = findViewById(R.id.un_deal_view)
        dealView = findViewById(R.id.deal_view)
        tvUnDeal = findViewById(R.id.tv_un_deal)
        tvDeal = findViewById(R.id.tv_deal)
        unDealTitle = findViewById(R.id.un_deal_title)
        dealTitle = findViewById(R.id.deal_title)
        unDealArrow = findViewById(R.id.iv_un_deal_arrow)
        dealArrow = findViewById(R.id.iv_deal_arrow)
        unDealRecycler = findViewById(R.id.un_deal_recycler)
        dealRecycler = findViewById(R.id.deal_recycler)

        unDealAdapter = TodoListAdapter();
        unDealRecycler.setHasFixedSize(true)
        unDealRecycler.layoutManager = LinearLayoutManager(this)
        unDealRecycler.adapter = unDealAdapter

        dealAdapter = TodoListAdapter()
        dealRecycler.setHasFixedSize(true)
        dealRecycler.layoutManager = LinearLayoutManager(this)
        dealRecycler.adapter = dealAdapter
    }

    private fun initData(){

    }

    private fun initListener(){
        ivBack.setOnClickListener(this)
        unDealTitle.setOnClickListener(this)
        dealTitle.setOnClickListener(this)

        unDealAdapter.listener = this
        dealAdapter.listener = this
    }

    private fun fetchToDoList(){
        showProgressDialog("加载中")
        EMAPIManager.getInstance().getAllToDoList(object : EMDataCallBack<String>(){
            override fun onSuccess(value: String?) {
                try {
                    val json = JSONObject(value)
                    runOnUiThread {
                        hideProgressDialog()
                        todoListEntity = TodoListEntity.create(json)
                        unDealAdapter.setData(todoListEntity.unDealList)
                        dealAdapter.setData(todoListEntity.dealList)
                        tvDeal.text = String.format(getString(R.string.have_completed), todoListEntity.dealList.size)
                        if(todoListEntity.unDealList.size > 0){
                            unDealView.visibility = VISIBLE
                        } else {
                            unDealView.visibility = GONE
                        }

                        if(todoListEntity.dealList.size > 0){
                            dealView.visibility = VISIBLE
                        } else {
                            dealView.visibility = GONE
                        }

//                        unDealRecycler.measure(0 ,0)
//                        dealRecycler.measure(0, 0)
//                        unDealListHeight = unDealRecycler.measuredHeight
//                        dealListHeight = dealRecycler.measuredHeight

                        unDealRecycler.post { unDealListHeight = unDealRecycler.height }
                        dealRecycler.post { dealListHeight = dealRecycler.height }

                    }
                } catch (e: JSONException){
                    e.printStackTrace()
                }
            }

            override fun onError(error: Int, errorMsg: String?) {
                MPLog.e(TAG, "getAllToDoList failed: $error, $errorMsg")
                runOnUiThread {
                    hideProgressDialog()
                    MyToast.showErrorToast("获取失败: $error, $errorMsg")
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.iv_back -> {
//                setResult(RESULT_OK, Intent().putExtra("unDealCount", todoListEntity.unDealList.size))
                finish()
            }
            R.id.un_deal_title -> {
                if(unDealHidden){
                    showAnimate(unDealRecycler, unDealListHeight)
                } else {
                    hideAnimate(unDealRecycler, unDealListHeight)
                }
                unDealHidden = !unDealHidden
                unDealArrow.setImageDrawable(if (unDealHidden) ContextCompat.getDrawable(this,R.drawable.mp_ic_arrow_down) else ContextCompat.getDrawable(this,R.drawable.mp_ic_arrow_up))
            }
            R.id.deal_title -> {
                if(dealHidden){
                    showAnimate(dealRecycler, dealListHeight)
                } else {
                    hideAnimate(dealRecycler, dealListHeight)
                }
                dealHidden = !dealHidden
                dealArrow.setImageDrawable(if (dealHidden) ContextCompat.getDrawable(this,R.drawable.mp_ic_arrow_down) else ContextCompat.getDrawable(this,R.drawable.mp_ic_arrow_up))

            }
        }
    }

    //显示条目长按下拉列表
    private fun showSelectPopWindow(itemView: View, position: Int, isDeal: Boolean) {
        val contentView = View.inflate(this, R.layout.popup_select_todo_list, null)

        val tvMarkCompleted = contentView.findViewById<TextView>(R.id.mark_completed)
        val tvClear = contentView.findViewById<TextView>(R.id.clear)

        tvMarkCompleted.text = if (isDeal) getString(R.string.set_to_unprocessed) else getString(R.string.mark_completed)

        contentView.measure(0, 0)
        selectPopupWindow = PopupWindow(this)
        selectPopupWindow.setContentView(contentView)
        selectPopupWindow.setFocusable(true)
        selectPopupWindow.setBackgroundDrawable(BitmapDrawable())
        selectPopupWindow.setOutsideTouchable(true)
        val location = IntArray(2)
        itemView.getLocationOnScreen(location)
        selectPopupWindow.showAtLocation(
            itemView,
            Gravity.NO_GRAVITY,
            location[0] + itemView.measuredWidth / 2 - contentView.measuredWidth / 2,
            (location[1] + itemView.measuredHeight.toFloat() / 2).toInt()
        )
        tvMarkCompleted.setOnClickListener { view: View? ->
            //让popupWindow消失
            selectPopupWindow.dismiss()
            if(isDeal){
                markCompleted(todoListEntity.dealList[position].id, 0)
            } else {
                markCompleted(todoListEntity.unDealList[position].id, 1)
            }

        }
        tvClear.setOnClickListener { view: View? ->
            //让popupWindow消失
            selectPopupWindow.dismiss()
            removeTodoList(if(isDeal) todoListEntity.dealList[position].id else todoListEntity.unDealList[position].id)
        }
    }

    override fun onItemClick(position: Int, isDeal: Boolean) {
        var msgId = ""
        var toId = ""
        var chatType = ""
        if(isDeal){
            msgId = todoListEntity.dealList[position].msgId
            toId = todoListEntity.dealList[position].msgEntity.toId
            if (TextUtils.equals(toId, EMClient.getInstance().currentUser)){
                toId = todoListEntity.dealList[position].msgEntity.fromId
            }
            chatType = todoListEntity.dealList[position].msgEntity.chatType
        } else {
            msgId = todoListEntity.unDealList[position].msgId
            toId = todoListEntity.unDealList[position].msgEntity.toId
            if (TextUtils.equals(toId, EMClient.getInstance().currentUser)){
                toId = todoListEntity.unDealList[position].msgEntity.fromId
            }
            chatType = todoListEntity.unDealList[position].msgEntity.chatType
        }

        val conversation = EMClient.getInstance().chatManager().getConversation(toId, if (TextUtils.equals(chatType, "groupchat")) EMConversation.EMConversationType.GroupChat else EMConversation.EMConversationType.Chat, true)
        val message = conversation.getMessage(msgId, false)
        if (message != null){
            startActivityForResult(Intent(this, ChatActivity::class.java)
                .putExtra(Constant.EXTRA_CHAT_TYPE, if (TextUtils.equals(chatType, "groupchat")) Constant.CHATTYPE_GROUP else Constant.CHATTYPE_SINGLE)
                .putExtra(Constant.EXTRA_USER_ID, toId)
                .putExtra("positionMsgId", msgId), 100)
        } else {
            MyToast.showToast("消息已被删除或者撤回")
        }
    }

    override fun onItemLongClick(view: View, position: Int, isDeal: Boolean): Boolean {
        showSelectPopWindow(view, position, isDeal)
        return true
    }

    private fun markCompleted(todoId: Int, status: Int){
        try{
            showProgressDialog("加载中")
            val json = JSONObject()
            json.put("todoId", todoId)
            json.put("status", status)
            EMAPIManager.getInstance().dealToDoList(json.toString(), object : EMDataCallBack<String>() {
                override fun onSuccess(value: String?) {
                    fetchToDoList()
                }

                override fun onError(error: Int, errorMsg: String?) {
                    MPLog.e(TAG, "dealToDoList failed: $error, $errorMsg")
                    runOnUiThread {
                        hideProgressDialog()
                        MyToast.showErrorToast("操作失败: $error, $errorMsg")
                    }
                }
            })
        } catch (e : JSONException){
            e.printStackTrace()
        }

    }

    private fun removeTodoList(todoId: Int){
        showProgressDialog("加载中")
        EMAPIManager.getInstance().removeToDoList(todoId.toString(), object: EMDataCallBack<String>(){
            override fun onSuccess(value: String?) {
                fetchToDoList()
            }

            override fun onError(error: Int, errorMsg: String?) {
                MPLog.e(TAG, "removeToDoList failed: $error, $errorMsg")
                runOnUiThread {
                    hideProgressDialog()
                    MyToast.showErrorToast("操作失败: $error, $errorMsg")
                }
            }
        })
    }

    private fun showAnimate(view: View, contentHeight: Int){
        val animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        animator.duration = 300
        animator.addUpdateListener{
            val fraction = it.animatedFraction
            val diffHeight = contentHeight * fraction

            if (diffHeight <= 0) {
                return@addUpdateListener
            }

            val params = view.layoutParams
            params.height = diffHeight.toInt()
            view.layoutParams = params
            view.visibility = VISIBLE
        }

        animator.addListener(object : AnimatorListener{
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                val params = view.layoutParams
                params.height = LayoutParams.WRAP_CONTENT
                view.layoutParams = params
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })
        animator.start()
    }

    private fun hideAnimate(view: View, contentHeight: Int){
        val animator = ObjectAnimator.ofFloat(view, "alpha", 0f)
        animator.duration = 300
        animator.addUpdateListener{
            if(view.height <= 0){
                return@addUpdateListener
            }

            val fraction = it.animatedFraction
            val diffHeight = contentHeight * fraction
            val height = contentHeight - diffHeight

            val params = view.layoutParams
            params.height = height.toInt()
            view.layoutParams = params
        }
        animator.addListener(object : AnimatorListener{
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(p0: Animator) {
                view.visibility = GONE
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }

        })
        animator.start()
    }

    override fun onResume() {
        super.onResume()
        fetchToDoList()
    }
}