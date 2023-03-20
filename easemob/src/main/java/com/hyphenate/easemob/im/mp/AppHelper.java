package com.hyphenate.easemob.im.mp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConferenceListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.easemob.im.EnvHelper;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.EaseMessageUtils;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.DraftEntity;
import com.hyphenate.easemob.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easemob.easeui.model.EaseNotifier;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.im.officeautomation.domain.NoDisturbEntity;
import com.hyphenate.easemob.im.officeautomation.domain.VoteMsgEntity;
import com.hyphenate.easemob.im.officeautomation.http.BaseRequest;
import com.hyphenate.easemob.im.officeautomation.utils.CommonUtils;
import com.hyphenate.easemob.imlibs.cache.OnlineCache;
import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;
import com.hyphenate.easemob.imlibs.mp.ConnectionListener;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.imlibs.mp.events.EventDealLater;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.imlibs.mp.events.EventEMMessageReceived;
import com.hyphenate.easemob.im.mp.manager.NoDisturbManager;
import com.hyphenate.easemob.im.mp.manager.DraftManager;
import com.hyphenate.easemob.imlibs.mp.prefs.PrefsUtil;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.db.AppDBManager;
import com.hyphenate.easemob.im.officeautomation.domain.ReferenceMsgEntity;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;
import com.hyphenate.easemob.im.officeautomation.ui.ChatActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.utils.MyToast;
import com.lxj.xpopup.XPopup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AppHelper {

    protected static final String TAG = "AppHelper";

    private EaseUI easeUI;

    @SuppressLint("StaticFieldLeak")
    private static AppHelper instance = new AppHelper();

    private String username;

    protected Handler handler = new Handler(Looper.getMainLooper());

    private boolean isInCalling;


    //true 静音  false 非静音
    public Map<String, Boolean> confMuteList;


    private Context context;

    private AppHelper() {

    }

    public static AppHelper getInstance() {
        return instance;
    }

    /**
     * init helper
     *
     * @param context application context
     */
    public void init(Context context, EnvHelper.BaseEnv baseEnv) {
        this.context = context;
        InternalAppHelper.getInstance().init(context);
        PrefsUtil.getInstance().init(context);
        EMOptions options = initChatOptions();
        if(baseEnv == null){
            Log.e(TAG, "baseEnv is null");
            return;
        }
        options.enableDNSConfig(baseEnv.enableDnsConfig());
        if (!baseEnv.enableDnsConfig()) {
            options.setRestServer(baseEnv.restServer());
            options.setImPort(baseEnv.imPort());
            options.setIMServer(baseEnv.imServer());
        }
        options.setAppKey(baseEnv.appKey());

        PrefsUtil.getInstance().setWhiteBoardServer(baseEnv.wbServer());

        //use default options if options is null
        if (MPClient.get().init(context, options)) {

            EaseUI.getInstance().setAppServer(baseEnv.ccsServer());
            EaseUI.getInstance().init(context, options);
            //debug mode, you'd better set it to false, if you want release your App officially.
            EMClient.getInstance().setDebugMode(true);
            //get easeui instance
            easeUI = EaseUI.getInstance();

            if (!TextUtils.isEmpty(EMClient.getInstance().getCurrentUser())) {
                AppDBManager.initDB(EMClient.getInstance().getCurrentUser());
                DraftManager.getInstance().loadDatas();
                NoDisturbManager.getInstance().loadDatas();
                InternalAppHelper.getInstance().asyncGetUserOptions();
            }

            //to set user's profile and avatar
            setEaseUIProviders();
            //set Global listeners
            setGlobalListeners();

            Stetho.initializeWithDefaults(context);

            XPopup.setPrimaryColor(context.getResources().getColor(com.hyphenate.easemob.R.color.text_btn));
            OnlineCache.init(30, TimeUnit.SECONDS);
        }

        confMuteList = new HashMap<>();
    }

    public Context getAppContext(){
        return context;
    }


    public boolean isPad(Context context) {
//        return (context.getResources().getConfiguration().screenLayout
//                & Configuration.SCREENLAYOUT_SIZE_MASK)
//                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        return false;
    }

    private EMOptions initChatOptions() {
        Log.e(TAG, "init HuanXin Options");

        EMOptions options = new EMOptions();
        // set if accept the invitation automatically
        options.setAcceptInvitationAlways(true);
        // set if you need read ack
        options.setRequireAck(true);
        // set if you need delivery ack
        options.setRequireDeliveryAck(false);
        options.setSortMessageByServerTime(true);

        options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());
        options.setDeleteMessagesAsExitGroup(getModel().isDeleteMessagesAsExitGroup());
        options.setAutoAcceptGroupInvitation(getModel().isAutoAcceptGroupInvitation());
        // Whether the message attachment is automatically uploaded to the Hyphenate server,
        options.setAutoTransferMessageAttachments(getModel().isSetTransferFileByUser());
        // Set Whether auto download thumbnail, default value is true.
        options.setAutoDownloadThumbnail(getModel().isSetAutodownloadThumbnail());

        return options;
    }

    public void resetCallStatus() {
        isInCalling = false;
    }

    public void makeAppInCalling(){
        isInCalling = true;
    }

    public boolean isInCalling() {
        return isInCalling;
    }
    protected void setEaseUIProviders() {
        //set user avatar to circle shape
        EaseAvatarOptions avatarOptions = new EaseAvatarOptions();
        avatarOptions.setAvatarShape(1);
        easeUI.setAvatarOptions(avatarOptions);

        // set profile provider if you want easeUI to handle avatar and nickname
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(int userId) {
                return getUserInfo(userId);
            }

            @Override
            public EaseUser getUser(String imUserId) {
                return getUserInfo(imUserId);
            }

            @Override
            public GroupBean getGroupBean(String imGroupId) {
                return getGroupInfo(imGroupId);
            }

            @Override
            public GroupBean getGroupBeanById(int groupId) {
                return getGroupInfoById(groupId);
            }

            @Override
            public boolean isNoDisturb(String noDisturbId) {
                return NoDisturbManager.getInstance().hasNoDisturb(noDisturbId);
            }
        });

        //set options
        easeUI.setSettingsProvider(new EaseUI.EaseSettingsProvider() {

            @Override
            public boolean isSpeakerOpened() {
                return getModel().getSettingMsgSpeaker();
            }

            @Override
            public boolean isMsgVibrateAllowed(EMMessage message) {
                return getModel().getSettingMsgVibrate();
            }

            @Override
            public boolean isMsgSoundAllowed(EMMessage message) {
                return getModel().getSettingMsgSound();
            }

            @Override
            public boolean isMsgNotifyAllowed(EMMessage message) {
                if (message == null) {
                    return getModel().getSettingMsgNotification();
                }
                if (!getModel().getSettingMsgNotification()) {
                    return false;
                } else {
                    String chatUsename = null;
                    List<String> notNotifyIds = null;
                    // get user or group id which was blocked to show message notifications
                    if (message.getChatType() == EMMessage.ChatType.Chat) {
                        chatUsename = message.getFrom();
                        notNotifyIds = getModel().getDisabledIds();
                    } else {
                        chatUsename = message.getTo();
                        notNotifyIds = getModel().getDisabledGroups();
                    }

                    if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });
        //set notification options, will use default if you don't set it
        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                //you can update title here
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //you can update icon here
                return 0;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                Context appContext = context;
                if (AppHelper.getInstance().getModel().isShowNotifyDetails()) {
                    // be used on notification bar, different text according the message type.
                    String ticker = EaseCommonUtils.getMessageDigest(message, context, false);
                    if (message.getType() == EMMessage.Type.TXT) {
                        ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                    }
                    EaseUser user = EaseUserUtils.getUserInfo(message.getFrom());
                    if (user != null) {
                        if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
                            return String.format(appContext.getString(R.string.at_your_in_group), user.getNick());
                        }
                        return user.getNick() + ": " + ticker;
                    } else {
                        if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
                            return String.format(appContext.getString(R.string.at_your_in_group), message.getFrom());
                        }
                        return message.getFrom() + ": " + ticker;
                    }
                } else {
                    return null;
                }
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                // here you can customize the text.
                // return fromUsersNum + "contacts send " + messageNum + "messages to you";
                if (AppHelper.getInstance().getModel().isShowNotifyDetails()) {
                    return "[" + messageNum + "条]" + getDisplayedText(message);
                } else {
                    return null;
                }
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                Intent intent = null;
                JSONObject confJson = null;
                try {
                    confJson = message.getJSONObjectAttribute(EaseConstant.MSG_ATTR_CONFERENCE);
                } catch (HyphenateException e) {
//                    e.printStackTrace();
                }

