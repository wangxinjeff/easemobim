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
package com.hyphenate.easemob.easeui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.easeui.widget.EaseChatMessageList.MessageListItemClickListener;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.easemob.easeui.widget.presenter.EaseChatBigExpressionPresenter;
import com.hyphenate.easemob.easeui.widget.presenter.EaseChatFilePresenter;
import com.hyphenate.easemob.easeui.widget.presenter.EaseChatImagePresenter;
import com.hyphenate.easemob.easeui.widget.presenter.EaseChatRowPresenter;
import com.hyphenate.easemob.easeui.widget.presenter.EaseChatTextPresenter;
import com.hyphenate.easemob.easeui.widget.presenter.EaseChatVideoPresenter;
import com.hyphenate.easemob.easeui.widget.presenter.EaseChatVoicePresenter;
import com.hyphenate.util.DensityUtil;

import java.util.HashMap;

public class EaseMessageAdapter extends BaseAdapter {
    private final static String TAG = "msg";

    private Context context;

    private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2;

    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
    private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
    private static final int MESSAGE_TYPE_SENT_VOICE = 6;
    private static final int MESSAGE_TYPE_RECV_VOICE = 7;
    private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
    private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
    private static final int MESSAGE_TYPE_SENT_FILE = 10;
    private static final int MESSAGE_TYPE_RECV_FILE = 11;
    private static final int MESSAGE_TYPE_SENT_EXPRESSION = 12;
    private static final int MESSAGE_TYPE_RECV_EXPRESSION = 13;


    public int itemTypeCount;

    // reference to conversation object in chatsdk
    private EMConversation conversation;
    EMMessage[] messages = null;

    private String toChatUsername;

    private MessageListItemClickListener itemClickListener;
    private EaseCustomChatRowProvider customRowProvider;

    private boolean showUserNick;
    private boolean showAvatar;
    private Drawable myBubbleBg;
    private Drawable otherBuddleBg;

    private ListView listView;
    private EaseMessageListItemStyle itemStyle;
    //    private HashSet<EMMessage> checkSet;
    private boolean isShowCheckbox;
    private HashMap<String, EMMessage> checkMap;
    public int mMinItemWidth;
    public int mMaxItemWidth;
    public View animView;
    public View currentPlayView;
    public int animStatus = -1; // -1 not play; 0 sent play; 1 received play

    public EaseMessageAdapter(Context context, String username, int chatType, ListView listView, HashMap<String, EMMessage> checkMap) {
        this.context = context;
        this.listView = listView;
//        this.checkSet = checkSet;
        this.checkMap = checkMap;
        toChatUsername = username;
        this.conversation = EMClient.getInstance().chatManager().getConversation(username, EaseCommonUtils.getConversationType(chatType), true);

        animStatus = -1;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        mMaxItemWidth = (int) (displayMetrics.widthPixels * 0.4f);
        mMinItemWidth = (int) (displayMetrics.widthPixels * 0.16f);
    }

    Handler handler = new Handler() {
        private void refreshList() {
            // you should not call getAllMessages() in UI thread
            // otherwise there is problem when refreshing UI and there is new message arrive
//            java.util.List<EMMessage> var = new ArrayList<>();
//            for (EMMessage message : conversation.getAllMessages()) {
//                if (TextUtils.isEmpty(message.getStringAttribute(EaseConstant.MSG_ATTR_CONFERENCE, ""))) {
//                    var.add(message);
//                }
//            }
//            messages = var.toArray(new EMMessage[var.size()]);
            messages = conversation.getAllMessages().toArray(new EMMessage[0]);
            conversation.markAllMessagesAsRead();
            notifyDataSetChanged();
        }

        @Override
        public void handleMessage(android.os.Message message) {
            switch (message.what) {
                case HANDLER_MESSAGE_REFRESH_LIST:
                    refreshList();
                    break;
                case HANDLER_MESSAGE_SELECT_LAST:
                    if (messages != null && messages.length > 0) {
                        listView.setSelection(messages.length - 1);
                    }
                    break;
                case HANDLER_MESSAGE_SEEK_TO:
                    int position = message.arg1;
//                    listView.setSelection(position);
                    listView.smoothScrollToPositionFromTop(position, 0);
                    break;
                default:
                    break;
            }
        }
    };

