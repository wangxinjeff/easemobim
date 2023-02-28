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

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.EaseMessageUtils;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.HanziToPinyin;
import com.hyphenate.util.HanziToPinyin.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EaseCommonUtils {
    private static final String TAG = "CommonUtils";

    public static final String PING_IP = "www.baidu.com";

    /**
     * check if network avalable
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }

        return false;
    }

    public static void showSoftKeyBoard(EditText editText){
        editText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideSoftKeyBoard(EditText editText){
        editText.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * check if sdcard exist
     *
     * @return
     */
    public static boolean isSdcardExist() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static EMMessage createExpressionMessage(String toChatUsername, String expressioName, String identityCode) {
        return createExpressionMessage(toChatUsername, expressioName, identityCode, false);
    }

    public static EMMessage createExpressionMessage(String toChatUsername, String expressioName, String identityCode, boolean isForward) {
        EMMessage message;
        if (!isForward) {
            message = EMMessage.createTxtSendMessage("[" + expressioName + "]", toChatUsername);
        } else {
            message = EMMessage.createTxtSendMessage(expressioName, toChatUsername);
        }
        if (identityCode != null) {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, identityCode);
        }
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, true);
        return message;
    }

    /**
     * Get digest according message type and content
     *
     * @param message
     * @param context
     * @return
     */
    public static String getMessageDigest(EMMessage message, Context context, boolean showInfo) {
        String digest = "";
        switch (message.getType()) {
            case LOCATION:
                if(showInfo){
                    EMLocationMessageBody localBody = (EMLocationMessageBody) message.getBody();
                    digest = getString(context, R.string.location_loc) + localBody.getAddress();
                } else {
                    if (message.direct() == EMMessage.Direct.RECEIVE) {
                        digest = getString(context, R.string.location_recv);
                        EaseUser userInfo = EaseUserUtils.getUserInfo(message.getFrom());
                        if (userInfo != null && !TextUtils.isEmpty(userInfo.getNickname())) {
                            digest = String.format(digest, userInfo.getNickname());
                        } else {
                            digest = String.format(digest, message.getFrom());
                        }
                    } else {
                        digest = getString(context, R.string.location_prefix);
                    }
                }
                break;
            case IMAGE:
                if (EaseMessageUtils.isBurnMessage(message)) {
                    digest = getString(context, R.string.burn_message);
                } else {
                    digest = getString(context, R.string.picture);
                }
                break;
            case VOICE:
                String length = "";
                if(showInfo){
                    EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();
                    length = voiceBody.getLength() + "\"";
                }

                digest = getString(context, R.string.voice_prefix) + length;
                break;
            case VIDEO:
                digest = getString(context, R.string.video);
                break;
            case TXT:
                EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                if (EaseMessageUtils.isRecallMessage(message)) {
                    digest = getString(context, R.string.notice_msg);
                } else if (EaseMessageUtils.isBurnMessage(message)) {
                    digest = getString(context, R.string.burn_message);
                } else if (EaseMessageUtils.isStickerMessage(message)) {
                    digest = getString(context, R.string.sticker);
                } else if (EaseMessageUtils.isVoiceCallMessge(message)) {
                    if (txtBody != null) {
                        digest = getString(context, R.string.voice_call) + txtBody.getMessage();
                    } else {
                        digest = getString(context, R.string.voice_call);
                    }
                } else if (EaseMessageUtils.isVideoCallMessage(message)) {
                    digest = getString(context, R.string.video_call) + txtBody.getMessage();
                } else if (EaseMessageUtils.isBigExprMessage(message)) {
                    if (!TextUtils.isEmpty(txtBody.getMessage())) {
                        digest = txtBody.getMessage();
                    } else {
                        digest = getString(context, R.string.dynamic_expression);
                    }
                } else if (EaseMessageUtils.isInviteMessage(message)) {
                    digest = getString(context, R.string.notice_msg);
                } else if (EaseMessageUtils.isNoticeMessage(message)) {
                    digest = getString(context, R.string.notice_msg);
                } else if (EaseMessageUtils.isChatHistoryMessage(message)) {
                    digest = getString(context, R.string.chats_history);
                } else if (EaseMessageUtils.isNameCard(message)) {
                    if(showInfo){
                        try {
                            JSONObject extMsg = new JSONObject(message.getJSONObjectAttribute(EaseConstant.EXT_EXTMSG).toString());
                            JSONObject cardMsg = extMsg.getJSONObject("content");
                            digest = String.format(getString(context, R.string.ease_name_card), cardMsg.optString("realName"));
                        } catch (JSONException | HyphenateException e) {
                            digest = getString(context, R.string.card_message);
                        }
                    } else {
                        digest = getString(context, R.string.card_message);
                    }
                } else {
                    digest = Html.fromHtml(txtBody.getMessage()).toString();
                }
                break;
            case FILE:
                String fileName = "";
                if(showInfo){
                    EMNormalFileMessageBody fileBody = (EMNormalFileMessageBody) message.getBody();
                    fileName = fileBody.getFileName();
                }
                digest = getString(context, R.string.file) + fileName;
                break;
            default:
                EMLog.e(TAG, "error, unknow type");
                return "";
        }

        return digest;
    }

    static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    /**
     * get top activity
     *
     * @param context
     * @return
     */
    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null)
            return runningTaskInfos.get(0).topActivity.getClassName();
        else
            return "";
    }

    /**
     * set initial letter of according user's nickname( username if no nickname)
     *
     * @param user
     */
    public static void setUserInitialLetter(EaseUser user) {
        final String DefaultLetter = "#";
        String letter = DefaultLetter;

        final class GetInitialLetter {
            String getLetter(String name) {
                if (TextUtils.isEmpty(name)) {
                    return DefaultLetter;
                }
                char char0 = name.toLowerCase().charAt(0);
                if (Character.isDigit(char0)) {
                    return DefaultLetter;
                }
                ArrayList<Token> l = HanziToPinyin.getInstance().get(name.substring(0, 1));
                if (l != null && l.size() > 0 && l.get(0).target.length() > 0) {
                    Token token = l.get(0);
                    String letter = token.target.substring(0, 1).toUpperCase();
                    char c = letter.charAt(0);
                    if (c < 'A' || c > 'Z') {
                        return DefaultLetter;
                    }
                    return letter;
                }
                return DefaultLetter;
            }
        }

        if (!TextUtils.isEmpty(user.getNick())) {
            letter = new GetInitialLetter().getLetter(user.getNick());
            user.setInitialLetter(letter);
            return;
        }
        if (letter.equals(DefaultLetter) && !TextUtils.isEmpty(user.getUsername())) {
            letter = new GetInitialLetter().getLetter(user.getUsername());
        }
        user.setInitialLetter(letter);
    }

    /**
     * change the chat type to EMConversationType
     *
     * @param chatType
     * @return
     */
    public static EMConversationType getConversationType(int chatType) {
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            return EMConversationType.Chat;
        } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
            return EMConversationType.GroupChat;
        } else {
            return EMConversationType.ChatRoom;
        }
    }

    /**
     * MD5加密
     *
     * @param input
     * @return
     */
    public static String getMd5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);

            while (md5.length() < 32)
                md5 = "0" + md5;

            return md5;
        } catch (NoSuchAlgorithmException e) {
            Log.e("MD5", e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * \~chinese
     * 判断是否是免打扰的消息,如果是app中应该不要给用户提示新消息
     * @param message
     * return
     *
     * \~english
     * check if the message is kind of slient message, if that's it, app should not play tone or vibrate
     *
     * @param message
     * @return
     */
//    public static boolean isSilentMessage(EMMessage message){
//        return message.getBooleanAttribute("em_ignore_notification", false);
//    }

    /**
     * \~chinese
     * 判断是否是免打扰的消息,如果是app中应该不要给用户提示新消息
     * \~english
     * check if the message is kind of slient message, if that's it, app should not play tone or vibrate
     */
    public static boolean isSilentMessage(EMMessage message) {
        if (EMClient.getInstance().pushManager().getPushConfigs() != null && EMClient.getInstance().pushManager().getPushConfigs().isNoDisturbOn()) {
            int startTime = EMClient.getInstance().pushManager().getPushConfigs().getNoDisturbStartHour();
            int endTime = EMClient.getInstance().pushManager().getPushConfigs().getNoDisturbEndHour();
//            Log.e(TAG, "startTime:" + startTime + ", endTime:" + endTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
//            if (currentHour >= startTime && currentHour <= endTime && startTime < endTime ){
//                return true;
//            }else if (currentHour >= startTime && endTime < startTime){
//                return true;
//            }
            if (startTime < endTime) {
                if (currentHour >= startTime && currentHour <= endTime) {
                    return true;
                }
            } else {
                //startTime > endTime
                if (currentHour >= startTime || currentHour <= endTime) {
                    return true;
                }

            }


        }

//        String toUserName = message.getFrom();
        if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
//            toUserName = message.getTo();
//            List<String> disabledIds = EMClient.getInstance().pushManager().getNoPushGroups();
            return EaseUI.getInstance().getUserProfileProvider().isNoDisturb(message.getTo());
        }
        return false;
//        return !PreferenceUtils.getInstance().isEnableMsgRing(EMClient.getInstance().getCurrentUser(), toUserName);
    }

    // 转换dip为px
    public static int convertDip2Px(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }


    public static void openFileEx(File file, String fileType, Context context) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        // 判断版本大于等于7.0
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//            StrictMode.setVmPolicy(builder.build());
//        }
//        Uri data = Uri.fromFile(new File(filePath));
//        intent.setDataAndType(data, getMap(fileType));
//        Toast.makeText(context, "uri:" + data.toString(), Toast.LENGTH_SHORT).show();
//        context.startActivity(intent);
        try {
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".easemob", file);
            } else {
                uri = Uri.fromFile(file);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, fileType);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            openFileEx(file, context);
        }


    }

    private static void openFileEx(File file, Context context) {
        try {
            Uri uri = null;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".easemob", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(file);
            }
//            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "*/*");
            context.startActivity(intent);
        } catch (Exception e) {
            throw e;
        }


    }

    public static String getMap(String key) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("rar", "application/x-rar-compressed");
        map.put("jpg", "image/jpeg");
        map.put("zip", "application/zip");
        map.put("pdf", "application/pdf");
        map.put("doc", "application/msword");
        map.put("docx", "application/msword");
        map.put("wps", "application/msword");
        map.put("xls", "application/vnd.ms-excel");
        map.put("et", "application/vnd.ms-excel");
        map.put("xlsx", "application/vnd.ms-excel");
        map.put("ppt", "application/vnd.ms-powerpoint");
        map.put("html", "text/html");
        map.put("htm", "text/html");
        map.put("txt", "text/html");
        map.put("mp3", "audio/mpeg");
        map.put("mp4", "video/mp4");
        map.put("3gp", "video/3gpp");
        map.put("wav", "audio/x-wav");
        map.put("avi", "video/x-msvideo");
        map.put("flv", "flv-application/octet-stream");
        map.put("png", "image/*");
        map.put("gif", "image/*");
        map.put("csv", "text/html");
        map.put("", "*/*");

        return map.get(key.toLowerCase());
    }

