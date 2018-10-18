package com.example.tencentnavigation.tencentnavidemo;

import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;



public class NaviSimuActivity extends BaseActivity implements AdapterView.OnClickListener{

    private Button stopBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        stopBtn = findViewById(R.id.navi_stop);
        stopBtn.setVisibility(View.VISIBLE);
        stopBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.navi_stop:
                //停止模拟导航
                tencentCarNaviManager.stopSimulateNavi();
                break;
        }
    }


}
