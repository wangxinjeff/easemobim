package com.hyphenate.easemob.imlibs.mp;

import com.hyphenate.easemob.imlibs.mp.utils.MPLog;

import java.util.HashMap;
import java.util.Map;

public class MPErrorUtils {

    private static final String TAG = "MPErrorUtils";



    private static final Map<Long, String> errorContainers = new HashMap<>();

    static {
        errorContainers.put(1000213L, "您的设备已被锁定，请联系管理员！");
        errorContainers.put(1000208L, "用户名或密码错误！");
        errorContainers.put(1000206L, "用户名不存在！");


        errorContainers.put(1000601L, "群不存在");
        errorContainers.put(1000602L, "创建群失败");
        errorContainers.put(1000603L, "修改群失败");
        errorContainers.put(1000604L, "删除群失败");
        errorContainers.put(1000605L, "群成员超过最大群人员上限");

        errorContainers.put(1000701L, "用户不在群中");
        errorContainers.put(1000702L, "用户已在群中");
        errorContainers.put(1000703L, "用户不属于此群");
        errorContainers.put(1000704L, "用户ID为空");
        errorContainers.put(1000705L, "用户ID列表为空");
        errorContainers.put(1000706L, "群主不能退出");
        errorContainers.put(1000707L, "用户审批不存在");
        errorContainers.put(1000708L, "用户在黑名单中");
        errorContainers.put(1000709L, "用户不同意");
        errorContainers.put(1000710L, "群主不能被移除");
        errorContainers.put(1000711L, "用户ID列表太大");
        errorContainers.put(2000201L, "群不存在");

        errorContainers.put(1000801L, "旧密码错误!");
        errorContainers.put(1000802L, "旧密码未填写！");
        errorContainers.put(1000803L, "新密码未填写！");

        errorContainers.put(1400001L, "该账号已锁定，请联系管理员处理！");

    }


    /**
     * 根据错误码，返回错误信息
     * @param errorCode
     * @param defaultValue
     * @return
     */
    public static String getErrorTips(long errorCode, String defaultValue) {
        String errorTips = defaultValue == null ? "请求失败，请联系管理员!" : defaultValue;
        if(errorContainers.containsKey(errorCode)) {
            return errorContainers.get(errorCode);
        }
        MPLog.e(TAG, "errorCode:" + errorCode);
        return errorTips;
    }

    public static String getErrorTips(long errorCode) {
        return getErrorTips(errorCode, null);
    }



}
