package com.hyphenate.easemob.im.officeautomation.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easemob.easeui.widget.EaseTitleBar;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.adapter.EventAdapter;
import com.hyphenate.easemob.im.officeautomation.widget.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author qby
 * @date 2018/7/11 14:59
 * 设置提醒时间
 */
public class SetEventReminderActivity extends BaseActivity {

    private static final String TAG = "SetEventReminderActivit";
    public static final int RESULT_CODE_REMINDER = 2;
    private EaseTitleBar title_bar;
    private RecyclerView rv;
    private int selectCode;
    private EventAdapter adapter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_set_event_reminder);
        initViews();
        initListeners();
        initData();
    }

    //初始化控件
    private void initViews() {
        title_bar = findViewById(R.id.title_bar);
        rv = findViewById(R.id.rv);

    }

    //初始化监听
    private void initListeners() {
        title_bar.setLeftLayoutClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("reminder_code", selectCode);
            setResult(RESULT_CODE_REMINDER, intent);
            finish();
        });
    }

    //初始化数据
    private void initData() {
        selectCode = getIntent().getIntExtra("select_reminder", 0);
        boolean isAllDay = getIntent().getBooleanExtra("isAllDay", false);
        String[] normalList = new String[]{
                getString(R.string.time_not_remind),
                getString(R.string.time_work_start),
                getString(R.string.time_before_5),
                getString(R.string.time_before_15),
                getString(R.string.time_before_30),
                getString(R.string.time_before_hour_1),
                getString(R.string.time_before_day_1)
        };
        String[] allDayList = new String[]{
                getString(R.string.time_not_remind),
                getString(R.string.time_work_start),
                getString(R.string.time_all_before_day_1),
                getString(R.string.time_all_before_day_2),
                getString(R.string.time_all_before_week_1)
        };
        List<String> mList = new ArrayList<>();
        if (isAllDay) {
            mList.addAll(Arrays.asList(allDayList));
        } else {
            mList.addAll(Arrays.asList(normalList));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new SimpleDividerItemDecoration(activity));
        adapter = new EventAdapter(activity, mList, selectCode, new EventAdapter.EventItemCallback() {
            @Override
            public void onItemClick(int position) {
                selectCode = position;
                adapter.setSelectPos(position);
            }
        });
        rv.setAdapter(adapter);
    }
}
