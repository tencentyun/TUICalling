package com.tencent.liteav.demo.tpnspush;

import android.content.Context;

import com.tencent.android.tpush.NotificationAction;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.tencent.liteav.trtccalling.model.impl.base.TRTCLogger;

public class TPNSMessageReceiver extends XGPushBaseReceiver {
    public static final String TAG = "TPNSMessageReceiver";

    /**
     * 消息透传处理
     *
     * @param context
     * @param message 解析自定义的 JSON
     */
    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {

    }

    /**
     * 通知展示
     *
     * @param context
     * @param notifiShowedRlt 包含通知的内容
     */
    @Override
    public void onNotificationShowedResult(Context context, XGPushShowedResult notifiShowedRlt) {

    }

    /**
     * 注册回调
     *
     * @param context
     * @param errorCode 0 为成功，其它为错误码
     */
    @Override
    public void onRegisterResult(Context context, int errorCode, XGPushRegisterResult message) {
        if (context == null || message == null) {
            return;
        }
        String text;
        if (errorCode == SUCCESS) {
            // 在这里拿token
            String token = message.getToken();
            text = "onRegisterResult success. token：" + token;
            ThirdPushTokenMgr.getInstance().setThirdPushToken(token);
            ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
        } else {
            text = message + " , onRegisterResult failed errorCode ：" + errorCode;
        }
        TRTCLogger.d(TAG, text);
    }

    /**
     * 反注册回调
     *
     * @param context
     * @param errorCode 0 为成功，其它为错误码
     */
    @Override
    public void onUnregisterResult(Context context, int errorCode) {
        if (context == null) {
            return;
        }
        String text;
        if (errorCode == SUCCESS) {
            text = "onUnregisterResult success";
        } else {
            text = "onUnregisterResult failed errorCode : " + errorCode;
        }
        TRTCLogger.d(TAG, text);
    }

    /**
     * 设置标签回调
     *
     * @param context
     * @param errorCode 0 为成功，其它为错误码
     * @param tagName   设置的 TAG
     */
    @Override
    public void onSetTagResult(Context context, int errorCode, String tagName) {

    }

    /**
     * 删除标签的回调
     *
     * @param context
     * @param errorCode 0 为成功，其它为错误码
     * @param tagName   设置的 TAG
     */
    @Override
    public void onDeleteTagResult(Context context, int errorCode, String tagName) {

    }

    /**
     * 设置账号回调
     *
     * @param context
     * @param errorCode 0 为成功，其它为错误码
     * @param account   设置的账号
     */
    @Override
    public void onSetAccountResult(Context context, int errorCode, String account) {

    }

    /**
     * 删除账号回调
     *
     * @param context
     * @param errorCode 0 为成功，其它为错误码
     * @param account   设置的账号
     */
    @Override
    public void onDeleteAccountResult(Context context, int errorCode, String account) {

    }

    @Override
    public void onSetAttributeResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteAttributeResult(Context context, int i, String s) {

    }

    @Override
    public void onQueryTagsResult(Context context, int errorCode, String data, String operateName) {

    }

    /**
     * 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击
     *
     * @param context
     * @param message 包含被点击通知的内容
     * @note TPNS 点击消息默认支持点击事件，触发后打开目标界面，如果在 onNotifactionClickedResult 设置跳转操作会与管理台/ API 中指定的自定义跳转冲突，导致自定义的跳转失效。
     */
    @Override
    public void onNotificationClickedResult(Context context, XGPushClickedResult message) {
        if (context == null || message == null) {
            return;
        }
        String text;
        if (message.getActionType() == NotificationAction.clicked.getType()) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            text = "Notification Clicked : " + message;

        } else if (message.getActionType() == NotificationAction.delete.getType()) {
            // 通知被清除啦。。。。
            // APP自己处理通知被清除后的相关动作
            text = "Notification Cleaned : " + message;
        } else {
            text = "Notification error";
        }
        TRTCLogger.d(TAG, text);
    }
}
