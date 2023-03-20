package com.hyphenate.easemob.imlibs.officeautomation.emrequest;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.imlibs.mp.prefs.PrefsUtil;
import com.hyphenate.easemob.imlibs.mp.utils.MPUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 26/08/2018
 */

class EMRequestManager {
    private static final String TAG = EMRequestManager.class.getSimpleName();

    private static volatile EMRequestManager requestManager = null;

    private boolean useCurl = false;

    private EMRequestManager() {
    }

    public static EMRequestManager getInstance() {
        if (requestManager == null) {
            synchronized (EMRequestManager.class) {
                if (requestManager == null) {
                    requestManager = new EMRequestManager();
                }
            }
        }
        return requestManager;
    }

    private String getRealRequestUrl(String absoluteUrl) {
        return MPUtil.getRealRequestUrl(absoluteUrl);
    }

    private String getWBRealRequestUrl(String absoluteUrl) {
        if (absoluteUrl.startsWith("http")) {
            return absoluteUrl;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(PrefsUtil.getInstance().getWhiteBoardServer());
        if (!absoluteUrl.startsWith("/")) {
            stringBuilder.append("/");
        }
        String encodeUrlParams = absoluteUrl;

        stringBuilder.append(encodeUrlParams);
        return stringBuilder.toString();
    }

    //===================== base request start =========================

    protected void setHttpGetDataCallBack(String paramUrl, Map<String, String> headers,
                                          final EMDataCallBack<String> callback) {
        if (useCurl) {
            EMCurlManager.setHttpGetDataCallBack(paramUrl, headers, callback);
        } else {
            OkHttpClientManager.getInstance().getAsync(paramUrl, headers, callback);
        }

    }

    protected void setHttpPostDataCallBack(String paramUrl, Map<String, String> headers, Map<String, Object> map, final EMDataCallBack<String> callback) {
        if (useCurl) {
            EMCurlManager.setHttpPostDataCallBack(paramUrl, headers, map, callback);
        } else {
            OkHttpClientManager.getInstance().postAsync(paramUrl, headers, map, callback);
        }
    }

    protected void setHttpPostDataCallBackWithCookie(String paramUrl, String jsonBody, final EMDataCallBack<String> callback) {
        if (useCurl) {
            EMCurlManager.setHttpPostDataCallBackWithCookie(paramUrl, jsonBody, callback);
        } else {
            setHttpPostDataCallBack(paramUrl, jsonBody, callback);
        }
    }

    protected void setHttpPostDataCallBack(String paramUrl, String jsonBody, final EMDataCallBack<String> callback) {
        if (useCurl) {
            EMCurlManager.setHttpPostDataCallBack(paramUrl, jsonBody, callback);
        } else {
            if (jsonBody == null) {
                jsonBody = "";
            }
            OkHttpClientManager.getInstance().postAsync(paramUrl, null, jsonBody, callback);
        }
    }

    protected void setHttpDeleteDataCallBack(String paramUrl, final EMDataCallBack<String> callback) {
        if (useCurl) {
            EMCurlManager.setHttpDeleteDataCallBack(paramUrl, callback);
        } else {
            OkHttpClientManager.getInstance().deleteAsync(paramUrl, null, callback);
        }
    }

    protected void setHttpDeleteDataCallBack(String paramUrl, String jsonBody, final EMDataCallBack<String> callback) {
        if (useCurl) {
            EMCurlManager.setHttpDeleteDataCallBack(paramUrl, jsonBody, callback);
        } else {
            OkHttpClientManager.getInstance().deleteAsync(paramUrl, null, jsonBody, callback);
        }
    }

    protected void setHttpPutDataCallBack(String paramUrl, String jsonBody, final EMDataCallBack<String> callback) {
        if (useCurl) {
            EMCurlManager.setHttpPutDataCallBack(paramUrl, jsonBody, callback);
        } else {
            if (jsonBody == null) {
                jsonBody = "";
            }
            OkHttpClientManager.getInstance().putAsync(paramUrl, null, jsonBody, callback);
        }
    }

    protected void downloadFile(String url, String localFilePath, EMDataCallBack<String> callBack) {
        if (useCurl) {
            EMCurlManager.downloadFile(getRealRequestUrl(url), localFilePath, callBack);
        } else {
            OkHttpClientManager.getInstance().downloadAsync(getRealRequestUrl(url), localFilePath, callBack);
        }
    }
    //===================== base request end =========================

    /**
     * 登录
     *
     * @param username
     * @param password
     * @param callBack
     */
    protected void login(String username, String password, EMDataCallBack<String> callBack) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tenantId", 1);
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("deviceType", "MOBILE");

            jsonObject.put("device", MPUtil.getDeviceJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setHttpPostDataCallBackWithCookie(getRealRequestUrl(EMAppUrl.POST_LOGIN_URL), jsonObject.toString(), callBack);
    }

    /**
     * sso登录
     *
     * @param ssoToken
     * @param callBack
     */
    protected void ssoLogin(String ssoToken, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_SSO_LOGIN_URL, "mobile", ssoToken)), null, callBack);
    }


    /**
     * 获取所有子部门列表
     *
     *
     * @param companyId
     * @param callBack
     */
    protected void getAllSubOrgs(int companyId, int orgId, int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_ALL_ORGS_LIST, companyId, orgId, page, size)), null, callBack);
    }

    /**
     * 获取指定部门下的用户
     *
     * @param companyId
     * @param callBack
     */
    protected void getSubOrgsOfUsers(int companyId, int orgId, int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_APPOINT_ORG_USERS, companyId, orgId, page, size)), null, callBack);
    }

    /**
     * 获取部门列表
     *
     * @param companyId
     * @param callBack
     */
    protected void getRootOrgs(int companyId, int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_ORGS_LIST, companyId, page, size)), null, callBack);
    }

    /**
     * 添加部门
     *
     * @param tenantId
     * @param jsonBody
     * @param callBack
     */
    protected void postAddOrg(int tenantId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_ADD_ORG, tenantId)), jsonBody, callBack);
    }

    /**
     * 获取子部门列表
     *
     * @param companyId
     * @param orgId
     * @param page
     * @param size
     * @param callBack
     */
    protected void getSubOrgInfo(int companyId, int orgId, int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_SUB_ORG, companyId, orgId, page, size)), null, callBack);
    }

    /**
     * 修改部门
     *
     * @param tenantId
     * @param orgId
     * @param callBack
     */
    protected void putOrgInfo(int tenantId, int orgId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.PUT_ORG, tenantId, orgId)), jsonBody, callBack);
    }

    /**
     * 获取部门下的所有用户
     *
     * @param companyId
     * @param orgId
     * @param page
     * @param size
     * @param callBack
     */
    protected void getUsersByOrgId(int companyId, int orgId, int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_ORG_USERS_BASE, companyId, orgId, page, size)), null, callBack);
    }

    /**
     * 增加用户
     *
     * @param tenantId
     * @param jsonBody
     * @param callBack
     */
    protected void addUser(int tenantId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_ADD_USER, tenantId)), jsonBody, callBack);
    }


    /**
     * 获取公司下所有成员
     *
     * @param companyId
     * @param callBack
     */
    protected void getAllUsers(int companyId, int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_UESRS_BASE, companyId, page, size)), null, callBack);
    }

    /**
     * 根据上次获取公司人员最后一次时间，进行增量更新
     *
     * @param companyId
     * @param lastTime
     * @param page
     * @param size
     * @param callBack
     */
    protected void getIncrementUsersByLastTime(int companyId, long lastTime, int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_INCREMENT_UESRS, companyId, lastTime, page, size)), null, callBack);
    }

    /**
     * 根据上次获取公司部门最后一次时间，进行增量更新
     *
     * @param companyId
     * @param lastTime
     * @param page
     * @param size
     * @param callBack
     */
    protected void getIncrementDepartmentsByLastTime(int companyId, long lastTime, int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_INCREMENT_DEPARTMENT, companyId, lastTime, page, size)), null, callBack);
    }

    /**
     * 获取所有星标好友
     *
     * @param callBack
     */
    protected void getStaredFriends(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_STARRED_FRIENDS_V3, page, size)), null, callBack);
    }

    /**
     * 获取我的好友
     *
     * @param callBack
     */
    protected void getMyFriends(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_MY_FRIENDS_V3, page, size)), null, callBack);
    }

    /**
     * 添加星标好友
     *
     * @param friendUserId
     * @param jsonBody
     * @param callBack
     */
    protected void addStaredFriends(int friendUserId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_ADD_STARRED_FRIENDS, friendUserId)), jsonBody, callBack);
    }

    /**
     * 取消星标好友
     *
     * @param friendUserId
     * @param callBack
     */
    protected void deleteStaredFriends(int friendUserId, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_STARRED_FRIENDS, friendUserId)), callBack);
    }


    /**
     * 获取此用户是否为星标好友
     *
     * @param friendUserId
     * @param callBack
     */
    protected void getIsStaredFriend(int friendUserId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_IS_STARRED_FRIEND, friendUserId)), null, callBack);
    }

    /**
     * 上传文件
     * 0 临时文件
     * 1 头像
     * 2 其他
     *
     * @param callBack
     */
    protected void postFile(File file, int type, final EMDataCallBack<String> callBack) {
        String remoteUrl = getRealRequestUrl(String.format(EMAppUrl.POST_MEDIA_FILE_V2, type));
        if (useCurl) {
            EMCurlManager.postFile(remoteUrl, file, type, callBack);
        } else {
            try {
                OkHttpClientManager.getInstance().postAsync(remoteUrl, callBack, file, "file");
            } catch (IOException e) {
                e.printStackTrace();
                if (callBack != null) {
                    callBack.onError(-1, e.getMessage());
                }
            }
        }
    }

    /**
     * 获取用户信息
     *
     * @param userId
     * @param callBack
     */
    protected void getUserInfo(int userId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_USERINFO_V2, userId)), null, callBack);
    }

    /**
     * 修改用户信息
     *
     * @param userId
     * @param jsonBody
     * @param callBack
     */
    protected void putUserInfo(int userId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(EMAppUrl.PUT_USER_AVATAR), jsonBody, callBack);
    }
