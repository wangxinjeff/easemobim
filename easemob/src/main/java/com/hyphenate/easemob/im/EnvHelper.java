package com.hyphenate.easemob.im;

public class EnvHelper {

    public interface BaseEnv {
        boolean enableDnsConfig();
        int imPort();
        String imServer();
        String restServer();
        String ccsServer();
        String wbServer();
        String appKey();
    }

//    public static BaseEnv getEnv() {
//        BaseEnv baseEnv = new POC();
//        return baseEnv;
//    }

}
