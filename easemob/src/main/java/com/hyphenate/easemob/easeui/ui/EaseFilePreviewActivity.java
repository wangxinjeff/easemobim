package com.hyphenate.easemob.easeui.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.util.TextFormater;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

public class EaseFilePreviewActivity extends EaseBaseActivity {

    private ImageView ivBack;
    private ImageView ivFileIcon;
    private TextView tvFileName;
    private TextView tvFileSize;

    private ProgressBar mProgressBar;
    private TextView tvProgress;

    private Button btnInternalOpen;
    private Button btnOpen;

    private EMMessage message;
    private File localFile;
    private boolean fileDownloaded;
    private View llProgressContainer;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.ease_activity_file_preview);
        initViews();
        initListeners();

        initData();

    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivFileIcon = findViewById(R.id.iv_file);
        tvFileName = findViewById(R.id.tv_file_name);
        mProgressBar = findViewById(R.id.progressBar);

        tvProgress = findViewById(R.id.tv_progress);
        tvFileSize = findViewById(R.id.tv_file_size);
        btnInternalOpen = findViewById(R.id.btn_internal_open);
        btnOpen = findViewById(R.id.btn_open);

        llProgressContainer = findViewById(R.id.ll_progress);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



    private void initListeners() {
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileDownloaded) {
                    openByOtherApp();
                } else {
                    startDownloadFile();
                }
            }
        });
        btnInternalOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileDownloaded) {
                    Intent intent = new Intent(getBaseContext(), EaseTBSActivity.class);
                    intent.putExtra("localPath", localFile.getPath());
                    startActivity(intent);
                }
            }
        });
    }

    private void initData() {
        message = getIntent().getParcelableExtra("msg");

        if (message == null) {
            return;
        }

        EMNormalFileMessageBody fileMessageBody = (EMNormalFileMessageBody) message.getBody();
        String localPath = fileMessageBody.getLocalUrl();
        localFile = new File(localPath);
        if (localFile.exists()) {
            fileDownloaded = true;
        }


        ivFileIcon.setImageResource(EaseCommonUtils.getFileIconRes(fileMessageBody.getFileName()));
        tvFileName.setText(fileMessageBody.getFileName());
        tvFileSize.setText(TextFormater.getDataSize(fileMessageBody.getFileSize()));

        refreshFileStatus();
    }

    private void startDownloadFile() {
        llProgressContainer.setVisibility(View.VISIBLE);
        btnOpen.setVisibility(View.GONE);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        llProgressContainer.setVisibility(View.GONE);
                        fileDownloaded = true;
                        refreshFileStatus();
                    }
                });

            }

            @Override
            public void onError(int code, String error) {
                if (isFinishing()){
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        llProgressContainer.setVisibility(View.GONE);
                        if(localFile != null && localFile.exists()&&localFile.isFile())
                            localFile.delete();
                        String str4 = getResources().getString(R.string.Failed_to_download_file);
                        Toast.makeText(getApplicationContext(), str4+message, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onProgress(final int progress, String status) {
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        mProgressBar.setProgress(progress);
                        tvProgress.setText("正在下载..." + progress + "%");
                    }
                });
            }
        });
        EMClient.getInstance().chatManager().downloadAttachment(message);
    }


    private void refreshFileStatus() {
        btnOpen.setVisibility(View.VISIBLE);
        if (fileDownloaded) {
            btnOpen.setText("用其他应用打开");
        } else {
            btnOpen.setText("开始下载");
        }

        if (MPClient.get().isTbsInited() && fileDownloaded) {
            if (supportInternalWebOpen()) {
                btnInternalOpen.setVisibility(View.VISIBLE);
            } else {
                btnInternalOpen.setVisibility(View.GONE);
            }
        } else {
            btnInternalOpen.setVisibility(View.GONE);
        }
    }

    private boolean supportInternalWebOpen() {
        TbsReaderView  tbsReaderView = new TbsReaderView(this, new TbsReaderView.ReaderCallback() {
            @Override
            public void onCallBackAction(Integer integer, Object o, Object o1) {

            }
        });
        return tbsReaderView.preOpen(parseFormat(localFile.getName()), false);
    }

    private String parseFormat(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }



    private void openByOtherApp() {
        if (localFile == null) {
            Toast.makeText(getApplicationContext(), "文件不存在！", Toast.LENGTH_SHORT).show();
            return;
        }
        // open files if it exist
        String suffix = "";
        try{
            String fileName = localFile.getName();
            suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        }catch (Exception e){}
        try{
            EaseCommonUtils.openFileEx(localFile, EaseCommonUtils.getMap(suffix), this);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "未安装能打开此文件的软件", Toast.LENGTH_SHORT).show();
        }
    }


}
