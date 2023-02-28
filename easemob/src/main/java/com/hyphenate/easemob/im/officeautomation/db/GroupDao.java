package com.hyphenate.easemob.im.officeautomation.db;

import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;

import java.util.ArrayList;
import java.util.List;

public class GroupDao {
    public static final String GROUP_TABLE_NAME = "table_group_info";
    public static final String COLUMN_NAME_GROUP_ID = "group_id";
    public static final String COLUMN_NAME_EASEMOB_GROUP_ID = "easemob_group_id";
    public static final String COLUMN_NAME_GROUP_NAME = "group_name";
    public static final String COLUMN_NAME_GROUP_AVATAR = "group_avatar";
    public static final String COLUMN_NAME_CREATE_TIME = "create_time";
    public static final String COLUMN_NAME_TYPE = "type";
    public static final String COLUMN_NAME_CLUSTER = "cluster";

//    public void saveGroupInfo(GroupBean groupBean) {
//        AppDBManager.getInstance().saveGroupInfo(groupBean);
//    }

    public void saveGroupInfo(MPGroupEntity groupEntity) {
        AppDBManager.getInstance().saveGroupInfo(groupEntity);
    }


    public GroupBean getGroupInfo(String easemob_group_id) {
        return AppDBManager.getInstance().getGroupInfo(easemob_group_id);
    }

    public GroupBean getGroupInfoById(int groupId) {
        return AppDBManager.getInstance().getGroupInfoById(groupId);
    }

    public ArrayList<GroupBean> getExtGroupList() {
        return AppDBManager.getInstance().getExtGroupList();
    }

    public List<MPGroupEntity> getAllGroups() {
        return AppDBManager.getInstance().getMPGroupEntities();
    }

    public void saveExtGroupList(List<GroupBean> groupsList) {
        AppDBManager.getInstance().saveExtGroupList(groupsList);
    }

    public void saveMPGroupList(List<MPGroupEntity> groupEntities) {
        AppDBManager.getInstance().saveMPGroupList(groupEntities);
    }

    public void delGroupInfo(String imGroupId) {
        AppDBManager.getInstance().delGroupInfo(imGroupId);
    }

    public void delGroupInfoById(int groupId) {
        AppDBManager.getInstance().delGroupInfoById(groupId);
    }

    public void deleteAllGroup(){
        if (AppDBManager.getInstance() != null){
            AppDBManager.getInstance().deleteAllGroup();
        }
    }
}
