/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easemob.easeui.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.easeui.utils.EaseImageUtils;
import com.hyphenate.easemob.easeui.utils.EncryptUtils;
import com.hyphenate.easemob.easeui.widget.dragphotoview.DragPhotoView;
import com.hyphenate.easemob.easeui.widget.dragphotoview.zoomlayout.ZoomRelativeLayout;
import com.hyphenate.easemob.easeui.widget.photoview.PhotoViewAttacher;
import com.hyphenate.easemob.imlibs.message.MessageUtils;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.events.MessageChanged;
import com.hyphenate.easemob.imlibs.mp.utils.MPPathUtil;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.eventbus.MPEventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * download and show original image
 */
public class EaseShowBigImageActivity extends EaseBaseActivity {
    private static final String TAG = "ShowBigImage";
    private DragPhotoView mPhotoView;
    private ViewPager viewPager;
    private List<ImageEntity> imgList = new ArrayList<>();
    private ImagesAdapter adapter;
    private int default_res = R.drawable.ease_default_image;
    private String localFilePath;
    private String remoteUrl;
    private ImageView ivSave;
    private Activity instance;
    private ProgressBar loadLocalPb;
    private String destroyMsgId;
    private boolean isDownloadSuccessed;
    private EMMessage message;
    private int currPosition = 0;
    private int pos = 0;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ease_activity_show_big_image);
        instance = this;
        Intent gIntent = getIntent();
        if (gIntent == null) {
            finish();
            return;
        }
        initViews();
        default_res = gIntent.getIntExtra("default_image", R.drawable.ease_default_image);
        String msgId = gIntent.getStringExtra("messageId");
        String convId = gIntent.getStringExtra("convId");
        destroyMsgId = gIntent.getStringExtra(EaseConstant.BURN_AFTER_READING_DESTORY_MSGID);
        remoteUrl = gIntent.getStringExtra("remote_url");

        if (!TextUtils.isEmpty(destroyMsgId)) {
            ivSave.setVisibility(View.GONE);
        }

        //聊天界面多少图片
//        if (convId != null) {
//            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(convId);
//            for (int i = 0; i < conversation.getAllMessages().size(); i++) {
//                message = conversation.getAllMessages().get(i);
//                if (message.getType() == EMMessage.Type.IMAGE) {
//
//                    if (message.getBooleanAttribute(EaseConstant.BURN_AFTER_READING_READED, false)) {
//                        continue;
//                    }
//                    pos++;
//                    if (message.getMsgId().equals(msgId))
//                        currPosition = pos - 1;
//                    EMImageMessageBody body = (EMImageMessageBody) message.getBody();
//
//                    ImageEntity imageEntity = new ImageEntity();
//                    imageEntity.setBurn(false);
//
//                    if (body.getLocalUrl() != null && new File(body.getLocalUrl()).exists()) {
//                        imageEntity.setImagePath(body.getLocalUrl());
//                        imgList.add(imageEntity);
//                    } else if (!TextUtils.isEmpty(body.getRemoteUrl())) {
//                        imageEntity.setImagePath(body.getRemoteUrl());
//                        imgList.add(imageEntity);
//                    } else {
//                        imageEntity.setImagePath("none");
//                        imgList.add(imageEntity);
//                    }
//                }
//            }
//            adapter.notifyDataSetChanged();
//            viewPager.setCurrentItem(currPosition);
//
//            //阅后即焚图片
//        } else
            if (!TextUtils.isEmpty(msgId)) {
                message = EMClient.getInstance().chatManager().getMessage(msgId);
                final EMImageMessageBody imgBody = (EMImageMessageBody) message.getBody();
                final ImageEntity entity = new ImageEntity();
                entity.setBurn(!TextUtils.isEmpty(destroyMsgId));

                String localUrl = imgBody.getLocalUrl();
                remoteUrl = imgBody.getRemoteUrl();

                if (localUrl != null && new File(localUrl).exists()) {
                    entity.setImagePath(imgBody.getLocalUrl());
                    imgList.add(entity);
                    adapter.notifyDataSetChanged();

                } else if (!TextUtils.isEmpty(remoteUrl)) {
                    loadLocalPb.setVisibility(View.VISIBLE);
                    message.setMessageStatusCallback(new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        if (isFinishing()) {
                            return;
                        }
                        isDownloadSuccessed = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadLocalPb.setVisibility(View.GONE);
                                entity.setImagePath(imgBody.getLocalUrl());
                                imgList.add(entity);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                        if (isFinishing()) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadLocalPb.setVisibility(View.GONE);
                                entity.setImagePath("none");
                                imgList.add(entity);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onProgress(final int i, String s) {
                        if (isFinishing()) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadLocalPb.setProgress(i);
                            }
                        });
                    }
                });

                EMClient.getInstance().chatManager().downloadAttachment(message);
            }

        } else if (!TextUtils.isEmpty(remoteUrl)) {
            final ImageEntity entity = new ImageEntity();
            entity.setBurn(false);
            if (localFilePath == null) {
                localFilePath = MPPathUtil.getInstance().getImagePath().getPath() + "/" + EncryptUtils.encryptMD5ToString(remoteUrl) + ".jpg";
            }
            File localFile = new File(localFilePath);
            if (localFile.exists() && localFile.canRead()) {
                entity.setImagePath(localFilePath);
                imgList.add(entity);
                adapter.notifyDataSetChanged();
            } else {
                loadLocalPb.setVisibility(View.VISIBLE);
                EMAPIManager.getInstance().downloadFile(remoteUrl, localFilePath, new EMDataCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        entity.setImagePath(localFilePath);
                        imgList.add(entity);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadLocalPb.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        entity.setImagePath("none");
                        imgList.add(entity);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadLocalPb.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }

                    @Override
                    public void onProgress(final int progress) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadLocalPb.setProgress((int) progress);
                            }
                        });
                    }
                });
