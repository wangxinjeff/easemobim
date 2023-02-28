package com.hyphenate.easemob.imlibs.message;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.imlibs.mp.events.EventEMessageSent;
import com.hyphenate.eventbus.MPEventBus;

public class MessageUtils {

    public static void sendMessage(EMMessage message) {
        EMMessage willSendMessage = checkRegionMessage(message);
        EMClient.getInstance().chatManager().sendMessage(willSendMessage);
        MPEventBus.getDefault().post(new EventEMessageSent(willSendMessage));
    }

    public static void updateMessage(EMMessage message) {
        EMClient.getInstance().chatManager().updateMessage(message);
    }

    private static EMMessage checkRegionMessage(EMMessage message) {
//        if (!isCommonRegion(message.getTo(), message.getFrom())) {
//            message.setAttribute("ccs_cluster", true);
//        }
        return message;
    }

    /**
     * 检查imUsername的前缀
     * @param username
     * @return
     */
    public static String getPrefixByUsername(String username) {
        if(username == null || username.length() < 2) {
            return null;
        }
        return username.substring(0, 2);
    }


    public static boolean isCommonRegion(String lImUsername, String rImUsername) {
        String lPrefix = getPrefixByUsername(lImUsername);
        String rPrefix = getPrefixByUsername(rImUsername);

        if (lPrefix != null && !lPrefix.equals(rPrefix)) {
            return false;
        }

        return true;
    }

    public static boolean isCommonRegionWithMe(String imUsername) {
        return isCommonRegion(MPClient.get().getCurrentUser().getImUserId(), imUsername);
    }

}
