package com.example.tencentnavigation.tencentnavidemo;

//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.tencent.map.navi.car.NaviMode;

public class VisibleRegionMarginActivity extends BaseActivity implements View.OnClickListener {

    private Button button_visible_margin;
    private LinearLayout turn_visible_margin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //2d全览模式
        carNaviView.setNaviMode(NaviMode.MODE_OVERVIEW);
        initView();
    }

    private void initView() {
        button_visible_margin = findViewById(R.id.button_visible_margin);
        turn_visible_margin = findViewById(R.id.turn_visible_margin);
        turn_visible_margin.setVisibility(View.VISIBLE);
        button_visible_margin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_visible_margin:
                carNaviView.setVisibleRegionMargin(0,300,0,300);
                break;
        }
    }
}
