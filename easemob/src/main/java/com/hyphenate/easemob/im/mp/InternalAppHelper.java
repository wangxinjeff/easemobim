package com.hyphenate.easemob.im.mp;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMultiDeviceListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.im.mp.manager.DraftManager;
import com.hyphenate.easemob.im.mp.manager.StickerManager;
import com.hyphenate.easemob.im.mp.rest.EMAllOrgRequest;
import com.hyphenate.easemob.im.mp.rest.EMAllUserRequest;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.im.officeautomation.db.AppDBManager;
import com.hyphenate.easemob.im.officeautomation.db.ConversationDao;
import com.hyphenate.easemob.im.officeautomation.db.InviteMessageDao;
import com.hyphenate.easemob.im.officeautomation.http.BaseRequest;
import com.hyphenate.easemob.im.officeautomation.utils.AppUtil;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.utils.PreferenceManager;
import com.hyphenate.easemob.imlibs.mp.ConnectionListener;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.imlibs.mp.events.EventConfCancel;
import com.hyphenate.easemob.imlibs.mp.events.EventEMMessageReceived;
import com.hyphenate.easemob.imlibs.mp.events.EventFinishWB;
import com.hyphenate.easemob.imlibs.mp.events.EventFriendNotify;
import com.hyphenate.easemob.imlibs.mp.events.EventUsersRefresh;
import com.hyphenate.easemob.imlibs.mp.events.EventUsersRemoved;
import com.hyphenate.easemob.imlibs.mp.events.EventWhiteBoard;
import com.hyphenate.easemob.im.mp.manager.NoDisturbManager;
import com.hyphenate.easemob.im.mp.rest.EMMyOrgUsersRequest;
import com.hyphenate.easemob.im.officeautomation.db.TenantOptionsDao;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.model.EaseNotifier;
import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.imlibs.mp.events.MessageChanged;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.imlibs.officeautomation.domain.TenantOption;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.imlibs.mp.dao.AuthListener;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupsChanged;
import com.hyphenate.easemob.imlibs.mp.events.EventLocChanged;
import com.hyphenate.easemob.imlibs.mp.events.EventLocUserRemoved;
import com.hyphenate.easemob.imlibs.mp.events.EventOrgsAdded;
import com.hyphenate.easemob.imlibs.mp.events.EventOrgsChanged;
import com.hyphenate.easemob.imlibs.mp.events.EventOrgsRefresh;
import com.hyphenate.easemob.imlibs.mp.events.EventOrgsRemoved;
import com.hyphenate.easemob.imlibs.mp.events.EventRolesRefresh;
import com.hyphenate.easemob.imlibs.mp.events.EventUsersAdded;
import com.hyphenate.easemob.imlibs.mp.events.EventUsersChanged;
import com.hyphenate.easemob.im.officeautomation.domain.InviteMessage;
import com.hyphenate.easemob.im.officeautomation.domain.InviteMessage.InviteMessageStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 26/08/2018
 */

class InternalAppHelper {

    private static final String TAG = "InternalAppHelper";

    private static InternalAppHelper instance = new InternalAppHelper();
    //	final private List<ConnectionListener> connectionListeners = Collections.synchronizedList(new ArrayList<ConnectionListener>());
    private AppModel appModel;
    private InviteMessageDao inviteMessageDao;
    private MPOrgEntity companyOrg = null;
    //	private ExecutorService executor;
    private MyMultiDeviceListener mMyMultiDeviceListener;
    private MyGroupChangeListener mMyGroupChangeListener;

    static InternalAppHelper getInstance() {
        return instance;
    }

    /**
     * sync groups status listener
     */
    private List<DataSyncListener> syncGroupsListeners;

    private boolean isSyncingGroupsWithServer = false;
    private boolean isGroupsSyncedWithServer = false;

    private boolean isGroupAndContactListenerRegisted;

    protected Handler handler = new Handler();

//    Queue<String> msgQueue = new ConcurrentLinkedQueue<>();


//	private InternalAppHelper(){
//		executor = Executors.newCachedThreadPool();
//	}

    AuthListener mAuthListener = new AuthListener() {
        @Override
        public void onAuthFailed() {
            logout(false);
        }
    };


    public void init(Context context) {
        appModel = new AppModel(context);
        MPClient.get().setAuthListener(mAuthListener);
        PreferenceManager.init(context);
        PreferenceUtils.getInstance().init(context);

        initDbDao();
        syncGroupsListeners = new ArrayList<>();
        isGroupsSyncedWithServer = getModel().isGroupsSynced();
        if (mMyMultiDeviceListener == null) {
            mMyMultiDeviceListener = new MyMultiDeviceListener();
        }
        if (mMyGroupChangeListener == null) {
            mMyGroupChangeListener = new MyGroupChangeListener();
        }
    }


    public GroupBean getGroupInfo(final String imGroupId) {
        GroupBean groupInfo = appModel.getGroupInfo(imGroupId);
        if (groupInfo == null) {
            requestGroupInfo(imGroupId);
        }
        return groupInfo;
    }

    public GroupBean getGroupInfoById(final int groupId) {
        GroupBean groupInfo = appModel.getGroupInfoById(groupId);
        if (groupInfo == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    requestGroupInfoById(groupId);
                }
            }).start();
        }
        return groupInfo;
    }

    MPOrgEntity getCompanyOrg() {
        if (companyOrg == null) {
            List<MPOrgEntity> mList = AppHelper.getInstance().getModel().getOrgsListByParent(-1);
            if (mList != null && !mList.isEmpty()) {
                companyOrg = mList.get(0);
            }
        }
        return companyOrg;
    }

    public EaseUser getUserInfo(final int userId) {
        // To get instance of EaseUser, here we get it from the user list in memory
        // You'd better cache it if you get it from your server
        EaseUser user = null;
//        if (PreferenceManager.getInstance().getLoginUserId() == userId)
//            return getCurrentLoginUser();
        user = appModel.getUserExtInfo(userId);

        // if user is not in your contacts, set inital letter for him/her
        if (user == null) {
            user = new EaseUser();
            user.setId(userId);
            EaseCommonUtils.setUserInitialLetter(user);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (userId > 0)
                        getUserPersonalInfo(userId);
                }
            }).start();
        }
        return user;
    }

