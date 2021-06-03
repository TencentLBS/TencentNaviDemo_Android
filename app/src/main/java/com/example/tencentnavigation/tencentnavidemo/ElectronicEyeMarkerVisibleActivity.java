package com.example.tencentnavigation.tencentnavidemo;

//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

public class ElectronicEyeMarkerVisibleActivity extends BaseActivity {

    private LinearLayout eye_marker_visible;
    private Switch eye_marker_visible_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        carNaviView.setElectronicEyeMarkerVisible(false);
        initView();
        initData();
    }

    private void initView() {
        eye_marker_visible = findViewById(R.id.eye_marker_visible);
        eye_marker_visible_switch = findViewById(R.id.eye_marker_visible_switch);
        eye_marker_visible.setVisibility(View.VISIBLE);
    }

    private void initData() {
        eye_marker_visible_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    //显示电子眼
                    carNaviView.setElectronicEyeMarkerVisible(true);
                }else {
                    //不显示电子眼
                    carNaviView.setElectronicEyeMarkerVisible(false);
                }
            }
        });
    }
}
