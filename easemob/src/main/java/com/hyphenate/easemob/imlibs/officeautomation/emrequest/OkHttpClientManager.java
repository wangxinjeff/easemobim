package com.hyphenate.easemob.imlibs.officeautomation.emrequest;

import android.text.TextUtils;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.hyphenate.easemob.imlibs.easeui.token.TokenManager;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.imlibs.mp.utils.MPUtil;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.okhttp.progress.helper.ProgressHelper3;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.okhttp.progress.listener.ProgressListener;
import com.hyphenate.util.EMLog;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpClientManager {

    private static final String TAG = OkHttpClientManager.class.getSimpleName();

    private static OkHttpClientManager mInstance;
    private OkHttpClient mOkHttpClient;

    private OkHttpClientManager() {
        mOkHttpClient = new OkHttpClient.Builder()
				.addNetworkInterceptor(new StethoInterceptor())
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
//				.proxy(Proxy.NO_PROXY)
                .build();
//		mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
//		mOkHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);
//		mOkHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
    }

    public void setCertificates(InputStream... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException ignored) {
                }
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory
                    .getDefaultAlgorithm());

            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            mOkHttpClient = mOkHttpClient.newBuilder().sslSocketFactory(sslContext.getSocketFactory()).build();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }




    /**
     * 同步的Get请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    private Response _getSync(String url, Map<String, String> headers) throws IOException {
        final Request request = new Request.Builder().url(url).headers(getHeaders(headers)).build();
        Call call = mOkHttpClient.newCall(request);
        return call.execute();
    }

    private Headers getHeaders(Map<String, String> params) {
        if (params == null) {
            params = new ConcurrentHashMap<>();
        }
//        TokenManager.Token token = TokenManager.getInstance().getToken();
//        if (token != null) {
//            params.put("Cookie", token.name + "=" + token.value);
//        }
        String strSession = TokenManager.getInstance().getSession();
        if (!TextUtils.isEmpty(strSession)) {
            params.put("Cookie", strSession);
        }
        if(!params.containsKey("User-Agent")) {
            params.put("User-Agent", MPUtil.getDefaultUserAgent());
        }
        return Headers.of(params);
    }

    /**
     * 同步的Get请求
     *
     * @param url
     * @return 字符串
     * @throws IOException
     */
    private String _getAsString(String url, Map<String, String> headers) throws IOException {
        Response execute = _getSync(url, headers);
        return execute.body().string();
    }

    /**
     * 异步的Get请求
     *
     * @param url
     * @param callback
     */
    private Call _getAsync(String url, Map<String, String> headers, final EMDataCallBack<String> callback) {
        final Request request = new Request.Builder().url(url).headers(getHeaders(headers)).build();
        return deliveryResult(callback, request);
    }

    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return
     * @throws IOException
     */
    private Response _post(String url, Map<String, String> headers, Param... params) throws IOException {
        Request request = buildPostRequest(url, headers, params);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return
     * @throws IOException
     */
    private Response _post(String url, Map<String, String> headers, Map<String, Object> params) throws IOException {
        Request request = buildPostRequest(url, headers, params);
        return mOkHttpClient.newCall(request).execute();
    }

    private Response _post(String url, Map<String, String> headers, String jsonBody) throws IOException {
        Request request = buildPostRequest(url, headers, jsonBody);
        return mOkHttpClient.newCall(request).execute();
    }


    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return 字符串
     * @throws IOException
     */
    private String _postAsString(String url, Map<String, String> headers, Param... params) throws IOException {
        Response response = _post(url, headers, params);
        return response.body().string();
    }

    /**
     * 异步的Post请求
     *
     * @param url
     * @param params
     * @param callback
     */
    private Call _postAsync(String url, Map<String, String> headers, Map<String, Object> params,
                            final EMDataCallBack<String> callback) {
        Param[] paramsArr = map2Params(params);
        Request request = buildPostRequest(url, headers, paramsArr);
        return deliveryResult(callback, request);
    }

    private Call _postAsync(String url, Map<String, String> headers, String jsonBody, EMDataCallBack<String> callback) {
        Request request = buildPostRequest(url, headers, jsonBody);
        return deliveryResult(callback, request);
    }

    private void _postLoginAsync(String url, Map<String, Object> params, final EMDataCallBack<String> callback) {
        Param[] paramsArr = map2Params(params);
        Request request = buildLoginRequest(url, paramsArr);
        deliveryLoginResult(callback, request);
    }


    /**
     * 同步的post请求
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    private Response _put(String url, Param... params) throws IOException {
        Request request = buildPutRequest(url, null, params);
        return mOkHttpClient.newCall(request).execute();
    }

    private Call _putAsync(String url, Map<String, String> headers, String jsonBody, EMDataCallBack<String> callback) {
        Request request = buildPutRequest(url, headers, jsonBody);
        return deliveryResult(callback, request);
    }

    /**
     * 同步的Delete请求
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    private Response _delete(String url, Param... params) throws IOException {
        Request request = buildDeleteRequest(url, null);
        return mOkHttpClient.newCall(request).execute();
    }

    private Call _deleteAsync(String url, Map<String, String> headers, EMDataCallBack<String> callback) {
        Request request = buildDeleteRequest(url, headers);
        return deliveryResult(callback, request);
    }

    private Call _deleteAsync(String url, Map<String, String> headers, String jsonBody, EMDataCallBack<String> callback) {
        Request request = buildDeleteRequest(url, headers, jsonBody);
        return deliveryResult(callback, request);
    }

    /**
     * 异步的Put请求
     *
     * @param url
     * @param params
     * @param callback
     */
    private Call _putAsync(String url, Map<String, String> headers, Map<String, Object> params,
                           final EMDataCallBack<String> callback) {
        Param[] paramsArr = map2Params(params);
        Request request = buildPutRequest(url, headers, paramsArr);
        return deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys
     * @throws IOException
     */
    private Call _postAsync(String url, final EMDataCallBack<String> callback, File[] files, String[] fileKeys,
                            Param... params) throws IOException {
        final ProgressListener progressListener = new ProgressListener() {
            @Override
            public void onProgress(long bytesWrite, long contentLength, boolean done) {
                long degree = (100 * bytesWrite) / contentLength;
                if (!done) {
                    if (callback != null) {
                        callback.onProgress(Long.valueOf(degree).intValue());
                    }
                }
            }
        };
        Request request = buildMultipartFormRequest(url, files, fileKeys, params, progressListener);
        return deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件不带参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @throws IOException
     */
    private Call _postAsync(String url, final EMDataCallBack<String> callback, File file, String fileKey) throws IOException {
        final ProgressListener progressListener = new ProgressListener() {
            @Override
            public void onProgress(long bytesWrite, long contentLength, boolean done) {
                long degree = (100 * bytesWrite) / contentLength;
                if (!done) {
                    if (callback != null) {
                        callback.onProgress(Long.valueOf(degree).intValue());
                    }
                }
            }
        };
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null, progressListener);
        return deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件且携带其他form参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @param params
     * @throws IOException
     */
    private Call _postAsync(String url, final EMDataCallBack<String> callback, File file, String fileKey, Param... params)
            throws IOException {
        final ProgressListener progressListener = new ProgressListener() {
            @Override
            public void onProgress(long bytesWrite, long contentLength, boolean done) {
                long degree = (100 * bytesWrite) / contentLength;
                if (!done) {
                    if (callback != null) {
                        callback.onProgress(Long.valueOf(degree).intValue());
                    }
                }
            }
        };
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params, progressListener);
        return deliveryResult(callback, request);
    }

    private void _downloadAsync(String url, final String destFilePath, final EMDataCallBack<String> callback) {
//		HDLog.e(TAG,"download url:" + url);
        final ProgressListener progressResponseListener = new ProgressListener() {
            @Override
            public void onProgress(long bytesRead, long contentLength, boolean done) {
                if (contentLength != -1) {
                    //长度未知的情况下返回-1
                    if (callback != null) {
                        callback.onProgress((int) ((100 * bytesRead) / contentLength));
                    }
                }
            }
        };
        try {
            final Request request = new Request.Builder().url(url).headers(getHeaders(null)).build();
            //包装Response使其支持进度回调
            ProgressHelper3.addProgressResponseListener(mOkHttpClient, progressResponseListener).newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (callback != null) {
                        callback.onError(-1, "IOException:" + e.getMessage());
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    int status = response.code();
                    if (status >= 200 && status < 300) {
                        InputStream is = null;
                        byte[] buf = new byte[2048];
                        int len;
                        FileOutputStream fos = null;
                        File file = null;
                        try {
//                            String header = response.header("Content-Disposition");
//                            String fileName = "";
//                            if (header != null) {
//                                fileName = header.substring(header.lastIndexOf("=") + 2, header.lastIndexOf("\""));
//                            }
                            is = response.body().byteStream();
                            file = new File(destFilePath);
                            fos = new FileOutputStream(file);
                            while ((len = is.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                            }
                            fos.flush();
                            if (callback != null) {
                                callback.onSuccess(destFilePath);
                            }
                        } catch (IOException e) {
                            if (file != null && file.exists()) {
                                file.delete();
                            }
                            if (callback != null) {
                                callback.onError(response.code(), e.getMessage());
                            }
                        } finally {
                            try {
                                if (is != null)
                                    is.close();
                            } catch (IOException ignored) {
                            }
                            try {
                                if (fos != null)
                                    fos.close();
                            } catch (Exception ignored) {
                            }
                        }
                    } else {
                        if (callback != null) {
                            callback.onError(response.code(), "fail down file");
                        }
                    }
                }
            });

        } catch (Exception e) {
//			e.printStackTrace();
            if (callback != null) {
                callback.onError(-1, "IOException:" + e.getMessage());
            }

        }
    }


    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    // *************对外公布的方法************

    public Response getSync(String url, Map<String, String> headers) throws IOException {
        return getInstance()._getSync(url, headers);
    }

    public String getAsString(String url, Map<String, String> headers) throws IOException {
        return getInstance()._getAsString(url, headers);
    }

    public Call getAsync(String url, Map<String, String> headers, EMDataCallBack<String> callback) {
        return getInstance()._getAsync(url, headers, callback);
    }

    public Response post(String url, Map<String, String> headers, Param... params) throws IOException {
        return getInstance()._post(url, headers, params);
    }

    public Response post(String url, Map<String, String> headers, Map<String, Object> params) throws IOException {
        return getInstance()._post(url, headers, params);
    }

    public Response post(String url, Map<String, String> headers, String jsonBody) throws IOException {
        return getInstance()._post(url, headers, jsonBody);
    }

    // public String postAsString(String url, Map<String, String> headers,
    // Param... params) throws IOException {
    // return getInstance()._postAsString(url, headers, params);
    // }

    public Call postAsync(String url, Map<String, String> headers, Map<String, Object> params,
                          final EMDataCallBack<String> callback) {
        return getInstance()._postAsync(url, headers, params, callback);
    }

    public Call postAsync(String url, Map<String, String> headers, String jsonbody,
                          final EMDataCallBack<String> callback) {
        return getInstance()._postAsync(url, headers, jsonbody, callback);
    }

    public void postLoginAsync(String url, Map<String, Object> params,
                               final EMDataCallBack<String> callback) {
        getInstance()._postLoginAsync(url, params, callback);
    }

    public Call putAsync(String url, Map<String, String> headers, String jsonbody, final EMDataCallBack<String> callback) {
        return getInstance()._putAsync(url, headers, jsonbody, callback);
    }

    public Call putAsync(String url, Map<String, String> headers, Map<String, Object> params,
                         final EMDataCallBack<String> callback) {
        return getInstance()._putAsync(url, headers, params, callback);
    }

    public Call deleteAsync(String url, Map<String, String> headers, final EMDataCallBack<String> callback) {
        return getInstance()._deleteAsync(url, headers, callback);
    }

    public Call deleteAsync(String url, Map<String, String> headers, String jsonBody, final EMDataCallBack<String> callback) {
        return getInstance()._deleteAsync(url, headers, jsonBody, callback);
    }

    public Call postAsync(String url, EMDataCallBack<String> callback, File[] files, String[] fileKeys,
                          Param... params) throws IOException {
        return getInstance()._postAsync(url, callback, files, fileKeys, params);
    }

    public Call postAsync(String url, EMDataCallBack<String> callback, File file, String fileKey)
            throws IOException {
        return getInstance()._postAsync(url, callback, file, fileKey);
    }

    public Call postAsync(String url, EMDataCallBack<String> callback, File file, String fileKey,
                          Param... params) throws IOException {
        return getInstance()._postAsync(url, callback, file, fileKey, params);
    }

    public void downloadAsync(String url, String destFile, EMDataCallBack<String> callback) {
        getInstance()._downloadAsync(url, destFile, callback);
    }


    // *************对外公布的方法 END************

    private Param[] validateParam(Param[] params) {
        if (params == null)
            return new Param[0];
        else
            return params;
    }

    private Param[] map2Params(Map<String, Object> params) {
        if (params == null)
            return new Param[0];
        int size = params.size();
        Param[] res = new Param[size];
        Set<Entry<String, Object>> entries = params.entrySet();
        int i = 0;
        for (Entry<String, Object> entry : entries) {
            res[i++] = new Param(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return res;
    }

    private Request buildPostRequest(String url, Map<String, String> headers, Param[] params) {
        if (params == null) {
            params = new Param[0];
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).headers(getHeaders(headers)).post(requestBody).build();
    }

    private Request buildLoginRequest(String url, Param[] params) {
        if (params == null) {
            params = new Param[0];
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).post(requestBody).build();
    }

    private Request buildPostRequest(String url, Map<String, String> headers, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Entry<String, Object> param : params.entrySet()) {
            builder.add(param.getKey(), String.valueOf(param.getValue()));
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).headers(getHeaders(headers)).post(requestBody).build();
    }

    private Request buildPostRequest(String url, Map<String, String> headers, String jsonBody) {
        if (jsonBody == null) {
            jsonBody = "";
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
        return new Request.Builder().url(url).headers(getHeaders(headers)).post(requestBody).build();
    }

    private Request buildPutRequest(String url, Map<String, String> headers, Param[] params) {
        if (params == null) {
            params = new Param[0];
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).headers(getHeaders(headers)).put(requestBody).build();
    }

    private Request buildPutRequest(String url, Map<String, String> headers, String jsonBody) {
        if (jsonBody == null) {
            jsonBody = "";
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
        return new Request.Builder().url(url).headers(getHeaders(headers)).put(requestBody).build();
    }

    private Request buildDeleteRequest(String url, Map<String, String> headers) {
        return new Request.Builder().url(url).headers(getHeaders(headers)).delete().build();
    }

    private Request buildDeleteRequest(String url, Map<String, String> headers, String jsonBody) {
        if (jsonBody == null) {
            jsonBody = "";
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
        return new Request.Builder().url(url).headers(getHeaders(headers)).delete(requestBody).build();
    }

    private Call deliveryResult(final EMDataCallBack<String> callback, final Request request) {
        Call mCall = mOkHttpClient.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    callback.onError(-1, e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, final Response response) {
                int statusCode = response.code();
                try {
                    if (statusCode >= 200 && statusCode <= 204) {
                        if (callback != null) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                try {
                                    String ret = responseBody.string();
                                    JSONObject body = new JSONObject(ret);
                                    if (response.header("Set-Cookie") != null) {
                                        JSONObject entity = body.optJSONObject("entity");
                                        JSONObject tokenEntity = new JSONObject();
                                        String[] cookie = response.header("Set-Cookie").split(";");
                                        tokenEntity.put("name", cookie[0].substring(0, cookie[0].indexOf("=")));
                                        tokenEntity.put("value", cookie[0].substring(cookie[0].indexOf("=") + 1));
                                        tokenEntity.put("expires", cookie[1].substring(cookie[1].indexOf("=") + 1));
                                        entity.put("token", tokenEntity);
                                        body.put("entity", entity);
                                        MPLog.e(TAG, "cookieCallback:" + response.header("Set-Cookie"));
                                        TokenManager.getInstance().setSession(response.header("Set-Cookie"));
                                    }
                                    callback.onSuccess(body.toString());
                                }catch (Exception e) {
                                    e.printStackTrace();
                                    callback.onSuccess("");
                                }
                            } else {
                                callback.onSuccess("");
                            }
                        }
                    } else if (statusCode == 401) {
                        if (callback != null) {
                            callback.onAuthenticationException();
                        }
                        EMLog.d(TAG, "onAuthenticationException:request:" + request.toString());
                        MPClient.get().clearCurrentUserInfo();
                    } else {
                        if (callback != null) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                callback.onError(statusCode, responseBody.string());
                            } else {
                                callback.onError(statusCode, response.message());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onError(-1, e.getMessage());
                    }
                }
            }
        });
        return mCall;
    }

    private void deliveryLoginResult(final EMDataCallBack<String> callback, Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    callback.onError(-1, e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, final Response response) {
                int statusCode = response.code();
                EMLog.d(TAG, "statusCode = " + statusCode);
                try {
                    if (statusCode >= 200 && statusCode <= 204) {
                        if (callback != null) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                callback.onSuccess(responseBody.string());
                            } else {
                                callback.onSuccess("");
                            }
                        }
                    } else {
                        if (callback != null) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                callback.onError(statusCode, responseBody.string());
                            } else {
                                callback.onError(statusCode, response.message());
                            }
                        }
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onError(-1, e.getMessage());
                    }
                }
            }
        });
    }

    private Request buildMultipartFormRequest1(String url, File[] files, String[] fileKeys, Param[] params) {
        params = validateParam(params);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Param param : params) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                    RequestBody.create(null, param.value));
        }
        if (files != null) {
            RequestBody fileBody;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                // TODO 根据文件名设置Content-Type
                builder.addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"" + fileKeys[i] + "\"; filename=\""
                                + fileName + "\""), fileBody);
            }

        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).headers(getHeaders(null)).post(requestBody).build();
    }

    private Request buildMultipartFormRequest(String url, File[] files, String[] fileKeys, Param[] params, final ProgressListener progressListener) {
        params = validateParam(params);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Param param : params) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                    RequestBody.create(null, param.value));

        }
        if (files != null) {
            RequestBody fileBody;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
//				fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

                String encodedFileName = "";
                try{
                    encodedFileName = URLEncoder.encode(fileName, String.valueOf("UTF-8"));
                }catch(Exception e){
                    e.printStackTrace();
                }
                // TODO 根据文件名设置Content-Type
                builder.addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"" + fileKeys[i] + "\"; filename=\""
                                + encodedFileName + "\""), fileBody);

//				builder.addFormDataPart("Content-Type","image/png");
            }

        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).headers(getHeaders(null)).post(ProgressHelper3.addProgressRequestListener(requestBody, progressListener)).build();
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    public static class Param {
        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }

}
