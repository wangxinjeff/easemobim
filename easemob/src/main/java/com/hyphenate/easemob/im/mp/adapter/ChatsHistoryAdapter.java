package com.hyphenate.easemob.im.mp.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.utils.EaseSmileUtils;
import com.hyphenate.easemob.im.mp.entity.history.HFileMessageBody;
import com.hyphenate.easemob.im.mp.entity.history.HImageMessageBody;
import com.hyphenate.easemob.im.mp.entity.history.HLocationMessageBody;
import com.hyphenate.easemob.im.mp.entity.history.HMessage;
import com.hyphenate.easemob.im.mp.entity.history.HMessageBody;
import com.hyphenate.easemob.im.mp.entity.history.HTextMessageBody;
import com.hyphenate.easemob.im.mp.entity.history.HVideoMessageBody;
import com.hyphenate.easemob.im.mp.entity.history.HVoiceMessageBody;
import com.hyphenate.easemob.R;
import com.hyphenate.util.DateUtils;
import com.hyphenate.util.TextFormater;

import java.util.Date;
import java.util.List;

/**
 * recyclerView上拉加载适配器,聊天记录
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 19/11/2018
 */
public class ChatsHistoryAdapter extends BaseMultiItemQuickAdapter<HMessage, BaseViewHolder> {
    private static final String TAG = "ChatsHistoryAdapter";
    private Context context;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ChatsHistoryAdapter(Context context, List<HMessage> data) {
        super(data);
        this.context = context;
        addItemType(HMessage.ITEM_TYPE_TXT, R.layout.item_chats_list_history);
        addItemType(HMessage.ITEM_TYPE_TXT_CARD, R.layout.item_chats_list_txt_card);
        addItemType(HMessage.ITEM_TYPE_IMAGE, R.layout.item_chats_h_image);
        addItemType(HMessage.ITEM_TYPE_FILE, R.layout.item_chats_h_file);
        addItemType(HMessage.ITEM_TYPE_AUDIO, R.layout.item_chats_h_voice);
        addItemType(HMessage.ITEM_TYPE_VIDEO, R.layout.item_chats_h_video);
        addItemType(HMessage.ITEM_TYPE_LOCATION, R.layout.item_chats_h_location);
        addItemType(HMessage.ITEM_TYPE_DEFAULT, R.layout.item_chats_list_history);
    }

    /**
     * iv_video = itemView.findViewById(R.id.iv_video);
     * rl_video = itemView.findViewById(R.id.rl_video);
     *
     * @param helper
     * @param item
     */

    @Override
    protected void convert(BaseViewHolder helper, HMessage item) {
        helper.setText(R.id.name, item.getFrom());
        AvatarUtils.setAvatarContent(context.getApplicationContext(), item.getFrom(), item.getAvatar(), helper.getView(R.id.iv_avatar));
        //日期、时间
        String timeStr = DateUtils.getTimestampString(new Date(item.getTimestamp()));
        helper.setText(R.id.time, timeStr);
        HMessageBody messageBody = item.getMessageBody();
        if (messageBody == null) {
            return;
        }
        if (messageBody instanceof HTextMessageBody) {
            HTextMessageBody textMessageBody = (HTextMessageBody) messageBody;

            if ("card".equals(item.getType())) {
                helper.setText(R.id.tv_card_name, textMessageBody.getRealName());
                GlideUtils.load(context, textMessageBody.getUserAvatar(), R.drawable.default_image, helper.getView(R.id.iv_card_avatar));
            } else {
                helper.setText(R.id.message, EaseSmileUtils.getSmiledText(context, textMessageBody.getContent()));
            }
        } else if (messageBody instanceof HImageMessageBody) {
            HImageMessageBody imgMsgBody = (HImageMessageBody) messageBody;
            String remoteUrl = imgMsgBody.getRemoteUrl();
            GlideUtils.loadFromRemote(context, remoteUrl, R.drawable.ease_default_image, helper.getView(R.id.iv_image));
        } else if (messageBody instanceof HFileMessageBody) {
            HFileMessageBody fileMsgBody = (HFileMessageBody) messageBody;
            String remoteUrl = fileMsgBody.getRemoteUrl();
            String displayName = fileMsgBody.getDisplayName();
            long fileSize = fileMsgBody.getFileLength();
            helper.setText(R.id.tv_file_name, displayName);
            helper.setText(R.id.tv_file_size, TextFormater.getDataSize(fileSize));
        } else if (messageBody instanceof HLocationMessageBody) {
            HLocationMessageBody locMsgBody = (HLocationMessageBody) messageBody;
            String address = locMsgBody.getAddress();
            double lat = locMsgBody.getLat();
            double lng = locMsgBody.getLng();
            try {
                helper.setText(R.id.tv_location, address);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (messageBody instanceof HVoiceMessageBody) {
            HVoiceMessageBody voiceMessageBody = (HVoiceMessageBody) messageBody;
            int duration = voiceMessageBody.getDuration();
            String remoteUrl = voiceMessageBody.getRemoteUrl();
            helper.setText(R.id.tv_audio_duration, duration + "\"");
        } else if (messageBody instanceof HVideoMessageBody) {
            HVideoMessageBody videoMessageBody = (HVideoMessageBody) messageBody;
            GlideUtils.load(context, videoMessageBody.getThumb_url(), R.drawable.default_image, helper.getView(R.id.iv_video_image));
        } else {
            try {
                helper.setText(R.id.message, "[特殊消息]");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
