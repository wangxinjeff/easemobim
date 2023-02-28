package com.hyphenate.easemob.im.mp.location;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.hyphenate.chat.EMClient;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.easemob.imlibs.mp.events.EventLocChanged;
import com.hyphenate.easemob.imlibs.mp.events.EventLocEnded;
import com.hyphenate.easemob.imlibs.mp.events.EventLocStarted;
import com.hyphenate.easemob.im.mp.service.BDLocationService;

public class LocServiceManager {

    private static LocServiceManager sInstance = new LocServiceManager();

    public static LocServiceManager getInstance() {
        return sInstance;
    }

    private LocServiceManager() {}


    private BDLocationService mLocationService;

    public void bindService(Context context) {
        Intent intent = new Intent(context, BDLocationService.class);
        context.bindService(intent, mLocationConnection, Context.BIND_AUTO_CREATE);
    }

    public void unBindService(Context context) {
        try {
            context.unbindService(mLocationConnection);
        } catch (Exception ignored) {
        }
    }

    public void startLocation() {
        if (mLocationService != null) {
            mLocationService.startLocation();
            MPEventBus.getDefault().post(new EventLocStarted());
        }
    }

    public void stopLocation() {
        if (mLocationService != null) {
            mLocationService.stopLocation();
            if (isStarted()) {
                MPEventBus.getDefault().post(new EventLocEnded());
            }
        }
    }

    public boolean isStarted() {
        if (mLocationService != null) {
            return mLocationService.locIsStarted();
        }
        return false;
    }

    private ServiceConnection mLocationConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocationService, cast the IBinder and get LocationService instance
            BDLocationService.LocalBinder localBinder = (BDLocationService.LocalBinder) service;
            mLocationService = localBinder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

}
