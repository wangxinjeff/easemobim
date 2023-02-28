package com.hyphenate.easemob.easeui.widget.sticker.businesslogic;

import android.content.Context;

import com.hyphenate.easemob.easeui.widget.sticker.util.FileUtil;

import java.io.File;
import java.util.Locale;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 18/12/2018
 */
public class StickerPackageStorageTask {

	private static final String STICKER_DIR = "sticker";
	private static final String STICKER_PACKAGES_CONFIG_FILE = "StickerPackagesConfig.json";
//	private static final String STICKER_CONFIG_FILE = "meta.json";
	private static final String IMAGE_FORMAT = "image_%s.gif";
	private static final String THUMB_FORMAT = "thumb_%s.png";
	private static String sStickerHomeDir;

	public static void init(Context context, String appKey, String userId){
		sStickerHomeDir = getPath(context, appKey, userId);
	}

	private static String getPath(Context context, String appKey, String userId){
		String[] pathArray = {context.getFilesDir().toString(), appKey, userId, STICKER_DIR};
		StringBuilder sb = new StringBuilder();
		for (String path : pathArray){
			sb.append(path).append(File.separator);
		}
		return sb.toString();
	}

	public static void saveStickerPackagesConfig(String json){
		File configFile = getStickerPackagesConfigFile();
		FileUtil.writeStringToFile(json, configFile);
	}

	public static String getStickerHomeDir(){
		return sStickerHomeDir;
	}

	private static File getStickerPackagesConfigFile(){
		String filePath = getStickerHomeDir() + STICKER_PACKAGES_CONFIG_FILE;
		File file = new File(filePath);
		if (!file.exists()){
			file.getParentFile().mkdirs();
		}
		return file;
	}

//	private static File getStickerPackageConfigFile(String packageId){
//		String filePath = getStickerPackageFolderPath(packageId) + STICKER_CONFIG_FILE;
//		return new File(filePath);
//	}

	private static String getStickerPackageFolderPath(String packageId){
		return getStickerHomeDir() + packageId + File.separator;
	}

	public static String getStickerImageFilePath(String packageId, String stickerId){
		return getStickerPackageFolderPath(packageId) + getStickerImageFileName(stickerId);
	}

	private static String getStickerImageFileName(String stickerId){
		return String.format(Locale.getDefault(), IMAGE_FORMAT, new Object[]{stickerId});
	}

	public static boolean isStickerExist(String packageId, String stickerId){
		String path = getStickerImageFilePath(packageId, stickerId);
		File file = new File(path);
		return file.exists();
	}

	public static String getStickerThumbFilePath(String packageId, String stickerId){
		return getStickerPackageFolderPath(packageId) + getStickerThumbFileName(stickerId);
	}

	private static String getStickerThumbFileName(String stickerId){
		return String.format(Locale.getDefault(), THUMB_FORMAT, new Object[]{stickerId});
	}

	public static void deleteStickerPackage(String packageId){
		String folder = getStickerPackageFolderPath(packageId);
		FileUtil.recursiveDelete(new File(folder));
	}

//	public static StickerPackage loadStickerPackage(String packageId){
//		File file = getStickerPackageConfigFile(packageId);
//		String json = FileUtil.toString(file);
//		StickerPackageInfo  info =
//	}



	private static String getUrlLastPath(String url){
		int index = url.lastIndexOf("/");
		return url.substring(index + 1);
	}



}
