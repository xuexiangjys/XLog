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

package com.xuexiang.xlogdemo.fragment;

import android.os.Environment;

import com.google.gson.Gson;
import com.xuexiang.xaop.annotation.Permission;
import com.xuexiang.xaop.consts.PermissionConsts;
import com.xuexiang.xlog.XLog;
import com.xuexiang.xlog.annotation.LogLevel;
import com.xuexiang.xlog.logger.LoggerFactory;
import com.xuexiang.xlogdemo.entity.UserInfo;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageSimpleListFragment;
import com.xuexiang.xutil.common.logger.ILogger;
import com.xuexiang.xutil.common.logger.Logger;
import com.xuexiang.xutil.resource.ResourceUtils;

import java.util.List;

/**
 * <pre>
 *     desc   :
 *     author : xuexiang
 *     time   : 2018/5/13 下午11:22
 * </pre>
 */
@Page(name = "日志记录")
public class LogFragment extends XPageSimpleListFragment {
    private final static String LOG_PATH = Environment.getExternalStorageDirectory() + "/xlog/logs/debug_logs";

    String json;

    @Override
    protected void initArgs() {
        super.initArgs();

        UserInfo userInfo = new UserInfo()
                .setLoginName("xuexiang")
                .setPassword("12345678");
        json = new Gson().toJson(userInfo);

        //为适配第三方日志打印接口创建的日志打印
        LoggerFactory.getPrettyLogger("LogUtils", 2);

        //适配第三方日志打印接口
        Logger.setLogger(new ILogger() {
            @Override
            public void log(int priority, String tag, String message, Throwable t) {
                XLog.get().getLogger("LogUtils").log(LoggerFactory.logPriority2LogLevel(priority), tag, message, t);
            }
        });
    }

    /**
     * 初始化例子
     *
     * @param lists
     * @return
     */
    @Override
    protected List<String> initSimpleData(List<String> lists) {
        lists.add("打印普通DEBUG日志");
        lists.add("打印JSON数据");
        lists.add("打印XML数据");
        lists.add("打印错误信息");
        lists.add("适配第三方打印日志接口");
        lists.add("设置日志输出根目录为绝对路径：" + LOG_PATH);
        return lists;
    }

    /**
     * 条目点击
     *
     * @param position
     */
    @Override
    protected void onItemClick(int position) {
        switch (position) {
            case 0: //打印普通DEBUG日志
                XLog.get().d(json);
                break;
            case 1: //打印JSON数据
                XLog.get().json(json);
                break;
            case 2: //打印XML数据
                XLog.get().xml(ResourceUtils.readStringFromAssert("AndroidManifest.xml"));
                break;
            case 3: //打印错误信息
                try {
                    throw new NullPointerException("出错啦！");
                } catch (Exception e) {
                    e.printStackTrace();
                    XLog.get().e(e);
                }
                break;
            case 4: //适配第三方打印日志接口
                Logger.i("适配第三方打印日志接口");
                Logger.iTag("xuexiang", "适配第三方打印日志接口");

                Logger.d(json);
                try {
                    throw new NullPointerException("出错啦！");
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e(e);
                }
                break;
            case 5:
                setDebugLogAbsolutePath();
                break;
            default:
                break;
        }
    }

    @Permission(PermissionConsts.STORAGE)
    public void setDebugLogAbsolutePath() {
        LoggerFactory.getDiskLogger("DEBUG_LOGGER", LOG_PATH, true, "debug_log_", 0, LogLevel.DEBUG);
    }
}
