package com.hyphenate.easemob.im.mp;

import android.content.Context;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.DraftEntity;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.im.officeautomation.db.AppDBManager;
import com.hyphenate.easemob.im.officeautomation.db.GroupDao;
import com.hyphenate.easemob.im.officeautomation.db.GroupMuteDao;
import com.hyphenate.easemob.im.officeautomation.db.OrgDao;
import com.hyphenate.easemob.im.officeautomation.db.UserDao;
import com.hyphenate.easemob.im.officeautomation.domain.ReferenceMsgEntity;
import com.hyphenate.easemob.im.officeautomation.domain.NoDisturbEntity;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.im.officeautomation.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppModel {
    private UserDao dao = null;
    protected Context context = null;
    private Map<Key, Object> valueCache = new HashMap<Key, Object>();

    public AppModel(Context ctx) {
        context = ctx;
        PreferenceManager.init(context);
    }

    public void saveAllOrgsList(List<MPOrgEntity> entities) {
        OrgDao dao = new OrgDao();
        dao.saveAllOrgsList(entities);
    }


    public void saveOrgInfo(MPOrgEntity org) {
        OrgDao dao = new OrgDao();
        dao.saveOrgInfo(org);
    }

    public void delOrgInfo(int orgId) {
        OrgDao dao = new OrgDao();
        dao.delOrgInfo(orgId);
    }

    public MPOrgEntity getOrgInfo(int orgId) {
        OrgDao dao = new OrgDao();
        return dao.getOrgInfo(orgId);
    }

    public List<MPOrgEntity> getOrgsListByParent(int parentId) {
        OrgDao dao = new OrgDao();
        return dao.getOrgsListByParent(parentId);
    }

    public boolean saveOrgsListByParentOrgId(List<MPOrgEntity> orgEntities) {
        OrgDao dao = new OrgDao();
        return dao.saveOrgsListByParentOrgId(orgEntities);
    }

    public void saveUsersList(List<MPUserEntity> usersList) {
        UserDao dao = new UserDao(context);
        dao.saveUsersList(usersList);
    }

    //根据团队Id获取团队成员
    public List<MPUserEntity> getUsersByOrgId(int orgId) {
        UserDao dao = new UserDao(context);
        return dao.getUsersByOrgId(orgId);
    }

    //根据团队Id获取团队成员扩展信息
    public List<EaseUser> getExtUsersByOrgId(int orgId) {
        UserDao dao = new UserDao(context);
        return dao.getExtUsersByOrgId(orgId);
    }

    //根据关键字搜索联系人
    public List<MPUserEntity> searchUsersByKeyword(String searchText) {
        UserDao dao = new UserDao(context);
        return dao.searchUsersByKeyword(searchText);
    }

//    public boolean saveUserExtInfo(String easemobName, int id, String realName, String image) {
//        EaseUser user = new EaseUser();
//        user.setId(id);
//        user.setUsername(easemobName);
//        user.setNickname(realName);
//        user.setAvatar(image);
//        return saveUserExtInfo(user);
//    }

//    public boolean saveUserExtInfo(String easemobName, String id, String realName, String image) {
//        int userId = -1;
//        try {
//            userId = Integer.parseInt(id);
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
//        return saveUserExtInfo(easemobName, userId, realName, image);
//    }

    public ArrayList<EaseUser> getExtUserList() {
        UserDao dao = new UserDao(context);
        return dao.getExtUserList();
    }

    public MPUserEntity getUserInfo(String hxId) {
        UserDao dao = new UserDao(context);
        return dao.getUserInfo(hxId);
    }

    public MPUserEntity getUserInfoById(int userId) {
        UserDao dao = new UserDao(context);
        return dao.getUserInfoById(userId);
    }

    public boolean saveUserInfo(MPUserEntity user) {
        UserDao dao = new UserDao(context);
        dao.saveUserInfo(user);
        UserProvider.getInstance().removeEaseUser(user.getImUserId());
        return true;
    }

    public boolean saveMPUserList(List<MPUserEntity> userEntities) {
        UserDao userDao = new UserDao(context);
        return userDao.saveMPUserList(userEntities);
    }

    public void delUserInfo(int userId) {
        UserDao dao = new UserDao(context);
        dao.delUserInfo(userId);
    }

//    public boolean saveUserExtInfo(EaseUser user) {
//        UserDao dao = new UserDao(context);
//        dao.saveUserExtInfo(user);
//        return true;
//    }

    public EaseUser getUserExtInfo(int userId) {
        UserDao dao = new UserDao(context);
        return dao.getUserExtInfo(userId);
    }

    public EaseUser getUserExtInfo(String easemobName) {
        UserDao dao = new UserDao(context);
        return dao.getUserExtInfo(easemobName);
    }

    public List<EaseUser> getUserExtInfos(String imUserIds) {
        UserDao dao = new UserDao(context);
        return dao.getUserExtInfos(imUserIds);
    }

    public List<EaseUser> getUserExtInfosById(List<Integer> userIds) {
        UserDao dao = new UserDao(context);
        return dao.getUserExtInfosById(userIds);
    }

    public List<String> getUserNamesByIds(String intUserIds) {
        UserDao dao = new UserDao(context);
        return dao.getUserNamesByIds(intUserIds);
    }

    public void delGroupInfo(String imGroupId) {
        GroupDao dao = new GroupDao();
        dao.delGroupInfo(imGroupId);
    }

    public void delGroupInfoById(int groupId) {
        GroupDao dao = new GroupDao();
        dao.delGroupInfoById(groupId);
    }


    public ArrayList<GroupBean> getExtGroupList() {
        GroupDao dao = new GroupDao();
        return dao.getExtGroupList();
    }

    public List<MPGroupEntity> getAllGroups() {
        GroupDao dao = new GroupDao();
        return dao.getAllGroups();
    }

//    public void saveExtGroupList(List<GroupBean> groupsList) {
//        GroupDao dao = new GroupDao();
//        dao.saveExtGroupList(groupsList);
//    }

    public void saveGroupEntities(List<MPGroupEntity> groupEntities) {
        GroupDao dao = new GroupDao();
        dao.saveMPGroupList(groupEntities);
    }

//    public boolean saveGroupInfo(int groupId, String easemob_group_id, String nick, String avatar) {
//        GroupBean groupBean = new GroupBean(groupId, easemob_group_id, nick, avatar);
//        return saveGroupInfo(groupBean);
//    }

//    public boolean saveGroupInfo(GroupBean groupBean) {
//        GroupDao dao = new GroupDao();
//        dao.saveGroupInfo(groupBean);
//        return true;
//    }

    public boolean saveGroupInfo(MPGroupEntity groupEntity) {
        GroupDao dao = new GroupDao();
        dao.saveGroupInfo(groupEntity);
        return true;
    }

    public GroupBean getGroupInfo(String easemob_group_id) {
        GroupDao dao = new GroupDao();
        return dao.getGroupInfo(easemob_group_id);
    }

    public GroupBean getGroupInfoById(int groupId) {
        GroupDao dao = new GroupDao();
        return dao.getGroupInfoById(groupId);
    }

    public void deleteGroupInfo(String easemob_group_id) {
        GroupDao dao = new GroupDao();
        dao.delGroupInfo(easemob_group_id);
    }

    public void deleteGroupInfoById(int groupId) {
        GroupDao dao = new GroupDao();
        dao.delGroupInfoById(groupId);
    }

    public void deleteAllGroup() {
        GroupDao dao = new GroupDao();
        dao.deleteAllGroup();
    }

    public void deleteAllUsers() {
        UserDao dao = new UserDao(context);
        dao.deleteAllUsers();
    }

    public void deleteAllOrgs() {
        UserDao dao = new UserDao(context);
        dao.deleteAllOrgs();
    }

    /**
     * save current username
     *
     * @param username
     */
    public void setCurrentUserName(String username) {
        PreferenceManager.getInstance().setCurrentUserName(username);
    }

    public String getCurrentUserName() {
        return PreferenceManager.getInstance().getCurrentUsername();
    }


    //======================Group Mutes start ====================
    public void muteGroupUsername(String groupId, String username, long muteTime) {
        GroupMuteDao groupMuteDao = new GroupMuteDao();
        groupMuteDao.muteGroupUsername(groupId, username, muteTime);
    }

    public void muteGroupUsernames(String groupId, List<String> imUsernames, long muteTime) {
        GroupMuteDao groupMuteDao = new GroupMuteDao();
        groupMuteDao.muteGroupUsernames(groupId, imUsernames, muteTime);
    }

    public void unMuteGroupUsernames(String groupId, List<String> imUsernames) {
        GroupMuteDao groupMuteDao = new GroupMuteDao();
        groupMuteDao.unMuteGroupUsernames(groupId, imUsernames);
    }

    public void unMuteGroupUsername(String groupId, String username) {
        GroupMuteDao groupMuteDao = new GroupMuteDao();
        groupMuteDao.unMuteGroupUsername(groupId, username);
    }

    public void updateGroupMutes(String groupId, Map<String, Long> imUsernames) {
        GroupMuteDao groupMuteDao = new GroupMuteDao();
        groupMuteDao.updateGroupMutes(groupId, imUsernames);
    }

    public boolean isMute(String groupId, String imUsername) {
        GroupMuteDao groupMuteDao = new GroupMuteDao();
        return groupMuteDao.isMute(groupId, imUsername);
    }
    //======================Group Mutes end====================

    public void setSettingMsgNotification(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgNotification(paramBoolean);
        valueCache.put(Key.VibrateAndPlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgNotification() {
        Object val = valueCache.get(Key.VibrateAndPlayToneOn);

        if (val == null) {
            val = PreferenceManager.getInstance().getSettingMsgNotification();
            valueCache.put(Key.VibrateAndPlayToneOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    public void setSettingMsgSound(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgSound(paramBoolean);
        valueCache.put(Key.PlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgSound() {
        Object val = valueCache.get(Key.PlayToneOn);

        if (val == null) {
            val = PreferenceManager.getInstance().getSettingMsgSound();
            valueCache.put(Key.PlayToneOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    public void setSettingMsgVibrate(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgVibrate(paramBoolean);
        valueCache.put(Key.VibrateOn, paramBoolean);
    }

    public boolean getSettingMsgVibrate() {
        Object val = valueCache.get(Key.VibrateOn);

        if (val == null) {
            val = PreferenceManager.getInstance().getSettingMsgVibrate();
            valueCache.put(Key.VibrateOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }

//    public void setSettingMsgSpeaker(boolean paramBoolean) {
//        PreferenceManager.getInstance().setSettingMsgSpeaker(paramBoolean);
//        valueCache.put(Key.SpakerOn, paramBoolean);
//    }

    public boolean getSettingMsgSpeaker() {
        Object val = valueCache.get(Key.SpakerOn);

        if (val == null) {
            val = PreferenceManager.getInstance().getSettingMsgSpeaker();
            valueCache.put(Key.SpakerOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }


//    public void setDisabledGroups(List<String> groups) {
//        if (dao == null) {
//            dao = new UserDao(context);
//        }
//
//        List<String> list = new ArrayList<String>();
//        list.addAll(groups);
//        for (int i = 0; i < list.size(); i++) {
//            if (EaseAtMessageHelper.get().getAtMeGroups().contains(list.get(i))) {
//                list.remove(i);
//                i--;
//            }
//        }
//
//        dao.setDisabledGroups(list);
//        valueCache.put(Key.DisabledGroups, list);
//    }

    public List<String> getDisabledGroups() {
        Object val = valueCache.get(Key.DisabledGroups);

        if (dao == null) {
            dao = new UserDao(context);
        }

        if (val == null) {
            val = dao.getDisabledGroups();
            valueCache.put(Key.DisabledGroups, val);
        }

        //noinspection unchecked
        return (List<String>) val;
    }

    public void setDisabledIds(List<String> ids) {
        if (dao == null) {
            dao = new UserDao(context);
        }

        dao.setDisabledIds(ids);
        valueCache.put(Key.DisabledIds, ids);
    }

    public List<String> getDisabledIds() {
        Object val = valueCache.get(Key.DisabledIds);

        if (dao == null) {
            dao = new UserDao(context);
        }

        if (val == null) {
            val = dao.getDisabledIds();
            valueCache.put(Key.DisabledIds, val);
        }

        //noinspection unchecked
        return (List<String>) val;
    }

    public void setGroupsSynced(boolean synced) {
        PreferenceManager.getInstance().setGroupsSynced(synced);
    }

    public boolean isGroupsSynced() {
        return PreferenceManager.getInstance().isGroupsSynced();
    }

    public boolean isChatroomOwnerLeaveAllowed() {
        return PreferenceManager.getInstance().getSettingAllowChatroomOwnerLeave();
    }

    public boolean isDeleteMessagesAsExitGroup() {
        return PreferenceManager.getInstance().isDeleteMessagesAsExitGroup();
    }

    public boolean isSetTransferFileByUser() {
        return PreferenceManager.getInstance().isSetTransferFileByUser();
    }

    public boolean isSetAutodownloadThumbnail() {
        return PreferenceManager.getInstance().isSetAutodownloadThumbnail();
    }

    public boolean isAutoAcceptGroupInvitation() {
        return PreferenceManager.getInstance().isAutoAcceptGroupInvitation();
    }

    public boolean isPushCall() {
        return PreferenceManager.getInstance().isPushCall();
    }


    //设置通知是否显示详情
    public void setShowNotifyDetails(boolean isShowNotifyDetails) {
        PreferenceManager.getInstance().setShowNotifyDetails(isShowNotifyDetails);
    }

    //获取通知是否显示详情
    public boolean isShowNotifyDetails() {
        return PreferenceManager.getInstance().getShowNotifyDetails();
    }


    public void asyncSaveDraft(DraftEntity entity) {
        EaseUI.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                AppDBManager.getInstance().saveDraft(entity);
            }
        });
    }

    public void asyncRemoveDraft(String id) {
        EaseUI.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                AppDBManager.getInstance().removeDraft(id);
            }
        });
    }

    public void asyncGetDraft(String id, EMValueCallBack<DraftEntity> callBack) {
        EaseUI.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                DraftEntity draftEntity = AppDBManager.getInstance().getDraft(id);
                if (callBack != null) {
                    callBack.onSuccess(draftEntity);
                }
            }
        });
    }

    public void asyncGetDrafts(EMValueCallBack<List<DraftEntity>> callBack) {
        EaseUI.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                List<DraftEntity> draftEntities = AppDBManager.getInstance().getDraftEntities();
                if (callBack != null) {
                    callBack.onSuccess(draftEntities);
                }
            }
        });
    }

    public void asyncGetNoDisturbs(EMValueCallBack<List<NoDisturbEntity>> callBack) {
        EaseUI.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                List<NoDisturbEntity> disturbEntities = AppDBManager.getInstance().getAllNoDisturbs();
                if (callBack != null) {
                    callBack.onSuccess(disturbEntities);
                }
            }
        });
    }

    public void asyncRemoveNoDistrub(String disturbId) {
        EaseUI.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                AppDBManager.getInstance().removeNoDisturb(disturbId);
            }
        });
    }

    public void asyncSaveNoDisturb(NoDisturbEntity noDisturbEntity) {
        EaseUI.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                AppDBManager.getInstance().saveNoDisturb(noDisturbEntity);
            }
        });
    }

    public void saveReferenceMsg(ReferenceMsgEntity entity){
        EaseUI.getInstance().execute(() -> AppDBManager.getInstance().saveReferenceMsg(entity));
    }

    public void getReferenceDataWithMsgId(String msgId, EMValueCallBack<List<ReferenceMsgEntity>> callBack){
        EaseUI.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                List<ReferenceMsgEntity> list = AppDBManager.getInstance().getReferenceDataWithMsgId(msgId);
                callBack.onSuccess(list);
            }
        });
    }

    public void saveVoteAndMsgId(String voteId, String msgId){
        EaseUI.getInstance().execute(() -> AppDBManager.getInstance().saveVoteAndMsgId(voteId, msgId));
    }

    public void getMsgIdWithVoteId(String voteId, EMValueCallBack<List<String>> callBack){
        EaseUI.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                List<String> list = AppDBManager.getInstance().getMsgIdWithVoteId(voteId);
                callBack.onSuccess(list);
            }
        });
    }

    enum Key {
        VibrateAndPlayToneOn,
        VibrateOn,
        PlayToneOn,
        SpakerOn,
        DisabledGroups,
        DisabledIds
    }
}
