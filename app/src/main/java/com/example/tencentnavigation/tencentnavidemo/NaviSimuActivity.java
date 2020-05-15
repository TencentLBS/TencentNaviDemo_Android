package com.example.tencentnavigation.tencentnavidemo;

import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.tencent.map.navi.ui.car.CarNaviInfoPanel;


public class NaviSimuActivity extends BaseActivity implements AdapterView.OnClickListener{

    private Button stopBtn;
    private Button clear_ui;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        stopBtn = findViewById(R.id.navi_stop);
        clear_ui = findViewById(R.id.clear_ui);
        stopBtn.setVisibility(View.GONE);
        clear_ui.setVisibility(View.GONE);

        showDefaultUi();
    }


    @Override
    public void onClick(View v) {
    }

    /**
     * 显示默认Ui
     */
    private void showDefaultUi() {

        CarNaviInfoPanel carNaviInfoPanel = carNaviView.showNaviInfoPanel();
        carNaviInfoPanel.setOnNaviInfoListener(new CarNaviInfoPanel.OnNaviInfoListener() {
            @Override
            public void onBackClick() {
                /**
                 * 退出按钮监听
                 *
                 * <p>这里直接结束导航并退出Activity了
                 */
                tencentCarNaviManager.stopSimulateNavi();
                finish();
            }
        });
        /**
         * 可控制默认Ui每部分的显示和隐藏
         * <pre>
         *     naviInfoPanleConfig.setButtomPanelEnable(false);
         * </pre>
         */
        CarNaviInfoPanel.NaviInfoPanelConfig naviInfoPanleConfig = new CarNaviInfoPanel.NaviInfoPanelConfig();
        carNaviInfoPanel.setNaviInfoPanelConfig(naviInfoPanleConfig);
        // 设置全览模式边距
        carNaviView.setVisibleRegionMargin(50,150,50,50);
    }
}
