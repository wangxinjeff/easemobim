package com.hyphenate.easemob.imlibs.mp.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

public class MPPathUtil {
    public static String pathPrefix;
    public static final String historyPathName = "/chat/";
    public static final String imagePathName = "/image/";
    public static final String voicePathName = "/voice/";
    public static final String filePathName = "/file/";
    public static final String videoPathName = "/video/";
    public static final String netdiskDownloadPathName = "/netdisk/";
    public static final String meetingPathName = "/meeting/";
    private static File storageDir = null;
    private static MPPathUtil instance = null;
    private File voicePath = null;
    private File imagePath = null;
    private File historyPath = null;
    private File videoPath = null;
    private File filePath;

    private MPPathUtil() {
    }

    public static MPPathUtil getInstance() {
        if (instance == null) {
            instance = new MPPathUtil();
        }

        return instance;
    }

    public void initDirs(String appKey, String userName, Context applicationContext) {
        String appPackageName = applicationContext.getPackageName();
        pathPrefix = "/Android/data/" + appPackageName + "/";
        this.voicePath = generateVoicePath(appKey, userName, applicationContext);
        if (!this.voicePath.exists()) {
            this.voicePath.mkdirs();
        }

        this.imagePath = generateImagePath(appKey, userName, applicationContext);
        if (!this.imagePath.exists()) {
            this.imagePath.mkdirs();
        }

        this.historyPath = generateHistoryPath(appKey, userName, applicationContext);
        if (!this.historyPath.exists()) {
            this.historyPath.mkdirs();
        }

        this.videoPath = generateVideoPath(appKey, userName, applicationContext);
        if (!this.videoPath.exists()) {
            this.videoPath.mkdirs();
        }

        this.filePath = generateFiePath(appKey, userName, applicationContext);
        if (!this.filePath.exists()) {
            this.filePath.mkdirs();
        }

    }

    public File getImagePath() {
        return this.imagePath;
    }

    public File getVoicePath() {
        return this.voicePath;
    }

    public File getFilePath() {
        return this.filePath;
    }

    public File getVideoPath() {
        return this.videoPath;
    }

    public File getHistoryPath() {
        return this.historyPath;
    }

    private static File getStorageDir(Context applicationContext) {
        if (storageDir == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    String dir = applicationContext.getExternalFilesDir("") + File.separator + "easemob" + File.separator;
                    File dirFile = new File(dir);
                    if (!dirFile.exists()) {
                        dirFile.mkdirs();
                    }
                    return dirFile;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                File sdPath = Environment.getExternalStorageDirectory();
                if (sdPath.exists()) {
                    return sdPath;
                }
            }
            storageDir = applicationContext.getFilesDir();
        }

        return storageDir;
    }

    private static File generateImagePath(String appKey, String userName, Context applicationContext) {
        String path = null;
        if (appKey == null) {
            path = pathPrefix + userName + "/image/";
        } else {
            path = pathPrefix + appKey + "/" + userName + "/image/";
        }

        return new File(getStorageDir(applicationContext), path);
    }

    private static File generateVoicePath(String appKey, String userName, Context applicationContext) {
        String path = null;
        if (appKey == null) {
            path = pathPrefix + userName + "/voice/";
        } else {
            path = pathPrefix + appKey + "/" + userName + "/voice/";
        }

        return new File(getStorageDir(applicationContext), path);
    }

    private static File generateFiePath(String appKey, String userName, Context applicationContext) {
        String path = null;
        if (appKey == null) {
            path = pathPrefix + userName + "/file/";
        } else {
            path = pathPrefix + appKey + "/" + userName + "/file/";
        }

        return new File(getStorageDir(applicationContext), path);
    }

    private static File generateVideoPath(String appKey, String userName, Context applicationContext) {
        String path = null;
        if (appKey == null) {
            path = pathPrefix + userName + "/video/";
        } else {
            path = pathPrefix + appKey + "/" + userName + "/video/";
        }

        return new File(getStorageDir(applicationContext), path);
    }

    private static File generateHistoryPath(String appKey, String userName, Context applicationContext) {
        String path = null;
        if (appKey == null) {
            path = pathPrefix + userName + "/chat/";
        } else {
            path = pathPrefix + appKey + "/" + userName + "/chat/";
        }

        return new File(getStorageDir(applicationContext), path);
    }

    public static File getTempPath(File file) {
        return new File(file.getAbsoluteFile() + ".tmp");
    }
}
