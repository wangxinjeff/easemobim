package com.hyphenate.easemob.easeui.widget.chatrow;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.TextFormater;

import java.io.File;

public class EaseChatRowFile extends EaseChatRow{
    private static final String TAG = "EaseChatRowFile";

    protected TextView fileNameView;
	protected TextView fileSizeView;
    protected TextView fileStateView;

    protected ImageView ivFile;

    private EMNormalFileMessageBody fileMessageBody;

    public EaseChatRowFile(Context context, EMMessage message, int position, EaseMessageAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
	protected void onInflateView() {
	    inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? 
	            R.layout.ease_row_received_file : R.layout.ease_row_sent_file, this);
	}

	@Override
	protected void onFindViewById() {
	    fileNameView = (TextView) findViewById(R.id.tv_file_name);
        fileSizeView = (TextView) findViewById(R.id.tv_file_size);
        fileStateView = (TextView) findViewById(R.id.tv_file_state);
        percentageView = (TextView) findViewById(R.id.percentage);
        ivFile = findViewById(R.id.iv_file);
	}


	@Override
	protected void onSetUpView() {
	    fileMessageBody = (EMNormalFileMessageBody) message.getBody();
        String filePath = fileMessageBody.getLocalUrl();
        fileNameView.setText(fileMessageBody.getFileName());
        fileSizeView.setText(TextFormater.getDataSize(fileMessageBody.getFileSize()));
        ivFile.setImageResource(EaseCommonUtils.getFileIconRes(fileMessageBody.getFileName()));
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            File file = new File(filePath);
            if (file.exists()) {
                fileStateView.setText(R.string.Have_downloaded);
            } else {
                fileStateView.setText(R.string.Did_not_download);
            }
            return;
        }
	}

    @Override
    protected void onViewUpdate(EMMessage msg) {
        switch (msg.status()) {
            case CREATE:
                onMessageCreate();
                break;
            case SUCCESS:
                onMessageSuccess();
                break;
            case FAIL:
                onMessageError();
                break;
            case INPROGRESS:
                onMessageInProgress();
                break;
        }
    }

    private void onMessageCreate() {
        onMessageInProgress();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (message.status() == EMMessage.Status.CREATE) {
                    progressBar.setVisibility(View.GONE);
                    if (percentageView != null)
                        percentageView.setVisibility(View.INVISIBLE);
                    if (statusView != null)
                        statusView.setVisibility(View.VISIBLE);
                }
            }
        }, 1000);
    }

    private void onMessageSuccess() {
        progressBar.setVisibility(View.INVISIBLE);
        if (percentageView != null)
            percentageView.setVisibility(View.INVISIBLE);
        if (statusView != null) {
            statusView.setVisibility(View.INVISIBLE);
        }
    }

    private void onMessageError() {
        progressBar.setVisibility(View.INVISIBLE);
        if (percentageView != null)
            percentageView.setVisibility(View.INVISIBLE);
        if (statusView != null)
            statusView.setVisibility(View.VISIBLE);
    }

    private void onMessageInProgress() {
        progressBar.setVisibility(View.VISIBLE);
        if (percentageView != null) {
            percentageView.setVisibility(View.VISIBLE);
            percentageView.setText(message.progress() + "%");
        }
        if (statusView != null)
            statusView.setVisibility(View.INVISIBLE);
    }

}
