package com.example.tencentnavigation.tencentnavidemo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class DriverPassengerShowActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_passenger_show);

        Button driverBtn = findViewById(R.id.driver);
        driverBtn.setOnClickListener(this);
        Button passengerBtn = findViewById(R.id.passenger);
        passengerBtn.setOnClickListener(this);

        //所要申请的权限
        String[] perms = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "必要的权限", 0, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.driver: {
                Intent intent = new Intent(this, DriverActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.passenger: {
                Intent intent = new Intent(this, PassengerActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
