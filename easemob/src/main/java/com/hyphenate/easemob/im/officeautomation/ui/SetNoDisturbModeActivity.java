package com.hyphenate.easemob.im.officeautomation.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.utils.MyToast;
import com.hyphenate.easemob.im.officeautomation.widget.NumberPickerDialog;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * @Author qby
 * @Time 2018/06/05 16:53:00
 * @Content 设置免打扰模式
 **/
public class SetNoDisturbModeActivity extends BaseActivity {

    private static final String TAG = "SetNoDisturbModeActivit";
    private ImageView ivBack;
    //是否设置消息免打扰
    private SwitchButton mNoDisturb;
    //推送设置
    private EMPushConfigs mPushConfigs;
    //免打扰模式开始时间、结束时间
    private RelativeLayout mRlTimeBegin, mRlTimeEnd;
    private TextView mTvNextDay, mTvTimeBegin, mTvTimeEnd;
    private LinearLayout ll_time;
    private int noDisturbStartHour = 22, noDisturbEndHour = 7;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(getLayout());
        initViews();
        initListeners();
        initData();
    }

    private int getLayout() {
        return R.layout.activity_setting_no_disturb_mode;
    }

    private void initViews() {
        ivBack = $(R.id.iv_back);
        mNoDisturb = findViewById(R.id.sb_no_disturb);
        mRlTimeBegin = findViewById(R.id.rl_no_disturb_time_begin);
        mRlTimeEnd = findViewById(R.id.rl_no_disturb_time_end);
        mTvNextDay = findViewById(R.id.tv_next_day);
        mTvTimeBegin = findViewById(R.id.tv_begin_time);
        mTvTimeEnd = findViewById(R.id.tv_end_time);
        ll_time = findViewById(R.id.ll_time);

        mPushConfigs = EMClient.getInstance().pushManager().getPushConfigs();
        if (mPushConfigs == null) {
            final ProgressDialog loadingPd = new ProgressDialog(this);
            loadingPd.setMessage("loading");
            loadingPd.setCanceledOnTouchOutside(false);
            loadingPd.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mPushConfigs = EMClient.getInstance().pushManager().getPushConfigsFromServer();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingPd.dismiss();
                                processPushConfigs();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingPd.dismiss();
                            }
                        });
                    }
                }
            }).start();
        } else {
            processPushConfigs();
        }
    }

    private void initListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRlTimeBegin.setOnClickListener(view -> {

            NumberPickerDialog numberPickerDialog = new NumberPickerDialog(activity, new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    noDisturbStartHour = newVal;
                    new Thread(() -> {
                        try {
                            EMClient.getInstance().pushManager().disableOfflinePush(noDisturbStartHour, noDisturbEndHour);
                            runOnUiThread(() -> {
                                //是否显示次日
                                mTvNextDay.setVisibility(noDisturbEndHour <= noDisturbStartHour ? View.VISIBLE : View.GONE);
                                mTvTimeBegin.setText(getStringTime(noDisturbStartHour));
                            });
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> MyToast.showToast(getString(R.string.push_save_failed)));
                        }
                    }).start();
                }
            }, 23, 0, noDisturbStartHour);
            numberPickerDialog.show();
        });

        mRlTimeEnd.setOnClickListener(view -> {
            NumberPickerDialog numberPickerDialog = new NumberPickerDialog(activity, new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    noDisturbEndHour = newVal;
                    new Thread(() -> {
                        try {
                            EMClient.getInstance().pushManager().disableOfflinePush(noDisturbStartHour, noDisturbEndHour);
                            runOnUiThread(() -> {
                                //是否显示次日
                                mTvNextDay.setVisibility(noDisturbEndHour <= noDisturbStartHour ? View.VISIBLE : View.GONE);
                                mTvTimeEnd.setText(getStringTime(noDisturbEndHour));
                            });
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> MyToast.showToast(getString(R.string.push_save_failed)));
                        }
                    }).start();

                }
            }, 23, 0, noDisturbEndHour);
            numberPickerDialog.show();

        });
    }


    private void initData() {
        mNoDisturb.setOnCheckedChangeListener((compoundButton, b) -> {
            final ProgressDialog savingPd = new ProgressDialog(SetNoDisturbModeActivity.this);
            savingPd.setMessage(getString(R.string.push_saving_settings));
            savingPd.setCanceledOnTouchOutside(false);
            savingPd.show();
            new Thread(() -> {
                try {
                    if (!b) {
                        EMClient.getInstance().pushManager().enableOfflinePush();
                    } else {
                        EMClient.getInstance().pushManager().disableOfflinePush(noDisturbStartHour, noDisturbEndHour);
                    }
                    mPushConfigs = EMClient.getInstance().pushManager().getPushConfigs();
                    runOnUiThread(() -> {
                        ll_time.setVisibility(b ? View.VISIBLE : View.GONE);
                        //是否显示次日
                        mTvNextDay.setVisibility(noDisturbEndHour <= noDisturbStartHour ? View.VISIBLE : View.GONE);
                        savingPd.dismiss();
                        processPushConfigs();
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        savingPd.dismiss();
                        MyToast.showToast(getString(R.string.push_save_failed));
                    });
                }
            }).start();
        });
    }

    private void processPushConfigs() {
        if (mPushConfigs == null)
            return;
        mNoDisturb.setChecked(mPushConfigs.isNoDisturbOn());
        //如果开启勿扰模式，则不显示时间
        ll_time.setVisibility(mPushConfigs.isNoDisturbOn() ? View.VISIBLE : View.GONE);
        //设置时间显示
        noDisturbStartHour = mPushConfigs.getNoDisturbStartHour() < 0 ? 22 : mPushConfigs.getNoDisturbStartHour();
        noDisturbEndHour = mPushConfigs.getNoDisturbEndHour() < 0 ? 7 : mPushConfigs.getNoDisturbEndHour();
//        MyLog.e(TAG, "noDisturbStartHour=" + noDisturbStartHour + ";noDisturbEndHour=" + noDisturbEndHour);
        //是否显示次日
        mTvNextDay.setVisibility(noDisturbEndHour <= noDisturbStartHour ? View.VISIBLE : View.GONE);
        mTvTimeBegin.setText(getStringTime(noDisturbStartHour));
        mTvTimeEnd.setText(getStringTime(noDisturbEndHour));
    }

    //获取String类型时间
    private String getStringTime(int time) {
        StringBuilder strTime = new StringBuilder();
        if (time < 10) {
            strTime.append("0").append(time).append(":00");
        } else {
            strTime.append(time).append(":00");
        }
        return strTime.toString();
    }
}
