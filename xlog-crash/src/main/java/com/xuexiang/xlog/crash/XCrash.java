package com.xuexiang.xlog.crash;

import com.xuexiang.xlog.crash.mail.MailInfo;

/**
 * Crash全局处理
 *
 * @author xuexiang
 * @since 2019/3/17 上午2:02
 */
public class XCrash {

    /**
     * 默认SMTP主机
     */
    private static final String DEFAULT_HOST = "smtp.163.com";
    /**
     * 默认SMTP服务端口
     */
    private static final String DEFAULT_PORT = "465";

    private static volatile XCrash sInstance = null;

    private MailInfo mMailInfo;
    /**
     * 发送邮箱的地址
     */
    private String mSendEmail;
    /**
     * 邮箱第三方登陆授权码
     */
    private String mAuthorizationCode;
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
    /**
     * 抄送人的邮箱地址
     */
    private String[] mCcEmails;


    private XCrash() {
        mServerHost = DEFAULT_HOST;
        mServerPort = DEFAULT_PORT;
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static XCrash getInstance() {
        if (sInstance == null) {
            synchronized (XCrash.class) {
                if (sInstance == null) {
                    sInstance = new XCrash();
                }
            }
        }
        return sInstance;
    }

    public XCrash setMailInfo(MailInfo mailInfo) {
        mMailInfo = mailInfo;
        return this;
    }

    public XCrash setSendEmail(String sendEmail) {
        mSendEmail = sendEmail;
        return this;
    }

    /**
     * 设置第三方邮件登陆授权码
     *
     * @param authorizationCode
     * @return
     */
    public XCrash setAuthorizationCode(String authorizationCode) {
        mAuthorizationCode = authorizationCode;
        return this;
    }

    /**
     * 设置邮件服务地址
     *
     * @param serverHost
     * @return
     */
    public XCrash setServerHost(String serverHost) {
        mServerHost = serverHost;
        return this;
    }

    /**
     * 设置邮件服务端口
     *
     * @param serverPort
     * @return
     */
    public XCrash setServerPort(String serverPort) {
        mServerPort = serverPort;
        return this;
    }

    /**
     * 设置收件人邮件地址
     *
     * @param toEmails
     * @return
     */
    public XCrash setToEmails(String... toEmails) {
        mToEmails = toEmails;
        return this;
    }

    /**
     * 设置抄送人邮件地址
     *
     * @param ccEmails
     * @return
     */
    public XCrash setCcEmails(String... ccEmails) {
        mCcEmails = ccEmails;
        return this;
    }

    //============get==============//

    public static MailInfo getMailInfo() {
        return getInstance().mMailInfo;
    }

    public static String getSendEmail() {
        return getInstance().mSendEmail;
    }

    public static String getAuthorizationCode() {
        return getInstance().mAuthorizationCode;
    }

    public static String getServerHost() {
        return getInstance().mServerHost;
    }

    public static String getServerPort() {
        return getInstance().mServerPort;
    }

    public static String[] getToEmails() {
        return getInstance().mToEmails;
    }

    public static String[] getCcEmails() {
        return getInstance().mCcEmails;
    }
}