//    /**
//     * 修改用户信息
//     *
//     * @param userId
//     * @param jsonBody
//     * @param callBack
//     */
//    protected Call putUserInfo(int userId, String jsonBody, EMDataCallBack<String> callBack) {
//        return setHttpPutDataCallBack(String.format(getRealRequestUrl(EMAppUrl.PUT_USERINFO_V2), userId), jsonBody, callBack);
//    }

    /**
     * 更新自己的头像
     */
    protected void putUserAvatar(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(EMAppUrl.PUT_USER_AVATAR), jsonBody, callBack);
    }

    /**
     * 通过ImUser获取用户信息
     *
     * @param imUsername
     * @param callBack
     */
    protected void getUserByImUser(String imUsername, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_USER_BY_IMID_V2, imUsername)), null, callBack);
    }

    /**
     * 通过手机号获取用户基本信息
     *
     * @param phone
     * @param callBack
     */
    protected void getSingleUserBaseInfo(String phone, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_SINGLE_USER_BASE_INFO, phone)), null, callBack);
    }

    /**
     * 通过userId获取用户详细信息
     *
     * @param userId
     * @param callBack
     */
    protected void getUserDetails(int userId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_SINGLE_USER_DETAILS, userId)), null, callBack);
    }

    /**
     * 通过userId集合获取用户列表基本信息
     *
     * @param userIds
     * @param callBack
     */
    protected void getUserBaseInfo(String userIds, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_USER_BASE_INFO, userIds)), null, callBack);
    }

    /**
     * 获取添加好友所有通知
     *
     * @param callBack
     */
    protected void getUserAllNotify(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_USER_ALL_NOTIFY_V3, page, size)), null, callBack);
    }

    /**
     * 获取群组邀请所有通知
     *
     * @param callBack
     */
    protected void getGroupAllNotify(String params, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_GROUP_ALL_NOTIFY_V2, params)), null, callBack);
    }

    /**
     * 添加备注
     *
     * @param userId
     * @param jsonBody
     * @param callBack
     */
    protected void postAlias(int userId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_ALIAS, userId)), jsonBody, callBack);
    }

    /**
     * 修改密码
     *
     * @param callBack
     */
    protected void putUpdatePassword(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(EMAppUrl.PUT_NEW_PASSWORD), jsonBody, callBack);
    }


    /**
     * 搜索用户
     *
     * @param tenantId
     * @param username
     * @param page
     * @param size
     * @param callBack
     */
    protected void getSearchUser(int tenantId, String username, int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_USERS_SEARCH, tenantId, username, page, size)), null, callBack);
    }

    /**
     * 搜索全部
     *
     * @param query
     * @param size
     * @param type
     * @param callBack
     */
    protected void getGlobalSearch(String query, int size, String type, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_GLOBAL_SEARCH, query, size, type)), null, callBack);
    }

    /**
     * 获取搜索的消息的统计数
     *
     * @param beginTime 起始时间
     * @param endTime   结束时间
     * @param fromId    im id
     * @param queryType chat/groupchat
     * @param msgType   消息类型txt
     * @param word      搜索的内容
     * @param callBack
     */
    protected void getSearchMsgStatistics(long beginTime, long endTime, String fromId, String queryType, String msgType, String word, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_MSG_SEARCH_STATISTICS, beginTime, endTime, fromId, queryType, msgType, word)), null, callBack);
    }

    /**
     * 搜索要展示的消息
     *
     * @param beginTime 起始时间
     * @param endTime   结束时间
     * @param fromId    im ids
     * @param toId      im ids
     * @param queryType chat/groupchat
     * @param page      页数
     * @param pageSize  条数
     * @param msgType   消息类型txt,img...
     * @param words     搜索的内容
     * @param paged     是否分页
     * @param search    是否搜索
     * @param context   获取上下文
     * @param callBack
     */
    protected void getSearchMsg(long beginTime, long endTime, String fromId, String toId, String queryType, int page, int pageSize, String msgType, String words, boolean paged, boolean search, boolean context, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_MSG_SEARCH, beginTime, endTime, fromId, toId, queryType, page, pageSize, msgType, words, paged, search, context)), null, callBack);
    }

    /**
     * 创建群组
     *
     * @param jsonBody
     * @param callBack
     */
    protected void postCreateGroup(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_ADD_GROUPCHATS_V2), jsonBody, callBack);
    }

    /**
     * 创建群组(跨区)
     *
     * @param jsonBody
     * @param callBack
     */
    protected void postCreateGroupCluster(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_ADD_GROUPCHATS_CLUSTER), jsonBody, callBack);
    }

    /**
     * 添加群成员
     *
     * @param jsonBody {
     *                 "userId":7
     *                 }
     */
    protected void addMemberToGroup(int groupId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_ADD_MEMBER_V2, groupId)), jsonBody, callBack);
    }

    /**
     * 添加群成员 批量
     *
     * @param groupId
     * @param jsonBody
     * @param callBack
     * @return
     */
    protected void addMembersToGroup(int groupId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_ADD_MEMBERLIST_V2, groupId)), jsonBody, callBack);
    }

    /**
     * 删除群成员
     */
    protected void deleteMemberFromGroup(int groupId, int removedUserId, boolean isRegion,EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_REMOVE_MEMBER_V2, groupId, removedUserId, isRegion ? 1 : 0)), callBack);
    }

    /**
     * 批量删除群成员
     */
    protected void deleteMembersFromGroup(int groupId, String removedUserIds, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_REMOVE_MEMBERS_V2, groupId, removedUserIds, isRegion ? 1 : 0)), callBack);
    }

    /**
     * 群主转移
     */
    protected void changeGroupOwner(int groupId, boolean isRegion, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.PUT_CHANGE_GROUP_OWNER_V2, groupId, isRegion ? 1 : 0)), jsonBody, callBack);
    }

    /**
     * 修改群名称
     */
    protected void changeGroupInfo(int groupId, boolean isRegion, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.PUT_GROUP_INFO_V2, groupId, isRegion ? 1 : 0)), jsonBody, callBack);
    }


    /**
     * 获取成员列表
     */
    protected void getMemberList(int groupId, String params, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_MEMBER_LIST_V2, groupId, params)), null, callBack);
    }

    /**
     * 保存群到通讯录
     *
     * @param groupId
     * @param callBack
     * @return
     */
    protected void saveGroupToContact(int groupId, String jsonBody, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.SAVE_GROUP_V2, groupId, isRegion)), jsonBody, callBack);
    }

    /**
     * 从通讯录中删除群
     */
    protected void deleteGroupFromContract(int groupId, boolean isRegion,  EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.SAVE_GROUP_V2, groupId, isRegion)), callBack);
    }

    /**
     * 添加群免打扰
     */
    protected void addGroupDisturb(int groupId, String jsonBody, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GROUP_DISTURB_V2, groupId, isRegion)), jsonBody, callBack);
    }

    /**
     * 获取群免打扰列表
     */
    protected void getGroupsDisturb(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GROUPS_DISTURB, page, size)), null, callBack);
    }

    /**
     * 去除群免打扰
     */
    protected void removeGroupDisturb(int groupId, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GROUP_DISTURB_V2, groupId, isRegion)), callBack);
    }

    /**
     * 获取群信息
     *
     * @param groupId
     * @param callBack
     */
    protected void getGroupInfo(int groupId, EMDataCallBack<String> callBack) {
        getGroupInfoById(groupId, callBack);
    }

    /**
     * 获取群信息
     *
     * @param imGroupId
     * @param callBack
     */
    protected void getGroupInfo(String imGroupId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_GROUP_DETAIL_V2_IM, imGroupId)), null, callBack);
    }

    protected void getGroupInfoById(int groupId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_GROUP_DETAIL_V2, groupId)), null, callBack);
    }

    /**
     * 获取群成员，带成员
     *
     * @param groupId
     * @param isRegion
     * @param callBack
     * @return
     */
    protected void getGroupDetailWithMemberList(int groupId, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_GROUP_DETAIL_WITH_MEMBERS_V2, groupId, isRegion ? 1 : 0)), null, callBack);
    }

    /**
     * 获取我收藏的群组
     *
     * @param callBack
     */
    protected void getCollectedGroups(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_COLLECT_GROUPS_V2, page, size)), null, callBack);
    }

    /**
     * 修改群信息
     *
     * @param groupId
     * @param isRegion
     * @param jsonBody
     * @param callBack
     */
    protected void putGroupInfo(int groupId, boolean isRegion, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.PUT_GROUP_V2, groupId, isRegion ? 1 : 0)), jsonBody, callBack);
    }

    /**
     * 删除群组
     *
     * @param groupId
     * @param callBack
     */
    protected void deleteGroup(int groupId, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_GROUP_V2, groupId, isRegion ? 1 : 0)), callBack);
    }

    /**
     * 退出群组
     *
     * @param groupId
     * @param callBack
     */
    protected void exitGroup(int groupId, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.EXIT_GROUP_V2, groupId, isRegion ? 1 : 0)), callBack);
    }

    /**
     * 批量禁言群成员
     */
    protected void muteGroupMembers(int groupId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.PUT_MUTE_GROUP_MEMBERS_V2, groupId)), jsonBody, callBack);
    }

    /**
     * 解除禁言
     */
    protected void unMuteGroupMember(int groupId, int userId, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_MUTE_GROUP_MEMBER_V2, groupId, userId, isRegion ? 1 : 0)), callBack);
    }

    /**
     * 获取禁言列表
     */
    protected void getMuteGroupMembers(int groupId, String params, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_MUTE_GROUP_MEMBER_V2, groupId, params)), null, callBack);
    }

    /**
     * 上传设备信息
     *
     * @param tenantId
     * @param callBack
     */
    protected void postDeviceInfo(int tenantId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_DEVICE, tenantId)), jsonBody, callBack);
    }

    /**
     * 意见反馈
     *
     * @param jsonBody
     * @param callBack
     */
    protected void postFeedBack(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_FEEDBACK), jsonBody, callBack);
    }

    /**
     * 请求验证码
     *
     * @param jsonBody
     * @param callBack
     */
    protected void postVerificationCode(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_VERIFICATIONCODE), jsonBody, callBack);
    }

    /**
     * 请求授权token
     *
     * @param jsonBody
     * @param callBack
     */
    protected void postVerificationToken(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_VERIFICATIONTOKEN), jsonBody, callBack);
    }


    /**
     * 设置密码
     *
     * @param token
     * @param jsonBody
     * @param callBack
     */
    protected void putPassword(String token, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.PUT_PASSWORD, token)), jsonBody, callBack);
    }

    /**
     * 设置日程提醒
     *
     * @param tenantId
     * @param jsonBody
     * @param callBack
     */
    protected void postSchedule(long tenantId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_SCHEDULES, tenantId)), jsonBody, callBack);
    }

    /**
     * 创建白板
     *
     * @param whiteBoardName
     * @param userId
     * @param callBack
     */
    protected void postCreateWhiteBoards(String whiteBoardName, String userId, EMDataCallBack<String> callBack) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userId", userId);
            jsonBody.put("whiteBoardName", whiteBoardName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setHttpPostDataCallBack(getWBRealRequestUrl(String.format(EMAppUrl.POST_CREATE_WHITEBOARDS, MPClient.get().getAppkey().replace("#", "/"))), jsonBody.toString(), callBack);
    }

    /**
     * 通过白板id加入白板
     *
     * @param whiteBoardId
     * @param userId
     * @param callBack
     */
    protected void postJoinWhiteBoardsById(String whiteBoardId, String userId, EMDataCallBack<String> callBack) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setHttpPostDataCallBack(getWBRealRequestUrl(String.format(EMAppUrl.POST_JOIN_WHITEBOARDS_BY_ID, MPClient.get().getAppkey().replace("#", "/"), whiteBoardId)), jsonBody.toString(), callBack);
    }

    /**
     * 通过白板名字加入白板
     *
     * @param whiteBoardName
     * @param userId
     * @param callBack
     */
    protected void postJoinWhiteBoardsByName(String whiteBoardName, String userId, EMDataCallBack<String> callBack) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userId", userId);
            jsonBody.put("whiteBoardName", whiteBoardName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setHttpPostDataCallBack(getWBRealRequestUrl(String.format(EMAppUrl.POST_JOIN_WHITEBOARDS_BY_NAME, MPClient.get().getAppkey().replace("#", "/"))), jsonBody.toString(), callBack);
    }

    /**
     * 销毁白板
     *
     * @param whiteBoardId
     * @param userId
     * @param callBack
     */
    protected void postDestroyWhiteBoards(String whiteBoardId, String userId, EMDataCallBack<String> callBack) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setHttpDeleteDataCallBack(getWBRealRequestUrl(String.format(EMAppUrl.DELETE_DESTROY_WHITEBOARDS, MPClient.get().getAppkey().replace("#", "/"), whiteBoardId)), jsonBody.toString(), callBack);
    }


    /**
     * 删除贴纸
     *
     * @param stickerId
     * @param callBack
     */
    protected void deleteSticker(final int stickerId, final EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_STRICKER, stickerId)), callBack);
    }

    /**
     * 修改贴纸
     *
     * @param stickerId
     * @param jsonBody
     * @param callBack
     */
    protected void putSticker(final int stickerId, final String jsonBody, final EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.PUT_STRICKER, stickerId)), jsonBody, callBack);
    }

    /**
     * 添加贴纸
     *
     * @param jsonBody
     * @param callBack
     */
    protected void postSticker(final String jsonBody, final EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_STICKERS), jsonBody, callBack);
    }

    /**
     * 获取贴纸列表
     *
     * @param groupName
     * @param callBack
     */
    protected void getStickers(final String groupName, final EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_STICKERS, "page=0&size=200&groupName=" + groupName)), null, callBack);
    }

    /**
     * 获取收藏的消息列表
     *
     * @param queryType groupchat/chat (群聊/单聊) 默认:all
     * @param colType   img,txt.. 默认:all
     * @param page
     * @param pageSize
     * @param callBack
     */
    protected void getCollectMsgs(String queryType, String colType, int page, int pageSize, final EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_COLLECTS, EMClient.getInstance().getCurrentUser(), queryType, colType, page, pageSize)), null, callBack);
    }

    /**
     * 添加收藏
     *
     * @param jsonBody
     * @param callBack
     */
    protected void postCollect(String jsonBody, final EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_COLLECT), jsonBody, callBack);
    }

    /**
     * 删除收藏
     *
     * @param msgId
     * @param callBack
     */
    protected void deleteCollect(String msgId, final EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_COLLECT, msgId)), callBack);
    }

    /**
     * 扫码登陆web
     *
     * @param authId
     * @param callBack
     */
    protected void authWebLogin(String authId, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_AUTH_WEB_LOGIN_URL), authId, callBack);
    }

    /**
     * 移动端踢出web端
     *
     * @param callBack
     */
    protected void kickWeb(EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_KICK_WEB_LOGIN), null, callBack);
    }

    /**
     * 添加好友/接受好友
     *
     * @param friendId
     * @param callBack
     */
    protected void invitedOrAcceptFriend(int friendId, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.INVITED_OR_ACCEPT_FRIEND, String.valueOf(friendId))), null, callBack);
    }

    /**
     * 审批成员入群申请
     *
     * @param chatGroupId
     * @param jsonBody
     * @param callBack
     */
    protected void approveMemberApply(int chatGroupId, boolean isRegion,  String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.MANAGE_MEMBER_APPLY, String.valueOf(chatGroupId), isRegion ? 1 : 0)), jsonBody, callBack);
    }

    /**
     * 删除好友
     *
     * @param friendId
     * @param callBack
     */
    protected void deleteFriend(int friendId, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.INVITED_OR_ACCEPT_FRIEND, String.valueOf(friendId))), callBack);
    }

    /**
     * 是否是好友关系
     *
     * @param friendId
     * @param callBack
     */
    protected void friendRelationStatus(int friendId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.FRIEND_RELATION_STATUS, String.valueOf(friendId))), null, callBack);
    }

    /**
     * 获取应用列表
     *
     * @param callBack
     */
    protected void getAppListFromServer(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_APPS_LIST, page, size)), null, callBack);
    }

    /**
     * 获取常用应用列表
     *
     * @param callBack
     */
    protected void getCommonAppListFromServer(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_COMMON_APPS_LIST, page, size)), null, callBack);
    }

    /**
     * 添加常用应用列表
     *
     * @param callBack
     */
    protected void addCommonAppListFromServer(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_COMMON_APPS_LIST), jsonBody, callBack);
    }

    /**
     * 获取分组应用列表
     *
     * @param callBack
     */
    protected void getGroupAppListFromServer(EMDataCallBack<String> callBack){
        setHttpGetDataCallBack(getRealRequestUrl(EMAppUrl.GET_GROUP_APPS_LIST), null, callBack);
    }

    /**
     * 获取轮播图
     *
     * @param callBack
     */
    protected void getBillBoardFromServer(EMDataCallBack<String> callBack){
        setHttpGetDataCallBack(getRealRequestUrl(EMAppUrl.GET_BILL_BOARD), null, callBack);
    }

    /**
     * 获取版本状态
     *
     * @param callBack
     */
    protected void getVersionStatus(EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(EMAppUrl.GET_VERSION_INFO), null, callBack);
    }

    /**
     * 新版本下载
     *
     * @param callBack
     */