//                if (confJson != null) {
//                    intent = getConferenceIntent(message, confJson);
//                } else
//
                if (confJson == null) {
                    // you can set what activity you want display when user click the notification
                    intent = new Intent(EaseUI.getInstance().getContext(), ChatActivity.class);
                    // open calling activity if there is call
                    EMMessage.ChatType chatType = message.getChatType();
                    if (chatType == EMMessage.ChatType.Chat) { // single chat message
                        intent.putExtra("userId", message.getFrom());
                        intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
                    } else { // group chat message
                        // message.getTo() is the group id
                        intent.putExtra("userId", message.getTo());
                        if (chatType == EMMessage.ChatType.GroupChat) {
                            intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                        } else {
                            intent.putExtra("chatType", Constant.CHATTYPE_CHATROOM);
                        }

                    }
                }
                return intent;
            }
        });
    }

    private GroupBean getGroupInfo(String groupId) {
        return InternalAppHelper.getInstance().getGroupInfo(groupId);
    }

    private GroupBean getGroupInfoById(int groupId) {
        return InternalAppHelper.getInstance().getGroupInfoById(groupId);
    }


    private EaseUser getUserInfo(int userId) {
        return InternalAppHelper.getInstance().getUserInfo(userId);
    }

    private EaseUser getUserInfo(String username) {
        return InternalAppHelper.getInstance().getUserInfo(username);
    }

    private EMConferenceListener emConferenceListener;

    public void setConferenceListener(EMConferenceListener listener) {
        this.emConferenceListener = listener;
    }

    public void clearListeners() {
        if (conferenceListener != null) {
            EMClient.getInstance().conferenceManager().removeConferenceListener(conferenceListener);
        }
    }

    private EMConferenceListener conferenceListener = null;

    /**
     * set global listener
     */
    protected void setGlobalListeners() {

        InternalAppHelper.getInstance().addEMConnectionListener();

        //register group and contact event listener
        registerGroupAndContactListener();
        //register message event listener
        registerMessageListener();
    }

    /**
     * register group and contact listener, you need register when login
     */
    public void registerGroupAndContactListener() {
        InternalAppHelper.getInstance().registerGroupAndContactListener();
    }

    /**
     * Global listener
     * If this event already handled by an activity, you don't need handle it again
     * activityList.size() <= 0 means all activities already in background or not in Activity Stack
     */
    private void registerMessageListener() {
        EMMessageListener messageListener = new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                MPLog.e(TAG, "onMessageReceived-size:" + messages.size());
                List<EMMessage> cacheMsgs = new ArrayList<>();
                for (EMMessage message : messages) {
                    PrefsUtil.getInstance().setTimeStamp(message.getMsgTime());
                    MPLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
                    saveReferenceMsg(message);
                    saveVoteData(message);
                    if (!EaseMessageUtils.isVideoInviteMessage(message)) {
                        // in background, do not refresh UI, notify it in notification bar
                        if (!easeUI.hasForegroundActivies() && !message.getFrom().equals(EMClient.getInstance().getCurrentUser())) {
                            cacheMsgs.add(message);
                        }
                        MPEventBus.getDefault().post(new EventEMMessageReceived(message));
                    }
                }
                if (!cacheMsgs.isEmpty()) {
                    getNotifier().onNewMesg(cacheMsgs);
                }
                cacheMsgs.clear();
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                InternalAppHelper.getInstance().dealCmdMessages(messages);
                for (EMMessage message : messages) {
                    if (message.getType() != EMMessage.Type.CMD) {
                        continue;
                    }
                    EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action();//获取自定义action
                    if (TextUtils.isEmpty(action)) {
                        MPLog.e(TAG, "received cmd message but action is null or empty");
                        return;
                    }

                    if(TextUtils.equals("app_config_update", action)){
                        // 服务配置修改
                        try {
                            JSONObject jsonObject = message.getJSONObjectAttribute("content");
                            if (jsonObject != null){
                                String code = jsonObject.optString("code");
                                String configValue = jsonObject.optString("configValue");
                                if(TextUtils.equals(code, "msg_recall")){
                                    PreferenceUtils.getInstance().setRecallDuration(Long.parseLong(configValue));
                                } else if (TextUtils.equals(code, "read_ack")){
                                    PreferenceUtils.getInstance().setShowRead(Boolean.parseBoolean(configValue));
                                }  else if (TextUtils.equals(code, "voice_duration")){
                                    PreferenceUtils.getInstance().setVoiceDuration(Long.parseLong(configValue));
                                }
                            }
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    } else if(TextUtils.equals(action, "groupVoteOneVoted")){
                        // 有人投票
                        try {
                            JSONObject jsonObject = new JSONObject();
                            JSONObject content = message.getJSONObjectAttribute("content");
                            int userId = content.getInt("userId");
                            JSONObject vote = content.getJSONObject("vote");
                            int id = vote.getInt("id");
                            jsonObject.put("type", "vote_notice");
                            jsonObject.put("title", context.getString(R.string.participate_in));
                            jsonObject.put("userId", userId);
                            jsonObject.put("content", context.getString(R.string.vote));
                            jsonObject.put("voteId", id);
                            EMMessage msg = EMMessage.createTxtSendMessage("[系统消息]", message.getTo());
                            msg.setChatType(message.getChatType());
                            msg.setMsgTime(message.getMsgTime());
                            msg.setStatus(EMMessage.Status.SUCCESS);
                            msg.setAttribute(EaseConstant.EXT_EXTMSG, jsonObject);
                            EMClient.getInstance().chatManager().saveMessage(msg);
                            MPEventBus.getDefault().post(new EventEMMessageReceived(message));
                        } catch (JSONException | HyphenateException e) {
                            e.printStackTrace();
                        }
                    } else if(TextUtils.equals(action, "groupVoteEndByCreator") || TextUtils.equals(action, "groupVoteExpireEnd") || TextUtils.equals(action, "groupVoteDeleteByCreator")){
                        // 投票结束 & 投票被删除
                        try {
                            JSONObject content = message.getJSONObjectAttribute("content");
                            int id = content.optInt("id");
                            if(TextUtils.equals(action, "groupVoteEndByCreator") || TextUtils.equals(action, "groupVoteExpireEnd")){
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("type", "vote_notice");
                                jsonObject.put("title", context.getString(R.string.vote_closed));
                                jsonObject.put("userId", 0);
                                jsonObject.put("content", context.getString(R.string.view_result));
                                jsonObject.put("voteId", id);
                                EMMessage msg = EMMessage.createTxtSendMessage("[系统消息]", message.getTo());
                                msg.setChatType(message.getChatType());
                                msg.setMsgTime(message.getMsgTime());
                                msg.setStatus(EMMessage.Status.SUCCESS);
                                msg.setAttribute(EaseConstant.EXT_EXTMSG, jsonObject);
                                EMClient.getInstance().chatManager().saveMessage(msg);
                            }

                            AppHelper.getInstance().getModel().getMsgIdWithVoteId(String.valueOf(id), new EMValueCallBack<List<String>>(){

                                @Override
                                public void onSuccess(List<String> strings) {
                                    for(String msgId : strings){
                                        EMMessage msg = EMClient.getInstance().chatManager().getMessage(msgId);
                                        try {
                                            JSONObject ext = msg.getJSONObjectAttribute(EaseConstant.MSG_EXT_VOTE);
                                            VoteMsgEntity entity = new Gson().fromJson(ext.toString(), VoteMsgEntity.class);
                                            entity.setStatus(content.optInt("voteStatus"));
                                            msg.setAttribute(EaseConstant.MSG_EXT_VOTE, new Gson().toJson(entity));
                                            EMClient.getInstance().chatManager().updateMessage(msg);
                                            MPEventBus.getDefault().post(new EventEMMessageReceived(message));
                                        } catch (HyphenateException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onError(int i, String s) {

                                }
                            });
                        }catch (JSONException | HyphenateException e) {

                        }
                    } else if (TextUtils.equals(action, "new_todoList") || TextUtils.equals(action, "deal_todoList") || TextUtils.equals(action, "remove_todoList")){
                        MPEventBus.getDefault().post(new EventDealLater(0));
                    }
                }
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {
                for (EMMessage msg : messages) {
                    if (msg.getChatType() == EMMessage.ChatType.GroupChat && EaseAtMessageHelper.get().isAtMeMsg(msg)) {
                        EaseAtMessageHelper.get().removeAtMeGroup(msg.getTo());
                    }
                    EMMessage msgNotification;
                    String nickName = null;
                    if (msg.direct() == EMMessage.Direct.RECEIVE) {
                        msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                        EaseUser userInfo = EaseUserUtils.getUserInfo(msg.getFrom());
                        nickName = userInfo == null ? msg.getFrom() : userInfo.getNickname();
                    } else {
                        msgNotification = EMMessage.createSendMessage(EMMessage.Type.TXT);
                        nickName = "您";
                    }

                    EMTextMessageBody txtBody = new EMTextMessageBody(String.format(context.getString(R.string.msg_recall_by_user), nickName));
                    msgNotification.addBody(txtBody);
                    msgNotification.setFrom(msg.getFrom());
                    msgNotification.setTo(msg.getTo());
                    msgNotification.setUnread(false);
                    msgNotification.setMsgTime(msg.getMsgTime());
                    msgNotification.setLocalTime(msg.getMsgTime());
                    msgNotification.setChatType(msg.getChatType());
                    msgNotification.setAttribute(Constant.MESSAGE_TYPE_RECALL, true);
                    msgNotification.setStatus(EMMessage.Status.SUCCESS);
                    EMClient.getInstance().chatManager().saveMessage(msgNotification);

                    // 撤回的消息是正在引用的消息，清空草稿保存的引用消息id
                    DraftEntity draftEntity = null;
                    if(msg.getChatType() == EMMessage.ChatType.Chat){
                        draftEntity = DraftManager.getInstance().getDraftEntity(msg.getFrom());
                    } else {
                        draftEntity = DraftManager.getInstance().getDraftEntity(msg.getTo());
                    }

                    if(draftEntity != null){
                        String msgId = DraftManager.getInstance().getReferenceMsgId(draftEntity);
                        EMMessage message = EMClient.getInstance().chatManager().getMessage(msgId);
                        if(message == null) {
                            String extra = draftEntity.getExtra();
                            try {
                                JSONObject jsonObject = new JSONObject(extra);
                                jsonObject.put(EaseConstant.DRAFT_EXT_REFERENCE_MSG_ID, "");
                                draftEntity.setExtra(jsonObject.toString());
                                DraftManager.getInstance().saveDraft(draftEntity);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            handler.post( () ->  MyToast.showToast("引用内容已撤回"));
                        }
                    }
                    recallReferenceContent(msg.getMsgId());
                }
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                MPLog.d(TAG, "onMessageChanged, msgid:" + (message == null ? "" : message.getMsgId()) + ", change:" + change);
            }

        };

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }


    /**
     * get instance of EaseNotifier
     *
     * @return
     */
    public EaseNotifier getNotifier() {
        return easeUI.getNotifier();
    }

    public AppModel getModel() {
        return InternalAppHelper.getInstance().getModel();
    }

    /**
     * set current username
     *
     * @param username
     */
    public void setCurrentUserName(String username) {
        this.username = username;
        getModel().setCurrentUserName(username);
    }

    /**
     * get current user's id
     */
    public String getCurrentUserName() {
        if (username == null) {
            username = getModel().getCurrentUserName();
        }
        return username;
    }


    /**
     * if ever logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }


    public void logout(boolean unBind) {
        InternalAppHelper.getInstance().logout(unBind);
    }

    public void forceLogout(boolean unBind, final EMCallBack callBack) {
        InternalAppHelper.getInstance().forceLogout(unBind, callBack);
    }

    /**
     * logout
     *
     * @param unbindDeviceToken whether you need unbind your device token
     * @param callback          callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        InternalAppHelper.getInstance().logout(unbindDeviceToken, callback);
    }

    /**
     * 登录方法
     *
     * @param username
     * @param password
     * @param callBack
     */
    public void login(String username, String password, EMCallBack callBack) {
        if (EMClient.getInstance().isLoggedInBefore()) {
            fetchServiceConfig();
            getGroupsDisturb();
            onMainActivityCreate();
            if (callBack != null) {
                callBack.onSuccess();
            }
        } else {
            InternalAppHelper.getInstance().appLogin(username, password, new EMCallBack() {
                @Override
                public void onSuccess() {
                    fetchServiceConfig();
                    getGroupsDisturb();
                    onMainActivityCreate();
                    handler.post(() -> {
                        PreferenceUtils.getInstance().setCryptedPwd(CommonUtils.getMd5Hash(password));
                        AppHelper.getInstance().setCurrentUserName(username);
                    });
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
                    if (callBack != null) {
                        callBack.onProgress(i, s);
                    }
                }
            });
        }
    }

    private void getGroupsDisturb() {
        EMAPIManager.getInstance().getGroupsDisturb(0, 100, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject result = new JSONObject(value);
                    JSONArray entities = result.getJSONArray("entities");
                    for (int i = 0; i < entities.length(); i++) {
                        JSONObject entity = entities.getJSONObject(i);
                        int chatGroupId = entity.optInt("chatGroupId");
                        boolean disturb = entity.optBoolean("disturb");
                        GroupBean groupBean = AppHelper.getInstance().getModel().getGroupInfoById(chatGroupId);
                        if (groupBean == null || !disturb) {
                            continue;
                        }
                        NoDisturbEntity disturbEntity = new NoDisturbEntity();
                        disturbEntity.setId(groupBean.getImGroupId());
                        disturbEntity.setLastUpdateTime(System.currentTimeMillis());
                        disturbEntity.setGroup(true);
                        disturbEntity.setName(groupBean.getNick());
                        NoDisturbManager.getInstance().saveNoDisturb(disturbEntity);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                Log.i("info", "error:" + errorMsg);
            }
        });
    }

    private void fetchServiceConfig(){
        EMAPIManager.getInstance().getServiceConfig(new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try{
                    JSONObject jsonObject = new JSONObject(value);
                    String status = jsonObject.optString("status");
                    if("OK".equalsIgnoreCase(status)){
                        JSONArray entities = jsonObject.getJSONArray("entities");
                        for(int i = 0; i < entities.length(); i ++){
                            JSONObject data = entities.getJSONObject(i);
                            if (data != null){
                                String code = data.optString("code");
                                String configValue = data.optString("configValue");
                                if(TextUtils.equals(code, "msg_recall")){
                                    PreferenceUtils.getInstance().setRecallDuration(Long.parseLong(configValue));
                                } else if (TextUtils.equals(code, "read_ack")){
                                    PreferenceUtils.getInstance().setShowRead(Boolean.parseBoolean(configValue));
                                }  else if (TextUtils.equals(code, "voice_duration")){
                                    PreferenceUtils.getInstance().setVoiceDuration(Long.parseLong(configValue));
                                }
                            }
                        }
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    /**
     * 登录方法
     *
     * @param token
     * @param callBack
     */
    public void ssoLogin(String token, EMCallBack callBack) {
        InternalAppHelper.getInstance().ssoLogin(token, callBack);
    }

    /**
     * 会话是否置顶
     */
    public String getStickyTime(String conversationId, EMConversation.EMConversationType type) {
        return InternalAppHelper.getInstance().getStickyTime(conversationId, type);
    }

    /**
     * 设置会话置顶
     */
    public void saveStickyTime(String conversationId, String stickyTime, EMConversation.EMConversationType type) {
        InternalAppHelper.getInstance().saveStickyTime(conversationId, stickyTime, type);
    }

    public void onMainActivityCreate() {
        InternalAppHelper.getInstance().onCreateMain();
    }

    public void addConnectionListener(ConnectionListener connectionListener) {
        InternalAppHelper.getInstance().addConnectionListener(connectionListener);
    }

    public void removeConnectionListener(ConnectionListener connectionListener) {
        InternalAppHelper.getInstance().removeConnectionListener(connectionListener);
    }

    public MPOrgEntity getCompanyOrg() {
        return InternalAppHelper.getInstance().getCompanyOrg();
    }


    public void asyncFetchGroupsFromServer(com.hyphenate.EMCallBack callback) {
        InternalAppHelper.getInstance().asyncFetchGroupsFromServer(callback);
    }

    public void recallReferenceContent(String msgId){
        AppHelper.getInstance().getModel().getReferenceDataWithMsgId(msgId, new EMValueCallBack<List<ReferenceMsgEntity>>() {
            @Override
            public void onSuccess(List<ReferenceMsgEntity> entities) {
                for(ReferenceMsgEntity entity : entities){
                    String msgId = entity.getRealMsgId();
                    EMMessage message = EMClient.getInstance().chatManager().getMessage(msgId);
                    if (message != null){
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put(EaseConstant.MSG_EXT_REFERENCE_MSG_ID, "");
                            jsonObject.put(EaseConstant.MSG_EXT_REFERENCE_MSG_NICK, "");
                            jsonObject.put(EaseConstant.MSG_EXT_REFERENCE_MSG_TYPE, EaseConstant.REFERENCE_MSG_TYPE_RECALL);
                            jsonObject.put(EaseConstant.MSG_EXT_REFERENCE_MSG_CONTENT, "引用内容已撤回");
                            message.setAttribute(EaseConstant.MSG_EXT_REFERENCE_MSG, jsonObject);
                            EMClient.getInstance().chatManager().updateMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    public void saveReferenceMsg(EMMessage message){
        // 保存引用消息Id和对应的消息id
        if(EaseMessageUtils.isReferenceMsg(message)){
            try {
                JSONObject json = message.getJSONObjectAttribute(EaseConstant.MSG_EXT_REFERENCE_MSG);
                ReferenceMsgEntity entity = null;
                if(json != null) {
                    String msgId = json.optString(EaseConstant.MSG_EXT_REFERENCE_MSG_ID);
                    if(!TextUtils.isEmpty(msgId)){
                        entity = new ReferenceMsgEntity();
                        entity.setReferenceMsgId(msgId);
                        entity.setRealMsgId(message.getMsgId());
                        getModel().saveReferenceMsg(entity);
                    }
                }
            } catch (HyphenateException e) {

            }
        }
    }

    public void saveVoteData(EMMessage message){
        if(EaseMessageUtils.isVoteMessage(message)){
            try {
                JSONObject json = message.getJSONObjectAttribute(EaseConstant.MSG_EXT_VOTE);
                VoteMsgEntity entity = new Gson().fromJson(json.toString(), VoteMsgEntity.class);
                getModel().saveVoteAndMsgId(entity.getId(), message.getMsgId());
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }
}
