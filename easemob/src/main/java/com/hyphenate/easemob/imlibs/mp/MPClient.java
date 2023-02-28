package com.hyphenate.easemob.imlibs.mp;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.Utils;
import com.easemob.emssl.utils.EMMisc;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easemob.imlibs.mp.dao.AuthListener;
import com.hyphenate.easemob.imlibs.mp.events.EventReLoginStart;
import com.hyphenate.easemob.imlibs.mp.events.EventReloginSuccess;
import com.hyphenate.easemob.imlibs.mp.prefs.PrefsUtil;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.imlibs.mp.utils.MPPathUtil;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.exceptions.HyphenateException;
import com.tencent.smtt.sdk.QbSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 11/12/2018
 */
public class MPClient {
    private static String TAG = "MPClient";
    private Context appContext;
    final private List<ConnectionListener> connectionListeners = Collections.synchronizedList(new ArrayList<ConnectionListener>());
    private static MPClient sClient = new MPClient();
    private ExecutorService executor;
    protected Handler handler = new Handler();
    private AuthListener mAuthListener;
    private String currentUserName;
    private MPUserEntity mCurrentUser;
    /**
     * init flag: test if the sdk has been inited before, we don't need to init again
     */
    private boolean sdkInited = false;

    public static MPClient get() {
        return sClient;
    }

    private MPClient() {
        executor = Executors.newCachedThreadPool();
    }

    private static final String DEFAULT_APP_HOST = "http://sandbox.mp.easemob.com";
    private String appHost;
    private boolean tbsInited;
    private String pcResources = "windows";

    public String getAppServer() {
        if (TextUtils.isEmpty(appHost)) {
            appHost = DEFAULT_APP_HOST;
        }
        return appHost;
    }

    public void setAppServer(String appServer) {
        if (appServer != null) {
            appHost = appServer;
        }
    }

    public String getPCResource() {
        return this.pcResources;
    }

    public String getPcTarget() {
        return mCurrentUser.getImUserId() + "/" + this.pcResources;
    }

    public boolean isFileHelper(String fromUserId) {
        if(TextUtils.isEmpty(fromUserId)) {
            return false;
        }
        return fromUserId.contains("/mobile") || fromUserId.contains("webim") || fromUserId.contains(pcResources);
    }

    public MPUserEntity getCurrentUser() {
        return mCurrentUser;
    }

    public boolean init(Context context, EMOptions emOptions) {
        if (sdkInited) {
            return true;
        }

        if (context != null) {
            appContext = context.getApplicationContext();
        }

        // init tools class
        if(context instanceof Application) {
            Utils.init((Application) context);
        } else {
            MPLog.e(TAG, "context is not application init Utils failed.");
        }

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

        PrefsUtil.getInstance().init(context);
        if (emOptions == null) {
            emOptions = initChatOptions();
        }
        emOptions.setAppKey(getAppkey());
        EMClient.getInstance().init(context, emOptions);
        if (EMClient.getInstance().isLoggedInBefore()) {
            MPPathUtil.getInstance().initDirs(getAppkey(), EMClient.getInstance().getCurrentUser(), appContext);
        }

        String jsonAll = PrefsUtil.getInstance().getLoginAllEntity();
        parseJSONToUser(jsonAll);


        sdkInited = true;
        asyncInitTbs(context);
        EMMisc.copyCertFile(context);
        return true;
    }


