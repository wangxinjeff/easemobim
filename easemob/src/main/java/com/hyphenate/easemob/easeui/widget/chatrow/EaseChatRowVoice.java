package com.hyphenate.easemob.easeui.widget.chatrow;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.util.EMLog;

public class EaseChatRowVoice extends EaseChatRowFile {
    private static final String TAG = "EaseChatRowVoice";

    public TextView voiceLengthView;
    public ImageView readStatusView;
    private int mMinItemWidth;
    private int mMaxItemWidth;

    public EaseChatRowVoice(Context context, EMMessage message, int position, EaseMessageAdapter adapter) {
        super(context, message, position, adapter);
        mMinItemWidth = adapter.mMinItemWidth;
        mMaxItemWidth = adapter.mMaxItemWidth;
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_voice : R.layout.ease_row_sent_voice, this);
    }

    @Override
    protected void onFindViewById() {
        voiceLengthView = (TextView) findViewById(R.id.tv_length);
        readStatusView = (ImageView) findViewById(R.id.iv_unread_voice);
    }


    @Override
    protected void onSetUpView() {
        final EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();
        int len = voiceBody.getLength();
        if (len > 0) {
            voiceLengthView.setText(voiceBody.getLength() + "\"");
            voiceLengthView.setVisibility(View.VISIBLE);
        } else {
            voiceLengthView.setVisibility(View.INVISIBLE);
        }
        ViewGroup.LayoutParams layoutParams = bubbleLayout.getLayoutParams();
        layoutParams.width = (int) (mMinItemWidth + Math.min(mMaxItemWidth / 20f * len, mMaxItemWidth));


        if (adapter.currentPlayView != null && adapter.currentPlayView == bubbleLayout) {
            int posTag = (int) adapter.currentPlayView.getTag();
            if (posTag != position) {
                if (message.direct() == EMMessage.Direct.SEND) {
//                    adapter.animView.setBackgroundResource(R.drawable.ease_chatto_voice_playing);
                    adapter.animView.setBackgroundResource(R.drawable.mp_ic_voice_send_anim_3);
                } else {
//                    adapter.animView.setBackgroundResource(R.drawable.ease_chatfrom_voice_playing);
                    adapter.animView.setBackgroundResource(R.drawable.mp_ic_voice_recv_anim_3);
                }
            } else {
                if (message.direct() == EMMessage.Direct.SEND) {
                    adapter.animView.setBackgroundResource(R.drawable.voice_to_icon);
                } else {
                    adapter.animView.setBackgroundResource(R.drawable.voice_from_icon);
                }
                AnimationDrawable anim = (AnimationDrawable) adapter.animView.getBackground();
                anim.start();
            }
        }

        if (message.direct() == EMMessage.Direct.RECEIVE) {
            if (message.isListened()) {
                // hide the unread icon
                readStatusView.setVisibility(View.INVISIBLE);
            } else {
                readStatusView.setVisibility(View.VISIBLE);
            }
            EMLog.d(TAG, "it is receive msg");
            if (voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                }

            } else {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }

    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
        super.onViewUpdate(msg);

        // Only the received message has the attachment download status.
        if (message.direct() == EMMessage.Direct.SEND) {
            return;
        }

        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) msg.getBody();
        if (voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
