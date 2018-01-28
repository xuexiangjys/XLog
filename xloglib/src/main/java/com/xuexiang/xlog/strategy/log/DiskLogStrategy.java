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

package com.xuexiang.xlog.strategy.log;


import com.xuexiang.xlog.XLog;
import com.xuexiang.xlog.annotation.LogLevel;
import com.xuexiang.xlog.annotation.LogSegment;
import com.xuexiang.xlog.utils.PrinterUtils;
import com.xuexiang.xlog.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 磁盘打印日志策略
 * @author xuexiang
 * @date 2018/1/16 上午12:23
 */
public class DiskLogStrategy implements ILogStrategy {
    /**
     * 日志保存的目录.
     */
    private String mLogDir;
    /**
     * 日志文件的前缀.
     */
    private String mLogPrefix;
    /**
     * 切片间隔，单位小时.
     */
    @LogSegment
    private int mLogSegment;
    /**
     * 时区偏移时间.
     */
    @TimeUtils.ZoneOffset
    private long mZoneOffset;
    /**
     * 写入文件的日志级别.
     */
    private List<String> mLogLevels;

    public DiskLogStrategy(Builder builder) {
        mLogDir = builder.logDir;
        mLogPrefix = builder.logPrefix;
        mLogSegment = builder.logSegment;
        mZoneOffset = builder.zoneOffset;
        mLogLevels = builder.logLevels;
    }

    /**
     * 打印日志
     *
     * @param level
     * @param tag
     * @param message
     */
    @Override
    public void log(@LogLevel String level, String tag, String message) {
        if (mLogLevels.contains(level)) {
            synchronized (DiskLogStrategy.class) {
                PrinterUtils.printFile(XLog.getContext(), mLogDir, mLogPrefix, mLogSegment, mZoneOffset, message);
            }
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        /**
         * 日志保存的目录.
         */
        String logDir;
        /**
         * 日志文件的前缀.
         */
        String logPrefix;
        /**
         * 切片间隔，单位小时.
         */
        @LogSegment
        int logSegment;
        /**
         * 时区偏移时间.
         */
        @TimeUtils.ZoneOffset
        long zoneOffset;

        /**
         * 写入文件的日志级别.
         */
        List<String> logLevels;

        private Builder() {
            logDir = "xlog";
            logPrefix = "";
            logSegment = LogSegment.TWENTY_FOUR_HOURS;
            zoneOffset = TimeUtils.ZoneOffset.P0800;
            logLevels = new ArrayList<>();
            logLevels.add(LogLevel.ERROR);
            logLevels.add(LogLevel.WTF);
        }

        public String getLogDir() {
            return logDir;
        }

        public Builder setLogDir(String logDir) {
            this.logDir = logDir;
            return this;
        }

        public String getLogPrefix() {
            return logPrefix;
        }

        public Builder setLogPrefix(String logPrefix) {
            this.logPrefix = logPrefix;
            return this;
        }

        public int getLogSegment() {
            return logSegment;
        }

        public Builder setLogSegment(int logSegment) {
            this.logSegment = logSegment;
            return this;
        }

        public long getZoneOffset() {
            return zoneOffset;
        }

        public Builder setZoneOffset(long zoneOffset) {
            this.zoneOffset = zoneOffset;
            return this;
        }

        public List<String> getLogLevels() {
            return logLevels;
        }

        public Builder setLogLevels(List<String> logLevels) {
            this.logLevels = logLevels;
            return this;
        }

        public Builder setLogLevels(@LogLevel String... logLevels) {
            setLogLevels(Arrays.asList(logLevels));
            return this;
        }

        public DiskLogStrategy build() {
            return new DiskLogStrategy(this);
        }
    }
}
