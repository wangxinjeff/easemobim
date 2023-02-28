package com.hyphenate.easemob.im.mp.ui.group;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.im.mp.ui.group.holder.GroupInviteHolder;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import drawthink.expandablerecyclerview.adapter.BaseRecyclerViewAdapter;
import drawthink.expandablerecyclerview.bean.RecyclerViewData;

public class GroupInviteAdapter extends BaseRecyclerViewAdapter<String, InviteEntity, GroupInviteHolder> {

    private Context ctx;
    private LayoutInflater mInflater;
    private OnItemOperateListener operateListener;

    public GroupInviteAdapter(Context ctx, List<RecyclerViewData> datas) {
        super(ctx, datas);
        mInflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
    }

    public void setOperateListener(OnItemOperateListener listener) {
        this.operateListener = listener;
    }
    /**
     * head View数据设置
     * @param holder
     * @param groupPos
     * @param position
     * @param groupData
     */
    @Override
    public void onBindGroupHolder(GroupInviteHolder holder, int groupPos,int position,String groupData) {
        holder.tvTitle.setText(groupData);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void setAllDatas(List<RecyclerViewData> allDatas) {
        super.setAllDatas(allDatas);
    }

    /**
     * child View数据设置
     * @param holder
     * @param groupPos
     * @param childPos
     * @param position
     * @param childData
     */
    @Override
    public void onBindChildpHolder(GroupInviteHolder holder, int groupPos,int childPos,int position, InviteEntity childData) {
        holder.tvName.setText(childData.getUserRealName());
        holder.tvInvitorName.setText("邀请人:" + childData.getInvitorRealName());
        holder.btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operateListener != null) {
                    operateListener.itemOperate(groupPos, childPos, position, childData,false);
                }
            }
        });
        holder.btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operateListener != null) {
                    operateListener.itemOperate(groupPos, childPos, position, childData,false);
                }
            }
        });
    }

    @Override
    public View getGroupView(ViewGroup parent) {
        return mInflater.inflate(R.layout.title_item_layout,parent,false);
    }

    @Override
    public View getChildView(ViewGroup parent) {
        return mInflater.inflate(R.layout.item_layout_group_invite,parent,false);
    }

    @Override
    public GroupInviteHolder createRealViewHolder(Context ctx, View view, int viewType) {
        return new GroupInviteHolder(ctx,view,viewType);
    }

    /**
     * true 全部可展开
     * fasle  同一时间只能展开一个
     * */
    @Override
    public boolean canExpandAll() {
        return false;
    }


    public interface OnItemOperateListener {
        void itemOperate(int groupPos,int childPos,int position, InviteEntity childData, boolean isRefuse);
    }
}
