# TUICalling Android 离线推送接入指引

本文档主要介绍如何在TUICalling Demo中快速体验离线推送功能。

TUICalling Demo使用 TPNS（Tencent Push Notification Service）技术提供的高效推送服务。
TPNS详情您可查阅官方文档进行了解--[TPNS概述](https://cloud.tencent.com/document/product/548/36645)。

## 步骤一：注册厂商通道
您需要先在各厂商平台上传或注册您的应用，获取注册信息。详情请查看：[厂商通道接入指南](https://cloud.tencent.com/document/product/548/45909)
>? 注册厂商通道需要传入自己的包名，各厂商填入的包名保持一致，用于消息互通。

## 步骤二：注册TPNS服务

1. 获取到厂商注册信息后，前往移动推送 TPNS 控制台 [创建产品和应用](https://cloud.tencent.com/document/product/548/37241)。

2. 按照文档一键填入各厂商对应的APPID和SecretKey值。
![#600px](https://qcloudimg.tencent-cloud.cn/raw/9f8e883e2fb88a885e80e3326a1e0674.png)

3. 填入完成后，下载配置文件`tpns-config.json`。该文件含有您的私有信息，注意不要外传。
   ![#600px](https://qcloudimg.tencent-cloud.cn/raw/77a1fdbb89d0c93595441a2380ab2bb3.png)

## 步骤三：绑定TUICalling应用和TPNS
根据步骤二创建完TPNS应用后，您需要将您的TUICalling与TPNS进行绑定，如下图所示：

1. 进入[TPNS配置界面](https://console.cloud.tencent.com/tpns/service-auth)，点击授权。

![#600px](https://qcloudimg.tencent-cloud.cn/raw/cf49adc442289e6e086136545a93c366.png)

2. 填入您的应用名称后提交
![#600px](https://qcloudimg.tencent-cloud.cn/raw/4f3114ec889c0cefd0b593bc9669e4ff.png)


## 步骤四：修改TUICalling工程配置

1. 用步骤二下载的配置文件替换工程中的`app/tpns-config.json`文件。
  
2. 修改`app/build.gradle`中的应用包名为自己的包名
   ```  
   applicationId '您的应用包名'
   ```

3. 设置`app/build.gradle`中的TPNS接入参数`XG_ACCESS_ID`和`XG_ACCESS_KEY`。  
   这两个值在上述步骤二下载的`tpns-config.json`中获取。
   ```
   manifestPlaceholders = [
         XG_ACCESS_ID : "",
         XG_ACCESS_KEY: ""
   ]
   ```
完成以上步骤，运行TUICalling工程，您可以体验TUICalling离线唤起功能。


## 常见问题
### 离线推送异常请先查阅以下文档   
[Android 常见问题](https://cloud.tencent.com/document/product/548/36674)    
[Android 错误码](https://cloud.tencent.com/document/product/548/36660)

### 收不到通知
1. 用厂商控制台进行推送测试，能成功说明厂商通道没有问题。再检查`TPNS`控制台厂商参数配置是否正确，按要求进行填写。（经测试：vivo x9必须在控制台配置消息类别）。
2. 部分手机收到通知会放到`不重要的通知`中，请下拉状态栏，检查是否归纳到`不重要的通知`中
3. 检查TPNS注册是否成功。成功日志：
   ```
   TPNSPushSetting: tpush register success token:*****
   ```
    如果您收不到通知，根据日志获取到 -502 的错误，则可以进行以下排查。
    ```
    TPNSPushSetting: tpush register failed errCode:-502，errorMsg:mqtt connect error
    ```
   1). 检查SDKAPPID输入正确
   
     > SDKAPPID是绑定的IM应用的账号，查看IM账号信息：[IM应用](https://console.cloud.tencent.com/im) 
     
   2). 检查TPNS控制台是否注册了TPNS应用并进行了绑定，请参考`步骤二`和`步骤三`   

### 应用在后台时拉不起界面

Android手机由于厂商和平台的限制，在后台唤起界面需要开启`悬浮窗`权限或者`后台拉起界面`的权限，不同机型对权限的开放情况不完全相同。

例如：小米6只需`后台弹出界面`权限，但是红米需要同时打开`后台弹出界面`和`显示悬浮窗`权限。

可以在设置里面手动开启该权限。
开启权限的方法：打开手机设置，找到应用管理，找到您的应用，点击权限，点击悬浮窗并允许。

> 如遇到该问题，需要做兼容处理，您可以加入我们下方的QQ群进行咨询与反馈~

### 锁屏时无法点亮屏幕

Android手机由于厂商和平台的限制，在锁屏情况下需要不同的权限。请按以下情况进行排查。
1. 确认打开厂商锁屏下通知权限
   
   部分厂商统一做了约束，例如小米锁屏下离线通知到达时未亮屏：在设置-锁屏里，点击开关“锁屏来通知时亮屏”，打开开关。
   
2. 确认打开应用锁屏通知权限

   例如：小米 需要锁屏显示权限。  

> 如遇到该问题，需要做兼容处理，您可以加入我们下方的QQ群进行咨询与反馈~


### TPNS版本更新导致编译异常问题
1. 如无需更新最新版本，则修改`tpns-config.json`文件，将`upgrade`设置为`false`；
2. 如需依赖最新版本，则修改`tpns-config.json`文件，将`Version`设置为最新([TPNS发布动态](https://cloud.tencent.com/document/product/548/44520))；   
同时修改`app/build.gradle`文件，将TPNS依赖修改为最新,例如：
```
implementation 'com.tencent.tpns:tpns:1.3.2.1-release'
```

### 其他问题:
如有其他问题，欢迎加入 `TUI组件交流群:592465424` 一起交流及学习~
