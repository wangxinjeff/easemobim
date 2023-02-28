package com.hyphenate.easemob.im.officeautomation.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.core.app.AppOpsManagerCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.officeautomation.listener.TextWatcherCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by qby on 2017/9/26.
 * 应用工具类
 */

public class AppUtil {

    private static final String TAG = "AppUtil";

    /**
     * 删除上次更新存储在本地的apk
     */
    public static void removeOldApk(Context context) {
        //获取后台下载的老ＡＰＫ的存储路径
        File temp = new File(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(temp.getAbsolutePath() + "senpu_marketing.apk");
        MPLog.e(TAG, "1老APK的存储路径 =" + file.getAbsolutePath());

        if (file.exists() && file.isFile()) {
            file.delete();
            MPLog.e(TAG, "1存储器内存在老APK，进行删除操作");
        }
    }

    /**
     * 正则判断手机号
     */
    public static boolean isPhone(String phone) {
        String matchPhone = phone.replaceAll(" ", "");
        Pattern p = Pattern.compile("^1\\d{10}$");
        Matcher m = p.matcher(matchPhone);
        return m.matches();
    }

    /**
     * 功能：判断字符串是否为数字
     *
     * @param str 传入字符串
     * @return 返回true/false
     */
    public static boolean isNum(String str) {
        Pattern pattern = Pattern.compile("^[0-9]*$");
        Matcher isNum = pattern.matcher(replaceBlock(str));
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断email格式是否正确
     *
     * @param email 邮箱
     */
    public static boolean isEmail(String email) {
//        String str = "^(\\w-*\\.*){6,18}@(\\w-?)+(\\.\\w{2,6})+$";
        String str = "^(\\w-*\\.*)+@(\\w-?)+(\\.\\w+)+$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    /**
     * @param aliNo 支付宝账户
     * @return 返回校验结果
     */
    public static boolean checkEmailOrPhone(String aliNo) {
        boolean isTrue = false;
        if (!TextUtils.isEmpty(aliNo)) {
            //检查是否是邮箱，手机号
            if (isEmail(aliNo) || isPhone(aliNo)) {
                isTrue = true;
            }
        }
        return isTrue;
    }

    /**
     * @param cid
     * @description 身份证号验证
     * @author qby
     */
    public static boolean isIdentification(String cid) {
        if (!cid.matches("^\\d{17}(\\d|x|X)$")) {
            return false;
        }
        String aCity = "{'11':'北京','12':'天津','13':'河北','14':'山西','15':'内蒙古','21':'辽宁','22':'吉林','23':'黑龙江','31':'上海','32':'江苏','33':'浙江','34':'安徽','35':'福建','36':'江西','37':'山东','41':'河南','42':'湖北',"
                + "'43':'湖南','44':'广东','45':'广西','46':'海南','50':'重庆','51':'四川','52':'贵州','53':'云南','54':'西藏','61':'陕西','62':'甘肃','63':'青海','64':'宁夏','65':'新疆','71':'台湾','81':'香港','82':'澳门','91':'国外'}";
        JSONObject jsonCity = parseFromJson(aCity);
        int[] arrExp = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5,
                8, 4, 2};// 加权因子
        Object[] arrValid = new Object[]{1, 0, "X", 9, 8, 7, 6, 5, 4, 3, 2};// 校验码
        int iSum = 0, idx;
        String sBirthday = cid.substring(6, 10) + "-"
                + Integer.parseInt(cid.substring(10, 12)) + "-"
                + Integer.parseInt(cid.substring(12, 14));

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d = df.parse(sBirthday);
            calendar.setTime(d);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        try {
            if (jsonCity.get(cid.substring(0, 2)) == null) {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!sBirthday.equals((calendar.get(Calendar.YEAR) + "-"
                + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar
                .get(Calendar.DAY_OF_MONTH)))) {
            return false;
        }
        for (int i = 0; i < cid.length() - 1; i++) {
            // 对前17位数字与权值乘积求和
            iSum += Integer.parseInt(cid.substring(i, i + 1), 10) * arrExp[i];
        }
        // 计算模（固定算法）
        idx = iSum % 11;
        // 检验第18为是否与校验码相等
        return cid.substring(17, 18).toUpperCase().equals(arrValid[idx] + "");
    }

    /**
     * @param json
     * @description string转Json对象
     * @author qby
     */
    public static JSONObject parseFromJson(String json) {
        if (TextUtils.isEmpty(json))
            return null;

        try {
            JSONObject object = new JSONObject(json);
            return object;
        } catch (JSONException e) {
            Log.d(TAG,
                    "parseFromJson 错误： "
                            + e.getMessage());
            // e.printStackTrace();
        }
        return null;
    }


    /**
     * 校验银行卡卡号
     *
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId
                .substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     *
     * @param nonCheckCodeCardId
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null
                || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            // 如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }


    /**
     * 判断字符串中是否含空格、换行、制表符
     *
     * @param str 传入字符串
     * @return 返回true/false
     */
    public static boolean hasBlock(String str) {
        Pattern p = Pattern.compile("\\s+");
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 替换字符串中所有空格、换行、制表符
     *
     * @param str 传入字符串
     * @return 返回替换结果
     */
    public static String replaceBlock(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 获取 2017-10-11格式时间
     *
     * @param create_time 发送时间
     * @return 2017-10-11格式时间
     */
    public static String getYMDHMTime(String create_time) {
        if (TextUtils.isEmpty(create_time)) {
            return "";
        } else {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.format(new Date(create_time.length() == 13 ? Long.parseLong(create_time) : Long.parseLong(create_time) * 1000));
        }
    }

    /**
     * 获取 2017-10-11格式时间
     *
     * @param create_time 发送时间
     * @return 2017-10-11格式时间
     */
    public static String getYMDTime(String create_time) {
        if (TextUtils.isEmpty(create_time)) {
            return "";
        } else {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(new Date(create_time.length() == 13 ? Long.parseLong(create_time) : Long.parseLong(create_time) * 1000));
        }
    }

    /**
     * 获取 10-11 13:13格式时间
     *
     * @param send_time 发送时间
     * @return 10-11 13:13格式时间
     */
    public static String getHMTime(String send_time) {
        if (TextUtils.isEmpty(send_time)) {
            return "";
        } else {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(new Date(send_time.length() == 13 ? Long.parseLong(send_time) : Long.parseLong(send_time) * 1000));
        }
    }

    /**
     * 获取 10-11 13:13格式时间
     *
     * @param send_time 发送时间
     * @return 10-11 13:13格式时间
     */
    public static String getMHTime(String send_time) {
        if (TextUtils.isEmpty(send_time)) {
            return "";
        } else {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
            return sdf.format(new Date(send_time.length() == 13 ? Long.parseLong(send_time) : Long.parseLong(send_time) * 1000));
        }
    }

    /**
     * 获取 2017-11格式时间
     *
     * @param create_time 发送时间
     * @return 2017年11月格式时间
     */
    public static String getYMTime2(String create_time) {
        if (TextUtils.isEmpty(create_time)) {
            return "";
        } else {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
            return sdf.format(new Date(create_time.length() == 13 ? Long.parseLong(create_time) : Long.parseLong(create_time) * 1000));
        }
    }

    /**
     * 获取 2017-11格式时间
     *
     * @param create_time 发送时间
     * @return 2017-11格式时间
     */
    public static String getYMTime(String create_time) {
        if (TextUtils.isEmpty(create_time)) {
            return "";
        } else {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            return sdf.format(new Date(Long.parseLong(create_time) * 1000));
        }
    }

    /**
     * 保留两位小数
     *
     * @param num 传入double数据
     * @return 返回带两位小数的字符串
     */
    public static String save2(double num) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(num);
    }

    /**
     * 保留两位小数
     *
     * @param numStr 传入String数据
     * @return 返回带两位小数的字符串
     */
    public static String save2(String numStr) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(Double.valueOf(numStr));
    }

    /* */

    /**
     * 配置ImageLoder
     *//*
    public static void configImageLoader(Context ctx) {
        // 初始化ImageLoader
        @SuppressWarnings("deprecation")
        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .showStubImage(R.drawable.icon_stub) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.icon_empty) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.icon_error) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx).defaultDisplayImageOptions(options)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }*/

    //网络请求数据GET
    public static String requestHttps(String httpUrl) {
        BufferedReader reader;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        try {
            URL url = new URL(httpUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 隐藏软键盘
     *
     * @param act
     */
    public static void hideInputMethod(Activity act) {
        if (act != null && act.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param ctx
     * @param view
     */
    public static void hideInputMethod(Context ctx, View view) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * 显示软键盘
     *
     * @param ctx
     * @param view
     */
    public static void showInputMethod(Context ctx, View view) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * 手机号码的格式 加空格
     *
     * @param textView 文本框
     */
    public static void phoneNumAddSpace(final TextView textView, final TextWatcherCallBack callBack) {
        textView.addTextChangedListener(new TextWatcher() {
            int beforeTextLength = 0;
            int onTextLength = 0;
            boolean isChanged = false;

            int location = 0;// 记录光标的位置
            private char[] tempChar;
            private StringBuffer buffer = new StringBuffer();
            int konggeNumberB = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                beforeTextLength = s.length();
                if (buffer.length() > 0) {
                    buffer.delete(0, buffer.length());
                }
                konggeNumberB = 0;
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == ' ') {
                        konggeNumberB++;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                onTextLength = s.length();
                buffer.append(s.toString());
                if (onTextLength == beforeTextLength
                        || isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isChanged) {
                    location = textView.getSelectionEnd();
                    int index = 0;
                    while (index < buffer.length()) {
                        if (buffer.charAt(index) == ' ') {
                            buffer.deleteCharAt(index);
                        } else {
                            index++;
                        }
                    }

                    index = 0;
                    int konggeNumberC = 0;
                    while (index < buffer.length()) {
                        if ((index == 3 || index == 8)) {
                            buffer.insert(index, ' ');
                            konggeNumberC++;
                        }
                        index++;
                    }

                    if (konggeNumberC > konggeNumberB) {
                        location += (konggeNumberC - konggeNumberB);
                    }

                    tempChar = new char[buffer.length()];
                    buffer.getChars(0, buffer.length(), tempChar, 0);
                    String str = buffer.toString();
                    if (location > str.length()) {
                        location = str.length();
                    } else if (location < 0) {
                        location = 0;
                    }

                    textView.setText(str);
                    Editable etable = textView.getEditableText();
                    Selection.setSelection(etable, location);
                    if (callBack != null) {
                        callBack.afterTextChanged(s.toString());
                    }
                    isChanged = false;
                }
            }
        });
    }

    /**
     * 手机号码的格式 加空格
     *
     * @param mEditText 编辑框
     * @param callBack  回调
     */
    public static void phoneNumAddSpace(final EditText mEditText, final TextWatcherCallBack callBack) {
        mEditText.addTextChangedListener(new TextWatcher() {
            int beforeTextLength = 0;
            int onTextLength = 0;
            boolean isChanged = false;

            int location = 0;// 记录光标的位置
            private char[] tempChar;
            private StringBuffer buffer = new StringBuffer();
            int konggeNumberB = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                beforeTextLength = s.length();
                if (buffer.length() > 0) {
                    buffer.delete(0, buffer.length());
                }
                konggeNumberB = 0;
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == ' ') {
                        konggeNumberB++;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                onTextLength = s.length();
                buffer.append(s.toString());
                if (onTextLength == beforeTextLength
                        || isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isChanged) {
                    location = mEditText.getSelectionEnd();
                    int index = 0;
                    while (index < buffer.length()) {
                        if (buffer.charAt(index) == ' ') {
                            buffer.deleteCharAt(index);
                        } else {
                            index++;
                        }
                    }

                    index = 0;
                    int konggeNumberC = 0;
                    while (index < buffer.length()) {
                        if ((index == 3 || index == 8)) {
                            buffer.insert(index, ' ');
                            konggeNumberC++;
                        }
                        index++;
                    }

                    if (konggeNumberC > konggeNumberB) {
                        location += (konggeNumberC - konggeNumberB);
                    }

                    tempChar = new char[buffer.length()];
                    buffer.getChars(0, buffer.length(), tempChar, 0);
                    String str = buffer.toString();
                    if (location > str.length()) {
                        location = str.length();
                    } else if (location < 0) {
                        location = 0;
                    }

                    mEditText.setText(str);
                    Editable etable = mEditText.getText();
                    Selection.setSelection(etable, location);
                    if (callBack != null) {
                        callBack.afterTextChanged(s.toString());
                    }
                    isChanged = false;
                }
            }
        });
    }


    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    public static boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 检查权限列表
     *
     * @param context
     * @param op       这个值被hide了，去AppOpsManager类源码找，如位置权限  AppOpsManager.OP_GPS==2
     * @param opString 如判断定位权限 AppOpsManager.OPSTR_FINE_LOCATION
     * @return @see 如果返回值 AppOpsManagerCompat.MODE_IGNORED 表示被禁用了
     */
    public static int checkOp(Context context, int op, String opString) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
//            Object object = context.getSystemService("appops");
            Class c = object.getClass();
            try {
                Class[] cArg = new Class[3];
                cArg[0] = int.class;
                cArg[1] = int.class;
                cArg[2] = String.class;
                Method lMethod = c.getDeclaredMethod("checkOp", cArg);
                return (Integer) lMethod.invoke(object, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                e.printStackTrace();
                if (Build.VERSION.SDK_INT >= 23) {
                    return AppOpsManagerCompat.noteOp(context, opString, context.getApplicationInfo().uid,
                            context.getPackageName());
                }

            }
        }
        return -1;
    }

    /**
     * 启动到应用商店app详情界面
     *
     * @param marketPkg 应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面，某些应用商店可能会失败
     */
    public static void launchAppDetail(Activity activity, String marketPkg) {
        try {
            String appPkg = activity.getPackageName();
            if (TextUtils.isEmpty(appPkg)) return;

            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    @Nullable
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取正在运行的进程
     *
     * @param cxt
     * @param pid
     * @return
     */
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    /**
     * 获取应用版本号
     *
     * @param context 上下文
     * @return 版本号
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // 获取安装包信息
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            MPLog.e("VersionInfo", e.getLocalizedMessage());
        }
        return versionName;
    }

    /**
     * 获取应用版本号
     *
     * @param context 上下文
     * @return 版本号
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = 1;
        try {
            // 获取安装包信息
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
            if (versionCode <= 0) {
                return 1;
            }
        } catch (Exception e) {
            MPLog.e("VersionInfo", e.getLocalizedMessage());
        }
        return versionCode;
    }

    /**
     * 判断是否开启了模拟位置
     *
     * @param context 上下文
     * @return boolean
     */
    public static boolean allowMockLocation(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断是否有需要模拟位置权限的应用
     *
     * @param context 上下文
     * @return boolean
     */
    public static boolean hasMockPermissionApps(Context context) {
        int count = 0;
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);
                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;
                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                MPLog.e("MockPermission", "Got exception " + e.getMessage());
            }
        }
        if (count > 0)
            return true;
        return false;
    }


    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    public static void cleanExternalFiles(Context context){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + context.getPackageName();
            File file = new File(path);
            if(file.exists()){
                deleteFilesByDirectory(file);
            }
        } else {
            deleteFilesByDirectory(context.getFilesDir());
        }
    }

    private static boolean deleteFilesByDirectory(File directory) {
        if (directory != null && directory.isDirectory()) {
            String[] children = directory.list();
            for (String child : children) {
                boolean success = deleteDir(new File(directory, child));
                if (!success) {
                    return false;
                }
            }
        }
        return directory.delete();

    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();

    }

    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }
}
