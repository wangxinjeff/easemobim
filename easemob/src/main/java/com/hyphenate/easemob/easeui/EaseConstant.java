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
package com.hyphenate.easemob.easeui;

public class EaseConstant {
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";

    public static final String MESSAGE_TYPE_RECALL = "message_recall";

    public static final String MESSAGE_ATTR_IS_BIG_EXPRESSION = "em_is_big_expression";
    public static final String MESSAGE_ATTR_EXPRESSION_ID = "em_expression_id";

    public static final String MESSAGE_ATTR_AT_MSG = "at";
    public static final String MESSAGE_ATTR_AT_ALL_TYPE = "allusers";
    public static final String MESSAGE_ATTR_VALUE_AT_MSG_ALL = "ALL";

    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static final int CHATTYPE_CHATROOM = 3;

    public static final String EXTRA_CHAT_TYPE = "chatType";
    public static final String EXTRA_USER_ID = "userId";
    public static final String EXTRA_SELECTED_MSGID = "selectedMsgId";


    public static final String AT_ALL_USER_NAME = "all";

    public static final String SYSTEM_USER_NAME = "admin";
    public static final String WEB_USER_NAME = "/webim";

    /**
     * 设置最大发送文件大小
     */
    public static final long MSG_FILE_SEND_LIMIT = 10;

    public static final String EXTRA_CONFERENCE_ID = "confrId";
    public static final String EXTRA_CONFERENCE_PASS = "password";
    public static final String EXTRA_CONFERENCE_INVITER = "inviter";
    public static final String EXTRA_CONFERENCE_IS_CREATOR = "is_creator";
    public static final String EXTRA_CONFERENCE_CONVERSATION_ID = "conversationId";
    public static final String EXTRA_CONFERENCE_EXISTS_MEMBERS = "exists_members";

//    public static final String EXTRA_CONFERENCE_INCOMING = "conference_incoming";
    public static final String EXTRA_CONFERENCE_FORWARD_TO_AUDIO = "conf_forwardto_voice";

    public static final String MSG_ATTR_CONF_ID = EXTRA_CONFERENCE_ID;
    public static final String MSG_ATTR_CONF_PASS = EXTRA_CONFERENCE_PASS;
//    public static final String MSG_ATTR_EXTENSION = "msg_extension";
    public static final String EXTRA_CONFERENCE_TYPE = "conf_type";
    public static final String MSG_ATTR_CONFERENCE = "conference";
    public static final String EXTRA_CONFERENCE_ACTION_CANCEL = "conf_action_cancel";
    public static final String EXTRA_CONFERENCE_ACTION_BUSY = "conference_ing";

    public static final String EXT_EXTMSG = "extMsg";
    public static final String EXT_MSGTYPE = "type";
    public static final String EXT_MSGCONTENT = "content";
    public static final String EXT_USER_TYPE = "userType";
    public static final String EXT_USER_ID = "userid";
    public static final String EXT_USER_NICK = "nick";
    public static final String EXT_USER_AVATAR = "avatar";
    public static final String EXT_GROUP_TYPE = "groupType";

    public static final String INTENT_ACTION_EASEMOB_LOGIN_SUCCESS = "intent_action_easemob_login_success";
    public static final String INTENT_ACTION_USERINFO_CHANGED = "intent_action_userinfo_changed";

    // invite
    public static final String EXTRA_INVITE_USERID = "invite_userid";
    public static final String EXTRA_INVITE_NICK = "invite_nick";
    public static final String EXTRA_INVITE_USERS = "invite_users";
//    public static final String EXTRA_CHAT_HISTORY = "chat_history";
    public static final String WHITE_LIST_SHOW = "white_list_show";

    public static final int MAX_GROUP_COUNT = 500;

    //============ fav message ===========
    public static final String FAV_TRANSFER_URLS = "transfer_urls";


    //=======burn after reading==========
    public static final String BURN_AFTER_READING_DESTORY_MSGID = "destory_msg_id";
    public static final String BURN_AFTER_READING_CMD_ACTION = "destory_msg";
    public static final String BURN_AFTER_READING_READED = "readed";

    // ====== realtime location ==========
    public static final String EXT_LOCATION = "oa_location";
    public static final String EXT_LOCATION_STATE = "oa_location_state";
    public static final String EXT_LOCATION_STATE_START = "startLoc";
    public static final String EXT_LOCATION_STATE_END = "endLoc";

    public static final String ACTION_SYNC_READ = "oa_sync_read_conv";
    public static final String ACTION_SYNC_READ_CONVID = "convId";
    public static final String ACTION_SYNC_READ_CHATTYPE = "chatType";

    // ============ schedule  ============
    public static final String EXT_WITH_BUTTON = "oa_with_button";
    public static final String EXT_WITH_BUTTON_EVENT = "oa_with_button_event";
    public static final String EXT_WITH_BUTTON_EVENT_EXTRA = "oa_with_button_event_extra";
    public static final String EXT_WITH_BUTTON_EVENT_MEETING = "oa_with_button_event_meeting";
    public static final String EXT_WITH_BUTTON_EVENT_SCHEDULE = "oa_with_button_event_schedule";

    public static final String DRAFT_EXT_AT_LIST = "at_list";
    public static final String DRAFT_EXT_REFERENCE_MSG_ID = "reference_msgId";
    public static final String MSG_EXT_REFERENCE_MSG = "reference_msg";
    public static final String MSG_EXT_REFERENCE_MSG_ID = "msgId";
    public static final String MSG_EXT_REFERENCE_MSG_TYPE = "msgType";
    public static final String MSG_EXT_REFERENCE_MSG_NICK = "msgNick";
    public static final String MSG_EXT_REFERENCE_MSG_CONTENT = "msgContent";

    public static final String REFERENCE_MSG_TYPE_TXT = "txt";
    public static final String REFERENCE_MSG_TYPE_IMAGE = "image";
    public static final String REFERENCE_MSG_TYPE_VIDEO = "video";
    public static final String REFERENCE_MSG_TYPE_VOICE = "voice";
    public static final String REFERENCE_MSG_TYPE_FILE = "file";
    public static final String REFERENCE_MSG_TYPE_LOCATION = "location";
    public static final String REFERENCE_MSG_TYPE_NAME_CARD = "nameCard";
    public static final String REFERENCE_MSG_TYPE_READ_BURN = "readBurn";
    public static final String REFERENCE_MSG_TYPE_RECALL = "recall";

    public static final String MSG_EXT_VOTE = "voteJson";
    public static final String VOTE_ID = "id";
    public static final String VOTE_SUBJECT = "subject";
    public static final String VOTE_MULTIPLE_CHOICE = "multipleChoice";
    public static final String VOTE_OPTIONS = "options";
    public static final String VOTE_STATUS = "status";
    public static final String VOTE_END_TIME = "endTime";
}
