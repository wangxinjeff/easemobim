package com.hyphenate.easemob.easeui.delegates;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 05/12/2018
 */
public interface DraftDelegate {

	boolean hasDraft(String key);

	boolean removeDraft(String key);

	String getContent(String key);

}
