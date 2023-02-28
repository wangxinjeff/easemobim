package com.hyphenate.easemob.easeui.widget.presenter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.player.MediaManager;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRowVoice;
import com.hyphenate.exceptions.HyphenateException;

import java.io.File;

/**
 * Created by zhangsong on 17-10-12.
 */

public abstract class EaseChatVoicePresenter extends EaseChatFilePresenter {
    private static final String TAG = "EaseChatVoicePresenter";

    private EaseChatRowVoice voiceChatRow;

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, EaseMessageAdapter adapter) {
        voiceChatRow = new EaseChatRowVoice(cxt, message, position, adapter);
        return voiceChatRow;
    }

    @Override
    public void onBubbleClick(final EMMessage message) {
        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();

        if (message.direct() == EMMessage.Direct.SEND){
            playVoice(voiceChatRow.bubbleLayout, voiceBody.getLocalUrl(), true);
        }else {
            File file = new File(voiceBody.getLocalUrl());
            if (file.exists()){
                playVoice(voiceChatRow.bubbleLayout, voiceBody.getLocalUrl(), false);
            }else if (voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING || voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING){
                Toast.makeText(getContext(), R.string.is_down_please_wait, Toast.LENGTH_SHORT).show();
            }else{
                new AsyncTask<Void, Void, Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {
                        EMClient.getInstance().chatManager().downloadAttachment(message);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        adapter.refresh();
                    }
                }.execute();
            }
        }
    }

    private void playVoice(View v, String localPath, final boolean isSend){

        //播放动画
        if (adapter.animView != null){
            boolean preIsSend = (boolean) adapter.animView.getTag();
            if (preIsSend){
                adapter.animView.setBackgroundResource(R.drawable.mp_ic_voice_send_anim_3);
            }else{
                adapter.animView.setBackgroundResource(R.drawable.mp_ic_voice_recv_anim_3);
            }
            adapter.animView = null;
        }
        if (adapter.currentPlayView != null && adapter.currentPlayView == v){
            MediaManager.getManager().release();
            adapter.currentPlayView = null;
            return;
        }

        adapter.currentPlayView = v;
        adapter.currentPlayView.setTag(getPosition());
        adapter.animView = v.findViewById(R.id.id_recorder_anim);
        adapter.animView.setTag(isSend);
        if (isSend){
            adapter.animView.setBackgroundResource(R.drawable.voice_to_icon);
        }else{
            adapter.animView.setBackgroundResource(R.drawable.voice_from_icon);
            ackMessage(message);
        }

        AnimationDrawable anim = (AnimationDrawable) adapter.animView.getBackground();
        anim.start();

        //播放音频
        MediaManager.getManager().playSound(localPath,0, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                adapter.currentPlayView = null;
                if (isSend){
                    adapter.animView.setBackgroundResource(R.drawable.mp_ic_voice_send_anim_3);
                }else{
                    adapter.animView.setBackgroundResource(R.drawable.mp_ic_voice_recv_anim_3);
                }

            }
        });

    }


    private void ackMessage(EMMessage message) {
        EMMessage.ChatType chatType = message.getChatType();
        if (!message.isAcked() && chatType == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
        if (!message.isListened()) {
            voiceChatRow.readStatusView.setVisibility(View.GONE);
            EMClient.getInstance().chatManager().setVoiceMessageListened(message);
        }
    }
}
