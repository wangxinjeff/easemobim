package com.hyphenate.easemob.im.officeautomation.db;

import java.util.List;
import java.util.Map;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 19/09/2018
 */
public class GroupMuteDao {
	public static final String GROUP_MUTES_TABLE_NAME = "group_mutes";
	public static final String COLUMN_NAME_GROUP_ID = "group_id";
	public static final String COLUMN_NAME_MUTE_USERNAME = "mute_username";
	public static final String COLUMN_NAME_MUTE_TIME = "mute_time";
	public static final String COLUMN_NAME_LAST_UPDATE_TIME = "last_update_time";

	public void muteGroupUsername(String groupId, String username, long muteTime) {
		AppDBManager.getInstance().muteGroupUsername(groupId, username, muteTime);
	}

	public void muteGroupUsernames(String groupId, List<String> imUsernames, long muteTime){
		AppDBManager.getInstance().muteGroupUsernames(groupId, imUsernames, muteTime);
	}

	public void unMuteGroupUsernames(String groupId, List<String> imUsernames){
		AppDBManager.getInstance().unMuteGroupUsernames(groupId, imUsernames);
	}

	public void unMuteGroupUsername(String groupId, String username) {
		AppDBManager.getInstance().unMuteGroupUsername(groupId, username);
	}

	public void updateGroupMutes(String groupId, Map<String, Long>  imUsernames) {
		AppDBManager.getInstance().updateGroupMutes(groupId, imUsernames);
	}

	public boolean isMute(String groupId, String imUsername){
		return AppDBManager.getInstance().isMuted(groupId, imUsername);
	}

}
