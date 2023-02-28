package com.hyphenate.easemob.imlibs.mp.utils;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 18/12/2018
 */
public class FileUtil {

	private static final int IO_BUFFER_SIZE = 1024;

	public static boolean hasSDCard(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 判断文件是否可读可写
	 */
	public static boolean isFileCanReadAndWrite(String filePath){
		if (null != filePath && filePath.length() > 0){
			File f = new File(filePath);
			if (f.exists()){
				return f.canRead() && f.canWrite();
			}
		}
		return false;
	}

	public static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] b = new byte[IO_BUFFER_SIZE];
		int read;
		while ((read = in.read(b)) != -1){
			out.write(b, 0, read);
		}
	}

	public static String getFileName(String path){
		int start = path.lastIndexOf("/");
		if (start != -1){
			return path.substring(start + 1);
		}
		return null;
	}


	/**
	 * 得到除去文件名部分的路径，实际上就是路径中的最后一个路径分隔符前的部分
	 * @param fileName
	 * @return
	 */
	public static String getNameDelLastPath(String fileName){
		int point = getPathLastIndex(fileName);
		if (point == -1){
			return fileName;
		} else {
			return fileName.substring(0, point);
		}
	}

	/**
	 * 得到路径分隔符在文件路径
	 * @param fileName
	 * @return
	 */
	public static int getPathLastIndex(String fileName){
		int point = fileName.lastIndexOf('/');
		if (point == -1) {
			point = fileName.lastIndexOf('\\');
		}
		return point;
	}

	/**
	 * 如果文件末尾有了"/"则判断是否有多个"/"，是则保留一个，没有则添加
	 * @param path
	 * @return
	 */
	public static String checkFileSeparator(String path){
		if (!TextUtils.isEmpty(path)) {
			if (!path.endsWith(File.separator)){
				return path.concat(File.separator);
			} else {
				final int sourceStringLength = path.length();
				int index = sourceStringLength;
				if (index >= 0){
					while(index >= 0){
						index--;
						if (path.charAt(index) != File.separatorChar){
							break;
						}
					}
				}
				if (index < sourceStringLength){
					path = path.substring(0, index + 1);
					return path.concat(File.separator);
				}
			}
		}
		return path;
	}

	public static boolean copyFile(String oldPath, String newPath){
		try {
			int byteread = 0;
			File oldFile = new File(oldPath);
			if (oldFile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath);// 读入源文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[IO_BUFFER_SIZE];

				while((byteread = inStream.read(buffer)) != -1){
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
		} catch (Exception e) {
			MPLog.e("copyFile", e);
			return false;
		}
		return true;
	}

	/**
	 * 写入文件
	 * @param strFileName 文件名
	 * @param ins 流
	 */
	public static void writeToFile(String strFileName, InputStream ins){
		try {
			File file = new File(strFileName);
			FileOutputStream fouts = new FileOutputStream(file);
			int len;
			int maxSize = 1024 * 1024;
			byte buf[] = new byte[maxSize];
			while ((len = ins.read(buf, 0, maxSize)) != -1) {
				fouts.write(buf, 0, len);
				fouts.flush();
			}
			fouts.close();
		} catch (IOException e) {
			MPLog.e("writeToFile", e);
		}
	}

	/**
	 * 写入文件
	 * @param strFileName 文件名
	 * @param bytes bytes
	 * @return
	 */
	public static boolean writeToFile(String strFileName, byte[] bytes){
		try {
			File file = new File(strFileName);
			FileOutputStream fouts = new FileOutputStream(file);
			fouts.write(bytes, 0, bytes.length);
			fouts.flush();
			fouts.close();
			return true;
		} catch (IOException e) {
			MPLog.e("writeToFile", e);
		}
		return false;
	}

	/**
	 * Prints some data to file using a BufferedWriter
	 * @param fileName 文件名
	 * @param data 字符文本数据
	 * @return
	 */
	public static boolean writeToFile(String fileName, String data){
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(fileName));
			// start writer to the output stream
			bufferedWriter.write(data);
			return true;
		} catch (Exception e) {
			MPLog.e("writeToFile", e);
		}finally {
			// Close the BufferedWriter
			try {
				if (bufferedWriter != null){
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException e) {
				MPLog.e("writeToFile", e);
			}
		}
		return false;
	}

	public static String Read(String fileName){
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			MPLog.e("Read", e);
		}
		return res;
	}

	public static void Write(String fileName, String message){
		try {
			FileOutputStream outSTr = null;
			try {
				outSTr = new FileOutputStream(new File(fileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			BufferedOutputStream bos = new BufferedOutputStream(outSTr);
			byte[] bs = message.getBytes();
			bos.write(bs);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			MPLog.e("Write", e);
		}
	}

	public static void Write(String fileName, String message, boolean append){
		try {
			FileOutputStream outStr = null;
			try {
				outStr = new FileOutputStream(new File(fileName), append);
			} catch (FileNotFoundException e) {
				MPLog.e("write", e);
			}
			BufferedOutputStream bos = new BufferedOutputStream(outStr);
			byte[] bs = message.getBytes();
			bos.write(bs);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			MPLog.e("Write", e);
		}
	}

	/**
	 * 删除文件，删除文件夹里面的所有文件
	 * @param path
	 */
	public static void deleteFile(String path){
		File file = new File(path);
		if (!file.exists()){
			return;
		}
		if (!file.isDirectory()){ // 如果是文件，则删除文件
			file.delete();
			return ;
		}
		File files[] = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()){
				deleteFile(files[i].getAbsolutePath());//先删除文件夹里面的文件
			}
			files[i].delete();
		}
		file.delete();
	}

	/**
	 * 删除文件 删除文件夹里面的所有文件
	 *
	 * <p>
	 *     (此方法和deleteFile(String path)这个方法总体是一样的，只是删除代码部分用的是先改名再删除的方法删除的，
	 *     为了避免EBUSY(Device or resource busy)的错误）
	 *
	 * </p>
	 * @param path
	 */
	public static void deleteFileSafely(String path){
		File file = new File(path);
		if(!file.exists()){
			return;
		}
		if (!file.isDirectory()){ // 如果是文件，则删除文件
			safelyDelete(file);
			return;
		}
		File files[] = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()){
				deleteFileSafely(files[i].getAbsolutePath()); // 先删除文件夹里面的文件
			}
			safelyDelete(files[i]);
		}
		safelyDelete(file);
	}

	/**
	 * 先改名，再删除(为了避免EBUSY(Device or resource busy)的错误)
	 * @param file
	 */
	private static void safelyDelete(File file){
		if (file == null || !file.exists()) return;
		try {
			final File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
			file.renameTo(to);
			to.delete();
		} catch (Exception e) {
			MPLog.e("safelyDelete", e);
		}
	}


	/**
	 * 文件大小
	 * @throws Exception
	 */
	public static long getFileSize(File file) throws Exception {
		long size = 0;
		if (!file.exists()) {
			return size;
		}
		if (!file.isDirectory()) {
			size = file.length();
		} else {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isDirectory()) {
					size = size + getFileSize(fileList[i]);
				} else {
					size = size + fileList[i].length();
				}
			}
		}
		return size;
	}

	/**
	 * @return 文件的大小，带单位(MB、KB等)
	 */
	public static String getFileLength(String filePath){
		try {
			File file = new File(filePath);
			return fileLengthFormat(getFileSize(file));
		} catch (Exception e) {
			MPLog.e("getFileLength", e);
			return "";
		}
	}

	/**
	 * @return 文件的大小，带单位(MB、KB等)
	 */
	public static String fileLengthFormat(long length){
		String lenStr = "";
		DecimalFormat formatter = new DecimalFormat("#0.##");
		if (length > 0 && length < 1024) {
			lenStr = formatter.format(length) + " Byte";
		} else if (length < 1024 * 1024){
			lenStr = formatter.format(length / 1024.f) + " KB";
		} else if (length < 1024 * 1024 * 1024){
			lenStr = formatter.format(length / (1024 * 1024.0f)) + " MB";
		} else {
			lenStr = formatter.format(length / (1024 * 1024 * 1024.0f)) + " GB";
		}
		return lenStr;
	}


	/**
	 * 得到文件的类型。实际上就是得到文件名最后一个"."后面的部分。
	 * @param fileName 文件名
	 * @return 文件名中的类型部分
	 */
	public static String pathExtension(String fileName){
		int point = fileName.lastIndexOf('.');
		int length = fileName.length();
		if (point == -1 || point == length -1){
			return "";
		} else {
			return fileName.substring(point, length);
		}
	}


	public static void moveFile(String oldPath, String newPath){
		copyFile(oldPath, newPath);
		delFile(oldPath);
	}

	public static void delFile(String filePathAndName){
		try {
			File myDelFile = new File(filePathAndName);
			myDelFile.delete();
		} catch (Exception e) {
			MPLog.e("delFile", "删除文件操作出错 " + MPLog.getStackTraceString(e));
		}
	}

	/**
	 * 得到内置或外置SD卡的路径
	 * @param context
	 * @param isExSD
	 * @return
	 */
	public static String getStoragePath(Context context, boolean isExSD){
		StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
		Class<?> storageVolumeClazz = null;
		try {
			storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
			Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
			Method getPath = storageVolumeClazz.getMethod("getPath");
			Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
			Object result = getVolumeList.invoke(mStorageManager);
			final int length = Array.getLength(result);
			for (int i = 0; i < length; i++) {
				Object storageVolumeElement = Array.get(result, i);
				String path = (String) getPath.invoke(storageVolumeElement);
				boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
				if (isExSD == removable) {
					return path;
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 复制文件
	 * @param source 输入文件
	 * @param target 输出文件
	 */
	public static void copy(File source, File target){
		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			fileInputStream = new FileInputStream(source);
			fileOutputStream = new FileOutputStream(target);
			byte[] buffer = new byte[1024];
			while (fileInputStream.read(buffer) > 0){
				fileOutputStream.write(buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			closeStream(fileInputStream);
			closeStream(fileOutputStream);
		}
	}

	/**
	 * 关闭流
	 */
	public static void closeStream(Closeable closeable){
		if (closeable != null){
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
