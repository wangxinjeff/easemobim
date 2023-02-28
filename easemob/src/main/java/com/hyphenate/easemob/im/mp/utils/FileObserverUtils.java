package com.hyphenate.easemob.im.mp.utils;

import android.os.Environment;
import android.os.FileObserver;

import androidx.annotation.Nullable;

import java.io.File;

public class FileObserverUtils {

    private static FileObserverUtils sInstance;

    public static FileObserverUtils getInstance() {
        if (sInstance == null) {
            synchronized (FileObserverUtils.class) {
                if (sInstance == null) {
                    sInstance = new FileObserverUtils();
                }
            }
        }
        return sInstance;
    }


    private ISnapShotCallBack snapShotCallBack;

    public static interface ISnapShotCallBack {
        void onSnapShotCreate(String path);
    }

    private String lastShownSnapshot;

    private FileObserver fileObserver;

    public void setSnapShotCallBack(ISnapShotCallBack callback) {
        snapShotCallBack = callback;
        initFileObserver();
    }


    private void initFileObserver() {
        String snapshotFolderPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_PICTURES
                + File.separator + "Screenshots" + File.separator;

        fileObserver = new FileObserver(snapshotFolderPath, FileObserver.CREATE) {

            @Override
            public void onEvent(int event, @Nullable String path) {
                if (null != path && event == FileObserver.CREATE && (!path.equals(lastShownSnapshot))) {
                    lastShownSnapshot = path; // 有些手机同一张截图会触发多个CREATE事件，避免重复展示
                    if (snapShotCallBack != null) {
                        snapShotCallBack.onSnapShotCreate(path);
                    }
                }

            }
        };
    }

    public void startSnapshotWatching() {
        if (fileObserver != null) {
            fileObserver.startWatching();
        }
    }

    public void stopSnapshotWatching() {
        if (fileObserver != null) {
            fileObserver.stopWatching();
        }
    }

}
