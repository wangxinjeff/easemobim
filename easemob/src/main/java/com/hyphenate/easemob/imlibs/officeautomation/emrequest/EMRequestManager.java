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
     * ??????
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
     * sso??????
     *
     * @param ssoToken
     * @param callBack
     */
    protected void ssoLogin(String ssoToken, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_SSO_LOGIN_URL, "mobile", ssoToken)), null, callBack);
    }


    /**
     * ???????????????????????????
     *
     *
     * @param companyId
     * @param callBack
     */
    protected void getAllSubOrgs(int companyId, int orgId, int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_ALL_ORGS_LIST, companyId, orgId, page, size)), null, callBack);
    }

    /**
     * ??????????????????????????????
     *
     * @param companyId
     * @param callBack
     */
    protected void getSubOrgsOfUsers(int companyId, int orgId, int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_APPOINT_ORG_USERS, companyId, orgId, page, size)), null, callBack);
    }

    /**
     * ??????????????????
     *
     * @param companyId
     * @param callBack
     */
    protected void getRootOrgs(int companyId, int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_ORGS_LIST, companyId, page, size)), null, callBack);
    }

    /**
     * ????????????
     *
     * @param tenantId
     * @param jsonBody
     * @param callBack
     */
    protected void postAddOrg(int tenantId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_ADD_ORG, tenantId)), jsonBody, callBack);
    }

    /**
     * ?????????????????????
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
     * ????????????
     *
     * @param tenantId
     * @param orgId
     * @param callBack
     */
    protected void putOrgInfo(int tenantId, int orgId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.PUT_ORG, tenantId, orgId)), jsonBody, callBack);
    }

    /**
     * ??????????????????????????????
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
     * ????????????
     *
     * @param tenantId
     * @param jsonBody
     * @param callBack
     */
    protected void addUser(int tenantId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_ADD_USER, tenantId)), jsonBody, callBack);
    }


    /**
     * ???????????????????????????
     *
     * @param companyId
     * @param callBack
     */
    protected void getAllUsers(int companyId, int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_UESRS_BASE, companyId, page, size)), null, callBack);
    }

    /**
     * ?????????????????????????????????????????????????????????????????????
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
     * ?????????????????????????????????????????????????????????????????????
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
     * ????????????????????????
     *
     * @param callBack
     */
    protected void getStaredFriends(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_STARRED_FRIENDS_V3, page, size)), null, callBack);
    }

    /**
     * ??????????????????
     *
     * @param callBack
     */
    protected void getMyFriends(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_MY_FRIENDS_V3, page, size)), null, callBack);
    }

    /**
     * ??????????????????
     *
     * @param friendUserId
     * @param jsonBody
     * @param callBack
     */
    protected void addStaredFriends(int friendUserId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_ADD_STARRED_FRIENDS, friendUserId)), jsonBody, callBack);
    }

    /**
     * ??????????????????
     *
     * @param friendUserId
     * @param callBack
     */
    protected void deleteStaredFriends(int friendUserId, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_STARRED_FRIENDS, friendUserId)), callBack);
    }


    /**
     * ????????????????????????????????????
     *
     * @param friendUserId
     * @param callBack
     */
    protected void getIsStaredFriend(int friendUserId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_IS_STARRED_FRIEND, friendUserId)), null, callBack);
    }

    /**
     * ????????????
     * 0 ????????????
     * 1 ??????
     * 2 ??????
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
     * ??????????????????
     *
     * @param userId
     * @param callBack
     */
    protected void getUserInfo(int userId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_USERINFO_V2, userId)), null, callBack);
    }

    /**
     * ??????????????????
     *
     * @param userId
     * @param jsonBody
     * @param callBack
     */
    protected void putUserInfo(int userId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(EMAppUrl.PUT_USER_AVATAR), jsonBody, callBack);
    }
