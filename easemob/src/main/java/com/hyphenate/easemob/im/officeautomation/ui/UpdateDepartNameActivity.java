package com.hyphenate.easemob.im.officeautomation.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.domain.CreateDepartPostEntity;
import com.hyphenate.easemob.im.officeautomation.domain.CreateDepartResultEntity;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.http.BaseRequest;
import com.hyphenate.util.EMLog;

import org.json.JSONObject;

/**
 * Created by qby on 2018/06/27
 * 修改部门名称
 **/
public class UpdateDepartNameActivity extends BaseActivity {
    private static final String TAG = "UpdateDepartNameActivit";
    public static final int RESULT_CODE_UPDATE = 1;
    private EditText mEtDepartment;
    private TextView mTvTitle;
    private TextView mTvSave;
    private ImageView mIvBack;
    private boolean isAdd;
    private int orgId;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_update_depart_name);
        initViews();
        initListeners();
        initData();
    }

    private void initViews() {
        mEtDepartment = $(R.id.et_department);
        mTvTitle = $(R.id.tv_title);
        mTvSave = $(R.id.tv_save);
        mIvBack = $(R.id.iv_back);
    }

    private void initListeners() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String department = mEtDepartment.getText().toString();
                if (!TextUtils.isEmpty(department)) {
                    MPOrgEntity orgInfo = AppHelper.getInstance().getModel().getOrgInfo(orgId);
                    String rank = orgInfo.getRank();
                    showProgressDialog();
                    if (isAdd) {
                        CreateDepartPostEntity entity = new  CreateDepartPostEntity(department.trim(), String.valueOf(Integer.parseInt(rank) + 1), orgId);

                        EMAPIManager.getInstance().postAddOrg(BaseRequest.getTenantId(), new Gson().toJson(entity), new EMDataCallBack<String>() {
                            @Override
                            public void onSuccess(String value) {
                                try {
                                    JSONObject jsonRes = new JSONObject(value);
                                    String status = jsonRes.optString("status");
                                    if ("OK".equalsIgnoreCase(status)) {
                                        JSONObject jsonEntity = jsonRes.optJSONObject("entity");
                                        if (jsonEntity != null){
                                            MPOrgEntity entitiesBean = new MPOrgEntity();
                                            entitiesBean.setName(jsonEntity.optString("orgName"));
                                            entitiesBean.setRank(jsonEntity.optString("rank"));
                                            entitiesBean.setParentId(jsonEntity.optInt("parentId"));
                                            entitiesBean.setId(jsonEntity.optInt("id"));
                                            entitiesBean.setTenantId(jsonEntity.optInt("tenantId"));
                                            AppHelper.getInstance().getModel().saveOrgInfo(entitiesBean);
                                        }
                                        finish();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    EMLog.e(TAG, "e:" + e.getMessage());
                                }
                            }

                            @Override
                            public void onError(int error, String errorMsg) {
                                EMLog.e(TAG, "error:" + errorMsg);
                            }
                        });
                    } else {
                        CreateDepartPostEntity createDepartPostEntity = new CreateDepartPostEntity(department.trim(), rank, orgInfo.getParentId());
                        //修改
                        EMAPIManager.getInstance().putOrgInfo(BaseRequest.getTenantId(), orgId, new Gson().toJson(createDepartPostEntity), new EMDataCallBack<String>() {
                            @Override
                            public void onSuccess(final String value) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideProgressDialog();
//                                        CreateDepartResultBean resultBean = null;
                                        CreateDepartResultEntity resultEntity = null;
                                        try {
                                            resultEntity = new Gson().fromJson(value, CreateDepartResultEntity.class);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (resultEntity != null) {
                                            String status = resultEntity.getStatus();
                                            if ("OK".equals(status)) {
                                                CreateDepartResultEntity.EntityBean entity = resultEntity.getEntity();
                                                if (entity != null) {
                                                    MPOrgEntity entitiesBean = new MPOrgEntity();
                                                    entitiesBean.setName(entity.getOrgName());
                                                    entitiesBean.setRank(entity.getRank());
                                                    entitiesBean.setParentId(entity.getParentId());
                                                    entitiesBean.setId(entity.getId());
                                                    entitiesBean.setTenantId(entity.getTenantId());
                                                    AppHelper.getInstance().getModel().saveOrgInfo(entitiesBean);
                                                    setResult(RESULT_CODE_UPDATE);
                                                    finish();
                                                } else {
                                                    toastInvalidResponse(TAG, "entity = null");
                                                }
                                            } else {
                                                toastInvalidResponse(TAG, "status = " + status);
                                            }
                                        } else {
                                            toastInvalidResponse(TAG, "resultBean = null");
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onError(int error, String errorMsg) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideProgressDialog();
                                    }
                                });

                            }
                        });
                    }
                }
            }
        });
    }

    private void initData() {
        orgId = getIntent().getIntExtra("orgId", -1);
        String title = getIntent().getStringExtra("title");
        if (TextUtils.isEmpty(title)) {
            if (orgId != -1) {
                MPOrgEntity orgInfo = AppHelper.getInstance().getModel().getOrgInfo(orgId);
                if (orgInfo != null && !TextUtils.isEmpty(orgInfo.getName())) {
                    mEtDepartment.setText(orgInfo.getName());
                    mEtDepartment.setSelection(orgInfo.getName().length());
                }
            }
        } else {
            isAdd = true;
            mTvTitle.setText(title);
        }
    }

}
