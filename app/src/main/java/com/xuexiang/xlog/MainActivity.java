package com.xuexiang.xlog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.xuexiang.xlog.logger.Logger;
import com.xuexiang.xlog.strategy.format.DiskFormatStrategy;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logger logger = Logger.newBuilder("PrettyFormatStrategy").build();
        Logger logger1 = Logger.newBuilder("DiskFormatStrategy")
                .setFormatStrategy(DiskFormatStrategy.newBuilder().build()).build();

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
