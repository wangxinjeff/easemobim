package com.hyphenate.easemob.im.mp.ui;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.hyphenate.easemob.easeui.player.MediaManager;
import com.hyphenate.easemob.easeui.ui.EaseShowBigImageActivity;
import com.hyphenate.easemob.easeui.ui.EaseShowNormalFileActivity;
import com.hyphenate.easemob.easeui.ui.EaseShowVideoActivity;
import com.hyphenate.easemob.easeui.widget.EaseTitleBar;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.im.mp.adapter.ChatsHistoryAdapter;
import com.hyphenate.easemob.im.mp.entity.history.HFileMessageBody;
import com.hyphenate.easemob.im.mp.entity.history.HImageMessageBody;
import com.hyphenate.easemob.im.mp.entity.history.HLocationMessageBody;
import com.hyphenate.easemob.im.mp.entity.history.HMessage;
import com.hyphenate.easemob.im.mp.entity.history.HMessageBody;
import com.hyphenate.easemob.im.mp.entity.history.HTextMessageBody;
import com.hyphenate.easemob.im.mp.entity.history.HVideoMessageBody;
import com.hyphenate.easemob.im.mp.entity.history.HVoiceMessageBody;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;
import com.hyphenate.easemob.im.officeautomation.ui.ContactDetailsActivity;
import com.hyphenate.easemob.im.officeautomation.ui.EMBaiduMapActivity;
import com.hyphenate.easemob.im.officeautomation.utils.CommonUtils;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.imlibs.mp.utils.MPPathUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 展示聊天记录页面
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 19/11/2018
 */
