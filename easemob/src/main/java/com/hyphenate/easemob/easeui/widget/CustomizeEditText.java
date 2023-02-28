package com.hyphenate.easemob.easeui.widget;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.util.AttributeSet;
import android.widget.EditText;

import com.hyphenate.easemob.easeui.utils.EaseSmileUtils;

public class CustomizeEditText extends EditText {
    public CustomizeEditText(Context context) {
        super(context);
    }

    public CustomizeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomizeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste) {
            ClipboardManager clip = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            if (clip != null) {
                String content = String.valueOf(clip.getText());
                content = content.replace("&nbsp;", " ");
                if (content.contains("<html>") ||
                        content.contains("<header>") ||
                        content.contains("<body>") ||
                        content.contains("<div>") ||
                        content.contains("<a>") ||
                        content.contains("<h>") ||
                        content.contains("<ul>") ||
                        content.contains("<li>") ||
                        content.contains("<span>") ||
                        content.contains("<strong>") ||
                        content.contains("<b>") ||
                        content.contains("<em>") ||
                        content.contains("<cite>") ||
                        content.contains("<dfn>") ||
                        content.contains("<i>") ||
                        content.contains("<big>") ||
                        content.contains("<small>") ||
                        content.contains("<font>") ||
                        content.contains("<blockquote>") ||
                        content.contains("<tt>") ||
                        content.contains("<u>") ||
                        content.contains("<del>") ||
                        content.contains("<s>") ||
                        content.contains("<strike>") ||
                        content.contains("<sub>") ||
                        content.contains("<sup>") ||
                        content.contains("<img>") ||
                        content.contains("<h1>") ||
                        content.contains("<h2>") ||
                        content.contains("<h3>") ||
                        content.contains("<h4>") ||
                        content.contains("<h5>") ||
                        content.contains("<h6>") ||
                        content.contains("<p>") ||
                        (content.contains("&lt;") &&
                                content.contains("&gt;"))) {
                    clip.setText(Html.fromHtml(content));
                } else {
                    Spannable span = EaseSmileUtils.getSmiledText(getContext(), content);
                    getText().insert(getSelectionStart(), span);
                    // 设置内容
//                    clip.setText(content);
                    return true;
                }
            }
        }
        return super.onTextContextMenuItem(id);
    }


    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

}
