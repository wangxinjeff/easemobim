package com.hyphenate.easemob.im.mp.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easemob.R;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 01/12/2018
 */
public class GridAppsAdapter extends RecyclerView.Adapter<GridAppsAdapter.MyViewHolder>{

	private Context mContext;
	public OnItemClickListener mOnItemClickListener = null;
	private String[] mDatas = new String[]{"签到", "视频会议"};
	private int[] mDrawable = new int[]{R.drawable.oa_icon_apps_signin, R.drawable.oa_icon_apps_videoconf};

	public GridAppsAdapter(Context context){
		mContext = context;
	}

	public void setOnItemClickListener(OnItemClickListener listener){
		mOnItemClickListener = listener;
	}

	@NonNull
	@Override
	public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_app_item, parent,false));
	}

	@Override
	public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
		if (position < mDatas.length){
			holder.item_title.setText(mDatas[position]);
			holder.item_image.setImageResource(mDrawable[position]);
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mOnItemClickListener != null){
						mOnItemClickListener.onitemClick(position);
					}
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return 2;
	}


	public interface OnItemClickListener {
		void onitemClick(int position);
	}


	class MyViewHolder extends RecyclerView.ViewHolder {

		TextView item_title;
		ImageView item_image;

		public MyViewHolder(View itemView) {
			super(itemView);
			item_title = itemView.findViewById(R.id.item_title);
			item_image = itemView.findViewById(R.id.item_image);
		}
	}


}
