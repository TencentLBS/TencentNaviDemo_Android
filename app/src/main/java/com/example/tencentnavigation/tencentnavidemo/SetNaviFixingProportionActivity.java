package com.example.tencentnavigation.tencentnavidemo;


import android.os.Bundle;
import android.support.annotation.Nullable;

public class SetNaviFixingProportionActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(carNaviView!=null){
            //设置自车点位置，默认是0.5,0.75
            carNaviView.setNaviFixingProportion3D(0.5f,0.5f);
        }
    }
}
