package com.example.tencentnavigation.tencentnavidemo.location;

import android.content.Context;

import com.example.tencentnavigation.tencentnavidemo.util.Singleton;
import com.tencent.map.fusionlocation.model.TencentGeoLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.location.api.GeoLocationObserver;
import com.tencent.map.location.core.FusionGeoLocationAdapter;

import java.util.ArrayList;

public class TnkLocationAdapter {
    private static final String TAG = "[tnklocation]";
    private Context mContext;
    private final ArrayList<IGeoLocationListeners> geoLists = new ArrayList<>();
    private FusionGeoLocationAdapter adapter;

    private final GeoLocationObserver observer = new GeoLocationObserver() {
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
    };


    private final ArrayList<TencentLocationListener> mTencentLocationListeners = new ArrayList<>();

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
        adapter = FusionGeoLocationAdapter.getInstance(mContext);
        adapter.addLocationObserver(observer, 1000);
    }

    // 结束定位
    public void stopTNKLocationAdapter() {
       if (null != adapter) {
           adapter.removeLocationObserver(observer);
       }
        removeAllLocationListener();
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
        if (tencentLocationListener != null){
            mTencentLocationListeners.remove(tencentLocationListener);
        }
    }

    public void addGeoLocationListener(IGeoLocationListeners listener) {
        if (null != listener && !geoLists.contains(listener)) {
            geoLists.add(listener);
        }
    }

    public void removeGeoLocationListener(IGeoLocationListeners listener) {
        if (null != listener) {
            geoLists.remove(listener);
        }
    }

    public interface IGeoLocationListeners {
        void onGeoLocationChanged(TencentGeoLocation tencentGeoLocation);
    }
}
