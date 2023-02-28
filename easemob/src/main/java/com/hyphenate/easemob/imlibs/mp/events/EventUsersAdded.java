package com.hyphenate.easemob.imlibs.mp.events;

import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;

import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 28/11/2018
 */
public class EventUsersAdded {
	private List<MPUserEntity> users;

	public List<MPUserEntity> getUsers() {
		return users;
	}

	public void setUsers(List<MPUserEntity> addUsers) {
		this.users = addUsers;
	}
}
