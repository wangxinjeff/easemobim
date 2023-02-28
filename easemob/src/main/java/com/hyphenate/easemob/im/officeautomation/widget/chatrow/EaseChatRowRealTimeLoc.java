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

public class EaseChatRowRealTimeLoc extends EaseChatRow {

    public EaseChatRowRealTimeLoc(Context context, EMMessage message, int position, EaseMessageAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_rt_loc : R.layout.ease_row_sent_rt_loc, this);
    }

    @Override
    protected void onFindViewById() {
    }

    @Override
    protected void onSetUpView() {
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
    }

    @Override
    protected boolean disableCheckBox() {
        return true;
    }
}