//    /**
//     * ??????????????????
//     *
//     * @param userId
//     * @param jsonBody
//     * @param callBack
//     */
//    protected Call putUserInfo(int userId, String jsonBody, EMDataCallBack<String> callBack) {
//        return setHttpPutDataCallBack(String.format(getRealRequestUrl(EMAppUrl.PUT_USERINFO_V2), userId), jsonBody, callBack);
//    }

    /**
     * ?????????????????????
     */
    protected void putUserAvatar(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(EMAppUrl.PUT_USER_AVATAR), jsonBody, callBack);
    }

    /**
     * ??????ImUser??????????????????
     *
     * @param imUsername
     * @param callBack
     */
    protected void getUserByImUser(String imUsername, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_USER_BY_IMID_V2, imUsername)), null, callBack);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param phone
     * @param callBack
     */
    protected void getSingleUserBaseInfo(String phone, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_SINGLE_USER_BASE_INFO, phone)), null, callBack);
    }

    /**
     * ??????userId????????????????????????
     *
     * @param userId
     * @param callBack
     */
    protected void getUserDetails(int userId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_SINGLE_USER_DETAILS, userId)), null, callBack);
    }

    /**
     * ??????userId????????????????????????????????????
     *
     * @param userIds
     * @param callBack
     */
    protected void getUserBaseInfo(String userIds, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_USER_BASE_INFO, userIds)), null, callBack);
    }

    /**
     * ??????????????????????????????
     *
     * @param callBack
     */
    protected void getUserAllNotify(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_USER_ALL_NOTIFY_V3, page, size)), null, callBack);
    }

    /**
     * ??????????????????????????????
     *
     * @param callBack
     */
    protected void getGroupAllNotify(String params, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_GROUP_ALL_NOTIFY_V2, params)), null, callBack);
    }

    /**
     * ????????????
     *
     * @param userId
     * @param jsonBody
     * @param callBack
     */
    protected void postAlias(int userId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_ALIAS, userId)), jsonBody, callBack);
    }

    /**
     * ????????????
     *
     * @param callBack
     */
    protected void putUpdatePassword(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(EMAppUrl.PUT_NEW_PASSWORD), jsonBody, callBack);
    }


    /**
     * ????????????
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
     * ????????????
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
     * ?????????????????????????????????
     *
     * @param beginTime ????????????
     * @param endTime   ????????????
     * @param fromId    im id
     * @param queryType chat/groupchat
     * @param msgType   ????????????txt
     * @param word      ???????????????
     * @param callBack
     */
    protected void getSearchMsgStatistics(long beginTime, long endTime, String fromId, String queryType, String msgType, String word, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_MSG_SEARCH_STATISTICS, beginTime, endTime, fromId, queryType, msgType, word)), null, callBack);
    }

    /**
     * ????????????????????????
     *
     * @param beginTime ????????????
     * @param endTime   ????????????
     * @param fromId    im ids
     * @param toId      im ids
     * @param queryType chat/groupchat
     * @param page      ??????
     * @param pageSize  ??????
     * @param msgType   ????????????txt,img...
     * @param words     ???????????????
     * @param paged     ????????????
     * @param search    ????????????
     * @param context   ???????????????
     * @param callBack
     */
    protected void getSearchMsg(long beginTime, long endTime, String fromId, String toId, String queryType, int page, int pageSize, String msgType, String words, boolean paged, boolean search, boolean context, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_MSG_SEARCH, beginTime, endTime, fromId, toId, queryType, page, pageSize, msgType, words, paged, search, context)), null, callBack);
    }

    /**
     * ????????????
     *
     * @param jsonBody
     * @param callBack
     */
    protected void postCreateGroup(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_ADD_GROUPCHATS_V2), jsonBody, callBack);
    }

    /**
     * ????????????(??????)
     *
     * @param jsonBody
     * @param callBack
     */
    protected void postCreateGroupCluster(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_ADD_GROUPCHATS_CLUSTER), jsonBody, callBack);
    }

    /**
     * ???????????????
     *
     * @param jsonBody {
     *                 "userId":7
     *                 }
     */
    protected void addMemberToGroup(int groupId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_ADD_MEMBER_V2, groupId)), jsonBody, callBack);
    }

    /**
     * ??????????????? ??????
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
     * ???????????????
     */
    protected void deleteMemberFromGroup(int groupId, int removedUserId, boolean isRegion,EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_REMOVE_MEMBER_V2, groupId, removedUserId, isRegion ? 1 : 0)), callBack);
    }

    /**
     * ?????????????????????
     */
    protected void deleteMembersFromGroup(int groupId, String removedUserIds, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_REMOVE_MEMBERS_V2, groupId, removedUserIds, isRegion ? 1 : 0)), callBack);
    }

    /**
     * ????????????
     */
    protected void changeGroupOwner(int groupId, boolean isRegion, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.PUT_CHANGE_GROUP_OWNER_V2, groupId, isRegion ? 1 : 0)), jsonBody, callBack);
    }

    /**
     * ???????????????
     */
    protected void changeGroupInfo(int groupId, boolean isRegion, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.PUT_GROUP_INFO_V2, groupId, isRegion ? 1 : 0)), jsonBody, callBack);
    }


    /**
     * ??????????????????
     */
    protected void getMemberList(int groupId, String params, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_MEMBER_LIST_V2, groupId, params)), null, callBack);
    }

    /**
     * ?????????????????????
     *
     * @param groupId
     * @param callBack
     * @return
     */
    protected void saveGroupToContact(int groupId, String jsonBody, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.SAVE_GROUP_V2, groupId, isRegion)), jsonBody, callBack);
    }

    /**
     * ????????????????????????
     */
    protected void deleteGroupFromContract(int groupId, boolean isRegion,  EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.SAVE_GROUP_V2, groupId, isRegion)), callBack);
    }

    /**
     * ??????????????????
     */
    protected void addGroupDisturb(int groupId, String jsonBody, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GROUP_DISTURB_V2, groupId, isRegion)), jsonBody, callBack);
    }

    /**
     * ????????????????????????
     */
    protected void getGroupsDisturb(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GROUPS_DISTURB, page, size)), null, callBack);
    }

    /**
     * ??????????????????
     */
    protected void removeGroupDisturb(int groupId, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GROUP_DISTURB_V2, groupId, isRegion)), callBack);
    }

    /**
     * ???????????????
     *
     * @param groupId
     * @param callBack
     */
    protected void getGroupInfo(int groupId, EMDataCallBack<String> callBack) {
        getGroupInfoById(groupId, callBack);
    }

    /**
     * ???????????????
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
     * ???????????????????????????
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
     * ????????????????????????
     *
     * @param callBack
     */
    protected void getCollectedGroups(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_COLLECT_GROUPS_V2, page, size)), null, callBack);
    }

    /**
     * ???????????????
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
     * ????????????
     *
     * @param groupId
     * @param callBack
     */
    protected void deleteGroup(int groupId, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_GROUP_V2, groupId, isRegion ? 1 : 0)), callBack);
    }

    /**
     * ????????????
     *
     * @param groupId
     * @param callBack
     */
    protected void exitGroup(int groupId, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.EXIT_GROUP_V2, groupId, isRegion ? 1 : 0)), callBack);
    }

    /**
     * ?????????????????????
     */
    protected void muteGroupMembers(int groupId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.PUT_MUTE_GROUP_MEMBERS_V2, groupId)), jsonBody, callBack);
    }

    /**
     * ????????????
     */
    protected void unMuteGroupMember(int groupId, int userId, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_MUTE_GROUP_MEMBER_V2, groupId, userId, isRegion ? 1 : 0)), callBack);
    }

    /**
     * ??????????????????
     */
    protected void getMuteGroupMembers(int groupId, String params, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_MUTE_GROUP_MEMBER_V2, groupId, params)), null, callBack);
    }

    /**
     * ??????????????????
     *
     * @param tenantId
     * @param callBack
     */
    protected void postDeviceInfo(int tenantId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_DEVICE, tenantId)), jsonBody, callBack);
    }

    /**
     * ????????????
     *
     * @param jsonBody
     * @param callBack
     */
    protected void postFeedBack(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_FEEDBACK), jsonBody, callBack);
    }

    /**
     * ???????????????
     *
     * @param jsonBody
     * @param callBack
     */
    protected void postVerificationCode(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_VERIFICATIONCODE), jsonBody, callBack);
    }

    /**
     * ????????????token
     *
     * @param jsonBody
     * @param callBack
     */
    protected void postVerificationToken(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_VERIFICATIONTOKEN), jsonBody, callBack);
    }


    /**
     * ????????????
     *
     * @param token
     * @param jsonBody
     * @param callBack
     */
    protected void putPassword(String token, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.PUT_PASSWORD, token)), jsonBody, callBack);
    }

    /**
     * ??????????????????
     *
     * @param tenantId
     * @param jsonBody
     * @param callBack
     */
    protected void postSchedule(long tenantId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_SCHEDULES, tenantId)), jsonBody, callBack);
    }

    /**
     * ????????????
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
     * ????????????id????????????
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
     * ??????????????????????????????
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
     * ????????????
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
     * ????????????
     *
     * @param stickerId
     * @param callBack
     */
    protected void deleteSticker(final int stickerId, final EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_STRICKER, stickerId)), callBack);
    }

    /**
     * ????????????
     *
     * @param stickerId
     * @param jsonBody
     * @param callBack
     */
    protected void putSticker(final int stickerId, final String jsonBody, final EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.PUT_STRICKER, stickerId)), jsonBody, callBack);
    }

    /**
     * ????????????
     *
     * @param jsonBody
     * @param callBack
     */
    protected void postSticker(final String jsonBody, final EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_STICKERS), jsonBody, callBack);
    }

    /**
     * ??????????????????
     *
     * @param groupName
     * @param callBack
     */
    protected void getStickers(final String groupName, final EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_STICKERS, "page=0&size=200&groupName=" + groupName)), null, callBack);
    }

    /**
     * ???????????????????????????
     *
     * @param queryType groupchat/chat (??????/??????) ??????:all
     * @param colType   img,txt.. ??????:all
     * @param page
     * @param pageSize
     * @param callBack
     */
    protected void getCollectMsgs(String queryType, String colType, int page, int pageSize, final EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_COLLECTS, EMClient.getInstance().getCurrentUser(), queryType, colType, page, pageSize)), null, callBack);
    }

    /**
     * ????????????
     *
     * @param jsonBody
     * @param callBack
     */
    protected void postCollect(String jsonBody, final EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_COLLECT), jsonBody, callBack);
    }

    /**
     * ????????????
     *
     * @param msgId
     * @param callBack
     */
    protected void deleteCollect(String msgId, final EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_COLLECT, msgId)), callBack);
    }

    /**
     * ????????????web
     *
     * @param authId
     * @param callBack
     */
    protected void authWebLogin(String authId, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_AUTH_WEB_LOGIN_URL), authId, callBack);
    }

    /**
     * ???????????????web???
     *
     * @param callBack
     */
    protected void kickWeb(EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_KICK_WEB_LOGIN), null, callBack);
    }

    /**
     * ????????????/????????????
     *
     * @param friendId
     * @param callBack
     */
    protected void invitedOrAcceptFriend(int friendId, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.INVITED_OR_ACCEPT_FRIEND, String.valueOf(friendId))), null, callBack);
    }

    /**
     * ????????????????????????
     *
     * @param chatGroupId
     * @param jsonBody
     * @param callBack
     */
    protected void approveMemberApply(int chatGroupId, boolean isRegion,  String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.MANAGE_MEMBER_APPLY, String.valueOf(chatGroupId), isRegion ? 1 : 0)), jsonBody, callBack);
    }

    /**
     * ????????????
     *
     * @param friendId
     * @param callBack
     */
    protected void deleteFriend(int friendId, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.INVITED_OR_ACCEPT_FRIEND, String.valueOf(friendId))), callBack);
    }

    /**
     * ?????????????????????
     *
     * @param friendId
     * @param callBack
     */
    protected void friendRelationStatus(int friendId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.FRIEND_RELATION_STATUS, String.valueOf(friendId))), null, callBack);
    }

    /**
     * ??????????????????
     *
     * @param callBack
     */
    protected void getAppListFromServer(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_APPS_LIST, page, size)), null, callBack);
    }

    /**
     * ????????????????????????
     *
     * @param callBack
     */
    protected void getCommonAppListFromServer(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_COMMON_APPS_LIST, page, size)), null, callBack);
    }

    /**
     * ????????????????????????
     *
     * @param callBack
     */
    protected void addCommonAppListFromServer(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_COMMON_APPS_LIST), jsonBody, callBack);
    }

    /**
     * ????????????????????????
     *
     * @param callBack
     */
    protected void getGroupAppListFromServer(EMDataCallBack<String> callBack){
        setHttpGetDataCallBack(getRealRequestUrl(EMAppUrl.GET_GROUP_APPS_LIST), null, callBack);
    }

    /**
     * ???????????????
     *
     * @param callBack
     */
    protected void getBillBoardFromServer(EMDataCallBack<String> callBack){
        setHttpGetDataCallBack(getRealRequestUrl(EMAppUrl.GET_BILL_BOARD), null, callBack);
    }

    /**
     * ??????????????????
     *
     * @param callBack
     */
    protected void getVersionStatus(EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(EMAppUrl.GET_VERSION_INFO), null, callBack);
    }

    /**
     * ???????????????
     *
     * @param callBack
     */
