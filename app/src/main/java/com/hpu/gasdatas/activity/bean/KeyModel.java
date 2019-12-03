package com.hpu.gasdatas.activity.bean;


public class KeyModel {

    public KeyModel(int code, String lable){
        this.code = code;
        this.lable = lable;
    }

    private String lable;
    private int code;

    public String getLable() {
        return lable;
    }

    public int getCode() {
        return code;
    }
}
