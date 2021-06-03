package com.example.tencentnavigation.tencentnavidemo;

import android.os.Bundle;
import androidx.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;

import com.tencent.map.navi.car.NaviMode;

public class SetNaviModeActivity extends BaseActivity {

    private RadioGroup naviMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        naviMode = findViewById(R.id.navi_mode);

        naviMode.setVisibility(View.VISIBLE);
        naviMode.setOnCheckedChangeListener((group, id)->{
            switch (id){
                //设置导航模式
                case R.id.three_d_mode:
                    carNaviView.setNaviMode(NaviMode.MODE_3DCAR_TOWARDS_UP);
                    break;
                case R.id.two_d_mode:
                    carNaviView.setNaviMode(NaviMode.MODE_2DMAP_TOWARDS_NORTH);
                    break;
                case R.id.over_mode:
                    carNaviView.setNaviMode(NaviMode.MODE_OVERVIEW);
                    break;
                case R.id.remaining_over_mode:
                    carNaviView.setNaviMode(NaviMode.MODE_REMAINING_OVERVIEW);
            }
        });
    }


}
