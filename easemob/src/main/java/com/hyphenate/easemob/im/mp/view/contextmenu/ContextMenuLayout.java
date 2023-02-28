package com.hyphenate.easemob.im.mp.view.contextmenu;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hyphenate.easemob.R;
import com.hyphenate.util.DensityUtil;


public class ContextMenuLayout extends LinearLayout {
    private String[] titles;
    private int[] iconResources;
    private OnItemClickListener listener;

    public ContextMenuLayout(Context context) {
        super(context);
        init(context);
    }

    public ContextMenuLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setBackgroundResource(R.drawable.bg_cm_border);
        setOrientation(LinearLayout.VERTICAL);
    }

    private void buildViews() {
        int pxValue = DensityUtil.dip2px(getContext(), 11);
        if(titles != null && titles.length > 0) {
            for (int j = 0; j < ((titles.length - 1) / 5) + 1; j++) {
                LinearLayout llLayout = new LinearLayout(getContext());
                llLayout.setOrientation(LinearLayout.HORIZONTAL);
                llLayout.setPadding(pxValue, pxValue, pxValue, pxValue);
                if (j > 0) {
                    View lineView = new View(getContext());
                    lineView.setBackgroundColor(Color.parseColor("#ffe1e4ea"));
                    addView(lineView, LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 1));
                }

                for (int i = j * 5; i < Math.min((j + 1) * 5 , titles.length); i++) {
                    View childView = View.inflate(getContext(), R.layout.layout_item_cm, null);
                    TextView tvTitle = childView.findViewById(R.id.tv_title);
                    tvTitle.setText(titles[i]);
                    ImageView ivImg = childView.findViewById(R.id.iv_img);
                    if(iconResources != null && iconResources.length > i) {
                        ivImg.setImageResource(iconResources[i]);
                    } else {
                        ivImg.setVisibility(View.GONE);
                    }
                    llLayout.addView(childView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    final int position = i;
                    childView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(listener != null) {
                                listener.onItemClick(position);
                            }
                        }
                    });
                }
                addView(llLayout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            }
        }
    }

    public static class Builder {
        private ContextMenuLayout layoutV;
        public Builder(Context context) {
            layoutV = new ContextMenuLayout(context);
        }

        public Builder setTitleAndIconData(String[] titles, int[] iconsRes) {
            layoutV.titles = titles;
            layoutV.iconResources = iconsRes;
            return this;
        }

        public Builder setOnItemListener(OnItemClickListener listener) {
            layoutV.listener = listener;
            return this;
        }

        public ContextMenuLayout build()  {
            layoutV.buildViews();
            return layoutV;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }



}
