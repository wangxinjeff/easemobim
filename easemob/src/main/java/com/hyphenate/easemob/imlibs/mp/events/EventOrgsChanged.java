package com.hyphenate.easemob.imlibs.mp.events;

import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;

import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 28/11/2018
 */
public class EventOrgsChanged {
	private List<MPOrgEntity> orgEntities;

	public List<MPOrgEntity> getOrgEntities() {
		return orgEntities;
	}

	public void setOrgEntities(List<MPOrgEntity> orgEntities) {
		this.orgEntities = orgEntities;
	}
}
