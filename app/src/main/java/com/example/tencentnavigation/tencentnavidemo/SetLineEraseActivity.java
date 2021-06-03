package com.example.tencentnavigation.tencentnavidemo;

import android.os.Bundle;
import androidx.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;

public class SetLineEraseActivity extends BaseActivity {
    private LinearLayout erase;
    private Switch eraseSwitch;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        erase = findViewById(R.id.erase);
        eraseSwitch = findViewById(R.id.erase_switch);
        erase.setVisibility(View.VISIBLE);
        eraseSwitch.setOnCheckedChangeListener((button, ischecked)->{
            if(ischecked){
                //设置路线擦除
                carNaviView.setRouteEraseType(1);
            }else {
                //取消路线擦除
                carNaviView.setRouteEraseType(0);
            }
        });
    }
}
