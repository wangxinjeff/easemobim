package com.hyphenate.easemob.pickerview;

import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 14/12/2018
 * <p>
 * picker点击事件监听
 */
public interface ImageShowPickerListener {

	void addOnClickListener(int remainNum);

	void picOnClickListener(List<ImageShowPickerBean> list, int position, int remainNum);

	void delOnClickListener(int position, ImageShowPickerBean item,  int remainNum);

}
