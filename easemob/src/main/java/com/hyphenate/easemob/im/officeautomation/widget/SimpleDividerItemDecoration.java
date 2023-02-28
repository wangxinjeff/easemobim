package com.hyphenate.easemob.im.officeautomation.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.utils.DensityUtil;

/**
 * Created by qby on 2018/06/09.
 * RecyclerView的条目分割线
 */

public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;   //分割线Drawable
    private int mDividerHeight;  //分割线高度
    private boolean isGone;

    /**
     * 使用line_divider中定义好的颜色,高度
     *
     * @param context 上下文
     */
    public SimpleDividerItemDecoration(Context context) {
        this(context, DensityUtil.dp2px(context, 0.5f));
    }

    /**
     * 使用line_divider中定义好的颜色
     *
     * @param context       上下文
     * @param dividerHeight 分割线高度
     */
    public SimpleDividerItemDecoration(Context context, int dividerHeight) {
        mDivider = ContextCompat.getDrawable(context, R.drawable.line_divider);
        mDividerHeight = dividerHeight;
    }

    /**
     * @param context       上下文
     * @param divider       分割线Drawable
     * @param dividerHeight 分割线高度
     */
    public SimpleDividerItemDecoration(Context context, Drawable divider, int dividerHeight) {
        if (divider == null) {
            mDivider = ContextCompat.getDrawable(context, R.drawable.line_divider);
        } else {
            mDivider = divider;
        }
        mDividerHeight = dividerHeight;
    }

    //获取分割线尺寸
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (i == 0 && isGone) {
                continue;
            }
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDividerHeight;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void setFirstDividerGone(boolean isGone) {
        this.isGone = isGone;
    }
}