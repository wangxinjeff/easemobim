package com.hyphenate.easemob.im.officeautomation.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;

import com.hyphenate.media.EMCallSurfaceView;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 25/07/2018
 */

public class CircleCallSurfaceView extends EMCallSurfaceView {
	public CircleCallSurfaceView(Context context) {
		super(context);
	}

	public CircleCallSurfaceView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	public CircleCallSurfaceView(Context context, AttributeSet attributeSet, int i) {
		super(context, attributeSet, i);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int height = getMeasuredHeight();
		Path path = new Path();
		path.addCircle(height / 2, height / 2, height / 2, Path.Direction.CCW);
		canvas.clipPath(path, Region.Op.REPLACE);
		super.onDraw(canvas);
	}
}
