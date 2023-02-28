package com.hyphenate.easemob.im.officeautomation.widget.chatrow;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;

public class EaseChatRowFire extends EaseChatRow {


    private ImageView ivTypeImg;

    public EaseChatRowFire(Context context, EMMessage message, int position, EaseMessageAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.em_row_recv_fire : R.layout.em_row_sent_fire, this);
    }

    @Override
    protected void onFindViewById() {
        ivTypeImg = findViewById(R.id.iv_type);
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
        switch (msg.status()) {
            case CREATE:
                onMessageCreate();
                break;
            case SUCCESS:
                onMessageSuccess();
                break;
            case FAIL:
                onMessageError();
                break;
            case INPROGRESS:
                onMessageInProgress();
                break;
        }
    }

    @Override
    protected void onSetUpView() {
        boolean readed = message.getBooleanAttribute(Constant.BURN_AFTER_READING_READED, false);
        if (message.getType() == EMMessage.Type.IMAGE) {
            if (!readed) {
                ivTypeImg.setImageResource(R.drawable.mp_ic_fire_chatrow_img);
            } else {
                // 已焚毁
                ivTypeImg.setImageResource(R.drawable.em_icon_burned);
            }
        } else {
            if (readed) {
                // 已焚毁
                ivTypeImg.setImageResource(R.drawable.em_icon_burned);
            } else {
                ivTypeImg.setImageResource(R.drawable.mp_ic_fire_chatrow_normal);
            }
        }
    }

    private void onMessageCreate() {
        progressBar.setVisibility(View.GONE);
        statusView.setVisibility(View.VISIBLE);
    }

    private void onMessageSuccess() {
        progressBar.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);
    }

    private void onMessageError() {
        progressBar.setVisibility(View.GONE);
        statusView.setVisibility(View.VISIBLE);
    }

    private void onMessageInProgress() {
        progressBar.setVisibility(View.VISIBLE);
        statusView.setVisibility(View.GONE);
    }
}
