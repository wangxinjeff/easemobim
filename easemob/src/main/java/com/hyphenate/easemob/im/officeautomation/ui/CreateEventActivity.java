package com.hyphenate.easemob.im.officeautomation.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.hyphenate.easemob.easeui.widget.EaseTitleBar;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.domain.CreateEventPostEntity;
import com.hyphenate.easemob.im.officeautomation.domain.CreateEventResultEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.http.BaseRequest;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.officeautomation.ui.SetEventReminderActivity;
import com.hyphenate.easemob.im.officeautomation.ui.SetEventRepeatActivity;
import com.hyphenate.easemob.im.officeautomation.utils.MyToast;
import com.kyleduo.switchbutton.SwitchButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author qby
 * @date 2018/7/10 11:24
 * 创建日程
 */
public class CreateEventActivity extends BaseActivity {

    private static final String TAG = "CreateEventActivity";
    private static final int REQUEST_CODE_SET_REMINDER = 1;
    private static final int REQUEST_CODE_SET_REPEAT = 3;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    //    private SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd,yyyy", Locale.ENGLISH);
    private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
    //    private SimpleDateFormat sdf4 = new SimpleDateFormat("MMM dd,yyyy HH:mm", Locale.ENGLISH);
    private SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.CHINA);
    private String[] normalList;
    private String[] allDayList;
    private String[] repeatList;
    private EaseTitleBar title_bar;
    private EditText et_content;
    private RelativeLayout rl_all_day;
    private SwitchButton sb_all_day;
    private LinearLayout ll_start;
    private TextView tv_start_date;
    private TextView tv_start_time;
    private LinearLayout ll_end;
    private TextView tv_end_date;
    private TextView tv_end_time;
    private RelativeLayout rl_remain;
    private TextView tv_remain_time;
    private RelativeLayout rl_repeat;
    private TextView tv_repeat_style;
    private RelativeLayout rl_deadline;
    private SwitchButton sb_deadline;
    private LinearLayout ll_deadline;
    private TextView tv_deadline_date;
    private TextView tv_deadline_time;
    private EditText et_notes;

    //是否全天
    private boolean isAllDay;
    //是否有截止时间
    private boolean hasDeadline;
    //开始时间
    private String startDate;
    private String startTime;
    private String startAllTime = "00:00";
    private String startWeekDay;
    private long startLongTime;
    //结束时间
    private String endDate;
    private String endTime;
    private String endAllTime = "23:59";
    private String endWeekDay;
    private long endLongTime;
    //截止时间
    private String deadDate;
    private String deadTime;
    private String deadAllTime = "23:59";
    private String deadWeekDay;
    private long deadLongTime;
    //当前时间
    private String currentDate;
    private String currentTime;
    private Calendar calendar;
    private long currentLongTime;
    //时间间隔
    private long interval;
    //提醒时间，重复方式
    private String time_reminder;
    private String time_reminder_all;
    private String repeat_style;
    //选中的提醒、重复
    private int selectReminder;
    private int selectAllReminder;
    private int selectRepeat;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_create_event);
        setSwipeEnabled(false);
        initViews();
        initListeners();
        initData();
    }

    private void initViews() {
        title_bar = findViewById(R.id.title_bar);
        et_content = findViewById(R.id.et_content);
        rl_all_day = findViewById(R.id.rl_all_day);
        sb_all_day = findViewById(R.id.sb_all_day);
        ll_start = findViewById(R.id.ll_start);
        tv_start_date = findViewById(R.id.tv_start_date);
        tv_start_time = findViewById(R.id.tv_start_time);
        ll_end = findViewById(R.id.ll_end);
        tv_end_date = findViewById(R.id.tv_end_date);
        tv_end_time = findViewById(R.id.tv_end_time);
        rl_remain = findViewById(R.id.rl_remain);
        tv_remain_time = findViewById(R.id.tv_remain_time);
        rl_repeat = findViewById(R.id.rl_repeat);
        tv_repeat_style = findViewById(R.id.tv_repeat_style);
        rl_deadline = findViewById(R.id.rl_deadline);
        sb_deadline = findViewById(R.id.sb_deadline);
        ll_deadline = findViewById(R.id.ll_deadline);
        tv_deadline_date = findViewById(R.id.tv_deadline_date);
        tv_deadline_time = findViewById(R.id.tv_deadline_time);
        et_notes = findViewById(R.id.et_notes);
    }

    private void initListeners() {
        title_bar.setLeftLayoutClickListener(view -> finish());
        title_bar.setRightLayoutClickListener(view -> {
            //点击保存
            submitSave();
        });
        sb_all_day.setOnCheckedChangeListener((compoundButton, b) -> {
            switchAllDay(b);
        });
        rl_all_day.setOnClickListener(view -> {
            boolean checked = !sb_all_day.isChecked();
            sb_all_day.setChecked(checked);
        });
        rl_remain.setOnClickListener(view -> {
            //跳转设置提醒时间页面
            startActivityForResult(new Intent(activity, SetEventReminderActivity.class).putExtra("isAllDay", isAllDay).putExtra("select_reminder", isAllDay ? selectAllReminder : selectReminder), REQUEST_CODE_SET_REMINDER);
        });
        rl_repeat.setOnClickListener(view -> {
            //跳转设置是否重复页面
            startActivityForResult(new Intent(activity, SetEventRepeatActivity.class).putExtra("select_repeat", selectRepeat), REQUEST_CODE_SET_REPEAT);
        });
        sb_deadline.setOnCheckedChangeListener((compoundButton, b) -> {
            hasDeadline = b;
            if (hasDeadline) {
                ll_deadline.setVisibility(View.VISIBLE);
            } else {
                ll_deadline.setVisibility(View.GONE);
            }
        });
        rl_deadline.setOnClickListener(view -> {
            boolean checked = !sb_deadline.isChecked();
            sb_deadline.setChecked(checked);
        });
        ll_start.setOnClickListener(view -> {
            //选择开始时间
            final DatePickerDialog datePickerDialog = new DatePickerDialog(activity, (datePicker, year, month, day) -> {
                month += 1;
                startDate = getDate(year, month, day);
                tv_start_date.setText(startDate);
                startLongTime = getLongTime(startDate + " " + (isAllDay ? startAllTime : startTime));
                initEndDateTime();
                initDeadDateTime();
                if (isAllDay) {
                    startWeekDay = getDayOfWeek(startLongTime);
                    tv_start_time.setText(startWeekDay);
                } else {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                            startTime = getTime(hourOfDay, minute);
                            tv_start_time.setText(startTime);
                            startLongTime = getLongTime(startDate + " " + startTime);
                            initEndDateTime();
                            initDeadDateTime();
                        }
                    }, getWhich(startLongTime, 3), getWhich(startLongTime, 4), true);
                    timePickerDialog.show();
                }
            }, getWhich(startLongTime, 0), getWhich(startLongTime, 1) - 1, getWhich(startLongTime, 2));
            datePickerDialog.show();
        });
        ll_end.setOnClickListener(view -> {
            //选择结束时间
            final DatePickerDialog datePickerDialog = new DatePickerDialog(activity, (datePicker, year, month, day) -> {
                month += 1;
                String tmpDate = getDate(year, month, day);
                long tmpLongTime = getLongTime(tmpDate + " " + (isAllDay ? endAllTime : endTime));
                if (tmpLongTime < startLongTime) {
                    MyToast.showToast(getString(R.string.end_time_cannot_earlier_than_start_time));
                    return;
                }
                endDate = tmpDate;
                tv_end_date.setText(endDate);
                endLongTime = tmpLongTime;
                getInterval();
                if (isAllDay) {
                    endWeekDay = getDayOfWeek(endLongTime);
                    tv_end_time.setText(endWeekDay);
                    initDeadDateTime();
                } else {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                            String tmpTime = getTime(hourOfDay, minute);
                            long tmpLongTime = getLongTime(endDate + " " + tmpTime);
                            if (tmpLongTime < startLongTime) {
                                MyToast.showToast(getString(R.string.end_time_cannot_earlier_than_start_time));
                                return;
                            }
                            endTime = tmpTime;
                            tv_end_time.setText(endTime);
                            endLongTime = tmpLongTime;
                            getInterval();
                            initDeadDateTime();
                        }
                    }, getWhich(endLongTime, 3), getWhich(endLongTime, 4), true);
                    timePickerDialog.show();
                }
            }, getWhich(endLongTime, 0), getWhich(endLongTime, 1) - 1, getWhich(endLongTime, 2));
            datePickerDialog.show();
        });
        ll_deadline.setOnClickListener(view -> {
            //选择截止时间
            final DatePickerDialog datePickerDialog = new DatePickerDialog(activity, (datePicker, year, month, day) -> {
                month += 1;
                String tmpDate = getDate(year, month, day);
                long tmpLongTime = getLongTime(tmpDate + " " + (isAllDay ? deadAllTime : deadTime));
                if (tmpLongTime < startLongTime) {
                    MyToast.showToast(getString(R.string.deadline_cannot_earlier_than_start_time));
                    return;
                }
                deadDate = tmpDate;
                tv_deadline_date.setText(deadDate);
                deadLongTime = tmpLongTime;
                if (isAllDay) {
                    deadWeekDay = getDayOfWeek(deadLongTime);
                    tv_deadline_time.setText(deadWeekDay);
                } else {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                            String tmpTime = getTime(hourOfDay, minute);
                            long tmpLongTime = getLongTime(deadDate + " " + tmpTime);
                            if (tmpLongTime < startLongTime) {
                                MyToast.showToast(getString(R.string.deadline_cannot_earlier_than_start_time));
                                return;
                            }
                            deadTime = tmpTime;
                            tv_deadline_time.setText(deadTime);
                            deadLongTime = tmpLongTime;
                        }
                    }, getWhich(deadLongTime, 3), getWhich(deadLongTime, 4), true);
                    timePickerDialog.show();
                }
            }, getWhich(deadLongTime, 0), getWhich(deadLongTime, 1) - 1, getWhich(deadLongTime, 2));
            datePickerDialog.show();
        });
    }

    //提交保存
    private void submitSave() {
        String content = et_content.getText().toString();
        String notes = et_notes.getText().toString();
        if (TextUtils.isEmpty(content)) {
            MyToast.showToast(getString(R.string.event_content_empty));
            return;
        }
        showProgressDialog();
        CreateEventPostEntity createEventPostEntity = new CreateEventPostEntity(content, String.valueOf(startLongTime), String.valueOf(endLongTime), hasDeadline ? String.valueOf(deadLongTime) : null, selectRepeat, (isAllDay ? 1 : 0), isAllDay ? selectAllReminder : selectReminder, notes);
//        CreateEventPostBean createEventPostBean = new CreateEventPostBean(content, String.valueOf(startLongTime), String.valueOf(endLongTime), hasDeadline ? String.valueOf(deadLongTime) : null, selectRepeat, (isAllDay ? 1 : 0), isAllDay ? selectAllReminder : selectReminder, notes);
        EMAPIManager.getInstance().postSchedule(BaseRequest.getTenantId(), new Gson().toJson(createEventPostEntity), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(final String value) {
                if (isFinishing()) return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        CreateEventResultEntity createEventResultEntity = new Gson().fromJson(value, CreateEventResultEntity.class);
//                        CreateEventResultBean createEventResultBean = new Gson().fromJson(value, CreateEventResultBean.class);
                        if (createEventResultEntity != null) {
                            String status = createEventResultEntity.getStatus();
                            if ("OK".equalsIgnoreCase(status)) {
                                //保存在本地
                                finish();
                            } else {
                                toastInvalidResponse(TAG, "status = " + status);
                            }
                        } else {
                            toastInvalidResponse(TAG, "createEventResultBean = null");
                        }
                    }
                });

            }

            @Override
            public void onError(int error, String errorMsg) {
                if (isFinishing()) return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                    }
                });
            }
        });