    public void refresh() {
        if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
            return;
        }
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
        handler.sendMessage(msg);
    }

    /**
     * refresh and select the last
     */
    public void refreshSelectLast() {
        final int TIME_DELAY_REFRESH_SELECT_LAST = 100;
        handler.removeMessages(HANDLER_MESSAGE_REFRESH_LIST);
        handler.removeMessages(HANDLER_MESSAGE_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_REFRESH_LIST, TIME_DELAY_REFRESH_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SELECT_LAST, TIME_DELAY_REFRESH_SELECT_LAST);
    }

    /**
     * refresh and seek to the position
     */
    public void refreshSeekTo(int position) {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
        msg.arg1 = position;
        handler.sendMessage(msg);
    }

    public EMMessage getItem(int position) {
        if (messages != null && position < messages.length) {
            return messages[position];
        }
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * get count of messages
     */
    public int getCount() {
        return messages == null ? 0 : messages.length;
    }

    /**
     * get number of message type, here 14 = (EMMessage.Type) * 2
     */
    public int getViewTypeCount() {
        if (customRowProvider != null && customRowProvider.getCustomChatRowTypeCount() > 0) {
            return customRowProvider.getCustomChatRowTypeCount() + 14;
        }
        return 14;
    }


    /**
     * get type of item
     */
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);
        if (message == null) {
            return -1;
        }

        if (customRowProvider != null && customRowProvider.getCustomChatRowType(message) > 0) {
            return customRowProvider.getCustomChatRowType(message) + 13;
        }

        if (message.getType() == EMMessage.Type.TXT) {
            if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EXPRESSION : MESSAGE_TYPE_SENT_EXPRESSION;
            }
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
        }
        if (message.getType() == EMMessage.Type.IMAGE) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;

        }
        if (message.getType() == EMMessage.Type.LOCATION) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;
        }
        if (message.getType() == EMMessage.Type.VOICE) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
        }
        if (message.getType() == EMMessage.Type.VIDEO) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;
        }
        if (message.getType() == EMMessage.Type.FILE) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SENT_FILE;
        }

        return -1;// invalid
    }

    protected EaseChatRowPresenter createChatRowPresenter(EMMessage message, int position) {
        if (customRowProvider != null && customRowProvider.getCustomChatRow(message, position, this) != null) {
            return customRowProvider.getCustomChatRow(message, position, this);
        }

        EaseChatRowPresenter presenter = null;

        switch (message.getType()) {
            case TXT:
                if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    presenter = new EaseChatBigExpressionPresenter() {
                        @Override
                        public void onChecked(EMMessage message, boolean b) {
                            fillCheckList(message, b);
                        }
                    };
                } else {
                    presenter = new EaseChatTextPresenter() {
                        @Override
                        public void onChecked(EMMessage message, boolean b) {
                            fillCheckList(message, b);
                        }
                    };
                }
                break;
            case FILE:
                presenter = new EaseChatFilePresenter() {
                    @Override
                    public void onChecked(EMMessage message, boolean b) {
                        fillCheckList(message, b);
                    }
                };
                break;
            case IMAGE:
                presenter = new EaseChatImagePresenter() {
                    @Override
                    public void onChecked(EMMessage message, boolean b) {
                        fillCheckList(message, b);
                    }
                };
                break;
            case VOICE:
                presenter = new EaseChatVoicePresenter() {
                    @Override
                    public void onChecked(EMMessage message, boolean b) {
                        fillCheckList(message, b);
                    }
                };
                break;
            case VIDEO:
                presenter = new EaseChatVideoPresenter() {
                    @Override
                    public void onChecked(EMMessage message, boolean b) {
                        fillCheckList(message, b);
                    }
                };
                break;
            default:
                break;
        }

        return presenter;
    }

    //填充选择列表
    private void fillCheckList(EMMessage message, boolean b) {
        if (checkMap != null) {
            if (checkMap.containsKey(message.getMsgId()) && !b) {
                checkMap.remove(message.getMsgId());
            }
            if (!checkMap.containsKey(message.getMsgId()) && b) {
                checkMap.put(message.getMsgId(), message);
            }
        }

    }


    @SuppressLint("NewApi")
    public View getView(final int position, View convertView, ViewGroup parent) {
        EMMessage message = getItem(position);

        EaseChatRowPresenter presenter = null;

        if (convertView == null) {
            presenter = createChatRowPresenter(message, position);
            convertView = presenter.createChatRow(context, message, position, this);
            convertView.setTag(presenter);
        } else {
            presenter = (EaseChatRowPresenter) convertView.getTag();
        }
        boolean checked = checkMap.containsKey(message.getMsgId());
        presenter.setup(message, position, itemClickListener, itemStyle, isShowCheckbox, checked);

        if (position == messages.length - 1) {
            convertView.setPadding(DensityUtil.dip2px(context, 0f), DensityUtil.dip2px(context, 0f), DensityUtil.dip2px(context, 0f), DensityUtil.dip2px(context, 50f));
        } else {
            convertView.setPadding(DensityUtil.dip2px(context, 0f), DensityUtil.dip2px(context, 0f), DensityUtil.dip2px(context, 0f), DensityUtil.dip2px(context, 0f));
        }

        return convertView;
    }


    public void setItemStyle(EaseMessageListItemStyle itemStyle) {
        this.itemStyle = itemStyle;
    }


    public void setItemClickListener(MessageListItemClickListener listener) {
        itemClickListener = listener;
    }

    public void setCustomChatRowProvider(EaseCustomChatRowProvider rowProvider) {
        customRowProvider = rowProvider;
    }


    public boolean isShowUserNick() {
        return showUserNick;
    }


    public boolean isShowAvatar() {
        return showAvatar;
    }


    public Drawable getMyBubbleBg() {
        return myBubbleBg;
    }


    public Drawable getOtherBubbleBg() {
        return otherBuddleBg;
    }

    public void showCheckbox() {
        isShowCheckbox = true;
        notifyDataSetChanged();
    }

    public void hideCheckbox() {
        isShowCheckbox = false;
        notifyDataSetChanged();
    }
}
