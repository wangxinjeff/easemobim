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

import android.content.ContentValues;

import com.hyphenate.easemob.im.officeautomation.domain.InviteMessage;

import java.util.List;

public class InviteMessageDao {
    static final String TABLE_NAME = "new_friends_msgs";
    static final String COLUMN_NAME_FROM = "hxId";
    static final String COLUMN_NAME_GROUP_ID = "groupid";
    static final String COLUMN_NAME_GROUP_Name = "groupname";
    static final String COLUMN_NAME_CLUSTER = "cluster";
    static final String COLUMN_NAME_FRIEND_ID = "friendId";
    static final String COLUMN_NAME_USER_ID = "userId";

    static final String COLUMN_NAME_TIME = "time";
    static final String COLUMN_NAME_REASON = "reason";
    public static final String COLUMN_NAME_STATUS = "status";
    static final String COLUMN_NAME_ISINVITEFROMME = "isInviteFromMe";
    static final String COLUMN_NAME_GROUPINVITER = "groupinviter";
    static final String COLUMN_NAME_CHAT_GROUP_ID = "chatGroupId";

    static final String COLUMN_NAME_UNREAD_MSG_COUNT = "unreadMsgCount";


    /**
     * save message
     * @param message
     * @return return cursor of the message
     */
    public long saveMessage(InviteMessage message) {
        return AppDBManager.getInstance().saveMessage(message);
    }

    /**
     * update message
     * @param userId
     * @param values
     */
    public void updateMessage(int userId, ContentValues values) {
        AppDBManager.getInstance().updateMessage(userId, values);
    }

    /**
     * get messge
     * @return
     */
    public InviteMessage getInviteMessage(int userId) {
        return AppDBManager.getInstance().getInviteMessage(userId);
    }
    /**
     * get messges
     * @return
     */
    public List<InviteMessage> getMessagesList() {
        return AppDBManager.getInstance().getMessagesList();
    }

    public void deleteMessage(String from) {
        AppDBManager.getInstance().deleteMessage(from);
    }

    public void deleteGroupMessage(String groupId) {
        AppDBManager.getInstance().deleteGroupMessage(groupId);
    }

    public void deleteGroupMessage(String groupId, String from) {
        AppDBManager.getInstance().deleteGroupMessage(groupId, from);
    }

    public int getUnreadMessagesCount() {
        return AppDBManager.getInstance().getUnreadNotifyCount();
    }

    public void saveUnreadMessageCount(int count) {
        AppDBManager.getInstance().setUnreadNotifyCount(count);
    }
}
