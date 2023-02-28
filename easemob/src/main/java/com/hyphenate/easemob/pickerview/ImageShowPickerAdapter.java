package com.hyphenate.easemob.pickerview;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easemob.R;

import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 14/12/2018
 */
public class ImageShowPickerAdapter extends RecyclerView.Adapter<ImageShowPickerAdapter.ViewHolder> implements ImageShowPickerPicListener {

	private int maxNum;
	private Context context;
	private List<ImageShowPickerBean> list;
	private ImageLoaderInterface imageLoaderInterface;
	private ImageShowPickerListener pickerListener;

	private static int iconHeight;
	private int delPicRes;
	private int addPicRes;
	private boolean isShowAnim;
	private boolean isShowDel;
	private boolean insertBefore;

	public void setInsertBefore(boolean insertBefore){
		this.insertBefore = insertBefore;
	}

	public void setShowDel(boolean showDel){
		this.isShowDel = showDel;
	}

	public void setShowAnim(boolean showAnim){
		this.isShowAnim = showAnim;
	}

	public void setIconHeight(int iconHeight){
		this.iconHeight = iconHeight;
	}

	public void setDelPicRes(int delPicRes){
		this.delPicRes = delPicRes;
	}

	public void changeShowDel(boolean showDel){
		this.isShowDel = showDel;
		notifyDataSetChanged();
	}

	public void setAddPicRes(int addPicRes){
		this.addPicRes = addPicRes;
	}

	public ImageShowPickerAdapter(int maxNum, Context context, List<ImageShowPickerBean> list, ImageLoaderInterface imageLoaderInterface, ImageShowPickerListener pickerListener){
		this.maxNum = maxNum;
		this.context = context;
		this.list = list;
		this.imageLoaderInterface = imageLoaderInterface;
		this.pickerListener = pickerListener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		FrameLayout frameLayout = new FrameLayout(context);
		parent.addView(frameLayout);
		frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
		ViewHolder vh = new ViewHolder(frameLayout, imageLoaderInterface, this);

		frameLayout.addView(vh.iv_pic);
		frameLayout.addView(vh.iv_del);
		return vh;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		if (!insertBefore){
			if (list.size() == 0 || list.size() == position){
				if (imageLoaderInterface != null){
					imageLoaderInterface.displayImage(context, addPicRes, holder.iv_pic);
				}
				holder.iv_del.setVisibility(View.GONE);
			} else {
				if (null == list.get(position).getImageShowPickerUrl() || "".equals(list.get(position).getImageShowPickerUrl())){
					if (imageLoaderInterface != null){
						imageLoaderInterface.displayImage(context, list.get(position).getImageShowPickerDelRes(), holder.iv_del);
					}
				} else {
					if (imageLoaderInterface != null){
						imageLoaderInterface.displayImage(context, list.get(position).getImageShowPickerUrl(), holder.iv_pic);
					}
				}
				if (isShowDel){
					holder.iv_del.setVisibility(View.VISIBLE);
					holder.iv_del.setImageResource(delPicRes);
				} else {
					holder.iv_del.setVisibility(View.GONE);
				}
			}
		} else {
			if (position == 0){
				if (imageLoaderInterface != null){
					imageLoaderInterface.displayImage(context, addPicRes, holder.iv_pic);
				}
				holder.iv_del.setVisibility(View.GONE);
			} else {
				if (null == list.get(position - 1).getImageShowPickerUrl() || "".equals(list.get(position - 1).getImageShowPickerUrl())){
					if (imageLoaderInterface != null){
						imageLoaderInterface.displayImage(context, list.get(position - 1).getImageShowPickerDelRes(), holder.iv_del);
					}
				} else {
					if (imageLoaderInterface != null){
						imageLoaderInterface.displayImage(context, list.get(position - 1).getImageShowPickerUrl(), holder.iv_pic);
					}
				}
				if (isShowDel){
					holder.iv_del.setVisibility(View.VISIBLE);
					holder.iv_del.setImageResource(delPicRes);
				} else {
					holder.iv_del.setVisibility(View.GONE);
				}
			}
		}

	}

	@Override
	public int getItemCount() {
		return list.size() < maxNum ?  list.size() + 1 : list.size();
	}

	@Override
	public void onDelClickListener(int position) {
		int realPostion = position;
		if (insertBefore){
			if (position == 0) {
				return;
			}
			realPostion = position - 1;
		}
		ImageShowPickerBean item = list.remove(realPostion);
		if (isShowAnim){
			notifyItemRemoved(realPostion);
			if (list.size() - 1 >= 0 && list.get(list.size() - 1) == null){
				notifyItemChanged(list.size() - 1);
			} else if (list.size() - 1 == 0){
				notifyItemChanged(0);
			}
		} else {
			notifyDataSetChanged();
		}
		pickerListener.delOnClickListener(realPostion, item, maxNum - list.size());
	}

	@Override
	public void onPicClickListener(int position) {
		if (insertBefore){
			if (position == 0) {
				if (pickerListener != null){
					pickerListener.addOnClickListener(maxNum - position - 1);
				}
			} else {
				if (pickerListener != null){
					pickerListener.picOnClickListener(list, position - 1, maxNum > list.size() ? maxNum - list.size() - 1 :
							(list.get(maxNum - 1) == null ? 1 : 0));
				}
			}
		}else {
			if (position == list.size()) {
				if (pickerListener != null){
					pickerListener.addOnClickListener(maxNum - position - 1);
				}
			} else {
				if (pickerListener != null){
					pickerListener.picOnClickListener(list, position, maxNum > list.size() ? maxNum - list.size() - 1 :
							(list.get(maxNum - 1) == null ? 1 : 0));
				}
			}
		}



	}


	//自定义的ViewHolder，持有每个Item的的所有界面元素
	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		public View iv_pic;
		public ImageView iv_del;
		private ImageShowPickerPicListener picOnClickListener;

		public ViewHolder(View view, ImageLoaderInterface imageLoaderInterface, ImageShowPickerPicListener picOnClickListener){
			super(view);
			this.picOnClickListener = picOnClickListener;
			iv_pic = imageLoaderInterface.createImageView(view.getContext());
			FrameLayout.LayoutParams pic_params = new FrameLayout.LayoutParams(iconHeight, iconHeight);
			int paddingPx = SizeUtils.dp2px(view.getContext(), 2);
			int marginPx = SizeUtils.dp2px(view.getContext(), 5);
			pic_params.setMargins(marginPx, marginPx, marginPx, marginPx);
			iv_pic.setLayoutParams(pic_params);
			iv_del = new ImageView(view.getContext());
			FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(SizeUtils.dp2px(view.getContext(), 20), SizeUtils.dp2px(view.getContext(), 20));
			layoutParams.gravity = Gravity.TOP | Gravity.END;

			iv_del.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
			iv_del.setLayoutParams(layoutParams);
			iv_pic.setId(R.id.iv_image_show_picker_pic);
			iv_del.setId(R.id.iv_image_show_picker_del);
			iv_pic.setOnClickListener(this);
			iv_del.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			int i = v.getId();
			if (i == R.id.iv_image_show_picker_pic) {
				if (picOnClickListener != null) {
					picOnClickListener.onPicClickListener(getLayoutPosition());
				}
			} else if (i == R.id.iv_image_show_picker_del) {
				if (picOnClickListener != null){
					picOnClickListener.onDelClickListener(getLayoutPosition());
				}
			}
		}
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}
}
