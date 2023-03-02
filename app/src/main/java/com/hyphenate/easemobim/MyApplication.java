package com.hyphenate.easemobim;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.multidex.MultiDex;

import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.im.officeautomation.ui.MainActivity;
import com.hyphenate.easemob.imlibs.mp.ConnectionListener;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static Context applicationContext;
    private static MyApplication instance;
    private final List<Activity> mActivities = Collections.synchronizedList(new LinkedList<Activity>());
    protected NotificationManager notificationManager = null;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        AppHelper.getInstance().init(applicationContext, new DevEnv());

        registerLifecycleCallbacks();

        registerConnectionListener();


    }

    private void registerConnectionListener(){
        AppHelper.getInstance().addConnectionListener(new ConnectionListener() {
            @Override
            public void onConnected() {
                MPLog.d(TAG, "onConnected");

            }

            @Override
            public void onAuthenticationFailed(int errorCode) {
                Toast.makeText(instance, com.hyphenate.easemob.R.string.kicked_by_other_device, Toast.LENGTH_SHORT).show();
                loginAgain();
            }

            @Override
            public void onDisconnected() {
                MPLog.d(TAG, "onDisconnected");
            }
        });
    }

    public void loginAgain() {
        clearActivitys();
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(MyApplication.getContext(), LoginActivity.class);
        startActivity(intent);
    }

    public int getActivitySize(){
        return mActivities.size();
    }

    private void registerLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if (activity != null && activity instanceof LoginActivity) {
                    return;
                }
                synchronized (mActivities) {
                    if (!mActivities.contains(activity)) {
                        mActivities.add(activity);
                    }
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                notificationManager.cancel(0);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (activity != null && activity instanceof MainActivity){
                }

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                synchronized (mActivities) {
                    if (mActivities.contains(activity)) {
                        mActivities.remove(activity);
                    }
                }
            }
        });
    }

    /**
     * 获取全局上下文
     *
     * @return 上下文
     */
    public static Context getContext() {
        return applicationContext;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public void clearActivitys() {
        synchronized (mActivities) {
            for (Activity itemActivity : mActivities) {
                if (itemActivity != null) {
                    itemActivity.finish();
                }
            }
        }

    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    //====================================  App Lock ==========================================


    @Override
    public void onTerminate() {
        super.onTerminate();
        AppHelper.getInstance().clearListeners();
    }


}
