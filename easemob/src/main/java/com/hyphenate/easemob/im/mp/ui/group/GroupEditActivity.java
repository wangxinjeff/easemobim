package com.hyphenate.easemob.im.mp.ui.group;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;

public class GroupEditActivity extends BaseActivity {

    public static final int UPDATE_GROUP_NAME = 0x01;
    public static final int UPDATE_GROUP_DESCRIPTION = 0x02;

    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvDone;
    private EditText etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        initViews();
        initDatas();
    }


    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvDone = findViewById(R.id.tv_done);
        etContent = findViewById(R.id.et_content);
    }

    private void initDatas() {
        int index = getIntent().getIntExtra("index", -1);
        if (index == UPDATE_GROUP_NAME) {
            tvTitle.setText("群名称");
            etContent.setHint("请输入群名称");
            etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        } else if (index == UPDATE_GROUP_DESCRIPTION) {
            tvTitle.setText("群描述");
            etContent.setHint("请输入群描述");
            etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showShort("内容不能为空！");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("content", content);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }



}
