package com.example.tencentnavigation.tencentnavidemo;

//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

public class CompassMarkerVisibleActivity extends BaseActivity {

    private LinearLayout marker_visible;
    private Switch marker_visible_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        carNaviView.setCompassMarkerVisible(false);
        initView();
        initData();
    }

    private void initView() {
        marker_visible = findViewById(R.id.marker_visible);
        marker_visible_switch = findViewById(R.id.marker_visible_switch);
        marker_visible.setVisibility(View.VISIBLE);
    }


    private void initData() {
        marker_visible_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //显示圆盘
                    carNaviView.setCompassMarkerVisible(true);
                } else {
                    //不显示圆盘
                    carNaviView.setCompassMarkerVisible(false);
                }
            }
        });
    }

}
