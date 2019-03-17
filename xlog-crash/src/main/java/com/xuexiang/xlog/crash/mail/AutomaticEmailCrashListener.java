package com.xuexiang.xlog.crash.mail;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;
import android.widget.Toast;

import com.xuexiang.xlog.crash.ICrashHandler;
import com.xuexiang.xlog.crash.OnCrashListener;
import com.xuexiang.xlog.crash.R;
import com.xuexiang.xlog.crash.XCrash;
import com.xuexiang.xlog.utils.TimeUtils;

import java.io.File;

/**
 * 通过配置的邮件账户(最好是163邮箱)进行崩溃信息发送的监听【需要申请系统悬浮窗的权限】
 *
 * @author xuexiang
 * @since 2019/3/12 下午11:49
 */
public class AutomaticEmailCrashListener implements OnCrashListener {
    /**
     * 整体的邮件信息
     */
    private MailInfo mMailInfo;
    /**
     * 发送邮箱的地址
     */
    private String mSendEmail;
    /**
     * 发送邮箱的授权码
     */
    private String mSendPassword;
    /**
     * SMTP服务主机
     */
    private String mServerHost;
    /**
     * SMTP服务端口
     */
    private String mServerPort;
    /**
     * 收件人的邮箱地址
     */
    private String[] mToEmails;
//    /**
//     * 抄送人的邮箱地址【暂时会被视为垃圾邮件】
//     */
//    private String[] mCcEmails;

    public AutomaticEmailCrashListener() {
        this(
                XCrash.getMailInfo(),
                XCrash.getSendEmail(),
                XCrash.getAuthorizationCode(),
                XCrash.getServerHost(),
                XCrash.getServerPort(),
                XCrash.getToEmails(),
                XCrash.getCcEmails()
        );
    }

    /**
     * 构造方法
     *
     * @param sendEmail    发送邮箱的地址
     * @param sendPassword 发送邮箱的授权码
     * @param host         SMTP服务主机
     * @param port         SMTP服务端口
     * @param toEmails     收件人的邮箱地址
     * @param ccEmails     抄送人的邮箱地址
     */
    public AutomaticEmailCrashListener(MailInfo mailInfo, String sendEmail, String sendPassword, String host, String port, String[] toEmails, String[] ccEmails) {
        mMailInfo = mailInfo;
        mSendEmail = sendEmail;
        mSendPassword = sendPassword;
        mServerHost = host;
        mServerPort = port;
        mToEmails = toEmails;
//        mCcEmails = ccEmails;
    }

    /**
     * 设置整体的邮件信息
     *
     * @param mailInfo
     * @return
     */
    public AutomaticEmailCrashListener setMailInfo(MailInfo mailInfo) {
        mMailInfo = mailInfo;
        return this;
    }

    /**
     * 设置发件人的邮箱地址
     *
     * @param sendEmail
     * @return
     */
    public AutomaticEmailCrashListener setSendEmail(String sendEmail) {
        mSendEmail = sendEmail;
        return this;
    }

    /**
     * 设置发件人的授权码
     *
     * @param sendPassword
     * @return
     */
    public AutomaticEmailCrashListener setSendPassword(String sendPassword) {
        mSendPassword = sendPassword;
        return this;
    }

    public AutomaticEmailCrashListener setServerHost(String host) {
        mServerHost = host;
        return this;
    }

    public AutomaticEmailCrashListener setServerPort(String port) {
        mServerPort = port;
        return this;
    }

    /**
     * 设置收件人邮件地址
     *
     * @param toEmails
     * @return
     */
    public AutomaticEmailCrashListener setToEmails(String... toEmails) {
        mToEmails = toEmails;
        return this;
    }

//    /**
//     * 设置抄送人邮件地址
//     *
//     * @param ccEmails
//     * @return
//     */
//    public AutomaticEmailCrashListener setCcEmails(String... ccEmails) {
//        mCcEmails = ccEmails;
//        return this;
//    }

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
        MailInfo sender;
        if (mMailInfo == null) {
            sender = new MailInfo()
                    .setAuthorizedUser(mSendEmail)
                    .setAuthorizationCode(mSendPassword)
                    .setSendEmail(mSendEmail)
                    .setToEmails(mToEmails)
//                .setCcEmails(mCcEmails)
                    .setHost(mServerHost)
                    .setPort(mServerPort)
                    .setTitle(context.getString(R.string.xlog_title_crash_report_email) + "【" + TimeUtils.getCurTime("yyyy-MM-dd HH:mm:ss") + "】")
                    .setContent(crashReport);
        } else {
            sender = mMailInfo;
        }
        sender.init();
        try {
            sender.addAttachment(crashLogFile.getPath());
            sender.send();
            Toast.makeText(context, R.string.xlog_email_send_success, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, R.string.xlog_email_send_failed, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally { // 退出
            //禁止重启
            crashHandler.setIsNeedReopen(false);
            crashHandler.setIsHandledCrash(true);
        }
    }


}
