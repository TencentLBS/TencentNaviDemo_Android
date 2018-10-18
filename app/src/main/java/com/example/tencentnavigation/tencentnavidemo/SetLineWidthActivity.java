package com.example.tencentnavigation.tencentnavidemo;

import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

public class SetLineWidthActivity extends BaseActivity {
    private SeekBar widthBar;
    private LinearLayout width;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        width = findViewById(R.id.width);
        widthBar = findViewById(R.id.widthbar);

        width.setVisibility(View.VISIBLE);
        widthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(carNaviView!=null){
                    //宽度值
                    int width = 20+progress;
                    //设置宽度
                    carNaviView.setNaviLineWidth(width);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
