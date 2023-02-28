/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easemob.easeui.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.mp.utils.MPPathUtil;
import com.hyphenate.util.EMLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class EaseImageUtils extends com.hyphenate.util.ImageUtils {

    public static String getImagePath(String remoteUrl) {
        String imageName = remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1, remoteUrl.length());
        String path = MPPathUtil.getInstance().getImagePath() + "/" + imageName;
        EMLog.d("msg", "image path:" + path);
        return path;

    }

    public static String getThumbnailImagePath(String thumbRemoteUrl) {
        String thumbImageName = thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
        String path = MPPathUtil.getInstance().getImagePath() + "/" + "th" + thumbImageName;
        EMLog.d("msg", "thum image path:" + path);
        return path;
    }

    public static int[] getImageWidthAndHeight(String localPath) {
        //获取Option对象
        BitmapFactory.Options options = new BitmapFactory.Options();
        //仅做解码处理，不加载到内存
        options.inJustDecodeBounds = true;
        //解析文件
        BitmapFactory.decodeFile(localPath, options);
        return new int[]{options.outWidth, options.outHeight};
    }

    public static String getImageDownloadPath(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // android 11
            return EaseConstants.SDCardConstants.getDir(context) + File.separator + "easemob" + File.separator;
        }
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/easemob";
    }

    public static void saveImgToGallery(final Context context, final String localPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String fileParentPath = getImageDownloadPath(context);
                    File appDir = new File(fileParentPath);
                    if (!appDir.exists()) {
                        appDir.mkdirs();
                    }
                    //获得源文件流
                    FileInputStream fis = new FileInputStream(localPath);
                    //保存的文件名
                    String fileName = "mp" + localPath.substring(localPath.lastIndexOf("/") + 1, localPath.length() - 2) + ".jpg";
                    //目标文件
                    final File targetFile = new File(appDir, fileName);
                    //输出文件流
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    // 缓冲数组
                    byte[] b = new byte[1024 * 8];
                    while (fis.read(b) != -1) {
                        fos.write(b);
                    }

                    fos.flush();
                    fis.close();
                    fos.close();
                    //扫描媒体库
//                    String extension = MimeTypeMap.getFileExtensionFromUrl(targetFile.getAbsolutePath());
//                    String mimeTypes = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//                    MediaScannerConnection.scanFile(context, new String[]{targetFile.getAbsolutePath()}, new String[]{mimeTypes}, null);
                    if (context instanceof Activity) {
                        Activity activity = (Activity) context;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, context.getResources().getString(R.string.tip_save_success) + "" + targetFile.getPath(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (context instanceof Activity) {
                        Activity activity = (Activity) context;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, R.string.tip_save_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }).start();

    }


}
