package com.hyphenate.easemob.im.officeautomation.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.domain.DepartmentBean;

import java.util.List;

public class CustomListAdapter extends ArrayAdapter<DepartmentBean> {
    private Context context;
    private List<DepartmentBean> list;
    private int layoutId;
//    public View view;
//    private int currPosition = 0;
    private DepartmentIndexItemCallback callback;

    public CustomListAdapter(Context context, int textViewResourceId,
                             List<DepartmentBean> list, DepartmentIndexItemCallback callback) {
        super(context, android.R.layout.simple_list_item_1, list);
        this.context = context;
        this.list = list;
        this.callback = callback;
        layoutId = textViewResourceId;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public DepartmentBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        CustomHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, layoutId, null);
            holder = new CustomHolder(convertView);
            holder.title = convertView.findViewById(R.id.txtNewsSource);
            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }
        if (position == list.size() - 1) {
            holder.iv_right.setVisibility(View.INVISIBLE);
        } else {
            holder.title.setTextColor(convertView.getResources().getColor(R.color.button_color));
            holder.iv_right.setVisibility(View.VISIBLE);
        }

        DepartmentBean departmentBean = getItem(position);
        String newsSource = "";
        if (departmentBean != null) {
            newsSource = departmentBean.getName();
        }
        holder.title.setText(newsSource);
        holder.rl_department_index.setOnClickListener(view1 -> {
            callback.onClick(position);
        });
        return convertView;
    }


    private class CustomHolder {
        public RelativeLayout rl_department_index;
        public TextView title;
        public ImageView iv_right;

        private CustomHolder(View contentView) {
            rl_department_index = contentView.findViewById(R.id.rl_department_index);
            title = contentView.findViewById(R.id.txtNewsSource);
            iv_right = contentView.findViewById(R.id.iv_right);
        }

    }

    public int getCurrentPosition() {
        return getCount() - 1;
    }

    public interface DepartmentIndexItemCallback {
        void onClick(Integer viewTag);
    }
}
