package com.hyphenate.easemob.easeui.widget.sticker.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.hyphenate.easemob.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 18/12/2018
 */
public class DownloadProgressView  extends View{

	private Paint bgPaint;
	private Paint tvPaint;
	private RectF rectF;
	private int strokeWidth;
	private int radius;
	private int progress = 30;
	private int status = 0;
	public static final int NOT_DOWNLOAD = 0;
	public static final int DOWNLONGING = 1;

	public DownloadProgressView(Context context){
		super(context);
		init();
	}

	public DownloadProgressView(Context context, AttributeSet attrs){
		super(context, attrs);
		init();
	}

	private void init(){
		this.bgPaint = new Paint(1);
		this.strokeWidth = getResources().getDimensionPixelOffset(R.dimen.download_progress_stroke_width);
		this.bgPaint.setStrokeWidth(strokeWidth);

		this.tvPaint = new Paint(1);
		this.tvPaint.setColor(Color.LTGRAY);
		this.tvPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.download_progress_text_size));
		this.tvPaint.setTextAlign(Paint.Align.CENTER);

		this.rectF = new RectF();
		this.rectF.left = this.strokeWidth / 2;
		this.rectF.top = this.strokeWidth / 2;

		this.radius = getResources().getDimensionPixelOffset(R.dimen.download_progress_radius);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (h != oldh){
			this.rectF.bottom = (h - this.strokeWidth / 2);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (status == 0){
			drawNotDownload(canvas);
			this.tvPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.download_text_size));
			this.tvPaint.setColor(-1);
			drawText(canvas, getResources().getString(R.string.download_sticker));
		} else {
			drawDownloading(canvas);
			this.tvPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.download_progress_text_size));
			this.tvPaint.setColor(1345532723);
			String text = this.progress + "%";
			drawText(canvas, text);
		}
	}

	public void setStatus(int status){
		this.status = status;
		invalidate();
	}

	public void setProgress(int progress){
		if (this.status == 0){
			this.status = 1;
		}
		this.progress = progress;
		invalidate();
	}

	private void drawNotDownload(Canvas canvas){
		this.bgPaint.setColor(-16737793);
		this.bgPaint.setStyle(Paint.Style.FILL);
		this.rectF.right = (getWidth() - this.strokeWidth);
		canvas.drawRoundRect(this.rectF, this.radius, this.radius, this.bgPaint);
	}

	private void drawDownloading(Canvas canvas){
		this.bgPaint.setColor(-9583361);
		this.bgPaint.setStyle(Paint.Style.STROKE);
		this.rectF.right = (getWidth() - this.strokeWidth);
		canvas.drawRoundRect(this.rectF, this.radius, this.radius, this.bgPaint);
		this.rectF.right = (this.rectF.left + this.progress / 100.0F * (getWidth() - this.strokeWidth));
		this.bgPaint.setStyle(Paint.Style.FILL);
		canvas.drawRoundRect(this.rectF, this.radius, this.radius, this.bgPaint);
	}

	private void drawText(Canvas canvas, String text){
		int x = getWidth() / 2;
		int y = (int) (getHeight() / 2 - (this.tvPaint.descent() + this.tvPaint.ascent()) / 2.0F);
		canvas.drawText(text, x, y, this.tvPaint);
	}

	@Retention(RetentionPolicy.SOURCE)
	public static @interface Status {}

}
