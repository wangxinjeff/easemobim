package com.hyphenate.easemob.im.officeautomation.exception;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.hyphenate.easemob.R;

/**
 * @author qby
 * @date 2018/7/6 9:50
 * 自定义异常帮助类
 */
public class ExceptionHelper {

    /**
     * 根据不同的BaseException给用户具体的提示
     *
     * @param e 传递过来BaseException ，我在这个类里面具体识别是什么异常，来提示对应的错误信息
     */
    public static void toastCustomException(Context context, BaseException e) {
        int errCode = 0;
        // errCode 具体化
        if (e instanceof InvalidResponseException) {
            errCode = 1;
        } else if(e instanceof NoSessionResponseException){
            errCode = 2;
        }
        // 根据不同的errcode给用户做提示
        toastByErrCode(context, errCode);
    }

    private static void toastByErrCode(Context context, int errCode) {
        String content = "";
        switch (errCode) {
            case 1:
                content = context.getString(R.string.error_code_json_invalid);
                break;

            case 2:
                content = context.getString(R.string.error_code_no_session);
                break;

            default:
                break;
        }
        if (!TextUtils.isEmpty(content))
            Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
