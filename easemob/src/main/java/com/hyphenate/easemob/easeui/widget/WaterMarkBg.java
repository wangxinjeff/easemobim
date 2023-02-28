package com.hyphenate.easemob.easeui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 水印背景
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 19/11/2018
 */
@Deprecated
public class WaterMarkBg extends Drawable {

    private Paint paint = new Paint();
    private List<String> labels;
    private Context context;
    private int degress;//角度
    private int fontSize;//字体大小 单位sp

    /**
     * 初始化构造
     *
     * @param context  上下文
     * @param labels   水印文字列表 多行显示支持
     * @param degress  水印角度
     * @param fontSize 水印文字大小
     */
    public WaterMarkBg(Context context, List<String> labels, int degress, int fontSize) {
        this.labels = labels;
        this.context = context;
        this.degress = degress;
        this.fontSize = fontSize;
    }

    public WaterMarkBg(Context context) {
        MPUserEntity entity = MPClient.get().getCurrentUser();
        List<String> labelList = new ArrayList<>();
        labelList.add(entity.getPhone());
        this.labels = labelList;
        this.context = context;
        this.degress = -30;
        this.fontSize = 13;
    }

    public static WaterMarkBg create(Context context) {
        WaterMarkBg waterMarkBg = new WaterMarkBg(context);
        return waterMarkBg;
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        if (labels == null || labels.isEmpty()) {
            return;
        }
        int width = getBounds().right;
        int height = getBounds().bottom;

        canvas.drawColor(Color.parseColor("#F9F7F9"));
        paint.setColor(Color.parseColor("#50AEAEAE"));

        paint.setAntiAlias(true);
        paint.setTextSize(sp2px(context, fontSize));
        canvas.save();
        canvas.rotate(degress);
        float textWidth = paint.measureText(labels.get(0));
        int index = 0;
        for (int positionY = height / 5; positionY <= height; positionY += height / 10 + 80) {
            float fromX = -width + (index++ % 2) * textWidth;
            for (float positionX = fromX; positionX < width; positionX += textWidth * 2) {
                int spacing = 0;//间距
                for (String label : labels) {
                    canvas.drawText(label, positionX, positionY + spacing, paint);
                    spacing = spacing + 50;
                }
            }
        }
        canvas.restore();
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }


    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dp2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }
}