    private void parseJSONToUser(String jsonAll) {
        if (TextUtils.isEmpty(jsonAll)) {
            return;
        }

        try {
            JSONObject jsonEntity = new JSONObject(jsonAll);
            JSONObject jsonUserObj = jsonEntity.optJSONObject("user");
            JSONArray jsonArrCompay = jsonEntity.optJSONArray("companyList");
            JSONArray jsonArrOrg = jsonEntity.optJSONArray("organizationList");
            JSONArray jsonArrUserCompany = jsonEntity.optJSONArray("userCompanyRelationshipList");
            List<MPOrgEntity> orgEntities = MPOrgEntity.create(jsonArrOrg, jsonArrCompay, jsonArrUserCompany);
            MPUserEntity userEntity = MPUserEntity.create(jsonUserObj);
            userEntity.setOrgEntities(orgEntities);
            mCurrentUser = userEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void asyncInitTbs(Context ctx) {
        QbSdk.initX5Environment(ctx, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(boolean b) {
                tbsInited = b;
            }
        });
    }

    public boolean isTbsInited() {
        return tbsInited;
    }

    public String getAppkey() {
        String strAppkey = PrefsUtil.getInstance().getAppkey();
        if (TextUtils.isEmpty(strAppkey)) {
            return "1141180531146130#easemoboa";
        }
        return strAppkey;
    }

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

    public void setAuthListener(AuthListener authListener) {
        this.mAuthListener = authListener;
    }

    public Context getAppContext() {
        return appContext;
    }


    public void addConnectionListener(ConnectionListener connectionListener) {
        if (connectionListener == null) return;
        synchronized (connectionListeners) {
            if (!connectionListeners.contains(connectionListener)) {
                connectionListeners.add(connectionListener);
            }
        }

    }

    public void removeConnectionListener(ConnectionListener connectionListener) {
        if (connectionListener == null) return;
        synchronized (connectionListeners) {
            if (connectionListeners.contains(connectionListener)) {
                connectionListeners.remove(connectionListener);
            }
        }
    }

    private volatile boolean isExit;

    public synchronized void clearCurrentUserInfo() {
        if (isExit) {
            return;
        }
        isExit = true;
        if (mAuthListener != null) {
            mAuthListener.onAuthFailed();
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (connectionListeners) {
                    for (ConnectionListener connectionListener : connectionListeners) {
                        if (connectionListener != null) {
                            connectionListener.onAuthenticationFailed(EMError.USER_LOGIN_ANOTHER_DEVICE);
                        }
                    }
                }
            }
        });
        delayResetIsExit();
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    private void delayResetIsExit() {
        execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isExit = false;
            }
        });
    }


    /**
     * sso授权登录接口，如果登录成功,根据返回的IM信息，执行IM登录操作
     */
    public void ssoLogin(final String token, final EMCallBack callBack) {
        isExit = false;
        EMAPIManager.getInstance().ssoLogin(token, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                Log.i("info", "sso login:" + value);
                try {
                    JSONObject jsonRes = new JSONObject(value);
                    String status = jsonRes.optString("status");
                    JSONObject jsonEntity = jsonRes.optJSONObject("entity");
                    if ("OK".equalsIgnoreCase(status)) {
                        if (jsonEntity != null) {
                            JSONObject jsonUser = jsonEntity.optJSONObject("user");
                            JSONObject jsonImUser = jsonUser.optJSONObject("imUser");
                            if (jsonImUser != null) {
                                String appkey = jsonImUser.optString("appkey");
                                if (EMClient.getInstance().isLoggedInBefore()) {
                                    EMClient.getInstance().logout(false);
                                }
                                if (!TextUtils.isEmpty(appkey)) {
                                    if (appkey.contains("/")) {
                                        appkey = appkey.replace("/", "#");
                                    }
                                }
                                currentUserName = token;
                                PrefsUtil.getInstance().setCurrentUserName(currentUserName);
                                mCurrentUser = MPUserEntity.create(jsonUser);
                                parseJSONToUser(jsonEntity.toString());
                                loginImServer(jsonEntity, jsonUser, appkey, callBack);
                            } else {
                                if (callBack != null) {
                                    callBack.onError(EMError.GENERAL_ERROR, "userBean = null or token = null");
                                }
                            }
                        } else {
                            if (callBack != null) {
                                callBack.onError(EMError.GENERAL_ERROR, "entityBean = null");
                            }
                        }
                    } else {
                        if (callBack != null) {
                            callBack.onError(EMError.GENERAL_ERROR, "status = " + status);
                        }
                    }


                } catch (Exception e) {
                    if (callBack != null) {
                        callBack.onError(EMError.GENERAL_ERROR, e.getMessage() + "");
                    }
                }

            }

            @Override
            public void onError(int error, String errorMsg) {
                if (callBack != null) {
                    callBack.onError(error, errorMsg);
                }
            }
        });
    }

    /**
     * 调用登录接口，如果登录成功,根据返回的IM信息，执行IM登录操作
     *
     * @param username 手机号
     * @param password 密码
     */
    public void appLogin(final String username, final String password, final EMCallBack callBack) {
        isExit = false;
        EMAPIManager.getInstance().login(username, password, new EMDataCallBack<String>() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(String value) {
                try {
                    MPLog.d(TAG, "login res:" + value);
                    JSONObject jsonRes = new JSONObject(value);
                    String status = jsonRes.optString("status");
                    JSONObject jsonEntity = jsonRes.optJSONObject("entity");
                    if ("OK".equalsIgnoreCase(status)) {
                        if (jsonEntity != null) {
                            JSONObject jsonUser = jsonEntity.optJSONObject("user");
                            JSONObject jsonImUser = jsonUser.optJSONObject("imUser");
//                            JSONObject jsonToken = jsonEntity.optJSONObject("token");
                            if (jsonImUser != null) {
                                String appkey = jsonImUser.optString("appkey");
                                if (EMClient.getInstance().isLoggedInBefore()) {
                                    EMClient.getInstance().logout(false);
                                }
                                if (!TextUtils.isEmpty(appkey)) {
                                    if (appkey.contains("/")) {
                                        appkey = appkey.replace("/", "#");
                                    }
                                }
//                                TokenManager.Token session = new TokenManager.Token();
//                                session.name = jsonToken.optString("name");
//                                session.value = jsonToken.optString("value");
//                                session.expires = jsonToken.optInt("expires");
//                                TokenManager.getInstance().setToken(session);
                                currentUserName = username;
                                PrefsUtil.getInstance().setCurrentUserName(currentUserName);
                                mCurrentUser = MPUserEntity.create(jsonUser);
                                parseJSONToUser(jsonEntity.toString());
                                loginImServer(jsonEntity, jsonUser, appkey, callBack);
                            } else {
                                if (callBack != null) {
                                    callBack.onError(EMError.GENERAL_ERROR, "userBean = null or token = null");
                                }
                            }
                        } else {
                            if (callBack != null) {
                                callBack.onError(EMError.GENERAL_ERROR, "entityBean = null");
                            }
                        }
                    } else {
                        if (callBack != null) {
                            callBack.onError(EMError.GENERAL_ERROR, value);
                        }
                    }
                } catch (Exception e) {
                    if (callBack != null) {
                        callBack.onError(EMError.GENERAL_ERROR, e.getMessage());
                    }
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                MPLog.e(TAG, "appLogin username:" + username + ", errorMsg:" + errorMsg);
                if (callBack != null) {
                    callBack.onError(error, errorMsg);
                }
            }
        });

    }

    //登录IM服务器
    public void loginImServer(JSONObject jsonEntity, JSONObject jsonUser, String appkey, final EMCallBack callBack) throws JSONException {
        if (!TextUtils.isEmpty(appkey)) {
            try {
                EMClient.getInstance().changeAppkey(appkey);
                PrefsUtil.getInstance().setAppkey(appkey);
            } catch (HyphenateException e) {
                e.printStackTrace();
                MPLog.e(TAG, e.getMessage());
            }
        }

        JSONObject jsonImUser = jsonUser.getJSONObject("imUser");
        String easemobName = jsonImUser.optString("imUsername");
        String easemobPwd = jsonImUser.optString("imPassword");
        int userId = jsonImUser.optInt("userId");
        String username = jsonUser.optString("username");
        final String realName = jsonUser.optString("realName");
        String image = jsonUser.optString("avatar");
        String email = jsonUser.optString("email");
        String gender = jsonUser.optString("gender");
        String phone = jsonUser.optString("phone");
        String telephone = jsonUser.optString("telephone");
        int tenantId = jsonUser.optInt("tenantId");

        JSONArray orgJsonArray = jsonEntity.optJSONArray("organizationList");
        int organizationId = -1;
        if (orgJsonArray != null && orgJsonArray.length() > 0) {
            JSONObject orgEntity = orgJsonArray.optJSONObject(0);
            organizationId = orgEntity.optInt("id");
        }
        PrefsUtil.getInstance().setLoginUser(userId, easemobName, easemobPwd, username, realName, image, gender, phone, telephone, email, tenantId, organizationId);
        PrefsUtil.getInstance().setLoginUser(jsonEntity.toString());
        PrefsUtil.getInstance().setLoginAllUserEntity(jsonEntity.toString());
        if (!TextUtils.isEmpty(easemobName) && !TextUtils.isEmpty(easemobPwd)) {
            // After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
            // close it before login to make sure DemoDB not overlap
            // reset current user name before login
//			setCurrentUserName(username);

            // call login method
            MPLog.d(TAG, "EMClient.getInstance().login");
            MPPathUtil.getInstance().initDirs(appkey, easemobName, MPClient.get().getAppContext());
//			AppDBManager.initDB(easemobName);
//			DraftManager.getInstance().loadDatas();
            EMClient.getInstance().login(easemobName, easemobPwd, new EMCallBack() {//回调
                @Override
                public void onSuccess() {
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    MPLog.d(TAG, "login chat server success!");
                    // update current user's display name for APNs
                    execute(new Runnable() {
                        @Override
                        public void run() {
                            boolean updatenick = false;
                            try {
                                updatenick = EMClient.getInstance().pushManager().updatePushNickname(
                                        realName);
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                            if (!updatenick) {
                                MPLog.e("LoginActivity", "update current user nick fail");
                            }
                        }
                    });
                    isExit = false;
                    if (callBack != null) {
                        callBack.onSuccess();
                    }

                }

                @Override
                public void onProgress(int progress, String status) {

                }

                @Override
                public void onError(int code, final String message) {
                    Log.d(TAG, "login: onError: " + code);
                    if (callBack != null) {
                        callBack.onError(code, message);
                    }
                }
            });
        } else {
            if (callBack != null) {
                callBack.onError(EMError.GENERAL_ERROR, "im is not found");
            }
        }
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
//		PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
//					CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
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

    public void addEMConnectionListener() {
        EMConnectionListener connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(final int error) {
                MPLog.d("global listener", "onDisconnect" + error);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (error == EMError.USER_REMOVED
                                || error == EMError.SERVER_SERVICE_RESTRICTED || error == EMError.USER_KICKED_BY_OTHER_DEVICE
                                || error == EMError.USER_KICKED_BY_CHANGE_PASSWORD) {
                            if (!isExit) {
                                isExit = true;
                                if (mAuthListener != null) {
                                    mAuthListener.onAuthFailed();
                                }
                                synchronized (connectionListeners) {
                                    for (ConnectionListener listener : connectionListeners) {
                                        listener.onAuthenticationFailed(error);
                                    }
                                }
                            }
                        } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                            MPEventBus.getDefault().post(new EventReLoginStart());
                            EMAPIManager.getInstance().getUserByImUser(EMClient.getInstance().getCurrentUser(), new EMDataCallBack<String>() {
                                @Override
                                public void onSuccess(String value) {
                                    String easeName = PrefsUtil.getInstance().getSharedKeyLoginNameEaseName();
                                    String easePwd = PrefsUtil.getInstance().getSharedKeyLoginNameEasePwd();
                                    if (easeName == null || easePwd == null) {
                                        if (mAuthListener != null) {
                                            mAuthListener.onAuthFailed();
                                        }
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                synchronized (connectionListeners) {
                                                    for (ConnectionListener listener : connectionListeners) {
                                                        listener.onAuthenticationFailed(EMError.USER_LOGIN_ANOTHER_DEVICE);
                                                    }
                                                }
                                            }
                                        });
                                        return;
                                    }

                                    EMClient.getInstance().login(easeName, easePwd, new EMCallBack() {
                                        @Override
                                        public void onSuccess() {
                                            MPEventBus.getDefault().post(new EventReloginSuccess());
                                        }

                                        @Override
                                        public void onError(int i, String s) {
                                            if (!isExit) {
                                                isExit = true;
                                                if (mAuthListener != null) {
                                                    mAuthListener.onAuthFailed();
                                                }
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        synchronized (connectionListeners) {
                                                            for (ConnectionListener listener : connectionListeners) {
                                                                listener.onAuthenticationFailed(error);
                                                            }
                                                        }
                                                    }
                                                });
                                            }

                                        }

                                        @Override
                                        public void onProgress(int i, String s) {

                                        }
                                    });

                                }

                                @Override
                                public void onError(int error, String errorMsg) {
                                    if (mAuthListener != null) {
                                        mAuthListener.onAuthFailed();
                                    }
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            synchronized (connectionListeners) {
                                                for (ConnectionListener listener : connectionListeners) {
                                                    listener.onAuthenticationFailed(EMError.USER_LOGIN_ANOTHER_DEVICE);
                                                }
                                            }
                                        }
                                    });

                                }
                            });
                        } else {
                            synchronized (connectionListeners) {
                                for (ConnectionListener listener : connectionListeners) {
                                    listener.onDisconnected();
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void onConnected() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (connectionListeners) {
                            for (ConnectionListener listener : connectionListeners) {
                                listener.onConnected();
                            }
                        }
                    }
                });
            }
        };

        //register connection listener
        EMClient.getInstance().addConnectionListener(connectionListener);
    }

}
