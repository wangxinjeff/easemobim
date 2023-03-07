package com.hyphenate.easemob.im.officeautomation.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hyphenate.easemob.R
import com.hyphenate.easemob.im.officeautomation.adapter.VoteOptionAdapter
import com.hyphenate.easemob.im.officeautomation.domain.VoteOptionEntity
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager

class VoteOptionDetailActivity : BaseActivity(), View.OnClickListener{

    private lateinit var ivBack: ImageView
    private lateinit var optionTitle : TextView
    private lateinit var optionRecyclerView: RecyclerView
    private lateinit var optionProportion : TextView
    private var title : String? = ""
    private var id = 0
    private var ratio = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote_option_detail)
        title = intent.getStringExtra("title")
        id = intent.getIntExtra("id", 0)
        ratio = intent.getIntExtra("ratio", 0)
        initView()
        initListener()
        initData()
    }

    private fun initView() {
        ivBack = findViewById(R.id.iv_back)
        optionTitle = findViewById(R.id.tv_title)
        optionRecyclerView = findViewById(R.id.option_recycler)
        optionProportion = findViewById(R.id.tv_option_proportion)

        optionRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = VoteOptionAdapter(context)
        }
    }

    private fun initListener(){
        ivBack.setOnClickListener(this)
    }

    private fun initData() {
        optionTitle.text = title
        getOptionDetail()
    }

    private fun getOptionDetail(){
        EMAPIManager.getInstance().getVoteOptionInfo(id.toString(), object : EMDataCallBack<String>() {
            override fun onSuccess(value: String?) {
                val entity = Gson().fromJson<VoteOptionEntity>(value, VoteOptionEntity::class.java)
                runOnUiThread {
                    (optionRecyclerView.adapter as VoteOptionAdapter).setEntity(entity)
                    optionProportion.text = String.format(getString(R.string.voted_proportion), "$ratio%")
                }
            }

            override fun onError(error: Int, errorMsg: String?) {
                runOnUiThread {
                    Toast.makeText(this@VoteOptionDetailActivity, "$error + $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.iv_back -> finish()
        }
    }
}