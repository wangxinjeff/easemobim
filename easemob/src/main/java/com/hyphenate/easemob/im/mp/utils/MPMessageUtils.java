package com.hyphenate.easemob.im.mp.utils;

import com.google.gson.Gson;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.imlibs.message.MessageUtils;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.im.officeautomation.domain.ExtUserType;

import org.json.JSONException;
import org.json.JSONObject;

public class MPMessageUtils {

    public static void doAddSendExtField(EMMessage message) {
        if (message == null) return;
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser == null) {
            return;
        }
        ExtUserType extUserType = new ExtUserType(loginUser.getId(), loginUser.getNick(), loginUser.getAvatar());
        try {
            message.setAttribute(EaseConstant.EXT_USER_TYPE, new JSONObject(new Gson().toJson(extUserType)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message.getChatType() == EMMessage.ChatType.GroupChat) {
            GroupBean groupInfo = EaseUserUtils.getGroupInfo(message.conversationId());
            if (groupInfo != null) {
                try {
                    JSONObject jsonObj = new JSONObject(new Gson().toJson(groupInfo));
                    jsonObj.put("groupid", jsonObj.optString("groupId"));
                    jsonObj.remove("groupId");
                    message.setAttribute(EaseConstant.EXT_GROUP_TYPE, jsonObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendMessage(EMMessage message) {
        doAddSendExtField(message);
        MessageUtils.sendMessage(message);
    }
}
