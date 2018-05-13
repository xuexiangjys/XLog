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

package com.xuexiang.xlog.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.xuexiang.xlog.XLog;
import com.xuexiang.xlog.annotation.LogLevel;
import com.xuexiang.xlog.annotation.LogSegment;
import com.xuexiang.xlog.logger.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.xuexiang.xlog.annotation.LogLevel.DEBUG;
import static com.xuexiang.xlog.annotation.LogLevel.ERROR;
import static com.xuexiang.xlog.annotation.LogLevel.INFO;
import static com.xuexiang.xlog.annotation.LogLevel.JSON;
import static com.xuexiang.xlog.annotation.LogLevel.VERBOSE;
import static com.xuexiang.xlog.annotation.LogLevel.WARN;
import static com.xuexiang.xlog.annotation.LogLevel.WTF;
import static com.xuexiang.xlog.utils.FileUtils.ZIP_EXT;

public final class Utils {
    public static final String LINE_BREAK = "\r\n";// 换行符
    public static final String SINGLE_DIVIDER = "------------------------------------------------------------------------------------";
    //    public static final String LINE_BREAK = System.lineSeparator();// 换行符
    public static final String CLASS_METHOD_LINE_FORMAT = "%s.%s()  Line:%d  (%s)\r\n\n";// 格式化日志信息

    private static final String TAG = "Utils";
    /**
     * The minimum stack trace index, starts at this class after two native calls.
     */
    private static final int MIN_STACK_OFFSET = 5;

    /**
     * logcat里日志的最大长度.
     */
    private static final int MAX_LOG_LENGTH = 4000;
    /**
     * 日志的扩展名.
     */
    private static final String LOG_EXT = ".log";
    /**
     * 读写文件的线程池，单线程模型.
     */
    private static final ExecutorService sExecutorService;

    static {
        sExecutorService = Executors.newSingleThreadExecutor();
    }

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 使用LogCat输出日志，字符长度超过4000则自动换行.
     *
     * @param level   级别
     * @param tag     标签
     * @param message 信息
     */
    public static void log(@LogLevel String level, @NonNull String tag, @NonNull String message) {
        int subNum = message.length() / MAX_LOG_LENGTH;
        if (subNum > 0) {
            int index = 0;
            for (int i = 0; i < subNum; i++) {
                int lastIndex = index + MAX_LOG_LENGTH;
                String sub = message.substring(index, lastIndex);
                logSub(level, tag, sub);
                index = lastIndex;
            }
            logSub(level, tag, message.substring(index, message.length()));
        } else {
            logSub(level, tag, message);
        }
    }

    /**
     * 获取TAG。
     *
     * @param element 堆栈元素
     * @return TAG
     */
    public static String getTag(@NonNull StackTraceElement element) {
        return getSimpleClassName(element.getClassName());
    }

    /**
     * 通过全限定类名来获取类名。
     *
     * @param className 全限定类名
     * @return 类名
     */
    public static String getSimpleClassName(@NonNull String className) {
        int lastIndex = className.lastIndexOf(".");
        int index = lastIndex + 1;
        if (lastIndex > 0 && index < className.length()) {
            return className.substring(index);
        }
        return className;
    }

    /**
     * 生成日志文件名.
     *
     * @param logPrefix  日志前缀
     * @param logSegment 日志切片
     * @param zoneOffset 时区偏移
     * @return 日志文件名
     */
    public static String getLogFileName(String logPrefix, @LogSegment int logSegment,
                                        @TimeUtils.ZoneOffset long zoneOffset) {
        logPrefix = TextUtils.isEmpty(logPrefix) ? "" : logPrefix + "_";
        String curDate = TimeUtils.getCurFormatDate(zoneOffset);
        String fileName;
        if (logSegment == LogSegment.TWENTY_FOUR_HOURS) {
            fileName = logPrefix + curDate + LOG_EXT;
        } else {
            fileName = logPrefix + curDate + "_" + getCurSegment(logSegment, zoneOffset) + LOG_EXT;
        }
        return fileName;
    }

