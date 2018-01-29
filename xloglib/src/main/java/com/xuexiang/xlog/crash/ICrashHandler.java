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

import java.io.File;

/**
 * 崩溃处理者
 * @author xuexiang
 * @date 2018/1/28 下午11:01
 */
public interface ICrashHandler {

    /**
     * 是否处理完成崩溃
     * @param isHandled
     * @return
     */
    ICrashHandler setIsHandledCrash(boolean isHandled);

    /**
     * 是否需要重启程序
     * @param isNeedReopen
     * @return
     */
    ICrashHandler setIsNeedReopen(boolean isNeedReopen);

    /**
     * 获取崩溃报告
     * @param throwable
     * @return
     */
    String getCrashReport(Throwable throwable);


    /**
     * 获取崩溃日志文件
     * @return
     */
    File getCrashLogFile();
}
