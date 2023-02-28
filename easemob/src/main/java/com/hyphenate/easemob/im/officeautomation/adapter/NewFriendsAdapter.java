/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easemob.im.officeautomation.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.db.AppDBManager;
import com.hyphenate.easemob.im.officeautomation.db.InviteMessageDao;
import com.hyphenate.easemob.im.officeautomation.domain.InviteMessage;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NewFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private InviteMessageDao messageDao;
    private List<InviteMessage> inviteMessageList;


    //上拉加载
    private static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //没有加载的时候
    public static final int LOADING_END = 2;
    //上拉加载更多状态-默认为0
    private int load_more_status = 0;

    public NewFriendsAdapter(Context context, List<InviteMessage> inviteMessageList) {
        this.context = context;
        messageDao = new InviteMessageDao();
        this.inviteMessageList = inviteMessageList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView;
        if (viewType == 0) {
            convertView = inflater.inflate(R.layout.invite_msg_item, parent, false);
            return new NormalViewHolder(convertView);
        } else if (viewType == 1) {
            convertView = inflater.inflate(R.layout.recycler_load_more_layout, parent, false);
            return new FootViewHolder(convertView);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NormalViewHolder) {
            final InviteMessage msg = inviteMessageList.get(position);
            if (msg != null) {
                if (msg.getChatGroupId() != 0) {
                    if (msg.getStatus() == InviteMessage.InviteMessageStatus.BEAPPLYED) {
                        EaseUser user = AppDBManager.getInstance().getUserExtInfo(msg.getUserId());
                        ((NormalViewHolder) holder).name.setText(user.getNickname() + "申请加入群组");
                        ((NormalViewHolder) holder).agreeBtn.setText(context.getResources().getString(R.string.agree));
                        ((NormalViewHolder) holder).agreeBtn.setTextColor(Color.parseColor("#309167"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ((NormalViewHolder) holder).agreeBtn.setBackground(context.getDrawable(R.drawable.accept_btn_bg));
                        } else {
                            ((NormalViewHolder) holder).agreeBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.accept_btn_bg));
                        }
                        ((NormalViewHolder) holder).agreeBtn.setEnabled(true);
                        // set click listener
                        ((NormalViewHolder) holder).agreeBtn.setOnClickListener(v -> {
                            // accept apply
                            approveMemberApply(((NormalViewHolder) holder).agreeBtn, msg);
                        });
                    } else if (msg.getStatus() == InviteMessage.InviteMessageStatus.AGREED) {
                        ((NormalViewHolder) holder).agreeBtn.setText(context.getResources().getString(R.string.Has_agreed_to));
                        ((NormalViewHolder) holder).agreeBtn.setTextColor(Color.parseColor("#CCCCCC"));
                        ((NormalViewHolder) holder).agreeBtn.setBackground(null);
                        ((NormalViewHolder) holder).agreeBtn.setEnabled(false);
                    }
                } else {
                    ((NormalViewHolder) holder).name.setText(msg.getRealName());
                    // holder.time.setText(DateUtils.getTimestampString(new
                    // Date(msg.getTime())));
                    if (msg.getStatus() == InviteMessage.InviteMessageStatus.BEAGREED) {
                        ((NormalViewHolder) holder).agreeBtn.setText(context.getResources().getString(R.string.Has_agreed_to));
                        ((NormalViewHolder) holder).agreeBtn.setTextColor(Color.parseColor("#CCCCCC"));
                        ((NormalViewHolder) holder).agreeBtn.setBackground(null);
                        ((NormalViewHolder) holder).agreeBtn.setEnabled(false);
                    } else if (msg.getStatus() == InviteMessage.InviteMessageStatus.BEINVITEED) {
                        ((NormalViewHolder) holder).agreeBtn.setText(context.getResources().getString(R.string.agree));
                        ((NormalViewHolder) holder).agreeBtn.setTextColor(Color.parseColor("#309167"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ((NormalViewHolder) holder).agreeBtn.setBackground(context.getDrawable(R.drawable.accept_btn_bg));
                        } else {
                            ((NormalViewHolder) holder).agreeBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.accept_btn_bg));
                        }
                        ((NormalViewHolder) holder).agreeBtn.setEnabled(true);
                        // set click listener
                        ((NormalViewHolder) holder).agreeBtn.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // accept invitation
                                acceptInvitation(((NormalViewHolder) holder).agreeBtn, msg);
                            }
                        });
                    }
                }
            }
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onClick(((NormalViewHolder) holder).agreeBtn, position);
                }
            });
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            switch (load_more_status) {
                case PULLUP_LOAD_MORE:
                    footViewHolder.pb_more.setVisibility(View.GONE);
                    footViewHolder.foot_view_item_tv.setText(context.getResources().getString(R.string.pull_to_load));
                    break;
                case LOADING_MORE:
                    footViewHolder.pb_more.setVisibility(View.VISIBLE);
                    footViewHolder.foot_view_item_tv.setText(context.getResources().getString(R.string.load_more));
                    break;
                case LOADING_END:
                    footViewHolder.pb_more.setVisibility(View.GONE);
                    footViewHolder.foot_view_item_tv.setText(context.getResources().getString(R.string.load_end));
                    break;
            }
        }


    }

    /**
     * 进行判断是普通Item视图还是FootView视图
     *
     * @param position 索引
     * @return 条目ID
     */
    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (inviteMessageList.size() >= Constant.PAGE_SIZE && position + 1 == getItemCount()) {
            return 1;
        } else {
            return 0;
        }
    }


    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     *
     * @param status 加载状态
     */
    public void changeMoreStatus(int status) {
        load_more_status = status;
        notifyDataSetChanged();
    }

    private ClickListener listener;

    public void setClickListener(ClickListener listener) {
        this.listener = listener;
    }

    public interface ClickListener {
        void onClick(TextView button, int position);
    }

    @Override
    public int getItemCount() {
        if (inviteMessageList.size() < Constant.PAGE_SIZE) {
            return inviteMessageList.size();
        } else {
            return inviteMessageList.size() + 1;
        }
    }

    /**
     * accept invitation
     *
     * @param buttonAgree
     * @param msg
     */
    private void acceptInvitation(final TextView buttonAgree, final InviteMessage msg) {
        final ProgressDialog pd = new ProgressDialog(context);
        String str1 = context.getResources().getString(R.string.Are_agree_with);
        final String str2 = context.getResources().getString(R.string.Has_agreed_to);
        final String str3 = context.getResources().getString(R.string.Agree_with_failure);
        pd.setMessage(str1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        new Thread(new Runnable() {
            public void run() {
                // call api
                try {
                    if (msg.getStatus() == InviteMessage.InviteMessageStatus.BEINVITEED) {//accept be friends
                        EMAPIManager.getInstance().invitedOrAcceptFriend(msg.getUserId(), new EMDataCallBack<String>() {
                            @Override
                            public void onSuccess(String value) {

                                try {
                                    JSONObject result = new JSONObject(value);
                                    if ("OK".equals(result.getString("status"))) {
                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, context.getResources().getString(R.string.Has_agreed_to), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(int error, String errorMsg) {

                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                    }
                    msg.setStatus(InviteMessage.InviteMessageStatus.BEAGREED);
                    // update database
                    ContentValues values = new ContentValues();
                    values.put(InviteMessageDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
                    messageDao.updateMessage(msg.getUserId(), values);
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            buttonAgree.setText(str2);
                            buttonAgree.setTextColor(Color.GRAY);
                            buttonAgree.setBackground(null);
                            buttonAgree.setEnabled(false);
                        }
                    });
                } catch (final Exception e) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(context, str3 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        }).start();
    }


    /**
     * accept member apply
     *
     * @param buttonAgree
     * @param msg
     */
    private void approveMemberApply(final TextView buttonAgree, final InviteMessage msg) {
        final ProgressDialog pd = new ProgressDialog(context);
        String str1 = context.getResources().getString(R.string.Are_agree_with);
        final String str2 = context.getResources().getString(R.string.Has_agreed_to);
        final String str3 = context.getResources().getString(R.string.Agree_with_failure);
        pd.setMessage(str1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        new Thread(() -> {
            // call api
            try {
                if (msg.getStatus() == InviteMessage.InviteMessageStatus.BEAPPLYED) {//accept be friends
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("userId", msg.getUserId());
                    jsonBody.put("approve", 1);
                    EMAPIManager.getInstance().approveMemberApply(msg.getChatGroupId(), msg.getCluster(), jsonBody.toString(), new EMDataCallBack<String>() {
                        @Override
                        public void onSuccess(String value) {

                            try {
                                JSONObject result = new JSONObject(value);
                                if ("OK".equals(result.getString("status"))) {
                                    ((Activity) context).runOnUiThread(() -> Toast.makeText(context, context.getResources().getString(R.string.Has_agreed_to), Toast.LENGTH_SHORT).show());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(int error, String errorMsg) {

                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                }
                msg.setStatus(InviteMessage.InviteMessageStatus.AGREED);
                // update database
                ContentValues values = new ContentValues();
                values.put(InviteMessageDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
                messageDao.updateMessage(msg.getUserId(), values);
                ((Activity) context).runOnUiThread(() -> {
                    pd.dismiss();
                    buttonAgree.setText(str2);
                    buttonAgree.setTextColor(Color.GRAY);
                    buttonAgree.setBackground(null);
                    buttonAgree.setEnabled(false);
                });
            } catch (final Exception e) {
                ((Activity) context).runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(context, str3 + e.getMessage(), Toast.LENGTH_LONG).show();
                });

            }
        }).start();
    }

    class NormalViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name;
        TextView agreeBtn;

        public NormalViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.name);
            agreeBtn = itemView.findViewById(R.id.agree);
        }
    }

    /**
     * 底部FootView布局
     */
    private static class FootViewHolder extends RecyclerView.ViewHolder {
        private TextView foot_view_item_tv;
        private ProgressBar pb_more;

        private FootViewHolder(View view) {
            super(view);
            foot_view_item_tv = view.findViewById(R.id.foot_view_item_tv);
            pb_more = view.findViewById(R.id.pb_more);
        }
    }
}
