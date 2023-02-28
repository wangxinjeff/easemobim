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
package com.hyphenate.easemob.im.officeautomation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.widget.EaseAlertDialog;
import com.hyphenate.easemob.easeui.widget.EaseAlertDialog.AlertDialogUser;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.imlibs.message.MessageUtils;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.mp.utils.MPMessageUtils;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.adapter.SelectContactAdapter;
import com.hyphenate.easemob.im.officeautomation.domain.ExtMediaMessage;
import com.hyphenate.easemob.im.officeautomation.domain.ExtMsg;
import com.hyphenate.easemob.im.officeautomation.domain.ExtTxtMessage;
import com.hyphenate.easemob.im.officeautomation.domain.ExtUserType;
import com.hyphenate.easemob.im.officeautomation.domain.SelectUser;
import com.hyphenate.easemob.im.officeautomation.domain.SizeBean;
import com.hyphenate.easemob.im.officeautomation.ui.PickContactNoCheckboxActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by qby on 29018/06/16.
 * 转发页面
 */
public class ForwardMessageActivity extends PickContactNoCheckboxActivity {

    private static final String TAG = "ForwardMessageActivity";
    //选中的会话
    private SelectUser selectUser;
    //单条转发message ID
    private String forward_msg_id;
    //逐条、合并 转发的message list
    private ArrayList<String> forward_msg_id_list;
    //合并转发title
    private String combine_title;
    //适配器
    private SelectContactAdapter selectContactAdapter;
    //供选择的联系人列表
    private ArrayList<SelectUser> selectContactList;
    //不展示当前聊天的会话
    private String current_id;

    @Override
    public int getLayout() {
        return R.layout.em_activity_pick_contact_no_checkbox;
    }

