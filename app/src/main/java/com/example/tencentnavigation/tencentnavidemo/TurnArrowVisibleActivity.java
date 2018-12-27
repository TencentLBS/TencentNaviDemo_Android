package com.example.tencentnavigation.tencentnavidemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

public class TurnArrowVisibleActivity extends BaseActivity {
    private LinearLayout turn_arrow_visible;
    private Switch turn_arrow_visible_switch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        carNaviView.setTurnArrowVisible(false);
        initView();
        initData();
    }

    private void initView() {
        turn_arrow_visible = findViewById(R.id.turn_arrow_visible);
        turn_arrow_visible_switch = findViewById(R.id.turn_arrow_visible_switch);
        turn_arrow_visible.setVisibility(View.VISIBLE);
    }

    private void initData() {
        turn_arrow_visible_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    //显示地图路线上的白色转向箭头
                    carNaviView.setTurnArrowVisible(true);
                }else {
                    //不显示地图路线上的白色转向箭头
                    carNaviView.setTurnArrowVisible(false);
                }
            }
        });
    }


}
