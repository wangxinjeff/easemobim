package com.hyphenate.easemob.imlibs.officeautomation.emrequest;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 30/08/2018
 */

public class EMAppUrl {
    /**
     * 登录接口
     */
//    public static final String POST_LOGIN_URL = "/v1/users/login";
    public static final String POST_LOGIN_URL = "/v2/login";
    public static final String POST_SSO_LOGIN_URL = "/login?from=%1$s&sso_token=%2$s";

    public static final String POST_AUTH_WEB_LOGIN_URL = "/v2/qrCode/token";

    public static final String POST_KICK_WEB_LOGIN = "/v2/kick";

    //版本信息
    public static final String GET_VERSION_INFO = "/v2/client-upgrades/current?platform=2";
    //获取部门根列表
    public static final String GET_ORGS_LIST = "/v2/companies/%1$s/organizations?page=%2$s&size=%3$s";

    //获取公司下子部门列表（包含子部门的子部门）
    public static final String GET_ALL_ORGS_LIST = "/v2/companies/%1$s/organizations/%2$s/lower?page=%3$s&size=%4$s";

    //创建子部门
    public static final String POST_ADD_ORG = "/v1/tenants/%s/organizations";

    //获取一级子部门
    public static final String GET_SUB_ORG = "/v2/companies/%1$s/organizations/%2$s/subs?page=%3$s&size=%4$s";

    //修改部门
    public static final String PUT_ORG = "/v1/tenants/%1$s/organizations/%2$s";

    //获取指定部门的用户列表
    public static final String GET_APPOINT_ORG_USERS = "/v2/companies/%1$s/organizations/%2$s/lower/users?page=%3$s&size=%4$s";
//    public static final String GET_APPOINT_ORG_USERS = "/v2/companies/%1$s/organizations/%2$s/lower/users/base?page=%3$s&size=%4$s";

    public static final String GET_ORG_USERS_BASE = "/v2/companies/%1$s/organizations/%2$s/users/base?page=%3$s&size=%4$s";

    //创建部门下成员
    public static final String POST_ADD_USER = "/v1/tenants/%s/users";

    //获取公司所有成员
    public static final String GET_UESRS_BASE = "/v2/companies/%1$s/users/base?page=%2$s&size=%3$s";

    //获取某一个时间段之后的公司所有成员
    public static final String GET_INCREMENT_UESRS = "/v2/companies/%1$s/users/increment?lastUpdateTime=%2$s&page=%3$s&size=%4$s";
    //获取某一个时间段之后的公司所有部门
    public static final String GET_INCREMENT_DEPARTMENT = "/v2/companies/%1$s/organizations/increment?lastUpdateTime=%2$s&page=%3$s&size=%4$s";

    //----------------------------------好友-----------------------------------
    public static final String INVITED_OR_ACCEPT_FRIEND = "/v2/friends?friendId=%1$s";
    public static final String FRIEND_RELATION_STATUS = "/v2/friends/status?friendId=%1$s";

    //审批成员加入群
    public static final String MANAGE_MEMBER_APPLY = "/v2/chatgroups/%1$s/relationships/approve?isRegion=%2$s";

    //----------------------------------星标朋友-----------------------------------
    //获取特别关心的员工列表
//    public static final String GET_STARRED_FRIENDS = "/v2/stars?page=%1$s&size=%2$s";
    public static final String GET_STARRED_FRIENDS_V3 = "/v3/stars?page=%1$s&size=%2$s";

    //获取我的好友
//    public static final String GET_MY_FRIENDS = "/v2/friends?page=%1$s&size=%2$s";
    public static final String GET_MY_FRIENDS_V3 = "/v3/friends?page=%1$s&size=%2$s";

    //添加特别关注
    public static final String POST_ADD_STARRED_FRIENDS = "/v2/stars?friendId=%1$s";

    //取消特别关注
    public static final String DELETE_STARRED_FRIENDS = "/v2/stars?friendId=%1$s";

    //判断是否是你特别关心的朋友
//    public static final String GET_IS_STARRED_FRIEND = "/v1/tenants/%1$s/users/%2$s/starfriends/%3$s";
    public static final String GET_IS_STARRED_FRIEND = "/v2/stars/starred?friendId=%1$s";

    //----------------------------------应用列表-----------------------------------

