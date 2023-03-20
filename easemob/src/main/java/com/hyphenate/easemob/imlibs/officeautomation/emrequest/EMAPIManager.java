package com.hyphenate.easemob.imlibs.officeautomation.emrequest;


import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 26/08/2018
 */

public class EMAPIManager {

    public static final String TAG = "HelpDeskManager";
    private static volatile EMAPIManager httpManager = null;

    //    ExecutorService sendThreadPool = Executors.newCachedThreadPool();
    ExecutorService requestThreadPool = Executors.newFixedThreadPool(7);

    private EMAPIManager() {

    }

    public static EMAPIManager getInstance() {
        if (httpManager == null) {
            synchronized (EMAPIManager.class) {
                if (httpManager == null) {
                    httpManager = new EMAPIManager();
                }
            }
        }
        return httpManager;
    }

    public void login(final String username, final String password, final EMDataCallBack<String> callback) {
        EMRequestManager.getInstance().login(username, password, callback);
    }

    public void ssoLogin(final String ssoToken, final EMDataCallBack<String> callback) {
        EMRequestManager.getInstance().ssoLogin(ssoToken, callback);
    }

    /**
     * 获取所有子部门列表
     *
     * @param companyId
     * @param callBack
     */
    public void getAllSubOrgs(final int companyId, int orgId, final int page, final int size, final EMDataCallBack<String> callBack) {
        EMRequestManager.getInstance().getAllSubOrgs(companyId, orgId, page, size, callBack);
    }

    /**
     * 获取指定部门下的用户
     *
     * @param companyId
     * @param callBack
     */
    public void getSubOrgsOfUsers(final int companyId, int orgId, final int page, final int size, final EMDataCallBack<String> callBack) {
        EMRequestManager.getInstance().getSubOrgsOfUsers(companyId, orgId, page, size, callBack);
    }

    /**
     * 获取根部门列表
     *
     * @param companyId
     * @param callBack
     */
    public void getRootOrgs(final int companyId, final int page, final int size, final EMDataCallBack<String> callBack) {
        EMRequestManager.getInstance().getRootOrgs(companyId, page, size, callBack);
    }

    /**
     * 添加部门
     *
     * @param tenantId
     * @param jsonBody
     * @param callBack
     */
    public void postAddOrg(final int tenantId, final String jsonBody, final EMDataCallBack<String> callBack) {
        EMRequestManager.getInstance().postAddOrg(tenantId, jsonBody, callBack);
    }

