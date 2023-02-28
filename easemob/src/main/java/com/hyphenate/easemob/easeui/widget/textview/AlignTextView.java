package com.hyphenate.easemob.easeui.widget.textview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.utils.EaseSmileUtils;

public class AlignTextView extends AppCompatTextView {
    public AlignTextView(Context context) {
        super(context);
    }

    public AlignTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 获取用于显示当前文本的布局
        Layout layout = getLayout();
        if (layout == null) return;
        final int lineCount = layout.getLineCount();
        if (lineCount < 2) {
            //想只有一行 则不需要转化
            super.onDraw(canvas);
            return;
        }
        Paint.FontMetrics fm = getPaint().getFontMetrics();
        int textHeight = (int) (Math.ceil(fm.descent - fm.ascent));
        textHeight = (int) (textHeight * layout.getSpacingMultiplier() + layout.getSpacingAdd());
        measureText(getMeasuredWidth(), getText(), textHeight, canvas);
    }

    /**
     * 计算一行  显示的文字
     *
     * @param width      文本的宽度
     * @param text//文本内容
     * @param textHeight 文本大小
     */
    public void measureText(int width, CharSequence text, int textHeight, Canvas canvas) {
        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();
        float textWidth = StaticLayout.getDesiredWidth(text, paint);
        int textLength = text.length();
        float textSize = paint.getTextSize();
        Spannable spannable = EaseSmileUtils.getIconText(getContext(), text);
        if (textWidth < width) canvas.drawText(spannable, 0, textLength, 0, textSize, paint);   //不需要换行
        else {
            //需要换行
            CharSequence lineOne = getOneLine(width, text, paint);
            int lineOneNum = lineOne.length();
            int index = 0;
            if(text.toString().contains("[单选]") || text.toString().contains("[多选]")){
                index = text.toString().indexOf("[单选]");
                if(index <= 0){
                    index = text.toString().indexOf("[多选]");
                }
                if(index > 0 && index < lineOneNum){
                    canvas.drawText(lineOne, 0, index, 0, textSize, paint);
                    paint.setColor(getContext().getColor(R.color.theme_color));
                    float normalWidth = paint.measureText(lineOne, 0, index);
                    canvas.drawText(lineOne, index, lineOneNum, normalWidth, textSize, paint);
                } else {
                    canvas.drawText(lineOne, 0, lineOneNum, 0, textSize, paint);
                }
            } else {
                spannable = EaseSmileUtils.getIconText(getContext(), lineOne);
                StaticLayout.Builder layoutBuilder = StaticLayout.Builder.obtain(spannable, 0, lineOneNum, paint, canvas.getWidth());
                layoutBuilder.build().draw(canvas);
            }
//            canvas.drawText(spannable, 0, lineOneNum, 0, textSize, paint);
            //画第二行
            if (lineOneNum < textLength) {
                CharSequence lineTwo = text.subSequence(lineOneNum, textLength);
                lineTwo = getTwoLine(width, lineTwo, paint);
                int twoIndex = index + 4 - lineOneNum;
                if(twoIndex > 0){
                    paint.setColor(getCurrentTextColor());
                    if(twoIndex <= 4){
                        paint.setColor(getContext().getColor(R.color.theme_color));
                        canvas.drawText(lineTwo, 0, lineTwo.length(), 0, textSize + textHeight, paint);
                    } else {
                        canvas.drawText(lineTwo, 0, twoIndex - 4, 0, textSize + textHeight, paint);
                        paint.setColor(getContext().getColor(R.color.theme_color));
                        float normalWidth = paint.measureText(lineTwo, 0, twoIndex - 4);
                        canvas.drawText(lineTwo, twoIndex - 4, lineTwo.length(), normalWidth, textSize + textHeight, paint);
                    }
                } else {
                    canvas.drawText(lineTwo, 0, lineTwo.length(), 0, textSize + textHeight, paint);
                }
            }
        }
    }

    public CharSequence getTwoLine(int width, CharSequence lineTwo, TextPaint paint) {
        int length = lineTwo.length();
        String ellipsis = "...";
        float ellipsisWidth = StaticLayout.getDesiredWidth(ellipsis, paint);
        for (int i = 0; i < length; i++) {
            CharSequence cha = lineTwo.subSequence(0, i);
            float textWidth = StaticLayout.getDesiredWidth(cha, paint);
            if (textWidth + ellipsisWidth > width) {//需要显示 ...
                lineTwo = lineTwo.subSequence(0, i - 1) + ellipsis;
                return lineTwo;
            }
        }
        return lineTwo;
    }

    /**
     * 获取第一行 显示的文本
     *
     * @param width 控件宽度
     * @param text  文本
     * @param paint 画笔
     * @return
     */
    public CharSequence getOneLine(int width, CharSequence text, TextPaint paint) {
        CharSequence lineOne = null;
        int length = text.length();
        for (int i = 0; i < length; i++) {
            lineOne = text.subSequence(0, i);
            float textWidth = StaticLayout.getDesiredWidth(lineOne, paint);
            if (textWidth >= width) {
                CharSequence lastWorld = text.subSequence(i - 1, i);//最后一个字符
                float lastWidth = StaticLayout.getDesiredWidth(lastWorld, paint);//最后一个字符的宽度
                if (textWidth - width < lastWidth) {//不够显示一个字符 //需要缩放
                    lineOne = text.subSequence(0, i - 1);
                }
                return lineOne;
            }
        }
        return lineOne;
    }
}
