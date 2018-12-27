package com.example.tencentnavigation.tencentnavidemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.map.locussynchro.TencentLocusSynchro;
import com.tencent.map.locussynchro.model.Order;
import com.tencent.map.locussynchro.model.PassengerSynchroOptions;
import com.tencent.map.locussynchro.model.RouteUploadError;
import com.tencent.map.locussynchro.model.SyncData;
import com.tencent.map.locussynchro.model.SynchroLocation;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.Polyline;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;
import com.tencent.tencentmap.mapsdk.vector.utils.animation.MarkerTranslateAnimator;

import java.util.ArrayList;

public class PassengerActivity extends Activity implements TencentLocationListener {
    private final static String TAG = "passenger";

    private MapView mapView;
    private TencentMap mMap;
    private TencentLocationRequest request = TencentLocationRequest.create();
    private TencentLocationManager locationManager = null;
    private TencentLocusSynchro tencentLocusSynchro;
    private String orderId = "";
    private String passengerId = "";
    private int orderStatus = Order.STATUS_CHARGING_STARTED;
    private MarkerTranslateAnimator mTranslateAnimator;
    private Marker driver;
    private Polyline polyline;
    private Marker loc;
    private LatLng lastpoint=null;
    private boolean enable = true;
    private String lastRouteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        setupMapIfNeeded();
        initLocusSynchro();
    }

    public void enableGps(Context context) {
        request.setInterval(1000);
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_GEO);
        //TencentLocationAdapter.setMockGpsEnable(true);
        locationManager = TencentLocationManager.getInstance(context);
        int error = locationManager.requestLocationUpdates(request, this);
        Log.e(TAG, "location error:" + error);
    }

    private void setupMapIfNeeded() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.mapview);
        if (mapView == null) {
            mapView = new MapView(this);
            layout.addView(mapView);
            mMap = mapView.getMap();
        }
    }

    private void initLocusSynchro() {
        PassengerSynchroOptions passengerOptions = new PassengerSynchroOptions();
        passengerOptions.setAccountId(passengerId);
        tencentLocusSynchro = new TencentLocusSynchro(this, passengerOptions);
//        if (driver == null) {
//            driver = mMap.addMarker(new MarkerOptions(mMap.getCameraPosition().target)
//                    .icon(BitmapDescriptorFactory.fromAsset("driver.png")));
//            driver_real = mMap.addMarker(new MarkerOptions(mMap.getCameraPosition().target)
//                    .icon(BitmapDescriptorFactory.fromAsset("driver2.png")));
//        }
    }
    public void startSynchro(View view) {
        try {
            tencentLocusSynchro.start(new TencentLocusSynchro.DataSyncListener() {

                @Override
                public Order onOrderInfoSynchro() {
                    Log.d(TAG, "onOrderInfoSynchro");
                    Order order = new Order();
                    order.setOrderId(orderId);
                    order.setOrderStatus(orderStatus);
                    return order;
                }

                @Override
                public void onSyncDataUpdated(SyncData syncData) {
                    if (syncData.getLocations().size() > 0) {

                        SynchroLocation last = (syncData.getLocations().get((syncData.getLocations().size() - 1)));
                        if (driver == null) {
                            driver = mMap.addMarker(new MarkerOptions(new LatLng(last.getAttachedLatitude(), last.getAttachedLongitude()))
                                    .icon(BitmapDescriptorFactory.fromAsset("taxi.png")));
                                   //driver.showInfoWindow();
                        } else {

//                           driver.setPosition(new LatLng(last.getAttachedLatitude(), last.getAttachedLongitude()));
                            driver.setRotation(last.getDirection());
                            ArrayList<LatLng> points = new ArrayList<>();
                            if (lastpoint!=null){
                                //添加到下一次定位的点
                                points.add(lastpoint);
                            }
                            //循环定位的点
                            for (int i=0; i<syncData.getLocations().size(); i++) {
                                points.add(new LatLng(syncData.getLocations().get(i).getLatitude(), syncData.getLocations().get(i).getLongitude()));
                            }
                            //获取最后一个点
                            LatLng[] latLngs = points.toArray(new LatLng[points.size()]);
                            //平移动画
                            lastpoint=points.get(latLngs.length-1);

                            mTranslateAnimator = new MarkerTranslateAnimator(driver, 5 * 1000, latLngs, true);
                            mTranslateAnimator.startAnimation();

                        }
                        driver.setTitle("leftDistance:" + syncData.getOrder().getLeftDistance() + " leftTime:" +
                                syncData.getOrder().getLeftTime());
                        Log.d(TAG, "size:" + (syncData.getLocations().size() + " provider:" + last.getProvider()));
                    } else {
                        Log.e(TAG, "收到定位点为空");
                    }
                    ArrayList<LatLng> points = new ArrayList<>();
                    for (int i=0; i<syncData.getRoute().getRoutePoints().size(); i++) {
                        points.add(new LatLng(syncData.getRoute().getRoutePoints().get(i).getLatitude(),
                                syncData.getRoute().getRoutePoints().get(i).getLongitude()));

                    }
                    if (polyline == null) {
                        polyline = mMap.addPolyline(new PolylineOptions().latLngs(points).color(1)
                                .colorType(PolylineOptions.ColorType.LINE_COLOR_TEXTURE));
                        lastRouteId = syncData.getRoute().getRouteId();
                    } else {
                        if (!syncData.getRoute().getRouteId().equals(lastRouteId)) {
                            polyline.setPoints(points);
                            lastRouteId = syncData.getRoute().getRouteId();
                        }
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
                    Log.e(TAG, "onRouteUploadFailed:" + error.getErrorMsg() + "  errorCode:" + error.getErrorCode());
                }

                @Override
                public void onRouteUploadComplete() {
                    if(mTranslateAnimator!=null){
                        mTranslateAnimator.endAnimation();
                    }
                    Log.e(TAG, "onRouteUploadComplete");
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "startSynchro Exception", e);
        }
        Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();
    }

    public void closeSynchro(View view) {
        tencentLocusSynchro.stop();
        Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        enableGps(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mapView.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        if (tencentLocusSynchro != null) {
            tencentLocusSynchro.updateLocation(convertToSynchroLocation(tencentLocation), new Order(orderId, orderStatus));
        }
        if (loc == null) {
            loc = mMap.addMarker(new MarkerOptions(new LatLng(tencentLocation.getLatitude(), tencentLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromAsset("ic_map_anchor.png")).rotation(tencentLocation.getBearing()));
        } else {
            loc.setPosition(new LatLng(tencentLocation.getLatitude(), tencentLocation.getLongitude()));
            loc.setRotation(tencentLocation.getBearing());
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }

    private SynchroLocation convertToSynchroLocation(TencentLocation location) {
        if (location == null) {
            return null;
        }
        SynchroLocation synchro = new SynchroLocation();
        synchro.setAccuracy(location.getAccuracy());
        synchro.setAltitude(location.getAltitude());
        synchro.setDirection(location.getBearing());
        synchro.setLatitude(location.getLatitude());
        synchro.setLongitude(location.getLongitude());
        synchro.setProvider(location.getProvider());
        synchro.setVelocity(location.getSpeed());
        synchro.setTime(location.getTime());
        return synchro;
    }
}
