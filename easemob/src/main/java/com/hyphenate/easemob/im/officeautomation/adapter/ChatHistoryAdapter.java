package com.hyphenate.easemob.im.officeautomation.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.utils.EaseSmileUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.R;
import com.hyphenate.util.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/06/14.
 * recyclerView上拉加载适配器,聊天记录
 */

public class ChatHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ChatHistoryAdapter";
    private Context context;
    private LayoutInflater mInflater;
    private List<Object> mList;
    private ChatListItemCallback callback;
    public final static int CLICK_TYPE_IMAGE = 0;
    public final static int CLICK_TYPE_VIDEO = 1;

    public ChatHistoryAdapter(Context context, ArrayList<Object> mList, ChatListItemCallback callback) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = mList;
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
        //进行判断显示类型，来创建返回不同的View
        View view = mInflater.inflate(R.layout.item_chat_list_history, parent, false);
        //这边可以做一些属性设置，甚至事件监听绑定
        //view.setBackgroundColor(Color.RED);
        return new ItemViewHolder(view);
    }

    /**
     * 数据的绑定显示
     *
     * @param holder   holder
     * @param position 索引
     */
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            try {
                Object obj = mList.get(position);
                JSONObject jsonObject = new JSONObject(new Gson().toJson(obj));
                String nick = jsonObject.optString("nick");
                String avatar = jsonObject.optString("avatar");
                long timestamp = jsonObject.optLong("timestamp");
                //昵称
                itemHolder.name.setText(nick);
                //日期、时间
                String timeStr = DateUtils.getTimestampString(new Date(timestamp));
                itemHolder.time.setText(timeStr);
                itemHolder.avatar_container.setVisibility(View.VISIBLE);
                showAvatar(itemHolder, nick, avatar);

                //类型
                String type = jsonObject.optString("type");
                if ("txt".equalsIgnoreCase(type)) {
                    String msg = jsonObject.optString("msg");
                    itemHolder.message.setVisibility(View.VISIBLE);
                    itemHolder.iv_image.setVisibility(View.GONE);
                    itemHolder.rl_video.setVisibility(View.GONE);
                    itemHolder.itemView.setOnClickListener(null);
//                    MyLog.e(TAG, "测试聊天记录：" + msg);
                    Spannable span = EaseSmileUtils.getSmiledText(context, msg);
                    // 设置内容
                    itemHolder.message.setText(span, TextView.BufferType.SPANNABLE);
                } else if ("image".equalsIgnoreCase(type)){
                    itemHolder.message.setVisibility(View.GONE);
                    String thumb_url = jsonObject.optString("thumb_url");
                    String remote_url = jsonObject.optString("remote_url");
                    JSONObject size = jsonObject.getJSONObject("size");
                    int width = size.getInt("width");
                    if (width == 0) {
                        width = 300;
                    }
                    int height = size.getInt("height");
                    if (height == 0) {
                        height = 300;
                    }
                    itemHolder.iv_image.setVisibility(View.VISIBLE);
                    itemHolder.rl_video.setVisibility(View.GONE);
                    GlideUtils.load(context, remote_url, R.drawable.ease_default_image, itemHolder.iv_image);
//                    Glide.with(context).load(remote_url).apply(RequestOptions.overrideOf(width, height)).into(itemHolder.iv_image);
                    itemHolder.iv_image.setOnClickListener(view -> {
                        callback.onItemClick(position, CLICK_TYPE_IMAGE, thumb_url, remote_url);
                    });
                }else if ("video".equalsIgnoreCase(type)){
                    itemHolder.message.setVisibility(View.GONE);
                    String thumb_url = jsonObject.optString("thumb_url");
                    String remote_url = jsonObject.optString("remote_url");
                    JSONObject size = jsonObject.getJSONObject("size");
                    int width = size.getInt("width");
                    if (width == 0) {
                        width = 300;
                    }
                    int height = size.getInt("height");
                    if (height == 0) {
                        height = 300;
                    }
                    itemHolder.iv_image.setVisibility(View.GONE);
                    itemHolder.rl_video.setVisibility(View.VISIBLE);
                    GlideUtils.load(context, thumb_url, itemHolder.iv_video);
//                    Glide.with(context).load(thumb_url).apply(RequestOptions.overrideOf(width, height)).into(itemHolder.iv_video);
                    itemHolder.rl_video.setOnClickListener(view -> {
                        callback.onItemClick(position, CLICK_TYPE_VIDEO, thumb_url, remote_url);
                    });
                }else if ("voice".equalsIgnoreCase(type)){
                    itemHolder.message.setVisibility(View.VISIBLE);
                    itemHolder.iv_image.setVisibility(View.GONE);
                    itemHolder.rl_video.setVisibility(View.GONE);
                    itemHolder.itemView.setOnClickListener(null);
                    // 设置内容
                    itemHolder.message.setText(R.string.voice_prefix);
                }else if("file".equalsIgnoreCase(type)){
                    itemHolder.message.setVisibility(View.VISIBLE);
                    itemHolder.iv_image.setVisibility(View.GONE);
                    itemHolder.rl_video.setVisibility(View.GONE);
                    itemHolder.itemView.setOnClickListener(null);
                    // 设置内容
                    itemHolder.message.setText(R.string.file);
                }else{
                    itemHolder.message.setVisibility(View.VISIBLE);
                    itemHolder.iv_image.setVisibility(View.GONE);
                    itemHolder.rl_video.setVisibility(View.GONE);
                    itemHolder.itemView.setOnClickListener(null);
                    // 设置内容
                    itemHolder.message.setText(R.string.special_message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            holder.itemView.setTag(position);
        }
    }

    /**
     * 显示头像
     *
     * @param itemHolder 条目持有者
     * @param nick       昵称
     * @param avatar     头像
     */
    private void showAvatar(ItemViewHolder itemHolder, String nick, String avatar) {
        AvatarUtils.setAvatarContent(context, nick, avatar, itemHolder.iv_avatar);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //自定义的ItemViewHolder，持有每个user的的所有界面元素
    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        /**
         * who you chat with
         */
        private TextView name;
        /**
         * content of last message
         */
        private TextView message;
        private ImageView iv_image;

        private RelativeLayout rl_video;
        private ImageView iv_video;
        /**
         * time of last message
         */
        private TextView time;
        /**
         * avatar
         */
        private AvatarImageView iv_avatar;
        /**
         * container of avatar
         */
        private RelativeLayout avatar_container;

        private ItemViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            message = itemView.findViewById(R.id.message);
            iv_image = itemView.findViewById(R.id.iv_image);
            iv_video = itemView.findViewById(R.id.iv_video);
            rl_video = itemView.findViewById(R.id.rl_video);
            time = itemView.findViewById(R.id.time);
            avatar_container = itemView.findViewById(R.id.avatar_container);
        }
    }


    public interface ChatListItemCallback {

        void onItemClick(int position, int clickType, String thumb_url, String remote_url);
    }
}