package com.hyphenate.easemob.im.officeautomation.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Tools for handler picture
 *
 * Created by lyuzhao on 2016/2/18.
 */
public class ImageTools {

    /**
     * Check the SD card
     * @return
     */
    public static boolean checkSDCardAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Save image to the SD card
     * @param photoBitmap
     * @param path
     * @param photoName
     */
    public static void savePhotoToSDCard(Bitmap photoBitmap, String path, String photoName){
        if(checkSDCardAvailable()){
            File photoFile = new File(path, photoName);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if(photoBitmap != null){
                    if(photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)){
                        fileOutputStream.flush();
                    }
                }
            }catch (Exception e){
                photoFile.delete();
                e.printStackTrace();
            }finally {
                try {
                    if (fileOutputStream != null){
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Save Bitmap to SDCard
     * @param photoBitmap
     * @param path
     */
    public static void savePhotoToSDCard(Bitmap photoBitmap, String path){
        if(checkSDCardAvailable()){
            File photoFile = new File(path);
            File parentDir = photoFile.getParentFile();
            if(!parentDir.exists()){
                parentDir.mkdirs();
            }

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if(photoBitmap != null){
                   if(photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)){
                      fileOutputStream.flush();
                   }
                }
            }catch (Exception e){
                photoFile.delete();
                e.printStackTrace();
            }finally {
                if(fileOutputStream != null){
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    /**
     * ??????????????????bitmap
     * @param path  ??????
     * @param w  ???
     * @param h  ???
     * @return
     */
    public static Bitmap convertToBitmap(String path, int w, int h){
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            //?????????true?????????????????????
            opts.inJustDecodeBounds = true;
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            //????????????
            BitmapFactory.decodeFile(path, opts);
            int width = opts.outWidth;
            int height = opts.outHeight;
            float scaleWidth = 0.f, scaleHeight = 0.f;
            if(width > w || height > h){
                //??????
                scaleWidth = ((float) width) / w;
                scaleHeight = ((float) height) / h;
            }
            opts.inJustDecodeBounds = false;
            float scale = Math.max(scaleWidth, scaleHeight);
            opts.inSampleSize = (int)scale;
            WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
            Bitmap bMapRotate = Bitmap.createBitmap(weak.get(), 0, 0, weak.get().getWidth(), weak.get().getHeight(), null, true);
            if(bMapRotate != null){
                return bMapRotate;
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }




}
