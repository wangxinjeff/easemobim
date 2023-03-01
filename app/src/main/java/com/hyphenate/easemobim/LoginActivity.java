package com.hyphenate.easemobim;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.im.mp.manager.NoDisturbManager;
import com.hyphenate.easemob.im.officeautomation.domain.NoDisturbEntity;
import com.hyphenate.easemob.im.officeautomation.ui.MainActivity;
import com.hyphenate.easemob.im.officeautomation.utils.CommonUtils;
import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        findViewById(R.id.btn_login).setOnClickListener(v -> {
            AppHelper.getInstance().login("wangxin", "123456", new EMCallBack() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() ->{
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    });
                }

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        });
    }

    private void getGroupsDisturb() {
        EMAPIManager.getInstance().getGroupsDisturb(0, 100, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject result = new JSONObject(value);
                    JSONArray entities = result.getJSONArray("entities");
                    for (int i = 0; i < entities.length(); i++) {
                        JSONObject entity = entities.getJSONObject(i);
                        int chatGroupId = entity.optInt("chatGroupId");
                        boolean disturb = entity.optBoolean("disturb");
                        GroupBean groupBean = AppHelper.getInstance().getModel().getGroupInfoById(chatGroupId);
                        if (groupBean == null || !disturb) {
                            continue;
                        }
                        NoDisturbEntity disturbEntity = new NoDisturbEntity();
                        disturbEntity.setId(groupBean.getImGroupId());
                        disturbEntity.setLastUpdateTime(System.currentTimeMillis());
                        disturbEntity.setGroup(true);
                        disturbEntity.setName(groupBean.getNick());
                        NoDisturbManager.getInstance().saveNoDisturb(disturbEntity);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                Log.i("info", "error:" + errorMsg);
            }
        });
    }
}
