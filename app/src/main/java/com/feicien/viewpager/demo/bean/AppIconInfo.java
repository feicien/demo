package com.feicien.viewpager.demo.bean;


public class AppIconInfo {
    public String name;
    public String packageName;


    public AppIconInfo(String name) {
        this(name, null);
    }

    public AppIconInfo(String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
    }
}
