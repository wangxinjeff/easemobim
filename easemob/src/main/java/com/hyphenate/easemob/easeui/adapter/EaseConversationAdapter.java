package com.hyphenate.easemob.easeui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.delegates.DraftDelegate;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.easeui.utils.EaseSmileUtils;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.easeui.widget.EaseConversationList.EaseConversationListHelper;
import com.hyphenate.easemob.imlibs.badge.QBadgeView;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.imlibs.mp.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * conversation list adapter
 */
public class EaseConversationAdapter extends ArrayAdapter<EMConversation> {
    private static final String TAG = "ChatAllHistoryAdapter";
    private List<EMConversation> conversationList;
    private List<EMConversation> copyConversationList;
    private ConversationFilter conversationFilter;
    private boolean notiyfyByFilter;

    private int primaryColor;
    private int secondaryColor;
    private int timeColor;
    private int primarySize;
    private int secondarySize;
    private float timeSize;

    private DraftDelegate mDelegate;

    public EaseConversationAdapter(Context context, int resource,
                                   List<EMConversation> objects) {
        super(context, resource, objects);
        conversationList = objects;
        copyConversationList = new ArrayList<>();
        copyConversationList.addAll(objects);
    }

    public void setDraftListener(DraftDelegate listener) {
        this.mDelegate = listener;
    }

    @Override
    public int getCount() {
        return conversationList.size();
    }

