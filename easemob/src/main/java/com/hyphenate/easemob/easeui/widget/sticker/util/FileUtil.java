package com.hyphenate.easemob.easeui.widget.sticker.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 18/12/2018
 */
public class FileUtil {

	public static void writeStringToFile(String content, File file){
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String toString(File file){
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			StringBuilder sb = new StringBuilder();
			char[] buffer = new char[2 * 1024];
			int read;
			while ((read = reader.read(buffer, 0, buffer.length)) != -1) {
				sb.append(buffer, 0, read);
			}
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}finally {
			if (reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static byte[] toByteArray(String filePath) throws IOException {
		File file = new File(filePath);
		return toByteArray(file);
	}

	public static byte[] toByteArray(File file) throws IOException {
		InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		return toByteArray(inputStream);
	}

	public static byte[] toByteArray(InputStream inputStream) throws IOException{
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[2 * 1024];
		int length;
		while((length = inputStream.read(buffer)) != -1){
			result.write(buffer, 0, length);
		}
		return result.toByteArray();

	}

	public static void recursiveDelete(File file){
		if (!file.exists()){
			return;
		}
		if (file.isDirectory()){
			for (File f : file.listFiles()){
				recursiveDelete(f);
			}
		}
		file.delete();
	}


}
