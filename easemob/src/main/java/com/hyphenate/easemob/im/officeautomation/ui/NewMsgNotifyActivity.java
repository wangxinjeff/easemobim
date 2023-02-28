package com.hyphenate.easemob.im.officeautomation.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.im.mp.AppModel;
import com.hyphenate.easemob.R;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * @Author qby
 * @Time 2018/06/05 14:31:00
 * @Content
 **/
public class NewMsgNotifyActivity extends BaseActivity {

    private SwitchButton mNewNotify, mNotifyDetails;
    private AppModel appModel;
    private ImageView ivBack;
    private SwitchButton mSbVoice, mSbShake;
    private LinearLayout llContainer;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(getLayout());
        initViews();
        initListeners();
        initData();
    }

    private int getLayout() {
        return R.layout.activity_notify_msg_new;
    }

    private void initViews() {
        ivBack = $(R.id.iv_back);
        mNewNotify = findViewById(R.id.sb_new_notify);
        mNotifyDetails = findViewById(R.id.sb_notify_details);
        mSbVoice = findViewById(R.id.sb_voice);
        mSbShake = findViewById(R.id.sb_shake);
        llContainer = findViewById(R.id.ll_container);

        appModel = AppHelper.getInstance().getModel();
    }

    private void initListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    private void initData() {
        boolean isChecked = appModel.getSettingMsgNotification();
        mNewNotify.setChecked(isChecked);
        refreshContainer(isChecked);
        mNewNotify.setOnCheckedChangeListener((compoundButton, b) -> {
            appModel.setSettingMsgNotification(b);
            refreshContainer(b);
        });
        mNotifyDetails.setChecked(appModel.isShowNotifyDetails());
        mNotifyDetails.setOnCheckedChangeListener((compoundButton, b) -> appModel.setShowNotifyDetails(b));
        mSbVoice.setChecked(appModel.getSettingMsgSound());
        mSbShake.setChecked(appModel.getSettingMsgVibrate());
        mSbVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                appModel.setSettingMsgSound(isChecked);
            }
        });
        mSbShake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                appModel.setSettingMsgVibrate(isChecked);
            }
        });

    }


    private void refreshContainer(boolean isChecked){
        if (isChecked){
            llContainer.setVisibility(View.VISIBLE);
        }else{
            llContainer.setVisibility(View.INVISIBLE);
        }
    }

}
