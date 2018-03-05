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

package com.xuexiang.xlogdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.xuexiang.xlog.XLog;

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
