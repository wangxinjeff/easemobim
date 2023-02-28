package com.hyphenate.easemob.im.mp.view.contextmenu;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.AttachPopupView;

public class CustomLayout extends AttachPopupView {

    private OnSelectListener listener;

    public void setOnSelectListener(OnSelectListener listener) {
        this.listener = listener;
    }

    public CustomLayout(@NonNull final Context context, String[] titles, int[] icons) {
        super(context);
        attachPopupContainer.addView(new ContextMenuLayout.Builder(context).setTitleAndIconData(titles, icons).setOnItemListener(new ContextMenuLayout.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                dismiss();
                if(listener != null) {
                    listener.onSelected(position);
                }
            }
        }).build());
    }

    @Override
    protected int getImplLayoutId() {
        return 0;
    }

    public interface OnSelectListener {
        void onSelected(int position);
    }

}
