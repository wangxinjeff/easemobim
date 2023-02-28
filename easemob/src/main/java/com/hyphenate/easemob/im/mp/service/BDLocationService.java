package com.hyphenate.easemob.im.mp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hyphenate.easemob.im.mp.location.RTLocationManager;

public class BDLocationService extends Service implements BDLocationListener{

    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        double lat = bdLocation.getLatitude();
        double lng = bdLocation.getLongitude();
        float radius = bdLocation.getRadius();
        float direction = bdLocation.getDirection();
        RTLocationManager.getInstance().notifyListener(lat, lng, radius, direction);
    }

    public class LocalBinder extends Binder {
        public BDLocationService getService() {
            return BDLocationService.this;
        }
    }

    private LocationClient mLocationClient;

    public void startLocation() {
        initLocation();
        if (mLocationClient != null) {
            mLocationClient.start();
        }
    }

    public void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
    }

    public boolean locIsStarted() {
        return mLocationClient.isStarted();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setScanSpan(1000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
//        option.setCoorType("gcj02");
        option.setNeedDeviceDirect(true);
//        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//        option.setOpenGps();
        mLocationClient.setLocOption(option);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
//        mLocationClient.registerNotifyLocationListener(this);
        mLocationClient.registerLocationListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


}
