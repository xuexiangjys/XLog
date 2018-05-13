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
 */

package com.xuexiang.xlog.logger;

import android.util.Log;

import com.xuexiang.xlog.annotation.LogLevel;
import com.xuexiang.xlog.annotation.LogSegment;
import com.xuexiang.xlog.strategy.format.DiskFormatStrategy;
import com.xuexiang.xlog.strategy.format.IFormatStrategy;
import com.xuexiang.xlog.strategy.format.PrettyFormatStrategy;
import com.xuexiang.xlog.strategy.log.DiskLogStrategy;
import com.xuexiang.xlog.strategy.log.ILogStrategy;

/**
 * <pre>
 *     desc   : Logger静态生产工厂
 *     author : xuexiang
 *     time   : 2018/5/13 下午11:35
 * </pre>
 */
public final class LoggerFactory {

    private LoggerFactory() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 获取自定义拼装的logger
     *
     * @param loggerName     logger的名称
     * @param formatStrategy 日志格式化策略
     * @return 自定义拼装的logger
     */
    public static Logger getLogger(String loggerName, IFormatStrategy formatStrategy) {
        return Logger.newBuilder(loggerName)
                .setFormatStrategy(formatStrategy)
                .build();
    }

    //=========================================================//

    /**
     * 获取漂亮的logger【打印方式是logcat】【默认方式】
     *
     * @param loggerName logger的名称
     * @return 漂亮的logger
     */
    public static Logger getPrettyLogger(String loggerName) {
        return Logger.newBuilder(loggerName).build();
    }

    /**
     * 获取漂亮的logger【打印方式是logcat】【默认方式】
     *
     * @param loggerName   logger的名称
     * @param methodOffset 方法的偏移数
     * @return 漂亮的logger
     */
    public static Logger getPrettyLogger(String loggerName, int methodOffset) {
        return Logger.newBuilder(loggerName)
                .setFormatStrategy(PrettyFormatStrategy.newBuilder().methodOffset(methodOffset).build())
                .build();
    }

    /**
     * 获取漂亮的logger【打印方式是logcat】【默认方式】
     *
     * @param loggerName   logger的名称
     * @param tag          日志标签
     * @param methodOffset 方法的偏移
     * @return 漂亮的logger
     */
    public static Logger getPrettyLogger(String loggerName, String tag, int methodOffset) {
        return Logger.newBuilder(loggerName)
                .setFormatStrategy(getPrettyFormatStrategy(tag, methodOffset))
                .build();
    }

    /**
     * 获取漂亮的日志打印格式
     *
     * @param tag            日志标签 【默认为 pretty】
     * @param methodCount    需要显示的方法数 【默认为 2】
     * @param methodOffset   方法的偏移 【默认为 0】
     * @param showThreadInfo 是否显示线程信息【默认是 {@code true}】
     * @return 漂亮的日志打印格式
     */
    public static PrettyFormatStrategy getPrettyFormatStrategy(String tag, int methodCount, int methodOffset, boolean showThreadInfo) {
        return PrettyFormatStrategy.newBuilder()
                .tag(tag)
                .methodCount(methodCount)
                .methodOffset(methodOffset)
                .showThreadInfo(showThreadInfo)
                .build();
    }

    /**
     * 获取漂亮的日志打印格式
     *
     * @param tag          日志标签
     * @param methodOffset 方法的偏移
     * @return 漂亮的日志打印格式
     */
    public static PrettyFormatStrategy getPrettyFormatStrategy(String tag, int methodOffset) {
        return PrettyFormatStrategy.newBuilder()
                .tag(tag)
                .methodOffset(methodOffset)
                .build();
    }

    //==================================磁盘打印========================================//

    /**
     * 获取磁盘打印的打印策略
     *
     * @param logDir     日志文件存储的根目录 【默认为"xlog"】
     * @param logPrefix  日志文件名的前缀 【默认为""】
     * @param logSegment 日志记录的时间片间隔 【默认为{@link LogSegment#TWENTY_FOUR_HOURS}】
     * @param logLevels  日志记录的等级 【默认为{@link LogLevel#ERROR} 和 {@link LogLevel#WTF}】
     * @return 磁盘打印的打印策略
     */
    public static DiskLogStrategy getDiskLogStrategy(String logDir, String logPrefix, @LogSegment int logSegment, @LogLevel String... logLevels) {
        return DiskLogStrategy.newBuilder()
                .setLogDir(logDir)
                .setLogPrefix(logPrefix)
                .setLogSegment(logSegment)
                .setLogLevels(logLevels)
                .build();
    }

    /**
     * 获取磁盘打印的打印策略
     *
     * @param logDir    日志文件存储的根目录
     * @param logPrefix 日志文件名的前缀
     * @param logLevels 日志记录的等级
     * @return 磁盘打印的打印策略
     */
    public static DiskLogStrategy getDiskLogStrategy(String logDir, String logPrefix, @LogLevel String... logLevels) {
        return DiskLogStrategy.newBuilder()
                .setLogDir(logDir)
                .setLogPrefix(logPrefix)
                .setLogLevels(logLevels)
                .build();
    }

