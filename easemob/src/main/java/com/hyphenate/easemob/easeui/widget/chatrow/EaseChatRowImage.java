package com.hyphenate.easemob.easeui.widget.chatrow;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.utils.EaseImageUtils;
import com.hyphenate.easemob.easeui.widget.BubbleImageView;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;

import java.io.File;

public class EaseChatRowImage extends EaseChatRowFile {

    //    protected ImageView imageView;
    protected BubbleImageView bImageView;
    private EMImageMessageBody imgBody;


    public EaseChatRowImage(Context context, EMMessage message, int position, EaseMessageAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_picture : R.layout.ease_row_sent_picture, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = (TextView) findViewById(R.id.percentage);
        bImageView = (BubbleImageView) findViewById(R.id.image);
    }


    @Override
    protected void onSetUpView() {
        imgBody = (EMImageMessageBody) message.getBody();

//        EMImageMessageBody messageBody = (EMImageMessageBody) message.getBody();
//        int width = messageBody.getWidth();
//        int height = messageBody.getHeight();
//        int pxValue = EaseCommonUtils.convertDip2Px(getContext(), 140);
//        int minPxValue = EaseCommonUtils.convertDip2Px(getContext(), 50);
//        int maxValue = Math.max(width, height);
//
//        if (maxValue > 0){
//            double ratio = pxValue * 1.0 / maxValue;
//            ViewGroup.LayoutParams layoutParams = bImageView.getLayoutParams();
//            int realWidth = (int) (width * ratio);
//            int realHeight = (int) (height * ratio);
//
//            layoutParams.width = Math.max(realWidth, minPxValue);
//            layoutParams.height = Math.max(realHeight, minPxValue);
//            bImageView.setLayoutParams(layoutParams);
//        } else if (maxValue == 0){
//            ViewGroup.LayoutParams layoutParams = bImageView.getLayoutParams();
//            layoutParams.width = pxValue;
//            layoutParams.height = minPxValue;
//            bImageView.setLayoutParams(layoutParams);
//            bImageView.setScaleType(ImageView.ScaleType.FIT_START);
//        }

        // received messages
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            return;
        }

        String filePath = imgBody.getLocalUrl();
        String thumbPath = EaseImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());


        showImageView(thumbPath, filePath, message);
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
        if (msg.direct() == EMMessage.Direct.SEND) {
            if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                super.onViewUpdate(msg);
                switch (msg.status()) {
                    case FAIL:
                        MPLog.e("EaseChatRowImage", "FAIL:" + msg.getMsgId());
                        break;
                    case CREATE:
                        MPLog.e("EaseChatRowImage", "CREATE:" + msg.getMsgId());
                        break;
                    case INPROGRESS:
                        MPLog.e("EaseChatRowImage", "INPROGRESS:" + msg.getMsgId());
                        break;
                    case SUCCESS:
                        MPLog.e("EaseChatRowImage", "SUCCESS:" + msg.getMsgId());
                        break;
                }
            } else {
                if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                        imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING ||
                        imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED) {
                    progressBar.setVisibility(View.INVISIBLE);
                    percentageView.setVisibility(View.INVISIBLE);
                    bImageView.setImageResource(R.drawable.ease_default_image);
                } else {
                    progressBar.setVisibility(View.GONE);
                    percentageView.setVisibility(View.GONE);
                    bImageView.setImageResource(R.drawable.ease_default_image);
                    String thumbPath = imgBody.thumbnailLocalPath();
                    if (!new File(thumbPath).exists()) {
                        // to make it compatible with thumbnail received in previous version
                        thumbPath = EaseImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
                    }
                    showImageView(thumbPath, imgBody.getLocalUrl(), message);
                }
            }
            return;
        }

        // received messages
        if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
            if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                bImageView.setImageResource(R.drawable.ease_default_image);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                percentageView.setVisibility(View.INVISIBLE);
                bImageView.setImageResource(R.drawable.ease_default_image);
            }
        } else if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED) {
//            if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
//                progressBar.setVisibility(View.VISIBLE);
//                percentageView.setVisibility(View.VISIBLE);
//            } else {
//                progressBar.setVisibility(View.INVISIBLE);
//                percentageView.setVisibility(View.INVISIBLE);
//            }
            progressBar.setVisibility(View.GONE);
            percentageView.setVisibility(View.GONE);
            bImageView.setImageResource(R.drawable.ease_default_image);
            String thumbPath = imgBody.thumbnailLocalPath();
            if (!new File(thumbPath).exists()) {
                // to make it compatible with thumbnail received in previous version
                thumbPath = EaseImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
            }
            showImageView(thumbPath, imgBody.getLocalUrl(), message);
        } else {
            progressBar.setVisibility(View.GONE);
            percentageView.setVisibility(View.GONE);
            bImageView.setImageResource(R.drawable.ease_default_image);
            String thumbPath = imgBody.thumbnailLocalPath();
            if (!new File(thumbPath).exists()) {
                // to make it compatible with thumbnail received in previous version
                thumbPath = EaseImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
            }
            showImageView(thumbPath, imgBody.getLocalUrl(), message);
        }
    }

    /**
     * load image into image view
     */
    private void showImageView(final String thumbernailPath, final String localFullSizePath, final EMMessage message) {

        String imagePath = null;
        if (localFullSizePath != null && new File(localFullSizePath).exists() && new File(localFullSizePath).canRead()) {
            imagePath = localFullSizePath;
        } else if (thumbernailPath != null && new File(thumbernailPath).exists()) {
            imagePath = thumbernailPath;
        } else if (!TextUtils.isEmpty(imgBody.getThumbnailUrl())) {
            imagePath = imgBody.getThumbnailUrl();
        } else if (!TextUtils.isEmpty(imgBody.getRemoteUrl())) {
            imagePath = imgBody.getRemoteUrl();
        }
        if (imagePath == null) {
            return;
        }
        Glide.with(getContext().getApplicationContext()).load(imagePath)
                .apply(RequestOptions.placeholderOf(R.drawable.ease_default_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                ).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                int width = resource.getIntrinsicWidth();
                int height = resource.getIntrinsicHeight();
                int pxValue = getResources().getDimensionPixelSize(R.dimen.dp_140);
                int maxValue = Math.max(width, height);
                int minPxValue = getResources().getDimensionPixelSize(R.dimen.dp_50);
                double ratio = pxValue * 1.0 / maxValue;
                ViewGroup.LayoutParams layoutParams = bImageView.getLayoutParams();
                int realWidth = (int) (width * ratio);
                int realHeight = (int) (height * ratio);

                layoutParams.width = Math.max(realWidth, minPxValue);
                layoutParams.height = Math.max(realHeight, minPxValue);
                bImageView.setLayoutParams(layoutParams);
                bImageView.setImageDrawable(resource);
            }
        });

//        if (localFullSizePath != null && new File(localFullSizePath).exists() && new File(localFullSizePath).canRead()){
//            GlideUtils.loadFromFile(activity, localFullSizePath, R.drawable.ease_default_image, bImageView);
//        }else if (!TextUtils.isEmpty(imgBody.getThumbnailUrl())){
//            GlideUtils.loadFromRemote(activity, imgBody.getThumbnailUrl(), R.drawable.ease_default_image, bImageView);
//        }else if (thumbernailPath != null && new File(thumbernailPath).exists()){
//            GlideUtils.loadFromFile(activity, thumbernailPath, R.drawable.ease_default_image, bImageView);
//        }

    }

}
