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

package com.xuexiang.xlogdemo;

import android.app.Application;
import android.content.Context;

import com.xuexiang.xaop.XAOP;
import com.xuexiang.xlog.XLog;
import com.xuexiang.xlog.annotation.LogLevel;
import com.xuexiang.xlog.logger.LoggerFactory;
import com.xuexiang.xlog.strategy.log.DiskLogStrategy;
import com.xuexiang.xpage.AppPageConfig;
import com.xuexiang.xpage.PageConfig;
import com.xuexiang.xpage.PageConfiguration;
import com.xuexiang.xpage.model.PageInfo;
import com.xuexiang.xutil.XUtil;

import java.util.List;

/**
 * @author xuexiang
 * @date 2018/1/26 上午11:35
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initLibs();

        initLog();
    }

    private void initLog() {
        XLog.init(this);

//        Logger.newBuilder("PrettyLogger").build();

        //构建logcat打印
        LoggerFactory.getPrettyLogger("PrettyLogger");

//        ILogStrategy diskLogStrategy = DiskLogStrategy.newBuilder()       //日志打印策略
//                .setLogDir("xlogDemo")                                    //设置日志文件存储的根目录
//                .setLogPrefix("xlog")                                     //设置日志文件名的前缀
//                .setLogSegment(LogSegment.FOUR_HOURS)                     //设置日志记录的时间片间隔
//                .setLogLevels(LogLevel.ERROR, LogLevel.DEBUG)             //设置日志记录的等级
//                .build();

        DiskLogStrategy diskLogStrategy = LoggerFactory.getDiskLogStrategy(
                "xlogDemo", //日志存储的目录名（相对路径）
                "xlog",  //生成日志的前缀名
                LogLevel.ERROR, LogLevel.DEBUG //日志记录的等级
        );

//        IFormatStrategy formatStrategy = DiskFormatStrategy.newBuilder()  //日志格式策略
//                .setShowThreadInfo(false)                                 //设置是否显示线程信息
//                .setTimeFormat(TimeUtils.LOG_LINE_TIME)                   //设置日志记录时间的时间格式
//                .setMethodCount(1)                                        //设置打印显示的方法数
//                .setLogStrategy(diskLogStrategy)                          //设置日志打印策略
//                .build();
//        Logger.newBuilder("DiskLogger")
//                .setFormatStrategy(formatStrategy)                        //设置日志格式策略
//                .build();

        //构建磁盘打印
        LoggerFactory.getSimpleDiskLogger(
                "DiskLogger", //Log的标示名
                diskLogStrategy, //磁盘打印的策略
                0  //方法的偏移（默认是0，可根据自己的需要设定）
        );
    }

    private void initLibs() {
        XUtil.init(this);
        XUtil.debug(true);
        XAOP.init(this);
        //页面注册
        PageConfig.getInstance().setPageConfiguration(new PageConfiguration() {
            @Override
            public List<PageInfo> registerPages(Context context) {
                //自动注册页面
                return AppPageConfig.getInstance().getPages();
            }
        }).debug("PageLog").enableWatcher(false).init(this);
    }
}
