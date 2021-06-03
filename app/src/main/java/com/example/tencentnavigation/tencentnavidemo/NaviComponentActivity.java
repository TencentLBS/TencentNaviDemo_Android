package com.example.tencentnavigation.tencentnavidemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.map.navi.DayNightModeChangeCallback;
import com.tencent.map.navi.INaviView;
import com.tencent.map.navi.TencentNaviCallback;
import com.tencent.map.navi.car.CarNaviView;
import com.tencent.map.navi.car.DayNightMode;
import com.tencent.map.navi.car.NaviMode;
import com.tencent.map.navi.car.TencentCarNaviManager;
import com.tencent.map.navi.data.AttachedLocation;
import com.tencent.map.navi.data.GpsLocation;
import com.tencent.map.navi.data.NaviTts;
import com.tencent.map.navi.data.NavigationData;
import com.tencent.map.navi.data.RouteData;
import com.tencent.map.navi.data.TrafficItem;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


public class NaviComponentActivity extends AppCompatActivity {

    private static final String TAG = "navisdk";

    private static final String FORMAT_SPEED = "%d|km/h";
    private static final String FORMAT_DISTANCE_TIME = "剩余%s %s";

    private TencentLocationManager locationManager = null;

    private TencentCarNaviManager mTencentCarNaviManager;

    private CarNaviView mCarNaviView;
    private TextView mSpeedView;
    private TextView mLimitSpeedView;
    private TextView mRoadNameView;
    private TextView mBtnSettings;
    private TextView mNaviInfoView;
    private TextView mBtnExit;

    private int mRouteIndex;
    private boolean mShowNaviPanel = true;
    private boolean mShowCrossingEnlarged = true;
    private DayNightMode mDayNightMode;
    private NaviMode mNaviMode;
    private boolean mSimulateNavi;
    private boolean mIsResumed;

