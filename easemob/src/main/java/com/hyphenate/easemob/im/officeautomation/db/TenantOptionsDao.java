package com.hyphenate.easemob.im.officeautomation.db;

import com.hyphenate.easemob.im.mp.cache.TenantOptionCache;
import com.hyphenate.easemob.imlibs.officeautomation.domain.TenantOption;

import java.util.List;

public class TenantOptionsDao {

    public static final String TABLE_NAME = "tenant_options";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_TENANT_ID = "tenantId";
    public static final String COLUMN_NAME_OPTION_NAME = "optionName";
    public static final String COLUMN_NAME_OPTION_VALUE = "optionValue";
    public static final String COLUMN_NAME_CREATE_USER_ID = "createUserId";
    public static final String COLUMN_NAME_CREATE_TIME = "createTime";
    public static final String COLUMN_NAME_LAST_UPDATE_TIME = "lastUpdateTime";


    public List<TenantOption> getTenantOptions() {
        return AppDBManager.getInstance().getTenantOptions();
    }

    public void saveTenantOptions(List<TenantOption> options) {
        AppDBManager.getInstance().saveTenantOptions(options);
        TenantOptionCache.getInstance().setAllOptions(options);
    }

    public TenantOption getTenantOptionForKey(String key) {
        return AppDBManager.getInstance().getTenantOptionForKey(key);
    }

    public void deleteAllTenantOptions() {
        AppDBManager.getInstance().deleteAllTenantOption();
    }
}
