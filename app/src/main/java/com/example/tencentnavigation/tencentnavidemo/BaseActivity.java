package com.example.tencentnavigation.tencentnavidemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tencent.map.navi.TencentNaviCallback;
import com.tencent.map.navi.TencentRouteSearchCallback;
import com.tencent.map.navi.car.CarNaviView;
import com.tencent.map.navi.car.CarRouteSearchOptions;
import com.tencent.map.navi.car.TencentCarNaviManager;
import com.tencent.map.navi.data.AttachedLocation;
import com.tencent.map.navi.data.NaviPoi;
import com.tencent.map.navi.data.NaviTts;
import com.tencent.map.navi.data.RouteData;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {
    /**
     * 基类
     */
    public TencentCarNaviManager tencentCarNaviManager;
    public CarNaviView carNaviView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);

        this.initManager();
        this.initRoute();

    }

    /**
     * 初始化TencentCarNaviManager，实例化CarNaviView
     */
    private void initManager() {
        //初始化TencentCarNaviManager
        tencentCarNaviManager = new TencentCarNaviManager(this);
        carNaviView = findViewById(R.id.carnaviview);
        //添加默认导航面板协议CarnaviView
        tencentCarNaviManager.addNaviView(carNaviView);

        //设置导航回调
        tencentCarNaviManager.setNaviCallback(tencentNaviCallback);

    }

    /**
     * 设置行驶路线
     */
    private void initRoute() {

        //定义起终点
        NaviPoi start = new NaviPoi(40.042845, 116.275763);
        NaviPoi dest = new NaviPoi(40.05001, 116.28296);

//        //设置途径点
//        ArrayList<NaviPoi> wayPoints = new ArrayList<NaviPoi>(); // 途经点
//        wayPoints.add(new NaviPoi(39.994169, 116.381199));//添加第一个途径点
//        wayPoints.add(new NaviPoi(39.994926, 116.394138));//添加第二个途径点

        //算路配置
        CarRouteSearchOptions options = CarRouteSearchOptions.create();
        options = options.avoidHighway(true);//设置不走高速

        //发起路线规划
        try {
            tencentCarNaviManager.searchRoute(start, dest, null, options, routeSearchCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 算路回调routeSearchCallback
     */
    private TencentRouteSearchCallback routeSearchCallback = new TencentRouteSearchCallback() {
        @Override
        public void onRouteSearchFailure(int i, String s) {
            //算路失败回调
        }

        @Override
        public void onRouteSearchSuccess(ArrayList<RouteData> arrayList) {
            //算路成功回调
            //算路成功后，开启模拟导航
            try {
                tencentCarNaviManager.startSimulateNavi(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 导航回调
     */
    private TencentNaviCallback tencentNaviCallback = new TencentNaviCallback() {
        @Override
        public void onStartNavi() {
            //导航开始回调
        }

        @Override
        public void onStopNavi() {
            //导航结束回调
        }

        @Override
        public void onOffRoute() {
            //偏航回调
        }

        @Override
        public void onRecalculateRouteSuccess(int i, ArrayList<RouteData> arrayList) {

        }

        @Override
        public void onRecalculateRouteFailure(int i, int i1, String s) {
            //路线重新规划失败回调
        }

        @Override
        public void onRecalculateRouteStarted(int i) {
            //路线重新规划开始回调
        }

        @Override
        public void onRecalculateRouteCanceled() {

        }

        @Override
        public int onVoiceBroadcast(NaviTts naviTts) {
            //tts播报回调
            Log.d("navitest", "onVoiceBroadcast" + naviTts.getText());
            return 0;
        }

        @Override
        public void onArrivedDestination() {
            //到达目的地回调
            Log.d("navitest", "onArrivedDestination");
        }

        @Override
        public void onPassedWayPoint(int i) {
            //经过途径点回调
        }

        @Override
        public void onUpdateRoadType(int i) {

        }

        @Override
        public void onUpdateAttachedLocation(AttachedLocation attachedLocation) {

        }

        @Override
        public void onFollowRouteClick(String s, ArrayList<LatLng> arrayList) {

        }
    };

    /**
     * 地图生命周期管理
     */
    @Override
    protected void onStart() {
        if (carNaviView != null) {
            carNaviView.onStart();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (carNaviView != null) {
            carNaviView.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        if (carNaviView != null) {
            carNaviView.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (carNaviView != null) {
            carNaviView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (tencentCarNaviManager.isNavigating()) {
            tencentCarNaviManager.stopSimulateNavi();
        }
        if (carNaviView != null) {
            carNaviView.onDestroy();
        }
        super.onDestroy();
    }

}
