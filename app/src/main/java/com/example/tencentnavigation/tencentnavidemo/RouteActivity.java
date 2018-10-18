package com.example.tencentnavigation.tencentnavidemo;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.tencent.map.navi.TencentRouteSearchCallback;
import com.tencent.map.navi.car.CarRouteSearchOptions;
import com.tencent.map.navi.car.TencentCarNaviManager;
import com.tencent.map.navi.data.NaviPoi;
import com.tencent.map.navi.data.RouteData;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.SupportMapFragment;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.UiSettings;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.Polyline;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * 路线规划
 */
public class RouteActivity extends AppCompatActivity {

    private TencentCarNaviManager carNaviManager;
    private TencentMap tencentMap;
    //定义起终点
    private NaviPoi start;
    private NaviPoi dest;

    //设置途径点
    private ArrayList<NaviPoi> wayPoints; // 途经点

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        initmap();
        initRoute();
    }

    /**
     * 初始化地图
     * 使用SupportMapFragment加载地图
     */
    private void initmap(){
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)fm.findFragmentById(R.id.map_frag);
        tencentMap = mapFragment.getMap();

    }

    /**
     * 请求路线
     */

    private void initRoute(){
        carNaviManager = new TencentCarNaviManager(this);
        //定义起终点
        start = new NaviPoi(39.984110, 116.307590);
        dest = new NaviPoi(39.994868, 116.406058);

        //设置途径点
        wayPoints = new ArrayList<NaviPoi>(); // 途经点
        wayPoints.add(new NaviPoi(39.994169,116.381199));//添加第一个途径点
        wayPoints.add(new NaviPoi(39.994926,116.394138));//添加第二个途径点

        //算路配置
        CarRouteSearchOptions options = CarRouteSearchOptions.create();
        options = options.avoidHighway(true);//设置不走高速

        //发起路线规划
        try {
            carNaviManager.searchRoute(start, dest, wayPoints, options, routeSearchCallback);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("routePlan:","鉴权异常" + e.getLocalizedMessage());
        }
    }

    /**
     * 算路回调
     */
    private TencentRouteSearchCallback routeSearchCallback = new TencentRouteSearchCallback() {
        @Override
        public void onRouteSearchFailure(int i, String s) {
            //i错误码提示:1001为网络错误，1002为无网络，2001为返回数据无效（或空），2002为起终点参数错误，2003为途经点参数错误，2004为吸附失败，2005为算路失败，2999为服务器内部错误
            Log.e("route","error:"+i);
        }

        @Override
        public void onRouteSearchSuccess(ArrayList<RouteData> arrayList) {
            addRoutes(arrayList);
            zoomToRoute(arrayList.get(0));
            addMarkerStart(start.getLatitude(),start.getLongitude());
            addMarkerDestination(dest.getLatitude(),dest.getLongitude());
            addMarkerPass(wayPoints);
        }
    };

    /**
     * 添加路线及其他marker
     */

    private void addMarkerStart(double latitude, double longitude) {
        LatLng startPoint = new LatLng(latitude, longitude);
        tencentMap.addMarker(new MarkerOptions(startPoint)
                .icon(BitmapDescriptorFactory.fromAsset("navi_marker_start.png"))
                .anchor(0.5f, 1));


    }

    private void addMarkerDestination(double latitude, double longitude) {

        LatLng destinationPoint = new LatLng(latitude, longitude);
        tencentMap.addMarker(new MarkerOptions(destinationPoint)
                .icon(BitmapDescriptorFactory.fromAsset("navi_marker_end.png"))
                .anchor(0.5f, 1));


    }

    private void addMarkerPass(ArrayList<NaviPoi> wayPoints) {

        for (NaviPoi wayPoint : wayPoints) {
            LatLng destinationPoint = new LatLng(wayPoint.getLatitude(), wayPoint.getLongitude());
            tencentMap.addMarker(new MarkerOptions(destinationPoint)
                    .icon(BitmapDescriptorFactory.fromAsset("navi_marker_pass.png"))
                    .anchor(0.5f, 1));
        }
    }

    private void addRoutes(ArrayList<RouteData> mRouteDatas) {

        RouteData routeData = mRouteDatas.get(0);
        PolylineOptions options = new PolylineOptions()
                .addAll(routeData.getRoutePoints())
                .arrow(true);

        options.color(Color.parseColor("#339933"));
        tencentMap.addPolyline(options);

    }
    private void zoomToRoute(RouteData routeData) {
            int marginLeft = getResources().getDimensionPixelSize(R.dimen.navigation_line_margin_left);
            int marginTop = getResources().getDimensionPixelSize(R.dimen.navigation_line_margin_top);
            int marginRight = getResources().getDimensionPixelSize(R.dimen.navigation_line_margin_right);
            int marginBottom = getResources().getDimensionPixelSize(R.dimen.navigation_line_margin_bottom);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            builder.include(routeData.getRoutePoints());
            LatLngBounds bounds = builder.build();
            tencentMap.moveCamera(CameraUpdateFactory.newLatLngBoundsRect(bounds, marginLeft, marginRight, marginTop, marginBottom));

    }
    @Override
    protected void onDestroy() {
        tencentMap = null;
        carNaviManager = null;
        super.onDestroy();
    }
}
