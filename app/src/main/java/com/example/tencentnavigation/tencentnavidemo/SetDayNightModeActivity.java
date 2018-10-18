package com.example.tencentnavigation.tencentnavidemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tencent.map.navi.DayNightModeChangeCallback;
import com.tencent.map.navi.car.DayNightMode;

public class SetDayNightModeActivity extends BaseActivity{

    private RadioGroup daynightMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        daynightMode = findViewById(R.id.navi_daynight_mode);
        carNaviView.setDayNightModeChangeCallback(dayNightModeChangeCallback);
        daynightMode.setVisibility(View.VISIBLE);

        daynightMode.setOnCheckedChangeListener((group, id)->{
            switch (id){
                case (R.id.auto_mode):
                    carNaviView.setDayNightMode(DayNightMode.AUTO_MODE);
                    break;
                case (R.id.day_mode):
                    carNaviView.setDayNightMode(DayNightMode.DAY_MODE);
                    break;
                case (R.id.night_mode):
                    carNaviView.setDayNightMode(DayNightMode.NIGHT_MODE);
                    break;
            }
        });
    }

    /**
     * 可以设置导航日夜模式状态回调，在日夜状态变化时通知用户
     */
    private DayNightModeChangeCallback dayNightModeChangeCallback = new DayNightModeChangeCallback() {
        @Override
        public void onDayNightModeChanged(boolean b) {
            if(b){
                Log.e("DayNightMode", "night");
                Toast.makeText(SetDayNightModeActivity.this, "夜间", Toast.LENGTH_SHORT).show();
            }else {
                Log.e("DayNightMode", "day");
                Toast.makeText(SetDayNightModeActivity.this, "日间", Toast.LENGTH_SHORT).show();
            }
        }
    };


}
