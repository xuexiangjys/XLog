package com.xuexiang.xlog.crash.ui;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 崩溃信息
 *
 * @author xuexiang
 * @since 2019/3/13 下午11:12
 */
public class CrashInfo implements Parcelable {

    /**
     * 崩溃主体信息
     */
    private Throwable mThrowable;
    /**
     * 包名
     */
    private String mPackageName;
    /**
     * 崩溃主信息
     */
    private String mExceptionMsg;
    /**
     * 崩溃类名
     */
    private String mClassName;
    /**
     * 崩溃文件名
     */
    private String mFileName;
    /**
     * 崩溃方法
     */
    private String mMethodName;
    /**
     * 崩溃行数
     */
    private int mLineNumber;
    /**
     * 崩溃类型
     */
    private String mExceptionType;
    /**
     * 全部信息
     */
    private String mFullException;
    /**
     * 崩溃时间
     */
    private long mTime;
    /**
     * 设备信息
     */
    private Device mDeviceInfo = new Device();

    /**
     * 崩溃日志的文件路径
     */
    private String mCrashLogFilePath;

    protected CrashInfo(Parcel in) {
        mThrowable = (Throwable) in.readSerializable();
        mExceptionMsg = in.readString();
        mPackageName = in.readString();
        mClassName = in.readString();
        mFileName = in.readString();
        mMethodName = in.readString();
        mLineNumber = in.readInt();
        mExceptionType = in.readString();
        mFullException = in.readString();
        mTime = in.readLong();
        mCrashLogFilePath = in.readString();
    }

    public CrashInfo() {
    }

    public static final Creator<CrashInfo> CREATOR = new Creator<CrashInfo>() {
        @Override
        public CrashInfo createFromParcel(Parcel in) {
            return new CrashInfo(in);
        }

        @Override
        public CrashInfo[] newArray(int size) {
            return new CrashInfo[size];
        }
    };

    public Throwable getThrowable() {
        return mThrowable;
    }

    public void setException(Throwable throwable) {
        mThrowable = throwable;
    }

    public String getExceptionMsg() {
        return mExceptionMsg;
    }

    public void setExceptionMsg(String exceptionMsg) {
        mExceptionMsg = exceptionMsg;
    }

    public String getClassName() {
        return mClassName;
    }

    public void setClassName(String className) {
        mClassName = className;
    }

    public String getFileName() {
        return TextUtils.isEmpty(mFileName) ? mClassName : mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public String getMethodName() {
        return mMethodName;
    }

    public void setMethodName(String methodName) {
        mMethodName = methodName;
    }

    public int getLineNumber() {
        return mLineNumber;
    }

    public void setLineNumber(int lineNumber) {
        mLineNumber = lineNumber;
    }

    public String getExceptionType() {
        return mExceptionType;
    }

    public void setExceptionType(String exceptionType) {
        mExceptionType = exceptionType;
    }

    public String getFullException() {
        return mFullException;
    }

    public void setFullException(String fullException) {
        mFullException = fullException;
    }

    public CrashInfo setPackageName(String packageName) {
        mPackageName = packageName;
        return this;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public long getTime() {
        return mTime;
    }

    public CrashInfo setCrashLogFilePath(String crashLogFilePath) {
        mCrashLogFilePath = crashLogFilePath;
        return this;
    }

    public String getCrashLogFilePath() {
        return mCrashLogFilePath;
    }

    public void setTime(long time) {
        this.mTime = time;
    }

    public Device getDevice() {
        return mDeviceInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mThrowable);
        dest.writeString(mExceptionMsg);
        dest.writeString(mPackageName);
        dest.writeString(mClassName);
        dest.writeString(mFileName);
        dest.writeString(mMethodName);
        dest.writeInt(mLineNumber);
        dest.writeString(mExceptionType);
        dest.writeString(mFullException);
        dest.writeLong(mTime);
        dest.writeString(mCrashLogFilePath);
    }

    public static class Device implements Parcelable {
        /**
         * 设备名
         */
        private String mModel = Build.MODEL;
        /**
         * 设备厂商
         */
        private String mBrand = Build.BRAND;
        /**
         * 系统版本号
         */
        private String mVersion = String.valueOf(Build.VERSION.SDK_INT);

        public Device() {
        }

        protected Device(Parcel in) {
            mModel = in.readString();
            mBrand = in.readString();
            mVersion = in.readString();
        }

        public static final Creator<Device> CREATOR = new Creator<Device>() {
            @Override
            public Device createFromParcel(Parcel in) {
                return new Device(in);
            }

            @Override
            public Device[] newArray(int size) {
                return new Device[size];
            }
        };

        public String getModel() {
            return mModel;
        }

        public String getBrand() {
            return mBrand;
        }

        public String getVersion() {
            return mVersion;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mModel);
            dest.writeString(mBrand);
            dest.writeString(mVersion);
        }
    }

    @Override
    public String toString() {
        return "CrashInfo{" +
                "mThrowable=" + mThrowable +
                ", mPackageName='" + mPackageName + '\'' +
                ", mExceptionMsg='" + mExceptionMsg + '\'' +
                ", mClassName='" + mClassName + '\'' +
                ", mFileName='" + mFileName + '\'' +
                ", mMethodName='" + mMethodName + '\'' +
                ", mLineNumber=" + mLineNumber +
                ", mExceptionType='" + mExceptionType + '\'' +
                ", mFullException='" + mFullException + '\'' +
                ", mTime=" + mTime +
                ", mDeviceInfo=" + mDeviceInfo +
                '}';
    }
}
