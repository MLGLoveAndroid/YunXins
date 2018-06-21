package com.my.engine.common;

import android.app.Application;
import android.graphics.Color;

import com.my.engine.common.clazz.ContactHelper;
import com.my.engine.common.clazz.NimDemoLocationProvider;
import com.my.engine.common.clazz.SessionHelper;
import com.netease.nim.uikit.api.AIUserInfoProvider;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.contact.core.query.PinYin;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.netease.nimlib.sdk.util.NIMUtil;

/**
 * 未来属于自己
 * 作者：MLG
 * QQ:2264280742
 * 2018-4-27 9:20
 */


public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NIMClient.init(this, null, options());
        init();
    }

    private void init() {
        if (NIMUtil.isMainProcess(this)) {

            NimUIKit.init(this);

            SessionHelper.init();

            // 通讯录列表定制初始化
            ContactHelper.init();

            // init pinyin
            PinYin.init(this);
            PinYin.validate();

            // 设置地理位置提供者。如果需要发送地理位置消息，该参数必须提供。如果不需要，可以忽略。
            NimUIKit.setLocationProvider(new NimDemoLocationProvider());
        }
    }

    // 如果返回值为 null，则全部使用默认参数。
    private SDKOptions options() {
        SDKOptions options = new SDKOptions();
        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
        options.statusBarNotificationConfig = config;
        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;
        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
        options.thumbnailSize = 480 / 2;
        return options;
    }

}
