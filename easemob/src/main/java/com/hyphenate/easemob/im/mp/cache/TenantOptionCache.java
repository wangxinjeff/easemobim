package com.hyphenate.easemob.im.mp.cache;

import com.hyphenate.easemob.im.officeautomation.db.TenantOptionsDao;
import com.hyphenate.easemob.imlibs.mp.events.EventTenantOptionChanged;
import com.hyphenate.easemob.imlibs.officeautomation.domain.TenantOption;
import com.hyphenate.eventbus.MPEventBus;

import java.util.List;

public class TenantOptionCache {

    private static TenantOptionCache sInstance;
    public static final String OPTION_NAME_WATERMARK = "watermark.enabled";
    private List<TenantOption> allOptions;
    private boolean showWaterMark;

    public static TenantOptionCache getInstance() {
        if (sInstance == null) {
            synchronized (TenantOptionCache.class) {
                if (sInstance == null) {
                    sInstance = new TenantOptionCache();
                }
            }
        }
        return sInstance;
    }


    public void setAllOptions(List<TenantOption> tenantOptions) {
        this.allOptions = tenantOptions;
        if (allOptions != null && !allOptions.isEmpty()) {
            for (TenantOption option : allOptions) {
                if (OPTION_NAME_WATERMARK.equals(option.getOptionName())) {
                    showWaterMark = "true".equals(option.getOptionValue());
                }
            }
        }
        MPEventBus.getDefault().post(new EventTenantOptionChanged(OPTION_NAME_WATERMARK));
    }

    public List<TenantOption> getAllOptions() {
        if (allOptions == null) {
            allOptions = new TenantOptionsDao().getTenantOptions();
        }
        return allOptions;
    }


    public boolean isShowWaterMark() {
        return showWaterMark;
    }






}
