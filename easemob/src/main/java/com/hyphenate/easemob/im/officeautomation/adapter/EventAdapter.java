package com.hyphenate.easemob.im.officeautomation.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easemob.R;

import java.util.List;

/**
 * Created by qby on 2018/7/11.
 * recyclerView设置提醒时间
 */

public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "EventAdapter";
    private int selectPos;
    private Context context;
    private LayoutInflater mInflater;
    private List<String> mList;
    private EventItemCallback callback;

    public EventAdapter(Context context, List<String> mList, int selectPos, EventItemCallback callback) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = mList;
        this.selectPos = selectPos;
        this.callback = callback;
    }

    /**
     * item显示类型
     *
     * @param parent   父控件
     * @param viewType view类型
     * @return holder
     */
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_list_reminder_time, parent, false);
        //这边可以做一些属性设置，甚至事件监听绑定
        //view.setBackgroundColor(Color.RED);
        return new ItemViewHolder(itemView);
    }

    /**
     * 数据的绑定显示
     *
     * @param holder   holder
     * @param position 索引
     */
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder contentHolder = (ItemViewHolder) holder;
            String item = mList.get(position);
            if (selectPos == position) {
                contentHolder.iv_right.setVisibility(View.VISIBLE);
                contentHolder.tv_reminder.setSelected(true);
            } else {
                contentHolder.iv_right.setVisibility(View.INVISIBLE);
                contentHolder.tv_reminder.setSelected(false);
            }
            contentHolder.tv_reminder.setText(item);
            if (position == 0) {
                contentHolder.divider.setVisibility(View.VISIBLE);
            } else {
                contentHolder.divider.setVisibility(View.GONE);
            }
            contentHolder.rl_reminder.setOnClickListener(view -> {
                callback.onItemClick(position);
            });
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setSelectPos(int selectPos){
        this.selectPos = selectPos;
        notifyDataSetChanged();
    }

    //自定义的ItemViewHolder，持有每个user的的所有界面元素
    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rl_reminder;
        private ImageView iv_right;
        private TextView tv_reminder;
        private View divider;

        private ItemViewHolder(View itemView) {
            super(itemView);
            rl_reminder = itemView.findViewById(R.id.rl_reminder);
            iv_right = itemView.findViewById(R.id.iv_right);
            tv_reminder = itemView.findViewById(R.id.tv_reminder);
            divider = itemView.findViewById(R.id.divider);
        }
    }

    public interface EventItemCallback {

        void onItemClick(int position);
    }
}