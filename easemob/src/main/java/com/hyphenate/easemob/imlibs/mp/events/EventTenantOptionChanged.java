package com.hyphenate.easemob.imlibs.mp.events;

public class EventTenantOptionChanged {
    private String optionName;

    public EventTenantOptionChanged() {
    }

    public EventTenantOptionChanged(String optionName) {
        this.optionName = optionName;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
}
