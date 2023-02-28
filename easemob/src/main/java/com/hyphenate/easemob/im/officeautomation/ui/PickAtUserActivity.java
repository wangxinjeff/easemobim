package com.hyphenate.easemob.im.officeautomation.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.adapter.EaseContactAdapter;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.EaseSidebar;
import com.hyphenate.easemob.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PickAtUserActivity extends BaseActivity {
    private static final String KEY_CID = "username";
    private static final String KEY_CNICK = "nick";
    ListView listView;
    View headerView;

    String groupId;
    EMGroup group;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_pick_at_user);

        groupId = getIntent().getStringExtra("groupId");
        group = EMClient.getInstance().groupManager().getGroup(groupId);

        EaseSidebar sidebar = (EaseSidebar) findViewById(R.id.sidebar);
        listView = (ListView) findViewById(R.id.list);
        sidebar.setListView(listView);
        updateList();

        updateGroupData();
    }

    void updateGroupData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    group = EMClient.getInstance().groupManager().getGroupFromServer(groupId, true);
//                    EMClient.getInstance().groupManager().fetchGroupMembers(groupId, "", 200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateList();
                    }
                });
            }
        }).start();
    }

    void updateList() {
        if (group == null) {
            return;
        }
        List<String> members = group.getMembers();
        List<EaseUser> userList = new ArrayList<EaseUser>();
        members.addAll(group.getAdminList());
        members.add(group.getOwner());
        for (String username : members) {
            EaseUser user = EaseUserUtils.getUserInfo(username);
            if (EMClient.getInstance().getCurrentUser().equalsIgnoreCase(username))
                continue;
            userList.add(user);
        }

        Collections.sort(userList, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if (lhs.getInitialLetter().equals(rhs.getInitialLetter())) {
                    return lhs.getNick().compareTo(rhs.getNick());
                } else {
                    if ("#".equals(lhs.getInitialLetter())) {
                        return 1;
                    } else if ("#".equals(rhs.getInitialLetter())) {
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });
        final boolean isOwner = EMClient.getInstance().getCurrentUser().equals(group.getOwner());
        final boolean isAdmin = group.getAdminList() != null && group.getAdminList().contains(EMClient.getInstance().getCurrentUser());
        if (isOwner || isAdmin) {
            addHeadView();
        } else {
            if (headerView != null) {
                listView.removeHeaderView(headerView);
                headerView = null;
            }
        }
        listView.setAdapter(new PickUserAdapter(this, 0, userList));
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isOwner || isAdmin) {
                    if (position != 0) {
                        EaseUser user = (EaseUser) listView.getItemAtPosition(position);
                        if (EMClient.getInstance().getCurrentUser().equals(user.getUsername()))
                            return;
                        Intent intent = new Intent();
                        intent.putExtra(KEY_CID, user.getUsername());
                        intent.putExtra(KEY_CNICK, user.getNickname() + " ");
                        setResult(RESULT_OK, intent);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(KEY_CID, EaseConstant.AT_ALL_USER_NAME);
                        intent.putExtra(KEY_CNICK, "所有人 ");
                        setResult(RESULT_OK, intent);
                    }
                } else {
                    EaseUser user = (EaseUser) listView.getItemAtPosition(position);
                    if (EMClient.getInstance().getCurrentUser().equals(user.getUsername()))
                        return;
                    Intent intent = new Intent();
                    intent.putExtra(KEY_CID, user.getUsername());
                    intent.putExtra(KEY_CNICK,  user.getNickname() + " ");
                    setResult(RESULT_OK, intent);
                }

                finish();
            }
        });
    }

    private void addHeadView() {
        if (listView.getHeaderViewsCount() == 0) {
            View view = LayoutInflater.from(this).inflate(R.layout.ease_row_contact, listView, false);
            ImageView avatarView = (ImageView) view.findViewById(R.id.iv_avatar);
            TextView textView = (TextView) view.findViewById(R.id.name);
            textView.setText(getString(R.string.all_members));
            avatarView.setImageResource(R.drawable.ease_groups_icon);
            listView.addHeaderView(view);
            headerView = view;
        }
    }

    public void back(View view) {
        finish();
    }

    private class PickUserAdapter extends EaseContactAdapter {

        public PickUserAdapter(Context context, int resource, List<EaseUser> objects) {
            super(context, resource, objects);
        }
    }
}
