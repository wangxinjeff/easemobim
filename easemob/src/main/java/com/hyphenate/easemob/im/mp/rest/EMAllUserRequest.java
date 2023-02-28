package com.hyphenate.easemob.im.mp.rest;

import com.hyphenate.EMError;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.events.EventUsersReady;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.http.BaseRequest;
import com.hyphenate.easemob.im.officeautomation.utils.PreferenceManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 26/08/2018
 */

public final class EMAllUserRequest {

    private final static int pageSize = 1000;
    private List<MPUserEntity> allUsers = Collections.synchronizedList(new ArrayList<>());
    private int page;
    private EMDataCallBack<List<MPUserEntity>> mDataCallBack;
//    private static Call mCall;

    public EMAllUserRequest setListener(EMDataCallBack<List<MPUserEntity>> callBack) {
        this.mDataCallBack = callBack;
        return this;
    }

    public void request() {
//        if (mCall != null && !mCall.isCanceled()) {
//            mCall.cancel();
//        }
        if (PreferenceManager.getInstance().getLastCacheUsersTime() > 0) {
            page = 0;
            getIncrementUsersByLastTime();
        } else {
            allUsers.clear();
            getAllUsers();
        }
    }

    private void getAllUsers() {
        EMAPIManager.getInstance().getAllUsers(BaseRequest.getCompanyId(), page, pageSize, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject jsonObj = new JSONObject(value);
                    int totalPages = jsonObj.optInt("totalPages");
                    boolean isLast = jsonObj.optBoolean("last");
                    List<MPUserEntity> userList = MPUserEntity.create(jsonObj.optJSONArray("entities"));
                    List<MPUserEntity> newList = new ArrayList<>();
                    for (MPUserEntity entity : userList) {
                        entity.setOrgId(-1);
                        newList.add(entity);
                    }
                    allUsers.addAll(newList);
                    if (page < totalPages - 1) {
                        page++;
                        getAllUsers();
                    }
                    if (isLast) {
                        PreferenceManager.getInstance().setLastCacheUsersTime(System.currentTimeMillis());
                        AppHelper.getInstance().getModel().deleteAllUsers();
                        AppHelper.getInstance().getModel().saveUsersList(allUsers);
                        MPEventBus.getDefault().post(new EventUsersReady());
                        if (mDataCallBack != null) {
                            mDataCallBack.onSuccess(allUsers);
                        }
                    }
                } catch (Exception e) {
                    if (mDataCallBack != null) {
                        mDataCallBack.onError(EMError.GENERAL_ERROR, "json exception");
                    }
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                if (mDataCallBack != null) {
                    mDataCallBack.onError(error, errorMsg);
                }

            }
        });

    }


    private void getIncrementUsersByLastTime() {
        EMAPIManager.getInstance().getIncrementUsersByLastTime(BaseRequest.getCompanyId(), PreferenceManager.getInstance().getLastCacheUsersTime(), page, pageSize, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject jsonObj = new JSONObject(value);
                    int totalPages = jsonObj.optInt("totalPages");
                    boolean isLast = jsonObj.optBoolean("last");
                    List<MPUserEntity> userList = MPUserEntity.create(jsonObj.optJSONArray("entities"));
                    List<MPUserEntity> newList = new ArrayList<>();
                    for (MPUserEntity entity : userList) {
                        entity.setOrgId(-1);
                        newList.add(entity);
                    }
                    allUsers.addAll(newList);
                    if (page < totalPages - 1) {
                        page++;
                        getIncrementUsersByLastTime();
                    }
                    if (isLast) {
                        PreferenceManager.getInstance().setLastCacheUsersTime(System.currentTimeMillis());
                        AppHelper.getInstance().getModel().saveUsersList(allUsers);
                        MPEventBus.getDefault().post(new EventUsersReady());
                        if (mDataCallBack != null) {
                            mDataCallBack.onSuccess(allUsers);
                        }
                    }
                } catch (Exception e) {
                    if (mDataCallBack != null) {
                        mDataCallBack.onError(EMError.GENERAL_ERROR, "json exception");
                    }
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                if (mDataCallBack != null) {
                    mDataCallBack.onError(error, errorMsg);
                }

            }
        });

    }


    public static void cancel() {
//        if (mCall != null && !mCall.isCanceled()) {
//            mCall.cancel();
//        }
    }


}
