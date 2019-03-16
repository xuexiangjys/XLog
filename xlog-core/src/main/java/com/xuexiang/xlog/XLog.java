/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.xlog;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xuexiang.xlog.crash.CrashHandler;
import com.xuexiang.xlog.logger.ILogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * XLog全局日志打印
 *
 * @author xuexiang
 * @since 2019/3/16 下午7:33
 */
public class XLog implements ILogger {
    private static Context sContext;

    private static XLog sInstance;
    /**
     * 日志容器.
     */
    private final Map<String, ILogger> mLoggers;

    private XLog() {
        mLoggers = new ConcurrentHashMap<>();
    }

    /**
     * 初始化日志【包括崩溃处理】
     *
     * @param application
     */
    public static void init(@NonNull Application application) {
        sContext = application.getApplicationContext();
        CrashHandler.getInstance().init(sContext);
    }

    /**
     * 初始化日志【不包括崩溃处理】
     *
     * @param context
     */
    public static void init(@NonNull Context context) {
        sContext = context.getApplicationContext();
    }

    /**
     * 初始化崩溃处理
     *
     * @param context
     */
    public static void initCrashHandler(@NonNull Context context) {
        CrashHandler.getInstance().init(context);
    }

    public static Context getContext() {
        testInitialize();
        return sContext;
    }

    private static void testInitialize() {
        if (sContext == null) {
            throw new ExceptionInInitializerError("请先在全局Application中调用 XLog.init() 初始化！");
        }
    }

    /**
     * 获取日志实例
     *
     * @return
     */
    public static XLog get() {
        if (sInstance == null) {
            synchronized (XLog.class) {
                if (sInstance == null) {
                    sInstance = new XLog();
                }
            }
        }
        return sInstance;
    }

    public Map<String, ILogger> getLoggers() {
        return mLoggers;
    }

    /**
     * 获取日志
     *
     * @param logName
     * @return
     */
    @Nullable
    public ILogger getLogger(String logName) {
        return mLoggers.containsKey(logName) ? mLoggers.get(logName) : null;
    }

    /**
     * 增加日志
     *
     * @param logger
     */
    public void addLogger(@NonNull ILogger logger) {
        mLoggers.put(logger.getName(), logger);
    }

    /**
     * 去除日志
     *
     * @param logName
     */
    public void removeLogger(String logName) {
        mLoggers.remove(logName);
    }

    /**
     * 去除日志
     *
     * @param logger
     */
    public void removeLogger(@NonNull ILogger logger) {
        mLoggers.remove(logger.getName());
    }

    /**
     * 清除日志
     */
    public void clearLoggers() {
        mLoggers.clear();
    }

    /**
     * 日志打印者名称
     *
     * @return
     */
    @Override
    public String getName() {
        return "XLog";
    }

    /**
     * 设置tag标签
     *
     * @param tag
     * @return
     */
    @Override
    public XLog tag(String tag) {
        for (ILogger logger : mLoggers.values()) {
            logger.tag(tag);
        }
        return this;
    }

    /**
     * 设置tag标签
     *
     * @param logName
     * @param tag
     * @return
     */
    public XLog tag(String logName, String tag) {
        ILogger logger = getLogger(logName);
        if (logger != null) {
            logger.tag(tag);
        }
        return this;
    }

    /**
     * 调试开关
     *
     * @param isDebug
     */
    @Override
    public XLog debug(boolean isDebug) {
        for (ILogger logger : mLoggers.values()) {
            logger.debug(isDebug);
        }
        return this;
    }

    /**
     * 调试开关
     *
     * @param logName
     * @param isDebug
     * @return
     */
    public XLog debug(String logName, boolean isDebug) {
        ILogger logger = getLogger(logName);
        if (logger != null) {
            logger.debug(isDebug);
        }
        return this;
    }

    /**
     * 打印调试日志
     *
     * @param message string模版
     * @param args    模版参数
     */
    @Override
    public void d(String message, Object... args) {
        for (ILogger logger : mLoggers.values()) {
            logger.d(message, args);
        }
    }

    /**
     * 打印调试日志
     *
     * @param object 对象
     */
    @Override
    public void d(Object object) {
        for (ILogger logger : mLoggers.values()) {
            logger.d(object);
        }
    }

    /**
     * 打印错误日志
     *
     * @param message
     * @param args
     */
    @Override
    public void e(String message, Object... args) {
        for (ILogger logger : mLoggers.values()) {
            logger.e(message, args);
        }
    }

    /**
     * 打印错误日志
     *
     * @param throwable 错误
     * @param args
     */
    @Override
    public void e(Throwable throwable, Object... args) {
        for (ILogger logger : mLoggers.values()) {
            logger.e(throwable, args);
        }
    }

    /**
     * 打印错误日志
     *
     * @param throwable 错误
     * @param message   string模版
     * @param args      模版参数
     */
    @Override
    public void e(Throwable throwable, String message, Object... args) {
        for (ILogger logger : mLoggers.values()) {
            logger.e(throwable, message, args);
        }
    }

    /**
     * 打印警告信息
     *
     * @param message
     * @param args
     */
    @Override
    public void w(String message, Object... args) {
        for (ILogger logger : mLoggers.values()) {
            logger.w(message, args);
        }
    }

    /**
     * 打印普通信息日志
     *
     * @param message
     * @param args
     */
    @Override
    public void i(String message, Object... args) {
        for (ILogger logger : mLoggers.values()) {
            logger.i(message, args);
        }
    }

    /**
     * 打印普通日志，和d类似
     *
     * @param message
     * @param args
     */
    @Override
    public void v(String message, Object... args) {
        for (ILogger logger : mLoggers.values()) {
            logger.v(message, args);
        }
    }

    /**
     * 打印严重bug的日志
     *
     * @param message
     * @param args
     */
    @Override
    public void wtf(String message, Object... args) {
        for (ILogger logger : mLoggers.values()) {
            logger.wtf(message, args);
        }
    }

    /**
     * 格式化json内容并打印
     *
     * @param json
     */
    @Override
    public void json(String json) {
        for (ILogger logger : mLoggers.values()) {
            logger.json(json);
        }
    }

    /**
     * 格式化xml内容并打印
     *
     * @param xml
     */
    @Override
    public void xml(String xml) {
        for (ILogger logger : mLoggers.values()) {
            logger.xml(xml);
        }
    }

    /**
     * 日志打印
     *
     * @param level
     * @param tag
     * @param message
     * @param throwable
     */
    @Override
    public void log(String level, String tag, String message, Throwable throwable) {
        for (ILogger logger : mLoggers.values()) {
            logger.log(level, tag, message, throwable);
        }
    }


}
