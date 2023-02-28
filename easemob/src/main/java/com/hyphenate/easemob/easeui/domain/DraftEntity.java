package com.hyphenate.easemob.easeui.domain;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 05/12/2018
 */
public class DraftEntity {
	private String id;
	private String content;
	private String extra;

	public DraftEntity(){}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}
}
