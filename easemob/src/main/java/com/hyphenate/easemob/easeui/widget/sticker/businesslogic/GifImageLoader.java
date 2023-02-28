package com.hyphenate.easemob.easeui.widget.sticker.businesslogic;

import android.os.Handler;
import android.util.LruCache;

import com.hyphenate.easemob.easeui.domain.EaseEmojicon;
import com.hyphenate.easemob.easeui.widget.sticker.util.FileUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 18/12/2018
 */
public class GifImageLoader {
	private static final String SEPARATOR = "";
	private static final int CACHE_SIZE = 5242880;
	private ExecutorService executorService = Executors.newCachedThreadPool();
	private LruCache<String, byte[]> cache = new LruCache<String, byte[]>(CACHE_SIZE)
	{
		@Override
		protected int sizeOf(String key, byte[] bytes) {
			return bytes.length;
		}

		@Override
		protected byte[] create(String key) {
			String[] strings = key.split(" ");
			String packageId = strings[0];
			String stickerId = strings[1];
			try {
				return FileUtil.toByteArray(StickerPackageStorageTask.getStickerImageFilePath(packageId, stickerId));
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	};

	private static GifImageLoader instance;

	public static synchronized GifImageLoader getInstance(){
		if (instance == null){
			instance = new GifImageLoader();
		}
		return instance;
	}

	public void obtain(EaseEmojicon easeEmojicon, SimpleCallback callback){
		Worker worker = new Worker(easeEmojicon, callback);
		executorService.submit(worker);
	}

	public void clear(){
		this.cache.evictAll();
	}

	private class Worker implements Runnable {
		private String packageId;
		private String stickerId;
		private Callback callback;

		Worker(EaseEmojicon easeEmojicon, Callback callback){
			this.packageId = "default";
			this.stickerId = easeEmojicon.getRemoteUrl();
			this.callback = callback;
		}

		@Override
		public void run() {
			if (!StickerPackageStorageTask.isStickerExist(this.packageId, this.stickerId)){
				StickerDownloadTask task = new StickerDownloadTask(this.packageId, this.stickerId);
				task.downloadSticker();
			}
			byte[] bytes = (byte[]) GifImageLoader.this.cache.get(createKey());
			this.callback.onResult(bytes);
		}

		private String createKey(){
			return this.packageId + "_" + this.stickerId;
		}
	}


	public static abstract interface Callback {
		public abstract void onResult(byte[] paramArrayOfByte);
	}
		public static abstract class SimpleCallback implements Callback{

			@Override
			public void onResult(final byte[] bytes) {
				Handler handler = StickerPackagesUiHandler.getUiHandler();
				if(handler != null){
					if (bytes != null){
						handler.post(new Runnable() {
							@Override
							public void run() {
								onSuccess(bytes);
							}
						});
					} else {
						handler.post(new Runnable() {
							@Override
							public void run() {
								onFail();
							}
						});
					}
				}
			}


			public abstract void onSuccess(byte[] bytes);

			public abstract void onFail();
		}



}
