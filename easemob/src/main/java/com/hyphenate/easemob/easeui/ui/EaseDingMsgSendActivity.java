package com.hyphenate.easemob.easeui.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easemob.R;
import com.hyphenate.util.EMLog;

/**
 * Created by zhangsong on 18-1-16.
 */

public class EaseDingMsgSendActivity extends EaseBaseActivity {
    private static final String TAG = "DingMsgSendActivity";

    private TextView title;
    private TextView send;
    private ImageView back;
    private EditText msgEidtText;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.ease_acitivity_ding_msg_send);

        back = findViewById(R.id.iv_back);
        title = findViewById(R.id.tv_title);
        send = findViewById(R.id.tv_send);
        msgEidtText = findViewById(R.id.et_sendmessage);

        setupView();
    }

    private void setupView() {
        title.setText(getString(R.string.title_group_notification));

        // Set the title bar left layout click listener to back to previous activity.
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back(v);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: send the ding-type msg.
                EMLog.i(TAG, "Click to send ding-type message.");
                String msgContent = msgEidtText.getText().toString();
                Intent i = new Intent();
                i.putExtra("msg", msgContent);
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }
}