//                OkHttpUtils.get().url(remoteUrl).build().execute(new FileCallBack(localFile.getParent(), localFile.getName()) {
//                    @Override
//                    public void inProgress(float progress, long total) {
//                        loadLocalPb.setProgress((int) progress);
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e) {
//                        loadLocalPb.setVisibility(View.GONE);
//                        entity.setImagePath("none");
//                        entity.setDownload(false);
//                        imgList.add(entity);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                adapter.notifyDataSetChanged();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onResponse(File response) {
//                        loadLocalPb.setVisibility(View.GONE);
//                        entity.setImagePath(response.getPath());
//                        entity.setDownload(true);
//                        imgList.add(entity);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                adapter.notifyDataSetChanged();
//                            }
//                        });
//                    }
//                });

            }
        } else {
            ImageEntity entity = new ImageEntity();
            entity.setImagePath("none");
            entity.setBurn(false);
            imgList.add(entity);
            ivSave.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                currPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initViews() {
        mPhotoView = findViewById(R.id.image);
        loadLocalPb = findViewById(R.id.pb_load_local);
        viewPager = findViewById(R.id.view_pager);
        ivSave = findViewById(R.id.iv_save);
        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(imgList.get(currPosition).getImagePath())) {
                    EaseImageUtils.saveImgToGallery(EaseShowBigImageActivity.this, imgList.get(currPosition).getImagePath());
                }
            }
        });

        ZoomRelativeLayout zoomParentLayout = findViewById(R.id.zoom_parent);
        mPhotoView.setZoomParentView(zoomParentLayout);
        mPhotoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                finish();
            }
        });

        adapter = new ImagesAdapter();
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!TextUtils.isEmpty(destroyMsgId) && isDownloadSuccessed && message != null) {
            if (message.direct() == EMMessage.Direct.RECEIVE) {
                EMMessage cmdMessage = EMMessage.createSendMessage(EMMessage.Type.CMD);
                cmdMessage.setTo(message.getFrom());
                cmdMessage.addBody(new EMCmdMessageBody(EaseConstant.BURN_AFTER_READING_CMD_ACTION));
                cmdMessage.setAttribute(EaseConstant.BURN_AFTER_READING_DESTORY_MSGID, message.getMsgId());
                MessageUtils.sendMessage(cmdMessage);

                message.setAttribute(EaseConstant.BURN_AFTER_READING_READED, true);
                EMClient.getInstance().chatManager().updateMessage(message);
                MPEventBus.getDefault().post(new MessageChanged(message.getMsgId()));
                try {
                    new File(localFilePath).delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private class ImagesAdapter extends PagerAdapter {

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ZoomRelativeLayout layout = new ZoomRelativeLayout(EaseShowBigImageActivity.this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            final DragPhotoView imageView = new DragPhotoView(EaseShowBigImageActivity.this);
            imageView.setLayoutParams(params);

            final ImageEntity entity = imgList.get(position);
            if (entity.getImagePath().equals("none")) {
                Glide.with(EaseShowBigImageActivity.this).load(default_res).into(imageView);
            } else {
                if (entity.imagePath.contains("http")) {
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            FutureTarget<File> target = Glide.with(EaseShowBigImageActivity.this).asFile().load(entity.imagePath).submit();
                            try {
                                final File file = target.get();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        entity.setImagePath(file.getPath());
                                        GlideUtils.loadFromFile(instance, file.getPath(), default_res, imageView);
                                    }
                                });
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    GlideUtils.loadFromFile(instance, entity.getImagePath(), default_res, imageView);
                }
            }
            if (entity.getImagePath().equals("none") || entity.isBurn()) {
                ivSave.setVisibility(View.GONE);
            }
            imageView.setZoomParentView(layout);
            layout.addView(imageView);
            container.addView(layout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    finish();
                }
            });
            return layout;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return imgList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    public class ImageEntity {
        String imagePath;
        boolean isBurn;

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public boolean isBurn() {
            return isBurn;
        }

        public void setBurn(boolean burn) {
            isBurn = burn;
        }
    }
}
