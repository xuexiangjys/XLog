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

package com.xuexiang.xlog.strategy.format;

import com.xuexiang.xlog.annotation.LogLevel;

/**
 * 日志格式策略实现接口
 * @author xuexiang
 * @date 2018/1/15 下午11:15
 */
public interface IFormatStrategy {

    /**
     * 格式化日志内容
     * @param level
     * @param tag
     * @param message
     */
    void format(@LogLevel String level, String tag, String message);
}
