package com.example.tencentnavigation.tencentnavidemo;

import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;



public class NaviSimuActivity extends BaseActivity implements AdapterView.OnClickListener{

    private Button stopBtn;
    private Button clear_ui;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        stopBtn = findViewById(R.id.navi_stop);
        clear_ui = findViewById(R.id.clear_ui);
        stopBtn.setVisibility(View.VISIBLE);
        clear_ui.setVisibility(View.VISIBLE);
        stopBtn.setOnClickListener(this);
        clear_ui.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.navi_stop:
                //停止模拟导航
                tencentCarNaviManager.stopSimulateNavi();
                break;
            case R.id.clear_ui:
                carNaviView.clearAllRouteUI();
                break;
        }
    }


}
