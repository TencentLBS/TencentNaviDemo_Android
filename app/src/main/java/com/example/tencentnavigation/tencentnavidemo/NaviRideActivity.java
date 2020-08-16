package com.example.tencentnavigation.tencentnavidemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tencent.map.navi.TencentRouteSearchCallback;
import com.tencent.map.navi.data.NaviPoi;
import com.tencent.map.navi.data.RouteData;
import com.tencent.map.navi.ride.RideNaviView;
import com.tencent.map.navi.ride.RideRouteSearchOptions;
import com.tencent.map.navi.ride.TencentRideNaviManager;
import com.tencent.map.navi.ui.car.CarNaviInfoPanel;

import java.util.ArrayList;

public class NaviRideActivity extends AppCompatActivity {

    // 算路的起点/终点
    NaviPoi mfrom = new NaviPoi(40.041032,116.27245);
    NaviPoi mDest = new NaviPoi(39.868699,116.32198);

    private TencentRideNaviManager mRideManager;
    private RideNaviView mRideNaviView;

    private CarNaviInfoPanel.OnNaviInfoListener onNaviInfoListener
            = () -> stopNavi();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navi_ride);

        mRideNaviView = findViewById(R.id.ride_navi_view);

        mRideManager = new TencentRideNaviManager(getApplicationContext());

        // 开始算路
        calculateRoute();

    }

    public void calculateRoute() {

        RideRouteSearchOptions rideOption = new RideRouteSearchOptions();
        rideOption.type(0); // 0:自行车，1:电动车

        try {
            mRideManager.searchRoute(mfrom, mDest, rideOption
                    , new TencentRouteSearchCallback() {

                        @Override
                        public void onRouteSearchFailure(int errorCode, String errorMessage) {
                            Toast.makeText(NaviRideActivity.this
                                    , "算路失败！！", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onRouteSearchSuccess(ArrayList<RouteData> route) {
                            Toast.makeText(NaviRideActivity.this
                                    , "算路成功", Toast.LENGTH_SHORT).show();

                            // 开始导航
                            startNavi();
                        }

                    });
        } catch (Exception e) {

        }
    }

    public void startNavi() {
        initNaviView();

        mRideManager.setInternalTtsEnabled(true); // 开启播报
        mRideManager.addTencentNaviListener(mRideNaviView);

        // 开始模拟导航
        try {
            mRideManager.startSimulateNavi(0);

        } catch (Exception e) {

        }
    }

    public void stopNavi() {
        if (mRideManager != null) {
            mRideManager.stopNavi();
            clearNaviView();

            mRideManager.removeTencentNaviListener(mRideNaviView);
        }
    }

    private void initNaviView() {
        if (mRideManager == null)
            return;

        // 导航面板
        mRideNaviView.setNaviPanelEnabled(true);
        mRideNaviView.setNaviPanelEnabled(true);

        // 开启默认UI
        CarNaviInfoPanel topNaviInfo = mRideNaviView.showNaviInfoPanel();
        topNaviInfo.setOnNaviInfoListener(onNaviInfoListener);
    }

    private void clearNaviView() {
        // 路线元素
        mRideNaviView.clearAllRouteUI();
        mRideNaviView.setNaviPanelEnabled(false);
        // 默认ui
        mRideNaviView.hideNaviInfoPanel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRideNaviView != null) {
            mRideNaviView.onDestroy();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mRideNaviView != null) {
            mRideNaviView.onStart();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mRideNaviView != null) {
            mRideNaviView.onRestart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRideNaviView != null) {
            mRideNaviView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRideNaviView != null) {
            mRideNaviView.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRideNaviView != null) {
            mRideNaviView.onStop();
        }
    }
}
