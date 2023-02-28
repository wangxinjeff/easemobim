package com.hyphenate.easemob.pickerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easemob.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 14/12/2018
 * 单纯的图片展示选项控件
 */
public class ImageShowPickerView extends LinearLayout {

	private RecyclerView mRecyclerView;
	/**
	 * 图片加载接口
	 */
	private ImageLoaderInterface imageLoaderInterface;

	private ImageShowPickerListener pickerListener;

	private Context context;

	private ImageShowPickerAdapter adapter;

	private List<ImageShowPickerBean> list;

	/**
	 * 默认单个大小
	 */
	private static final int PIC_SIZE = 80;
	/**
	 * 默认单行显示数量
	 */
	private static final int ONE_LINE_SHOW_NUM = 4;
	/**
	 * 默认单个大小
	 */
	private static final int MAX_NUM = 9;
	/**
	 * 单个item大小
	 */
	private int mPicSize;
	/**
	 * 添加图片
	 */
	private int mAddLabel;
	/**
	 * 删除图片
	 */
	private int mDelLabel;
	/**
	 * 是否显示删除
	 */
	private boolean isShowDel;
	/**
	 * 单行显示数量，默认4
	 *
	 */
	private int oneLineShowNum;
	/**
	 * 是否展示动画，默认false
	 */
	private boolean isShowAnim;
	/**
	 * 最大数量
	 */
	private int maxNum;
	/**
	 * 设置插入顺序，默认是插入到后面
	 */
	private boolean insertBefore;

	/**
	 * 设置插入顺序
	 */
	public void setInsertBefore(boolean insertBefore){
		this.insertBefore = insertBefore;
	}


	/**
	 * 设置单个item大小
	 */
	public void setPicSize(int picSize){
		this.mPicSize = picSize;
	}

	/**
	 * 设置增加图片
	 */
	public void setAddLabel(int addLabel){
		this.mAddLabel = addLabel;
	}

	/**
	 * 设置删除图片
	 */
	public void setDelLabel(int delLabel){
		this.mDelLabel = delLabel;
	}

	/**
	 * 设置是否显示删除
	 */
	public void setShowDel(boolean showDel){
		this.isShowDel = showDel;
	}

	public void changeShowDel(boolean showDel){
		isShowDel = showDel;
		if (adapter != null){
			adapter.changeShowDel(showDel);
		}
	}

	/**
	 * 设置单行显示数量
	 */
	public void setOneLineShowNum(int oneLineShowNum){
		this.oneLineShowNum = oneLineShowNum;
	}

	/**
	 * 设置是否显示动画
	 */
	public void setShowAnim(boolean showAnim){
		this.isShowAnim = showAnim;
	}

	/**
	 * 设置最大允许图片数量
	 * @param maxNum
	 */
	public void setMaxNum(int maxNum){
		this.maxNum = maxNum;
	}

	public ImageShowPickerView(Context context){
		this(context, null);
	}

	public ImageShowPickerView(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}

