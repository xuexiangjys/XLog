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

package com.xuexiang.xlog.crash;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.view.WindowManager;

import com.xuexiang.xlog.R;

import java.io.File;

/**
 * 发邮件的崩溃监听
 *
 * @author xuexiang
 * @date 2018/1/29 上午12:16
 */
public class SendEmailCrashListener implements OnCrashListener {
    /**
     * 默认发送地址
     */
    private final static String DEFAULT_SEND_EMAIL_ADDRESS = "xuexiangandroid@163.com";
    /**
     * 默认抄送地址
     */
    private final static String DEFAULT_COPY_EMAIL_ADDRESS = "xuexiangjys2012@gmail.com";

    /**
     * 邮件发送地址
     */
    private String mSendEmailAddress;
    /**
     * 邮件抄送地址
     */
    private String mCopyEmailAddress;

    public SendEmailCrashListener() {
        this(DEFAULT_SEND_EMAIL_ADDRESS, DEFAULT_COPY_EMAIL_ADDRESS);
    }

    /**
     * 构造方法
     * @param sendEmailAddress 邮件发送地址
     * @param copyEmailAddress 邮件抄送地址
     */
    public SendEmailCrashListener(String sendEmailAddress, String copyEmailAddress) {
        mSendEmailAddress = sendEmailAddress;
        mCopyEmailAddress = copyEmailAddress;
    }
    /**
     * 发生奔溃
     *
     * @param context
     * @param crashHandler
     * @param throwable
     */
    @Override
    public void onCrash(final Context context, final ICrashHandler crashHandler, final Throwable throwable) {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                sendAppCrashReport(context, crashHandler, throwable);
                Looper.loop();
            }
        }.start();
    }


    /**
     * 发送应用崩溃报告
     * @param context
     * @param crashHandler
     * @param throwable
     */
    private void sendAppCrashReport(final Context context, final ICrashHandler crashHandler, Throwable throwable) {
        final File crashLogFile = crashHandler.getCrashLogFile();
        final String crashReport = crashHandler.getCrashReport(throwable);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.title_app_crash)
                .setMessage(R.string.tip_crash_msg)
                .setPositiveButton(R.string.lab_submit_report,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                sendCrashReportEmail(context, crashHandler, crashLogFile, crashReport);
                            }
                        })
                .setNeutralButton(R.string.lab_show_detail, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showCrashDetail(context, crashReport, crashHandler);
                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 退出
                                crashHandler.setIsHandledCrash(true);
                            }
                        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        //需要的窗口句柄方式，没有这句会报错的
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    /**
     * 发送崩溃日志邮件
     * @param context
     * @param crashHandler
     * @param crashLogFile
     * @param crashReport
     */
    private void sendCrashReportEmail(Context context, ICrashHandler crashHandler, File crashLogFile, String crashReport) {
        try {
            //这以下的内容，只做参考，因为没有服务器
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String[] tos = {mSendEmailAddress};
            String[] ccs = {mCopyEmailAddress};
            intent.putExtra(Intent.EXTRA_EMAIL, tos); //收件人
            intent.putExtra(Intent.EXTRA_CC, ccs);  //抄送者
            intent.putExtra(Intent.EXTRA_BCC, ccs);  //密送者

            intent.putExtra(Intent.EXTRA_SUBJECT,
                    context.getString(R.string.title_crash_report_email));
            if (crashLogFile != null) {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(crashLogFile));
                intent.putExtra(Intent.EXTRA_TEXT,
                        context.getString(R.string.content_crash_report_email));
            } else {
                intent.putExtra(Intent.EXTRA_TEXT,
                        context.getString(R.string.content_crash_report_email)
                                + crashReport);
            }
            intent.setType("text/plain");
            intent.setType("message/rfc882");
            Intent.createChooser(intent, context.getString(R.string.title_please_choose_email_client));
            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 退出
            crashHandler.setIsNeedReopen(false); //禁止重启
            crashHandler.setIsHandledCrash(true);
        }
    }


    /**
     * 显示崩溃详情
     *
     * @param context
     * @param crashLogReport
     */
    private void showCrashDetail(Context context, String crashLogReport, final ICrashHandler crashHandler) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.title_crash_detail)
                .setMessage(crashLogReport)
                .setPositiveButton(R.string.lab_submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 退出
                        crashHandler.setIsHandledCrash(true);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }
}
