/*
 * @(#)Snippet.java		       Project: CrashHandler
 * Date: 2014-5-27
 *
 * Copyright (c) 2014 CFuture09, Institute of Software,
 * Guangdong Ocean University, Zhanjiang, GuangDong, China.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed mToEmails in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xuexiang.xlog.crash.mail;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * 邮件类，用于构造要发送的邮件。
 *
 * @author xuexiang
 * @since 2019/3/16 下午9:59
 */
public class MailInfo extends Authenticator implements Parcelable {
    /**
     * 邮箱授权用户名
     */
    private String mAuthorizedUser;
    /**
     * 邮箱第三方登陆授权码
     */
    private String mAuthorizationCode;
    /**
     * SMTP主机
     */
    private String mHost;
    /**
     * SMTP端口号
     */
    private String mPort;
    /**
     * 发件人的邮箱地址
     */
    private String mSendEmail;
    /**
     * 收件人的邮箱地址
     */
    private String[] mToEmails;
    /**
     * 抄送人的邮箱地址
     */
    private String[] mCcEmails;
    /**
     * 邮件的标题
     */
    private String mTitle;
    /**
     * 邮件的正文
     */
    private String mContent;
    /**
     * 附件
     */
    private Multipart mMultipart;
    private Properties mProps;

    public MailInfo() {

    }

    /**
     * @param authorizedUser    邮箱授权用户名
     * @param authorizationCode 邮箱第三方登陆授权码
     * @param host              SMTP主机
     * @param port              SMTP端口号
     * @param sendEmail         发件人
     * @param toEmails          收件人
     * @param ccEmails          抄送人
     * @param title             邮件主题
     * @param content           邮件正文
     */
    public MailInfo(String authorizedUser, String authorizationCode, String host, String port, String sendEmail, String[] toEmails, String[] ccEmails, String title, String content) {
        mAuthorizedUser = authorizedUser;
        mAuthorizationCode = authorizationCode;
        mHost = host;
        mPort = port;
        mSendEmail = sendEmail;
        mToEmails = toEmails;
        mCcEmails = ccEmails;
        mTitle = title;
        mContent = content;
    }

    protected MailInfo(Parcel in) {
        mAuthorizedUser = in.readString();
        mAuthorizationCode = in.readString();
        mHost = in.readString();
        mPort = in.readString();
        mSendEmail = in.readString();
        mToEmails = in.createStringArray();
        mCcEmails = in.createStringArray();
        mTitle = in.readString();
        mContent = in.readString();
    }

    public static final Creator<MailInfo> CREATOR = new Creator<MailInfo>() {
        @Override
        public MailInfo createFromParcel(Parcel in) {
            return new MailInfo(in);
        }

        @Override
        public MailInfo[] newArray(int size) {
            return new MailInfo[size];
        }
    };

    public MailInfo setAuthorizedUser(String authorizedUser) {
        mAuthorizedUser = authorizedUser;
        return this;
    }

    public MailInfo setAuthorizationCode(String authorizationCode) {
        mAuthorizationCode = authorizationCode;
        return this;
    }

    public MailInfo setHost(String host) {
        mHost = host;
        return this;
    }

    public MailInfo setPort(String port) {
        mPort = port;
        return this;
    }

    public MailInfo setSendEmail(String sendEmail) {
        mSendEmail = sendEmail;
        return this;
    }

    public MailInfo setToEmails(@NonNull String... toEmails) {
        mToEmails = toEmails;
        return this;
    }

    public MailInfo setCcEmails(@NonNull String... ccEmails) {
        mCcEmails = ccEmails;
        return this;
    }

    public MailInfo setTitle(String title) {
        mTitle = title;
        return this;
    }

    public MailInfo setContent(String content) {
        mContent = content;
        return this;
    }

    public MailInfo setProperties(Properties props) {
        mProps = props;
        return this;
    }

    /**
     * 初始化。它在设置好用户名、密码、发件人、收件人、主题、正文、主机及端口号之后显示调用。
     */
    public void init() {
        mMultipart = new MimeMultipart();
        //发送附件时有时候会报java-mail的Error, eg:javax.activation.UnsupportedDataTypeException: no object DCH for MIME type mMultipart/related;所以务必添加以下几行代码来确定DCH
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
        mProps = new Properties();
        mProps.put("mail.smtp.host", mHost);
        mProps.put("mail.smtp.auth", "true");
        mProps.put("mail.smtp.port", mPort);
        mProps.put("mail.smtp.socketFactory.port", mPort);
        mProps.put("mail.transport.protocol", "smtp");
        mProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        mProps.put("mail.smtp.socketFactory.fallback", "false");
    }

    /**
     * 发送邮件
     *
     * @return 是否发送成功
     * @throws MessagingException
     */
    public boolean send() throws MessagingException {
        if (TextUtils.isEmpty(mAuthorizedUser) || TextUtils.isEmpty(mAuthorizationCode) || TextUtils.isEmpty(mSendEmail) || mToEmails == null) {
            return false;
        }

        Session session = Session.getDefaultInstance(mProps, this);
        MimeMessage msg = new MimeMessage(session);
        InternetAddress fromAddress = new InternetAddress(mSendEmail);
        msg.setFrom(fromAddress);
        //收件人
        for (String toEmail : mToEmails) {
            msg.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(toEmail));
        }
        //抄送人（抄送人设置为发件人，就是为了解决垃圾邮件的验证）
        msg.setRecipient(MimeMessage.RecipientType.CC, fromAddress);
        //抄送人
        if (mCcEmails != null && mCcEmails.length > 0) {
            for (String ccEmail : mCcEmails) {
                msg.addRecipient(MimeMessage.RecipientType.CC, new InternetAddress(ccEmail));
            }
        }
        msg.setSubject(mTitle);
        msg.setSentDate(new Date());
        // setup message 正文
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(mContent);
        mMultipart.addBodyPart(messageBodyPart, 0);
        // Put parts in message
        msg.setContent(mMultipart);
        // send email
        Transport.send(msg);
        return true;
    }

    /**
     * 添加附件
     *
     * @param filePath 附件路径
     * @throws Exception
     */
    public void addAttachment(String filePath) throws Exception {
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.attachFile(filePath);
        mMultipart.addBodyPart(messageBodyPart);
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(mAuthorizedUser, mAuthorizationCode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAuthorizedUser);
        dest.writeString(mAuthorizationCode);
        dest.writeString(mHost);
        dest.writeString(mPort);
        dest.writeString(mSendEmail);
        dest.writeStringArray(mToEmails);
        dest.writeStringArray(mCcEmails);
        dest.writeString(mTitle);
        dest.writeString(mContent);
    }
}