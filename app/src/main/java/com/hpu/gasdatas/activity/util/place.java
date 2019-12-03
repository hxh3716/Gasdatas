package com.hpu.gasdatas.activity.util;

/**
 * Created by：何学慧
 * Detail:巷道位置信息
 * on 2019/10/30
 */

public class place {
    private int id;
    private String tunnel;
    private int groups;
    private int holes;
    private String detail;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTunnel() {
        return tunnel;
    }

    public void setTunnel(String tunnel) {
        this.tunnel = tunnel;
    }

    public int getGroups() {
        return groups;
    }

    public void setGroups(int groups) {
        this.groups = groups;
    }

    public int getHoles() {
        return holes;
    }

    public void setHoles(int holes) {
        this.holes = holes;
    }

    @Override
    public String toString() {
        return "place{" +
                "id=" + id +
                ", tunnel='" + tunnel + '\'' +
                ", groups=" + groups +
                ", holes=" + holes +
                ", detail='" + detail + '\'' +
                '}';
    }
}