//	public List<EaseUser> getUserInfos(final List<String> usernames){
//		List<EaseUser> easeUsers = appModel.getUserExtInfos(usernames);
//		if (easeUsers == null || easeUsers.isEmpty()){
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					for (String username : usernames){
//						if (!"系统管理员".equals(username) && !"admin".equals(username) && !EaseConstant.AT_ALL_USER_NAME.equals(username))
//							getUserPersonalInfo(username);
//					}
//				}
//			}).start();
//		}
//
//		return easeUsers;
//
//
//	}

    public EaseUser getUserInfo(final String username) {
        EaseUser user = UserProvider.getInstance().getEaseUser(username);
        // if user is not in your contacts, set inital letter for him/her
        if (user == null) {
            user = new EaseUser();
            user.setUsername(username);
            user.setInitialLetter(username);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!"系统管理员".equals(username) && !"admin".equals(username) && !EaseConstant.AT_ALL_USER_NAME.equals(username))
                        getUserPersonalInfo(username);
                }
            }).start();
        }
        return user;
    }

    //获取员工信息
    private void getUserPersonalInfo(String easemobName) {
        EMAPIManager.getInstance().getUserByImUser(easemobName, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject jsonObj = new JSONObject(value);
                    JSONObject jsonEntity = jsonObj.optJSONObject("entity");
                    MPUserEntity userEntity = MPUserEntity.create(jsonEntity);
                    if (userEntity != null) {
                        appModel.saveUserInfo(userEntity);
                    }
                } catch (Exception e) {
                    MPLog.e(TAG, "getUserByImUser error :" + MPLog.getStackTraceString(e));
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                MPLog.e(TAG, "getUserByImUser error:" + errorMsg);
            }
        });
    }


    private void requestGroupInfo(String imGroupId) {
        EMAPIManager.getInstance().getGroupInfo(imGroupId, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject jsonObj = new JSONObject(value);
                    JSONObject jsonEntity = jsonObj.optJSONObject("entity");
                    MPGroupEntity groupEntity = MPGroupEntity.create(jsonEntity);
                    if (groupEntity == null) {
                        MPLog.e(TAG, "requestGroupInfo groupEntity is null");
                        return;
                    }
//                    GroupBean groupBean = new GroupBean(groupEntity.getId(), groupEntity.getImChatGroupId(),
//                            groupEntity.getName(),
//                            groupEntity.getAvatar(), groupEntity.getCreateTime());
                    appModel.saveGroupInfo(groupEntity);
                    MPEventBus.getDefault().post(new EventGroupsChanged());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                MPLog.d(TAG, "requestGroupInfo error:" + errorMsg);
            }
        });

    }

    private void requestGroupInfoById(int groupId) {
        EMAPIManager.getInstance().getGroupInfoById(groupId, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                JSONObject jsonObj = new JSONObject();
                JSONObject jsonEntity = jsonObj.optJSONObject("entity");
                MPGroupEntity groupEntity = MPGroupEntity.create(jsonEntity);
                if (groupEntity == null) {
                    MPLog.e(TAG, "requestGroupInfo groupEntity is null");
                    return;
                }
//                GroupBean groupBean = new GroupBean(groupEntity.getId(), groupEntity.getImChatGroupId(),
//                        groupEntity.getName(),
//                        groupEntity.getAvatar(), groupEntity.getCreateTime(), groupEntity.getType());
                appModel.saveGroupInfo(groupEntity);
                MPEventBus.getDefault().post(new EventGroupsChanged());
            }

            @Override
            public void onError(int error, String errorMsg) {
                MPLog.d(TAG, "requestGroupInfo error:" + errorMsg);
            }
        });

    }

    //获取员工信息
    private void getUserPersonalInfo(int userId) {
        EMAPIManager.getInstance().getUserInfo(userId, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject jsonObj = new JSONObject(value);
                    JSONObject jsonEntity = jsonObj.optJSONObject("entity");
                    MPUserEntity userEntity = MPUserEntity.create(jsonEntity);
                    if (userEntity != null) {
                        appModel.saveUserInfo(userEntity);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MPLog.e(TAG, "getUserInfo error:" + MPLog.getStackTraceString(e));
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                MPLog.e(TAG, "getUserInfo error:" + errorMsg);
            }
        });
    }


    public AppModel getModel() {
        return (AppModel) appModel;
    }


    private void initDbDao() {
        inviteMessageDao = new InviteMessageDao();
    }

    /**
     * save and notify invitation message
     *
     * @param msg
     */
    public void notifyNewInviteMessage(InviteMessage msg) {
        if (inviteMessageDao == null) {
            inviteMessageDao = new InviteMessageDao();
        }
        inviteMessageDao.saveMessage(msg);
        //increase the unread message count
        inviteMessageDao.saveUnreadMessageCount(1);
        // notify there is new message
        AppHelper.getInstance().getNotifier().vibrateAndPlayTone(null);
    }


    /**
     * get conversation sticky time
     */
    public String getStickyTime(String conversationId, EMConversation.EMConversationType type) {
        return ConversationDao.getStickyTime(conversationId, type);
    }

    /**
     * save conversation sticky time
     */
    public void saveStickyTime(String conversationId, String stickyTime, EMConversation.EMConversationType type) {
        ConversationDao.saveStickyTime(conversationId, stickyTime, type);
    }

    public void dealCmdMessages(List<EMMessage> cmdMessages) {
        List<EMMessage> tempMessages = new ArrayList<>(cmdMessages);
        for (EMMessage cmdMessage : tempMessages) {
            if (cmdMessage.getType() != EMMessage.Type.CMD) {
                continue;
            }
            try {
                dealCmdMessage(cmdMessage);
            } catch (HyphenateException e) {
                e.printStackTrace();
                MPLog.e(TAG, "" + MPLog.getStackTraceString(e));
            }
        }
    }

    //根据CMD消息判断更新内容，并保存在本地
    private void dealCmdMessage(EMMessage message) throws HyphenateException {
        Context appContext = EaseUI.getInstance().getContext();
        //get message body
        EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
        final String action = cmdMsgBody.action();//获取自定义action
//        MPLog.e(TAG, "cmdMessage, action = " + action);
        if (TextUtils.isEmpty(action)) {
            return;
        }

        if (CmdMessageFilter.dealWithSyncReadNotice(action, message)) {
            return;
        }

        if (CmdMessageFilter.dealWithServerGroupNotice(action, message)) {
            return;
        }

        if (CmdMessageFilter.dealWithScheduleCreateCmd(action, message)) {
            return;
        }

        if (CmdMessageFilter.dealWithOnlineOfflineNotice(action, message)) {
            return;
        }

        if ("broadcast".equals(action)) {
            JSONObject result = message.getJSONObjectAttribute("content");
            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            msg.setChatType(EMMessage.ChatType.Chat);
            msg.setFrom(message.getFrom());
            msg.setMsgId(UUID.randomUUID().toString());
            msg.setMsgTime(result.optLong("sendTime"));
            msg.addBody(new EMTextMessageBody(" " + result.optString("content")));
            msg.setStatus(EMMessage.Status.SUCCESS);
            msg.setUnread(true);
            // notify invitation message
            AppHelper.getInstance().getNotifier().vibrateAndPlayTone(msg);
            EMClient.getInstance().chatManager().saveMessage(msg);
            MPEventBus.getDefault().post(new EventEMMessageReceived(msg));
        }

//		if (action.equals(Constant.CMD_ACTION_START_LOCATION)) {
//			MPEventBus.getDefault().post(new EventLocUserAdded(message.getFrom(), message.getFrom(), (message.getChatType() == EMMessage.ChatType.GroupChat) ? Constant.CHATTYPE_GROUP : Constant.CHATTYPE_SINGLE));
//		} else
        if (action.equals(Constant.CMD_ACTION_END_LOCATION)) {
            MPEventBus.getDefault().post(new EventLocUserRemoved(message.getFrom(), message.getTo(), (message.getChatType() == EMMessage.ChatType.GroupChat) ? Constant.CHATTYPE_GROUP : Constant.CHATTYPE_SINGLE));
        } else if (action.equals(Constant.CMD_ACTION_REFRESH_LOCATION)) {
            String from = message.getFrom();
            int chatType = Constant.CHATTYPE_SINGLE;
            if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                chatType = Constant.CHATTYPE_GROUP;
            }
            JSONObject jsonObj = message.getJSONObjectAttribute(Constant.CMD_ACTION_REFRESH_LOCATION_LOC);
            double lat = jsonObj.optDouble(Constant.CMD_ACTION_REFRESH_LOCATION_LAT);
            double lng = jsonObj.optDouble(Constant.CMD_ACTION_REFRESH_LOCATION_LNG);
            float radius = (float) jsonObj.optDouble(Constant.CMD_ACTION_REFRESH_LOCATION_RADIUS);
            float direction = (float) jsonObj.optDouble(Constant.CMD_ACTION_REFRESH_LOCATION_DIRECTION);
            MPEventBus.getDefault().post(new EventLocChanged(lat, lng, radius, direction, message.getFrom(), message.getTo(), chatType));

        } else if (action.equals(Constant.BURN_AFTER_READING_CMD_ACTION)) {
            String msgId = message.getStringAttribute(Constant.BURN_AFTER_READING_DESTORY_MSGID, null);
            if (msgId != null) {
                EMMessage changeMessage = EMClient.getInstance().chatManager().getMessage(msgId);
                changeMessage.setAttribute(Constant.BURN_AFTER_READING_READED, true);
                EMClient.getInstance().chatManager().updateMessage(changeMessage);
                MPEventBus.getDefault().post(new MessageChanged(msgId));
            }
            return;
        }
        if (Constant.CMD_ACTION_INVITED_FRIEND.equals(action)) {
            MPEventBus.getDefault().post(new EventFriendNotify(message));
            return;
        }
        if (Constant.CMD_ACTION_ACCEPT_FRIEND.equals(action)) {
            MPEventBus.getDefault().post(new EventFriendNotify(message));
            return;
        }
        if (Constant.CMD_ACTION_DELETED_FRIEND.equals(action)) {
            MPEventBus.getDefault().post(new EventFriendNotify(message));
            return;
        }
        if (Constant.CMD_ACTION_USERS_REFRESH.equals(action)) {
            UserProvider.getInstance().clear();
            MPEventBus.getDefault().post(new EventUsersRefresh());
            return;
        }
        if (Constant.CMD_ACTION_ORGS_REFRESH.equals(action)) {
            MPEventBus.getDefault().post(new EventOrgsRefresh());
            return;
        }
        if (Constant.CMD_ACTION_ROLES_REFRESH.equals(action)) {
            MPEventBus.getDefault().post(new EventRolesRefresh());
            return;
        }

        if (Constant.CMD_ACTION_CLIENT_WHITE_BOARD.equals(action)) {
            JSONObject jsonObject = message.getJSONObjectAttribute(Constant.CMD_ACTION_CLIENT_WHITE_BOARD);

            MPEventBus.getDefault().post(new EventWhiteBoard(jsonObject.optString("roomName")));
            return;
        }
        if (Constant.CMD_ACTION_CLIENT_WHITE_BOARD_CLOSE.equals(action)) {
            MPEventBus.getDefault().post(new EventFinishWB());
            return;
        }

        if (Constant.CMD_ACTION_CONF_CANCEL.equals(action)) {
            MPLog.e(TAG, "recieved CMD_ACTION_CONF_CANCEL, nothing operater");
//            MPEventBus.getDefault().post(new EventConfCancel(message));
            return;
        }

        if (Constant.CMD_ACTION_GROUPS_CHANGED.equals(action)) {
            JSONArray jsonArray = message.getJSONArrayAttribute(Constant.CMD_EXT_GROUPS);

            if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                EMMessage emMsg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                emMsg.setMsgTime(message.getMsgTime());
                emMsg.setFrom(message.getFrom());
                emMsg.setTo(message.getTo());
                emMsg.setChatType(EMMessage.ChatType.GroupChat);
                emMsg.addBody(new EMTextMessageBody(appContext.getResources().getString(R.string.updated_group_info)));
                emMsg.setAttribute(EaseConstant.EXTRA_INVITE_USERID, message.getFrom());
                emMsg.setStatus(EMMessage.Status.SUCCESS);
                EMClient.getInstance().chatManager().saveMessage(emMsg);
            }

            long latestRfTime = PreferenceUtils.getInstance().getLatestGroupRefreshTime();
            if (latestRfTime > 0 && latestRfTime > message.getMsgTime()) {
                return;
            }
            // TODO. 更新了群规范
