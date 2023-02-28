package com.hyphenate.easemob.im.mp;

import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.easeui.domain.MPGroupMemberEntity;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.imlibs.mp.events.EventEMMessageReceived;
import com.hyphenate.easemob.imlibs.mp.events.EventEMessageSent;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupDeleted;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupNotify;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupsChanged;
import com.hyphenate.easemob.imlibs.mp.events.EventTabReceived;
import com.hyphenate.easemob.imlibs.mp.prefs.PrefsUtil;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.util.EMLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

class CmdMessageFilter {

    private static final String TAG = "CmdMessageFilter";

    static boolean dealWithServerGroupNotice(String action, EMMessage cmdMessage) {

        if (TextUtils.isEmpty(action)) {
            return false;
        }

        if (!action.startsWith("chatgroup")) {
            return false;
        }

        boolean isRegion = cmdMessage.getBooleanAttribute("isRegion", false);
        JSONContent jsonContent = getJSONContent(cmdMessage);
        if (jsonContent.jsonObj == null) {
            return true;
        }

        switch (action) {
            case "chatgroup_created": {
                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj, isRegion);
                if (groupEntity == null) {

                    return true;
                }

                groupEntity.setCluster(isRegion);
                AppHelper.getInstance().getModel().saveGroupInfo(groupEntity);
                String st3 = EaseUI.getInstance().getContext().getString(R.string.create_group_success, groupEntity.getName());
                EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.TXT);
                msg.setChatType(EMMessage.ChatType.GroupChat);
                msg.setFrom(EMClient.getInstance().getCurrentUser());
                msg.setTo(groupEntity.getImChatGroupId());
                msg.setMsgId(UUID.randomUUID().toString());
                msg.addBody(new EMTextMessageBody(st3));
                msg.setStatus(EMMessage.Status.SUCCESS);
                msg.setMsgTime(jsonContent.timestamp);
                msg.setAttribute(Constant.MESSAGE_TYPE_RECALL, true);
                // save invitation as messages
                EMClient.getInstance().chatManager().saveMessage(msg);
                // notify invitation message
                AppHelper.getInstance().getNotifier().vibrateAndPlayTone(msg);
                MPEventBus.getDefault().post(new EventGroupsChanged());

                return true;
            }
            case "chatgroup_modified": {
                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj, isRegion);
                if (groupEntity == null) {
                    return true;
                }
                groupEntity.setCluster(isRegion);
                AppHelper.getInstance().getModel().saveGroupInfo(groupEntity);

                if (groupEntity.isNoticeChanged()) {
                    EMMessage tipMessage = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    tipMessage.setFrom(groupEntity.getImChatGroupId());
                    tipMessage.addBody(new EMTextMessageBody("管理员发布了新的群公告，请及时查看"));
                    tipMessage.setAttribute(Constant.MESSAGE_TYPE_RECALL, true);
                    tipMessage.setUnread(false);
                    tipMessage.setAcked(true);
                    EMClient.getInstance().chatManager().saveMessage(tipMessage);
                }
                MPEventBus.getDefault().post(new EventGroupsChanged());

                return true;
            }
            case "chatgroup_deleted": {
                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj, isRegion);
                if (groupEntity != null) {
                    AppHelper.getInstance().getModel().deleteGroupInfoById(groupEntity.getId());
                    MPEventBus.getDefault().post(new EventGroupDeleted(groupEntity.getId(), groupEntity.getImChatGroupId()));
                }

                return true;
            }
            case "chatgroup_add_member": {
                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj, isRegion);
                if (groupEntity == null || groupEntity.getImChatGroupId() == null) {
                    MPLog.e(TAG, "received chatgroup_add_member event, but groupEntity is null or imGroupId is null");
                    return true;
                }
                groupEntity.setCluster(isRegion);
                MPGroupMemberEntity memberEntity = null;
                if (groupEntity.getMemberEntities() != null && groupEntity.getMemberEntities().size() > 0) {
                    memberEntity = groupEntity.getMemberEntities().get(0);
                }
                if (memberEntity == null) {
                    EMLog.e(TAG, "chatgroup_add_member but memberEntity is null , groupId:" + groupEntity.getId() + ",groupName:" + groupEntity.getName());
                    return true;
                }
                EaseUser inviteUser = UserProvider.getInstance().getEaseUserById(memberEntity.getCreateUserId());
                GroupBean bean = AppHelper.getInstance().getModel().getGroupInfoById(memberEntity.getChatGroupId());
                if (inviteUser == null || bean == null) {
                    MPLog.e(TAG, "chatgroup_add_member invite is null or groupbean is null");
                    return true;
                }
                String st3 = EaseUI.getInstance().getContext().getString(R.string.Invite_you_to_join_a_group_chat);
                EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                msg.setChatType(EMMessage.ChatType.GroupChat);
                msg.setFrom(inviteUser.getUsername());
                msg.setTo(bean.getImGroupId());
                msg.setMsgId(UUID.randomUUID().toString());
                msg.setMsgTime(jsonContent.timestamp);
                msg.addBody(new EMTextMessageBody(" " + st3));
                msg.setStatus(EMMessage.Status.SUCCESS);
                msg.setAttribute(EaseConstant.EXTRA_INVITE_USERID, inviteUser.getUsername());
                // save invitation as messages
                EMClient.getInstance().chatManager().saveMessage(msg);
                // notify invitation message
                AppHelper.getInstance().getNotifier().vibrateAndPlayTone(msg);
                MPEventBus.getDefault().post(new EventGroupsChanged());
                MPEventBus.getDefault().post(new EventEMMessageReceived(msg));
                return true;

            }
            case "chatgroup_remove_member_notice": {

                String realName = "";
                try{
                    realName = jsonContent.jsonObj.optString("realName");
                }catch (Exception e){}
                String st3 = EaseUI.getInstance().getContext().getString(R.string.removed_user_from_group_chat);
                EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                msg.setChatType(EMMessage.ChatType.GroupChat);
                msg.setFrom(cmdMessage.getFrom());
                msg.setTo(cmdMessage.getTo());
                msg.setMsgId(UUID.randomUUID().toString());
                msg.setMsgTime(jsonContent.timestamp);
                msg.addBody(new EMTextMessageBody(String.format(st3, realName)));
                msg.setStatus(EMMessage.Status.SUCCESS);
                msg.setAttribute(EaseConstant.MESSAGE_TYPE_RECALL, true);
                // save invitation as messages
                EMClient.getInstance().chatManager().saveMessage(msg);
                // notify invitation message
                AppHelper.getInstance().getNotifier().vibrateAndPlayTone(msg);
                MPEventBus.getDefault().post(new EventGroupsChanged());
                MPEventBus.getDefault().post(new EventEMMessageReceived(msg));
                return true;
            }
            case "chatgroup_add_member_success": {
                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj, isRegion);
                if (groupEntity == null || groupEntity.getImChatGroupId() == null) {
                    MPLog.e(TAG, "received chatgroup_add_member event, but groupEntity is null or imGroupId is null");
                    return true;
                }
                groupEntity.setCluster(isRegion);
                MPGroupMemberEntity memberEntity = null;
                if (groupEntity.getMemberEntities() != null && groupEntity.getMemberEntities().size() > 0) {
                    memberEntity = groupEntity.getMemberEntities().get(0);
                }
                if (memberEntity == null) {
                    EMLog.e(TAG, "chatgroup_add_member but memberEntity is null , groupId:" + groupEntity.getId() + ",groupName:" + groupEntity.getName());
                    return true;
                }
