package com.hyphenate.easemobim;

import com.hyphenate.easemob.im.EnvHelper;

public class HOCEnv implements EnvHelper.BaseEnv {
    @Override
    public boolean enableDnsConfig() {
        return false;
    }

    @Override
    public int imPort() {
        return 16717;
    }

    @Override
    public String imServer() {
        return "106.38.243.169";
    }

    @Override
    public String restServer() {
        return "http://106.38.243.169:12001";
    }

    @Override
    public String ccsServer() {
        return "http://106.38.243.169:12007";
    }

    @Override
    public String wbServer() {
        return "http://whiteboard-mp-dev.easemob.com";
    }

    @Override
    public String appKey() {
        return "easemob-demo#easeim";
    }
}
