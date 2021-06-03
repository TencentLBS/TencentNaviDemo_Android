package com.example.tencentnavigation.tencentnavidemo;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tencent.map.navi.TencentRouteSearchCallback;
import com.tencent.map.navi.data.NaviPoi;
import com.tencent.map.navi.data.RouteData;
import com.tencent.map.navi.ui.car.CarNaviInfoPanel;
import com.tencent.map.navi.walk.TencentWalkNaviManager;
import com.tencent.map.navi.walk.WalkNaviView;

import java.util.ArrayList;

public class NaviWalkActivity extends AppCompatActivity {

    // 算路的起点/终点
    NaviPoi mfrom = new NaviPoi(40.041032,116.27245);
    NaviPoi mDest = new NaviPoi(39.868699,116.32198);

    private TencentWalkNaviManager mWalkManager;
    private WalkNaviView mWalkNaviView;

    private CarNaviInfoPanel.OnNaviInfoListener onNaviInfoListener
            = () -> stopNavi();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navi_walk);

        mWalkNaviView = findViewById(R.id.walk_navi_view);

        mWalkManager = new TencentWalkNaviManager(getApplicationContext());

        // 开始算路
        calculateRoute();

    }

    public void calculateRoute() {

        try {
            mWalkManager.searchRoute(mfrom, mDest
                    , new TencentRouteSearchCallback() {

                        @Override
                        public void onRouteSearchFailure(int errorCode, String errorMessage) {
                            Toast.makeText(NaviWalkActivity.this
                                    , "算路失败！！", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onRouteSearchSuccess(ArrayList<RouteData> route) {
                            Toast.makeText(NaviWalkActivity.this
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

        mWalkManager.setInternalTtsEnabled(true); // 开启播报
        mWalkManager.addTencentNaviListener(mWalkNaviView);

        // 开始模拟导航
        try {
            mWalkManager.startSimulateNavi(0);

        } catch (Exception e) {

        }
    }

    public void stopNavi() {
        if (mWalkManager != null) {
            mWalkManager.stopNavi();
            clearNaviView();

            mWalkManager.removeTencentNaviListener(mWalkNaviView);
        }
    }

    private void initNaviView() {
        if (mWalkManager == null)
            return;

        // 导航面板
        mWalkNaviView.setNaviPanelEnabled(true);
        mWalkNaviView.setNaviPanelEnabled(true);

        // 开启默认UI
        CarNaviInfoPanel topNaviInfo = mWalkNaviView.showNaviInfoPanel();
        topNaviInfo.setOnNaviInfoListener(onNaviInfoListener);
    }

    private void clearNaviView() {
        // 路线元素
        mWalkNaviView.clearAllRouteUI();
        mWalkNaviView.setNaviPanelEnabled(false);
        // 默认ui
        mWalkNaviView.hideNaviInfoPanel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWalkNaviView != null) {
            mWalkNaviView.onDestroy();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mWalkNaviView != null) {
            mWalkNaviView.onStart();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mWalkNaviView != null) {
            mWalkNaviView.onRestart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWalkNaviView != null) {
            mWalkNaviView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWalkNaviView != null) {
            mWalkNaviView.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mWalkNaviView != null) {
            mWalkNaviView.onStop();
        }
    }
}
