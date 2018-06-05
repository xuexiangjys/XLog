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
package com.xuexiang.xlog.crash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xuexiang.xlog.utils.FileUtils;
import com.xuexiang.xlog.utils.PrinterUtils;
import com.xuexiang.xlog.utils.TimeUtils;
import com.xuexiang.xlog.utils.Utils;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * <pre>
 *     desc   : 在Application中统一捕获异常，保存到文件中下次再打开时上传
 *     author : xuexiang
 *     time   : 2018/5/13 下午10:33
 * </pre>
 */
public class CrashHandler implements UncaughtExceptionHandler, ICrashHandler {
    private final static String DEFAULT_CRASH_LOG_FLAG = "crash_log";
    private final static String DEFAULT_CRASH_LOG_TIME_FORMAT = "yyyy-MM-dd__HH-mm-ss";
    /**
     * CrashHandler实例
     */
    private static CrashHandler sInstance;

    private Context mContext;
    /**
     * 系统默认的UncaughtException处理类
     */
    private UncaughtExceptionHandler mDefaultHandler;

    /**
     * 崩溃监听
     */
    private OnCrashListener mOnCrashListener;

    private OnAppExitListener mOnAppExitListener;

    /**
     * 崩溃是否处理完毕
     */
    private volatile boolean mIsHandledCrash;

    /**
     * 崩溃是否需要重启程序
     */
    private boolean mIsNeedReopen;

    /**
     * 崩溃日志保存的目录.
     */
    private String mCrashLogDir;

    /**
     * mCrashLogDir设置的是否是绝对路径【默认是false：相对路径】
     */
    private boolean mAbsolutePath;

    /**
     * 崩溃日志保存的文件前缀.
     */
    private String mCrashLogPrefix;

    /**
     * 时区偏移时间.
     */
    @TimeUtils.ZoneOffset
    private long mZoneOffset;

    /**
     * 日志的时间格式.默认格式【yyyy-MM-dd__HH-mm-ss】
     */
    private String mTimeFormat;

    /**
     * 崩溃日志的文件
     */
    private File mCrashLogFile;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
        mIsHandledCrash = false;
        mIsNeedReopen = true;
        mOnCrashListener = new ToastCrashListener();

        mCrashLogDir = DEFAULT_CRASH_LOG_FLAG;
        mAbsolutePath = false;
        mCrashLogPrefix = DEFAULT_CRASH_LOG_FLAG;

        mZoneOffset = TimeUtils.ZoneOffset.P0800;
        mTimeFormat = DEFAULT_CRASH_LOG_TIME_FORMAT;
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (sInstance == null) {
            synchronized (CrashHandler.class) {
                if (sInstance == null) {
                    sInstance = new CrashHandler();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     *
     * @param context 上下文
     */
    public CrashHandler init(Context context) {
        mContext = context.getApplicationContext();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        return this;
    }

    /**
     * 设置崩溃监听
     *
     * @param listener
     * @return
     */
    public CrashHandler setOnCrashListener(OnCrashListener listener) {
        mOnCrashListener = listener;
        return this;
    }

    /**
     * 设置应用程序退出监听
     *
     * @param onAppExitListener
     * @return
     */
    public CrashHandler setOnAppExitListener(OnAppExitListener onAppExitListener) {
        mOnAppExitListener = onAppExitListener;
        return this;
    }

    @Override
    public CrashHandler setIsHandledCrash(boolean isHandled) {
        mIsHandledCrash = isHandled;
        return this;
    }

    /**
     * 是否需要重启
     *
     * @param isNeedReopen
     * @return
     */
    @Override
    public CrashHandler setIsNeedReopen(boolean isNeedReopen) {
        mIsNeedReopen = isNeedReopen;
        return this;
    }

    /**
     * 设置崩溃日志保存的目录.
     *
     * @param crashLogDir 崩溃日志保存的目录
     * @return
     */
    public CrashHandler setCrashLogDir(String crashLogDir) {
        mCrashLogDir = crashLogDir;
        return this;
    }

    /**
     * 设置崩溃日志的目录是否是绝对路径.
     *
     * @param absolutePath 崩溃日志的目录是否是绝对路径
     * @return
     */
    public CrashHandler setAbsolutePath(boolean absolutePath) {
        mAbsolutePath = absolutePath;
        return this;
    }

    /**
     * 设置崩溃日志保存的文件前缀.
     *
     * @param crashLogPrefix 崩溃日志保存的文件前缀
     * @return
     */
    public CrashHandler setCrashLogPrefix(String crashLogPrefix) {
        mCrashLogPrefix = crashLogPrefix;
        return this;
    }

    /**
     * 设置时区偏移时间.
     *
     * @param zoneOffset 时区偏移时间
     * @return
     */
    public CrashHandler setZoneOffset(long zoneOffset) {
        mZoneOffset = zoneOffset;
        return this;
    }

    /**
     * 设置日志的时间格式.
     *
     * @param timeFormat 日志的时间格式，默认格式【yyyy-MM-dd__HH-mm-ss】
     * @return
     */
    public CrashHandler setTimeFormat(String timeFormat) {
        mTimeFormat = timeFormat;
        return this;
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else { // 如果自己处理了异常，则不会弹出错误对话框，则需要手动退出app
            while (!mIsHandledCrash) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (mOnAppExitListener == null) {
                if (mIsNeedReopen) {
                    Intent intent = new Intent(mContext, StartAppReceiver.class);
                    PendingIntent restartIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    //退出程序
                    AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
                }

                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            } else {
                mOnAppExitListener.onAppExit();
            }

        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param throwable 出错信息
     * @return {@code true}:处理了该异常信息;<br>{@code false}:该异常信息未处理。
     */
    private boolean handleException(Throwable throwable) {
        if (throwable == null || mContext == null || mOnCrashListener == null) {
            return false;
        }
        setIsHandledCrash(false);
        mCrashLogFile = saveCrashInfo(throwable); //保存崩溃信息到本地文件
        mOnCrashListener.onCrash(mContext, this, throwable);
        return true;
    }


    /**
     * 保存崩溃信息
     *
     * @param throwable
     * @return 崩溃日志文件
     */
    private File saveCrashInfo(Throwable throwable) {
        return PrinterUtils.printFile(getCrashLogDirPath(), mCrashLogPrefix, mZoneOffset, mTimeFormat, getCrashLogInfo(throwable));
    }

    /**
     * 获取崩溃报告
     *
     * @param throwable
     * @return
     */
    @Override
    public String getCrashReport(Throwable throwable) {
        return Utils.getDeviceInfos(mContext) + getCrashLogInfo(throwable);
    }

    @Override
    public File getCrashLogFile() {
        return mCrashLogFile;
    }

    /**
     * 获取崩溃的信息
     *
     * @param throwable
     * @return
     */
    private String getCrashLogInfo(Throwable throwable) {
        return Log.getStackTraceString(throwable);
    }

    /**
     * 获得崩溃日志的根目录路径
     *
     * @return
     */
    public String getCrashLogDirPath() {
        return mAbsolutePath ? mCrashLogDir : FileUtils.getDiskCacheDir(mContext, mCrashLogDir);
    }

    public String getCrashLogPrefix() {
        return mCrashLogPrefix;
    }

    /**
     * 应用即将退出的监听
     */
    public interface OnAppExitListener {
        /**
         * 应用即将退出
         */
        void onAppExit();
    }
}
