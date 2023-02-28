package com.hyphenate.easemob.imlibs.officeautomation.emrequest.okhttp.progress.listener.impl;

import java.io.Serializable;

/**
 * UI进度回调实体类
 * 
 * @author lyuzhao
 * 
 */
public class ProgressModel implements Serializable {

	// 当前读取字节长度
	private long currentBytes;
	// 总字节长度
	private long contentLength;
	// 是否读取完成
	private boolean done;

	public ProgressModel(long currentBytes, long contentLength, boolean done) {
		super();
		this.currentBytes = currentBytes;
		this.contentLength = contentLength;
		this.done = done;
	}

	public long getCurrentBytes() {
		return currentBytes;
	}

	public long getContentLength() {
		return contentLength;
	}

	public boolean isDone() {
		return done;
	}

	public void setCurrentBytes(long currentBytes) {
		this.currentBytes = currentBytes;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	@Override
	public String toString() {
		return "ProgressModel [currentBytes=" + currentBytes + ", contentLength=" + contentLength + ", done=" + done
				+ "]";
	}

}