    public static final String GET_APPS_LIST = "/v2/customApplications?supportType=android&page=%1$s&size=%2$s";
    public static final String GET_COMMON_APPS_LIST = "/v2/customApplications/common?supportType=android&page=%1$s&size=%2$s";
    public static final String POST_COMMON_APPS_LIST = "/v2/users/this/options";
    public static final String GET_GROUP_APPS_LIST = "/v2/customApplications/getAllGroupApp?supportType=android";
    public static final String GET_BILL_BOARD = "/v2/billBoard/show?devType=2";

    //----------------------------------用户信息-----------------------------------
    //上传图片，获取URL
//    public static final String POST_MEDIAFILE = "/v1/tenants/%s/mediafile";
    public static final String POST_MEDIA_FILE_V2 = "/v2/mediafiles?type=%s";

    //根据appId查询用户信息
//    public static final String GET_USERINFO = "/v1/tenants/%1$s/users/%2$s";
    public static final String GET_USERINFO_V2 = "/v2/users/%s";

    //根据appId修改用户信息
//    public static final String PUT_USERINFO = "/v1/tenants/%1$s/users/%2$s";
    public static final String PUT_USERINFO_V2 = "/v2/users/%s";

    //更新自己的头像
    public static final String PUT_USER_AVATAR = "/v2/users/this";

    //根据IMID查询用户信息
//    public static final String GET_USER_BY_IMID = "/v1/tenants/%1$s/users/easemobname/%2$s";
    public static final String GET_USER_BY_IMID_V2 = "/v2/userByIm?imUsername=%s";

    //查询单个用户详情
    public static final String GET_SINGLE_USER_DETAILS = "/v2/users/%1$s/details";
    //查询单个用户基本信息
    public static final String GET_SINGLE_USER_BASE_INFO = "/v2/users?phone=%1$s";
    //获取用户列表基本信息
    public static final String GET_USER_BASE_INFO = "/v2/usersByIdListParam?idList=%1$s";

    //获取添加好友的通知（待接受/已接收）
//    public static final String GET_USER_ALL_NOTIFY = "/v2/friends/acceptingAndAccepted?page=%1$s&size=%2$s";
    public static final String GET_USER_ALL_NOTIFY_V3 = "/v3/friends/acceptingAndAccepted?page=%1$s&size=%2$s";
    //获取群组邀请通知
    public static final String GET_GROUP_ALL_NOTIFY_V2 = "/v2/chatgroups/all/relationships/approve?%1$s";

    //设置备注
    public static final String POST_ALIAS = "/v2/alias?friendId=%1$s";

    //修改密码
//    public static final String POST_PASSWORD = "/v1/tenants/%s/users/password";
    public static final String PUT_NEW_PASSWORD = "/v2/users/this/password";


    //------------------------------------搜索-------------------------------------
    //搜索联系人
    public static final String GET_USERS_SEARCH = "/v1/tenants/%1$s/users/search?username=%2$s&page=%3$s&size=%4$s";

    public static final String GET_GLOBAL_SEARCH = "/v2/globalsearch?query=%1$s&size=%2$s&type=%3$s";

    public static final String GET_MSG_SEARCH_STATISTICS = "/v1/history/message/search?beginTime=%1$s&endTime=%2$s&fromId=%3$s&queryType=%4$s&msgType=%5$s&words=%6$s";
    public static final String GET_MSG_SEARCH = "/v1/history/message/chat?beginTime=%1$s&endTime=%2$s&fromId=%3$s&toId=%4$s&queryType=%5$s&page=%6$s&pageSize=%7$s&msgType=%8$s&words=%9$s&paged=%10$s&search=%11$s&context=%12$s";
    //------------------------------------群组-------------------------------------
    //创建群组
//    public static final String POST_ADD_GROUPCHATS = "/v1/tenants/%s/groupchats";
    public static final String POST_ADD_GROUPCHATS_V2 = "/v2/chatgroups";

    //创建群组（跨区）
    public static final String POST_ADD_GROUPCHATS_CLUSTER = "/v2/chatgroup/region/create";

    // 添加群成员
    public static final String POST_ADD_MEMBER_V2 = "/v2/chatgroups/%1$s/relationships";
    public static final String POST_ADD_MEMBERLIST_V2 = "/v2/chatgroups/%1$s/relationships/batch";

    // 删除群成员
    public static final String DELETE_REMOVE_MEMBER_V2 = "/v2/chatgroups/%1$s/relationships?userId=%2$s&isRegion=%3$s";

    // 批量删除群成员
    public static final String DELETE_REMOVE_MEMBERS_V2 = "/v2/chatgroups/%1$s/relationships/batch?userIdList=%2$s&isRegion=%3$s";

