package com.hyphenate.easemob.easeui;

import static com.hyphenate.chat.EMMessage.Type.VIDEO;
import static com.hyphenate.chat.EMMessage.Type.VOICE;

import android.text.TextUtils;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;
import com.hyphenate.exceptions.HyphenateException;

import org.json.JSONObject;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 12/08/2018
 */

public class EaseMessageUtils {

    public static boolean isRecallMessage(EMMessage message) {
        if (message.getBooleanAttribute(EaseConstant.MESSAGE_TYPE_RECALL, false)) {
            return true;
        }
        return false;
    }

    public static boolean isChatHistoryMessage(EMMessage message) {
        try {
            JSONObject jsonObject = message.getJSONObjectAttribute(EaseConstant.EXT_EXTMSG);
            String type = jsonObject.optString(EaseConstant.EXT_MSGTYPE);
            if (type != null && "chatMsgs".equalsIgnoreCase(type)) {
                return true;
            }
        } catch (Exception ignored) {
//			e.printStackTrace();
        }
        return false;
    }

    public static boolean isInviteMessage(EMMessage message) {
        if (message.getStringAttribute(EaseConstant.EXTRA_INVITE_USERID, null) != null) {
            return true;
        }
        if (message.getStringAttribute(EaseConstant.EXTRA_INVITE_USERS, null) != null) {
            return true;
        }
        return false;
    }

    public static boolean isVideoCallMessage(EMMessage message) {
        if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
            return true;
        }
        return false;
    }

    public static boolean isVoiceCallMessge(EMMessage message) {
        if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
            return true;
        }
        return false;
    }

    public static boolean isVideoInviteMessage(EMMessage message) {
        JSONObject confJson = null;
        try {
            confJson = message.getJSONObjectAttribute(EaseConstant.MSG_ATTR_CONFERENCE);
        } catch (HyphenateException ignored) {
        }
        return confJson != null;
    }

    public static boolean isBigExprMessage(EMMessage message) {
        if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
            return true;
        }
        return false;
    }

    public static boolean isNoticeMessage(EMMessage message) {
        try {
            JSONObject extMsgJson = message.getJSONObjectAttribute(EaseConstant.EXT_EXTMSG);
            String type = extMsgJson.optString("type");
            if (type != null && (type.equals("notice") || type.equals("conf_notice") || type.equals("vote_notice"))) {
                return true;
            }
        } catch (HyphenateException ignored) {
        }
        return false;
    }

    public static boolean isStickerMessage(EMMessage message) {
        try {
            JSONObject extMsgJson = message.getJSONObjectAttribute(EaseConstant.EXT_EXTMSG);
            String type = extMsgJson.optString("type");
            if (type != null && (type.equals("sticker"))) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean isVoteMessage(EMMessage message){
        try {
            JSONObject extMsgJson = message.getJSONObjectAttribute(EaseConstant.EXT_EXTMSG);
            String type = extMsgJson.optString("type");
            if (type.equals("vote")) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * 阅后即焚的消息
     *
     * @param message
     * @return
     */
    public static boolean isBurnMessage(EMMessage message) {
        try {
            JSONObject extMsgJson = message.getJSONObjectAttribute(EaseConstant.EXT_EXTMSG);
            String type = extMsgJson.optString("type");
            if (type != null && (type.equals("burn_after_reading"))) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * 名片信息
     *
     * @param message
     * @return
     */
    public static boolean isNameCard(EMMessage message) {
        try {
            JSONObject extMsgJson = message.getJSONObjectAttribute(EaseConstant.EXT_EXTMSG);
            String type = extMsgJson.optString("type");
            if (type != null && (type.equals("people_card"))) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean isRTLocMessage(EMMessage message) {
        try {
            JSONObject extMsgJson = message.getJSONObjectAttribute(EaseConstant.EXT_LOCATION);
            String state = extMsgJson.optString(EaseConstant.EXT_LOCATION_STATE);
            if (state != null && state.equals(EaseConstant.EXT_LOCATION_STATE_START)) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean canCollect(EMMessage message) {
        if (message.status() == EMMessage.Status.SUCCESS) {
            switch (message.getType()) {
                case TXT:
                    if (isStickerMessage(message) || isBurnMessage(message)) {
                        return false;
                    } else if (isVideoInviteMessage(message)) {
                        return false;
                    } else if (isVideoCallMessage(message) || isVoiceCallMessge(message)) {
                        return false;
                    } else if (isNoticeMessage(message) || isInviteMessage(message)) {
                        return false;
                    } else if (isBigExprMessage(message) || isRecallMessage(message)) {
                        return false;
                    } else if (isChatHistoryMessage(message)) {
                        return true;
                    } else {
                        return true;
                    }
                case IMAGE:
                    if (isBurnMessage(message)) {
                        return false;
                    }
                case LOCATION:
                case VOICE:
                case VIDEO:
                case FILE:
                    long currTime = System.currentTimeMillis();
                    long msgTime = message.getMsgTime();
                    return currTime - msgTime <= 6 * 24 * 60 * 60 * 1000;
            }

        }
        return false;
    }

    public static boolean hasMultiChoices(EMMessage message) {
        if (isChatHistoryMessage(message)) {
            return false;
        }
        if (isVideoCallMessage(message)) {
            return false;
        }
        if (isVoiceCallMessge(message)) {
            return false;
        }
        if (isNoticeMessage(message)) {
            return false;
        }

        if (isBurnMessage(message)) {
            return false;
        }

        if (message.getType() == VOICE) {
            return false;
        }
        if (message.getType() == VIDEO) {
            return false;
        }

//		if (message.getType() == EMMessage.Type.LOCATION){
//			return false;
//		}

        if (message.status() != EMMessage.Status.SUCCESS) {
            return false;
        }
        if (isInviteMessage(message)) {
            return false;
        }
        return true;
    }


    public static boolean canRecall(EMMessage message) {
        if (message.status() != EMMessage.Status.SUCCESS) {
            return false;
        }
        if (message.direct() != EMMessage.Direct.SEND) {
            return false;
        }
        long msgTime = message.getMsgTime();
        long recallTime = PreferenceUtils.getInstance().getRecallDuration() * 1000;
        if (System.currentTimeMillis() - msgTime > recallTime) {
            return false;
        }

        return true;
    }

	public static boolean isReferenceMsg(EMMessage message){
        try{
            JSONObject jsonObject = message.getJSONObjectAttribute(EaseConstant.MSG_EXT_REFERENCE_MSG);
            if(jsonObject != null){
                String msgId = jsonObject.optString(EaseConstant.MSG_EXT_REFERENCE_MSG_ID);
                if(!TextUtils.isEmpty(msgId)){
                    return true;
                }
            }
        }catch(HyphenateException e){}
        return false;
    }

}
