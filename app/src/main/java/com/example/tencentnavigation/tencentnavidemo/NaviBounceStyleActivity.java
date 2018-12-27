package com.example.tencentnavigation.tencentnavidemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class NaviBounceStyleActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

    private LinearLayout bounce;
    private SeekBar bouncebar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initDate();
    }

    private void initView() {
        bounce = findViewById(R.id.bounce);
        bouncebar = findViewById(R.id.bouncebar);
        bounce.setVisibility(View.VISIBLE);
    }

    private void initDate() {
        carNaviView.setBounceTime(1);
        bouncebar.setOnSeekBarChangeListener(this);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //是否是用户操作
        if (fromUser) {
            carNaviView.setBounceTime(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
