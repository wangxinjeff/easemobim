package com.hyphenate.easemob.im.officeautomation.db;

import com.hyphenate.chat.EMConversation;

public class ConversationDao {

    public static final String CONVERSATION_TABLE_NAME = "conversation";
    public static final String COLUMN_CONVERSATION_NAME_ID = "id";
    public static final String COLUMN_NAME_STICKY_TIME = "sticky";
    public static final String COLUMN_NAME_CONVERSATION_TYPE = "type";

    public ConversationDao() {

    }

    public static String getStickyTime(String conversationId, EMConversation.EMConversationType type) {
        return AppDBManager.getInstance().getStickyTime(conversationId, type);
    }

    public static void saveStickyTime(String conversationId, String stickyTime, EMConversation.EMConversationType type) {
        AppDBManager.getInstance().saveStickyTime(conversationId, stickyTime, type);
    }

}