//    protected void downloadApk(String url, EMDataCallBack<String> callBack) {
//        setHttpPostDataCallBack(getRealRequestUrl(url), null, callBack);
//    }

    /**
     * ????????????????????????
     */
    protected void getUserOptions(EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(EMAppUrl.GET_USER_OPTIONS), null, callBack);
    }

    /**
     * ??????????????????
     */
    protected void getTenantOptions(EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(EMAppUrl.GET_TENANT_OPTIONS), null, callBack);
    }

    /**
     * ??????????????????
     */
    protected void getSessions(EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(EMAppUrl.GET_SESSIONS_V3), null, callBack);
    }

    /**
     * ????????????
     */
    protected void deleteSession(String toId, String chatType, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_SESSION, toId, chatType)), callBack);
    }

    /**
     * ????????????
     */
    protected void postSession(String reqBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.GET_SESSIONS), reqBody, callBack);
    }

    /**
     * ????????????
     */
    protected void putSession(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(EMAppUrl.GET_SESSIONS), jsonBody, callBack);
    }

    /**
     * ????????????
     */
    protected void newSchedule(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.NEW_SCHEDULE), jsonBody, callBack);
    }

    /**
     * ??????????????????????????????????????????
     * @param year
     * @param month
     * @param callBack
     */
    protected void getScheduleByMonth(int year, int month, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(String.format(getRealRequestUrl(EMAppUrl.GET_SCHEDULE_BY_MONTH), year, month), null, callBack);
    }

    /**
     * ????????????????????????
     */
    protected void getAppointDateSchedules(int year, int month, int day, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_APPOINT_DATE_SCHEDULE, year, month, day)), null, callBack);
    }

    /**
     * ?????????????????????
     * @param scheduleId
     * @param callBack
     */
    protected void getScheduleReceiverList(int scheduleId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_SCHEDULE_RECEIVERLIST, scheduleId)), null, callBack);
    }

    /**
     * ???????????????????????????????????????
     */
    protected void getStatisticsSchedules(String appointDate, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_STATISTICS_SCHEDULE, appointDate)), null, callBack);
    }

    /**
     * ??????????????????
     */
    protected void getScheduleDetails(int scheduleId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_SCHEDULE_DETAILS, scheduleId)), null, callBack);
    }

    /**
     * ????????????
     */
    protected void publishScheduleMessage(int scheduleId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.PUBLISH_SCHEDULE_MESSAGE, scheduleId)), jsonBody, callBack);
    }


    /**
     * ????????????
     */
    protected void deleteSchedule(int scheduleId, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_SCHEDULE, scheduleId)), callBack);
    }

    /**
     * ????????????
     */
    protected void modifySchedule(int scheduleId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.MODIFY_SCHEDULE_DETAIL, scheduleId)), jsonBody, callBack);
    }


    /**
     * ????????????
     */
    protected void acceptSchedule(int scheduleId, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.ACCEPT_SCHEDULE, scheduleId)), null, callBack);
    }

    /**
     * ????????????
     */
    protected void refuseSchedule(int scheduleId, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.REFUSE_SCHEDULE, scheduleId)), null, callBack);
    }

    /**
     * ????????????
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
     * ????????????
     */
    protected void newTask(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.NEW_TASK), jsonBody, callBack);
    }

    /**
     * ???????????????/????????????????????????
     */
    protected void getReceiveTaskList(int status, int isClosed, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_RECEIVE_TASK_LIST, status, isClosed)), null, callBack);
    }

    /**
     * ????????????????????????????????????
     */
    protected void getMyTaskList(EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(EMAppUrl.GET_MY_TASK_LIST), null, callBack);
    }

    /**
     * ???????????????????????????
     */
    protected void getCreateTaskList(int isClosed, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_CREATE_TASK_LIST, isClosed)), null, callBack);
    }

    /**
     * ???????????????????????????
     */
    protected void getCcTaskList(int isClosed, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_CC_TASK_LIST, isClosed)), null, callBack);
    }

    /**
     * ??????????????????
     */
    protected void getTaskDetails(int taskId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_TASK_DETAILS, taskId)), null, callBack);
    }

    /**
     * ????????????
     */
    protected void publishTaskMessage(int taskId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.PUBLISH_TASK_MESSAGE, taskId)), jsonBody, callBack);
    }

    /**
     * ?????????/????????? ???????????????/?????????
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
     * ????????????
     */
    protected void deleteTask(int taskId, EMDataCallBack<String> callBack) {
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_TASK, taskId)), callBack);
    }

    /**
     * ????????????
     */
    protected void modifyTask(int taskId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_TASK, taskId)), jsonBody, callBack);
    }

    /**
     * ????????????
     *
     * @param status close ?????????open ??????
     */
    protected void closeOrOpenTask(int taskId, String status, EMDataCallBack<String> callBack) {
        setHttpPutDataCallBack(getRealRequestUrl(String.format(EMAppUrl.CLOSE_OR_OPEN_TASK, taskId, status)), null, callBack);
    }

    /**
     * ??????mcu????????????
     *
     * @param jsonBody
     */
    protected void createConferenceWithMCU(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.CREATE_CONFERENCE_WITH_MCU), jsonBody, callBack);
    }

    /**
     * ??????mcu????????????
     *
     * @param jsonBody
     */
    protected void joinConferenceWithMCU(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.JOIN_CONFERENCE_WITH_MCU), jsonBody, callBack);
    }


    /**
     * ??????sip??????mcu????????????
     *
     * @param jsonBody
     */
    protected void directCreateConferenceWithMCU(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.DIRECT_CREATE_CONFERENCE_WITH_MCU), jsonBody, callBack);
    }

    /**
     * ??????sip??????mcu????????????
     *
     * @param jsonBody
     */
    protected void directJoinConferenceWithMCU(String sessionId, String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DIRECT_JOIN_CONFERENCE_WITH_MCU, sessionId)), jsonBody, callBack);
    }


    /**
     * ???????????? ????????????
     *
     * @param jsonBody
     */
    protected void startConference(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.START_CONFERENCE), jsonBody, callBack);
    }

    /**
     * ???????????? ????????????
     *
     * @param jsonBody
     */
    protected void joinConference(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.JOIN_CONFERENCE), jsonBody, callBack);
    }

    /**
     * ???????????? ????????????????????????
     */
    protected void getConferenceListOfMine(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(String.format(getRealRequestUrl(EMAppUrl.CONFERENCE_LIST_OF_MINE), page, size), null, callBack);
    }


    /**
     * ???????????? ????????????????????????
     */
    protected void getConferenceListOfJoined(int page, int size, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(String.format(getRealRequestUrl(EMAppUrl.CONFERENCE_LIST_OF_JOINED), page, size), null, callBack);
    }

    /**
     * ???????????? ??????????????????
     */
    protected void getConferenceDetail(int confId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.CONFERENCE_DETAIL, confId)), null, callBack);
    }

    /**
     * ???????????? ???????????????????????????
     */
    protected void getConfParticipants(int confId, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.CONFERENCE_PARTICIPANT_LIST, confId, isRegion ? 1 : 0)), null, callBack);
    }

    /**
     * ???????????? ????????????????????????????????????
     */
    protected void getConfListItemDetail(int confId, boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.CONFERENCE_LIST_ITEM_DETAIL, confId, isRegion ? 1 : 0)), null, callBack);
    }

    /**
     * ???????????? ????????????????????????????????????????????????
     */
    protected void joinConfFromListItem(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.JOIN_CONF_FROM_LIST_ITEM), jsonBody, callBack);
    }

    /**
     * ???????????? ??????????????????????????????????????????????????????
     */
    protected void tryToJoinConfFromListItem(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.JOIN_CONF_FROM_LIST_ITEM_TRY), jsonBody, callBack);
    }

    /**
     * ???????????? ?????????????????????
     */
    protected void closeConfByHost(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.CLOSE_CONFERENCE_BY_HOST), jsonBody, callBack);
    }

    /**
     * ???????????? ????????????????????????
     */
    protected void kickUserByHost(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.KICK_USER_BY_HOST), jsonBody, callBack);
    }


    /**
     * ???????????? ????????????????????????????????????
     */
    protected void changeUserVideoPermissionByHost(String jsonBody, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.CHANGE_USER_VIDEO_PERMISSION_BY_HOST), jsonBody, callBack);
    }


    /**
     * ???????????? ??????????????????????????????
     */
    protected void getConfVideoFilePath(String confId, EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(String.format(getRealRequestUrl(EMAppUrl.GET_CONF_VIDEO_FILE), confId), null, callBack);
    }

    /**
     * ???????????? ????????????????????????
     */
    protected void quietAll(int confId,boolean isRegion, EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(String.format(getRealRequestUrl(EMAppUrl.QUIET_ALL), confId, isRegion), null, callBack);
    }

    /**
     * ?????????/?????????????????????
     */
    protected void createRegionConference(EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_REGION_CONF), null, callBack);
    }

    /**
     * ?????????/???????????????????????????
     * @param callBack
     */
    public void destroyRegionConference(String confId, final EMDataCallBack<String> callBack) {
        setHttpPostDataCallBack(String.format(getRealRequestUrl(EMAppUrl.DESTROY_REGION_CONF), confId), null, callBack);
    }

    public void getBindSchedule(String reservationId, final EMDataCallBack<String> callBack) {
        setHttpGetDataCallBack(String.format(getRealRequestUrl(EMAppUrl.GET_BIND_SCHEDULE), reservationId), null, callBack);
    }

    /**
     * ??????????????????
     */
    public void getServiceConfig(EMDataCallBack<String> callBack){
        setHttpGetDataCallBack(getRealRequestUrl(EMAppUrl.GET_SERVICE_CONFIG), null, callBack);
    }

    /**
     * ????????????
     */
    public void createVote(String jsonBody, EMDataCallBack<String> callBack){
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_CREATE_VOTE), jsonBody, callBack);
    }

    /**
     * ????????????
     */
    public void takeVote(String jsonBody, EMDataCallBack<String> callBack){
        setHttpPostDataCallBack(getRealRequestUrl(EMAppUrl.POST_TAKE_VOTE), jsonBody, callBack);
    }

    /**
     * ??????????????????
     */
    public void getVoteInfo(String voteId, EMDataCallBack<String> callBack){
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_VOTE_INFO, voteId)), null, callBack);
    }

    /**
     * ????????????????????????
     */
    public void getVoteOptionInfo(String voteOptionId, EMDataCallBack<String> callBack){
        setHttpGetDataCallBack(getRealRequestUrl(String.format(EMAppUrl.GET_VOTE_OPTION_INFO, voteOptionId)), null, callBack);
    }

    /**
     * ????????????
     */
    public void closeVote(String voteId, EMDataCallBack<String> callBack){
        setHttpPostDataCallBack(getRealRequestUrl(String.format(EMAppUrl.POST_CLOSE_VOTE, voteId)), null, callBack);
    }

    /**
     * ????????????
     */
    public void removeVote(String voteId, EMDataCallBack<String> callBack){
        setHttpDeleteDataCallBack(getRealRequestUrl(String.format(EMAppUrl.DELETE_VOTE, voteId)), callBack);
    }

    /**
     * ???????????????ip??????
     * @param url
     * @param callBack
     */
    public void getServerIp(String ip, EMDataCallBack<String> callBack){
        setHttpGetDataCallBack(String.format(EMAppUrl.GET_SERVER_JSON, ip), null, callBack);
    }
}

