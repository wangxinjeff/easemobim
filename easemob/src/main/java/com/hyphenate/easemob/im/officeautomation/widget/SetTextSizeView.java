package com.hyphenate.easemob.im.officeautomation.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.hyphenate.easemob.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 02/06/2018
 * <p>
 * 仿微信设置字体大小的view
 * 自定义属性有：1.一共多少格 2.线条颜色与粗细 3.圆的半径和颜色
 */

public class SetTextSizeView extends View {

	private int defaultLineColor = Color.rgb(33, 33, 33);
	private int defaultLineWidth;
	private int defaultMax = 5;
	private int defaultCircleColor = Color.WHITE;
	private int defaultCircleRadius;
	private int defaultPosition = 1;

	//一种有多少格
	private int max = 5;
	//线条颜色
	private int lineColor;
	//线条粗细
	private int lineWidth;
	//突出部分的线条高度
	private int lineHeight;
	//圆半径
	private int circleRadius;
	private int circleColor;
	//一段的宽度，根据总宽度和总格数计算得来
	private int itemWidth;
	//控件的宽度
	private int height;
	private int width;
	//当前所在位置
	private int currentProgress = defaultPosition;
	//画笔
	private Paint mLinePaint;
	//文字画笔
	private Paint mTextPaint;
	private Paint mCirclePaint;
	//滑动过程中x坐标
	private float currentX = 0;
	//有效数据点
	private List<Point> points = new ArrayList<>();

	private float circleX;
	private float circleY;
	private int marginText;
	private int marginTop;
	private Rect mTextRect;

	public SetTextSizeView(Context context) {
		this(context, null);
	}


	public SetTextSizeView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		// initDefault
		defaultLineWidth = dp2px(context, 2);
		defaultCircleRadius = dp2px(context, 35);

		lineColor = Color.rgb(33, 33, 33);
		lineWidth = dp2px(context, 2);
		marginText = dp2px(context, 10);

		circleColor = Color.WHITE;