    /**
     * 生成日志文件名.
     *
     * @param logPrefix  日志前缀
     * @param zoneOffset 时区偏移
     * @param fmt        时间格式
     * @return 日志文件名
     */
    public static String getLogFileName(String logPrefix, @TimeUtils.ZoneOffset long zoneOffset, @NonNull String fmt) {
        logPrefix = TextUtils.isEmpty(logPrefix) ? "" : logPrefix + "_";
        String curDate = TimeUtils.getCurTime(zoneOffset, fmt);
        return logPrefix + curDate + LOG_EXT;
    }

    /**
     * 根据切片时间获取当前的时间段.
     *
     * @param logSegment 日志切片
     * @param zoneOffset 时区偏移
     * @return 比如“0001”表示00:00-01:00
     */
    public static String getCurSegment(@LogSegment int logSegment,
                                       @TimeUtils.ZoneOffset long zoneOffset) {
        int hour = TimeUtils.getCurHour(zoneOffset);
        int start = hour - hour % logSegment;
        int end = start + logSegment;
        if (end == 24) {
            end = 0;
        }
        return getDoubleNum(start) + getDoubleNum(end);
    }

    /**
     * 获取日录下的所有日志文件.
     *
     * @param logDir 日志目录
     * @return 日志文件数组
     */
    public static File[] getLogFiles(@NonNull File logDir) {
        FilenameFilter logFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(LOG_EXT);
            }
        };
        return logDir.listFiles(logFilter);
    }

    /**
     * 压缩日志文件.
     *
     * @param file 日志文件，以".log"结尾
     */
    public static void zipLogs(@NonNull File file) throws IOException {
        String filePath = file.getAbsolutePath();
        int index = filePath.lastIndexOf('.');
        if (index == -1) {
            return;
        }
        String destPath = filePath.substring(0, index) + ZIP_EXT;
        FileUtils.zip(filePath, destPath, true);
    }

    /**
     * 根据当前日志的时间切片来过滤日志文件.
     *
     * @param logFiles   所有的日志文件
     * @param zoneOffset 时区
     * @param logPrefix  前缀
     * @param logSegment 日志切片
     * @return 当前时间切片之前的日志
     */
    public static File[] filterLogFiles(@NonNull File[] logFiles,
                                        @TimeUtils.ZoneOffset long zoneOffset,
                                        String logPrefix, @LogSegment int logSegment) {
        logPrefix = TextUtils.isEmpty(logPrefix) ? "" : logPrefix + "_";
        String curDate = TimeUtils.getCurFormatDate(zoneOffset);
        String referFileName;
        if (logSegment == LogSegment.TWENTY_FOUR_HOURS) {
            referFileName = logPrefix + curDate + LOG_EXT;
        } else {
            referFileName = logPrefix + curDate + "_" + Utils.getCurSegment(logSegment,
                    zoneOffset) + LOG_EXT;
        }

        List<File> files = new ArrayList<>();
        for (File logFile : logFiles) {
            if (logFile.getName().compareTo(referFileName) < 0) {
                files.add(logFile);
            }
        }
        return files.toArray(new File[files.size()]);
    }

    /**
     * 对于1-9的数值进行前置补0.
     *
     * @param num 数值
     * @return num在[0, 9]时前置补0，否则返回原值
     */
    private static String getDoubleNum(int num) {
        return num < 10 ? "0" + num : String.valueOf(num);
    }

    /**
     * 使用LogCat输出日志.
     *
     * @param level 级别
     * @param tag   标签
     * @param sub   信息
     */
    private static void logSub(@LogLevel String level, @NonNull String tag, @NonNull String sub) {
        switch (level) {
            case VERBOSE:
                Log.v(tag, sub);
                break;
            case DEBUG:
                Log.d(tag, sub);
                break;
            case JSON:
                Log.d(tag, sub);
                break;
            case INFO:
                Log.i(tag, sub);
                break;
            case WARN:
                Log.w(tag, sub);
                break;
            case ERROR:
                Log.e(tag, sub);
                break;
            case WTF:
                Log.wtf(tag, sub);
                break;
            default:
                break;
        }
    }

    /**
     * 把文本写入文件中.
     *
     * @param context  Context
     * @param dirPath  目录路径
     * @param fileName 文件名
     * @param content  待写内容
     */
    public static void write(@NonNull final Context context, @NonNull final String dirPath,
                             @NonNull final String fileName,
                             @NonNull final String content) {
        sExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                BufferedWriter bw = null;
                try {
                    if (FileUtils.createDir(dirPath)) {
                        String outContent = content;
                        File file = new File(dirPath, fileName);
                        if (!FileUtils.isExist(file)) {
                            outContent = getDevicesInfo(context) + outContent;
                        }
                        bw = new BufferedWriter(new FileWriter(file, true));
                        bw.write(outContent);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "写日志异常", e);
                } finally {
                    CloseUtils.closeQuietly(bw);
                }
            }
        });
    }

    /**
     * 生成系统相关的信息.
     *
     * @param context
     * @return
     */
    public static String getDevicesInfo(@NonNull Context context) {
        Map<String, String> logInfo = new TreeMap<>();
        try {
            // 获得包管理器
            PackageManager packageManager = context.getPackageManager();
            // 得到该应用的信息，即主Activity
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                logInfo.put("packageName", packageInfo.packageName);
                logInfo.put("versionName", packageInfo.versionName);
                logInfo.put("versionCode", String.valueOf(packageInfo.versionCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 反射机制
        Field[] fields = Build.class.getDeclaredFields();
        // 迭代Build的字段key-value 此处的信息主要是为了在服务器端手机各种版本手机报错的原因
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                logInfo.put(field.getName(), toString(field.get("")));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : logInfo.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + LINE_BREAK);
        }
        sb.append(LINE_BREAK).append(LINE_BREAK).append(SINGLE_DIVIDER).append(LINE_BREAK);
        return sb.toString();
    }


    /**
     * 获取异常栈信息，不同于Log.getStackTraceString()，该方法不会过滤掉UnknownHostException.
     *
     * @param t {@link Throwable}
     * @return 异常栈里的信息
     */
    public static String getStackTraceString(@NonNull Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    public static String toString(Object object) {
        if (object == null) {
            return "null";
        }
        if (!object.getClass().isArray()) {
            return object.toString();
        }
        if (object instanceof boolean[]) {
            return Arrays.toString((boolean[]) object);
        }
        if (object instanceof byte[]) {
            return Arrays.toString((byte[]) object);
        }
        if (object instanceof char[]) {
            return Arrays.toString((char[]) object);
        }
        if (object instanceof short[]) {
            return Arrays.toString((short[]) object);
        }
        if (object instanceof int[]) {
            return Arrays.toString((int[]) object);
        }
        if (object instanceof long[]) {
            return Arrays.toString((long[]) object);
        }
        if (object instanceof float[]) {
            return Arrays.toString((float[]) object);
        }
        if (object instanceof double[]) {
            return Arrays.toString((double[]) object);
        }
        if (object instanceof Object[]) {
            return Arrays.deepToString((Object[]) object);
        }
        return "Couldn't find a correct type for the object";
    }


    /**
     * Determines the starting index of the stack trace, after method calls made by this class.
     *
     * @param trace the stack trace
     * @return the stack offset
     */
    public static int getStackOffset(StackTraceElement[] trace) {
        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(XLog.class.getName()) && !name.equals(Logger.class.getName())) {
                return --i;
            }
        }
        return -1;
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * Returns true if a and b are equal, including if they are both null.
     * <p><i>Note: In platform versions 1.1 and earlier, this method only worked well if
     * both the arguments were instances of String.</i></p>
     *
     * @param a first CharSequence to check
     * @param b second CharSequence to check
     * @return true if a and b are equal
     * <p>
     * NOTE: Logic slightly change due to strict policy on CI -
     * "Inner assignments should be avoided"
     */
    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        if (a != null && b != null) {
            int length = a.length();
            if (length == b.length()) {
                if (a instanceof String && b instanceof String) {
                    return a.equals(b);
                } else {
                    for (int i = 0; i < length; i++) {
                        if (a.charAt(i) != b.charAt(i)) return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }
}