package com.example.tencentnavigation.tencentnavidemo;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.tencent.map.navi.TencentRouteSearchCallback;
import com.tencent.map.navi.car.CarNaviView;
import com.tencent.map.navi.car.CarRouteSearchOptions;
import com.tencent.map.navi.car.TencentCarNaviManager;
import com.tencent.map.navi.data.NaviPoi;
import com.tencent.map.navi.data.RouteData;
import com.tencent.map.navi.ui.car.CarNaviInfoPanel;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class NaviDefaultUi extends AppCompatActivity implements
        EasyPermissions.PermissionCallbacks {

    private static final String TAG = "navisdk";

    /**
     * CarNaviManager
     */
    private TencentCarNaviManager tencentCarNaviManager;
    /**
     * 导航地图
     */
    private CarNaviView carNaviView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi_default_ui);

        /**
         * 所要申请的权限
         * <ul>
         * <li>通过GPS得到精确位置
         * <li>通过网络得到粗略位置
         * <li>访问手机当前状态
         * <li>外部存储写权限
         * </ul>
         */
        String[] perms = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (EasyPermissions.hasPermissions(this, perms)) { //检查是否获取该权限
            Log.i(TAG, "已获取权限");
        } else {
            /**
             * <ul>
             * <li>第二个参数是被拒绝后再次申请该权限的解释
             * <li>第三个参数是请求码
             * <li>第四个参数是要申请的权限
             * </ul>
             */
            EasyPermissions.requestPermissions(this, "必要的权限", 0, perms);
        }

        initManager();
    }

    /**
     * 显示默认Ui
     */
    public void showDefaultUi(View view) {
        startSearch(); // 算路->导航
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
    }

    /**
     * 隐藏导航默认Ui
     */
    public void hideDefaultUi(View view) {
        if (carNaviView != null)
            carNaviView.hideNaviInfoPanel();
    }

    /**
     * 导航view和manager初始化
     *
     * <p>TencentCarNaviManager 推荐使用单例模式
     */
    private void initManager() {
        carNaviView = findViewById(R.id.default_navi_car_view);
        tencentCarNaviManager = new TencentCarNaviManager(this);
        tencentCarNaviManager.addNaviView(carNaviView);
    }

    /**
     * 开始算路
     */
    private void startSearch() {
        NaviPoi start = new NaviPoi(39.984110, 116.307590); // 导航起点
        NaviPoi dest = new NaviPoi(39.994868, 116.406058); // 导航终点
        ArrayList<NaviPoi> ways = new ArrayList<>();

        try {
            tencentCarNaviManager.searchRoute(start, dest, ways
                    , CarRouteSearchOptions.create()
                    , new TencentRouteSearchCallback() {
                        @Override
                        public void onRouteSearchFailure(int i, String s) {

                        }

                        @Override
                        public void onRouteSearchSuccess(ArrayList<RouteData> arrayList) {
                            /**
                             * 算路成功
                             *
                             * <p>成功后这里就立即开始导航了
                             * 这里默认选取第一条路线导航了
                             * 用户可根据需要选择哪条算路结果
                             */
                            try {
                                tencentCarNaviManager.startSimulateNavi(0);
                            } catch (Exception e) {
                                Log.e(TAG, "err : " + e.getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关联生命周期管理
     */
    @Override
    protected void onStart() {
        if(carNaviView!=null){
            carNaviView.onStart();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(carNaviView!=null){
            carNaviView.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        if(carNaviView!=null){
            carNaviView.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(carNaviView!=null){
            carNaviView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(carNaviView!=null){
            carNaviView.onDestroy();
        }
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}
