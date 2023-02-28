package com.hyphenate.easemob.imlibs.officeautomation.emrequest;

import android.text.TextUtils;

import com.easemob.emssl.EMCookieCallback;
import com.easemob.emssl.EMHttpCallback;
import com.easemob.emssl.MPNetManager;
import com.easemob.emssl.utils.EMMisc;
import com.hyphenate.easemob.imlibs.easeui.token.TokenManager;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.imlibs.mp.utils.MPUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;

public class EMCurlManager {

    private static final String TAG = "EMCurlManager";
    private static boolean checkSession(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            String status = jsonObj.optString("status");
            if ("ERROR".equalsIgnoreCase(status)) {
                long errorCode = jsonObj.optLong("errorCode");
                if (errorCode == 1000007L) {
                    // session is null
                    MPClient.get().clearCurrentUserInfo();
                    return false;
                }
            }
        } catch (Exception e) {
        }
        return true;
    }

    private static MPNetManager.Builder getBuilder() {
        MPNetManager.Builder builder = new MPNetManager.Builder();
        String strSession = TokenManager.getInstance().getSession();
        builder.addHeader("User-Agent", MPUtil.getDefaultUserAgent());
        if (!TextUtils.isEmpty(strSession)) {
            builder.setCookie(strSession);
        }
        builder.setCertPath(EMMisc.getAppDir(MPClient.get().getAppContext()) + EMMisc.CERT_NAME);
        builder.setSSLCertPath(EMMisc.getAppDir(MPClient.get().getAppContext()) + EMMisc.CERT_NAME_CLIENT_CER, EMMisc.getAppDir(MPClient.get().getAppContext()) + EMMisc.CERT_NAME_CLIENT_KEY, "");
        return builder;
    }


    public static void setHttpGetDataCallBack(String paramUrl, Map<String, String> headers,
                                              final EMDataCallBack<String> callback) {

        getBuilder().setUrlPath(paramUrl).addHeaders(headers).get(new EMHttpCallback() {
            @Override
            public void success(String response) {
                if (!checkSession(response)) {
                    return;
                }
                try {
                    JSONObject value = new JSONObject(response);
                    long serverTime = value.getLong("responseDate");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (callback != null) {
                    callback.onSuccess(response);

                }
            }

            @Override
            public void progress(float percent) {
                if (callback != null) {
                    callback.onProgress((int) percent);

                }
            }

            @Override
            public void fail(int errCode) {
                if (callback != null) {
                    callback.onError(errCode, "unknown");
                }
            }
        });

    }


    public static void setHttpPostDataCallBack(String paramUrl, Map<String, String> headers, Map<String, Object> map, final EMDataCallBack<String> callback) {
        getBuilder()
                .setUrlPath(paramUrl)
                .addHeaders(headers)
                .addFormDatas(map)
                .postFormData(new EMHttpCallback() {
                    @Override
                    public void success(String response) {
                        if (!checkSession(response)) {
                            return;
                        }
                        if (callback != null) {
                            callback.onSuccess(response);
                        }
                    }

                    @Override
                    public void progress(float percent) {
                        if (callback != null) {
                            callback.onProgress((int) percent);
                        }
                    }

                    @Override
                    public void fail(int errCode) {
                        if (callback != null) {
                            callback.onError(errCode, "unknown");
                        }
                    }
                });
    }


    public static void setHttpPostDataCallBackWithCookie(String paramUrl, String jsonBody, final EMDataCallBack<String> callback) {
        getBuilder()
                .setUrlPath(paramUrl)
                .setJsonContent(jsonBody)
                .postJson(new EMHttpCallback() {
                    @Override
                    public void success(String response) {
                        if (!checkSession(response)) {
                            return;
                        }
                        if (callback != null) {
                            callback.onSuccess(response);
                        }
                    }

                    @Override
                    public void progress(float percent) {
                        if (callback != null) {
                            callback.onProgress((int) percent);
                        }
                    }

                    @Override
                    public void fail(int errCode) {
                        if (callback != null) {
                            callback.onError(errCode, "unknown");
                        }
                    }
                }, new EMCookieCallback() {
                    @Override
                    public void success(String originCookie) {
                        String cookie = dealCookie(originCookie);
                        MPLog.e(TAG, "cookieCallback:" + cookie);
                        TokenManager.getInstance().setSession(cookie);
                    }
                });
    }


    private static String dealCookie(String originCookies) {
        StringBuilder sb = new StringBuilder();
        String[] strArr = originCookies.split(";");
        for (String item : strArr) {
            String[] strs = item.split("\t");
            if (strs.length >= 2) {
                String k = strs[strs.length - 2];
                String v = strs[strs.length - 1];
                sb.append(k).append("=").append(v).append(";");
            }
        }
        return sb.toString();
    }


    public static void setHttpPostDataCallBack(String paramUrl, String jsonBody, final EMDataCallBack<String> callback) {
        getBuilder()
                .setUrlPath(paramUrl)
                .setJsonContent(jsonBody)
                .postJson(new EMHttpCallback() {
                    @Override
                    public void success(String response) {
                        if (!checkSession(response)) {
                            return;
                        }
                        if (callback != null) {
                            callback.onSuccess(response);
                        }
                    }

                    @Override
                    public void progress(float percent) {
                        if (callback != null) {
                            callback.onProgress((int) percent);
                        }
                    }

                    @Override
                    public void fail(int errCode) {
                        if (callback != null) {
                            callback.onError(errCode, "unknown");
                        }
                    }
                });
    }

    public static void setHttpDeleteDataCallBack(String paramUrl, final EMDataCallBack<String> callback) {
        getBuilder()
                .setUrlPath(paramUrl)
                .delete(new EMHttpCallback() {
                    @Override
                    public void success(String response) {
                        if (!checkSession(response)) {
                            return;
                        }
                        if (callback != null) {
                            callback.onSuccess(response);
                        }
                    }

                    @Override
                    public void progress(float percent) {
                        if (callback != null) {
                            callback.onProgress((int) percent);
                        }
                    }

                    @Override
                    public void fail(int errCode) {
                        if (callback != null) {
                            callback.onError(errCode, "unknown");
                        }
                    }
                });
    }


    public static void setHttpDeleteDataCallBack(String paramUrl, String jsonBody, final EMDataCallBack<String> callback) {
        getBuilder().setUrlPath(paramUrl)
                .setJsonContent(jsonBody)
                .delete(new EMHttpCallback() {
                    @Override
                    public void success(String response) {
                        if (!checkSession(response)) {
                            return;
                        }
                        if (callback != null) {
                            callback.onSuccess(response);

                        }
                    }

                    @Override
                    public void progress(float percent) {
                        if (callback != null) {
                            callback.onProgress((int) percent);
                        }
                    }

                    @Override
                    public void fail(int errCode) {
                        if (callback != null) {
                            callback.onError(errCode, "unknown");

                        }
                    }
                });
    }

    public static void setHttpPutDataCallBack(String paramUrl, String jsonBody, final EMDataCallBack<String> callback) {
        getBuilder().setUrlPath(paramUrl)
                .setJsonContent(jsonBody)
                .putJson(new EMHttpCallback() {
                    @Override
                    public void success(String response) {
                        if (!checkSession(response)) {
                            return;
                        }
                        if (callback != null) {
                            callback.onSuccess(response);
                        }
                    }

                    @Override
                    public void progress(float percent) {
                        if (callback != null) {
                            callback.onProgress((int) percent);
                        }
                    }

                    @Override
                    public void fail(int errCode) {
                        if (callback != null) {
                            callback.onError(errCode, "unknown");
                        }
                    }
                });
    }


    public static void postFile(String remoteUrl, File file, int type, final EMDataCallBack<String> callBack) {
        getBuilder().setUrlPath(remoteUrl)
                .setLocalFilePath(file.getAbsolutePath())
                .setFileKeyName("file")
                .setFileName(file.getName())
                .postFile(new EMHttpCallback() {
                    @Override
                    public void success(String response) {
                        if (callBack != null) {
                            callBack.onSuccess(response);
                        }
                    }

                    @Override
                    public void progress(float percent) {
                        if (callBack != null) {
                            callBack.onProgress((int) percent);
                        }

                    }

                    @Override
                    public void fail(int errCode) {
                        if (callBack != null) {
                            callBack.onError(errCode, "unknown");
                        }
                    }
                });
    }

    public static void downloadFile(String url, String localFilePath, final EMDataCallBack<String> callBack) {
        getBuilder()
                .setUrlPath(url)
                .setLocalFilePath(localFilePath)
                .downloadFile(new EMHttpCallback() {
                    @Override
                    public void success(String respones) {
                        if (callBack != null) {
                            callBack.onSuccess(respones);
                        }
                    }

                    @Override
                    public void progress(float percent) {
                        if (callBack != null) {
                            callBack.onProgress((int) percent);
                        }
                    }

                    @Override
                    public void fail(int errcode) {
                        if (callBack != null) {
                            callBack.onError(errcode, "unknown");
                        }
                    }
                });

    }


}
