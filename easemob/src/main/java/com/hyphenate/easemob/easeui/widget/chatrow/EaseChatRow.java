package com.hyphenate.easemob.easeui.widget.chatrow;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.Direct;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easemob.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.easeui.widget.EaseChatMessageList.MessageListItemClickListener;
import com.hyphenate.easemob.easeui.widget.EaseImageView;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;
import com.hyphenate.easemob.imlibs.message.MessageUtils;
import com.hyphenate.easemob.imlibs.mp.utils.DateTimeUtil;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.util.DateUtils;

import java.util.Date;

public abstract class EaseChatRow extends LinearLayout {
    public interface EaseChatRowActionCallback {
        void onResendClick(EMMessage message);

        void onBubbleClick(EMMessage message);

        void onDetachedFromWindow();

        void onChecked(EMMessage message, boolean b);
    }

    protected static final String TAG = EaseChatRow.class.getSimpleName();

    protected LayoutInflater inflater;
    protected Context context;
    protected EaseMessageAdapter adapter;
    protected EMMessage message;
    protected int position;

    protected TextView timeStampView;
    protected AvatarImageView userAvatarView;
    public View bubbleLayout;
    protected TextView usernickView;

    protected TextView percentageView;
    protected ProgressBar progressBar;
    protected ImageView statusView;
    protected Activity activity;

    protected TextView ackedView;
    protected TextView deliveredView;
    protected CheckBox cb;

    protected MessageListItemClickListener itemClickListener;
    protected EaseMessageListItemStyle itemStyle;

    private EaseChatRowActionCallback itemActionCallback;

    public EaseChatRow(Context context, EMMessage message, int position, EaseMessageAdapter adapter) {
        super(context);
        this.context = context;
        this.message = message;
        this.position = position;
        this.adapter = adapter;
        this.activity = (Activity) context;
        inflater = LayoutInflater.from(context);

        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        itemActionCallback.onDetachedFromWindow();
        super.onDetachedFromWindow();
    }