    private int mSpeed, mLimitSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.tencent_navigation);
        super.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        super.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ActionBar actionBar = super.getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        initDatas();
        initViews();
        initTencentNavigation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCarNaviView != null) {
            mCarNaviView.onStart();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mCarNaviView != null) {
            mCarNaviView.onRestart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsResumed = true;
        if (mCarNaviView != null) {
            mCarNaviView.onResume();
        }
        if (!mTencentCarNaviManager.isNavigating()) {
            mCarNaviView.setDayNightModeChangeCallback(mDayNightModeChangeCallback);
            mCarNaviView.setNaviPanelEnabled(mShowNaviPanel);
            mCarNaviView.setDayNightMode(mDayNightMode);
            mCarNaviView.setNaviMode(mNaviMode);
            updateLimitSpeedView(0);
            updateSpeedView(0);

            if (mSimulateNavi) {
                try {
                    mTencentCarNaviManager.startSimulateNavi(mRouteIndex);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                int error = enableGps(this);
                if (error != 0) {
                    Log.e(TAG, "can't start gps!!!");
                }
                try {
                    mTencentCarNaviManager.startNavi(mRouteIndex);
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        if (mCarNaviView != null) {
            mCarNaviView.onPause();
        }
        mIsResumed = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mCarNaviView != null) {
            mCarNaviView.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mTencentCarNaviManager.removeNaviView(mINaviView);
        mTencentCarNaviManager.removeNaviView(mCarNaviView);
        mTencentCarNaviManager.setNaviCallback(null);
        mTencentCarNaviManager = null;
        if (mCarNaviView != null) {
            mCarNaviView.setDayNightModeChangeCallback(null);
            mCarNaviView.onDestroy();
            mCarNaviView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        stopNavigation();
        super.onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initDatas();
        initViews();
        initTencentNavigation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            mShowNaviPanel = data.getBooleanExtra("showNaviPanel", true);
            mShowCrossingEnlarged = data.getBooleanExtra("crossingEnlarge", true);
            mDayNightMode = (DayNightMode) data.getSerializableExtra("dayNightMode");
            mNaviMode = (NaviMode) data.getSerializableExtra("naviMode");
            if (mCarNaviView != null) {
                mCarNaviView.setNaviPanelEnabled(mShowNaviPanel);
                mCarNaviView.setDayNightMode(mDayNightMode);
                mCarNaviView.setNaviMode(mNaviMode);
            }
        }
    }

    private void initDatas() {
        Intent intent = getIntent();
        if (intent != null) {
            mRouteIndex = intent.getIntExtra("routeIndex", 0);
            mShowNaviPanel = intent.getBooleanExtra("showNaviPanel", true);
            mDayNightMode = (DayNightMode) intent.getSerializableExtra("dayNightMode");
            mNaviMode = (NaviMode) intent.getSerializableExtra("naviMode");
            mSimulateNavi = intent.getBooleanExtra("simulateNavi", false);
        }
    }
    private int enableGps(Context context) {
        TencentLocationRequest locationRequest = TencentLocationRequest.create();
        locationRequest.setInterval(1000);
        //locationRequest.setAllowCache(true);
        locationRequest.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_NAME);
        locationManager = TencentLocationManager.getInstance(context);
        int error = locationManager.requestLocationUpdates(locationRequest, mTencentLocationListener);
        Log.e(TAG, "enableGps error: " + error);
        return error;
    }
    /**
     * 初始化界面元素
     */
    private void initViews() {
        mCarNaviView = findViewById(R.id.car_navi_view);
        mSpeedView = findViewById(R.id.speed);
        mLimitSpeedView = findViewById(R.id.limit_speed);
        mRoadNameView = findViewById(R.id.road_name);
        mBtnSettings = findViewById(R.id.settings);
        mBtnSettings.setOnClickListener(onClickListener);
        mNaviInfoView = findViewById(R.id.distance_and_time);
        mBtnExit = findViewById(R.id.exit);
        mBtnExit.setOnClickListener(onClickListener);

        //
        int color, resId;
        boolean isNight = mCarNaviView.isNightStatus();
        if (isNight) {
            color = Color.parseColor("#FFFFFF");
            resId = R.drawable.navi_road_name_background_night;
        } else {
            color = Color.parseColor("#111111");
            resId = R.drawable.navi_road_name_background_day;
        }
        mRoadNameView.setBackgroundResource(resId);
        mBtnSettings.setTextColor(color);
        mBtnExit.setTextColor(color);
    }

    /**
     * 初始化mTencentCarNaviManager
     */
    private void initTencentNavigation() {
        mTencentCarNaviManager = RouteComponentActivity.getTencentNaviManager(this);
        mTencentCarNaviManager.addNaviView(mINaviView);
        mTencentCarNaviManager.addNaviView(mCarNaviView);
        mTencentCarNaviManager.setNaviCallback(mTencentNaviCallback);
        mTencentCarNaviManager.setInternalTtsEnabled(true);
    }


    private void stopNavigation() {
        if (mSimulateNavi) {
            mTencentCarNaviManager.stopSimulateNavi();
        } else {
            mTencentCarNaviManager.stopNavi();
        }
    }

    /**
     * 坐标转化工具类
     * @param tencentLocation
     * @return
     */
    public GpsLocation convertToGpsLocation(TencentLocation tencentLocation) {
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

    public GpsLocation convertToGpsLocation(Location location) {
        if (location == null) {
            return null;
        }
        GpsLocation gpsLocation = new GpsLocation();
        gpsLocation.setDirection(location.getBearing());
        gpsLocation.setAccuracy(location.getAccuracy());
        gpsLocation.setLatitude(location.getLatitude());
        gpsLocation.setLongitude(location.getLongitude());
        gpsLocation.setAltitude(location.getAltitude());
        gpsLocation.setProvider(location.getProvider());
        gpsLocation.setVelocity(location.getSpeed());
        gpsLocation.setTime(location.getTime());

        return gpsLocation;
    }

    /**
     * 位置监听器
     */
    private TencentLocationListener mTencentLocationListener = new TencentLocationListener() {
        @Override
        public void onLocationChanged(TencentLocation tencentLocation, int error, String reason) {
            //Log.e(TAG, "onLocationChanged longitude: " + tencentLocation.getLongitude() + ", latitude: " + tencentLocation.getLatitude());
            if (mTencentCarNaviManager != null) {
                mTencentCarNaviManager.updateLocation(convertToGpsLocation(tencentLocation), error, reason);
            }
        }

        @Override
        public void onStatusUpdate(String name, int status, String desc) {
            if (mTencentCarNaviManager != null) {
                mTencentCarNaviManager.updateGpsStatus(name, status, desc);
            }
        }
    };

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //Log.e(TAG, "onLocationChanged longitude: " + location.getLongitude() + ", latitude: " + location.getLatitude());
            if (mTencentCarNaviManager != null) {
                mTencentCarNaviManager.updateLocation(convertToGpsLocation(location), 0, "");
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (mTencentCarNaviManager != null) {
                mTencentCarNaviManager.updateGpsStatus(provider, status, "");
            }
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.settings: {
                    Intent intent = new Intent(getBaseContext(), SettingsComponentActivity.class);
                    intent.putExtra("isNavigating", true);
                    intent.putExtra("showNaviPanel", mShowNaviPanel);
                    intent.putExtra("crossingEnlarge", mShowCrossingEnlarged);
                    intent.putExtra("dayNightMode", mDayNightMode);
                    intent.putExtra("naviMode", mNaviMode);
                    startActivityForResult(intent, 0);
                    break;
                }
                case R.id.exit: {
                    stopNavigation();
                    finish();
                    break;
                }
            }
        }
    };

    /**
     * 导航回调
     */
    private TencentNaviCallback mTencentNaviCallback = new TencentNaviCallback() {

        @Override
        public void onRecalculateRouteFailure(int type, int errorCode, String errorMessage) {

        }

        @Override
        public void onRecalculateRouteStarted(int type) {

        }

        @Override
        public void onRecalculateRouteCanceled() {

        }

        @Override
        public int onVoiceBroadcast(NaviTts tts) {
            Log.e(TAG, "语音播报文案：" + tts.getText());
            return 1;
        }

        @Override
        public void onArrivedDestination() {
            Log.e(TAG, "到达目的地");
            if (mIsResumed) {
                mCarNaviView.postDelayed(() -> {
                    stopNavigation();
                    finish();
                }, 5000);
            } else {
                stopNavigation();
                finish();
            }
        }

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
            mRouteIndex = 0;
        }


        @Override
        public void onPassedWayPoint(int passPointIndex) {

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
     * 日夜模式改变回调
     */
    private DayNightModeChangeCallback mDayNightModeChangeCallback = new DayNightModeChangeCallback() {
        @Override
        public void onDayNightModeChanged(boolean isNight) {
            Log.e(TAG, "onDayNightModeChanged isNight: " + isNight);
            int color, resId;
            if (isNight) {
                color = Color.parseColor("#FFFFFF");
                resId = R.drawable.navi_road_name_background_night;
            } else {
                color = Color.parseColor("#111111");
                resId = R.drawable.navi_road_name_background_day;
            }
            mRoadNameView.setBackgroundResource(resId);
            mBtnSettings.setTextColor(color);
            mBtnExit.setTextColor(color);
        }
    };

    //实现INaviView协议
    private INaviView mINaviView = new INaviView() {
        @Override
        public void onUpdateNavigationData(NavigationData data) {
            updateLimitSpeedView(data.getLimitSpeed());
            updateSpeedView(data.getCurrentSpeed());
            updateLeftDistanceTime(data.getLeftDistance(), data.getLeftTime());
            mRoadNameView.setText(data.getCurrentRoadName());
        }

        @Override
        public void onShowEnlargedIntersection(Bitmap bitmap) {

        }

        @Override
        public void onHideEnlargedIntersection() {

        }

        @Override
        public void onShowGuidedLane(Bitmap lane) {

        }

        @Override
        public void onHideGuidedLane() {

        }

        @Override
        public void onUpdateTraffic(String s, int i, int i1, ArrayList<LatLng> arrayList, ArrayList<TrafficItem> arrayList1, boolean b) {

        }

        @Override
        public void onGpsRssiChanged(int rssi) {

        }

        @Override
        public void onSmartLocStart() {

        }

        @Override
        public void onSmartLocEnd() {

        }

        @Override
        public void onGpsWeakNotify() {

        }

        @Override
        public void onGpsStrongNotify() {

        }

    };

    /**
     * 更新速度控件
     * @param speed
     */
    private void updateSpeedView(int speed) {
        mSpeed = speed;
        int color, resId;
        if (mSpeed <= mLimitSpeed) {
            color = Color.parseColor("#4875FD");
            resId = R.drawable.navi_bg_speed;
        } else {
            color = Color.parseColor("#FFFFFF");
            resId = R.drawable.navi_bg_overspeed;
        }
        if (mSpeed > 0) {
            String msg = String.format(Locale.getDefault(), FORMAT_SPEED, mSpeed);
            msg = msg.replace("|", "\n");
            int index = msg.indexOf("km/h");
            SpannableString str = new SpannableString(msg);
            str.setSpan(new AbsoluteSizeSpan(22, true), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new ForegroundColorSpan(color), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new StyleSpan(Typeface.BOLD), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new AbsoluteSizeSpan(9, true), index, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new ForegroundColorSpan(color), index, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new StyleSpan(Typeface.NORMAL), index, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mSpeedView.setText(str);
        } else {
            mSpeedView.setText("--");
        }
        mSpeedView.setBackgroundResource(resId);
    }

    /**
     * 更新限速控件
     * @param limitSpeed
     */
    private void updateLimitSpeedView(int limitSpeed) {
        mLimitSpeed = limitSpeed;
        int textSize = 22;
        if (limitSpeed <= 0) {
        } else if (limitSpeed < 100) {
            textSize = 23;
        } else {
            textSize = 20;
        }
        mLimitSpeedView.setTextSize(textSize);
        if (mLimitSpeed > 0) {
            mLimitSpeedView.setText(String.valueOf(limitSpeed));
        } else {
            mLimitSpeedView.setText("--");
        }
    }

    /**
     * 更新剩余距离时间控件
     * @param leftDistance
     * @param leftTime
     */
    private void updateLeftDistanceTime(int leftDistance, int leftTime) {
        int color, resId;
        boolean isNight = mCarNaviView.isNightStatus();
        if (isNight) {
            color = Color.parseColor("#FFFFFF");
            resId = R.drawable.navi_distance_time_background_night;
        } else {
            color = Color.parseColor("#111111");
            resId = R.drawable.navi_distance_time_background_day;
        }
        String distance = NaviUtil.distance2string(leftDistance, true);
        String time = NaviUtil.time2string(leftTime);
        String msg = String.format(Locale.getDefault(), FORMAT_DISTANCE_TIME, distance, time);
        SpannableString str = new SpannableString(msg);
        try {
            Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)");
            Matcher matcher = pattern.matcher(msg);
            int index = 0;
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                //非数字部分
                if (index < start) {
                    str.setSpan(new AbsoluteSizeSpan(15, true), index, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    str.setSpan(new ForegroundColorSpan(color), index, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    str.setSpan(new StyleSpan(Typeface.NORMAL), index, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                //数字部分
                str.setSpan(new AbsoluteSizeSpan(22, true), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                str.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                str.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //
                index = end;
            }
            //非数字部分
            if (index < msg.length()) {
                str.setSpan(new AbsoluteSizeSpan(15, true), index, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                str.setSpan(new ForegroundColorSpan(color), index, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                str.setSpan(new StyleSpan(Typeface.NORMAL), index, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mNaviInfoView.setText(str);
        View navigationView = findViewById(R.id.navigation);
        navigationView.setBackgroundResource(resId);
    }
}
