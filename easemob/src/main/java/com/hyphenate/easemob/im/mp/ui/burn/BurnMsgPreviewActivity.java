package com.hyphenate.easemob.im.mp.ui.burn;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.utils.EaseSmileUtils;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.easemob.imlibs.message.MessageUtils;
import com.hyphenate.easemob.imlibs.mp.events.MessageChanged;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;

import java.util.Timer;
import java.util.TimerTask;

public class BurnMsgPreviewActivity extends BaseActivity {

    private EMMessage message;

    private TextView tvContent;
    private TextView tvTime;

    private View backView;
    private Timer mTimer;
    private int curentTime = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_burn_preview_txt);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setSwipeEnabled(false);
        initViews();
        initDatas();
    }


    private void initViews() {
        backView = findViewById(R.id.iv_back);
        tvContent = findViewById(R.id.tv_content);
        tvTime = findViewById(R.id.tv_time);

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.direct() == EMMessage.Direct.RECEIVE) {
                    destoryMessage();
                } else {
                    finish();
                }
            }
        });

    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private void initDatas() {
        Intent gIntent = getIntent();
        if (gIntent == null) {
            return;
        }

        String msgId = gIntent.getStringExtra("msgId");

        if (msgId != null) {
            message = EMClient.getInstance().chatManager().getMessage(msgId);
        }

        if (message == null) {
            finish();
            return;
        }

        if (message.getType() != EMMessage.Type.TXT) {
            finish();
            return;
        }

        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        String strContent = txtBody.getMessage();
        Spannable span = EaseSmileUtils.getSmiledText(this, strContent);
        // 设置内容
        tvContent.setText(span, TextView.BufferType.SPANNABLE);

        if (message.direct() == EMMessage.Direct.RECEIVE) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    curentTime--;
                    if (curentTime > 0) {
                        refreshTimeView();
                    } else {
                        destoryMessage();
                    }
                }
            }, 1000, 1000);
        }

    }


    private void refreshTimeView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(getFormatTime());
            }
        });
    }

    private void destoryMessage(){
        EMMessage cmdMessage = EMMessage.createSendMessage(EMMessage.Type.CMD);
        cmdMessage.setTo(message.getFrom());
        cmdMessage.addBody(new EMCmdMessageBody(Constant.BURN_AFTER_READING_CMD_ACTION));
        cmdMessage.setAttribute(Constant.BURN_AFTER_READING_DESTORY_MSGID, message.getMsgId());
        MessageUtils.sendMessage(cmdMessage);

        message.setAttribute(EaseConstant.BURN_AFTER_READING_READED, true);
        EMClient.getInstance().chatManager().updateMessage(message);
        MPEventBus.getDefault().post(new MessageChanged(message.getMsgId()));

        finish();
    }

    private String getFormatTime() {
        if (curentTime > 10) {
            return "00:" + curentTime;
        }
        return "00:0" + curentTime;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
    }
}