//            if (jsonArray != null) {
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.optJSONObject(i);
//                    if (jsonObject != null) {
//                        String groupId = jsonObject.optString("id");
//                        String groupNick = jsonObject.optString("name");
//                        String groupAvatar = jsonObject.optString("avatar");
//                        appModel.saveGroupInfo(groupId, groupNick, groupAvatar);
//                    }
//                }
//                MPEventBus.getDefault().post(new EventGroupsChanged());
//            }
//            return;
        }

        if (Constant.CMD_ACTION_ORGS_REMOVED.equals(action)) {
            try {
                String orgsJson = message.getStringAttribute(Constant.CMD_EXT_ORGS);
                MPLog.d(TAG, "orgsJson=" + orgsJson);
                List<MPOrgEntity> orgs = MPOrgEntity.create(new JSONArray(orgsJson));
                MPLog.d(TAG, "orgs=" + orgs.toString());
                EaseUI.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < orgs.size(); i++) {
                            MPOrgEntity entitiesBean = orgs.get(i);
                            appModel.delOrgInfo(entitiesBean.getId());
                        }
                        EventOrgsRemoved orgsRemoved = new EventOrgsRemoved();
                        orgsRemoved.setOrgEntities(orgs);
                        MPEventBus.getDefault().post(orgsRemoved);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        if (Constant.CMD_ACTION_ORGS_ADDED.equals(action)) {
            try {
                String orgsJson = message.getStringAttribute(Constant.CMD_EXT_ORGS);
                MPLog.d(TAG, "orgsJson=" + orgsJson);
                List<MPOrgEntity> orgs = MPOrgEntity.create(new JSONArray(orgsJson));
                MPLog.d(TAG, "orgs=" + orgs.toString());
                EaseUI.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < orgs.size(); i++) {
                            MPOrgEntity entitiesBean = orgs.get(i);
                            appModel.saveOrgInfo(entitiesBean);
                        }
                        EventOrgsAdded orgsAdded = new EventOrgsAdded();
                        orgsAdded.setOrgEntities(orgs);
                        MPEventBus.getDefault().post(orgsAdded);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

        if (Constant.CMD_ACTION_ORGS_CHANGED.equals(action)) {
            try {
                String orgsJson = message.getStringAttribute(Constant.CMD_EXT_ORGS);
                MPLog.d(TAG, "orgsJson=" + orgsJson);
                List<MPOrgEntity> orgs = MPOrgEntity.create(new JSONArray(orgsJson));
                MPLog.d(TAG, "orgs=" + orgs.toString());
                EaseUI.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < orgs.size(); i++) {
                            MPOrgEntity entitiesBean = orgs.get(i);
                            appModel.saveOrgInfo(entitiesBean);
                        }
                        EventOrgsChanged orgsChanged = new EventOrgsChanged();
                        orgsChanged.setOrgEntities(orgs);
                        MPEventBus.getDefault().post(orgsChanged);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }


        if (Constant.CMD_ACTION_USERS_REMOVED.equals(action)) {
            String usersJson = message.getStringAttribute(Constant.CMD_EXT_USERS);
            MPLog.d(TAG, "usersJson=" + usersJson);
            try {
                JSONArray jsonArr = new JSONArray(usersJson);
                List<MPUserEntity> users = MPUserEntity.create(jsonArr);
                EaseUI.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < users.size(); i++) {
                            MPUserEntity userEntty = users.get(i);
                            appModel.delUserInfo(userEntty.getId());
                            UserProvider.getInstance().removeEaseUser(userEntty.getImUserId());
                        }
                        EventUsersRemoved usersRemoved = new EventUsersRemoved();
                        usersRemoved.setUserEntities(users);
                        MPEventBus.getDefault().post(usersRemoved);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (Constant.CMD_ACTION_USERS_ADDED.equals(action)) {
            String usersJson = message.getStringAttribute(Constant.CMD_EXT_USERS);
            MPLog.d(TAG, "usersJson=" + usersJson);
            try {
                JSONArray jsonArr = new JSONArray(usersJson);
                List<MPUserEntity> users = MPUserEntity.create(jsonArr);
                EaseUI.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < users.size(); i++) {
                            MPUserEntity userEntty = users.get(i);
                            appModel.saveUserInfo(userEntty);
                        }
                        EventUsersAdded usersAdded = new EventUsersAdded();
                        usersAdded.setUsers(users);
                        MPEventBus.getDefault().post(usersAdded);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (Constant.CMD_ACTION_USERS_CHANGED.equals(action)) {
            String usersJson = message.getStringAttribute(Constant.CMD_EXT_USERS);
            MPLog.d(TAG, "usersJson=" + usersJson);
            try {
                JSONArray jsonArr = new JSONArray(usersJson);
                List<MPUserEntity> users = MPUserEntity.create(jsonArr);
                EaseUI.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < users.size(); i++) {
                            MPUserEntity userEntty = users.get(i);
                            appModel.saveUserInfo(userEntty);
                        }
                        EventUsersChanged usersChanged = new EventUsersChanged();
                        usersChanged.setUsers(users);
                        MPEventBus.getDefault().post(usersChanged);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MPLog.d(TAG, String.format("Command：action:%s,message:%s", action, message.toString()));
    }

    /**
     * 调用登录接口，如果登录成功,根据返回的IM信息，执行IM登录操作
     *
     * @param username 手机号
     * @param password 密码
     */
    public void appLogin(final String username, final String password, final EMCallBack callBack) {
        MPClient.get().appLogin(username, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                String easemobName = EMClient.getInstance().getCurrentUser();
                AppDBManager.initDB(easemobName);
                DraftManager.getInstance().loadDatas();
                NoDisturbManager.getInstance().loadDatas();
                asyncGetUserOptions();
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }

            @Override
            public void onError(int i, String s) {
                if (callBack != null) {
                    callBack.onError(i, s);
                }
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });

    }

    /**
     * 调用sso登录接口，如果登录成功,根据返回的IM信息，执行IM登录操作
     *
     * @param token
     */
    public void ssoLogin(final String token, final EMCallBack callBack) {
        MPClient.get().ssoLogin(token, new EMCallBack() {
            @Override
            public void onSuccess() {
                String easemobName = EMClient.getInstance().getCurrentUser();
                AppDBManager.initDB(easemobName);
                DraftManager.getInstance().loadDatas();
                NoDisturbManager.getInstance().loadDatas();
                asyncGetUserOptions();
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }

            @Override
            public void onError(int i, String s) {
                if (callBack != null) {
                    callBack.onError(i, s);
                }
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });

    }


    public void asyncGetUserOptions() {
        EMAPIManager.getInstance().getTenantOptions(new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                MPLog.d(TAG, "getTenantOptions:" + value);
                try {
                    JSONObject jsonObj = new JSONObject(value);
                    JSONArray jsonArr = jsonObj.optJSONArray("entities");
                    List<TenantOption> options = TenantOption.create(jsonArr);
                    TenantOptionsDao dao = new TenantOptionsDao();
                    dao.saveTenantOptions(options);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                MPLog.e(TAG, "getTenantOptions error:" + errorMsg);
            }
        });
    }

    /**
     * group change listener
     */
    class MyGroupChangeListener implements EMGroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {

            new InviteMessageDao().deleteMessage(groupId);

            // user invite you to join group
            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            msg.setGroupInviter(inviter);
            MPLog.d(TAG, "receive invitation to join the group：" + groupName);
            msg.setStatus(InviteMessageStatus.GROUPINVITATION);
            notifyNewInviteMessage(msg);
            MPEventBus.getDefault().post(new EventGroupsChanged());
        }

        @Override
        public void onInvitationAccepted(String groupId, String invitee, String reason) {

            new InviteMessageDao().deleteMessage(groupId);

            //user accept your invitation
            boolean hasGroup = false;
            EMGroup _group = null;
            for (EMGroup group : EMClient.getInstance().groupManager().getAllGroups()) {
                if (group.getGroupId().equals(groupId)) {
                    hasGroup = true;
                    _group = group;
                    break;
                }
            }
            if (!hasGroup)
                return;

            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            GroupBean groupInfo = EaseUserUtils.getGroupInfo(groupId);
            msg.setGroupName(groupInfo == null ? groupId : groupInfo.getNick());
            msg.setReason(reason);
            msg.setGroupInviter(invitee);
            showToast(invitee + "Accept to join the group：" + (groupInfo == null ? groupId : groupInfo.getNick()));
            msg.setStatus(InviteMessageStatus.GROUPINVITATION_ACCEPTED);
            notifyNewInviteMessage(msg);
            MPEventBus.getDefault().post(new EventGroupsChanged());
        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {

            new InviteMessageDao().deleteMessage(groupId);

            //user declined your invitation
            EMGroup group = null;
            for (EMGroup _group : EMClient.getInstance().groupManager().getAllGroups()) {
                if (_group.getGroupId().equals(groupId)) {
                    group = _group;
                    break;
                }
            }
            if (group == null)
                return;

            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            GroupBean groupInfo = EaseUserUtils.getGroupInfo(groupId);
            msg.setGroupName(groupInfo == null ? groupId : groupInfo.getNick());
            msg.setReason(reason);
            msg.setGroupInviter(invitee);
            MPLog.d(TAG, invitee + "Declined to join the group：" + (groupInfo == null ? groupId : groupInfo.getNick()));
            msg.setStatus(InviteMessageStatus.GROUPINVITATION_DECLINED);
            notifyNewInviteMessage(msg);
            MPEventBus.getDefault().post(new EventGroupsChanged());
        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
//            getModel().deleteGroupInfo(groupId);
        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {
            getModel().deleteGroupInfo(groupId);
        }

        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applyer, String reason) {

            // user apply to join group
            InviteMessage msg = new InviteMessage();
            msg.setFrom(applyer);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            showToast(applyer + " Apply to join group：" + groupId);
            msg.setStatus(InviteMessageStatus.BEAPPLYED);
            notifyNewInviteMessage(msg);
            MPEventBus.getDefault().post(new EventGroupsChanged());
        }

        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {

            String st4 = EaseUI.getInstance().getContext().getString(R.string.Agreed_to_your_group_chat_application);
            // your application was accepted
            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            msg.setChatType(EMMessage.ChatType.GroupChat);
            msg.setFrom(accepter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new EMTextMessageBody(accepter + " " + st4));
            msg.setStatus(EMMessage.Status.SUCCESS);
            // save accept message
            EMClient.getInstance().chatManager().saveMessage(msg);
            // notify the accept message
            getNotifier().vibrateAndPlayTone(msg);

            showToast("request to join accepted, groupId:" + groupId);
            MPEventBus.getDefault().post(new EventGroupsChanged());
        }

        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
            // your application was declined, we do nothing here in demo
            showToast("request to join declined, groupId:" + groupId);
        }

        @Override
        public void onAutoAcceptInvitationFromGroup(final String groupId, String inviter, String inviteMessage) {
//            if ("系统管理员".equals(inviter)) {
//            } else {
//                saveInviteMsgFromSystem(groupId, inviter);
//            }
        }

        // ============================= group_reform new add api begin
        @Override
        public void onMuteListAdded(String groupId, final List<String> mutes, final long muteExpire) {
//            setGroupMuteTip(groupId, mutes, R.string.set_mute_someone);
//            AppHelper.getInstance().getModel().muteGroupUsernames(groupId, mutes, muteExpire);
        }


        @Override
        public void onMuteListRemoved(String groupId, final List<String> mutes) {
//            setGroupMuteTip(groupId, mutes, R.string.cancel_mute_someone);
//            AppHelper.getInstance().getModel().unMuteGroupUsernames(groupId, mutes);
        }

        @Override
        public void onWhiteListAdded(String s, List<String> list) {

        }

        @Override
        public void onWhiteListRemoved(String s, List<String> list) {

        }

        @Override
        public void onAllMemberMuteStateChanged(String s, boolean b) {

        }


        @Override
        public void onAdminAdded(String groupId, String administrator) {
            showGroupTip(groupId, administrator, R.string.set_admin_someone);
        }

        @Override
        public void onAdminRemoved(String groupId, String administrator) {
            showGroupTip(groupId, administrator, R.string.cancel_admin_someone);
        }

        @Override
        public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
//            showGroupTip(groupId, newOwner, R.string.change_owner_someone);
        }

        @Override
        public void onMemberJoined(String groupId, String member) {
//			showGroupTip(groupId, member, R.string.joined_someone);
//            putJoinedGroup(groupId, member);
        }

        @Override
        public void onMemberExited(String groupId, String member) {
//            showGroupTip(groupId, member, R.string.exited_someone);
        }

        @Override
        public void onAnnouncementChanged(String groupId, String announcement) {
//			showToast("onAnnouncementChanged, groupId" + groupId);
        }

        @Override
        public void onSharedFileAdded(String groupId, EMMucSharedFile sharedFile) {
//			showToast("onSharedFileAdded, groupId" + groupId);
            String username = sharedFile.getFileOwner();
            String fileName = sharedFile.getFileName();
            String message = "共享了群文件：" + fileName;
            tempSaveMessage(groupId, username, message);
        }

        @Override
        public void onSharedFileDeleted(String groupId, String fileId) {
//			showToast("onSharedFileDeleted, groupId" + groupId);
        }
        // ============================= group_reform new add api end
    }

    private void tempSaveMessage(final String groupId, final String newOwner, final String message) {
        final long msgTime = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                msgNotification.setChatType(EMMessage.ChatType.GroupChat);
                EMTextMessageBody txtBody = new EMTextMessageBody(message);
                msgNotification.addBody(txtBody);
                msgNotification.setFrom(newOwner);
                msgNotification.setTo(groupId);
                msgNotification.setMsgTime(msgTime);
                msgNotification.setLocalTime(msgTime);
                msgNotification.setAttribute(EaseConstant.EXTRA_INVITE_USERID, newOwner);
                msgNotification.setStatus(EMMessage.Status.SUCCESS);
                EMClient.getInstance().chatManager().saveMessage(msgNotification);
                getNotifier().vibrateAndPlayTone(msgNotification);
                MPEventBus.getDefault().post(new EventGroupsChanged());
            }
        }).start();
    }


