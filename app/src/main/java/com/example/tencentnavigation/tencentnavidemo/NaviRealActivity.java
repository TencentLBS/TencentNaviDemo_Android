package com.example.tencentnavigation.tencentnavidemo;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.map.navi.TencentNaviCallback;
import com.tencent.map.navi.TencentRouteSearchCallback;
import com.tencent.map.navi.car.CarNaviView;
import com.tencent.map.navi.car.CarRouteSearchOptions;
import com.tencent.map.navi.car.TencentCarNaviManager;
import com.tencent.map.navi.data.AttachedLocation;
import com.tencent.map.navi.data.GpsLocation;
import com.tencent.map.navi.data.NaviPoi;
import com.tencent.map.navi.data.NaviTts;
import com.tencent.map.navi.data.RouteData;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class NaviRealActivity extends AppCompatActivity implements View.OnClickListener,EasyPermissions.PermissionCallbacks{

    private static final String TAG = "navisdk";

    /**
     *CarNaviManager
     */
    private TencentCarNaviManager tencentCarNaviManager;
    private CarNaviView carNaviView;

    private TencentLocationManager locationManager = null;

    private Button stopBtn;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);

        //所要申请的权限
        String[] perms = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (EasyPermissions.hasPermissions(this, perms)) {//检查是否获取该权限
            Log.i(TAG, "已获取权限");
        } else {
            //第二个参数是被拒绝后再次申请该权限的解释
            //第三个参数是请求码
            //第四个参数是要申请的权限
            EasyPermissions.requestPermissions(this, "必要的权限", 0, perms);
        }
        initManager();
        initRoute();
    }

    private void initManager(){

        stopBtn = findViewById(R.id.navi_stop);
        //实例化TencentCarNaviManager
        tencentCarNaviManager = new TencentCarNaviManager(this);
        carNaviView = findViewById(R.id.carnaviview);

        //添加默认导航面板协议CarnaviView
        tencentCarNaviManager.addNaviView(carNaviView);

        //设置导航回调
        tencentCarNaviManager.setNaviCallback(tencentNaviCallback);

        //停止
        stopBtn.setOnClickListener(this);
    }

    private void initRoute(){
        //定义起终点
        NaviPoi start = new NaviPoi(39.984110, 116.307590);
        NaviPoi dest = new NaviPoi(39.994868, 116.406058);

        //设置途径点
        ArrayList<NaviPoi> wayPoints = new ArrayList<NaviPoi>(); // 途经点
        wayPoints.add(new NaviPoi(39.994169,116.381199));//添加第一个途径点
        wayPoints.add(new NaviPoi(39.994926,116.394138));//添加第二个途径点

        //算路配置
        CarRouteSearchOptions options = CarRouteSearchOptions.create();
        options = options.avoidHighway(true);//设置不走高速

        //发起路线规划

        try {
            tencentCarNaviManager.searchRoute(start, dest, wayPoints, options, routeSearchCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.navi_stop:
                tencentCarNaviManager.stopNavi();
                break;
        }
    }

    /**
     * 获取GPS信息
     * @param context
     * @return
     */
    private int enableGps(Context context) {
        TencentLocationRequest locationRequest = TencentLocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_NAME);
        locationManager = TencentLocationManager.getInstance(context);
        int error = locationManager.requestLocationUpdates(locationRequest, tencentLocationListener);
        Log.e(TAG, "enableGps error: " + error);
        return error;
    }

    //腾讯位置监听,给导航SDK传递GPS信息
    private TencentLocationListener tencentLocationListener = new TencentLocationListener() {
        @Override
        public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
            if(tencentCarNaviManager!=null) {
                tencentCarNaviManager.updateLocation(convertToGpsLocation(tencentLocation), i, s);
            }
        }

        @Override
        public void onStatusUpdate(String s, int i, String s1) {
            if(tencentCarNaviManager!=null){
                tencentCarNaviManager.updateGpsStatus(s, i, s1);
            }

        }
    };

    /**
     * 算路回调
     */
    private TencentRouteSearchCallback routeSearchCallback = new TencentRouteSearchCallback() {
        @Override
        public void onRouteSearchFailure(int i, String s) {

        }

        @Override
        public void onRouteSearchSuccess(ArrayList<RouteData> arrayList) {
            //开启导航
            if(enableGps(NaviRealActivity.this) == 0){
                try {
                    tencentCarNaviManager.startNavi(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                Log.e(TAG, "can't start gps!!!");
            }

        }
    };

    /**
     * 导航回调
     */
    private TencentNaviCallback tencentNaviCallback = new TencentNaviCallback() {
        @Override
        public void onStartNavi() {

        }

        @Override
        public void onStopNavi() {

        }

        @Override
        public void onOffRoute() {

        }

        @Override
        public void onRecalculateRouteSuccess(int i, ArrayList<RouteData> arrayList) {

        }

        @Override
        public void onRecalculateRouteFailure(int i, int i1, String s) {

        }

        @Override
        public void onRecalculateRouteStarted(int i) {

        }

        @Override
        public void onRecalculateRouteCanceled() {

        }

        @Override
        public int onVoiceBroadcast(NaviTts naviTts) {
            return 0;
        }

        @Override
        public void onArrivedDestination() {

        }

        @Override
        public void onPassedWayPoint(int i) {

        }

        @Override
        public void onUpdateRoadType(int i) {

        }

        @Override
        public void onUpdateAttachedLocation(AttachedLocation attachedLocation) {

        }
    };

    /**
     * 坐标转化
     * @param tencentLocation
     * @return
     */
    public static GpsLocation convertToGpsLocation(TencentLocation tencentLocation) {
        if (tencentLocation == null) {
            return null;
        }
        GpsLocation location = new GpsLocation();
        location.setDirection(tencentLocation.getBearing());
        location.setAccuracy(tencentLocation.getAccuracy());
        location.setLatitude(tencentLocation.getLatitude());
        location.setLongitude(tencentLocation.getLongitude());
        location.setAltitude(tencentLocation.getAltitude());
        location.setProvider(tencentLocation.getProvider());
        location.setVelocity(tencentLocation.getSpeed());
        location.setTime(tencentLocation.getTime());

        return location;
    }

    /**
     * 地图生命周期管理
     */
    @Override
    protected void onStart() {
        if(carNaviView!=null){
            carNaviView.onStart();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(carNaviView!=null){
            carNaviView.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        if(carNaviView!=null){
            carNaviView.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(carNaviView!=null){
            carNaviView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(carNaviView!=null){
            carNaviView.onDestroy();
        }
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}
