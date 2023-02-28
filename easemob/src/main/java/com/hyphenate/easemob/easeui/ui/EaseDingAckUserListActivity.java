package com.hyphenate.easemob.easeui.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.model.EaseDingMessageHelperV2;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.EaseTitleBar;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangsong on 18-1-23.
 */

public class EaseDingAckUserListActivity extends EaseBaseActivity {
    private static final String TAG = "EaseDingAckUserListActi";

    private ListView ackUserListView;
    private EaseTitleBar titleBar;

    private EMMessage msg;

    private AckUserAdapter userAdapter;
    private List<String> userList;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.ease_activity_ding_ack_user_list);
        ackUserListView = findViewById(R.id.list_view);
        titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle(getString(R.string.title_ack_read_list));

        // Set the title bar left layout click listener to back to previous activity.
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back(v);
            }
        });

        msg = getIntent().getParcelableExtra("msg");
        EMLog.i(TAG, "Get msg from intent, msg: " + msg.toString());

        userList = new ArrayList<>();
        userAdapter = new AckUserAdapter(this, userList);

        ackUserListView.setAdapter(userAdapter);

        EaseDingMessageHelperV2.get().fetchGroupReadAck(msg);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        List<String> list = EaseDingMessageHelper.get().getAckUsers(msg);
//        userList.clear();
//        if (list != null) {
//            userList.addAll(list);
//        }
//        userAdapter.notifyDataSetChanged();

        // Set ack-user change listener.
        EaseDingMessageHelperV2.get().setUserUpdateListener(msg, userUpdateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Remove ack-user change listener.
        EaseDingMessageHelperV2.get().setUserUpdateListener(msg, null);
    }

    private EaseDingMessageHelperV2.IAckUserUpdateListener userUpdateListener =
            new EaseDingMessageHelperV2.IAckUserUpdateListener() {
                @Override
                public void onUpdate(List<String> list) {
                    EMLog.i(TAG, "onUpdate: " + list.size());

                    userList.clear();
                    userList.addAll(list);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

    private static class AckUserAdapter extends BaseAdapter {
        private Context context;
        private List<String> userList;

        public AckUserAdapter(Context context, List<String> userList) {
            this.context = context;
            this.userList = userList;
        }

        @Override
        public int getCount() {
            return userList.size();
        }

        @Override
        public Object getItem(int position) {
            return userList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.ease_row_ding_ack_user, null);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            String username = userList.get(position);
            if (username != null){
               EaseUser user =  EaseUserUtils.getUserInfo(username);
               if (user != null && user.getNick() != null){
                   vh.nameView.setText(user.getNick());
               }else{
                   vh.nameView.setText(username);
               }
            }

            return convertView;
        }

        private static class ViewHolder {
            public TextView nameView;

            public ViewHolder(View contentView) {
                nameView = contentView.findViewById(R.id.username);
            }
        }
    }
}