    /**
     * 修改部门
     *
     * @param tenantId
     * @param orgId
     * @param callBack
     */
    public void putOrgInfo(final int tenantId, final int orgId, final String jsonBody, final EMDataCallBack<String> callBack) {
        EMRequestManager.getInstance().putOrgInfo(tenantId, orgId, jsonBody, callBack);
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
    public void getUsersByOrgId(final int companyId, final int orgId, final int page, final int size, final EMDataCallBack<String> callBack) {
        EMRequestManager.getInstance().getUsersByOrgId(companyId, orgId, page, size, callBack);
    }

    /**
     * 获取所有用户
     *
     * @param companyId
     * @param page
     * @param size
     * @param callback
     */
    public void getAllUsers(final int companyId, final int page, final int size, final EMDataCallBack<String> callback) {
        EMRequestManager.getInstance().getAllUsers(companyId, page, size, callback);
    }

    /**
     * 根据上次获取公司人员最后一次时间，进行增量更新
     *
     * @param companyId
     * @param lastTime
     * @param page
     * @param size
     * @param callback
     */
    public void getIncrementUsersByLastTime(final int companyId, final long lastTime, final int page, final int size, final EMDataCallBack<String> callback) {
        EMRequestManager.getInstance().getIncrementUsersByLastTime(companyId, lastTime, page, size, callback);
    }

    /**
     * 根据上次获取公司部门最后一次时间，进行增量更新
     *
     * @param companyId
     * @param lastTime
     * @param page
     * @param size
     * @param callback
     */
    public void getIncrementDepartmentsByLastTime(final int companyId, final long lastTime, final int page, final int size, final EMDataCallBack<String> callback) {
        EMRequestManager.getInstance().getIncrementDepartmentsByLastTime(companyId, lastTime, page, size, callback);
    }

    /**
     * 获取子部门
     *
     * @param companyId
     * @param orgId
     * @param page
     * @param size
     * @param callBack
     */
    public void getOrgInfoForSub(final int companyId, final int orgId, int page, int size, final EMDataCallBack<String> callBack) {
        EMRequestManager.getInstance().getSubOrgInfo(companyId, orgId, page, size, callBack);
    }

    /**
     * 增加用户
     *
     * @param tenantId
     * @param jsonBody
     * @param callBack
     */
    public void addUser(final int tenantId, final String jsonBody, final EMDataCallBack<String> callBack) {
        EMRequestManager.getInstance().addUser(tenantId, jsonBody, callBack);
    }

    /**
     * 获取所有星标好友
     *
     * @param callBack
     */
    public void getStaredFriends(final int page, final int size, final EMDataCallBack<String> callBack) {
        EMRequestManager.getInstance().getStaredFriends(page, size, callBack);
    }

    /**
     * 获取所有星标好友
     *
     * @param callBack
     */
    public void getMyFriends(final int page, final int size, final EMDataCallBack<String> callBack) {
        EMRequestManager.getInstance().getMyFriends(page, size, callBack);
    }

    /**
     * 添加星标好友
     *
     * @param friendUserId
     * @param jsonBody
     * @param callBack
     */
    public void addStartedFriends(final int friendUserId, final String jsonBody, final EMDataCallBack<String> callBack) {
        EMRequestManager.getInstance().addStaredFriends(friendUserId, jsonBody, callBack);
    }

    /**
     * 取消星标好友
     *
     * @param friendUserId
     * @param callBack
     */
    public void deleteStaredFriends(final int friendUserId, final EMDataCallBack<String> callBack) {
        EMRequestManager.getInstance().deleteStaredFriends(friendUserId, callBack);
    }


    /**
     * 获取此用户是否为星标好友
     *
     * @param friendUserId
     * @param callBack
     */
    public void getIsStaredFriend(final int friendUserId, final EMDataCallBack<String> callBack) {
        EMRequestManager.getInstance().getIsStaredFriend(friendUserId, callBack);
    }

    /**
     * 上传文件
     *
     * @param callBack
     */
    public void postFile(final File file, final int type, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().postFile(file, type, callBack);
            }
        });
    }

    /**
     * 获取用户信息
     *
     * @param userId
     * @param callBack
     */
    public void getUserInfo(final int userId, final EMDataCallBack<String> callBack) {
        EMRequestManager.getInstance().getUserInfo(userId, callBack);
    }

    /**
     * 修改用户信息
     *
     * @param userId
     * @param jsonBody
     * @param callBack
     */
    public void putUserInfo(final int userId, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().putUserInfo(userId, jsonBody, callBack);
            }
        });
    }

    /**
     * 更新个人头像
     *
     * @param jsonBody
     * @param callBack
     */
    public void putUserAvatar(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().putUserAvatar(jsonBody, callBack);
            }
        });
    }


    /**
     * 通过ImUser获取用户信息
     *
     * @param imUsername
     * @param callBack
     */
    public void getUserByImUser(final String imUsername, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getUserByImUser(imUsername, callBack);
            }
        });
    }

    /**
     * 通过手机号获取用户基本信息
     *
     * @param phone
     * @param callBack
     */
    public void getSingleUserBaseInfo(final String phone, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getSingleUserBaseInfo(phone, callBack);
            }
        });
    }

    /**
     * 通过userId获取用户详细信息
     *
     * @param userId
     * @param callBack
     */
    public void getUserDetails(final int userId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getUserDetails(userId, callBack);
            }
        });
    }

    /**
     * 通过userId获取用户基本信息
     *
     * @param userIds
     * @param callBack
     */
    public void getUserBaseInfo(final String userIds, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getUserBaseInfo(userIds, callBack);
            }
        });
    }

    /**
     * 获取添加好友的所有通知
     *
     * @param callBack
     */
    public void getUserAllNotify(final int page, final int size, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getUserAllNotify(page, size, callBack);
            }
        });
    }

    /**
     * 获取群邀请所有通知
     *
     * @param callBack
     */
    public void getGroupAllNotify(final String params, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getGroupAllNotify(params, callBack);
            }
        });
    }

    /**
     * 添加备注
     *
     * @param userId
     * @param jsonBody
     * @param callBack
     */
    public void postAlias(final int userId, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().postAlias(userId, jsonBody, callBack);
            }
        });
    }

    /**
     * 修改密码
     *
     * @param callBack
     */
    public void putUpdatePassword(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().putUpdatePassword(jsonBody, callBack);
            }
        });
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
    public void getSearchUser(final int tenantId, final String username, final int page, final int size, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getSearchUser(tenantId, username, page, size, callBack);
            }
        });
    }

    /**
     * 搜索用户
     *
     * @param query
     * @param size
     * @param type
     * @param callBack
     */
    public void getGlobalSearch(final String query, final int size, final String type, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {

            @Override
            public void run() {
                String queryParams = query;
                try {
                    queryParams = URLEncoder.encode(queryParams, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                }

                EMRequestManager.getInstance().getGlobalSearch(queryParams, size, type, callBack);
            }
        });
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
    public void getSearchMsgStatistics(final long beginTime, final long endTime, final String fromId, final String queryType, final String msgType, final String word, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String queryParams = word;
                try {
                    queryParams = URLEncoder.encode(queryParams, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                }
                EMRequestManager.getInstance().getSearchMsgStatistics(beginTime, endTime, fromId, queryType, msgType, queryParams, callBack);
            }
        });
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
     * @param search    是否搜索
     * @param context   搜索内容的上下文
     * @param callBack
     */
    public void getSearchMsg(final long beginTime, final long endTime, final String fromId, final String toId, final String queryType,
                             final int page, final int pageSize, final String msgType,
                             final String words, final boolean search, final boolean context, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String queryParams = words;
                try {
                    queryParams = URLEncoder.encode(queryParams, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                }
                EMRequestManager.getInstance().getSearchMsg(beginTime, endTime, fromId, toId, queryType, page, pageSize, msgType, queryParams, true, search, context, callBack);
            }
        });
    }

    /**
     * 创建群组
     *
     * @param jsonBody
     * @param callBack
     */
    public void postCreateGroup(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().postCreateGroup(jsonBody, callBack);
            }
        });
    }

    /**
     * 创建群组(跨集群)
     *
     * @param jsonBody
     * @param callBack
     */
    public void postCreateGroupCluster(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().postCreateGroupCluster(jsonBody, callBack);
            }
        });
    }

    /**
     * 添加群成员
     *
     * @param jsonBody {
     *                 "userId":7
     *                 }
     */
    public void addMemberToGroup(final int groupId, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().addMemberToGroup(groupId, jsonBody, callBack);
            }
        });
    }

    /**
     * 添加群成员  批量
     *
     * @param groupId
     * @param jsonBody {
     *                 "userIdList":[8,9] //最大不超过60个id
     *                 }
     */
    public void addMembersToGroup(final int groupId, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().addMembersToGroup(groupId, jsonBody, callBack);
            }
        });
    }

    /**
     * 删除群成员
     */
    public void deleteMemberFromGroup(final int groupId, final int removedUserId, final boolean isRegion, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().deleteMemberFromGroup(groupId, removedUserId, isRegion, callBack);
            }
        });
    }

    /**
     * 批量删除群成员
     */
    public void deleteMembersFromGroup(final int groupId, final String removedUserIds, final boolean isRegion, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().deleteMembersFromGroup(groupId, removedUserIds, isRegion, callBack);
            }
        });
    }

    /**
     * 群主转移
     */
    public void changeGroupOwner(final int groupId, final boolean isRegion, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().changeGroupOwner(groupId, isRegion, jsonBody, callBack);
            }
        });
    }

    /**
     * 修改群信息
     */
    public void changeGroupInfo(final int groupId,final boolean isRegion, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().changeGroupInfo(groupId, isRegion, jsonBody, callBack);
            }
        });
    }


    /**
     * 获取群聊的成员列表
     */
    public void getMemberList(final int groupId, final String params, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getMemberList(groupId, params, callBack);
            }
        });
    }


    /**
     * 保存群到通讯录
     */
    public void saveGroupToContract(final int groupId, final String jsonBody, final boolean isRegion, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().saveGroupToContact(groupId, jsonBody, isRegion, callBack);
            }
        });
    }

    /**
     * 从通讯录中删除群
     */
    public void deleteGroupFromContract(final int groupId, final boolean isRegion, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().deleteGroupFromContract(groupId, isRegion, callBack);
            }
        });
    }

    /**
     * 添加群免打扰
     */
    public void addGroupDisturb(final int groupId, final String jsonBody,  final boolean isRegion, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().addGroupDisturb(groupId, jsonBody, isRegion, callBack);
            }
        });
    }

    /**
     * 获取群免打扰列表
     */
    public void getGroupsDisturb(final int page, final int size, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getGroupsDisturb(page, size, callBack);
            }
        });
    }

    /**
     * 去除群免打扰
     */
    public void removeGroupDisturb(final int groupId, final boolean isRegion, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().removeGroupDisturb(groupId, isRegion, callBack);
            }
        });
    }

    /**
     * 获取群信息
     *
     * @param groupId
     * @param callBack
     */
    public void getGroupInfo(final int groupId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getGroupInfo(groupId, callBack);
            }
        });
    }

    /**
     * 获取群信息
     *
     * @param imGroupId
     * @param callBack
     */
    public void getGroupInfo(final String imGroupId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getGroupInfo(imGroupId, callBack);
            }
        });
    }

    /**
     * 通过群id获取群详情
     *
     * @param groupId
     * @param callBack
     */
    public void getGroupInfoById(final int groupId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getGroupInfoById(groupId, callBack);
            }
        });
    }

    /**
     * 群详情，包含群成员
     *
     * @param groupId
     * @param isRegion
     * @param callBack
     */
    public void getGroupDetailWithMemberList(final int groupId, final boolean isRegion, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getGroupDetailWithMemberList(groupId, isRegion, callBack);
            }
        });
    }


    /**
     * 获取我收藏的群组
     *
     * @param callBack
     */
    public void getCollectedGroups(int page, int size, final EMDataCallBack<String> callBack) {
        EMRequestManager.getInstance().getCollectedGroups(page, size, callBack);
    }

    /**
     * 修改群信息
     *
     * @param groupId
     * @param jsonBody
     * @param callBack
     */
    public void putGroupInfo(final int groupId, final boolean isRegion, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().putGroupInfo(groupId, isRegion, jsonBody, callBack);
            }
        });
    }

    /**
     * 删除群组
     *
     * @param groupId
     * @param callBack
     */
    public void deleteGroup(final int groupId, final boolean isRegion, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().deleteGroup(groupId, isRegion, callBack);
            }
        });
    }

    /**
     * 退出群组
     *
     * @param groupId
     * @param callBack
     */
    public void exitGroup(final int groupId, final boolean isRegion, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().exitGroup(groupId, isRegion, callBack);
            }
        });
    }

    /**
     * 批量禁言群成员
     *
     * @param groupId
     * @param jsonBody
     * @param callBack
     */
    public void muteGroupMembers(final int groupId, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().muteGroupMembers(groupId, jsonBody, callBack);
            }
        });
    }

    /**
     * 解除禁言
     */
    public void unmuteGroupMember(final int groupId, final int userId, final boolean isRegion, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().unMuteGroupMember(groupId, userId, isRegion, callBack);
            }
        });
    }

    /**
     * 查询禁言
     */
    public void getMuteGroupMembers(final int groupId, final String params, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getMuteGroupMembers(groupId, params, callBack);
            }
        });
    }

    /**
     * 上传设备信息
     *
     * @param tenantId
     * @param callBack
     */
    public void postDeviceInfo(final int tenantId, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().postDeviceInfo(tenantId, jsonBody, callBack);
            }
        });
    }

    /**
     * 意见反馈
     *
     * @param jsonBody
     * @param callBack
     */
    public void postFeedBack(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().postFeedBack(jsonBody, callBack);
            }
        });
    }

    /**
     * 请求验证码
     *
     * @param jsonBody
     * @param callBack
     */
    public void postVerificationCode(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().postVerificationCode(jsonBody, callBack);
            }
        });
    }

    /**
     * 请求授权token
     *
     * @param jsonBody
     * @param callBack
     */
    public void postVerificationToken(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().postVerificationToken(jsonBody, callBack);
            }
        });
    }


    /**
     * 设置密码
     *
     * @param token
     * @param jsonBody
     * @param callBack
     */
    public void putPassword(final String token, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().putPassword(token, jsonBody, callBack);
            }
        });
    }

    /**
     * 设置日程提醒
     *
     * @param tenantId
     * @param jsonBody
     * @param callBack
     */
    public void postSchedule(final long tenantId, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().postSchedule(tenantId, jsonBody, callBack);
            }
        });
    }

    /**
     * 创建白板
     *
     * @param whiteBoardName
     * @param userId
     * @param callBack
     */
    public void createWhiteBoards(final String whiteBoardName, final String userId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().postCreateWhiteBoards(whiteBoardName, userId, callBack);
            }
        });
    }

    /**
     * 通过白板id加入白板
     *
     * @param whiteBoardId
     * @param userId
     * @param callBack
     */
    public void joinWhiteBoardsById(final String whiteBoardId, final String userId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().postJoinWhiteBoardsById(whiteBoardId, userId, callBack);
            }
        });
    }

    /**
     * 通过白板名字加入白板
     *
     * @param whiteBoardName
     * @param userId
     * @param callBack
     */
    public void joinWhiteBoardsByName(final String whiteBoardName, final String userId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().postJoinWhiteBoardsByName(whiteBoardName, userId, callBack);
            }
        });
    }

    /**
     * 销毁白板白板
     *
     * @param whiteBoardId
     * @param userId
     * @param callBack
     */
    public void destroyWhiteBoards(final String whiteBoardId, final String userId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().postDestroyWhiteBoards(whiteBoardId, userId, callBack);
            }
        });
    }

    /**
     * 下载文件
     *
     * @param url
     * @param destFilePath
     * @param callBack
     */
    public void downloadFile(final String url, final String destFilePath, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().downloadFile(url, destFilePath, callBack);
            }
        });
    }

    /**
     * 删除贴纸
     *
     * @param stickerId
     * @param callBack
     */
    public void deleteSticker(final int stickerId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().deleteSticker(stickerId, callBack);
            }
        });
    }

    /**
     * 修改贴纸
     *
     * @param stickerId
     * @param jsonBody
     * @param callBack
     */
    public void putSticker(final int stickerId, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().putSticker(stickerId, jsonBody, callBack);
            }
        });
    }

    /**
     * 添加贴纸
     *
     * @param jsonBody
     * @param callBack
     */
    public void postSticker(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().postSticker(jsonBody, callBack);
            }
        });
    }

    /**
     * 获取贴纸列表
     *
     * @param groupName
     * @param callBack
     */
    public void getStickers(final String groupName, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String encodedGroupName = groupName;
                try {
                    encodedGroupName = URLEncoder.encode(encodedGroupName, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                }
                EMRequestManager.getInstance().getStickers(encodedGroupName, callBack);
            }
        });
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
    public void getCollectMsgs(final String queryType, final String colType, final int page, final int pageSize, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getCollectMsgs(queryType, colType, page, pageSize, callBack);
            }
        });
    }

    /**
     * 添加收藏
     *
     * @param jsonBody
     * @param callBack
     */
    public void postCollect(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().postCollect(jsonBody, callBack);
            }
        });
    }

    /**
     * 删除收藏
     *
     * @param favId
     * @param callBack
     */
    public void deleteCollect(final String favId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().deleteCollect(favId, callBack);
            }
        });
    }

    /**
     * 扫描登陆web
     *
     * @param authId
     * @param callBack
     */
    public void authWebLogin(final String authId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().authWebLogin(authId, callBack);
            }
        });
    }

    /**
     * 移动端踢出web
     *
     * @param callBack
     */
    public void kickWeb(final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().kickWeb(callBack);
            }
        });
    }

    /**
     * 添加好友/接受好友
     *
     * @param friendId((not hxid)
     * @param callBack
     */
    public void invitedOrAcceptFriend(final int friendId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().invitedOrAcceptFriend(friendId, callBack);
            }
        });
    }

    /**
     * 审批成员入群申请
     *
     * @param chatGroupId((not hxGroupId)
     * @param jsonBody
     * @param callBack
     */
    public void approveMemberApply(final int chatGroupId, final boolean isRegion, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().approveMemberApply(chatGroupId, isRegion, jsonBody, callBack);
            }
        });
    }

    /**
     * 删除好友
     *
     * @param friendId((not hxid)
     * @param callBack
     */
    public void deleteFriend(final int friendId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().deleteFriend(friendId, callBack);
            }
        });
    }

    /**
     * 是否是好友关系
     *
     * @param friendId(not hxid)
     * @param callBack
     */
    public void friendRelationStatus(final int friendId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().friendRelationStatus(friendId, callBack);
            }
        });
    }


    /**
     * 获取应用列表
     *
     * @param page
     * @param size
     * @param callBack
     */
    public void getAppListFromServer(final int page, final int size, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getAppListFromServer(page, size, callBack);
            }
        });
    }

    /**
     * 获取常用应用列表
     *
     * @param page
     * @param size
     * @param callBack
     */
    public void getCommonAppListFromServer(final int page, final int size, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getCommonAppListFromServer(page, size, callBack);
            }
        });
    }

    /**
     * 添加常用应用列表
     *
     * @param callBack
     */
    public void addCommonAppListFromServer(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().addCommonAppListFromServer(jsonBody, callBack);
            }
        });
    }

    /**
     * 获取分组应用列表
     *
     * @param callBack
     */
    public void getGroupAppListFromServer(final EMDataCallBack callBack){
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getGroupAppListFromServer(callBack);
            }
        });
    }

    /**
     * 获取轮播图
     *
     * @param callBack
     */
    public void getBillBoardFromServer(final EMDataCallBack callBack){
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getBillBoardFromServer(callBack);
            }
        });
    }

    /**
     * 获取版本状态
     *
     * @param callBack
     */
    public void getVersionStatus(final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getVersionStatus(callBack);
            }
        });
    }

    /**
     * 新版本下载
     *
     * @param callBack
     */
