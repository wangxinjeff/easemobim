package com.hyphenate.easemob.easeui.domain;

import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.pickerview.ImageShowPickerBean;

import java.io.Serializable;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 12/12/2018
 */
public class StickerEntity extends ImageShowPickerBean implements Serializable {
	static final long serialVersionUID = 59L;

	private int id;
	private long createTime;
	private long lastUpdateTime;
	private int tenantId;
	private int userId;
	private String groupName;
	private String type;
	private int rank;
	private String url;
	private String thumbnail;
	private boolean isDelete;
	private int width;
	private int height;
	private String localThumbUrl;
	private String localUrl;
	private String digest;
	private String md5Val;

	private int resId;

	public String getMd5Val() {
		return md5Val;
	}

	public void setMd5Val(String md5Val) {
		this.md5Val = md5Val;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public int getTenantId() {
		return tenantId;
	}

	public void setTenantId(int tenantId) {
		this.tenantId = tenantId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean delete) {
		isDelete = delete;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
	public String setImageShowPickerUrl() {
		return MPClient.get().getAppServer() + url;
	}

	@Override
	public int setImageShowPickerDelRes() {
		return resId;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getLocalThumbUrl() {
		return localThumbUrl;
	}

	public void setLocalThumbUrl(String localThumbUrl) {
		this.localThumbUrl = localThumbUrl;
	}

	public String getLocalUrl() {
		return localUrl;
	}

	public void setLocalUrl(String localUrl) {
		this.localUrl = localUrl;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}
}
