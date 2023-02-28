package com.hyphenate.easemob.im.officeautomation.widget.chatrow;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by easemob on 2017/7/31.
 */

public class EaseChatRowInvite extends EaseChatRow {

    private TextView contentView;

    public EaseChatRowInvite(Context context, EMMessage message, int position, EaseMessageAdapter adapter) {
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
        String nameId = message.getStringAttribute(EaseConstant.EXTRA_INVITE_USERID, null);
        JSONArray jsonUsers = null;
        try {
            jsonUsers = message.getJSONArrayAttribute(EaseConstant.EXTRA_INVITE_USERS);
        } catch (HyphenateException ignored) {
        }
        String nick = message.getStringAttribute(EaseConstant.EXTRA_INVITE_NICK, null);
        if (!TextUtils.isEmpty(nameId)){
            if (TextUtils.isEmpty(nick)){
                EaseUser easeUser = EaseUserUtils.getUserInfo(nameId);
                messageStr = (easeUser == null ? nameId : easeUser.getNick()) + messageStr;
                if (easeUser != null && easeUser.getNick() != null){
                    message.setAttribute(EaseConstant.EXTRA_INVITE_NICK, easeUser.getNick());
                    EMClient.getInstance().chatManager().updateMessage(message);
                }
            }else{
                messageStr = nick + messageStr;
            }
        }else if (jsonUsers != null){
            StringBuilder stringBuilder = new StringBuilder();
            boolean isChanged = false;
            try {
                for (int i = 0; i < jsonUsers.length(); i++){
                    JSONObject jsonUser = jsonUsers.getJSONObject(i);
                    String username = jsonUser.optString("username");
                    String nickname = jsonUser.optString("nick");
                    if (TextUtils.isEmpty(nickname)){
                        EaseUser easeUser = EaseUserUtils.getUserInfo(username);
                        stringBuilder.append(easeUser == null ? username : easeUser.getNick());
                        if (easeUser != null && easeUser.getNick() != null){
                            jsonUser.put("nick", easeUser.getNick());
                            isChanged = true;
                        }
                    }else{
                        stringBuilder.append(nickname);
                    }
                    if (i < jsonUsers.length() - 1){
                        stringBuilder.append(", ");
                    }
                }
                messageStr = String.format(messageStr, stringBuilder.toString());
                if (isChanged){
                    message.setAttribute(EaseConstant.EXTRA_INVITE_USERS, jsonUsers);
                    EMClient.getInstance().chatManager().updateMessage(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
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
