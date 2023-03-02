/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hyphenate.easemob.im.officeautomation.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;
import com.hyphenate.easemob.easeui.ui.EaseBaseActivity;
import com.hyphenate.easemob.easeui.widget.WaterMarkBgView;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.eventbus.Subscribe;
import com.hyphenate.eventbus.ThreadMode;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.im.mp.cache.TenantOptionCache;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.imlibs.mp.events.EventReLoginStart;
import com.hyphenate.easemob.imlibs.mp.events.EventReloginSuccess;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.domain.ExtUserType;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.utils.MyToast;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.jude.swipbackhelper.SwipeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qby on 2018/05/30 0001.
 * 基类，滑动关闭页面基类，使用时继承此类并使用BlankTheme主题即可
 */
@SuppressLint({"Registered"})
public abstract class BaseActivity extends EaseBaseActivity {

    private static final String TAG = "BaseActivity";
    protected BaseActivity activity;
    private ProgressDialog progressDialog;
    private ProgressDialog loginProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this).setSwipeEdge(50).addListener(new SwipeListener() {
            @Override
            public void onScroll(float v, int i) {
            }

            @Override
            public void onEdgeTouch() {
            }

            @Override
            public void onScrollToClose() {

                finishActivity();
            }
        });
        MPEventBus.getDefault().register(this);
        activity = this;
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    public void finishActivity() {

    }

    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            List<EMMessage> tempMessages = new ArrayList<>(messages);
            for (EMMessage cmdMessage : tempMessages) {
                if (cmdMessage.getType() != EMMessage.Type.CMD) {
                    continue;
                }

                EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) cmdMessage.getBody();
                final String action = cmdMsgBody.action();
                if (TextUtils.isEmpty(action)) {
                    return;
                }

                if (action.equals(Constant.CMD_ACTION_CONF_CANCEL)
                        || action.equals(Constant.CMD_ACTION_CLIENT_WHITE_BOARD)
                        || action.equals(Constant.CMD_ACTION_CLIENT_WHITE_BOARD_CLOSE)) {
                    return;
                }
                JSONObject content = null;
                int id = -1;
                try {
                    content = cmdMessage.getJSONObjectAttribute("content");
                    if (content == null) return;
                    id = content.getInt("id");
                    JSONObject user = content.optJSONObject("user");
                    if (user == null || EMClient.getInstance().getCurrentUser().equals(user.getString("imUsername"))) {
                        MPLog.e(TAG, "ignore self user remind");
                        return;
                    }
                } catch (HyphenateException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
        }

        @Override
        public void onMessageRecalled(List<EMMessage> messages) {

        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
        }
    };

    public void popupRemind(Context context, JSONObject json, String type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReloginStart(EventReLoginStart reLoginStart) {
        showLoginProgressDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReloginSuccess(EventReloginSuccess reloginSuccess) {
        hideLoginProgressDialog();
    }


//    public void onCheck(){
//        MyApplication application = (MyApplication) getApplication();
//        try {
//            Class clazz = Class.forName("com.hyphenate.easemob.im.officeautomation.ui.SplashActivity");
//            Class loginClazz = Class.forName("com.hyphenate.easemob.im.officeautomation.ui.LoginActivity");
//            if (application.getActivitySize() == 0 && !getClass().equals(clazz) && !getClass().equals(loginClazz)){
//                // 不是正常启动流程，则重回入口activity进入
//                Intent intent = new Intent(this, clazz);
//                if (null != getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)) {
//                    startActivity(new Intent(this, clazz));
//                } else {
//                    Log.e(TAG, "找不到指定的activity");
//                }
//                return;
//            }
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            Log.e(TAG, "error:" + e.getLocalizedMessage());
//        }
//    }

    /**
     * 通过xml查找相应的ID，通用方法
     */
    protected <T extends View> T $(@IdRes int id) {
        return (T) findViewById(id);
    }


    void refreshWaterMark(Activity activity) {
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if(loginUser == null) {
            MyToast.showInfoToast("登录信息已经过期，请重新登录！");
            AppHelper.getInstance().logout(false, new EMCallBack() {
                @Override
                public void onSuccess() {}

                @Override
                public void onError(int i, String s) {}

                @Override
                public void onProgress(int i, String s) {}
            });
            return;
        }
        String text = "";
        if (!TextUtils.isEmpty(loginUser.getAlias())) {
            text = loginUser.getAlias();
        } else if (!TextUtils.isEmpty(loginUser.getNickname())) {
            text = loginUser.getNickname();
        } else if (!TextUtils.isEmpty(loginUser.getEntityBean().getAlias())) {
            text = loginUser.getEntityBean().getAlias();
        } else if (!TextUtils.isEmpty(loginUser.getEntityBean().getRealName())) {
            text = loginUser.getEntityBean().getRealName();
        }
        if (TenantOptionCache.getInstance().isShowWaterMark()) {
            WaterMarkBgView.getInstance().setTextColor(0x33AEAEAE).show(activity, text);
        } else {
            WaterMarkBgView.getInstance().setTextColor(0x00000000).show(activity, text);
        }
    }

    /**
     * 显示加载框
     */
    public void showProgressDialog(String... promptContent) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.show();
        View inflate = View.inflate(activity, R.layout.dialog_loading, null);
        if (promptContent.length != 0)
            ((TextView) inflate.findViewById(R.id.tv_prompt)).setText(promptContent[0]);
        progressDialog.setContentView(inflate);
        progressDialog.setCancelable(true);
    }


    public void showLoginProgressDialog() {
        loginProgressDialog = new ProgressDialog(activity);
        loginProgressDialog.show();
        View inflate = View.inflate(activity, R.layout.dialog_loading, null);
        loginProgressDialog.setContentView(inflate);
        loginProgressDialog.setCanceledOnTouchOutside(false);
        loginProgressDialog.setCancelable(false);
    }

    public void hideLoginProgressDialog() {
        if (loginProgressDialog != null && loginProgressDialog.isShowing()) {
            loginProgressDialog.dismiss();
            loginProgressDialog = null;
        }
    }

    /**
     * 隐藏正在展示的加载框
     */
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    //弹出自定义异常提示
    protected void toastInvalidResponse(String tag, String message) {
        if (message != null) {
            MPLog.d(tag, message);
        }
//        ExceptionHelper.toastCustomException(activity, new InvalidResponseException());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SwipeBackHelper.onDestroy(this);
        MPEventBus.getDefault().unregister(this);
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        hideLoginProgressDialog();
        hideProgressDialog();
    }

    public void setSwipeEnabled(boolean swipeEnabled) {
        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(swipeEnabled);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    protected void addExtFieldForWeb(EMMessage message) {
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser != null) {
            ExtUserType extUserType = new ExtUserType(loginUser.getId(), loginUser.getNick(), loginUser.getAvatar());
            try {
                message.setAttribute(EaseConstant.EXT_USER_TYPE, new JSONObject(new Gson().toJson(extUserType)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 增加群成员
     *
     * @param newMembers
     */
    protected void addMembersToGroup(ArrayList<Integer> newMembers, MPGroupEntity groupEntity) {
        if (groupEntity == null) {
            return;
        }
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("userIdList", new JSONArray(newMembers));
            jsonObj.put("isRegion", groupEntity.isCluster() ? 1 : 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser == null) {
            return;
        }

        showProgressDialog();
        EMAPIManager.getInstance().addMembersToGroup(groupEntity.getId(), jsonObj.toString(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        if(groupEntity.getOwnerId() != MPClient.get().getCurrentUser().getId()) {
//                            MyToast.showInfoToast("已添加群成员，等待群主审批");
                        }
                        Intent intent = new Intent();
                        intent.putExtra("pickList", newMembers);
                        setResult(1000, intent);
                        finish();
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        hideProgressDialog();
                        final String st6 = getResources().getString(R.string.Add_group_members_fail);
//                            if (e.getErrorCode() == EMError.GROUP_MEMBERS_FULL){
//                                Toast.makeText(getApplicationContext(), st6 + getString(R.string.excced_the_maximux_group_size_limit), Toast.LENGTH_LONG).show();
//                            }else{
//                                Toast.makeText(getApplicationContext(), st6, Toast.LENGTH_LONG).show();
//                            }
                        Toast.makeText(getApplicationContext(), st6, Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    protected void fetchServiceConfig(){
        EMAPIManager.getInstance().getServiceConfig(new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try{
                    JSONObject jsonObject = new JSONObject(value);
                    String status = jsonObject.optString("status");
                    if("OK".equalsIgnoreCase(status)){
                        JSONArray entities = jsonObject.getJSONArray("entities");
                        for(int i = 0; i < entities.length(); i ++){
                            JSONObject data = entities.getJSONObject(i);
                            if (data != null){
                                String code = data.optString("code");
                                String configValue = data.optString("configValue");
                                if(TextUtils.equals(code, "msg_recall")){
                                    PreferenceUtils.getInstance().setRecallDuration(Long.parseLong(configValue));
                                } else if (TextUtils.equals(code, "read_ack")){
                                    PreferenceUtils.getInstance().setShowRead(Boolean.parseBoolean(configValue));
                                }  else if (TextUtils.equals(code, "voice_duration")){
                                    PreferenceUtils.getInstance().setVoiceDuration(Long.parseLong(configValue));
                                }
                            }
                        }
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

}
