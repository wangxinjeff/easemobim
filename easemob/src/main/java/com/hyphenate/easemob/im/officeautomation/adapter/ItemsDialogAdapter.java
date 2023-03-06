package com.hyphenate.easemob.im.officeautomation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easemob.R;

import java.util.List;

public class ItemsDialogAdapter extends RecyclerView.Adapter<ItemsDialogAdapter.ItemsHolder> {

    private List<String> mData;
    private OnItemClickListener listener;

    @NonNull
    @Override
    public ItemsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemsHolder(LayoutInflater.from(parent.getContext()).inflate( R.layout.dialog_items_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsHolder holder, int position) {
        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<String> mData){
        this.mData = mData;
        notifyDataSetChanged();
    }

    public class ItemsHolder extends RecyclerView.ViewHolder{

        private TextView tvContent;

        public ItemsHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content);
        }

        public void bind(int position){
            tvContent.setText(mData.get(position));
            tvContent.setOnClickListener(v -> {
                if(listener != null){
                    listener.onClickItem(position);
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onClickItem(int position);
    }

    public void setListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
