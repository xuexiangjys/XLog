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

import com.xuexiang.xlog.crash.CrashHandler;
import com.xuexiang.xlog.crash.SendEmailCrashListener;
import com.xuexiang.xlog.crash.ToastCrashListener;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.SimpleListFragment;

import java.util.List;

/**
 * <pre>
 *     desc   :
 *     author : xuexiang
 *     time   : 2018/5/13 下午11:22
 * </pre>
 */
@Page(name = "程序崩溃处理")
public class CrashFragment extends SimpleListFragment {
    /**
     * 初始化例子
     *
     * @param lists
     * @return
     */
    @Override
    protected List<String> initSimpleData(List<String> lists) {
        lists.add("崩溃处理：简单的toast提示 + 程序自动启动。");
        lists.add("崩溃处理：发送崩溃日志邮件。");
        return lists;
    }

    /**
     * 条目点击
     *
     * @param position
     */
    @Override
    protected void onItemClick(int position) {
        switch(position) {
            case 0:
                CrashHandler.getInstance().setOnCrashListener(new ToastCrashListener());
                break;
            case 1:
                CrashHandler.getInstance().setOnCrashListener(new SendEmailCrashListener());
                break;
            default:
                break;
        }
        crash();
    }


    private void crash() {
        throw new NullPointerException("崩溃啦！");
    }
}
