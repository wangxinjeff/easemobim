package com.hyphenate.easemob.pickerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 14/12/2018
 * 处理recyclerview在adapter内调用notifyItemChanged崩溃的解决方法
 */
public class MyGridLayoutManager extends GridLayoutManager {
	public MyGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public MyGridLayoutManager(Context context, int spanCount) {
		super(context, spanCount);
		setItemPrefetchEnabled(false);
	}

	public MyGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
		super(context, spanCount, orientation, reverseLayout);
	}

	@Override
	public boolean supportsPredictiveItemAnimations() {
		return false;
	}

	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
		//override this method and implement code as below
		try {
			super.onLayoutChildren(recycler, state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void collectAdjacentPrefetchPositions(int dx, int dy, RecyclerView.State state, LayoutPrefetchRegistry layoutPrefetchRegistry) {
		try {
			super.collectAdjacentPrefetchPositions(dx, dy, state, layoutPrefetchRegistry);
		} catch (IllegalArgumentException e) {
			Log.e("TAG","catch exception");
		}
	}

	@Override
	public void collectInitialPrefetchPositions(int adapterItemCount, LayoutPrefetchRegistry layoutPrefetchRegistry) {
		try {
			super.collectInitialPrefetchPositions(adapterItemCount, layoutPrefetchRegistry);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
