# XLog
[![xl][xlsvg]][xl]  [![api][apisvg]][api]

一个简易的日志打印框架（支持打印策略自定义，默认提供2种策略：logcat打印和磁盘打印）

## 关于我
[![github](https://img.shields.io/badge/GitHub-xuexiangjys-blue.svg)](https://github.com/xuexiangjys)   [![csdn](https://img.shields.io/badge/CSDN-xuexiangjys-green.svg)](http://blog.csdn.net/xuexiangjys)

## 特点
- 支持自定义日志格式策略IFormatStrategy和打印策略ILogStrategy。
- 提供默认的两种日志打印方式：logcat（PrettyFormatStrategy）和磁盘打印（PrettyFormatStrategy）。
- 兼容android logcat，VERBOSE、DEBUG、INFO、WARN、ERROR和WTF全都有，一个都不能少
- 突破了logcat的4000字长度限制
- 支持打印xml，json，模版String等形式。
- 支持自定义日志文件存储形式（文件前缀、时间片存储等）。
- 在日志文件的顶部，XLog提供了很多有用的运行环境相关的信息，比如操作系统信息、设备信息和应用信息
- 支持时区设置。
- 支持日志文件信息可选择打印。
- 支持打印线程信息。
- 支持打印方法的数量。
- 支持捕捉并打印崩溃日志。
- 支持自定义崩溃日志处理。

## 1、演示（请star支持）

### 1.1、logcat打印效果

#### 打印debug信息和json日志
![](https://github.com/xuexiangjys/XLog/blob/master/img/1.jpg)

#### 打印xml信息
![](https://github.com/xuexiangjys/XLog/blob/master/img/2.jpg)

#### 打印出错信息
![](https://github.com/xuexiangjys/XLog/blob/master/img/3.jpg)

### 1.2、磁盘打印效果
#### 打印debug信息和json日志
![](https://github.com/xuexiangjys/XLog/blob/master/img/6.jpg)

#### 打印xml信息
![](https://github.com/xuexiangjys/XLog/blob/master/img/5.jpg)

#### 打印出错信息
![](https://github.com/xuexiangjys/XLog/blob/master/img/4.jpg)

## 2、如何使用
目前支持主流开发工具AndroidStudio的使用，直接配置build.gradle，增加依赖即可.

### 2.1、Android Studio导入方法，添加Gradle依赖

先在项目根目录的 build.gradle 的 repositories 添加:
```
allprojects {
     repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

然后在dependencies添加:

```
dependencies {
   ...
   implementation 'com.github.xuexiangjys:XLog:1.0.1'
   implementation 'com.squareup.okio:okio:1.10.0'
}
```
### 2.2、初始化
在Application中初始化
```
XLog.init(this);
```
### 2.3、构建Logger
构建一个Logger需要一个日志格式化策略和一个日志打印策略。
```
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
CrashHandler.getInstance().setOnCrashListener(new SendEmailCrashListener()); //增加崩溃发送email的监听处理
```
### 2.4、日志记录
```
UserInfo userInfo = new UserInfo().setLoginName("xuexiang").setPassword("12345678");
String json = new Gson().toJson(userInfo);

XLog.get().d(json);                   //打印debug日志
XLog.get().json(json);                //打印json信息
XLog.get().xml(ResourceUtils.readStringFromAssert(this, "AndroidManifest.xml"));    //打印xml

try {
    throw new NullPointerException("出错啦！");
} catch (Exception e) {
    XLog.get().e(e);     //打印错误信息
}
```


## 特别感谢
https://github.com/orhanobut/logger

https://github.com/JiongBull/jlog



[xlsvg]: https://img.shields.io/badge/XLog-v1.0.1-brightgreen.svg
[xl]: https://github.com/xuexiangjys/XLog
[apisvg]: https://img.shields.io/badge/API-14+-brightgreen.svg
[api]: https://android-arsenal.com/api?level=14
