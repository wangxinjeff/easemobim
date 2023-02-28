package com.hyphenate.easemob.im.officeautomation.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.im.officeautomation.domain.SelectUser;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.List;

public class SelectContactAdapter extends ArrayAdapter<SelectUser> implements SectionIndexer {
    private static final String TAG = "SelectContactAdapter";
    List<String> list;
    List<SelectUser> userList;
    List<SelectUser> copyUserList;
    private LayoutInflater layoutInflater;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private int res;
    private MyFilter myFilter;
    private boolean notiyfyByFilter;

    public SelectContactAdapter(Context context, int resource, List<SelectUser> objects) {
        super(context, resource, objects);
        this.res = resource;
        this.userList = objects;
        copyUserList = new ArrayList<SelectUser>();
        copyUserList.addAll(objects);
        layoutInflater = LayoutInflater.from(context);
    }

    private static class ViewHolder {
        AvatarImageView avatar;
        TextView nameView;
        TextView headerView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            if (res == 0)
                convertView = layoutInflater.inflate(R.layout.ease_row_contact, parent, false);
            else
                convertView = layoutInflater.inflate(res, null);
            holder.avatar = (AvatarImageView) convertView.findViewById(R.id.iv_avatar);
            holder.nameView = (TextView) convertView.findViewById(R.id.name);
            holder.headerView = (TextView) convertView.findViewById(R.id.header);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SelectUser user = getItem(position);
        if (user == null)
            Log.d("ContactAdapter", position + "");

        String header = user.getInitialLetter();

        if (position == 0 || header != null && !header.equals(getItem(position - 1).getInitialLetter())) {
            if (TextUtils.isEmpty(header)) {
                holder.headerView.setVisibility(View.GONE);
            } else {
                holder.headerView.setVisibility(View.VISIBLE);
                holder.headerView.setText(header);
            }
        } else {
            holder.headerView.setVisibility(View.GONE);
        }

        if (user.getType() == EMConversation.EMConversationType.Chat) {
            EaseUserUtils.setUserNick(user.getUser_id(), holder.nameView);
            AvatarUtils.setAvatarContent(getContext(), user.getUsername(), holder.avatar);
        } else {
            holder.nameView.setText(TextUtils.isEmpty(user.getNick()) ? user.getUsername() : user.getNick());
            String avatar = user.getAvatar();
            if (TextUtils.isEmpty(avatar)) {
                holder.avatar.setImageResource(R.drawable.ease_group_icon);
            } else {
                GlideUtils.load(getContext(), avatar, R.drawable.ease_group_icon, holder.avatar);
            }
        }


        if (primaryColor != 0)
            holder.nameView.setTextColor(primaryColor);
        if (primarySize != 0)
            holder.nameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
        if (initialLetterBg != null)
            holder.headerView.setBackgroundDrawable(initialLetterBg);
        if (initialLetterColor != 0)
            holder.headerView.setTextColor(initialLetterColor);

        return convertView;
    }

    @Override
    public SelectUser getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public int getPositionForSection(int section) {
        return positionOfSection.get(section);
    }

    @Override
    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getCount();
        list = new ArrayList<String>();
        list.add(getContext().getString(R.string.search_header));
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {

            String letter = getItem(i).getInitialLetter();
            int section = list.size() - 1;
            if (list.get(section) != null && !list.get(section).equals(letter)) {
                list.add(letter);
                section++;
                positionOfSection.put(section, i);
            }
            sectionOfPosition.put(i, section);
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public Filter getFilter() {
        if (myFilter == null) {
            myFilter = new MyFilter(userList);
        }
        return myFilter;
    }

    protected class MyFilter extends Filter {
        List<SelectUser> mOriginalList = null;

        public MyFilter(List<SelectUser> myList) {
            this.mOriginalList = myList;
        }

        @Override
        protected synchronized FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (mOriginalList == null) {
                mOriginalList = new ArrayList<SelectUser>();
            }
            EMLog.d(TAG, "contacts original size: " + mOriginalList.size());
            EMLog.d(TAG, "contacts copy size: " + copyUserList.size());

            if (prefix == null || prefix.length() == 0) {
                results.values = copyUserList;
                results.count = copyUserList.size();
            } else {

                if (copyUserList.size() > mOriginalList.size()) {
                    mOriginalList = copyUserList;
                }
                String prefixString = prefix.toString();
                final int count = mOriginalList.size();
                final ArrayList<SelectUser> newValues = new ArrayList<SelectUser>();
                for (int i = 0; i < count; i++) {
                    final SelectUser user = mOriginalList.get(i);
                    String username = user.getUsername();

                    if (username.startsWith(prefixString)) {
                        newValues.add(user);
                    } else {
                        final String[] words = username.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (String word : words) {
                            if (word.startsWith(prefixString)) {
                                newValues.add(user);
                                break;
                            }
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            EMLog.d(TAG, "contacts filter results size: " + results.count);
            return results;
        }

        @Override
        protected synchronized void publishResults(CharSequence constraint,
                                                   FilterResults results) {
            userList.clear();
            userList.addAll((List<SelectUser>) results.values);
            EMLog.d(TAG, "publish contacts filter results size: " + results.count);
            if (results.count > 0) {
                notiyfyByFilter = true;
                notifyDataSetChanged();
                notiyfyByFilter = false;
            } else {
                notifyDataSetInvalidated();
            }
        }
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (!notiyfyByFilter) {
            copyUserList.clear();
            copyUserList.addAll(userList);
        }
    }

    protected int primaryColor;
    protected int primarySize;
    protected Drawable initialLetterBg;
    protected int initialLetterColor;

    public SelectContactAdapter setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
        return this;
    }


    public SelectContactAdapter setPrimarySize(int primarySize) {
        this.primarySize = primarySize;
        return this;
    }

    public SelectContactAdapter setInitialLetterBg(Drawable initialLetterBg) {
        this.initialLetterBg = initialLetterBg;
        return this;
    }

    public SelectContactAdapter setInitialLetterColor(int initialLetterColor) {
        this.initialLetterColor = initialLetterColor;
        return this;
    }

}
