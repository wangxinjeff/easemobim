package com.hyphenate.easemob.im.officeautomation.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.hyphenate.easemob.easeui.ui.EaseShowBigImageActivity;
import com.hyphenate.easemob.easeui.ui.EaseShowVideoActivity;
import com.hyphenate.easemob.easeui.widget.EaseTitleBar;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.adapter.ChatHistoryAdapter;
import com.hyphenate.easemob.im.officeautomation.domain.ExtMsg;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;

import java.util.ArrayList;

/**
 * Created by qby on 2018/06/15.
 * 展示聊天记录页面
 */
public class ChatHistoryActivity extends BaseActivity {

    private EaseTitleBar title_bar;
    private RecyclerView rv;
    private ArrayList<Object> chatList;
    private ChatHistoryAdapter chatHistoryAdapter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat_history);
        initViews();
        initListeners();
        initData();
    }

    private void initViews() {
        title_bar = findViewById(R.id.title_bar);
        rv = findViewById(R.id.rv);
    }

    private void initListeners() {
        chatList = new ArrayList<>();

        title_bar.setLeftLayoutClickListener(view -> finish());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
//        rv.addItemDecoration(new SimpleDividerItemDecoration(this));
        chatHistoryAdapter = new ChatHistoryAdapter(this, chatList, new ChatHistoryAdapter.ChatListItemCallback() {
            @Override
            public void onItemClick(int position, int clickType, String thumb_url, String remote_url) {
                if (clickType == ChatHistoryAdapter.CLICK_TYPE_IMAGE) {
                    Intent intent = new Intent(ChatHistoryActivity.this, EaseShowBigImageActivity.class);
                    intent.putExtra("remote_url", remote_url);
                    startActivity(intent);
                } else if (clickType == ChatHistoryAdapter.CLICK_TYPE_VIDEO) {
                    Intent intent = new Intent(ChatHistoryActivity.this, EaseShowVideoActivity.class);
                    intent.putExtra("remote_url", remote_url);
                    startActivity(intent);
                }
            }
        });
        rv.setAdapter(chatHistoryAdapter);

    }

    private void initData() {
        String extMsg = getIntent().getStringExtra(Constant.EXT_EXTMSG);
        if (TextUtils.isEmpty(extMsg)) {
            finish();
            return;
        }
        ExtMsg msg = new Gson().fromJson(extMsg, ExtMsg.class);
        if (msg != null) {
            title_bar.setTitle(msg.getTitle());
            chatList.addAll(msg.getContents());
            chatHistoryAdapter.notifyDataSetChanged();
        }
    }


}