	public ImageShowPickerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		init(getContext(), attrs);
	}

	private void init(Context context, AttributeSet attrs){
		list = new ArrayList<>();
		viewTypeArray(context, attrs);
		mRecyclerView = new RecyclerView(context);
		addView(mRecyclerView);
	}

	private void viewTypeArray(Context context, AttributeSet attrs){
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageShowPickerView);
		mPicSize = typedArray.getDimensionPixelSize(R.styleable.ImageShowPickerView_pic_size, SizeUtils.dp2px(getContext(), PIC_SIZE));
		isShowDel = typedArray.getBoolean(R.styleable.ImageShowPickerView_is_show_del, true);
		isShowAnim = typedArray.getBoolean(R.styleable.ImageShowPickerView_is_show_anim, false);
		mAddLabel = typedArray.getResourceId(R.styleable.ImageShowPickerView_add_label, R.drawable.image_show_piceker_add);
		mDelLabel = typedArray.getResourceId(R.styleable.ImageShowPickerView_del_label, R.drawable.image_show_piceker_del);
		oneLineShowNum = typedArray.getInt(R.styleable.ImageShowPickerView_one_line_show_num, ONE_LINE_SHOW_NUM);
		maxNum = typedArray.getInt(R.styleable.ImageShowPickerView_max_num, MAX_NUM);
		typedArray.recycle();
	}

	/**
	 * 设置选择器监听
	 *
	 * @param pickerListener 选择器监听事件
	 */
	public void setPickerListener(ImageShowPickerListener pickerListener) {
		this.pickerListener = pickerListener;
	}

	/**
	 * 图片加载器
	 *
	 * @param imageLoaderInterface
	 */
	public void setImageLoaderInterface(ImageLoaderInterface imageLoaderInterface) {
		this.imageLoaderInterface = imageLoaderInterface;

	}

	/**
	 * 添加新数据
	 *
	 * @param bean
	 * @param <T>
	 */
	public <T extends ImageShowPickerBean> void addData(T bean) {
		if (bean == null) {
			return;
		}
		if (insertBefore) {
			this.list.add(0, bean);
		}else{
			this.list.add(bean);
		}
		if (isShowAnim) {
			if (adapter != null) {
				adapter.notifyItemChanged(list.size() - 1);
				adapter.notifyItemChanged(list.size());
			}
		} else {
			adapter.notifyDataSetChanged();
		}

	}

	/**
	 * 添加新数据
	 *
	 * @param list
	 * @param <T>
	 */
	public <T extends ImageShowPickerBean> void addData(List<T> list) {
		if (list == null) {
			return;
		}
		this.list.addAll(list);

		if (isShowAnim) {
			if (adapter != null)
				adapter.notifyItemRangeChanged(this.list.size() - list.size(), list.size());

		} else {
			if (adapter != null)
				adapter.notifyDataSetChanged();
		}

	}


	/**
	 * 获取picker的list数据集合
	 *
	 * @param <T>
	 * @return
	 */
	public <T extends ImageShowPickerBean> List<T> getDataList() {
		return (List<T>) list;
	}


	/**
	 * 首次添加数据
	 *
	 * @param list
	 * @param <T>
	 */
	public <T extends ImageShowPickerBean> void setNewData(List<T> list) {
		if (this.list == null){
			this.list = new ArrayList<>();
		}else{
			this.list.clear();
		}
		this.list.addAll(list);
		if (isShowAnim) {
			if (adapter != null)
				adapter.notifyItemRangeChanged(this.list.size() - list.size(), list.size());

		} else {
			if (adapter != null)
				adapter.notifyDataSetChanged();
		}

	}




	/**
	 * 最后调用方法显示，必须最后调用
	 */
	public void show() {
		MyGridLayoutManager layoutManager = new MyGridLayoutManager(context, oneLineShowNum);
//		GridLayoutManager layoutManager = new GridLayoutManager(context, oneLineShowNum);
//		layoutManager.setAutoMeasureEnabled(true);
		layoutManager.setSmoothScrollbarEnabled(false);
		mRecyclerView.setLayoutManager(layoutManager);
		int color = Color.GRAY;
		mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(context, oneLineShowNum, 1));
//		mRecyclerView.addItemDecoration(new GridItemDecoration(context, 1, color) {
//			@Override
//			public boolean[] getItemSidesIsHaveOffsets(int itemPosition) {
//				//顺序:left, top, right, bottom
//				boolean[] booleans = {false, false, false, false};
//				if (itemPosition == 0) {
//					//因为给 RecyclerView 添加了 header，所以原本的 position 发生了变化
//					//position 为 0 的地方实际上是 header，真正的列表 position 从 1 开始
//				} else {
//					switch (itemPosition % 2) {
//						case 0:
//							//每一行第二个只显示左边距和下边距
//							booleans[0] = true;
//							booleans[3] = true;
//							break;
//						case 1:
//							//每一行第一个显示右边距和下边距
//							booleans[2] = true;
//							booleans[3] = true;
//							break;
//					}
//				}
//				return booleans;
//			}
//		});

		adapter = new ImageShowPickerAdapter(maxNum, context, list, imageLoaderInterface, pickerListener);
		adapter.setHasStableIds(true);
		adapter.setAddPicRes(mAddLabel);
		adapter.setDelPicRes(mDelLabel);
		adapter.setIconHeight(mPicSize);
		adapter.setShowDel(isShowDel);
		adapter.setInsertBefore(insertBefore);
		adapter.setShowAnim(isShowAnim);
		mRecyclerView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}



}