		// initCustomAttrs
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SetTextSizeView);
		final int N = typedArray.getIndexCount();
		for (int i = 0; i < N; i++) {
			initCustomAttr(typedArray.getIndex(i), typedArray);
		}
		typedArray.recycle();
		//初始化画笔
		mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mLinePaint.setColor(lineColor);
		mLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mLinePaint.setStrokeWidth(lineWidth);

		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(Color.GRAY);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setTextSize(dp2px(getContext(), 18));

		String str = getNormalText(context);
		mTextRect = new Rect();
		mTextPaint.getTextBounds(str, 0, str.length(), mTextRect);
		marginTop = mTextRect.height() + marginText * 2;

		mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCirclePaint.setColor(circleColor);
		mCirclePaint.setStyle(Paint.Style.FILL);
		//设置阴影效果
		setLayerType(LAYER_TYPE_SOFTWARE, null);
		mCirclePaint.setShadowLayer(2, -1, 1, Color.rgb(213, 213, 213));
	}

	private void initCustomAttr(int attr, TypedArray typedArray) {
		if (attr == R.styleable.SetTextSizeView_lineColor) {
			lineColor = typedArray.getColor(attr, defaultLineColor);
		} else if (attr == R.styleable.SetTextSizeView_circleColor) {
			circleColor = typedArray.getColor(attr, defaultCircleColor);
		} else if (attr == R.styleable.SetTextSizeView_lineWidth) {
			lineWidth = typedArray.getDimensionPixelOffset(attr, defaultLineWidth);
		} else if (attr == R.styleable.SetTextSizeView_circleRadius) {
			circleRadius = typedArray.getDimensionPixelSize(attr, defaultCircleRadius);
		} else if (attr == R.styleable.SetTextSizeView_totalCount) {
			max = typedArray.getInteger(attr, defaultMax);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		height = h;
		width = w;
		circleY = (height - marginTop) / 2;
		lineHeight = dp2px(getContext(), 4);
		// 横线宽度是总高度 - 2个圆的半径
		itemWidth = (w - 2 * circleRadius) / max;
		// 把可点击点保存起来
		for (int i = 0; i <= max; i++) {
			points.add(new Point(circleRadius + i * itemWidth, (int)circleY));
		}

	}

	public String getNormalText(Context context){
		return context.getResources().getString(R.string.set_text_size_normal);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 先画中间的横线
		canvas.drawLine(points.get(0).x, (height - marginTop) / 2, points.get(points.size() - 1).x, (height - marginTop) / 2, mLinePaint);
		// 绘制刻度
		for (Point point : points) {
			canvas.drawLine(point.x, (height - marginTop) / 2 - lineHeight, point.x, (height - marginTop) / 2 + lineHeight, mLinePaint);
		}
		// 画圆
		if (canMove) {
			// 随手指滑动过程
			if (currentX < circleRadius) {
				currentX = circleRadius;
			}
			if (currentX > width - circleRadius) {
				currentX = width - circleRadius;
			}
			circleX = currentX;
		} else {
			//最终
			circleX = points.get(currentProgress).x;
		}
		//实体圆
		canvas.drawCircle(circleX, circleY, circleRadius, mCirclePaint);

		canvas.drawText(getNormalText(getContext()), points.get(1).x - mTextRect.width() / 2, points.get(1).y + circleRadius + marginText + mTextRect.height() / 2, mTextPaint);
	}

	float downX = 0;
	private boolean canMove = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// 判断是否是数据点
				downX = event.getX();
				canMove = isDownOnCircle(downX);
				break;
			case MotionEvent.ACTION_MOVE:
				if (canMove) {
					currentX = event.getX();
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				// 手指抬起之后就圆就不能在非有有效点
				currentX = 0;
				float upX = event.getX();
				if (canMove) {
					// 是滑动过来的， 要判断距离哪个有效点最近，就滑动到哪个点
					Point targetPoint = getNearesPoint(upX);
					if (targetPoint != null) {
						invalidate();
					}
				} else {
					if (Math.abs(downX - upX) < 30) {
						Point point = isValidPoint(upX);
						if (point != null) {
							invalidate();
						}
					}
				}
				if (onPointResultListener != null) {
					onPointResultListener.onPointResult(currentProgress);
				}
				downX = 0;
				canMove = false;
				break;
		}
		return true;
	}

	public void setCurrentProgress(int progress){
		this.currentProgress = progress;
		if (progress < points.size()){
			currentX = points.get(progress).x;
		}
		invalidate();
	}


	/**
	 * 滑动抬起之后，要滑动到最近的一个点那里
	 *
	 * @param x
	 * @return
	 */
	private Point getNearesPoint(float x) {
		for (int i = 0; i < points.size(); i++) {
			Point point = points.get(i);
			if (Math.abs(point.x - x) < itemWidth / 2) {
				currentProgress = i;
				return point;
			}
		}
		return null;
	}


	/**
	 * 判断是否点击到圆上
	 *
	 * @param x
	 * @return
	 */
	private boolean isDownOnCircle(float x) {
		return Math.abs(points.get(currentProgress).x - x) < circleRadius;
	}

	/**
	 * 判断是否是有效的点击点
	 *
	 * @param x
	 * @return
	 */
	private Point isValidPoint(float x) {
		for (int i = 0; i < points.size(); i++) {
			Point point = points.get(i);
			if (Math.abs(point.x - x) < 30) {
				currentProgress = i;
				return point;
			}
		}
		return null;
	}

	public void setOnPointResultListener(OnPointResultListener listener) {
		this.onPointResultListener = listener;
	}


	private OnPointResultListener onPointResultListener;

	public interface OnPointResultListener {
		void onPointResult(int position);
	}


	private int dp2px(Context context, int dpValue) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
	}


}
