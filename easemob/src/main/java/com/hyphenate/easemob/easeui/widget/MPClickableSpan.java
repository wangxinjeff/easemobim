package com.hyphenate.easemob.easeui.widget;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import com.hyphenate.easemob.imlibs.mp.utils.MPLog;

public class MPClickableSpan  extends ClickableSpan {

    private String event;
    private View.OnClickListener listener;

    public MPClickableSpan(String event, View.OnClickListener listener) {
        this.event = event;
        this.listener = listener;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(Color.parseColor("#026FFE"));
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(@NonNull View widget) {
        if (listener != null) {
            listener.onClick(widget);
        }
        MPLog.e("MPClickableSpan", "onClick:" + event);
    }
}
