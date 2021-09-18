/*
 * All rights Reserved, Designed By lbs.qq.com
 * @version: 5.2.0
 * @author: mjzuo
 * @date: 2020/10/15
 * @Copyright: Copyright (c) 2020 tencent Inc. All rights reserved.
 */

package com.example.tencentnavigation.tencentnavidemo.util;

/**
 * 单例类。
 */
public abstract class Singleton<T> {

    private T mInstance;

    protected abstract T create();

    public final T get() {
        synchronized (this) {
            if (mInstance == null) {
                mInstance = create();
            }
            return mInstance;
        }
    }

    public final void destory() {
        if (mInstance != null) {
            mInstance = null;
        }
    }
}
