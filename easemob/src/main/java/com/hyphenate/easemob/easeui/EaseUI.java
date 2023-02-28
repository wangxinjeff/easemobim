package com.hyphenate.easemob.easeui;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupReadAck;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easemob.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easemob.easeui.domain.EaseEmojicon;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easemob.easeui.model.EaseDingMessageHelperV2;
import com.hyphenate.easemob.easeui.model.EaseNotifier;
import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.imlibs.mp.events.EventOnLineOffLineNotify;
import com.hyphenate.easemob.imlibs.mp.events.EventOnLineOffLineQuery;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.eventbus.MPEventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class EaseUI {
    private static final String TAG = EaseUI.class.getSimpleName();

    /**
     * the global EaseUI instance
     */
    private static EaseUI instance = null;

    /**
     * user profile provider
     */
    private EaseUserProfileProvider userProvider;

    private EaseSettingsProvider settingsProvider;

    private EaseAvatarOptions avatarOptions;

    /**
     * application context
     */
    private Context appContext = null;

    /**
     * init flag: test if the sdk has been inited before, we don't need to init again
     */
    private boolean sdkInited = false;

    /**
     * the notifier
     */
    private EaseNotifier notifier = null;

    private ExecutorService executor;

    /**
     * save foreground Activity which registered eventlistener
     */
    private List<Activity> activityList = new ArrayList<Activity>();

//    private EaseUIDataBase easeUIDataBase;

    public void pushActivity(Activity activity) {
        if (!activityList.contains(activity)) {
            activityList.add(0, activity);
        }
    }

    public void popActivity(Activity activity) {
        activityList.remove(activity);
    }

    public Activity getTopActivity() {
        return activityList.get(0);
    }


    public String getAppServer() {
        return MPClient.get().getAppServer();
    }

    public void setAppServer(String appServer) {
        MPClient.get().setAppServer(appServer);
    }

    private EaseUI() {
        executor = Executors.newCachedThreadPool();
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    /**
     * get instance of EaseUI
     *
     * @return
     */
    public synchronized static EaseUI getInstance() {
        if (instance == null) {
            instance = new EaseUI();
        }
        return instance;
    }

    /**
     * this function will initialize the SDK and easeUI kit
     *
     * @param context
     * @param options use default if options is null
     * @return
     */
    public synchronized boolean init(Context context, EMOptions options) {
        if (sdkInited) {
            return true;
        }
        appContext = context.getApplicationContext();

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);

        Log.d(TAG, "process app name : " + processAppName);

        // if there is application has remote service, application:onCreate() maybe called twice
        // this check is to make sure SDK will initialized only once
        // return if process name is not application's name since the package name is the default process name
        if (processAppName == null || !processAppName.equalsIgnoreCase(appContext.getPackageName())) {
            Log.e(TAG, "enter the service process!");
            return false;
        }
        initNotifier();
        registerMessageListener();

        if (settingsProvider == null) {
            settingsProvider = new DefaultSettingsProvider();
        }

//        easeUIDataBase = Room.databaseBuilder(context, EaseUIDataBase.class, "group_info_room.db")
//                .allowMainThreadQueries()
//                .build();

        sdkInited = true;
        return true;
    }

//    public void insertGroup(GroupBean groupBean) {
//        easeUIDataBase.groupInfoDao().insert(groupBean);
//    }

//    public List<String> loadGroupIdList() {
//        return easeUIDataBase.groupInfoDao().loadGroupList();
//    }

    protected EMOptions initChatOptions() {
        Log.d(TAG, "init HuanXin Options");

        EMOptions options = new EMOptions();
        // change to need confirm contact invitation
        options.setAcceptInvitationAlways(false);
        // set if need read ack
        options.setRequireAck(true);
        // set if need delivery ack
        options.setRequireDeliveryAck(false);

        options.setAutoLogin(true);

        return options;
    }

    void initNotifier() {
        notifier = createNotifier();
        notifier.init(appContext);
    }

    private void registerMessageListener() {
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                MPLog.e(TAG, "onMessageReceived-size:" + messages.size());
                EaseAtMessageHelper.get().parseMessages(messages);
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> messages) {
            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {

            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
//                for (EMMessage message : messages) {
//                    // To handle group-ack msg.
//                    EaseDingMessageHelperV2.get().handleGroupReadAck(message);
//                }
            }

            @Override
            public void onGroupMessageRead(List<EMGroupReadAck> list) {
                for (EMGroupReadAck ack : list) {
                    EaseDingMessageHelperV2.get().handleGroupReadAck(ack);
                }
            }

            @Override
            public void onReadAckForGroupMessageUpdated() {

            }

            @Override
            public void onNoticeList(Map<String, String> map) {
                MPLog.i(TAG, "onNoticeList:" + map.toString());
                MPEventBus.getDefault().post(new EventOnLineOffLineNotify(map));
            }

            @Override
            public void onContactStatusChanged(Map<String, String> map) {
                MPLog.i(TAG, "onContactStatusChanged:" + map.toString());
                MPEventBus.getDefault().post(new EventOnLineOffLineNotify(map));
            }

            @Override
            public void onQueryUserStatusList(Map<String, String> map) {
                MPLog.i(TAG, "onQueryUserStatusList:" + map.toString());
                MPEventBus.getDefault().post(new EventOnLineOffLineQuery(map));
            }

            @Override
            public void onAllContactsStatusList(Map<String, String> map) {
                MPLog.i(TAG, "onAllContactsStatusList:" + map.toString());
                MPEventBus.getDefault().post(new EventOnLineOffLineNotify(map));
            }


        });
    }

    protected EaseNotifier createNotifier() {
        return new EaseNotifier();
    }

    public EaseNotifier getNotifier() {
        return notifier;
    }

    public boolean hasForegroundActivies() {
        return activityList.size() != 0;
    }


    public void setAvatarOptions(EaseAvatarOptions avatarOptions) {
        this.avatarOptions = avatarOptions;
    }

    public EaseAvatarOptions getAvatarOptions() {
        return avatarOptions;
    }


    /**
     * set user profile provider
     *
     * @param userProvider
     */
    public void setUserProfileProvider(EaseUserProfileProvider userProvider) {
        this.userProvider = userProvider;
    }

    /**
     * get user profile provider
     *
     * @return
     */
    public EaseUserProfileProvider getUserProfileProvider() {
        return userProvider;
    }

    public void setSettingsProvider(EaseSettingsProvider settingsProvider) {
        this.settingsProvider = settingsProvider;
    }

    public EaseSettingsProvider getSettingsProvider() {
        return settingsProvider;
    }


    /**
     * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
     *
     * @param pID
     * @return
     */
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    /**
     * User profile provider
     *
     * @author wei
     */
    public interface EaseUserProfileProvider {
        /**
         * return EaseUser for input username
         *
         * @param userId
         * @return
         */
        EaseUser getUser(int userId);

        EaseUser getUser(String imUserId);

        GroupBean getGroupBean(String imGroupId);

        GroupBean getGroupBeanById(int groupId);

        boolean isNoDisturb(String noDisturbId);
    }

    /**
     * Emojicon provider
     */
    public interface EaseEmojiconInfoProvider {
        /**
         * return EaseEmojicon for input emojiconIdentityCode
         *
         * @param emojiconIdentityCode
         * @return
         */
        EaseEmojicon getEmojiconInfo(String emojiconIdentityCode);

        /**
         * get Emojicon map, key is the text of emoji, value is the resource id or local path of emoji icon(can't be URL on internet)
         *
         * @return
         */
        Map<String, Object> getTextEmojiconMapping();
    }

    private EaseEmojiconInfoProvider emojiconInfoProvider;

    /**
     * Emojicon provider
     *
     * @return
     */
    public EaseEmojiconInfoProvider getEmojiconInfoProvider() {
        return emojiconInfoProvider;
    }

    /**
     * set Emojicon provider
     *
     * @param emojiconInfoProvider
     */
    public void setEmojiconInfoProvider(EaseEmojiconInfoProvider emojiconInfoProvider) {
        this.emojiconInfoProvider = emojiconInfoProvider;
    }

    /**
     * new message options provider
     */
    public interface EaseSettingsProvider {
        boolean isMsgNotifyAllowed(EMMessage message);

        boolean isMsgSoundAllowed(EMMessage message);

        boolean isMsgVibrateAllowed(EMMessage message);

        boolean isSpeakerOpened();
    }

    /**
     * default settings provider
     */
    protected class DefaultSettingsProvider implements EaseSettingsProvider {

        @Override
        public boolean isMsgNotifyAllowed(EMMessage message) {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public boolean isMsgSoundAllowed(EMMessage message) {
            return true;
        }

        @Override
        public boolean isMsgVibrateAllowed(EMMessage message) {
            return true;
        }

        @Override
        public boolean isSpeakerOpened() {
            return !PreferenceUtils.getInstance().isEarpieceOn();
        }
    }

    public Context getContext() {
        return appContext;
    }
}
