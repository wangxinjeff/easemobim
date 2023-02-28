package com.hyphenate.easemob.im.officeautomation.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.DraftEntity;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.officeautomation.domain.ReferenceMsgEntity;
import com.hyphenate.easemob.im.officeautomation.domain.NoDisturbEntity;
import com.hyphenate.easemob.im.officeautomation.domain.InviteMessage;
import com.hyphenate.easemob.im.officeautomation.domain.InviteMessage.InviteMessageStatus;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPSessionEntity;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.imlibs.officeautomation.domain.TenantOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppDBManager {
    private static final String TAG = AppDBManager.class.getSimpleName();
    private static DbOpenHelper dbHelper;
    private static AppDBManager dbMgr = new AppDBManager();
    private String currentUserId;

    public synchronized static void initDB(String userId) {
        MPLog.e(TAG, "initDB: " + userId);
        if (dbMgr.currentUserId != null && !dbMgr.currentUserId.equals(userId)) {
            closeDatabase();
        }

        if (dbHelper == null) {
            dbHelper = DbOpenHelper.getInstance(EaseUI.getInstance().getContext(), userId);
        }
        dbMgr.currentUserId = userId;
    }


    public static void closeDatabase() {
        DbOpenHelper.closeDB();
    }


    private AppDBManager() {
    }

    public static synchronized AppDBManager getInstance() {
        return dbMgr;
    }

    //======================== org db start ========================

    /**
     * 保存组织机构信息列表
     *
     * @param org
     */
    synchronized void saveOrgInfo(MPOrgEntity org) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(OrgDao.COLUMN_NAME_ORG_ID, org.getId());
            values.put(OrgDao.COLUMN_NAME_ORG_PARENT_ID, org.getParentId());
            values.put(OrgDao.COLUMN_NAME_ORG_COMPANY_ID, org.getCompanyId());
            values.put(OrgDao.COLUMN_NAME_ORG_TENANT_ID, org.getTenantId());
            values.put(OrgDao.COLUMN_NAME_ORG_DEPTH, org.getDepth());
            values.put(OrgDao.COLUMN_NAME_ORG_MEMBER_COUNT, org.getMemberCount());
            if (!TextUtils.isEmpty(org.getName()))
                values.put(OrgDao.COLUMN_NAME_ORG_NAME, org.getName());
            if (!TextUtils.isEmpty(org.getRank()))
                values.put(OrgDao.COLUMN_NAME_ORG_RANK, org.getRank());
            db.replace(OrgDao.TABLE_NAME_ORG, null, values);
        }
    }

    /**
     * 根据orgId删除部门
     *
     * @param orgId
     */
    public synchronized void delOrgInfo(int orgId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(OrgDao.TABLE_NAME_ORG, OrgDao.COLUMN_NAME_ORG_ID + " = ?", new String[]{String.valueOf(orgId)});
        }
    }


    private synchronized void deleteOrgEntityByParentOrgId(int parentOrgId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(OrgDao.TABLE_NAME_ORG, OrgDao.COLUMN_NAME_ORG_PARENT_ID + " = ?", new String[]{String.valueOf(parentOrgId)});
        }
    }

    public boolean saveOrgsListByParentOrgId(List<MPOrgEntity> orgEntities) {
        if (orgEntities == null || orgEntities.isEmpty()) {
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (!db.isOpen()) {
            return false;
        }
        int parentOrgId = orgEntities.get(0).getParentId();
        deleteOrgEntityByParentOrgId(parentOrgId);
        saveAllOrgsList(orgEntities);
        return true;
    }


    /**
     * 根据orID查询组织名称
     *
     * @param orgId
     * @return
     */
    public synchronized MPOrgEntity getOrgInfo(int orgId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        MPOrgEntity orgEntity = null;
        if (db.isOpen()) {
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("select * from " + OrgDao.TABLE_NAME_ORG + " where " + OrgDao.COLUMN_NAME_ORG_ID + " =?", new String[]{String.valueOf(orgId)});
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        orgEntity = getOrgEntityFromCursor(cursor);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return orgEntity;
    }

    private MPOrgEntity getOrgEntityFromCursor(Cursor cursor) {
        int org_id = cursor.getInt(cursor.getColumnIndex(OrgDao.COLUMN_NAME_ORG_ID));
        int parent_id = cursor.getInt(cursor.getColumnIndex(OrgDao.COLUMN_NAME_ORG_PARENT_ID));
        int company_id = cursor.getInt(cursor.getColumnIndex(OrgDao.COLUMN_NAME_ORG_COMPANY_ID));
        int tenant_id = cursor.getInt(cursor.getColumnIndex(OrgDao.COLUMN_NAME_ORG_TENANT_ID));
        String org_name = cursor.getString(cursor.getColumnIndex(OrgDao.COLUMN_NAME_ORG_NAME));
        String org_rank = cursor.getString(cursor.getColumnIndex(OrgDao.COLUMN_NAME_ORG_RANK));
        int depth = cursor.getInt(cursor.getColumnIndex(OrgDao.COLUMN_NAME_ORG_DEPTH));
        int memberCount = cursor.getInt(cursor.getColumnIndex(OrgDao.COLUMN_NAME_ORG_MEMBER_COUNT));
        MPOrgEntity orgEntity = new MPOrgEntity();
        orgEntity.setId(org_id);
        orgEntity.setTenantId(tenant_id);
        orgEntity.setParentId(parent_id);
        orgEntity.setName(org_name);
        orgEntity.setCompanyId(company_id);
        orgEntity.setRank(org_rank);
        orgEntity.setDepth(depth);
        orgEntity.setMemberCount(memberCount);
        return orgEntity;
    }

    /**
     * 获取parentId下的部门列表信息
     *
     * @param parentOrgId 父部门id
     * @return List&lt;OrgEntity&gt;
     */
    public synchronized List<MPOrgEntity> getOrgsListByParent(int parentOrgId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<MPOrgEntity> orgsList = new ArrayList<>();
        if (db.isOpen()) {
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("select * from " + OrgDao.TABLE_NAME_ORG + " where " + OrgDao.COLUMN_NAME_ORG_PARENT_ID + " =?",
                        new String[]{String.valueOf(parentOrgId)});
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        orgsList.add(getOrgEntityFromCursor(cursor));
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return orgsList;
    }


    synchronized public void deleteAllOrgs() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(OrgDao.TABLE_NAME_ORG, null, null);
        }
    }


    //======================== org db end ========================


    //======================== user db start ========================


    /**
     * 保存用户列表
     *
     * @param usersList 用户列表
     */
    public synchronized void saveUsersList(List<MPUserEntity> usersList) {
        saveMPUsers(usersList);
    }

    /**
     * 保存组织机构信息列表
     *
     * @param entities
     */
    synchronized void saveAllOrgsList(List<MPOrgEntity> entities) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            try {
                db.beginTransaction();
                for (MPOrgEntity entitiesBean : entities) {
                    ContentValues values = new ContentValues();
                    values.put(OrgDao.COLUMN_NAME_ORG_ID, entitiesBean.getId());
                    values.put(OrgDao.COLUMN_NAME_ORG_PARENT_ID, entitiesBean.getParentId());
                    values.put(OrgDao.COLUMN_NAME_ORG_COMPANY_ID, entitiesBean.getCompanyId());
                    values.put(OrgDao.COLUMN_NAME_ORG_TENANT_ID, entitiesBean.getTenantId());
                    values.put(OrgDao.COLUMN_NAME_ORG_DEPTH, entitiesBean.getDepth());
                    values.put(OrgDao.COLUMN_NAME_ORG_MEMBER_COUNT, entitiesBean.getMemberCount());
                    if (!TextUtils.isEmpty(entitiesBean.getName()))
                        values.put(OrgDao.COLUMN_NAME_ORG_NAME, entitiesBean.getName());
                    if (!TextUtils.isEmpty(entitiesBean.getRank()))
                        values.put(OrgDao.COLUMN_NAME_ORG_RANK, entitiesBean.getRank());
                    db.replace(OrgDao.TABLE_NAME_ORG, null, values);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }


    /**
     * 保存单个用户
     *
     * @param user
     */
    public void saveUserInfo(MPUserEntity user) {
        if (user == null) {
            return;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(UserDao.COLUMN_EXT_NAME_IM_USER_ID, user.getImUserId());
            values.put(UserDao.COLUMN_EXT_NAME_USER_ID, user.getId());
            values.put(UserDao.COLUMN_EXT_NAME_ORG_ID, user.getOrgId());
            if (user.getRealName() != null) {
                values.put(UserDao.COLUMN_EXT_NAME_NICK, user.getRealName());
            }
            if (user.getAlias() != null) {
                values.put(UserDao.COLUMN_EXT_NAME_ALIAS, user.getAlias());
            }
            if (user.getUsername() != null) {
                values.put(UserDao.COLUMN_EXT_NAME_ACCOUNT, user.getUsername());
            }
            if (user.getAvatar() != null) {
                values.put(UserDao.COLUMN_EXT_NAME_AVATAR, user.getAvatar());
            }
            if (user.getGender() != null) {
                values.put(UserDao.COLUMN_EXT_NAME_GENDER, user.getGender());
            }
            if (user.getPhone() != null) {
                values.put(UserDao.COLUMN_EXT_NAME_PHONE, user.getPhone());
            }
            if (user.getTelephone() != null) {
                values.put(UserDao.COLUMN_EXT_NAME_TEL_PHONE, user.getTelephone());
            }
            if (user.getEmail() != null) {
                values.put(UserDao.COLUMN_EXT_NAME_EMAIL, user.getEmail());
            }
            if (user.getPinyin() != null) {
                values.put(UserDao.COLUMN_EXT_NAME_USER_PINYIN, user.getPinyin());
            }
            db.replace(UserDao.EXT_TABLE_NAME, null, values);
        }

    }

    /**
     * 根据userId 删除用户
     *
     * @param userId
     */
    synchronized public void delUserInfo(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.EXT_TABLE_NAME, UserDao.COLUMN_EXT_NAME_USER_ID + " =? ", new String[]{String.valueOf(userId)});
        }
    }

    /**
     * 获取部门ID为orgId下的员工信息
     *
     * @param orgId
     * @return
     */
    synchronized List<MPUserEntity> getUsersByOrgId(int orgId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<MPUserEntity> entitiesBeanList = new ArrayList<>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.EXT_TABLE_NAME + " where " + UserDao.COLUMN_EXT_NAME_ORG_ID + " =? order by " + UserDao.COLUMN_EXT_NAME_USER_PINYIN + " asc ", new String[]{String.valueOf(orgId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MPUserEntity user = getMPUserEntityFromCursor(cursor);
                    if (user != null) {
                        entitiesBeanList.add(user);
                    }

                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return entitiesBeanList;
    }

    public synchronized boolean saveMPUserList(List<MPUserEntity> mpUserEntities) {
        if (mpUserEntities == null || mpUserEntities.isEmpty()) {
            return false;
        }
        return saveMPUsers(mpUserEntities);
    }

    private boolean saveMPUsers(List<MPUserEntity> mpUserEntities) {
        boolean ret;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (!db.isOpen()) {
            return false;
        }
        try {
            db.beginTransaction();
            ContentValues cvs;
            for (MPUserEntity userEntity : mpUserEntities) {
                cvs = getContentValuesFromMPUserEntity(userEntity);
//                db.insertWithOnConflict(UserDao.EXT_TABLE_NAME, null,  cvs, SQLiteDatabase.CONFLICT_IGNORE);
                insertOrUpdateMPUsers(db, cvs);
            }
            db.setTransactionSuccessful();
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        } finally {
            db.endTransaction();
        }
        return ret;
    }


    private ContentValues getContentValuesFromMPUserEntity(MPUserEntity userEntity) {
        ContentValues cvs = new ContentValues();
        cvs.put(UserDao.COLUMN_EXT_NAME_USER_ID, userEntity.getId());
        cvs.put(UserDao.COLUMN_EXT_NAME_IM_USER_ID, userEntity.getImUserId());
        cvs.put(UserDao.COLUMN_EXT_NAME_NICK, userEntity.getRealName());
        cvs.put(UserDao.COLUMN_EXT_NAME_AVATAR, userEntity.getAvatar());
        cvs.put(UserDao.COLUMN_EXT_NAME_ORG_ID, userEntity.getOrgId());
        cvs.put(UserDao.COLUMN_EXT_NAME_ACCOUNT, userEntity.getUsername());
        cvs.put(UserDao.COLUMN_EXT_NAME_GENDER, userEntity.getGender());
        cvs.put(UserDao.COLUMN_EXT_NAME_PHONE, userEntity.getPhone());
        cvs.put(UserDao.COLUMN_EXT_NAME_EMAIL, userEntity.getEmail());
        cvs.put(UserDao.COLUMN_EXT_NAME_USER_PINYIN, userEntity.getPinyin());
        cvs.put(UserDao.COLUMN_EXT_NAME_ALIAS, userEntity.getAlias());

        return cvs;
    }


    private boolean insertOrUpdateMPUsers(SQLiteDatabase db, ContentValues contentValues) {
        long rows = db.insertWithOnConflict(UserDao.EXT_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (rows <= 0) {
            rows = db.updateWithOnConflict(UserDao.EXT_TABLE_NAME, contentValues, UserDao.COLUMN_EXT_NAME_IM_USER_ID + " =? ", new String[]{contentValues.getAsString(UserDao.COLUMN_EXT_NAME_IM_USER_ID)}, SQLiteDatabase.CONFLICT_IGNORE);
        }
        return rows > 0;
    }

    private boolean insertOrUpdate(SQLiteDatabase db, ContentValues contentValues, String tableName, String primaryKey) {
        long rows = db.insertWithOnConflict(tableName, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (rows <= 0) {
            rows = db.updateWithOnConflict(tableName, contentValues, primaryKey + "=? ", new String[]{contentValues.getAsString(primaryKey)}, SQLiteDatabase.CONFLICT_IGNORE);
        }
        return rows > 0;
    }


    private boolean deleteUsersByOrgId(int orgId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean ret = false;
        if (db.isOpen()) {
            ret = db.delete(UserDao.EXT_TABLE_NAME, UserDao.COLUMN_EXT_NAME_ORG_ID + "=? ", new String[]{String.valueOf(orgId)}) > 0;
        }
        return ret;

    }

    private MPUserEntity getMPUserEntityFromCursor(Cursor cursor) {
        String imUserId = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_IM_USER_ID));
        String nick = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_NICK));
        String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_AVATAR));
        String account = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_ACCOUNT));
        String gender = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_GENDER));
        String phone = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_PHONE));
        String telPhone = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_TEL_PHONE));
        String email = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_EMAIL));
        String pinyin = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_USER_PINYIN));
        int id = cursor.getInt(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_USER_ID));
        String alias = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_ALIAS));
        MPUserEntity user = new MPUserEntity();
        user.setImUserId(imUserId);
        user.setRealName(nick);
        user.setUsername(account);
        user.setAvatar(avatar);
        user.setGender(gender);
        user.setPhone(phone);
        user.setTelephone(telPhone);
        user.setEmail(email);
        user.setAlias(alias);
        user.setId(id);
        user.setPinyin(pinyin);
        return user;
    }


    /**
     * 获取部门ID为orgId下的员工扩展信息
     *
     * @param orgId
     * @return
     */
    synchronized List<EaseUser> getExtUsersByOrgId(int orgId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<EaseUser> users = new ArrayList<>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.EXT_TABLE_NAME + " where " + UserDao.COLUMN_EXT_NAME_ORG_ID + " =?", new String[]{String.valueOf(orgId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    EaseUser user = getEaseUserFromCursor(cursor);
                    if (user != null) {
                        users.add(user);
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return users;
    }

    List<MPUserEntity> searchUsersByKeyword(String searchText) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        searchText = searchText.replace("\'", "\'\'");
        List<MPUserEntity> entitiesBeanList = new ArrayList<>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.EXT_TABLE_NAME + " where " + UserDao.COLUMN_EXT_NAME_NICK + " like '%" + searchText + "%' or " + UserDao.COLUMN_EXT_NAME_USER_PINYIN + " like '%" + searchText + "%' or " + UserDao.COLUMN_EXT_NAME_PHONE + " like '%" + searchText + "%' or " + UserDao.COLUMN_EXT_NAME_ALIAS + " like '%" + searchText + "%' order by " + UserDao.COLUMN_EXT_NAME_USER_PINYIN, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MPUserEntity user = getMPUserEntityFromCursor(cursor);
                    if (user != null) {
                        entitiesBeanList.add(user);
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return entitiesBeanList;
    }


    private EaseUser getEaseUserFromCursor(Cursor cursor) {
        int userId = cursor.getInt(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_USER_ID));
        String imUserId = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_IM_USER_ID));
        String nick = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_NICK));
        String account = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_ACCOUNT));
        String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_AVATAR));
        String phone = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_PHONE));
        String telPhone = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_TEL_PHONE));
        String pinyin = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_USER_PINYIN));
        String userType = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_USER_TYPE));
        String email = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_EMAIL));
        String alias = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_ALIAS));

        EaseUser user = new EaseUser();
        user.setUsername(imUserId);
        user.setNickname(TextUtils.isEmpty(nick) ? account : nick);
        user.setAvatar(avatar);
        user.setEmail(email);
        user.setId(userId);
        user.setMobilePhone(phone);
        user.setPinyin(pinyin);
        user.setAlias(alias);
        user.setUserType(userType);
        user.setTelephone(telPhone);
        return user;
    }


    /**
     * 获取用户列表
     *
     * @return
     */
    synchronized ArrayList<EaseUser> getExtUserList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<EaseUser> easeUsers = new ArrayList<>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.EXT_TABLE_NAME + " order by " + UserDao.COLUMN_EXT_NAME_USER_PINYIN, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    EaseUser easeUser = getEaseUserFromCursor(cursor);
                    if (easeUser != null) {
                        easeUsers.add(easeUser);
                    }

                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return easeUsers;
    }


    /**
     * get getUserInfo
     *
     * @param hxId appId
     *             获取用户所有信息
     */
    synchronized public MPUserEntity getUserInfo(String hxId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        MPUserEntity user = null;
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.EXT_TABLE_NAME + " where " + UserDao.COLUMN_EXT_NAME_IM_USER_ID + " =? ", new String[]{hxId});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    user = getMPUserEntityFromCursor(cursor);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return user;
    }

    synchronized public MPUserEntity getUserInfoById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        MPUserEntity user = null;
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.EXT_TABLE_NAME + " where " + UserDao.COLUMN_EXT_NAME_USER_ID + " =? ", new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    user = getMPUserEntityFromCursor(cursor);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return user;
    }

    /**
     * get userExtInfo
     *
     * @param userId(imusername) 获取用户扩展信息
     */
    public synchronized EaseUser getUserExtInfo(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        EaseUser user = null;
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.EXT_TABLE_NAME + " where " + UserDao.COLUMN_EXT_NAME_USER_ID + " =? ", new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    user = getEaseUserFromCursor(cursor);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return user;
    }

    /**
     * 根据easemobName获取用户数据
     *
     * @param easemobName
     * @return
     */
    public EaseUser getUserByEasemobName(String easemobName) {
        if (TextUtils.isEmpty(easemobName)) {
            return null;
        }
        EaseUser emUser = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            try (Cursor cursor = db.rawQuery("select * from " + UserDao.EXT_TABLE_NAME + " where " + UserDao.COLUMN_EXT_NAME_IM_USER_ID + " =? ", new String[]{easemobName})) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        emUser = getEaseUserFromCursor(cursor);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return emUser;

    }

    synchronized List<EaseUser> getUserExtInfos(String imUserIds) {
        if (imUserIds == null || imUserIds.isEmpty()) {
            return new ArrayList<>();
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<EaseUser> users = new ArrayList<>();
        if (db.isOpen()) {
            try (Cursor cursor = db.rawQuery("select * from " + UserDao.EXT_TABLE_NAME + " where " + UserDao.COLUMN_EXT_NAME_IM_USER_ID + " in (" + imUserIds + ")", null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        EaseUser user = getEaseUserFromCursor(cursor);
                        if (user != null) {
                            users.add(user);
                        }
                    } while (cursor.moveToNext());
                }
            }
        }
        return users;
    }

    synchronized List<EaseUser> getUserExtInfosById(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<>();
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<EaseUser> users = new ArrayList<>();
        if (db.isOpen()) {
            for (Integer userId : userIds) {
                try (Cursor cursor = db.rawQuery("select * from " + UserDao.EXT_TABLE_NAME + " where " + UserDao.COLUMN_EXT_NAME_USER_ID + " = ?", new String[]{String.valueOf(userId)})) {
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            EaseUser user = getEaseUserFromCursor(cursor);
                            if (user != null) {
                                users.add(user);
                            }
                        } while (cursor.moveToNext());
                    }
                }
            }
        }
        return users;
    }

    synchronized List<String> getUserNameExtInfosById(String intUserIds) {
        if (intUserIds == null || intUserIds.isEmpty()) {
            return new ArrayList<>();
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> users = new ArrayList<>();
        if (db.isOpen()) {
            String sql = String.format("select * from " + UserDao.EXT_TABLE_NAME + " where " + UserDao.COLUMN_EXT_NAME_USER_ID + " in (%s)", intUserIds);
            MPLog.e(TAG, "getUserNameExtInfosById-sql:" + sql);
            try (Cursor cursor = db.rawQuery(sql, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        EaseUser user = getEaseUserFromCursor(cursor);
                        users.add(user.getUsername());
                    } while (cursor.moveToNext());
                }
            }
        }
        return users;
    }

    synchronized void deleteAllUsers() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            try {
                db.delete(UserDao.EXT_TABLE_NAME, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //======================== user db end ========================

    //======================== group db start ========================

    /**
     * delete groupInfo
     *
     * @param imGroupId
     */
    synchronized public void delGroupInfo(String imGroupId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(GroupDao.GROUP_TABLE_NAME, GroupDao.COLUMN_NAME_EASEMOB_GROUP_ID + " = ?", new String[]{imGroupId});
        }
    }

    synchronized public void delGroupInfoById(int groupId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(GroupDao.GROUP_TABLE_NAME, GroupDao.COLUMN_NAME_GROUP_ID + "=" + groupId, null);
        }
    }

    synchronized public void deleteAllGroup() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(GroupDao.GROUP_TABLE_NAME, null, null);
        }
    }


    /**
     * 保存群列表信息
     *
     * @return
     */
    synchronized void saveExtGroupList(List<GroupBean> groupsList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(GroupDao.GROUP_TABLE_NAME, null, null);
            try {
                db.beginTransaction();
                for (GroupBean item : groupsList) {
                    ContentValues values = new ContentValues();
                    values.put(GroupDao.COLUMN_NAME_EASEMOB_GROUP_ID, item.getImGroupId());
                    values.put(GroupDao.COLUMN_NAME_GROUP_ID, item.getGroupId());
                    values.put(GroupDao.COLUMN_NAME_CLUSTER, item.isCluster());
                    if (item.getNick() != null) {
                        values.put(GroupDao.COLUMN_NAME_GROUP_NAME, item.getNick());
                    }
                    if (item.getAvatar() != null) {
                        values.put(GroupDao.COLUMN_NAME_GROUP_AVATAR, item.getAvatar());
                    }
                    if (item.getType() != null) {
                        values.put(GroupDao.COLUMN_NAME_TYPE, item.getType());
                    }
                    if (item.getCreateTime() > 0) {
                        values.put(GroupDao.COLUMN_NAME_CREATE_TIME, item.getCreateTime());
                    }
                    db.replace(GroupDao.GROUP_TABLE_NAME, null, values);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }

    /**
     * 获取群列表
     *
     * @return
     */
    synchronized public List<MPGroupEntity> getMPGroupEntities() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<MPGroupEntity> groupBeans = new ArrayList<>();
        Cursor cursor = null;
        try {
            if (!db.isOpen()) {
                return groupBeans;
            }
            cursor = db.rawQuery("select * from " + GroupDao.GROUP_TABLE_NAME, null);
            while (cursor.moveToNext()) {
                MPGroupEntity groupEntity = getMPGroupEntityFromCursor(cursor);
                groupBeans.add(groupEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return groupBeans;
    }


    /**
     * 获取群列表
     *
     * @return
     */
    synchronized public ArrayList<GroupBean> getExtGroupList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<GroupBean> groupBeans = new ArrayList<>();
        Cursor cursor = null;
        try {
            if (!db.isOpen()) {
                return groupBeans;
            }
            cursor = db.rawQuery("select * from " + GroupDao.GROUP_TABLE_NAME, null);
            while (cursor.moveToNext()) {
                int groupId = cursor.getInt(cursor.getColumnIndex(GroupDao.COLUMN_NAME_GROUP_ID));
                String imGroupId = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_EASEMOB_GROUP_ID));
                String nick = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_GROUP_NAME));
                String avatar = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_GROUP_AVATAR));
                String type = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_TYPE));
                long createTime = cursor.getLong(cursor.getColumnIndex(GroupDao.COLUMN_NAME_CREATE_TIME));
                boolean cluster = cursor.getInt(cursor.getColumnIndex(GroupDao.COLUMN_NAME_CLUSTER)) == 1;
                GroupBean groupBean = new GroupBean(groupId, imGroupId, nick, avatar, createTime, type);
                groupBean.setCluster(cluster);
                groupBeans.add(groupBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return groupBeans;
    }

    /**
     * 获取群扩展信息
     *
     * @param groupId
     * @return
     */
    synchronized public GroupBean getGroupInfoById(int groupId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        GroupBean groupBean = null;
        if (!db.isOpen()) {
            return groupBean;
        }
        Cursor cursor = null;
        try {
            String sql = String.format("select * from " + GroupDao.GROUP_TABLE_NAME + " where " + GroupDao.COLUMN_NAME_GROUP_ID + "=%s", groupId);
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                String easemobGroupId = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_EASEMOB_GROUP_ID));
                String nick = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_GROUP_NAME));
                String avatar = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_GROUP_AVATAR));
                String type = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_TYPE));
                long createTime = cursor.getLong(cursor.getColumnIndex(GroupDao.COLUMN_NAME_CREATE_TIME));
                boolean cluster = cursor.getInt(cursor.getColumnIndex(GroupDao.COLUMN_NAME_CLUSTER)) == 1;
                groupBean = new GroupBean(groupId, easemobGroupId, nick, avatar, createTime, type);
                groupBean.setCluster(cluster);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return groupBean;
    }

    /**
     * 获取群扩展信息
     *
     * @param easemob_group_id
     * @return
     */
    synchronized public GroupBean getGroupInfo(String easemob_group_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        GroupBean groupBean = null;
        if (!db.isOpen()) {
            return groupBean;
        }
        Cursor cursor = null;
        try {
            String sql = String.format("select * from " + GroupDao.GROUP_TABLE_NAME + " where " + GroupDao.COLUMN_NAME_EASEMOB_GROUP_ID + "=%s", easemob_group_id);
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                int groupId = cursor.getInt(cursor.getColumnIndex(GroupDao.COLUMN_NAME_GROUP_ID));
                String nick = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_GROUP_NAME));
                String avatar = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_GROUP_AVATAR));
                String type = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_TYPE));
                long createTime = cursor.getLong(cursor.getColumnIndex(GroupDao.COLUMN_NAME_CREATE_TIME));
                boolean cluster = cursor.getInt(cursor.getColumnIndex(GroupDao.COLUMN_NAME_CLUSTER)) == 1;
                groupBean = new GroupBean(groupId, easemob_group_id, nick, avatar, createTime, type);
                groupBean.setCluster(cluster);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return groupBean;
    }

    private MPGroupEntity getMPGroupEntityFromCursor(Cursor cursor) {
        int groupId = cursor.getInt(cursor.getColumnIndex(GroupDao.COLUMN_NAME_GROUP_ID));
        String groupName = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_GROUP_NAME));
        String avatar = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_GROUP_AVATAR));
        String type = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_TYPE));
        String imGroupId = cursor.getString(cursor.getColumnIndex(GroupDao.COLUMN_NAME_EASEMOB_GROUP_ID));
        long createTime = cursor.getLong(cursor.getColumnIndex(GroupDao.COLUMN_NAME_CREATE_TIME));
        boolean cluster = cursor.getInt(cursor.getColumnIndex(GroupDao.COLUMN_NAME_CLUSTER)) == 1;

        MPGroupEntity groupEntity = new MPGroupEntity();
        groupEntity.setId(groupId);
        groupEntity.setCluster(cluster);
        groupEntity.setCreateTime(createTime);
        groupEntity.setType(type);
        groupEntity.setImChatGroupId(imGroupId);
        groupEntity.setName(groupName);
        groupEntity.setAvatar(avatar);
        return groupEntity;
    }

    synchronized public MPGroupEntity getMPGroupEntity(String imGroupId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        MPGroupEntity groupBean = null;
        if (!db.isOpen()) {
            return groupBean;
        }
        Cursor cursor = null;
        try {
            String sql = String.format("select * from " + GroupDao.GROUP_TABLE_NAME + " where " + GroupDao.COLUMN_NAME_EASEMOB_GROUP_ID + "=%s", imGroupId);
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                groupBean = getMPGroupEntityFromCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return groupBean;
    }



    public boolean createOrUpdateGroupInfo(GroupBean groupBean) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues cvs = new ContentValues();
            if (groupBean.getNick() != null)
                cvs.put(GroupDao.COLUMN_NAME_GROUP_NAME, groupBean.getNick());
            if (groupBean.getAvatar() != null)
                cvs.put(GroupDao.COLUMN_NAME_GROUP_AVATAR, groupBean.getAvatar());
            if (groupBean.getImGroupId() != null) {
                cvs.put(GroupDao.COLUMN_NAME_EASEMOB_GROUP_ID, groupBean.getImGroupId());
            }
            cvs.put(GroupDao.COLUMN_NAME_GROUP_ID, groupBean.getGroupId());
            cvs.put(GroupDao.COLUMN_NAME_CLUSTER, groupBean.isCluster() ? 1 : 0);
            if (groupBean.getCreateTime() > 0) {
                cvs.put(GroupDao.COLUMN_NAME_CREATE_TIME, groupBean.getCreateTime());
            }
            if (groupBean.getType() != null) {
                cvs.put(GroupDao.COLUMN_NAME_TYPE, groupBean.getType());
            }
            long rows = db.insertWithOnConflict(GroupDao.GROUP_TABLE_NAME, null, cvs, SQLiteDatabase.CONFLICT_IGNORE);
            int rowsId = -1;
            if (rows <= 0) {
                cvs.remove(GroupDao.COLUMN_NAME_CLUSTER);
                rowsId = db.update(GroupDao.GROUP_TABLE_NAME, cvs, GroupDao.COLUMN_NAME_EASEMOB_GROUP_ID + "=?", new String[]{groupBean.getImGroupId()});
            }
            if (rows <= 0 && rowsId <= 0) {
                return false;
            }
        }
        return true;
    }

    public boolean createOrUpdateGroupInfo(MPGroupEntity groupEntity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues cvs = getGroupInfoCvs(groupEntity);
            long rows = db.insertWithOnConflict(GroupDao.GROUP_TABLE_NAME, null, cvs, SQLiteDatabase.CONFLICT_IGNORE);
            int rowsId = -1;
            if (rows <= 0) {
                cvs.remove(GroupDao.COLUMN_NAME_CLUSTER);
                rowsId = db.update(GroupDao.GROUP_TABLE_NAME, cvs, GroupDao.COLUMN_NAME_EASEMOB_GROUP_ID + "=?", new String[]{groupEntity.getImChatGroupId()});
            }
            if (rows <= 0 && rowsId <= 0) {
                return false;
            }
        }
        return true;
    }

    private ContentValues getGroupInfoCvs(MPGroupEntity groupEntity) {
        ContentValues cvs = new ContentValues();
        if (groupEntity.getName() != null)
            cvs.put(GroupDao.COLUMN_NAME_GROUP_NAME, groupEntity.getName());
        if (groupEntity.getAvatar() != null)
            cvs.put(GroupDao.COLUMN_NAME_GROUP_AVATAR, groupEntity.getAvatar());
        if (groupEntity.getImChatGroupId() != null) {
            cvs.put(GroupDao.COLUMN_NAME_EASEMOB_GROUP_ID, groupEntity.getImChatGroupId());
        }
        cvs.put(GroupDao.COLUMN_NAME_GROUP_ID, groupEntity.getId());
        cvs.put(GroupDao.COLUMN_NAME_CLUSTER, groupEntity.isCluster() ? 1 : 0);
        if (groupEntity.getCreateTime() > 0) {
            cvs.put(GroupDao.COLUMN_NAME_CREATE_TIME, groupEntity.getCreateTime());
        }
        if (groupEntity.getType() != null) {
            cvs.put(GroupDao.COLUMN_NAME_TYPE, groupEntity.getType());
        }
        return cvs;
    }

    //群扩展信息
    synchronized public void saveGroupInfo(GroupBean groupBean) {
        boolean isSuccess = createOrUpdateGroupInfo(groupBean);
        if (!isSuccess) {
            MPLog.e(TAG, "save group failed: groupId:" + groupBean.getImGroupId());
        }
    }

    synchronized public void saveGroupInfo(MPGroupEntity groupEntity) {
        boolean isSuccess = createOrUpdateGroupInfo(groupEntity);
        if (!isSuccess) {
            MPLog.e(TAG, "save group failed: groupId:" + groupEntity.getImChatGroupId());
        }
    }

    /**
     * 保存群列表信息
     *
     * @return
     */
    synchronized void saveMPGroupList(List<MPGroupEntity> groupsList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(GroupDao.GROUP_TABLE_NAME, null, null);
            try {
                db.beginTransaction();
                for (MPGroupEntity item : groupsList) {
                    ContentValues cvs =  getGroupInfoCvs(item);
                    db.replace(GroupDao.GROUP_TABLE_NAME, null, cvs);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
                MPLog.e(TAG, "save ext group list error :" + MPLog.getStackTraceString(e));
            } finally {
                db.endTransaction();
            }
        }
    }

    public void setDisabledGroups(List<String> groups) {
        setList(UserDao.COLUMN_NAME_DISABLED_GROUPS, groups);
    }

    public List<String> getDisabledGroups() {
        return getList(UserDao.COLUMN_NAME_DISABLED_GROUPS);
    }

    public void setDisabledIds(List<String> ids) {
        setList(UserDao.COLUMN_NAME_DISABLED_IDS, ids);
    }

    public List<String> getDisabledIds() {
        return getList(UserDao.COLUMN_NAME_DISABLED_IDS);
    }
    //======================== group db end ========================


    synchronized private void setList(String column, List<String> strList) {
        StringBuilder strBuilder = new StringBuilder();

        for (String hxid : strList) {
            strBuilder.append(hxid).append("$");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(column, strBuilder.toString());

            db.update(UserDao.PREF_TABLE_NAME, values, null, null);
        }
    }

    synchronized private List<String> getList(String column) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + column + " from " + UserDao.PREF_TABLE_NAME, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        String strVal = cursor.getString(0);
        if (strVal == null || strVal.equals("")) {
            return null;
        }

        cursor.close();

        String[] array = strVal.split("$");

        if (array.length > 0) {
            List<String> list = new ArrayList<String>();
            Collections.addAll(list, array);
            return list;
        }

        return null;
    }

    /**
     * save a notify message
     *
     * @param message
     * @return return cursor of the message
     */
    public synchronized long saveMessage(InviteMessage message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = -1l;
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(InviteMessageDao.COLUMN_NAME_FRIEND_ID, message.getFriendId());
            values.put(InviteMessageDao.COLUMN_NAME_USER_ID, message.getUserId());
            values.put(InviteMessageDao.COLUMN_NAME_FROM, message.getFrom());
            values.put(InviteMessageDao.COLUMN_NAME_GROUP_ID, message.getGroupId());
            values.put(InviteMessageDao.COLUMN_NAME_GROUP_Name, message.getGroupName());
            values.put(InviteMessageDao.COLUMN_NAME_REASON, message.getReason());
            values.put(InviteMessageDao.COLUMN_NAME_TIME, message.getTime());
            values.put(InviteMessageDao.COLUMN_NAME_STATUS, message.getStatus().ordinal());
            values.put(InviteMessageDao.COLUMN_NAME_GROUPINVITER, message.getGroupInviter());
            values.put(InviteMessageDao.COLUMN_NAME_GROUPINVITER, message.getGroupInviter());
            values.put(InviteMessageDao.COLUMN_NAME_CHAT_GROUP_ID, message.getChatGroupId());
            values.put(InviteMessageDao.COLUMN_NAME_CLUSTER, message.getCluster() ? 1 : 0);
            db.replace(InviteMessageDao.TABLE_NAME, null, values);

            Cursor cursor = db.rawQuery("select last_insert_rowid() from " + InviteMessageDao.TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                id = cursor.getLong(0);
            }

            cursor.close();
        }
        return id;
    }

    /**
     * update notify message
     *
     * @param userId
     * @param values
     */
    synchronized public void updateMessage(int userId, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.update(InviteMessageDao.TABLE_NAME, values, InviteMessageDao.COLUMN_NAME_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        }
    }

    /**
     * get notify messges
     *
     * @return
     */
    synchronized public List<InviteMessage> getMessagesList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<InviteMessage> msgs = new ArrayList<>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select i.*, e.nick from " + InviteMessageDao.TABLE_NAME + " i, " + UserDao.EXT_TABLE_NAME + " e where i." + InviteMessageDao.COLUMN_NAME_USER_ID + "==" + "e." + UserDao.COLUMN_EXT_NAME_USER_ID, null);
            while (cursor.moveToNext()) {
                InviteMessage msg = new InviteMessage();
                String from = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_FROM));
                String groupid = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUP_ID));
                String groupname = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUP_Name));
                String reason = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_REASON));
                long time = cursor.getLong(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_TIME));
                int status = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_STATUS));
                int friendId = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_FRIEND_ID));
                int userId = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_USER_ID));
                int chatGroupId = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_CHAT_GROUP_ID));
                String groupInviter = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUPINVITER));
                boolean cluster = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_CLUSTER)) == 1;
                int nickIndex = cursor.getColumnIndex(UserDao.COLUMN_EXT_NAME_NICK);
                String nick = "";
                if (nickIndex != -1) {
                    nick = cursor.getString(nickIndex);
                }
                msg.setFrom(from);
                msg.setGroupId(groupid);
                msg.setGroupName(groupname);
                msg.setReason(reason);
                msg.setTime(time);
                msg.setFriendId(friendId);
                msg.setUserId(userId);
                msg.setGroupInviter(groupInviter);
                msg.setStatus(InviteMessageStatus.values()[status]);
                msg.setRealName(nick);
                msg.setChatGroupId(chatGroupId);
                msg.setCluster(cluster);
                msgs.add(msg);
            }
            cursor.close();
        }
        return msgs;
    }

    /**
     * get notify messge
     *
     * @return
     */
    synchronized public InviteMessage getInviteMessage(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        InviteMessage msg = new InviteMessage();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + InviteMessageDao.TABLE_NAME + " where " + InviteMessageDao.COLUMN_NAME_USER_ID + " =? " /*+ " desc"*/, new String[]{String.valueOf(userId)});
            while (cursor.moveToNext()) {
                String from = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_FROM));
                String groupid = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUP_ID));
                String groupname = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUP_Name));
                String reason = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_REASON));
                long time = cursor.getLong(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_TIME));
                int status = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_STATUS));
                int friendId = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_FRIEND_ID));
                String groupInviter = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUPINVITER));
                boolean cluster = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_CLUSTER)) == 1;

                msg.setFrom(from);
                msg.setGroupId(groupid);
                msg.setGroupName(groupname);
                msg.setReason(reason);
                msg.setTime(time);
                msg.setFriendId(friendId);
                msg.setUserId(userId);
                msg.setCluster(cluster);
                msg.setGroupInviter(groupInviter);
                msg.setStatus(InviteMessageStatus.values()[status]);
            }
            cursor.close();
        }
        return msg;
    }

    /**
     * delete invitation message
     *
     * @param from
     */
    synchronized public void deleteMessage(String from) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(InviteMessageDao.TABLE_NAME, InviteMessageDao.COLUMN_NAME_FROM + " = ?", new String[]{from});
        }
    }

    /**
     * delete invitation message
     *
     * @param groupId
     */
    synchronized public void deleteGroupMessage(String groupId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(InviteMessageDao.TABLE_NAME, InviteMessageDao.COLUMN_NAME_GROUP_ID + " = ?", new String[]{groupId});
        }
    }

    /**
     * delete invitation message
     *
     * @param groupId
     */
    synchronized public void deleteGroupMessage(String groupId, String from) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(InviteMessageDao.TABLE_NAME, InviteMessageDao.COLUMN_NAME_GROUP_ID + " = ? AND " + InviteMessageDao.COLUMN_NAME_FROM + " = ? ",
                    new String[]{groupId, from});
        }
    }

    synchronized int getUnreadNotifyCount() {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select " + InviteMessageDao.COLUMN_NAME_UNREAD_MSG_COUNT + " from " + InviteMessageDao.TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    synchronized void setUnreadNotifyCount(int count) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(InviteMessageDao.COLUMN_NAME_UNREAD_MSG_COUNT, count);

            db.update(InviteMessageDao.TABLE_NAME, values, null, null);
        }
    }

    synchronized public void closeDB() {
        if (dbHelper != null) {
            dbHelper.closeDB();
        }
//        dbMgr = null;
    }

    /**
     * 设置会话置顶时间
     *
     * @param conversationId 会话ID
     * @param stickyTime     置顶时间
     * @param type           会话类型
     */
    synchronized void saveStickyTime(String conversationId, String stickyTime, EMConversation.EMConversationType type) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(ConversationDao.COLUMN_CONVERSATION_NAME_ID, conversationId);
            values.put(ConversationDao.COLUMN_NAME_STICKY_TIME, stickyTime);
            values.put(ConversationDao.COLUMN_NAME_CONVERSATION_TYPE, getIntConversationType(type));
            db.replace(ConversationDao.CONVERSATION_TABLE_NAME, null, values);
        }
    }

    /**
     * 获取会话置顶时间
     *
     * @param conversationId 会话ID
     * @param type
     * @return String 置顶时间
     */
    synchronized String getStickyTime(String conversationId, EMConversation.EMConversationType type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String stickyTime = "";
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select " + ConversationDao.COLUMN_NAME_STICKY_TIME + " from " + ConversationDao.CONVERSATION_TABLE_NAME + " where " + ConversationDao.COLUMN_CONVERSATION_NAME_ID + " = ? AND " + ConversationDao.COLUMN_NAME_CONVERSATION_TYPE + " = ? limit 1", new String[]{conversationId, String.valueOf(getIntConversationType(type))});
            if (cursor.moveToFirst()) {
                stickyTime = cursor.getString(0);
            }
            cursor.close();
        }
        return stickyTime;
    }
    //根据会话类型返回对应int值

    public int getIntConversationType(EMConversation.EMConversationType type) {
        if (type == EMConversation.EMConversationType.Chat) {
            return 1;
        } else if (type == EMConversation.EMConversationType.GroupChat) {
            return 2;
        } else if (type == EMConversation.EMConversationType.ChatRoom) {
            return 3;
        } else if (type == EMConversation.EMConversationType.DiscussionGroup) {
            return 4;
        } else if (type == EMConversation.EMConversationType.HelpDesk) {
            return 5;
        }
        return 1;

    }

    //================= Group Mutes start=================

    public void muteGroupUsernames(String groupId, List<String> imUsernames, long muteTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            try {
                db.beginTransaction();
                for (String username : imUsernames) {
                    ContentValues cvs = new ContentValues();
                    cvs.put(GroupMuteDao.COLUMN_NAME_GROUP_ID, groupId);
                    cvs.put(GroupMuteDao.COLUMN_NAME_MUTE_USERNAME, username);
                    cvs.put(GroupMuteDao.COLUMN_NAME_MUTE_TIME, muteTime);
                    cvs.put(GroupMuteDao.COLUMN_NAME_LAST_UPDATE_TIME, System.currentTimeMillis());
//                    db.insertWithOnConflict(GroupMuteDao.GROUP_MUTES_TABLE_NAME, null, cvs, SQLiteDatabase.CONFLICT_IGNORE);
                    db.replace(GroupMuteDao.GROUP_MUTES_TABLE_NAME, null, cvs);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }

    public void unMuteGroupUsernames(String groupId, List<String> imUsernames) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            try {
                db.beginTransaction();
                for (String imUsername : imUsernames) {
                    db.delete(GroupMuteDao.GROUP_MUTES_TABLE_NAME, GroupMuteDao.COLUMN_NAME_GROUP_ID + " = ? AND " + GroupMuteDao.COLUMN_NAME_MUTE_USERNAME + " = ?", new String[]{groupId, imUsername});
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }

    public boolean muteGroupUsername(String groupId, String username, long muteTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rows = -1;
        if (db.isOpen()) {
            try {
                ContentValues cvs = new ContentValues();
                cvs.put(GroupMuteDao.COLUMN_NAME_GROUP_ID, groupId);
                cvs.put(GroupMuteDao.COLUMN_NAME_MUTE_USERNAME, username);
                cvs.put(GroupMuteDao.COLUMN_NAME_MUTE_TIME, muteTime);
                cvs.put(GroupMuteDao.COLUMN_NAME_LAST_UPDATE_TIME, System.currentTimeMillis());
//                rows = db.insertWithOnConflict(GroupMuteDao.GROUP_MUTES_TABLE_NAME, null, cvs, SQLiteDatabase.CONFLICT_IGNORE);
                rows = db.replace(GroupMuteDao.GROUP_MUTES_TABLE_NAME, null, cvs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rows >= 0;
    }

    public boolean unMuteGroupUsername(String groupId, String imUsername) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = 0;
        if (db.isOpen()) {
            try {
                rows = db.delete(GroupMuteDao.GROUP_MUTES_TABLE_NAME, GroupMuteDao.COLUMN_NAME_GROUP_ID + " = ? AND " + GroupMuteDao.COLUMN_NAME_MUTE_USERNAME + " = ?", new String[]{groupId, imUsername});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rows > 0;
    }


    public void updateGroupMutes(String groupId, Map<String, Long> imUsernames) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        deleteGroupMutes(groupId);
        if (db.isOpen()) {
            try {
                db.beginTransaction();
                for (Map.Entry<String, Long> item : imUsernames.entrySet()) {
                    ContentValues cvs = new ContentValues();
                    cvs.put(GroupMuteDao.COLUMN_NAME_GROUP_ID, groupId);
                    cvs.put(GroupMuteDao.COLUMN_NAME_MUTE_USERNAME, item.getKey());
                    cvs.put(GroupMuteDao.COLUMN_NAME_MUTE_TIME, item.getValue());
                    cvs.put(GroupMuteDao.COLUMN_NAME_LAST_UPDATE_TIME, System.currentTimeMillis());
                    db.insertWithOnConflict(GroupMuteDao.GROUP_MUTES_TABLE_NAME, null, cvs, SQLiteDatabase.CONFLICT_IGNORE);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }

    public void deleteGroupMutes(String groupId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(GroupMuteDao.GROUP_MUTES_TABLE_NAME, GroupMuteDao.COLUMN_NAME_GROUP_ID + "=? ", new String[]{groupId});
        }
    }

    public boolean isMuted(String groupId, String imUsername) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean currentUserIsMuted = false;
        if (db.isOpen()) {
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("select * from " + GroupMuteDao.GROUP_MUTES_TABLE_NAME + " where " + GroupMuteDao.COLUMN_NAME_GROUP_ID + " = ? AND " + GroupMuteDao.COLUMN_NAME_MUTE_USERNAME + " = ?", new String[]{groupId, imUsername});
                if (cursor.moveToFirst()) {
                    long muteTime = cursor.getLong(cursor.getColumnIndex(GroupMuteDao.COLUMN_NAME_MUTE_TIME));
                    long updateTime = cursor.getLong(cursor.getColumnIndex(GroupMuteDao.COLUMN_NAME_LAST_UPDATE_TIME));
                    if (System.currentTimeMillis() - updateTime < muteTime) {
                        currentUserIsMuted = true;
                    } else {
                        unMuteGroupUsername(groupId, imUsername);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return currentUserIsMuted;
    }

    //================= Group Mutes =================

    //================= Draft start =================
    public boolean saveDraft(DraftEntity entity) {
        if (entity == null) {
            return false;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues cvs = new ContentValues();
            cvs.put(DraftDao.COLUMN_NAME_ID, entity.getId());
            cvs.put(DraftDao.COLUMN_NAME_CONTENT, entity.getContent());
            cvs.put(DraftDao.COLUMN_NAME_EXTRA, entity.getExtra());
            cvs.put(DraftDao.COLUMN_NAME_LAST_UPDATE_TIME, System.currentTimeMillis());
            return insertOrUpdate(db, cvs, DraftDao.TABLE_NAME, DraftDao.COLUMN_NAME_ID);
        }

        return false;
    }

    public void removeDraft(String id) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(DraftDao.TABLE_NAME, DraftDao.COLUMN_NAME_ID + "=? ", new String[]{id});
        }
    }

    public DraftEntity getDraft(String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("select * from " + DraftDao.TABLE_NAME + " where " + DraftDao.COLUMN_NAME_ID + "=? ", new String[]{id});
                if (cursor.moveToFirst()) {
                    DraftEntity draftEntity = new DraftEntity();
                    draftEntity.setId(cursor.getString(cursor.getColumnIndex(DraftDao.COLUMN_NAME_ID)));
                    draftEntity.setContent(cursor.getString(cursor.getColumnIndex(DraftDao.COLUMN_NAME_CONTENT)));
                    draftEntity.setExtra(cursor.getString(cursor.getColumnIndex(DraftDao.COLUMN_NAME_EXTRA)));
                    return draftEntity;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

        }
        return null;
    }


    public List<DraftEntity> getDraftEntities() {
        List<DraftEntity> draftEntities = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("select * from " + DraftDao.TABLE_NAME, null);
                while (cursor.moveToNext()) {
                    DraftEntity draftEntity = new DraftEntity();
                    draftEntity.setId(cursor.getString(cursor.getColumnIndex(DraftDao.COLUMN_NAME_ID)));
                    draftEntity.setContent(cursor.getString(cursor.getColumnIndex(DraftDao.COLUMN_NAME_CONTENT)));
                    draftEntity.setExtra(cursor.getString(cursor.getColumnIndex(DraftDao.COLUMN_NAME_EXTRA)));
                    draftEntities.add(draftEntity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return draftEntities;
    }


    //================= Draft end =================

    //================= Disturb start =================
    public void saveNoDisturb(NoDisturbEntity entity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (!db.isOpen()) {
            MPLog.e(TAG, "saveNoDisturb db is closed. id:" + entity.getId());
            return;
        }
        ContentValues cvs = new ContentValues();
        cvs.put(NoDisturbDao.COLUMN_NAME_ID, entity.getId());
        cvs.put(NoDisturbDao.COLUMN_NAME_IS_GROUP, entity.isGroup() ? 1 : 0);
        cvs.put(NoDisturbDao.COLUMN_NAME_NAME, entity.getName());
        cvs.put(NoDisturbDao.COLUMN_NAME_LAST_UPDATE_TIME, entity.getLastUpdateTime());
        insertOrUpdate(db, cvs, NoDisturbDao.TABLE_NAME, NoDisturbDao.COLUMN_NAME_ID);
    }

    public void removeNoDisturb(String noDisturbId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (!db.isOpen()) {
            MPLog.e(TAG, "remove noDisturb db is closed. id:" + noDisturbId);
            return;
        }
        int rows = db.delete(NoDisturbDao.TABLE_NAME, " id = ?", new String[]{noDisturbId});
        if (rows <= 0) {
            MPLog.e(TAG, "remove removeNoDisturb failed. id:" + noDisturbId);
        }
    }

    public List<NoDisturbEntity> getAllNoDisturbs() {
        List<NoDisturbEntity> nodisturbs = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("select * from " + NoDisturbDao.TABLE_NAME, null);
                while (cursor.moveToNext()) {
                    NoDisturbEntity noDisturbEntity = new NoDisturbEntity();
                    noDisturbEntity.setId(cursor.getString(cursor.getColumnIndex(NoDisturbDao.COLUMN_NAME_ID)));
                    noDisturbEntity.setGroup(cursor.getInt(cursor.getColumnIndex(NoDisturbDao.COLUMN_NAME_IS_GROUP)) == 1);
                    noDisturbEntity.setLastUpdateTime(cursor.getLong(cursor.getColumnIndex(NoDisturbDao.COLUMN_NAME_LAST_UPDATE_TIME)));
                    noDisturbEntity.setName(cursor.getString(cursor.getColumnIndex(NoDisturbDao.COLUMN_NAME_NAME)));
                    nodisturbs.add(noDisturbEntity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return nodisturbs;
    }
    //================= Disturb end =================


    //================= TenantOption start =================
    public List<TenantOption> getTenantOptions() {
        List<TenantOption> options = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("select * from " + TenantOptionsDao.TABLE_NAME, null);
                TenantOption option = null;
                while (cursor.moveToNext()) {
                    option = new TenantOption();
                    option.setId(cursor.getInt(cursor.getColumnIndex(TenantOptionsDao.COLUMN_NAME_ID)));
                    option.setCreateTime(cursor.getLong(cursor.getColumnIndex(TenantOptionsDao.COLUMN_NAME_CREATE_TIME)));
                    option.setLastUpdateTime(cursor.getLong(cursor.getColumnIndex(TenantOptionsDao.COLUMN_NAME_LAST_UPDATE_TIME)));
                    option.setCreateUserId(cursor.getInt(cursor.getColumnIndex(TenantOptionsDao.COLUMN_NAME_CREATE_USER_ID)));
                    option.setOptionName(cursor.getString(cursor.getColumnIndex(TenantOptionsDao.COLUMN_NAME_OPTION_NAME)));
                    option.setOptionValue(cursor.getString(cursor.getColumnIndex(TenantOptionsDao.COLUMN_NAME_OPTION_VALUE)));
                    option.setTenantId(cursor.getInt(cursor.getColumnIndex(TenantOptionsDao.COLUMN_NAME_TENANT_ID)));
                    options.add(option);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
        return options;
    }

    public void saveTenantOptions(List<TenantOption> options) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }

        try {
            db.beginTransaction();
            db.delete(TenantOptionsDao.TABLE_NAME, null, null);
            for (TenantOption option : options) {
                ContentValues cvs = new ContentValues();
                cvs.put(TenantOptionsDao.COLUMN_NAME_ID, option.getId());
                cvs.put(TenantOptionsDao.COLUMN_NAME_CREATE_TIME, option.getCreateTime());
                cvs.put(TenantOptionsDao.COLUMN_NAME_CREATE_USER_ID, option.getCreateUserId());
                cvs.put(TenantOptionsDao.COLUMN_NAME_LAST_UPDATE_TIME, option.getLastUpdateTime());
                cvs.put(TenantOptionsDao.COLUMN_NAME_OPTION_NAME, option.getOptionName());
                cvs.put(TenantOptionsDao.COLUMN_NAME_OPTION_VALUE, option.getOptionValue());
                cvs.put(TenantOptionsDao.COLUMN_NAME_TENANT_ID, option.getTenantId());
                insertOrUpdate(db, cvs, TenantOptionsDao.TABLE_NAME, TenantOptionsDao.COLUMN_NAME_ID);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public TenantOption getTenantOptionForKey(String optionKey) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        TenantOption option = null;
        if (db.isOpen()) {
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("select * from " + TenantOptionsDao.TABLE_NAME + " where " + TenantOptionsDao.COLUMN_NAME_OPTION_NAME + "=?", new String[]{optionKey});
                if (cursor.moveToNext()) {
                    option = new TenantOption();
                    option.setId(cursor.getInt(cursor.getColumnIndex(TenantOptionsDao.COLUMN_NAME_ID)));
                    option.setCreateTime(cursor.getLong(cursor.getColumnIndex(TenantOptionsDao.COLUMN_NAME_CREATE_TIME)));
                    option.setLastUpdateTime(cursor.getLong(cursor.getColumnIndex(TenantOptionsDao.COLUMN_NAME_LAST_UPDATE_TIME)));
                    option.setCreateUserId(cursor.getInt(cursor.getColumnIndex(TenantOptionsDao.COLUMN_NAME_CREATE_USER_ID)));
                    option.setOptionName(cursor.getString(cursor.getColumnIndex(TenantOptionsDao.COLUMN_NAME_OPTION_NAME)));
                    option.setOptionValue(cursor.getString(cursor.getColumnIndex(TenantOptionsDao.COLUMN_NAME_OPTION_VALUE)));
                    option.setTenantId(cursor.getInt(cursor.getColumnIndex(TenantOptionsDao.COLUMN_NAME_TENANT_ID)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
        return option;
    }

    public void deleteAllTenantOption() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        db.delete(TenantOptionsDao.TABLE_NAME, null, null);
    }

    //================= TenantOption end =================

    //================= Session start =================
    public void saveSessions(List<MPSessionEntity> sessions) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            for (MPSessionEntity entity : sessions) {
                ContentValues cvs = getContentValuesBySession(entity);
                insertOrUpdate(db, cvs, MPSessionDao.TABLE_NAME, MPSessionDao.COLUMN_NAME_ID);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    public List<MPSessionEntity> getSessions() {
        List<MPSessionEntity> sessionEntities = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (!db.isOpen()) {
            return sessionEntities;
        }
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from " + MPSessionDao.TABLE_NAME, null);
            while(cursor.moveToNext()) {
                MPSessionEntity sessionEntity = getSessionEntityFromCursor(cursor);
                if (sessionEntity != null) {
                    sessionEntities.add(sessionEntity);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return sessionEntities;
    }

    private MPSessionEntity getSessionEntityFromCursor(Cursor cursor) {
        MPSessionEntity sessionEntity = new MPSessionEntity();
        sessionEntity.setId(cursor.getInt(cursor.getColumnIndex(MPSessionDao.COLUMN_NAME_ID)));
        sessionEntity.setAvatar(cursor.getString(cursor.getColumnIndex(MPSessionDao.COLUMN_NAME_AVATAR)));
        sessionEntity.setChatType(cursor.getString(cursor.getColumnIndex(MPSessionDao.COLUMN_NAME_CHAT_TYPE)));
        sessionEntity.setCreateTime(cursor.getLong(cursor.getColumnIndex(MPSessionDao.COLUMN_NAME_CREATE_TIME)));
        sessionEntity.setLastUpdateTime(cursor.getLong(cursor.getColumnIndex(MPSessionDao.COLUMN_NAME_LAST_UPDATE_TIME)));
        sessionEntity.setDisturb(cursor.getInt(cursor.getColumnIndex(MPSessionDao.COLUMN_NAME_IS_DISTURB)) == 1);
        sessionEntity.setImId(cursor.getString(cursor.getColumnIndex(MPSessionDao.COLUMN_NAME_IM_ID)));
        sessionEntity.setName(cursor.getString(cursor.getColumnIndex(MPSessionDao.COLUMN_NAME_NAME)));
        sessionEntity.setToId(cursor.getInt(cursor.getColumnIndex(MPSessionDao.COLUMN_NAME_TO_ID)));
        sessionEntity.setTop(cursor.getInt(cursor.getColumnIndex(MPSessionDao.COLUMN_NAME_IS_TOP)) == 1);
        return sessionEntity;
    }


    public boolean updateSession(MPSessionEntity sessionEntity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (!db.isOpen()) {
            return false;
        }
        ContentValues contentValues = getContentValuesBySession(sessionEntity);
        if (contentValues == null) {
            return false;
        }
        int rows = db.update(MPSessionDao.TABLE_NAME, contentValues, MPSessionDao.COLUMN_NAME_ID + "=?", new String[]{String.valueOf(sessionEntity.getId())});
        return rows > 0;
    }

    private ContentValues getContentValuesBySession(MPSessionEntity sessionEntity) {
        if (sessionEntity == null) {
            return null;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MPSessionDao.COLUMN_NAME_ID, sessionEntity.getId());
        contentValues.put(MPSessionDao.COLUMN_NAME_AVATAR, sessionEntity.getAvatar());
        contentValues.put(MPSessionDao.COLUMN_NAME_CHAT_TYPE, sessionEntity.getChatType());
        contentValues.put(MPSessionDao.COLUMN_NAME_CREATE_TIME, sessionEntity.getCreateTime());
        contentValues.put(MPSessionDao.COLUMN_NAME_IM_ID, sessionEntity.getImId());
        contentValues.put(MPSessionDao.COLUMN_NAME_IS_DISTURB, sessionEntity.isDisturb() ? 1 : 0);
        contentValues.put(MPSessionDao.COLUMN_NAME_IS_TOP, sessionEntity.isTop() ? 1 : 0);
        contentValues.put(MPSessionDao.COLUMN_NAME_LAST_UPDATE_TIME, sessionEntity.getLastUpdateTime());
        contentValues.put(MPSessionDao.COLUMN_NAME_NAME, sessionEntity.getName());
        contentValues.put(MPSessionDao.COLUMN_NAME_TO_ID, sessionEntity.getToId());
        contentValues.put(MPSessionDao.COLUMN_NAME_TOP_TIME, sessionEntity.getTopTime());
        contentValues.put(MPSessionDao.COLUMN_NAME_USER_ID, sessionEntity.getUserId());
        return contentValues;
    }


    public void deleteSession(int sessionId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        db.delete(MPSessionDao.TABLE_NAME, MPSessionDao.COLUMN_NAME_ID + "=?", new String[]{String.valueOf(sessionId)});
    }

    //================= Session end =================


    // ======= File Downloader =======

    /**
     * 获取每条线程已经下载的文件长度
     * @param path
     * @return
     */
    public Map<Integer, Integer> getFileData(String path) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<Integer, Integer> data = new HashMap<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select threadid, downlength from SmartFileDownlog where downpath=?", new String[]{path});
            while(cursor.moveToNext()) {
                data.put(cursor.getInt(0), cursor.getInt(1));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 保存每条线程已经下载的文件长度
     * @param path
     * @param map
     */
    public void saveFileData(String path, Map<Integer, Integer> map) { // int threadid, int position
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                db.execSQL("insert into SmartFileDownlog(downpath, threadid, downlength) values(?,?,?)", new Object[]{path, entry.getKey(), entry.getValue()});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 实时更新每条线程已经下载的文件长度
     * @param path
     * @param map
     */
    public void updateFileData(String path, Map<Integer, Integer> map) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                db.execSQL("update SmartFileDownlog set downlength=? where downpath=? and threadid=?", new Object[]{entry.getValue(), path, entry.getKey()});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 当文件下载完成后，删除对应的下载记录
     * @param path
     */
    public void deleteFileData(String path) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from SmartFileDownlog where downpath=?", new Object[]{path});
    }

    public boolean saveReferenceMsg(ReferenceMsgEntity entity){
        if (entity == null) {
            return false;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues cvs = new ContentValues();
            cvs.put(ReferenceDao.COLUMN_NAME_REFERENCE_MSG_ID, entity.getReferenceMsgId());
            cvs.put(ReferenceDao.COLUMN_NAME_REAL_MSG_ID, entity.getRealMsgId());
            return insertOrUpdate(db, cvs, ReferenceDao.TABLE_NAME, ReferenceDao.COLUMN_NAME_ID);
        }
        return false;
    }

    public List<ReferenceMsgEntity> getReferenceDataWithMsgId(String msgId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<ReferenceMsgEntity> data = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from " + ReferenceDao.TABLE_NAME + " where " + ReferenceDao.COLUMN_NAME_REFERENCE_MSG_ID + "=?", new String[]{msgId});
            while(cursor.moveToNext()) {
                ReferenceMsgEntity entity = getReferenceEntityFromCursor(cursor);
                data.add(entity);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return data;
    }

    private ReferenceMsgEntity getReferenceEntityFromCursor(Cursor cursor){
        ReferenceMsgEntity entity = new ReferenceMsgEntity();
        entity.setId(cursor.getInt(cursor.getColumnIndex(ReferenceDao.COLUMN_NAME_ID)));
        entity.setReferenceMsgId(cursor.getString(cursor.getColumnIndex(ReferenceDao.COLUMN_NAME_REFERENCE_MSG_ID)));
        entity.setRealMsgId(cursor.getString(cursor.getColumnIndex(ReferenceDao.COLUMN_NAME_REAL_MSG_ID)));
        return entity;
    }

    public boolean saveVoteAndMsgId(String voteId, String msgId){
        if(TextUtils.isEmpty(voteId) || TextUtils.isEmpty(msgId)){
            return false;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues cvs = new ContentValues();
            cvs.put(VoteDao.COLUMN_NAME_VOTE_ID, voteId);
            cvs.put(VoteDao.COLUMN_NAME_MSG_ID, msgId);
            return insertOrUpdate(db, cvs, VoteDao.TABLE_NAME, VoteDao.COLUMN_NAME_ID);
        }
        return false;
    }

    public List<String> getMsgIdWithVoteId(String voteId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> data = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from " + VoteDao.TABLE_NAME + " where " + VoteDao.COLUMN_NAME_VOTE_ID + "=?", new String[]{voteId});
            while(cursor.moveToNext()) {
                String msgId = cursor.getString(cursor.getColumnIndex(VoteDao.COLUMN_NAME_MSG_ID));
                data.add(msgId);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return data;
    }
}