//    private Map<String, List<String>> membersJoinedGroup = new ConcurrentHashMap<>();

//    private void putJoinedGroup(String groupId, String member) {
//        List<String> members;
//        if (membersJoinedGroup.containsKey(groupId)) {
//            members = membersJoinedGroup.get(groupId);
//        } else {
//            members = new ArrayList<>();
//        }
//        if (!members.contains(member)) {
//            members.add(member);
//        }
//        membersJoinedGroup.put(groupId, members);
//        memberHandler.removeMessages(3);
//        memberHandler.sendEmptyMessageDelayed(3, 1000);
//    }

    private void dealJoinedGroup(String groupId, List<String> members) {

        final JSONArray jsonUserArr = new JSONArray();
        for (String item : members) {
            JSONObject jsonUser = new JSONObject();
            try {
                jsonUser.put("username", item);
                jsonUserArr.put(jsonUser);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        final long msgTime = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                msgNotification.setChatType(EMMessage.ChatType.GroupChat);
                EMTextMessageBody txtBody = new EMTextMessageBody(EaseUI.getInstance().getContext().getResources().getString(R.string.member_joined));
                msgNotification.addBody(txtBody);
                msgNotification.setFrom(EMClient.getInstance().getCurrentUser());
                msgNotification.setTo(groupId);
                msgNotification.setMsgTime(msgTime);
                msgNotification.setLocalTime(msgTime);
                msgNotification.setAttribute(EaseConstant.EXTRA_INVITE_USERS, jsonUserArr);
                msgNotification.setStatus(EMMessage.Status.SUCCESS);
                EMClient.getInstance().chatManager().saveMessage(msgNotification);
                getNotifier().vibrateAndPlayTone(msgNotification);
                MPEventBus.getDefault().post(new EventGroupsChanged());
            }
        }).start();

    }

