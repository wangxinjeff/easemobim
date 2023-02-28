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
 * 设置重复提醒方式
 */
public class SetEventRepeatActivity extends BaseActivity {

    private static final String TAG = "SetEventRepeatActivit";
    public static final int RESULT_CODE_REPEAT = 4;
    private EaseTitleBar title_bar;
    private RecyclerView rv;
    private int selectStyle;
    private EventAdapter adapter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_set_event_repeat);
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
            intent.putExtra("repeat_code", selectStyle);
            setResult(RESULT_CODE_REPEAT, intent);
            finish();
        });
    }

    //初始化数据
    private void initData() {
        selectStyle = getIntent().getIntExtra("select_repeat", 0);
        String[] normalList = new String[]{
                getString(R.string.no_repeat),
                getString(R.string.repeat_day),
                getString(R.string.repeat_week),
                getString(R.string.repeat_week_2),
                getString(R.string.repeat_month),
                getString(R.string.repeat_year)
        };
        List<String> mList = new ArrayList<>(Arrays.asList(normalList));
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new SimpleDividerItemDecoration(activity));
        adapter = new EventAdapter(activity, mList, selectStyle, position -> {
            selectStyle = position;
            adapter.setSelectPos(position);
        });
        rv.setAdapter(adapter);
    }
}
