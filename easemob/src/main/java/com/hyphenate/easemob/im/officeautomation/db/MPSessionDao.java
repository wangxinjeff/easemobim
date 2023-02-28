package com.hyphenate.easemob.im.officeautomation.db;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPSessionEntity;

import java.util.List;

public class MPSessionDao {

    public static final String TABLE_NAME = "mp_sessions";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_CREATE_TIME = "createTime";
    public static final String COLUMN_NAME_LAST_UPDATE_TIME = "lastUpdateTime";
    public static final String COLUMN_NAME_USER_ID = "userId";
    public static final String COLUMN_NAME_TO_ID = "toId";
    public static final String COLUMN_NAME_CHAT_TYPE = "chatType";
    public static final String COLUMN_NAME_IS_TOP = "isTop";
    public static final String COLUMN_NAME_TOP_TIME = "topTime";
    public static final String COLUMN_NAME_AVATAR = "avatar";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_IS_DISTURB = "isNoDisturb";
    public static final String COLUMN_NAME_IM_ID = "imId";

    private MPSessionDao() {
    }

    public static String getStickyTime(String conversationId, EMConversation.EMConversationType type) {
        return AppDBManager.getInstance().getStickyTime(conversationId, type);
    }

    public static void saveStickyTime(String conversationId, String stickyTime, EMConversation.EMConversationType type) {
        AppDBManager.getInstance().saveStickyTime(conversationId, stickyTime, type);
    }

    public static List<MPSessionEntity> getSessions() {
        return AppDBManager.getInstance().getSessions();
    }

    public static void updateSession(MPSessionEntity sessionEntity) {
        AppDBManager.getInstance().updateSession(sessionEntity);
    }


    public static void saveSessions(List<MPSessionEntity> sessionEntities) {
        //postSession
        AppDBManager.getInstance().saveSessions(sessionEntities);
    }


    public static void deleteSession(int sessionId) {
        AppDBManager.getInstance().deleteSession(sessionId);
    }

}
