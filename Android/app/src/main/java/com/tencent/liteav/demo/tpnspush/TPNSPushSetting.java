package com.tencent.liteav.demo.tpnspush;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.liteav.demo.BaseApplication;
import com.tencent.liteav.trtccalling.model.impl.base.TRTCLogger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TPNSPushSetting implements PushSettingInterface {
    private static final String          TAG       = "TPNSPushSetting";
    private static       TPNSPushSetting sInstance = new TPNSPushSetting();

    public static TPNSPushSetting getInstance() {
        return sInstance;
    }

    @Override
    public void init() {
        // 关闭 TPNS SDK 拉活其他 app 的功能
        // ref: https://cloud.tencent.com/document/product/548/36674#.E5.A6.82.E4.BD.95.E5.85.B3.E9.97.AD-tpns-.E7.9A.84.E4.BF.9D.E6.B4.BB.E5.8A.9F.E8.83.BD.EF.BC.9F
        XGPushConfig.enablePullUpOtherApp(BaseApplication.getApplication(), false);

        // TPNS SDK 注册
        prepareTPNSRegister();
    }

    @Override
    public void unInit() {
        XGPushManager.unregisterPush(BaseApplication.getApplication(),
                new XGIOperateCallback() {
                    // 操作成功时的回调。
                    // @param data 操作成功的业务数据，如注册成功时的token信息等。
                    // @param flag 标记码
                    @Override
                    public void onSuccess(Object data, int flag) {
                        TRTCLogger.d(TAG, "tpns unInit success");
                    }

                    @Override
                    public void onFail(Object data, int errCode, String msg) {
                        TRTCLogger.d(TAG, "tpns unInit failed, errorCode：" + errCode + ", errorMsg：" + msg);
                    }
                });
    }

    @Override
    public void bindUserID(String userId) {
        // 重要：IM 登录用户账号时，调用 TPNS 账号绑定接口绑定业务账号，即可以此账号为目标进行 TPNS 离线推送
        XGPushManager.AccountInfo accountInfo = new XGPushManager.AccountInfo(
                XGPushManager.AccountType.UNKNOWN.getValue(), userId);
        XGPushManager.upsertAccounts(BaseApplication.getApplication(), Arrays.asList(accountInfo),
                new XGIOperateCallback() {
                    @Override
                    public void onSuccess(Object o, int i) {
                        TRTCLogger.d(TAG, "upsertAccounts success");
                    }

                    @Override
                    public void onFail(Object o, int i, String s) {
                        TRTCLogger.e(TAG, "upsertAccounts failed");
                    }
                });
    }

    @Override
    public void unBindUserID(String userId) {
        TRTCLogger.d(TAG, "tpns unBindUserID");
        // TPNS 账号解绑业务账号
        XGIOperateCallback xgiOperateCallback = new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                TRTCLogger.i(TAG, "onSuccess, data:" + data + ", flag:" + flag);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                TRTCLogger.e(TAG, "onFail, data:" + data + ", code:" + errCode + ", msg:" + msg);
            }
        };
        Set<Integer> accountTypeSet = new HashSet<>();
        accountTypeSet.add(XGPushManager.AccountType.CUSTOM.getValue());
        accountTypeSet.add(XGPushManager.AccountType.IMEI.getValue());
        XGPushManager.delAccounts(BaseApplication.getApplication(), accountTypeSet, xgiOperateCallback);
    }

    /**
     * TPNS SDK 推送服务注册接口
     */
    private void prepareTPNSRegister() {
        TRTCLogger.i(TAG, "prepareTPNSRegister start");

        final Context context = BaseApplication.getApplication();
        XGPushConfig.enableDebug(context, true);

        // 重要：开启厂商通道注册
        XGPushConfig.enableOtherPush(context, true);

        // 注册 TPNS 推送服务
        XGPushManager.registerPush(context, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                TRTCLogger.d(TAG, "tpush register success token: " + data);

                String token = (String) data;
                if (!TextUtils.isEmpty(token)) {
                    ThirdPushTokenMgr.getInstance().setThirdPushToken(token);
                    ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
                }

                // 重要：获取通过 TPNS SDK 注册到的厂商推送 token，并调用 IM 接口设置和上传。
                if (XGPushConfig.isUsedOtherPush(context)) {
                    String otherPushToken = XGPushConfig.getOtherPushToken(context);
                    TRTCLogger.d(TAG, "otherPushToken token: " + otherPushToken);
                }
            }

            @Override
            public void onFail(Object data, int errorCode, String msg) {
                TRTCLogger.w(TAG, "tpush register failed errCode: " + errorCode + ", errMsg: " + msg);
            }
        });
    }
}
