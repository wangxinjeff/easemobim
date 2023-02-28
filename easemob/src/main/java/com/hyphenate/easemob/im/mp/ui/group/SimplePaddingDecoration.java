package com.hyphenate.easemob.im.mp.ui.group;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easemob.R;

public class SimplePaddingDecoration extends RecyclerView.ItemDecoration {

    private int dividerHeight;

    public SimplePaddingDecoration(Context context) {
        dividerHeight = context.getResources().getDimensionPixelOffset(R.dimen.dp_5);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = dividerHeight; // 类似增加了一个bottom padding
    }
}
