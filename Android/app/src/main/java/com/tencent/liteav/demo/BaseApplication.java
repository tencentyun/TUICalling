package com.tencent.liteav.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMConversationListener;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.liteav.demo.tpnspush.OfflineMessageDispatcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BaseApplication extends MultiDexApplication {
    private static String          TAG = "BaseApplication";
    private static BaseApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
        closeAndroidPDialog();
        registerActivityLifecycleCallbacks(new InnerActivityLifecycle());
    }

    public static BaseApplication getApplication() {
        return mInstance;
    }

    class InnerActivityLifecycle implements ActivityLifecycleCallbacks {
        private int     foregroundActivities = 0;
        private boolean isChangingConfiguration;

        private final V2TIMConversationListener unreadListener = new V2TIMConversationListener() {
            @Override
            public void onTotalUnreadMessageCountChanged(long totalUnreadCount) {
                OfflineMessageDispatcher.updateBadge(BaseApplication.this, (int) totalUnreadCount);
            }
        };

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            Log.i(TAG, "onActivityCreated bundle: " + bundle);
            if (bundle != null) { // 若bundle不为空则程序异常结束
                // 重启整个程序
                Intent intent = new Intent(activity, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            foregroundActivities++;
            if (foregroundActivities == 1 && !isChangingConfiguration) {
                // 应用切到前台
                Log.i(TAG, "application enter foreground");
                V2TIMManager.getOfflinePushManager().doForeground(new V2TIMCallback() {
                    @Override
                    public void onError(int code, String desc) {
                        Log.e(TAG, "doForeground err = " + code + ", desc = " + desc);
                    }

                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "doForeground success");
                    }
                });

                V2TIMManager.getConversationManager().removeConversationListener(unreadListener);
            }
            isChangingConfiguration = false;
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            foregroundActivities--;
            if (foregroundActivities == 0) {
                // 应用切到后台
                Log.i(TAG, "application enter background");
                V2TIMManager.getConversationManager().getTotalUnreadMessageCount(new V2TIMValueCallback<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        int totalCount = aLong.intValue();
                        V2TIMManager.getOfflinePushManager().doBackground(totalCount, new V2TIMCallback() {
                            @Override
                            public void onError(int code, String desc) {
                                Log.e(TAG, "doBackground err = " + code + ", desc = " + desc);
                            }

                            @Override
                            public void onSuccess() {
                                Log.i(TAG, "doBackground success");
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String desc) {
                        Log.d(TAG, "onError: code = " + code + " ,errorMsg = " + desc);
                    }
                });

                V2TIMManager.getConversationManager().addConversationListener(unreadListener);
            }
            isChangingConfiguration = activity.isChangingConfigurations();
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }

    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}