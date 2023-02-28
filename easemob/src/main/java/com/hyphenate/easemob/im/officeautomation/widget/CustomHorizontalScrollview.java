package com.hyphenate.easemob.im.officeautomation.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.hyphenate.easemob.im.officeautomation.adapter.CustomListAdapter;

/**
 * @author qby
 * @time 2018/06/07 16:36
 * @content 自定义横向滑动标题栏
 */
public class CustomHorizontalScrollview extends HorizontalScrollView {
    Context context;
    int prevIndex = 0;

    public CustomHorizontalScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.setSmoothScrollingEnabled(true);

    }

    public void setAdapter(CustomListAdapter mAdapter) {

        fillViewWithAdapter(mAdapter);
    }

    public void fillViewWithAdapter(CustomListAdapter mAdapter) {
        if (getChildCount() == 0 || mAdapter == null)
            return;

        ViewGroup parent = (ViewGroup) getChildAt(0);

        parent.removeAllViews();

        for (int i = 0; i < mAdapter.getCount(); i++) {
            parent.addView(mAdapter.getView(i, null, parent));
        }
    }

    public void setCenter(int index) {

        ViewGroup parent = (ViewGroup) getChildAt(0);

        View preView = parent.getChildAt(prevIndex);
        preView.setBackgroundColor(Color.parseColor("#64CBD8"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(5, 5, 5, 5);
        preView.setLayoutParams(lp);

        View view = parent.getChildAt(index);
        view.setBackgroundColor(Color.RED);

        int screenWidth = ((Activity) context).getWindowManager()
                .getDefaultDisplay().getWidth();

        int scrollX = (view.getLeft() - (screenWidth / 2))
                + (view.getWidth() / 2);
        this.smoothScrollTo(scrollX, 0);
        prevIndex = index;
    }
}
