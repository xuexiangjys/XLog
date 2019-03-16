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

package com.xuexiang.xlog.logger;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.xuexiang.xlog.XLog;
import com.xuexiang.xlog.annotation.LogLevel;
import com.xuexiang.xlog.strategy.format.IFormatStrategy;
import com.xuexiang.xlog.strategy.format.PrettyFormatStrategy;
import com.xuexiang.xlog.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * 日志打印者 【-> format -> log】
 *
 * @author xuexiang
 * @since 2019/3/16 下午8:56
 */
public class Logger implements ILogger {
    /**
     * It is used for json pretty print
     */
    private static final int JSON_INDENT = 2;

    /**
     * 标识.
     */
    private String mName;

    /**
     * DEBUG模式.
     */
    private boolean mDebug;

    /**
     * 日志tag
     */
    private String mLogTag;

    /**
     * 日志格式化策略
     */
    private IFormatStrategy mIFormatStrategy;

    /**
     * 构造方法
     *
     * @param builder
     */
    public Logger(Builder builder) {
        mName = builder.name;
        mDebug = builder.debug;
        tag(builder.logTag);
        mIFormatStrategy = builder.formatStrategy;
    }

    /**
     * 设置tag标签
     *
     * @param tag 标签
     * @return
     */
    @Override
    public Logger tag(String tag) {
        mLogTag = tag;
        return this;
    }

    private String getTag() {
        return mLogTag;
    }

    /**
     * 获取TAG
     *
     * @param element 堆栈元素
     * @return TAG
     */
    private String getTag(@NonNull StackTraceElement element) {
        return Utils.getTag(element);
    }

    /**
     * 设置调试模式
     *
     * @param isDebug
     * @return
     */
    @Override
    public ILogger debug(boolean isDebug) {
        mDebug = isDebug;
        return this;
    }

    @Override
    public void d(String message, Object... args) {
        log(LogLevel.DEBUG, null, message, args);
    }

    @Override
    public void d(Object object) {
        log(LogLevel.DEBUG, null, Utils.toString(object));
    }

    @Override
    public void e(String message, Object... args) {
        log(LogLevel.ERROR, null, message, args);
    }

    @Override
    public void e(Throwable throwable, Object... args) {
        log(LogLevel.ERROR, throwable, null, args);
    }

    @Override
    public void e(Throwable throwable, String message, Object... args) {
        log(LogLevel.ERROR, throwable, message, args);
    }

    @Override
    public void w(String message, Object... args) {
        log(LogLevel.WARN, null, message, args);
    }

    @Override
    public void i(String message, Object... args) {
        log(LogLevel.INFO, null, message, args);
    }

    @Override
    public void v(String message, Object... args) {
        log(LogLevel.VERBOSE, null, message, args);
    }

    @Override
    public void wtf(String message, Object... args) {
        log(LogLevel.WTF, null, message, args);
    }

    /**
     * 格式化json内容并打印
     *
     * @param json  json内容
     */
    @Override
    public void json(String json) {
        if (TextUtils.isEmpty(json)) {
            e("Empty/Null json content");
            return;
        }
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String message = jsonObject.toString(JSON_INDENT);
                d(message);
                return;
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String message = jsonArray.toString(JSON_INDENT);
                d(message);
                return;
            }
            e("Invalid Json");
        } catch (JSONException e) {
            e("Invalid Json");
        }
    }

    /**
     * 格式化xml内容并打印
     *
     * @param xml xml内容
     */
    @Override
    public void xml(String xml) {
        if (TextUtils.isEmpty(xml)) {
            e("Empty/Null xml content");
            return;
        }
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            d(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
        } catch (TransformerException e) {
            e("Invalid xml");
        }
    }

    /**
     * This method is synchronized in order to avoid messy of logs' order.
     */
    private void log(@LogLevel String level, Throwable throwable, String msg, Object... args) {
        String tag = getTag();
        if (TextUtils.isEmpty(tag) && throwable != null) {
            StackTraceElement[] elements = throwable.getStackTrace();
            int index = Utils.getStackOffset(elements);
            tag = getTag(elements[index]);
        }
        String message = createMessage(msg, args);
        log(level, tag, message, throwable);
    }

    private String createMessage(String message, Object... args) {
        return args == null || args.length == 0 ? message : String.format(message, args);
    }

    /**
     * 日志打印（具体的日志打印方法）
     *
     * @param level     日志等级
     * @param tag       标签
     * @param message   信息
     * @param throwable 错误信息
     */
    @Override
    public void log(@LogLevel String level, String tag, String message, Throwable throwable) {
        if (mIFormatStrategy != null) {
            synchronized (mIFormatStrategy) {
                if (throwable != null) {
                    if (TextUtils.isEmpty(message)) {
                        message = Log.getStackTraceString(throwable);
                    } else {
                        message += Utils.LINE_BREAK + Utils.getStackTraceString(throwable);
                    }
                }

                if (!TextUtils.isEmpty(message) && mDebug) {
                    mIFormatStrategy.format(level, tag, message);
                }
            }
        }
    }

    @Override
    public String getName() {
        return mName;
    }

    /**
     * 设置日志的名字
     *
     * @param name
     * @return
     */
    public Logger setName(String name) {
        mName = name;
        return this;
    }

    public IFormatStrategy getIFormatStrategy() {
        return mIFormatStrategy;
    }

    /**
     * 设置日志格式化策略
     *
     * @param strategy 日志格式化策略
     * @return
     */
    public Logger setFormatStrategy(IFormatStrategy strategy) {
        mIFormatStrategy = strategy;
        return this;
    }

    /**
     * 创建日志者
     *
     * @param name
     * @return
     */
    public static Builder newBuilder(@NonNull String name) {
        return new Builder(name);
    }

    public static class Builder {
        /**
         * 标识.
         */
        String name;

        /**
         * DEBUG模式.
         */
        boolean debug;

        /**
         * tag
         */
        String logTag;

        /**
         * 日志格式化策略
         */
        IFormatStrategy formatStrategy;

        private Builder(String name) {
            this.name = name;
            this.debug = true;
            logTag = "Logger";
            formatStrategy = PrettyFormatStrategy.newBuilder().build();
        }

        public String getName() {
            return name;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public boolean isDebug() {
            return debug;
        }

        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public String getLogTag() {
            return logTag;
        }

        public Builder setLogTag(String logTag) {
            this.logTag = logTag;
            return this;
        }

        public IFormatStrategy getFormatStrategy() {
            return formatStrategy;
        }

        public Builder setFormatStrategy(IFormatStrategy formatStrategy) {
            this.formatStrategy = formatStrategy;
            return this;
        }

        public Logger build() {
            Logger logger = new Logger(this);
            XLog.get().addLogger(logger);
            return logger;
        }

        public Logger buildNotAddToXLog() {
            return new Logger(this);
        }
    }
}
