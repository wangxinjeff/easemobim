package com.hyphenate.easemob.easeui.widget.chatrow;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.model.EaseDingMessageHelperV2;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.easeui.utils.EaseSmileUtils;
import com.hyphenate.easemob.easeui.widget.textview.AlignTextView;
import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class EaseChatRowText extends EaseChatRow {

    private TextView contentView;
    private View referenceBubble;
    private View referenceBgView;
    private AlignTextView referenceContent;
    private ImageView referenceImg;
    private ImageView referenceVideo;

    public EaseChatRowText(Context context, EMMessage message, int position, EaseMessageAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_message : R.layout.ease_row_sent_message, this);
    }

    boolean upReturn = true;

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(R.id.tv_chatcontent);
        referenceBubble = findViewById(R.id.reference_bubble);
        referenceBgView = findViewById(R.id.reference_bg);
        referenceContent =findViewById(R.id.reference_content);
        referenceImg = findViewById(R.id.reference_img);
        referenceVideo = findViewById(R.id.reference_video);
        contentView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (bubbleLayout != null) {
                    bubbleLayout.performLongClick();
                }
                upReturn = true;
                return false;
            }
        });
        contentView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    return upReturn;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    upReturn = false;
                }
                return false;
            }
        });
        contentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bubbleLayout != null) {
                    bubbleLayout.callOnClick();
                }
            }
        });
