package com.hyphenate.easemob.easeui.widget;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import androidx.annotation.NonNull;

import com.hyphenate.easemob.imlibs.mp.utils.MPLog;

public class UrlSpanNoUnderline extends URLSpan {

    public UrlSpanNoUnderline(String url) {
        super(url);

    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(Color.parseColor("#026FFE"));
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
//        super.onClick(widget);
        String url = getURL();
        MPLog.e("UrlSpanNoUnderline-onClick", url);
    }
}
