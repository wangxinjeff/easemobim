package com.hyphenate.easemob.easeui.glide;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 26/07/2018
 */
//
//public class GlideRoundTransform extends BitmapTransformation {
//
//	private static float radius = 0f;
//
//	public GlideRoundTransform(Context context) {
//		this(context, 3);
//	}
//
//	public GlideRoundTransform(Context context, int dp) {
//		super(context);
//		this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
//	}
//
//	public GlideRoundTransform(BitmapPool bitmapPool) {
//		super(bitmapPool);
//	}
//
//	@Override
//	protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
//		return roundCrop(pool, toTransform);
//	}
//
//	private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
//		if (source == null) return null;
//		Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
//		if (result == null) {
//			result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
//		}
//		Canvas canvas = new Canvas(result);
//		Paint paint = new Paint();
//		paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
//		paint.setAntiAlias(true);
//		RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
//		canvas.drawRoundRect(rectF, radius, radius, paint);
//		return result;
//	}
//
//
//	@Override
//	public String getId() {
//		return getClass().getName() + Math.round(radius);
//	}
//}