//        contentView.setMovementMethod(new LinkMovementClickMethod());
//        contentView.setOnLongClickListener(new OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (bubbleLayout != null){
//                    boolean isLongClick = bubbleLayout.performLongClick();
//                    return isLongClick;
//                }
//            }
//        });
    }

    @Override
    public void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        if (txtBody == null) {
            EMLog.e(TAG, "txtBody is null ->" + message.toString());
            return;
        }

        String content = txtBody.getMessage();
        content = content.replace("&nbsp;", " ");
        if (content.contains("&lt;") && content.contains("&gt;")) {
            content = Html.fromHtml(Html.fromHtml(content).toString()).toString();
        } else if (content.contains("<html>") ||
                content.contains("<header>") ||
                content.contains("<body>") ||
                content.contains("<div>") ||
                content.contains("<a>") ||
                content.contains("<h>") ||
                content.contains("<ul>") ||
                content.contains("<li>") ||
                content.contains("<span>") ||
                content.contains("<strong>") ||
                content.contains("<b>") ||
                content.contains("<em>") ||
                content.contains("<cite>") ||
                content.contains("<dfn>") ||
                content.contains("<i>") ||
                content.contains("<big>") ||
                content.contains("<small>") ||
                content.contains("<font>") ||
                content.contains("<blockquote>") ||
                content.contains("<tt>") ||
                content.contains("<u>") ||
                content.contains("<del>") ||
                content.contains("<s>") ||
                content.contains("<strike>") ||
                content.contains("<sub>") ||
                content.contains("<sup>") ||
                content.contains("<img>") ||
                content.contains("<h1>") ||
                content.contains("<h2>") ||
                content.contains("<h3>") ||
                content.contains("<h4>") ||
                content.contains("<h5>") ||
                content.contains("<h6>") ||
                content.contains("<p>")) {

            content = Html.fromHtml(content).toString();
        }

        Spannable span = EaseSmileUtils.getSmiledText(context, content);
        // 设置内容
        contentView.setText(span, BufferType.SPANNABLE);
        int currentProgress = PreferenceUtils.getInstance().getTextSizeProgress();
        float x = 0.1f * (currentProgress - 2) + 1;
        contentView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dp_15) * x);

        // 处理引用消息
        JSONObject json = null;
        try {
            json = message.getJSONObjectAttribute(EaseConstant.MSG_EXT_REFERENCE_MSG);
            if(json != null){
                String msgId = json.optString(EaseConstant.MSG_EXT_REFERENCE_MSG_ID);
                String msgType = json.optString(EaseConstant.MSG_EXT_REFERENCE_MSG_TYPE);
                String msgNick = json.optString(EaseConstant.MSG_EXT_REFERENCE_MSG_NICK);
                String msgContent = json.optString(EaseConstant.MSG_EXT_REFERENCE_MSG_CONTENT);
                final EMMessage referenceMsg = EMClient.getInstance().chatManager().getMessage(msgId);
                referenceBubble.setVisibility(VISIBLE);
                String text = "";
                switch (msgType) {
                    case EaseConstant.REFERENCE_MSG_TYPE_TXT:
                        referenceImg.setVisibility(GONE);
                        referenceVideo.setVisibility(GONE);
                        text = msgNick + ":" + msgContent;
                        Spannable spannable = EaseSmileUtils.getSmiledText(context, text);
                        referenceContent.setText(spannable, BufferType.SPANNABLE);
//                        referenceContent.setText(text);
                        break;
                    case EaseConstant.REFERENCE_MSG_TYPE_NAME_CARD:
                        referenceImg.setVisibility(GONE);
                        referenceVideo.setVisibility(GONE);
                        text = msgNick + ":" + String.format(context.getString(R.string.ease_name_card), msgContent);
                        referenceContent.setText(text);
                        break;
//                    case EaseConstant.REFERENCE_MSG_TYPE_READ_BURN:
//                        referenceImg.setVisibility(GONE);
//                        referenceVideo.setVisibility(GONE);
//                        text = msgNick + ":" + context.getString(R.string.burn_message);
//                        referenceContent.setText(text);
//                        break;
                    case EaseConstant.REFERENCE_MSG_TYPE_VIDEO:
                        referenceImg.setVisibility(VISIBLE);
                        referenceImg.setScaleType(ImageView.ScaleType.CENTER);
                        text = msgNick + ":";
                        referenceContent.setText(text);
                        if(referenceMsg != null){
                            referenceVideo.setVisibility(VISIBLE);
                            EMVideoMessageBody videoBody = (EMVideoMessageBody) referenceMsg.getBody();
                            String localThumb = videoBody.getLocalThumb();
                            if (localThumb != null && new File(localThumb).exists() && new File(localThumb).canRead()) {
                                Glide.with(context).load(localThumb)
                                        .apply(RequestOptions.placeholderOf(R.drawable.ease_default_image)
                                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                                        .into(referenceImg);
                            }
                        } else {
                            referenceVideo.setVisibility(GONE);
                        }
                        break;
                    case EaseConstant.REFERENCE_MSG_TYPE_VOICE:
                        referenceImg.setVisibility(GONE);
                        referenceVideo.setVisibility(GONE);
                        text = msgNick + ":" + context.getString(R.string.voice_prefix) + msgContent;
                        referenceContent.setText(EaseSmileUtils.getIconText(context, text), BufferType.SPANNABLE);
                        break;
                    case EaseConstant.REFERENCE_MSG_TYPE_IMAGE:
                        referenceImg.setVisibility(VISIBLE);
                        referenceImg.setScaleType(ImageView.ScaleType.CENTER);
                        referenceVideo.setVisibility(GONE);
                        text = msgNick + ":";
                        referenceContent.setText(text);
                        if(referenceMsg != null){
                            EMImageMessageBody imgBody = (EMImageMessageBody) referenceMsg.getBody();
                            String filePath = imgBody.getLocalUrl();
                            String thumbPath = imgBody.thumbnailLocalPath();
                            String imagePath = null;
                            if (new File(thumbPath).exists()) {
                                imagePath = thumbPath;
                            } else if (filePath != null && new File(filePath).exists() && new File(filePath).canRead()) {
                                imagePath = filePath;
                            } else if (!TextUtils.isEmpty(imgBody.getThumbnailUrl())) {
                                imagePath = imgBody.getThumbnailUrl();
                            } else if (!TextUtils.isEmpty(imgBody.getRemoteUrl())) {
                                imagePath = imgBody.getRemoteUrl();
                            }
                            if (imagePath == null) {
                                return;
                            }
                            Glide.with(context).load(imagePath)
                                    .apply(RequestOptions.placeholderOf(R.drawable.reference_default_image)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                                    .into(referenceImg);
                        } else {

                        }
                        break;
                    case EaseConstant.REFERENCE_MSG_TYPE_FILE:
                        referenceImg.setVisibility(VISIBLE);
                        referenceImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        referenceVideo.setVisibility(GONE);
                        text = msgNick + ":" + context.getString(R.string.file) + msgContent;
                        referenceContent.setText(text);
                        if(referenceMsg != null){
                            String fileName = EaseCommonUtils.getMessageDigest(referenceMsg, context, true);
                            referenceImg.setImageDrawable(ContextCompat.getDrawable(context, EaseCommonUtils.getFileIconRes(fileName)));
                        } else {

                        }
                        break;
                    case EaseConstant.REFERENCE_MSG_TYPE_LOCATION:
                        referenceImg.setVisibility(VISIBLE);
                        referenceImg.setScaleType(ImageView.ScaleType.CENTER);
                        referenceVideo.setVisibility(GONE);
                        text = msgNick + ":" + context.getString(R.string.location_loc) + msgContent;
                        referenceContent.setText(EaseSmileUtils.getIconText(context, text), BufferType.SPANNABLE);
                        if(referenceMsg != null){
                            referenceImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ease_location_image));
                        } else {

                        }
                        break;
                    case EaseConstant.REFERENCE_MSG_TYPE_RECALL:
                        referenceImg.setVisibility(GONE);
                        referenceVideo.setVisibility(GONE);
                        referenceContent.setText(msgContent);
                        break;
                }
                referenceBgView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(itemClickListener != null){
                            itemClickListener.onReferenceMsgClick(referenceMsg);
                        }
                    }
                });
            } else {
                referenceBubble.setVisibility(GONE);
            }
        } catch (HyphenateException e) {
//            e.printStackTrace();
            referenceBubble.setVisibility(GONE);
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

    public void onAckUserUpdate(final int count) {
        if (ackedView != null) {
            ackedView.post(new Runnable() {
                @Override
                public void run() {
                    ackedView.setVisibility(VISIBLE);
                    ackedView.setText(String.format(getContext().getString(R.string.group_ack_read_count), count));
                }
            });
        }
    }

    private void onMessageCreate() {
        progressBar.setVisibility(View.GONE);
        statusView.setVisibility(View.VISIBLE);
    }

    private void onMessageSuccess() {
        progressBar.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);
        // Show "1 Read" if this msg is a ding-type msg.
        if (EaseDingMessageHelperV2.get().isDingMessage(message) && ackedView != null) {
            ackedView.setVisibility(VISIBLE);
            int count = message.groupAckCount();
            ackedView.setText(String.format(getContext().getString(R.string.group_ack_read_count), count));
        }

        // Set ack-user list change listener.
        EaseDingMessageHelperV2.get().setUserUpdateListener(message, userUpdateListener);
    }

    private void onMessageError() {
        progressBar.setVisibility(View.GONE);
        statusView.setVisibility(View.VISIBLE);
    }

    private void onMessageInProgress() {
        progressBar.setVisibility(View.VISIBLE);
        statusView.setVisibility(View.GONE);
    }

    private EaseDingMessageHelperV2.IAckUserUpdateListener userUpdateListener =
            new EaseDingMessageHelperV2.IAckUserUpdateListener() {
                @Override
                public void onUpdate(List<String> list) {
                    onAckUserUpdate(list.size());
                }
            };
}
