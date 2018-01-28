package com.xuexiang.xlog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.xuexiang.xlog.annotation.LogLevel;
import com.xuexiang.xlog.annotation.LogSegment;
import com.xuexiang.xlog.logger.Logger;
import com.xuexiang.xlog.strategy.format.DiskFormatStrategy;
import com.xuexiang.xlog.strategy.format.IFormatStrategy;
import com.xuexiang.xlog.strategy.log.DiskLogStrategy;
import com.xuexiang.xlog.strategy.log.ILogStrategy;
import com.xuexiang.xlog.utils.TimeUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logger.newBuilder("PrettyLogger").build();

        ILogStrategy diskLogStrategy = DiskLogStrategy.newBuilder()  //日志打印策略
                .setLogDir("xlogDemo")
                .setLogPrefix("xlog")
                .setLogSegment(LogSegment.FOUR_HOURS)
                .setLogLevels(LogLevel.ERROR, LogLevel.DEBUG)
                .build();
        IFormatStrategy formatStrategy = DiskFormatStrategy.newBuilder()  //日志打印格式策略
                .setShowThreadInfo(false)
                .setTimeFormat(TimeUtils.LOG_LINE_TIME)
                .setMethodCount(1)
                .setLogStrategy(diskLogStrategy)
                .build();
        Logger.newBuilder("DiskLogger")
                .setFormatStrategy(formatStrategy)
                .build();

        UserInfo userInfo = new UserInfo().setLoginName("xuexiang").setPassword("12345678");

        String json = new Gson().toJson(userInfo);
        XLog.get().d(json);

        try {
            throw new NullPointerException("出错啦！");
        } catch (Exception e) {
            XLog.get().e(e);
            e.printStackTrace();
        }
    }

}