    // 转让群聊
    public static final String PUT_CHANGE_GROUP_OWNER_V2 = "/v2/chatgroups/%1$s/relationships/newOwner?isRegion=%2$s";

    // 修改群信息
    public static final String PUT_GROUP_INFO_V2 = "/v2/chatgroups/%1$s?isRegion=%2$s";


    // 获取群成员列表
//    public static final String GET_MEMBER_LIST_V2 = "/v2/chatgroups/%1$s/relationships?page=%2$s&size=%3$s&isRegion=%4$s";
    public static final String GET_MEMBER_LIST_V2 = "/v2/chatgroups/%1$s/relationships?%2$s";


    // 添加群到通讯录
    public static final String SAVE_GROUP_V2 = "/v2/contract?chatGroupId=%1$s&isRegion=%2$s";

    // 群免打扰
//    public static final String POST_GROUP_DISTURB_V2 = "/v2/disturb?chatGroupId=%1$s";
    public static final String GROUP_DISTURB_V2 = "/v2/disturb?chatGroupId=%1$s&isRegion=%2$s";
    //获取群免打扰列表
    public static final String GROUPS_DISTURB = "/v2/disturb/groups?page=%1$s&size=%2$s";

    //获取群组信息
//    public static final String GET_GROUPCHATINFO = "/v1/tenants/%1$s/groupchats/%2$s";
    public static final String GET_GROUP_DETAIL_V2 = "/v2/chatgroups/%s";
    public static final String GET_GROUP_DETAIL_V2_IM = "/v2/chatgroupByIm?imChatGroupId=%s";

    public static final String GET_GROUP_DETAIL_WITH_MEMBERS_V2 = "/v2/chatgroups/%1$s/details?isRegion=%2$s";

    //获取群组列表
//    public static final String GET_GROUPLISTS = "/v1/tenants/%s/groupchats";
    public static final String GET_COLLECT_GROUPS_V2 = "/v2/chatgroups?page=%1$s&size=%2$s";

    //修改群组信息
//    public static final String PUT_GROUPINFO = "/v1/tenants/%1$s/groupchats/%2$s";
    public static final String PUT_GROUP_V2 = "/v2/chatgroups/%1$s?isRegion=%2$s";

    //删除某个群组
//    public static final String DELETE_GROUPINFO = "/v1/tenants/%1$s/groupchats/%2$s";
    public static final String DELETE_GROUP_V2 = "/v2/chatgroups/%1$s?isRegion=%2$s";

    // 退出群聊
    public static final String EXIT_GROUP_V2 = "/v2/chatgroups/%1$s/relationships/exit?isRegion=%2$s";

    // 批量群禁言
    public static final String MUTE_GROUP_MEMBERS_V2 = "/v2/chatgroups/%1$s/relationships/batch/mute?isRegion=%2$s";
    public static final String PUT_MUTE_GROUP_MEMBERS_V2 = "/v2/chatgroups/%1$s/relationships/batch/mute";

    // 解除 群禁言
    public static final String DELETE_MUTE_GROUP_MEMBER_V2 = "/v2/chatgroups/%1$s/relationships/mute?userId=%2$s&isRegion=%3$s";
    //查询群禁言
//    public static final String GET_MUTE_GROUP_MEMBER_V2 = "/v2/chatgroups/%1$s/relationships/mute?page=%2$s&size=%3$s&isRegion=%4$s";
    public static final String GET_MUTE_GROUP_MEMBER_V2 = "/v2/chatgroups/%1$s/relationships/mute?%2$s";

    // 获取用户配置列表
    public static final String GET_USER_OPTIONS = "/v2/users/this/options";

    // 获取租户配置列表
    public static final String GET_TENANT_OPTIONS = "/v2/tenants/this/options";

    // 获取会话列表 GET or POST
    public static final String GET_SESSIONS = "/v2/sessions";
    public static final String GET_SESSIONS_V3 = "/v3/sessions";

    //创建日程
    public static final String NEW_SCHEDULE = "/v2/scheduleInfo";
    // 获取以月为单位的每天是否有日程
    public static final String GET_SCHEDULE_BY_MONTH = "/v2/scheduleInfo/month?year=%1$s&month=%2$s";

    public static final String GET_SCHEDULE_RECEIVERLIST = "/v2/scheduleInfo/scheduleReceiverList?scheduleId=%s";

