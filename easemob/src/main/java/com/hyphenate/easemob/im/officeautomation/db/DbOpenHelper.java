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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.blankj.utilcode.util.EncryptUtils;
import com.hyphenate.easemob.im.officeautomation.utils.PreferenceManager;

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 19;//14
    private static DbOpenHelper instance;

    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + UserDao.TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_NICK + " TEXT, "
            + UserDao.COLUMN_NAME_AVATAR + " TEXT, "
            + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";

    private static final String ORG_TABLE_CREATE = "CREATE TABLE "
            + OrgDao.TABLE_NAME_ORG + " ("
            + OrgDao.COLUMN_NAME_ORG_NAME + " TEXT, "
            + OrgDao.COLUMN_NAME_ORG_RANK + " TEXT, "
            + OrgDao.COLUMN_NAME_ORG_PARENT_ID + " INTEGER, "
            + OrgDao.COLUMN_NAME_ORG_COMPANY_ID + " INTEGER, "
            + OrgDao.COLUMN_NAME_ORG_TENANT_ID + " INTEGER, "
            + OrgDao.COLUMN_NAME_ORG_DEPTH + " INTEGER, "
            + OrgDao.COLUMN_NAME_ORG_MEMBER_COUNT + " INTEGER, "
            + OrgDao.COLUMN_NAME_ORG_ID + " INTEGER PRIMARY KEY);";

    private static final String EXT_TABLE_CREATE = "CREATE TABLE "
            + UserDao.EXT_TABLE_NAME + " ("
            + UserDao.COLUMN_EXT_NAME_NICK + " TEXT, "
            + UserDao.COLUMN_EXT_NAME_AVATAR + " TEXT, "
            + UserDao.COLUMN_EXT_NAME_ACCOUNT + " TEXT, "
            + UserDao.COLUMN_EXT_NAME_PHONE + " TEXT, "
            + UserDao.COLUMN_EXT_NAME_TEL_PHONE + " TEXT, "
            + UserDao.COLUMN_EXT_NAME_EMAIL + " TEXT, "
            + UserDao.COLUMN_EXT_NAME_GENDER + " TEXT, "
            + UserDao.COLUMN_EXT_NAME_STAFFNO + " TEXT, "
            + UserDao.COLUMN_EXT_NAME_USER_PINYIN + " TEXT, "
            + UserDao.COLUMN_EXT_NAME_ALIAS + " TEXT, "
            + UserDao.COLUMN_EXT_NAME_USER_TYPE + " TEXT, "
            + UserDao.COLUMN_EXT_NAME_ORG_ID + " INTEGER, "
            + UserDao.COLUMN_EXT_NAME_USER_ID + " INTEGER, "
            + UserDao.COLUMN_EXT_NAME_IM_USER_ID + " TEXT PRIMARY KEY);";

    private static final String EXT_TABLE_GROUP_CREATE = "CREATE TABLE "
            + GroupDao.GROUP_TABLE_NAME + " ("
            + GroupDao.COLUMN_NAME_EASEMOB_GROUP_ID + " TEXT PRIMARY KEY, "
            + GroupDao.COLUMN_NAME_GROUP_ID + " INTEGER, "
            + GroupDao.COLUMN_NAME_GROUP_AVATAR + " TEXT, "
            + GroupDao.COLUMN_NAME_GROUP_NAME + " TEXT, "
            + GroupDao.COLUMN_NAME_TYPE + " TEXT, "
            + GroupDao.COLUMN_NAME_CLUSTER + " BIT,"
            + GroupDao.COLUMN_NAME_CREATE_TIME + " INTEGER);";

    private static final String INVITE_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + InviteMessageDao.TABLE_NAME + " ("
            + InviteMessageDao.COLUMN_NAME_USER_ID + " INTEGER PRIMARY KEY UNIQUE, "
            + InviteMessageDao.COLUMN_NAME_FROM + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_GROUP_ID + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_GROUP_Name + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_REASON + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_STATUS + " INTEGER, "
            + InviteMessageDao.COLUMN_NAME_CHAT_GROUP_ID + " INTEGER, "
            + InviteMessageDao.COLUMN_NAME_CLUSTER + " BIT, "
            + InviteMessageDao.COLUMN_NAME_FRIEND_ID + " INTEGER, "
            + InviteMessageDao.COLUMN_NAME_ISINVITEFROMME + " INTEGER, "
            + InviteMessageDao.COLUMN_NAME_UNREAD_MSG_COUNT + " INTEGER, "
            + InviteMessageDao.COLUMN_NAME_TIME + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_GROUPINVITER + " TEXT); ";

    private static final String CREATE_PREF_TABLE = "CREATE TABLE "
            + UserDao.PREF_TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_DISABLED_GROUPS + " TEXT, "
            + UserDao.COLUMN_NAME_DISABLED_IDS + " TEXT);";

    private static final String CREATE_CHAT_TABLE = "CREATE TABLE "
            + ConversationDao.CONVERSATION_TABLE_NAME + " ("
            + ConversationDao.COLUMN_CONVERSATION_NAME_ID + " TEXT PRIMARY KEY,"
            + ConversationDao.COLUMN_NAME_STICKY_TIME + " TEXT, "
            + ConversationDao.COLUMN_NAME_CONVERSATION_TYPE + " INTEGER);";

    private static final String CREATE_GROUP_MUTES = "CREATE TABLE "
            + GroupMuteDao.GROUP_MUTES_TABLE_NAME + " ("
            + GroupMuteDao.COLUMN_NAME_GROUP_ID + " TEXT, "
            + GroupMuteDao.COLUMN_NAME_MUTE_USERNAME + " TEXT, "
            + GroupMuteDao.COLUMN_NAME_LAST_UPDATE_TIME + " INTEGER, "
            + GroupMuteDao.COLUMN_NAME_MUTE_TIME + " INTEGER, "
            + "PRIMARY KEY (" + GroupMuteDao.COLUMN_NAME_GROUP_ID + ", " + GroupMuteDao.COLUMN_NAME_MUTE_USERNAME + "));";

    private static final String CREATE_DISTRUBS = "CREATE TABLE "
            + NoDisturbDao.TABLE_NAME + " ("
            + NoDisturbDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY, "
            + NoDisturbDao.COLUMN_NAME_NAME + " TEXT, "
            + NoDisturbDao.COLUMN_NAME_IS_GROUP + " BIT, "
            + NoDisturbDao.COLUMN_NAME_LAST_UPDATE_TIME + " INTEGER);";

    private static final String CREATE_DRAFT_TABLE = "CREATE TABLE "
            + DraftDao.TABLE_NAME + " ("
            + DraftDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY, "
            + DraftDao.COLUMN_NAME_CONTENT + " TEXT, "
            + DraftDao.COLUMN_NAME_EXTRA + " TEXT, "
            + DraftDao.COLUMN_NAME_LAST_UPDATE_TIME + " INTEGER);";

    private static final String CREATE_TENANT_OPTIONS = "CREATE TABLE "
            + TenantOptionsDao.TABLE_NAME + " ("
            + TenantOptionsDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, "
            + TenantOptionsDao.COLUMN_NAME_OPTION_NAME + " TEXT,"
            + TenantOptionsDao.COLUMN_NAME_OPTION_VALUE + " TEXT, "
            + TenantOptionsDao.COLUMN_NAME_CREATE_USER_ID + " INTEGER, "
            + TenantOptionsDao.COLUMN_NAME_CREATE_TIME + " INTEGER,"
            + TenantOptionsDao.COLUMN_NAME_LAST_UPDATE_TIME + " INTEGER,"
            + TenantOptionsDao.COLUMN_NAME_TENANT_ID + " INTEGER);";


    private static final String CREATE_MP_SESSION = "CREATE TABLE "
            + MPSessionDao.TABLE_NAME + " ("
            + MPSessionDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, "
            + MPSessionDao.COLUMN_NAME_AVATAR + " TEXT, "
            + MPSessionDao.COLUMN_NAME_CHAT_TYPE + " TEXT, "
            + MPSessionDao.COLUMN_NAME_CREATE_TIME + " INTEGER, "
            + MPSessionDao.COLUMN_NAME_IM_ID + " TEXT, "
            + MPSessionDao.COLUMN_NAME_IS_DISTURB + " BIT, "
            + MPSessionDao.COLUMN_NAME_IS_TOP + " BIT, "
            + MPSessionDao.COLUMN_NAME_LAST_UPDATE_TIME + " INTEGER, "
            + MPSessionDao.COLUMN_NAME_NAME + " TEXT, "
            + MPSessionDao.COLUMN_NAME_TO_ID + " TEXT, "
            + MPSessionDao.COLUMN_NAME_TOP_TIME + " INTEGER, "
            + MPSessionDao.COLUMN_NAME_USER_ID + " INTEGER);";

    private static final String CREATE_FILE_DOWNLOAD = "CREATE TABLE IF NOT EXISTS SmartFileDownlog (id integer primary key autoincrement, downpath varchar(100), threadid INTEGER, downlength INTEGER)";

    private static final String CREATE_REFERENCE_TABLE = "CREATE TABLE "
            + ReferenceDao.TABLE_NAME + " ("
            + ReferenceDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, "
            + ReferenceDao.COLUMN_NAME_REFERENCE_MSG_ID + " TEXT, "
            + ReferenceDao.COLUMN_NAME_REAL_MSG_ID + " TEXT);";

    private static final String CREATE_VOTE_TABLE = "CREATE TABLE "
            + VoteDao.TABLE_NAME + " ("
            + VoteDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, "
            + VoteDao.COLUMN_NAME_VOTE_ID + " TEXT, "
            + VoteDao.COLUMN_NAME_MSG_ID + " TEXT);";

    private DbOpenHelper(Context context, String userId) {
        super(context, getUserDatabaseName(userId), null, DATABASE_VERSION);
    }

    public static DbOpenHelper getInstance(Context context, String userId) {
        if (instance == null) {
            instance = new DbOpenHelper(context.getApplicationContext(), userId);
        }
        return instance;
    }

    private static String getUserDatabaseName(String userId) {
        return EncryptUtils.encryptMD5ToString(userId).toLowerCase() + "_oa.db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERNAME_TABLE_CREATE);
        db.execSQL(ORG_TABLE_CREATE);
        db.execSQL(EXT_TABLE_CREATE);
        db.execSQL(EXT_TABLE_GROUP_CREATE);
        db.execSQL(INVITE_MESSAGE_TABLE_CREATE);
        db.execSQL(CREATE_PREF_TABLE);
        db.execSQL(CREATE_CHAT_TABLE);
        db.execSQL(CREATE_GROUP_MUTES);
        db.execSQL(CREATE_DRAFT_TABLE);
        db.execSQL(CREATE_TENANT_OPTIONS);
        db.execSQL(CREATE_MP_SESSION);
        db.execSQL(CREATE_DISTRUBS);
        db.execSQL(CREATE_FILE_DOWNLOAD);
        db.execSQL(CREATE_REFERENCE_TABLE);
        db.execSQL(CREATE_VOTE_TABLE);
    }

    private void dropTableDB(SQLiteDatabase db) {
        db.execSQL("drop table  if exists " + UserDao.TABLE_NAME);
        db.execSQL("drop table  if exists " + OrgDao.TABLE_NAME_ORG);
        db.execSQL("drop table  if exists " + UserDao.EXT_TABLE_NAME);
        db.execSQL("drop table  if exists " + GroupDao.GROUP_TABLE_NAME);
        db.execSQL("drop table  if exists " + InviteMessageDao.TABLE_NAME);
        db.execSQL("drop table  if exists " + UserDao.PREF_TABLE_NAME);
        db.execSQL("drop table  if exists " + ConversationDao.CONVERSATION_TABLE_NAME);
        db.execSQL("drop table  if exists " + GroupMuteDao.GROUP_MUTES_TABLE_NAME);
        db.execSQL("drop table  if exists " + DraftDao.TABLE_NAME);
        db.execSQL("drop table  if exists " + TenantOptionsDao.TABLE_NAME);
        db.execSQL("drop table  if exists " + MPSessionDao.TABLE_NAME);
        db.execSQL("drop table  if exists " + NoDisturbDao.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS SmartFileDownlog");
        db.execSQL("drop table  if exists " + ReferenceDao.TABLE_NAME);
        db.execSQL("drop table  if exists " + VoteDao.TABLE_NAME);
        try {
            PreferenceManager.getInstance().clearCacheTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTableDB(db);
        onCreate(db);

    }

    public synchronized static void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }

}
