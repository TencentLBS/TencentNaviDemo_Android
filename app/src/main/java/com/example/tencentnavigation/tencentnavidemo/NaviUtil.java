package com.example.tencentnavigation.tencentnavidemo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import android.text.TextUtils;

import java.util.Locale;

/**
 * All rights Reserved, Designed By lbs.qq.com
 *
 * @version V5.0.0
 * @Description:导航工具类
 * @author: daxiazhang
 * @date: 2018/3/29
 * @Copyright: 2018 tencent Inc. All rights reserved.
 */

public class NaviUtil {

    private static final String SDK_KEY_OLD = "TencentMapSDK";
    private static final String SDK_KEY = "com.tencent.map.api_key";
    private static final String IS_KEY_VALID = "IsKeyValid";
    private static final String USER_ID = "UserId";
    private static final String CACHE_EXPIRE = "CacheExpire";
    private static final String EDIT_TIME = "EditTime";
    private static final String TAG = "NaviUtil";

    public static String distance2string(int distance, boolean useKilometers) {
        if (distance < 1000 && !useKilometers) {
            return distance + "米";
        }
        String disStr = String.format(Locale.getDefault(), "%.1f", ((double) distance / 1000));
        if (disStr.endsWith(".0")) {
            disStr = disStr.substring(0, disStr.length() - 2);
        }
        return disStr + "公里";
    }

    public static String time2string(int minutes) {
        if (minutes <= 60) {
            return minutes + "分钟";
        }
        int hours = minutes / 60;
        minutes = minutes % 60;
        if (minutes <= 0) {
            minutes = 1;
        }
        return hours + "小时" + minutes + "分钟";
    }

    public static String getAuthKey(Context context) {
        if (context == null) {
            return "";
        }
        String key = "";
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(),
                    PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                key = appInfo.metaData.getString(SDK_KEY);
            }
            if (TextUtils.isEmpty(key) && appInfo.metaData != null) {
                //为了向下兼容
                key = appInfo.metaData.getString(SDK_KEY_OLD);
            }
        } catch (Exception e) {
        }
        return key;
    }

}
