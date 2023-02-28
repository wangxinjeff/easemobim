package com.hyphenate.easemob.im.officeautomation.exception;

/**
 * @author qby
 * @date 2018/7/6 9:42
 * 自定义异常基类
 */
public class BaseException extends Exception {
    public BaseException() {
        super();
    }

    public BaseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BaseException(String detailMessage) {
        super(detailMessage);
    }

    public BaseException(Throwable throwable) {
        super(throwable);
    }
}