    @Override
    protected void initData() {
        super.initData();
        Intent gIntent = getIntent();
        if (gIntent == null) return;
        forward_msg_id = gIntent.getStringExtra("forward_msg_id");
        forward_msg_id_list = gIntent.getStringArrayListExtra("forward_msg_id_list");
        combine_title = gIntent.getStringExtra("combine_title");
        current_id = gIntent.getStringExtra("current_id");

        selectContactList = new ArrayList<SelectUser>();

        getSelectContactList();
        // set adapter
        selectContactAdapter = new SelectContactAdapter(this, R.layout.ease_row_contact, selectContactList);
        listView.setAdapter(selectContactAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(position);
            }
        });
    }

    @Override
    protected void onListItemClick(int position) {
        selectUser = selectContactAdapter.getItem(position);
        if (!TextUtils.isEmpty(forward_msg_id) || (forward_msg_id_list != null && forward_msg_id_list.size() > 0)) {
            new EaseAlertDialog(this, null, getString(R.string.confirm_forward_to, selectUser.getNick()), null, new AlertDialogUser() {
                @Override
                public void onResult(boolean confirmed, Bundle bundle) {
                    if (confirmed) {
                        if (selectUser == null)
                            return;
                        if (TextUtils.isEmpty(combine_title)) {
                            try {
                                ChatActivity.activityInstance.finish();
                            } catch (Exception e) {
                            }
                            Intent intent = new Intent(ForwardMessageActivity.this, ChatActivity.class);
                            // it is single chat
                            intent.putExtra("userId", selectUser.getUsername());
                            if (TextUtils.isEmpty(forward_msg_id)) {
                                intent.putExtra("forward_msg_id_list", forward_msg_id_list);
                            } else {
                                intent.putExtra("forward_msg_id", forward_msg_id);
                            }
                            if (selectUser.getType() == EMConversation.EMConversationType.GroupChat) {
                                // it is group chat
                                intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            sendCombineExt();
                            finish();
                        }
                    }
                }
            }, true).show();
        } else {
            Intent intent = new Intent(ForwardMessageActivity.this, ChatActivity.class);
            intent.putExtra("userId", selectUser.getUsername());
            if (selectUser.getType() == EMConversation.EMConversationType.GroupChat) {
                intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
            }
            startActivity(intent);

            finish();
        }
    }

    private void sendCombineExt() {
        EMMessage msg = EMMessage.createTxtSendMessage(combine_title, selectUser.getUsername());
        if (selectUser.getType() == EMConversation.EMConversationType.GroupChat) {
            msg.setChatType(EMMessage.ChatType.GroupChat);
        } else if (selectUser.getType() == EMConversation.EMConversationType.ChatRoom) {
            msg.setChatType(EMMessage.ChatType.ChatRoom);
        }
        ArrayList<Object> objectList = new ArrayList<>();
        MPLog.d(TAG, forward_msg_id_list.size() + "条转发");
        for (int i = 0; i < forward_msg_id_list.size(); i++) {
            String id = forward_msg_id_list.get(i);
            final EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(id);
            EMMessage.Type type = forward_msg.getType();
            Map<String, Object> ext = forward_msg.ext();
            if (EMMessage.Type.TXT == type) {
                if (!ext.containsKey(EaseConstant.EXT_EXTMSG)) {
                    if (forward_msg.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
//                        String emojiconId = forward_msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null);
//                        putExtTxtMessage(objectList, forward_msg);
                        putExtSpecialObject(objectList, forward_msg);
                    } else {
                        // get the content and send it
                        putExtTxtMessage(objectList, forward_msg);
                    }
                }
            } else if (EMMessage.Type.IMAGE == type) {
                EMImageMessageBody imgBody = ((EMImageMessageBody) forward_msg.getBody());
                // send image
                String thumbnailUrl = imgBody.getThumbnailUrl();
                String remoteUrl = imgBody.getRemoteUrl();
                int width = imgBody.getWidth();
                int height = imgBody.getHeight();
                putMediaObject(objectList, forward_msg, thumbnailUrl, remoteUrl, "image", width, height);
            } else if (EMMessage.Type.VIDEO == type) {
                EMVideoMessageBody videoBody = (EMVideoMessageBody) forward_msg.getBody();
                String thumbnailUrl = videoBody.getThumbnailUrl();
                String remoteUrl1 = videoBody.getRemoteUrl();
                int width = videoBody.getThumbnailWidth();
                int height = videoBody.getThumbnailHeight();
                putMediaObject(objectList, forward_msg, thumbnailUrl, remoteUrl1, "video", width, height);
            } else {
                putExtSpecialObject(objectList, forward_msg);
            }
        }
        ExtMsg extMsg = new ExtMsg("chatMsgs", combine_title, objectList);
        //extmsg
        String extMsgJson = new Gson().toJson(extMsg);
        try {
            msg.setAttribute(EaseConstant.EXT_EXTMSG, new JSONObject(extMsgJson));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Send message.
        MPMessageUtils.sendMessage(msg);
        finish();
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
            if (ext.containsKey(EaseConstant.EXT_USER_TYPE)) {
                String extUserType = forward_msg.getStringAttribute(EaseConstant.EXT_USER_TYPE);
                if (!TextUtils.isEmpty(extUserType)) {
                    ExtUserType userType = new Gson().fromJson(extUserType, ExtUserType.class);
                    String extMsg = null;
                    try {
                        extMsg = forward_msg.getJSONObjectAttribute("extMsg").toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ExtTxtMessage extTxtMessage = new ExtTxtMessage("txt", getString(R.string.special_message), userType.nick,
                            userType.avatar, forward_msg.getMsgTime(), forward_msg.getMsgId(), forward_msg.getChatType(), forward_msg.getFrom(), forward_msg.getTo(), extMsg);
                    objectList.add(extTxtMessage);
                }
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }


    //构造文字合并转发
    private void putExtTxtMessage(ArrayList<Object> objectList, EMMessage forward_msg) {
        try {
            Map<String, Object> ext = forward_msg.ext();
            if (ext.containsKey(EaseConstant.EXT_USER_TYPE)) {
                String extUserType = forward_msg.getStringAttribute(EaseConstant.EXT_USER_TYPE);
                if (extUserType != null) {
                    ExtUserType userType = new Gson().fromJson(extUserType, ExtUserType.class);
                    String extMsg = null;
                    try {
                        extMsg = forward_msg.getJSONObjectAttribute("extMsg").toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ExtTxtMessage extTxtMessage = new ExtTxtMessage("txt", ((EMTextMessageBody) forward_msg.getBody()).getMessage(),
                            TextUtils.isEmpty(userType.nick) ? forward_msg.getFrom() : userType.nick, userType.avatar, forward_msg.getMsgTime(), forward_msg.getMsgId(), forward_msg.getChatType(), forward_msg.getFrom(), forward_msg.getTo(), extMsg);
                    objectList.add(extTxtMessage);
                }
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    //构造多媒体合并转发model
    private void putMediaObject(ArrayList<Object> objectList, EMMessage forward_msg, String thumbUrl, String remoteUrl, String image, int width, int height) {
        try {
            Map<String, Object> ext = forward_msg.ext();
            if (ext.containsKey(EaseConstant.EXT_USER_TYPE)) {
                String extUserType = forward_msg.getStringAttribute(EaseConstant.EXT_USER_TYPE);
                if (extUserType != null) {
                    ExtUserType userType = new Gson().fromJson(extUserType, ExtUserType.class);
                    SizeBean sizeBean = new SizeBean(width, height);
                    ExtMediaMessage extMediaMessage = new ExtMediaMessage(image, thumbUrl, remoteUrl, sizeBean,
                            TextUtils.isEmpty(userType.nick) ? forward_msg.getFrom() : userType.nick, userType.avatar,
                            forward_msg.getMsgTime(), forward_msg.getMsgId(), forward_msg.getChatType(), forward_msg.getFrom(), forward_msg.getTo());
                    objectList.add(extMediaMessage);
                }
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    protected void getSelectContactList() {
        selectContactList.clear();
        ArrayList<GroupBean> groupBeans = AppHelper.getInstance().getModel().getExtGroupList();
        for (int i = 0; i < groupBeans.size(); i++) {
            GroupBean groupBean = groupBeans.get(i);
            String username = groupBean.getImGroupId();
//            if (username.equalsIgnoreCase(current_id))
//                continue;
            SelectUser selectUser = new SelectUser();
            selectUser.setUsername(username);
            selectUser.setAvatar(groupBean.getAvatar());
            selectUser.setNickname(groupBean.getNick());
            selectUser.setType(EMConversation.EMConversationType.GroupChat);
            selectContactList.add(selectUser);
        }

        ArrayList<EaseUser> easeUsers = AppHelper.getInstance().getModel().getExtUserList();
        for (int i = 0; i < easeUsers.size(); i++) {
            EaseUser easeUser = easeUsers.get(i);
            String username = easeUser.getUsername();
            if (TextUtils.isEmpty(username))
                continue;
//            if (username.equalsIgnoreCase(current_id))
//                continue;
//            if (username.equalsIgnoreCase(PreferenceManager.getInstance().getLoginEaseName()))
//                continue;
            SelectUser selectUser = new SelectUser();
            selectUser.setAvatar(easeUser.getAvatar());
            selectUser.setUsername(username);
            selectUser.setId(easeUser.getUser_id());
            selectUser.setNickname(easeUser.getNickname());
            selectUser.setType(EMConversation.EMConversationType.Chat);
            selectContactList.add(selectUser);
        }

        Collections.sort(selectContactList, new Comparator<SelectUser>() {

            @Override
            public int compare(SelectUser lhs, SelectUser rhs) {
                if (lhs.getInitialLetter().equals(rhs.getInitialLetter())) {
                    return lhs.getNick().compareTo(rhs.getNick());
                } else {
                    if ("#".equals(lhs.getInitialLetter())) {
                        return 1;
                    } else if ("#".equals(rhs.getInitialLetter())) {
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });
    }

}
