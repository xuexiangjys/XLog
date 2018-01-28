# XLog
[![xl][xlsvg]][xl]  [![api][apisvg]][api]

一个简易的日志打印框架（支持打印策略自定义，默认提供2种策略：logcat打印和磁盘打印）

## 关于我
[![github](https://img.shields.io/badge/GitHub-xuexiangjys-blue.svg)](https://github.com/xuexiangjys)   [![csdn](https://img.shields.io/badge/CSDN-xuexiangjys-green.svg)](http://blog.csdn.net/xuexiangjys)

## 1、演示


## 2、如何使用
目前支持主流开发工具AndtoidStudio的使用，直接配置build.gradle，增加依赖即可.

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







[xlsvg]: https://img.shields.io/badge/XLog-v1.0.1-brightgreen.svg
[xl]: https://github.com/xuexiangjys/XLog
[apisvg]: https://img.shields.io/badge/API-14+-brightgreen.svg
[api]: https://android-arsenal.com/api?level=14