    @Override
    public EMConversation getItem(int arg0) {
        if (arg0 < conversationList.size()) {
            return conversationList.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ease_row_chat_history, parent, false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.name);
            holder.unreadLabel = convertView.findViewById(R.id.unread_msg_number);
            holder.message = convertView.findViewById(R.id.message);
            holder.time = convertView.findViewById(R.id.time);
            holder.ivAvatar = convertView.findViewById(R.id.iv_avatar);
            holder.msgState = convertView.findViewById(R.id.msg_state);
            holder.list_itease_layout = convertView.findViewById(R.id.list_itease_layout);
            holder.motioned = convertView.findViewById(R.id.mentioned);
            holder.draft = convertView.findViewById(R.id.draft);
            holder.ivIsTop = convertView.findViewById(R.id.iv_istop);
            holder.ivNoDisturb = convertView.findViewById(R.id.iv_nodisturb);
            holder.badgeView = new QBadgeView(getContext());
            holder.badgeView.setBadgeTextSize(10, true);
            holder.badgeView.setShowShadow(false);
            holder.badgeView.setBadgeGravity(Gravity.CENTER);
            holder.badgeView.bindTarget(holder.unreadLabel)
                    .setBadgeBackgroundColor(getContext().getResources().getColor(R.color.badge_color));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // get conversation
        EMConversation conversation = getItem(position);
        if (conversation == null) {
            return convertView;
        }
        // get username or group id
        String username = conversation.conversationId();
        boolean isNoDisturb = false;

        if (TextUtils.isEmpty(conversation.getExtField())) {
            holder.list_itease_layout.setBackgroundResource(R.drawable.ease_mm_listitem);
            holder.ivIsTop.setVisibility(View.GONE);
        } else {
            holder.list_itease_layout.setBackgroundResource(R.drawable.conversation_list_item_sticky);
            holder.ivIsTop.setVisibility(View.VISIBLE);
        }
        if (holder.ivNoDisturb != null) {
            holder.ivNoDisturb.setVisibility(View.INVISIBLE);
        }

        if (conversation.getType() == EMConversationType.GroupChat) {
            String groupId = conversation.conversationId();
            if (EaseAtMessageHelper.get().hasAtMeMsg(groupId)) {
                holder.motioned.setVisibility(View.VISIBLE);
            } else {
                holder.motioned.setVisibility(View.GONE);
            }
            GroupBean groupInfo = EaseUserUtils.getGroupInfo(groupId);
            if (groupInfo != null) {
                // group message, show group ivAvatar
                String avatar = groupInfo.getAvatar();
                if (TextUtils.isEmpty(avatar)) {
                    AvatarUtils.setGroupAvatarContent(groupInfo.getNick(), holder.ivAvatar);
                } else {
                    GlideUtils.load(getContext(), avatar, R.drawable.ease_group_icon, holder.ivAvatar);
                }
                holder.name.setText(groupInfo.getNick() != null ? groupInfo.getNick() : username);
            } else {
                EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
                if (group != null) {
                    holder.name.setText(group.getGroupName());
                } else {
                    holder.name.setText(username);
                }
                // group message, show group ivAvatar
                AvatarUtils.setGroupAvatarContent(holder.name.getText().toString(), holder.ivAvatar);
            }
//            List<String> noPushGroups = EMClient.getInstance().pushManager().getNoPushGroups();
//            if (noPushGroups != null) {
//                if (noPushGroups.contains(groupId)) {
            boolean noDisturb = EaseUI.getInstance().getUserProfileProvider().isNoDisturb(groupId);
            if (noDisturb) {
                isNoDisturb = true;
                holder.ivNoDisturb.setVisibility(View.VISIBLE);
            } else {
                holder.ivNoDisturb.setVisibility(View.INVISIBLE);
            }
//            }
        } else if (conversation.getType() == EMConversationType.ChatRoom) {
            holder.ivAvatar.setImageResource(R.drawable.ease_group_icon);
            EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(username);
            holder.name.setText(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : username);
            holder.motioned.setVisibility(View.GONE);
        } else {
            if (EaseConstant.SYSTEM_USER_NAME.equals(username)) {
                holder.ivAvatar.setImageResource(R.drawable.mp_ic_notif_system);
                holder.name.setText(getContext().getString(R.string.system_msg));
            } else if (MPClient.get().isFileHelper(username)) {
                holder.name.setText(getContext().getResources().getString(R.string.file_transfer));
                AvatarUtils.setAvatarContent(getContext(), holder.ivAvatar);
            } else {
                EaseUserUtils.setUserNick(username, holder.name, getContext());
                AvatarUtils.setAvatarContent(getContext(), username, holder.ivAvatar);
            }
            holder.motioned.setVisibility(View.GONE);
        }
        int unreadCount = conversation.getUnreadMsgCount();
        if (unreadCount > 0) {
            if (!isNoDisturb) {
                if (holder.badgeView != null) {
                    holder.badgeView.setBadgeNumber(conversation.getUnreadMsgCount());
                }

//                holder.unreadLabel.setBackgroundResource(R.drawable.ease_unread_count_bg);
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                lp.topMargin = EaseCommonUtils.convertDip2Px(getContext(), 2);
//                lp.rightMargin = EaseCommonUtils.convertDip2Px(getContext(), 3);
//                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                holder.unreadLabel.setLayoutParams(lp);
//                if (unreadCount > 99) {
//                    holder.unreadLabel.setText("99+");
//                } else {
//                    holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
//                }
            } else {
//                holder.unreadLabel.setBackgroundResource(R.drawable.shape_bg_circle_red_small);
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(EaseCommonUtils.convertDip2Px(getContext(), 8), EaseCommonUtils.convertDip2Px(getContext(), 8));
//                lp.topMargin = EaseCommonUtils.convertDip2Px(getContext(), 5);
//                lp.rightMargin = EaseCommonUtils.convertDip2Px(getContext(), 5);
//                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                holder.unreadLabel.setLayoutParams(lp);
//                holder.unreadLabel.setText("");
                if (holder.badgeView != null) {
                    holder.badgeView.hide(false);
                }
            }

            holder.unreadLabel.setVisibility(View.VISIBLE);
        } else {
            holder.unreadLabel.setVisibility(View.INVISIBLE);
            if (holder.badgeView != null) {
                holder.badgeView.hide(false);
            }
        }

        if (conversation.getAllMsgCount() != 0) {

            // show the content of latest message
            EMMessage lastMessage = conversation.getLastMessage();

//            String content = null;
//            if (cvsListHelper != null) {
//                content = cvsListHelper.onSetItemSecondaryText(lastMessage);
//            }
//
//            if (conversation.getType() == EMConversationType.Chat) {
//                for (int i = conversation.getAllMessages().size() - 1; i >= 0; i--) {
//                    EMMessage message = conversation.getAllMessages().get(i);
//                    if (message.getType() == EMMessage.Type.TXT) {
//                        if (TextUtils.isEmpty(message.getStringAttribute(EaseConstant.MSG_ATTR_CONFERENCE, ""))) {
//                            lastMessage = message;
//                            content = ((EMTextMessageBody) lastMessage.getBody()).getMessage();
//                            break;
//                        } else {
//                            content = "";
//                        }
//                    }
//                }
//            }

            holder.message.setText(EaseSmileUtils.getSmiledText(getContext(), EaseCommonUtils.getMessageDigest(lastMessage, (this.getContext()), false)),
                    BufferType.SPANNABLE);
            holder.time.setText(DateTimeUtil.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                holder.msgState.setVisibility(View.VISIBLE);
            } else {
                holder.msgState.setVisibility(View.GONE);
            }
        }

        if (mDelegate != null) {
            if (mDelegate.hasDraft(username)) {
                holder.draft.setVisibility(View.VISIBLE);
                holder.message.setText(EaseSmileUtils.getSmiledText(getContext(), mDelegate.getContent(username)));
            } else {
                holder.draft.setVisibility(View.GONE);
            }
        } else {
            holder.draft.setVisibility(View.GONE);
        }

        //set property
        holder.name.setTextColor(primaryColor);
        holder.message.setTextColor(secondaryColor);
        holder.time.setTextColor(timeColor);
        if (primarySize != 0)
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
        if (secondarySize != 0)
            holder.message.setTextSize(TypedValue.COMPLEX_UNIT_PX, secondarySize);
        if (timeSize != 0)
            holder.time.setTextSize(TypedValue.COMPLEX_UNIT_PX, timeSize);

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (!notiyfyByFilter) {
            copyConversationList.clear();
            copyConversationList.addAll(conversationList);
            notiyfyByFilter = false;
        }
    }

