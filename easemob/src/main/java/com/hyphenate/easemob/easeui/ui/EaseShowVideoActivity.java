package com.hyphenate.easemob.easeui.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.easeui.utils.EncryptUtils;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.utils.MPPathUtil;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.util.EMLog;

import java.io.File;

/**
 * show the video
 */
public class EaseShowVideoActivity extends EaseBaseActivity {
    private static final String TAG = "ShowVideoActivity";

    private RelativeLayout loadingLayout;
    private ProgressBar progressBar;
    private String localFilePath;
    private String remote_url;
    private EMMessage mEMMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ease_showvideo_activity);
        loadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        String msgId = getIntent().getStringExtra("msgId");
        if (!TextUtils.isEmpty(msgId)) {
            mEMMessage = EMClient.getInstance().chatManager().getMessage(msgId);
        }
        remote_url = getIntent().getStringExtra("remote_url");
        if (mEMMessage != null) {
            EMVideoMessageBody msgBody = (EMVideoMessageBody) mEMMessage.getBody();
            localFilePath = msgBody.getLocalUrl();
            if (localFilePath != null) {
                File file = new File(localFilePath);
                if (file.exists()) {
                    showLocalVideo(file);
                    finish();
                } else {
                    EMLog.d(TAG, "download remote video file");
                    downloadVideo(localFilePath, msgBody.getRemoteUrl());
                }

            } else {
                EMLog.d(TAG, "download remote video file");
//                downloadVideo(mEMMessage);
                downloadVideo(localFilePath, msgBody.getRemoteUrl());
            }
        } else if (!TextUtils.isEmpty(remote_url)) {
            localFilePath = MPPathUtil.getInstance().getVideoPath().getPath() + "/" + EncryptUtils.encryptMD5ToString(remote_url) + ".mp4";
            File file = new File(localFilePath);
            if (file.exists()) {
                showLocalVideo(file);
                finish();
            } else {
                downloadVideo(localFilePath, remote_url);
            }
        }


    }

    /**
     * show local video
     *
     * @param file -- local path of the video file
     */
    private void showLocalVideo(File file) {
        try {
            EaseCommonUtils.openFileEx(file, "video/mp4", this);
        } catch (Exception e) {
            Toast.makeText(this, "未安装能打开此文件的软件", Toast.LENGTH_SHORT).show();
        }
//        try{
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.fromFile(new File(localPath)),
//                    "video/mp4");
//            startActivity(intent);

//        }catch (Exception e){
//            e.printStackTrace();
//        }
        finish();
    }

    private void downloadVideo(final String localFilePath, String remoteUrl) {
        loadingLayout.setVisibility(View.VISIBLE);
        EMAPIManager.getInstance().downloadFile(remoteUrl, localFilePath, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        loadingLayout.setVisibility(View.GONE);
                        progressBar.setProgress(0);
                        showLocalVideo(new File(localFilePath));
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                Log.e("###", "offline file transfer error:" + errorMsg);
                File file = new File(localFilePath);
                if (file.exists()) {
                    file.delete();
                }
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        loadingLayout.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), R.string.downwaiting, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onProgress(final int progress) {
                Log.d("ease", "video progress:" + progress);
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progressBar.setProgress(progress);
                    }
                });
            }
        });

//        EMHttpClient.getInstance().downloadFile(remoteUrl, localFilePath, null, new EMCloudOperationCallback() {
//            @Override
//            public void onSuccess(String s) {
//                if (isFinishing()){
//                    return;
//                }
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        loadingLayout.setVisibility(View.GONE);
//                        progressBar.setProgress(0);
//                        showLocalVideo(new File(localFilePath));
//                    }
//                });
//            }
//
//            @Override
//            public void onError(String msg) {
//                Log.e("###", "offline file transfer error:" + msg);
//                File file = new File(localFilePath);
//                if (file.exists()) {
//                    file.delete();
//                }
//                if (isFinishing()){
//                    return;
//                }
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        loadingLayout.setVisibility(View.GONE);
//                        Toast.makeText(getApplicationContext(), R.string.downwaiting, Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                });
//            }
//
//            @Override
//            public void onProgress(final int progress) {
//                Log.d("ease", "video progress:" + progress);
//                if (isFinishing()){
//                    return;
//                }
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        progressBar.setProgress(progress);
//                    }
//                });
//            }
//        });
    }

    /**
     * download video file
     */
    private void downloadVideo(EMMessage message) {
        loadingLayout.setVisibility(View.VISIBLE);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        loadingLayout.setVisibility(View.GONE);
                        progressBar.setProgress(0);
                        showLocalVideo(new File(localFilePath));
                    }
                });
            }

            @Override
            public void onProgress(final int progress, String status) {
                Log.d("ease", "video progress:" + progress);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progressBar.setProgress(progress);
                    }
                });

            }

            @Override
            public void onError(int error, String msg) {
                Log.e("###", "offline file transfer error:" + msg);
                File file = new File(localFilePath);
                if (file.exists()) {
                    file.delete();
                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        loadingLayout.setVisibility(View.GONE);
                        finish();
                        Toast.makeText(getApplicationContext(), R.string.downwaiting, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        EMClient.getInstance().chatManager().downloadAttachment(message);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
