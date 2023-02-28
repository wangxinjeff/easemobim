package com.hyphenate.easemob.im.officeautomation.widget;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.ui.EaseShowBigImageActivity;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easemob.easeui.widget.presenter.EaseChatRowPresenter;
import com.hyphenate.easemob.im.mp.ui.burn.BurnMsgPreviewActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.utils.MyToast;
import com.hyphenate.easemob.im.officeautomation.widget.chatrow.EaseChatRowFire;

public abstract class EaseChatBurnPresenter extends EaseChatRowPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, EaseMessageAdapter adapter) {
        return new EaseChatRowFire(cxt, message, position, adapter);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        boolean readed = message.getBooleanAttribute("readed", false);
        if (readed) {
            if (message.direct() == EMMessage.Direct.RECEIVE) {
                MyToast.showToast("消息已焚毁");
            } else {
                MyToast.showToast("对方已阅，消息已焚毁");
            }
        } else {
            if (message.getType() == EMMessage.Type.TXT) {
                try {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), BurnMsgPreviewActivity.class);
                    intent.putExtra("msgId", message.getMsgId());
                    getContext().startActivity(intent);
                }catch (Exception ignored){}
            } else if (message.getType() == EMMessage.Type.IMAGE) {
                Intent intent = new Intent(getContext(), EaseShowBigImageActivity.class);
                String msgId = message.getMsgId();
                intent.putExtra("messageId", msgId);
                intent.putExtra(Constant.BURN_AFTER_READING_DESTORY_MSGID, msgId);
                getContext().startActivity(intent);
            }

        }

    }
}
