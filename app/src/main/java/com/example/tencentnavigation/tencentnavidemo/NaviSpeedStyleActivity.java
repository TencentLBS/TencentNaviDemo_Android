package com.example.tencentnavigation.tencentnavidemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import com.tencent.map.navi.INaviView;
import com.tencent.map.navi.data.NavigationData;
import com.tencent.map.navi.data.TrafficItem;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;

import java.util.ArrayList;

public class NaviSpeedStyleActivity extends BaseActivity {
    private ConstraintLayout speedBar;
    private TextView roadName, limitSpeed, speed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        speedBar = findViewById(R.id.navi_speed_style);
        roadName = findViewById(R.id.navi_road_name);
        limitSpeed = findViewById(R.id.navi_limit_speed);
        speed = findViewById(R.id.navi_speed);

        speedBar.setVisibility(View.VISIBLE);

        if (tencentCarNaviManager != null) {
            //添加导航协议
            tencentCarNaviManager.addNaviView(speedView);
        }
    }

    private INaviView speedView = new INaviView() {
        @Override
        public void onGpsRssiChanged(int i) {

        }

        @Override
        public void onUpdateNavigationData(NavigationData navigationData) {
            roadName.setText(navigationData.getCurrentRoadName());
            speed.setText(navigationData.getCurrentSpeed() + "");
            limitSpeed.setText(navigationData.getLimitSpeed() + "");
        }

        @Override
        public void onShowEnlargedIntersection(Bitmap bitmap) {

        }

        @Override
        public void onHideEnlargedIntersection() {

        }

        @Override
        public void onShowGuidedLane(Bitmap bitmap) {

        }

        @Override
        public void onHideGuidedLane() {

        }

        @Override
        public void onUpdateTraffic(String s, int i, int i1
                , ArrayList<LatLng> arrayList
                , ArrayList<TrafficItem> arrayList1, boolean b) {

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
}