//    private HandlerThread mHandlerThread = new HandlerThread("memberJoined");
//
//    {
//        mHandlerThread.start();
//    }
//
//    private Handler memberHandler = new Handler(mHandlerThread.getLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 3) {
//                for (Map.Entry<String, List<String>> item : membersJoinedGroup.entrySet()) {
//                    dealJoinedGroup(item.getKey(), item.getValue());
//                }
//                membersJoinedGroup.clear();
//            }
//
//        }
//    };


    private void setGroupMuteTip(final String groupId, List<String> mutes, final int res) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mutes.size(); i++) {
            String member = mutes.get(i);
            EaseUser userExtInfo = EaseUserUtils.getUserInfo(member);
            sb.append(userExtInfo != null ? userExtInfo.getNickname() : member);
            if (mutes.size() > 1 && i < mutes.size() - 1)
                sb.append(",");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                msgNotification.setChatType(EMMessage.ChatType.GroupChat);
                EMTextMessageBody messageBody = new EMTextMessageBody(String.format(EaseUI.getInstance().getContext().getResources().getString(res), sb.toString()));
                msgNotification.addBody(messageBody);
                EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
                if (group == null) {
                    return;
                }
                EaseUser userExtInfo = EaseUserUtils.getUserInfo(group.getOwner());
                msgNotification.setFrom(userExtInfo != null ? userExtInfo.getNickname() : group.getOwner());
                msgNotification.setTo(groupId);
                msgNotification.setMsgTime(System.currentTimeMillis());
                msgNotification.setLocalTime(System.currentTimeMillis());
                msgNotification.setAttribute(Constant.MESSAGE_TYPE_RECALL, true);
                msgNotification.setStatus(EMMessage.Status.SUCCESS);
                EMClient.getInstance().chatManager().saveMessage(msgNotification);
                getNotifier().vibrateAndPlayTone(msgNotification);
                MPEventBus.getDefault().post(new EventGroupsChanged());
            }
        }).start();
    }

    private void showGroupTip(final String groupId, final String newOwner, final int change_owner_someone) {
        final long msgTime = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                msgNotification.setChatType(EMMessage.ChatType.GroupChat);
                EMTextMessageBody txtBody = new EMTextMessageBody(EaseUI.getInstance().getContext().getResources().getString(change_owner_someone));
                msgNotification.addBody(txtBody);
                msgNotification.setFrom(newOwner);
                msgNotification.setTo(groupId);
                msgNotification.setMsgTime(msgTime);
                msgNotification.setLocalTime(msgTime);
                msgNotification.setAttribute(EaseConstant.EXTRA_INVITE_USERID, newOwner);
                msgNotification.setStatus(EMMessage.Status.SUCCESS);
                EMClient.getInstance().chatManager().saveMessage(msgNotification);
                getNotifier().vibrateAndPlayTone(msgNotification);
                MPEventBus.getDefault().post(new EventGroupsChanged());
            }
        }).start();
    }

    private EaseNotifier getNotifier() {
        return AppHelper.getInstance().getNotifier();
    }

    class MyMultiDeviceListener implements EMMultiDeviceListener {

        @Override
        public void onContactEvent(int event, String target, String ext) {
        }

        @Override
        public void onGroupEvent(final int event, final String target, final List<String> usernames) {
            EaseUI.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        String groupId = target;
                        switch (event) {
                            case GROUP_CREATE:
                                showToast("GROUP_CREATE");
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_CREATE);
                                break;
                            case GROUP_DESTROY:
                                showToast("GROUP_DESTROY");
                                inviteMessageDao.deleteGroupMessage(groupId);
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_DESTROY);
                                MPEventBus.getDefault().post(new EventGroupsChanged());
                                break;
                            case GROUP_JOIN:
                                showToast("GROUP_JOIN");
                                MPEventBus.getDefault().post(new EventGroupsChanged());
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_JOIN);
                                break;
                            case GROUP_LEAVE:
                                showToast("GROUP_LEAVE");
                                inviteMessageDao.deleteGroupMessage(groupId);
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_LEAVE);
                                MPEventBus.getDefault().post(new EventGroupsChanged());
                                break;
                            case GROUP_APPLY:
                                showToast("GROUP_APPLY");
                                inviteMessageDao.deleteGroupMessage(groupId);
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY);
                                break;
                            case GROUP_APPLY_ACCEPT:
                                showToast("GROUP_ACCEPT");
                                inviteMessageDao.deleteGroupMessage(groupId, usernames.get(0));
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY_ACCEPT);
                                break;
                            case GROUP_APPLY_DECLINE:
                                showToast("GROUP_APPLY_DECLINE");
                                inviteMessageDao.deleteGroupMessage(groupId, usernames.get(0));
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY_DECLINE);
                                break;
                            case GROUP_INVITE:
                                showToast("GROUP_INVITE");
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE);
                                break;
                            case GROUP_INVITE_ACCEPT:
                                showToast("GROUP_INVITE_ACCEPT");
                                String st3 = EaseUI.getInstance().getContext().getString(R.string.Invite_you_to_join_a_group_chat);
                                EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                                msg.setChatType(EMMessage.ChatType.GroupChat);
                                String from = "";
                                if (usernames != null && usernames.size() > 0) {
                                    msg.setFrom(usernames.get(0));
                                }
                                msg.setTo(groupId);
                                msg.setMsgId(UUID.randomUUID().toString());
                                EaseUser userInfo = EaseUserUtils.getUserInfo(msg.getFrom());
                                msg.addBody(new EMTextMessageBody(userInfo == null ? msg.getFrom() : userInfo.getNickname() + " " + st3));
                                msg.setStatus(EMMessage.Status.SUCCESS);
                                // save invitation as messages
                                EMClient.getInstance().chatManager().saveMessage(msg);

                                inviteMessageDao.deleteMessage(groupId);
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE_ACCEPT);
                                MPEventBus.getDefault().post(new EventGroupsChanged());
                                break;
                            case GROUP_INVITE_DECLINE:
                                inviteMessageDao.deleteMessage(groupId);
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE_DECLINE);
                                break;
                            case GROUP_KICK:
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE_DECLINE);
                                break;
                            case GROUP_BAN:
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_BAN);
                                break;
                            case GROUP_ALLOW:
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ALLOW);
                                break;
                            case GROUP_BLOCK:
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_BLOCK);
                                break;
                            case GROUP_UNBLOCK:
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_UNBLOCK);
                                break;
                            case GROUP_ASSIGN_OWNER:
                                showGroupTip(groupId, usernames.get(0), R.string.change_owner_someone);
                                break;
                            case GROUP_ADD_ADMIN:
                                showGroupTip(groupId, usernames.get(0), R.string.set_admin_someone);
                                break;
                            case GROUP_REMOVE_ADMIN:
                                showGroupTip(groupId, usernames.get(0), R.string.cancel_admin_someone);
                                break;
                            case GROUP_ADD_MUTE:
                                setGroupMuteTip(groupId, usernames, R.string.set_mute_someone);
                                break;
                            case GROUP_REMOVE_MUTE:
                                setGroupMuteTip(groupId, usernames, R.string.cancel_mute_someone);
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        private void saveGroupNotification(String groupId, String groupName, String inviter, String reason, InviteMessageStatus status) {
            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            msg.setGroupInviter(inviter);
            MPLog.d(TAG, "receive invitation to join the group：" + groupName);
            msg.setStatus(status);
            notifyNewInviteMessage(msg);
        }

        private void updateGroupNotificationStatus(String groupId, String groupName, String inviter, String reason, InviteMessageStatus status) {
            InviteMessage msg = null;
            for (InviteMessage _msg : inviteMessageDao.getMessagesList()) {
                if (_msg.getGroupId().equals(groupId)) {
                    msg = _msg;
                    break;
                }
            }
            if (msg != null) {
                ContentValues values = new ContentValues();
                msg.setStatus(status);
                values.put(InviteMessageDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
                inviteMessageDao.updateMessage(msg.getUserId(), values);
            }
        }
    }

    /**
     * register group and contact listener, you need register when login
     */
    public void registerGroupAndContactListener() {
        if (!isGroupAndContactListenerRegisted) {
            EMClient.getInstance().groupManager().addGroupChangeListener(mMyGroupChangeListener);
            EMClient.getInstance().addMultiDeviceListener(mMyMultiDeviceListener);
            isGroupAndContactListenerRegisted = true;
        }
    }


    /**
     * Get group list from server
     * This method will save the sync state
     *
     * @throws HyphenateException
     */
    public synchronized void asyncFetchGroupsFromServer(final EMCallBack callback) {
        boolean isSynced = getModel().isGroupsSynced();
        if (isSynced) {
            return;
        }

        if (isSyncingGroupsWithServer) {
            return;
        }

        isSyncingGroupsWithServer = true;


//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    new EMJoinedGroupsRequest().request();
//                    List<EMGroup> groups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
//                    // in case that logout already before server returns, we should return immediately
//                    if (!isLoggedIn()) {
//                        isGroupsSyncedWithServer = false;
//                        isSyncingGroupsWithServer = false;
//                        noitifyGroupSyncListeners(false);
//                        return;
//                    }
//
//                    getModel().setGroupsSynced(true);
//
//                    isGroupsSyncedWithServer = true;
//                    isSyncingGroupsWithServer = false;
//
//                    //notify sync group list success
//                    noitifyGroupSyncListeners(true);
//
//                    if (callback != null) {
//                        callback.onSuccess();
//                    }
//                } catch (HyphenateException e) {
//                    getModel().setGroupsSynced(false);
//                    isGroupsSyncedWithServer = false;
//                    isSyncingGroupsWithServer = false;
//                    noitifyGroupSyncListeners(false);
//                    if (callback != null) {
//                        callback.onError(e.getErrorCode(), e.toString());
//                    }
//                }
//
//            }
//        }.start();
    }

    public void noitifyGroupSyncListeners(boolean success) {
        for (DataSyncListener listener : syncGroupsListeners) {
            listener.onSyncComplete(success);
        }
    }

    /**
     * data sync listener
     */
    public interface DataSyncListener {

        /**
         * sync complete
         *
         * @param success true：data sync successful，false: failed to sync data
         */
        void onSyncComplete(boolean success);

    }


    //保存邀请入群信息
    private void saveInviteMsgFromSystem(String groupId, String inviter) {
        // got an invitation
        String st3 = EaseUI.getInstance().getContext().getString(R.string.Invite_you_to_join_a_group_chat);
        EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        msg.setChatType(EMMessage.ChatType.GroupChat);
        msg.setFrom(inviter);
        msg.setTo(groupId);
        msg.setMsgId(UUID.randomUUID().toString());
        msg.addBody(new EMTextMessageBody(" " + st3));
        msg.setStatus(EMMessage.Status.SUCCESS);
        msg.setAttribute(EaseConstant.EXTRA_INVITE_USERID, inviter);
        // save invitation as messages
        EMClient.getInstance().chatManager().saveMessage(msg);
        // notify invitation message
        getNotifier().vibrateAndPlayTone(msg);
        showToast("auto accept invitation from groupId:" + groupId);
        MPEventBus.getDefault().post(new EventGroupsChanged());
    }


    private void showToast(final String message) {
        MPLog.d(TAG, "receive invitation to join the group：" + message);
    }


    public void addEMConnectionListener() {
        // create the global connection listener
        MPClient.get().addEMConnectionListener();

        //register connection listener
//        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
//            @Override
//            public void onConnected() {
//                // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
//                if (!isGroupsSyncedWithServer) {
//                    asyncFetchGroupsFromServer(null);
//                } else {
//                    com.hyphenate.easemob.imlibs.mp.utils.MPLog.d(TAG, "group and contact already synced with servre");
//                }
//            }
//
//            @Override
//            public void onDisconnected(int i) {
//
//            }
//        });
    }


    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }


    /**
     * logout
     *
     * @param unbindDeviceToken whether you need unbind your device token
     * @param callback          callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        MPLog.d(TAG, "logout: " + unbindDeviceToken);
        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                MPLog.d(TAG, "logout: onSuccess");
                internalLogout();
                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {
                logout(false);
                if (callback != null) {
                    callback.onSuccess();
                }
            }
        });
    }

    public void logout(boolean unBind) {
        EMClient.getInstance().logout(unBind);
        internalLogout();
    }

    private void internalLogout() {
		MPLog.e("#####", "internalLogout");
        if (AppDBManager.getInstance() != null) {
            getModel().deleteAllGroup();
            getModel().deleteAllUsers();
            getModel().deleteAllOrgs();
            AppDBManager.getInstance().closeDB();
        }
        DraftManager.getInstance().clear();
        NoDisturbManager.getInstance().clear();
        StickerManager.get().clearAll();
        UserProvider.getInstance().clear();
        EMClient.getInstance().conferenceManager().exitConference(null);
        PreferenceManager.getInstance().clearAll();
        isSyncingGroupsWithServer = false;
        getModel().setGroupsSynced(false);

        isGroupsSyncedWithServer = false;
        isGroupAndContactListenerRegisted = false;
        companyOrg = null;
        EMAllOrgRequest.cancel();
        EMAllUserRequest.cancel();
        EMMyOrgUsersRequest.Companion.cancel();
    }


    public void forceLogout(boolean unBind, final EMCallBack callBack) {
        MPLog.d(TAG, "forceLogout->" + unBind);
        EMClient.getInstance().logout(unBind, new EMCallBack() {
            @Override
            public void onSuccess() {
                internalLogout();
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }

            @Override
            public void onError(int i, String s) {
                EMClient.getInstance().logout(false);
                internalLogout();
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    public void onCreateMain() {
        asyncLoadIMServerData();
        asyncPostDeviceInfo();
        asyncFetchGroupsFromServer(null);
    }

    private void asyncPostDeviceInfo() {
        int tenantId = BaseRequest.getTenantId();
        if (tenantId == -1) {
            return;
        }
        Context appContext = EaseUI.getInstance().getContext();
        if (appContext == null) {
            return;
        }
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("systemCategory", "Android");
            jsonBody.put("appVersion", AppUtil.getAppVersionName(appContext) + "(" + AppUtil.getAppVersionCode(appContext) + ")");
            jsonBody.put("systemVersion", DeviceUtils.getModel());
            jsonBody.put("userId", BaseRequest.getUserId() + "");
            jsonBody.put("vendor", DeviceUtils.getManufacturer());
            jsonBody.put("extension", PhoneUtils.getSimOperatorByMnc());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EMAPIManager.getInstance().postDeviceInfo(BaseRequest.getTenantId(), jsonBody.toString(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                MPLog.d(TAG, "postDeviceInfo success");
            }

            @Override
            public void onError(int error, String errorMsg) {
                MPLog.e(TAG, "postDeviceInfo error:" + error + ", errorMsg:" + errorMsg);
            }
        });

    }


    private void asyncLoadIMServerData() {
        EaseUI.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().pushManager().getPushConfigsFromServer();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void addConnectionListener(ConnectionListener connectionListener) {
        MPClient.get().addConnectionListener(connectionListener);
    }

    public void removeConnectionListener(ConnectionListener connectionListener) {
        MPClient.get().removeConnectionListener(connectionListener);
    }


    synchronized void clearCurrentUserInfo() {
        MPClient.get().clearCurrentUserInfo();
    }


}
