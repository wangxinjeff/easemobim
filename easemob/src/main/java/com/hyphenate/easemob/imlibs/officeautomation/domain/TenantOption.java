package com.hyphenate.easemob.imlibs.officeautomation.domain;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TenantOption {
    private int id;
    private long createTime;
    private long lastUpdateTime;
    private int tenantId;
    private String optionName;
    private String optionValue;
    private int createUserId;


    public static TenantOption create(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        TenantOption option = new TenantOption();
        option.setTenantId(jsonObj.optInt("tenantId"));
        option.setOptionValue(jsonObj.optString("optionValue"));
        option.setOptionName(jsonObj.optString("optionName"));
        option.setCreateUserId(jsonObj.optInt("createUserId"));
        option.setLastUpdateTime(jsonObj.optLong("lastUpdateTime"));
        option.setCreateTime(jsonObj.optLong("createTime"));
        option.setId(jsonObj.optInt("id"));
        return option;
    }

    public static List<TenantOption> create(JSONArray jsonArr) {
        List<TenantOption> optionList = new ArrayList<>();
        if (jsonArr == null || jsonArr.length() == 0) {
            return optionList;
        }
        for (int i = 0; i < jsonArr.length(); i++) {
            TenantOption option = TenantOption.create(jsonArr.optJSONObject(i));
            if (option != null) {
                optionList.add(option);
            }
        }
        return optionList;
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

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }
}
