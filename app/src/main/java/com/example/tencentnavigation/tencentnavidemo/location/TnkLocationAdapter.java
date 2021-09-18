package com.example.tencentnavigation.tencentnavidemo.location;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.example.tencentnavigation.tencentnavidemo.util.Singleton;
import com.tencent.map.fusionlocation.TencentLocationAdapter;
import com.tencent.map.fusionlocation.model.TencentGeoLocation;
import com.tencent.map.fusionlocation.model.TencentGnssInfo;
import com.tencent.map.fusionlocation.observer.TencentGeoLocationObserver;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.map.geolocation.internal.TencentExtraKeys;
import com.tencent.map.geolocation.routematch.bean.init.LocationPreference;
import com.tencent.map.navi.TencentNavi;

import java.util.ArrayList;

public class TnkLocationAdapter {
    private static final String TAG = "[tnklocation]";
    private Context mContext;
    private TencentLocationAdapter mTencentLocationAdapter;
    private ArrayList<IGeoLocationListeners> geoLists = new ArrayList<>();

    private TencentGeoLocationObserver mTencentGeoLocationObserver = new TencentGeoLocationObserver() {
        @Override
        public void onGeoLocationChanged(TencentGeoLocation tencentGeoLocation) {
            for (TencentLocationListener listener: mTencentLocationListeners) {
                listener.onLocationChanged(tencentGeoLocation.getLocation(),
                        tencentGeoLocation.getStatus(),
                        tencentGeoLocation.getReason());
            }
            for (IGeoLocationListeners listener : geoLists) {
                listener.onGeoLocationChanged(tencentGeoLocation);
            }
        }

        @Override
        public void onNmeaMsgChanged(String s) {

        }

        @Override
        public void onGNSSInfoChanged(TencentGnssInfo tencentGnssInfo) {

        }
    };

    private ArrayList<TencentLocationListener> mTencentLocationListeners = new ArrayList<>();

    // 单例方法
    public static final Singleton<TnkLocationAdapter> mTnkLocationSingleton =
            new Singleton<TnkLocationAdapter>() {
                @Override
                protected TnkLocationAdapter create() {
                    return new TnkLocationAdapter();
                }
            };

    // 设置context，需要先设置
    public void setContext(Context context) {
        this.mContext = context;
    }

    // 开始定位
    public void startTNKLocationAdapter() {
        if (null != mTencentLocationAdapter) {
            return;
        }
        // 1.配置全局Context
        TencentExtraKeys.setContext(mContext);

        // 2.配置设备ID
        Pair<String, String> deviceID = new Pair<String, String>(TencentLocationAdapter.TYPE_QIMEI,
                TencentNavi.getDeviceId(mContext));
        TencentLocationAdapter.setDeviceId(mContext, deviceID);

        try {
            // 3.获取大定位实例
            mTencentLocationAdapter = TencentLocationAdapter.getInstance(mContext);

            // 注意，这里启动了普适定位，即客户端希望从大定位获取原始位置信号提供吸附使用
            TencentLocationRequest request = TencentLocationRequest.create()
                    .setInterval(1000) // 频率1s
                    .setAllowGPS(true) // 关闭省电省流量
                    .setAllowDirection(true)
                    .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA);
            mTencentLocationAdapter.startIndoorLocation();
            mTencentLocationAdapter.startCommonLocation(request,
                    LocationPreference.PLATFORM_AUTO);
            // 4. 普适定位添加listener
            mTencentLocationAdapter.addLocationObserver(mTencentGeoLocationObserver);

        } catch (Exception ex) {
            Log.w(TAG,"initLocation: exception happened: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    // 结束定位
    public void stopTNKLocationAdapter() {
        mTencentLocationAdapter.stopCommonLocation();
        mTencentLocationAdapter.destroyAdapter();
        removeAllLocationListener();
        mTencentLocationAdapter = null;
    }

    // 添加listener
    public void addLocationListener(TencentLocationListener tencentLocationListener) {
        if (tencentLocationListener != null && !mTencentLocationListeners.contains(tencentLocationListener))
            mTencentLocationListeners.add(tencentLocationListener);
    }

    // 移除 all listener
    public void removeAllLocationListener() {
            mTencentLocationListeners.clear();
    }

    // 移除 listener
    public void removeLocationListener(TencentLocationListener tencentLocationListener) {
        if (tencentLocationListener != null && mTencentLocationListeners.indexOf(tencentLocationListener) != -1){
            mTencentLocationListeners.remove(tencentLocationListener);
        }
    }

    public void addGeoLocationListener(IGeoLocationListeners listener) {
        if (null != listener && !geoLists.contains(listener)) {
            geoLists.add(listener);
        }
    }

    public void removeGeoLocationListener(IGeoLocationListeners listener) {
        if (null != listener && -1 != geoLists.indexOf(listener)) {
            geoLists.remove(listener);
        }
    }

    public interface IGeoLocationListeners {
        void onGeoLocationChanged(TencentGeoLocation tencentGeoLocation);
    }
}
