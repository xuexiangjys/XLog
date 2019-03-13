package com.xuexiang.xlogdemo.crash.mail;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;
import android.widget.Toast;

import com.xuexiang.xlog.crash.ICrashHandler;
import com.xuexiang.xlog.crash.OnCrashListener;
import com.xuexiang.xlogdemo.R;
import com.xuexiang.xutil.data.DateUtils;

import java.io.File;

/**
 * 自动发送邮件
 *
 * @author xuexiang
 * @since 2019/3/12 下午11:49
 */
public class AutoSendEmailCrashListener implements OnCrashListener {

    /**
     * 默认发送地址
     */
    private final static String DEFAULT_SEND_EMAIL_ADDRESS = "xuexiangjys2012@163.com";
    /**
     * 默认抄送地址
     */
    private final static String DEFAULT_RECEIVE_EMAIL_ADDRESS = "xuexiangandroid@163.com";

    private static final String DEFAULT_SEND_PASSWORD = "xuexiang123";

    private static final String DEFAULT_HOST = "smtp.163.com";

    private static final String DEFAULT_PORT = "465";

    /**
     * 发送邮箱的地址
     */
    private String mSendEmail;
    /**
     * 发送邮箱的授权码
     */
    private String mSendPassword;
    /**
     * 接收邮件的地址
     */
    private String mReceiveEmail;
    /**
     * 设置SMTP主机
     */
    private String mServerHost;
    /**
     * 设置端口
     */
    private String mServerPort;


    public AutoSendEmailCrashListener() {
        this(DEFAULT_SEND_EMAIL_ADDRESS, DEFAULT_SEND_PASSWORD, DEFAULT_RECEIVE_EMAIL_ADDRESS, DEFAULT_HOST, DEFAULT_PORT);
    }

    /**
     * 构造方法
     *
     * @param sendEmail    邮件发送地址
     * @param receiveEmail 邮件抄送地址
     */
    public AutoSendEmailCrashListener(String sendEmail, String sendPassword, String receiveEmail, String host, String port) {
        mSendEmail = sendEmail;
        mSendPassword = sendPassword;
        mReceiveEmail = receiveEmail;
        mServerHost = host;
        mServerPort = port;
    }

    public AutoSendEmailCrashListener setSendEmail(String sendEmail) {
        mSendEmail = sendEmail;
        return this;
    }

    public AutoSendEmailCrashListener setSendPassword(String sendPassword) {
        mSendPassword = sendPassword;
        return this;
    }

    public AutoSendEmailCrashListener setReceiveEmail(String receiveEmail) {
        mReceiveEmail = receiveEmail;
        return this;
    }

    public AutoSendEmailCrashListener setServerHost(String host) {
        mServerHost = host;
        return this;
    }

    public AutoSendEmailCrashListener setServerPort(String port) {
        mServerPort = port;
        return this;
    }

    /**
     * 发生崩溃
     *
     * @param context
     * @param crashHandler
     * @param throwable
     */
    @Override
    public void onCrash(final Context context, final ICrashHandler crashHandler, final Throwable throwable) {
        sendAppCrashReport(context, crashHandler, throwable);
    }


    /**
     * 发送应用崩溃报告
     *
     * @param context
     * @param crashHandler
     * @param throwable
     */
    private void sendAppCrashReport(final Context context, final ICrashHandler crashHandler, Throwable throwable) {
        final File crashLogFile = crashHandler.getCrashLogFile();
        final String crashReport = crashHandler.getCrashReport(throwable);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.xlog_title_app_crash)
                .setMessage(R.string.xlog_tip_crash_msg)
                .setPositiveButton(R.string.xlog_lab_submit_report,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendCrashReportEmail(context, crashHandler, crashLogFile, crashReport);
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
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
     *
     * @param context
     * @param crashHandler
     * @param crashLogFile
     * @param crashReport
     */
    private void sendCrashReportEmail(Context context, ICrashHandler crashHandler, File crashLogFile, String crashReport) {
        MailInfo sender = new MailInfo()
                .setUser(mSendEmail)
                .setPass(mSendPassword)
                .setFrom(mSendEmail)
                .setTo(mReceiveEmail)
                .setHost(mServerHost)
                .setPort(mServerPort)
                .setSubject(context.getString(R.string.xlog_title_crash_report_email) + "【" + DateUtils.getNowString(DateUtils.yyyyMMddHHmmss.get()) + "】")
                .setBody(crashReport);
        sender.init();
        try {
            sender.addAttachment(crashLogFile.getPath());
            sender.send();
            Toast.makeText(context, "邮件发送成功！", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "邮件发送失败，验证失败！", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally { // 退出
            //禁止重启
            crashHandler.setIsNeedReopen(false);
            crashHandler.setIsHandledCrash(true);
        }
    }


}