    /**
     * 获取磁盘打印的格式策略
     *
     * @param logStrategy    打印的策略【默认是 {@link DiskLogStrategy}】
     * @param methodOffset   方法的偏移数 【默认是 0】
     * @param methodCount    日志显示的方法数 【默认是 1】
     * @param showThreadInfo 是否显示线程信息 【默认是 {@code false}】
     * @param timeFormat     日志打印的时间格式 【默认是"yyyy-MM-dd HH:mm:ss.SSS"】
     * @return 磁盘打印的格式策略
     */
    public static DiskFormatStrategy getDiskFormatStrategy(ILogStrategy logStrategy, int methodOffset, int methodCount, boolean showThreadInfo, String timeFormat) {
        return DiskFormatStrategy.newBuilder()
                .setMethodOffset(methodOffset)
                .setMethodCount(methodCount)
                .setShowThreadInfo(showThreadInfo)
                .setTimeFormat(timeFormat)
                .setLogStrategy(logStrategy)
                .build();

    }

    /**
     * 获取简化的磁盘打印的格式策略
     *
     * @param logStrategy  磁盘打印的打印策略
     * @param methodOffset 方法的偏移数
     * @return 简化的磁盘打印的格式策略
     */
    public static DiskFormatStrategy getSimpleDiskFormatStrategy(DiskLogStrategy logStrategy, int methodOffset) {
        return DiskFormatStrategy.newBuilder()
                .setMethodOffset(methodOffset)
                .setLogStrategy(logStrategy)
                .build();

    }

    /**
     * 获取简化的磁盘打印的格式策略
     *
     * @param logStrategy    磁盘打印的打印策略
     * @param methodOffset   方法的偏移数
     * @param showThreadInfo 是否显示线程信息
     * @return 简化的磁盘打印的格式策略
     */
    public static DiskFormatStrategy getSimpleDiskFormatStrategy(DiskLogStrategy logStrategy, int methodOffset, boolean showThreadInfo) {
        return DiskFormatStrategy.newBuilder()
                .setMethodOffset(methodOffset)
                .setShowThreadInfo(showThreadInfo)
                .setLogStrategy(logStrategy)
                .build();

    }

    /**
     * 获取简化的磁盘打印的logger
     *
     * @param loggerName   logger的名称
     * @param logStrategy  磁盘打印的打印策略
     * @param methodOffset 方法的偏移数
     * @return 简化的磁盘打印的logger
     */
    public static Logger getSimpleDiskLogger(String loggerName, DiskLogStrategy logStrategy, int methodOffset) {
        return Logger.newBuilder(loggerName)
                .setFormatStrategy(getSimpleDiskFormatStrategy(logStrategy, methodOffset))
                .build();
    }

    /**
     * 获取简化的磁盘打印的logger
     *
     * @param loggerName     logger的名称
     * @param logStrategy    磁盘打印的打印策略
     * @param methodOffset   方法的偏移数
     * @param showThreadInfo 是否显示线程信息
     * @return 简化的磁盘打印的logger
     */
    public static Logger getSimpleDiskLogger(String loggerName, DiskLogStrategy logStrategy, int methodOffset, boolean showThreadInfo) {
        return Logger.newBuilder(loggerName)
                .setFormatStrategy(getSimpleDiskFormatStrategy(logStrategy, methodOffset, showThreadInfo))
                .build();
    }

    //=================日志等级和日志优先级的转化=========================//

    /**
     * 日志优先级 -> 日志等级
     *
     * @param priority 日志优先级
     * @return
     */
    public static String logPriority2LogLevel(int priority) {
        switch (priority) {
            case Log.VERBOSE:
                return LogLevel.VERBOSE;
            case Log.DEBUG:
                return LogLevel.DEBUG;
            case Log.INFO:
                return LogLevel.INFO;
            case Log.WARN:
                return LogLevel.WARN;
            case Log.ERROR:
                return LogLevel.ERROR;
            case Log.ASSERT:
                return LogLevel.WTF;
            default:
                return LogLevel.VERBOSE;
        }
    }

    /**
     * 日志等级 -> 日志优先级
     *
     * @param logLevel 日志等级
     * @return
     */
    public static int logLevel2LogPriority(@LogLevel String logLevel) {
        switch (logLevel) {
            case LogLevel.VERBOSE:
                return Log.VERBOSE;
            case LogLevel.DEBUG:
            case LogLevel.JSON:
                return Log.DEBUG;
            case LogLevel.INFO:
                return Log.INFO;
            case LogLevel.WARN:
                return Log.WARN;
            case LogLevel.ERROR:
                return Log.ERROR;
            case LogLevel.WTF:
                return Log.ASSERT;
            default:
                return Log.VERBOSE;
        }
    }
}
