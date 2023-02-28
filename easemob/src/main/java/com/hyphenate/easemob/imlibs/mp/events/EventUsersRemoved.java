package com.hyphenate.easemob.imlibs.mp.events;

import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;

import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 28/11/2018
 */
public class EventUsersRemoved {
	private List<MPUserEntity> userEntities;

	public List<MPUserEntity> getUserEntities() {
		return userEntities;
	}

	public void setUserEntities(List<MPUserEntity> userEntities) {
		this.userEntities = userEntities;
	}
}