    //获取指定日期日程
    public static final String GET_APPOINT_DATE_SCHEDULE = "/v2/scheduleInfo/day?year=%1$s&month=%2$s&day=%3$s";
    //获取指定日期的当月日程统计
    public static final String GET_STATISTICS_SCHEDULE = "/v2/schedules/countByMonth?date=%s";
    //获取日程详情
    public static final String GET_SCHEDULE_DETAILS = "/v2/schedules/%s/details";

    //发布日程留言
    public static final String PUBLISH_SCHEDULE_MESSAGE = "/v2/schedules/%s/comment";

    //修改日程
    public static final String MODIFY_SCHEDULE_DETAIL = "/v2/scheduleInfo/%s";
    //删除日程
    public static final String DELETE_SCHEDULE = "/v2/scheduleInfo?scheduleId=%s";
    //接受日程
    public static final String ACCEPT_SCHEDULE = "/v2/schedules/%s/accept";
    //拒绝日程
    public static final String REFUSE_SCHEDULE = "/v2/schedules/%s/refuse";


    public static final String DELETE_ATTACHEMENTS = "/v2/%1$s/%2$s/attachments?list=%3$s";


    //创建任务
    public static final String NEW_TASK = "/v2/tasks";
    //获取接收的任务列表
    public static final String GET_RECEIVE_TASK_LIST = "/v2/tasksByReceiver?receiverStatus=%1$s&taskStatus=%2$s";
    //获取我的所有任务列表
    public static final String GET_MY_TASK_LIST = "/v2/tasksByUser";
    //获取创建的任务列表
    public static final String GET_CREATE_TASK_LIST = "/v2/tasksByCreator?taskStatus=%s";
    //获取抄送的任务列表
    public static final String GET_CC_TASK_LIST = "/v2/tasksByCc?taskStatus=%s";
    //获取任务详情
    public static final String GET_TASK_DETAILS = "/v2/tasks/%s/details";
    //发布任务留言
    public static final String PUBLISH_TASK_MESSAGE = "/v2/tasks/%s/comments";

    //接收方将任务标记已完成
    public static final String RECEIVER_MARK_FINISHED = "/v2/tasks/%s/finishByReceiver";
    //接收方将任务标记未完成
    public static final String RECEIVER_MARK_UNFINISHED = "/v2/tasks/%s/unfinishByReceiver";
    //创建者将接收方标记已完成
    public static final String CREATOR_MARK_FINISHED = "/v2/tasks/%s/finishByCreator";
    //创建者将接收方标记未完成
    public static final String CREATOR_MARK_UNFINISHED = "/v2/tasks/%s/unfinishByCreator";

    //删除任务
    public static final String DELETE_TASK = "/v2/tasks/%s";
    //关闭任务%2$s = close,打开任务 %2$s = open
    public static final String CLOSE_OR_OPEN_TASK = "/v2/tasks/%1$s/%2$s";

    // 删除会话
    public static final String DELETE_SESSION = "/v2/sessions?toId=%1$s&chatType=%2$s";


    //------------------------------设备信息------------------------------
    public static final String POST_DEVICE = "/v1/tenants/%s/users/device";

    //------------------------------意见反馈------------------------------
    public static final String POST_FEEDBACK = "/v1/feedbacks";

    //------------------------------找回密码------------------------------

    public static final String POST_VERIFICATIONCODE = "/v2/verification-code";

    public static final String POST_VERIFICATIONTOKEN = "/v2/verification-token";

    public static final String PUT_PASSWORD = "/v2/password?token=%s";

    //------------------------------------日程-------------------------------------
    public static final String POST_SCHEDULES = "/v1/tenants/%s/schedules";


    //------------------------------------白板-------------------------------------
    public static final String POST_CREATE_WHITEBOARDS = "%s/whiteboards";
    public static final String POST_JOIN_WHITEBOARDS_BY_ID = "%1$s/whiteboards/%2$s/url";
    public static final String POST_JOIN_WHITEBOARDS_BY_NAME = "%s/whiteboards/url-by-name";
    public static final String DELETE_DESTROY_WHITEBOARDS = "%1$s/whiteboards/%2$s";

    //==================== 自定义表情 ========================
    public static final String POST_STICKERS = "/v2/stickers";
    public static final String GET_STICKERS = "/v2/stickers?%s";
    public static final String GET_STRICKERS_DETAIL = "/v2/stickers/%s";
    public static final String PUT_STRICKER = "/v2/stickers/%s";
    public static final String DELETE_STRICKER = "/v2/stickers/%s";

