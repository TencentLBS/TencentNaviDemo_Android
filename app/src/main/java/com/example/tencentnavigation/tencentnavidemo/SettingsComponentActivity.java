package com.example.tencentnavigation.tencentnavidemo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.LocationProvider;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.map.navi.car.DayNightMode;
import com.tencent.map.navi.car.NaviMode;
import com.tencent.map.util.CommonUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * 导航设置界面
 *
 */
public class SettingsComponentActivity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private final String TAG = "navisdk";
    private RelativeLayout avoidJam;
    private RelativeLayout noTolls;
    private RelativeLayout avoidHighway;
    private ImageView avoidJamImage;
    private ImageView noTollsImage;
    private ImageView avoidHighwayImage;
    private TextView avoidJamText;
    private TextView noTollsText;
    private TextView avoidHighwayText;
    private Switch naviPanel;
    private Switch electriEyes;
    private Switch crossingEnlarge;
    private RadioGroup dayOrNightMode;
    private RadioGroup naviModeRadioGroup;


    private boolean isNavigating = false;
    private boolean avoidJamSelected = false;
    private boolean noTollsSelected = false;
    private boolean avoidHighwaySelected = false;
    private boolean showNaviPanel = true;
    private boolean showElectriEyesPicture = true;
    private boolean showCrossingEnlargePicture = true;
    private DayNightMode dayNightMode;
    private NaviMode naviMode;
    private int gpsStatus = LocationProvider.AVAILABLE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initDatas();
        initViews();
        initEvents();
        initDeviceInfo();
    }

    private void initDatas() {
        Intent intent = getIntent();
        if (intent != null) {
            isNavigating = intent.getBooleanExtra("isNavigating", false);
            avoidJamSelected = intent.getBooleanExtra("avoidJam", false);
            noTollsSelected = intent.getBooleanExtra("noTolls", false);
            avoidHighwaySelected = intent.getBooleanExtra("avoidHighway", false);
            showNaviPanel = intent.getBooleanExtra("showNaviPanel", true);
            showElectriEyesPicture = intent.getBooleanExtra("electriEyes", true);
            showCrossingEnlargePicture = intent.getBooleanExtra("crossingEnlarge", true);
            dayNightMode = (DayNightMode) intent.getSerializableExtra("dayNightMode");
            naviMode = (NaviMode) intent.getSerializableExtra("naviMode");
        }

    }

    private void initViews(){
        avoidJam = findViewById(R.id.avoidJam);
        noTolls = findViewById(R.id.noTolls);
        avoidHighway = findViewById(R.id.avoidHighway);
        avoidJamImage = findViewById(R.id.avoidJamImage);
        noTollsImage = findViewById(R.id.noTollsImage);
        avoidHighwayImage = findViewById(R.id.avoidHighwayImage);
        avoidJamText = findViewById(R.id.avoidJamText);
        noTollsText = findViewById(R.id.noTollsText);
        avoidHighwayText = findViewById(R.id.avoidHighwayText);
        naviPanel = findViewById(R.id.naviPanelSwitch);
        electriEyes = findViewById(R.id.electriEyesSwitch);
        crossingEnlarge = findViewById(R.id.crossingEnlargeSwitch);
        dayOrNightMode = findViewById(R.id.dayOrNightMode);
        naviModeRadioGroup = findViewById(R.id.naviMode);

        changeImageAndTextColor(1);
        changeImageAndTextColor(2);
        changeImageAndTextColor(3);
        naviPanel.setChecked(showNaviPanel);
        electriEyes.setChecked(showElectriEyesPicture);
        crossingEnlarge.setChecked(showCrossingEnlargePicture);

        switch (dayNightMode) {
            case DAY_MODE:
                dayOrNightMode.check(R.id.dayMode);
                break;
            case NIGHT_MODE:
                dayOrNightMode.check(R.id.nightMode);
                break;
            case AUTO_MODE:
                dayOrNightMode.check(R.id.autoMode);
                break;
            default:
                break;
        }
        switch (naviMode) {
            case MODE_3DCAR_TOWARDS_UP:
                naviModeRadioGroup.check(R.id.mode_3d_up);
                break;
            case MODE_2DMAP_TOWARDS_NORTH:
                naviModeRadioGroup.check(R.id.mode_2d_north);
                break;
            case MODE_OVERVIEW:
                naviModeRadioGroup.check(R.id.mode_2d_overview);
                break;
            case MODE_REMAINING_OVERVIEW:
                naviModeRadioGroup.check(R.id.mode_remaining_overview);
                break;
        }

    }

    private void initEvents() {
        avoidJam.setOnClickListener(this);
        noTolls.setOnClickListener(this);
        avoidHighway.setOnClickListener(this);
        naviPanel.setOnClickListener(this);
        electriEyes.setOnClickListener(this);
        crossingEnlarge.setOnClickListener(this);
        dayOrNightMode.setOnCheckedChangeListener(this);
        naviModeRadioGroup.setOnCheckedChangeListener(this);
    }

    private void initDeviceInfo() {
        String imei = CommonUtil.getImei(this);
        TextView textView = findViewById(R.id.deviceInfo);
        textView.setText("IMEI: " + imei);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.avoidJam:
                avoidJam();
                break;
            case R.id.noTolls:
                noTolls();
                break;
            case R.id.avoidHighway:
                avoidHighway();
                break;
            case R.id.naviPanelSwitch:
                showNaviPanel = !showNaviPanel;
                break;
            case R.id.electriEyesSwitch:
                showElectriEyesPicture = !showElectriEyesPicture;
                break;
            case R.id.crossingEnlargeSwitch:
                showCrossingEnlargePicture = !showCrossingEnlargePicture;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.dayMode:
                dayNightMode = DayNightMode.DAY_MODE;
                break;
            case R.id.nightMode:
                dayNightMode = DayNightMode.NIGHT_MODE;
                break;
            case R.id.autoMode:
                dayNightMode = DayNightMode.AUTO_MODE;
                break;
            case R.id.mode_3d_up:
                naviMode = NaviMode.MODE_3DCAR_TOWARDS_UP;
                break;
            case R.id.mode_2d_north:
                naviMode = NaviMode.MODE_2DMAP_TOWARDS_NORTH;
                break;
            case R.id.mode_2d_overview:
                naviMode = NaviMode.MODE_OVERVIEW;
                break;
            case R.id.mode_remaining_overview:
                naviMode = NaviMode.MODE_REMAINING_OVERVIEW;
                break;
            default:
                break;
        }
    }

    /**
     *完成用户点击躲避拥堵后的界面和变量变化
     */
    private void avoidJam(){
        avoidJamSelected = !avoidJamSelected;
        changeImageAndTextColor(1);
    }

    /**
     * 完成用户点击避开收费后的界面和变量变化
     */
    private void noTolls(){
        noTollsSelected = !noTollsSelected;
        changeImageAndTextColor(2);
    }

    /**
     * 完成用户点击不走高速的界面和变量变化
     */
    private void avoidHighway(){
        avoidHighwaySelected = !avoidHighwaySelected;
        changeImageAndTextColor(3);
    }

    /**
     * 根据用户点击情况改变对应图片和文字颜色
     * @param which
     */
    private void changeImageAndTextColor(int which){
        if( 1 == which){
            avoidJamImage.setImageResource(avoidJamSelected ? R.drawable.route_avoid_jam_active : R.drawable.route_avoid_jam_normal);
            avoidJamText.setTextColor(avoidJamSelected ? getResources().getColor(R.color.my_blue) : Color.BLACK);
        }else if(2 == which){
            noTollsImage.setImageResource(noTollsSelected ? R.drawable.route_no_toll_active : R.drawable.route_no_toll_normal);
            noTollsText.setTextColor(noTollsSelected ? getResources().getColor(R.color.my_blue): Color.BLACK);
        }else{
            avoidHighwayImage.setImageResource(avoidHighwaySelected ? R.drawable.route_no_hightway_active : R.drawable.route_no_hightway_normal);
            avoidHighwayText.setTextColor(avoidHighwaySelected ? getResources().getColor(R.color.my_blue) : Color.BLACK);
        }
    }

    private void copyAssets(String gpsDirPath) {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("GPS");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to get asset gps file list.", e);
        }
        if (files != null) {
            for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    File outFile = new File(gpsDirPath, filename);
                    if (outFile.exists()) {
                        continue;
                    } else {
                        File parent = outFile.getParentFile();
                        if (parent != null && !parent.exists()) {
                            parent.mkdirs();
                        }
                    }
                    in = assetManager.open("GPS" + File.separator + filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                } catch(IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Failed to copy asset file: " + filename, e);
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Failed to close input stream", e);
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Failed to close output stream", e);
                        }
                    }
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    @Override
    public void onBackPressed() {

        giveBackConfigInfo();
        super.onBackPressed();
    }

    /**
     * 将用户设置内容返回给导航界面
     */
    private void giveBackConfigInfo() {
        Intent intent = new Intent();
        intent.putExtra("avoidJam", avoidJamSelected);
        intent.putExtra("noTolls", noTollsSelected);
        intent.putExtra("avoidHighway", avoidHighwaySelected);
        intent.putExtra("showNaviPanel", showNaviPanel);
        intent.putExtra("electriEyes", showElectriEyesPicture);
        intent.putExtra("crossingEnlarge", showCrossingEnlargePicture);
        intent.putExtra("dayNightMode", dayNightMode);
        intent.putExtra("naviMode", naviMode);
        intent.putExtra("gpsStatus", gpsStatus);
        setResult(RESULT_OK, intent);
    }


}