    public void updateView(final EMMessage msg) {
        if (activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onViewUpdate(msg);
            }
        });
    }

    private void initView() {
        onInflateView();
        timeStampView = (TextView) findViewById(R.id.timestamp);
        userAvatarView = findViewById(R.id.iv_userhead);
        bubbleLayout = findViewById(R.id.bubble);
        usernickView = (TextView) findViewById(R.id.tv_userid);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        statusView = (ImageView) findViewById(R.id.msg_status);
        ackedView = (TextView) findViewById(R.id.tv_ack);
        deliveredView = (TextView) findViewById(R.id.tv_delivered);

        cb = (CheckBox) findViewById(R.id.cb);

        onFindViewById();
    }

    /**
     * set property according message and position
     *
     * @param message
     * @param position
     * @param isShowCheckbox
     */
    public void setUpView(EMMessage message, int position,
                          MessageListItemClickListener itemClickListener,
                          EaseChatRowActionCallback itemActionCallback,
                          EaseMessageListItemStyle itemStyle, boolean isShowCheckbox, boolean isChecked) {
        this.message = message;
        this.position = position;
        this.itemClickListener = itemClickListener;
        this.itemActionCallback = itemActionCallback;
        this.itemStyle = itemStyle;

        if (cb != null) {
            cb.setVisibility(isShowCheckbox ? View.VISIBLE : View.GONE);
            cb.setChecked(isChecked);
        }

        setUpBaseView();
        onSetUpView();
        setClickListener();
    }

    private void setUpBaseView() {
        // set nickname, avatar and background of bubble
        TextView timestamp = (TextView) findViewById(R.id.timestamp);
        if (timestamp != null) {
            if (position == 0) {
                timestamp.setText(DateTimeUtil.getTimestampString(new Date(message.getMsgTime())));
                timestamp.setVisibility(View.VISIBLE);
            } else {
                // show time stamp if interval with last message is > 30 seconds
                EMMessage prevMessage = (EMMessage) adapter.getItem(position - 1);
                if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
                    timestamp.setVisibility(View.GONE);
                } else {
                    timestamp.setText(DateTimeUtil.getTimestampString(new Date(message.getMsgTime())));
                    timestamp.setVisibility(View.VISIBLE);
                }
            }
        }
        if (userAvatarView != null) {
            //set nickname and avatar
            if (message.direct() == Direct.SEND) {
                MPUserEntity entityBean = AppHelper.getInstance().getModel().getUserInfo(EMClient.getInstance().getCurrentUser());
                if(entityBean != null){
                    AvatarUtils.setAvatarContent(context, entityBean.getRealName(), entityBean.getAvatar(), userAvatarView);
                } else {
                    AvatarUtils.setAvatarContent(context, message.getFrom(), userAvatarView);
                }
            } else {
                EaseUserUtils.setUserNick(message.getFrom(), usernickView, context);
                MPUserEntity entityBean = AppHelper.getInstance().getModel().getUserInfo(message.getFrom());
                if(entityBean != null){
                    AvatarUtils.setAvatarContent(context, entityBean.getRealName(), entityBean.getAvatar(), userAvatarView);
                } else {
                    AvatarUtils.setAvatarContent(context, message.getFrom(), userAvatarView);
                }
            }
        }
        if (EMClient.getInstance().getOptions().getRequireDeliveryAck() && message.getChatType() == EMMessage.ChatType.Chat) {
            if (deliveredView != null) {
                if (message.isDelivered()) {
                    deliveredView.setVisibility(View.VISIBLE);
                } else {
                    deliveredView.setVisibility(View.INVISIBLE);
                }
            }
        }
        if (EMClient.getInstance().getOptions().getRequireAck() && message.getChatType() == EMMessage.ChatType.Chat && PreferenceUtils.getInstance().getShowRead()) {
            if (message.getFrom().equals(message.getTo()) || (!MessageUtils.isCommonRegion(message.getFrom(), message.getTo()))) {
                if(ackedView != null) {
                    ackedView.setText("");
                }
            } else {
                if (ackedView != null) {
                    if (message.isAcked()) {
                        if (deliveredView != null) {
                            deliveredView.setVisibility(View.INVISIBLE);
                        }
                        ackedView.setText(getResources().getString(R.string.text_ack_msg));
                    } else {
                        ackedView.setText(getResources().getString(R.string.text_ack_msg_unread));
                    }
                }
            }
        }

        if (itemStyle != null) {
            if (userAvatarView != null) {
                if (itemStyle.isShowAvatar()) {
                    userAvatarView.setVisibility(View.VISIBLE);
                } else {
                    userAvatarView.setVisibility(View.GONE);
                }
            }
            if (usernickView != null) {
                if (itemStyle.isShowUserNick())
                    usernickView.setVisibility(View.VISIBLE);
                else
                    usernickView.setVisibility(View.GONE);
            }
            if (bubbleLayout != null) {
                if (message.direct() == Direct.SEND) {
                    if (itemStyle.getMyBubbleBg() != null) {
                        bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getMyBubbleBg());
                    }
                } else if (message.direct() == Direct.RECEIVE) {
                    if (itemStyle.getOtherBubbleBg() != null) {
                        bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getOtherBubbleBg());
                    }
                }
            }
        }

    }

    private void setClickListener() {
        if (bubbleLayout != null) {
            bubbleLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && itemClickListener.onBubbleClick(message)) {
                        return;
                    }
                    if (itemActionCallback != null) {
                        itemActionCallback.onBubbleClick(message);
                    }
                }
            });

            if (longClickEnable()) {
                bubbleLayout.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (itemClickListener != null) {
                            itemClickListener.onBubbleLongClick(userAvatarView, bubbleLayout, message, position);
                        }
                        return true;
                    }
                });
            }
        }

        if (statusView != null) {
            statusView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && itemClickListener.onResendClick(message)) {
                        return;
                    }
                    if (itemActionCallback != null) {
                        itemActionCallback.onResendClick(message);
                    }
                }
            });
        }

        if (userAvatarView != null) {
            userAvatarView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        if (message.direct() == Direct.SEND) {
                            itemClickListener.onUserAvatarClick(EMClient.getInstance().getCurrentUser());
                        } else {
                            itemClickListener.onUserAvatarClick(message.getFrom());
                        }
                    }
//                    if (itemClickListener != null) {
//                        itemClickListener.onUserAvatarClick(message.getFrom());
////                        try {
////                            Map<String, Object> ext = message.ext();
////                            if (ext.containsKey(EaseConstant.EXT_USER_TYPE)) {
////                                String extUserType = message.getStringAttribute(EaseConstant.EXT_USER_TYPE);
////                                try {
////                                    JSONObject userType = new JSONObject(extUserType);
////                                    itemClickListener.onUserAvatarClick(userType.getInt(EaseConstant.EXT_USER_ID));
////                                } catch (JSONException e) {
////                                    e.printStackTrace();
////                                }
////                            }
////                        } catch (HyphenateException e) {
////                            e.printStackTrace();
////                        }
//                    }
                }
            });
            userAvatarView.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null) {
                        if (message.direct() == Direct.SEND) {
                            itemClickListener.onUserAvatarLongClick(EMClient.getInstance().getCurrentUser());
                        } else {
                            itemClickListener.onUserAvatarLongClick(message.getFrom());
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
        if (cb != null) {
            cb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    itemActionCallback.onChecked(message, b);
                }

            });
        }
    }

    protected void showCheckBox() {
        if (cb == null) {
            return;
        }
        if (!disableCheckBox()) {
            cb.setVisibility(View.VISIBLE);
        }

    }

    protected void hideCheckBox() {
        if (cb == null) {
            return;
        }
        cb.setVisibility(View.GONE);
    }

    protected abstract void onInflateView();

    protected boolean disableCheckBox() {
        return false;
    }

    /**
     * find view by id
     */
    protected abstract void onFindViewById();

    /**
     * refresh view when message status change
     */
    protected abstract void onViewUpdate(EMMessage msg);

    /**
     * setup view
     */
    protected abstract void onSetUpView();

    protected boolean longClickEnable() {
        return true;
    }
}
