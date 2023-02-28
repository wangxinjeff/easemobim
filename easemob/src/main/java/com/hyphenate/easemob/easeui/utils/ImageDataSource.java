package com.hyphenate.easemob.easeui.utils;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.hyphenate.easemob.easeui.domain.ImageItem;


/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 03/06/2018
 */

public class ImageDataSource implements LoaderManager.LoaderCallbacks<Cursor> {

	public static final int LOADER_ALL = 0; // 加载所有图片
	public static final int LOADER_CATEGORY = 1; // 分类加载图片
	private final String[] IMAGE_PROJECTION = {
			MediaStore.Images.Media.DISPLAY_NAME,   //图片的显示名称
			MediaStore.Images.Media.DATA,   //图片的真实路径
			MediaStore.Images.Media.SIZE,   //图片的大小
			MediaStore.Images.Media.WIDTH,  // 图片的宽度
			MediaStore.Images.Media.HEIGHT, // 图片的高度
			MediaStore.Images.Media.MIME_TYPE,  // 图片的类型
			MediaStore.Images.Media.DATE_ADDED  // 图片被添加的时间
	};

	private FragmentActivity mFragmentActivity;

	/**
	 * 图片加载完成的回调接口
	 */
	private OnImagesLoadedListener mLoadedListener;

	/**
	 * @param activity 用于初始化的LoaderManager
	 * @param path     指定扫描的文件夹目录，可以为null,表示扫描所有图片
	 * @param listener 图片加载完成的监听
	 */
	public ImageDataSource(FragmentActivity activity, String path, OnImagesLoadedListener listener) {
		this.mFragmentActivity = activity;
		this.mLoadedListener = listener;
		LoaderManager loaderManager = activity.getSupportLoaderManager();
		if (path == null) {
			loaderManager.initLoader(LOADER_ALL, null, this);// 加载所有的图片
		} else {
			//加载指定目录的图片
			Bundle bundle = new Bundle();
			bundle.putString("path", path);
			loaderManager.initLoader(LOADER_CATEGORY, bundle, this);
		}
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = null;
		//扫描所有图片
		if (id == LOADER_ALL) { //时间逆序
			cursorLoader = new CursorLoader(mFragmentActivity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
					null, null, IMAGE_PROJECTION[6] + " DESC");
		}
		//扫描某个图片文件夹
		if (id == LOADER_CATEGORY) {
			cursorLoader = new CursorLoader(mFragmentActivity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
					IMAGE_PROJECTION[1] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[6] + " DESC");

		}
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		//imageFolders.clear()
		ImageItem imageItem = new ImageItem();
		//只取第一个
		if (data != null) {
			// ArrayList<ImageItem> allItems = new ArrayList<>(); //所有图片集合
			if (data.moveToFirst()) {
				//查询数据
				String imageName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
				String imagePath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
				long imageSize = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
				int imageWidth = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
				int imageHeight = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
				String imageMimeType = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
				long imageAddTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6]));

				imageItem.name = imageName;
				imageItem.path = imagePath;
				imageItem.size = imageSize;
				imageItem.width = imageWidth;
				imageItem.height = imageHeight;
				imageItem.mimeType = imageMimeType;
				imageItem.addTime = imageAddTime;
			}

		}
		if (mLoadedListener != null){
			//回调接口，通知图片数据准备完成
			mLoadedListener.onImagesLoaded(imageItem);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}


	/**
	 * 所有图片加载完成的回调接口
	 */
	public interface OnImagesLoadedListener {
		void onImagesLoaded(ImageItem imageItem);
	}


}
