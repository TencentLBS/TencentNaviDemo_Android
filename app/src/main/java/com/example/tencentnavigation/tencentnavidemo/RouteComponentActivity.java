package com.example.tencentnavigation.tencentnavidemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.map.navi.TencentRouteSearchCallback;
import com.tencent.map.navi.car.CarRouteSearchOptions;
import com.tencent.map.navi.car.DayNightMode;
import com.tencent.map.navi.car.NaviMode;
import com.tencent.map.navi.car.TencentCarNaviManager;
import com.tencent.map.navi.data.GpsLocation;
import com.tencent.map.navi.data.NaviPoi;
import com.tencent.map.navi.data.RouteData;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.SupportMapFragment;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.TencentMap.OnMarkerClickListener;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.Polyline;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;


public class RouteComponentActivity extends AppCompatActivity implements TencentLocationListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "navisdk";


    private static final int REQUEST_FROM = 1;
    private static final int REQUEST_TO = 2;
    private static final int REQUEST_PASS1 = 3;
    private static final int REQUEST_PASS2 = 4;
    private static final int REQUEST_PASS3 = 5;
    private static final int REQUEST_SETTING = 6;

    private TextView mSearchViewFrom = null;
    private TextView mSearchViewTo = null;
    private ImageView mBtnChange = null;
    private TextView mBtnStartNavigation = null;
    private TextView mBtnSimulate;


    private ImageView mBtnTraffic = null;
    private ImageView mBtnPreference = null;
    private ImageView mBtnLocate = null;

    private boolean mFromCurrentLocation = true;
    private boolean mToCurrentLocation = false;

    private String mFromAddress;
    private String mToAddress;

    private boolean mAvoidJam = false;
    private boolean mAvoidTolls = false;
    private boolean mAvoidHighway = false;
    private boolean mShowNaviPanel = true;
    private boolean mShowElectriEyesPicture = true;
    private boolean mShowCrossingEnlargePicture = true;
    private DayNightMode mDayNightMode = DayNightMode.AUTO_MODE;
    private NaviMode mNaviMode = NaviMode.MODE_3DCAR_TOWARDS_UP;

    private TencentLocationManager locationManager = null;
    private boolean mAllowDisableGps = true;

    private static TencentCarNaviManager mTencentCarNaviManager;
    private CarRouteSearchOptions mCarSearchOptions;

    private int mSelectedIndex;
    private TencentMap mMap = null;
    private ArrayList<Polyline> mRoutes = new ArrayList<Polyline>();
    private ArrayList<RouteData> mRouteDatas = new ArrayList<RouteData>();

    private NaviPoi startPoint = new NaviPoi(0, 0);// 起点
    private NaviPoi destPoint = new NaviPoi(0, 0); // 终点
    private ArrayList<NaviPoi> wayPoints = new ArrayList<NaviPoi>(); // 途经点

    private Marker markerStart = null; // 起点
    private Marker markerDestination = null; // 目的地
    private ArrayList<Marker> markerWayPoints = new ArrayList<Marker>(); // 途经点

    private String strStartMarkerId = "";
    private String strDestiMarkerId = "";

    private static final String FORMAT_ROUTE_INFO = "%s|%d分钟|%s";

    public static TencentCarNaviManager getTencentNaviManager(Context context) {
        if (mTencentCarNaviManager == null) {
            mTencentCarNaviManager = new TencentCarNaviManager(context);
        }
        return mTencentCarNaviManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_route_component);

        ActionBar actionBar = super.getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

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

        initViews();
        initTencentNavigation();
        mSearchViewFrom.setText(R.string.current_location);
        mFromCurrentLocation = true;
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        if (mAllowDisableGps) {
            enableGps(this);
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        if (mAllowDisableGps) {
            disableGps();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mTencentCarNaviManager = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String address = data.getStringExtra("address");
            float latitude = data.getFloatExtra("latitude", 0);
            float longitude = data.getFloatExtra("longitude", 0);
            switch (requestCode) {
                case REQUEST_FROM:
                    mFromCurrentLocation = false;
                    mSearchViewFrom.setText(address);
                    startPoint.setLatitude(latitude);
                    startPoint.setLongitude(longitude);
                    mFromAddress = address;
                    break;
                case REQUEST_TO:
                    mToCurrentLocation = false;
                    mSearchViewTo.setText(address);
                    destPoint.setLatitude(latitude);
                    destPoint.setLongitude(longitude);
                    mToAddress = address;
                    break;
                case REQUEST_PASS1: {
                    NaviPoi wayPoint = new NaviPoi(latitude, longitude);
                    wayPoints.add(wayPoint);
                    TextView searchPass1 = (TextView) findViewById(R.id.search_pass1);
                    searchPass1.setText(address);
                    searchPass1.setTag(wayPoint);
                    break;
                }
                case REQUEST_PASS2: {
                    NaviPoi wayPoint = new NaviPoi(latitude, longitude);
                    wayPoints.add(wayPoint);
                    TextView searchPass2 = (TextView) findViewById(R.id.search_pass2);
                    searchPass2.setText(address);
                    searchPass2.setTag(wayPoint);
                    break;
                }
                case REQUEST_PASS3: {
                    NaviPoi wayPoint = new NaviPoi(latitude, longitude);
                    wayPoints.add(wayPoint);
                    TextView searchPass3 = (TextView) findViewById(R.id.search_pass3);
                    searchPass3.setText(address);
                    searchPass3.setTag(wayPoint);
                    break;
                }
                case REQUEST_SETTING: {
                    mAvoidJam = data.getBooleanExtra("avoidJam", false);
                    mAvoidTolls = data.getBooleanExtra("noTolls", false);
                    mAvoidHighway = data.getBooleanExtra("avoidHighway", false);
                    mShowNaviPanel = data.getBooleanExtra("showNaviPanel", true);
                    mShowElectriEyesPicture = data.getBooleanExtra("electriEyes", true);
                    mShowCrossingEnlargePicture = data.getBooleanExtra("crossingEnlarge", true);
                    mDayNightMode = (DayNightMode) data.getSerializableExtra("dayNightMode");
                    mNaviMode = (NaviMode) data.getSerializableExtra("naviMode");
                    // update search options
                    mCarSearchOptions = mCarSearchOptions
                            .avoidCongestion(mAvoidJam)
                            .avoidHighway(mAvoidHighway)
                            .avoidToll(mAvoidTolls);
                    break;
                }
            }
            if (mFromCurrentLocation || mToCurrentLocation) {
                TencentLocation tencentLocation = locationManager.getLastKnownLocation();
                if (tencentLocation == null) {
                    Log.e(TAG, "onActivityResult can not get location!!!");
                    Toast.makeText(RouteComponentActivity.this, "onActivityResult can not get location!!!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (mFromCurrentLocation) {
                    startPoint.setLatitude(tencentLocation.getLatitude());
                    startPoint.setLongitude(tencentLocation.getLongitude());
                    mFromAddress = tencentLocation.getAddress();
                }
                if (mToCurrentLocation) {
                    destPoint.setLatitude(tencentLocation.getLatitude());
                    destPoint.setLongitude(tencentLocation.getLongitude());
                    mToAddress = tencentLocation.getAddress();
                }
            }
            calculateRoute();
            mAllowDisableGps = true;
        }
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int error, String reason) {
        Log.e(TAG, "onLocationChanged");

    }

    @Override
    public void onStatusUpdate(String name, int status, String desc) {
        Log.e(TAG, "onStatusUpdate");

    }

    private void initViews() {
        //初始化地图
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMapType(TencentMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        mMap.setDrawPillarWith2DStyle(true);
        mMap.setTrafficEnabled(true);

        mSearchViewFrom = findViewById(R.id.search_from);
        mSearchViewTo = findViewById(R.id.search_to);
        mBtnChange = findViewById(R.id.exchange_btn);
        mBtnStartNavigation = findViewById(R.id.start_navigation);


        mSearchViewFrom.setOnClickListener(onClickListener);
        mSearchViewTo.setOnClickListener(onClickListener);
        mBtnChange.setOnClickListener(onClickListener);
        mBtnStartNavigation.setOnClickListener(onClickListener);


        mBtnTraffic = findViewById(R.id.traffic);
        mBtnTraffic.setOnClickListener(onClickListener);
        mBtnTraffic.setSelected(true);
        mBtnSimulate = findViewById(R.id.simulate);
        mBtnSimulate.setOnClickListener(onClickListener);

        mBtnPreference = findViewById(R.id.preference);
        mBtnPreference.setOnClickListener(onClickListener);
        mBtnLocate = findViewById(R.id.locate);
        mBtnLocate.setOnClickListener(onClickListener);

        //途经点
        View iconFrom = findViewById(R.id.icon_from);
        iconFrom.setOnClickListener(onClickListener);
        View iconTo = findViewById(R.id.icon_to);
        iconTo.setOnClickListener(onClickListener);
        View iconPass1 = findViewById(R.id.icon_pass1);
        iconPass1.setOnClickListener(onClickListener);
        View iconPass2 = findViewById(R.id.icon_pass2);
        iconPass2.setOnClickListener(onClickListener);
        View iconPass3 = findViewById(R.id.icon_pass3);
        iconPass3.setOnClickListener(onClickListener);
        View searchPass1 = findViewById(R.id.search_pass1);
        searchPass1.setOnClickListener(onClickListener);
        View searchPass2 = findViewById(R.id.search_pass2);
        searchPass2.setOnClickListener(onClickListener);
        View searchPass3 = findViewById(R.id.search_pass3);
        searchPass3.setOnClickListener(onClickListener);
    }

    private void initTencentNavigation() {
        mTencentCarNaviManager = getTencentNaviManager(this);
        mTencentCarNaviManager.setIsDefaultRes(true);
        mCarSearchOptions = CarRouteSearchOptions.create()
                .avoidCongestion(mAvoidJam)
                .avoidHighway(mAvoidHighway)
                .avoidToll(mAvoidTolls);
    }

    /**
     * GPS管理
     * @param context
     * @return
     */
    private int enableGps(Context context) {
        TencentLocationRequest locationRequest = TencentLocationRequest.create();
        locationRequest.setInterval(1000);
        //locationRequest.setAllowCache(true);
        locationRequest.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_NAME);
        locationManager = TencentLocationManager.getInstance(context);
        int error = locationManager.requestLocationUpdates(locationRequest, this);
        Log.e(TAG, "enableGps error: " + error);
        return error;
    }

    private void disableGps() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    /**
     * 添加标注
     * @param latitude
     * @param longitude
     */
    private void addMarkerStart(double latitude, double longitude) {
        if (markerStart != null) {
            markerStart.remove();
            markerStart = null;
        }
        LatLng startPoint = new LatLng(latitude, longitude);
        markerStart = mMap.addMarker(new MarkerOptions(startPoint)
                .icon(BitmapDescriptorFactory.fromAsset("navi_marker_start.png"))
                .anchor(0.5f, 1));
        markerStart.setInfoWindowEnable(false);
        strStartMarkerId = markerStart.getId();
    }

    private void addMarkerDestination(double latitude, double longitude) {
        if (markerDestination != null) {
            markerDestination.remove();
            markerDestination = null;
        }
        LatLng destinationPoint = new LatLng(latitude, longitude);
        markerDestination = mMap.addMarker(new MarkerOptions(destinationPoint)
                .icon(BitmapDescriptorFactory.fromAsset("navi_marker_end.png"))
                .anchor(0.5f, 1));
        markerDestination.setInfoWindowEnable(false);
        strDestiMarkerId = markerDestination.getId();
    }

    private void addMarkerPass(ArrayList<NaviPoi> wayPoints) {
        for (Marker markerPass : markerWayPoints) {
            if (markerPass != null) {
                markerPass.remove();
            }
        }
        markerWayPoints.clear();
        for (NaviPoi wayPoint : wayPoints) {
            LatLng destinationPoint = new LatLng(wayPoint.getLatitude(), wayPoint.getLongitude());
            Marker markerPass = mMap.addMarker(new MarkerOptions(destinationPoint)
                    .icon(BitmapDescriptorFactory.fromAsset("navi_marker_pass.png"))
                    .anchor(0.5f, 1));
            markerPass.setInfoWindowEnable(false);
            markerWayPoints.add(markerPass);
        }
    }

    private void calculateRoute() {
        if (!((startPoint.getLatitude() == 0 && startPoint.getLongitude() == 0)
                || (destPoint.getLatitude() == 0 && destPoint.getLongitude() == 0))) {
            try {
                mTencentCarNaviManager.searchRoute(startPoint, destPoint, wayPoints, mCarSearchOptions, mTencentSearchCallback);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG,"鉴权异常" + e.getLocalizedMessage());
            }
        } else {
            Log.e(TAG, "params error!!!");
        }
    }

    /**
     * 进入导航
     * @param simulateNavi
     */
    private void startNavigation(boolean simulateNavi) {
        Intent intent = new Intent(this, NaviComponentActivity.class);
        intent.putExtra("routeIndex", mSelectedIndex);
        intent.putExtra("showNaviPanel", mShowNaviPanel);
        intent.putExtra("dayNightMode", mDayNightMode);
        intent.putExtra("naviMode", mNaviMode);
        intent.putExtra("simulateNavi", simulateNavi);
        startActivity(intent);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.search_from: {
                    TencentLocation tencentLocation = locationManager.getLastKnownLocation();
                    if (tencentLocation == null) {
                        Log.e(TAG, "onClick search_from, can not get location!!!");
                        Toast.makeText(RouteComponentActivity.this, "can not get location!!!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent(getBaseContext(), LocationSearchActivity.class);
                    intent.putExtra("latitude", tencentLocation.getLatitude());
                    intent.putExtra("longitude", tencentLocation.getLongitude());
                    startActivityForResult(intent, REQUEST_FROM);
                    mAllowDisableGps = false;
                    break;
                }
                case R.id.search_to: {
                    TencentLocation tencentLocation = locationManager.getLastKnownLocation();
                    if (tencentLocation == null) {
                        Log.e(TAG, "onClick search_to, can not get location!!!");
                        Toast.makeText(RouteComponentActivity.this, "can not get location!!!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent(getBaseContext(), LocationSearchActivity.class);
                    intent.putExtra("latitude", tencentLocation.getLatitude());
                    intent.putExtra("longitude", tencentLocation.getLongitude());
                    startActivityForResult(intent, REQUEST_TO);
                    mAllowDisableGps = false;
                    break;
                }
                case R.id.exchange_btn: {
                    String from = mSearchViewFrom.getText().toString();
                    String to = mSearchViewTo.getText().toString();
                    mSearchViewFrom.setText(to);
                    mSearchViewTo.setText(from);
                    double latitude = startPoint.getLatitude();
                    double longitude = startPoint.getLongitude();
                    startPoint.setLatitude(destPoint.getLatitude());
                    startPoint.setLongitude(destPoint.getLongitude());
                    destPoint.setLatitude(latitude);
                    destPoint.setLongitude(longitude);
                    boolean useCurrentLocation = mFromCurrentLocation;
                    mFromCurrentLocation = mToCurrentLocation;
                    mToCurrentLocation = useCurrentLocation;
                    TencentLocation tencentLocation = locationManager.getLastKnownLocation();
                    if (tencentLocation == null) {
                        Log.e(TAG, "onClick exchange_btn, can not get location!!!");
                        Toast.makeText(RouteComponentActivity.this, "can not get location!!!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (mFromCurrentLocation) {
                        startPoint.setLatitude(tencentLocation.getLatitude());
                        startPoint.setLongitude(tencentLocation.getLongitude());
                    }
                    if (mToCurrentLocation) {
                        destPoint.setLatitude(tencentLocation.getLatitude());
                        destPoint.setLongitude(tencentLocation.getLongitude());
                    }
                    if (wayPoints != null && !wayPoints.isEmpty()) {
                        Collections.reverse(wayPoints);
                    }
                    calculateRoute();
                    break;
                }
                case R.id.icon_from: {
                    for (Polyline polyline : mRoutes) {
                        polyline.remove();
                    }
                    mRoutes.clear();
                    finish();
                    break;
                }
                case R.id.icon_to: {
                    View pass1 = findViewById(R.id.pass1);
                    View pass2 = findViewById(R.id.pass2);
                    View pass3 = findViewById(R.id.pass3);
                    if (!pass1.isShown()) {
                        pass1.setVisibility(View.VISIBLE);
                    } else {
                        if (!pass2.isShown()) {
                            pass2.setVisibility(View.VISIBLE);
                        } else {
                            if (!pass3.isShown()) {
                                pass3.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    if (pass1.isShown() && pass2.isShown() && pass3.isShown()) {
                        View iconTo = findViewById(R.id.icon_to);
                        iconTo.setVisibility(View.INVISIBLE);
                    }
                    break;
                }
                case R.id.icon_pass1: {
                    View pass1 = findViewById(R.id.pass1);
                    pass1.setVisibility(View.GONE);
                    View searchPass1 = findViewById(R.id.search_pass1);
                    NaviPoi wayPoint = (NaviPoi) searchPass1.getTag();
                    if (wayPoint != null) {
                        wayPoints.remove(wayPoint);
                        calculateRoute();
                    }
                    View iconTo = findViewById(R.id.icon_to);
                    iconTo.setVisibility(View.VISIBLE);
                    break;
                }
                case R.id.icon_pass2: {
                    View pass2 = findViewById(R.id.pass2);
                    pass2.setVisibility(View.GONE);
                    View searchPass2 = findViewById(R.id.search_pass2);
                    NaviPoi wayPoint = (NaviPoi) searchPass2.getTag();
                    if (wayPoint != null) {
                        wayPoints.remove(wayPoint);
                        calculateRoute();
                    }
                    View iconTo = findViewById(R.id.icon_to);
                    iconTo.setVisibility(View.VISIBLE);
                    break;
                }
                case R.id.icon_pass3: {
                    View pass3 = findViewById(R.id.pass3);
                    pass3.setVisibility(View.GONE);
                    View searchPass3 = findViewById(R.id.search_pass3);
                    NaviPoi wayPoint = (NaviPoi) searchPass3.getTag();
                    if (wayPoint != null) {
                        wayPoints.remove(wayPoint);
                        calculateRoute();
                    }
                    View iconTo = findViewById(R.id.icon_to);
                    iconTo.setVisibility(View.VISIBLE);
                    break;
                }
                case R.id.search_pass1: {
                    Intent intent = new Intent(getBaseContext(), LocationSearchActivity.class);
                    startActivityForResult(intent, REQUEST_PASS1);
                    mAllowDisableGps = false;
                    break;
                }
                case R.id.search_pass2: {
                    Intent intent = new Intent(getBaseContext(), LocationSearchActivity.class);
                    startActivityForResult(intent, REQUEST_PASS2);
                    mAllowDisableGps = false;
                    break;
                }
                case R.id.search_pass3: {
                    Intent intent = new Intent(getBaseContext(), LocationSearchActivity.class);
                    startActivityForResult(intent, REQUEST_PASS3);
                    mAllowDisableGps = false;
                    break;
                }

                case R.id.traffic: {
                    boolean isTrafficEnabled = mBtnTraffic.isSelected();
                    mMap.setTrafficEnabled(!isTrafficEnabled);
                    mBtnTraffic.setSelected(!isTrafficEnabled);
                    break;
                }
                case R.id.simulate: {
                    boolean isSimulate = true;
                    startNavigation(isSimulate);
                    break;
                }
                case R.id.preference: {
                    Intent intent = new Intent(getBaseContext(), SettingsComponentActivity.class);
                    intent.putExtra("avoidJam", mAvoidJam);
                    intent.putExtra("noTolls", mAvoidTolls);
                    intent.putExtra("avoidHighway", mAvoidHighway);
                    intent.putExtra("showNaviPanel", mShowNaviPanel);
                    intent.putExtra("electriEyes", mShowElectriEyesPicture);
                    intent.putExtra("crossingEnlarge", mShowCrossingEnlargePicture);
                    intent.putExtra("dayNightMode", mDayNightMode);
                    intent.putExtra("naviMode", mNaviMode);
                    startActivityForResult(intent, REQUEST_SETTING);
                    mAllowDisableGps = false;
                    break;
                }
                case R.id.locate: {
                    break;
                }
                case R.id.start_navigation: {
                    if ((startPoint.getLatitude() == 0 && startPoint.getLongitude() == 0)
                            || (destPoint.getLatitude() == 0 && destPoint.getLongitude() == 0)) {
                        Log.e(TAG, "onClick start navigation error, please set begin and end point first!!!");
                        Toast.makeText(RouteComponentActivity.this, "please set begin and end point first!!!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    boolean isSimulate = false;
                    startNavigation(isSimulate);
                    break;
                }
                case R.id.route1:
                    mSelectedIndex = 0;
                    addRouteInfos();
                    addRoutes();
                    zoomToRoute();
                    break;
                case R.id.route2:
                    mSelectedIndex = 1;
                    addRouteInfos();
                    addRoutes();
                    zoomToRoute();
                    break;
                case R.id.route3:
                    mSelectedIndex = 2;
                    addRouteInfos();
                    addRoutes();
                    zoomToRoute();
                    break;
            }
        }
    };

    private OnMarkerClickListener listener = new OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker arg0) {
            if (arg0 == null) {
                return true;
            }
            if (strDestiMarkerId.equals(arg0.getId())) {
                LatLng dest = arg0.getPosition();
                if (markerDestination != null) {
                    markerDestination.remove();
                    markerDestination = null;
                }
                return true;
            }
            if (strStartMarkerId.equals(arg0.getId())) {
                LatLng start = arg0.getPosition();
                GpsLocation startPt = new GpsLocation();
                startPt.setLatitude(start.latitude);
                startPt.setLongitude(start.longitude);
                if (markerStart != null) {
                    markerStart.remove();
                    markerStart = null;
                }
                return true;
            }

            return true;
        }
    };

    private TencentRouteSearchCallback mTencentSearchCallback = new TencentRouteSearchCallback() {
        @Override
        public void onRouteSearchFailure(int errorCode, String errorMessage) {
            Toast.makeText(getApplicationContext(),"算路失败!!!", Toast.LENGTH_SHORT).show();
            Log.e(TAG,"算路失败回调");
        }

        @Override
        public void onRouteSearchSuccess(ArrayList<RouteData> routes) {
            if (routes == null || routes.size() <= 0) {
                Log.e(TAG, "failed to search");
                return;
            }
            Log.e(TAG, "finish searching route, size = " + routes.size());
            for (int i = 0; i < routes.size(); i++) {
                RouteData naviRoute = routes.get(i);
                Log.e(TAG,"算路成功回调-推荐路线理由: " + naviRoute.getRecommendMsg());
                Log.e(TAG,"算路成功回调-距离信息: " + naviRoute.getDistanceInfo());
                Log.e(TAG,"算路成功回调-时间信息: " + naviRoute.getTime());
                Log.e(TAG,"算路成功回调-点串长度: " + naviRoute.getRoutePoints().size());
            }
            for (Polyline polyline : mRoutes) {
                polyline.remove();
            }
            mRoutes.clear();
            mRouteDatas.clear();
            mRouteDatas.addAll(routes);
            mSelectedIndex = 0;

            addRoutes();
            addRouteInfos();
            addMarkerStart(startPoint.getLatitude(), startPoint.getLongitude());
            addMarkerDestination(destPoint.getLatitude(), destPoint.getLongitude());
            addMarkerPass(wayPoints);

            zoomToRoute();
        }
    };

    private void zoomToRoute() {
        View routeInfoLayout = findViewById(R.id.route_info_layout);
        routeInfoLayout.post(() -> {
            int marginLeft = getResources().getDimensionPixelSize(R.dimen.navigation_line_margin_left);
            int marginTop = getResources().getDimensionPixelSize(R.dimen.navigation_line_margin_top);
            int marginRight = getResources().getDimensionPixelSize(R.dimen.navigation_line_margin_right);
            int marginBottom = getResources().getDimensionPixelSize(R.dimen.navigation_line_margin_bottom);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            RouteData routeData = mRouteDatas.get(mSelectedIndex);
            builder.include(routeData.getRoutePoints());
            LatLngBounds bounds = builder.build();
            mMap.moveCamera(CameraUpdateFactory.newLatLngBoundsRect(bounds, marginLeft, marginRight, marginTop, marginBottom));
        });
    }

    private void addRouteInfos() {
        int ids[] = {R.id.route1, R.id.route2, R.id.route3};
        for (int i = 0; i < mRouteDatas.size(); i++) {
            RouteData routeData = mRouteDatas.get(i);
            String recommendMsg = routeData.getRecommendMsg();
            String distanceInfo = routeData.getDistanceInfo();
            int minutes = routeData.getTime();
            String msg = String.format(Locale.getDefault(), FORMAT_ROUTE_INFO, recommendMsg, minutes, distanceInfo);
            int timeStartIndex = msg.indexOf("|");
            int timeEndIndex = msg.lastIndexOf("|");
            msg = msg.replace("|", "\n");
            //
            int color = Color.WHITE;
            if (i == mSelectedIndex) {
                color = Color.parseColor("#ff3c69ef");
            }
            SpannableString str = new SpannableString(msg);
            str.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, timeStartIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new AbsoluteSizeSpan(12, true), 0, timeStartIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new ForegroundColorSpan(color), 0, timeStartIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new StyleSpan(Typeface.NORMAL), 0, timeStartIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), timeStartIndex, timeEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new AbsoluteSizeSpan(22, true), timeStartIndex, timeEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new ForegroundColorSpan(color), timeStartIndex, timeEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new StyleSpan(Typeface.BOLD), timeStartIndex, timeEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), timeEndIndex, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new AbsoluteSizeSpan(12, true), timeEndIndex, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new ForegroundColorSpan(color), timeEndIndex, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new StyleSpan(Typeface.NORMAL), timeEndIndex, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //
            TextView textView = findViewById(ids[i]);
            textView.setOnClickListener(onClickListener);
            textView.setText(str);
            textView.setVisibility(View.VISIBLE);
        }
        for (int i = mRouteDatas.size(); i < 3; i++) {
            TextView textView = findViewById(ids[i]);
            textView.setVisibility(View.GONE);
        }
        View routeInfoLayout = findViewById(R.id.route_info_layout);
        routeInfoLayout.setVisibility(View.VISIBLE);
    }

    private void addRoutes() {
        for (Polyline polyline : mRoutes) {
            polyline.remove();
        }
        mRoutes.clear();
        for (int i = 0; i < mRouteDatas.size(); i++) {
            RouteData routeData = mRouteDatas.get(i);
            PolylineOptions options = new PolylineOptions()
                    .addAll(routeData.getRoutePoints())
                    .arrow(true);
            if (i == mSelectedIndex) {
                options.color(Color.parseColor("#339933"));
                options.zIndex(10);
            } else {
                options.color(Color.parseColor("#99CC33"));
                options.zIndex(8);
            }
            Polyline polyline = mMap.addPolyline(options);
            mRoutes.add(polyline);
        }
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
