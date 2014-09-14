package com.github.cuter44.paperfly;

public class Constants
{
    /** 配置:是否清除已读消息
     */
    public static Boolean CONF_CLEAR_AFTER_READ = Boolean.FALSE;
    /** 配置:长连接的最长 hold on 时间, 单位秒
     * 长于该时间的长连接请求将被置为该时间, 该时间越长意味可能要同时维持更多的TCP连接
     */
    public static Integer CONF_RETRIEVE_MAX_WAIT = 30;

    /** 异常处理, 当发生异常时, 将被转发到该地址.
     */
    public static final String URI_ERROR_HANDLER = "/sys/exception.api";
    /** 异常处理, 当发生异常转发时, 会在 request 中以该键名附带抛出的异常
     */
    public static final String KEY_EXCEPTION = "exception";
}
