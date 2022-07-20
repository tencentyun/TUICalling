package com.tencent.liteav.demo.tpnspush;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.tencent.liteav.trtccalling.model.impl.base.TRTCLogger;
import com.tencent.liteav.trtccalling.model.util.BrandUtil;

public class OfflineMessageDispatcher {
    private static final String TAG = "OfflineMessageDispatcher";

    public static void updateBadge(final Context context, final int number) {
        if (!BrandUtil.isBrandHuawei()) {
            return;
        }
        TRTCLogger.i(TAG, "huawei badge = " + number);
        try {
            Bundle extra = new Bundle();
            extra.putString("package", "com.tencent.liteav.demo");
            extra.putString("class", "com.tencent.liteav.demo.SplashActivity");
            extra.putInt("badgenumber", number);
            context.getContentResolver().call(
                    Uri.parse("content://com.huawei.android.launcher.settings/badge/"),
                    "change_badge", null, extra);
        } catch (Exception e) {
            TRTCLogger.e(TAG, "huawei badge exception: " + e.getLocalizedMessage());
        }
    }
}
