package com.example.tencentnavigation.tencentnavidemo;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SetNaviFunctionPowerActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener{
    private LinearLayout naviFunction;
    private CheckBox naviCorner,naviLane;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        naviFunction = findViewById(R.id.navi_function);
        naviCorner = findViewById(R.id.navi_corner);
        naviLane = findViewById(R.id.navi_lane);
        naviFunction.setVisibility(View.VISIBLE);

        naviLane.setOnCheckedChangeListener(this);
        naviCorner.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.navi_corner:
                if(tencentCarNaviManager!=null){
                    //设置是否开启路口放大图功能
                    if(isChecked){
                        tencentCarNaviManager.setEnlargedIntersectionEnabled(true);
                        Toast.makeText(SetNaviFunctionPowerActivity.this, "开启路口放大图功能",Toast.LENGTH_SHORT).show();

                    }else {
                        tencentCarNaviManager.setEnlargedIntersectionEnabled(false);
                        Toast.makeText(SetNaviFunctionPowerActivity.this, "关闭路口放大图功能",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.navi_lane:
                if(tencentCarNaviManager!=null){
                    //设置是否开启车道线功能
                    if(isChecked){
                        tencentCarNaviManager.setGuidedLaneEnabled(true);
                        Toast.makeText(SetNaviFunctionPowerActivity.this, "开启车道线功能",Toast.LENGTH_SHORT).show();
                    }else {
                        tencentCarNaviManager.setGuidedLaneEnabled(false);
                        Toast.makeText(SetNaviFunctionPowerActivity.this, "关闭车道线功能",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
