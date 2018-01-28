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

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.xuexiang.xlog.annotation.LogSegment.FOUR_HOURS;
import static com.xuexiang.xlog.annotation.LogSegment.ONE_HOUR;
import static com.xuexiang.xlog.annotation.LogSegment.SIX_HOURS;
import static com.xuexiang.xlog.annotation.LogSegment.THREE_HOURS;
import static com.xuexiang.xlog.annotation.LogSegment.TWELVE_HOURS;
import static com.xuexiang.xlog.annotation.LogSegment.TWENTY_FOUR_HOURS;
import static com.xuexiang.xlog.annotation.LogSegment.TWO_HOURS;

/**
 * 日志时间切片，用于文件日志记录
 * @author xuexiang
 * @date 2018/1/15 下午10:20
 */
@IntDef({ONE_HOUR, TWO_HOURS, THREE_HOURS, FOUR_HOURS, SIX_HOURS, TWELVE_HOURS, TWENTY_FOUR_HOURS})
@Retention(RetentionPolicy.SOURCE)
public @interface LogSegment {
    int ONE_HOUR = 1;
    int TWO_HOURS = 2;
    int THREE_HOURS = 3;
    int FOUR_HOURS = 4;
    int SIX_HOURS = 6;
    int TWELVE_HOURS = 12;
    int TWENTY_FOUR_HOURS = 24;
}
