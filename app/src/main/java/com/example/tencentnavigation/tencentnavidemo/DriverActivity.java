package com.example.tencentnavigation.tencentnavidemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.tencent.map.locussynchro.TencentLocusSynchro;
import com.tencent.map.locussynchro.model.DriverSynchroOptions;
import com.tencent.map.locussynchro.model.Order;
import com.tencent.map.locussynchro.model.RouteUploadError;
import com.tencent.map.locussynchro.model.SyncData;
import com.tencent.map.locussynchro.model.SynchroLocation;
import com.tencent.map.locussynchro.model.SynchroRoute;
import com.tencent.map.navi.DayNightModeChangeCallback;
import com.tencent.map.navi.INaviView;
import com.tencent.map.navi.TencentNaviCallback;
import com.tencent.map.navi.TencentRouteSearchCallback;
import com.tencent.map.navi.car.CarNaviView;
import com.tencent.map.navi.car.CarRouteSearchOptions;
import com.tencent.map.navi.car.TencentCarNaviManager;
import com.tencent.map.navi.data.AttachedLocation;
import com.tencent.map.navi.data.NaviPoi;
import com.tencent.map.navi.data.NaviTts;
import com.tencent.map.navi.data.NavigationData;
import com.tencent.map.navi.data.RouteData;
import com.tencent.map.navi.data.TrafficItem;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class DriverActivity extends Activity {
    private final static String TAG = "driver";
    private TencentMap mMap;
    private Marker passenger;
    private Marker markerStart = null;
    private Marker markerEnd = null;
    private Marker markerCenter = null;// 底图中心固定的marker
    private ArrayList<LatLng> extraPoints = new ArrayList<>();

    private TencentCarNaviManager tencentCarNaviManager = null;
    private CarNaviView carNaviView;
    private CarRouteSearchOptions options = CarRouteSearchOptions.create();
    private LatLng start = new LatLng(39.896532,116.321077);  // 39.983964, 116.308619  36.075543,120.117001
    private LatLng end = new LatLng(39.984072,116.307780);   // 39.985021, 116.308452  36.095586,120.132321

    private TencentLocusSynchro tencentLocusSynchro;
    private String orderId = "";
    private int orderStatus = Order.STATUS_CHARGING_STARTED;
    private String driverId = "";
    private int driverStatus = Order.DRIVER_STATUS_SERVING;
    private boolean enable = true;
    private String routeId = "100000";

    private ArrayList<com.tencent.map.locussynchro.model.LatLng> routePoints = new ArrayList<>();
    private Order order = new Order(orderId, orderStatus);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        initTencentNavigation();
        initMapSetting();
        initLocusSynchro();
    }

    private void initTencentNavigation() {
        carNaviView = findViewById(R.id.naviview);
        mMap = carNaviView.getMap();

        tencentCarNaviManager = new TencentCarNaviManager(getApplicationContext());
        tencentCarNaviManager.setNaviCallback(navigationCallback);
        tencentCarNaviManager.addNaviView(carNaviView);
        CustomNaviView customNaviView = new CustomNaviView();
        tencentCarNaviManager.addNaviView(customNaviView);

        carNaviView.setDayNightModeChangeCallback(new DayNightModeChangeCallback() {
            @Override
            public void onDayNightModeChanged(boolean b) {
                Log.e(TAG, "onDayNightModeChanged:" + b);
            }
        });
        carNaviView.setNaviFixingProportion3D(0.76f, 0.76f);
        carNaviView.setNaviFixingProportion2D(0.24f, 0.24f);
        carNaviView.setBounceTime(Integer.MAX_VALUE);
    }

    private void initMapSetting() {
        addCenterMarker();
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new TencentMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                if (markerEnd != null) {
                    markerEnd.remove();
                    markerEnd = null;
                }
                markerEnd = mMap.addMarker(new MarkerOptions(arg0).anchor(0.5f, 1)
                        .icon(BitmapDescriptorFactory.fromAsset("navi_icon_goal.png")));
                markerEnd.setInfoWindowEnable(false);
                end = arg0;
            }
        });
        mMap.setOnMapLongClickListener(new TencentMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng arg0) {
                if (!tencentCarNaviManager.isNavigating()) {
                    if (markerStart != null) {
                        markerStart.remove();
                        markerStart = null;
                    }
                    markerStart = mMap.addMarker(new MarkerOptions(arg0).anchor(0.5f, 1)
                            .icon(BitmapDescriptorFactory.fromAsset("navi_icon_start.png")));
                    markerStart.setInfoWindowEnable(false);
                    start = arg0;
                } else {
                    mMap.addMarker(new MarkerOptions(arg0).icon(BitmapDescriptorFactory.fromAsset("ORANGE.png")));
                    extraPoints.add(arg0);
                    carNaviView.updateExtraPointsInVisibleRegion(extraPoints);
                }
            }
        });
    }

    private void initLocusSynchro() {
        DriverSynchroOptions driverOptions = new DriverSynchroOptions();
        driverOptions.setAccountId(driverId);
        tencentLocusSynchro = new TencentLocusSynchro(this, driverOptions);
//        if (passenger == null) {
//            passenger = mMap.addMarker(new MarkerOptions(mMap.getCameraPosition().target)
//                    .icon(BitmapDescriptorFactory.fromAsset("passenger.png")));
//            passenger.showInfoWindow();
//        }
    }

    private void addCenterMarker() {
        markerCenter = mMap.addMarker(new MarkerOptions(mMap.getCameraPosition().target)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cross)).anchor(0.5f, 0.5f));
        markerCenter.setInfoWindowEnable(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                markerCenter.setFixingPoint(mMap.getMapWidth()/2, mMap.getMapHeight()/2);
            }
        }, 5000);
    }

    public void startSynchro(View view) {
        try {
            tencentLocusSynchro.start(new TencentLocusSynchro.DataSyncListener() {

                @Override
                public Order onOrderInfoSynchro() {
                    Log.d(TAG, "onOrderInfoSynchro");
                    Order order_temp = new Order();
                    order_temp.setOrderId(orderId);
                    order_temp.setOrderStatus(orderStatus);
                    return order_temp;
                }
                @Override
                public void onSyncDataUpdated(SyncData syncData) {
                    if (syncData.getLocations().size() > 0) {
                        SynchroLocation last = syncData.getLocations().get(syncData.getLocations().size() - 1);
                        if (passenger == null) {
                            passenger = mMap.addMarker(new MarkerOptions(new LatLng(last.getLatitude(), last.getLongitude()))
                                    .icon(BitmapDescriptorFactory.fromAsset("passenger.png")));
                            //passenger.showInfoWindow();
                        } else {
                            passenger.setPosition(new LatLng(last.getLatitude(), last.getLongitude()));
                            passenger.setRotation(last.getDirection());
                        }
                        passenger.setTitle("distance:" + syncData.getOrder().getDistance() + " time:" +
                                syncData.getOrder().getTime());
                        Log.d(TAG, "size:" + syncData.getLocations().size() + " provider:" + last.getProvider());
                    } else {
                        Log.e(TAG, "收到定位点为空");
                    }
                    Log.d(TAG, "orderId:" + syncData.getOrder().getOrderId() + " orderStatus:" +
                            syncData.getOrder().getOrderStatus() + " routeId:" +
                            syncData.getOrder().getRouteId() + " driverStatus:" +
                            syncData.getOrder().getDriverStatus() + " distance:" +
                            syncData.getOrder().getDistance() + " leftDistance:" +
                            syncData.getOrder().getLeftDistance() + " time:" +
                            syncData.getOrder().getTime() + " leftTime:" +
                            syncData.getOrder().getLeftTime());
                }

                @Override
                public void onRouteUploadFailed(RouteUploadError error) {
                    Log.e(TAG, "onRouteUploadFailed:" + error.getErrorMsg() + ", errorCode:" + error.getErrorCode());
                }

                @Override
                public void onRouteUploadComplete() {
                    Log.e(TAG, "onRouteUploadComplete");
                }

                @Override
                public void onLocationUploadFailed(RouteUploadError routeUploadError) {

                }

                @Override
                public void onLocationUploadComplete() {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "startSynchro", e);
        }
        Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();
    }

    public void closeSynchro(View view) {
        tencentLocusSynchro.stop();
        Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();
    }

    public void updateRoute(View view) {
        SynchroRoute synchroRoute = new SynchroRoute();
        synchroRoute.setRouteId(routeId);
        synchroRoute.setRoutePoints(routePoints);
        tencentLocusSynchro.updateRoute(synchroRoute, order);//new Order(orderId, orderStatus));
    }

    public void internalMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.internal, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.time_1:
                        tencentLocusSynchro.setLocusSyncInterval(1);
                        break;

                    case R.id.time_2:
                        tencentLocusSynchro.setLocusSyncInterval(2);
                        break;

                    case R.id.time_3:
                        tencentLocusSynchro.setLocusSyncInterval(3);
                        break;

                    case R.id.time_5:
                        tencentLocusSynchro.setLocusSyncInterval(5);
                        break;

                    case R.id.time_10:
                        tencentLocusSynchro.setLocusSyncInterval(10);
                        break;

                    case R.id.time_20:
                        tencentLocusSynchro.setLocusSyncInterval(20);
                        break;

                    case R.id.time_55:
                        tencentLocusSynchro.setLocusSyncInterval(55);
                        break;

                    case R.id.time_1200:
                        tencentLocusSynchro.setLocusSyncInterval(1200);
                        break;
                }
                return false;
            }

        });
        popup.show();
    }

    public void setSyncEnable(View view) {
        Toast.makeText(this, "enabled:" + (enable ? "YES" : "NO"), Toast.LENGTH_SHORT).show();
        tencentLocusSynchro.setSyncEnabled(enable);
        enable = !enable;
    }

    public void reCalculateRoute(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.recalculate, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.type_1:
                        tencentCarNaviManager.changeNaviRoute(1);
                        break;

                    case R.id.type_2:
                        tencentCarNaviManager.changeNaviRoute(2);
                        break;

                    case R.id.type_3:
                        tencentCarNaviManager.changeNaviRoute(3);
                        break;
                }
                return false;
            }

        });
        popup.show();
    }

    public void resume(View button) {
        tencentCarNaviManager.stopNavi();
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        mMap.moveCamera(CameraUpdateFactory.rotateTo(0, 0));
        addCenterMarker();
    }

    public void calculateRoute(View button) {
        NaviPoi from = new NaviPoi(start.latitude, start.longitude);
        NaviPoi to = new NaviPoi(end.latitude, end.longitude);

        // 途经点：乘客位置
        ArrayList<NaviPoi> wayPoints = null;//new ArrayList<NaviPoi>();
        if (passenger != null) {
            LatLng position = passenger.getPosition();
            NaviPoi wayPoint = new NaviPoi(position.latitude, position.longitude);
            wayPoints = new ArrayList<NaviPoi>();
            wayPoints.add(wayPoint);
        }

        try {
            tencentCarNaviManager.searchRoute(from, to, wayPoints, options, searchRouteCallback);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    public void startNavi(View button) {
        try {
            tencentCarNaviManager.startNavi(0);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        carNaviView.setNaviPanelEnabled(true);
    }

    public void simulateNavi(View button) {
        try {
            tencentCarNaviManager.startSimulateNavi(0);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    public void stopNavi(View button) {
        tencentCarNaviManager.stopNavi();
        mMap.clear();
        carNaviView.setNaviPanelEnabled(false);
    }

    private TencentRouteSearchCallback searchRouteCallback = new TencentRouteSearchCallback() {
        @Override
        public void onRouteSearchFailure(int errorCode, String errorMessage) {
            Toast.makeText(getApplicationContext(), "算路失败", Toast.LENGTH_LONG).show();
            Log.e(TAG,"失败原因:" + errorMessage);
            Toast.makeText(getApplicationContext(), "错误分类:" + errorCode  + "\n错误信息:"
                    + errorMessage, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRouteSearchSuccess(ArrayList<RouteData> route) {
            Toast.makeText(getApplicationContext(), "算路成功", Toast.LENGTH_LONG).show();
            if (route != null) {
                RouteData naviRoute = route.get(0);
                for (int i = 0; i < route.size(); i++) {
                    mMap.addPolyline(new PolylineOptions().addAll(route.get(i).getRoutePoints()).color(i));
                    Log.d(TAG, "推荐理由:" + route.get(i).getRecommendMsg());
                    Log.d(TAG, "时间:" + route.get(i).getTime() + "  距离:" + route.get(i).getDistanceInfo());
                    Log.d(TAG, "封路:" + route.get(i).getCloseInfo() + "  限行:" + route.get(i).getLimitInfo());
                }
                LatLngBounds bounds = new LatLngBounds(start, end).builder().include(naviRoute.getRoutePoints()).build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                // 司乘同显
                routePoints.clear();
                List<LatLng> points = naviRoute.getRoutePoints();
                for (int i = 0; i < points.size(); i++) {
                    routePoints.add(new com.tencent.map.locussynchro.model.LatLng(points.get(i).latitude, points.get(i).longitude));
                }
                routeId = naviRoute.getRouteId();
                order.setRouteId(routeId);
                SynchroRoute synchroRoute = new SynchroRoute();
                synchroRoute.setRouteId(routeId);
                synchroRoute.setRoutePoints(routePoints);
                tencentLocusSynchro.updateRoute(synchroRoute, order);//new Order(orderId, orderStatus));
            }
        }
    };


    private TencentNaviCallback navigationCallback = new TencentNaviCallback() {

        @Override
        public void onArrivedDestination() {
            String msg = "到达目的地";
//            Log.d(Configs.TAG, msg);
            tencentCarNaviManager.stopNavi();
        }

        @Override
        public void onPassedWayPoint(int i) {
            String msg = "onPassedWayPoint:" + i;
//            Log.d(Configs.TAG, msg);
        }

        @Override
        public void onUpdateRoadType(int roadType) {
            String msg = "onUpdateRoadType:" + roadType;
            Log.d(TAG, msg);
        }

        @Override
        public void onUpdateAttachedLocation(AttachedLocation attachedLocation) {
            Log.e(TAG,"更新吸附点");
            SynchroLocation location = new SynchroLocation();
            location.setTime(attachedLocation.getTime());
            location.setLatitude(attachedLocation.getLatitude());
            location.setLongitude(attachedLocation.getLongitude());
            location.setAttachedLatitude(attachedLocation.getAttachedLatitude());
            location.setAttachedLongitude(attachedLocation.getAttachedLongitude());
            location.setAccuracy(attachedLocation.getAccuracy());
            location.setDirection(attachedLocation.getRoadDirection());
            location.setVelocity(attachedLocation.getVelocity());
            location.setProvider(attachedLocation.getProvider());
            tencentLocusSynchro.updateLocation(location, order);
        }

        @Override
        public void onFollowRouteClick(String s, ArrayList<LatLng> arrayList) {

        }

        @Override
        public void onStartNavi() {
            String msg = "onStartNavi";
            Log.d(TAG, msg);
        }

        @Override
        public void onStopNavi() {
            String msg = "onStopNavi";
            Log.d(TAG, msg);
        }

        @Override
        public void onOffRoute() {
            String msg = "onOffRoute";
            Log.d(TAG, msg);
        }

        @Override
        public void onRecalculateRouteSuccess(int type, ArrayList<RouteData> routeDataList) {
            String msg = "onOffRouteSearchSuccess";
            Log.d(TAG, msg);

            Toast.makeText(getApplicationContext(), "重新算路成功", Toast.LENGTH_LONG).show();
            if (routeDataList != null) {
                for (int i=0; i<routeDataList.size(); i++) {
                    Log.d(TAG, "推荐理由:" + routeDataList.get(i).getRecommendMsg());
                    Log.d(TAG, "时间:" + routeDataList.get(i).getTime() + "  距离:" + routeDataList.get(i).getDistanceInfo());
                    Log.d(TAG, "封路:" + routeDataList.get(i).getCloseInfo() + "  限行:" + routeDataList.get(i).getLimitInfo());
                    Log.d(TAG, "数据状态:" + routeDataList.get(i).getDataStatus());
                    if (routeDataList.get(i).getRoutePoints() != null) {
                        Log.d(TAG, "点串:" + routeDataList.get(i).getRoutePoints().size());
                    }
                }
                // 司乘同显
                routePoints.clear();
                for (int i=0; i<routeDataList.get(0).getRoutePoints().size(); i++) {
                    routePoints.add(new com.tencent.map.locussynchro.model.LatLng(routeDataList.get(0).getRoutePoints().get(i).latitude,
                            routeDataList.get(0).getRoutePoints().get(i).longitude));
                }
                routeId = routeDataList.get(0).getRouteId();
                order.setRouteId(routeId);
                SynchroRoute synchroRoute = new SynchroRoute();
                synchroRoute.setRouteId(routeId);
                synchroRoute.setRoutePoints(routePoints);
                tencentLocusSynchro.updateRoute(synchroRoute, order);
            }
        }

        @Override
        public void onRecalculateRouteFailure(int type, int errorCode, String errorMessage) {
            String msg = "onRecalculateRouteFailure:" + type + "  " + errorCode + "  " + errorMessage;
            Log.d(TAG, msg);
        }

        @Override
        public void onRecalculateRouteStarted(int type) {
            String msg = "onRecalculateRouteStarted:" + type;
            Log.d(TAG, msg);
        }

        @Override
        public void onRecalculateRouteCanceled() {
            String msg = "onRecalculateRouteCanceled";
            Log.d(TAG, msg);
        }
        @Override
        public int onVoiceBroadcast(final NaviTts tts) {
//            TtsHelper.getInstance().read(tts, DriverActivity.this);
//            Log.e(Configs.TAG, "语音播报文案：" + tts.getText());
            return 1;
        }
    };

    @Override
    protected void onStart() {
        if (carNaviView != null) {
            carNaviView.onStart();
        }
        super.onStart();
    }

    @Override
    protected void onRestart() {
        if (carNaviView != null) {
            carNaviView.onRestart();
        }
        super.onRestart();
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
    protected void onStop() {
        if (carNaviView != null) {
            carNaviView.onStop();
        }
        super.onStop();
    }

    /**
     * 销毁导航实例
     */
    @Override
    protected void onDestroy() {
        tencentCarNaviManager.stopSimulateNavi();
        tencentCarNaviManager.stopNavi();
        tencentCarNaviManager.removeAllNaviViews();
        if (carNaviView != null) {
            carNaviView.onDestroy();
        }
        super.onDestroy();
    }

    class CustomNaviView implements INaviView {

        @Override
        public void onShowEnlargedIntersection(Bitmap bitmap) {
            String msg = "onShowEnlargedIntersection";
//            Log.d(Configs.TAG, msg);
        }

        @Override
        public void onHideEnlargedIntersection() {
            String msg = "onHideEnlargedIntersection";
//            Log.d(Configs.TAG, msg);
        }

        @Override
        public void onShowGuidedLane(Bitmap bitmap) {
            String msg = "onShowGuidedLane";
//            Log.d(Configs.TAG, msg);
        }

        @Override
        public void onHideGuidedLane() {
            String msg = "onHideGuidedLane";
//            Log.d(Configs.TAG, msg);
        }

        @Override
        public void onUpdateTraffic(String s, int i, int i1, ArrayList<LatLng> arrayList, ArrayList<TrafficItem> arrayList1, boolean b) {

        }

        @Override
        public void onGpsRssiChanged(int i) {
            String msg = "onGpsRssiChanged:" + i;
//            Log.d(Configs.TAG, msg);
        }

        @Override
        public void onUpdateNavigationData(NavigationData navigationData) {
            order.setDriverStatus(driverStatus);
            order.setLeftDistance(navigationData.getLeftDistance());
            order.setLeftTime(navigationData.getLeftTime());
        }
    }
}