//    protected void downloadApk(String url, EMDataCallBack<String> callBack) {
//        setHttpPostDataCallBack(getRealRequestUrl(url), null, callBack);
//    }

    /**
     * 获取用户配置状态
     */
    protected void getUserOptions(EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(EMAppUrl.GET_USER_OPTIONS), null, callBack);
    }

    /**
     * 获取租户配置
     */
    protected void getTenantOptions(EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(EMAppUrl.GET_TENANT_OPTIONS), null, callBack);
    }

    /**
     * 获取会话列表
     */
    protected void getSessions(EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(EMAppUrl.GET_SESSIONS_V3), null, callBack);
    }

    /**
     * 删除会话
     */
    protected void deleteSession(String toId, String chatType, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_SESSION, toId, chatType)), callBack);
    }

    /**
     * 添加会话
     */
    protected void postSession(String reqBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.GET_SESSIONS), reqBody, callBack);
    }

    /**
     * 修改会话
     */
    protected void putSession(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(EMAppUrl.GET_SESSIONS), jsonBody, callBack);
    }

    /**
     * 创建日程
     */
    protected void newSchedule(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.NEW_SCHEDULE), jsonBody, callBack);
    }

    /**
     * 以月为单位获取每天是否有日程
     * @param year
     * @param month
     * @param callBack
     */
    protected void getScheduleByMonth(int year, int month, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(String.format(getRealRequestUrl(EMAppUrl.GET_SCHEDULE_BY_MONTH), year, month), null, callBack);
    }

    /**
     * 获取指定日期日程
     */
    protected void getAppointDateSchedules(int year, int month, int day, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_APPOINT_DATE_SCHEDULE, year, month, day)), null, callBack);
    }

    /**
     * 获取日程分配人
     * @param scheduleId
     * @param callBack
     */
    protected void getScheduleReceiverList(int scheduleId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_SCHEDULE_RECEIVERLIST, scheduleId)), null, callBack);
    }

    /**
     * 获取指定日期的当月日程统计
     */
    protected void getStatisticsSchedules(String appointDate, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_STATISTICS_SCHEDULE, appointDate)), null, callBack);
    }

    /**
     * 获取日程详情
     */
    protected void getScheduleDetails(int scheduleId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_SCHEDULE_DETAILS, scheduleId)), null, callBack);
    }

    /**
     * 日程留言
     */
    protected void publishScheduleMessage(int scheduleId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.PUBLISH_SCHEDULE_MESSAGE, scheduleId)), jsonBody, callBack);
    }


    /**
     * 删除日程
     */
    protected void deleteSchedule(int scheduleId, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_SCHEDULE, scheduleId)), callBack);
    }

    /**
     * 修改日程
     */
    protected void modifySchedule(int scheduleId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.MODIFY_SCHEDULE_DETAIL, scheduleId)), jsonBody, callBack);
    }


    /**
     * 接受日程
     */
    protected void acceptSchedule(int scheduleId, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.ACCEPT_SCHEDULE, scheduleId)), null, callBack);
    }

    /**
     * 拒绝日程
     */
    protected void refuseSchedule(int scheduleId, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.REFUSE_SCHEDULE, scheduleId)), null, callBack);
    }

    /**
     * 删除附件
     * type      0 schedules 1 tasks
     */
    protected void deleteAttachments(int scheduleId, String fileIds, int type, EMDataCallBack<String> callBack) {
        String deleteType;
        if (type == 0) {
            deleteType = "schedules";
        } else {
            deleteType = "tasks";
        }
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_ATTACHEMENTS, deleteType, scheduleId, fileIds)), callBack);
    }

    /**
     * 创建任务
     */
    protected void newTask(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.NEW_TASK), jsonBody, callBack);
    }

    /**
     * 获取已完成/未完成的任务列表
     */
    protected void getReceiveTaskList(int status, int isClosed, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_RECEIVE_TASK_LIST, status, isClosed)), null, callBack);
    }

    /**
     * 获取我收到的所有任务列表
     */
    protected void getMyTaskList(EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(EMAppUrl.GET_MY_TASK_LIST), null, callBack);
    }

    /**
     * 获取创建的任务列表
     */
    protected void getCreateTaskList(int isClosed, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_CREATE_TASK_LIST, isClosed)), null, callBack);
    }

    /**
     * 获取抄送的任务列表
     */
    protected void getCcTaskList(int isClosed, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_CC_TASK_LIST, isClosed)), null, callBack);
    }

    /**
     * 获取任务详情
     */
    protected void getTaskDetails(int taskId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_TASK_DETAILS, taskId)), null, callBack);
    }

    /**
     * 任务留言
     */
    protected void publishTaskMessage(int taskId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.PUBLISH_TASK_MESSAGE, taskId)), jsonBody, callBack);
    }

    /**
     * 接收方/创建者 标记已完成/未完成
     */
    protected void markFinishedStatus(int taskId, String who, int finishedStatus, String jsonBody, EMDataCallBack<String> callBack) {
        if ("rec".equals(who)) {
            if (finishedStatus == 0) {
                setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.RECEIVER_MARK_UNFINISHED, taskId)), jsonBody, callBack);
            } else {
                setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.RECEIVER_MARK_FINISHED, taskId)), jsonBody, callBack);
            }
        } else {
            if (finishedStatus == 0) {
                setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.CREATOR_MARK_UNFINISHED, taskId)), jsonBody, callBack);
            } else {
                setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.CREATOR_MARK_FINISHED, taskId)), jsonBody, callBack);
            }
        }
    }

    /**
     * 任务删除
     */
    protected void deleteTask(int taskId, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_TASK, taskId)), callBack);
    }

    /**
     * 任务修改
     */
    protected void modifyTask(int taskId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_TASK, taskId)), jsonBody, callBack);
    }

    /**
     * 任务关闭
     *
     * @param status close 关闭，open 打开
     */
    protected void closeOrOpenTask(int taskId, String status, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.CLOSE_OR_OPEN_TASK, taskId, status)), null, callBack);
    }

    /**
     * 通过mcu创建会议
     *
     * @param jsonBody
     */
    protected void createConferenceWithMCU(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.CREATE_CONFERENCE_WITH_MCU), jsonBody, callBack);
    }

    /**
     * 通过mcu加入会议
     *
     * @param jsonBody
     */
    protected void joinConferenceWithMCU(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.JOIN_CONFERENCE_WITH_MCU), jsonBody, callBack);
    }


    /**
     * 直连sip通过mcu创建会议
     *
     * @param jsonBody
     */
    protected void directCreateConferenceWithMCU(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.DIRECT_CREATE_CONFERENCE_WITH_MCU), jsonBody, callBack);
    }

    /**
     * 直连sip通过mcu加入会议
     *
     * @param jsonBody
     */
    protected void directJoinConferenceWithMCU(String sessionId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DIRECT_JOIN_CONFERENCE_WITH_MCU, sessionId)), jsonBody, callBack);
    }


    /**
     * 非聊天版 创建会议
     *
     * @param jsonBody
     */
    protected void startConference(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.START_CONFERENCE), jsonBody, callBack);
    }

    /**
     * 非聊天版 加入会议
     *
     * @param jsonBody
     */
    protected void joinConference(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.JOIN_CONFERENCE), jsonBody, callBack);
    }

    /**
     * 非聊天版 我创建的会议列表
     */
    protected void getConferenceListOfMine(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(String.format(getRealRequestUrl(EMAppUrl.CONFERENCE_LIST_OF_MINE), page, size), null, callBack);
    }


    /**
     * 非聊天版 我加入的会议列表
     */
    protected void getConferenceListOfJoined(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(String.format(getRealRequestUrl(EMAppUrl.CONFERENCE_LIST_OF_JOINED), page, size), null, callBack);
    }

    /**
     * 非聊天版 获取会议详情
     */
    protected void getConferenceDetail(int confId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.CONFERENCE_DETAIL, confId)), null, callBack);
    }

    /**
     * 非聊天版 获取会议参与者列表
     */
    protected void getConfParticipants(int confId, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.CONFERENCE_PARTICIPANT_LIST, confId, isRegion ? 1 : 0)), null, callBack);
    }

    /**
     * 非聊天版 获取会议列表中的条目详情
     */
    protected void getConfListItemDetail(int confId, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.CONFERENCE_LIST_ITEM_DETAIL, confId, isRegion ? 1 : 0)), null, callBack);
    }

    /**
     * 非聊天版 从会议列表中的条目详情中加入会议
     */
    protected void joinConfFromListItem(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.JOIN_CONF_FROM_LIST_ITEM), jsonBody, callBack);
    }

    /**
     * 非聊天版 从会议列表中的条目详情中尝试加入会议
     */
    protected void tryToJoinConfFromListItem(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.JOIN_CONF_FROM_LIST_ITEM_TRY), jsonBody, callBack);
    }

    /**
     * 非聊天版 主持人关闭会议
     */
    protected void closeConfByHost(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.CLOSE_CONFERENCE_BY_HOST), jsonBody, callBack);
    }

    /**
     * 非聊天版 主持人踢出与会者
     */
    protected void kickUserByHost(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.KICK_USER_BY_HOST), jsonBody, callBack);
    }


    /**
     * 非聊天版 主持人更改与会者视频权限
     */
    protected void changeUserVideoPermissionByHost(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.CHANGE_USER_VIDEO_PERMISSION_BY_HOST), jsonBody, callBack);
    }


    /**
     * 非聊天版 获取会议视频文件路径
     */
    protected void getConfVideoFilePath(String confId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(String.format(getRealRequestUrl(EMAppUrl.GET_CONF_VIDEO_FILE), confId), null, callBack);
    }

    /**
     * 非聊天版 主持人静音所有人
     */
    protected void quietAll(int confId,boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(String.format(getRealRequestUrl(EMAppUrl.QUIET_ALL), confId, isRegion), null, callBack);
    }

    /**
     * 创建单/群聊，跨区会议
     */
    protected void createRegionConference(EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_REGION_CONF), null, callBack);
    }

    /**
     * 销毁单/群聊音视频跨区会议
     * @param callBack
     */
    public void destroyRegionConference(String confId, final EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(String.format(getRealRequestUrl(EMAppUrl.DESTROY_REGION_CONF), confId), null, callBack);
    }

    public void getBindSchedule(String reservationId, final EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(String.format(getRealRequestUrl(EMAppUrl.GET_BIND_SCHEDULE), reservationId), null, callBack);
    }

    /**
     * 获取服务配置
     */
    public void getServiceConfig(EMDataCallBack<String> callBack){
        setHttpGetDataCallBack(getRealRequestUrl(EMAppUrl.GET_SERVICE_CONFIG), null, callBack);
    }

    /**
     * 创建投票
     */
    public void createVote(String jsonBody, EMDataCallBack<String> callBack){
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_CREATE_VOTE), jsonBody, callBack);
    }

    /**
     * 进行投票
     */
    public void takeVote(String jsonBody, EMDataCallBack<String> callBack){
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_TAKE_VOTE), jsonBody, callBack);
    }

    /**
     * 查询投票详情
     */
    public void getVoteInfo(String voteId, EMDataCallBack<String> callBack){
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_VOTE_INFO, voteId)), null, callBack);
    }

    /**
     * 获取投票选项详情
     */
    public void getVoteOptionInfo(String voteOptionId, EMDataCallBack<String> callBack){
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_VOTE_OPTION_INFO, voteOptionId)), null, callBack);
    }

    /**
     * 结束投票
     */
    public void closeVote(String voteId, EMDataCallBack<String> callBack){
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_CLOSE_VOTE, voteId)), null, callBack);
    }

    /**
     * 删除投票
     */
    public void removeVote(String voteId, EMDataCallBack<String> callBack){
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_VOTE, voteId)), callBack);
    }

    /**
     * 获取服务器ip配置
     * @param ip
     * @param callBack
     */
    public void getServerIp(String ip, EMDataCallBack<String> callBack){
        setHttpGetDataCallBack(String.format(EMAppUrl.GET_SERVER_JSON, ip), null, callBack);
    }

    /**
     * 添加稍后处理消息
     * @param jsonBody
     * @param callBack
     */
    public void addToDoList(String jsonBody, EMDataCallBack<String> callBack){
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.ADD_TO_DO_LIST), jsonBody, callBack);
    }

    /**
     * 查询所有稍后处理消息
     * @param callBack
     */
    public void getAllToDoList(EMDataCallBack<String> callBack){
        setHttpGetDataCallBack(getRealRequestUrl(EMAppUrl.GET_ALL_TO_DO_LIST), null, callBack);
    }

    /**
     * 处理稍后处理消息
     * @param jsonBody
     * @param callBack
     */
    public void dealToDoList(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(EMAppUrl.DEAL_TO_DO_LIST), jsonBody, callBack);
    }

    /**
     * 删除稍后处理消息
     * @param todoId
     * @param callBack
     */
    public void removeToDoList(String todoId, EMDataCallBack<String> callBack){
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_TO_DO_LIST, todoId)), callBack);
    }
}

