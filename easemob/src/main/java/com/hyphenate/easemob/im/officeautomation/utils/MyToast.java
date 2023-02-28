package com.hyphenate.easemob.im.officeautomation.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.hyphenate.easemob.im.mp.AppHelper;

import nl.invissvenska.toaster.Toaster;


/**
 * Created by qby on 2017/09/28.
 * 静态Toast工具类
 */
public class MyToast extends Toast {

    /*
     * 单例toast 防止toast连弹
     */
    private static Toast toast;

    private MyToast(Context context) {
        super(context);
    }

    // 直接调用此方法即可
    public static void showToast(CharSequence content) {
        Toast toast = Toaster.normal(AppHelper.getInstance().getAppContext(), content);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
//        try {
//            if (toast == null) {
//                toast = Toast.makeText(MyApplication.getContext(), content, Toast.LENGTH_SHORT);
//            } else {
//                toast.setText(content);
//                toast.setDuration(Toast.LENGTH_SHORT);
//            }
//            toast.setGravity(Gravity.TOP, 0, 0);
//            toast.show();
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
    }

    public static void showInfoToast(CharSequence content) {
        Toast toast = Toaster.normal(AppHelper.getInstance().getAppContext(), content);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    public static void showWarning(CharSequence content) {
        Toast toast = Toaster.warning(AppHelper.getInstance().getAppContext(), content);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }


    public static void showErrorToast(CharSequence content) {
        Toast toast = Toaster.error(AppHelper.getInstance().getAppContext(), content);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }


}
