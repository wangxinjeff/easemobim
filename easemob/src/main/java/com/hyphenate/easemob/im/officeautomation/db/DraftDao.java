package com.hyphenate.easemob.im.officeautomation.db;


import com.hyphenate.easemob.easeui.domain.DraftEntity;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 04/12/2018
 */
public class DraftDao {
	public static final String TABLE_NAME = "draft";
	public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_CONTENT = "content";
	public static final String COLUMN_NAME_EXTRA = "extra";
	public static final String COLUMN_NAME_LAST_UPDATE_TIME = "last_update_time";

	public boolean saveDraft(DraftEntity draft){
		return AppDBManager.getInstance().saveDraft(draft);
	}

	public DraftEntity getDraft(String id){
		return AppDBManager.getInstance().getDraft(id);
	}



	public DraftDao() {
	}

}
