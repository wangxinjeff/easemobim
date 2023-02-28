package com.hyphenate.easemob.im.officeautomation.widget.chatrow;

import android.content.Context;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easemob.R;

/**
 * Created by easemob on 2017/7/31.
 */

public class EaseChatRowRecall extends EaseChatRow {

    private TextView contentView;

    public EaseChatRowRecall(Context context, EMMessage message, int position, EaseMessageAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(R.layout.em_row_recall_message, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(R.id.text_content);
    }

    @Override
    protected void onSetUpView() {
        // 设置显示内容
        String messageStr = ((EMTextMessageBody) this.message.getBody()).getMessage();
        /*String messageStr = null;
        if (message.direct() == EMMessage.Direct.SEND) {
            messageStr = String.format(context.getString(R.string.msg_recall_by_self));
        } else {
            EaseUser userInfo = EaseUserUtils.getUserInfo(message.getFrom());
            String username = message.getFrom();
            if (userInfo != null) {
                username = userInfo.getNickname();
            }
            messageStr = String.format(context.getString(R.string.msg_recall_by_user), username);
        }*/
        contentView.setText(messageStr);
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
    }

    @Override
    protected boolean disableCheckBox() {
        return true;
    }
}
