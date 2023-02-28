package com.hyphenate.easemob.imlibs.officeautomation.emrequest.okhttp.progress.helper;


import com.hyphenate.easemob.imlibs.officeautomation.emrequest.okhttp.progress.ProgressRequestBody3;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.okhttp.progress.ProgressResponseBody3;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.okhttp.progress.listener.ProgressListener;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 进度回调辅助类
 */
public class ProgressHelper3 {
	
	/**
     * 包装OkHttpClient，用于下载文件的回调
     * @param client 待包装的OkHttpClient
     * @param progressListener 进度回调接口
     * @return 包装后的OkHttpClient，使用clone方法返回
     */
    public static OkHttpClient addProgressResponseListener(OkHttpClient client, final ProgressListener progressListener){
        //增加拦截器
        OkHttpClient.Builder builder = client.newBuilder();
        builder.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //拦截
                Response originalResponse = chain.proceed(chain.request());
                //包装响应体并返回
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody3(originalResponse.body(), progressListener))
                        .build();
            }
        });
        return builder.build();
    }

    /**
     * 包装请求体用于上传文件的回调
     * @param requestBody 请求体RequestBody
     * @param progressRequestListener 进度回调接口
     * @return 包装后的进度回调请求体
     */
    public static ProgressRequestBody3 addProgressRequestListener(RequestBody requestBody, ProgressListener progressRequestListener){
        //包装请求体
        return new ProgressRequestBody3(requestBody,progressRequestListener);
    }
}
