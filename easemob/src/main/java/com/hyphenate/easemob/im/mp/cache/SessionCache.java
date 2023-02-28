package com.hyphenate.easemob.im.mp.cache;

import android.util.SparseArray;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.im.officeautomation.db.ConversationDao;
import com.hyphenate.easemob.im.officeautomation.db.MPSessionDao;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPSessionEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionCache {

    public static final String CHATTYPE_GROUPCHAT = "chatgroups";
    public static final String CHATTYPE_CHAT = "users";
    private static SessionCache sInstance;

    private Map<String, MPSessionEntity> cachedSessions = Collections.synchronizedMap(new HashMap<>());


    public static SessionCache getInstance() {
        if (sInstance == null) {
            synchronized (SessionCache.class) {
                if (sInstance == null) {
                    sInstance = new SessionCache();
                }
            }
        }
        return sInstance;
    }



    public void loadSessionCache() {
        List<MPSessionEntity> sessionEntities = MPSessionDao.getSessions();
        cachedSessions.clear();
        for (MPSessionEntity entity : sessionEntities) {
            if (entity == null) {
                continue;
            }
            cachedSessions.put(entity.getImId(), entity);
        }
    }

    public void saveSessions(List<MPSessionEntity> sessionEntities) {
        cachedSessions.clear();
        for (MPSessionEntity entity : sessionEntities) {
            if (entity == null) {
                continue;
            }
            cachedSessions.put(entity.getImId(), entity);
            if (entity.isTop()) {
                String chatType = entity.getChatType();
                if (CHATTYPE_CHAT.equals(chatType)) {
                    ConversationDao.saveStickyTime(entity.getImId(), String.valueOf(entity.getTopTime()), EMConversation.EMConversationType.Chat);
                } else if (CHATTYPE_GROUPCHAT.equals(chatType)) {
                    ConversationDao.saveStickyTime(entity.getImId(), String.valueOf(entity.getTopTime()), EMConversation.EMConversationType.GroupChat);
                }
            }
        }
        MPSessionDao.saveSessions(sessionEntities);
    }


    public void setSticky(String imId, EMConversation.EMConversationType convType) {
        AppHelper.getInstance().saveStickyTime(imId, String.valueOf(System.currentTimeMillis()), convType);
        MPSessionEntity entity = cachedSessions.remove(imId);
        if (entity == null) {
            return;
        }
        entity.setTop(true);
        cachedSessions.put(entity.getImId(), entity);
        MPSessionDao.updateSession(entity);
        putSession(entity);
    }

    public void cancelSticky(String imId, EMConversation.EMConversationType convType) {
        AppHelper.getInstance().saveStickyTime(imId, "", convType);
        MPSessionEntity entity = cachedSessions.remove(imId);
        if (entity == null) {
            return;
        }
        entity.setTop(false);
        cachedSessions.put(entity.getImId(), entity);
        MPSessionDao.updateSession(entity);
        putSession(entity);
    }


    private void putSession(MPSessionEntity entity) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("toId", entity.getToId());
            jsonBody.put("chatType", entity.getChatType());
            jsonBody.put("isTop", entity.isTop());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EMAPIManager.getInstance().putSession(jsonBody.toString(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {

            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });

    }

    public void deleteSession(String imId, EMConversation.EMConversationType chatType) {
        cachedSessions.remove(imId);
        if (chatType == EMConversation.EMConversationType.GroupChat) {
            GroupBean groupBean = AppHelper.getInstance().getModel().getGroupInfo(imId);
            if (groupBean != null) {
                EMAPIManager.getInstance().deleteSession(String.valueOf(groupBean.getGroupId()), CHATTYPE_GROUPCHAT, null);
            }
        } else if (chatType == EMConversation.EMConversationType.Chat) {
            EaseUser easeUser = UserProvider.getInstance().getEaseUser(imId);
            if (easeUser != null) {
                EMAPIManager.getInstance().deleteSession(String.valueOf(easeUser.getId()), CHATTYPE_CHAT, null);
            }
        }
    }


    public void onMessageSent(EMMessage message) {
        if (message.getType() == EMMessage.Type.CMD) {
            return;
        }
        String imId = message.getTo();
        if (EMClient.getInstance().getCurrentUser().equals(imId)) {
            return;
        }
        boolean isContain = cachedSessions.containsKey(imId);
        if (!isContain) {
            EMMessage.ChatType chatType = message.getChatType();
            dealWithSession(chatType, imId);
        }
    }


    private void dealWithSession(EMMessage.ChatType chatType, String imId) {
        cachedSessions.put(imId, null);
        if (chatType == EMMessage.ChatType.GroupChat) {
            GroupBean groupBean = AppHelper.getInstance().getModel().getGroupInfo(imId);
            if(groupBean == null) {
                return;
            }
            int groupId = groupBean.getGroupId();
            postSession(groupId, CHATTYPE_GROUPCHAT);
        } else if (chatType == EMMessage.ChatType.Chat) {
            EaseUser easeUser = UserProvider.getInstance().getEaseUser(imId);
            if (easeUser == null) {
                return;
            }
            postSession(easeUser.getId(), CHATTYPE_CHAT);
        }
    }


    public void onMessageReceived(EMMessage message) {
        if (message.getType() == EMMessage.Type.CMD) {
            return;
        }
        String imId = message.getFrom();
        if (EMClient.getInstance().getCurrentUser().equals(imId)) {
            return;
        }
        boolean isContain = cachedSessions.containsKey(imId);
        if (!isContain) {
            EMMessage.ChatType chatType = message.getChatType();
            dealWithSession(chatType, imId);
        }
    }


    private void postSession(int toId, String chatType) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("toId", toId);
            jsonObj.put("chatType", chatType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EMAPIManager.getInstance().postSession(jsonObj.toString(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    JSONObject jsonEntity = jsonObject.optJSONObject("entity");
                    MPSessionEntity sessionEntity = MPSessionEntity.create(jsonEntity);
                    cachedSessions.put(sessionEntity.getImId(), sessionEntity);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }




}
