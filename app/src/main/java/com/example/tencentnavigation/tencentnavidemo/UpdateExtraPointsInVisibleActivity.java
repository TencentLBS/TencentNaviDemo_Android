package com.example.tencentnavigation.tencentnavidemo;

import android.media.DrmInitData;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.tencent.map.navi.car.NaviMode;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;

import java.util.ArrayList;

public class UpdateExtraPointsInVisibleActivity extends BaseActivity implements View.OnClickListener {

    //添加经纬度点  LatLng
    private LinearLayout turn_clear_map;
    private Button button_clear_map;
    private Button button_add_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全览模式
        carNaviView.setNaviMode(NaviMode.MODE_REMAINING_OVERVIEW);
        initView();
        initData();
    }

    private void initView() {
        button_add_map = findViewById(R.id.button_add_map);
        turn_clear_map = findViewById(R.id.turn_clear_map);
        button_clear_map = findViewById(R.id.button_clear_map);
        turn_clear_map.setVisibility(View.VISIBLE);
    }

    private void initData() {
        button_add_map.setOnClickListener(this);
        button_clear_map.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //在剩余全览模式添加地图中可视区域内的点
            case R.id.button_add_map:
                //添加标记
                ArrayList<LatLng> latLngs = new ArrayList<>();
                LatLng latLng = new LatLng(39.988161, 116.254853);
                latLngs.add(latLng);
                carNaviView.getMap().addMarker(new MarkerOptions().position(latLng).snippet("DefaultMarker"));
                carNaviView.updateExtraPointsInVisibleRegion(latLngs);
                break;
            //在剩余全览模式清除地图中可视区域内的点
            case R.id.button_clear_map:
                carNaviView.clearExtraPointsInVisibleRegion();
                carNaviView.getMap().clear();
                break;
        }
    }
}
