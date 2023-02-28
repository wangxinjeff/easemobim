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
package com.hyphenate.easemob.im.officeautomation.utils;

import com.hyphenate.easemob.easeui.EaseConstant;

public class Constant extends EaseConstant {

    //CMD消息action
    public static final String CMD_EXT_USERS = "users";
    public static final String CMD_EXT_ORGS = "orgs";
    public static final String CMD_EXT_GROUPS = "groups";
    public static final String CMD_ACTION_USERS_CHANGED = "oa_users_changed";
    public static final String CMD_ACTION_USERS_ADDED = "oa_users_added";
    public static final String CMD_ACTION_USERS_REMOVED = "oa_users_removed";
    public static final String CMD_ACTION_USERS_REFRESH = "oa_users_refresh";
    public static final String CMD_ACTION_ORGS_ADDED = "oa_orgs_added";
    public static final String CMD_ACTION_ORGS_CHANGED = "oa_orgs_changed";
    public static final String CMD_ACTION_ORGS_REMOVED = "oa_orgs_removed";
    public static final String CMD_ACTION_ORGS_REFRESH = "oa_orgs_refresh";
    public static final String CMD_ACTION_ROLES_REFRESH = "oa_roles_refresh";
    public static final String CMD_ACTION_GROUPS_CHANGED = "oa_groups_changed";
    public static final String CMD_ACTION_INVITED_FRIEND = "friend_invited";
    public static final String CMD_ACTION_ACCEPT_FRIEND = "friend_accepted";
    public static final String CMD_ACTION_DELETED_FRIEND = "friend_deleted";
    public static final String CMD_ACTION_CONF_CANCEL = "conf_action_cancel";

    public static final String CMD_ACTION_CLIENT_SYNC = "oa_client_sync";
    public static final String CMD_ACTION_CLIENT_WHITE_BOARD = "whiteBoard_send";
    public static final String CMD_ACTION_CLIENT_WHITE_BOARD_CLOSE = "whiteBoard_close";
//    public static final String CMD_ACTION_CLIENT_WHITE_BOARD = "oa_client_white_board";

    public static final String CMD_ACTION_CONFERENCE_GROUP_NOTICE = "oa_group_call_notice";

    // realtime location
    public static final String CMD_ACTION_REFRESH_LOCATION = "oa_location_refresh";
    public static final String CMD_ACTION_REFRESH_LOCATION_LOC = "oa_location";
    public static final String CMD_ACTION_REFRESH_LOCATION_LAT = "oa_location_refresh_lat";
    public static final String CMD_ACTION_REFRESH_LOCATION_LNG = "oa_location_refresh_lng";
    public static final String CMD_ACTION_REFRESH_LOCATION_RADIUS = "oa_location_refresh_radius";
    public static final String CMD_ACTION_REFRESH_LOCATION_DIRECTION = "oa_location_refresh_direction";
    public static final String MESSAGE_CONTENT_REALTIME_LOC_START = "[位置共享]";
    public static final String MESSAGE_CONTENT_REALTIME_LOC_END = "位置共享已结束";


    //    public static final String CMD_ACTION_START_LOCATION = "oa_location_start";
    public static final String CMD_ACTION_END_LOCATION = "oa_location_end";


    //specifiled
    public static final String ACTION_ACCOUNT_RELOGIN_START = "account_relogin_start";
    public static final String ACTION_ACCOUNT_RELOGIN_SUCCESS = "account_relogin_success";


    // 音视频部分
    public static final String ACTION_CONFERENCES_ACCEPTED = "conferences_accepted";
    public static final String ACTION_CONFERENCES_CANCELED = "conferences_cancel";



    //企业根节点ID
    public static final int COMPANY_BASE_ID = -1;
    public static final int PAGE_SIZE = 20;

    //===================
    public static final String SETTINGS_CHANGE_LANGUAGE = "language_change";

    //========= app lock =========
    public static final String PASSWORD_PREFERECENCE_KEY = "PASSWORD_PREFERECENCE_KEY";
    public static final String PASSWORD_SALT = "PASSWORD_SALT";
    public static final String PASSWORD_ENC_SECRET = "PASSWORD_ENC_SECRET";
    public static final String DB_SECRET = "DB_SECRET";

    //========= file upload type =========
    public static final int FILE_UPLOAD_TYPE_TEMPORARY = 0;
    public static final int FILE_UPLOAD_TYPE_AVATAR = 1;
    public static final int FILE_UPLOAD_TYPE_STICKER = 2;
    public static final int FILE_UPLOAD_TYPE_OTHER = 2;


    // ============= mute duration time ===============
    public static final int MUTE_DURATION_ONE_MINIMUTE = 60 * 1000;
    public static final int MUTE_DURATION_TEN_MINIMUTE = 10 * MUTE_DURATION_ONE_MINIMUTE;
    public static final int MUTE_DURATION_ONE_HOUR = 60 * MUTE_DURATION_ONE_MINIMUTE;
    public static final int MUTE_DURATION_ONE_DAY = 24 * MUTE_DURATION_ONE_HOUR;
    public static final int MUTE_DURATION_SEVEN_DAY = 7 * MUTE_DURATION_ONE_DAY;
    public static final int MUTE_DURATION_ONE_MOUNTH = 30 * MUTE_DURATION_ONE_DAY;

    // ================ online/offline ====================
    public static final String ACTION_ONLINE_PREFIX = "oa_online_";
    public static final String ACTION_ONLINE_REQUEST = "oa_online_request";
    public static final String ACTION_ONLINE_CANCEL = "oa_online_cancel";






}
