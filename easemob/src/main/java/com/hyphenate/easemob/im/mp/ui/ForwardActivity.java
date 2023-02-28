package com.hyphenate.easemob.im.mp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.EaseMessageUtils;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.EaseAlertDialog;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.imlibs.message.MessageUtils;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.im.mp.adapter.RecentAdapter;
import com.hyphenate.easemob.im.mp.adapter.RecentItem;
import com.hyphenate.easemob.im.mp.adapter.SelectedItem;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.im.mp.ui.ForwardUsersActivity;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.mp.utils.MPMessageUtils;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.domain.ExtFileMessage;
import com.hyphenate.easemob.im.officeautomation.domain.ExtLocationMessage;
import com.hyphenate.easemob.im.officeautomation.domain.ExtMediaMessage;
import com.hyphenate.easemob.im.officeautomation.domain.ExtMsg;
import com.hyphenate.easemob.im.officeautomation.domain.ExtTxtMessage;
import com.hyphenate.easemob.im.officeautomation.domain.ExtUserType;
import com.hyphenate.easemob.im.officeautomation.domain.ExtVoiceMessage;
import com.hyphenate.easemob.im.officeautomation.domain.SizeBean;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;
import com.hyphenate.easemob.im.officeautomation.ui.ChatActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 13/09/2018
 */
public class ForwardActivity extends BaseActivity {