public class ChatsHistoryActivity extends BaseActivity {
    private String TAG = "ChatsHistoryActivity";
    private EaseTitleBar title_bar;
    private RecyclerView rv;
    private List<HMessage> chatList = new ArrayList<>();
    private ChatsHistoryAdapter chatHistoryAdapter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat_history);
        initViews();
        initListeners();
        initData();
    }

    private void initViews() {
        title_bar = findViewById(R.id.title_bar);
        rv = findViewById(R.id.rv);
    }

    private void initListeners() {

        title_bar.setLeftLayoutClickListener(view -> finish());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
//        rv.addItemDecoration(new SimpleDividerItemDecoration(this));
        chatHistoryAdapter = new ChatsHistoryAdapter(this, chatList);
        rv.setAdapter(chatHistoryAdapter);
        chatHistoryAdapter.setEnableLoadMore(false);

        chatHistoryAdapter.setOnItemClickListener((adapter, view, position) -> {
            HMessage message = (HMessage) adapter.getItem(position);
            if (message == null) {
                return;
            }
            HMessageBody messageBody = message.getMessageBody();
            if (messageBody == null) {
                return;
            }
            if (messageBody instanceof HTextMessageBody) {
                if ("card".equals(message.getType())) {
                    Intent intent = new Intent(ChatsHistoryActivity.this, ContactDetailsActivity.class);
                    intent.putExtra("userId", ((HTextMessageBody) messageBody).getUserId());
                    startActivity(intent);
                }

            } else if (messageBody instanceof HLocationMessageBody) {
                HLocationMessageBody locMsgBody = (HLocationMessageBody) messageBody;
                Intent intent = new Intent(ChatsHistoryActivity.this, EMBaiduMapActivity.class);
                intent.putExtra("latitude", locMsgBody.getLat());
                intent.putExtra("longitude", locMsgBody.getLng());
                intent.putExtra("address", locMsgBody.getAddress());
                startActivity(intent);
            } else if (messageBody instanceof HImageMessageBody) {
                HImageMessageBody imgMsgBody = (HImageMessageBody) messageBody;
                String remoteUrl = imgMsgBody.getRemoteUrl();
                Intent intent = new Intent(ChatsHistoryActivity.this, EaseShowBigImageActivity.class);
                intent.putExtra("remote_url", remoteUrl);
                startActivity(intent);
            } else if (messageBody instanceof HFileMessageBody) {
                HFileMessageBody fileMsgBody = (HFileMessageBody) messageBody;
                String remoteUrl = fileMsgBody.getRemoteUrl();
                Intent intent = new Intent(ChatsHistoryActivity.this, EaseShowNormalFileActivity.class);
                intent.putExtra("remote_url", remoteUrl);
                intent.putExtra("display_name", fileMsgBody.getDisplayName());
                startActivity(intent);
            } else if (messageBody instanceof HVideoMessageBody) {
                HVideoMessageBody videoMessageBody = (HVideoMessageBody) messageBody;
                String remoteUrl = videoMessageBody.getRemote_url();
                Intent intent = new Intent(ChatsHistoryActivity.this, EaseShowVideoActivity.class);
                intent.putExtra("remote_url", remoteUrl);
                startActivity(intent);
            } else if (messageBody instanceof HVoiceMessageBody) {
                HVoiceMessageBody voiceMessageBody = (HVoiceMessageBody) messageBody;
                downloadFile(voiceMessageBody.getRemoteUrl(), view.findViewById(R.id.iv_audio_play));
            }
        });

    }

    private void initData() {
        String extMsg = getIntent().getStringExtra(Constant.EXT_EXTMSG);
        if (TextUtils.isEmpty(extMsg)) {
            finish();
            return;
        }
        parseMessages(extMsg);
    }


    private void parseMessages(String extMsg) {
        try {
            JSONObject jsonExtMsg = new JSONObject(extMsg);
            String title = jsonExtMsg.optString("title");
            String type = jsonExtMsg.optString("type");

            title_bar.setTitle(title);
            if ("chatMsgs".equalsIgnoreCase(type)) {
                List<HMessage> messageList = new ArrayList<>();
                JSONArray contentArr = jsonExtMsg.optJSONArray("contents");
                for (int i = 0; i < contentArr.length(); i++) {
                    JSONObject jsonMsg = contentArr.getJSONObject(i);
                    messageList.add(parseMessage(jsonMsg));
                }
                chatList.addAll(messageList);
                chatHistoryAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private HMessage parseMessage(JSONObject jsonMsg) {
        String type = jsonMsg.optString("type");
        String nick = jsonMsg.optString("nick");
        String avatar = jsonMsg.optString("avatar");
        long timestamp = jsonMsg.optLong("timestamp", 0);
        HMessage message = new HMessage();
        message.setFrom(nick);
        message.setAvatar(avatar);
        message.setTimestamp(timestamp);
        message.setType(type);
        if ("txt".equalsIgnoreCase(type)) {
            String content = jsonMsg.optString("msg");
            HTextMessageBody txtMsgBody = new HTextMessageBody();
            txtMsgBody.setContent(content);
            try {
                JSONObject extMsg = new JSONObject(jsonMsg.getString("extMsg"));
                if ("people_card".equals(extMsg.getString("type"))) {
                    txtMsgBody.setImUserId(extMsg.getJSONObject("content").getString("im_user_id"));
                    txtMsgBody.setUserAvatar(extMsg.getJSONObject("content").getString("user_avatar"));
                    txtMsgBody.setUserId(extMsg.getJSONObject("content").getInt("user_id"));
                    txtMsgBody.setRealName(extMsg.getJSONObject("content").getString("realName"));
                    message.setType("card");
                } else {
                    message.setType("txt");
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.setType("txt");
            }

            message.setMessageBody(txtMsgBody);
        } else if ("image".equalsIgnoreCase(type)) {
            String remoteUrl = jsonMsg.optString("remote_url");
            String thumbUrl = jsonMsg.optString("thumb_url");
            HImageMessageBody imgMsgBody = new HImageMessageBody();
            imgMsgBody.setRemoteUrl(remoteUrl);
            imgMsgBody.setThumbnailUrl(thumbUrl);
            message.setMessageBody(imgMsgBody);
        } else if ("file".equalsIgnoreCase(type)) {
            String remoteUrl = jsonMsg.optString("remote_url");
            String displayName = jsonMsg.optString("display_name");
            long fileSize = jsonMsg.optLong("file_size");
            HFileMessageBody fileMsgBody = new HFileMessageBody();
            fileMsgBody.setDisplayName(displayName);
            fileMsgBody.setRemoteUrl(remoteUrl);
            fileMsgBody.setFileLength(fileSize);
            message.setMessageBody(fileMsgBody);
        } else if ("location".equalsIgnoreCase(type)) {
            String address = jsonMsg.optString("addr");
            double lat = jsonMsg.optDouble("lat");
            double lng = jsonMsg.optDouble("lng");
            HLocationMessageBody locationMessageBody = new HLocationMessageBody();
            locationMessageBody.setAddress(address);
            locationMessageBody.setLat(lat);
            locationMessageBody.setLng(lng);
            message.setMessageBody(locationMessageBody);
        } else if ("voice".equalsIgnoreCase(type)) {
            int duration = jsonMsg.optInt("duration");
            String remoteUrl = jsonMsg.optString("remote_url");
            HVoiceMessageBody voiceMessageBody = new HVoiceMessageBody();
            voiceMessageBody.setDuration(duration);
            voiceMessageBody.setRemoteUrl(remoteUrl);
            message.setMessageBody(voiceMessageBody);
        } else if ("video".equalsIgnoreCase(type)) {
            String remoteUrl = jsonMsg.optString("remote_url");
            String thumbUrl = jsonMsg.optString("thumb_url");
            HVideoMessageBody videoMessageBody = new HVideoMessageBody();
            videoMessageBody.setRemote_url(remoteUrl);
            videoMessageBody.setThumb_url(thumbUrl);
            message.setMessageBody(videoMessageBody);

        } else {
            String content = "[特殊消息]";
            HTextMessageBody txtMsgBody = new HTextMessageBody();
            txtMsgBody.setContent(content);
            message.setMessageBody(txtMsgBody);
        }

        return message;
    }

    private void downloadFile(String remoteUrl, ImageView animView) {
        File voiceDirFile = MPPathUtil.getInstance().getVoicePath();

        if (voiceDirFile == null) {
            MPLog.e(TAG, "voiceDirFile is null");
            return;
        }
        String fileName = CommonUtils.getMd5Hash(remoteUrl);
        if (fileName == null) {
            MPLog.e(TAG, "file Name is null");
            return;
        }
        File voiceFile = new File(voiceDirFile, fileName);
        if (voiceFile.exists() && voiceFile.isFile()) {
            playLocalVoice(voiceFile.getPath(), animView);
            return;
        }
        EMAPIManager.getInstance().downloadFile(remoteUrl, voiceFile.getPath(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playLocalVoice(voiceFile.getPath(), animView);
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getString(R.string.tip_audio_download_failed), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void playLocalVoice(String localVoicePath, ImageView animView) {

        if (MediaManager.getManager().isPlaying()) {
            animView.setImageResource(R.drawable.ease_chatfrom_voice_playing);
            MediaManager.getManager().release();
            return;
        }

        animView.setImageResource(R.drawable.voice_from_icon);

        AnimationDrawable animationDrawable = (AnimationDrawable) animView.getDrawable();
        animationDrawable.start();

        MediaManager.getManager().playSound(localVoicePath, 0, mp -> {
            if (isFinishing()) {
                return;
            }
            animationDrawable.stop();
            animView.setImageResource(R.drawable.ease_chatfrom_voice_playing);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.getManager().release();
    }
}
