package com.hyphenate.easemob.easeui.domain;

import java.io.Serializable;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 03/06/2018
 */

public class ImageItem implements Serializable {

	public String name; //图片的姓名
	public String path; //图片的路径
	public long size;   //图片的大小
	public int width;   //图片的宽度
	public int height;  //图片的高度
	public String mimeType; //图片的类型
	public long addTime;    //图片的创建时间

	/** 图片的路径和创建时间相同就认为是同一张图片 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ImageItem){
			ImageItem item = (ImageItem)obj;
			return this.path.equalsIgnoreCase(item.path) && this.addTime == item.addTime;
		}
		return super.equals(obj);
	}
}
