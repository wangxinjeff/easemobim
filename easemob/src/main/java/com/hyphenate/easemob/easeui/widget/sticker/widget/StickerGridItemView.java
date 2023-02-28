package com.hyphenate.easemob.easeui.widget.sticker.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.EaseEmojicon;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.easeui.utils.EaseSmileUtils;
import com.hyphenate.easemob.imlibs.mp.utils.MPUtil;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 18/12/2018
 */
public class StickerGridItemView extends LinearLayout {

	private EaseEmojicon easeEmojicon;
	private ImageView imageView;
	private TextView textView;


	public StickerGridItemView(Context context) {
		super(context);
		init();
	}

	private void init() {
		inflate(getContext(), R.layout.sticker_item, this);
		setOrientation(VERTICAL);
		setGravity(Gravity.CENTER);
		this.imageView = findViewById(R.id.iv);
		this.textView = findViewById(R.id.tv);
	}

	public EaseEmojicon getEaseEmojicon() {
		return easeEmojicon;
	}

	public void setNumColumns(int numColumns) {
		int screenWidth = MPUtil.getScreenWidth();
		int width = screenWidth / numColumns - EaseCommonUtils.convertDip2Px(getContext(), 25);
//		int width = screenWidth / numColumns - EaseCommonUtils.convertDip2Px(getContext(), 2);
		if (numColumns > 4) {
			int pxValue = EaseCommonUtils.convertDip2Px(getContext(), 40);
			int paddingValue = EaseCommonUtils.convertDip2Px(getContext(), 5);
			imageView.setLayoutParams(new LayoutParams(pxValue, pxValue));
			imageView.setPadding(paddingValue, paddingValue, paddingValue, paddingValue);
			width = screenWidth / numColumns - EaseCommonUtils.convertDip2Px(getContext(), 10);
		}
		int height = width;
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(width, height);
		setLayoutParams(lp);
	}

	public void setSticker(EaseEmojicon emojicon) {
		this.easeEmojicon = emojicon;
		updateView();
	}

	private void updateView() {
		if (EaseSmileUtils.DELETE_KEY.equals(easeEmojicon.getEmojiText())){
			imageView.setImageResource(R.drawable.ease_icon_emoji_delete);
		}else if (easeEmojicon.getThumbPath() != null) {
			Bitmap bitmap = BitmapFactory.decodeFile(easeEmojicon.getThumbPath());
			imageView.setImageBitmap(bitmap);
			this.textView.setText(easeEmojicon.getEmojiText());
		} else if (easeEmojicon.getThumbnailUrl() != null){
			setView(easeEmojicon.getThumbnailUrl());
		} else if (easeEmojicon.getRemoteUrl() != null){
			setView(easeEmojicon.getRemoteUrl());
		} else if (easeEmojicon.getIcon() > 0) {
			imageView.setImageResource(easeEmojicon.getIcon());
			if (easeEmojicon.getType() == EaseEmojicon.Type.BIG_EXPRESSION) {
				this.textView.setText(easeEmojicon.getName());
			}
		}
	}

	private void setView(String tempUrl){
		String remoteUrl = tempUrl;
		if (!remoteUrl.startsWith("http")){
			remoteUrl = EaseUI.getInstance().getAppServer() + remoteUrl;
		}
		RequestOptions options = RequestOptions.placeholderOf(R.drawable.ease_default_expression).error(R.drawable.sticker_load_fail).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL);
		options = options.dontAnimate();
		Glide.with(getContext().getApplicationContext()).load(remoteUrl).apply(options).into(imageView);
	}

}
