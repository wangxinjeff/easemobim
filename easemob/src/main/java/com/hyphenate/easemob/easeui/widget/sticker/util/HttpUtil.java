package com.hyphenate.easemob.easeui.widget.sticker.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 18/12/2018
 */
public class HttpUtil {
	public static void get(String urlString, Callback<String> callBack) {
		get(urlString, null, callBack);
	}

	public static void get(String urlString, Map<String, String> map, Callback<String> callback) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			if (map != null) {
				Set<Map.Entry<String, String>> set = map.entrySet();
				for (Map.Entry<String, String> entry : set) {
					connection.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			InputStream inputStream = connection.getInputStream();
			String result = inputStreamToString(inputStream);
			callback.onSuccess(result);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			callback.onError(e);
		} catch (IOException e) {
			e.printStackTrace();
			callback.onError(e);
		}
	}

	public static String get(String urlString, Map<String, String> map, Type typeOfT) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if (map != null) {
			Set<Map.Entry<String, String>> set = map.entrySet();
			for (Map.Entry<String, String> entry : set) {
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		int responseCode = connection.getResponseCode();
		if (responseCode == 200) {
			InputStream inputStream = connection.getInputStream();
			return inputStreamToString(inputStream);
		}
		throw new RuntimeException("http error:" + responseCode);
	}

	private static String inputStreamToString(InputStream inputStream) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[2048];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
			result.write(buffer, 0, length);
		}
		return result.toString("UTF-8");
	}


//	private static class ResultType implements ParameterizedType {
//		private final Type type;
//
//		public ResultType(Type type) {
//			this.type = type;
//		}
//
//		public Type[] getActualTypeArguments() {
//			return new Type[]{this.type};
//		}
//
//		public Type getOwnerType() {
//			return null;
//		}
//
//		public Type getRawType() {
//			return FullResponse.class;
//		}
//
//	}

	public static abstract interface Callback<T> {
		public abstract void onSuccess(T paramT);

		public abstract void onError(Exception paramException);
	}
}