    private static final String TAG = "ForwardActivity";
    private static final int REQUEST_CODE_FORWARD_USERS = 0x01;
    private RecyclerView rvRecent;
    private ImageView ivBack;
    private TextView tvRight;
    private RecentAdapter recentAdapter;
    private List<RecentItem> allConversations = new ArrayList<RecentItem>();
    private boolean isMultiSelect;
    private HashMap<String, SelectedItem> selectedItemMap = new HashMap<>();
    private ArrayList<String> forwardMsgIds;
    private TextView headerTitle;
    //合并转发title
    private String combineTitle;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_forward);
        setSwipeEnabled(false);
        initViews();
        initDatas();
    }

    private void initViews() {
        rvRecent = findViewById(R.id.rv_recent);
        ivBack = findViewById(R.id.iv_back);
        tvRight = findViewById(R.id.tv_right);
    }

    @SuppressLint("StringFormatMatches")
    private void notifyRightText() {
        if (isMultiSelect) {
            if (tvHeaderView != null) {
                tvHeaderView.setText(R.string.title_choose_contact);
            }
            if (selectedItemMap.isEmpty()) {
                tvRight.setText(R.string.the_radio);
            } else {
//				tvRight.setText("发送(" + selectedItemMap.size() + ")");
                int count = selectedItemMap.size();
                tvRight.setText(getString(R.string.btn_send_format, count));
            }
        } else {
            tvRight.setText(R.string.multi_select);
            if (tvHeaderView != null) {
                tvHeaderView.setText(R.string.title_new_chat);
            }
        }
    }


    private void initDatas() {
        Intent gIntent = getIntent();
        if (gIntent != null) {
            forwardMsgIds = gIntent.getStringArrayListExtra("forwardMsgIds");
            combineTitle = gIntent.getStringExtra("combine_title");
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItemMap.isEmpty()) {
                    isMultiSelect = !isMultiSelect;
                    recentAdapter.setMultiCheckEnable(isMultiSelect);
                    notifyRightText();
                } else {
                    StringBuilder sbNick = new StringBuilder();
                    for (SelectedItem selectedItem : selectedItemMap.values()) {
                        String conversationId = selectedItem.conversationId;
                        String nick;
                        if (selectedItem.isGroupChat) {
                            GroupBean groupInfo = EaseUserUtils.getGroupInfo(conversationId);
                            nick = groupInfo != null ? groupInfo.getNick() : conversationId;
                        } else {
                            EaseUser easeUser = EaseUserUtils.getUserInfo(conversationId);
                            nick = easeUser != null ? easeUser.getNick() : conversationId;
                        }
                        sbNick.append(nick).append(",");
                    }
                    if (sbNick.length() > 0) {
                        sbNick.deleteCharAt(sbNick.length() - 1);
                    }


                    new EaseAlertDialog(activity, null, getString(R.string.confirm_forward_to, sbNick.toString()), null, new EaseAlertDialog.AlertDialogUser() {
                        @Override
                        public void onResult(boolean confirmed, Bundle bundle) {
                            if (confirmed) {
                                if (TextUtils.isEmpty(combineTitle)) {
                                    showProgressDialog();
                                    EaseUI.getInstance().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            Collection<SelectedItem> selectedItems = selectedItemMap.values();
                                            for (SelectedItem selectedItem : selectedItems) {
                                                for (String forwardMsgId : forwardMsgIds) {
                                                    forwardMessage(selectedItem.conversationId, selectedItem.isGroupChat, forwardMsgId);
                                                }
                                            }
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    hideProgressDialog();
                                                    finish();
                                                    try {
                                                        ChatActivity.activityInstance.refresh();
                                                    } catch (Exception ignored) {
                                                    }
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    sendCombineExt();
                                }
                            }
                        }
                    }, true).show();

//					Toast.makeText(getApplicationContext(), "发出去！！！" + selectedItemMap.size() + "个人", Toast.LENGTH_SHORT).show();
                }


            }
        });
        rvRecent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvRecent.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvRecent.setAdapter(recentAdapter = new RecentAdapter(R.layout.em_row_item_forward, allConversations));
        recentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                RecentItem recentItem = (RecentItem) adapter.getItem(position);
                if (recentItem == null) return;
                String conversationId = recentItem.conversation.conversationId();
                EMConversation.EMConversationType conversationType = recentItem.conversation.getType();
                boolean isGroup = conversationType == EMConversation.EMConversationType.GroupChat;
                recentItem.isChecked = !recentItem.isChecked;
                SelectedItem selectedItem = new SelectedItem(conversationId, isGroup);
                if (recentItem.isChecked) {
                    if (!selectedItemMap.containsKey(conversationId)) {
                        selectedItemMap.put(conversationId, selectedItem);
                    }
                } else {
                    if (selectedItemMap.containsKey(conversationId)) {
                        selectedItemMap.remove(conversationId);
                    }
                }
                if (isMultiSelect) {
                    notifyRightText();
                    recentAdapter.notifyDataSetChanged();
                } else {
                    String nick;
                    if (isGroup) {
                        GroupBean groupInfo = EaseUserUtils.getGroupInfo(conversationId);
                        nick = groupInfo != null ? groupInfo.getNick() : conversationId;
                    } else {
                        EaseUser easeUser = EaseUserUtils.getUserInfo(conversationId);
                        nick = easeUser != null ? easeUser.getNick() : conversationId;
                    }


                    new EaseAlertDialog(activity, null, getString(R.string.confirm_forward_to, nick), null, new EaseAlertDialog.AlertDialogUser() {
                        @Override
                        public void onResult(boolean confirmed, Bundle bundle) {
                            if (confirmed) {
                                if (TextUtils.isEmpty(combineTitle)) {
                                    try {
                                        ChatActivity.activityInstance.finish();
                                    } catch (Exception e) {
                                    }
                                    Intent intent = new Intent(activity, ChatActivity.class);
                                    // it is single chat
                                    intent.putExtra("userId", conversationId);
                                    intent.putExtra("forward_msg_id_list", forwardMsgIds);
                                    if (isGroup) {
                                        // it is group chat
                                        intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                                    }
                                    startActivity(intent);
                                    finish();
                                } else {
                                    sendCombineExt();
                                }
                            } else {
                                selectedItemMap.clear();
                                for (RecentItem recentItem1 : allConversations) {
                                    recentItem1.isChecked = false;
                                }
                                recentAdapter.notifyDataSetChanged();
                            }

                        }
                    }, true).show();
                }
            }
        });
        View headerView = LayoutInflater.from(this).inflate(R.layout.em_layout_header_activity_forward, null);
        tvHeaderView = headerView.findViewById(R.id.tv_title);
        tvHeaderView.setText(R.string.title_choose_contact);
        tvHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//				Toast.makeText(getApplicationContext(), "新聊天！", Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(activity, ForwardUsersActivity.class)
                        .putExtra("selectedItems", selectedItemMap)
                        .putExtra("isMultiSelect", isMultiSelect), REQUEST_CODE_FORWARD_USERS);

            }
        });
        recentAdapter.addHeaderView(headerView);
        getAllConversationsFromDB();
    }

    private TextView tvHeaderView;


    private void getAllConversationsFromDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                allConversations.clear();
                Map<String, EMConversation> conversationMap = EMClient.getInstance().chatManager().getAllConversations();
                List<EMConversation> tempConversations = new ArrayList<>();
                synchronized (conversationMap) {
                    for (EMConversation conversation : conversationMap.values()) {
                        if ("admin".equals(conversation.conversationId())) continue;
                        if (conversation.getAllMessages().size() > 0) {
                            //判断是否置顶了
                            String stickyTime = AppHelper.getInstance().getStickyTime(conversation.conversationId(), conversation.getType());
                            conversation.setExtField(stickyTime);
                            tempConversations.add(conversation);
                        }
                    }
                }
                Collections.sort(tempConversations, new Comparator<EMConversation>() {
                    //		@Override
                    public int compare(EMConversation convLeft, EMConversation convRight) {

                        boolean leftSticky = !TextUtils.isEmpty(convLeft.getExtField());
                        boolean rightSticky = !TextUtils.isEmpty(convRight.getExtField());
                        if (leftSticky && rightSticky) {
                            return Long.compare(getConversationSortTime(convRight), getConversationSortTime(convLeft));
                        } else if (rightSticky) {
                            return 1;
                        } else if (leftSticky) {
                            return -1;
                        } else {
                            return Long.compare(convRight.getLastMessage().getMsgTime(), convLeft.getLastMessage().getMsgTime());
                        }
                    }
                });

                for (EMConversation conversation : tempConversations) {
                    RecentItem recentItem = new RecentItem(conversation);
                    if (selectedItemMap.containsKey(conversation.conversationId())) {
                        recentItem.isChecked = true;
                    } else {
                        recentItem.isChecked = false;
                    }
                    allConversations.add(recentItem);
                }
                if (isFinishing()) return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyRightText();
                        recentAdapter.setNewData(allConversations);
                    }
                });
            }
        }).start();
    }

    private long getConversationSortTime(EMConversation conversation) {
        boolean isSticky = !TextUtils.isEmpty(conversation.getExtField());
        long stickyTime = isSticky ? Long.parseLong(conversation.getExtField()) : 0l;
        long latestMsgTime = conversation.getLastMessage().getMsgTime();
        return Math.max(latestMsgTime, stickyTime);
    }

    private void sendCombineExt() {

        for (SelectedItem selectedItem : selectedItemMap.values()) {
            EMMessage msg = EMMessage.createTxtSendMessage(combineTitle, selectedItem.conversationId);
            if (selectedItem.isGroupChat) {
                msg.setChatType(EMMessage.ChatType.GroupChat);
            }
            ArrayList<Object> objectList = new ArrayList<>();

            for (int i = 0; i < forwardMsgIds.size(); i++) {
                String msgId = forwardMsgIds.get(i);
                final EMMessage forwardMsg = EMClient.getInstance().chatManager().getMessage(msgId);
                EMMessage.Type msgType = forwardMsg.getType();
                Map<String, Object> ext = forwardMsg.ext();
                if (msgType == EMMessage.Type.TXT) {
                    if (forwardMsg.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
//                        String emojiconId = forward_msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null);
//                        putExtTxtMessage(objectList, forward_msg);
                        putExtSpecialObject(objectList, forwardMsg);
                    } else {
                        // get the content and send it
                        putExtTxtMessage(objectList, forwardMsg);
                    }
                } else if (msgType == EMMessage.Type.IMAGE) {
                    EMImageMessageBody imgBody = ((EMImageMessageBody) forwardMsg.getBody());
                    // send image
                    String thumbnailUrl = imgBody.getThumbnailUrl();
                    String remoteUrl = imgBody.getRemoteUrl();
                    int width = imgBody.getWidth();
                    int height = imgBody.getHeight();
                    putMediaObject(objectList, forwardMsg, thumbnailUrl, remoteUrl, "image", width, height);
                } else if (msgType == EMMessage.Type.VIDEO) {
                    EMVideoMessageBody videoBody = (EMVideoMessageBody) forwardMsg.getBody();
                    String thumbnailUrl = videoBody.getThumbnailUrl();
                    String remoteUrl1 = videoBody.getRemoteUrl();
                    int width = videoBody.getThumbnailWidth();
                    int height = videoBody.getThumbnailHeight();
                    putMediaObject(objectList, forwardMsg, thumbnailUrl, remoteUrl1, "video", width, height);
                } else if (msgType == EMMessage.Type.VOICE) {
                    EMVoiceMessageBody voiceMessageBody = (EMVoiceMessageBody) forwardMsg.getBody();
                    String remoteUrl = voiceMessageBody.getRemoteUrl();
                    putExtVoiceMessage(remoteUrl, objectList, forwardMsg);
                } else if (msgType == EMMessage.Type.FILE) {
                    EMNormalFileMessageBody fileMessageBody = (EMNormalFileMessageBody) forwardMsg.getBody();
                    putFileObject(objectList, forwardMsg, fileMessageBody.getRemoteUrl(), fileMessageBody.displayName(), fileMessageBody.getFileSize());
                } else if (msgType == EMMessage.Type.LOCATION) {
                    putExtLocationMessage(objectList, forwardMsg);
                } else {
                    putExtSpecialObject(objectList, forwardMsg);
                }
            }
            ExtMsg extMsg = new ExtMsg("chatMsgs", combineTitle, objectList);
            //extmsg
            String extMsgJson = new Gson().toJson(extMsg);
            try {
                msg.setAttribute(EaseConstant.EXT_EXTMSG, new JSONObject(extMsgJson));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Send message.
            MPMessageUtils.sendMessage(msg);
        }
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_FORWARD_USERS) {
                if (data != null) {
                    HashMap<String, SelectedItem> itemMaps = (HashMap<String, SelectedItem>) data.getSerializableExtra("selectedItems");
                    if (itemMaps == null) {
                        selectedItemMap = new HashMap<>();
                    } else {
                        selectedItemMap = itemMaps;
                    }
                    if (!isMultiSelect) {
                        for (Map.Entry<String, SelectedItem> selectedItemEntry : selectedItemMap.entrySet()) {
                            SelectedItem selectedItem = selectedItemEntry.getValue();
                            boolean isGroup = selectedItem.isGroupChat;
                            String conversationId = selectedItem.conversationId;
                            String nick;
                            if (isGroup) {
                                GroupBean groupInfo = EaseUserUtils.getGroupInfo(conversationId);
                                nick = groupInfo != null ? groupInfo.getNick() : conversationId;
                                nick = "群(" + nick + ")";
                            } else {
                                EaseUser easeUser = EaseUserUtils.getUserInfo(conversationId);
                                nick = easeUser != null ? easeUser.getNick() : conversationId;
                            }

                            new EaseAlertDialog(activity, null, getString(R.string.confirm_forward_to, nick), null, new EaseAlertDialog.AlertDialogUser() {
                                @Override
                                public void onResult(boolean confirmed, Bundle bundle) {
                                    if (confirmed) {
                                        if (TextUtils.isEmpty(combineTitle)) {
                                            try {
                                                ChatActivity.activityInstance.finish();
                                            } catch (Exception e) {
                                            }
                                            Intent intent = new Intent(activity, ChatActivity.class);
                                            // it is single chat
                                            intent.putExtra("userId", conversationId);
                                            intent.putExtra("forward_msg_id_list", forwardMsgIds);
                                            if (isGroup) {
                                                // it is group chat
                                                intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                                            }
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            sendCombineExt();
                                        }
                                    }
                                }
                            }, true).show();
                        }

                    } else {
                        getAllConversationsFromDB();
                    }
                }

            }
        }
    }

    /**
     * 构造特殊消息展示
     *
     * @param objectList
     * @param forward_msg
     */
    private void putExtSpecialObject(ArrayList<Object> objectList, EMMessage forward_msg) {
        try {
            Map<String, Object> ext = forward_msg.ext();
            ExtUserType userType = null;
            if (ext.containsKey(EaseConstant.EXT_USER_TYPE)) {
                String extUserType = forward_msg.getStringAttribute(EaseConstant.EXT_USER_TYPE);
                if (!TextUtils.isEmpty(extUserType)) {
                    userType = new Gson().fromJson(extUserType, ExtUserType.class);
                }
            } else {
                EaseUser easeUser = EaseUserUtils.getUserInfo(forward_msg.getFrom());
                if (easeUser != null) {
                    userType = new ExtUserType(easeUser.getUser_id(), easeUser.getNickname(), easeUser.getAvatar());
                } else {
                    userType = new ExtUserType(-1, forward_msg.getFrom(), null);
                }
            }
            String extMsg = null;
            try {
                extMsg = forward_msg.getJSONObjectAttribute("extMsg").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ExtTxtMessage extTxtMessage = new ExtTxtMessage("txt", getString(R.string.special_message), userType.nick,
                    userType.avatar, forward_msg.getMsgTime(), forward_msg.getMsgId(), forward_msg.getChatType(), forward_msg.getFrom(), forward_msg.getTo(), extMsg);
            objectList.add(extTxtMessage);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }


    //构造文字合并转发
    private void putExtTxtMessage(ArrayList<Object> objectList, EMMessage forward_msg) {
        try {
            Map<String, Object> ext = forward_msg.ext();
            ExtUserType userType = null;
            if (ext.containsKey(EaseConstant.EXT_USER_TYPE)) {
                String extUserType = forward_msg.getStringAttribute(EaseConstant.EXT_USER_TYPE);
                if (extUserType != null) {
                    userType = new Gson().fromJson(extUserType, ExtUserType.class);
                }
            } else {
                EaseUser easeUser = EaseUserUtils.getUserInfo(forward_msg.getFrom());
                if (easeUser != null) {
                    userType = new ExtUserType(easeUser.getUser_id(), easeUser.getNickname(), easeUser.getAvatar());
                } else {
                    userType = new ExtUserType(-1, forward_msg.getFrom(), null);
                }
            }

            String extMsg = null;
            try {
                extMsg = forward_msg.getJSONObjectAttribute("extMsg").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ExtTxtMessage extTxtMessage = new ExtTxtMessage("txt", ((EMTextMessageBody) forward_msg.getBody()).getMessage(),
                    TextUtils.isEmpty(userType.nick) ? forward_msg.getFrom() : userType.nick, userType.avatar, forward_msg.getMsgTime(), forward_msg.getMsgId(), forward_msg.getChatType(), forward_msg.getFrom(), forward_msg.getTo(), extMsg);
            objectList.add(extTxtMessage);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    //构造位置合并转发
    private void putExtLocationMessage(ArrayList<Object> objectList, EMMessage forward_msg) {
        try {
            Map<String, Object> ext = forward_msg.ext();
            ExtUserType userType = null;
            if (ext.containsKey(EaseConstant.EXT_USER_TYPE)) {
                String extUserType = forward_msg.getStringAttribute(EaseConstant.EXT_USER_TYPE);
                if (extUserType != null) {
                    userType = new Gson().fromJson(extUserType, ExtUserType.class);
                }
            } else {
                EaseUser easeUser = EaseUserUtils.getUserInfo(forward_msg.getFrom());
                if (easeUser != null) {
                    userType = new ExtUserType(easeUser.getUser_id(), easeUser.getNickname(), easeUser.getAvatar());
                } else {
                    userType = new ExtUserType(-1, forward_msg.getFrom(), null);
                }
            }
            EMLocationMessageBody locBody = (EMLocationMessageBody) forward_msg.getBody();

            ExtLocationMessage extLocationMessage = new ExtLocationMessage("location", TextUtils.isEmpty(userType.nick) ? forward_msg.getFrom() : userType.nick, userType.avatar, forward_msg.getMsgTime(),
                    locBody.getAddress(), locBody.getLatitude(), locBody.getLongitude(), forward_msg.getMsgId(), forward_msg.getChatType(), forward_msg.getFrom(), forward_msg.getTo());

//			ExtTxtMessage extTxtMessage = new ExtTxtMessage("txt", "[位置]" + ((EMLocationMessageBody) forward_msg.getBody()).getAddress(),
//					TextUtils.isEmpty(userType.nick) ? forward_msg.getFrom() : userType.nick, userType.avatar, forward_msg.getMsgTime());
            objectList.add(extLocationMessage);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    //构造语音合并转发
    private void putExtVoiceMessage(String remoteUrl, ArrayList<Object> objectList, EMMessage forward_msg) {
        try {
            Map<String, Object> ext = forward_msg.ext();
            ExtUserType userType = null;
            if (ext.containsKey(EaseConstant.EXT_USER_TYPE)) {
                String extUserType = forward_msg.getStringAttribute(EaseConstant.EXT_USER_TYPE);
                if (extUserType != null) {
                    userType = new Gson().fromJson(extUserType, ExtUserType.class);
                }
            } else {
                EaseUser easeUser = EaseUserUtils.getUserInfo(forward_msg.getFrom());
                if (easeUser != null) {
                    userType = new ExtUserType(easeUser.getUser_id(), easeUser.getNickname(), easeUser.getAvatar());
                } else {
                    userType = new ExtUserType(-1, forward_msg.getFrom(), null);
                }
            }
            EMVoiceMessageBody voiceMessageBody = (EMVoiceMessageBody) forward_msg.getBody();
            if (voiceMessageBody == null) {
                return;
            }

            ExtVoiceMessage extVoiceMessage = new ExtVoiceMessage("voice", TextUtils.isEmpty(userType.nick) ? forward_msg.getFrom() : userType.nick, userType.avatar, forward_msg.getMsgTime(), remoteUrl, voiceMessageBody.getLength(), forward_msg.getMsgId(), forward_msg.getChatType(), forward_msg.getFrom(), forward_msg.getTo());
//			ExtTxtMessage extTxtMessage = new ExtTxtMessage("txt", "[语音消息]",
//					TextUtils.isEmpty(userType.nick) ? forward_msg.getFrom() : userType.nick, userType.avatar, forward_msg.getMsgTime());
            objectList.add(extVoiceMessage);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    //构造File合并转发model
    private void putFileObject(ArrayList<Object> objectList, EMMessage forward_msg, String remoteUrl, String displayName, long fileSize) {
        try {
            Map<String, Object> ext = forward_msg.ext();
            ExtUserType userType = null;
            if (ext.containsKey(EaseConstant.EXT_USER_TYPE)) {
                String extUserType = forward_msg.getStringAttribute(EaseConstant.EXT_USER_TYPE);
                if (extUserType != null) {
                    userType = new Gson().fromJson(extUserType, ExtUserType.class);

                }
            } else {
                EaseUser easeUser = EaseUserUtils.getUserInfo(forward_msg.getFrom());
                if (easeUser != null) {
                    userType = new ExtUserType(easeUser.getUser_id(), easeUser.getNickname(), easeUser.getAvatar());
                } else {
                    userType = new ExtUserType(-1, forward_msg.getFrom(), null);
                }
            }

            ExtFileMessage extFileMessage = new ExtFileMessage("file", remoteUrl, displayName, fileSize, TextUtils.isEmpty(userType.nick) ? forward_msg.getFrom() : userType.nick, userType.avatar,
                    forward_msg.getMsgTime(), forward_msg.getMsgId(), forward_msg.getChatType(), forward_msg.getFrom(), forward_msg.getTo());
            objectList.add(extFileMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //构造多媒体合并转发model
    private void putMediaObject(ArrayList<Object> objectList, EMMessage forward_msg, String thumbUrl, String remoteUrl, String image, int width, int height) {
        try {
            Map<String, Object> ext = forward_msg.ext();
            ExtUserType userType = null;
            if (ext.containsKey(EaseConstant.EXT_USER_TYPE)) {
                String extUserType = forward_msg.getStringAttribute(EaseConstant.EXT_USER_TYPE);
                if (extUserType != null) {
                    userType = new Gson().fromJson(extUserType, ExtUserType.class);
                }
            } else {
                EaseUser easeUser = EaseUserUtils.getUserInfo(forward_msg.getFrom());
                if (easeUser != null) {
                    userType = new ExtUserType(easeUser.getUser_id(), easeUser.getNickname(), easeUser.getAvatar());
                } else {
                    userType = new ExtUserType(-1, forward_msg.getFrom(), null);
                }

            }

            SizeBean sizeBean = new SizeBean(width, height);
            ExtMediaMessage extMediaMessage = new ExtMediaMessage(image, thumbUrl, remoteUrl, sizeBean,
                    TextUtils.isEmpty(userType.nick) ? forward_msg.getFrom() : userType.nick, userType.avatar,
                    forward_msg.getMsgTime(), forward_msg.getMsgId(), forward_msg.getChatType(), forward_msg.getFrom(), forward_msg.getTo());
            objectList.add(extMediaMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //==============================




    protected void sendMessage(EMMessage message) {
        if (message == null) {
            return;
        }
        // Send message.
        MPMessageUtils.sendMessage(message);
    }

    protected void sendTextMessage(String toChatUsername, boolean isGroup, String content) {
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        if (isGroup) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        sendMessage(message);
    }

    protected void sendStickerMessage(String toChatUsername, boolean isGroup, String content, JSONObject extMsgJson) {
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        if (isGroup) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        if (extMsgJson != null) {
            message.setAttribute(EaseConstant.EXT_EXTMSG, extMsgJson);
        }
        sendMessage(message);
    }


    protected void sendBigExpressionMessage(String toChatUsername, boolean isGroup, String expressioName, String identityCode) {
        EMMessage message = EaseCommonUtils.createExpressionMessage(toChatUsername, expressioName, identityCode, true);
        if (isGroup) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        sendMessage(message);
    }

    protected void sendVoiceMessage(String toChatUsername, boolean isGroup, String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUsername);
        if (isGroup) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        sendMessage(message);
    }

    protected void sendImageMessage(String toChatUsername, boolean isGroup, String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toChatUsername);
        if (isGroup) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        sendMessage(message);
    }

    protected void sendLocationMessage(String toChatUsername, boolean isGroup, double latitude, double longitude, String locationAddress, String locImage) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, toChatUsername);
        if (locImage != null) {
            message.setAttribute("locImage", locImage);
        }
        if (isGroup) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        sendMessage(message);
    }

    protected void sendVideoMessage(String toChatUsername, boolean isGroup, String videoPath, String thumbPath, int videoLength) {
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, toChatUsername);
        if (isGroup) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        sendMessage(message);
    }

    protected void sendFileMessage(String toChatUsername, boolean isGroup, String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, toChatUsername);
        if (isGroup) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        sendMessage(message);
    }

    protected void forwardMessage(String toChatUsername, boolean isGroup, String forward_msg_id) {
        final EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(forward_msg_id);
        EMMessage.Type type = forward_msg.getType();
        switch (type) {
            case TXT:
                if (forward_msg.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    sendBigExpressionMessage(toChatUsername, isGroup, ((EMTextMessageBody) forward_msg.getBody()).getMessage(),
                            forward_msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null));
                } else if (EaseMessageUtils.isStickerMessage(forward_msg)) {
                    String content = ((EMTextMessageBody) forward_msg.getBody()).getMessage();
                    JSONObject jsonExtMsg = null;
                    try {
                        jsonExtMsg = forward_msg.getJSONObjectAttribute(EaseConstant.EXT_EXTMSG);
                    } catch (Exception ignored) {
                    }
                    sendStickerMessage(toChatUsername, isGroup, content, jsonExtMsg);
                } else if (EaseMessageUtils.isBurnMessage(forward_msg)) {

                } else if (EaseMessageUtils.isNameCard(forward_msg)) {
                    MessageUtils.sendMessage(forward_msg);
                } else {
                    // get the content and send it
                    String content = ((EMTextMessageBody) forward_msg.getBody()).getMessage();
                    sendTextMessage(toChatUsername, isGroup, content);
                }
                break;
            case IMAGE:
                // send image
                String filePath = ((EMImageMessageBody) forward_msg.getBody()).getLocalUrl();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        // send thumb nail if original image does not exist
                        filePath = ((EMImageMessageBody) forward_msg.getBody()).thumbnailLocalPath();
                    }
                    sendImageMessage(toChatUsername, isGroup, filePath);
                }
                break;
            case LOCATION:
                EMLocationMessageBody locBody = (EMLocationMessageBody) forward_msg.getBody();
                String locImage = forward_msg.getStringAttribute("locImage", null);
                sendLocationMessage(toChatUsername, isGroup, locBody.getLatitude(), locBody.getLongitude(), locBody.getAddress(), locImage);
                break;
            case FILE:
                EMNormalFileMessageBody fileBody = (EMNormalFileMessageBody) forward_msg.getBody();
                sendFileMessage(toChatUsername, isGroup, fileBody.getLocalUrl());
                break;
            default:
                break;
        }
    }

}
