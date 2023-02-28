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
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.domain.CreateUserPostEntity;
import com.hyphenate.easemob.im.officeautomation.domain.CreateUserResultEntity;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.http.BaseRequest;
import com.hyphenate.easemob.im.officeautomation.utils.MyToast;

/**
 * Created by qby on 2018/06/29.
 * 创建子部门
 */
public class CreateUserActivity extends BaseActivity {

    private static final String TAG = "CreateUserActivity";
    private ImageView iv_back;
    private TextView tv_save;
    private EditText et_account;
    private EditText et_pwd;
    private EditText et_name;
    private EditText et_phone;
    private int orgId;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_create_user);
        setSwipeEnabled(false);
        initViews();
        initListeners();
        initData();
    }

    private void initViews() {
        iv_back = $(R.id.iv_back);
        tv_save = $(R.id.tv_save);
        et_account = $(R.id.et_account);
        et_pwd = $(R.id.et_pwd);
        et_name = $(R.id.et_name);
        et_phone = $(R.id.et_phone);
    }

    private void initListeners() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = et_account.getText().toString();
                String pwd = et_pwd.getText().toString();
                String name = et_name.getText().toString();
                String phone = et_phone.getText().toString().trim();

                // TODO. 此接口需要修改，暂无法完成使命 liyuzhao
                if (1 == 1) {
                    return;
                }


                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
                    MyToast.showToast(getString(R.string.please_complete_the_info));
                } else {
                    int tenantId = BaseRequest.getTenantId();
                    showProgressDialog();
                    EMAPIManager.getInstance().addUser(tenantId, new Gson().toJson(new CreateUserPostEntity(account, pwd, name, phone, tenantId, orgId)), new EMDataCallBack<String>() {
                        @Override
                        public void onSuccess(final String value) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressDialog();
                                    MPLog.d(TAG, value);
                                    CreateUserResultEntity resultBean = null;
                                    try {
                                        resultBean = new Gson().fromJson(value, CreateUserResultEntity.class);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (resultBean != null) {
                                        String status = resultBean.getStatus();
                                        if ("OK".equals(status)) {
                                            CreateUserResultEntity.EntityBean entity = resultBean.getEntity();
                                            if (entity != null) {
                                                MPUserEntity entitiesBean = new MPUserEntity();
                                                entitiesBean.setUsername(entity.getUsername());
                                                entitiesBean.setRealName(entity.getRealName());
                                                entitiesBean.setOrgId(entity.getOrganizationId());
                                                entitiesBean.setId(entity.getId());
                                                AppHelper.getInstance().getModel().saveUserInfo(entitiesBean);
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
        });
    }

    private void initData() {
        orgId = getIntent().getIntExtra("orgId", -1);
    }
}
