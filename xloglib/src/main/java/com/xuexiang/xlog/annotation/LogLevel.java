
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

package com.xuexiang.xlog.annotation;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.xuexiang.xlog.annotation.LogLevel.DEBUG;
import static com.xuexiang.xlog.annotation.LogLevel.ERROR;
import static com.xuexiang.xlog.annotation.LogLevel.INFO;
import static com.xuexiang.xlog.annotation.LogLevel.JSON;
import static com.xuexiang.xlog.annotation.LogLevel.VERBOSE;
import static com.xuexiang.xlog.annotation.LogLevel.WARN;
import static com.xuexiang.xlog.annotation.LogLevel.WTF;

/**
 * <pre>
 *     desc   : 日志级别
 *     author : xuexiang
 *     time   : 2018/5/13 下午10:41
 * </pre>
 */
@StringDef({VERBOSE, DEBUG, JSON, INFO, WARN, ERROR, WTF})
@Retention(RetentionPolicy.SOURCE)
public @interface LogLevel {
    String VERBOSE = "VERBOSE";
    String DEBUG = "DEBUG";
    String JSON = "JSON";
    String INFO = "INFO";
    String WARN = "WARN";
    String ERROR = "ERROR";
    String WTF = "WTF";

}
