package com.example.tencentnavigation.tencentnavidemo;

import android.os.Bundle;
import android.support.annotation.Nullable;


public class SetDefaultResouceActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不使用默认资源
        //使用自己的图标替换默认资源
        tencentCarNaviManager.setIsDefaultRes(false);
    }
}