    @Override
    public Filter getFilter() {
        if (conversationFilter == null) {
            conversationFilter = new ConversationFilter(conversationList);
        }
        return conversationFilter;
    }


    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public void setSecondaryColor(int secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public void setTimeColor(int timeColor) {
        this.timeColor = timeColor;
    }

    public void setPrimarySize(int primarySize) {
        this.primarySize = primarySize;
    }

    public void setSecondarySize(int secondarySize) {
        this.secondarySize = secondarySize;
    }

    public void setTimeSize(float timeSize) {
        this.timeSize = timeSize;
    }


    private class ConversationFilter extends Filter {
        List<EMConversation> mOriginalValues = null;

        public ConversationFilter(List<EMConversation> mList) {
            mOriginalValues = mList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<EMConversation>();
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = copyConversationList;
                results.count = copyConversationList.size();
            } else {
                if (copyConversationList.size() > mOriginalValues.size()) {
                    mOriginalValues = copyConversationList;
                }
                String prefixString = prefix.toString();
                final int count = mOriginalValues.size();
                final ArrayList<EMConversation> newValues = new ArrayList<EMConversation>();

                for (int i = 0; i < count; i++) {
                    final EMConversation value = mOriginalValues.get(i);
                    String username = value.conversationId();
                    String phone = null;
                    if (value.getType() != EMConversationType.Chat) {
                        GroupBean groupInfo = EaseUserUtils.getGroupInfo(username);
                        username = groupInfo.getNick();
                    } else {
                        EaseUser user = EaseUserUtils.getUserInfo(username);
                        username = user.getNick();
                        phone = user.getMobilePhone();
                    }

                    // First match against the whole ,non-splitted value
                    if (username.startsWith(prefixString)) {
                        newValues.add(value);
                    } else if (phone != null && phone.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = username.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (String word : words) {
                            if (word.startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            conversationList.clear();
            if (results.values != null) {
                conversationList.addAll((List<EMConversation>) results.values);
            }
            if (results.count > 0) {
                notiyfyByFilter = true;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    private EaseConversationListHelper cvsListHelper;

    public void setCvsListHelper(EaseConversationListHelper cvsListHelper) {
        this.cvsListHelper = cvsListHelper;
    }

    private static class ViewHolder {
        /**
         * who you chat with
         */
        TextView name;
        /**
         * unread message count
         */
        TextView unreadLabel;
        /**
         * content of last message
         */
        TextView message;
        /**
         * time of last message
         */
        TextView time;
        /**
         * ivAvatar
         */
        AvatarImageView ivAvatar;
        /**
         * isTop
         */
        ImageView ivIsTop;
        /**
         * status of last message
         */
        View msgState;
        /**
         * layout
         */
        RelativeLayout list_itease_layout;
        TextView motioned;
        TextView draft;

        ImageView ivNoDisturb;

        QBadgeView badgeView;
    }
}

