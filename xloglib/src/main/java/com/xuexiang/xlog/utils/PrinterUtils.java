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
import android.support.annotation.NonNull;

import com.xuexiang.xlog.annotation.LogLevel;
import com.xuexiang.xlog.annotation.LogSegment;

import java.io.File;


/**
 * <pre>
 *     desc   : 打印相关
 *     author : xuexiang
 *     time   : 2018/5/13 下午10:08
 * </pre>
 */
public final class PrinterUtils {

    private PrinterUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 日志打印输出到控制台.
     *
     * @param level   级别
     * @param tag     标签
     * @param message 信息
     */
    public static void printConsole(@LogLevel String level, @NonNull String tag,
                                    @NonNull String message) {
        Utils.log(level, tag, message);
    }


    /**
     * 日志打印输出到文件.
     *
     * @param context
     * @param logDir     日志文件根目录
     * @param logPrefix  日志文件前缀
     * @param logSegment 日志时间片
     * @param zoneOffset 时区
     * @param message    信息
     */
    public static void printFile(@NonNull Context context, @NonNull String logDir, String logPrefix,
                                 @LogSegment int logSegment, @TimeUtils.ZoneOffset long zoneOffset,
                                 @NonNull String message) {
        String dirPath = FileUtils.getDiskCacheDir(context, logDir);
        String fileName = Utils.getLogFileName(logPrefix, logSegment, zoneOffset);
        Utils.write(context, dirPath, fileName, message);
    }

    /**
     * 日志打印输出到文件.
     *
     * @param context
     * @param logDir     日志文件根目录
     * @param logPrefix  日志文件前缀
     * @param zoneOffset 时区
     * @param fmt        时间格式
     * @param message    信息
     */
    public static File printFile(@NonNull Context context, @NonNull String logDir, String logPrefix,
                                 @TimeUtils.ZoneOffset long zoneOffset, @NonNull String fmt,
                                 @NonNull String message) {
        String dirPath = FileUtils.getDiskCacheDir(context, logDir);
        String fileName = Utils.getLogFileName(logPrefix, zoneOffset, fmt);
        Utils.write(context, dirPath, fileName, message);
        return new File(dirPath, fileName);
    }

}