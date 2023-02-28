package com.hyphenate.easemob.im.mp.ui.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.eventbus.Subscribe;
import com.hyphenate.eventbus.ThreadMode;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.imlibs.mp.events.EventLocNotify;
import com.hyphenate.easemob.im.mp.location.LatLngManager;
import com.hyphenate.easemob.im.mp.location.LocServiceManager;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.im.mp.widget.loc.LocAvatarGroup;
import com.hyphenate.easemob.im.mp.widget.loc.LocImageView;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShareLocationActivity extends BaseActivity {

    private static final String TAG = "sharelocation";
    private MapView mBmapView;
    private BaiduMap mBaiduMap;

//    private MapSDKReceiver mapReceiver;
//    private Marker mCurrentMarker;
    private ImageButton ibtnExit;
    private ImageButton ibtnMinium;
    private ImageButton ibMyLoc;

    private LatLng mSelfLatlng;
    private LocAvatarGroup llContainer;
    private String toChatUsername;
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_share_location);
        MPEventBus.getDefault().register(this);
        initViews();
        initListeners();
        initDatas();
    }

    private void initViews() {
        mBmapView = findViewById(R.id.bmapView);
        ibtnExit = findViewById(R.id.ib_exit);
        ibtnMinium = findViewById(R.id.ib_minium);
        ibMyLoc = findViewById(R.id.ib_my_location);
        llContainer = findViewById(R.id.ll_container);
    }

    private void initListeners() {
        ibtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new XPopup.Builder(activity).asConfirm(null, "是否结束共享位置", "取消", "结束共享", new OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        LatLngManager.getInstance().removeUser(toChatUsername, EMClient.getInstance().getCurrentUser());
                        LocServiceManager.getInstance().stopLocation();
                        finish();
                    }
                }, null, false).show();
            }
        });
        ibtnMinium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ibMyLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMyLocation();
            }
        });
        llContainer.setListener(new LocAvatarGroup.LocImageClickListener() {
            @Override
            public void onClick(LocImageView locIV) {
//                CoordinateConverter converter = new CoordinateConverter();
//                converter.coord(new LatLng(locIV.getLat(), locIV.getLng()));
//                LatLng convertLatLng = converter.convert();
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(locIV.getLat(), locIV.getLng()));
                mBaiduMap.animateMapStatus(u);
            }
        });
    }

    private void toMyLocation() {
        if (mSelfLatlng == null) {
            return;
        }
        CoordinateConverter converter = new CoordinateConverter();
        converter.coord(mSelfLatlng);
        LatLng convertLatLng = converter.convert();
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(convertLatLng);
        mBaiduMap.animateMapStatus(u);
    }

    private void initDatas() {
        mBaiduMap = mBmapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                true, null));
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(13.0f);
        mBaiduMap.setMapStatus(msu);
        toChatUsername = getIntent().getStringExtra(Constant.EXTRA_USER_ID);

        startLooper();
    }

    private void startLooper() {
//        threadPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                List<LatLngManager.LatLngEntity> enttys = LatLngManager.getInstance().getRealLatLng();
//                if (isFinishing()) {
//                    return;
//                }
//                refreshAllViews(enttys);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        startLooper();
//                    }
//                }, 5000);
//            }
//        });
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onLocUserChanged(EventLocNotify event) {
        List<LatLngManager.LatLngEntity> enttys = LatLngManager.getInstance().getRealLatLng();
        if (isFinishing()) {
            return;
        }
        refreshAllViews(enttys);
    }



    @Override
    protected void onResume() {
        mBmapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mBmapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        MPEventBus.getDefault().unregister(this);
        mBaiduMap.setMyLocationEnabled(true);
        mBmapView.onDestroy();
        super.onDestroy();

    }






    private void refreshAllViews(List<LatLngManager.LatLngEntity> enttys) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                llContainer.removeAllViews();
                List<OverlayOptions> olos = new ArrayList<>();
                for (LatLngManager.LatLngEntity item : enttys) {
                    LatLng latLng = new LatLng(item.getLat(), item.getLng());
                    boolean isFirst = false;
                    if (mSelfLatlng == null) {
                        isFirst = true;
                    }
                    if (EMClient.getInstance().getCurrentUser().equals(item.getUsername())) {
                        mSelfLatlng = latLng;
                        MyLocationData locData = new MyLocationData.Builder()
                                .latitude(item.getLat())
                                .longitude(item.getLng())
                                .accuracy(item.getRadius())
                                .direction(item.getDirection())
                                .build();
                        mBaiduMap.setMyLocationData(locData);
                        if (isFirst) {
                            MapStatus.Builder builder = new MapStatus.Builder();
                            builder.target(latLng).zoom(13.0f);
                            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                        }
                    }
                    CoordinateConverter converter = new CoordinateConverter();
                    converter.coord(latLng);
                    LatLng convertLatLng = converter.convert();
                    EaseUser easeUser = UserProvider.getInstance().getEaseUser(item.getUsername());

                    Bitmap avatarBitmap;
                    if (easeUser.getAvatar() != null) {
                        String avatarUrl = easeUser.getAvatar();
                        if (!avatarUrl.startsWith("http")) {
                            avatarUrl = MPClient.get().getAppServer() + avatarUrl;
                        }
                        avatarBitmap = changeView2Drawable(avatarUrl);
                        llContainer.addLocImageView(item.getUsername(), avatarUrl, item.getLat(), item.getLng(), item.getRadius(), item.getDirection());
                    } else {
                        avatarBitmap = changeView2Drawable(null);
                        llContainer.addLocImageView(item.getUsername(), R.drawable.ease_default_avatar, item.getLat(), item.getLng(), item.getRadius(), item.getDirection());
                    }

                    OverlayOptions olo = new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(avatarBitmap))
                            .zIndex(3).draggable(true);
                    olos.add(olo);

//                String from = item.getKey();
//                EaseUser user = UserProvider.getInstance().getEaseUser(from);
//                String avatarUrl = user.getAvatar();
                }
                mBaiduMap.clear();
                mBaiduMap.addOverlays(olos);
            }
        });
    }


    private Bitmap changeView2Drawable(String avatarUrl) {
//        Glide.with(activity).load(avatarUrl).apply(RequestOptions.circleCropTransform().error(R.drawable.ease_default_avatar)).into(imageView);
        View view = LayoutInflater.from(activity).inflate(R.layout.loc_item_bd_pop, null);
        ImageView imgView = view.findViewById(R.id.imageView);
        if (!TextUtils.isEmpty(avatarUrl)) {
            Glide.with(activity).load(avatarUrl).apply(RequestOptions.circleCropTransform().error(R.drawable.ease_default_avatar)).into(imgView);
        }
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        // 获取到图片，这样就可以添加到Map上
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        return bitmap;
    }



    private Handler mHandler = new Handler();

    class MapSDKReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            String st1 = getResources().getString(R.string.Network_error);
            if (s == null) {
                return;
            }
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)){
                String st2 = getResources().getString(R.string.please_check);
                Log.e(TAG, "map:" + st2);
            }else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)){
                Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
            }
        }
    }



}
