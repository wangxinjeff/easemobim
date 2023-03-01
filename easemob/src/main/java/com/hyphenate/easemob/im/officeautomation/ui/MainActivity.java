package com.hyphenate.easemob.im.officeautomation.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMMultiDeviceListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.EaseMessageUtils;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.im.mp.manager.NoDisturbManager;
import com.hyphenate.easemob.im.officeautomation.db.InviteMessageDao;
import com.hyphenate.easemob.im.officeautomation.fragment.ContactListFragment;
import com.hyphenate.easemob.im.officeautomation.fragment.ConversationListFragment;
import com.hyphenate.easemob.im.officeautomation.http.BaseRequest;
import com.hyphenate.easemob.im.officeautomation.runtimepermissions.PermissionsManager;
import com.hyphenate.easemob.im.officeautomation.runtimepermissions.PermissionsResultAction;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.imlibs.interfaces.MultiClickListener;
import com.hyphenate.easemob.imlibs.message.MessageUtils;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupsChanged;
import com.hyphenate.easemob.imlibs.mp.events.EventTabReceived;
import com.hyphenate.easemob.imlibs.mp.events.EventUsersRefresh;
import com.hyphenate.easemob.imlibs.mp.prefs.PrefsUtil;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.eventbus.Subscribe;
import com.hyphenate.eventbus.ThreadMode;
import com.hyphenate.util.EMLog;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by qby on 2018/5/31 0020.
 * 主页面
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_CALL = 300;
    public static MainActivity instance = null;

    private JPTabBar mJPTabBar;
    private ConversationListFragment conversationListFragment;
    private ContactListFragment contactListFragment;
    private InviteMessageDao inviteMessageDao;
    private int currentTabIndex;

    private PowerManager mPowerManager;
    private MyMultiDeviceListener mMyMultiDeviceListener;
    private boolean moreEnable = true;
    private boolean selectEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(getLayout());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode()) {//禁止分屏
            Context ctx = null;
            try {
                ctx = this.createPackageContext("com.android.systemui",
                        Context.CONTEXT_INCLUDE_CODE
                                | Context.CONTEXT_IGNORE_SECURITY);
                int stringId = ctx.getResources().getIdentifier(
                        "dock_non_resizeble_failed_to_dock_text", "string", ctx.getPackageName());
                String toast = ctx.getResources().getString(stringId);
                Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
            } catch (PackageManager.NameNotFoundException ex) {
                Log.e(TAG, "[onCreate] NameNotFoundException ", ex);
            }
            finish();
        }
        EMClient.getInstance().addConnectionListener(connectionListener);

        setSwipeEnabled(false);

        // runtime permission for android 6.0, just require all permissions here for simple
        requestAllPermissions();

        initView();

        mPowerManager = (PowerManager) this.

                getSystemService(Context.POWER_SERVICE);

        inviteMessageDao = new

                InviteMessageDao();

        initListener();

        initData();
        if (savedInstanceState != null) {
            currentTabIndex = savedInstanceState.getInt("selectedIndex", 0);
            if (conversationListFragment == null) {
                conversationListFragment = (ConversationListFragment) getSupportFragmentManager().findFragmentByTag("conversationListFragment");
//                workFragment = (WorkFragment) getSupportFragmentManager().findFragmentByTag("workFragment");
                contactListFragment = (ContactListFragment) getSupportFragmentManager().findFragmentByTag("contactListFragment");
            }
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (conversationListFragment == null) {
            conversationListFragment = new ConversationListFragment();
            transaction.add(R.id.content_container, conversationListFragment, "conversationListFragment");
        }
        if (contactListFragment == null) {
            contactListFragment = new ContactListFragment();
            transaction.add(R.id.content_container, contactListFragment, "contactListFragment");
        }


        transaction.hide(contactListFragment)
                .show(conversationListFragment)
                .commit();
        //register broadcast receiver to receive the change of group from DemoHelper
        registerBroadcastReceiver();


        if (mMyMultiDeviceListener == null) {
            mMyMultiDeviceListener = new MyMultiDeviceListener();
        }
        EMClient.getInstance().addMultiDeviceListener(mMyMultiDeviceListener);

        AppHelper.getInstance().onMainActivityCreate();
    }
    private int getLayout() {
        return R.layout.activity_main;
    }

    private void initView() {
        mJPTabBar = findViewById(R.id.navigation);
//        mJPTabBar.setTitles(R.string.title_conversation, R.string.title_remind, R.string.title_contact, /*R.string.title_apps,*/R.string.conference, R.string.title_my);
//        mJPTabBar.setTitles(R.string.title_conversation, R.string.title_contact, R.string.title_apps, R.string.title_my);
        mJPTabBar.setTitles(R.string.title_conversation, R.string.title_contact );
//        mJPTabBar.setNormalIcons(R.drawable.mp_nav_conv_normal, R.drawable.mp_nav_remind_normal, R.drawable.mp_nav_contacts_normal, R.drawable.mp_nav_apps_normal, R.drawable.mp_nav_my_normal);
        mJPTabBar.setNormalIcons(R.drawable.mp_nav_conv_normal, R.drawable.mp_nav_contacts_normal);
//        mJPTabBar.setSelectedIcons(R.drawable.mp_nav_conv_selected, R.drawable.mp_nav_remind_selected, R.drawable.mp_nav_contacts_selected, R.drawable.mp_nav_apps_selected, R.drawable.mp_nav_my_selected);
        mJPTabBar.setSelectedIcons(R.drawable.mp_nav_conv_selected, R.drawable.mp_nav_contacts_selected);
//        mJPTabBar.setNormalColor(Color.parseColor("#2F2E41"));
//        mJPTabBar.setSelectedColor(Color.parseColor("#5AAAF9"));
//        mJPTabBar.setBackgroundColor(Color.parseColor("#EDEFF2"));
//        mJPTabBar.setBadgeColor(Color.parseColor("#FF3A36"));
        mJPTabBar.generate();

        mJPTabBar.setSelectTab(0);

    }

    private void initListener() {
//        mJPTabBar.setDismissListener(new BadgeDismissListener() {
//            @Override
//            public void onDismiss(int position) {
//                EMClient.getInstance().chatManager().markAllConversationsAsRead();
//                if (conversationListFragment != null) {
//                    conversationListFragment.refresh();
//                }
//            }
//        });
        mJPTabBar.setTabListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                //开启事务
                transaction
                        .hide(conversationListFragment)
                        .hide(contactListFragment);

                switch (position) {
                    case 0:
                        currentTabIndex = 0;
                        transaction
                                .show(conversationListFragment);
                        break;
                    case 1:
                        currentTabIndex = 1;
                        mJPTabBar.hideBadge(1);
                        transaction
                                .show(contactListFragment);
                        break;
                    default:
                        break;
                }
                // 事务提交
                transaction.commit();
            }

            @Override
            public boolean onInterruptSelect(int index) {
                return false;
            }
        });


    }


    private void initData() {

        mJPTabBar.getTabAtPosition(0).setOnClickListener(new MultiClickListener(){

            @Override
            public void onSingleClick() {
            }
            @Override
            public void onDoubleClick() {
                if (conversationListFragment != null){
                    conversationListFragment.refreshToUnreadConversation();
                }
            }
        });
//        mJPTabBar.setDoubleClickListener(new BottomNavigationBar.DoubleClickListener() {
//            @Override
//            public void onDoubleClick(int position) {
//                if (position == 0){
//                    if (conversationListFragment != null){
//                        conversationListFragment.refreshToUnreadConversation();
//                    }
//                }
//
//            }
//        });

        EMClient.getInstance().chatManager().getAllContactsStatusList(new EMCallBack() {
            @Override
            public void onSuccess() {
                EMLog.e(TAG, "getAllContactsStatusList-onSuccess");
            }

            @Override
            public void onError(int i, String s) {
                EMLog.e(TAG, "getAllContactsStatusList-onError");
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    EMConnectionListener connectionListener = new EMConnectionListener() {
        @Override
        public void onConnected() {

        }

        @Override
        public void onDisconnected(int i) {
            if (i == EMError.USER_REMOVED || i == EMError.USER_LOGIN_ANOTHER_DEVICE || i == EMError.SERVER_SERVICE_RESTRICTED
                    || i == EMError.USER_KICKED_BY_CHANGE_PASSWORD || i == EMError.USER_KICKED_BY_OTHER_DEVICE) {
            }
        }
    };


    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            MPLog.e(TAG, "onMessageReceived-size:" + messages.size());
            // notify new message
            List<EMMessage> cacheMessages = new ArrayList<>();
            for (EMMessage message : messages) {
                if (EaseUI.getInstance().hasForegroundActivies()) {
                    // 判断一下是否是会议邀请
                    if (!EaseMessageUtils.isVideoInviteMessage(message)) {
                        cacheMessages.add(message);
                    }
                }
                if (MPClient.get().isFileHelper(message.getTo())) {
                    message.setTo(MPClient.get().getPcTarget());
                    EMClient.getInstance().chatManager().updateMessage(message);
                }
            }
            refreshUIWithMessage();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            refreshUIWithMessage();

            List<EMMessage> tempMessages = new ArrayList<>(messages);
            for (EMMessage cmdMessage : tempMessages) {
                if (cmdMessage.getType() != EMMessage.Type.CMD) {
                    continue;
                }

                EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) cmdMessage.getBody();
                final String action = cmdMsgBody.action();//获取自定义action
                MPLog.i(TAG, "cmdMessage, action = " + action);
                if (TextUtils.isEmpty(action)) {
                    return;
                }
                if (action.equals(Constant.CMD_ACTION_INVITED_FRIEND) || action.equals("chatgroup_add_members_approving")) {
                    AppHelper.getInstance().getNotifier().onNewMsg(cmdMessage, true);
                    if (mJPTabBar.getSelectPosition() == 2) return;
                    mJPTabBar.showBadge(1, "new");
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
            refreshUIWithMessage();
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
        }

        @Override
        public void onNoticeList(Map<String, String> map) {
            //MPLog.e(TAG, "onNoticeList:" + map.toString());

        }

        @Override
        public void onContactStatusChanged(Map<String, String> map) {
            //MPLog.e(TAG, "onContactStatusChanged:" + map.toString());
        }

        @Override
        public void onQueryUserStatusList(Map<String, String> map) {
            //MPLog.e(TAG, "onQueryUserStatusList:" + map.toString());
        }

        @Override
        public void onAllContactsStatusList(Map<String, String> map) {
            //MPLog.e(TAG, "onAllContactsStatusList:" + map.toString());
        }
    };

    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                // refresh unread count
                updateUnreadLabel();
//                if (currentTabIndex == 0) {
//                    // refresh conversation list
//                    if (conversationListFragment != null) {
//                        conversationListFragment.refresh();
//                    }
//                }
            }
        });
    }

    @Override
    public void back(View view) {
        super.back(view);
    }

    private void registerBroadcastReceiver() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onContactChanged(EventUsersRefresh usersRefresh) {
        refreshUI();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupsChanged(EventGroupsChanged groupsChanged) {
        refreshUI();
    }


    private void refreshUI() {
        updateUnreadLabel();
        updateUnreadAddressLable();
        if (conversationListFragment != null) {
            conversationListFragment.refresh();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public class MyMultiDeviceListener implements EMMultiDeviceListener {

        @Override
        public void onContactEvent(int event, String target, String ext) {

        }

        @Override
        public void onGroupEvent(int event, String target, final List<String> username) {
            switch (event) {

            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
        EMClient.getInstance().removeConnectionListener(connectionListener);
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        if (mMyMultiDeviceListener != null) {
            EMClient.getInstance().removeMultiDeviceListener(mMyMultiDeviceListener);
        }
    }


    /**
     * update unread message count
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
//            mJPTabBar.setBadgeColor(getResources().getColor(R.color.holo_red_light));
            if (count > 99) {
                mJPTabBar.showBadge(0, "99+");
            } else {
                mJPTabBar.showBadge(0, String.valueOf(count));
            }

        } else {
            mJPTabBar.hideBadge(0);
        }
    }

    /**
     * update the total unread count
     */
    public void updateUnreadAddressLable() {


    }

    /**
     * get unread event notification count, including application, accepted, etc
     *
     * @return
     */
    public int getUnreadAddressCountTotal() {
        int unreadAddressCountTotal = 0;
        unreadAddressCountTotal = inviteMessageDao.getUnreadMessagesCount();
        return unreadAddressCountTotal;
    }

    /**
     * get unread message count
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        Map<String, EMConversation> conversationMap = EMClient.getInstance().chatManager().getAllConversations();
        int count = 0;
        for (EMConversation conversation : conversationMap.values()) {
            if (!NoDisturbManager.getInstance().hasNoDisturb(conversation.conversationId())) {
                count += conversation.getUnreadMsgCount();
            }
        }
        return count;
//        return EMClient.getInstance().chatManager().getUnreadMessageCount();
//        return getUnreadMessageCount();
    }

    private void sendTestMessage() {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody("test");
        cmdMessageBody.deliverOnlineOnly(true);
        message.setTo("admin");
        message.addBody(cmdMessageBody);
        MessageUtils.sendMessage(message);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                PrefsUtil.getInstance().setTimeStamp(message.getMsgTime());
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mPowerManager.isScreenOn()) {
            if (!EMClient.getInstance().isConnected()) {
                sendTestMessage();
            }
        }

        if (Math.abs(PrefsUtil.getInstance().getTimeStamp() - System.currentTimeMillis()) >= 24 * 60 * 60 * 1000) {
            sendTestMessage();
        }

        boolean isCurrentAccountRemoved = false;
        updateUnreadLabel();
        updateUnreadAddressLable();

        // unregister this event listener when this activity enters the
        // background
        EaseUI.getInstance().pushActivity(this);

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    @Override
    protected void onStop() {
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        EaseUI.getInstance().popActivity(this);

        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("selectedIndex", currentTabIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentTabIndex = savedInstanceState.getInt("selectedIndex", 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(false);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean isChanged = intent.getBooleanExtra(Constant.SETTINGS_CHANGE_LANGUAGE, false);
            if (isChanged) {
                finish();
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);// overridePendingTransition(0, 0);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventTabEventReceived(EventTabReceived tabReceived) {

    }


    private void requestAllPermissions() {
//        checkPermission();

        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
                MPLog.d(TAG, "All permissions have been granted");
            }

            @Override
            public void onDenied(String permission) {
                MPLog.e(TAG, "Permission:" + permission + "  has been denied");
//                Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    String[] mPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.BODY_SENSORS};

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, mPermissions[0]) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, mPermissions[1]) != PackageManager.PERMISSION_GRANTED) {

//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, mPermissions[0]) ||
//                    ActivityCompat.shouldShowRequestPermissionRationale(this, mPermissions[1])) {
            ActivityCompat.requestPermissions(this, mPermissions, REQUEST_CODE_PERMISSIONS);
//            } else {
//                //用户点击了拒绝并勾选不在询问则提示用户
//                Toast.makeText(this, "需要在设置中开启存储或传感器权限", Toast.LENGTH_SHORT).show();
//            }
        }
    }

    private static final int REQUEST_CODE_PERMISSIONS = 300;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
//        switch(requestCode) {
//            case REQUEST_CODE_PERMISSIONS:
//                int permission = 0;
//                for (int i = 0; i < permissions.length; i++) {
//                    for (int j = 0; j < mPermissions.length; j++) {
//                        if (mPermissions[j].equals(permissions[i])) {
//                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                                permission++;
//                            }
//                        }
//                    }
//                }
//                if (permission == permissions.length) {
//
//                } else {
//
//                }
//
//
//                break;
//        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (currentTabIndex == 0) {
            conversationListFragment.onActivityResult(requestCode, resultCode, data);
        } else if (currentTabIndex == 1) {
            contactListFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}
