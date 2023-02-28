package com.hyphenate.easemob.im.officeautomation.widget.chatrow;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

public class EaseChatRowNameCard extends EaseChatRow {

    private AvatarImageView avatar;

    private TextView name;

    public EaseChatRowNameCard(Context context, EMMessage message, int position, EaseMessageAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.em_row_receive_card : R.layout.em_row_send_card, this);
    }

    @Override
    protected void onFindViewById() {

        avatar = findViewById(R.id.iv_card_avatar);
        name = findViewById(R.id.tv_card_name);
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
        try {
            JSONObject extMsg = new JSONObject(message.getJSONObjectAttribute(Constant.EXT_EXTMSG).toString());
            JSONObject cardMsg = extMsg.getJSONObject("content");
            AvatarUtils.setAvatarContent(context, cardMsg.optString("realName"), cardMsg.optString("user_avatar"), avatar);
            name.setText(cardMsg.optString("realName"));
        } catch (JSONException | HyphenateException e) {
            e.printStackTrace();
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
