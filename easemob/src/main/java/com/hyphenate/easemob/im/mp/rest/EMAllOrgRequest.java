package com.hyphenate.easemob.im.mp.rest;


import com.hyphenate.EMError;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.events.EventOrgsReady;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.http.BaseRequest;
import com.hyphenate.easemob.im.officeautomation.utils.PreferenceManager;

import org.json.JSONArray;
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

public final class EMAllOrgRequest {

    private static final int pageSize = 500;
    private List<MPOrgEntity> allOrgs = Collections.synchronizedList(new ArrayList<>());
    private int page;
    private EMDataCallBack<List<MPOrgEntity>> mDataCallBack;


    public EMAllOrgRequest() {
    }

    public EMAllOrgRequest setListener(EMDataCallBack<List<MPOrgEntity>> callBack) {
        this.mDataCallBack = callBack;
        return this;
    }

    public void request() {
        cancel();
        if (PreferenceManager.getInstance().getCachePreTime() > 0) {
            page = 0;
            getIncrmentDepartmentsByLastTime();
        } else {
            getAllOrgs();
        }
    }

    private void getAllOrgs() {
        EMAPIManager.getInstance().getAllSubOrgs(BaseRequest.getCompanyId(), -1, page, pageSize, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                cancel();
                try {
                    JSONObject jsonObj = new JSONObject(value);
                    JSONArray jsonEntities = jsonObj.optJSONArray("entities");
                    int totalPages = jsonObj.optInt("totalPages");
                    boolean isLast = jsonObj.optBoolean("last");
                    List<MPOrgEntity> orgEntities = MPOrgEntity.create(jsonEntities);
                    if (orgEntities != null) {
                        allOrgs.addAll(orgEntities);
                    }
                    if (page < totalPages - 1) {
                        page++;
                        getAllOrgs();
                    }
                    if (isLast) {
                        if (allOrgs != null) {
                            AppHelper.getInstance().getModel().deleteAllOrgs();
                            AppHelper.getInstance().getModel().saveAllOrgsList(allOrgs);
                        }
                        PreferenceManager.getInstance().saveCachePreTime(System.currentTimeMillis());
                        MPEventBus.getDefault().post(new EventOrgsReady());
                        if (mDataCallBack != null) {
                            mDataCallBack.onSuccess(allOrgs);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mDataCallBack != null) {
                        mDataCallBack.onError(EMError.GENERAL_ERROR, e.getMessage());
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

    private void getIncrmentDepartmentsByLastTime() {
        EMAPIManager.getInstance().getIncrementDepartmentsByLastTime(BaseRequest.getCompanyId(), PreferenceManager.getInstance().getCachePreTime(), page, pageSize, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                cancel();
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    JSONArray jsonEntities = jsonObject.optJSONArray("entities");
                    List<MPOrgEntity> orgEntities = MPOrgEntity.create(jsonEntities);
                    int totalPages = jsonObject.optInt("totalPages");
                    boolean isLast = jsonObject.optBoolean("last");
                    if (orgEntities != null) {
                        allOrgs.addAll(orgEntities);
                    }
                    if (page < totalPages - 1) {
                        page++;
                        getIncrmentDepartmentsByLastTime();
                    }
                    if (isLast) {
                        if (allOrgs != null) {
                            AppHelper.getInstance().getModel().saveAllOrgsList(allOrgs);
                        }
                        PreferenceManager.getInstance().saveCachePreTime(System.currentTimeMillis());
                        MPEventBus.getDefault().post(new EventOrgsReady());
                        if (mDataCallBack != null) {
                            mDataCallBack.onSuccess(allOrgs);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mDataCallBack != null) {
                        mDataCallBack.onError(EMError.GENERAL_ERROR, e.getMessage());
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
    }


}
