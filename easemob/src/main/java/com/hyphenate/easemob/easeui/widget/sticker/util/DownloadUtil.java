package com.hyphenate.easemob.easeui.widget.sticker.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 18/12/2018
 */
public class DownloadUtil {
	private String urlString;
	private List<DownloadListener> listeners = new ArrayList<>();

	public DownloadUtil(String url){
		this.urlString  = url;
	}

	public void download(String savePath)
	{
		try {
			URL url = new URL(this.urlString);
			URLConnection connection = url.openConnection();
			connection.connect();
			int totalLength = connection.getContentLength();
			InputStream inputStream = connection.getInputStream();
			InputStream input = new BufferedInputStream(inputStream, 8192);
			File file = new File(savePath);
			if (!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			OutputStream output = new FileOutputStream(savePath);
			int downloadSize = 0;
			byte[] data = new byte[2 * 1024];
			int count;
			while((count = input.read(data)) != -1){
				output.write(data, 0, count);
				downloadSize += count;
				int progress = (int) (downloadSize / totalLength * 100.0F);
				notifyListenersOnProgress(progress);
			}
			output.flush();
			output.close();
			input.close();
			notifyListenersOnComplete(savePath);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			notifyListenersOnError(e);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			notifyListenersOnError(e);
		} catch (IOException e) {
			e.printStackTrace();
			notifyListenersOnError(e);
		}
	}

	public void addDownloadListener(DownloadListener listener){
		if (listener != null){
			if (!listeners.contains(listener)){
				listeners.add(listener);
			}
		}
	}

	public void notifyListenersOnProgress(int progress){
		synchronized (listeners){
			for (DownloadListener listener : this.listeners){
				listener.onProgress(progress);
			}
		}
	}

	public void notifyListenersOnComplete(String path){
		synchronized (listeners){
			for (DownloadListener listener : listeners){
				listener.onComplete(path);
			}
		}
	}

	public void notifyListenersOnError(Exception e){
		synchronized (listeners){
			for (DownloadListener listener : this.listeners){
				listener.onError(e);
			}
		}
	}

	public static abstract interface DownloadListener
	{
		public abstract void onProgress(int paramInt);
		public abstract void onComplete(String paramString);
		public abstract void onError(Exception paramException);
	}

}
