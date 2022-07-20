package com.tencent.liteav.demo.tpnspush;

import android.text.TextUtils;

import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMOfflinePushConfig;
import com.tencent.liteav.trtccalling.model.impl.base.TRTCLogger;

/**
 * 用来保存厂商注册离线推送token的管理类示例，当登陆IM后，通过 setOfflinePushToken 上报证书 ID 及设备 token 给 IM 后台。
 * 开发者可以根据自己的需求灵活实现
 */
public class ThirdPushTokenMgr {
    private static final String TAG = "ThirdPushTokenMgr";
    private              String mThirdPushToken;

    private static final ThirdPushTokenMgr sInstance = new ThirdPushTokenMgr();

    public static ThirdPushTokenMgr getInstance() {
        return sInstance;
    }

    public String getThirdPushToken() {
        return mThirdPushToken;
    }

    public void setThirdPushToken(String token) {
        this.mThirdPushToken = token;
    }

    /**
     * 更新IM离线推送config
     */
    public void setPushTokenToTIM() {
        String token = ThirdPushTokenMgr.getInstance().getThirdPushToken();
        if (TextUtils.isEmpty(token)) {
            TRTCLogger.i(TAG, "setPushTokenToTIM third token is empty");
            return;
        }
        V2TIMOfflinePushConfig v2TIMOfflinePushConfig = null;
        v2TIMOfflinePushConfig = new V2TIMOfflinePushConfig(0, token, true);

        V2TIMManager.getOfflinePushManager().setOfflinePushConfig(v2TIMOfflinePushConfig,
                new V2TIMCallback() {
                    @Override
                    public void onError(int code, String desc) {
                        TRTCLogger.e(TAG, "setOfflinePushToken failed errorCode = "
                                + code + " , errorMsg = " + desc);
                    }

                    @Override
                    public void onSuccess() {
                        TRTCLogger.d(TAG, "setOfflinePushToken success");
                    }
                });
    }
}
