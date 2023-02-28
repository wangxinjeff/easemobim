package com.hyphenate.easemob.easeui.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.utils.MPPathUtil;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;

import java.io.File;


public class EaseShowNormalFileActivity extends EaseBaseActivity {
    private ProgressBar progressBar;
    private String remoteUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ease_activity_show_file);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        final EMMessage message = getIntent().getParcelableExtra("msg");
        if (message != null) {
            if (!(message.getBody() instanceof EMFileMessageBody)) {
                Toast.makeText(EaseShowNormalFileActivity.this, "Unsupported message body", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            final File file = new File(((EMFileMessageBody) message.getBody()).getLocalUrl());

            message.setMessageStatusCallback(new EMCallBack() {
                @Override
                public void onSuccess() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String suffix = "";
                            try {
                                String fileName = file.getName();
                                suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                            } catch (Exception e) {
                            }
                            try {
                                EaseCommonUtils.openFileEx(file, EaseCommonUtils.getMap(suffix), EaseShowNormalFileActivity.this);
                            } catch (Exception e) {
                                Toast.makeText(EaseShowNormalFileActivity.this, "未安装能打开此文件的软件", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        }
                    });

                }

                @Override
                public void onError(int code, String error) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (file != null && file.exists() && file.isFile())
                                file.delete();
                            String str4 = getResources().getString(R.string.Failed_to_download_file);
                            Toast.makeText(EaseShowNormalFileActivity.this, str4 + message, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }

                @Override
                public void onProgress(final int progress, String status) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progress);
                        }
                    });
                }
            });
            EMClient.getInstance().chatManager().downloadAttachment(message);
        } else {
            remoteUrl = getIntent().getStringExtra("remote_url");
            String displayName = getIntent().getStringExtra("display_name");
            if (TextUtils.isEmpty(remoteUrl)) {
                finish();
                return;
            }
            String localFileName = EaseCommonUtils.getMd5Hash(remoteUrl);
            if (!TextUtils.isEmpty(displayName)) {
                localFileName = localFileName + "_" + displayName;
            }
            final File file = new File(MPPathUtil.getInstance().getFilePath(), localFileName);
            if (file.exists()) {
                String suffix = "";
                try {
                    String fileName = file.getName();
                    suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                } catch (Exception e) {
                }
                try {
                    EaseCommonUtils.openFileEx(file, EaseCommonUtils.getMap(suffix), EaseShowNormalFileActivity.this);
                } catch (Exception e) {
                    Toast.makeText(EaseShowNormalFileActivity.this, "未安装能打开此文件的软件", Toast.LENGTH_SHORT).show();
                }
                finish();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            EMAPIManager.getInstance().downloadFile(remoteUrl, file.getAbsolutePath(), new EMDataCallBack<String>() {
                @Override
                public void onSuccess(String value) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            String suffix = "";
                            try {
                                String fileName = file.getName();
                                suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                            } catch (Exception e) {
                            }
                            try {
                                EaseCommonUtils.openFileEx(file, EaseCommonUtils.getMap(suffix), EaseShowNormalFileActivity.this);
                            } catch (Exception e) {
                                Toast.makeText(EaseShowNormalFileActivity.this, "未安装能打开此文件的软件", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        }
                    });
                }

                @Override
                public void onError(int error, String errorMsg) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), R.string.download_failed, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }

                @Override
                public void onProgress(final int progress) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress((int) (progress * 100));
                        }
                    });
                }
            });

//            RequestCall call = OkHttpUtils.get().url(remoteUrl).build();
//		    call.execute(new FileCallBack(MPPathUtil.getInstance().getFilePath().getAbsolutePath(), localFileName) {
//                @Override
//                public void inProgress(final float progress, long total) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progressBar.setProgress((int) (progress * 100));
//                        }
//                    });
//
//                }
//
//                @Override
//                public void onError(Call call, Exception e) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progressBar.setVisibility(View.GONE);
//                            Toast.makeText(getApplicationContext(), R.string.download_failed, Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    });
//                }
//
//                @Override
//                public void onResponse(final File file) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progressBar.setVisibility(View.GONE);
//                            String suffix = "";
//                            try{
//                                String fileName = file.getName();
//                                suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
//                            }catch (Exception e){}
//                            try{
//                                EaseCommonUtils.openFileEx(file, EaseCommonUtils.getMap(suffix), EaseShowNormalFileActivity.this);
//                            }catch (Exception e){
//                                Toast.makeText(EaseShowNormalFileActivity.this, "未安装能打开此文件的软件", Toast.LENGTH_SHORT).show();
//                            }
//                            finish();
//                        }
//                    });
//
//                }
//            });
        }


    }
}
