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

import com.hyphenate.easemob.im.officeautomation.domain.ReferenceMsgEntity;

import java.util.List;

public class VoteDao {
    public static final String TABLE_NAME = "vote_msg";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_VOTE_ID = "vote_id";
    public static final String COLUMN_NAME_MSG_ID = "msg_id";


    public VoteDao(Context context) {
    }

}
