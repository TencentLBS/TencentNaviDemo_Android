package com.example.tencentnavigation.tencentnavidemo;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SetPanelHideActivity extends BaseActivity {
    private Switch aSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aSwitch = findViewById(R.id.panel_hide);
        aSwitch.setVisibility(View.VISIBLE);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    carNaviView.setNaviPanelEnabled(false);
                }else {
                    carNaviView.setNaviPanelEnabled(true);
                }
            }
        });
    }
}
