package com.hyphenate.easemob.im.officeautomation.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.widget.SetTextSizeView;

import java.util.ArrayList;
import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 02/06/2018
 */

public class SetTextSizeActivity extends BaseActivity {


    private SetTextSizeView mSetTextSizeView;
    private ListView mListView;
    private List<EMMessage> mMessageList = new ArrayList<>();
    private LayoutInflater mInflater;
    private int mDefaultTextSize = 15;
    private int currentProgress = 2;
    private MyAdapter mMyAdapter;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_set_textsize);
        mInflater = LayoutInflater.from(this);
        initViews();
        initListeners();
        initDatas();
    }

    private void initViews() {
        mSetTextSizeView = findViewById(R.id.settextsizeview);
        mListView = findViewById(R.id.listview);
        currentProgress = PreferenceUtils.getInstance().getTextSizeProgress();
        if (currentProgress <= 0) {
            currentProgress = 2;
        }
        mSetTextSizeView.setCurrentProgress(currentProgress - 1);
        ivBack = findViewById(R.id.iv_back);
    }


    private void initDatas() {
        EMMessage sendMessage = EMMessage.createTxtSendMessage(getString(R.string.set_text_size_tip1), "robot");
        sendMessage.setStatus(EMMessage.Status.SUCCESS);
        mMessageList.add(sendMessage);
        EMMessage receiveMessage1 = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        receiveMessage1.addBody(new EMTextMessageBody(getString(R.string.set_text_size_tip2)));
        receiveMessage1.setMsgTime(System.currentTimeMillis());
        mMessageList.add(receiveMessage1);
        EMMessage receiveMessage2 = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        receiveMessage2.addBody(new EMTextMessageBody(getString(R.string.set_text_size_tip3)));
        receiveMessage2.setMsgTime(System.currentTimeMillis());
        mMessageList.add(receiveMessage2);
        mListView.setAdapter(mMyAdapter = new MyAdapter());

    }

    private void initListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSetTextSizeView.setOnPointResultListener(new SetTextSizeView.OnPointResultListener() {
            @Override
            public void onPointResult(int position) {
                currentProgress = position + 1;
                PreferenceUtils.getInstance().setTextSizeProgress(currentProgress);
                mMyAdapter.notifyDataSetChanged();
            }
        });
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            EMMessage message = getItem(position);
            if (message.direct() == EMMessage.Direct.SEND) {
                return 0;
            }
            return 1;
        }

        @Override
        public int getCount() {
            return mMessageList.size();
        }

        @Override
        public EMMessage getItem(int position) {
            return mMessageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tvContent;
            EMMessage message = getItem(position);
            if (message.direct() == EMMessage.Direct.SEND) {
                convertView = mInflater.inflate(R.layout.ease_row_sent_rt_loc, parent, false);
            } else {
                convertView = mInflater.inflate(R.layout.ease_row_received_rt_loc, parent, false);
            }
            View timestampView = convertView.findViewById(R.id.timestamp);
            if (timestampView != null) {
                timestampView.setVisibility(View.GONE);
            }

            View progressView = convertView.findViewById(R.id.progress_bar);
            if (progressView != null) {
                progressView.setVisibility(View.GONE);
            }
            tvContent = convertView.findViewById(R.id.tv_chatcontent);
            tvContent.setText(((EMTextMessageBody) message.getBody()).getMessage());
            float x = 0.1f * (currentProgress - 2) + 1;
            tvContent.setTextSize(mDefaultTextSize * x);
            return convertView;
        }

    }


}