//    public void downloadApk(final String url, final EMDataCallBack<String> callBack) {
//        requestThreadPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                EMRequestManager.getInstance().downloadApk(url, callBack);
//            }
//        });
//    }


    /**
     * 获取用户配置信息
     *
     * @param callBack
     */
    public void getUserOptions(final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getUserOptions(callBack);
            }
        });
    }

    /**
     * 获取租户配置信息
     */
    public void getTenantOptions(final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getTenantOptions(callBack);
            }
        });
    }

    /**
     * 获取会话列表
     */
    public void getSessions(final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getSessions(callBack);
            }
        });
    }

    /**
     * 删除会话
     */
    public void deleteSession(final String toId, final String chatType, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().deleteSession(toId, chatType, callBack);
            }
        });
    }

    /**
     * 添加会话
     */
    public void postSession(final String reqBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().postSession(reqBody, callBack);
            }
        });
    }

    /**
     * 修改会话
     */
    public void putSession(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().putSession(jsonBody, callBack);
            }
        });
    }

    /**
     * 创建日程
     */
    public void newSchedule(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().newSchedule(jsonBody, callBack);
            }
        });
    }

    /**
     * 以月为单位获取每天是否有日程
     * @param year
     * @param month
     * @param callBack
     */
    public void getScheduleByMonth(final int year, final int month, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getScheduleByMonth(year, month, callBack);
            }
        });
    }

    /**
     * 获取日程分配人
     * @param scheduleId
     * @param callBack
     */
    public void getScheduleReceiverList(final int scheduleId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getScheduleReceiverList(scheduleId, callBack);
            }
        });
    }

    /**
     * 获取指定日期日程
     */
    public void getAppointDateSchedules(final int year, final int month, final int day, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getAppointDateSchedules(year, month, day, callBack);
            }
        });
    }

    /**
     * 获取指定日期的当月日程统计
     */
    public void getStatisticsSchedules(final String appointDate, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getStatisticsSchedules(appointDate, callBack);
            }
        });
    }

    /**
     * 获取日程详细信息
     */
    public void getScheduleDetails(final int scheduleId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getScheduleDetails(scheduleId, callBack);
            }
        });
    }

    /**
     * 日程留言
     */
    public void publishScheduleMessage(final int scheduleId, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().publishScheduleMessage(scheduleId, jsonBody, callBack);
            }
        });
    }

    /**
     * 日程删除
     */
    public void deleteSchedule(final int scheduleId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().deleteSchedule(scheduleId, callBack);
            }
        });
    }

    /**
     * 日程修改
     */
    public void modifySchedule(final int scheduleId, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().modifySchedule(scheduleId, jsonBody, callBack);
            }
        });
    }

    /**
     * 日程接受
     */
    public void acceptSchedule(final int scheduleId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().acceptSchedule(scheduleId, callBack);
            }
        });
    }

    /**
     * 日程拒绝
     */
    public void refuseSchedule(final int scheduleId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().refuseSchedule(scheduleId, callBack);
            }
        });
    }

    /**
     * 删除附件
     * type    0 schedules 1 tasks
     */
    public void deleteAttachments(final int scheduleId, final String fileIds, final int type, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().deleteAttachments(scheduleId, fileIds, type, callBack);
            }
        });
    }


    /**
     * 创建任务
     */
    public void newTask(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().newTask(jsonBody, callBack);
            }
        });
    }


    /**
     * 获取未完成/已完成 任务列表
     *
     * @param status   0-未完成，1-已完成
     * @param isClosed 0-已打开，1-已关闭
     */
    public void getReceiveTaskList(final int status, final int isClosed, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getReceiveTaskList(status, isClosed, callBack);
            }
        });
    }

    /**
     * 获取我的所有 任务列表
     */

    public void getMyTaskList(final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getMyTaskList(callBack);
            }
        });
    }

    /**
     * 获取我创建的任务列表
     *
     * @param isClosed 0-已打开，1-已关闭
     */
    public void getCreateTaskList(final int isClosed, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getCreateTaskList(isClosed, callBack);
            }
        });
    }

    /**
     * 获取抄送的任务列表
     *
     * @param isClosed 0-已打开，1-已关闭
     */
    public void getCcTaskList(final int isClosed, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getCcTaskList(isClosed, callBack);
            }
        });
    }


    /**
     * 获取任务详细信息
     */
    public void getTaskDetails(final int taskId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getTaskDetails(taskId, callBack);
            }
        });
    }

    /**
     * 任务留言
     */
    public void publishTaskMessage(final int taskId, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().publishTaskMessage(taskId, jsonBody, callBack);
            }
        });
    }

    /**
     * 接收方标记 已完成/未完成
     *
     * @param finishedStatus 0-未完成，1-已完成
     */
    public void receiverMarkFinishedStatus(final int taskId, final int finishedStatus, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().markFinishedStatus(taskId, "rec", finishedStatus, jsonBody, callBack);
            }
        });
    }

    /**
     * 创建者标记 已完成/未完成
     *
     * @param finishedStatus 0-未完成，1-已完成
     */
    public void creatorMarkFinishedStatus(final int taskId, final int finishedStatus, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().markFinishedStatus(taskId, "cre", finishedStatus, jsonBody, callBack);
            }
        });
    }

    /**
     * 删除任务
     */
    public void deleteTask(final int taskId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().deleteTask(taskId, callBack);
            }
        });
    }

    /**
     * 关闭任务
     */
    public void closeTask(final int taskId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().closeOrOpenTask(taskId, "close", callBack);
            }
        });
    }

    /**
     * 打开任务
     */
    public void openTask(final int taskId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().closeOrOpenTask(taskId, "open", callBack);
            }
        });
    }

    /**
     * 修改任务
     */
    public void modifyTask(final int taskId, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().modifyTask(taskId, jsonBody, callBack);
            }
        });
    }

    /**
     * 通过mcu创建会议
     */
    public void createConferenceWithMCU(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().createConferenceWithMCU(jsonBody, callBack);
            }
        });
    }

    /**
     * 通过mcu加入会议
     */
    public void joinConferenceWithMCU(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().joinConferenceWithMCU(jsonBody, callBack);
            }
        });
    }

    /**
     * 直连sip通过mcu创建会议
     */
    public void directCreateConferenceWithMCU(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().directCreateConferenceWithMCU(jsonBody, callBack);
            }
        });
    }

    /**
     * 直连sip通过mcu加入会议
     */
    public void directJoinConferenceWithMCU(final String sessionId, final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().directJoinConferenceWithMCU(sessionId, jsonBody, callBack);
            }
        });
    }

    /**
     * 非聊天版 创建会议
     */
    public void startConference(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().startConference(jsonBody, callBack);
            }
        });
    }

    /**
     * 非聊天版 加入会议
     */
    public void joinConference(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().joinConference(jsonBody, callBack);
            }
        });
    }

    /**
     * 非聊天版 会议列表
     * type 1 我创建的 2 我加入的
     */
    public void getConferenceList(final int type, final int page, final int size, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (type == 1) {
                    //我创建的会议列表
                    EMRequestManager.getInstance().getConferenceListOfMine(page, size, callBack);
                } else {
                    //我加入的会议列表
                    EMRequestManager.getInstance().getConferenceListOfJoined(page, size, callBack);
                }
            }
        });
    }


    /**
     * 非聊天版 获取会议详情
     */
    public void getConferenceDetail(final int confId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getConferenceDetail(confId, callBack);
            }
        });
    }


    /**
     * 非聊天版 获取会议参与者列表
     */
    public void getConfParticipants(final int confId, final boolean isRegion, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getConfParticipants(confId, isRegion, callBack);
            }
        });
    }

    /**
     * 非聊天版 获取会议参与者列表
     */
    public void getConfListItemDetail(final int confId, final boolean isRegion,final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getConfListItemDetail(confId, isRegion, callBack);
            }
        });
    }

    /**
     * 非聊天版 从会议列表详情中加入会议
     */
    public void joinConfFromListItem(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().joinConfFromListItem(jsonBody, callBack);
            }
        });
    }

    /**
     * 非聊天版 从会议列表详情中尝试加入会议
     */
    public void tryToJoinConfFromListItem(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().tryToJoinConfFromListItem(jsonBody, callBack);
            }
        });
    }
    /**
     * 非聊天版 主持人关闭会议
     */
    public void closeConfByHost(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().closeConfByHost(jsonBody, callBack);
            }
        });
    }


    /**
     * 非聊天版 主持人踢出与会者
     */
    public void kickUserByHost(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().kickUserByHost(jsonBody, callBack);
            }
        });
    }


    /**
     * 非聊天版 主持人改变与会者视频权限
     */
    public void changeUserVideoPermissionByHost(final String jsonBody, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().changeUserVideoPermissionByHost(jsonBody, callBack);
            }
        });
    }


    /**
     * 非聊天版 获取会议视频文件路径
     */
    public void getConfVideoFilePath(final String confId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getConfVideoFilePath(confId, callBack);
            }
        });
    }

    /**
     * 非聊天版 主持人静音所有人
     */
    public void quietAll(final int confId, final boolean isRegion, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().quietAll(confId, isRegion, callBack);
            }
        });
    }

    /**
     * 创建单/群聊音视频跨区会议
     * @param callBack
     */
    public void createRegionConference(final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().createRegionConference(callBack);
            }
        });
    }

    /**
     * 销毁单/群聊音视频跨区会议
     * @param callBack
     */
    public void destroyRegionConference(final String confId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().destroyRegionConference(confId, callBack);
            }
        });
    }

    public void getBindSchedule(final String reservationId, final EMDataCallBack<String> callBack) {
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getBindSchedule(reservationId, callBack);
            }
        });
    }

    /**
     * 获取服务配置
     * @param callBack
     */
    public void getServiceConfig(final EMDataCallBack<String> callBack){
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getServiceConfig(callBack);
            }
        });
    }

    /**
     * 创建投票
     * @param jsonBody
     * @param callBack
     */
    public void createVote(final String jsonBody, final EMDataCallBack<String> callBack){
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().createVote(jsonBody, callBack);
            }
        });
    }

    /**
     * 进行投票
     * @param jsonBody
     * @param callBack
     */
    public void takeVote(final String jsonBody, final EMDataCallBack<String> callBack){
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().takeVote(jsonBody, callBack);
            }
        });
    }

    /**
     * 查询投票详情
     * @param voteId
     * @param callBack
     */
    public void getVoteInfo(final String voteId, final EMDataCallBack<String> callBack){
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getVoteInfo(voteId, callBack);
            }
        });
    }

    /**
     * 获取投票选项详情
     * @param voteOptionId
     * @param callBack
     */
    public void getVoteOptionInfo(final String voteOptionId, final EMDataCallBack<String> callBack){
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getVoteOptionInfo(voteOptionId, callBack);
            }
        });
    }

    /**
     * 结束投票
     * @param voteId
     * @param callBack
     */
    public void closeVote(final String voteId, final EMDataCallBack<String> callBack){
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().closeVote(voteId, callBack);
            }
        });
    }

    /**
     * 删除投票
     * @param voteId
     * @param callBack
     */
    public void removeVote(final String voteId, final EMDataCallBack<String> callBack){
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().removeVote(voteId, callBack);
            }
        });
    }

    /**
     * 获取服务器ip配置
     * @param ip
     * @param callBack
     */
    public void getServerIp(final String ip, final EMDataCallBack<String> callBack){
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getServerIp(ip, callBack);
            }
        });
    }

    /**
     * 添加稍后处理消息
     * @param jsonBody
     * @param callBack
     */
    public void addToDoList(final String jsonBody, final EMDataCallBack<String> callBack){
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().addToDoList(jsonBody, callBack);
            }
        });
    }

    /**
     * 查询所有稍后处理消息
     * @param callBack
     */
    public void getAllToDoList(final EMDataCallBack<String> callBack){
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().getAllToDoList(callBack);
            }
        });
    }

    /**
     * 处理稍后处理消息
     * @param jsonBody
     * @param callBack
     */
    public void dealToDoList(final String jsonBody, final EMDataCallBack<String> callBack){
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().dealToDoList(jsonBody, callBack);
            }
        });
    }

    /**
     * 删除稍后处理消息
     * @param todoId
     * @param callBack
     */
    public void removeToDoList(final String todoId, final EMDataCallBack<String> callBack){
        requestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EMRequestManager.getInstance().removeToDoList(todoId, callBack);
            }
        });
    }
}