//                EaseUser inviteUser = UserProvider.getInstance().getEaseUserById(memberEntity.getCreateUserId());
//                GroupBean bean = AppHelper.getInstance().getModel().getGroupInfoById(memberEntity.getChatGroupId());
//                if (inviteUser == null || bean == null) {
//                    MPLog.e(TAG, "chatgroup_add_member invite is null or groupbean is null");
//                    return true;
//                }
                String st3 = EaseUI.getInstance().getContext().getString(R.string.Welcome_join_a_group_chat);
                EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                msg.setChatType(EMMessage.ChatType.GroupChat);
                msg.setFrom(memberEntity.getImUsername());
                msg.setTo(groupEntity.getImChatGroupId());
                msg.setMsgId(UUID.randomUUID().toString());
                msg.setMsgTime(jsonContent.timestamp);
                msg.addBody(new EMTextMessageBody(String.format(st3, memberEntity.getNick())));
                msg.setStatus(EMMessage.Status.SUCCESS);
//                JSONArray jsonArr = new JSONArray();
//                JSONObject jsonObj = new JSONObject();
//                try {
//                    jsonObj.put("username", memberEntity.getImUsername());
//                    jsonObj.put("nick", memberEntity.getNick());
//                } catch (JSONException e) {
//                }
//                jsonArr.put(jsonObj);
                msg.setAttribute(EaseConstant.MESSAGE_TYPE_RECALL, true);
                // save invitation as messages
                EMClient.getInstance().chatManager().saveMessage(msg);
                // notify invitation message
                AppHelper.getInstance().getNotifier().vibrateAndPlayTone(msg);
                MPEventBus.getDefault().post(new EventGroupsChanged());
                MPEventBus.getDefault().post(new EventEMMessageReceived(msg));
                return true;
            }
            case "chatgroup_add_members": {
                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj, isRegion);
                if (groupEntity == null || groupEntity.getImChatGroupId() == null) {
                    MPLog.e(TAG, "received chatgroup_add_members event, but groupEntity is null or imGroupId is null");
                    return true;
                }

                StringBuilder stringBuilder = new StringBuilder();
                boolean isFirst = true;
                MPGroupMemberEntity firstUser = null;
                if (groupEntity.getMemberEntities() != null) {
                    for (MPGroupMemberEntity entity : groupEntity.getMemberEntities()) {
                        if (entity.getUserEntity() != null) {
                            if (isFirst) {
                                isFirst = false;
                                firstUser = entity;
                            } else {
                                stringBuilder.append(",");
                            }
                            stringBuilder.append(entity.getUserEntity().getRealName());
                        }
                    }
                }

                if (firstUser == null || firstUser.getUserEntity() == null || firstUser.getUserEntity().getImUserId() == null) {
                    return true;
                }

                EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                msgNotification.setChatType(EMMessage.ChatType.GroupChat);
                EMTextMessageBody txtBody = new EMTextMessageBody(String.format("%s 加入了群聊", stringBuilder.toString()));
                msgNotification.addBody(txtBody);
                msgNotification.setFrom(firstUser.getUserEntity().getImUserId());
                msgNotification.setTo(groupEntity.getImChatGroupId());
                msgNotification.setMsgTime(jsonContent.timestamp);
                msgNotification.setMsgId(UUID.randomUUID().toString());
                msgNotification.setAttribute(EaseConstant.MESSAGE_TYPE_RECALL, true);
                msgNotification.setStatus(EMMessage.Status.SUCCESS);
                EMClient.getInstance().chatManager().saveMessage(msgNotification);
                AppHelper.getInstance().getNotifier().vibrateAndPlayTone(msgNotification);
                MPEventBus.getDefault().post(new EventEMMessageReceived(msgNotification));
                return true;
            }
            case "chatgroup_remove_member": {
                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj, isRegion);
                if (groupEntity == null) {
                    return true;
                }
                groupEntity.setCluster(isRegion);
                if (groupEntity.getMemberEntities() == null || groupEntity.getMemberEntities().isEmpty()) {
                    MPLog.e(TAG, "chatgroup_remove_member member entity is null or empty");
                    return true;
                }
                MPGroupMemberEntity memberEntity = groupEntity.getMemberEntities().get(0);

                if (memberEntity == null || memberEntity.getUserEntity() == null) {
                    MPLog.e(TAG, "chatgroup_remove_member member user entity is null");
                    return true;
                }
                EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                msgNotification.setChatType(EMMessage.ChatType.GroupChat);
                EMTextMessageBody txtBody = new EMTextMessageBody(String.format("%s 被移除群", memberEntity.getUserEntity().getRealName()));
                msgNotification.addBody(txtBody);
                msgNotification.setFrom(memberEntity.getUserEntity().getImUserId());
                msgNotification.setTo(groupEntity.getImChatGroupId());
                msgNotification.setMsgId(UUID.randomUUID().toString());
                msgNotification.setMsgTime(jsonContent.timestamp);
                msgNotification.setAttribute(EaseConstant.MESSAGE_TYPE_RECALL, true);
                msgNotification.setStatus(EMMessage.Status.SUCCESS);
                EMClient.getInstance().chatManager().saveMessage(msgNotification);
                AppHelper.getInstance().getNotifier().vibrateAndPlayTone(msgNotification);
                MPEventBus.getDefault().post(new EventEMMessageReceived(msgNotification));
                return true;
            }
            case "chatgroup_remove_members": {
                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj, isRegion);
                if (groupEntity == null) {
                    MPLog.e(TAG, "chatgroup_remove_members groupEntity is null");
                    return true;
                }
                groupEntity.setCluster(isRegion);
                List<MPGroupMemberEntity> entities = groupEntity.getMemberEntities();
                if (entities == null || entities.isEmpty()) {
                    MPLog.e(TAG, "chatgroup_remove_members memberentities is null or empty");
                    return true;
                }

                StringBuilder stringBuilder = new StringBuilder();
                boolean isFirst = true;
                MPGroupMemberEntity firstUser = null;
                for (MPGroupMemberEntity entity : entities) {
                    if (entity.getUserEntity() != null) {
                        if (isFirst) {
                            isFirst = false;
                            firstUser = entity;
                        } else {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(entity.getUserEntity().getRealName());
                    }
                }

                if (firstUser == null || firstUser.getUserEntity() == null || firstUser.getUserEntity().getImUserId() == null) {
                    return true;
                }

                EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                msgNotification.setChatType(EMMessage.ChatType.GroupChat);
                EMTextMessageBody txtBody = new EMTextMessageBody(String.format("%s 被移除群", stringBuilder.toString()));
                msgNotification.addBody(txtBody);
                msgNotification.setFrom(firstUser.getUserEntity().getImUserId());
                msgNotification.setTo(groupEntity.getImChatGroupId());
                msgNotification.setMsgTime(jsonContent.timestamp);
                msgNotification.setMsgId(UUID.randomUUID().toString());
                msgNotification.setAttribute(EaseConstant.MESSAGE_TYPE_RECALL, true);
                msgNotification.setStatus(EMMessage.Status.SUCCESS);
                EMClient.getInstance().chatManager().saveMessage(msgNotification);
                AppHelper.getInstance().getNotifier().vibrateAndPlayTone(msgNotification);
                MPEventBus.getDefault().post(new EventEMMessageReceived(msgNotification));

                return true;
            }
            case "chatgroup_remove_you": {
                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj, isRegion);
                if (groupEntity == null) {
                    MPLog.e(TAG, "chatgroup_remove_you groupEntity is null");
                    return true;
                }
                AppHelper.getInstance().getModel().deleteGroupInfoById(groupEntity.getId());
                MPEventBus.getDefault().post(new EventGroupDeleted(groupEntity.getId(), groupEntity.getImChatGroupId()));
                return true;
            }
            case "chatgroup_member_exit": {
                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj, isRegion);
                if (groupEntity == null) {
                    MPLog.e(TAG, "chatgroup_member_exit groupEntity is null");
                    return true;
                }
                groupEntity.setCluster(isRegion);
                List<MPGroupMemberEntity> entities = groupEntity.getMemberEntities();
                if (entities == null || entities.isEmpty()) {
                    MPLog.e(TAG, "chatgroup_member_exit memberentities is null or empty");
                    return true;
                }

                MPGroupMemberEntity memberEntity = entities.get(0);
                String imUser = memberEntity.getUserEntity() != null ? memberEntity.getUserEntity().getImUserId() : null;

                EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                msgNotification.setChatType(EMMessage.ChatType.GroupChat);
                EMTextMessageBody txtBody = new EMTextMessageBody(EaseUI.getInstance().getContext().getResources().getString(R.string.exited_someone));
                msgNotification.addBody(txtBody);
                msgNotification.setFrom(imUser);
                msgNotification.setMsgId(UUID.randomUUID().toString());
                msgNotification.setTo(groupEntity.getImChatGroupId());
                msgNotification.setMsgTime(jsonContent.timestamp);
                msgNotification.setAttribute(EaseConstant.EXTRA_INVITE_USERID, imUser);
                msgNotification.setStatus(EMMessage.Status.SUCCESS);
                EMClient.getInstance().chatManager().saveMessage(msgNotification);
                AppHelper.getInstance().getNotifier().vibrateAndPlayTone(msgNotification);
                MPEventBus.getDefault().post(new EventGroupsChanged());
                return true;


            }
            case "chatgroup_exit": {
                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj, isRegion);
                if (groupEntity == null) {
                    MPLog.e(TAG, "chatgroup_exit groupEntity is null");
                    return true;
                }
                AppHelper.getInstance().getModel().deleteGroupInfoById(groupEntity.getId());
                MPEventBus.getDefault().post(new EventGroupDeleted());
                return true;

            }
            case "chatgroup_add_member_approving": {
                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj, isRegion);
                if (groupEntity == null) {
                    return true;
                }
                groupEntity.setCluster(isRegion);
                ToastUtils.showShort("chatGroup approving:" + groupEntity.getName());
                return true;

            }
            case "chatgroup_add_members_approving": {
                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj, isRegion);
                if (groupEntity == null) {
                    return true;
                }
                groupEntity.setCluster(isRegion);
                MPEventBus.getDefault().post(new EventGroupNotify());
                return true;

            }
            case "chatgroup_add_member_approved": {
                MPGroupEntity groupEntity = MPGroupEntity.create(jsonContent.jsonObj);
                groupEntity.setCluster(isRegion);
                JSONObject jsonUser = jsonContent.jsonObj.optJSONObject("userChatGroupRelationship");
                MPGroupMemberEntity memberEntity = MPGroupMemberEntity.create(jsonUser, isRegion);

                String imUser = memberEntity.getUserEntity() != null ? memberEntity.getUserEntity().getImUserId() : null;

                EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                msgNotification.setChatType(EMMessage.ChatType.GroupChat);
                EMTextMessageBody txtBody = new EMTextMessageBody(EaseUI.getInstance().getContext().getResources().getString(R.string.joined_someone));
                msgNotification.addBody(txtBody);
                msgNotification.setFrom(imUser);
                msgNotification.setMsgId(UUID.randomUUID().toString());
                msgNotification.setTo(groupEntity.getImChatGroupId());
                msgNotification.setMsgTime(jsonContent.timestamp);
                msgNotification.setAttribute(EaseConstant.EXTRA_INVITE_USERID, imUser);
                msgNotification.setStatus(EMMessage.Status.SUCCESS);
                EMClient.getInstance().chatManager().saveMessage(msgNotification);
                AppHelper.getInstance().getNotifier().vibrateAndPlayTone(msgNotification);
                MPEventBus.getDefault().post(new EventGroupsChanged());
                return true;

            }
            case "chatgroup_add_member_disapproved": {
                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj, isRegion);
                if (groupEntity == null) {
                    return true;
                }
                groupEntity.setCluster(isRegion);
                ToastUtils.showShort("chatGroup disapproved" + groupEntity.getName());
                return true;

            }
            case "chatgroup_add_members_approved":
                return true;

            case "chatgroup_add_members_disapproved":
                return true;

            case "chatgroup_mute_member": {
//                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj);
//                if (groupEntity == null) {
//                    return true;
//                }
//
//                List<MPGroupMemberEntity> memberEntities = groupEntity.getMemberEntities();
//                if (memberEntities == null || memberEntities.isEmpty()) {
//                    return true;
//                }
//
//                MPGroupMemberEntity memberEntity = memberEntities.get(0);

                MPGroupEntity groupEntity = MPGroupEntity.create(jsonContent.jsonObj);
                groupEntity.setCluster(isRegion);
                JSONObject jsonUser = jsonContent.jsonObj.optJSONObject("userChatGroupRelationship");
                MPGroupMemberEntity memberEntity = MPGroupMemberEntity.create(jsonUser, isRegion);

                String imUser = memberEntity.getUserEntity() != null ? memberEntity.getUserEntity().getImUserId() : null;

                EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                msgNotification.setChatType(EMMessage.ChatType.GroupChat);
                EMTextMessageBody txtBody = new EMTextMessageBody(String.format(EaseUI.getInstance().getContext().getResources().getString(R.string.set_mute_someone), "已"));
                msgNotification.addBody(txtBody);
                msgNotification.setFrom(imUser);
                msgNotification.setTo(groupEntity.getImChatGroupId());
                msgNotification.setMsgId(UUID.randomUUID().toString());
                msgNotification.setMsgTime(jsonContent.timestamp);
                msgNotification.setAttribute(EaseConstant.EXTRA_INVITE_USERID, imUser);
                msgNotification.setStatus(EMMessage.Status.SUCCESS);
                EMClient.getInstance().chatManager().saveMessage(msgNotification);
                AppHelper.getInstance().getNotifier().vibrateAndPlayTone(msgNotification);
                MPEventBus.getDefault().post(new EventGroupsChanged());
                MPEventBus.getDefault().post(new EventEMMessageReceived(msgNotification));
                List<String> mutes = new ArrayList<>();
                mutes.add(imUser);

                AppHelper.getInstance().getModel().muteGroupUsernames(groupEntity.getImChatGroupId(), mutes, (memberEntity.getLastUpdateTime() + memberEntity.getMuteExpire()));
                return true;

            }
            case "chatgroup_remove_mute_member": {
//                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj);
//                if (groupEntity == null) {
//                    return true;
//                }
//
//                List<MPGroupMemberEntity> memberEntities = groupEntity.getMemberEntities();
//                if (memberEntities == null || memberEntities.isEmpty()) {
//                    return true;
//                }
//
//                MPGroupMemberEntity memberEntity = memberEntities.get(0);

                MPGroupEntity groupEntity = MPGroupEntity.create(jsonContent.jsonObj);
                groupEntity.setCluster(isRegion);
                JSONObject jsonUser = jsonContent.jsonObj.optJSONObject("userChatGroupRelationship");
                MPGroupMemberEntity memberEntity = MPGroupMemberEntity.create(jsonUser, isRegion);

                String imUser = memberEntity.getUserEntity() != null ? memberEntity.getUserEntity().getImUserId() : null;

                EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                msgNotification.setChatType(EMMessage.ChatType.GroupChat);
                EMTextMessageBody txtBody = new EMTextMessageBody(EaseUI.getInstance().getContext().getResources().getString(R.string.cancel_mute_someone));
                msgNotification.addBody(txtBody);
                msgNotification.setMsgId(UUID.randomUUID().toString());
                msgNotification.setFrom(imUser);
                msgNotification.setTo(groupEntity.getImChatGroupId());
                msgNotification.setMsgTime(jsonContent.timestamp);
                msgNotification.setAttribute(EaseConstant.EXTRA_INVITE_USERID, imUser);
                msgNotification.setStatus(EMMessage.Status.SUCCESS);
                EMClient.getInstance().chatManager().saveMessage(msgNotification);
                AppHelper.getInstance().getNotifier().vibrateAndPlayTone(msgNotification);
                MPEventBus.getDefault().post(new EventGroupsChanged());
                MPEventBus.getDefault().post(new EventEMMessageReceived(msgNotification));
                List<String> mutes = new ArrayList<>();
                mutes.add(imUser);
                AppHelper.getInstance().getModel().unMuteGroupUsernames(groupEntity.getImChatGroupId(), mutes);

                return true;

            }
            case "chatgroup_mute_members": {
                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj, isRegion);
                if (groupEntity == null) {
                    return true;
                }
                groupEntity.setCluster(isRegion);

                List<MPGroupMemberEntity> entities = groupEntity.getMemberEntities();
                if (entities == null || entities.isEmpty()) {
                    return true;
                }

                StringBuilder stringBuilder = new StringBuilder();
                boolean isFirst = true;
                MPGroupMemberEntity firstUser = null;
                for (MPGroupMemberEntity entity : entities) {
                    if (entity.getUserEntity() != null) {
                        if (isFirst) {
                            isFirst = false;
                            firstUser = entity;
                        } else {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(entity.getUserEntity().getRealName());
                    }
                }

                if (firstUser == null || firstUser.getUserEntity() == null || firstUser.getUserEntity().getImUserId() == null) {
                    return true;
                }

                EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                msgNotification.setChatType(EMMessage.ChatType.GroupChat);
                EMTextMessageBody txtBody = new EMTextMessageBody(String.format("%s 被禁言", stringBuilder.toString()));
                msgNotification.addBody(txtBody);
                msgNotification.setFrom(firstUser.getUserEntity().getImUserId());
                msgNotification.setTo(groupEntity.getImChatGroupId());
                msgNotification.setMsgId(UUID.randomUUID().toString());
                msgNotification.setMsgTime(jsonContent.timestamp);
                msgNotification.setAttribute(EaseConstant.MESSAGE_TYPE_RECALL, true);
                msgNotification.setStatus(EMMessage.Status.SUCCESS);
                EMClient.getInstance().chatManager().saveMessage(msgNotification);
                AppHelper.getInstance().getNotifier().vibrateAndPlayTone(msgNotification);
                MPEventBus.getDefault().post(new EventEMMessageReceived(msgNotification));


                return true;

            }
            case "chatgroup_remove_mute_members": {

                MPGroupEntity groupEntity = getGroupEntity(jsonContent.jsonObj, isRegion);

                if (groupEntity == null) {
                    return true;
                }
                groupEntity.setCluster(isRegion);
                List<MPGroupMemberEntity> entities = groupEntity.getMemberEntities();
                if (entities == null || entities.isEmpty()) {
                    return true;
                }

                StringBuilder stringBuilder = new StringBuilder();
                boolean isFirst = true;
                MPGroupMemberEntity firstUser = null;
                for (MPGroupMemberEntity entity : entities) {
                    if (entity.getUserEntity() != null) {
                        if (isFirst) {
                            isFirst = false;
                            firstUser = entity;
                        } else {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(entity.getUserEntity().getRealName());
                    }
                }

                if (firstUser == null || firstUser.getUserEntity() == null || firstUser.getUserEntity().getImUserId() == null) {
                    return true;
                }

                EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                msgNotification.setChatType(EMMessage.ChatType.GroupChat);
                EMTextMessageBody txtBody = new EMTextMessageBody(String.format("%s 移除禁言", stringBuilder.toString()));
                msgNotification.addBody(txtBody);
                msgNotification.setFrom(firstUser.getUserEntity().getImUserId());
                msgNotification.setTo(groupEntity.getImChatGroupId());
                msgNotification.setMsgId(UUID.randomUUID().toString());
                msgNotification.setMsgTime(jsonContent.timestamp);
                msgNotification.setAttribute(EaseConstant.MESSAGE_TYPE_RECALL, true);
                msgNotification.setStatus(EMMessage.Status.SUCCESS);
                EMClient.getInstance().chatManager().saveMessage(msgNotification);
                AppHelper.getInstance().getNotifier().vibrateAndPlayTone(msgNotification);
                MPEventBus.getDefault().post(new EventEMMessageReceived(msgNotification));
                return true;

            }
            case "chatgroup_block_member":
                return true;

            case "chatgroup_remove_block_member":
                return true;

            case "chatgroup_block_members":
                return true;

            case "chatgroup_remove_block_members":


                return true;

            case "chatgroup_new_owner": {
                JSONObject jsonOldOwner = jsonContent.jsonObj.optJSONObject("oldOwner");
                JSONObject jsonNewOwner = jsonContent.jsonObj.optJSONObject("newOwner");
                MPGroupMemberEntity oldOwner = MPGroupMemberEntity.create(jsonOldOwner, isRegion);
                MPGroupMemberEntity newOwner = MPGroupMemberEntity.create(jsonNewOwner, isRegion);
                if (oldOwner == null || newOwner == null) {
                    return true;
                }
                GroupBean groupBean = AppHelper.getInstance().getModel().getGroupInfoById(newOwner.getChatGroupId());
                if (groupBean == null) {
                    return true;
                }
                groupBean.setCluster(isRegion);
                if (newOwner.getUserEntity() == null || newOwner.getUserEntity().getImUserId() == null) {
                    return true;
                }
                String content = String.format("%s 成为群主", newOwner.getUserEntity().getRealName());

                EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                msgNotification.setChatType(EMMessage.ChatType.GroupChat);
                EMTextMessageBody txtBody = new EMTextMessageBody(content);
                msgNotification.addBody(txtBody);
                msgNotification.setFrom(newOwner.getUserEntity().getImUserId());
                msgNotification.setTo(groupBean.getImGroupId());
                msgNotification.setMsgId(UUID.randomUUID().toString());
                msgNotification.setMsgTime(jsonContent.timestamp);
                msgNotification.setAttribute(EaseConstant.MESSAGE_TYPE_RECALL, true);
                msgNotification.setStatus(EMMessage.Status.SUCCESS);
                EMClient.getInstance().chatManager().saveMessage(msgNotification);
                AppHelper.getInstance().getNotifier().vibrateAndPlayTone(msgNotification);
                MPEventBus.getDefault().post(new EventGroupsChanged());
                MPEventBus.getDefault().post(new EventEMMessageReceived(msgNotification));
                return true;
            }
        }
        PrefsUtil.getInstance().setTimeStamp(jsonContent.timestamp);
        return false;
    }

    static boolean dealWithScheduleCreateCmd(String action, EMMessage cmdMessage) {

        long timestamp = cmdMessage.getMsgTime();
        if ("schedule_created".equals(action)) {
            long startTime = 0;
            String title = "";
            try {
                JSONObject jsonContent = cmdMessage.getJSONObjectAttribute("content");
                String bindType = jsonContent.optString("bindDataType", "");
                startTime = jsonContent.optLong("startTime", 0);
                title = jsonContent.optString("title", "");

                if (bindType.equals("SQT_MEETING")) {
                    EMMessage meetingMsg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    meetingMsg.addBody(new EMTextMessageBody(String.format("您有新的会议邀请「%s」，请准时参加。去查看", title)));
                    meetingMsg.setFrom("admin");
                    meetingMsg.setChatType(EMMessage.ChatType.Chat);
                    meetingMsg.setStatus(EMMessage.Status.SUCCESS);
                    meetingMsg.setMsgId(UUID.randomUUID().toString());
                    meetingMsg.setUnread(true);
                    meetingMsg.setMsgTime(timestamp);
                    meetingMsg.setAttribute(EaseConstant.EXT_WITH_BUTTON, true);
                    meetingMsg.setAttribute(EaseConstant.EXT_WITH_BUTTON_EVENT, EaseConstant.EXT_WITH_BUTTON_EVENT_MEETING);
                    meetingMsg.setAttribute("type", 0); // 0 代表会议 1 代表日程
                    EMClient.getInstance().chatManager().saveMessage(meetingMsg);
                    MPEventBus.getDefault().post(new EventTabReceived(2));
                }
            } catch (HyphenateException ignored) {
            }

            EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            message.addBody(new EMTextMessageBody(String.format("您有新的待办事项「%s」。去查看", title)));
            message.setFrom("admin");
            message.setChatType(EMMessage.ChatType.Chat);
            message.setStatus(EMMessage.Status.SUCCESS);
            message.setMsgId(UUID.randomUUID().toString());
            message.setUnread(true);
            message.setMsgTime(timestamp);
            message.setAttribute(EaseConstant.EXT_WITH_BUTTON, true);
            message.setAttribute(EaseConstant.EXT_WITH_BUTTON_EVENT, EaseConstant.EXT_WITH_BUTTON_EVENT_SCHEDULE);
            message.setAttribute(EaseConstant.EXT_WITH_BUTTON_EVENT_EXTRA, startTime);
            EMClient.getInstance().chatManager().saveMessage(message);
            MPEventBus.getDefault().post(new EventEMMessageReceived(message));
            MPEventBus.getDefault().post(new EventTabReceived(3));
            return true;
        }
        else if ("schedule_modified".equals(action)) {
            long startTime = 0;
            String title = "";
            try {
                JSONObject jsonContent = cmdMessage.getJSONObjectAttribute("content");
                String bindType = jsonContent.optString("bindDataType", "");
                startTime = jsonContent.optLong("startTime", 0);
                title = jsonContent.optString("title", "");

                if (bindType.equals("SQT_MEETING")) {
                    EMMessage meetingMsg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    meetingMsg.addBody(new EMTextMessageBody(String.format("会议邀请「%s」已更新，请准时参加。去查看", title)));
                    meetingMsg.setFrom("admin");
                    meetingMsg.setChatType(EMMessage.ChatType.Chat);
                    meetingMsg.setStatus(EMMessage.Status.SUCCESS);
                    meetingMsg.setMsgId(UUID.randomUUID().toString());
                    meetingMsg.setUnread(true);
                    meetingMsg.setMsgTime(timestamp);
                    meetingMsg.setAttribute(EaseConstant.EXT_WITH_BUTTON, true);
                    meetingMsg.setAttribute(EaseConstant.EXT_WITH_BUTTON_EVENT, EaseConstant.EXT_WITH_BUTTON_EVENT_MEETING);
                    meetingMsg.setAttribute("type", 0); // 0 代表会议 1 代表日程
                    EMClient.getInstance().chatManager().saveMessage(meetingMsg);
                    MPEventBus.getDefault().post(new EventTabReceived(2));
                }
            } catch (HyphenateException ignored) {
            }

            EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            message.addBody(new EMTextMessageBody(String.format("待办事项「%s」已更新。去查看", title)));
            message.setFrom("admin");
            message.setChatType(EMMessage.ChatType.Chat);
            message.setStatus(EMMessage.Status.SUCCESS);
            message.setMsgId(UUID.randomUUID().toString());
            message.setUnread(true);
            message.setMsgTime(timestamp);
            message.setAttribute(EaseConstant.EXT_WITH_BUTTON, true);
            message.setAttribute(EaseConstant.EXT_WITH_BUTTON_EVENT, EaseConstant.EXT_WITH_BUTTON_EVENT_SCHEDULE);
            message.setAttribute(EaseConstant.EXT_WITH_BUTTON_EVENT_EXTRA, startTime);
            EMClient.getInstance().chatManager().saveMessage(message);
            MPEventBus.getDefault().post(new EventEMMessageReceived(message));
            MPEventBus.getDefault().post(new EventTabReceived(3));
            return true;

        } else if ("schedule_deleted".equals(action)) {
            String title = "";
            try {
                JSONObject jsonContent = cmdMessage.getJSONObjectAttribute("content");
                String bindType = jsonContent.optString("bindDataType", "");
                title = jsonContent.optString("title", "");

                if (bindType.equals("SQT_MEETING")) {
                    EMMessage meetingMsg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    meetingMsg.addBody(new EMTextMessageBody(String.format("会议「%s」已被取消", title)));
                    meetingMsg.setFrom("admin");
                    meetingMsg.setChatType(EMMessage.ChatType.Chat);
                    meetingMsg.setStatus(EMMessage.Status.SUCCESS);
                    meetingMsg.setMsgId(UUID.randomUUID().toString());
                    meetingMsg.setUnread(true);
                    meetingMsg.setMsgTime(timestamp);
                    EMClient.getInstance().chatManager().saveMessage(meetingMsg);
                    MPEventBus.getDefault().post(new EventTabReceived(2));
                }
            } catch (HyphenateException ignored) {
            }

            EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            message.addBody(new EMTextMessageBody(String.format("待办事项「%s」已被取消", title)));
            message.setFrom("admin");
            message.setChatType(EMMessage.ChatType.Chat);
            message.setStatus(EMMessage.Status.SUCCESS);
            message.setMsgId(UUID.randomUUID().toString());
            message.setUnread(true);
            message.setMsgTime(timestamp);
            EMClient.getInstance().chatManager().saveMessage(message);
            MPEventBus.getDefault().post(new EventEMMessageReceived(message));
            MPEventBus.getDefault().post(new EventTabReceived(3));
            return true;
        } else if ("schedule_remind".equals(action)) {
            long startTime = 0;
            String title = "";
            int remindType = 0;
            boolean isNowStart = false;
            try {
                JSONObject jsonContent = cmdMessage.getJSONObjectAttribute("content");
                startTime = jsonContent.optLong("startTime", 0);
                title = jsonContent.optString("title", "");
                remindType = jsonContent.optInt("remindType", 0);
                isNowStart = jsonContent.optBoolean("isNowStart", false);
            } catch (HyphenateException ignored) {
            }
            EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            if (isNowStart) {
                message.addBody(new EMTextMessageBody(String.format(Locale.getDefault(), "待办事项「%s」已经开始。去查看", title)));
            } else {
                message.addBody(new EMTextMessageBody(String.format(Locale.getDefault(), "待办事项「%s」还有「%s」开始。去查看", title, getRemindDuration(remindType))));
            }
            message.setFrom("admin");
            message.setChatType(EMMessage.ChatType.Chat);
            message.setStatus(EMMessage.Status.SUCCESS);
            message.setMsgId(UUID.randomUUID().toString());
            message.setUnread(true);
            message.setMsgTime(timestamp);
            message.setAttribute(EaseConstant.EXT_WITH_BUTTON, true);
            message.setAttribute(EaseConstant.EXT_WITH_BUTTON_EVENT, EaseConstant.EXT_WITH_BUTTON_EVENT_SCHEDULE);
            message.setAttribute(EaseConstant.EXT_WITH_BUTTON_EVENT_EXTRA, startTime);
            EMClient.getInstance().chatManager().saveMessage(message);
            MPEventBus.getDefault().post(new EventEMMessageReceived(message));
            MPEventBus.getDefault().post(new EventTabReceived(3));
        } else if ("schedule_disinvitation".equals(action)) {
            String title = "";
            try {
                JSONObject jsonContent = cmdMessage.getJSONObjectAttribute("content");
                String bindType = jsonContent.optString("bindDataType", "");
                title = jsonContent.optString("title", "");

                if (bindType.equals("SQT_MEETING")) {
                    EMMessage meetingMsg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    meetingMsg.addBody(new EMTextMessageBody(String.format("会议「%s」邀请已被取消", title)));
                    meetingMsg.setFrom("admin");
                    meetingMsg.setChatType(EMMessage.ChatType.Chat);
                    meetingMsg.setStatus(EMMessage.Status.SUCCESS);
                    meetingMsg.setMsgId(UUID.randomUUID().toString());
                    meetingMsg.setUnread(true);
                    meetingMsg.setMsgTime(timestamp);
                    EMClient.getInstance().chatManager().saveMessage(meetingMsg);
                    MPEventBus.getDefault().post(new EventTabReceived(2));
                }
            } catch (HyphenateException ignored) {
            }

            EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            message.addBody(new EMTextMessageBody(String.format("待办事项「%s」邀请已被取消", title)));
            message.setFrom("admin");
            message.setChatType(EMMessage.ChatType.Chat);
            message.setStatus(EMMessage.Status.SUCCESS);
            message.setMsgId(UUID.randomUUID().toString());
            message.setUnread(true);
            message.setMsgTime(timestamp);
            EMClient.getInstance().chatManager().saveMessage(message);
            MPEventBus.getDefault().post(new EventEMMessageReceived(message));
            MPEventBus.getDefault().post(new EventTabReceived(3));
        }

        return false;
    }

    private static String getRemindDuration(int remindType) {
        String remindTypeStr = "";
        switch(remindType) {
            case 1:
                remindTypeStr = "5分钟";
                break;
            case 2:
                remindTypeStr = "15分钟";
                break;
            case 3:
                remindTypeStr = "30分钟";
                break;
            case 4:
                remindTypeStr = "1小时";
                break;
            case 5:
                remindTypeStr = "1天";
                break;
            case 6:
                remindTypeStr = "1周";
                break;
        }
        return remindTypeStr;
    }

    private static MPGroupEntity getGroupEntity(JSONObject jsonObject, boolean isRegion) {
        MPGroupEntity groupEntity = MPGroupEntity.create(jsonObject);
        if (groupEntity != null) {
            groupEntity.setCluster(isRegion);
        }
        JSONArray jsonMemberArr = jsonObject.optJSONArray("userChatGroupRelationshipList");
        if (jsonMemberArr != null) {
            List<MPGroupMemberEntity> memberEntities = MPGroupMemberEntity.create(jsonMemberArr, isRegion);
            if (memberEntities != null && memberEntities.size() > 0) {
                groupEntity.setMemberEntities(memberEntities);
            }
        }
        if(jsonObject.has("addSuccessMember")) {
            MPGroupMemberEntity memberEntity = MPGroupMemberEntity.create(jsonObject.optJSONObject("addSuccessMember"), isRegion);
            List<MPGroupMemberEntity> memberEntities = new ArrayList<>();
            memberEntities.add(memberEntity);
            groupEntity.setMemberEntities(memberEntities);
        }
        return groupEntity;
    }

    private static JSONContent getJSONContent(EMMessage cmdMessage) {
        JSONContent jsonContent = new JSONContent();
//        jsonContent.timestamp = cmdMessage.getLongAttribute("sendTime", System.currentTimeMillis());
        jsonContent.timestamp = cmdMessage.getMsgTime();
        try {
            jsonContent.jsonObj = cmdMessage.getJSONObjectAttribute("content");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonContent;
    }


    static class JSONContent {
        long timestamp;
        JSONObject jsonObj;
    }

    static boolean dealWithSyncReadNotice(String action, EMMessage cmdMessage) {
        if (action.equals(EaseConstant.ACTION_SYNC_READ)) {
            String convId = cmdMessage.getStringAttribute(EaseConstant.ACTION_SYNC_READ_CONVID, "");
            int chatType = cmdMessage.getIntAttribute(EaseConstant.ACTION_SYNC_READ_CHATTYPE, 0);
            EMConversation localConv = EMClient.getInstance().chatManager().getConversation(convId, chatType == 0 ? EMConversation.EMConversationType.Chat : EMConversation.EMConversationType.GroupChat);
            localConv.markAllMessagesAsRead();
            return true;
        }
        return false;
    }

    //=====================online/offline==================
    static boolean dealWithOnlineOfflineNotice(String action, EMMessage cmdMessage) {
        if (action == null) {
            return true;
        }
        if (!action.startsWith(Constant.ACTION_ONLINE_PREFIX)) {
            return false;
        }
        if (action.equals(Constant.ACTION_ONLINE_REQUEST)) {
            ArrayList<String> userIds = new ArrayList<>(1);
            userIds.add(cmdMessage.getFrom());
            EMClient.getInstance().chatManager().addUsersToNoticeList(userIds, new EMCallBack() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(int i, String s) {
                    EMClient.getInstance().chatManager().addUsersToNoticeList(userIds, null);
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });

        } else if (action.equals(Constant.ACTION_ONLINE_CANCEL)) {
            ArrayList<String> userIds = new ArrayList<>(1);
            userIds.add(cmdMessage.getFrom());
            EMClient.getInstance().chatManager().deleteUsersFromNoticeList(userIds, new EMCallBack() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(int i, String s) {
                    EMClient.getInstance().chatManager().deleteUsersFromNoticeList(userIds, null);
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        }
        return true;
    }

}
