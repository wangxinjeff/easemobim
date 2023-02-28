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
package com.hyphenate.easemob.im.officeautomation.db;

import android.content.Context;
import android.util.Log;

import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;

import java.util.ArrayList;
import java.util.List;

public class UserDao {
    public static final String EXT_TABLE_NAME = "ext_fields";
    public static final String COLUMN_EXT_NAME_IM_USER_ID = "im_user_id";
    public static final String COLUMN_EXT_NAME_USER_ID = "user_id";
    public static final String COLUMN_EXT_NAME_NICK = "nick";
    public static final String COLUMN_EXT_NAME_AVATAR = "avatar";
    public static final String COLUMN_EXT_NAME_ACCOUNT = "account";
    public static final String COLUMN_EXT_NAME_GENDER = "gender";
    public static final String COLUMN_EXT_NAME_PHONE = "phone";
    public static final String COLUMN_EXT_NAME_TEL_PHONE = "tel_phone";
    public static final String COLUMN_EXT_NAME_EMAIL = "email";
    public static final String COLUMN_EXT_NAME_USER_PINYIN = "pinyin";
    public static final String COLUMN_EXT_NAME_STAFFNO = "staff_no";
    public static final String COLUMN_EXT_NAME_USER_TYPE = "cache_user_type";
    public static final String COLUMN_EXT_NAME_ORG_ID = "org_id";
    public static final String COLUMN_EXT_NAME_ALIAS = "alias";

    public static final String TABLE_NAME = "users";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_AVATAR = "avatar";

    public static final String PREF_TABLE_NAME = "pref";
    public static final String COLUMN_NAME_DISABLED_GROUPS = "disabled_groups";
    public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";

    public UserDao(Context context) {
    }

//    public void setDisabledGroups(List<String> groups) {
//        AppDBManager.getInstance().setDisabledGroups(groups);
//    }

    public List<String> getDisabledGroups() {
        return AppDBManager.getInstance().getDisabledGroups();
    }

    public void setDisabledIds(List<String> ids) {
        AppDBManager.getInstance().setDisabledIds(ids);
    }

    public List<String> getDisabledIds() {
        return AppDBManager.getInstance().getDisabledIds();
    }

    public void saveUsersList(List<MPUserEntity> usersList) {
        AppDBManager.getInstance().saveUsersList(usersList);
    }

    public void saveUserInfo(MPUserEntity user) {
        AppDBManager.getInstance().saveUserInfo(user);
    }

    public boolean saveMPUserList(List<MPUserEntity> mpUserEntities) {
        return AppDBManager.getInstance().saveMPUserList(mpUserEntities);
    }

    public EaseUser getUserExtInfo(int userId) {
        return AppDBManager.getInstance().getUserExtInfo(userId);
    }

    public List<EaseUser> getUserExtInfos(String imUserIds) {
        return AppDBManager.getInstance().getUserExtInfos(imUserIds);
    }

    public List<EaseUser> getUserExtInfosById(List<Integer> userIds) {
        return AppDBManager.getInstance().getUserExtInfosById(userIds);
    }

    public List<String> getUserNamesByIds(String intUserIds) {
        return AppDBManager.getInstance().getUserNameExtInfosById(intUserIds);
    }

    public EaseUser getUserExtInfo(String easemobName) {
        return AppDBManager.getInstance().getUserByEasemobName(easemobName);
    }

//    public void saveUserExtInfo(EaseUser user) {
//        AppDBManager.getInstance().saveUserExtInfo(user);
//    }

    public ArrayList<EaseUser> getExtUserList() {
        return AppDBManager.getInstance().getExtUserList();
    }

    public MPUserEntity getUserInfo(String hxId) {
        return AppDBManager.getInstance().getUserInfo(hxId);
    }

    public MPUserEntity getUserInfoById(int userId) {
        return AppDBManager.getInstance().getUserInfoById(userId);
    }

    public List<MPUserEntity> getUsersByOrgId(int orgId) {
        return AppDBManager.getInstance().getUsersByOrgId(orgId);
    }

    public List<EaseUser> getExtUsersByOrgId(int orgId) {
        return AppDBManager.getInstance().getExtUsersByOrgId(orgId);
    }

    public List<MPUserEntity> searchUsersByKeyword(String searchText) {
        return AppDBManager.getInstance().searchUsersByKeyword(searchText);
    }

    public void delUserInfo(int userId) {
        try {
            AppDBManager.getInstance().delUserInfo(userId);
        } catch (Exception ignored) {
            MPLog.d(TABLE_NAME, Log.getStackTraceString(ignored));
        }
    }

    public void deleteAllUsers() {
        try {
            AppDBManager.getInstance().deleteAllUsers();
        } catch (Exception ignored) {
            MPLog.d(TABLE_NAME, Log.getStackTraceString(ignored));
        }
    }

    public void deleteAllOrgs() {
        try {
            AppDBManager.getInstance().deleteAllOrgs();
        } catch (Exception ignored) {
            MPLog.d(TABLE_NAME, Log.getStackTraceString(ignored));
        }
    }
}
