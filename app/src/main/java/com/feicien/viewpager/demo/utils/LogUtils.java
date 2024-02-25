package com.feicien.viewpager.demo.utils;

import android.util.Log;


public class LogUtils {
    public static void e(String tag, String msg) {
        Log.e("HiCar_" + tag, msg);
    }

    public static void i(String tag, String msg) {
        Log.i("HiCar_" + tag, msg);
    }

    public static void d(String tag, String msg) {
        Log.d("HiCar_" + tag, msg);
    }
}
