package com.hyphenate.easemob.im.officeautomation.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.ClickListener;
import com.cjt2325.cameralibrary.listener.ErrorListener;
import com.cjt2325.cameralibrary.listener.JCameraListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easemob.easeui.utils.BitmapUtils;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.mp.utils.MPPathUtil;

import java.io.File;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 18/08/2018
 */

public class CameraActivity extends BaseActivity {
	private final int GET_PERMISSION_REQUEST = 100; //权限申请自定义码
	private JCameraView mJCameraView;
	private boolean granted = false;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		setSwipeEnabled(false);
		mJCameraView = findViewById(R.id.jcameraview);

		//设置视频保存路径
		mJCameraView.setSaveVideoPath(MPPathUtil.getInstance().getVideoPath().getPath());

		//JCameraView监听
		mJCameraView.setJCameraLisenter(new JCameraListener() {
			@Override
			public void captureSuccess(Bitmap bitmap) {
				File imageFile = new File(MPPathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
				File storeFile = BitmapUtils.storeBitmap(imageFile, bitmap);
				setResult(RESULT_OK, new Intent().putExtra("type", 0).putExtra("path", storeFile.getPath()));
				finish();
			}

			@Override
			public void recordSuccess(String url, Bitmap firstFrame) {
				File imageFile = new File(MPPathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
						+ System.currentTimeMillis() + ".jpg");
				File storeFile = BitmapUtils.storeBitmap(imageFile, firstFrame);
				setResult(RESULT_OK, new Intent().putExtra("type", 1).putExtra("thumb_path", storeFile.getPath()).putExtra("path", url));
				finish();

			}
		});
		mJCameraView.setErrorLisenter(new ErrorListener() {
			@Override
			public void onError() {
				//打开Camera失败回调
				Toast.makeText(activity, R.string.open_camera_failed, Toast.LENGTH_SHORT).show();

			}

			@Override
			public void AudioPermissionError() {
				//没有录制权限回调
				Toast.makeText(activity, R.string.no_record_permission, Toast.LENGTH_SHORT).show();
			}
		});

		mJCameraView.setLeftClickListener(new ClickListener() {
			@Override
			public void onClick() {
				finish();
			}
		});

		//6.0动态权限获取
		getPermissions();
	}


	@Override
	protected void onStart() {
		super.onStart();
		//全屏显示
		if (Build.VERSION.SDK_INT >= 19){
			View decorView = getWindow().getDecorView();
			decorView.setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
			);
		}else{
			View decorView = getWindow().getDecorView();
			int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
			decorView.setSystemUiVisibility(option);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (granted){
			mJCameraView.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mJCameraView.onPause();
	}

	/**
	 * 获取权限
	 */
	private void getPermissions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
					ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
					ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
				//具有权限
				granted = true;
			} else {
				//不具有获取权限，需要进行权限申请
				ActivityCompat.requestPermissions(this, new String[]{
						Manifest.permission.WRITE_EXTERNAL_STORAGE,
						Manifest.permission.RECORD_AUDIO,
						Manifest.permission.CAMERA
				}, GET_PERMISSION_REQUEST);
				granted = false;
			}
		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == GET_PERMISSION_REQUEST){
			int size = 0;
			if (grantResults.length >= 1){
				int writeResult = grantResults[0];
				//读写内存权限
				boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;//读写内存权限
				if (!writeGranted){
					size++;
				}
				//录音权限
				int recordPermissionResult = grantResults[1];
				boolean recordPermissionGranted = recordPermissionResult == PackageManager.PERMISSION_GRANTED;
				if (!recordPermissionGranted) {
					size++;
				}
				//相机权限
				int cameraPermissionResult = grantResults[2];
				boolean cameraPermissionGranted = cameraPermissionResult == PackageManager.PERMISSION_GRANTED;
				if (!cameraPermissionGranted) {
					size++;
				}
				if (size == 0) {
					granted = true;
					mJCameraView.onResume();
				}else{
					Toast.makeText(this, "请到设置-权限管理中开启", Toast.LENGTH_SHORT).show();
					finish();
				}
			}


		}



	}
}
