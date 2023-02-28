package com.hyphenate.easemob.im.officeautomation.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.hyphenate.easemob.easeui.ui.EaseBaseActivity;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.utils.PackageManagerUtil;
import com.hyphenate.easemob.im.officeautomation.utils.PositionUtil;
import com.hyphenate.easemob.imlibs.mp.utils.MPPathUtil;
import com.hyphenate.easemob.im.officeautomation.widget.SearchBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 06/07/2018
 */

public class EMBaiduMapActivity extends EaseBaseActivity implements OnGetPoiSearchResultListener {

	private final static String TAG = "map";
	LocationClient mLocClient;
	private Button btnSend;
	ProgressDialog mProgressDialog;
	private BaiduMap mBaiduMap;
	private ListView mListView;
	private MapView mBmapView;
	BDLocation lastLocation = null;
	private List<PoiInfo> nearList = new ArrayList<>();
	private LocNearAddressAdapter adapter;
	public BDAbstractLocationListener myListener = new MyLocationListener();
	private double mCurrentLantitude;
	private double mCurrentLongitude;
	private String mCurrentAddress;
	private PoiSearch mPoiSearch;
	private Map<Integer, Boolean> isSelected = new HashMap<>();
	private BaiduSDKReceiver mBaiduSDKReceiver;
	private Marker mCurrentMarker;
	private RelativeLayout rlAddressContainer;
	private TextView tvAddress;
	private ImageButton ibOpenMap;

	private LinearLayout resultView;
	private SearchBar searchBar;

