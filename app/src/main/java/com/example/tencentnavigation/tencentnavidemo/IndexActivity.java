package com.example.tencentnavigation.tencentnavidemo;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class IndexActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listIndex;
    private myAdpter myadpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        initListView();
    }

    public void initListView(){
        listIndex = findViewById(R.id.list_index);
        myadpter = new myAdpter(this,R.id.list_index,getContents());
        listIndex.setAdapter(myadpter);
        listIndex.setOnItemClickListener(this);
    }

    private class myAdpter extends ArrayAdapter<String>{
        myAdpter(Context context, int ResourceId, ArrayList<String> strings){
            super(context, ResourceId, strings);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.index_adapter,null);
            }
            String name = getItem(position);
            TextView textView = convertView.findViewById(R.id.content);

            if (!(position == 0 || position == 1 || position == 2
                    || position == 3 || position == 4
                    || position == 13 || position == 16
                    || position == 23 || position == 24)) {
                textView.setText("      "+name); // 父项
            } else {
                textView.setText("  "+name);     // 子项
            }

            return convertView;

        }
    }

    public ArrayList<String> getContents() {
        ArrayList<String>  strings = new ArrayList<>();

        strings.add(getString(R.string.navi_component));                                        // index == 0
        strings.add(getString(R.string.route));
        strings.add(getString(R.string.navi_time));
        strings.add(getString(R.string.navi_simu));

        strings.add(getString(R.string.navi_set));                                    // index == 4
        strings.add(getString(R.string.navimode));
        strings.add(getString(R.string.daynightmode));
        strings.add(getString(R.string.navifuntion));
        strings.add(getString(R.string.navi_default_res));
        strings.add(getString(R.string.navifixing_proportion));
        strings.add(getString(R.string.naviline_width));
        strings.add(getString(R.string.naviline_erase));
        strings.add(getResources().getString(R.string.navi_panel_hide));

        strings.add(getString(R.string.navi_panel));                   // index == 13
        strings.add(getString(R.string.navi_panel_style));
        strings.add(getString(R.string.navi_speed_style));

        strings.add(getString(R.string.navi_addnew_style));            // index == 16
        //新加功能
        strings.add(getString(R.string.navi_bounce_style));
        strings.add(getString(R.string.navi_compassmarker_hide));
        strings.add(getString(R.string.navi_eyemarker_hide));
        strings.add(getString(R.string.navi_turnarrow_hide));
        strings.add(getString(R.string.navi_regionmargin_hide));
        strings.add(getString(R.string.navi_updateExtraPoints_hide));

        strings.add(getString(R.string.navi_ride));                   // index == 23 骑行导航
        strings.add(getString(R.string.navi_walk));                   // index == 24 步行导航

        return strings;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                //导航组件
                Intent NaviComponent = new Intent(this, RouteComponentActivity.class);
                startActivity(NaviComponent);
                break;
            case 1:
                //路线规划
                Intent RouteIntent = new Intent(this, RouteActivity.class);
                startActivity(RouteIntent);
                break;
            case 2:
                //实时导航
                Intent NaviRealIntent = new Intent(this, NaviRealActivity.class);
                startActivity(NaviRealIntent);
                break;
            case 3:
                //模拟导航
                Intent NaviSimuIntent = new Intent(this, NaviSimuActivity.class);
                startActivity(NaviSimuIntent);
                break;
            case 5:
                //导航模式设置
                Intent NaviModeIntent = new Intent(this, SetNaviModeActivity.class);
                startActivity(NaviModeIntent);
                break;
            case 6:
                //日夜模式设置
                Intent setDayNightModeIntent = new Intent(this, SetDayNightModeActivity.class);
                startActivity(setDayNightModeIntent);
                break;
            case 7:
                //导航功能设置
                Intent SetNaviFunctionIntent = new Intent(this, SetNaviFunctionPowerActivity.class);
                startActivity(SetNaviFunctionIntent);
                break;
            case 8:
                //导航是否使用默认资源设置
                Intent SetNaviDefaultIntent = new Intent(this, SetDefaultResouceActivity.class);
                startActivity(SetNaviDefaultIntent);
                break;
            case 9:
                //导航自车点位置设置
                Intent SetFixingIntent = new Intent(this, SetNaviFixingProportionActivity.class);
                startActivity(SetFixingIntent);
                break;
            case 10:
                //线路宽度设置
                Intent SetLineWidthIntent = new Intent(this, SetLineWidthActivity.class);
                startActivity(SetLineWidthIntent);
                break;
            case 11:
                //线路擦除设置
                Intent SetLineEraseIntent = new Intent(this, SetLineEraseActivity.class);
                startActivity(SetLineEraseIntent);
                break;
            case 12:
                Intent setPanelHideIntent = new Intent(this, SetPanelHideActivity.class);
                startActivity(setPanelHideIntent);
                break;
            case 14:
                //自定义导航面板
                Intent NaviPanelStyle = new Intent(this, NaviPanelStyleActivity.class);
                startActivity(NaviPanelStyle);
                break;

            case 15:
                //自定义限速样式
                Intent NaviSpeedStyle = new Intent(this, NaviSpeedStyleActivity.class);
                startActivity(NaviSpeedStyle);
                break;

            case 17:
                //设置用户拖动地图进入回弹模式
                Intent NaviBounceStyle = new Intent(this, NaviBounceStyleActivity.class);
                startActivity(NaviBounceStyle);
                break;
            case 18:
                //设置是否显示小车罗盘marker
                Intent MarkerVisivle = new Intent(this, CompassMarkerVisibleActivity.class);
                startActivity(MarkerVisivle);
                break;
            case 19:
                //设置是否显示电子眼marker
                Intent EyeMarkerVisivle = new Intent(this, ElectronicEyeMarkerVisibleActivity.class);
                startActivity(EyeMarkerVisivle);
                break;
            case 20:
                //设置是否显示地图路线上的白色转向箭头
                Intent TurnArrowVisivle = new Intent(this, TurnArrowVisibleActivity.class);
                startActivity(TurnArrowVisivle);
                break;
            case 21:
                //设置导航路线显示区域距离屏幕四周的边距。
                Intent VisibleRegionMargin = new Intent(this, VisibleRegionMarginActivity.class);
                startActivity(VisibleRegionMargin);
                break;
            case 22:
                //在剩余全览模式下,显示在可视区域内开发者传入的坐标点和清除地图中可视区域内的点。
                Intent updateExtraPoints = new Intent(this, UpdateExtraPointsInVisibleActivity.class);
                startActivity(updateExtraPoints);
                break;
            case 23:
                // 骑行导航
//                Intent toNaviRideIntent = new Intent(this, NaviRideActivity.class);
//                startActivity(toNaviRideIntent);
                break;
            case 24:
                // 步行导航
                Intent toNaviWalkIntent = new Intent(this, NaviWalkActivity.class);
                startActivity(toNaviWalkIntent);
                break;
        }
    }
}
