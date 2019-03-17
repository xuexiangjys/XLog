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

package com.xuexiang.xlogdemo.crash;

import android.content.Context;
import android.widget.Toast;

import com.xuexiang.xlog.crash.ICrashHandler;
import com.xuexiang.xlog.crash.OnCrashListener;

/**
 * 简单提示toast的崩溃处理
 *
 * @author xuexiang
 * @since 2019/3/16 下午9:12
 */
public class ToastCrashListener implements OnCrashListener {
    /**
     * 发生崩溃
     *
     * @param context
     * @param crashHandler
     * @param throwable
     */
    @Override
    public void onCrash(final Context context, final ICrashHandler crashHandler, Throwable throwable) {
        Toast.makeText(context, "程序无响应，正在恢复...", Toast.LENGTH_LONG).show();
        crashHandler.setIsHandledCrash(true);
    }
}