	class BaiduSDKReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			String st1 = getResources().getString(R.string.Network_error);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				String st2 = getResources().getString(R.string.please_check);
				Log.e(TAG, "baidumap:" + st2);
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
			}
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.em_activity_baidumap);
		initViews();
		initDatas();

	}

	private void initDatas() {
		Intent intent = getIntent();
		double latitude = intent.getDoubleExtra("latitude", 0);
		mBaiduMap = mBmapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
		MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
		initMapView();
		if (latitude == 0) {
//			mBmapView = new MapView(this, new BaiduMapOptions());
			mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));
			showMapWithLocationClient();
			rlAddressContainer.setVisibility(View.GONE);
		} else {
			btnSend.setVisibility(View.INVISIBLE);
			double longtitude = intent.getDoubleExtra("longitude", 0);
			String address = intent.getStringExtra("address");
			LatLng p = new LatLng(latitude, longtitude);
			mCurrentLantitude = latitude;
			mCurrentLongitude = longtitude;
			mCurrentAddress = address;
//			mBmapView = new MapView(this, new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(p).build()));
			showMap(latitude, longtitude, address);
			resultView.setVisibility(View.GONE);
			rlAddressContainer.setVisibility(View.VISIBLE);
			tvAddress.setText(address);
		}

		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mBaiduSDKReceiver = new BaiduSDKReceiver();
		registerReceiver(mBaiduSDKReceiver, iFilter);
	}

	private void initViews() {
		mListView = findViewById(R.id.listview);
		mBmapView = findViewById(R.id.bmapView);
		btnSend = findViewById(R.id.btn_location_send);
		rlAddressContainer = findViewById(R.id.rl_address_container);
		tvAddress = findViewById(R.id.tv_address);
		ibOpenMap = findViewById(R.id.ib_openmap);

		resultView = findViewById(R.id.result_view);
		searchBar = findViewById(R.id.search_bar);
		searchBar.init(true);
		searchBar.setOnSearchBarListener(new SearchBar.OnSearchBarListener() {
			@Override
			public void onSearchContent(String text) {
				searchByCity(text);
			}
		});

		nearList.clear();
		adapter = new LocNearAddressAdapter(nearList, isSelected);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				adapter.setSelected(i);
				adapter.notifyDataSetChanged();
				PoiInfo poiInfo = (PoiInfo) adapter.getItem(i);
				if (poiInfo == null) return;
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(poiInfo.location);
				PositionUtil.Gps gps = PositionUtil.bd09_To_Gcj02(poiInfo.location.latitude, poiInfo.location.longitude);
				mCurrentLantitude = gps.getWgLat();
				mCurrentLongitude = gps.getWgLon();
//				mCurrentLantitude = poiInfo.location.latitude;
//				mCurrentLongitude = poiInfo.location.longitude;
				mCurrentAddress = poiInfo.address;
				mBaiduMap.animateMapStatus(u);
				mCurrentMarker.setPosition(poiInfo.location);
			}
		});
		findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ibOpenMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (PackageManagerUtil.haveGaodeMap(getApplicationContext())) {
					openGaodeMapToGuide();
				}
				else if (PackageManagerUtil.haveBaiduMap(getApplicationContext())) {
					openBaiduMapToGuide();
				}
				else {
					openBrowserToGuide();
				}

			}
		});
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mProgressDialog = new ProgressDialog(EMBaiduMapActivity.this);
				mProgressDialog.setMessage(getString(com.hyphenate.easemob.R.string.tip_sending));
				mProgressDialog.show();
				mBaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
					@Override
					public void onSnapshotReady(Bitmap snapshot) {
						final String imagePath = saveMyBitmap(System.currentTimeMillis() + "", snapshot);
						sendImageToServer(imagePath);
					}
				});
			}
		});
	}



	private void openBaiduMapToGuide(){
		PositionUtil.Gps gps = PositionUtil.gcj02_To_Bd09(mCurrentLantitude, mCurrentLongitude);
		Intent intent = new Intent();
		intent.setData(Uri.parse("baidumap://map/direction?" +
				"destination=latlng:" + gps.getWgLat() + "," + gps.getWgLon() + "|name:" + mCurrentAddress +"&mode=driving"));
		startActivity(intent);
	}

	private void openGaodeMapToGuide(){
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		String url = "androidamap://route?sourceApplication=amap&dlat="+mCurrentLantitude+"&dlon="+mCurrentLongitude+"&dname="+mCurrentAddress+"&dev=0&t=1";
		Uri uri = Uri.parse(url);
		// 将功能Schema以Uri的方式传入data
		intent.setData(uri);
		// 启动该页面即可
		startActivity(intent);
	}

	private void openBrowserToGuide(){
		String url = "http://uri.amap.com/navigation?to=" + mCurrentLongitude + "," + mCurrentLantitude + "," + mCurrentAddress + "&mode=car&policy=1&src=mypage&coordinate=gaode&callnative=0";
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}


	@Override
	protected void onPause() {
		mBmapView.onPause();
		if (mLocClient != null) {
			mLocClient.stop();
		}
		super.onPause();

	}

	@Override
	protected void onResume() {
		mBmapView.onResume();
		if (mLocClient != null) {
			mLocClient.start();
		}
		super.onResume();
		lastLocation = null;
	}

	@Override
	protected void onDestroy() {
		if (mLocClient != null) {
			mLocClient.unRegisterLocationListener(myListener);
			mLocClient.stop();
		}
		if (nearList != null){
			nearList.clear();
		}
		mPoiSearch.destroy();
		mBmapView.onDestroy();
		unregisterReceiver(mBaiduSDKReceiver);
		dismissDialog();
		super.onDestroy();
	}

	private void initMapView() {
		mBmapView.setLongClickable(true);
	}

	private void dismissDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}

	}

	/**
	 * 接收周边地理位置结果
	 *
	 * @param poiResult
	 */
	@Override
	public void onGetPoiResult(PoiResult poiResult) {
		if (poiResult != null) {
			if (poiResult.getAllPoi() != null && poiResult.getAllPoi().size() > 0) {
				if(nearList.size() > 1){
					nearList.clear();
					isSelected.clear();
				}
				nearList.addAll(poiResult.getAllPoi());
				if (nearList != null && nearList.size() > 0) {
					for (int i = 0; i < nearList.size(); i++) {
						isSelected.put(i, i == 0);
					}
				}
				if (isFinishing()) {
					return;
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapter.notifyDataSetChanged();
						mListView.setSelection(0);
						PoiInfo poiInfo = (PoiInfo) adapter.getItem(0);
						if (poiInfo == null) return;
						MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(poiInfo.location);
						PositionUtil.Gps gps = PositionUtil.bd09_To_Gcj02(poiInfo.location.latitude, poiInfo.location.longitude);
						mCurrentLantitude = gps.getWgLat();
						mCurrentLongitude = gps.getWgLon();
						mCurrentAddress = poiInfo.address;
						mBaiduMap.animateMapStatus(u);
						mCurrentMarker.setPosition(poiInfo.location);
					}
				});
			}
		}
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

	}

	@Override
	public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

	}

	class MyLocationListener extends BDAbstractLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			MPLog.d(TAG, "On location change received:" + location);
			MPLog.d(TAG, "addr:" + location.getAddrStr());
			dismissDialog();
			if (lastLocation != null) {
				if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
					MPLog.d(TAG, "same location, skip refresh");
					return;
				}
			}

			lastLocation = location;
			mBaiduMap.clear();
			mCurrentLantitude = lastLocation.getLatitude();
			mCurrentLongitude = lastLocation.getLongitude();
			mCurrentAddress = lastLocation.getAddrStr();
			MPLog.e(">>>>>>>", mCurrentLantitude + "," + mCurrentLongitude);
			LatLng llA = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
			CoordinateConverter converter = new CoordinateConverter();
			converter.coord(llA);
			converter.from(CoordinateConverter.CoordType.COMMON);
			LatLng convertLatLng = converter.convert();
			OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory
					.fromResource(R.drawable.ease_icon_marker))
					.zIndex(4).draggable(true);
			mCurrentMarker = (Marker) mBaiduMap.addOverlay(ooA);
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 16.0f);
			mBaiduMap.animateMapStatus(u);
			if (lastLocation != null){
				nearList.clear();
				PoiInfo poiInfo = new PoiInfo();
				PositionUtil.Gps gps = PositionUtil.gcj02_To_Bd09(lastLocation.getLatitude(), lastLocation.getLongitude());
				poiInfo.location = new LatLng(gps.getWgLat(), gps.getWgLon());
				poiInfo.name = lastLocation.getAddrStr();
				poiInfo.address = lastLocation.getAddrStr();
				nearList.add(poiInfo);
				isSelected.put(0, true);

				if(!TextUtils.isEmpty(lastLocation.getCity())){
					mLocClient.stop();
					searchNearBy();
				}
			}
		}
	}


	public void back(View v) {
		finish();
	}


	public String saveMyBitmap(String bitName, Bitmap mBitmap) {
		File f = new File(MPPathUtil.getInstance().getImagePath(), bitName + ".png");
		try {
			f.createNewFile();
		} catch (IOException e) {
			System.out.println("在保存图片时出错：" + e.toString());
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		} catch (Exception e) {
			return "create_bitmap_error";
		}
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f.getPath();
	}

	public void closeUI(String remoteUrl){
		Intent intent = new Intent();
		intent.putExtra("locImage", remoteUrl);
		intent.putExtra("latitude", mCurrentLantitude);
		intent.putExtra("longitude", mCurrentLongitude);
		intent.putExtra("address", mCurrentAddress);
		this.setResult(RESULT_OK, intent);
		finish();
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}


	private void showMap(double latitude, double longtitude, String address) {
		LatLng llA = new LatLng(latitude, longtitude);
		CoordinateConverter converter = new CoordinateConverter();
		converter.coord(llA);
		converter.from(CoordinateConverter.CoordType.COMMON);
		LatLng convertLatLng = converter.convert();
		OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory
				.fromResource(R.drawable.ease_icon_marker))
				.zIndex(4).draggable(true);
		mBaiduMap.addOverlay(ooA);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 16.0f);
		mBaiduMap.animateMapStatus(u);
	}

	private void showMapWithLocationClient() {
		String str1 = getResources().getString(R.string.Making_sure_your_location);
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage(str1);

		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			public void onCancel(DialogInterface arg0) {
				dismissDialog();
				MPLog.d("map", "cancel retrieve location");
				finish();
			}
		});
		mProgressDialog.show();

		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		initLocation();
	}

	/*
	 * 搜索周边地理位置
	 */
	private void searchNearBy() {
		PoiNearbySearchOption option = new PoiNearbySearchOption();
		option.keyword("大厦");
		option.sortType(PoiSortType.distance_from_near_to_far);
		option.location(new LatLng(mCurrentLantitude, mCurrentLongitude));
		option.radius(2000);
		option.pageCapacity(30);
		mPoiSearch.searchNearby(option);
	}

	private void searchByCity(String key){
		PoiCitySearchOption option = new PoiCitySearchOption();
		option.keyword(key);
		option.city(lastLocation.getCity());
		option.pageCapacity(30);
		mPoiSearch.searchInCity(option);
	}

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("gcj02");//可选，默认gcj02，设置返回的定位结果坐标系
		int span = 3000;
		option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);//可选，默认false,设置是否使用gps
		option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocClient.setLocOption(option);
	}

	class LocNearAddressAdapter extends BaseAdapter {
		private Map<Integer, Boolean> isSelected;

		private List<PoiInfo> mPoiInfos;

		LocNearAddressAdapter(List<PoiInfo> objects, Map<Integer, Boolean> selected) {
			this.isSelected = selected;
			this.mPoiInfos = objects;
		}

		public void setSelected(int index) {
			if (isSelected == null) return;
			for (Map.Entry<Integer, Boolean> item : isSelected.entrySet()) {
				item.setValue(false);
			}
			isSelected.put(index, true);
		}

		public boolean isSelected(int index){
			if(isSelected == null) return false;
			return isSelected.get(index);
		}

		@Override
		public int getCount() {
			return mPoiInfos.size();
		}

		@Override
		public PoiInfo getItem(int i) {
			if (i < mPoiInfos.size()) {
				return mPoiInfos.get(i);
			}
			return null;
		}

		@Override
		public long getItemId(int i) {
			return 0;
		}

		@Override
		public View getView(int i, View convertView, ViewGroup viewGroup) {
			ViewHolder holder = null;
			if (convertView == null){
				convertView = LayoutInflater.from(EMBaiduMapActivity.this).inflate(R.layout.ease_layout_item_baidu, viewGroup,false);
				holder = new ViewHolder();
				holder.ivImg = convertView.findViewById(R.id.iv_selected);
				holder.tvName = convertView.findViewById(R.id.name);
				holder.tvAddress = convertView.findViewById(R.id.address);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			PoiInfo poiInfo = getItem(i);
			if (poiInfo == null) return convertView;
			boolean isSelected = isSelected(i);
			String strName = poiInfo.name == null ? poiInfo.address : poiInfo.name;
			holder.tvName.setText(i == 0 ? "当前位置：" + strName : strName);
			holder.tvAddress.setText(poiInfo.address);
			holder.ivImg.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
			return convertView;
		}


		class ViewHolder {
			TextView tvName;
			TextView tvAddress;
			ImageView ivImg;
		}
	}



	private void sendImageToServer(String localPath){
		EMAPIManager.getInstance().postFile(new File(localPath), Constant.FILE_UPLOAD_TYPE_TEMPORARY, new EMDataCallBack<String>() {
			@Override
			public void onSuccess(final String value) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dismissDialog();
						try {
							JSONObject jsonObject = new JSONObject(value);
							JSONObject jsonEntty = jsonObject.optJSONObject("entity");
							if (jsonEntty != null && jsonEntty.has("url")){
								String imgUrl = jsonEntty.optString("url");
								sendSuccess(imgUrl);
							}else{
								sentFailedToast();
							}

						} catch (JSONException e) {
							sentFailedToast();
						}
					}
				});

			}

			@Override
			public void onError(int error, String errorMsg) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dismissDialog();
						sentFailedToast();
					}
				});

			}
		});
	}


	private void sentFailedToast(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				dismissDialog();
				Toast.makeText(EMBaiduMapActivity.this, "发送失败，请检查网络!", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void sendSuccess(final String url){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				closeUI(url);
			}
		});
	}

}





