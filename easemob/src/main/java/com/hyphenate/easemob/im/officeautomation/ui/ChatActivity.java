package com.hyphenate.easemob.im.officeautomation.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.ui.EaseChatFragment;
import com.hyphenate.easemob.im.mp.location.LatLngManager;
import com.hyphenate.easemob.im.mp.location.LocServiceManager;
import com.hyphenate.easemob.im.mp.utils.FileObserverUtils;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.fragment.ChatFragment;
import com.hyphenate.easemob.im.officeautomation.runtimepermissions.PermissionsManager;
import com.hyphenate.easemob.im.officeautomation.utils.MyToast;
import com.mylhyl.circledialog.CircleDialog;

import java.util.ArrayList;
import java.util.Objects;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

/**
 * chat activity，EaseChatFragment was used
 */
public class ChatActivity extends BaseActivity {
    private static final String TAG = "ChatActivity";
    public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    String toChatUsername;
    private static final int REQUEST_CODE_PERMISSION_FILE = 100;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_chat);
        activityInstance = this;
        //get user id or group id
        toChatUsername = Objects.requireNonNull(getIntent().getExtras()).getString("userId");
        //use EaseChatFratFragment
        chatFragment = new ChatFragment();
        //pass parameters to chat fragment
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();

        PermissionGen.with(this).addRequestCode(REQUEST_CODE_PERMISSION_FILE)
                .permissions(Manifest.permission.RECORD_AUDIO).request();
        initListener();
    }

    private void initListener(){
        FileObserverUtils.getInstance().setSnapShotCallBack(new MySnapShotCallback());
    }

    class MySnapShotCallback implements FileObserverUtils.ISnapShotCallBack {

        @Override
        public void onSnapShotCreate(String path) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alertSnapshotDialog();
                }
            });
        }
    }

    private void dismissDialog(){
        if (dialogFragment != null) {
            dialogFragment.dismissAllowingStateLoss();
        }
    }
    private DialogFragment dialogFragment;

    private void alertSnapshotDialog () {
        dialogFragment = new CircleDialog.Builder()
                .setGravity(Gravity.CENTER)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setText("聊天记录截屏 仅限 内部传播，请勿外传！")
                .setPositive("我知道了", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show(getSupportFragmentManager());
    }

    @Override
    protected void onResume() {
        super.onResume();
        FileObserverUtils.getInstance().startSnapshotWatching();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FileObserverUtils.getInstance().stopSnapshotWatching();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
        FileObserverUtils.getInstance().setSnapShotCallBack(null);
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // make sure only one chat activity is opened
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username)) {
            super.onNewIntent(intent);
            String selectedMsgId = intent.getStringExtra(EaseConstant.EXTRA_SELECTED_MSGID);
            if (selectedMsgId != null) {
                chatFragment.refreshToMessage(selectedMsgId);
            }
        } else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
        if (!LocServiceManager.getInstance().isStarted()) {
            LatLngManager.getInstance().removeUser(toChatUsername, EMClient.getInstance().getCurrentUser());
            LocServiceManager.getInstance().stopLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        chatFragment.onActivityResult(requestCode, resultCode, data);
    }

    public String getToChatUsername() {
        return toChatUsername;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    //权限申请成功
    @PermissionSuccess(requestCode = REQUEST_CODE_PERMISSION_FILE)
    public void doSomething() {
        //在这个方法中做一些权限申请成功的事情
    }
    //申请失败
    @PermissionFail(requestCode = REQUEST_CODE_PERMISSION_FILE)
    public void doFailSomething() {
        MyToast.showInfoToast("需要在设置中开启权限");
    }


    /**
     * 保存MyTouchListener接口的列表
     */
    private ArrayList<MyTouchListener> myTouchListeners = new ArrayList<>();

    /**
     * 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法
     */
    public void registerMyTouchListener(MyTouchListener listener) {
        myTouchListeners.add(listener);
    }

    /**
     * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
     */
    public void unRegisterMyTouchListener(MyTouchListener listener) {
        myTouchListeners.remove(listener);
    }

    public void refresh(){
        if (chatFragment != null){
            chatFragment.refreshMessageListSelectLast();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyTouchListener listener : myTouchListeners) {
            listener.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public interface MyTouchListener {
        /**
         * onTOuchEvent的实现
         */
        void onTouchEvent(MotionEvent event);
    }
}
