# XLog
[![xl][xlsvg]][xl]  [![api][apisvg]][api]

一个简易的日志打印框架（支持打印策略自定义，默认提供2种策略：logcat打印和磁盘打印）

## 特征

* 支持自定义日志格式策略IFormatStrategy和打印策略ILogStrategy。
* 提供默认的两种日志打印方式：logcat（PrettyFormatStrategy）和磁盘打印（PrettyFormatStrategy）。
* 兼容android logcat，VERBOSE、DEBUG、INFO、WARN、ERROR和WTF全都有，一个都不能少
* 突破了logcat的4000字长度限制
* 支持打印xml，json，模版String等形式。
* 支持自定义日志文件存储形式（文件前缀、时间片存储等）。
* 在日志文件的顶部，XLog提供了很多有用的运行环境相关的信息，比如操作系统信息、设备信息和应用信息
* 支持时区设置。
* 支持日志文件信息可选择打印。
* 支持打印线程信息。
* 支持打印方法的数量。
* 支持捕捉并打印崩溃日志。
* 支持自定义崩溃日志处理【默认提供了3种处理方式】。
* 支持第三方打印接口适配。

## 添加Gradle依赖

1.先在项目根目录的 build.gradle 的 repositories 添加:

```
allprojects {
     repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

2.然后在dependencies添加:

```
dependencies {
   ...
   implementation 'com.github.xuexiangjys.XLog:xlog-core:1.2.1'
   //崩溃处理相关（非必要）
   implementation 'com.github.xuexiangjys.XLog:xlog-crash:1.2.1'
}
```

3.在Application中初始化

```
XLog.init(this);
```

## 特别感谢

https://github.com/orhanobut/logger

https://github.com/JiongBull/jlog

## 联系方式

[![](https://img.shields.io/badge/点击一键加入QQ群-602082750-blue.svg)](http://shang.qq.com/wpa/qunwpa?idkey=9922861ef85c19f1575aecea0e8680f60d9386080a97ed310c971ae074998887)


[xlsvg]: https://img.shields.io/badge/XLog-v1.2.1-brightgreen.svg
[xl]: https://github.com/xuexiangjys/XLog
[apisvg]: https://img.shields.io/badge/API-14+-brightgreen.svg
[api]: https://android-arsenal.com/api?level=14
