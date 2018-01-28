package com.xuexiang.xlog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.xuexiang.xlog.annotation.LogLevel;
import com.xuexiang.xlog.annotation.LogSegment;
import com.xuexiang.xlog.logger.Logger;
import com.xuexiang.xlog.strategy.format.DiskFormatStrategy;
import com.xuexiang.xlog.strategy.format.IFormatStrategy;
import com.xuexiang.xlog.strategy.log.DiskLogStrategy;
import com.xuexiang.xlog.strategy.log.ILogStrategy;
import com.xuexiang.xlog.utils.TimeUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_log).setOnClickListener(this);
        findViewById(R.id.btn_crash).setOnClickListener(this);
    }

    private void log() {
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

    private void crash() {
        throw new NullPointerException("崩溃啦！");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_log:
                log();
                break;
            case R.id.btn_crash:
                crash();
                break;
            default:
                break;
        }
    }

}