//    public static void ScannerByMedia(Context context, String path) {
//        MediaScannerConnection.scanFile(context, new String[] {path}, null, null);
//        Log.v("TAG", "media scanner completed");
//    }
//

    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap, String filePath) {
        File filePic;
        try {
            filePic = new File(filePath);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    /**
     * TextView 显示高亮
     *
     * @param view
     * @param str1 要高亮显示的文字（输入的关键词）
     * @param str2 包含高亮文字的字符串
     */
    public static void initHighLight(Context context, TextView view, String str1, String str2) {
        if (str1 == null || str2 == null) {
            view.setText("");
            return;
        }
        SpannableString sp = new SpannableString(str2);
        for (int i = 0; i < str1.length(); i++) {
            String s = String.valueOf(str1.charAt(i));
            // 正则匹配
            Pattern p = Pattern.compile(s);
            Matcher m = p.matcher(sp);
            // 查找下一个
            while (m.find()) {
                // 字符开始下标
                int start = m.start();
                // 字符结束下标
                int end = m.end();

                try {
                    // 设置高亮
                    sp.setSpan(new ForegroundColorSpan(context.getColor(R.color.refused_red)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        view.setText(sp);
    }

    /**
     * 是否为鸿蒙系统
     *
     * @return true为鸿蒙系统
     */
    public static boolean isHarmonyOs() {
        try {
            Class<?> buildExClass = Class.forName("com.huawei.system.BuildEx");
            Object osBrand = buildExClass.getMethod("getOsBrand").invoke(buildExClass);
            return "Harmony".equalsIgnoreCase(osBrand.toString());
        } catch (Throwable x) {
            return false;
        }
    }

    /**
     * 获取鸿蒙系统版本号
     *
     * @return 版本号
     */
    public static String getHarmonyVersion() {
        return getProp("hw_sc.build.platform.version", "");
    }

    private static String getProp(String property, String defaultValue) {
        try {
            Class spClz = Class.forName("android.os.SystemProperties");
            Method method = spClz.getDeclaredMethod("get", String.class);
            String value = (String) method.invoke(spClz, property);
            if (TextUtils.isEmpty(value)) {
                return defaultValue;
            }
            return value;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return defaultValue;
    }


        public static int getFileIconRes(String fileName) {
        if (fileName.endsWith(".doc")) {
            return R.drawable.em_icon_file_word;// word
//            return -1;
        } else if (fileName.endsWith(".docx")) {
            return R.drawable.em_icon_file_word;// word
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            return R.drawable.em_icon_file_excel;//excel
//            return -1;
        } else if (fileName.endsWith(".pdf")) {
            return R.drawable.em_icon_file_pdf; // pdf
//            return -1;
        } else if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
            return R.drawable.ease_icon_file_ppt; // ppt
        } else if (fileName.endsWith(".mp3")) {
            return R.drawable.ease_icon_file_music; // music
        } else if (fileName.endsWith("txt")) {
            return R.drawable.text_icon;
        } else if (fileName.endsWith("png") || fileName.endsWith("jpg")) {
            return R.drawable.img_icon;
        } else {
            return R.drawable.unknow_icon; // other file
        }

    }

}
