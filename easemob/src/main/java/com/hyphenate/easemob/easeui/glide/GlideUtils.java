package com.hyphenate.easemob.easeui.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.imlibs.easeui.token.TokenManager;

import java.io.File;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 26/07/2018
 */

public class GlideUtils {

	public static void loadFromRemote(Context context, String remoteUrl, int placeholder, ImageView imageView){
		if (imageView == null){
			return;
		}
		if(remoteUrl == null){
			if (placeholder > 0)
				imageView.setImageResource(placeholder);
			return;
		}
		load(context, remoteUrl, placeholder, imageView, false, false);
	}

	public static void load(Context context, String remoteUrl, int placeholder, ImageView imageView){
		loadFromRemote(context,remoteUrl, placeholder, imageView);
	}

	public static void load(Context context, int res, ImageView imageView){
		Glide.with(context).load(res).into(imageView);
	}


	public static void load(Context context, String filePath, ImageView imageView){
		Glide.with(context).load(filePath).into(imageView);
	}

	//=======================Glide 4.x start==============================

	public static void load(Context context, final String remoteUrl, int placeholder, final ImageView imageView, boolean round, boolean dontAnimate){
		if (context == null) return;
		if (imageView == null) return;
		String imageUrl = remoteUrl;
		if (!imageUrl.startsWith("http")) {
			imageUrl =  EaseUI.getInstance().getAppServer()+ imageUrl;
		}
		String strSession = TokenManager.getInstance().getSession();
		GlideUrl glideUrl = new GlideUrl(imageUrl, new LazyHeaders.Builder().addHeader("Cookie", strSession).build());
		RequestOptions options;
		if (round){
			options = RequestOptions.fitCenterTransform().placeholder(placeholder).error(placeholder).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL);
			if (dontAnimate){
				options = options.dontAnimate();
			}
			Glide.with(context).load(glideUrl).apply(options).into(imageView);
		}else{
			options = RequestOptions.placeholderOf(placeholder).error(placeholder).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL);
			if (dontAnimate){
				options = options.dontAnimate();
			}
			Glide.with(context).load(glideUrl).apply(options).into(imageView);
		}

	}
//	public static void load(Context context, String remoteUrl, SimpleTarget simpleTarget){
//		TokenManager.Token token = TokenManager.getInstance().getToken();
//		String strCookie = "";
//		if (token != null){
//			strCookie = token.name + "=" + token.value;
//		}
//		GlideUrl glideUrl = new GlideUrl(remoteUrl, new LazyHeaders.Builder().addHeader("Cookie", strCookie).build());
//		Glide.with(context).load(glideUrl).into(simpleTarget);
//	}
	public static void loadFromFile(Context context, String filePath, int placeholder, ImageView imageView){
		Glide.with(context).load(filePath).apply(RequestOptions.placeholderOf(placeholder)).into(imageView);
	}

	public static void loadFromFile(Context context, File file, int placeholder, ImageView imageView){
		Glide.with(context).load(file).apply(RequestOptions.placeholderOf(placeholder)).into(imageView);
	}

	public static void loadAsBitmap(Context context, String remoteUrl,final View view){
		if (context == null) return;
		String imageUrl = remoteUrl;
		if (!imageUrl.startsWith("http")) {
			imageUrl =  EaseUI.getInstance().getAppServer()+ imageUrl;
		}
		String strSession = TokenManager.getInstance().getSession();
		GlideUrl glideUrl = new GlideUrl(imageUrl, new LazyHeaders.Builder().addHeader("Cookie", strSession).build());
		Glide.with(context).load(glideUrl).into(new SimpleTarget<Drawable>() {
			@Override
			public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
				view.setBackgroundDrawable(resource);
			}
		});
	}

	//=======================Glide 4.x end ==============================

	//=======================Glide 3.x start ==============================


//	public static void load(Context context, final String remoteUrl, int placeholder, final ImageView imageView, boolean round){
//		if (context == null) return;
//		String imageUrl = remoteUrl;
//		if (!imageUrl.startsWith("http")) {
//			imageUrl =  EaseUI.getInstance().getAppServer()+ imageUrl;
//		}
//		TokenManager.Token token = TokenManager.getInstance().getToken();
//		String strCookie = "";
//		if (token != null){
//			strCookie = token.name + "=" + token.value;
//		}
//		GlideUrl glideUrl = new GlideUrl(imageUrl, new LazyHeaders.Builder().addHeader("Cookie", strCookie).build());
//		if (round){
//			Glide.with(context).load(glideUrl).transform(new GlideRoundTransform(context)).placeholder(placeholder).error(placeholder).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
//		}else{
//			Glide.with(context).load(glideUrl).placeholder(placeholder).error(placeholder).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
//		}
//	}


//	public static void load(Context context, String remoteUrl, SimpleTarget simpleTarget){
//		TokenManager.Token token = TokenManager.getInstance().getToken();
//		String strCookie = "";
//		if (token != null){
//			strCookie = token.name + "=" + token.value;
//		}
//		GlideUrl glideUrl = new GlideUrl(remoteUrl, new LazyHeaders.Builder().addHeader("Cookie", strCookie).build());
//		Glide.with(context).load(remoteUrl).into(simpleTarget);
//	}

//	public static void loadFromFile(Context context, String filePath, int placeholder, ImageView imageView){
//		Glide.with(context).load(filePath).placeholder(placeholder).into(imageView);
//	}

//	public static void loadFromFile(Context context, File file, int placeholder, ImageView imageView){
//		Glide.with(context).load(file).placeholder(placeholder).into(imageView);
//	}
//
//	public static void loadAsBitmap(Context context, String remoteUrl,final View view){
//		if (context == null) return;
//		String imageUrl = remoteUrl;
//		if (!imageUrl.startsWith("http")) {
//			imageUrl =  EaseUI.getInstance().getAppServer()+ imageUrl;
//		}
//		TokenManager.Token token = TokenManager.getInstance().getToken();
//		String strCookie = "";
//		if (token != null){
//			strCookie = token.name + "=" + token.value;
//		}
//		GlideUrl glideUrl = new GlideUrl(imageUrl, new LazyHeaders.Builder().addHeader("Cookie", strCookie).build());
//		Glide.with(context).load(glideUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
//			@Override
//			public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//				Drawable drawable = new BitmapDrawable(resource);
//				view.setBackground(drawable);
//			}
//		});
//	}
}