//        Map<String, String> headers = new HashMap<>();
//        headers.put(Constant.CONTENT_TYPE_KEY, Constant.CONTENT_TYPE_VALUE);
//        headers.put(Constant.COOKIE_KEY, PreferenceManager.getInstance().getLoginTokenKey() + "=" + PreferenceManager.getInstance().getLoginTokenValue());
//        RequestBody eventInfo = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), new Gson().toJson(createEventPostBean));
//        createEventCall = HttpUtil.getInstance().createEvent(headers, PreferenceManager.getInstance().getTenantId(), eventInfo);
//        createEventCall.enqueue(createCallback);
    }

    private void initData() {
        normalList = new String[]{
                getString(R.string.time_not_remind),
                getString(R.string.time_work_start),
                getString(R.string.time_before_5),
                getString(R.string.time_before_15),
                getString(R.string.time_before_30),
                getString(R.string.time_before_hour_1),
                getString(R.string.time_before_day_1)
        };
        allDayList = new String[]{
                getString(R.string.time_not_remind),
                getString(R.string.time_work_start),
                getString(R.string.time_all_before_day_1),
                getString(R.string.time_all_before_day_2),
                getString(R.string.time_all_before_week_1)
        };
        repeatList = new String[]{
                getString(R.string.no_repeat),
                getString(R.string.repeat_day),
                getString(R.string.repeat_week),
                getString(R.string.repeat_week_2),
                getString(R.string.repeat_month),
                getString(R.string.repeat_year)
        };
        calendar = Calendar.getInstance();
        initDateTime();
        selectReminder = selectAllReminder = 1;
        selectRepeat = 0;
        time_reminder_all = time_reminder = getString(R.string.time_work_start);
        repeat_style = getString(R.string.no_repeat);
        tv_remain_time.setText(time_reminder);
        tv_repeat_style.setText(repeat_style);
    }

    //初始化日期
    private void initDateTime() {
        getInterval();
        initCurrentDateTime();
        initStartDateTime();
        initEndDateTime();
        initDeadDateTime();
    }

    //初始化当前时间
    private void initCurrentDateTime() {
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        currentMonth += 1;
        currentDate = getDate(currentYear, currentMonth, currentDay);
        currentTime = getTime(currentHour, currentMinute);
        currentLongTime = getLongTime(currentDate + " " + currentTime);
        MPLog.e(TAG, "current:currentLongTime=" + currentLongTime + ":" + currentDate + currentTime);
    }


    //初始化开始时间
    private void initStartDateTime() {
        startDate = currentDate;
        startTime = currentTime;
        startLongTime = currentLongTime;
        tv_start_date.setText(startDate);
        startWeekDay = getDayOfWeek(startLongTime);
        tv_start_time.setText(isAllDay ? startWeekDay : startTime);
        MPLog.e(TAG, "start:startLongTime=" + startLongTime + ":" + startDate + startTime);
    }

    //初始化结束时间
    private void initEndDateTime() {
        endLongTime = startLongTime + interval;
        endDate = getDate(getWhich(endLongTime, 0), getWhich(endLongTime, 1), getWhich(endLongTime, 2));
        endTime = getTime(getWhich(endLongTime, 3), getWhich(endLongTime, 4));
        tv_end_date.setText(endDate);
        endWeekDay = getDayOfWeek(endLongTime);
        tv_end_time.setText(isAllDay ? endWeekDay : endTime);
        MPLog.e(TAG, "end:endLongTime=" + endLongTime + ":" + endDate + endTime);
    }

    //初始化截止时间
    private void initDeadDateTime() {
        deadDate = endDate;
        deadTime = endTime;
        deadLongTime = endLongTime;
        tv_deadline_date.setText(deadDate);
        deadWeekDay = getDayOfWeek(deadLongTime);
        tv_deadline_time.setText(isAllDay ? deadWeekDay : deadTime);
        MPLog.e(TAG, "dead:" + deadDate + deadTime);
    }

    //获取“2018年07月10日”格式时间
    private String getDate(int year, int month, int day) {
        String monthStr = String.valueOf(month);
        String dayStr = String.valueOf(day);
        if (month < 10)
            monthStr = "0" + month;
        if (day < 10)
            dayStr = "0" + day;
        StringBuilder date = new StringBuilder();
        date.append(year).append("年").append(monthStr).append("月").append(dayStr).append("日");
        String format = date.toString();
//        if ("en".equals(Locale.getDefault().getLanguage())) {
//            try {
//                Date parse = sdf.parse(date.toString());
//                format = sdf2.format(parse);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
        return format;
    }

    //获取“22:01”格式时间
    private String getTime(int hour, int minute) {
        String hourStr = String.valueOf(hour);
        String minStr = String.valueOf(minute);
        if (hour < 10) {
            hourStr = "0" + hourStr;
        }
        if (minute < 10) {
            minStr = "0" + minStr;
        }
        return hourStr + ":" + minStr;
    }

    //根据 “2018年07月10日 22:01”转为long类型时间
    private long getLongTime(String dateTime) {
        try {
            Date parse = sdf3.parse(dateTime);
//            if ("en".equals(Locale.getDefault().getLanguage())) {
//                parse = sdf4.parse(dateTime);
//            }
            return parse.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getWhich(long dateTime, int index) {
        String format = sdf5.format(new Date(dateTime));
        String[] split = format.split("-");
        if (split.length < index + 1) {
            return 0;
        }
        return Integer.parseInt(split[index]);
    }

    //返回周*
    private String getDayOfWeek(long dateTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(dateTime));
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        String dayStr = "";
        if (dayOfWeek == 1) {
            dayStr = "周日";
        } else if (dayOfWeek == 2) {
            dayStr = "周一";
        } else if (dayOfWeek == 3) {
            dayStr = "周二";
        } else if (dayOfWeek == 4) {
            dayStr = "周三";
        } else if (dayOfWeek == 5) {
            dayStr = "周四";
        } else if (dayOfWeek == 6) {
            dayStr = "周五";
        } else if (dayOfWeek == 7) {
            dayStr = "周六";
        }
        return dayStr;
    }

    //获取结束时间与开始时间间隔
    private void getInterval() {
        if (startLongTime > 0 && endLongTime > 0) {
            interval = endLongTime - startLongTime;
        } else {
            interval = 60 * 60 * 1000;
        }
    }

    //切换是否全天
    private void switchAllDay(boolean isAllDay) {
        this.isAllDay = isAllDay;
        //切换全天时，开始、结束、截止时间
        String start = isAllDay ? startAllTime : startTime;
        startLongTime = getLongTime(startDate + " " + start);
        String end = isAllDay ? endAllTime : endTime;
        endLongTime = getLongTime(startDate + " " + end);
        String dead = isAllDay ? deadAllTime : deadTime;
        deadLongTime = getLongTime(deadDate + " " + dead);
        //设置开始、结束、截止时间
        tv_start_time.setText(isAllDay ? startWeekDay : startTime);
        tv_end_time.setText(isAllDay ? endWeekDay : endTime);
        tv_deadline_time.setText(isAllDay ? deadWeekDay : deadTime);
        //设置提醒时间，重复方式
        tv_remain_time.setText(isAllDay ? time_reminder_all : time_reminder);
        tv_repeat_style.setText(repeat_style);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SET_REMINDER) {
            if (resultCode == SetEventReminderActivity.RESULT_CODE_REMINDER) {
                int reminder_code = data.getIntExtra("reminder_code", 0);
                if (isAllDay) {
                    selectAllReminder = reminder_code;
                    time_reminder_all = allDayList[selectAllReminder];
                    tv_remain_time.setText(time_reminder_all);
                } else {
                    selectReminder = reminder_code;
                    time_reminder = normalList[selectReminder];
                    tv_remain_time.setText(time_reminder);
                }
            }
        } else if (requestCode == REQUEST_CODE_SET_REPEAT) {
            if (resultCode == SetEventRepeatActivity.RESULT_CODE_REPEAT) {
                selectRepeat = data.getIntExtra("repeat_code", 0);
                repeat_style = repeatList[selectRepeat];
                tv_repeat_style.setText(repeat_style);
            }
        }
    }

}