    //==================== 收藏的消息 ========================
    public static final String GET_COLLECTS = "/v1/history/client/collection?fromId=%1$s&queryType=%2$s&colType=%3$s&page=%4$s&pageSize=%5$s";
    public static final String POST_COLLECT = "/v1/history/client/collection";
    public static final String DELETE_COLLECT = "/v1/history/client/collection?ids=%s";


    public static final String CREATE_CONFERENCE_WITH_MCU = "http://172.17.2.120:9095/v5/meeting/create";
    public static final String JOIN_CONFERENCE_WITH_MCU = "http://172.17.2.120:9095/v5/meeting/submitPassword";


    //test
    public static final String DIRECT_CREATE_CONFERENCE_WITH_MCU = "http://172.17.2.171:2111/c2sip/mcu/sessions";
    public static final String DIRECT_JOIN_CONFERENCE_WITH_MCU = "http://172.17.2.171:2111/c2sip/mcu/sessions/%s";


    //======================new conference======================
    //创建会议
    public static final String START_CONFERENCE = "/v2/own/conference/create";
    //输入会议ID，密码加入会议
    public static final String JOIN_CONFERENCE = "/v2/own/conference/tryToJoin";
    //获取我创建的会议列表
    public static final String CONFERENCE_LIST_OF_MINE = "/v2/own/conference/created?page=%1$s&size=%2$s";
    //获取我加入的会议列表
    public static final String CONFERENCE_LIST_OF_JOINED = "/v2/own/conference/joined?page=%1$s&size=%2$s";
    //获取会议详情
    public static final String CONFERENCE_DETAIL = "/v2/own/conference/detail/%s";
    //获取与会人员的详细信息
    public static final String CONFERENCE_PARTICIPANT_LIST = "/v2/own/conference/userDetail/%1$s?isRegion=%2$s";
    //获取会议列表的条目详情
    public static final String CONFERENCE_LIST_ITEM_DETAIL = "/v2/own/conference/totalDetail/%1$s?isRegion=%2$s";
    //从我的列表里面选择会议加入
    public static final String JOIN_CONF_FROM_LIST_ITEM = "/v2/own/conference/join";
    public static final String JOIN_CONF_FROM_LIST_ITEM_TRY = "/v2/own/conference/tryToJoin";

    //主持人关闭会议
    public static final String CLOSE_CONFERENCE_BY_HOST = "/v2/own/conference/close";

    //主持人踢出与会者
    public static final String KICK_USER_BY_HOST = "/v2/own/conference/kickUser";

    //主持人更改与会者视频权限
    public static final String CHANGE_USER_VIDEO_PERMISSION_BY_HOST = "/v2/own/conference/operateVideoState";


    //获取会议录播文件
    public static final String GET_CONF_VIDEO_FILE = "/v2/own/conference/recordInfo/%s";


    public static final String QUIET_ALL = "/v2/own/conference/quietAll/%1$s/%2$s";

    // 创建单/群聊跨区会议
    public static final String POST_REGION_CONF = "/v2/av/region/create";

    // 销毁单/群聊跨区会议
    public static final String DESTROY_REGION_CONF = "/v2/av/region/destroy/%s";

    // 获取Frtc与日程的bind接口数据
    public static final String GET_BIND_SCHEDULE = "/v2/scheduleInfo/by/binddata?bindDataType=SQT_MEETING&bindData=%s";

    //======================server config======================
    //获取服务配置
    public static final String GET_SERVICE_CONFIG = "/v2/settings/getSettings";

    //获取服务器ip配置
    public static final String GET_SERVER_JSON = "http://%s/config";

    //======================vote======================
    //创建投票
    public static final String POST_CREATE_VOTE = "/v2/groupVote";
    //进行投票
    public static final String POST_TAKE_VOTE = "/v2/groupVote/vote";
    //查询投票详情
    public static final String GET_VOTE_INFO = "/v2/groupVote/getVoteInfo?voteId=%s";
    //获取投票选项详情
    public static final String GET_VOTE_OPTION_INFO = "/v2/groupVote/getVoteDetailInfo?voteOptionId=%s";
    //结束投票
    public static final String POST_CLOSE_VOTE = "/v2/groupVote/closeVote?voteId=%s";
    //删除投票
    public static final String DELETE_VOTE = "/v2/groupVote/deleteVote?voteId=%s";

}
