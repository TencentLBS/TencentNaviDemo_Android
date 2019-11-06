package com.example.tencentnavigation.tencentnavidemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.map.navi.INaviView;
import com.tencent.map.navi.data.NavigationData;
import com.tencent.map.navi.data.TrafficItem;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class NaviPanelStyleActivity extends BaseActivity {

    private ConstraintLayout newPanel;//新面板
    private TextView leftDisNext, roadNext;//距离下一个转弯事件的距离，下一个道路名称
    private ImageView turnNext, lane;//下一个转弯事件、车道线
    private TextView leftTimeDis;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }

    private void initView(){

        newPanel = findViewById(R.id.panel_style);
        leftDisNext = findViewById(R.id.left_dis_next);
        roadNext = findViewById(R.id.road_next);
        turnNext = findViewById(R.id.turn_next);
        lane = findViewById(R.id.lane);
        leftTimeDis = findViewById(R.id.left_time_dis);

        //隐藏默认的导航面板
        if(carNaviView!=null){
            carNaviView.setNaviPanelEnabled(false);
            newPanel.setVisibility(View.VISIBLE);
        }
        if(tencentCarNaviManager!=null){
            //添加导航协议
            tencentCarNaviManager.addNaviView(customView);
        }

    }

    private String formatDis(int dis){
        DecimalFormat df = new DecimalFormat("0.0");
        String distance;
        if(dis>=1000){
            distance =df.format(dis/1000.0)+"公里";
        }else {
            distance = +dis+"米";
        }
        return distance;
    }
    private void setLeftTimeDis(int time, int dis){

        String content = "";
        content = "剩余"+formatDis(dis)+",";
        if(time>60){
            content +=time/60+"小时";
        }else {
            content+=time+"分钟";
        }
        leftTimeDis.setText(content);
    }

    //实现InaviView协议
    private INaviView customView = new INaviView() {
        @Override
        public void onGpsRssiChanged(int i) {
        }

        @Override
        public void onUpdateNavigationData(NavigationData navigationData) {
            //更新导航面板数据
            leftDisNext.setText(formatDis(navigationData.getDistanceToNextRoad())+"后");

            setLeftTimeDis(navigationData.getLeftTime(),navigationData.getLeftDistance());
            roadNext.setText("进入"+navigationData.getNextRoadName());
            turnNext.setImageBitmap(navigationData.getTurnIcon());

        }

        @Override
        public void onShowEnlargedIntersection(Bitmap bitmap) {

        }

        @Override
        public void onHideEnlargedIntersection() {

        }

        @Override
        public void onShowGuidedLane(Bitmap bitmap) {
            //设置车道线
            lane.setImageBitmap(bitmap);
        }

        @Override
        public void onHideGuidedLane() {

        }

        @Override
        public void onUpdateTraffic(String s, int i, int i1, ArrayList<LatLng> arrayList, ArrayList<TrafficItem> arrayList1, boolean b) {

        }
    };
}
