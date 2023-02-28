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

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.R;

import java.util.List;

public class NewChatGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private GroupCallback callback;
    private Context context;
    private List<GroupBean> groups;
    private LayoutInflater mInflater;

    public NewChatGroupAdapter(Context context, List<GroupBean> groups, GroupCallback callback) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.groups = groups;
        this.callback = callback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.em_row_group, parent, false);
        //这边可以做一些属性设置，甚至事件监听绑定
        //view.setBackgroundColor(Color.RED);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        if (holder instanceof ItemHolder) {
            ItemHolder contentHolder = (ItemHolder) holder;
            GroupBean groupBean = groups.get(position);
            contentHolder.tv_name.setText(groupBean == null ? "" : groupBean.getNick());
            if (groupBean != null && !TextUtils.isEmpty(groupBean.getAvatar())) {
                String remoteUrl = groupBean.getAvatar();
                GlideUtils.load(context, remoteUrl, R.drawable.ease_group_icon, contentHolder.iv_avatar);
            } else {
                contentHolder.iv_avatar.setImageResource(R.drawable.ease_group_icon);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onItemClick(position);
                }
            });
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private ImageView iv_avatar;

        public ItemHolder(View convertView) {
            super(convertView);
            tv_name = convertView.findViewById(R.id.name);
            iv_avatar = convertView.findViewById(R.id.avatar);
        }
    }


    public interface GroupCallback {
        void onItemClick(int position);
    }

}
