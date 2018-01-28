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

        ILogStrategy diskLogStrategy = DiskLogStrategy.newBuilder()       //日志打印策略
                .setLogDir("xlogDemo")                                    //设置日志文件存储的根目录
                .setLogPrefix("xlog")                                     //设置日志文件名的前缀
                .setLogSegment(LogSegment.FOUR_HOURS)                     //设置日志记录的时间片间隔
                .setLogLevels(LogLevel.ERROR, LogLevel.DEBUG)             //设置日志记录的等级
                .build();
        IFormatStrategy formatStrategy = DiskFormatStrategy.newBuilder()  //日志格式策略
                .setShowThreadInfo(false)                                 //设置是否显示线程信息
                .setTimeFormat(TimeUtils.LOG_LINE_TIME)                   //设置日志记录时间的时间格式
                .setMethodCount(1)                                        //设置打印显示的方法数
                .setLogStrategy(diskLogStrategy)                          //设置日志打印策略
                .build();
        Logger.newBuilder("DiskLogger")
                .setFormatStrategy(formatStrategy)                        //设置日志格式策略
                .build();

        UserInfo userInfo = new UserInfo().setLoginName("xuexiang").setPassword("12345678");

        String json = new Gson().toJson(userInfo);
        XLog.get().d(json);
        XLog.get().json(json);
        XLog.get().xml(ResourceUtils.readStringFromAssert(this, "AndroidManifest.xml"));

        try {
            throw new NullPointerException("出错啦！");
        } catch (Exception e) {
            XLog.get().e(e);
            e.printStackTrace();
        }
    }

}
