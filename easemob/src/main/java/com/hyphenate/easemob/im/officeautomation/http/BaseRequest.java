package com.hyphenate.easemob.im.officeautomation.http;

import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.imlibs.mp.prefs.PrefsUtil;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 22/08/2018
 */

public class BaseRequest {

    public static int getTenantId() {
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser != null) {
            return loginUser.getTenantId();
        }
        return -1;
    }

    public static int getCompanyId() {
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser != null && loginUser.getEntityBean() != null) {
            MPUserEntity entity = loginUser.getEntityBean();
            if (entity.getOrgEntities() != null && !entity.getOrgEntities().isEmpty()) {
                return entity.getOrgEntities().get(0).getCompanyId();
            }
        }
        return -1;
    }

    public static int getUserId() {
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser != null) {
            return loginUser.getId();
        }
        return -1;
    }

    public static String getAppKey() {
        return PrefsUtil.getInstance().getAppkey();
    }


}
